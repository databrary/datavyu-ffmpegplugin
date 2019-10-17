#include "FrameQueue.h"
#include "PacketQueue.h"
#include <condition_variable>
#include <thread>

extern "C" {
#include <libavcodec/avcodec.h> // codecs
#include <libavutil/error.h>    // error codes
#include <libavutil/rational.h>
}

#ifndef DECODER_H_
#define DECODER_H_

class Decoder {
public:
  Decoder(AVCodecContext *avctx, PacketQueue *queue,
          std::condition_variable *empty_queue_cond);
  virtual ~Decoder();

  int Decode(AVFrame *frame);

  inline void SetStartPts(int64_t start_pts) { start_pts_ = start_pts; }
  void SetStartPtsTimebase(AVRational start_pts_timebase) {
    start_pts_timebase_ = start_pts_timebase;
  }

  inline int GetSerial() const { return serial_; }
  inline int64_t GetNumberOfIncorrectDtsValues() const {
    return p_codec_context_->pts_correction_num_faulty_dts;
  }
  inline int64_t GetNumberOfIncorrectPtsValues() const {
    return p_codec_context_->pts_correction_num_faulty_pts;
  }

  inline bool IsFinished() const { return is_finished_; }
  inline void setFinished(bool finished) { is_finished_ = finished; }

  int Start(const std::function<void()> &decoding);
  void Stop(FrameQueue *frame_queue);

private:
  AVPacket packet_;
  AVCodecContext *p_codec_context_;
  PacketQueue *p_packet_queue_;
  std::condition_variable *p_is_empty_condition_;
  int serial_;
  bool is_finished_;
  bool is_packet_pending_;
  int do_reorder_; // let decoder reorder pts 0=off 1=on -1=auto
  int64_t start_pts_;
  AVRational start_pts_timebase_;
  int64_t next_pts_;
  AVRational next_pts_timebase_;
  std::thread *p_decoder_thread_;
};

#endif DECODER_H_
