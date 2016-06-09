#include <jni.h>
#include <stdio.h>
#include <math.h>
extern "C" {
	#include <libavcodec/avcodec.h>
	#include <libavformat/avformat.h>
	#include <libswscale/swscale.h>
}
#include "DisplayImageFromVideo.h"

// Florian Raudies, 06/06/2016, Mountain View, CA.
// vcvarsall.bat x64
// cl DisplayImageFromVideo.cpp /Fe"..\..\lib\DisplayImageFromVideo" /I"C:\Users\Florian\FFmpeg" /I"C:\Program Files\Java\jdk1.8.0_91\include" /I"C:\Program Files\Java\jdk1.8.0_91\include\win32" /showIncludes /MD /LD /link "C:\Program Files\Java\jdk1.8.0_91\lib\jawt.lib" "C:\Users\Florian\FFmpeg2\libavcodec\avcodec.lib" "C:\Users\Florian\FFmpeg2\libavformat\avformat.lib" "C:\Users\Florian\FFmpeg2\libavutil\avutil.lib" "C:\Users\Florian\FFmpeg\libswscale\swscale.lib"

typedef unsigned char BYTE;
BYTE				*frameData;
int					width = 0;
int					height = 0;
int					nChannel = 3;
AVFormatContext *pFormatCtx = NULL;
int             i, videoStream;
AVCodecContext  *pCodecCtx = NULL;
AVCodec         *pCodec = NULL;
AVFrame         *pFrame = NULL; 
AVFrame         *pFrameRGB = NULL;
AVPacket        packet;
int             frameFinished;
int             numBytes;
uint8_t         *buffer = NULL;
AVDictionary    *optionsDict = NULL;
struct SwsContext      *sws_ctx = NULL;

void loadNextFrame() {
	while(av_read_frame(pFormatCtx, &packet)>=0) {
		// Is this a packet from the video stream?
		if(packet.stream_index==videoStream) {
			// Decode video frame
			avcodec_decode_video2(pCodecCtx, pFrame, &frameFinished, &packet);

			// Did we get a video frame?
			if(frameFinished) {
				// Convert the image from its native format to RGB
				sws_scale
				(
					sws_ctx,
					(uint8_t const * const *)pFrame->data,
					pFrame->linesize,
					0,
					pCodecCtx->height,
					pFrameRGB->data,
					pFrameRGB->linesize
				);

				frameData = pFrameRGB->data[0];
				width = pCodecCtx->width;
				height = pCodecCtx->height;
				av_free_packet(&packet);
				return;
			}
			// Free the packet that was allocated by av_read_frame
			av_free_packet(&packet);
		}
	}
}


JNIEXPORT jobject JNICALL Java_DisplayImageFromVideo_getFrameBuffer
(JNIEnv *env, jobject thisObject) {
	return env->NewDirectByteBuffer((void*) frameData, width*height*nChannel*sizeof(BYTE));
}

JNIEXPORT void JNICALL Java_DisplayImageFromVideo_loadNextFrame
(JNIEnv *, jobject) {
	loadNextFrame();
}

// Opens movie file and loads first frame.
JNIEXPORT void JNICALL Java_DisplayImageFromVideo_loadMovie
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
  videoStream=-1;
  for(i=0; i<pFormatCtx->nb_streams; i++)
    if(pFormatCtx->streams[i]->codec->codec_type==AVMEDIA_TYPE_VIDEO) {
      videoStream=i;
      break;
    }
if(videoStream==-1) {
	fprintf(stderr, "Didn't find a video stream.\n");
	exit(1);
}
  
  // Get a pointer to the codec context for the video stream
  pCodecCtx=pFormatCtx->streams[videoStream]->codec;
  
  // Find the decoder for the video stream
  pCodec=avcodec_find_decoder(pCodecCtx->codec_id);
  if(pCodec==NULL) {
    fprintf(stderr, "Unsupported codec!\n");
	exit(1);
  }
  // Open codec
  if(avcodec_open2(pCodecCtx, pCodec, &optionsDict)<0) {
	fprintf(stderr, "Could not open codec.\n");
	exit(1);
  }
  
  // Allocate video frame
  pFrame=av_frame_alloc();
  
  // Allocate an AVFrame structure
  pFrameRGB=av_frame_alloc();
  if(pFrameRGB==NULL) {
	fprintf(stderr, "Could not allocate RGB frame.\n");
	exit(1);
  }
  
  // Determine required buffer size and allocate buffer
  numBytes=avpicture_get_size(AV_PIX_FMT_RGB24, pCodecCtx->width,
			      pCodecCtx->height);
  buffer=(uint8_t *)av_malloc(numBytes*sizeof(uint8_t));

  sws_ctx =
    sws_getContext
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
  
  // Assign appropriate parts of buffer to image planes in pFrameRGB
  // Note that pFrameRGB is an AVFrame, but AVFrame is a superset
  // of AVPicture
  avpicture_fill((AVPicture *)pFrameRGB, buffer, AV_PIX_FMT_RGB24,
		 pCodecCtx->width, pCodecCtx->height);
  
	loadNextFrame();

	env->ReleaseStringUTFChars(jFileName, fileName);
}

JNIEXPORT jint JNICALL Java_DisplayImageFromVideo_getMovieHeight
(JNIEnv *env, jobject thisObject) {
	return (jint) height;
}

JNIEXPORT jint JNICALL Java_DisplayImageFromVideo_getMovieWidth
(JNIEnv *env, jobject thisObject) {
	return (jint) width;
}


JNIEXPORT void JNICALL Java_DisplayImageFromVideo_release
(JNIEnv *env, jobject thisObject) {
  // Free the RGB image
  av_free(buffer);
  av_free(pFrameRGB);
  
  // Free the YUV frame
  av_free(pFrame);
  
  // Close the codec
  avcodec_close(pCodecCtx);
  
  // Close the video file
  avformat_close_input(&pFormatCtx);  
}