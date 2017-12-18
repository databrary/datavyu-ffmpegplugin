#define CATCH_CONFIG_MAIN  // Catch provides a main

#include <thread>
#include <atomic>
#include <mutex>

#include "BufferForStream.hpp"
#include "Logger.h"
#include "catch.hpp"

// Compile with: cl -EHsc -I%CATCH_SINGLE_INCLUDE% TestBufferForStream.cpp
// Run with: TestBufferForStream.exe

// Note: Each test case produces a log file for debugging purposes. However; the log file is not written if the test
// fails.

TEST_CASE( "Single threaded write-read test (pass)", "[single-file]" ) {
    // Tests:
    // - writing and reading in forward mode
    // - toggle and reading backward
    // - toggle and reading forward
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
    pLogger->info("\n");
    pLogger->info("Read backward");
    pLogger->info("Toggle items %d.", nToggleItem);
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
    pLogger->info("\n");
    pLogger->info("Read forward.");
    pLogger->info("Toggle items %d.", nToggleItem);
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
    // Tests
    // Toggle
    // Reading backward
    // Writing backward
    // Reading backward
    // Toggle
    // Writing forward
    // Reading forward
    Logger* pLogger = new FileLogger("single-threaded-write-read-backward-test.txt");
    long first = 0; // first item in stream
    long currentRead = 15;
    long currentWrite = currentRead;
    BufferForStream<long> bufferForStream(first, 8, 4);

    // Write two values
    for (int count = 0; count < 2; ++count) {
        pLogger->info("Writing %ld.", currentWrite);
        bufferForStream.writeRequest(currentWrite, currentWrite);
        currentWrite += bufferForStream.writeComplete(currentWrite);
        bufferForStream.log(*pLogger);
        currentWrite++;
    }

    // Read forward
    pLogger->info("\n");
    pLogger->info("Read forward.");
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
    for (int count = 0; count < 4; ++count) {
        pLogger->info("Writing %ld.", currentWrite);
        bufferForStream.writeRequest(currentWrite, currentWrite);
        currentWrite += bufferForStream.writeComplete(currentWrite);
        bufferForStream.log(*pLogger);
        currentWrite++;
    }

    // Read some values
    pLogger->info("\n");
    pLogger->info("Reading in reverse.");
    currentRead--;
    for (int count = 0; count < 5; ++count) {
        long value = 0;
        bufferForStream.read(value);
        pLogger->info("Read value %ld.", value);
        bufferForStream.log(*pLogger);
        currentRead--;
        REQUIRE( value == currentRead );
    }

    // Write some values
    pLogger->info("\n");
    pLogger->info("Write some more in reverse.");
    for (int count = 0; count < 4; ++count) {
        pLogger->info("Writing %ld.", currentWrite);
        bufferForStream.writeRequest(currentWrite, currentWrite);
        currentWrite += bufferForStream.writeComplete(currentWrite);
        bufferForStream.log(*pLogger);
        currentWrite++;
    }

    // Switch back into forward writing
    nToggleItem = bufferForStream.toggle(currentWrite);
    currentWrite += nToggleItem;
    pLogger->info("\n");
    pLogger->info("Writing in forward mode (after backward writing)");
    for (int count = 0; count < 3; ++count) {
        bufferForStream.writeRequest(currentWrite, currentWrite);
        currentWrite += bufferForStream.writeComplete(currentWrite);
        pLogger->info("Writing %ld.", currentWrite);
        bufferForStream.log(*pLogger);
        currentWrite++;
    }

    // Read a few values in forward mode
    pLogger->info("\n");
    pLogger->info("Read a few values in forward mode.");
    for (int count = 0; count < 4; ++count) {
        long value;
        bufferForStream.read(value);
        pLogger->info("Read value %ld.", value);
        bufferForStream.log(*pLogger);
        currentRead++;
        REQUIRE( value == currentRead );
    }

    // Clean-up
    delete pLogger;
}


TEST_CASE( "Multi threaded write-read test (pass)", "[single-file]" ) {
    // Tests
    // Writer and reader thread in forward mode
    Logger* pLogger = new FileLogger("multi-threaded-read-write-forward-test.txt");
    int nItem = 8;
    std::atomic<long> currentRead = 15;
    std::atomic<long> currentWrite = 15;
    BufferForStream<long> bufferForStream(0, nItem, nItem/2);

    // Define the writer thread to write some items
    std::thread writer([&bufferForStream, &currentWrite, &pLogger]{
        long writeValue;
        for (int writes = 0; writes < 10; writes++) {
            writeValue = currentWrite;
            bufferForStream.writeRequest(writeValue, currentWrite);
            currentWrite += bufferForStream.writeComplete(currentWrite);
            pLogger->info("Wrote %ld.", writeValue);
            bufferForStream.log(*pLogger);
            currentWrite++;
        }
    });

    // Define the reader thread to read some items
    std::thread reader([&bufferForStream, &currentRead, &pLogger]{
        long readValue;
        for (int reads = 0; reads < 10; reads++) {
            bufferForStream.read(readValue);
            pLogger->info("Read %ld and expected %ld.", readValue, currentRead.load());
            bufferForStream.log(*pLogger);
            REQUIRE( readValue == currentRead );
            currentRead++;
        }
    });

    writer.join();
    reader.join();

    // Clean-up
    delete pLogger;
}


TEST_CASE( "Multi threaded write-read backward test (pass)", "[single-file]" ) {
    // Tests
    // Writer and reader thread in backward mode
    Logger* pLogger = new FileLogger("multi-threaded-read-write-backward-test.txt");
    int nItem = 8;
    std::atomic<long> currentRead = 25;
    std::atomic<long> currentWrite = 25;
    BufferForStream<long> bufferForStream(0, nItem, nItem/2);

    // Write two items/read two items, then toggle
    long writeValue;
    long readValue;
    for (int writeReads = 0; writeReads < 2; writeReads++) {
        // Read write value
        writeValue = currentWrite;
        bufferForStream.writeRequest(writeValue, currentWrite);
        currentWrite += bufferForStream.writeComplete(currentWrite);
        pLogger->info("Wrote %ld.", writeValue);
        bufferForStream.log(*pLogger);
        // Read value
        bufferForStream.read(readValue);
        pLogger->info("Read %ld and expected %ld.", readValue, currentRead.load());
        bufferForStream.log(*pLogger);
        // Compare and update
        REQUIRE( readValue == currentRead );
        currentWrite++;
        currentRead++;
    }

    // Toggle
    currentWrite += bufferForStream.toggle(currentWrite);
    currentRead -= 2;

    // Define the writer thread to write multiple of nItem values
    std::thread writer([&bufferForStream, &currentWrite, &pLogger, &nItem]{
        long writeValue;
        for (int writes = 0; writes < 2*nItem; writes++) {
            writeValue = currentWrite;
            bufferForStream.writeRequest(writeValue, currentWrite);
            currentWrite += bufferForStream.writeComplete(currentWrite);
            pLogger->info("Wrote %ld.", writeValue);
            bufferForStream.log(*pLogger);
            currentWrite++;
        }
    });

    // Define the reader thread to read multiple of nItem values (same number as in teh writer thread)
    std::thread reader([&bufferForStream, &currentRead, &pLogger, &nItem]{
        long readValue;
        for (int reads = 0; reads < 2*nItem; reads++) {
            bufferForStream.read(readValue);
            pLogger->info("Read %ld and expected %ld.", readValue, currentRead.load());
            bufferForStream.log(*pLogger);
            REQUIRE( readValue == currentRead );
            currentRead--;
        }
    });

    // Ensure threads finished
    writer.join();
    reader.join();

    // Cleanup
    delete pLogger;
}
