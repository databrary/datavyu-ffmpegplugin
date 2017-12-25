#include "Logger.h"
#include <atomic>
#include <mutex>
#include <condition_variable>
#include <algorithm>
#include <sstream>


template<class Item>
class LogicForBuffer {
    long firstItem; // first item in the stream
    int nItem; // Capacity of the buffer
    int nMaxReverse; // Maximum backward jump in stream; must be smaller than nItem, e.g. nItem/2
    int nReverseLast; // Number of items jumped back last
    int nBefore; // Number of items before read
    int nAfter; // Number of items behind read
    int iRead; // Read pointer
    int iWrite; // Write pointer
    int iReverse; // Counter for steps in reverse
    bool backward; // Forward or backward mode
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
protected:
    Item* buffer;
public:
    LogicForBuffer(long firstItem, int nItem, int nMaxReverse) : buffer(nullptr), firstItem(firstItem),
        nItem(nItem), nMaxReverse(nMaxReverse), backward(false), flushing(false) {
        reset();
    }
    inline bool isBackward() const { return backward; }
    inline bool inReverse() const { return iReverse > 0; }
    inline bool empty() const { return nBefore == 0; }
    inline int size() const { return nItem; }
    inline int nFree() { return nItem - nBefore; }
    inline int nNonOccupied() { return nItem - nBefore - nAfter; } // Number of non-occupied spaces
    inline int nReverse(long currentItem) const {
        return (int) std::min(currentItem - firstItem - nReverseLast + 1, (long) nMaxReverse); // +1 for 0
    }
    virtual void log(Logger& pLogger) {
        pLogger.info("iRead = %d, iWrite = %d, nBefore = %d, nAfter = %d, nReverseLast = %d, iReverse = %d.",
                     iRead, iWrite, nBefore, nAfter, nReverseLast, iReverse);
    }
    void read(Item* item) { // item is allocated
        std::unique_lock<std::mutex> locker(mu);
        cv.wait(locker, [this](){return nBefore > 0 || flushing;});
        if (!flushing) {
            *item = buffer[iRead];
            iRead = (backward ? (iRead - 1 + nItem) : (iRead + 1)) % nItem;
            nAfter++;
            nBefore = std::max(nBefore - 1, 0);
        }
		locker.unlock();
		cv.notify_all();
    }
    void writeRequest(Item* item, long currentItem) {
        std::unique_lock<std::mutex> locker(mu);
        // Always leave one spot open because it could be currently where the read pointer is pointing too
        cv.wait(locker, [this, currentItem](){ return !backward && nFree() > 1
                                                    || backward && iReverse==1 && nFree() >= 1+nReverse(currentItem)
                                                    || backward && iReverse>1
                                                    || flushing; });
        if (!flushing) {
            *item = buffer[iWrite];
        }
		locker.unlock();
		cv.notify_all();
    }
    int writeComplete(long currentItem) {
        int nBackItem = 0;
        std::unique_lock<std::mutex> locker(mu);
        cv.wait(locker, [this, currentItem](){ return !backward && nFree() > 1
                                                    || backward && iReverse==1 && nFree() >= 1+nReverse(currentItem)
                                                    || backward && iReverse>1
                                                    || flushing; });
        if (!flushing) {
            iWrite = (iWrite + 1) % nItem;
            if (backward) {
                iReverse--;
                if (iReverse == 0) {
                    int nDiff = nReverseLast - nNonOccupied(); // Whatever more we reversed than were non-occupied
                    if (nDiff > 0) {
                        nAfter = std::max(0, nAfter - nDiff);
                    }
                    nBefore += nReverseLast;
                    nBackItem = -nReverseLast - nReverse(currentItem);
                    iWrite = (iWrite + nBackItem + nItem) % nItem;
                    iReverse = nReverseLast = nReverse(currentItem);
                }
            } else {
                nAfter = std::max(0, nAfter - int(nBefore + nAfter == nItem));
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
        cv.wait(locker, [this](){ return nAfter > 0 || flushing; });
        if (!flushing) {
            // Switch from backward to forward
            if (backward) {
                nToggleItem = nBefore + nAfter + iReverse;
                iWrite = (iWrite + nToggleItem) % nItem;
                iRead = (iRead + 2) % nItem;
                iReverse = nReverseLast = 0;
            // Switch from forward to backward
            } else {
                iReverse = nReverseLast = std::min(nAfter+nFree(), nReverse(currentItem));
                nToggleItem = -nReverseLast -nBefore -nAfter;
                iWrite = (iWrite - nReverseLast + nItem) % nItem;
                iRead = (iRead - 2 + nItem) % nItem;
            }
            nBefore++;
            nAfter--;
            std::swap(nAfter, nBefore);
            backward = !backward;
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