#include "FrameQueue.h"

void unref_item(Frame *vp) { av_frame_unref(vp->p_frame_); }

FrameQueue::FrameQueue(const PacketQueue *pktq, int max_size, bool keep_last)
    : read_index_(0), write_index_(0), size_(0), max_size_(max_size),
      keep_last_(keep_last), read_index_shown_(0), p_packet_queue_(pktq) {}

FrameQueue::~FrameQueue() {
  for (int i = 0; i < max_size_; i++) {
    Frame *vp = &p_frames_[i];
    if (vp) {
      unref_item(vp);
      av_frame_free(&vp->p_frame_);
    }
  }
  delete[] p_frames_;
}

int FrameQueue::CreateFrameQueue(FrameQueue **pp_frame_queue,
                                 const PacketQueue *p_packet_queue,
                                 int max_size, bool keep_last) {
  *pp_frame_queue =
      new (std::nothrow) FrameQueue(p_packet_queue, max_size, keep_last);

  if (!(*pp_frame_queue)) {
    av_log(NULL, AV_LOG_ERROR, "Unable to create frame queue object");
    return ENOMEM;
  }

  (*pp_frame_queue)->p_frames_ =
      new (std::nothrow) Frame[max_size](); // initialize with zeros

  if (!(*pp_frame_queue)->p_frames_) {
    av_log(NULL, AV_LOG_ERROR, "Unable to initialize frame queue");
    return ENOMEM;
  }

  for (int i = 0; i < max_size; i++) {
    (*pp_frame_queue)->p_frames_[i].p_frame_ = av_frame_alloc();

    if (!(*pp_frame_queue)->p_frames_[i].p_frame_) {
      av_log(NULL, AV_LOG_ERROR, "Unable to create frame in queue");
      delete (*pp_frame_queue); // will clean up any memory allocated before
      return ENOMEM;
    }
  }

  return 0;
}

void FrameQueue::Signal() {
  std::unique_lock<std::mutex> locker(mutex_);
  condition_.notify_one();
  locker.unlock();
}

void FrameQueue::PeekWritable(Frame **pp_frame) {
  // waits until we have space to put a new frame
  std::unique_lock<std::mutex> locker(mutex_);
  condition_.wait(locker, [&] {
    return size_ < max_size_ || p_packet_queue_->IsAbortRequested();
  });
  locker.unlock();
  *pp_frame =
      p_packet_queue_->IsAbortRequested() ? nullptr : &p_frames_[write_index_];
}

void FrameQueue::PeekReadable(Frame **pp_frame) {
  // waits until we have a readable new frame
  std::unique_lock<std::mutex> locker(mutex_);
  condition_.wait(locker, [&] {
    return size_ - read_index_shown_ > 0 || p_packet_queue_->IsAbortRequested();
  });
  locker.unlock();
  *pp_frame = p_packet_queue_->IsAbortRequested()
                  ? nullptr
                  : &p_frames_[(read_index_ + read_index_shown_) % max_size_];
}

void FrameQueue::Push() {
  if (++write_index_ == max_size_) {
    write_index_ = 0;
  }
  std::unique_lock<std::mutex> locker(mutex_);
  size_++;
  condition_.notify_one();
  locker.unlock();
}

void FrameQueue::Next() {
  if (keep_last_ && !read_index_shown_) {
    read_index_shown_ = 1;
    return;
  }
  unref_item(&p_frames_[read_index_]);
  if (++read_index_ == max_size_) {
    read_index_ = 0;
  }
  std::unique_lock<std::mutex> locker(mutex_);
  size_--;
  condition_.notify_one();
  locker.unlock();
}

// return last shown position
int64_t FrameQueue::GetBytePosOfLastFrame() {
  Frame *fp = &p_frames_[read_index_];
  return read_index_shown_ && fp->serial_ == p_packet_queue_->GetSerial()
             ? fp->byte_pos_
             : -1;
}
