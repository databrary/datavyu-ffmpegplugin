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
  AVFrame *frame;
  int serial;
  double pts;      // presentation timestamp for the frame
  double duration; // estimated duration of the frame
  int64_t pos;     // byte position of the frame in the input file
  int width;
  int height;
  int format;
  AVRational aspect_ration;
  int uploaded;
  int flip_v;
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
private:
  Frame *queue; // TOOD: This might be a bit slower than the original code;
                // check whether it matters
  int rindex;   // read index
  int windex;   // write index
  int size;
  int max_size;
  int keep_last;
  int rindex_shown; // read index shown
  std::mutex mutex;
  std::condition_variable cond;
  const PacketQueue *pktq; // packet queue

  static void unref_item(Frame *vp) { av_frame_unref(vp->frame); }

  FrameQueue(const PacketQueue *pktq, int max_size,
             int keep_last); // private because of memory management
public:
  // Use this method to create a new frame queue to ensure cases where memory
  // allocation fails are handled properly
  static FrameQueue *create_frame_queue(const PacketQueue *pktq, int max_size,
                                        int keep_last);

  virtual ~FrameQueue() {
    for (int i = 0; i < max_size; i++) {
      Frame *vp = &queue[i];
      if (vp) {
        unref_item(vp);
        av_frame_free(&vp->frame);
      }
    }
    delete[] queue;
  }

  void signal();

  inline Frame *peek() { return &queue[(rindex + rindex_shown) % max_size]; }

  inline Frame *peek_next() {
    return &queue[(rindex + rindex_shown + 1) % max_size];
  }

  inline Frame *peek_last() { return &queue[rindex]; }

  inline std::mutex &get_mutex() { return mutex; }

  inline int get_rindex_shown() const { return rindex_shown; }

  Frame *peek_writable();

  Frame *peek_readable();

  void push();

  void next();

  // return the number of undisplayed frames in the queue
  inline int nb_remaining() const { return size - rindex_shown; }

  // return last shown position
  int64_t last_pos();
};

#endif FRAME_QUEUE_H_
