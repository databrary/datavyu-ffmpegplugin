#include <condition_variable>
#include <mutex>

extern "C" {
#include <libavcodec/avcodec.h> // codecs
#include <libavutil/mem.h>      // memory
}

#ifndef PACKET_QUEUE_H_
#define PACKET_QUEUE_H_

// Port of the packet queue from ffplay.c into c++
//
// Replaces the SDL mutex through c++ std mutex/condition variable
class PacketQueue {
public:
  AVPacket
      flush_packet_; // TODO(fraudies): better use one object for all queues

  PacketQueue();

  virtual ~PacketQueue() {
    Flush();
    av_packet_unref(&flush_packet_);
    av_freep(&flush_packet_);
  }

  void Flush();

  void Abort();

  inline bool IsAbortRequested() const { return is_abort_requested_; }

  inline void GetPtrSerial(int **pp_serial) { *pp_serial = &serial_; }

  inline int GetSerial() const { return serial_; }

  inline int GetSize() const { return size_; }

  inline int getNumberOfPackets() const { return num_packets_; }

  inline int64_t GetDuration() const { return duration_; }

  void Start();

  int Put(AVPacket *p_packet); // Must remain a pointer to cleanup package

  int PutNullPacket(int stream_index);

  int PutFlushPacket();

  inline bool IsFlushPacket(const AVPacket &pkt) const {
    return pkt.data == flush_packet_.data;
  }

  /* return < 0 if aborted, 0 if no packet and > 0 if packet.  */
  int Get(AVPacket *pkt, int block, int *serial);

private:
  // This list maintains the next for the queue
  typedef struct MyAVPacketList {
    AVPacket packet_;
    struct MyAVPacketList *p_next_;
    int serial_;
  } MyAVPacketList;

  bool is_abort_requested_;
  int serial_;
  int num_packets_;

  MyAVPacketList *p_first_packet_;
  MyAVPacketList *p_last_packet_;
  int size_;
  int64_t duration_;
  bool put_condition_;
  std::mutex mutex_;
  std::condition_variable condition_;

  int _Put(AVPacket *p_packet);
};

#endif PACKET_QUEUE_H_
