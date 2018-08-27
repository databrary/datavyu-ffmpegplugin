// Test cases for the frame queue
//
// Compile with
// cl -EHsc -I%CATCH_SINGLE_INCLUDE% TestFrameQueue.cpp /Fe"..\..\..\TestFrameQueue" /I"C:\Users\Florian\FFmpeg\FFmpeg-n3.4" "C:\Users\Florian\FFmpeg\FFmpeg-n3.4\libavcodec\avcodec.lib" "C:\Users\Florian\FFmpeg\FFmpeg-n3.4\libavutil\avutil.lib"
//
// Run from the 'datavyu-ffmpegplugin' directory
// TestFrameQueue.exe

#include <thread>

#include "gtest/gtest.h"
#include "PacketQueue.h"
#include "FrameQueue.h"

TEST( FrameQueueTest, CreateDeleteFrameQueueTest ) {
	PacketQueue packetQueue;
    FrameQueue* frameQueue = FrameQueue::create_frame_queue(&packetQueue, SAMPLE_QUEUE_SIZE, 1);
}

TEST(FrameQueueTest, SingleReadWRite ) {
	
	// Initialize and start the packet queue
	PacketQueue packetQueue;
	packetQueue.start();

	// Initialize the frame queue
	FrameQueue* frameQueue = FrameQueue::create_frame_queue(&packetQueue, SAMPLE_QUEUE_SIZE, 1);

	// Write
	Frame* pWriteable = frameQueue->peek_writable();
	ASSERT_TRUE(pWriteable != nullptr);
	pWriteable->pos = 123;
	frameQueue->push();

	// Read
	Frame* pReadable = frameQueue->peek_readable();
	ASSERT_TRUE(pReadable != nullptr);
	ASSERT_TRUE(pReadable->pos == pWriteable->pos);
}

TEST(FrameQueueTest, StatusFrameTest) {
	// Tests nb_remaining and last_pos

	// Initialize and start the packet queue
	PacketQueue packetQueue;
	packetQueue.start();

	// Initialize the frame queue
	FrameQueue* frameQueue = FrameQueue::create_frame_queue(&packetQueue, SAMPLE_QUEUE_SIZE, 1);

	// Test the intial status of the frame queue
	ASSERT_TRUE(frameQueue->nb_remaining() == 0);
	ASSERT_TRUE(frameQueue->last_pos() == -1); // nothing was shown yet => -1

	// Push two frames
	frameQueue->push();
	frameQueue->push();

	// Test the status of the frame queue
	ASSERT_TRUE(frameQueue->nb_remaining() == 2);
	ASSERT_TRUE(frameQueue->last_pos() == -1); // nothing was shown yet => -1

	// Read one frame
	frameQueue->next();
	ASSERT_TRUE(frameQueue->nb_remaining() == 1);
	ASSERT_TRUE(frameQueue->last_pos() == -1); // serials don't match
}

TEST(FrameQueueTest, MultiThreadReadWriteTest) {

	// Initialize and start the packet queue
	PacketQueue packetQueue;
	packetQueue.start();

	// Initialize the frame queue with smaller size to test for blocking/unblocking
	FrameQueue* frameQueue = FrameQueue::create_frame_queue(&packetQueue, VIDEO_PICTURE_QUEUE_SIZE, 1);

	// Write some frames
	std::thread writer([&frameQueue] {
		for (int writes = 0; writes < 10; writes++) {
			Frame *pWritable = frameQueue->peek_writable();
			ASSERT_TRUE(pWritable != nullptr);
			pWritable->pos = writes;
			frameQueue->push();
		}
	});

	// Read some frames (blocking) and check the pos field
	std::thread reader([&frameQueue] {
		for (int reads = 0; reads < 10; reads++) {
			Frame* pReadable = frameQueue->peek_readable();
			ASSERT_TRUE(pReadable != nullptr);
			frameQueue->next();
			ASSERT_TRUE(pReadable->pos == reads);
		}
	});

	writer.join();
	reader.join();
}

TEST(FrameQueueTest, SignalFrameQueueTest) {

	// Intialize and start the packet queue
	PacketQueue packetQueue;
	packetQueue.start();

	// Initialize the frame queue
	FrameQueue* frameQueue = FrameQueue::create_frame_queue(&packetQueue, SAMPLE_QUEUE_SIZE, 1);

	// Read packet but blocked
	std::thread reader([&frameQueue] {
		ASSERT_TRUE(nullptr == frameQueue->peek_readable());
	});

	packetQueue.abort(); // sets abort in the packet queue
	frameQueue->signal(); // picks up abort from the packet queue and returns nullptr
	reader.join();
}

TEST(FrameQueueTest, PeekLastNextFrameTest) {

	// Initialize and start the packet queue
	PacketQueue packetQueue;
	packetQueue.start();

	// Initialize the frame queue
	FrameQueue* frameQueue = FrameQueue::create_frame_queue(&packetQueue, SAMPLE_QUEUE_SIZE, 1);

	// Write frames with pos 1, 2, 3, 4
	for (int writes = 1; writes <= 4; ++writes) {
		Frame* pWritable = frameQueue->peek_writable();
		ASSERT_TRUE(pWritable != nullptr);
		pWritable->pos = writes;
		frameQueue->push();
	}

	// Peek first, second, and last
	Frame* pReadable = nullptr;
	pReadable = frameQueue->peek();
	ASSERT_TRUE(pReadable != nullptr);
	ASSERT_TRUE(pReadable->pos == 1);
	pReadable = frameQueue->peek_next();
	ASSERT_TRUE(pReadable != nullptr);
	ASSERT_TRUE(pReadable->pos == 2);
	pReadable = frameQueue->peek_last();
	ASSERT_TRUE(pReadable != nullptr);
	ASSERT_TRUE(pReadable->pos == 1);

	// Consume one frames
	frameQueue->next();
	pReadable = frameQueue->peek();
	ASSERT_TRUE(pReadable != nullptr);
	ASSERT_TRUE(pReadable->pos == 2);
	pReadable = frameQueue->peek_last();
	ASSERT_TRUE(pReadable != nullptr);
	ASSERT_TRUE(pReadable->pos == 1);

	// Consume two frames
	frameQueue->next();
	pReadable = frameQueue->peek();
	ASSERT_TRUE(pReadable != nullptr);
	ASSERT_TRUE(pReadable->pos == 3);
	pReadable = frameQueue->peek_last();
	ASSERT_TRUE(pReadable != nullptr);
	ASSERT_TRUE(pReadable->pos == 2);
}

//int main(int argc, char **argv) {
//	::testing::InitGoogleTest(&argc, argv);
//	return RUN_ALL_TESTS();
//}