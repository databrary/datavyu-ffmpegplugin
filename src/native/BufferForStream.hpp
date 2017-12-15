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
    int nMaxReverse; // Maximum backward jump in stream; must be smaller than nItem, e.g. nItem/2
    int nReverseLast; // Number of items jumped back last
    int nBefore; // Number of items before read
    int nAfter; // Number of items behind read
    int iRead; // Read pointer
    int iWrite; // Write pointer
    int iReverse; // Counter for steps in reverse
    bool back; // Forward or backward mode
    std::mutex mu;
    std::condition_variable cv;
    std::atomic<bool> flushing;
    void reset() {
        nReverseLast = 0;
        iReverse = 0;
        iRead = 0;
        iWrite = 0;
        nBefore = 0;
        nAfter = 0;
    }
public:
    BufferForStream(long firstItem, int nItem, int nMaxReverse) : firstItem(firstItem), nItem(nItem),
        nMaxReverse(nMaxReverse), back(false), flushing(false) {
        buffer = new Item[nItem];
        reset();
    }
    virtual ~BufferForStream() {
        delete [] buffer;
    }
    inline int nFree() { return nItem - nBefore; }
    inline int nReverse(long currentItem) const {
        return (int) std::min(currentItem - firstItem, (long) nMaxReverse);
    }
    void log(Logger& pLogger) {
        pLogger.info("iRead = %d, iWrite = %d, nBefore = %d, nAfter = %d, nReverseLast = %d, iReverse = %d.",
                     iRead, iWrite, nBefore, nAfter, nReverseLast, iReverse);
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
    // TODO: Simplify writeRequest and writeComplete into one method for now
    void writeRequest(Item& item, long currentItem) {
        std::unique_lock<std::mutex> locker(mu);
        cv.wait(locker, [this, currentItem](){ return !back && nFree() > 0 || back && nFree() >= nReverse(currentItem)
                                                      || flushing; });
        if (!flushing) {
            *(buffer + iWrite) = item;
        }
		locker.unlock();
		cv.notify_all();
    }
    int writeComplete(long currentItem) {
        int nBackItem = 0;
        std::unique_lock<std::mutex> locker(mu);
        cv.wait(locker, [this, currentItem](){ return !back && nFree() > 0 || back && nFree() >= nReverse(currentItem)
                                               || flushing; });
        if (!flushing) {
            iWrite = (iWrite + 1) % nItem;
            if (back) {
                iReverse--;
                if (iReverse == 0) {
                    int nNonOccupied = nItem - nBefore - nAfter; // Number of non-occupied spaces
                    int nDiff = nReverseLast - nNonOccupied; // Whatever more we reversed than were non-occupied
                    if (nDiff > 0) {
                        nAfter = std::max(0, nAfter - nDiff);
                    }
                    nBefore += nReverseLast;
                    nBackItem = -nReverseLast - nReverse(currentItem);
                    iWrite = (iWrite - nBackItem + nItem) % nItem;
                    iReverse = nReverseLast = nReverse(currentItem);
                }
            } else {
                nAfter = std::max(0, nAfter - 1);
                nBefore++;
            }
        }
		locker.unlock();
		cv.notify_all();
		return nBackItem;
    }
    int toggle(long currentItem) {
        int nToggleItem = 0;
        std::unique_lock<std::mutex> locker(mu);
        cv.wait(locker, [this](){ return nAfter > 1 || flushing; });
        if (!flushing) {
            // Switch from backward to forward
            if (back) {
                //nToggleItem = nBefore + nAfter + 1;
                //iWrite = (iWrite + nBefore + nAfter + 1) % nItem;
                nToggleItem = nBefore + nAfter + nReverseLast;
                iWrite = (iWrite + nBefore + nAfter + nReverseLast) % nItem;
                iRead = (iRead + 2) % nItem;
                iReverse = nReverseLast = 0;
            // Switch from forward to backward
            } else {
                iReverse = nReverseLast = std::min(nFree(), nReverse(currentItem));
                nToggleItem = -nAfter - nReverseLast;
                iWrite = (iWrite - nAfter - nReverseLast + nItem) % nItem;
                iRead = (iRead - 2 + nItem) % nItem;
                //nToggleItem = -nAfter - 1;
                //iWrite = (iWrite - nAfter - 1 + nItem) % nItem;
                //iRead = (iRead - 2 + nItem) % nItem;
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