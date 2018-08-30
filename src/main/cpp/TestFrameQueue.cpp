#include "gtest/gtest.h"

#include "PacketQueue.h"
#include "FrameQueue.h"

#include <thread>

TEST(FrameQueueTest, CreateDeleteFrameQueueTest ) {
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
	ASSERT_NE(pWriteable, nullptr);
	pWriteable->pos = 123;
	frameQueue->push();

	// Read
	Frame* pReadable = frameQueue->peek_readable();
	ASSERT_NE(pReadable, nullptr);
	ASSERT_EQ(pReadable->pos, pWriteable->pos);

	delete frameQueue;
	delete pWriteable;
	delete pReadable;
}

TEST(FrameQueueTest, StatusFrameTest) {
	// Tests nb_remaining and last_pos

	// Initialize and start the packet queue
	PacketQueue packetQueue;
	packetQueue.start();

	// Initialize the frame queue
	FrameQueue* frameQueue = FrameQueue::create_frame_queue(&packetQueue, SAMPLE_QUEUE_SIZE, 1);

	// Test the intial status of the frame queue
	ASSERT_EQ(frameQueue->nb_remaining(), 0);
	ASSERT_EQ(frameQueue->last_pos(), -1); // nothing was shown yet => -1

	// Push two frames
	frameQueue->push();
	frameQueue->push();

	// Test the status of the frame queue
	ASSERT_EQ(frameQueue->nb_remaining(), 2);
	ASSERT_EQ(frameQueue->last_pos(), -1); // nothing was shown yet => -1

	// Read one frame
	frameQueue->next();
	ASSERT_EQ(frameQueue->nb_remaining(), 1);
	ASSERT_EQ(frameQueue->last_pos(), -1); // serials don't match

	delete frameQueue;
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
			ASSERT_NE(pWritable, nullptr);
			pWritable->pos = writes;
			frameQueue->push();
		}
	});

	// Read some frames (blocking) and check the pos field
	std::thread reader([&frameQueue] {
		for (int reads = 0; reads < 10; reads++) {
			Frame* pReadable = frameQueue->peek_readable();
			ASSERT_NE(pReadable, nullptr);
			frameQueue->next();
			ASSERT_EQ(pReadable->pos, reads);
		}
	});

	writer.join();
	reader.join();

	delete frameQueue;
}

TEST(FrameQueueTest, SignalFrameQueueTest) {

	// Intialize and start the packet queue
	PacketQueue packetQueue;
	packetQueue.start();

	// Initialize the frame queue
	FrameQueue* frameQueue = FrameQueue::create_frame_queue(&packetQueue, SAMPLE_QUEUE_SIZE, 1);

	// Read packet but blocked
	std::thread reader([&frameQueue] {
		ASSERT_EQ(nullptr,frameQueue->peek_readable());
	});

	packetQueue.abort(); // sets abort in the packet queue
	frameQueue->signal(); // picks up abort from the packet queue and returns nullptr
	reader.join();

	delete frameQueue;
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
		ASSERT_NE(pWritable, nullptr);
		pWritable->pos = writes;
		frameQueue->push();
	}

	// Peek first, second, and last
	Frame* pReadable = nullptr;
	pReadable = frameQueue->peek();
	ASSERT_NE(pReadable, nullptr);
	ASSERT_EQ(pReadable->pos, 1);
	pReadable = frameQueue->peek_next();
	ASSERT_NE(pReadable, nullptr);
	ASSERT_EQ(pReadable->pos, 2);
	pReadable = frameQueue->peek_last();
	ASSERT_NE(pReadable, nullptr);
	ASSERT_EQ(pReadable->pos, 1);

	// Consume one frames
	frameQueue->next();
	pReadable = frameQueue->peek();
	ASSERT_NE(pReadable, nullptr);
	ASSERT_EQ(pReadable->pos, 2);
	pReadable = frameQueue->peek_last();
	ASSERT_NE(pReadable, nullptr);
	ASSERT_EQ(pReadable->pos, 1);

	// Consume two frames
	frameQueue->next();
	pReadable = frameQueue->peek();
	ASSERT_NE(pReadable, nullptr);
	ASSERT_EQ(pReadable->pos, 3);
	pReadable = frameQueue->peek_last();
	ASSERT_NE(pReadable, nullptr);
	ASSERT_EQ(pReadable->pos, 2);

	delete frameQueue;
	delete pReadable;
}