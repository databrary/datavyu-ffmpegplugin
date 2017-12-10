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
    int nItem;
    int nMaxBack; // must be smaller than nItem, e.g. nItem/2
    int nBackLast;
    int nBehind; // Number of items behind that can be read
    int iRead;
    int iWrite;
    int iWriteEnd;
    bool back;
    std::mutex mu;
    std::condition_variable cv;
    std::atomic<bool> flushing;
    void reset() {
        nBackLast = 0;
        iRead = 0;
        iWrite = 0;
        iWriteEnd = 0;
        nBehind = 0;
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
    inline int nRead() const {
        return (back ? (iRead - iWriteEnd + nItem) : (iWrite - iRead + nItem)) % nItem;
    }
    inline int nWrite() const {
        return nItem - nRead();
    }
    inline int iWriteStart() const {
        return (iWriteEnd - nBackLast + nItem) % nItem;
    }
    inline int nDelta() const {
        return (iWriteStart() - iRead + nItem) % nItem;
    }
    inline int nBack(long currentItem) const {
        return (int) std::min(currentItem - firstItem, (long) nMaxBack);
    }
    void log(Logger& pLogger) {
        pLogger.info("iRead = %d, iWrite = %d, iWriteStart = %d, iWriteEnd = %d, nRead = %d, nWrite = %d, nBehind = %d, nBack = %d.",
                     iRead, iWrite, iWriteStart(), iWriteEnd, nRead(), nWrite(), nBehind, nBack(nItem));
        std::stringstream ss;
        for (int iItem = 0; iItem < nItem; ++iItem) {
            ss << buffer[iItem] << ", ";
        }
        pLogger.info("Contents: %s", ss.str().c_str());
    }
    void read(Item& item) {
        std::unique_lock<std::mutex> locker(mu);
        cv.wait(locker, [this](){return nRead() > 0 || flushing;});
        if (!flushing) {
            item = buffer[iRead];
            iRead = (back ? (iRead - 1 + nItem) : (iRead + 1)) % nItem;
            nBehind--;
        }
		locker.unlock();
		cv.notify_all();
    }
    void writeRequest(Item& item, long currentItem) {
        std::unique_lock<std::mutex> locker(mu);
        cv.wait(locker, [this, currentItem](){ return back && (nWrite() > 0 || nDelta() >= nBack(currentItem))
                                                  || !back && nWrite() > 0 || flushing; });
        if (!flushing) {
            *(buffer + iWrite) = item;
        }
		locker.unlock();
		cv.notify_all();
    }
    int writeComplete(long currentItem) {
        int nBackItem = 0;
        std::unique_lock<std::mutex> locker(mu);
        cv.wait(locker, [this, currentItem](){ return back && (nWrite() > 0 || nDelta() >= nBack(currentItem))
                                             || !back && nWrite() > 0 || flushing; });
        if (!flushing) {
            // We need to jump backward
            if (back && nWrite() == 0) {
                int delta = std::min(nDelta(), nBack(currentItem));
                iWrite = (iWrite - delta + nItem) % nItem;
                iWriteEnd = (iWriteEnd - nBackLast + nItem) % nItem;
                nBackItem = nBackLast + delta;
                nBackLast = delta;
            }
            iWrite = (iWrite + 1) % nItem;
            nBehind++;
        }
		locker.unlock();
		cv.notify_all();
		return -nBackItem;
    }
    int toggle(long currentItem) {
        int nToggleItem = 0;
        std::unique_lock<std::mutex> locker(mu);
        cv.wait(locker, [this](){ return nBehind > 1 || flushing; });
        if (!flushing) {
            // Switch from backward to forward
            if (back) {
                iWrite = iWriteStart();
                nBackLast = 0;
                nToggleItem = (iWrite - iRead + nItem) % nItem; // Number of frames in between
                iRead = (iRead - 2 + nItem) % nItem;
            } else {
                nBehind = nToggleItem = (iRead - iWrite + nItem) % nItem;
                iWriteEnd = iWrite;
                nBackLast = std::min(nDelta(), nBack(currentItem));
                nToggleItem = -(nToggleItem + nBackLast);
                iRead = (iRead + 2) % nItem;
            }
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