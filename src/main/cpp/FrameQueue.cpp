#include "FrameQueue.h"

void unref_item(Frame *vp) { av_frame_unref(vp->frame); }

FrameQueue::FrameQueue(const PacketQueue *pktq, int max_size, int keep_last)
    : rindex(0), windex(0), size(0), max_size(max_size), keep_last(keep_last),
      rindex_shown(0), pktq(pktq) {}

FrameQueue *FrameQueue::create_frame_queue(const PacketQueue *pktq,
                                           int max_size, int keep_last) {
  FrameQueue *fq = new (std::nothrow) FrameQueue(pktq, max_size, keep_last);

  if (!fq) {
    av_log(NULL, AV_LOG_ERROR, "Unable to create frame queue object");
    return nullptr;
  }

  fq->queue = new (std::nothrow) Frame[max_size](); // initialize with zeros

  if (!fq->queue) {
    av_log(NULL, AV_LOG_ERROR, "Unable to initialize frame queue");
    return nullptr;
  }

  for (int i = 0; i < max_size; i++) {
    fq->queue[i].frame = av_frame_alloc();

    if (!fq->queue[i].frame) {
      av_log(NULL, AV_LOG_ERROR, "Unable to create frame in queue");
      delete fq; // will clean up any memory allocated before
      return nullptr;
    }
  }

  return fq;
}

void FrameQueue::signal() {
  std::unique_lock<std::mutex> locker(mutex);
  cond.notify_one();
  locker.unlock();
}

Frame *FrameQueue::peek_writable() {
  // waits until we have space to put a new frame
  std::unique_lock<std::mutex> locker(mutex);
  cond.wait(locker,
            [&] { return size < max_size || pktq->is_abort_request(); });
  locker.unlock();
  return pktq->is_abort_request() ? nullptr : &queue[windex];
}

Frame *FrameQueue::peek_readable() {
  // waits until we have a readable new frame
  std::unique_lock<std::mutex> locker(mutex);
  cond.wait(locker, [&] {
    return size - rindex_shown > 0 || pktq->is_abort_request();
  });
  locker.unlock();
  return pktq->is_abort_request() ? nullptr
                                  : &queue[(rindex + rindex_shown) % max_size];
}

void FrameQueue::push() {
  if (++windex == max_size)
    windex = 0;
  std::unique_lock<std::mutex> locker(mutex);
  size++;
  cond.notify_one();
  locker.unlock();
}

void FrameQueue::next() {
  if (keep_last && !rindex_shown) {
    rindex_shown = 1;
    return;
  }
  unref_item(&queue[rindex]);
  if (++rindex == max_size)
    rindex = 0;
  std::unique_lock<std::mutex> locker(mutex);
  size--;
  cond.notify_one();
  locker.unlock();
}

// return last shown position
int64_t FrameQueue::last_pos() {
  Frame *fp = &queue[rindex];
  return rindex_shown && fp->serial == pktq->get_serial() ? fp->pos : -1;
}
