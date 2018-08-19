// Test cases for the frame queue
//
// Compile with
// cl -EHsc -I%CATCH_SINGLE_INCLUDE% TestFrameQueue.cpp /Fe"..\..\..\TestFrameQueue" /I"C:\Users\Florian\FFmpeg\FFmpeg-n3.4" "C:\Users\Florian\FFmpeg\FFmpeg-n3.4\libavcodec\avcodec.lib" "C:\Users\Florian\FFmpeg\FFmpeg-n3.4\libavutil\avutil.lib"
//
// Run from the 'datavyu-ffmpegplugin' directory
// TestFrameQueue.exe

#define CATCH_CONFIG_MAIN // Catch provides a main method

#include <thread>

#include "catch.hpp"
#include "PacketQueue.hpp"
#include "FrameQueue.hpp"

TEST_CASE( "Create and delete (pass)", "[create-delete]" ) {
	PacketQueue packetQueue;
    FrameQueue frameQueue = FrameQueue::create_frame_queue(&packetQueue, SAMPLE_QUEUE_SIZE, 1);
}

TEST_CASE( "Test single write and read (pass)", "[single-read-write]" ) {
	
	// Initialize and start the packet queue
	PacketQueue packetQueue;
	packetQueue.start();

	// Initialize the frame queue
	FrameQueue frameQueue = FrameQueue::create_frame_queue(&packetQueue, SAMPLE_QUEUE_SIZE, 1);

	// Write
	Frame* pWriteable = frameQueue.peek_writable();
	REQUIRE(pWriteable != nullptr);
	pWriteable->pos = 123;
	frameQueue.push();

	// Read
	Frame* pReadable = frameQueue.peek_readable();
	REQUIRE(pReadable != nullptr);
	REQUIRE(pReadable->pos == pWriteable->pos);
}

TEST_CASE("Test status (pass)", "[status]") {
	// Tests nb_remaining and last_pos

	// Initialize and start the packet queue
	PacketQueue packetQueue;
	packetQueue.start();

	// Initialize the frame queue
	FrameQueue frameQueue = FrameQueue::create_frame_queue(&packetQueue, SAMPLE_QUEUE_SIZE, 1);

	// Test the intial status of the frame queue
	REQUIRE(frameQueue.nb_remaining() == 0);
	REQUIRE(frameQueue.last_pos() == -1); // nothing was shown yet => -1

	// Push two frames
	frameQueue.push();
	frameQueue.push();

	// Test the status of the frame queue
	REQUIRE(frameQueue.nb_remaining() == 2);
	REQUIRE(frameQueue.last_pos() == -1); // nothing was shown yet => -1

	// Read one frame
	frameQueue.next();
	REQUIRE(frameQueue.nb_remaining() == 1);
	REQUIRE(frameQueue.last_pos() == -1); // serials don't match
}

TEST_CASE("Test multi-threaded write and read (pass)", "[multi-threaded-read-write]") {

	// Initialize and start the packet queue
	PacketQueue packetQueue;
	packetQueue.start();

	// Initialize the frame queue with smaller size to test for blocking/unblocking
	FrameQueue frameQueue = FrameQueue::create_frame_queue(&packetQueue, VIDEO_PICTURE_QUEUE_SIZE, 1);

	// Write some frames
	std::thread writer([&frameQueue] {
		for (int writes = 0; writes < 10; writes++) {
			Frame *pWritable = frameQueue.peek_writable();
			REQUIRE(pWritable != nullptr);
			pWritable->pos = writes;
			frameQueue.push();
		}
	});

	// Read some frames (blocking) and check the pos field
	std::thread reader([&frameQueue] {
		for (int reads = 0; reads < 10; reads++) {
			Frame* pReadable = frameQueue.peek_readable();
			REQUIRE(pReadable != nullptr);
			frameQueue.next();
			REQUIRE(pReadable->pos == reads);
		}
	});

	writer.join();
	reader.join();
}

TEST_CASE("Test signal", "[signal]") {

	// Intialize and start the packet queue
	PacketQueue packetQueue;
	packetQueue.start();

	// Initialize the frame queue
	FrameQueue frameQueue = FrameQueue::create_frame_queue(&packetQueue, SAMPLE_QUEUE_SIZE, 1);

	// Read packet but blocked
	std::thread reader([&frameQueue] {
		REQUIRE(nullptr == frameQueue.peek_readable());
	});

	packetQueue.abort(); // sets abort in the packet queue
	frameQueue.signal(); // picks up abort from the packet queue and returns nullptr
	reader.join();
}

TEST_CASE("Test peek, peek next, and peek last", "[peek-next-last]") {

	// Initialize and start the packet queue
	PacketQueue packetQueue;
	packetQueue.start();

	// Initialize the frame queue
	FrameQueue frameQueue = FrameQueue::create_frame_queue(&packetQueue, SAMPLE_QUEUE_SIZE, 1);

	// Write frames with pos 1, 2, 3, 4
	for (int writes = 1; writes <= 4; ++writes) {
		Frame* pWritable = frameQueue.peek_writable();
		REQUIRE(pWritable != nullptr);
		pWritable->pos = writes;
		frameQueue.push();
	}

	// Peek first, second, and last
	Frame* pReadable = nullptr;
	pReadable = frameQueue.peek();
	REQUIRE(pReadable != nullptr); 
	REQUIRE(pReadable->pos == 1);
	pReadable = frameQueue.peek_next();
	REQUIRE(pReadable != nullptr);
	REQUIRE(pReadable->pos == 2);
	pReadable = frameQueue.peek_last();
	REQUIRE(pReadable != nullptr);
	REQUIRE(pReadable->pos == 1);

	// Consume one frames
	frameQueue.next();
	pReadable = frameQueue.peek();
	REQUIRE(pReadable != nullptr);
	REQUIRE(pReadable->pos == 2);
	pReadable = frameQueue.peek_last();
	REQUIRE(pReadable != nullptr);
	REQUIRE(pReadable->pos == 1);

	// Consume two frames
	frameQueue.next();
	pReadable = frameQueue.peek();
	REQUIRE(pReadable != nullptr);
	REQUIRE(pReadable->pos == 3);
	pReadable = frameQueue.peek_last();
	REQUIRE(pReadable != nullptr);
	REQUIRE(pReadable->pos == 2);
}