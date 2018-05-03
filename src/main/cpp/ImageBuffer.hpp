#include "LogicForBuffer.hpp"
#include "Logger.h"

extern "C" {
	#include <libavcodec/avcodec.h>
	#include <libavformat/avformat.h>
}

#ifndef IMAGE_BUFFER_H_
#define IMAGE_BUFFER_H_

#define DEFAULT_BUFFER_SIZE 196

class ImageBuffer : public LogicForBuffer<AVFrame*> {
public:
    ImageBuffer(int width, int height, long firstItem,
                int nItem = DEFAULT_BUFFER_SIZE, int nMaxReverse = DEFAULT_BUFFER_SIZE/2)
        : LogicForBuffer<AVFrame*>(nItem) {
		buffer = new AVFrame*[nItem];
		for (int iItem = 0; iItem < nItem; ++iItem) {
			AVFrame* pFrame = av_frame_alloc();
			buffer[iItem] = pFrame;
			// TODO: Rework initialization
			// Create the data buffer and associate it with the buffer
			int nByte = avpicture_get_size(AV_PIX_FMT_RGB24, width, height);
			uint8_t* bufferShow = (uint8_t*) av_malloc(nByte*sizeof(uint8_t));
			avpicture_fill((AVPicture*)pFrame, bufferShow, AV_PIX_FMT_RGB24, width, height);
		}
    }
    virtual ~ImageBuffer() {
		for (int iItem = 0; iItem < size(); ++iItem) {
			AVFrame* pFrame = buffer[iItem];
			// TODO: Rework free
			av_free(pFrame->data[0]); // Frees the data buffer
			av_free(pFrame);
		}
		delete [] buffer;
    }
    virtual void log(Logger& pLogger, int64_t avgDeltaPts) {
        LogicForBuffer::log(pLogger);
    }
};

#endif IMAGE_BUFFER_H_
