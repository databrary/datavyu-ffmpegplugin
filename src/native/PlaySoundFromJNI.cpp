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

int				nLen = 0;
uint8_t			*streamAudio = nullptr;
AVFormatContext *pFormatCtx = NULL;
AVCodecContext  *aCodecCtx = NULL;
AVCodecContext	*aCodecCtxOrig = NULL;
std::thread		*decodingThread = nullptr;
int             iAudioStream = -1;

// allocate buffer.
JNIEXPORT jobject JNICALL Java_PlaySoundFromJNI_getAudioBuffer
(JNIEnv *env, jobject thisObject, jint nByte) {
	nLen = nByte;
	streamAudio = (uint8_t*) malloc(nByte); // TODO: add error handling.
	return env->NewDirectByteBuffer((void*) streamAudio, nByte*sizeof(uint8_t));
}

JNIEXPORT jboolean JNICALL Java_PlaySoundFromJNI_loadNextFrame
(JNIEnv *env, jobject thisObject) {
	return true;
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
		fprintf(stderr, "Couldn't copy codec context.\n");
		exit(1);
	}

	if(avcodec_open2(aCodecCtx, aCodec, NULL)<0) {
		fprintf(stderr, "Could not open audio codec.\n");
		exit(1);
	}

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
	return aCodecCtx->bits_per_coded_sample;
}

JNIEXPORT jint JNICALL Java_PlaySoundFromJNI_getNumberOfChannels
(JNIEnv *env, jobject thisObject) {
	return aCodecCtx->channels;
}

JNIEXPORT jint JNICALL Java_PlaySoundFromJNI_getFrameSizeInBy
(JNIEnv *env, jobject thisObject) {
	AVSampleFormat sampleFormat = aCodecCtx->sample_fmt;
	return av_get_bytes_per_sample(sampleFormat);
}

JNIEXPORT jfloat JNICALL Java_PlaySoundFromJNI_getFramesPerSecond
(JNIEnv *env, jobject thisObject) {
	//fprintf(stderr, "Framerate numerator %d.\n", aCodecCtx->framerate.num);
	//fprintf(stderr, "Framerate denumerator %d.\n", aCodecCtx->framerate.den);
	// see http://ffmpeg.org/doxygen/trunk/structAVRational.html
	//return (float) av_q2d(aCodecCtx->framerate); 
	// Makes only sense for video.
	return aCodecCtx->sample_rate;
}

JNIEXPORT jboolean JNICALL Java_PlaySoundFromJNI_bigEndian
(JNIEnv *env, jobject thisObject) {
    short int number = 0x1;
    char *numPtr = (char*)&number;
    return (numPtr[0] != 1);
}

JNIEXPORT void JNICALL Java_PlaySoundFromJNI_release
(JNIEnv *env, jobject thisObject) {
	// Close the codec
	avcodec_close(aCodecCtx);
	avcodec_close(aCodecCtxOrig);

	// Close the video file
	avformat_close_input(&pFormatCtx);

	free(streamAudio);
}