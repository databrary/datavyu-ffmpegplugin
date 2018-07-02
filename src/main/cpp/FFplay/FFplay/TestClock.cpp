#define CATCH_CONFIG_MAIN  // Catch provides a main

#include "Logger.h"
#include "Clock.hpp"
#include "catch.hpp"

// Compile with:
// cl -EHsc -I%CATCH_SINGLE_INCLUDE% TestClock.cpp /Fe"..\..\..\TestClock" /I"C:\Users\Florian\FFmpeg\FFmpeg-n3.4" "C:\Users\Florian\FFmpeg\FFmpeg-n3.4\libavutil\avutil.lib"

// Run with: TestClock.exe in the 'datavy-ffmpegplugin' directory

TEST_CASE( "Create and delete clock (pass)", "[create-delete]" ) {
    Logger* pLogger = new FileLogger("create-delete-clock.log");
    pLogger->info("Simple test case");
    int serial = 0;
    Clock* pClock = new Clock(&serial);
    pLogger->info("Get time of the clock %f", pClock->get_clock());
    delete pClock;
    delete pLogger;
}
