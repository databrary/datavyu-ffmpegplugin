#define CATCH_CONFIG_MAIN  // Catch provides a main

#include <thread>
#include <atomic>
#include <mutex>

#include "LongBuffer.hpp"
#include "Logger.h"
#include "catch.hpp"

// Compile with: cl -EHsc -I%CATCH_SINGLE_INCLUDE% TestLongBuffer.cpp
// Run with: TestLongBuffer.exe

// Note: Each test case produces a log file for debugging purposes. However; the log file is not written if the test
// fails.
/*
TEST_CASE( "Single threaded write-read-wrap test (pass)", "[single-file]" ) {
    // Tests:
    // - writing and reading in forward mode with wrap around
    Logger* pLogger = new FileLogger("single-threaded-write-read-wrap-test.txt");
    //Logger* pLogger = new StreamLogger(&std::cerr);
    long first = 0;
    LongBuffer longBuffer(first, 8, 4);
    long *pValue = nullptr;
    long currentWrite = first;
    long currentRead = first;
    pLogger->info("Write a few values in forward mode.");
    // Write only
    for (int value = 0; value < 4; ++value) {
        longBuffer.writeRequest(&pValue, currentWrite);
        *pValue = currentWrite; // Writ value
        currentWrite += longBuffer.writeComplete(currentWrite);
        currentWrite++;
    }
    for (int value = 0; value < 18; ++value) {
        // Write
        longBuffer.writeRequest(&pValue, currentWrite);
        *pValue = currentWrite; // Writ value
        currentWrite += longBuffer.writeComplete(currentWrite);
        pLogger->info("Wrote %ld.", *pValue);
        longBuffer.log(*pLogger);
        currentWrite++;
        // Read
        longBuffer.read(&pValue);
        pLogger->info("Read %ld.", *pValue);
        longBuffer.log(*pLogger);
        REQUIRE( *pValue == currentRead );
        currentRead++;
    }
    pLogger->info("\n");
    // Toggle
    int nToggleItem = longBuffer.toggle(currentWrite);
    currentWrite += nToggleItem;
    currentRead -= 2;
    pLogger->info("After toggle.");
    pLogger->info("Number of items to toggle is %d.", nToggleItem);
    pLogger->info("Current write %ld and current read %ld.", currentWrite, currentRead);
    longBuffer.log(*pLogger);
    for (int value = 0; value < 14; value++) {
        // Write
        longBuffer.writeRequest(&pValue, currentWrite);
        *pValue = currentWrite; // Writ value
        currentWrite += longBuffer.writeComplete(currentWrite);
        pLogger->info("Wrote %ld.", *pValue);
        longBuffer.log(*pLogger);
        currentWrite++;
        // Read
        longBuffer.read(&pValue);
        pLogger->info("Read %ld.", *pValue);
        longBuffer.log(*pLogger);
        REQUIRE( *pValue == currentRead );
        currentRead--;
        pLogger->info("\n");
    }
    delete pLogger;
}


TEST_CASE( "Single threaded write-read test (pass)", "[single-file]" ) {
    // Tests:
    // - writing and reading in forward mode
    // - toggle and reading backward
    // - toggle and reading forward
    Logger* pLogger = new FileLogger("single-threaded-write-read-test.txt");
    long first = 0; // first item in stream
    LongBuffer longBuffer(first, 8, 4);
    long* pValue = nullptr;
    for (int value = 0; value < 6; ++value) {
        long current = first + value; // current item in stream
        longBuffer.writeRequest(&pValue, current);
        *pValue = value; // write the value here
        longBuffer.writeComplete(current);
        pLogger->info("Writing %d.", value);
        longBuffer.log(*pLogger);
    }
    pLogger->info("\n");
    pLogger->info("Read forward");
    // Read forward
    for (int item = 0; item < 6; ++item) {
        longBuffer.read(&pValue);
        pLogger->info("Read value %d.", *pValue);
        longBuffer.log(*pLogger);
        REQUIRE( *pValue == item );
    }
    // Toggle
    int nToggleItem = longBuffer.toggle(6);
    pLogger->info("\n");
    pLogger->info("Read backward");
    pLogger->info("Toggle items %d.", nToggleItem);
    longBuffer.log(*pLogger);
    // Read backward
    for (int item = 4; item >= 0; --item) {
        longBuffer.read(&pValue);
        pLogger->info("Read value %d.", *pValue);
        longBuffer.log(*pLogger);
        REQUIRE( *pValue == item );
    }
    // Toggle
    nToggleItem = longBuffer.toggle(7);
    pLogger->info("\n");
    pLogger->info("Read forward.");
    pLogger->info("Toggle items %d.", nToggleItem);
    longBuffer.log(*pLogger);
    // Read forward
    for (int item = 1; item < 6; ++item) {
        longBuffer.read(&pValue);
        pLogger->info("Read value %d.", *pValue);
        longBuffer.log(*pLogger);
        REQUIRE( *pValue == item );
    }
    delete pLogger;
}
*/

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
    //Logger* pLogger = new StreamLogger(&std::cerr);
    long first = 0; // first item in stream
    long currentRead = 15;
    long currentWrite = currentRead;
    LongBuffer longBuffer(first, 8, 4);
    long* pValue = nullptr;
    for (int count = 0; count < 2; ++count) {
        // Write
        longBuffer.writeRequest(&pValue, currentWrite);
        *pValue = currentWrite; // write the value here
        currentWrite += longBuffer.writeComplete(currentWrite);
        pLogger->info("Wrote %ld.", currentWrite);
        longBuffer.log(*pLogger);
        currentWrite++;
        // Read
        longBuffer.read(&pValue);
        pLogger->info("Read %ld.", *pValue);
        longBuffer.log(*pLogger);
        REQUIRE( *pValue == currentRead );
        currentRead++;
        pLogger->info("\n");
    }
    // Switch into backward mode
    int nToggleItem = longBuffer.toggle(currentWrite);
    currentWrite += nToggleItem;
    pLogger->info("In stream jumped to %ld.", currentWrite);
    pLogger->info("\n");
    for (int count = 0; count < 4; ++count) {
        pLogger->info("Writing %ld.", currentWrite);
        longBuffer.writeRequest(&pValue, currentWrite);
        *pValue = currentWrite; // write value here
        currentWrite += longBuffer.writeComplete(currentWrite);
        longBuffer.log(*pLogger);
        currentWrite++;
    }

    // Read some values
    pLogger->info("\n");
    pLogger->info("Reading in reverse.");
    currentRead--;
    for (int count = 0; count < 5; ++count) {
        longBuffer.read(&pValue);
        pLogger->info("Read value %ld.", *pValue);
        longBuffer.log(*pLogger);
        currentRead--;
        REQUIRE( *pValue == currentRead );
    }

    // Write some values
    pLogger->info("\n");
    pLogger->info("Write some more in reverse.");
    for (int count = 0; count < 4; ++count) {
        pLogger->info("Writing %ld.", currentWrite);
        longBuffer.writeRequest(&pValue, currentWrite);
        *pValue = currentWrite; // write value here
        currentWrite += longBuffer.writeComplete(currentWrite);
        longBuffer.log(*pLogger);
        currentWrite++;
    }

    // Switch back into forward writing
    nToggleItem = longBuffer.toggle(currentWrite);
    currentWrite += nToggleItem;
    pLogger->info("\n");
    pLogger->info("Writing in forward mode (after backward writing)");
    for (int count = 0; count < 3; ++count) {
        longBuffer.writeRequest(&pValue, currentWrite);
        *pValue = currentWrite; // write value here
        currentWrite += longBuffer.writeComplete(currentWrite);
        pLogger->info("Writing %ld.", currentWrite);
        longBuffer.log(*pLogger);
        currentWrite++;
    }

    // Read a few values in forward mode
    pLogger->info("\n");
    pLogger->info("Read a few values in forward mode.");
    for (int count = 0; count < 4; ++count) {
        longBuffer.read(&pValue);
        pLogger->info("Read value %ld.", *pValue);
        longBuffer.log(*pLogger);
        currentRead++;
        REQUIRE( *pValue == currentRead );
    }

    // Clean-up
    delete pLogger;
}

/*
TEST_CASE( "Multi threaded write-read test (pass)", "[single-file]" ) {
    // Tests
    // Writer and reader thread in forward mode
    Logger* pLogger = new FileLogger("multi-threaded-read-write-forward-test.txt");
    int nItem = 8;
    std::atomic<long> currentRead = 15;
    std::atomic<long> currentWrite = 15;
    LongBuffer longBuffer(0, nItem, nItem/2);

    // Define the writer thread to write some items
    std::thread writer([&longBuffer, &currentWrite, &pLogger]{
        long* pValue = nullptr;
        for (int writes = 0; writes < 10; writes++) {
            longBuffer.writeRequest(&pValue, currentWrite);
            *pValue = currentWrite; // write value here
            currentWrite += longBuffer.writeComplete(currentWrite);
            pLogger->info("Wrote %ld.", *pValue);
            longBuffer.log(*pLogger);
            currentWrite++;
        }
    });

    // Define the reader thread to read some items
    std::thread reader([&longBuffer, &currentRead, &pLogger]{
        long* pValue = nullptr;
        for (int reads = 0; reads < 10; reads++) {
            longBuffer.read(&pValue);
            pLogger->info("Read %ld and expected %ld.", *pValue, currentRead.load());
            longBuffer.log(*pLogger);
            REQUIRE( *pValue == currentRead );
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
    //Logger* pLogger = new StreamLogger(&std::cerr);
    int nItem = 8;
    std::atomic<long> currentRead = 25;
    std::atomic<long> currentWrite = 25;
    LongBuffer longBuffer(0, nItem, nItem/2);

    // Write two items/read two items, then toggle
    long* pWriteValue = nullptr;
    long* pReadValue = nullptr;
    for (int writeReads = 0; writeReads < 2; writeReads++) {
        // Read write value
        longBuffer.writeRequest(&pWriteValue, currentWrite);
        *pWriteValue = currentWrite; // write value here
        currentWrite += longBuffer.writeComplete(currentWrite);
        pLogger->info("Wrote %ld.", *pWriteValue);
        longBuffer.log(*pLogger);
        // Read value
        longBuffer.read(&pReadValue);
        pLogger->info("Read %ld and expected %ld.", *pReadValue, currentRead.load());
        longBuffer.log(*pLogger);
        // Compare and update
        REQUIRE( *pReadValue == currentRead );
        currentWrite++;
        currentRead++;
    }

    // Toggle
    currentWrite += longBuffer.toggle(currentWrite);
    currentRead -= 2;

    // Define the writer thread to write multiple of nItem values
    std::thread writer([&longBuffer, &currentWrite, &pLogger, &nItem]{
        long* pValue = nullptr;
        for (int writes = 0; writes < 2*nItem; writes++) {
            longBuffer.writeRequest(&pValue, currentWrite);
            *pValue = currentWrite; // write value here
            currentWrite += longBuffer.writeComplete(currentWrite);
            pLogger->info("Wrote %ld.", *pValue);
            longBuffer.log(*pLogger);
            currentWrite++;
        }
    });

    // Define the reader thread to read multiple of nItem values (same number as in teh writer thread)
    std::thread reader([&longBuffer, &currentRead, &pLogger, &nItem]{
        long* pValue = nullptr;
        for (int reads = 0; reads < 2*nItem; reads++) {
            longBuffer.read(&pValue);
            pLogger->info("Read %ld and expected %ld.", *pValue, currentRead.load());
            longBuffer.log(*pLogger);
            REQUIRE( *pValue == currentRead );
            currentRead--;
        }
    });

    // Ensure threads finished
    writer.join();
    reader.join();

    // Cleanup
    delete pLogger;
}
*/