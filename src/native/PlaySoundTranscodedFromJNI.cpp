#include "PlaySoundTranscodedFromJNI.h"
#include <stdio.h>
#include <assert.h>
#include <mutex> // std::mutex
#include <condition_variable>  // std::condition_variable
#include <thread> // std::thread

extern "C" {
	#include <libavcodec/avcodec.h>
	#include <libavformat/avformat.h>
	#include <libswresample/swresample.h>
}

// Florian Raudies, 07/20/2016, Mountain View, CA.
// vcvarsall.bat x64
// cl PlaySoundTranscodedFromJNI.cpp /Fe"..\..\lib\PlaySoundTranscodedFromJNI" /I"C:\Users\Florian\FFmpeg" /I"C:\Program Files\Java\jdk1.8.0_91\include" /I"C:\Program Files\Java\jdk1.8.0_91\include\win32" /showIncludes /MD /LD /link "C:\Program Files\Java\jdk1.8.0_91\lib\jawt.lib" "C:\Users\Florian\FFmpeg2\libavcodec\avcodec.lib" "C:\Users\Florian\FFmpeg2\libavformat\avformat.lib" "C:\Users\Florian\FFmpeg2\libavutil\avutil.lib" "C:\Users\Florian\FFmpeg2\libswresample\swresample.lib"

#define AUDIO_BUFFER_SIZE 1024
#define MAX_AUDIO_FRAME_SIZE 192000
#define AUDIO_QUEUE_MAX_SIZE 128

/** The output bit rate in kbit/s */
#define OUTPUT_BIT_RATE 22050
/** The number of output channels */
#define OUTPUT_CHANNELS 1

/**
 * Convert an error code into a text message.
 * @param error Error code to be converted
 * @return Corresponding error text (not thread-safe)
 */
static const char *get_error_text(const int error)
{
    static char error_buffer[256];
    av_strerror(error, error_buffer, sizeof(error_buffer));
    return error_buffer;
}

typedef struct PacketQueue {
  AVPacketList *first_pkt, *last_pkt;
  int nb_packets;
  int size;
  bool getPkt; // false
  std::mutex *mu;
  std::condition_variable *cv;
} PacketQueue;

PacketQueue		audioq;

int				flush			= 0;
int				quit			= 0;
int				nLen			= 0;
int             iAudioStream	= -1;

uint8_t			*streamAudio		= nullptr;
AVFormatContext *pFormatCtx			= nullptr;
AVCodecContext  *aInCodecCtx		= nullptr;
AVCodecContext	*aOutCodecCtx		= nullptr;
AVCodecContext	*aInCodecCtxOrig	= nullptr;
std::thread		*decodingThread		= nullptr;
SwrContext		*resample_context	= nullptr;

static void packet_queue_init(PacketQueue *q) {
	memset(q, 0, sizeof(PacketQueue));
	q->mu = new std::mutex;
	q->cv = new std::condition_variable;
}

static void packet_queue_flush(PacketQueue *q)
{
    AVPacketList *pkt, *pkt1;
	flush = 1; // this will release producer and consumer.
	std::unique_lock<std::mutex> locker(*q->mu);
    for (pkt = q->first_pkt; pkt; pkt = pkt1) {
        pkt1 = pkt->next;
        av_packet_unref(&pkt->pkt);
        av_freep(&pkt);
    }
    q->last_pkt = NULL;
    q->first_pkt = NULL;
    q->nb_packets = 0;
    q->size = 0;
	flush = 0;
	locker.unlock();
}

static void packet_queue_destroy(PacketQueue *q)
{
    packet_queue_flush(q);
	delete q->mu;
	delete q->cv;
}

// multi threaded consumer/producer model
// http://codereview.stackexchange.com/questions/84109/a-multi-threaded-producer-consumer-with-c11
int packet_queue_put(PacketQueue *q, AVPacket *pkt) {
	AVPacketList *pkt1;
	if(av_dup_packet(pkt) < 0) {
		return -1;
	}
	pkt1 = (AVPacketList*) av_malloc(sizeof(AVPacketList));

	if (!pkt1)
		return -1;

	pkt1->pkt = *pkt;
	pkt1->next = NULL;

	std::unique_lock<std::mutex> locker(*q->mu);
	q->cv->wait(locker, [q](){return (q->nb_packets < AUDIO_QUEUE_MAX_SIZE) || (flush==1);});

	if (flush==1) {
		av_free(pkt1);
	} else {
		if (!q->last_pkt)
			q->first_pkt = pkt1;
		else
			q->last_pkt->next = pkt1;

		q->last_pkt = pkt1;
		q->nb_packets++;
		q->size += pkt1->pkt.size;	
	}

	locker.unlock();
	q->cv->notify_one();
	
	return 0;
}

static int packet_queue_get(PacketQueue *q, AVPacket *pkt) {
	AVPacketList *pkt1;
	int ret = 0;

	if(quit) {
		return -1;
	}

	std::unique_lock<std::mutex> locker(*q->mu);
    q->cv->wait(locker, [q](){return (q->nb_packets > 0) || (flush==1);});
	//fprintf(stderr, "Acquired mutex to read packet queue.\n");

	if (flush==1) {
		ret = -1; // puts silence
	} else {
		pkt1 = q->first_pkt;
		q->first_pkt = pkt1->next;

		if (!q->first_pkt)
			q->last_pkt = NULL;

		q->nb_packets--;
		q->size -= pkt1->pkt.size;
		*pkt = pkt1->pkt;
		av_free(pkt1);
		ret = 1;	
	}

	locker.unlock();
	q->cv->notify_one();

	return ret;
}


static int init_resampler(AVCodecContext *input_codec_context,
                          AVCodecContext *output_codec_context,
                          SwrContext **resample_context) {
        int error;

        /**
         * Create a resampler context for the conversion.
         * Set the conversion parameters.
         * Default channel layouts based on the number of channels
         * are assumed for simplicity (they are sometimes not detected
         * properly by the demuxer and/or decoder).
         */
        *resample_context = swr_alloc_set_opts(NULL,
                                              av_get_default_channel_layout(output_codec_context->channels),
                                              output_codec_context->sample_fmt,
                                              output_codec_context->sample_rate,
                                              av_get_default_channel_layout(input_codec_context->channels),
                                              input_codec_context->sample_fmt,
                                              input_codec_context->sample_rate,
                                              0, NULL);
        if (!*resample_context) {
            fprintf(stderr, "Could not allocate resample context\n");
            return AVERROR(ENOMEM);
        }
        /**
        * Perform a sanity check so that the number of converted samples is
        * not greater than the number of samples to be converted.
        * If the sample rates differ, this case has to be handled differently
        */
        assert(output_codec_context->sample_rate == input_codec_context->sample_rate);

        /** Open the resampler with the specified parameters. */
        if ((error = swr_init(*resample_context)) < 0) {
			fprintf(stderr, "Could not open resamlpe context. Error: '%s'.\n",
					get_error_text(error));
            swr_free(resample_context);
            return error;
        }
    return 0;
}

/**
 * Initialize a temporary storage for the specified number of audio samples.
 * The conversion requires temporary storage due to the different format.
 * The number of audio samples to be allocated is specified in frame_size.
 */
static int init_converted_samples(uint8_t ***converted_input_samples,
                                  AVCodecContext *output_codec_context,
                                  int frame_size)
{
    int error;

    /**
     * Allocate as many pointers as there are audio channels.
     * Each pointer will later point to the audio samples of the corresponding
     * channels (although it may be NULL for interleaved formats).
     */
    if (!(*converted_input_samples = (uint8_t**)calloc(output_codec_context->channels,
                                            sizeof(**converted_input_samples)))) {
        fprintf(stderr, "Could not allocate converted input sample pointers.\n");
        return 1;
    }

    /**
     * Allocate memory for the samples of all channels in one consecutive
     * block for convenience.
     */
    if ((error = av_samples_alloc(*converted_input_samples, NULL,
                                  output_codec_context->channels,
                                  frame_size,
                                  output_codec_context->sample_fmt, 0)) < 0) {
		fprintf(stderr, "Could not allocate converted input samples. Error: '%s')\n",
						get_error_text(error));
        av_freep(&(*converted_input_samples)[0]);
        free(*converted_input_samples);
        return 1;
    }
    return 0;
}

/**
 * Convert the input audio samples into the output sample format.
 * The conversion happens on a per-frame basis, the size of which is specified
 * by frame_size.
 */
static int convert_samples(const uint8_t **input_data,
                           uint8_t **converted_data, const int frame_size,
                           SwrContext *resample_context)
{
    int error;
    /** Convert samples using the resampler context. */
    if ((error = swr_convert(resample_context,
                             converted_data, frame_size,
                             input_data    , frame_size)) < 0) {
        fprintf(stderr, "Could not convert input samples. Error '%s')\n",
                get_error_text(error));
        return 1;
    }
    return 0;
}

// decodes packet from queue into audio_buf
int audio_decode_frame(AVCodecContext *aInCodecCtx, uint8_t *audio_buf, int buf_size) {
	//fprintf(stderr, "Called audio_decode_frame.\n");
	static AVPacket pkt;
	static uint8_t *audio_pkt_data = NULL;
	static int audio_pkt_size = 0;
	static AVFrame frame;
	uint8_t **converted_input_samples = NULL;
	int len1, data_size = 0;
	int error;

	for(;;) {
		while(audio_pkt_size > 0) {
			int got_frame = 0;
			len1 = avcodec_decode_audio4(aInCodecCtx, &frame, &got_frame, &pkt);
			if(len1 < 0) {
				/* if error, skip frame */
				audio_pkt_size = 0;
				break;
			}
			
			audio_pkt_data += len1;
			audio_pkt_size -= len1;
			data_size = 0;

			if(got_frame) {
				//fprintf(stderr, "Got data from packet queue.\n");
				data_size = av_samples_get_buffer_size(NULL, 
								aOutCodecCtx->channels,
								frame.nb_samples,
								aOutCodecCtx->sample_fmt,
								1);
				//fprintf(stderr, "\tFrame has %d samples.\n", frame.nb_samples);
				//fprintf(stderr, "\t\tData size = %d.\n", data_size);

				assert(data_size <= buf_size);

				if ((error = init_converted_samples(&converted_input_samples, 
											aOutCodecCtx, frame.nb_samples))<0) {
					// clean-up
					if (converted_input_samples) {
						av_freep(&converted_input_samples[0]);
						free(converted_input_samples);					
					}
					return error;
				}

				if ((error = convert_samples((const uint8_t**)frame.extended_data, 
											converted_input_samples, 
											frame.nb_samples, resample_context))<0) {
					// clean-up
					if (converted_input_samples) {
						av_freep(&converted_input_samples[0]);
						free(converted_input_samples);					
					}
					return error;
				}

				memcpy(audio_buf, converted_input_samples[0], data_size);

				av_freep(&converted_input_samples[0]);
				free(converted_input_samples);
			}

			if(data_size <= 0) {
				/* No data yet, get more frames */
				continue;
			}
			/* We have data, return it and come back for more later */
			//fprintf(stderr, "decoded %d data from packet.\n", data_size);
			return data_size;
		}
		if(pkt.data)
			av_free_packet(&pkt);

		if(quit) {
			return -1;
		}

		//fprintf(stderr, "  Calling packet_queue_get\n");
		if(packet_queue_get(&audioq, &pkt) < 0) {
			//fprintf(stderr, "No data in packet queue.\n");
			return -1;
		}

		audio_pkt_data = pkt.data;
		audio_pkt_size = pkt.size;
	}
}

// allocate buffer.
JNIEXPORT jobject JNICALL Java_PlaySoundTranscodedFromJNI_getAudioBuffer
(JNIEnv *env, jobject thisObject, jint nByte) {
	nLen = nByte;
	streamAudio = (uint8_t*) malloc(nByte);
	if (!streamAudio) {
		fprintf(stderr, "Failed to allocate stream audio buffer.\n");
	}
	return env->NewDirectByteBuffer((void*) streamAudio, nByte*sizeof(uint8_t));
}

JNIEXPORT jboolean JNICALL Java_PlaySoundTranscodedFromJNI_loadNextFrame
(JNIEnv *env, jobject thisObject) {
	//fprintf(stderr, "Loading next audio frame.\n");

	int len = nLen; // get length of buffer
	uint8_t *stream = streamAudio; // get a write pointer.

	//AVCodecContext *aInCodecCtx = (AVCodecContext *)userdata;
	int len1, audio_size;  

	static uint8_t audio_buf[(MAX_AUDIO_FRAME_SIZE * 3) / 2];
	static unsigned int audio_buf_size = 0;
	static unsigned int audio_buf_index = 0;

	while(len > 0) {
		//fprintf(stderr, "Length that still needs to be read is %d.\n", len);

		if(audio_buf_index >= audio_buf_size) {
			/* We have already sent all our data; get more */
			audio_size = audio_decode_frame(aInCodecCtx, audio_buf, sizeof(audio_buf));

			//fprintf(stderr, "\tThe audio size is %d.\n", audio_size);

			if(audio_size < 0) {
				/* If error, output silence */
				audio_buf_size = 1024; // arbitrary?
				//fprintf(stderr, "Set silience for %d bytes.\n", audio_buf_size);
				memset(audio_buf, 0, audio_buf_size);
			} else {
				audio_buf_size = audio_size;
			}
			audio_buf_index = 0;
		}
		len1 = audio_buf_size - audio_buf_index;
		
		if(len1 > len)
			len1 = len;

		memcpy(stream, (uint8_t *)audio_buf + audio_buf_index, len1);
		len -= len1;
		stream += len1;
		audio_buf_index += len1;
	}
	//fprintf(stderr, "Audio buffer address: %x\n", streamAudio);
	//fprintf(stderr, "Stream address: %x\n", stream);

	return quit == 0;
}

void decodeLoop() {
	AVPacket packet;
	while(!quit && av_read_frame(pFormatCtx, &packet)>=0) {
		if(packet.stream_index==iAudioStream) {
			//fprintf(stderr, "Decoded packet for audio stream %d.\n",iAudioStream);
			packet_queue_put(&audioq, &packet);
		} else {
			av_free_packet(&packet);
		}
	}
}

JNIEXPORT void JNICALL Java_PlaySoundTranscodedFromJNI_loadAudio
(JNIEnv *env, jobject thisObject, jstring jFileName) {
	const char *fileName = env->GetStringUTFChars(jFileName, 0);
	int             i;
	AVCodec         *aInCodec = nullptr;
	AVCodec			*aOutCodec = nullptr;
	int				error;
	// Register all formats and codecs
	av_register_all();

	// Open video file
	if((error = avformat_open_input(&pFormatCtx, fileName, NULL, NULL))<0) {
		fprintf(stderr, "Could not open input file '%s'. Error: '%s'.\n",
                fileName, get_error_text(error));
		exit(error);
	}

	// Retrieve stream information
	if((error = avformat_find_stream_info(pFormatCtx, NULL))<0) {
		fprintf(stderr, "Could not find stream information. Error: '%s'.\n",
                fileName, get_error_text(error));
		avformat_close_input(&pFormatCtx);
		exit(error);
	}

	// Dump information about file onto standard error
	av_dump_format(pFormatCtx, 0, fileName, 0);

	// Find the first video stream
	iAudioStream = -1;
	for(i = 0; i < pFormatCtx->nb_streams; i++) {
		if(pFormatCtx->streams[i]->codec->codec_type==AVMEDIA_TYPE_AUDIO) {
			iAudioStream = i;
			break;
		}
	}
	if(iAudioStream == -1) {
		fprintf(stderr, "Could not find an audio stream.\n");
		avformat_close_input(&pFormatCtx);
		exit(AVERROR_EXIT);
	}

	aInCodecCtxOrig = pFormatCtx->streams[iAudioStream]->codec;
	if(!(aInCodec = avcodec_find_decoder(aInCodecCtxOrig->codec_id))) {
		fprintf(stderr, "Could not find input codec!\n");
		avformat_close_input(&pFormatCtx);
		exit(AVERROR_EXIT);
	}

	if (!(aInCodecCtx = avcodec_alloc_context3(aInCodec))) {
		fprintf(stderr, "Could not allocate a decoding context.\n");
		avformat_close_input(&pFormatCtx);
		exit(AVERROR_EXIT);
	}

	if((error = avcodec_copy_context(aInCodecCtx, aInCodecCtxOrig))<0) {
		fprintf(stderr, "Could not copy codec context. Error: '%s'.\n",
                fileName, get_error_text(error));
		avformat_close_input(&pFormatCtx);
		exit(error);
	}

	if((error = avcodec_open2(aInCodecCtx, aInCodec, NULL))<0) {
		fprintf(stderr, "Could not open audio codec. Error: '%s'.\n",
                get_error_text(error));
		avformat_close_input(&pFormatCtx);
		exit(error);
	}

	packet_queue_init(&audioq);

	// create the output codec
	if (!(aOutCodec = avcodec_find_encoder(AV_CODEC_ID_PCM_U8))) {
		fprintf(stderr, "Could not create output codec.");
		avformat_close_input(&pFormatCtx);
		exit(AVERROR_EXIT);
	}

    if (!(aOutCodecCtx = avcodec_alloc_context3(aOutCodec))) {
        fprintf(stderr, "Could not allocate an encoding output context\n");
		avformat_close_input(&pFormatCtx);
		exit(AVERROR_EXIT);
    }

    aOutCodecCtx->channels       = OUTPUT_CHANNELS;
    aOutCodecCtx->channel_layout = av_get_default_channel_layout(OUTPUT_CHANNELS);
    aOutCodecCtx->sample_rate    = aInCodecCtx->sample_rate;
    aOutCodecCtx->sample_fmt     = av_get_sample_fmt("u8");//aInCodec->sample_fmts[0];
    aOutCodecCtx->bit_rate       = OUTPUT_BIT_RATE;

    /** Open the encoder for the audio stream to use it later. */
    if ((error = avcodec_open2(aOutCodecCtx, aOutCodec, NULL))<0) {
		fprintf(stderr, "Could not open output codec. Error: '%s'.\n",
                get_error_text(error));
		avformat_close_input(&pFormatCtx);
        exit(error);
    }

    /** Initialize the resampler to be able to convert audio sample formats. */
    if ((error = init_resampler(aInCodecCtx, aOutCodecCtx, &resample_context))<0) {
		avformat_close_input(&pFormatCtx);
		exit(error);
	}

	decodingThread = new std::thread(decodeLoop);

	env->ReleaseStringUTFChars(jFileName, fileName);
}

JNIEXPORT void JNICALL Java_PlaySoundTranscodedFromJNI_release
(JNIEnv *env, jobject thisObject) {
	quit = 1;

	decodingThread->join();

	delete decodingThread;

	packet_queue_destroy(&audioq);

	// Close the codec
	avcodec_close(aInCodecCtx);
	avcodec_close(aInCodecCtxOrig);
	avcodec_close(aOutCodecCtx);
	
	// Cleanup conversion context
	swr_free(&resample_context);

	// Close the video file
	avformat_close_input(&pFormatCtx);

	free(streamAudio);
}