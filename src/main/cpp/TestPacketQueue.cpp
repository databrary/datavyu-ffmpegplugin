#include <thread>

#include "gtest/gtest.h"
#include "PacketQueue.h"

extern "C" {
#include <libavcodec/avcodec.h> // codecs
}

// Compile with:
// cl -EHsc -I%CATCH_SINGLE_INCLUDE% TestPacketQueue.cpp /Fe"..\..\..\TestPacketQueue" /I"C:\Users\Florian\FFmpeg\FFmpeg-n3.4" "C:\Users\Florian\FFmpeg\FFmpeg-n3.4\libavcodec\avcodec.lib" "C:\Users\Florian\FFmpeg\FFmpeg-n3.4\libavutil\avutil.lib"

// Run with: TestPacketQueue.exe in the 'datavy-ffmpegplugin' directory

TEST ( PacketQueueTest, CreateDeletePacketTest ) {
    PacketQueue packetQueue;
    packetQueue.start();
    packetQueue.flush();
}

TEST ( PacketQueueTest,  FlushPacketQueueTest) {
    // Prepare the queue
    PacketQueue packetQueue;
    packetQueue.start();
    packetQueue.flush(); // flushes all packets
    AVPacket getPkt;
	ASSERT_TRUE( 0 == packetQueue.get(&getPkt, 0, nullptr) ); // non-blocking get of empty queue
}

TEST (PacketQueueTest, PutGetPacketTest ) {
    // Create and initialize some packets
    uint8_t dummy = 123;
    AVPacket putPkt;
    AVPacket getPkt;
    av_init_packet(&putPkt);
    putPkt.pos = 123;
    putPkt.data = &dummy;
    // Create, start the packet queue and put/get
    PacketQueue packetQueue;
    packetQueue.start();
    packetQueue.flush();
	ASSERT_TRUE( 0 == packetQueue.put(&putPkt) );
	ASSERT_TRUE( 0 < packetQueue.get(&getPkt, 1, nullptr) );
	ASSERT_TRUE( getPkt.pos == putPkt.pos );
	ASSERT_TRUE( *getPkt.data == *putPkt.data);
    // Note, I don't free the packets here to simplify the code
}

TEST (PacketQueueTest , PutGetFlushPacketTest ) {
    AVPacket getPkt;
    PacketQueue packetQueue;
    packetQueue.start();
    packetQueue.flush();
    packetQueue.put_flush_packet();
	ASSERT_TRUE( 0 < packetQueue.get(&getPkt, 1, nullptr) );
	ASSERT_TRUE( packetQueue.is_flush_packet(getPkt) == true );
}

TEST (PacketQueueTest, AbortBlockReadPacketTest ) {
    // Prepare the queue
    PacketQueue packetQueue;
    packetQueue.start();
    packetQueue.flush();

    // Read packet but blocked
    std::thread reader([&packetQueue]{
        AVPacket getPkt;
		ASSERT_TRUE( -1 == packetQueue.get(&getPkt, 1, nullptr) ); // abort returns -1
    });

    packetQueue.abort();
    reader.join();
}

TEST (PacketQueueTest, MultiThreadPutGetPacketTest ) {
    // Prepare the queue
    PacketQueue packetQueue;
    packetQueue.start();
    packetQueue.flush();

    // Write some packets
    std::thread writer([&packetQueue]{
        for (int writes = 0; writes < 10; writes++) {
            AVPacket putPkt;
            av_init_packet(&putPkt);
            putPkt.pos = writes;
            packetQueue.put(&putPkt);
        }
    });

    // Read some packets (blocking)
    std::thread reader([&packetQueue]{
        AVPacket getPkt;
        for (int reads = 0; reads < 10; reads++) {
			ASSERT_TRUE( 0 < packetQueue.get(&getPkt, 1, nullptr) );
			ASSERT_TRUE( getPkt.pos == reads );
        }
    });

    writer.join();
    reader.join();
}

int main(int argc, char **argv) {
	::testing::InitGoogleTest(&argc, argv);
	return RUN_ALL_TESTS();
}