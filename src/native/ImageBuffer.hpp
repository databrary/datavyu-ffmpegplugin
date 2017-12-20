#include "LogicForBuffer.hpp"

template<>
class ImageBuffer : public LogicForBuffer<AVFrame*> {
public:
    BufferForStream(int width, int height, long firstItem, int nItem, int nMaxReverse)
        : BufferForStream<AVFrame*>(firstItem, nItem, nMaxReverse) {
		buffer = new AVFrame*[nData];
		for (int iItem = 0; iItem < nItem; ++iItem) {
			AVFrame* pFrame = av_frame_alloc();
			buffer[iData] = pFrame;
			// Create the data buffer and associate it with the buffer.
			int nByte = avpicture_get_size(AV_PIX_FMT_RGB24, width, height);
			uint8_t* bufferShow = (uint8_t*) av_malloc(nByte*sizeof(uint8_t));
			avpicture_fill((AVPicture*)pFrame, bufferShow, AV_PIX_FMT_RGB24, width, height);
		}
    }
    virtual ~BufferForStream() {
		for (int iItem = 0; iItem < nItem; ++iItem) {
			AVFrame* pFrame = buffer[iItem];
			av_free(pFrame->data[0]); // Frees the data buffer.
			av_free(pFrame);
		}
		delete [] buffer;
    }
};