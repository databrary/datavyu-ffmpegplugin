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
    FrameBuffer(int width, int height, long firstItem, int nItem = DEFAULT_BUFFER_SIZE)
        : LogicForBuffer<Frame*>(nItem) {
		buffer = new Frame*[nItem];
		for (int iItem = 0; iItem < nItem; ++iItem) {
		    // Get the frame pointer
		    Frame* pFrame = new Frame();
		    // Allocate the frame
		    pFrame->frame = av_frame_alloc();
		    pFrame->frame->width = width;
		    pFrame->frame->height = height;
		    pFrame->frame->format = AV_PIX_FMT_RGB24;
		    av_frame_get_buffer(pFrame->frame, 0);
		    buffer[iItem] = pFrame;
		}
    }
    virtual ~FrameBuffer() {
		for (int iItem = 0; iItem < size(); ++iItem) {
		    Frame* pFrame = buffer[iItem];
		    //av_free(pFrame->frame->data[0]); // Free data buffer
			av_frame_free(&pFrame->frame);
			delete pFrame;
		}
		delete [] buffer;
    }
/*    void writeRequestWithAllocate(Frame* pFrame, const AVFrame& src) {
        writeRequest(&pFrame);
        // allocate or resize the buffer
        if (!pFrame->frame->data) {
            pFrame->frame->width = src.width;
            pFrame->frame->height = src.height;
            pFrame->frame->format = AV_PIX_FMT_RGB24;

        } else if (pFrame->width != src.width || pFrame->height != src.height) {
		    av_free(pFrame->frame->data[0]); // Free data buffer

        }
    }*/
    virtual void log(Logger& pLogger, int64_t avgDeltaPts) {
        LogicForBuffer::log(pLogger);
    }
};

#endif FRAME_BUFFER_H_
