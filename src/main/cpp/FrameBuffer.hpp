#include "LogicForBuffer.hpp"
#include "Logger.h"

extern "C" {
	#include <libavcodec/avcodec.h>
	#include <libavformat/avformat.h>
}

#ifndef FRAME_BUFFER_H_
#define FRAME_BUFFER_H_

#define DEFAULT_BUFFER_SIZE 196

typedef struct Frame {
    AVFrame *frame;
    double pts;           /* presentation timestamp for the frame */
    double duration;      /* estimated duration of the frame */
    int width;
    int height;
} Frame;

class FrameBuffer : public LogicForBuffer<Frame*> {
public:
    FrameBuffer(int width, int height, long firstItem,
                int nItem = DEFAULT_BUFFER_SIZE, int nMaxReverse = DEFAULT_BUFFER_SIZE/2)
        : LogicForBuffer<Frame*>(nItem) {
		buffer = new Frame*[nItem];
		for (int iItem = 0; iItem < nItem; ++iItem) {
		    Frame* pFrame = new Frame();
		    pFrame->frame = av_frame_alloc();
			buffer[iItem] = pFrame;
		}
    }
    virtual ~FrameBuffer() {
		for (int iItem = 0; iItem < size(); ++iItem) {
		    Frame* pFrame = buffer[iItem];
			av_frame_free(&pFrame->frame);
			delete pFrame;
		}
		delete [] buffer;
    }
/*    void writeRequestWithAllocate(Frame* pFrame, const Frame& src) {
        writeRequest(pFrame);
        if (pFrame) {
            av_image_alloc(pFrame->data, dst_linesize, dst_w, dst_h, dst_pix_fmt, 16)


        }
            pFrame->frame

*//*
            AVFrame* pFrame = av_frame_alloc();
			pFrame->opaque = new AVFrameMetaData();
			buffer[iItem] = pFrame;
			// Create the data buffer and associate it with the buffer
			int nByte = avpicture_get_size(AV_PIX_FMT_RGB24, width, height);
			uint8_t* bufferShow = (uint8_t*) av_malloc(nByte*sizeof(uint8_t));
			avpicture_fill((AVPicture*)pFrame, bufferShow, AV_PIX_FMT_RGB24, width, height);
*//*

        }
    }*/
    virtual void log(Logger& pLogger, int64_t avgDeltaPts) {
        LogicForBuffer::log(pLogger);
    }
};

#endif FRAME_BUFFER_H_
