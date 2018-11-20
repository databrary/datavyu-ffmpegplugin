#include "Decoder.h"

Decoder::Decoder(AVCodecContext *avctx, PacketQueue *queue,
                 std::condition_variable *empty_queue_cond)
    : p_codec_context_(avctx), p_packet_queue_(queue),
      p_is_empty_condition_(empty_queue_cond), serial_(-1), is_finished_(false),
      is_packet_pending_(false), do_reorder_(false),
      start_pts_(AV_NOPTS_VALUE), start_pts_timebase_(av_make_q(0, 0)),
      next_pts_(0), next_pts_timebase_(av_make_q(0, 0)),
      p_decoder_thread_(nullptr) {
  // Note, that pkt will need to be initialized for the case when decode_frame
  // is never run Sidenote: the move ref code will clean this initialization
  av_init_packet(&packet_);
}

Decoder::~Decoder() {
  av_packet_unref(&packet_);
  // TODO(fraudies): Move initialization of codec context into Decoder from VideoState
	avcodec_free_context(&p_codec_context_);
}

int Decoder::Decode(AVFrame *frame) {
  int ret = AVERROR(EAGAIN);

  for (;;) {
    AVPacket packet;

    if (p_packet_queue_->GetSerial() == serial_) {
      do {
        if (p_packet_queue_->IsAbortRequested())
          return -1;

        switch (p_codec_context_->codec_type) {
        case AVMEDIA_TYPE_VIDEO:
          ret = avcodec_receive_frame(p_codec_context_, frame);
          if (ret >= 0) {
            if (do_reorder_ == -1) {
              frame->pts = frame->best_effort_timestamp;
            } else if (!do_reorder_) {
              frame->pts = frame->pkt_dts;
            }
          }
          break;
        case AVMEDIA_TYPE_AUDIO:
          ret = avcodec_receive_frame(p_codec_context_, frame);
          if (ret >= 0) {
            AVRational tb = av_make_q(1, frame->sample_rate);
            if (frame->pts != AV_NOPTS_VALUE)
              frame->pts =
                  av_rescale_q(frame->pts, p_codec_context_->pkt_timebase, tb);
            else if (next_pts_ != AV_NOPTS_VALUE)
              frame->pts = av_rescale_q(next_pts_, next_pts_timebase_, tb);
            if (frame->pts != AV_NOPTS_VALUE) {
              next_pts_ = frame->pts + frame->nb_samples;
              next_pts_timebase_ = tb;
            }
          }
          break;
        }
        if (ret == AVERROR_EOF) {
          is_finished_ = serial_;
          avcodec_flush_buffers(p_codec_context_);
          return 0;
        }
        if (ret >= 0)
          return 1;
      } while (ret != AVERROR(EAGAIN));
    }

    do {
      if (p_packet_queue_->getNumberOfPackets() == 0)
        p_is_empty_condition_->notify_one();
      if (is_packet_pending_) {
        av_packet_move_ref(&packet, &packet_);
        is_packet_pending_ = false;
      } else {
        if (p_packet_queue_->Get(&packet, 1, &serial_) < 0)
          return -1;
      }
    } while (p_packet_queue_->GetSerial() != serial_);

    if (p_packet_queue_->IsFlushPacket(packet)) {
      avcodec_flush_buffers(p_codec_context_);
      is_finished_ = 0;
      next_pts_ = start_pts_;
      next_pts_timebase_ = start_pts_timebase_;
    } else {
      if (p_codec_context_->codec_type != AVMEDIA_TYPE_SUBTITLE &&
          avcodec_send_packet(p_codec_context_, &packet) == AVERROR(EAGAIN)) {
        av_log(p_codec_context_, AV_LOG_ERROR,
               "Receive_frame and send_packet both returned EAGAIN, which is "
               "an API violation.\n");
        is_packet_pending_ = true;
        av_packet_move_ref(&packet_, &packet);
      }
      av_packet_unref(&packet);
    }
  }
}

int Decoder::Start(const std::function<void()> &decoding) {
  p_packet_queue_->Start();
  p_decoder_thread_ = new std::thread([decoding] { decoding(); });
  if (!p_decoder_thread_) {
    av_log(NULL, AV_LOG_ERROR, "Can't create thread");
    return AVERROR(ENOMEM);
  }
  return 0;
}

void Decoder::Stop(FrameQueue *frame_queue) {
  // TODO(fraudies): Cleanup this design by keeping frame queue and packet queue
  // together
  p_packet_queue_->Abort();
  frame_queue->Signal();
  // Take care of the case when we never called start
  if (p_decoder_thread_) {
    p_decoder_thread_->join();
    delete p_decoder_thread_;
    p_decoder_thread_ = nullptr;
  }
  p_packet_queue_->Flush();
}
