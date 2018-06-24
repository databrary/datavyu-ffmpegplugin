#define CATCH_CONFIG_MAIN  // Catch provides a main

#include <thread>
#include <atomic>
#include <mutex>

#include "Logger.h"
#include "Clock.hpp"
#include "catch.hpp"

// Compile with:
// cl -EHsc -I%CATCH_SINGLE_INCLUDE% TestClock.cpp /Fe"..\..\..\TestClock" /I"C:\Users\Florian\FFmpeg\FFmpeg-n3.4" "C:\Users\Florian\FFmpeg\FFmpeg-n3.4\libavutil\avutil.lib"

// Run with: TestClock.exe in the main directory

TEST_CASE( "Single threaded clock (pass)", "[single-file]" ) {
    // Tests:
    // - writing and reading in forward mode with wrap around
    Logger* pLogger = new FileLogger("single-threaded-clock.log");
    pLogger->info("Simple test case");
    int serial = 0;
    Clock* pClock = new Clock(&serial);
    pLogger->info("Get time of the clock %f", pClock->get_clock());
    delete pClock;
    delete pLogger;
}
