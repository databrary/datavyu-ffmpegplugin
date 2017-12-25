#include "LogicForBuffer.hpp"
#include "Logger.h"

extern "C" {
	#include <libavcodec/avcodec.h>
	#include <libavformat/avformat.h>
}

#define DEFAULT_BUFFER_SIZE 32

class ImageBuffer : public LogicForBuffer<AVFrame*> {
public:
    ImageBuffer(int width, int height, long firstItem,
                int nItem = DEFAULT_BUFFER_SIZE, int nMaxReverse = DEFAULT_BUFFER_SIZE/2)
        : LogicForBuffer<AVFrame*>(firstItem, nItem, nMaxReverse) {
		buffer = new AVFrame*[nItem];
		for (int iItem = 0; iItem < nItem; ++iItem) {
			AVFrame* pFrame = av_frame_alloc();
			buffer[iItem] = pFrame;
			// Create the data buffer and associate it with the buffer.
			int nByte = avpicture_get_size(AV_PIX_FMT_RGB24, width, height);
			uint8_t* bufferShow = (uint8_t*) av_malloc(nByte*sizeof(uint8_t));
			avpicture_fill((AVPicture*)pFrame, bufferShow, AV_PIX_FMT_RGB24, width, height);
		}
    }
    virtual ~ImageBuffer() {
		for (int iItem = 0; iItem < size(); ++iItem) {
			AVFrame* pFrame = buffer[iItem];
			av_free(pFrame->data[0]); // Frees the data buffer.
			av_free(pFrame);
		}
		delete [] buffer;
    }
    virtual void log(Logger& pLogger, int64_t avgDeltaPts) {
        LogicForBuffer::log(pLogger);
        std::stringstream ss;
        for (int iItem = 0; iItem < size(); ++iItem) {
            ss << "(" << iItem << ";" << buffer[iItem]->pts/avgDeltaPts << "), ";
        }
        pLogger.info("Contents: %s", ss.str().c_str());
    }
};
