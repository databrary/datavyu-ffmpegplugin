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
#include <algorithm>
#include <cstdlib>
// Florian Raudies, Mountain View, CA.
// vcvarsall.bat x64
// cl PlayImageFromVideo.cpp /Fe"..\..\lib\PlayImageFromVideo" /I"C:\Users\Florian\FFmpeg" /I"C:\Program Files\Java\jdk1.8.0_91\include" /I"C:\Program Files\Java\jdk1.8.0_91\include\win32" /showIncludes /MD /LD /link "C:\Program Files\Java\jdk1.8.0_91\lib\jawt.lib" "C:\Users\Florian\FFmpeg2\libavcodec\avcodec.lib" "C:\Users\Florian\FFmpeg2\libavformat\avformat.lib" "C:\Users\Florian\FFmpeg2\libavutil\avutil.lib" "C:\Users\Florian\FFmpeg\libswscale\swscale.lib"

#define N_MAX_IMAGES 32 // ATTENTION BUFFER SHOULD BE LARGER THAN FPS FOR REVERSE PLAYBACK!!!
#define N_MAX_BUFFER 6
#define AV_SYNC_THRESHOLD 0.01
#define AV_NOSYNC_THRESHOLD 10.0
#define PTS_DELTA_THRESHOLD 3

class ImageBufferBuffer; // forward declaration for linker.

// Basic information about the movie.
int					width			= 0;
int					height			= 0;
int					nChannel		= 3;
double				duration		= 0;
long				nFrame			= 0;
int64_t				iFrame			= 0; // Current frame number.

AVFormatContext		*pFormatCtx		= nullptr;
int					iVideoStream	= -1;
AVStream			*pVideoStream	= nullptr;
AVCodecContext		*pCodecCtx		= nullptr;
AVCodec				*pCodec			= nullptr;
AVFrame				*pFrame			= nullptr; // use opaque pointer for frame no.
AVFrame				*pFrameShow		= nullptr;
AVPacket			packet;
AVDictionary		*optsDict		= nullptr;
struct SwsContext   *swsCtx			= nullptr;
std::thread			*decodeThread	= nullptr;
bool				quit			= false;
ImageBufferBuffer	*ibb			= nullptr;
bool				loadedMovie		= false;

// Playback speed and reverse playback.
std::mutex			playback;
bool				reversePlay		= false;
double				speed			= 1;
//bool				init			= true; // on reset set to true
int64_t				lastPts			= 0; // on reset set to 0
int64_t				deltaPts		= 0;
double				diff			= 0; // on reset set to 0
std::chrono::high_resolution_clock::time_point lastTime;

// Random seeking within the video.
bool				seekReq			= false;
int64_t				seekTime		= 0;
int					seekFlags		= AVSEEK_FLAG_ANY;


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
			pFrame->opaque = new int64_t();
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
			delete pFrame->opaque;
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
		} else {
			fprintf(stdout,"No read pointer but flush.\n");
			fflush(stdout);
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

inline int64_t getShowFrame() {
	return (pFrameShow != nullptr) ? *((int64_t*)pFrameShow->opaque) : 0;
}

void loadNextFrame() {
	int frameFinished;
	bool reverseRefresh = false;

	while (!quit) {

		// Random seeking within the video stream.
		if (seekReq) {
			bool reverse = seekTime < iFrame*deltaPts;
			reverseRefresh = reverse;
			if (reverse) {
				seekFlags |= AVSEEK_FLAG_BACKWARD;
			} else {
				seekFlags &= ~AVSEEK_FLAG_BACKWARD;
			}
			if (av_seek_frame(pFormatCtx, iVideoStream, seekTime, seekFlags) < 0) {
				fprintf(stderr, "Random seek of %I64d time unsuccessful.\n", seekTime);
			} else {
				ibb->lock();
				ibb->setReverse(reversePlay);
				ibb->doFlush();
				avcodec_flush_buffers(pCodecCtx);
				ibb->unlock();
			}
			seekReq = false;
		}
		

		// Need to seek forward by 2 x N_MAX_IMAGES.
		if (reversePlay && ibb->writeFull() && reverseRefresh) {

			seekFlags |= AVSEEK_FLAG_BACKWARD;

			iFrame -= 2*N_MAX_IMAGES;
			seekTime = iFrame*deltaPts;

			if (iFrame >= 0) {
				if (av_seek_frame(pFormatCtx, iVideoStream, seekTime, seekFlags) < 0) {
					fprintf(stderr, "Reverse seek of %I64d time unsuccessful.\n", seekTime);
				} else {
					avcodec_flush_buffers(pCodecCtx);
				}
			} // otherwise reached start of file.

			reverseRefresh = false;
		}		

		if (av_read_frame(pFormatCtx, &packet) < 0) {
			// Reached the end of file.
			fprintf(stdout,"Reached end/beginning of file.\n");
			fflush(stdout);

			std::this_thread::sleep_for(std::chrono::milliseconds(500));

		} else {
			// Is this a packet from the video stream?
			if (packet.stream_index == iVideoStream) {
				
				// Decode the video frame.
				avcodec_decode_video2(pCodecCtx, pFrame, &frameFinished, &packet);
				
				// Did we get a full video frame?
				if(frameFinished) {
					reverseRefresh = true;

					// Get the next writeable buffer (this may block and can be unblocked with a flush)
					AVFrame* pFrameBuffer = ibb->getWritePtr();

					// Did we get a frame buffer?
					if (pFrameBuffer) {

						// Convert the image from its native format into RGB.
						sws_scale
						(
							swsCtx,
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
						*((int64_t*)(pFrameBuffer->opaque)) = iFrame;

						// Increase the frame count.
						iFrame++;

						// Reset frame container to initial state.
						av_frame_unref(pFrame);
					}
				}
				// Free the packet that was allocated by av_read_frame.
				av_free_packet(&packet);
			}
		}
	}
}

JNIEXPORT void JNICALL Java_PlayImageFromVideo_setPlaybackSpeed
(JNIEnv *env, jobject thisObject, jfloat inSpeed) {

	if (loadedMovie) {

		// Playing direction changed -- may jump a bit because of buffer.
		if (reversePlay != (inSpeed < 0)) {
			reversePlay = inSpeed < 0;
			Java_PlayImageFromVideo_setTimeInFrames(env, thisObject, iFrame);
		}

		speed = fabs(inSpeed);
	}
}

JNIEXPORT void JNICALL Java_PlayImageFromVideo_setTimeInSeconds
(JNIEnv *env, jobject thisObject, jdouble time) { // time in seconds

	Java_PlayImageFromVideo_setTimeInFrames(env, thisObject, 
		(jlong)(time/(deltaPts*av_q2d(pVideoStream->time_base))));
}

JNIEXPORT void JNICALL Java_PlayImageFromVideo_setTimeInFrames
(JNIEnv *env, jobject thisObject, jlong time) {

	// If time is out of range given the buffer bring it into range.
	int64_t iFrameNew = reversePlay ? std::max(time, (jlong)(2*N_MAX_IMAGES)) 
		: std::min(time, (jlong)(nFrame-4*N_MAX_IMAGES)); // in frames
	// TODO: Check why we have the large minimum buffer at the end!!

	// Set the new frame.
	iFrame = iFrameNew; // in frames
	
	// Compute the seek time.
	seekTime = iFrame*deltaPts; // in time base units.

	// Issue a seek request.
	seekReq = true;
}


JNIEXPORT jobject JNICALL Java_PlayImageFromVideo_getFrameBuffer
(JNIEnv *env, jobject thisObject) {

	// Construct a new direct byte buffer pointing to data from pFrameShow.
	return loadedMovie ? env->NewDirectByteBuffer((void*) pFrameShow->data[0], 
									width*height*nChannel*sizeof(uint8_t))
									: 0;
}

JNIEXPORT jint JNICALL Java_PlayImageFromVideo_loadNextFrame
(JNIEnv *env, jobject thisObject) {
	
	// No movie was loaded return -1.
	if (!loadedMovie) return -1;

	// Counts the number of frames that this method requested (could be 0, 1, 2).
	int nFrame = 0;

	// Get the next read pointer.
	AVFrame *pFrameTmp = ibb->getReadPtr();

	// We received a frame (no flushing).
	if (pFrameTmp) {

		// Retrieve the presentation time for this first frame.
		uint64_t firstPts = pFrameTmp->pts;

		// Increase the number of read frames by one.
		nFrame++;

		// Initialize if the pts difference is above threshold as a result of a seek.
		bool init = std::labs(firstPts - lastPts) > PTS_DELTA_THRESHOLD*deltaPts;

		// Compute the difference for the presentation time stamps.
		double ptsDiff = init ? 0 : std::labs(firstPts - lastPts)/speed*av_q2d(pVideoStream->time_base);

		// Get the current time.
		auto time = std::chrono::high_resolution_clock::now();

		// Compute the time difference.
		double timeDiff = init ? 0 
			: std::chrono::duration_cast<std::chrono::microseconds>(time-lastTime).count()/1000000.0;

		// Compute the difference between times and pts.
		diff = init ? 0 : (diff + ptsDiff - timeDiff);

		fprintf(stdout, "firstPts = %I64d, lastPts = %I64d, ptsDiff = %f, pts diff = %ld, deltaPts = %I64d, timeDiff = %f, diff = %f, init = %d.\n", 
			firstPts, lastPts, ptsDiff, std::labs(firstPts - lastPts), deltaPts, timeDiff, diff, init);
		fflush(stdout);

		// Calculate the delay that this display thread is required to wait.
		double delay = ptsDiff;

		// If the frame is repeated split this delay in half.
		if (pFrameTmp->repeat_pict) {
			delay += delay/2;
		}

		// Compute the synchronization threshold (see ffplay.c)
		double syncThreshold = (delay > AV_SYNC_THRESHOLD) ? delay : AV_SYNC_THRESHOLD;

		// The time difference is within the no sync threshold.
		if(fabs(diff) < AV_NOSYNC_THRESHOLD) {

			// If our time difference is lower than the sync threshold, then skip a frame.
			if (diff <= -syncThreshold) {
				AVFrame *pFrameTmp2 = ibb->getReadPtr();
				if (pFrameTmp2) {
					pFrameTmp = pFrameTmp2;
					nFrame++;
				}

			// If the time difference is within -syncThreshold ... +0 then show frame instantly.
			} else if (diff < 0) {
				delay = 0;

			// If the time difference is greater than the syncThreshold increase the delay.
			} else if (diff >= syncThreshold) {
				delay *= 2;
			}
		}

		// Save values for next call.
		deltaPts = init ? deltaPts : std::labs(firstPts - lastPts);
		lastPts = firstPts; // Need to use the first pts.
		lastTime = time;

		// Delay read to keep the desired frame rate.
		if (delay > 0) {
			std::this_thread::sleep_for(std::chrono::milliseconds((int)(delay*1000+0.5)));
		}

		// Update the pointer for the show frame.
		pFrameShow = pFrameTmp;

	}

	// Return the number of read frames (not neccesarily all are displayed).
	return (jint) nFrame;
}


// Opens movie file.
JNIEXPORT void JNICALL Java_PlayImageFromVideo_loadMovie
(JNIEnv *env, jobject thisObject, jstring jFileName) {

	// Release resources first before loading another movie.
	if (loadedMovie) {
		Java_PlayImageFromVideo_releaseMovie(env, thisObject);
	}

	const char *fileName = env->GetStringUTFChars(jFileName, 0);

	// Register all formats and codecs.
	av_register_all();

	// Open the video file.
	if (avformat_open_input(&pFormatCtx, fileName, nullptr, nullptr) != 0) {
		fprintf(stderr, "Couldn't open file %s.\n", fileName);
		exit(1);
	}

	// Retrieve the stream information.
	if (avformat_find_stream_info(pFormatCtx, nullptr) < 0) {
		fprintf(stderr, "Couldn't find stream information for file %s.\n", fileName);
		exit(1);
	}
  
	// Dump information about file onto standard error.
	av_dump_format(pFormatCtx, 0, fileName, 0);

	// Find the first video stream.
	iVideoStream = -1;
	for (int iStream = 0; iStream < pFormatCtx->nb_streams; ++iStream) {
		if (pFormatCtx->streams[iStream]->codec->codec_type == AVMEDIA_TYPE_VIDEO) {
			iVideoStream = iStream;
			break;
		}
	}

	if (iVideoStream == -1) {
		fprintf(stderr, "Unable to find a video stream in file %s.\n", fileName);
		exit(1);
	}

	// Get a poitner to the video stream.
	pVideoStream = pFormatCtx->streams[iVideoStream];

	// Get a pointer to the codec context for the video stream.
	pCodecCtx = pVideoStream->codec;


	// Find the decoder for the video stream.
	pCodec = avcodec_find_decoder(pCodecCtx->codec_id);
	if (pCodec == nullptr) {
		fprintf(stderr, "Unsupported codec for file %s!\n", fileName);
		exit(1);
	}

	// Open codec.
	if(avcodec_open2(pCodecCtx, pCodec, &optsDict)<0) {
		fprintf(stderr, "Could not open codec for file %s.\n", fileName);
		exit(1);
	}

	// Allocate video frame.
	pFrame = av_frame_alloc();

	// Initialize the color model conversion/rescaling context.
	swsCtx = sws_getContext
		(
			pCodecCtx->width,
			pCodecCtx->height,
			pCodecCtx->pix_fmt,
			pCodecCtx->width,
			pCodecCtx->height,
			AV_PIX_FMT_RGB24,
			SWS_BILINEAR,
			nullptr,
			nullptr,
			nullptr
		);

	// Initialize the widht, height, duration, number of frames.
	width = pCodecCtx->width;
	height = pCodecCtx->height;
	duration = pVideoStream->duration*av_q2d(pVideoStream->time_base);
	nFrame = pVideoStream->nb_frames;

	// Initialize the image buffer buffer.
	ibb = new ImageBufferBuffer(width, height);

	// Initialize the delta pts using the average frame rate.
	deltaPts = (int64_t)(1.0/(av_q2d(pVideoStream->time_base)*av_q2d(pVideoStream->avg_frame_rate)));

	// Set the value for loaded move true.
	loadedMovie = true;

	// Start the decode thread.
	decodeThread = new std::thread(loadNextFrame);

	// Free the string.
	env->ReleaseStringUTFChars(jFileName, fileName);
}

JNIEXPORT jint JNICALL Java_PlayImageFromVideo_getMovieColorChannels
(JNIEnv *env, jobject thisObject) {
	return (jint) nChannel;
}

JNIEXPORT jint JNICALL Java_PlayImageFromVideo_getMovieHeight
(JNIEnv *env, jobject thisObject) {
	return (jint) height;
}

JNIEXPORT jint JNICALL Java_PlayImageFromVideo_getMovieWidth
(JNIEnv *env, jobject thisObject) {
	return (jint) width;
}

JNIEXPORT jdouble JNICALL Java_PlayImageFromVideo_getMovieDuration
(JNIEnv *env, jobject thisObject) {
	return (jdouble) duration;
}

JNIEXPORT jlong JNICALL Java_PlayImageFromVideo_getMovieNumberOfFrames
(JNIEnv *env, jobject thisObject) {
	return (jlong) nFrame;
}

JNIEXPORT jdouble JNICALL Java_PlayImageFromVideo_getMovieTimeInSeconds
(JNIEnv *env, jobject thisObject) {
	return loadedMovie ? (jdouble) getShowFrame()*deltaPts*av_q2d(pVideoStream->time_base) : 0;
}

JNIEXPORT jlong JNICALL Java_PlayImageFromVideo_getMovieTimeInFrames
(JNIEnv *env, jobject thisObject) {
	return (jlong) getShowFrame();
}

JNIEXPORT void JNICALL Java_PlayImageFromVideo_releaseMovie
(JNIEnv *env, jobject thisObject) {

	if (loadedMovie) {
		// Set the quit flag for the docding thread.
		quit = true;

		// Flush the image buffer buffer (which unblocks all readers/writers).
		ibb->doFlush();

		// Join the decoding thread with this one.
		decodeThread->join();

		// Free the decoding thread.
		delete decodeThread;
		
		// Free the image buffer buffer.
		delete ibb;
		ibb = nullptr;

		// Free scaling context.
		sws_freeContext(swsCtx);

		// Free the YUV frame
		av_free(pFrame);

		// Close the video file
		avformat_close_input(&pFormatCtx);

		// Set default values for movie information.
		loadedMovie = false;
		width = 0;
		height = 0;
		duration = 0;
		nFrame = 0;
		iFrame = 0;

		// Set default values for playback speed.
		reversePlay = false;
		speed = 1;
		lastPts = 0;
		deltaPts = 0;
		diff = 0;

		// Reset value for seek request.
		seekReq = false;
		seekTime = 0;
		seekFlags = AVSEEK_FLAG_ANY;

		quit = false;
	}
}