#include <jni.h>
#include <stdio.h>
#include <math.h>
#include <assert.h>
#include <vector>

extern "C" {
	#include <libavcodec/avcodec.h>
	#include <libavformat/avformat.h>
	#include <libswscale/swscale.h>
}
#include "PlayImageFromVideo.h"
#include <mutex>
#include <condition_variable>
#include <thread>
// Florian Raudies, 11/06/2016, Mountain View, CA.
// vcvarsall.bat x64
// cl PlayImageFromVideo.cpp /Fe"..\..\lib\PlayImageFromVideo" /I"C:\Users\Florian\FFmpeg" /I"C:\Program Files\Java\jdk1.8.0_91\include" /I"C:\Program Files\Java\jdk1.8.0_91\include\win32" /showIncludes /MD /LD /link "C:\Program Files\Java\jdk1.8.0_91\lib\jawt.lib" "C:\Users\Florian\FFmpeg2\libavcodec\avcodec.lib" "C:\Users\Florian\FFmpeg2\libavformat\avformat.lib" "C:\Users\Florian\FFmpeg2\libavutil\avutil.lib" "C:\Users\Florian\FFmpeg\libswscale\swscale.lib"

#define N_MAX_IMAGES 8
#define N_MAX_BUFFER 4

class ImageBuffer { // ring buffer
	AVFrame** data;
	int nData;
	int iRead;
	int iWrite;
public:
	ImageBuffer(int width, int height) : nData(N_MAX_IMAGES) {
		data = new AVFrame*[nData];
		for (int iData = 0; iData < nData; ++iData) {
			AVFrame* pFrame = av_frame_alloc();
			data[iData] = pFrame;
			int nByte = avpicture_get_size(AV_PIX_FMT_RGB24, width, height);
			uint8_t* bufferShow = (uint8_t*) av_malloc(nByte*sizeof(uint8_t));
			avpicture_fill((AVPicture*)pFrame, bufferShow, AV_PIX_FMT_RGB24,
				 width, height);
		}
		reset();
	}
	virtual ~ImageBuffer() {
		for (int iData = 0; iData < nData; ++iData) {
			AVFrame* pFrame = data[iData];
			av_free(pFrame->data[0]); // free the buffer
			av_free(pFrame);
		}
		delete [] data;
	}
	bool writeFull() {
		return iWrite==nData;
	}
	bool readEmpty() {
		return iRead==nData;
	}
	void reset() {
		iRead = 0;
		iWrite = 0;
	}
	AVFrame* getWritePtr() {
		if (writeFull()) return nullptr;
		AVFrame* pFrame = data[iWrite];
		iWrite += (iWrite != nData);
		return pFrame;
	}
	AVFrame* getReadPtr() {
		if (readEmpty()) return nullptr;
		AVFrame* pFrame = data[iRead];
		iRead += (iRead != nData);
		return pFrame;
	}
};

class ImageBufferBuffer {
	ImageBuffer** data;
	int nData;
	int iRead;
	int iWrite;
	int iDiff;
	std::mutex mu;
	std::condition_variable cv;
public:
	ImageBufferBuffer(int width, int height) : nData(N_MAX_BUFFER) {
		data = new ImageBuffer*[nData];
		for (int iData = 0; iData < nData; ++iData) {
			data[iData] = new ImageBuffer(width, height);
		}
		reset();
	}
	virtual ~ImageBufferBuffer() {
		for (int iData = 0; iData < nData; ++iData) {
			delete data[iData];
		}
		delete [] data;
	}
	void reset() {
		iRead = 0;
		iWrite = 0;
		iDiff = 0;
	}
	AVFrame* getReadPtr() {
		AVFrame* pFrame = nullptr;
		std::unique_lock<std::mutex> locker(mu);
		cv.wait(locker, [this](){return iDiff>(int)data[iRead]->readEmpty();});
		if (data[iRead]->readEmpty()) {
			data[iRead]->reset();
			iRead = (iRead+1) % nData;
			iDiff--;
		}
		pFrame = data[iRead]->getReadPtr();
		mu.unlock();
		cv.notify_one();
		return pFrame;
	}
	AVFrame* getWritePtr() {
		AVFrame* pFrame = nullptr;
		std::unique_lock<std::mutex> locker(mu);
		cv.wait(locker, [this](){return iDiff<(nData-data[iWrite]->writeFull());});
		if (data[iWrite]->writeFull()) {
			iWrite = (iWrite+1) % nData;
			iDiff++;
		}
		pFrame = data[iWrite]->getWritePtr();
		mu.unlock();
		cv.notify_one();
		return pFrame;
	}
};

int					width			= 0;
int					height			= 0;
int					nChannel		= 3;
int					iFrame			= 0;
AVFormatContext		*pFormatCtx		= NULL;
int					iVideoStream	= -1;
AVCodecContext		*pCodecCtx		= NULL;
AVCodec				*pCodec			= NULL;
AVFrame				*pFrame			= NULL;
AVFrame				*pFrameShow		= NULL;
AVPacket			packet;
AVDictionary		*optionsDict	= NULL;
struct SwsContext   *sws_ctx		= NULL;
ImageBufferBuffer	*ibb			= NULL;
std::thread			*decodingThread = NULL;

void loadNextFrame() {
	int frameFinished;
	while(av_read_frame(pFormatCtx, &packet) >= 0) {
		// Is this a packet from the video stream?
		if(packet.stream_index == iVideoStream) {
			// Decode video frame
			avcodec_decode_video2(pCodecCtx, pFrame, &frameFinished, &packet);

			// Did we get a video frame?
			if(frameFinished) {
				// Convert the image from its native format to RGB
				AVFrame* pFrameBuffer = ibb->getWritePtr(); // get next may block
				sws_scale
				(
					sws_ctx,
					(uint8_t const * const *)pFrame->data,
					pFrame->linesize,
					0,
					pCodecCtx->height,
					pFrameBuffer->data,
					pFrameBuffer->linesize
				);
				fprintf(stdout,"Read frame %d.\n",iFrame++);
			}
			// Free the packet that was allocated by av_read_frame
			av_free_packet(&packet);
		}
	}
}

JNIEXPORT void JNICALL Java_PlayImageFromVideo_setPlaybackSpeed
(JNIEnv *env, jobject thisObject, jfloat speed) {

}

JNIEXPORT void JNICALL Java_PlayImageFromVideo_setTime
(JNIEnv *env, jobject thisObject, jfloat time) {

}


JNIEXPORT jobject JNICALL Java_PlayImageFromVideo_getFrameBuffer
(JNIEnv *env, jobject thisObject) {
	return env->NewDirectByteBuffer((void*) pFrameShow->data[0], width*height*nChannel*sizeof(uint8_t));
}

JNIEXPORT void JNICALL Java_PlayImageFromVideo_loadNextFrame
(JNIEnv *env, jobject thisObject) {
	// TODO: Add clock here!
	pFrameShow = ibb->getReadPtr();
}

// Opens movie file and loads first frame.
JNIEXPORT void JNICALL Java_PlayImageFromVideo_loadMovie
(JNIEnv *env, jobject thisObject, jstring jFileName) {
	const char *fileName = env->GetStringUTFChars(jFileName, 0);

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
	iVideoStream = -1;
	for(int i = 0; i < pFormatCtx->nb_streams; i++)
		if(pFormatCtx->streams[i]->codec->codec_type==AVMEDIA_TYPE_VIDEO) {
		iVideoStream=i;
		break;
	}

	if(iVideoStream == -1) {
		fprintf(stderr, "Didn't find a video stream.\n");
		exit(1);
	}

	// Get a pointer to the codec context for the video stream
	pCodecCtx = pFormatCtx->streams[iVideoStream]->codec;

	// Find the decoder for the video stream
	pCodec = avcodec_find_decoder(pCodecCtx->codec_id);
	if(pCodec == NULL) {
		fprintf(stderr, "Unsupported codec!\n");
		exit(1);
	}
	// Open codec
	if(avcodec_open2(pCodecCtx, pCodec, &optionsDict)<0) {
		fprintf(stderr, "Could not open codec.\n");
		exit(1);
	}

	// Allocate video frame
	pFrame = av_frame_alloc();

	sws_ctx = sws_getContext
		(
			pCodecCtx->width,
			pCodecCtx->height,
			pCodecCtx->pix_fmt,
			pCodecCtx->width,
			pCodecCtx->height,
			AV_PIX_FMT_RGB24,
			SWS_BILINEAR,
			NULL,
			NULL,
			NULL
		);

	width = pCodecCtx->width;
	height = pCodecCtx->height;

	ibb = new ImageBufferBuffer(width, height);

	decodingThread = new std::thread(loadNextFrame);

	env->ReleaseStringUTFChars(jFileName, fileName);

	pFrameShow = ibb->getReadPtr(); // load the first frame from the buffer
}

JNIEXPORT jint JNICALL Java_PlayImageFromVideo_getMovieHeight
(JNIEnv *env, jobject thisObject) {
	return (jint) height;
}

JNIEXPORT jint JNICALL Java_PlayImageFromVideo_getMovieWidth
(JNIEnv *env, jobject thisObject) {
	return (jint) width;
}


JNIEXPORT void JNICALL Java_PlayImageFromVideo_release
(JNIEnv *env, jobject thisObject) {
	decodingThread->join();

	delete ibb;

	// Free the YUV frame
	av_free(pFrame);

	// Close the codec
	avcodec_close(pCodecCtx);

	// Close the video file
	avformat_close_input(&pFormatCtx);  
}