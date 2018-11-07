#include "gtest/gtest.h"

#include "PacketQueue.h"

#include <thread>

extern "C" {
#include <libavcodec/avcodec.h> // codecs
}

TEST(PacketQueueTest, CreateDeletePacketTest) {
  PacketQueue packetQueue;
  packetQueue.Start();
  packetQueue.Flush();
}

TEST(PacketQueueTest, FlushPacketQueueTest) {
  // Prepare the queue
  PacketQueue packetQueue;
  packetQueue.Start();
  packetQueue.Flush(); // flushes all packets
  AVPacket getPkt;
  ASSERT_EQ(0, packetQueue.Get(&getPkt, 0,
                               nullptr)); // non-blocking get of empty queue
}

TEST(PacketQueueTest, PutGetPacketTest) {
  // Create and initialize some packets
  uint8_t dummy = 123;
  AVPacket putPkt;
  AVPacket getPkt;
  av_init_packet(&putPkt);
  putPkt.pos = 123;
  putPkt.data = &dummy;
  // Create, start the packet queue and put/get
  PacketQueue packetQueue;
  packetQueue.Start();
  packetQueue.Flush();
  ASSERT_EQ(0, packetQueue.Put(&putPkt));
  ASSERT_LT(0, packetQueue.Get(&getPkt, 1, nullptr));
  ASSERT_EQ(getPkt.pos, putPkt.pos);
  ASSERT_EQ(*getPkt.data, *putPkt.data);
  // Note, I don't free the packets here to simplify the code
}

TEST(PacketQueueTest, PutGetFlushPacketTest) {
  AVPacket getPkt;
  PacketQueue packetQueue;
  packetQueue.Start();
  packetQueue.Flush();
  packetQueue.PutFlushPacket();
  ASSERT_LT(0, packetQueue.Get(&getPkt, 1, nullptr));
  ASSERT_TRUE(packetQueue.IsFlushPacket(getPkt));
}

TEST(PacketQueueTest, AbortBlockReadPacketTest) {
  // Prepare the queue
  PacketQueue packetQueue;
  packetQueue.Start();
  packetQueue.Flush();

  // Read packet but blocked
  std::thread reader([&packetQueue] {
    AVPacket getPkt;
    ASSERT_EQ(-1, packetQueue.Get(&getPkt, 1, nullptr)); // abort returns -1
  });

  packetQueue.Abort();
  reader.join();
}

TEST(PacketQueueTest, MultiThreadPutGetPacketTest) {
  // Prepare the queue
  PacketQueue packetQueue;
  packetQueue.Start();
  packetQueue.Flush();

  // Write some packets
  std::thread writer([&packetQueue] {
    for (int writes = 0; writes < 10; writes++) {
      AVPacket putPkt;
      av_init_packet(&putPkt);
      putPkt.pos = writes;
      packetQueue.Put(&putPkt);
    }
  });

  // Read some packets (blocking)
  std::thread reader([&packetQueue] {
    AVPacket getPkt;
    for (int reads = 0; reads < 10; reads++) {
      ASSERT_LT(0, packetQueue.Get(&getPkt, 1, nullptr));
      ASSERT_EQ(getPkt.pos, reads);
    }
  });

  writer.join();
  reader.join();
}
