#define CATCH_CONFIG_MAIN  // Catch provides a main

#include <thread>

#include "catch.hpp"
#include "PacketQueue.hpp"

extern "C" {
	#include <libavcodec/avcodec.h> // codecs
}

// Compile with:
// cl -EHsc -I%CATCH_SINGLE_INCLUDE% TestPacketQueue.cpp /Fe"..\..\..\TestPacketQueue" /I"C:\Users\Florian\FFmpeg\FFmpeg-n3.4" "C:\Users\Florian\FFmpeg\FFmpeg-n3.4\libavcodec\avcodec.lib" "C:\Users\Florian\FFmpeg\FFmpeg-n3.4\libavutil\avutil.lib"

// Run with: TestPacketQueue.exe in the 'datavy-ffmpegplugin' directory

TEST_CASE( "Create, start, flush, delete (pass)", "[create-delete]" ) {
    PacketQueue packetQueue;
    packetQueue.start();
    packetQueue.flush();
}

TEST_CASE( "Flush queue (pass)", "[flush-queue]") {
    // Prepare the queue
    PacketQueue packetQueue;
    packetQueue.start();
    packetQueue.flush(); // flushes all packets
    AVPacket getPkt;
    REQUIRE( 0 == packetQueue.get(&getPkt, 0, nullptr) ); // non-blocking get of empty queue
}

TEST_CASE( "Put and get packet (pass)", "[put-get]" ) {
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
    REQUIRE( 0 == packetQueue.put(&putPkt) );
    REQUIRE( 0 < packetQueue.get(&getPkt, 1, nullptr) );
    REQUIRE( getPkt.pos == putPkt.pos );
    REQUIRE( *getPkt.data == *putPkt.data);
    // Note, I don't free the packets here to simplify the code
}

TEST_CASE( "Abort blocked read (pass)", "[abort-blocked-read]" ) {
    // Prepare the queue
    PacketQueue packetQueue;
    packetQueue.start();
    packetQueue.flush();

    // Read packet but blocked
    std::thread reader([&packetQueue]{
        AVPacket getPkt;
        REQUIRE( -1 == packetQueue.get(&getPkt, 1, nullptr) ); // abort returns -1
    });

    packetQueue.abort();
    reader.join();
}

TEST_CASE( "Multi-threaded put and get (pass)", "[multi-threaded-put-get]" ) {
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
            REQUIRE( 0 < packetQueue.get(&getPkt, 1, nullptr) );
            REQUIRE( getPkt.pos == reads );
        }
    });

    writer.join();
    reader.join();
}