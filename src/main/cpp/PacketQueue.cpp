#include "PacketQueue.h"

int PacketQueue::_Put(AVPacket *p_packet) {
  MyAVPacketList *pkt1;

  if (is_abort_requested_) {
    return -1;
  }

  pkt1 = (struct MyAVPacketList *)av_malloc(sizeof(MyAVPacketList));
  if (!pkt1) {
    return -1;
  }
  pkt1->packet_ = *p_packet;
  pkt1->p_next_ = NULL;

  // If we request a flush, then increase the serial to flush all dependent
  // queues
  if (p_packet == &flush_packet_) {
    serial_++;
  }

  pkt1->serial_ = serial_;

  if (!p_last_packet_) {
    p_first_packet_ = pkt1;
  } else {
    p_last_packet_->p_next_ = pkt1;
  }
  p_last_packet_ = pkt1;
  num_packets_++;
  size_ += pkt1->packet_.size + sizeof(*pkt1);
  duration_ += pkt1->packet_.duration;
  /* XXX: should duplicate packet data in DV case */
  put_condition_ = true;
  condition_.notify_one();
  return 0;
}

PacketQueue::PacketQueue()
    : is_abort_requested_(true), serial_(0), num_packets_(0),
      p_first_packet_(nullptr), p_last_packet_(nullptr), size_(0), duration_(0),
      put_condition_(false) {

  av_init_packet(&flush_packet_);
  flush_packet_.data = (uint8_t *)&flush_packet_;
}

void PacketQueue::Flush() {
  MyAVPacketList *pkt, *pkt1;

  std::unique_lock<std::mutex> locker(mutex_);
  for (pkt = p_first_packet_; pkt; pkt = pkt1) {
    pkt1 = pkt->p_next_;
    av_packet_unref(&pkt->packet_);
    av_freep(&pkt);
  }
  p_last_packet_ = NULL;
  p_first_packet_ = NULL;
  num_packets_ = 0;
  size_ = 0;
  duration_ = 0;
  locker.unlock();
}

void PacketQueue::Abort() {
  std::unique_lock<std::mutex> locker(mutex_);

  is_abort_requested_ = 1;

  put_condition_ = true;
  condition_.notify_one();

  locker.unlock();
}

void PacketQueue::Start() {
  std::unique_lock<std::mutex> locker(mutex_);
  is_abort_requested_ = 0;
  _Put(&flush_packet_);
  locker.unlock();
}

int PacketQueue::Put(AVPacket *p_packet) {
  int ret;

  std::unique_lock<std::mutex> locker(mutex_);
  ret = _Put(p_packet);
  locker.unlock();

  if (p_packet != &flush_packet_ && ret < 0) {
    av_packet_unref(p_packet);
  }

  return ret;
}

int PacketQueue::PutNullPacket(int stream_index) {
  AVPacket pkt1, *pkt = &pkt1;
  av_init_packet(pkt);
  pkt->data = NULL;
  pkt->size = 0;
  pkt->stream_index = stream_index;
  return Put(pkt);
}

int PacketQueue::PutFlushPacket() { return Put(&flush_packet_); }

/* return < 0 if aborted, 0 if no packet and > 0 if packet.  */
int PacketQueue::Get(AVPacket *pkt, int block, int *serial) {
  MyAVPacketList *pkt1;
  int ret;

  std::unique_lock<std::mutex> locker(mutex_);

  for (;;) {
    if (is_abort_requested_) {
      ret = -1;
      break;
    }

    pkt1 = p_first_packet_;
    if (pkt1) {
      p_first_packet_ = pkt1->p_next_;
      if (!p_first_packet_) {
        p_last_packet_ = NULL;
      }
      num_packets_--;
      size_ -= pkt1->packet_.size + sizeof(*pkt1);
      duration_ -= pkt1->packet_.duration;
      *pkt = pkt1->packet_;
      if (serial) {
        *serial = pkt1->serial_;
      }
      av_free(pkt1);
      ret = 1;
      break;
    } else if (!block) {
      ret = 0;
      break;
    } else {
      put_condition_ = false;
      condition_.wait(locker, [&] { return put_condition_; });
    }
  }
  locker.unlock();
  return ret;
}
