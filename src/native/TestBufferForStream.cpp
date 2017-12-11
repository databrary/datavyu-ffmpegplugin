#define CATCH_CONFIG_MAIN  // Catch provides a main

#include "BufferForStream.hpp"
#include "Logger.h"
#include "catch.hpp"

// cl -EHsc -I%CATCH_SINGLE_INCLUDE% TestBufferForStream.cpp


TEST_CASE( "Single threaded write-read test (pass)", "[single-file]" ) {
    Logger* pLogger = new FileLogger("single-threaded-write-read-test.txt");
    long first = 0; // first item in stream
    BufferForStream<int> bufferForStream(first, 8, 4);
    for (int value = 0; value < 6; ++value) {
        long current = first + value; // current item in stream
        bufferForStream.writeRequest(value, current);
        bufferForStream.writeComplete(current);
        pLogger->info("Writing %d, nRead %d.", value);
        bufferForStream.log(*pLogger);
    }
    pLogger->info("\n");
    pLogger->info("Read forward");
    // Read forward
    for (int item = 0; item < 6; ++item) {
        int value = 0;
        bufferForStream.read(value);
        pLogger->info("Read value %d.", value);
        bufferForStream.log(*pLogger);
        REQUIRE( value == item );
    }
    // Toggle
    int nToggleItem = bufferForStream.toggle(6);
    pLogger->info("Toggle items %d.", nToggleItem);
    pLogger->info("\n");
    bufferForStream.log(*pLogger);
    // Read backward
    for (int item = 4; item >= 0; --item) {
        int value = 0;
        bufferForStream.read(value);
        pLogger->info("Read value %d.", value);
        bufferForStream.log(*pLogger);
        REQUIRE( value == item );
    }
    // Toggle
    nToggleItem = bufferForStream.toggle(7);
    pLogger->info("Toggle items %d.", nToggleItem);
    pLogger->info("\n");
    bufferForStream.log(*pLogger);
    // Read forward
    for (int item = 1; item < 6; ++item) {
        int value = 0;
        bufferForStream.read(value);
        pLogger->info("Read value %d.", value);
        bufferForStream.log(*pLogger);
        REQUIRE( value == item );
    }
    delete pLogger;
}

TEST_CASE( "Single threaded write-read-backward test (pass)", "[single-file]" ) {
    Logger* pLogger = new FileLogger("single-threaded-write-read-backward-test.txt");
    long first = 0; // first item in stream
    long currentRead = 15;
    long currentWrite = currentRead;
    BufferForStream<long> bufferForStream(first, 8, 4);
    for (int count = 0; count < 2; ++count) {
        bufferForStream.writeRequest(currentWrite, currentWrite);
        bufferForStream.writeComplete(currentWrite);
        pLogger->info("Writing %ld.", currentWrite);
        bufferForStream.log(*pLogger);
        currentWrite++;
    }
    pLogger->info("\n");
    // Read forward
    for (int count = 0; count < 2; ++count) {
        long value = 0;
        bufferForStream.read(value);
        pLogger->info("Read value %ld.", value);
        bufferForStream.log(*pLogger);
        REQUIRE( value == currentRead );
        currentRead++;
    }
    // Switch into backward mode
    int nToggleItem = bufferForStream.toggle(currentWrite);
    currentWrite += nToggleItem;
    pLogger->info("In stream jumped to %ld.", currentWrite);
    pLogger->info("\n");
    for (int count = 0; count < 7; ++count) {
        bufferForStream.writeRequest(currentWrite, currentWrite);
        bufferForStream.writeComplete(currentWrite);
        pLogger->info("Writing %ld.", currentWrite);
        bufferForStream.log(*pLogger);
        currentWrite--;
    }

    // Read all the available values; there should be 5 reads
    pLogger->info("\n");
    pLogger->info("Reading in reverse.");
    currentRead--;
    for (int count = 0; count < 4; ++count) {
        long value = 0;
        bufferForStream.read(value);
        pLogger->info("Read value %ld.", value);
        bufferForStream.log(*pLogger);
        currentRead--;
        REQUIRE( value == currentRead );
    }
    delete pLogger;
}
