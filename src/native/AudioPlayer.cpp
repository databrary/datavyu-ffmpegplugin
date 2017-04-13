#include "AudioPlayer.h"
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

// Florian Raudies, 04/09/2017, Mountain View, CA.
// vcvarsall.bat x64
// cl AudioPlayer.cpp /Fe"..\..\lib\AudioPlayer" /I"C:\Users\Florian\FFmpeg-release-3.2" /I"C:\Program Files\Java\jdk1.8.0_91\include" /I"C:\Program Files\Java\jdk1.8.0_91\include\win32" /showIncludes /MD /LD /link "C:\Program Files\Java\jdk1.8.0_91\lib\jawt.lib" "C:\Users\Florian\FFmpeg-release-3.2\libavcodec\avcodec.lib" "C:\Users\Florian\FFmpeg-release-3.2\libavformat\avformat.lib" "C:\Users\Florian\FFmpeg-release-3.2\libavutil\avutil.lib" "C:\Users\Florian\FFmpeg-release-3.2\libswresample\swresample.lib"
// To view the function handles in the dll use the command line argument:
//	dumpbin /exports ..\..\lib\AudioPlayer.dll
// To find out about the compiled class methods from java use the tool javap in 
// jdk1.8.0_91/jre/bin
// Examples:
//	javap javax.sound.sampled.AudioFormat
//  javap javax.sound.sampled.AudioFormat$Encoding

#define MAX_AUDIO_FRAME_SIZE 192000
#define AUDIO_QUEUE_MAX_SIZE 128

/**
 * Convert an error code into a text message.
 * @param error Error code to be converted
 * @return Corresponding error text (not thread-safe)
 */
static const char *get_error_text(const int error) {
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
int				done			= 0;
bool			loadedAudio		= false;

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

static bool packet_queue_empty(PacketQueue *q) {
	return q->nb_packets == 0;
}

static void packet_queue_flush(PacketQueue *q) {
    AVPacketList *pkt, *pkt1;
	flush = 1; // this releases the producer and the consumer
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

static void packet_queue_destroy(PacketQueue *q) {
    packet_queue_flush(q);
	delete q->mu;
	delete q->cv;
}

// multi threaded consumer/producer model
// http://codereview.stackexchange.com/questions/84109/a-multi-threaded-producer-consumer-with-c11
int packet_queue_put(PacketQueue *q, AVPacket *pkt) {
	AVPacketList *pkt1;
	if(av_dup_packet(pkt) < 0) { return -1; }
	pkt1 = (AVPacketList*) av_malloc(sizeof(AVPacketList));
	if (!pkt1) { return -1; }

	pkt1->pkt = *pkt;
	pkt1->next = NULL;

	std::unique_lock<std::mutex> locker(*q->mu);
	q->cv->wait(locker, [q](){return (q->nb_packets < AUDIO_QUEUE_MAX_SIZE) || (flush==1);});

	if (flush == 1) {
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

	if (quit) { return -1; }

	std::unique_lock<std::mutex> locker(*q->mu);
    q->cv->wait(locker, [q](){return (q->nb_packets > 0) || (flush==1);});

	if (flush == 1) {
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
    int errNo;

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
    if ((errNo = swr_init(*resample_context)) < 0) {
		fprintf(stderr, "Unable to open resample context. Error: '%s'.\n",
				get_error_text(errNo));
        swr_free(resample_context);
        return errNo;
    }
    return 0;
}

/**
 * Initialize a temporary storage for the specified number of audio samples.
 * The conversion requires temporary storage due to the different formats.
 * The number of audio samples to be allocated is specified in frame_size.
 */
static int init_converted_samples(uint8_t ***converted_input_samples,
                                  AVCodecContext *output_codec_context,
                                  int frame_size) {
    int errNo;

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
    if ((errNo = av_samples_alloc(*converted_input_samples, NULL,
                                  output_codec_context->channels,
                                  frame_size,
                                  output_codec_context->sample_fmt, 0)) < 0) {
		fprintf(stderr, "Could not allocate converted input samples. Error: '%s')\n",
						get_error_text(errNo));
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
static int convert_samples(const uint8_t **input_data, uint8_t **converted_data, 
						   const int frame_size, SwrContext *resample_context) {
    int errNo;
    /** Convert samples using the resampler context. */
    if ((errNo = swr_convert(resample_context,
                             converted_data, frame_size,
                             input_data    , frame_size)) < 0) {
        fprintf(stderr, "Could not convert input samples. Error '%s')\n",
                get_error_text(errNo));
        return 1;
    }
    return 0;
}

// decodes packet from queue into audio_buf
int audio_decode_frame(AVCodecContext *aInCodecCtx, uint8_t *audio_buf, int buf_size) {
	static AVPacket pkt;
	static uint8_t *audio_pkt_data = NULL;
	static int audio_pkt_size = 0;
	static AVFrame frame;
	uint8_t **converted_input_samples = NULL;
	int len1, data_size = 0;
	int errNo;

	for(;;) {
		while (audio_pkt_size > 0) {
			int got_frame = 0;
			len1 = avcodec_decode_audio4(aInCodecCtx, &frame, &got_frame, &pkt);
			if (len1 < 0) {
				/* if error, skip frame */
				audio_pkt_size = 0;
				break;
			}
			
			audio_pkt_data += len1;
			audio_pkt_size -= len1;
			data_size = 0;

			if (got_frame) {
				data_size = av_samples_get_buffer_size(NULL, 
								aOutCodecCtx->channels,
								frame.nb_samples,
								aOutCodecCtx->sample_fmt,
								1);

				assert(data_size <= buf_size);

				if ((errNo = init_converted_samples(&converted_input_samples, 
											aOutCodecCtx, frame.nb_samples)) < 0) {
					// clean-up
					if (converted_input_samples) {
						av_freep(&converted_input_samples[0]);
						free(converted_input_samples);					
					}
					return errNo;
				}

				if ((errNo = convert_samples((const uint8_t**)frame.extended_data, 
											converted_input_samples, 
											frame.nb_samples, resample_context)) < 0) {
					// clean-up
					if (converted_input_samples) {
						av_freep(&converted_input_samples[0]);
						free(converted_input_samples);					
					}
					return errNo;
				}

				memcpy(audio_buf, converted_input_samples[0], data_size);

				av_freep(&converted_input_samples[0]);
				free(converted_input_samples);
			}

			if (data_size <= 0) {
				/* No data yet, get more frames */
				continue;
			}
			/* We have data, return it and come back for more later */
			return data_size;
		}
		if (pkt.data)
			av_free_packet(&pkt);

		if (quit) {
			return -1;
		}
		if (done && packet_queue_empty(&audioq)) {
			return -1;
		}
		if (packet_queue_get(&audioq, &pkt) < 0) {
			return -1;
		}
		audio_pkt_data = pkt.data;
		audio_pkt_size = pkt.size;
	}
}

// allocate buffer.
JNIEXPORT jobject JNICALL Java_AudioPlayer_getAudioBuffer
(JNIEnv *env, jobject thisObject, jint nByte) {
	nLen = nByte;
	streamAudio = (uint8_t*) malloc(nByte);
	if (!streamAudio) {
		fprintf(stderr, "Failed to allocate stream audio buffer.\n");
		return 0;
	}
	return env->NewDirectByteBuffer((void*) streamAudio, nByte*sizeof(uint8_t));
}

JNIEXPORT jboolean JNICALL Java_AudioPlayer_loadNextFrame
(JNIEnv *env, jobject thisObject) {
	//fprintf(stderr, "Loading next audio frame.\n");

	int len = nLen; // get length of buffer
	uint8_t *stream = streamAudio; // get a write pointer.

	//AVCodecContext *aInCodecCtx = (AVCodecContext *)userdata;
	int len1, audio_size;  

	static uint8_t audio_buf[(MAX_AUDIO_FRAME_SIZE * 3) / 2];
	static unsigned int audio_buf_size = 0;
	static unsigned int audio_buf_index = 0;

	// If we are at the end of the end of the file then do not load another 
	// empty buffer
	if (done && packet_queue_empty(&audioq)) {
		return false;
	}

	while (len > 0) {
		// We still need to read len bytes
		if (audio_buf_index >= audio_buf_size) {
			/* We already sent all our data; get more */
			audio_size = audio_decode_frame(aInCodecCtx, audio_buf, sizeof(audio_buf));

			if (audio_size < 0) {
				/* If error, output silence */
				audio_buf_size = 1024; // arbitrary?
				memset(audio_buf, 0, audio_buf_size); // set silience for the rest
			} else {
				audio_buf_size = audio_size;
			}
			audio_buf_index = 0;
		}
		len1 = audio_buf_size - audio_buf_index;
		
		if (len1 > len) {
			len1 = len;
		}

		memcpy(stream, (uint8_t *)audio_buf + audio_buf_index, len1);
		len -= len1;
		stream += len1;
		audio_buf_index += len1;
	}
	return quit == 0;
}

void decodeLoop() {
	AVPacket packet;
	while (!quit && av_read_frame(pFormatCtx, &packet) >= 0) {
		if (packet.stream_index == iAudioStream) {
			// Decode packet from audio stream
			packet_queue_put(&audioq, &packet);
		} else {
			av_free_packet(&packet);
		}
	}
	// We are done
	done = 1;
}

JNIEXPORT jint JNICALL Java_AudioPlayer_loadAudio
(JNIEnv *env, jobject thisObject, jstring jFileName, jobject audioFormat) {
	if (loadedAudio) {
		Java_AudioPlayer_release(env, thisObject);
	}
	const char *fileName = env->GetStringUTFChars(jFileName, 0);
	int iStream;
	AVCodec *aInCodec = nullptr;
	AVCodec *aOutCodec = nullptr;
	int errNo = 0;
	const char *encodingName = nullptr;
	jclass audioFormatClass = nullptr;
	jfieldID encodingId = nullptr;
	jobject encoding = nullptr;
	jclass encodingClass = nullptr;
	jfieldID encodingNameId = nullptr;
	jstring jEncodingName = nullptr;
	jfieldID bigEndianId = nullptr;
	bool bigEndian = false;
	jfieldID sampleRateId = nullptr;
	float sampleRate = 0;
	jfieldID sampleSizeInBitsId = nullptr;
	int sampleSizeInBits = 0;
	jfieldID channelsId = nullptr;
	int channels = 0;
	jfieldID frameSizeId = nullptr;
	float frameSize = 0;
	jfieldID frameRateId = nullptr;
	float frameRate = 0;
	AVCodecID codecId;
	AVSampleFormat sampleFormat;
	// Get a reference to this object's class
	audioFormatClass = env->GetObjectClass(audioFormat);
	// Get the encoding
	encodingId = env->GetFieldID(audioFormatClass, 
					"encoding", "Ljavax/sound/sampled/AudioFormat$Encoding;");
	if (encodingId == nullptr) {
		fprintf(stderr, "Could not find 'encoding' attribute in AudioFormat.\n");
		return (jint) AVERROR_INVALIDDATA;
	}
	encoding = env->GetObjectField(audioFormat, encodingId);
	if (encoding == nullptr) {
		fprintf(stderr, "Could not find value for 'encoding' attribute in AudioEncoding.\n");
		return (jint) AVERROR_INVALIDDATA;
	}
	encodingClass = env->GetObjectClass(encoding);
	encodingNameId = env->GetFieldID(encodingClass, "name", "Ljava/lang/String;");
	if (encodingNameId == nullptr) {
		fprintf(stderr, "Could not find 'name' in AudioFormat$Encoding");
		return (jint) AVERROR_INVALIDDATA;
	}
	jEncodingName = (jstring) env->GetObjectField(encoding, encodingNameId);
	encodingName = env->GetStringUTFChars(jEncodingName, 0);
	if (strcmp("PCM_UNSIGNED", encodingName) == 0) {
		sampleFormat = AV_SAMPLE_FMT_U8;
	} else if (strcmp("PCM_SIGNED", encodingName) == 0) {
		sampleFormat = AV_SAMPLE_FMT_S16;
	} else {
		fprintf(stderr, "Encoding %s is not supported.\n", encodingName);
		return (jint) AVERROR_INVALIDDATA;
	}
	bigEndianId = env->GetFieldID(audioFormatClass, "bigEndian", "Z");
	bigEndian = (bool) env->GetBooleanField(audioFormat, bigEndianId);
	codecId = av_get_pcm_codec(sampleFormat, bigEndian); // AV_CODEC_ID_PCM_U8, AV_CODEC_ID_PCM_S16LE
	// Get sample rate
	sampleRateId = env->GetFieldID(audioFormatClass, "sampleRate", "F");
	if (sampleRateId == nullptr) {
		fprintf(stderr, "Could not find attribute 'sampleRate' in AudioEncoding.\n");
		return (jint) AVERROR_INVALIDDATA;
	}
	sampleRate = (float) env->GetFloatField(audioFormat, sampleRateId);
	// Get sample size in bits
	sampleSizeInBitsId = env->GetFieldID(audioFormatClass, "sampleSizeInBits", "I");
	if (sampleSizeInBitsId == nullptr) {
		fprintf(stderr, "Could not find attribute 'sampleSizeInBits' in AudioEncoding.\n");
		return (jint) AVERROR_INVALIDDATA;
	}
	sampleSizeInBits = (int) env->GetIntField(audioFormat, sampleSizeInBitsId);
	// Get the number of channels
	channelsId = env->GetFieldID(audioFormatClass, "channels", "I");
	if (channelsId == nullptr) {
		fprintf(stderr, "Could not find attribute 'channelsId' in AudioEncoding.\n");
		return (jint) AVERROR_INVALIDDATA;
	}
	channels = (int) env->GetIntField(audioFormat, channelsId);
	// Get the frame size in B
	frameSizeId = env->GetFieldID(audioFormatClass, "frameSize", "I");
	if (frameSizeId == nullptr) {
		fprintf(stderr, "Could not find attribute 'frameSize' in AudioEncoding.\n");
		return (jint) AVERROR_INVALIDDATA;
	}
	frameSize = (int) env->GetIntField(audioFormat, frameSizeId);
	// Get the frame rate in Hz
	frameRateId = env->GetFieldID(audioFormatClass, "frameRate", "F");
	if (frameRateId == nullptr) {
		fprintf(stderr, "Could not find attribute 'frameRate' in AudioEncoding.\n");
		return (jint) AVERROR_INVALIDDATA;
	}
	frameRate = env->GetFloatField(audioFormat, frameRateId);
	
	// Register all formats and codecs
	av_register_all();

	// Open video file
	if ((errNo = avformat_open_input(&pFormatCtx, fileName, NULL, NULL)) < 0) {
		fprintf(stderr, "Could not open input file '%s'. Error: '%s'.\n",
                fileName, get_error_text(errNo));
		return errNo;
	}

	// Retrieve stream information
	if ((errNo = avformat_find_stream_info(pFormatCtx, NULL)) < 0) {
		fprintf(stderr, "Could not find stream information. Error: '%s'.\n",
                fileName, get_error_text(errNo));
		avformat_close_input(&pFormatCtx);
		return errNo;
	}

	// Dump information about file onto standard error
	av_dump_format(pFormatCtx, 0, fileName, 0);

	// Find the first audio stream
	iAudioStream = -1;
	for (iStream = 0; iStream < pFormatCtx->nb_streams; iStream++) {
		if (pFormatCtx->streams[iStream]->codec->codec_type==AVMEDIA_TYPE_AUDIO) {
			iAudioStream = iStream;
			break;
		}
	}

	if (iAudioStream == -1) {
		fprintf(stderr, "Could not find an audio stream.\n");
		avformat_close_input(&pFormatCtx);
		return AVERROR_EXIT;
	}

	aInCodecCtxOrig = pFormatCtx->streams[iAudioStream]->codec;
	if (!(aInCodec = avcodec_find_decoder(aInCodecCtxOrig->codec_id))) {
		fprintf(stderr, "Could not find input codec!\n");
		avformat_close_input(&pFormatCtx);
		return AVERROR_EXIT;
	}

	if (!(aInCodecCtx = avcodec_alloc_context3(aInCodec))) {
		fprintf(stderr, "Could not allocate a decoding context.\n");
		avformat_close_input(&pFormatCtx);
		return AVERROR_EXIT;
	}

	if ((errNo = avcodec_copy_context(aInCodecCtx, aInCodecCtxOrig)) < 0) {
		fprintf(stderr, "Could not copy codec context. Error: '%s'.\n",
                fileName, get_error_text(errNo));
		avformat_close_input(&pFormatCtx);
		return errNo;
	}

	if ((errNo = avcodec_open2(aInCodecCtx, aInCodec, NULL)) < 0) {
		fprintf(stderr, "Could not open audio codec. Error: '%s'.\n",
                get_error_text(errNo));
		avformat_close_input(&pFormatCtx);
		return errNo;
	}

	packet_queue_init(&audioq);

	// create the output codec (alternative is stero: AV_CODEC_ID_PCM_U8)
	if (!(aOutCodec = avcodec_find_encoder(codecId))) {
		fprintf(stderr, "Could not create output codec.");
		avformat_close_input(&pFormatCtx);
		return AVERROR_EXIT;
	}

	// Allocate the output codec context
    if (!(aOutCodecCtx = avcodec_alloc_context3(aOutCodec))) {
        fprintf(stderr, "Could not allocate an encoding output context\n");
		avformat_close_input(&pFormatCtx);
		return AVERROR_EXIT;
    }

    // Set the sample format
	aOutCodecCtx->sample_fmt = sampleFormat;

	// Set channels, either from input audioFormat or from input codec
	if (channels != 0) {
		aOutCodecCtx->channels = channels;
		aOutCodecCtx->channel_layout = av_get_default_channel_layout(channels);
	} else {
		aOutCodecCtx->channels = aInCodecCtx->channels;
		aOutCodecCtx->channel_layout = aInCodecCtx->channel_layout;
		env->SetIntField(audioFormat, channelsId, aInCodecCtx->channels);
	}

	// Set sample rate, either from input audioFormat or from input codec
	if (sampleRate != 0) {
		aOutCodecCtx->sample_rate = (int) sampleRate;
	} else {
	    aOutCodecCtx->sample_rate = aInCodecCtx->sample_rate;
		env->SetFloatField(audioFormat, sampleRateId, aOutCodecCtx->sample_rate);
	}

	// Set bit rate
	if (frameRate != 0) {
		aOutCodecCtx->bit_rate = (int) frameRate;
	} else {
	    aOutCodecCtx->bit_rate = aInCodecCtx->bit_rate;
		env->SetFloatField(audioFormat, frameRateId, aOutCodecCtx->bit_rate);
	}
	
	// Set the frame size
	env->SetIntField(audioFormat, frameSizeId, 
					 av_get_bytes_per_sample(sampleFormat));

    /** Open the encoder for the audio stream to use it later. */
    if ((errNo = avcodec_open2(aOutCodecCtx, aOutCodec, NULL)) < 0) {
		fprintf(stderr, "Could not open output codec. Error: '%s'.\n",
                get_error_text(errNo));
		avformat_close_input(&pFormatCtx);
        return errNo;
    }

    /** Initialize the resampler to be able to convert audio sample formats. */
    if ((errNo = init_resampler(aInCodecCtx, aOutCodecCtx, &resample_context)) < 0) {
		avformat_close_input(&pFormatCtx);
		return errNo;
	}

	// bits_per_coded_sample is only set after opening the audio codec context
	env->SetIntField(audioFormat, sampleSizeInBitsId, 
					 aOutCodecCtx->bits_per_coded_sample);

	decodingThread = new std::thread(decodeLoop);
	env->ReleaseStringUTFChars(jFileName, fileName);

	loadedAudio = true;

	return 0;
}

JNIEXPORT jstring JNICALL Java_AudioPlayer_getSampleFormat
(JNIEnv *env, jobject thisObject) {
	// sample formats http://ffmpeg.org/doxygen/trunk/group__lavu__sampfmts.html#gaf9a51ca15301871723577c730b5865c5
	AVSampleFormat sampleFormat = aOutCodecCtx->sample_fmt;
	const char* name = av_get_sample_fmt_name(sampleFormat);
	return env->NewStringUTF(name);
}

JNIEXPORT jstring JNICALL Java_AudioPlayer_getCodecName
(JNIEnv *env, jobject thisObject) {
	const char* name = aOutCodecCtx->codec->name;
	return env->NewStringUTF(name);
}

JNIEXPORT jfloat JNICALL Java_AudioPlayer_getSampleRate
(JNIEnv *env, jobject thisObject) {
	return aOutCodecCtx->sample_rate;
}

JNIEXPORT jint JNICALL Java_AudioPlayer_getSampleSizeInBits
(JNIEnv *env, jobject thisObject) {
	return aOutCodecCtx->bits_per_coded_sample;
}

JNIEXPORT jint JNICALL Java_AudioPlayer_getNumberOfChannels
(JNIEnv *env, jobject thisObject) {
	return aOutCodecCtx->channels;
}

JNIEXPORT jint JNICALL Java_AudioPlayer_getFrameSizeInBy
(JNIEnv *env, jobject thisObject) {
	AVSampleFormat sampleFormat = aOutCodecCtx->sample_fmt;
	return av_get_bytes_per_sample(sampleFormat);
}

JNIEXPORT jfloat JNICALL Java_AudioPlayer_getFramesPerSecond
(JNIEnv *env, jobject thisObject) {
	return aOutCodecCtx->sample_rate;
}

JNIEXPORT jboolean JNICALL Java_AudioPlayer_bigEndian
(JNIEnv *env, jobject thisObject) {
    short int number = 0x1;
    char *numPtr = (char*)&number;
    return (numPtr[0] != 1);
}

JNIEXPORT void JNICALL Java_AudioPlayer_release
(JNIEnv *env, jobject thisObject) {
	quit = 1;
	done = 0;

	if (loadedAudio) {
		decodingThread->join();
		delete decodingThread;

		// If the audio queue has been initialized the free it up
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

		aInCodecCtx = nullptr;
		aInCodecCtxOrig = nullptr;
		aOutCodecCtx = nullptr;
		resample_context = nullptr;
		pFormatCtx = nullptr;
		streamAudio = nullptr;
		decodingThread = nullptr;	
	}
	loadedAudio = false;
}