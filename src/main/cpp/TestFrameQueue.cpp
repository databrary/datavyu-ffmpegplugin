#include "gtest/gtest.h"

#include "FrameQueue.h"
#include "PacketQueue.h"

#include <thread>

#define SAMPLE_QUEUE_SIZE 9
#define VIDEO_PICTURE_QUEUE_SIZE 3

TEST(FrameQueueTest, CreateDeleteFrameQueueTest) {
  PacketQueue packetQueue;
  FrameQueue *frameQueue = nullptr;
  FrameQueue::CreateFrameQueue(&frameQueue, &packetQueue, SAMPLE_QUEUE_SIZE, true);
  delete frameQueue;
}

TEST(FrameQueueTest, SingleReadWRite) {
  // Initialize and start the packet queue
  PacketQueue packetQueue;
  packetQueue.Start();

  // Initialize the frame queue
  FrameQueue *frameQueue = nullptr;
  FrameQueue::CreateFrameQueue(&frameQueue, &packetQueue, SAMPLE_QUEUE_SIZE, true);

  // Write
  Frame *pWriteable = nullptr;
  frameQueue->PeekWritable(&pWriteable);
  ASSERT_NE(pWriteable, nullptr);
  pWriteable->byte_pos_ = 123;
  frameQueue->Push();

  // Read
  Frame *pReadable = nullptr;
  frameQueue->PeekReadable(&pReadable);
  ASSERT_NE(pReadable, nullptr);
  ASSERT_EQ(pReadable->byte_pos_, pWriteable->byte_pos_);

  delete frameQueue;
}

TEST(FrameQueueTest, StatusFrameTest) {
  // Tests nb_remaining and last_pos

  // Initialize and start the packet queue
  PacketQueue packetQueue;
  packetQueue.Start();

  // Initialize the frame queue
  FrameQueue *frameQueue = nullptr;
  FrameQueue::CreateFrameQueue(&frameQueue, &packetQueue, SAMPLE_QUEUE_SIZE,
                               true);

  // Test the intial status of the frame queue
  ASSERT_EQ(frameQueue->GetNumToDisplay(), 0);
  ASSERT_EQ(frameQueue->GetBytePosOfLastFrame(),
            -1); // nothing was shown yet => -1

  // Push two frames
  frameQueue->Push();
  frameQueue->Push();

  // Test the status of the frame queue
  ASSERT_EQ(frameQueue->GetNumToDisplay(), 2);
  ASSERT_EQ(frameQueue->GetBytePosOfLastFrame(),
            -1); // nothing was shown yet => -1

  // Read one frame
  frameQueue->Next();
  ASSERT_EQ(frameQueue->GetNumToDisplay(), 1);
  ASSERT_EQ(frameQueue->GetBytePosOfLastFrame(), -1); // serials don't match

  delete frameQueue;
}

TEST(FrameQueueTest, MultiThreadReadWriteTest) {
  // Initialize and start the packet queue
  PacketQueue packetQueue;
  packetQueue.Start();

  // Initialize the frame queue with smaller size to test for
  // blocking/unblocking
  FrameQueue *frameQueue = nullptr;
  FrameQueue::CreateFrameQueue(&frameQueue, &packetQueue,
                               VIDEO_PICTURE_QUEUE_SIZE, true);

  // Write some frames
  std::thread writer([&frameQueue] {
    for (int writes = 0; writes < 10; writes++) {
      Frame *pWritable = nullptr;
      frameQueue->PeekWritable(&pWritable);
      ASSERT_NE(pWritable, nullptr);
      pWritable->byte_pos_ = writes;
      frameQueue->Push();
    }
  });

  // Read some frames (blocking) and check the pos field
  std::thread reader([&frameQueue] {
    for (int reads = 0; reads < 10; reads++) {
      Frame *pReadable = nullptr;
      frameQueue->PeekReadable(&pReadable);
      ASSERT_NE(pReadable, nullptr);
      frameQueue->Next();
      ASSERT_EQ(pReadable->byte_pos_, reads);
    }
  });

  writer.join();
  reader.join();

  delete frameQueue;
}

TEST(FrameQueueTest, SignalFrameQueueTest) {
  // Intialize and start the packet queue
  PacketQueue packetQueue;
  packetQueue.Start();

  // Initialize the frame queue
  FrameQueue *frameQueue = nullptr;
  FrameQueue::CreateFrameQueue(&frameQueue, &packetQueue, SAMPLE_QUEUE_SIZE,
                               true);

  // Read packet but blocked
  std::thread reader([&frameQueue] {
    Frame *p_frame = nullptr;
    frameQueue->PeekReadable(&p_frame);
    ASSERT_EQ(nullptr, p_frame);
  });

  packetQueue.Abort(); // sets abort in the packet queue
  frameQueue
      ->Signal(); // picks up abort from the packet queue and returns nullptr
  reader.join();

  delete frameQueue;
}

TEST(FrameQueueTest, PeekLastNextFrameTest) {
  // Initialize and start the packet queue
  PacketQueue packetQueue;
  packetQueue.Start();

  // Initialize the frame queue
  FrameQueue *frameQueue = nullptr;
  FrameQueue::CreateFrameQueue(&frameQueue, &packetQueue, SAMPLE_QUEUE_SIZE,
                               true);

  // Write frames with pos 1, 2, 3, 4
  for (int writes = 1; writes <= 4; ++writes) {
    Frame *pWritable = nullptr;
    frameQueue->PeekWritable(&pWritable);
    ASSERT_NE(pWritable, nullptr);
    pWritable->byte_pos_ = writes;
    frameQueue->Push();
  }

  // Peek first, second, and last
  Frame *pReadable = nullptr;
  frameQueue->Peek(&pReadable);
  ASSERT_NE(pReadable, nullptr);
  ASSERT_EQ(pReadable->byte_pos_, 1);
  frameQueue->PeekNext(&pReadable);
  ASSERT_NE(pReadable, nullptr);
  ASSERT_EQ(pReadable->byte_pos_, 2);
  frameQueue->PeekLast(&pReadable);
  ASSERT_NE(pReadable, nullptr);
  ASSERT_EQ(pReadable->byte_pos_, 1);

  // Consume one frames
  frameQueue->Next();
  frameQueue->Peek(&pReadable);
  ASSERT_NE(pReadable, nullptr);
  ASSERT_EQ(pReadable->byte_pos_, 2);
  frameQueue->PeekLast(&pReadable);
  ASSERT_NE(pReadable, nullptr);
  ASSERT_EQ(pReadable->byte_pos_, 1);

  // Consume two frames
  frameQueue->Next();
  frameQueue->Peek(&pReadable);
  ASSERT_NE(pReadable, nullptr);
  ASSERT_EQ(pReadable->byte_pos_, 3);
  frameQueue->PeekLast(&pReadable);
  ASSERT_NE(pReadable, nullptr);
  ASSERT_EQ(pReadable->byte_pos_, 2);

  delete frameQueue;
}
