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

#define N_MAX_IMAGES 16
#define N_MIN_IMAGES N_MAX_IMAGES/2
#define AV_SYNC_THRESHOLD 0.01
#define AV_NOSYNC_THRESHOLD 10.0
#define PTS_DELTA_THRESHOLD 3

class ImageBuffer; // Forward declaration of the image buffer class for the linker.

// Basic information about the movie.
int					width			= 0; // Width of the image in pixels.
int					height			= 0; // Height of the image in pixels.
int					nChannel		= 3; // Number of color channels.
double				duration		= 0; // Duration of the video in seconds.

// Variables (most of them are pointers) to the video stream, video frames,...
AVFormatContext		*pFormatCtx		= nullptr;	// Audio and video format context.
int					iVideoStream	= -1;		// Index of the (1st) video stream.
AVStream			*pVideoStream	= nullptr;	// Audio or video stream (here video stream).
AVCodecContext		*pCodecCtx		= nullptr;	// Video codec context.
AVCodec				*pCodec			= nullptr;	// Video codec.
AVFrame				*pFrame			= nullptr;	// Video frame read from stream.
AVFrame				*pFrameShow		= nullptr;	// Video frame displayed (points into buffer).
AVPacket			packet;						// Audio or video packet (here video packet).
AVDictionary		*optsDict		= nullptr;	// Dictionary is a key-value store.
struct SwsContext   *swsCtx			= nullptr;	// Scaling context from source to target image format and size.
std::thread			*decodeThread	= nullptr;	// Thread that decodes frames and is started when opening a video.
bool				quit			= false;	// Quit the decoding thread.
bool				loadedMovie		= false;	// Flag indicates that we loaded a movie. This is used to avoid access to uninitialized pointers.
ImageBuffer			*ib				= nullptr;	// Pointer to the image buffer.

// Variables to control playback speed and reverse playback.
bool				toggle			= false;
double				speed			= 1;
int64_t				lastPts			= 0; // on reset set to 0
int64_t				deltaPts		= 0;
int64_t				avgDeltaPts		= 0; // on reset set to 0 (used for reverse seek)
int64_t				lastWritePts	= 0; // on reset set to 0 
double				diff			= 0; // on reset set to 0
std::chrono::high_resolution_clock::time_point lastTime;

// Variables to control random seeking.
bool				seekReq			= false;
int64_t				seekPts			= 0;
int					seekFlags		= 0;

/**
 * This image buffer is a ring buffer with a forward mode and backward mode.
 * In forward mode frames are written sequentially, one frame at a time.
 * In backward mode frames are written in a block of at least N_MIN_IMAGES by 
 * jumping the amount of images to be written back and then writing the images 
 * in forward direction into the buffer. In the stream we need to jump 
 * appropriately. This buffer is thread-safe! Typically we have one producer,
 * one consumer, and one control thread. The control thread changes direction or
 * flushes the buffer.
 *
 * In all cases we ensure that 
 * - the frame displayed is not written.
 * - we only write as many frames as there are spaces available.
 */
class ImageBuffer {
	AVFrame** data;				// Pointer to the data elements.
	int nData;					// Number of data elements in buffer.
	int iRead;					// Read pointer in 0...nData-1. The read pointer is at the next read position.
	int iWrite;					// Write pointer in 0...nData-1. The write poitner is at the next write position.
	int nBefore;				// Readable frames before read pointer (including current iRead position).
	int nAfter;					// Readable frames after read pointer (excluding current iRead position).
	int nReverse;				// Number of reverse frames in 0...nData-1 (one frame is displayed).
	int iReverse;				// Counter for number of reverse frames to be read.
	bool flush;					// If true this buffer is flushed.
	bool reverse;				// If true this buffer is in reverse mode.
	int nMinImages;				// Minimum number of images for buffer allocation in reverse direction.
	std::mutex mu;				// Mutex to control access of variables.
	std::condition_variable cv; // Conditional variable to control acces of variables.

	/**
	 * Resets all index variables within the buffer.
	 */
	void reset() {
		iRead = 0;
		iWrite = 0;
		nBefore = 0;
		nAfter = 0;
		nReverse = 0;
		iReverse = 0;
		nMinImages = N_MIN_IMAGES;
	}

	/**
	 * Returns the number of free items that can be written.
	 */
	inline int nFree() const { return std::min(nData - nBefore, nData) - 1; }

	/**
	 * Returns true if we are in reverse and we have not started to reverse.
	 */
	inline bool notReversed() const { return iReverse == nReverse && nReverse > 0; }

public:

	/**
	 * Create an image buffer for images of the width and height.
	 * width -- Integer width in pixels.
	 * height -- Integer height in pixels.
	 */
	ImageBuffer(int width, int height) : nData(N_MAX_IMAGES), flush(false), 
		reverse(false) {
		// Initialize the data of the buffer.
		data = new AVFrame*[nData];
		for (int iData = 0; iData < nData; ++iData) {
			AVFrame* pFrame = av_frame_alloc();
			data[iData] = pFrame;
			// Create the data buffer and associate it with the buffer.
			int nByte = avpicture_get_size(AV_PIX_FMT_RGB24, width, height);
			uint8_t* bufferShow = (uint8_t*) av_malloc(nByte*sizeof(uint8_t));
			avpicture_fill((AVPicture*)pFrame, bufferShow, AV_PIX_FMT_RGB24,
				 width, height);
		}
		// Initialize the index variables within the buffer through a reset.
		reset();
	}

	/**
	 * Free the memory allocated for this image buffer.
	 */
	virtual ~ImageBuffer() {
		for (int iData = 0; iData < nData; ++iData) {
			AVFrame* pFrame = data[iData];
			av_free(pFrame->data[0]); // Frees the data buffer.
			av_free(pFrame);
		}
		delete [] data;
	}

	/**
	 * True if the buffer is in reverse mode otherwise false.
	 */
	inline bool isReverse() const { return reverse; }

	/**
	 * Set the minimum number of images.
	 */
	void setNMinImages(int nMin) { nMinImages = nMin; }

	/**
	 * Flushes the buffer and sets it into its initial state same as after 
	 * creating this obejct instance. The only difference is that data in the 
	 * buffer has been written, but this data will never be shown.
	 */
	void doFlush() {
		flush = true;
		cv.notify_all();
		std::unique_lock<std::mutex> locker(mu);
		reset();
		locker.unlock();
		flush = false;
	}

	/**
	 * Toggles the direction of replay.
	 * Returns a pair with delta and offset in frames.
	 * The variable delta defines how many steps we need to 
	 */
	std::pair<int,int> toggle() { // pair of delta, offset

		std::unique_lock<std::mutex> locker(mu);
		cv.wait(locker, [this](){return nBefore > 1 || flush;}); // Must have 2 frames for reverse.
		
		// When toggeling we have these cases:
		// Switching into backward replay:
		// * offset
		//   We need to go backward by at least nBefore+nAfter in the stream to 
		//   skip to the start of the reverse playback location in the stream.
		// * delta
		//   We can at most jump back in the buffer by nData-Before-1 and read 
		//   this amount of frames into the buffer.
		// Switching into forward replay:
		// * offset
		//   if we have not written in reverse yet then return 0
		//   otherwise we need to seek forward by nBefore+nAfter+iReverse+1
		// * delta
		//   is always zero since we only advance by +1 frame in forward replay
		reverse = !reverse;
		std::pair<int,int> ret = reverse ?
			std::make_pair(nBefore+nAfter, nData-nBefore-1) :
			std::make_pair(nBefore+nAfter+iReverse, 0);
		
		//fprintf(stdout, "\nBefore toggle.\n");
		//print();
		
		if (reverse) {
			iRead = (iRead - 2 + nData) % nData;
			iWrite = (iRead - (nAfter-1) + nData) % nData;
		} else {
			iWrite = notReversed() ? iWrite : (iRead+nAfter+1) % nData;
			iRead = (iRead + 2) % nData;
		}

		int tmp = nAfter - 1;
		nAfter = nBefore + 1;
		nBefore = tmp;

		nReverse = iReverse = 0;

		//fprintf(stdout, "After toggle.\n");
		//print();

		nMinImages = N_MIN_IMAGES;
		locker.unlock();
		cv.notify_all();
		return ret;
	}
	std::pair<int,int> seekBackward() {
		std::unique_lock<std::mutex> locker(mu);
		cv.wait(locker, [this](){return (nData-nBefore) > nMinImages || flush;});
		std::pair<int,int> ret = std::make_pair(nReverse, nData-nBefore-1);
		locker.unlock();
		cv.notify_all();
		return ret;
	}
	void setBackwardAfterToggle(int delta) {
		std::unique_lock<std::mutex> locker(mu);
		iWrite = (iWrite - delta + 1 + nData) % nData;
		nAfter = (nAfter - delta + nData) % nData;
		nReverse = iReverse = delta;
		locker.unlock();
	}
	void setBackwardAfterSeek(int delta) {
		std::unique_lock<std::mutex> locker(mu);
		iWrite = (iRead - nBefore - delta + 1 + nData) % nData;
		nAfter = (nAfter - delta + nData) % nData;
		nReverse = iReverse = delta;
		locker.unlock();
	}
	AVFrame* getReadPtr() {
		AVFrame* pFrame = nullptr;
		std::unique_lock<std::mutex> locker(mu);
		cv.wait(locker, [this](){return nBefore > 2 || flush;}); // leave 2 frames for reverse!
		if (!flush) {
			pFrame = data[iRead];
			iRead = (reverse ? (iRead - 1 + nData) : (iRead + 1)) % nData;
			nBefore--;
			nAfter++;
		}
		locker.unlock();
		cv.notify_all();
		return pFrame;
	}
	AVFrame* reqWritePtr() {
		AVFrame* pFrame = nullptr;
		std::unique_lock<std::mutex> locker(mu);
		cv.wait(locker, [this](){return nFree() > 1 || flush;});
		if (!flush) {
			pFrame = data[iWrite];
		}
		locker.unlock();
		cv.notify_all();
		return pFrame;
	}
	void cmplWritePtr() {
		std::unique_lock<std::mutex> locker(mu);
		if (!flush) {
			iWrite = (iWrite + 1) % nData;
			iReverse -= reverse;
			nAfter -= (nBefore + nAfter) == nData;
			nBefore += reverse ? (iReverse == 0) * nReverse : 1;
		}

		//fprintf(stdout, "Wrote frame:\n");
		//print();

		locker.unlock();	
		cv.notify_all();
	}
	bool seekReq() {
		return reverse && iReverse==0;
	}
	void print() {
		fprintf(stdout, "iRead = %d, iWrite = %d, nBefore = %d, nAfter = %d, nReverse = %d, iReverse = %d, reverse = %d.\n",
			iRead, iWrite, nBefore, nAfter, nReverse, iReverse, (int)reverse);
		fprintf(stdout, "Frames: ");
		for (int iData = 0; iData < nData; ++iData) {
			fprintf(stdout, "%I64d, ", data[iData]->pts/avgDeltaPts);
		}
		fprintf(stdout, "\n");
		fflush(stdout);
	}
};

void loadNextFrame() {
	int frameFinished;
	bool reverseRefresh = true;

	while (!quit) {

		/*
		if (lastWritePts < pVideoStream->start_time-avgDeltaPts) {
			fprintf(stdout, "Reached start of file, seek pts = %I64d, last write pts = %I64d.\n", seekPts, lastWritePts);
			fflush(stdout);
			std::this_thread::sleep_for(std::chrono::milliseconds(500));
			continue;
		}
		*/

		// Switch direction of playback.
		if (toggle) {
			std::pair<int,int> offsetDelta = ib->toggle();
			int offset = offsetDelta.first;
			int delta = offsetDelta.second;
			int nShift = 0;
			// Even if we may not seek backward it is safest to get the prior keyframe.
			seekFlags |= AVSEEK_FLAG_BACKWARD;
			if (ib->isReverse()) {
				int maxDelta = lastWritePts/avgDeltaPts;
				delta = std::min(offset+delta, maxDelta) - offset;
				ib->setBackwardAfterToggle(delta);
				nShift = -(offset + delta) + 1;
			} else {
				nShift = offset;
			}

			lastWritePts = seekPts = lastWritePts + nShift*avgDeltaPts;

			//fprintf(stdout, "Last write frame %I64d, shift %d frames.\n", lastWritePts/avgDeltaPts, nShift);
			//fflush(stdout);

			if (av_seek_frame(pFormatCtx, iVideoStream, seekPts, seekFlags) < 0) {
				fprintf(stderr, "Toggle seek of %I64d frames unsuccessful.\n", seekPts/avgDeltaPts);
			} else {
				//fprintf(stdout, "Toggle seek of %I64d frames successful.\n", seekPts/avgDeltaPts);
				//fflush(stdout);
				avcodec_flush_buffers(pCodecCtx);
			}			
			toggle = false;			
		}

		if (ib->seekReq()) {
			// Find the number of frames that can still be read
			int maxDelta = lastWritePts/avgDeltaPts;
			std::pair<int,int> offsetDelta = ib->seekBackward();
			
			int offset = offsetDelta.first;
			int delta = offsetDelta.second;
			delta = std::min(offset+delta, maxDelta) - offset;
			ib->setBackwardAfterSeek(delta);
			
			//fprintf(stdout,"Backward seek with offset = %d and delta = %d.\n", offset, delta);
			//fflush(stdout);

			seekFlags |= AVSEEK_FLAG_BACKWARD;
			int nShift = -(offset + delta) + 1;

			lastWritePts = seekPts = lastWritePts + nShift*avgDeltaPts;
			if (av_seek_frame(pFormatCtx, iVideoStream, seekPts, seekFlags) < 0) {
				fprintf(stderr, "Reverse seek of %I64d pts or %I64d frames unsuccessful.\n", seekPts, seekPts/avgDeltaPts);
			} else {
				//fprintf(stdout, "Reverse seek of %I64d pts or %I64d frames successful.\n", seekPts, seekPts/avgDeltaPts);
				//fflush(stdout);
				avcodec_flush_buffers(pCodecCtx);
			}
		}

		// Random seek.
		if (seekReq) {
			bool reverse = seekPts < lastWritePts;
			if (reverse) {
				seekFlags |= AVSEEK_FLAG_BACKWARD;
			} else {
				seekFlags &= ~AVSEEK_FLAG_BACKWARD;
			}
			if (av_seek_frame(pFormatCtx, iVideoStream, seekPts, seekFlags) < 0) {
				fprintf(stderr, "Random seek of %I64d frame unsuccessful.\n", seekPts/avgDeltaPts);
			} else {
				//fprintf(stdout, "Random seek of %I64d frame successful.\n", seekPts/avgDeltaPts);
				//fflush(stdout);
				avcodec_flush_buffers(pCodecCtx);
				lastWritePts = seekPts;
			}
			seekReq = false;
		}
		
		// Read frame.
		int ret = av_read_frame(pFormatCtx, &packet);

		if (ret < 0) {
			// TODO: FIX the printing of the error code.
			fprintf(stdout, "Error no of av_read_frame is %d.\n", ret);
			fprintf(stdout, "Error %c, %c, %c, %c.\n",
				static_cast<char>((ret >> 0) & 0xFF),
				(char)((ret >> 8) & 0xFF),
				(char)((ret >> 16) & 0xFF),
				(char)((ret >> 24) & 0xFF));
			fflush(stdout);
			std::this_thread::sleep_for(std::chrono::milliseconds(500));

		} else {
			// Is this a packet from the video stream?
			if (packet.stream_index == iVideoStream) {
				
				// Decode the video frame.
				avcodec_decode_video2(pCodecCtx, pFrame, &frameFinished, &packet);
				
				// Did we get a full video frame?
				if(frameFinished) {

					// Set the presentation time stamp.
					int64_t readPts = packet.dts == AV_NOPTS_VALUE ? 0 : pFrame->pkt_pts;

					// Skip frames until we are at or beyond of the seekPts time stamp.
					if (readPts >= seekPts) {
					
						// Get the next writeable buffer. This may block and can be unblocked with a flush.
						AVFrame* pFrameBuffer = ib->reqWritePtr();

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
							pFrameBuffer->repeat_pict = pFrame->repeat_pict;
							pFrameBuffer->pts = lastWritePts = readPts;
							ib->cmplWritePtr();
						}
					}

					// Reset frame container to initial state.
					av_frame_unref(pFrame);
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
		ib->setNMinImages(1);
		toggle = ib->isReverse() != (inSpeed < 0);
		speed = fabs(inSpeed);
	}
}

JNIEXPORT void JNICALL Java_PlayImageFromVideo_setTimeInSeconds
(JNIEnv *env, jobject thisObject, jdouble time) { // time in seconds

	lastWritePts = seekPts = ((int64_t)(time/(avgDeltaPts*av_q2d(pVideoStream->time_base))))*avgDeltaPts;
	seekReq = true;
}

JNIEXPORT void JNICALL Java_PlayImageFromVideo_setTimeInFrames
(JNIEnv *env, jobject thisObject, jlong time) {
	
	lastWritePts = seekPts = time*avgDeltaPts;	
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
	AVFrame *pFrameTmp = ib->getReadPtr();

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
				AVFrame *pFrameTmp2 = ib->getReadPtr();
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
		deltaPts = init ? (int64_t)(1.0/(av_q2d(pVideoStream->time_base)*av_q2d(pVideoStream->avg_frame_rate))) : std::labs(firstPts - lastPts);
		lastPts = firstPts; // Need to use the first pts.
		lastTime = time;

		// Delay read to keep the desired frame rate.
		if (delay > 0) {
			std::this_thread::sleep_for(std::chrono::milliseconds((int)(delay*1000+0.5)));
		}

		//fprintf(stdout, "Display frame %I64d.\n", firstPts/avgDeltaPts);
		//fflush(stdout);

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

	// Initialize the image buffer buffer.
	ib = new ImageBuffer(width, height);

	// Initialize the delta pts using the average frame rate and the average pts.
	avgDeltaPts = deltaPts = (int64_t)(1.0/(av_q2d(pVideoStream->time_base)*av_q2d(pVideoStream->avg_frame_rate)));

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
	return (jlong) 0; // TODO: FIXME
}

JNIEXPORT jdouble JNICALL Java_PlayImageFromVideo_getMovieTimeInSeconds
(JNIEnv *env, jobject thisObject) {
	return loadedMovie ? (pFrameShow->pts - pVideoStream->start_time)*av_q2d(pVideoStream->time_base) : 0;
}

JNIEXPORT jlong JNICALL Java_PlayImageFromVideo_getMovieTimeInFrames
(JNIEnv *env, jobject thisObject) {
	return loadedMovie ? (jlong) pFrameShow->pts/avgDeltaPts : 0;
	//return loadedMovie ? (jlong) (pFrameShow->pts - pVideoStream->start_time)/avgDeltaPts : 0;
}

JNIEXPORT jlong JNICALL Java_PlayImageFromVideo_getMoveTimeInStreamUnits
(JNIEnv *, jobject){
	return loadedMovie ? (jlong) (pFrameShow->pts - pVideoStream->start_time) : 0;
}


JNIEXPORT void JNICALL Java_PlayImageFromVideo_releaseMovie
(JNIEnv *env, jobject thisObject) {

	if (loadedMovie) {
		// Set the quit flag for the docding thread.
		quit = true;

		// Flush the image buffer buffer (which unblocks all readers/writers).
		ib->doFlush();

		// Join the decoding thread with this one.
		decodeThread->join();

		// Free the decoding thread.
		delete decodeThread;
		
		// Free the image buffer buffer.
		delete ib;
		ib = nullptr;

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
		lastWritePts = 0;

		// Set default values for playback speed.
		toggle = false;
		speed = 1;
		lastPts = 0;
		deltaPts = 0;
		avgDeltaPts = 0;
		diff = 0;

		// Reset value for seek request.
		seekReq = false;
		seekPts = 0;
		seekFlags = AVSEEK_FLAG_ANY;

		quit = false;
	}
}