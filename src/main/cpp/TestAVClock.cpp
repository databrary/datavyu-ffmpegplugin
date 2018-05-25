#define CATCH_CONFIG_MAIN  // Catch provides a main

#include <thread>
#include <atomic>
#include <mutex>

#include "Logger.h"
#include "AVClock.hpp"
#include "catch.hpp"

// Compile with:
// cl -EHsc -I%CATCH_SINGLE_INCLUDE% TestAVClock.cpp
// Run with: TestAVClock.exe

TEST_CASE( "Single threaded clock (pass)", "[single-file]" ) {
    // Tests:
    // - writing and reading in forward mode with wrap around
    Logger* pLogger = new FileLogger("single-threaded-clock.txt");
    pLogger->info("Simple test case");
    AVClock* pClock = new AVClock();
    pLogger->info("Get time of the clock %f", pClock->getTime());
    delete pClock;
    delete pLogger;
}
