#include "Decoder.h"

Decoder::Decoder(AVCodecContext *avctx, PacketQueue *queue, std::condition_variable *empty_queue_cond) :
	avctx(avctx),
	queue(queue),
	empty_queue_cond(empty_queue_cond),
	pkt_serial(-1),
	finished(0),
	packet_pending(0),
	decoder_reorder_pts(0),
	start_pts(AV_NOPTS_VALUE),
	start_pts_tb(av_make_q(0, 0)),
	next_pts(0),
	next_pts_tb(av_make_q(0, 0)),
	step(1),
	decoder_tid(nullptr) {
	// Note, that pkt will need to be initialized for the case when decode_frame is never run
	// Sidenote: the move ref code will clean this initialization
	av_init_packet(&pkt);
}


int Decoder::decode_frame(AVFrame *frame, AVSubtitle *sub) {
	int ret = AVERROR(EAGAIN);

	for (;;) {
		AVPacket pkt;

		if (queue->get_serial() == pkt_serial) {
			do {
				if (queue->is_abort_request())
					return -1;

				switch (avctx->codec_type) {
				case AVMEDIA_TYPE_VIDEO:
					ret = avcodec_receive_frame(avctx, frame);
					if (ret >= 0) {
						if (decoder_reorder_pts == -1) {
							frame->pts = frame->best_effort_timestamp * step;
						}
						else if (!decoder_reorder_pts) {
							frame->pts = frame->pkt_dts * step;
						}
					}
					break;
				case AVMEDIA_TYPE_AUDIO:
					ret = avcodec_receive_frame(avctx, frame);
					if (ret >= 0) {
						AVRational tb = av_make_q(1, frame->sample_rate);
						if (frame->pts != AV_NOPTS_VALUE)
							frame->pts = av_rescale_q(frame->pts, avctx->pkt_timebase, tb);
						else if (next_pts != AV_NOPTS_VALUE)
							frame->pts = av_rescale_q(next_pts, next_pts_tb, tb);
						if (frame->pts != AV_NOPTS_VALUE) {
							next_pts = frame->pts + frame->nb_samples ;
							next_pts_tb = tb;
						}
					}
					break;
				}
				if (ret == AVERROR_EOF) {
					finished = pkt_serial;
					avcodec_flush_buffers(avctx);
					return 0;
				}
				if (ret >= 0)
					return 1;
			} while (ret != AVERROR(EAGAIN));
		}

		do {
			if (queue->get_nb_packets() == 0)
				empty_queue_cond->notify_one();
			if (packet_pending) {
				av_packet_move_ref(&pkt, &this->pkt);
				packet_pending = 0;
			}
			else {
				if (queue->get(&pkt, 1, &pkt_serial) < 0)
					return -1;
			}
		} while (queue->get_serial() != pkt_serial);

		if (queue->is_flush_packet(pkt)) {
			avcodec_flush_buffers(avctx);
			finished = 0;
			next_pts = start_pts;
			next_pts_tb = start_pts_tb;
		}
		else {
			if (avctx->codec_type == AVMEDIA_TYPE_SUBTITLE) {
				int got_frame = 0;
				ret = avcodec_decode_subtitle2(avctx, sub, &got_frame, &pkt);
				if (ret < 0) {
					ret = AVERROR(EAGAIN);
				}
				else {
					if (got_frame && !pkt.data) {
						packet_pending = 1;
						av_packet_move_ref(&this->pkt, &pkt);
					}
					ret = got_frame ? 0 : (pkt.data ? AVERROR(EAGAIN) : AVERROR_EOF);
				}
			}
			else {
				if (avcodec_send_packet(avctx, &pkt) == AVERROR(EAGAIN)) {
					av_log(avctx, AV_LOG_ERROR, "Receive_frame and send_packet both returned EAGAIN, which is an API violation.\n");
					packet_pending = 1;
					av_packet_move_ref(&this->pkt, &pkt);
				}
			}
			av_packet_unref(&pkt);
		}
	}
}

void Decoder::set_start_pts(int64_t start_pts) {
	this->start_pts = start_pts;
}

void Decoder::set_start_pts_tb(AVRational start_pts_tb) {
	this->start_pts_tb = start_pts_tb;
}

void Decoder::set_pts_step(int newStep) {
	step = newStep;
}

// TODO(fraudies): This is tied to the audio/image/subtitle decode thread; 
// all three use the decode thread method from above with the respective object
// Re-design with lambda and tighter typing -- rather than passing the void pointers around
int Decoder::start(int(*fn)(void *), void *arg) {
	queue->start();
	decoder_tid = new std::thread(fn, arg);
	if (!decoder_tid) {
		av_log(NULL, AV_LOG_ERROR, "Can't create thread");
		return AVERROR(ENOMEM);
	}
	return 0;
}

void Decoder::abort(FrameQueue *fq) {
	// TODO(fraudies): Cleanup this design by keeping frame queue and packet queue together
	queue->abort();
	fq->signal();
	// Take care of the case when we never called start
	if (decoder_tid) {
		decoder_tid->join();
		delete decoder_tid;
		decoder_tid = nullptr;
	}
	queue->flush();
}