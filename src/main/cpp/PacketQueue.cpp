#include "PacketQueue.h"

int PacketQueue::_put(AVPacket *pkt) {
  MyAVPacketList *pkt1;

  if (abort_request)
    return -1;

  pkt1 = (struct MyAVPacketList *)av_malloc(sizeof(MyAVPacketList));
  if (!pkt1)
    return -1;
  pkt1->pkt = *pkt;
  pkt1->next = NULL;
  if (pkt == &flush_pkt)
    serial++;
  pkt1->serial = serial;

  if (!last_pkt)
    first_pkt = pkt1;
  else
    last_pkt->next = pkt1;
  last_pkt = pkt1;
  nb_packets++;
  size += pkt1->pkt.size + sizeof(*pkt1);
  duration += pkt1->pkt.duration;
  /* XXX: should duplicate packet data in DV case */
  condition = 1;
  cond.notify_one();
  return 0;
}

PacketQueue::PacketQueue()
    : abort_request(1), serial(0), nb_packets(0), first_pkt(nullptr),
      last_pkt(nullptr), size(0), duration(0), condition(0) {

  av_init_packet(&flush_pkt);
  flush_pkt.data = (uint8_t *)&flush_pkt;
}

void PacketQueue::flush() {
  MyAVPacketList *pkt, *pkt1;

  std::unique_lock<std::mutex> locker(mutex);
  for (pkt = first_pkt; pkt; pkt = pkt1) {
    pkt1 = pkt->next;
    av_packet_unref(&pkt->pkt);
    av_freep(&pkt);
  }
  last_pkt = NULL;
  first_pkt = NULL;
  nb_packets = 0;
  size = 0;
  duration = 0;
  locker.unlock();
}

void PacketQueue::abort() {
  std::unique_lock<std::mutex> locker(mutex);

  abort_request = 1;

  condition = 1;
  cond.notify_one();

  locker.unlock();
}

void PacketQueue::start() {
  std::unique_lock<std::mutex> locker(mutex);
  abort_request = 0;
  _put(&flush_pkt);
  locker.unlock();
}

int PacketQueue::put(AVPacket *pkt) {
  int ret;

  std::unique_lock<std::mutex> locker(mutex);
  ret = _put(pkt);
  locker.unlock();

  if (pkt != &flush_pkt && ret < 0)
    av_packet_unref(pkt);

  return ret;
}

int PacketQueue::put_null_packet(int stream_index) {
  AVPacket pkt1, *pkt = &pkt1;
  av_init_packet(pkt);
  pkt->data = NULL;
  pkt->size = 0;
  pkt->stream_index = stream_index;
  return put(pkt);
}

int PacketQueue::put_flush_packet() { return put(&flush_pkt); }

/* return < 0 if aborted, 0 if no packet and > 0 if packet.  */
int PacketQueue::get(AVPacket *pkt, int block, int *serial) {
  MyAVPacketList *pkt1;
  int ret;

  std::unique_lock<std::mutex> locker(mutex);

  for (;;) {
    if (abort_request) {
      ret = -1;
      break;
    }

    pkt1 = first_pkt;
    if (pkt1) {
      first_pkt = pkt1->next;
      if (!first_pkt)
        last_pkt = NULL;
      nb_packets--;
      size -= pkt1->pkt.size + sizeof(*pkt1);
      duration -= pkt1->pkt.duration;
      *pkt = pkt1->pkt;
      if (serial)
        *serial = pkt1->serial;
      av_free(pkt1);
      ret = 1;
      break;
    } else if (!block) {
      ret = 0;
      break;
    } else {
      condition = 0;
      cond.wait(locker, [&] { return condition; });
    }
  }
  locker.unlock();
  return ret;
}
