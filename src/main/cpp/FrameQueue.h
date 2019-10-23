#include "PacketQueue.h"
#include <condition_variable>
#include <mutex>

extern "C" {
#include <libavcodec/avcodec.h> // codecs
}

#ifndef FRAME_QUEUE_H_
#define FRAME_QUEUE_H_

// Common struct for handling decoded data and allocated buffers
typedef struct Frame {
  AVFrame *p_frame_;
  int serial_;
  double pts_;       // presentation timestamp for the frame
  double duration_;  // estimated duration of the frame
  int64_t byte_pos_; // byte position of the frame in the input file
  int frame_pos_;    // The Frame positin in the input file
  int width_;
  int height_;
  int format_;
  AVRational aspect_ratio_;
  bool is_uploaded_;
} Frame;

// Frame queue provides a container for frames that hold audio and image data
// Notice that the typical size for the audio/image data differs
//
// I made the following changes to the original code
// - removed the sub title
// - replaced the SDL mutex/condition by the
// std::mutex/std::conditional_variable
// - changed the allocation of the queue from static to dymanic to support all
// max_sizes
//
// Odities of the current API (per original code)
// - Always call push and next to write/read a frame
// - nb_remaining is the number of available frames to read
// - Coupled to packet queue which can cause an abort and when the frame queue
// is signaled returns nullptr
// - Use peek_readable and peek_writable to get a read/write pointer (in most
// cases)
// - In rarer cases use peek, peek_next, and peek_last (see tests for examples
// of the logic)
//
class FrameQueue {
public:
  // Use this method to create a new frame queue to ensure cases where memory
  // allocation fails are handled properly
  static int CreateFrameQueue(FrameQueue **pp_frame_queue,
                              const PacketQueue *p_packet_queue, int max_size,
                              bool keep_last);

  virtual ~FrameQueue();

  void Signal();

  // Last shown frame
  inline void Peek(Frame **pp_frame) {
    *pp_frame = &p_frames_[(read_index_ + read_index_shown_) % max_size_];
  }

  // Next to show frame
  inline void PeekNext(Frame **pp_frame) {
    *pp_frame = &p_frames_[(read_index_ + read_index_shown_ + 1) % max_size_];
  }

  // Last read index
  inline void PeekLast(Frame **pp_frame) {
    *pp_frame = &p_frames_[read_index_];
  }

  // Need to pass out a reference for the lock to work
  inline std::mutex &GetMutex() { return mutex_; }

  // True if this frame queue has shown a frame
  inline bool HasShownFrame() const { return read_index_shown_; }

  // Peek frame pointer to write to (Push will write)
  void PeekWritable(Frame **pp_frame);

  // Peek frame pointer to read from (Next will advance the pointer)
  void PeekReadable(Frame **pp_frame);

  void Push();

  void Next();

  // return the number of undisplayed frames in the queue
  inline int GetNumToDisplay() const { return size_ - read_index_shown_; }

  // return the byte position of the frame last shown
  int64_t GetBytePosOfLastFrame();

private:
  Frame *p_frames_; // container for the frames
  int read_index_;  // read index
  int write_index_; // write index
  int size_;        // size in bytes
  int max_size_;
  bool keep_last_;
  int read_index_shown_; // read index shown
  std::mutex mutex_;
  std::condition_variable condition_;
  const PacketQueue *p_packet_queue_; // packet queue

  static void unref_item(Frame *vp) { av_frame_unref(vp->p_frame_); }

  FrameQueue(const PacketQueue *pktq, int max_size,
             bool keep_last); // private because of memory management
};

#endif FRAME_QUEUE_H_
