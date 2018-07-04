#include "PacketQueue.hpp"
#include <mutex>
#include <condition_variable>

extern "C" {
	#include <libavcodec/avcodec.h> // codecs
}

#ifndef FRAME_QUEUE_H_
#define FRAME_QUEUE_H_

#define VIDEO_PICTURE_QUEUE_SIZE 3
#define SAMPLE_QUEUE_SIZE 9

// Common struct for handling decoded data and allocated buffers
typedef struct Frame {
    AVFrame *frame;
	AVSubtitle sub;
    int serial;
    double pts;      // presentation timestamp for the frame
    double duration; // estimated duration of the frame
    int64_t pos;     // byte position of the frame in the input file
    int width;
    int height;
    int format;
    AVRational sar;
    int uploaded;
    int flip_v;
} Frame;

// Frame queue provides a container for frames that hold audio and image data
// Notice that the typical size for the audio/image data differs
//
// I made the following changes to the original code
// - removed the sub title 
// - replaced the SDL mutex/condition by the std::mutex/std::conditional_variable
// - changed the allocation of the queue from static to dymanic to support all max_sizes
// 
// Odities of the current API (per original code)
// - Always call push and next to write/read a frame
// - nb_remaining is the number of available frames to read
// - Coupled to packet queue which can cause an abort and when the frame queue is signaled returns nullptr
// - Use peek_readable and peek_writable to get a read/write pointer (in most cases)
// - In rarer cases use peek, peek_next, and peek_last (see tests for examples of the logic)
//
class FrameQueue{
    private:
        Frame* queue; // TOOD: This might be a bit slower than the original code; check whether it matters
        int rindex; // read index
        int windex; // write index
        int size;
        int max_size;
        int keep_last;
        int rindex_shown; // read index shown
        std::mutex mutex;
        std::condition_variable cond;
        const PacketQueue* pktq; // packet queue

		static void unref_item(Frame *vp) {
			av_frame_unref(vp->frame);
		}
    public:
        FrameQueue(const PacketQueue* pktq, int max_size, int keep_last) :
            rindex(0),
            windex(0),
            size(0),
            max_size(max_size),
            keep_last(keep_last),
            rindex_shown(0),
            pktq(pktq) {
			queue = new Frame[max_size];
            for (int i = 0; i < max_size; i++) {
                queue[i].frame = av_frame_alloc();
            }
        }

		// Same like PacketQueue we should destroy the mutex 
        virtual ~FrameQueue() {
            for (int i = 0; i < max_size; i++) {
                Frame *vp = &queue[i];
                unref_item(vp);
                av_frame_free(&vp->frame);
            }
			delete[] queue;
        }

        void signal() {
            std::unique_lock<std::mutex> locker(mutex);
            cond.notify_all();
            locker.unlock();
        }

        inline Frame* peek() { return &queue[(rindex + rindex_shown) % max_size]; }

        inline Frame* peek_next() { return &queue[(rindex + rindex_shown + 1) % max_size]; }

        inline Frame* peek_last() { return &queue[rindex]; }

		inline std::mutex & get_mutex() { return mutex; }

		inline int get_rindex_shown() const { return rindex_shown; }

        Frame *peek_writable() {
            // waits until we have space to put a new frame
            std::unique_lock<std::mutex> locker(mutex);
            cond.wait(locker, [&]{ return size < max_size || pktq->is_abort_request(); } );
            locker.unlock();
            return pktq->is_abort_request() ? nullptr : &queue[windex];
        }

        Frame *peek_readable() {
            // waits until we have a readable new frame
            std::unique_lock<std::mutex> locker(mutex);
            cond.wait(locker, [&]{ return size - rindex_shown > 0 || pktq->is_abort_request(); } );
            locker.unlock();
            return pktq->is_abort_request() ? nullptr : &queue[(rindex + rindex_shown) % max_size];
        }

        void push() {
            if (++windex == max_size)
                windex = 0;
            std::unique_lock<std::mutex> locker(mutex);
            size++;
            cond.notify_all();
            locker.unlock();
        }

        void next() {
            if (keep_last && !rindex_shown) {
                rindex_shown = 1;
                return;
            }
            unref_item(&queue[rindex]);
            if (++rindex == max_size)
                rindex = 0;
            std::unique_lock<std::mutex> locker(mutex);
            size--;
            cond.notify_all();
            locker.unlock();
        }

        // return the number of undisplayed frames in the queue
        inline int nb_remaining() const { return size - rindex_shown; }

        // return last shown position
        int64_t last_pos() {
            Frame *fp = &queue[rindex];
			return rindex_shown && fp->serial == pktq->get_serial() ? fp->pos : -1;
        }
};

#endif FRAME_QUEUE_H_