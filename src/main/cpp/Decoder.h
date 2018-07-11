#include "PacketQueue.h"
#include "FrameQueue.h"
#include <condition_variable>
#include <thread>

extern "C" {
	#include <libavcodec/avcodec.h> // codecs
    #include <libavutil/error.h> // error codes
    #include <libavutil/rational.h>
}

#ifndef DECODER_H_
#define DECODER_H_

// Note, I stripped out the sub title and replaced the SDL mutex by the std mutex
// and the SDL_Thread by the std::thread

class Decoder {
    private:
		AVPacket pkt;
		AVCodecContext *avctx; //  TODO: Change this to ref
		PacketQueue *queue; // TODO: Change this to ref
		std::condition_variable *empty_queue_cond; // TODO: Change this to ref
		int pkt_serial;
		int finished;
		int packet_pending;
		int decoder_reorder_pts;
		int64_t start_pts;
		AVRational start_pts_tb;
		int64_t next_pts;
		AVRational next_pts_tb;
		int step; // The step is 1/MASTER_CLOCK_SPEED
		std::thread *decoder_tid;
    public:
		Decoder(AVCodecContext *avctx, PacketQueue *queue,
			std::condition_variable *empty_queue_cond);

		virtual ~Decoder() {			
			av_packet_unref(&pkt);
			// TODO(fraudies): Clean-up design, move this de-allocation to the VideoState (where it is initialized)
			avcodec_free_context(&avctx);
		}

		int decode_frame(AVFrame *frame, AVSubtitle *sub);

		void set_start_pts(int64_t start_pts);

		void set_start_pts_tb(AVRational start_pts_tb);

		void set_pts_step(int step);

		inline int get_pkt_serial() const { return pkt_serial; }

		inline const AVCodecContext* get_avctx() const { return avctx; }

		inline int is_finished() const { return finished; }

		inline void setFinished(int f) { finished = f; }

		// TODO(fraudies): This is tied to the audio/image/subtitle decode thread; 
		// all three use the decode thread method from above with the respective object
		// Re-design with lambda and tighter typing -- rather than passing the void pointers around
		int start(int(*fn)(void *), void *arg);

		void abort(FrameQueue *fq);
};

#endif DECODER_H_