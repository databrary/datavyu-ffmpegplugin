// Test the decoder
//
// Compile with
// cl -EHsc -I%CATCH_SINGLE_INCLUDE% TestDecoder.cpp /Fe"..\..\..\TestDecoder" /I"C:\Users\Florian\FFmpeg\FFmpeg-n3.4" "C:\Users\Florian\FFmpeg\FFmpeg-n3.4\libavutil\avutil.lib" "C:\Users\Florian\FFmpeg\FFmpeg-n3.4\libavcodec\avcodec.lib"
//
// Run in the 'datavy-ffmpegplugin' directory with: 
// TestDecoder.exe
//
// Note, the tests here are limited to memory allo/free
// TODO(fraudies): Add more tests with e.g. a single video frame/file to decode.

#include "gtest/gtest.h"

#include <iostream>

#include "Decoder.h"

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

