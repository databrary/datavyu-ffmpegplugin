#define CATCH_CONFIG_MAIN  // Catch provides a main

#include "BufferForStream.hpp"
#include "Logger.h"
#include "catch.hpp"

// Compile: cl -EHsc -I%CATCH_SINGLE_INCLUDE% 010-TestCase.cpp && 010-TestCase --success
//cl org_datavyu_plugins_ffmpegplayer_MovieStream.cpp /Fe"..\..\MovieStream"^
// /I"C:\Users\Florian\FFmpeg-release-3.3"^
// /I"C:\Program Files\Java\jdk1.8.0_144\include"^
// /I"C:\Program Files\Java\jdk1.8.0_144\include\win32"^
// /showIncludes /MD /LD /link "C:\Program Files\Java\jdk1.8.0_144\lib\jawt.lib"^
// "C:\Users\Florian\FFmpeg-release-3.3\libavcodec\avcodec.lib"^
// "C:\Users\Florian\FFmpeg-release-3.3\libavformat\avformat.lib"^
// "C:\Users\Florian\FFmpeg-release-3.3\libavutil\avutil.lib"^
// "C:\Users\Florian\FFmpeg-release-3.3\libswscale\swscale.lib"^
// "C:\Users\Florian\FFmpeg-release-3.3\libswresample\swresample.lib"

// cl -EHsc -I%CATCH_SINGLE_INCLUDE% TestBufferForStream.cpp

TEST_CASE( "Single threaded write-read test (pass)", "[single-file]" ) {
    Logger* pLogger = new FileLogger("logger.txt");
    long first = 0; // first item in stream
    BufferForStream<int> bufferForStream(first, 8, 4);
    for (int value = 0; value < 6; ++value) {
        long current = first + value; // current item in stream
        bufferForStream.writeRequest(value, current);
        bufferForStream.writeComplete(current);
        pLogger->info("Writing %d, nRead %d.", value);
        bufferForStream.log(*pLogger);
    }
    // Read forward
    for (int item = 0; item < 6; ++item) {
        int value = 0;
        bufferForStream.read(value);
        pLogger->info("Read value %d.", value);
        bufferForStream.log(*pLogger);
        REQUIRE( value == item );
    }
    // Toggle
    //bufferForStream.toggle(6);
    //pLogger->info("After toggle.");
    //bufferForStream.log(*pLogger);
    // Read backward
    /*
    for (int item = 5; item >= 0; --item) {
        int value = 0;
        bufferForStream.read(value);
        pLogger->info("Read value %d.", value);
        bufferForStream.log(*pLogger);
        REQUIRE( value == item );
    }*/
    delete pLogger;
}
