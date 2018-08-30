// Note, the tests here are limited to memory allo/free
// TODO(fraudies): Add more tests with e.g. a single video frame/file to decode.

#include "gtest/gtest.h"
#include "Decoder.h"

#include <iostream>



TEST (DecoderTest, CreateDeleteTest) {
	std::condition_variable empty_queue_cond;
	PacketQueue packetQueue;
	AVCodecContext* pAvctx = avcodec_alloc_context3(NULL);
	Decoder decoder(pAvctx, &packetQueue, &empty_queue_cond);
}

//int main(int argc, char **argv) {
//	::testing::InitGoogleTest(&argc, argv);
//	return RUN_ALL_TESTS();
//}

