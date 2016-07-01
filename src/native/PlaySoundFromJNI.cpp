#include "PlaySoundFromJNI.h"
#include <stdio.h>
#include <assert.h>
#include <mutex> // std::mutex
#include <condition_variable>  // std::condition_variable
#include <thread> // std::thread
//#include <rational.h>

extern "C" {
	#include <libavcodec/avcodec.h>
	#include <libavformat/avformat.h>
}

// Florian Raudies, 06/30/2016, Mountain View, CA.
// vcvarsall.bat x64
// cl PlaySoundFromJNI.cpp /Fe"..\..\lib\PlaySoundFromJNI" /I"C:\Users\Florian\FFmpeg" /I"C:\Program Files\Java\jdk1.8.0_91\include" /I"C:\Program Files\Java\jdk1.8.0_91\include\win32" /showIncludes /MD /LD /link "C:\Program Files\Java\jdk1.8.0_91\lib\jawt.lib" "C:\Users\Florian\FFmpeg2\libavcodec\avcodec.lib" "C:\Users\Florian\FFmpeg2\libavformat\avformat.lib" "C:\Users\Florian\FFmpeg2\libavutil\avutil.lib"

#define AUDIO_BUFFER_SIZE 1024
#define MAX_AUDIO_FRAME_SIZE 192000
//#define AUDIO_QUEUE_MAX_SIZE 128 // TODO: Implement the max size.

typedef struct PacketQueue {
  AVPacketList *first_pkt, *last_pkt;
  int nb_packets;
  int size;
  std::mutex *mutex;
  std::condition_variable *cond;
} PacketQueue;

PacketQueue		audioq;
int				quit = 0;
int				nLen = 0;
uint8_t			*streamAudio = nullptr;
AVFormatContext *pFormatCtx = NULL;
AVCodecContext  *aCodecCtx = NULL;
AVCodecContext	*aCodecCtxOrig = NULL;
std::thread		*decodingThread = nullptr;
int             iAudioStream = -1;

static void packet_queue_init(PacketQueue *q) {
	q->mutex = new std::mutex;
	q->cond = new std::condition_variable;
	memset(q, 0, sizeof(PacketQueue));
}

static void packet_queue_flush(PacketQueue *q)
{
    AVPacketList *pkt, *pkt1;
    //SDL_LockMutex(q->mutex);
	q->mutex->lock();
    for (pkt = q->first_pkt; pkt; pkt = pkt1) {
        pkt1 = pkt->next;
        av_packet_unref(&pkt->pkt);
        av_freep(&pkt);
    }
    q->last_pkt = NULL;
    q->first_pkt = NULL;
    q->nb_packets = 0;
    q->size = 0;
    //SDL_UnlockMutex(q->mutex);
	q->mutex->unlock();
}

static void packet_queue_destroy(PacketQueue *q)
{
    packet_queue_flush(q);
	delete q->mutex;
	delete q->cond;
}

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

	q->mutex->lock();
	//SDL_LockMutex(q->mutex);

	if (!q->last_pkt)
		q->first_pkt = pkt1;
	else
		q->last_pkt->next = pkt1;

	q->last_pkt = pkt1;
	q->nb_packets++;
	q->size += pkt1->pkt.size;

	//SDL_CondSignal(q->cond);
	q->cond->notify_one();
	//SDL_UnlockMutex(q->mutex);
	q->mutex->unlock();
	
	return 0;
}

static int packet_queue_get(PacketQueue *q, AVPacket *pkt, int block) {
	AVPacketList *pkt1;
	int ret;
	//SDL_LockMutex(q->mutex);
	q->mutex->lock();

	for(;;) {

		if(quit) {
			ret = -1;
			break;
		}

		pkt1 = q->first_pkt;
		if (pkt1) {
			q->first_pkt = pkt1->next;

			if (!q->first_pkt)
				q->last_pkt = NULL;

			q->nb_packets--;
			q->size -= pkt1->pkt.size;
			*pkt = pkt1->pkt;
			av_free(pkt1);
			ret = 1;
			break;
		} else if (!block) {
			ret = 0;
		break;
		} else {
			//SDL_CondWait(q->cond, q->mutex);
			q->cond->wait(std::unique_lock<std::mutex>(*q->mutex), []{return true;});
		}
	}

	//SDL_UnlockMutex(q->mutex);
	q->mutex->unlock();
	return ret;
}

// decodes packet from queue into audio_buf
int audio_decode_frame(AVCodecContext *aCodecCtx, uint8_t *audio_buf, int buf_size) {
	static AVPacket pkt;
	static uint8_t *audio_pkt_data = NULL;
	static int audio_pkt_size = 0;
	static AVFrame frame;

	int len1, data_size = 0;

	for(;;) {
		while(audio_pkt_size > 0) {
			int got_frame = 0;
			len1 = avcodec_decode_audio4(aCodecCtx, &frame, &got_frame, &pkt);
			if(len1 < 0) {
				/* if error, skip frame */
				audio_pkt_size = 0;
				break;
			}
			
			audio_pkt_data += len1;
			audio_pkt_size -= len1;
			data_size = 0;

			if(got_frame) {
				data_size = av_samples_get_buffer_size(NULL, 
								aCodecCtx->channels,
								frame.nb_samples,
								aCodecCtx->sample_fmt,
								1);
				assert(data_size <= buf_size);
				memcpy(audio_buf, frame.data[0], data_size);
			}

			if(data_size <= 0) {
				/* No data yet, get more frames */
				continue;
			}
			/* We have data, return it and come back for more later */
			return data_size;
		}
		if(pkt.data)
			av_free_packet(&pkt);

		if(quit) {
			return -1;
		}

		if(packet_queue_get(&audioq, &pkt, 1) < 0) {
			return -1;
		}

		audio_pkt_data = pkt.data;
		audio_pkt_size = pkt.size;
	}
}

// allocate buffer.
JNIEXPORT jobject JNICALL Java_PlaySoundFromJNI_getAudioBuffer
(JNIEnv *env, jobject thisObject, jint nByte) {
	nLen = nByte;
	streamAudio = (uint8_t*) malloc(nByte); // TODO: add error handling.
	return env->NewDirectByteBuffer((void*) streamAudio, nByte);
}

JNIEXPORT jboolean JNICALL Java_PlaySoundFromJNI_loadNextFrame
(JNIEnv *env, jobject thisObject) {
	int len = nLen; // get length of buffer
	uint8_t *stream = streamAudio; // get a write pointer.

	//AVCodecContext *aCodecCtx = (AVCodecContext *)userdata;
	int len1, audio_size;  

	static uint8_t audio_buf[(MAX_AUDIO_FRAME_SIZE * 3) / 2];
	static unsigned int audio_buf_size = 0;
	static unsigned int audio_buf_index = 0;

	while(len > 0) {
		if(audio_buf_index >= audio_buf_size) {
			/* We have already sent all our data; get more */
			audio_size = audio_decode_frame(aCodecCtx, audio_buf, sizeof(audio_buf));
			if(audio_size < 0) {
			/* If error, output silence */
			audio_buf_size = 1024; // arbitrary?
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
	return true;
}

void decodeLoop() {
	AVPacket packet;
	while(!quit && av_read_frame(pFormatCtx, &packet)>=0) {
		if(packet.stream_index==iAudioStream) {
			packet_queue_put(&audioq, &packet);
			quit = 1; // TODO: remove this!
		} else {
			av_free_packet(&packet);
		}
	}
}

JNIEXPORT void JNICALL Java_PlaySoundFromJNI_loadAudio
(JNIEnv *env, jobject thisObject, jstring jFileName) {
	const char *fileName = env->GetStringUTFChars(jFileName, 0);
	int             i;
	AVCodec         *aCodec = NULL;

	// Register all formats and codecs
	av_register_all();

	// Open video file
	if(avformat_open_input(&pFormatCtx, fileName, NULL, NULL)!=0) {
		fprintf(stderr, "Couldn't open file.\n");
		exit(1);
	}

	// Retrieve stream information
	if(avformat_find_stream_info(pFormatCtx, NULL)<0) {
		fprintf(stderr, "Couldn't find stream information.\n");
		exit(1);		
	}

	// Dump information about file onto standard error
	av_dump_format(pFormatCtx, 0, fileName, 0);

	// Find the first video stream
	iAudioStream = -1;
	for(i=0; i<pFormatCtx->nb_streams; i++) {
		if(pFormatCtx->streams[i]->codec->codec_type==AVMEDIA_TYPE_AUDIO) {
			iAudioStream = i;
			break;
		}
	}
	if(iAudioStream==-1) {
		fprintf(stderr, "Couldn't not find audio stream.\n");
		exit(1);
	}

	aCodecCtxOrig = pFormatCtx->streams[iAudioStream]->codec;
	aCodec = avcodec_find_decoder(aCodecCtxOrig->codec_id);
	if(!aCodec) {
		fprintf(stderr, "Unsupported codec!\n");
		exit(1);
	}

	aCodecCtx = avcodec_alloc_context3(aCodec);
	if(avcodec_copy_context(aCodecCtx, aCodecCtxOrig) != 0) {
		fprintf(stderr, "Couldn't copy codec context");
		exit(1); // Error copying codec context
	}

	// Get audio context // TODO: Check if this needs to be copied.
	aCodecCtx = avcodec_alloc_context3(aCodec);

	avcodec_open2(aCodecCtx, aCodec, NULL);

	packet_queue_init(&audioq);

	decodingThread = new std::thread(decodeLoop);

	env->ReleaseStringUTFChars(jFileName, fileName);
}

JNIEXPORT jstring JNICALL Java_PlaySoundFromJNI_getSampleFormat
(JNIEnv *env, jobject thisObject) {
	// sample formats http://ffmpeg.org/doxygen/trunk/group__lavu__sampfmts.html#gaf9a51ca15301871723577c730b5865c5
	AVSampleFormat sampleFormat = aCodecCtx->sample_fmt;
	const char* name = av_get_sample_fmt_name(sampleFormat);
	return env->NewStringUTF(name);
}

JNIEXPORT jfloat JNICALL Java_PlaySoundFromJNI_getSampleRate
(JNIEnv *env, jobject thisObject) {
	return aCodecCtx->sample_rate;
}

JNIEXPORT jint JNICALL Java_PlaySoundFromJNI_getSampleSizeInBits
(JNIEnv *env, jobject thisObject) {
	return aCodecCtx->bit_rate;
}

JNIEXPORT jint JNICALL Java_PlaySoundFromJNI_getNumberOfChannels
(JNIEnv *env, jobject thisObject) {
	return aCodecCtx->channels;
}

JNIEXPORT jint JNICALL Java_PlaySoundFromJNI_getFrameSizeInBy
(JNIEnv *env, jobject thisObject) {
	AVSampleFormat sampleFormat = aCodecCtx->sample_fmt;
	return aCodecCtx->frame_size * av_get_bytes_per_sample(sampleFormat);
}

JNIEXPORT jfloat JNICALL Java_PlaySoundFromJNI_getFramesPerSecond
(JNIEnv *env, jobject thisObject) {
	// see http://ffmpeg.org/doxygen/trunk/structAVRational.html
	return (float) av_q2d(aCodecCtx->framerate); 
}

JNIEXPORT jboolean JNICALL Java_PlaySoundFromJNI_bigEndian
(JNIEnv *env, jobject thisObject) {
    short int number = 0x1;
    char *numPtr = (char*)&number;
    return (numPtr[0] != 1);
}

JNIEXPORT void JNICALL Java_PlaySoundFromJNI_release
(JNIEnv *env, jobject thisObject) {
	quit = 1;

	decodingThread->join();

	delete decodingThread;

	packet_queue_destroy(&audioq);

	// Close the codec
	avcodec_close(aCodecCtx);
	avcodec_close(aCodecCtxOrig);

	// Close the video file
	avformat_close_input(&pFormatCtx);

	free(streamAudio);
}