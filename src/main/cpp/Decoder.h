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
private:
  AVPacket pkt;
  AVCodecContext *avctx;                     //  TODO: Change this to ref
  PacketQueue *queue;                        // TODO: Change this to ref
  std::condition_variable *empty_queue_cond; // TODO: Change this to ref
  int pkt_serial;
  int finished;
  int packet_pending;
  int decoder_reorder_pts;
  int64_t start_pts;
  AVRational start_pts_tb;
  int64_t next_pts;
  AVRational next_pts_tb;
  std::thread *decoder_tid;

public:
  Decoder(AVCodecContext *avctx, PacketQueue *queue,
          std::condition_variable *empty_queue_cond);
  virtual ~Decoder();

  int decode_frame(AVFrame *frame);

  void set_start_pts(int64_t start_pts);
  void set_start_pts_tb(AVRational start_pts_tb);

  inline int get_pkt_serial() const { return pkt_serial; }
  inline const AVCodecContext *get_avctx() const { return avctx; }

  inline int is_finished() const { return finished; }
  inline void setFinished(int f) { finished = f; }

  int start(const std::function<void()> &decoding);
  void abort(FrameQueue *fq);
};

#endif DECODER_H_
