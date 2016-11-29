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
#include <chrono>

using namespace std::chrono;

// Florian Raudies, Mountain View, CA.
// vcvarsall.bat x64
// cl PlayImageFromVideo.cpp /Fe"..\..\lib\PlayImageFromVideo" /I"C:\Users\Florian\FFmpeg" /I"C:\Program Files\Java\jdk1.8.0_91\include" /I"C:\Program Files\Java\jdk1.8.0_91\include\win32" /showIncludes /MD /LD /link "C:\Program Files\Java\jdk1.8.0_91\lib\jawt.lib" "C:\Users\Florian\FFmpeg2\libavcodec\avcodec.lib" "C:\Users\Florian\FFmpeg2\libavformat\avformat.lib" "C:\Users\Florian\FFmpeg2\libavutil\avutil.lib" "C:\Users\Florian\FFmpeg\libswscale\swscale.lib"

#define N_MAX_IMAGES 32 // ATTENTION BUFFER SHOULD BE LARGER THAN FPS FOR REVERSE PLAYBACK!!!
#define N_MAX_BUFFER 6
#define AV_SYNC_THRESHOLD 0.01
#define AV_NOSYNC_THRESHOLD 10.0

class ImageBufferBuffer; // forward declaration for linker.

int					width			= 0;
int					height			= 0;
int					nChannel		= 3;
AVFormatContext		*pFormatCtx		= NULL;
int					iVideoStream	= -1;
AVStream			*pVideoStream	= NULL;
AVCodecContext		*pCodecCtx		= NULL;
AVCodec				*pCodec			= NULL;
AVFrame				*pFrame			= NULL;
AVFrame				*pFrameShow		= NULL;
AVPacket			packet;
AVDictionary		*optionsDict	= NULL;
struct SwsContext   *sws_ctx		= NULL;
std::thread			*decodingThread = NULL;
bool				quit			= false;
ImageBufferBuffer	*ibb			= NULL;

// Playback speed and reverse playback.
bool				reverse			= false;
double				speed			= 1;
bool				init			= true; // on reset set to true
int64_t				last_read_pts	= 0; // on reset set to 0
int64_t				delta_pts		= 0;
double				diff			= 0; // on reset set to 0
high_resolution_clock::time_point last_time;

// Random seeking within the video.
bool				seek_req		= false;
int64_t				seek_time		= 0;
int					seek_flags		= AVSEEK_FLAG_ANY;
bool				seek_reverse	= false;
int64_t				frame_no		= 0; // Have the current frame number.


class ImageBuffer {
	AVFrame** data;
	int nData;
	int iRead;
	int iWrite;
	bool reverse;
public:
	ImageBuffer(int width, int height) : nData(N_MAX_IMAGES), reverse(false) {
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
	bool writeFull() const {
		return iWrite == nData;
	}
	bool readEmpty() const {
		return reverse ? iRead == 0 : iRead == nData;
	}
	void reset() {
		iRead = reverse ? nData : 0;
		iWrite = 0;
	}
	void setReverse(bool r) {
		reverse = r;
		reset();
	}
	AVFrame* getWritePtr() {
		if (writeFull()) return nullptr;
		AVFrame* pFrame = data[iWrite++];
		return pFrame;
	}
	AVFrame* getReadPtr() {
		if (readEmpty()) return nullptr;
		AVFrame* pFrame = reverse ? data[--iRead] : data[iRead++];
		return pFrame;
	}
};

// A ring buffer where reader and writer never work on the same buffer 
// to support reversed read/write.
class ImageBufferBuffer {
	ImageBuffer** data;
	int nData;
	int iRead;
	int iWrite;
	int iDiff;
	bool flush;
	bool locked;
	std::mutex mu;
	std::condition_variable cv;
public:
	ImageBufferBuffer(int width, int height) : nData(N_MAX_BUFFER), flush(false), locked(false) {
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
	void setReverse(bool r) {
		for (int iData = 0; iData < nData; ++iData) {
			data[iData]->setReverse(r);
		}
	}
	void reset() {
		for (int iData = 0; iData < nData; ++iData) {
			data[iData]->reset();
		}
		iRead = 0;
		iWrite = 0;
		iDiff = 0;
	}
	void lock() {
		locked = true;
	}
	void unlock() {
		locked = false;
		cv.notify_all();
	}
	void doFlush() {
		flush = true;
		cv.notify_all();
		std::unique_lock<std::mutex> locker(mu);
		reset();
		locker.unlock();
		flush = false;
	}
	AVFrame* getReadPtr() {
		AVFrame* pFrame = nullptr;
		std::unique_lock<std::mutex> locker(mu);
		cv.wait(locker, [this](){return (iDiff>1 && !locked) || flush;});
		if (!flush) {
			if (data[iRead]->readEmpty()) {
				data[iRead]->reset();
				iRead = (iRead+1) % nData;
				iDiff--;
			}
			pFrame = data[iRead]->getReadPtr();
		}
		locker.unlock();
		cv.notify_all();
		return pFrame;
	}
	AVFrame* getWritePtr() {
		AVFrame* pFrame = nullptr;
		std::unique_lock<std::mutex> locker(mu);
		cv.wait(locker, [this](){return (iDiff<(nData-1) && !locked) || flush;});
		if (!flush) {
			if (data[iWrite]->writeFull()) {
				iWrite = (iWrite+1) % nData;
				iDiff++;
			}
			pFrame = data[iWrite]->getWritePtr();
		}
		locker.unlock();
		cv.notify_all();
		return pFrame;
	}
	bool writeFull() const {
		return data[iWrite]->writeFull();
	}
};

void loadNextFrame() {
	int frameFinished;
	bool do_reverse = false;

	while(!quit) {

		// Random seeking within the video stream.
		if (seek_req) {
			if (reverse) {
				seek_flags |= AVSEEK_FLAG_BACKWARD;
			} else {
				seek_flags &= ~AVSEEK_FLAG_BACKWARD;
			}
			if (av_seek_frame(pFormatCtx, iVideoStream, seek_time, seek_flags) < 0) {
				fprintf(stderr, "Random seek of %I64d time unsuccessful.\n", seek_time);
			} else {
				ibb->lock();
				ibb->doFlush();
				avcodec_flush_buffers(pCodecCtx);
				init = true;
				last_read_pts = 0;
				diff = 0;
				ibb->unlock();
			}
			seek_req = false;
		}
		

		// Need to seek forward by 2 x N_MAX_IMAGES.
		if (reverse && ibb->writeFull() && do_reverse) {

			seek_flags = AVSEEK_FLAG_ANY;
			seek_flags |= AVSEEK_FLAG_FRAME;
			seek_flags |= AVSEEK_FLAG_BACKWARD;

			frame_no -= 2*N_MAX_IMAGES;
			seek_time = frame_no*delta_pts;

			if (frame_no < 0) {
				// Reached the start of the file. TODO: Partial load into buffer.
				break;
			}

			if (av_seek_frame(pFormatCtx, iVideoStream, seek_time, seek_flags) < 0) {
				fprintf(stderr, "Reverse seek of %I64d time unsuccessful.\n", seek_time);
			} else {
				avcodec_flush_buffers(pCodecCtx);
			}

			do_reverse = false;
		}		

		if (av_read_frame(pFormatCtx, &packet) < 0) {
			// Reached the end of file.
			break;
		}

		// Is this a packet from the video stream?
		if(packet.stream_index == iVideoStream) {
			
			// Decode the video frame.
			avcodec_decode_video2(pCodecCtx, pFrame, &frameFinished, &packet);
			
			// Did we get a full video frame?
			if(frameFinished) {
				do_reverse = true;

				frame_no++;

				// Get the next writeable buffer (this may block and can be unblocked with a flush)
				AVFrame* pFrameBuffer = ibb->getWritePtr();

				// Did we get a frame buffer?
				if (pFrameBuffer) {

					// Convert the image from its native format into RGB.
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

					// Set the presentation time stamp.
					pFrameBuffer->pts = packet.dts == AV_NOPTS_VALUE ? 0 
						: av_frame_get_best_effort_timestamp(pFrame);
					pFrameBuffer->repeat_pict = pFrame->repeat_pict;

					// Reset frame container to initial state.
					av_frame_unref(pFrame);
				}
			}
		}

		// Free the packet that was allocated by av_read_frame.
		av_free_packet(&packet);
	}
}

JNIEXPORT void JNICALL Java_PlayImageFromVideo_setPlaybackSpeed
(JNIEnv *env, jobject thisObject, jfloat inSpeed) {	
	
	if (reverse != (inSpeed<0) ) {
		reverse = inSpeed < 0;
		ibb->setReverse(reverse);
		seek_time = frame_no*3003;
		seek_req = true;
	}
	
	speed = fabs(inSpeed);
}

JNIEXPORT void JNICALL Java_PlayImageFromVideo_setTime
(JNIEnv *env, jobject thisObject, jfloat time) { // time in seconds

	seek_flags = AVSEEK_FLAG_ANY;
	
	seek_time = time < 0 ? pVideoStream->duration-1 : (int64_t)(time/av_q2d(pVideoStream->time_base));
	
	seek_time = seek_time > pVideoStream->duration-1 ? pVideoStream->duration-1 : seek_time;


	frame_no = (int64_t)(time/(delta_pts*av_q2d(pVideoStream->time_base)));

	seek_req = true;

	/*
	fprintf(stdout, "Time is %f sec and %I64d in stream time base units.\n", time, seek_time);

	fprintf(stdout, "Duration from format context %I64d and from stream %I64d.\n", 
		pFormatCtx->duration, pVideoStream->duration);

	fprintf(stdout, "Time base of codec %f and of stream %f.\n",
		av_q2d(pCodecCtx->time_base), av_q2d(pVideoStream->time_base));

	fprintf(stdout, "Number of frames %I64d.\n", pVideoStream->nb_frames);
	*/
}


JNIEXPORT jobject JNICALL Java_PlayImageFromVideo_getFrameBuffer
(JNIEnv *env, jobject thisObject) {
	return env->NewDirectByteBuffer((void*) pFrameShow->data[0], 
									width*height*nChannel*sizeof(uint8_t));
}

JNIEXPORT jint JNICALL Java_PlayImageFromVideo_loadNextFrame
(JNIEnv *env, jobject thisObject) {
	int nFrame = 0;
	AVFrame *pFrameTmp = ibb->getReadPtr();	

	if (pFrameTmp) {
		nFrame++;

		//fprintf(stdout,"pts diff = %I64d.\n", fabs(pFrameTmp->pts - last_read_pts));
		//fflush(stdout);

		double pts_diff = init ? 0 : fabs(pFrameTmp->pts - last_read_pts)/speed*av_q2d(pVideoStream->time_base);

		auto time = high_resolution_clock::now();
		double time_diff = init ? 0 : duration_cast<microseconds>(time-last_time).count()/1000000.0;

		diff += pts_diff - time_diff;
		double delay = pts_diff;
		if (pFrameTmp->repeat_pict) {
			delay += delay/2;
		}
		double sync_threshold = (delay > AV_SYNC_THRESHOLD) ? delay : AV_SYNC_THRESHOLD;
		if(fabs(diff) < AV_NOSYNC_THRESHOLD) {
			if (diff <= -sync_threshold) {
				AVFrame *pFrameTmp2 = ibb->getReadPtr();
				if (pFrameTmp2) {
					pFrameTmp = pFrameTmp2;
					nFrame++;
				}
			} else if (diff < 0) {
				delay = 0;
			} else if (diff >= sync_threshold) {
				delay *= 2;
			}
		}

		// Delay read to keep the desired frame rate.
		if (delay>0) {
			std::this_thread::sleep_for(milliseconds((int)(delay*1000+0.5)));
		}

		// Update show pointer
		pFrameShow = pFrameTmp;

		// save for next time
		delta_pts = abs(pFrameTmp->pts - last_read_pts);
		last_read_pts = pFrameTmp->pts;
		last_time = time;
		init = false;
	}
	return (jint) nFrame;
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

	pVideoStream = pFormatCtx->streams[iVideoStream];

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

	delta_pts = (int64_t)(1.0/(av_q2d(pVideoStream->time_base)*av_q2d(pVideoStream->avg_frame_rate)));

	//fprintf(stdout, "Average frame rate = %f.\n", av_q2d(pVideoStream->avg_frame_rate));
	//fflush(stdout);
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

	// Set the quit flag for the docding thread.
	quit = true;

	// Flush the image buffer buffer (which unblocks all readers/writers).
	ibb->doFlush();

	// Join the decoding thread with this one.
	decodingThread->join();

	// Free the decoding thread.
	delete decodingThread;
	
	// Free the image buffer buffer.
	delete ibb;

	// Free the YUV frame
	av_free(pFrame);

	// Close the video file
	avformat_close_input(&pFormatCtx);
}