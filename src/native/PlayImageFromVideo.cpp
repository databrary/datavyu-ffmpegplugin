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
AVStream			*pVideoStream	= nullptr;	// Video stream.
AVCodecContext		*pCodecCtx		= nullptr;	// Video codec context.
AVCodec				*pCodec			= nullptr;	// Video codec.
AVFrame				*pFrame			= nullptr;	// Video frame read from stream.
AVFrame				*pFrameShow		= nullptr;	// Video frame displayed (wrapped by buffer).
AVPacket			packet;						// Audio or video packet (here video packet).
AVDictionary		*optsDict		= nullptr;	// Dictionary is a key-value store.
struct SwsContext   *swsCtx			= nullptr;	// Scaling context from source to target image format and size.
std::thread			*decodeThread	= nullptr;	// Decoding thread that decodes frames and is started when opening a video.
bool				quit			= false;	// Quit the decoding thread.
bool				loadedMovie		= false;	// Flag indicates that we loaded a movie. Used to protect uninitialized pointers.
ImageBuffer			*ib				= nullptr;	// Pointer to the image buffer.
bool				eof				= false;	// The stream reached the end of the file.

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
 * however many frames we wrote last plus the frames we intend to write. This 
 * buffer is thread-safe! Typically we have one producer, one consumer, and one 
 * control thread. The control thread changes direction or flushes the buffer.
 *
 * In all cases we ensure that 
 * - the displayed frame is not written.
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
	int iReverse;				// Counter for the number of reverse frames to be read.
	bool flush;					// If true, this buffer is flushed.
	bool reverse;				// If true, this buffer is in reverse mode.
	int nMinImages;				// Minimum number of images for buffer allocation in reverse direction.
	std::mutex mu;				// Mutex to control access of variables.
	std::condition_variable cv; // Conditional variable to control acces of variables.

	/**
	 * Resets the buffer into the intial state.
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
	 * Returns true if we are in reverse and we have not written any frames in 
	 * reverse.
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
		// Initialize the index variables in the buffer through a reset.
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
	 * Is in reverse mode.
	 */
	inline bool inReverse() const { return iReverse > 0; }

	/**
	 * This buffer is emtpy if all loaded frames have been read.
	 */
	inline bool empty() const { return nBefore == 0; }

	/**
	 * Set the minimum number of images required to reverse. Typically we set 
	 * this to one before a change in direction from backward into forward 
	 * direction.
	 */
	void setNMinImages(int nMin) { nMinImages = nMin; }

	/**
	 * Flushes the buffer and sets it into its initial state. This state is the
	 * same as after creating this obejct instance. The only difference is that 
	 * the playback direction is not reset.
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
	 * Returns a pair with offset and delta in frames.
	 * - The offset is the amount we need to go back in the stream to get at the 
	 *   beginning for backward or forward play.
	 * - The delta is the maximum amount of frames we can jump backward.
	 */
	std::pair<int,int> toggle() { // pair of delta, offset

		std::unique_lock<std::mutex> locker(mu);
		//cv.wait(locker, [this](){return nBefore > 1 || flush;}); // Must have 2 frames for reverse.
		cv.wait(locker, [this](){ return nAfter > 1 || flush; }); // Must have 2 frames behind for reverse.
		
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
		//   We need to seek forward by nBefore+nAfter+iReverse+1.
		// * delta
		//   is always zero since we only advance by +1 frame in forward replay.
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

		// Swap nBefore and nAfter and decrement nBefore by one and increment 
		// nAfter by one.
		int tmp = nAfter - 1;
		nAfter = nBefore + 1;
		nBefore = tmp;

		nReverse = iReverse = 0;

		//fprintf(stdout, "After toggle.\n");
		//print();

		// Reset nMinImages.
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
		// nAfter should be larger than > 2 but this should always hold.
		cv.wait(locker, [this](){ return nBefore > 0 || flush;}); // leave 2 frames for reverse!
		/*
		cv.wait(locker, [this](){
			fprintf(stdout,"reverse = %d, nBefore = %d, nAfter = %d.\n", 
				reverse, nBefore, nAfter);
			fflush(stdout);
			return reverse ? nAfter > 1 : nBefore > 2 || flush;}); // leave 2 frames for reverse!
			*/
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

// If writing reached the start of the file. This happens only in reverse mode.
bool atStartForWrite() {
	return ib->isReverse() && lastWritePts <= pVideoStream->start_time+avgDeltaPts && !ib->inReverse();
}

// If reading reached the start of the file. This happens only in reverse mode.
bool atStartForRead() {
	return atStartForWrite() && ib->empty();
}

bool atEndForWrite() {
	//fprintf(stdout, "lastWritePts = %I64d, start_time = %I64d, lastWritePts-start_time = %I64d, duration = %I64d.\n",
	//	lastWritePts, pVideoStream->start_time, lastWritePts-pVideoStream->start_time, pVideoStream->duration);
	//fflush(stdout);
	// Duration is just an estimate and usually larger than the acutal number of frames.
	// I measured 8 - 14 additional frames that duration specifies.
	return !ib->isReverse() && eof; //lastWritePts-pVideoStream->start_time >= pVideoStream->duration;
}

bool atEndForRead() {
	return !ib->isReverse() && eof && ib->empty();//lastPts-pVideoStream->start_time >= pVideoStream->duration;
}

void loadNextFrame() {
	int frameFinished;
	bool reverseRefresh = true;

	while (!quit) {

		// Random seek.
		if (seekReq) {
			seekFlags |= AVSEEK_FLAG_BACKWARD;
			if (av_seek_frame(pFormatCtx, iVideoStream, seekPts, seekFlags) < 0) {
				fprintf(stderr, "Random seek of %I64d frame unsuccessful.\n", seekPts/avgDeltaPts);
			} else {
				fprintf(stdout, "Random seek of %I64d frame successful.\n", seekPts/avgDeltaPts);
				fflush(stdout);
				ib->doFlush();
				avcodec_flush_buffers(pCodecCtx);
				lastWritePts = seekPts;
			}
			seekReq = false;
		}
		
		// Switch direction of playback.
		if (toggle) {
			std::pair<int,int> offsetDelta = ib->toggle();
			int offset = offsetDelta.first;
			int delta = offsetDelta.second;
			int nShift = 0;
			// Even if we may not seek backward it is safest to get the prior keyframe.
			seekFlags |= AVSEEK_FLAG_BACKWARD;
			if (ib->isReverse()) {
				int maxDelta = (-pVideoStream->start_time+lastWritePts)/avgDeltaPts;
				delta = std::min(offset+delta, maxDelta) - offset;
				ib->setBackwardAfterToggle(delta);
				nShift = -(offset + delta) + 1;
			} else {
				nShift = offset;
			}

			lastWritePts = seekPts = lastWritePts + nShift*avgDeltaPts;

			if (av_seek_frame(pFormatCtx, iVideoStream, seekPts, seekFlags) < 0) {
				fprintf(stderr, "Toggle seek of %I64d frames unsuccessful.\n", seekPts/avgDeltaPts);
			} else {
				//fprintf(stdout, "Toggle seek of %I64d frames successful.\n", seekPts/avgDeltaPts);
				//fflush(stdout);
				avcodec_flush_buffers(pCodecCtx);
			}			
			toggle = false;			
		}
/*
		if (atStartForWrite() || atEndForWrite()) {
			//ib->print();
			fprintf(stdout, "Reached end/start of file, seek pts = %I64d, last write pts = %I64d.\n", seekPts, lastWritePts);
			fflush(stdout);
			std::this_thread::sleep_for(std::chrono::milliseconds(500));
			continue;
		}
*/
		if (ib->seekReq()) {
			// Find the number of frames that can still be read
			int maxDelta = (-pVideoStream->start_time+lastWritePts)/avgDeltaPts;

			std::pair<int,int> offsetDelta = ib->seekBackward();
			
			int offset = offsetDelta.first;
			int delta = offsetDelta.second;

			delta = std::min(offset+delta, maxDelta) - offset;

			ib->setBackwardAfterSeek(delta);

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

		// Read frame.
		int ret = av_read_frame(pFormatCtx, &packet);

		eof = ret == AVERROR_EOF;

		if (atStartForWrite() || atEndForWrite()) {
			//ib->print();
			fprintf(stdout, "Reached end/start of file, seek pts = %I64d, last write pts = %I64d.\n", seekPts, lastWritePts);
			fflush(stdout);
			std::this_thread::sleep_for(std::chrono::milliseconds(500));
			continue;
		}

		if (ret < 0 && !eof) {
			fprintf(stderr, "Error:  %c, %c, %c, %c.\n",
				static_cast<char>((-ret >> 0) & 0xFF),
				static_cast<char>((-ret >> 8) & 0xFF),
				static_cast<char>((-ret >> 16) & 0xFF),
				static_cast<char>((-ret >> 24) & 0xFF));
			fflush(stderr);
			std::this_thread::sleep_for(std::chrono::milliseconds(500));
		} else {
			// Is this a packet from the video stream?
			if (packet.stream_index == iVideoStream) {
				
				// Decode the video frame.
				avcodec_decode_video2(pCodecCtx, pFrame, &frameFinished, &packet);
				
				// Did we get a full video frame?
				if(frameFinished) {

					// Set the presentation time stamp.
					int64_t readPts = pFrame->pkt_pts;

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
		double diffPts = init ? 0 : std::labs(firstPts - lastPts)/speed*av_q2d(pVideoStream->time_base);

		// Get the current time.
		auto time = std::chrono::high_resolution_clock::now();

		// Compute the time difference.
		double timeDiff = init ? 0 
			: std::chrono::duration_cast<std::chrono::microseconds>(time-lastTime).count()/1000000.0;

		// Compute the difference between times and pts.
		diff = init ? 0 : (diff + diffPts - timeDiff);

		// Calculate the delay that this display thread is required to wait.
		double delay = diffPts;

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

		// Update the pointer for the show frame.
		pFrameShow = pFrameTmp;

		//fprintf(stdout, "Display pts = %I64d or %I64d frames.\n", pFrameShow->pts, pFrameShow->pts/avgDeltaPts);
		//ib->print();
		//fflush(stdout);
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

	// Seek to the start of the file.
	lastWritePts = seekPts = pVideoStream->start_time;
	seekReq = true;

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

JNIEXPORT jdouble JNICALL Java_PlayImageFromVideo_getMovieStartTimeInSeconds
(JNIEnv *, jobject) {
	return (jdouble) loadedMovie ? pVideoStream->start_time * av_q2d(pVideoStream->time_base) : 0;
}

JNIEXPORT jdouble JNICALL Java_PlayImageFromVideo_getMovieEndTimeInSeconds
(JNIEnv *, jobject) {
	return (jdouble) loadedMovie ? (pVideoStream->duration - pVideoStream->start_time) * av_q2d(pVideoStream->time_base) : 0;
}

JNIEXPORT jdouble JNICALL Java_PlayImageFromVideo_getMovieDuration
(JNIEnv *env, jobject thisObject) {
	return (jdouble) duration;
}

JNIEXPORT jdouble JNICALL Java_PlayImageFromVideo_getMovieTimeInSeconds
(JNIEnv *env, jobject thisObject) {
	return loadedMovie ? pFrameShow->pts*av_q2d(pVideoStream->time_base) : 0;
}

JNIEXPORT jboolean JNICALL Java_PlayImageFromVideo_forwardPlayback
(JNIEnv *, jobject) {
	return loadedMovie ? (jboolean) !ib->isReverse() : true;
}

JNIEXPORT void JNICALL Java_PlayImageFromVideo_rewindMovie
(JNIEnv *, jobject) {
	if (loadedMovie) {
		if (ib->isReverse()) {
			// TODO: Correct the seek point to the end of the file.
			lastWritePts = seekPts = pVideoStream->duration;
			seekReq = true;
		} else {
			// Seek to the start of the file.
			lastWritePts = seekPts = pVideoStream->start_time;
			seekReq = true;			
		}
	}
}

JNIEXPORT jboolean JNICALL Java_PlayImageFromVideo_atStartForRead
(JNIEnv *, jobject){
	return loadedMovie ? atStartForRead() : false;
}

JNIEXPORT jboolean JNICALL Java_PlayImageFromVideo_atEndForRead
(JNIEnv *, jobject) {
	return loadedMovie ? atEndForRead() : false;
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