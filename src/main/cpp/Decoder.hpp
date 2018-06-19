#include "PacketQueue.hpp"
#include "FrameQueue.hpp"
#include <condition_variable>
#include <thread>

extern "C" {
	#include <libavcodec/avcodec.h> // codecs
    #include <libavutil/error.h> // error codes
    #include <libavutil/rational.h>
}

#ifndef AV_DECODER_H_
#define AV_DECODER_H_

// TODO(fraudies): Convert this into a C++ class
// Note, I stripped out the sub title and replaced the SDL mutex by the std mutex
// and the SDL_Thread by the std::thread

class Decoder{
    private:
    public:
        Decoder(AVCodecContext *avctx,
                PacketQueue *queue,
                std::condition_variable *empty_queue_cond):
        decoder_reorder_pts(-1) {
            this->avctx = avctx;
            this->queue = queue;
            this->empty_queue_cond = empty_queue_cond;
            this->start_pts = AV_NOPTS_VALUE;
            this->pkt_serial = -1;
        }

        AVPacket pkt;
        PacketQueue *queue;
        AVCodecContext *avctx;
        int pkt_serial;
        int finished;
        int packet_pending;
        int decoder_reorder_pts;
        std::condition_variable *empty_queue_cond;
        int64_t start_pts;
        AVRational start_pts_tb;
        int64_t next_pts;
        AVRational next_pts_tb;
        std::thread *decoder_tid;

        int decoder_decode_frame(AVFrame *frame) {
            int ret = AVERROR(EAGAIN);

            for (;;) {
                AVPacket pkt;

                if (this->queue->serial == this->pkt_serial) {
                    do {
                        if (this->queue->abort_request)
                            return -1;

                        switch (this->avctx->codec_type) {
                            case AVMEDIA_TYPE_VIDEO:
                                ret = avcodec_receive_frame(this->avctx, frame);
                                if (ret >= 0) {
                                    if (decoder_reorder_pts == -1) {
                                        frame->pts = frame->best_effort_timestamp;
                                    } else if (!decoder_reorder_pts) {
                                        frame->pts = frame->pkt_dts;
                                    }
                                }
                                break;
                            case AVMEDIA_TYPE_AUDIO:
                                ret = avcodec_receive_frame(this->avctx, frame);
                                if (ret >= 0) {
                                    AVRational tb = struct AVRational {1, frame->sample_rate};
                                    if (frame->pts != AV_NOPTS_VALUE)
                                        frame->pts = av_rescale_q(frame->pts, this->avctx->pkt_timebase, tb);
                                    else if (this->next_pts != AV_NOPTS_VALUE)
                                        frame->pts = av_rescale_q(this->next_pts, this->next_pts_tb, tb);
                                    if (frame->pts != AV_NOPTS_VALUE) {
                                        this->next_pts = frame->pts + frame->nb_samples;
                                        this->next_pts_tb = tb;
                                    }
                                }
                                break;
                        }
                        if (ret == AVERROR_EOF) {
                            this->finished = this->pkt_serial;
                            avcodec_flush_buffers(this->avctx);
                            return 0;
                        }
                        if (ret >= 0)
                            return 1;
                    } while (ret != AVERROR(EAGAIN));
                }

                do {
                    if (this->queue->nb_packets == 0)
                        this->empty_queue_cond->notify_all();
                        //SDL_CondSignal(this->empty_queue_cond);
                    if (this->packet_pending) {
                        av_packet_move_ref(&pkt, &this->pkt);
                        this->packet_pending = 0;
                    } else {
                        if (PacketQueue::packet_queue_get(this->queue, &pkt, 1, &this->pkt_serial) < 0)
                            return -1;
                    }
                } while (this->queue->serial != this->pkt_serial);

                if (pkt.data == flush_pkt.data) {
                    avcodec_flush_buffers(this->avctx);
                    this->finished = 0;
                    this->next_pts = this->start_pts;
                    this->next_pts_tb = this->start_pts_tb;
                } else {
                    if (avcodec_send_packet(this->avctx, &pkt) == AVERROR(EAGAIN)) {
                        // TODO: Improve logging
                        av_log(this->avctx, AV_LOG_ERROR,
                            "Receive_frame and send_packet both returned EAGAIN, which is an API violation.\n");
                        this->packet_pending = 1;
                        av_packet_move_ref(&this->pkt, &pkt);
                    }
                    av_packet_unref(&pkt);
                }
            }
        }

        void decoder_destroy() {
            av_packet_unref(&this->pkt);
            avcodec_free_context(&this->avctx);
        }

        int decoder_start(int (*fn)(void *), void *arg)
        {
            PacketQueue::packet_queue_start(this->queue);
            this->decoder_tid = new std::thread(fn, arg);
            if (!this->decoder_tid) {
                // TODO: Proper logging
                av_log(NULL, AV_LOG_ERROR, "Can't create thread");
                return AVERROR(ENOMEM);
            }
            return 0;
        }

        void decoder_abort(FrameQueue *fq)
        {
            PacketQueue::packet_queue_abort(this->queue);
            FrameQueue::frame_queue_signal(fq);
            this->decoder_tid->join();
            delete this->decoder_tid;
            this->decoder_tid = NULL;
            PacketQueue::packet_queue_flush(this->queue);
        }
};

#endif AV_DECODER_H_