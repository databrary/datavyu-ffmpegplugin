#include "Logger.h"
#include <atomic>
#include <mutex>
#include <condition_variable>
#include <algorithm>
#include <sstream>


template<class Item>
class BufferForStream {
    Item* buffer;
    long firstItem; // first item in the stream
    int nItem; // Capacity of the buffer
    int nMaxBack; // Maximum backward jump in stream; must be smaller than nItem, e.g. nItem/2
    int nBefore;
    int nAfter; // Number of items behind that can be read
    int iRead; // Read pointer
    int iWrite; // Write pointer
    bool back; // Forward or backward mode
    std::mutex mu;
    std::condition_variable cv;
    std::atomic<bool> flushing;
    void reset() {
        iRead = 0;
        iWrite = 0;
        nBefore = 0;
        nAfter = 0;
    }
public:
    BufferForStream(long firstItem, int nItem, int nMaxBack) : firstItem(firstItem), nItem(nItem), nMaxBack(nMaxBack),
        back(false), flushing(false) {
        buffer = new Item[nItem];
        reset();
    }
    virtual ~BufferForStream() {
        delete [] buffer;
    }
    inline int nFree() { return nItem - nBefore; }
    void log(Logger& pLogger) {
        pLogger.info("iRead = %d, iWrite = %d, nBefore = %d, nAfter = %d.",
                     iRead, iWrite, nBefore, nAfter);
        std::stringstream ss;
        for (int iItem = 0; iItem < nItem; ++iItem) {
            ss << buffer[iItem] << ", ";
        }
        pLogger.info("Contents: %s", ss.str().c_str());
    }
    void read(Item& item) {
        std::unique_lock<std::mutex> locker(mu);
        cv.wait(locker, [this](){return nBefore > 0 || flushing;});
        if (!flushing) {
            item = buffer[iRead];
            iRead = (back ? (iRead - 1 + nItem) : (iRead + 1)) % nItem;
            nAfter++;
            nBefore = std::max(nBefore - 1, 0);
        }
		locker.unlock();
		cv.notify_all();
    }
    void writeRequest(Item& item, long currentItem) {
        std::unique_lock<std::mutex> locker(mu);
        cv.wait(locker, [this, currentItem](){ return nFree() > 0 || flushing; });
        if (!flushing) {
            *(buffer + iWrite) = item;
        }
		locker.unlock();
		cv.notify_all();
    }
    int writeComplete(long currentItem) {
        int nBackItem = 0;
        std::unique_lock<std::mutex> locker(mu);
        cv.wait(locker, [this, currentItem](){ return nFree() > 0 || flushing; });
        if (!flushing) {
            iWrite = (back ? (iWrite - 1 + nItem) : (iWrite + 1)) % nItem;
            nAfter = std::max(0, nAfter - 1);
            nBefore++;
        }
		locker.unlock();
		cv.notify_all();
		return -nBackItem;
    }
    int toggle(long currentItem) {
        int nToggleItem = 0;
        std::unique_lock<std::mutex> locker(mu);
        cv.wait(locker, [this](){ return nAfter > 1 || flushing; });
        if (!flushing) {
            // Switch from backward to forward
            if (back) {
                nToggleItem = nBefore + nAfter + 1;
                iWrite = (iWrite + nBefore + nAfter + 1) % nItem;
                iRead = (iRead + 2) % nItem;
            } else {
                nToggleItem = -nAfter - 1;
                iWrite = (iWrite - nAfter - 1 + nItem) % nItem;
                iRead = (iRead - 2 + nItem) % nItem;
            }
            nBefore++;
            nAfter--;
            std::swap(nAfter, nBefore);
            back = !back;
        }
		locker.unlock();
		cv.notify_all();
        return nToggleItem;
    }
    void flush() {
        flushing = true;
        cv.notify_all();
        std::unique_lock<std::mutex> locker(mu);
        reset();
        locker.unlock();
        flushing = false;
    }
};