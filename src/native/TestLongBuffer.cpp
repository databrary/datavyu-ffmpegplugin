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
TEST_CASE( "Single threaded write-read-wrap test (pass)", "[single-file]" ) {
    // Tests:
    // - writing and reading in forward mode with wrap around
    Logger* pLogger = new FileLogger("single-threaded-write-read-wrap-test.txt");
    //Logger* pLogger = new StreamLogger(&std::cerr);
    long first = 0;
    LongBuffer longBuffer(first, 10, 5);
    long *pValue = nullptr;
    long currentWrite = first;
    long currentRead = first;

    // Write a few values in forward mode
    pLogger->info("Write a few values in forward mode.");
    for (int counter = 0; counter < 4; ++counter) {
        longBuffer.writeRequest(&pValue, currentWrite);
        *pValue = currentWrite; // Writ value
        pLogger->info("Wrote %ld.", *pValue);
        currentWrite += longBuffer.writeComplete(currentWrite);
        currentWrite++;
    }
    pLogger->info("\n");

    // Write/read in forward mode
    pLogger->info("Write/read in forward mode.");
    for (int counter = 0; counter < 18; ++counter) {
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
        pLogger->info("\n");
    }

    // Toggle
    currentWrite += longBuffer.toggle(currentWrite);
    currentRead -= 2;
    pLogger->info("After toggle.");
    pLogger->info("Current write %ld and current read %ld.", currentWrite, currentRead);
    longBuffer.log(*pLogger);
    pLogger->info("\n");

    pLogger->info("Write/read in backward mode.");
    // Write/read in backward mode
    for (int counter = 0; counter < 10; counter++) {
        // Read
        longBuffer.read(&pValue);
        pLogger->info("Read %ld.", *pValue);
        longBuffer.log(*pLogger);
        REQUIRE( *pValue == currentRead );
        currentRead--;
        // Write
        longBuffer.writeRequest(&pValue, currentWrite);
        *pValue = currentWrite; // Writ value
        currentWrite += longBuffer.writeComplete(currentWrite);
        pLogger->info("Wrote %ld.", *pValue);
        longBuffer.log(*pLogger);
        currentWrite++;
        pLogger->info("\n");
    }

    // Cleanup
    delete pLogger;
}


TEST_CASE( "Single threaded write-read test (pass)", "[single-file]" ) {
    // Tests:
    // - writing and reading in forward mode
    // - toggle and reading backward
    // - toggle and reading forward
    Logger* pLogger = new FileLogger("single-threaded-write-read-test.txt");
    //Logger* pLogger = new StreamLogger(&std::cerr);
    long first = 0; // first item in stream
    LongBuffer longBuffer(first, 10, 5);
    long* pValue = nullptr;
    long currentRead = first;
    long currentWrite = first;

    // Write a few values
    pLogger->info("Write a few values.");
    for (int counter = 0; counter < 3; ++counter) {
        longBuffer.writeRequest(&pValue, currentWrite);
        *pValue = currentWrite; // write the value here
        currentWrite += longBuffer.writeComplete(currentWrite);
        currentWrite++;
        pLogger->info("Writing %d.", currentWrite);
        longBuffer.log(*pLogger);
        pLogger->info("\n");
    }

    pLogger->info("Read all values in forward direction.");
    // Read forward
    for (int counter = 0; counter < 3; ++counter) {
        longBuffer.read(&pValue);
        pLogger->info("Read value %d.", *pValue);
        longBuffer.log(*pLogger);
        REQUIRE( *pValue == currentRead );
        currentRead++;
        pLogger->info("\n");
    }

    // Toggle
    currentWrite += longBuffer.toggle(currentWrite);
    currentRead -= 2;
    pLogger->info("After toggle");
    pLogger->info("Current write %ld and current read %ld.", currentWrite, currentRead);
    longBuffer.log(*pLogger);
    pLogger->info("\n");

    // Read backward
    pLogger->info("Read backward");
    for (int counter = 0; counter < 2; counter++) {
        longBuffer.read(&pValue);
        pLogger->info("Read value %d.", *pValue);
        longBuffer.log(*pLogger);
        REQUIRE( *pValue == currentRead );
        currentRead--;
        pLogger->info("\n");
    }

    // Toggle
    pLogger->info("Toggle");
    pLogger->info("Before toggle the current write is %ld and the current read is %ld.", currentWrite, currentRead);
    currentWrite += longBuffer.toggle(currentWrite);
    currentRead += 2;
    pLogger->info("After the toggle the Current write is %ld and the current read is %ld.", currentWrite, currentRead);
    longBuffer.log(*pLogger);
    pLogger->info("\n");

    // Read forward
    pLogger->info("Read forward.");
    for (int counter = 0; counter < 2; ++counter) {
        longBuffer.read(&pValue);
        pLogger->info("Read value %d.", *pValue);
        longBuffer.log(*pLogger);
        REQUIRE( *pValue == currentRead );
        currentRead++;
    }

    // Cleanup
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
    //Logger* pLogger = new StreamLogger(&std::cerr);
    long first = 0; // first item in stream
    long currentRead = 15;
    long currentWrite = currentRead;
    LongBuffer longBuffer(first, 10, 5);
    long* pValue = nullptr;

    pLogger->info("Write/read a value.");
    for (int counter = 0; counter < 1; ++counter) {
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
    pLogger->info("Toggle");
    pLogger->info("Before toggle the current write is %ld and the current read is %ld.", currentWrite, currentRead);
    currentWrite +=  longBuffer.toggle(currentWrite);
    currentRead -= 2;
    pLogger->info("After the toggle the Current write is %ld and the current read is %ld.", currentWrite, currentRead);
    pLogger->info("\n");

    // Write a few values in backward mode
    pLogger->info("Write a few values in backward mode.");
    for (int count = 0; count < 5; ++count) {
        pLogger->info("Writing %ld.", currentWrite);
        longBuffer.writeRequest(&pValue, currentWrite);
        *pValue = currentWrite; // write value here
        currentWrite += longBuffer.writeComplete(currentWrite);
        longBuffer.log(*pLogger);
        currentWrite++;
        pLogger->info("\n");
    }

    // Read some values
    pLogger->info("Reading in reverse.");
    for (int counter = 0; counter < 5; ++counter) {
        longBuffer.read(&pValue);
        pLogger->info("Read value %ld.", *pValue);
        longBuffer.log(*pLogger);
        REQUIRE( *pValue == currentRead );
        currentRead--;
        pLogger->info("\n");
    }

    // Write some values
    pLogger->info("Write some more in reverse.");
    for (int counter = 0; counter < 5; ++counter) {
        pLogger->info("Writing %ld.", currentWrite);
        longBuffer.writeRequest(&pValue, currentWrite);
        *pValue = currentWrite; // write value here
        currentWrite += longBuffer.writeComplete(currentWrite);
        longBuffer.log(*pLogger);
        currentWrite++;
        pLogger->info("\n");
    }

    // Switch back into forward writing
    currentWrite += longBuffer.toggle(currentWrite);
    currentRead += 2;

    pLogger->info("Write a value in forward mode (after backward writing)");
    for (int counter = 0; counter < 1; ++counter) {
        longBuffer.writeRequest(&pValue, currentWrite);
        *pValue = currentWrite; // write value here
        currentWrite += longBuffer.writeComplete(currentWrite);
        pLogger->info("Writing %ld.", currentWrite);
        longBuffer.log(*pLogger);
        currentWrite++;
        pLogger->info("\n");
    }

    // Read a few values in forward mode
    pLogger->info("\n");
    pLogger->info("Read a few values in forward mode.");
    for (int counter = 0; counter < 4; ++counter) {
        longBuffer.read(&pValue);
        pLogger->info("Read value %ld.", *pValue);
        longBuffer.log(*pLogger);
        REQUIRE( *pValue == currentRead );
        currentRead++;
        pLogger->info("\n");
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
