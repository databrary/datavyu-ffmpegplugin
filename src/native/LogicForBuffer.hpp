#include "Logger.h"
#include <atomic>
#include <mutex>
#include <condition_variable>
#include <algorithm>
#include <sstream>
#include <stdlib.h>

#ifndef LOGIC_FOR_BUFFER_H_
#define LOGIC_FOR_BUFFER_H_

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
    std::atomic<bool> unblocking;
    void reset() {
        nReverseLast = 0;
        iReverse = 0;
        iRead = 0;
        iWrite = 0;
        nBefore = 0;
        nAfter = 0;
    }
    inline bool inBalance() { return abs(nBefore - nAfter) <= 2; }
protected:
    Item* buffer;
public:
    LogicForBuffer(long firstItem, int nItem, int nMaxReverse) : buffer(nullptr), firstItem(firstItem),
        nItem(nItem), nMaxReverse(nMaxReverse), backward(false), unblocking(false) {
        reset();
    }
    inline bool atStart() const { return backward && nReverseLast == 0; }
    inline bool isBackward() const { return backward; }
    inline bool inReverse() const { return iReverse > 0; }
    inline bool empty() const { return nBefore == 0; }
    inline int size() const { return nItem; }
    inline int nFree() { return nItem - nBefore; } // number of non-occupied spaces
    inline int nNonOccupied() { return nItem - nBefore - nAfter; }
    inline int nReverse(long currentItem) const {
        return (int) std::min(currentItem - firstItem, (long) nMaxReverse);
    }
    virtual void log(Logger& pLogger) {
        pLogger.info("iRead = %d, iWrite = %d, nBefore = %d, nAfter = %d, nReverseLast = %d, iReverse = %d.",
                     iRead, iWrite, nBefore, nAfter, nReverseLast, iReverse);
    }
    void read(Item* item) { // item is allocated
        std::unique_lock<std::mutex> locker(mu);
        cv.wait(locker, [this](){return nBefore > 0 || unblocking;});
        if (unblocking) {
            *item = nullptr;
        } else {
            *item = buffer[iRead];
            iRead = (backward ? (iRead - 1 + nItem) : (iRead + 1)) % nItem;
            nAfter++;
            nBefore--; // since nBefore > 0
        }
		locker.unlock();
		cv.notify_all();
    }
    void writeRequest(Item* item, long currentItem) {
        std::unique_lock<std::mutex> locker(mu);
        // Always leave one spot open because it could be currently where the read pointer is pointing too
        cv.wait(locker, [this, currentItem](){ return !backward && nFree() > nMaxReverse
                                                    || backward && nFree() > nMaxReverse && nReverseLast > 0
                                                    || unblocking; });
        *item = unblocking ? nullptr : buffer[iWrite];
		locker.unlock();
		cv.notify_all();
    }
    int writeComplete(long currentItem) {
        int nBackItem = 0;
        std::unique_lock<std::mutex> locker(mu);
        cv.wait(locker, [this, currentItem](){ return !backward && nFree() > nMaxReverse
                                                    || backward && nFree() > nMaxReverse && nReverseLast > 0
                                                    || unblocking; });
        if (!unblocking) {
            if (backward) {
                iReverse--;
                if (iReverse == 0) {
                    nAfter -= std::max(0, nReverseLast - nNonOccupied()); // Make space for nReverseLast items, which we have
                    nBefore += nReverseLast;
                    nBackItem = -nReverseLast - nReverse(currentItem - nReverseLast + 1);
                    iWrite = (iWrite + nBackItem + nItem) % nItem;
                    iReverse = nReverseLast = nReverse(currentItem - nReverseLast + 1);
                }
            } else {
                nAfter -= nNonOccupied() == 0;
                nBefore++;
            }
            iWrite = (iWrite + 1) % nItem;
        }
		locker.unlock();
		cv.notify_all();
		return nBackItem;
    }
    int toggle(long currentItem) {
        int nToggleItem = 0;
        std::unique_lock<std::mutex> locker(mu);
        cv.wait(locker, [this, currentItem](){ return nAfter > 0 || unblocking; });
        if (!unblocking) {
            // Switch from backward to forward
            if (backward) {
                nToggleItem = nBefore + nAfter + iReverse;
                iWrite = (iWrite + nToggleItem) % nItem;
                iRead = (iRead + 2) % nItem;
                iReverse = nReverseLast = 0;
            // Switch from forward to backward
            } else {
                // We know nFree() > nMaxReverse <=> nItem - nBefore > nItem/2 <=> nItem/2 > nBefore
                // An we know nReverse <= nItem/2
                iReverse = nReverseLast = nReverse(currentItem - nBefore - nAfter);
                nToggleItem = -nBefore -nAfter - nReverseLast;
                nBefore -= std::max(0, nReverseLast - nNonOccupied()); // Corrects nAfter after the swap
                iWrite = (iWrite + nToggleItem + 2*nItem) % nItem;
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
    void unblock() {
        unblocking = true;
        cv.notify_all();
        //unblocking = false;
    }
    void block() {
        unblocking = false;
    }
    void flush() {
        unblocking = true;
        cv.notify_all();
        std::unique_lock<std::mutex> locker(mu);
        reset();
        locker.unlock();
        unblocking = false;
    }
};

#endif #LOGIC_FOR_BUFFER_H_
