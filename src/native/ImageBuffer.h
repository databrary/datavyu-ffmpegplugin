#include "Logger.h"
#include <atomic>
#include <mutex>
#include <condition_variable>
#include <cstdlib>
#include <algorithm>
#include <sstream>

extern "C" {
	#include <libavcodec/avcodec.h>
	#include <libavformat/avformat.h>
}

/** Maximum number of images in the buffer. Won't work for videos < 32 frames */
#define N_MAX_IMAGES 32

/** Minimum number of images for allocation in reverse direction */
#define N_MIN_IMAGES N_MAX_IMAGES/2

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
 * We ensure that 
 * - the displayed frame is not written.
 * - we only write as many frames as there are spaces available.
 */
class ImageBuffer {
	/** Pointer to the data elements. */
	AVFrame** data;

	/** Number of data elements in buffer. */
	int nData;

	/** Read pointer in 0...nData-1. The read pointer is at the next read position. */
	int iRead;

	/** Write pointer in 0...nData-1. The write pointer is at the next write position. */
	int iWrite;

	/** Readable frames before read pointer (including current iRead position). */
	std::atomic<int> nBefore;

	/** Readable frames after read pointer (excluding current iRead position). */
	std::atomic<int> nAfter;

	/** Number of reverse frames in 0...nData-1 (one frame is displayed). */
	int nReverse;

	/** Counter for the number of reverse frames to be read. */
	int iReverse;

	/** If true, this buffer is flushed. */
	std::atomic<bool> flushing;

	/** If true, this buffer is in reverse mode. */
	bool reverse;

	/** Minimum number of images for buffer allocation in reverse direction. */
	int nMinImages;

	/** Mutex to control access of variables. */
	std::mutex mu;

	/** Conditional variable to control access of variables. */
	std::condition_variable cv;

	/** Pointer to logger. Assumes that his pointer is initialized. */
	Logger* pLogger;

	/** Used for conversion of pts into frame for printing. */
	int64_t avgDeltaPts;

	/**
	 * Resets the buffer to the initial state.
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
	 * RETURNS the number of free items that can be written.
	 */
	inline int nFree() const { return std::min(nData - nBefore, nData) - 1; }

	/**
	 * RETURNS true if we are in reverse and we have not written any frames in reverse.
	 */
	inline bool notReversed() const { return iReverse == nReverse && nReverse > 0; }

public:

	/**
	 * Create an image buffer for images of the width and height.
	 * width -- Integer width in pixels.
	 * height -- Integer height in pixels.
	 */
	ImageBuffer(int width, int height, int64_t avgDeltaPts, Logger* pLogger) 
		: nData(N_MAX_IMAGES), flushing(false), reverse(false), 
		avgDeltaPts(avgDeltaPts), pLogger(pLogger) {

		// Initialize the buffer.
		data = new AVFrame*[nData];
		for (int iData = 0; iData < nData; ++iData) {
			AVFrame* pFrame = av_frame_alloc();
			data[iData] = pFrame;

			// Create the data buffer and associate it with the buffer.
			int nByte = avpicture_get_size(AV_PIX_FMT_RGB24, width, height);
			uint8_t* bufferShow = (uint8_t*) av_malloc(nByte*sizeof(uint8_t));
			avpicture_fill((AVPicture*)pFrame, bufferShow, AV_PIX_FMT_RGB24, width, height);
		}

		// Initialize the index variables in the buffer by invoking reset.
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
	 * True if this buffer is currently reversing, writing into a block.
	 */
	inline bool inReverse() const { return iReverse > 0; }

	/**
	 * This buffer is empty if all loaded frames are read.
	 */
	inline bool empty() const { return nBefore == 0; }

	/**
	 * Set the minimum number of images required to reverse. Set this to 1 before changing direction from backward to
	 * forward. Set this to N_MIN_IMAGES when changing direction from forward to backward.
	 */
	void setNMinImages(int nMin) { nMinImages = nMin; }

	/**
	 * Flushes the buffer and sets it into its initial state. This state is the same as the one after creating this
	 * object instance with one exception: The playback direction is not reset.
	 */
	void flush() {
		flushing = true;
		cv.notify_all();
		std::unique_lock<std::mutex> locker(mu);
		reset();
		locker.unlock();
		flushing = false;
	}

	/**
	 * Toggles the direction of replay.
	 *
	 * RETURNS: A pair with offset and delta in frames.
	 *
	 * - The offset is the amount we need to go back in the stream to get at the 
	 *   beginning for backward or forward writing into the buffer.
	 * - The delta is the maximum amount of frames we can jump backward given 
	 *   the number of frames before/after the current frame.
	 */
	std::pair<int, int> toggle() { // (delta, offset)

		std::unique_lock<std::mutex> locker(mu);

		// Ensure there are at least two frames after the current frame.
		// If we are in reverse then nAfter is at least 1, if we are in forward 
		// mode then nAfter is at least 2.
		//cv.wait(locker, [this](){return nAfter > 1-reverse;});
		cv.wait(locker, [this](){return nAfter > 0;});

		// When toggling we have the following cases:
		// Switching into backward replay:
		// * offset
		//   We need to go backward by at least nBefore+nAfter in the stream to 
		//   skip to the start of the reverse playback location in the stream.
		// * delta
		//   We can at most jump back in the buffer by Before and write this 
		//	 amount of frames into the buffer.
		// Switching into forward replay:
		// * offset
		//   We need to seek forward by nBefore+nAfter+iReverse.
		// * delta
		//   is always zero since we only advance by +1 frame in forward replay.
		pLogger->info("Before toggle.");
		//printLog();
		
		reverse = !reverse;
		std::pair<int,int> ret = reverse ?
			std::make_pair(nBefore + nAfter, 1+nBefore-1) :
			std::make_pair(nBefore + nAfter + iReverse, 0);

		if (reverse) {
			// Go back by 2 frames from current, current is the next to display.
			iRead = (iRead - 2 + nData) % nData;

			// Write from the read location - (nAfter-1) frames.
			iWrite = (iRead - (nAfter-1) + nData) % nData;
		} else {
			// If we are not in reverse mode make no change. If we are in 
			// reverse mode, then set iWrite to iRead+nAfter+1. Don't write on 
			// nAfter which becomes nBefore after the reversing.
			iWrite = notReversed() ? iWrite : (iRead+nAfter+1) % nData;

			// Go forward by two frames.
			iRead = (iRead + 2) % nData;
		}

		// Swap nBefore and nAfter and decrement nBefore by one and increment 
		// nAfter by one.
		int tmp = nAfter - 1;
		nAfter = nBefore + 1;
		nBefore = tmp;

		// Reset the reverse.
		nReverse = iReverse = 0;

		pLogger->info("After toggle.");
		//printLog();

		// Reset nMinImages.
		nMinImages = N_MIN_IMAGES;

		// Done with exclusive region.
		locker.unlock();
		cv.notify_all();

		return ret;
	}

	/**
	 * Get delta and offset when seeking backward.
	 * Blocks if there is no space for at least nMinImages in the buffer.
	 */
	std::pair<int, int> seekBackward() { // (delta, offset)
		std::unique_lock<std::mutex> locker(mu);
		
		// Ensure we have at least a block of nMinImages to go backward.
		// Leave two frames for the backup when toggling.
		cv.wait(locker, [this](){return (nData-nBefore-2) > nMinImages || flushing;});
		
		// The delta is nReverse and the offset is nData-nBefore-1.
		std::pair<int, int> ret = std::make_pair(nReverse, nData-nBefore-1);

		// Done with exclusive region.
		locker.unlock();
		cv.notify_all();
		return ret;
	}

	/**
	 * Set delta when seeking backward after a toggle event.
	 * This changes iWrite, nAfter, nReverse, and iReverse.
	 */
	void setBackwardAfterToggle(int delta) {
		std::unique_lock<std::mutex> locker(mu);
		iWrite = (iWrite - delta + 1 + nData) % nData;
		nAfter = (nAfter - delta + nData) % nData;
		nReverse = iReverse = delta;
		locker.unlock();
	}

	/**
	 * Set delta when seeking backward after a random seek.
	 * This changes iWrite, nAfter, nReverse, and iReverse.
	 */
	void setBackwardAfterSeek(int delta) {
		std::unique_lock<std::mutex> locker(mu);
		iWrite = (iRead - nBefore - delta + 1 + nData) % nData;
		nAfter = (nAfter - delta + nData) % nData;
		nReverse = iReverse = delta;
		locker.unlock();
	}

	/**
	 * Get the get pointer for the next frame. This method blocks if there is 
	 * no next frame available.
	 */
	AVFrame* getGetPtr() {
		AVFrame* pFrame = nullptr;
		std::unique_lock<std::mutex> locker(mu);
		
		// There must be 1 frame before!
		cv.wait(locker, [this](){ return nBefore > 0 || flushing;}); 
		
		// When not flushing show data[iRead] and increment iRead by one when in 
		// forward mode or decrement iRead by one when in backward mode.
		if (!flushing) {
			pFrame = data[iRead];
			iRead = (reverse ? (iRead - 1 + nData) : (iRead + 1)) % nData;
			nBefore--;
			nAfter++;
		}
		locker.unlock();
		cv.notify_all();
		return pFrame;
	}

	/**
	 * Request a put pointer. Notice this request does not change the internal
	 * pointers within the buffer. We separated requestPutPtr and completePutPtr
	 * because we may request the same put pointer several times without
	 * wanting to change the internal pointers of the buffer.
	 *
	 * RETURNS A pointer to an AVFrame.
	 */
	AVFrame* requestPutPtr() {
		AVFrame* pFrame = nullptr;
		std::unique_lock<std::mutex> locker(mu);
		
		// At least 2 frames are required by toggle
		cv.wait(locker, [this](){return nFree() >= 2 || flushing;});

		// If we do not flush get the write position.
		if (!flushing) {
			pFrame = data[iWrite];
		}
		locker.unlock();
		cv.notify_all();
		return pFrame;
	}

	/**
	 * Completes the put for a write pointer. This will adjust the internal 
	 * pointers in the buffer to confirm the write.
	 */
	void completePutPtr() {
		std::unique_lock<std::mutex> locker(mu);

		// If not flushing iReverse is subtracted by one if reverse == true.
		// nAfter is reduced if we have written all elements that is nBefore + nAfter == Data.
		// nBefore is increased by 1 if not in reverse otherwise it is increased
		// by nReverse if we are done with writing the block that is when iReverse == 0.
		if (!flushing) {
			iWrite = (iWrite + 1) % nData;
			iReverse -= reverse;
			nAfter -= (nBefore + nAfter) == nData;
			nBefore += reverse ? (iReverse == 0) * nReverse : 1;
		}
		locker.unlock();
		cv.notify_all();
	}

	/**
	 * RETURNS true if this buffer requires a seek request to fill in another 
	 * block of frames in reverse direction.
	 */
	bool seekReq() {
		return reverse && iReverse == 0;
	}

	/**
	 * Print state and contents of the buffer to the logger.
	 */
	inline void printLog() {
		pLogger->info("iRead = %d, iWrite = %d, nBefore = %d, "
				"nAfter = %d, nReverse = %d, iReverse = %d, reverse = %d.",
			iRead, iWrite, nBefore, nAfter, nReverse, iReverse, (int)reverse);
		std::stringstream ss;
		for (int iData = 0; iData < nData; ++iData) {
			ss << "(" << iData << ";" << data[iData]->pts/avgDeltaPts << "), ";
		}
		// Ensure that the buffer is large enough in the logger!
		pLogger->info("Buffer: %s", ss.str().c_str());
	}
};
