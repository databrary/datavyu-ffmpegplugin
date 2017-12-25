#include "LogicForBuffer.hpp"

class LongBuffer : public LogicForBuffer<long*> {
    long* data;
public:
    LongBuffer(long firstItem, int nItem, int nMaxReverse) : LogicForBuffer<long*>(firstItem, nItem, nMaxReverse) {
        buffer = new long*[nItem]; // Pointers to data
        data = new long[nItem]; // The actual data
        for (int iItem = 0; iItem < nItem; ++iItem) {
            buffer[iItem] = &data[iItem]; // Have the pointers point to the data
        }
    }
    virtual ~LongBuffer() {
        delete [] buffer;
        delete [] data;
    }
    virtual void log(Logger& pLogger) {
        LogicForBuffer::log(pLogger);
        std::stringstream ss;
        for (int iItem = 0; iItem < size(); ++iItem) {
            ss << data[iItem] << ", ";
        }
        pLogger.info("Contents: %s", ss.str().c_str());
    }
};