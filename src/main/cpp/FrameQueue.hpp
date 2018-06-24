#include "PacketQueue.hpp"
#include <mutex>
#include <condition_variable>

extern "C" {
	#include <libavcodec/avcodec.h> // codecs
    #include <libavutil/error.h> // error codes
}

#ifndef AV_FRAME_QUEUE_H_
#define AV_FRAME_QUEUE_H_

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
    public:
        Frame queue[FRAME_QUEUE_SIZE];

        int rindex; // read index
        int windex; // write index
        int size;
        int max_size;
        int keep_last;
        int rindex_shown; // read index shown
        std::mutex *mutex;
        std::condition_variable* cond;
        PacketQueue *pktq; // packet queue
        FrameQueue(){}

        static int frame_queue_init(FrameQueue *f, PacketQueue *pktq, int max_size, int keep_last)
        {
            int i;
            memset(f, 0, sizeof(FrameQueue));
            if (!(f->mutex = new std::mutex())) {
                // TODO: Add proper logging
                // av_log(NULL, AV_LOG_FATAL, "SDL_CreateMutex(): %s\n", SDL_GetError());
                return AVERROR(ENOMEM);
            }
            if (!(f->cond = new std::condition_variable())) {
                // TODO: Add proper logging
                // av_log(NULL, AV_LOG_FATAL, "SDL_CreateCond(): %s\n", SDL_GetError());
                return AVERROR(ENOMEM);
            }
            f->pktq = pktq;
            f->max_size = FFMIN(max_size, FRAME_QUEUE_SIZE);
            f->keep_last = !!keep_last;
            for (i = 0; i < f->max_size; i++)
                if (!(f->queue[i].frame = av_frame_alloc()))
                    return AVERROR(ENOMEM);
            return 0;
        }

        static void frame_queue_destory(FrameQueue *f)
        {
            for (int i = 0; i < f->max_size; i++) {
                Frame *vp = &f->queue[i];
                frame_queue_unref_item(vp);
                av_frame_free(&vp->frame);
            }
            delete f->mutex;
            delete f->cond;
        }

        static void frame_queue_unref_item(Frame *vp)
        {
            av_frame_unref(vp->frame);
        }

        static void frame_queue_signal(FrameQueue *f) {
            std::unique_lock<std::mutex> locker(*f->mutex);
            f->cond->notify_all();
            locker.unlock();
        }

        static Frame *frame_queue_peek(FrameQueue *f)
        {
            return &f->queue[(f->rindex + f->rindex_shown) % f->max_size];
        }

        static Frame *frame_queue_peek_next(FrameQueue *f)
        {
            return &f->queue[(f->rindex + f->rindex_shown + 1) % f->max_size];
        }

        static Frame *frame_queue_peek_last(FrameQueue *f)
        {
            return &f->queue[f->rindex];
        }

        static Frame *frame_queue_peek_writable(FrameQueue *f)
        {
            /* wait until we have space to put a new frame */
            std::unique_lock<std::mutex> locker(*f->mutex);
            f->cond->wait(locker, [&f]{ return f->size < f->max_size || f->pktq->abort_request; } );
            locker.unlock();

            if (f->pktq->abort_request)
                return NULL;

            return &f->queue[f->windex];
        }

        static Frame *frame_queue_peek_readable(FrameQueue *f)
        {
            /* wait until we have a readable new frame */
            std::unique_lock<std::mutex> locker(*f->mutex);
            f->cond->wait(locker, [&f]{ return f->size - f->rindex_shown > 0 || f->pktq->abort_request; } );
            locker.unlock();

            if (f->pktq->abort_request)
                return NULL;

            return &f->queue[(f->rindex + f->rindex_shown) % f->max_size];
        }

        static void frame_queue_push(FrameQueue *f)
        {
            if (++f->windex == f->max_size)
                f->windex = 0;
            std::unique_lock<std::mutex> locker(*f->mutex);
            f->size++;
            f->cond->notify_all();
            locker.unlock();
        }

        static void frame_queue_next(FrameQueue *f)
        {
            if (f->keep_last && !f->rindex_shown) {
                f->rindex_shown = 1;
                return;
            }
            frame_queue_unref_item(&f->queue[f->rindex]);
            if (++f->rindex == f->max_size)
                f->rindex = 0;
            std::unique_lock<std::mutex> locker(*f->mutex);
            f->size--;
            f->cond->notify_all();
            locker.unlock();
        }

        /* return the number of undisplayed frames in the queue */
        static int frame_queue_nb_remaining(FrameQueue *f)
        {
            return f->size - f->rindex_shown;
        }

        /* return last shown position */
        static int64_t frame_queue_last_pos(FrameQueue *f)
        {
            Frame *fp = &f->queue[f->rindex];
            if (f->rindex_shown && fp->serial == f->pktq->serial)
                return fp->pos;
            else
                return -1;
        }
};

#endif AV_FRAME_QUEUE_H_