#include "PacketQueue.hpp"
#include <mutex>
#include <condition_variable>

extern "C" {
	#include <libavcodec/avcodec.h> // codecs
    #include <libavutil/error.h> // error codes
}

#ifndef FRAME_QUEUE_H_
#define FRAME_QUEUE_H_

// TODO(fraudies): Convert this into a C++ class
// Note, I stripped out the sub title and replaced the SDL mutex by the std mutex

#define VIDEO_PICTURE_QUEUE_SIZE 3
#define SAMPLE_QUEUE_SIZE 9
#define FRAME_QUEUE_SIZE FFMAX(SAMPLE_QUEUE_SIZE, VIDEO_PICTURE_QUEUE_SIZE)
/* Common struct for handling all types of decoded data and allocated render buffers. */
typedef struct Frame
{
    AVFrame *frame;
    int serial;
    double pts;      /* presentation timestamp for the frame */
    double duration; /* estimated duration of the frame */
    int64_t pos;     /* byte position of the frame in the input file */
    int width;
    int height;
    int format;
    AVRational sar;
    int uploaded;
    int flip_v;
} Frame;

class FrameQueue{
    private:
        Frame queue[FRAME_QUEUE_SIZE];
        int rindex; // read index
        int windex; // write index
        int size;
        int max_size;
        int keep_last;
        int rindex_shown; // read index shown
        std::mutex mutex;
        std::condition_variable cond;
        PacketQueue *pktq; // packet queue

    public:
        FrameQueue(PacketQueue *pktq, int max_size, int keep_last) :
            rindex(0),
            windex(0),
            size(0),
            max_size(FFMIN(max_size, FRAME_QUEUE_SIZE)),
            keep_last(keep_last),
            rindex_shown(0),
            pktq(pktq) {
            for (int i = 0; i < max_size; i++) {
                queue[i].frame = av_frame_alloc();
            }
        }

        virtual ~FrameQueue() {
            for (int i = 0; i < max_size; i++) {
                Frame *vp = &queue[i];
                frame_queue_unref_item(vp);
                av_frame_free(&vp->frame);
            }
        }

        static void unref_item(Frame *vp) {
            av_frame_unref(vp->frame);
        }

        void signal() {
            std::unique_lock<std::mutex> locker(mutex);
            cond.notify_all();
            locker.unlock();
        }

        inline Frame *peek() const { return &queue[(rindex + rindex_shown) % max_size]; }

        inline Frame *peek_next() const { return &queue[(rindex + rindex_shown + 1) % max_size]; }

        inline Frame *peek_last() const { return &f->queue[f->rindex]; }

        Frame *peek_writable() const {
            /* wait until we have space to put a new frame */
            std::unique_lock<std::mutex> locker(mutex);
            cond.wait(locker, [&this]{ return this->size < this->max_size || this->pktq->abort_request; } );
            locker.unlock();
            return pktq->abort_request ? nullptr : &queue[windex];
        }

        Frame *peek_readable() const {
            /* wait until we have a readable new frame */
            std::unique_lock<std::mutex> locker(mutex);
            cond.wait(locker, [&this]{ return this->size - this->rindex_shown > 0 || this->pktq->abort_request; } );
            locker.unlock();
            return pktq->abort_request ? nullptr : &queue[(rindex + rindex_shown) % max_size];
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
            frame_queue_unref_item(&queue[rindex]);
            if (++rindex == max_size)
                rindex = 0;
            std::unique_lock<std::mutex> locker(mutex);
            size--;
            cond.notify_all();
            locker.unlock();
        }

        /* return the number of undisplayed frames in the queue */
        inline int nb_remaining() const { return size - rindex_shown; }

        /* return last shown position */
        int64_t last_pos() const {
            Frame *fp = &queue[rindex];
            if (rindex_shown && fp->serial == pktq->serial)
                return fp->pos;
            else
                return -1;
        }
};

#endif FRAME_QUEUE_H_