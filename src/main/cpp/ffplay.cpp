#include <inttypes.h>
#include <math.h>
#include <limits.h>
#include <signal.h>
#include <stdint.h>

#include "Clock.hpp"
#include "PacketQueue.hpp"
#include "FrameQueue.hpp"

extern "C" {
	#include "libavutil/avstring.h"
	#include "libavutil/eval.h"
	#include "libavutil/mathematics.h"
	#include "libavutil/pixdesc.h"
	#include "libavutil/imgutils.h"
	#include "libavutil/dict.h"
	#include "libavutil/parseutils.h"
	#include "libavutil/samplefmt.h"
	#include "libavutil/avassert.h"
	#include "libavutil/time.h"
	#include "libavutil/log.h"
	#include "libavformat/avformat.h"
	#include "libavdevice/avdevice.h"
	#include "libswscale/swscale.h"
	#include "libavutil/opt.h"
	#include "libavcodec/avfft.h"
	#include "libswresample/swresample.h"

	#if CONFIG_AVFILTER
	# include "libavfilter/avfilter.h"
	# include "libavfilter/buffersink.h"
	# include "libavfilter/buffersrc.h"
	#endif

	#include <SDL2/SDL.h>
	#include <SDL2/SDL_thread.h>

	#include <assert.h>
}

const char program_name[] = "ffplay";
const int program_birth_year = 2003;

#define MAX_QUEUE_SIZE (15 * 1024 * 1024)
#define MIN_FRAMES 25
#define EXTERNAL_CLOCK_MIN_FRAMES 2
#define EXTERNAL_CLOCK_MAX_FRAMES 10

/* Minimum SDL audio buffer size, in samples. */
#define SDL_AUDIO_MIN_BUFFER_SIZE 512
/* Calculate actual buffer size keeping in mind not cause too frequent audio callbacks */
#define SDL_AUDIO_MAX_CALLBACKS_PER_SEC 30

/* Step size for volume control in dB */
#define SDL_VOLUME_STEP (0.75)

/* no AV sync correction is done if below the minimum AV sync threshold */
#define AV_SYNC_THRESHOLD_MIN 0.04
/* AV sync correction is done if above the maximum AV sync threshold */
#define AV_SYNC_THRESHOLD_MAX 0.1
/* If a frame duration is longer than this, it will not be duplicated to compensate AV sync */
#define AV_SYNC_FRAMEDUP_THRESHOLD 0.1
/* no AV correction is done if too big error */
#define AV_NOSYNC_THRESHOLD 10.0

/* maximum audio speed change to get correct sync */
#define SAMPLE_CORRECTION_PERCENT_MAX 10

/* external clock speed adjustment constants for realtime sources based on buffer fullness */
#define EXTERNAL_CLOCK_SPEED_MIN  0.900
#define EXTERNAL_CLOCK_SPEED_MAX  1.010
#define EXTERNAL_CLOCK_SPEED_STEP 0.001

/* we use about AUDIO_DIFF_AVG_NB A-V differences to make the average */
#define AUDIO_DIFF_AVG_NB   20

/* polls for possible required screen refresh at least this often, should be less than 1/fps */
#define REFRESH_RATE 0.01

/* NOTE: the size must be big enough to compensate the hardware audio buffersize size */
/* TODO: We assume that a decoded and resampled frame fits into this buffer */
#define SAMPLE_ARRAY_SIZE (8 * 65536)

#define CURSOR_HIDE_DELAY 1000000

#define USE_ONEPASS_SUBTITLE_RENDER 1

static unsigned sws_flags = SWS_BICUBIC;

#define VIDEO_PICTURE_QUEUE_SIZE 3
#define SUBPICTURE_QUEUE_SIZE 16
#define SAMPLE_QUEUE_SIZE 9
#define FRAME_QUEUE_SIZE FFMAX(SAMPLE_QUEUE_SIZE, FFMAX(VIDEO_PICTURE_QUEUE_SIZE, SUBPICTURE_QUEUE_SIZE))

#define MY_AV_TIME_BASE_Q av_make_q(1, AV_TIME_BASE)

typedef struct AudioParams {
	int freq;
	int channels;
	int64_t channel_layout;
	enum AVSampleFormat fmt;
	int frame_size;
	int bytes_per_sec;
} AudioParams;

enum {
	AV_SYNC_AUDIO_MASTER, /* default choice */
	AV_SYNC_VIDEO_MASTER,
	AV_SYNC_EXTERNAL_CLOCK, /* synchronize to an external clock */
};

typedef struct Decoder {
	AVPacket pkt;
	PacketQueue *queue;
	AVCodecContext *avctx;
	int pkt_serial;
	int finished;
	int packet_pending;
	SDL_cond *empty_queue_cond;
	int64_t start_pts;
	AVRational start_pts_tb;
	int64_t next_pts;
	AVRational next_pts_tb;
	SDL_Thread *decoder_tid;
} Decoder;

enum ShowMode {
	SHOW_MODE_NONE = -1,
	SHOW_MODE_VIDEO = 0,
	SHOW_MODE_WAVES,
	SHOW_MODE_RDFT,
	SHOW_MODE_NB
};

/* options specified by the user */
static AVInputFormat *file_iformat;
static const char *input_filename;
static const char *window_title;
static int default_width = 640;
static int default_height = 480;
static int screen_width = 0;
static int screen_height = 0;
static int audio_disable;
static int video_disable;
static int subtitle_disable;
static const char* wanted_stream_spec[AVMEDIA_TYPE_NB] = { 0 };
static int seek_by_bytes = -1;
static int display_disable;
static int borderless;
static int startup_volume = 100;
static int show_status = 1;
static int av_sync_type = AV_SYNC_AUDIO_MASTER;
static int64_t start_time = AV_NOPTS_VALUE;
static int64_t duration = AV_NOPTS_VALUE;
static int fast = 0;
static int genpts = 0;
static int lowres = 0;
static int decoder_reorder_pts = -1;
static int autoexit;
static int exit_on_keydown;
static int exit_on_mousedown;
static int loop = 1;
static int framedrop = -1;
static int infinite_buffer = -1;
static ShowMode show_mode = SHOW_MODE_NONE;
static const char *audio_codec_name;
static const char *subtitle_codec_name;
static const char *video_codec_name;
double rdftspeed = 0.02;
static int64_t cursor_last_shown;
static int cursor_hidden = 0;
#if CONFIG_AVFILTER
static const char **vfilters_list = NULL;
static int nb_vfilters = 0;
static char *afilters = NULL;
#endif
static int autorotate = 1;
static int find_stream_info = 1;

/* current context */
static int is_full_screen;
static int64_t audio_callback_time;

static AVPacket flush_pkt;

#define FF_QUIT_EVENT    (SDL_USEREVENT + 2)

static SDL_Window *window;
static SDL_Renderer *renderer;
static SDL_RendererInfo renderer_info = { 0 };
static SDL_AudioDeviceID audio_dev;

static const struct TextureFormatEntry {
	enum AVPixelFormat format;
	int texture_fmt;
} sdl_texture_format_map[] = {
	{ AV_PIX_FMT_RGB8,           SDL_PIXELFORMAT_RGB332 },
{ AV_PIX_FMT_RGB444,         SDL_PIXELFORMAT_RGB444 },
{ AV_PIX_FMT_RGB555,         SDL_PIXELFORMAT_RGB555 },
{ AV_PIX_FMT_BGR555,         SDL_PIXELFORMAT_BGR555 },
{ AV_PIX_FMT_RGB565,         SDL_PIXELFORMAT_RGB565 },
{ AV_PIX_FMT_BGR565,         SDL_PIXELFORMAT_BGR565 },
{ AV_PIX_FMT_RGB24,          SDL_PIXELFORMAT_RGB24 },
{ AV_PIX_FMT_BGR24,          SDL_PIXELFORMAT_BGR24 },
{ AV_PIX_FMT_0RGB32,         SDL_PIXELFORMAT_RGB888 },
{ AV_PIX_FMT_0BGR32,         SDL_PIXELFORMAT_BGR888 },
{ AV_PIX_FMT_NE(RGB0, 0BGR), SDL_PIXELFORMAT_RGBX8888 },
{ AV_PIX_FMT_NE(BGR0, 0RGB), SDL_PIXELFORMAT_BGRX8888 },
{ AV_PIX_FMT_RGB32,          SDL_PIXELFORMAT_ARGB8888 },
{ AV_PIX_FMT_RGB32_1,        SDL_PIXELFORMAT_RGBA8888 },
{ AV_PIX_FMT_BGR32,          SDL_PIXELFORMAT_ABGR8888 },
{ AV_PIX_FMT_BGR32_1,        SDL_PIXELFORMAT_BGRA8888 },
{ AV_PIX_FMT_YUV420P,        SDL_PIXELFORMAT_IYUV },
{ AV_PIX_FMT_YUYV422,        SDL_PIXELFORMAT_YUY2 },
{ AV_PIX_FMT_UYVY422,        SDL_PIXELFORMAT_UYVY },
{ AV_PIX_FMT_NONE,           SDL_PIXELFORMAT_UNKNOWN },
};



//what will be the streamer in the future implementations
class VideoState {
private:
	SDL_Thread * read_tid;
	AVInputFormat *iformat;
	int abort_request;
	int force_refresh;
	int paused;
	int last_paused;
	int queue_attachments_req;
	int seek_req;
	int seek_flags;
	int64_t seek_pos;
	int64_t seek_rel;
	int read_pause_return;
	AVFormatContext *ic;
	int realtime;

	Clock *pAudclk;
	Clock *pVidclk;
	Clock *pExtclk;

	FrameQueue *pPictq;
	FrameQueue *pSubpq;
	FrameQueue *pSampq;

	Decoder auddec;
	Decoder viddec;
	Decoder subdec;

	int audio_stream;

	int av_sync_type;

	double audio_clock;
	int audio_clock_serial;
	double audio_diff_cum; /* used for AV difference average computation */
	double audio_diff_avg_coef;
	double audio_diff_threshold;
	int audio_diff_avg_count;
	AVStream *audio_st;
	PacketQueue *pAudioq;
	int audio_hw_buf_size;
	uint8_t *audio_buf;
	uint8_t *audio_buf1;
	unsigned int audio_buf_size; /* in bytes */
	unsigned int audio_buf1_size;
	int audio_buf_index; /* in bytes */
	int audio_write_buf_size;
	int audio_volume;
	int muted;
	struct AudioParams audio_src;
#if CONFIG_AVFILTER
	struct AudioParams audio_filter_src;
#endif
	struct AudioParams audio_tgt;
	struct SwrContext *swr_ctx;
	int frame_drops_early;
	int frame_drops_late;

	int16_t sample_array[SAMPLE_ARRAY_SIZE];
	int sample_array_index;
	int last_i_start;
	RDFTContext *rdft;
	int rdft_bits;
	FFTSample *rdft_data;
	int xpos;
	double last_vis_time;
	SDL_Texture *vis_texture;
	SDL_Texture *sub_texture;
	SDL_Texture *vid_texture;

	int subtitle_stream;
	AVStream *subtitle_st;
	PacketQueue *pSubtitleq;

	double frame_timer;
	double frame_last_returned_time;
	double frame_last_filter_delay;
	int video_stream;
	AVStream *video_st;
	PacketQueue *pVideoq;
	double max_frame_duration;      // maximum duration of a frame - above this, we consider the jump a timestamp discontinuity
	struct SwsContext *img_convert_ctx;
	struct SwsContext *sub_convert_ctx;
	int eof;

	char *filename;
	int width, height, xleft, ytop;
	int step;

#if CONFIG_AVFILTER
	int vfilter_idx;
	AVFilterContext *in_video_filter;   // the first filter in the video chain
	AVFilterContext *out_video_filter;  // the last filter in the video chain
	AVFilterContext *in_audio_filter;   // the first filter in the audio chain
	AVFilterContext *out_audio_filter;  // the last filter in the audio chain
	AVFilterGraph *agraph;              // audio filter graph
#endif

	int last_video_stream, last_audio_stream, last_subtitle_stream;

	SDL_cond *continue_read_thread;

	// Decoder functions not processed yet.

	int decoder_start(Decoder *d, int(*fn)(void *))
	{
		d->queue->start();
		d->decoder_tid = SDL_CreateThread(fn, "decoder", this);
		if (!d->decoder_tid) {
			av_log(NULL, AV_LOG_ERROR, "SDL_CreateThread(): %s\n", SDL_GetError());
			return AVERROR(ENOMEM);
		}
		return 0;
	}


	static void decoder_init(Decoder *d, AVCodecContext *avctx, PacketQueue *queue, SDL_cond *empty_queue_cond) {
		memset(d, 0, sizeof(Decoder));
		d->avctx = avctx;
		d->queue = queue;
		d->empty_queue_cond = empty_queue_cond;
		d->start_pts = AV_NOPTS_VALUE;
		d->pkt_serial = -1;
	}

	static void decoder_abort(Decoder *d, FrameQueue *fq)
	{
		d->queue->abort();
		fq->signal();
		SDL_WaitThread(d->decoder_tid, NULL);
		d->decoder_tid = NULL;
		d->queue->flush();
	}

	static int decoder_decode_frame(Decoder *d, AVFrame *frame, AVSubtitle *sub) {
		int ret = AVERROR(EAGAIN);

		for (;;) {
			AVPacket pkt;

			if (d->queue->get_serial() == d->pkt_serial) {
				do {
					if (d->queue->is_abort_request())
						return -1;

					switch (d->avctx->codec_type) {
					case AVMEDIA_TYPE_VIDEO:
						ret = avcodec_receive_frame(d->avctx, frame);
						if (ret >= 0) {
							if (decoder_reorder_pts == -1) {
								frame->pts = frame->best_effort_timestamp;
							}
							else if (!decoder_reorder_pts) {
								frame->pts = frame->pkt_dts;
							}
						}
						break;
					case AVMEDIA_TYPE_AUDIO:
						ret = avcodec_receive_frame(d->avctx, frame);
						if (ret >= 0) {
							AVRational tb = av_make_q(1, frame->sample_rate);
							if (frame->pts != AV_NOPTS_VALUE)
								frame->pts = av_rescale_q(frame->pts, d->avctx->pkt_timebase, tb);
							else if (d->next_pts != AV_NOPTS_VALUE)
								frame->pts = av_rescale_q(d->next_pts, d->next_pts_tb, tb);
							if (frame->pts != AV_NOPTS_VALUE) {
								d->next_pts = frame->pts + frame->nb_samples;
								d->next_pts_tb = tb;
							}
						}
						break;
					}
					if (ret == AVERROR_EOF) {
						d->finished = d->pkt_serial;
						avcodec_flush_buffers(d->avctx);
						return 0;
					}
					if (ret >= 0)
						return 1;
				} while (ret != AVERROR(EAGAIN));
			}

			do {
				if (d->queue->get_nb_packets() == 0)
					SDL_CondSignal(d->empty_queue_cond);
				if (d->packet_pending) {
					av_packet_move_ref(&pkt, &d->pkt);
					d->packet_pending = 0;
				}
				else {
					if (d->queue->get(&pkt, 1, &d->pkt_serial) < 0)
						return -1;
				}
			} while (d->queue->get_serial() != d->pkt_serial);

			if (pkt.data == flush_pkt.data) {
				avcodec_flush_buffers(d->avctx);
				d->finished = 0;
				d->next_pts = d->start_pts;
				d->next_pts_tb = d->start_pts_tb;
			}
			else {
				if (d->avctx->codec_type == AVMEDIA_TYPE_SUBTITLE) {
					int got_frame = 0;
					ret = avcodec_decode_subtitle2(d->avctx, sub, &got_frame, &pkt);
					if (ret < 0) {
						ret = AVERROR(EAGAIN);
					}
					else {
						if (got_frame && !pkt.data) {
							d->packet_pending = 1;
							av_packet_move_ref(&d->pkt, &pkt);
						}
						ret = got_frame ? 0 : (pkt.data ? AVERROR(EAGAIN) : AVERROR_EOF);
					}
				}
				else {
					if (avcodec_send_packet(d->avctx, &pkt) == AVERROR(EAGAIN)) {
						av_log(d->avctx, AV_LOG_ERROR, "Receive_frame and send_packet both returned EAGAIN, which is an API violation.\n");
						d->packet_pending = 1;
						av_packet_move_ref(&d->pkt, &pkt);
					}
				}
				av_packet_unref(&pkt);
			}
		}
	}

	static void decoder_destroy(Decoder *d) {
		av_packet_unref(&d->pkt);
		avcodec_free_context(&d->avctx);
	}

	/* Clock function
	*/
	int get_master_sync_type() {
		if (this->av_sync_type == AV_SYNC_VIDEO_MASTER) {
			if (this->video_st)
				return AV_SYNC_VIDEO_MASTER;
			else
				return AV_SYNC_AUDIO_MASTER;
		}
		else if (this->av_sync_type == AV_SYNC_AUDIO_MASTER) {
			if (this->audio_st)
				return AV_SYNC_AUDIO_MASTER;
			else
				return AV_SYNC_EXTERNAL_CLOCK;
		}
		else {
			return AV_SYNC_EXTERNAL_CLOCK;
		}
	}

	/* get the current master clock value */
	double get_master_clock()
	{
		double val;

		switch (this->get_master_sync_type()) {
		case AV_SYNC_VIDEO_MASTER:
			val = pVidclk->get_clock();
			break;
		case AV_SYNC_AUDIO_MASTER:
			val = pAudclk->get_clock();
			break;
		default:
			val = pExtclk->get_clock();
			break;
		}
		return val;
	}

	void check_external_clock_speed() {
		if (this->video_stream >= 0 && pVideoq->get_nb_packets() <= EXTERNAL_CLOCK_MIN_FRAMES ||
			this->audio_stream >= 0 && pAudioq->get_nb_packets() <= EXTERNAL_CLOCK_MIN_FRAMES) {
			this->pExtclk->set_clock_speed(FFMAX(EXTERNAL_CLOCK_SPEED_MIN, pExtclk->get_clock_speed() - EXTERNAL_CLOCK_SPEED_STEP));
		}
		else if ((this->video_stream < 0 || pVideoq->get_nb_packets() > EXTERNAL_CLOCK_MAX_FRAMES) &&
			(this->audio_stream < 0 || pAudioq->get_nb_packets() > EXTERNAL_CLOCK_MAX_FRAMES)) {
			pExtclk->set_clock_speed(FFMIN(EXTERNAL_CLOCK_SPEED_MAX, pExtclk->get_clock_speed() + EXTERNAL_CLOCK_SPEED_STEP));
		}
		else {
			//TODO add a getter for the speed
			double speed = pExtclk->get_clock_speed();
			if (speed != 1.0)
				pExtclk->set_clock_speed(speed + EXTERNAL_CLOCK_SPEED_STEP * (1.0 - speed) / fabs(1.0 - speed));
		}
	}

	// Note romoved void *opaque
	static int audio_open(int64_t wanted_channel_layout, int wanted_nb_channels, int wanted_sample_rate,
		struct AudioParams *audio_hw_params);

	int queue_picture(AVFrame *src_frame, double pts, double duration, int64_t pos, int serial)
	{
		Frame *vp;

#if defined(DEBUG_SYNC)
		printf("frame_type=%c pts=%0.3f\n",
			av_get_picture_type_char(src_frame->pict_type), pts);
#endif

		if (!(vp = pPictq->peek_writable()))
			return -1;

		vp->sar = src_frame->sample_aspect_ratio;
		vp->uploaded = 0;

		vp->width = src_frame->width;
		vp->height = src_frame->height;
		vp->format = src_frame->format;

		vp->pts = pts;
		vp->duration = duration;
		vp->pos = pos;
		vp->serial = serial;

		set_default_window_size(vp->width, vp->height, vp->sar);

		av_frame_move_ref(vp->frame, src_frame);
		this->pPictq->push();
		return 0;
	}


	int get_video_frame( AVFrame *frame)
	{
		int got_picture;

		if ((got_picture = decoder_decode_frame(&this->viddec,frame, NULL)) < 0)
			return -1;

		if (got_picture) {
			double dpts = NAN;

			if (frame->pts != AV_NOPTS_VALUE)
				dpts = av_q2d(this->video_st->time_base) * frame->pts;

			frame->sample_aspect_ratio = av_guess_sample_aspect_ratio(this->ic, this->video_st, frame);

			if (framedrop>0 || (framedrop && this->get_master_sync_type() != AV_SYNC_VIDEO_MASTER)) {
				if (frame->pts != AV_NOPTS_VALUE) {
					double diff = dpts - this->get_master_clock();
					if (!isnan(diff) && fabs(diff) < AV_NOSYNC_THRESHOLD &&
						diff - this->frame_last_filter_delay < 0 &&
						this->viddec.pkt_serial == pVidclk->get_serial() &&
						pVideoq->get_nb_packets()) {
						this->frame_drops_early++;
						av_frame_unref(frame);
						got_picture = 0;
					}
				}
			}
		}

		return got_picture;
	}

	/* open a given stream. Return 0 if OK */
	int stream_component_open(int stream_index);

	void stream_component_close(int stream_index)
	{
		AVFormatContext *ic = this->ic;
		AVCodecParameters *codecpar;

		if (stream_index < 0 || stream_index >= ic->nb_streams)
			return;
		codecpar = ic->streams[stream_index]->codecpar;

		switch (codecpar->codec_type) {
		case AVMEDIA_TYPE_AUDIO:
			decoder_abort(&this->auddec, pSampq);
			SDL_CloseAudioDevice(audio_dev);
			decoder_destroy(&this->auddec);
			swr_free(&this->swr_ctx);
			av_freep(&this->audio_buf1);
			this->audio_buf1_size = 0;
			this->audio_buf = NULL;

			if (this->rdft) {
				av_rdft_end(this->rdft);
				av_freep(&this->rdft_data);
				this->rdft = NULL;
				this->rdft_bits = 0;
			}
			break;
		case AVMEDIA_TYPE_VIDEO:
			decoder_abort(&this->viddec, pPictq);
			decoder_destroy(&this->viddec);
			break;
		case AVMEDIA_TYPE_SUBTITLE:
			decoder_abort(&this->subdec, pSubpq);
			decoder_destroy(&this->subdec);
			break;
		default:
			break;
		}

		ic->streams[stream_index]->discard = AVDISCARD_ALL;
		switch (codecpar->codec_type) {
		case AVMEDIA_TYPE_AUDIO:
			this->audio_st = NULL;
			this->audio_stream = -1;
			break;
		case AVMEDIA_TYPE_VIDEO:
			this->video_st = NULL;
			this->video_stream = -1;
			break;
		case AVMEDIA_TYPE_SUBTITLE:
			this->subtitle_st = NULL;
			this->subtitle_stream = -1;
			break;
		default:
			break;
		}
	}

	// Clean up memory
	// TODO: need to review this function 
	void stream_close()
	{
		/* XXX: use a special url_shutdown call to abort parse cleanly */
		this->abort_request = 1;
		SDL_WaitThread(this->read_tid, NULL);

		/* close each stream */
		if (this->audio_stream >= 0)
			this->stream_component_close(this->audio_stream);
		if (this->video_stream >= 0)
			this->stream_component_close(this->video_stream);
		if (this->subtitle_stream >= 0)
			this->stream_component_close(this->subtitle_stream);

		avformat_close_input(&this->ic);

		// TODO Need to be added to the ~PacketQueue(); flush, destroy both mutex and condition
		//this->pVideoq->packet_queue_destroy();
		//this->pAudioq->packet_queue_destroy();
		//this->pSubtitleq->packet_queue_destroy();

		/* free all pictures */
		//this->pPictq->frame_queue_destory();
		//this->pSampq->frame_queue_destory();
		//this->pSubpq->frame_queue_destory();
		SDL_DestroyCond(this->continue_read_thread);
		sws_freeContext(this->img_convert_ctx);
		sws_freeContext(this->sub_convert_ctx);
		av_free(this->filename);
		if (this->vis_texture)
			SDL_DestroyTexture(this->vis_texture);
		if (this->vid_texture)
			SDL_DestroyTexture(this->vid_texture);
		if (this->sub_texture)
			SDL_DestroyTexture(this->sub_texture);
		av_free(this);
	}

	int video_open()
	{
		int w, h;

		if (screen_width) {
			w = screen_width;
			h = screen_height;
		}
		else {
			w = default_width;
			h = default_height;
		}

		if (!window_title)
			window_title = input_filename;
		SDL_SetWindowTitle(window, window_title);

		SDL_SetWindowSize(window, w, h);
		SDL_SetWindowPosition(window, SDL_WINDOWPOS_CENTERED, SDL_WINDOWPOS_CENTERED);
		if (is_full_screen)
			SDL_SetWindowFullscreen(window, SDL_WINDOW_FULLSCREEN_DESKTOP);
		SDL_ShowWindow(window);

		this->width = w;
		this->height = h;

		return 0;
	}

	void video_image_display()
	{
		Frame *vp;
		Frame *sp = NULL;
		SDL_Rect rect;

		vp = this->pPictq->peek_last();
		if (this->subtitle_st) {
			if (this->pSubpq->nb_remaining() > 0) {
				sp = this->pSubpq->peek();

				if (vp->pts >= sp->pts + ((float)sp->sub.start_display_time / 1000)) {
					if (!sp->uploaded) {
						uint8_t* pixels[4];
						int pitch[4];
						int i;
						if (!sp->width || !sp->height) {
							sp->width = vp->width;
							sp->height = vp->height;
						}
						if (realloc_texture(&this->sub_texture, SDL_PIXELFORMAT_ARGB8888, sp->width, sp->height, SDL_BLENDMODE_BLEND, 1) < 0)
							return;

						for (i = 0; i < sp->sub.num_rects; i++) {
							AVSubtitleRect *sub_rect = sp->sub.rects[i];

							sub_rect->x = av_clip(sub_rect->x, 0, sp->width);
							sub_rect->y = av_clip(sub_rect->y, 0, sp->height);
							sub_rect->w = av_clip(sub_rect->w, 0, sp->width - sub_rect->x);
							sub_rect->h = av_clip(sub_rect->h, 0, sp->height - sub_rect->y);

							this->sub_convert_ctx = sws_getCachedContext(this->sub_convert_ctx,
								sub_rect->w, sub_rect->h, AV_PIX_FMT_PAL8,
								sub_rect->w, sub_rect->h, AV_PIX_FMT_BGRA,
								0, NULL, NULL, NULL);
							if (!this->sub_convert_ctx) {
								av_log(NULL, AV_LOG_FATAL, "Cannot initialize the conversion context\n");
								return;
							}
							if (!SDL_LockTexture(this->sub_texture, (SDL_Rect *)sub_rect, (void **)pixels, pitch)) {
								sws_scale(this->sub_convert_ctx, (const uint8_t * const *)sub_rect->data, sub_rect->linesize,
									0, sub_rect->h, pixels, pitch);
								SDL_UnlockTexture(this->sub_texture);
							}
						}
						sp->uploaded = 1;
					}
				}
				else
					sp = NULL;
			}
		}

		calculate_display_rect(&rect, this->xleft, this->ytop, this->width, this->height, vp->width, vp->height, vp->sar);

		if (!vp->uploaded) {
			if (upload_texture(&this->vid_texture, vp->frame, &this->img_convert_ctx) < 0)
				return;
			vp->uploaded = 1;
			vp->flip_v = vp->frame->linesize[0] < 0;
		}

		SDL_RenderCopyEx(renderer, this->vid_texture, NULL, &rect, 0, NULL, vp->flip_v ? SDL_FLIP_VERTICAL : SDL_FLIP_NONE);
		if (sp) {
#if USE_ONEPASS_SUBTITLE_RENDER
			SDL_RenderCopy(renderer, this->sub_texture, NULL, &rect);
#else
			int i;
			double xratio = (double)rect.w / (double)sp->width;
			double yratio = (double)rect.h / (double)sp->height;
			for (i = 0; i < sp->sub.num_rects; i++) {
				SDL_Rect *sub_rect = (SDL_Rect*)sp->sub.rects[i];
				SDL_Rect target = { .x = rect.x + sub_rect->x * xratio,
					.y = rect.y + sub_rect->y * yratio,
					.w = sub_rect->w * xratio,
					.h = sub_rect->h * yratio };
				SDL_RenderCopy(renderer, this->sub_texture, sub_rect, &target);
			}
#endif
		}
	}

	inline int compute_mod(int a, int b)
	{
		return a < 0 ? a % b + b : a % b;
	}

	inline void fill_rectangle(int x, int y, int w, int h)
	{
		SDL_Rect rect;
		rect.x = x;
		rect.y = y;
		rect.w = w;
		rect.h = h;
		if (w && h)
			SDL_RenderFillRect(renderer, &rect);
	}

	int realloc_texture(SDL_Texture **texture, Uint32 new_format, int new_width, int new_height, SDL_BlendMode blendmode, int init_texture)
	{
		Uint32 format;
		int access, w, h;
		if (!*texture || SDL_QueryTexture(*texture, &format, &access, &w, &h) < 0 || new_width != w || new_height != h || new_format != format) {
			void *pixels;
			int pitch;
			if (*texture)
				SDL_DestroyTexture(*texture);
			if (!(*texture = SDL_CreateTexture(renderer, new_format, SDL_TEXTUREACCESS_STREAMING, new_width, new_height)))
				return -1;
			if (SDL_SetTextureBlendMode(*texture, blendmode) < 0)
				return -1;
			if (init_texture) {
				if (SDL_LockTexture(*texture, NULL, &pixels, &pitch) < 0)
					return -1;
				memset(pixels, 0, pitch * new_height);
				SDL_UnlockTexture(*texture);
			}
			av_log(NULL, AV_LOG_VERBOSE, "Created %dx%d texture with %s.\n", new_width, new_height, SDL_GetPixelFormatName(new_format));
		}
		return 0;
	}

	void calculate_display_rect(SDL_Rect *rect,
		int scr_xleft, int scr_ytop, int scr_width, int scr_height,
		int pic_width, int pic_height, AVRational pic_sar)
	{
		float aspect_ratio;
		int width, height, x, y;

		if (pic_sar.num == 0)
			aspect_ratio = 0;
		else
			aspect_ratio = av_q2d(pic_sar);

		if (aspect_ratio <= 0.0)
			aspect_ratio = 1.0;
		aspect_ratio *= (float)pic_width / (float)pic_height;

		/* XXX: we suppose the screen has a 1.0 pixel ratio */
		height = scr_height;
		width = lrint(height * aspect_ratio) & ~1;
		if (width > scr_width) {
			width = scr_width;
			height = lrint(width / aspect_ratio) & ~1;
		}
		x = (scr_width - width) / 2;
		y = (scr_height - height) / 2;
		rect->x = scr_xleft + x;
		rect->y = scr_ytop + y;
		rect->w = FFMAX(width, 1);
		rect->h = FFMAX(height, 1);
	}

	void get_sdl_pix_fmt_and_blendmode(int format, Uint32 *sdl_pix_fmt, SDL_BlendMode *sdl_blendmode)
	{
		int i;
		*sdl_blendmode = SDL_BLENDMODE_NONE;
		*sdl_pix_fmt = SDL_PIXELFORMAT_UNKNOWN;
		if (format == AV_PIX_FMT_RGB32 ||
			format == AV_PIX_FMT_RGB32_1 ||
			format == AV_PIX_FMT_BGR32 ||
			format == AV_PIX_FMT_BGR32_1)
			*sdl_blendmode = SDL_BLENDMODE_BLEND;
		for (i = 0; i < FF_ARRAY_ELEMS(sdl_texture_format_map) - 1; i++) {
			if (format == sdl_texture_format_map[i].format) {
				*sdl_pix_fmt = sdl_texture_format_map[i].texture_fmt;
				return;
			}
		}
	}

	int upload_texture(SDL_Texture **tex, AVFrame *frame, struct SwsContext **img_convert_ctx) {
		int ret = 0;
		Uint32 sdl_pix_fmt;
		SDL_BlendMode sdl_blendmode;
		get_sdl_pix_fmt_and_blendmode(frame->format, &sdl_pix_fmt, &sdl_blendmode);
		if (realloc_texture(tex, sdl_pix_fmt == SDL_PIXELFORMAT_UNKNOWN ? SDL_PIXELFORMAT_ARGB8888 : sdl_pix_fmt, frame->width, frame->height, sdl_blendmode, 0) < 0)
			return -1;
		switch (sdl_pix_fmt) {
		case SDL_PIXELFORMAT_UNKNOWN:
			/* This should only happen if we are not using avfilter... */
			*img_convert_ctx = sws_getCachedContext(*img_convert_ctx,
				frame->width, frame->height, static_cast<AVPixelFormat>(frame->format), frame->width, frame->height,
				AV_PIX_FMT_BGRA, sws_flags, NULL, NULL, NULL);
			if (*img_convert_ctx != NULL) {
				uint8_t *pixels[4];
				int pitch[4];
				if (!SDL_LockTexture(*tex, NULL, (void **)pixels, pitch)) {
					sws_scale(*img_convert_ctx, (const uint8_t * const *)frame->data, frame->linesize,
						0, frame->height, pixels, pitch);
					SDL_UnlockTexture(*tex);
				}
			}
			else {
				av_log(NULL, AV_LOG_FATAL, "Cannot initialize the conversion context\n");
				ret = -1;
			}
			break;
		case SDL_PIXELFORMAT_IYUV:
			if (frame->linesize[0] > 0 && frame->linesize[1] > 0 && frame->linesize[2] > 0) {
				ret = SDL_UpdateYUVTexture(*tex, NULL, frame->data[0], frame->linesize[0],
					frame->data[1], frame->linesize[1],
					frame->data[2], frame->linesize[2]);
			}
			else if (frame->linesize[0] < 0 && frame->linesize[1] < 0 && frame->linesize[2] < 0) {
				ret = SDL_UpdateYUVTexture(*tex, NULL, frame->data[0] + frame->linesize[0] * (frame->height - 1), -frame->linesize[0],
					frame->data[1] + frame->linesize[1] * (AV_CEIL_RSHIFT(frame->height, 1) - 1), -frame->linesize[1],
					frame->data[2] + frame->linesize[2] * (AV_CEIL_RSHIFT(frame->height, 1) - 1), -frame->linesize[2]);
			}
			else {
				av_log(NULL, AV_LOG_ERROR, "Mixed negative and positive linesizes are not supported.\n");
				return -1;
			}
			break;
		default:
			if (frame->linesize[0] < 0) {
				ret = SDL_UpdateTexture(*tex, NULL, frame->data[0] + frame->linesize[0] * (frame->height - 1), -frame->linesize[0]);
			}
			else {
				ret = SDL_UpdateTexture(*tex, NULL, frame->data[0], frame->linesize[0]);
			}
			break;
		}
		return ret;
	}

	void video_audio_display()
	{
		int i, i_start, x, y1, y, ys, delay, n, nb_display_channels;
		int ch, channels, h, h2;
		int64_t time_diff;
		int rdft_bits, nb_freq;

		for (rdft_bits = 1; (1 << rdft_bits) < 2 * this->height; rdft_bits++)
			;
		nb_freq = 1 << (rdft_bits - 1);

		/* compute display index : center on currently output samples */
		channels = this->audio_tgt.channels;
		nb_display_channels = channels;
		if (!this->paused) {
			int data_used = show_mode == SHOW_MODE_WAVES ? this->width : (2 * nb_freq);
			n = 2 * channels;
			delay = this->audio_write_buf_size;
			delay /= n;

			/* to be more precise, we take into account the time spent since
			the last buffer computation */
			if (audio_callback_time) {
				time_diff = av_gettime_relative() - audio_callback_time;
				delay -= (time_diff * this->audio_tgt.freq) / 1000000;
			}

			delay += 2 * data_used;
			if (delay < data_used)
				delay = data_used;

			i_start = x = compute_mod(this->sample_array_index - delay * channels, SAMPLE_ARRAY_SIZE);
			if (show_mode == SHOW_MODE_WAVES) {
				h = INT_MIN;
				for (i = 0; i < 1000; i += channels) {
					int idx = (SAMPLE_ARRAY_SIZE + x - i) % SAMPLE_ARRAY_SIZE;
					int a = this->sample_array[idx];
					int b = this->sample_array[(idx + 4 * channels) % SAMPLE_ARRAY_SIZE];
					int c = this->sample_array[(idx + 5 * channels) % SAMPLE_ARRAY_SIZE];
					int d = this->sample_array[(idx + 9 * channels) % SAMPLE_ARRAY_SIZE];
					int score = a - d;
					if (h < score && (b ^ c) < 0) {
						h = score;
						i_start = idx;
					}
				}
			}

			this->last_i_start = i_start;
		}
		else {
			i_start = this->last_i_start;
		}

		if (show_mode == SHOW_MODE_WAVES) {
			SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

			/* total height for one channel */
			h = this->height / nb_display_channels;
			/* graph height / 2 */
			h2 = (h * 9) / 20;
			for (ch = 0; ch < nb_display_channels; ch++) {
				i = i_start + ch;
				y1 = this->ytop + ch * h + (h / 2); /* position of center line */
				for (x = 0; x < this->width; x++) {
					y = (this->sample_array[i] * h2) >> 15;
					if (y < 0) {
						y = -y;
						ys = y1 - y;
					}
					else {
						ys = y1;
					}
					fill_rectangle(this->xleft + x, ys, 1, y);
					i += channels;
					if (i >= SAMPLE_ARRAY_SIZE)
						i -= SAMPLE_ARRAY_SIZE;
				}
			}

			SDL_SetRenderDrawColor(renderer, 0, 0, 255, 255);

			for (ch = 1; ch < nb_display_channels; ch++) {
				y = this->ytop + ch * h;
				fill_rectangle(this->xleft, y, this->width, 1);
			}
		}
		else {
			if (realloc_texture(&this->vis_texture, SDL_PIXELFORMAT_ARGB8888, this->width, this->height, SDL_BLENDMODE_NONE, 1) < 0)
				return;

			nb_display_channels = FFMIN(nb_display_channels, 2);
			if (rdft_bits != this->rdft_bits) {
				av_rdft_end(this->rdft);
				av_free(this->rdft_data);
				this->rdft = av_rdft_init(rdft_bits, DFT_R2C);
				this->rdft_bits = rdft_bits;
				this->rdft_data = (FFTSample*) av_malloc_array(nb_freq, 4 * sizeof(*this->rdft_data));
			}
			if (!this->rdft || !this->rdft_data) {
				av_log(NULL, AV_LOG_ERROR, "Failed to allocate buffers for RDFT, switching to waves display\n");
				show_mode = SHOW_MODE_WAVES;
			}
			else {
				FFTSample *data[2];
				SDL_Rect rect = {}; // {.x = s->xpos, .y = 0, .w = 1, .h = s->height };
				rect.x = this->xpos;
				rect.y = 0;
				rect.w = 1;
				rect.h = this->height;
				uint32_t *pixels;
				int pitch;
				for (ch = 0; ch < nb_display_channels; ch++) {
					data[ch] = this->rdft_data + 2 * nb_freq * ch;
					i = i_start + ch;
					for (x = 0; x < 2 * nb_freq; x++) {
						double w = (x - nb_freq) * (1.0 / nb_freq);
						data[ch][x] = this->sample_array[i] * (1.0 - w * w);
						i += channels;
						if (i >= SAMPLE_ARRAY_SIZE)
							i -= SAMPLE_ARRAY_SIZE;
					}
					av_rdft_calc(this->rdft, data[ch]);
				}
				/* Least efficient way to do this, we should of course
				* directly access it but it is more than fast enough. */
				if (!SDL_LockTexture(this->vis_texture, &rect, (void **)&pixels, &pitch)) {
					pitch >>= 2;
					pixels += pitch * this->height;
					for (y = 0; y < this->height; y++) {
						double w = 1 / sqrt(nb_freq);
						int a = sqrt(w * sqrt(data[0][2 * y + 0] * data[0][2 * y + 0] + data[0][2 * y + 1] * data[0][2 * y + 1]));
						int b = (nb_display_channels == 2) ? sqrt(w * hypot(data[1][2 * y + 0], data[1][2 * y + 1]))
							: a;
						a = FFMIN(a, 255);
						b = FFMIN(b, 255);
						pixels -= pitch;
						*pixels = (a << 16) + (b << 8) + ((a + b) >> 1);
					}
					SDL_UnlockTexture(this->vis_texture);
				}
				SDL_RenderCopy(renderer, this->vis_texture, NULL, NULL);
			}
			if (!this->paused)
				this->xpos++;
			if (this->xpos >= this->width)
				this->xpos = this->xleft;
		}
	}

	/* display the current picture, if any */
	void video_display()
	{
		if (!this->width)
			this->video_open();

		SDL_SetRenderDrawColor(renderer, 0, 0, 0, 255);
		SDL_RenderClear(renderer);
		if (this->audio_st && show_mode != SHOW_MODE_VIDEO)
			this->video_audio_display();
		else if (this->video_st)
			this->video_image_display();
		SDL_RenderPresent(renderer);
	}

	double compute_target_delay(double delay)
	{
		double sync_threshold, diff = 0;

		/* update delay to follow master synchronisation source */
		if (this->get_master_sync_type() != AV_SYNC_VIDEO_MASTER) {
			/* if video is slave, we try to correct big delays by
			duplicating or deleting a frame */
			diff = this->pVidclk->get_clock() - this->get_master_clock();

			/* skip or repeat frame. We take into account the
			delay to compute the threshold. I still don't know
			if it is the best guess */
			sync_threshold = FFMAX(AV_SYNC_THRESHOLD_MIN, FFMIN(AV_SYNC_THRESHOLD_MAX, delay));
			if (!isnan(diff) && fabs(diff) < this->max_frame_duration) {
				if (diff <= -sync_threshold)
					delay = FFMAX(0, delay + diff);
				else if (diff >= sync_threshold && delay > AV_SYNC_FRAMEDUP_THRESHOLD)
					delay = delay + diff;
				else if (diff >= sync_threshold)
					delay = 2 * delay;
			}
		}

		av_log(NULL, AV_LOG_TRACE, "video: delay=%0.3f A-V=%f\n",
			delay, -diff);

		return delay;
	}

	double vp_duration(Frame *vp, Frame *nextvp) {
		if (vp->serial == nextvp->serial) {
			double duration = nextvp->pts - vp->pts;
			if (isnan(duration) || duration <= 0 || duration > this->max_frame_duration)
				return vp->duration;
			else
				return duration;
		}
		else {
			return 0.0;
		}
	}

	void update_video_pts(double pts, int64_t pos, int serial) {
		/* update current video pts */
		pVidclk->set_clock(pts, serial);
		Clock::sync_clock_to_slave(pExtclk, pVidclk);
	}

	/* called to display each frame */
	void video_refresh(double *remaining_time)
	{
		double time;

		Frame *sp, *sp2;

		if (!this->paused && this->get_master_sync_type() == AV_SYNC_EXTERNAL_CLOCK && this->realtime)
			this->check_external_clock_speed();

		if (!display_disable && show_mode != SHOW_MODE_VIDEO && this->audio_st) {
			time = av_gettime_relative() / 1000000.0;
			if (this->force_refresh || this->last_vis_time + rdftspeed < time) {
				this->video_display();
				this->last_vis_time = time;
			}
			*remaining_time = FFMIN(*remaining_time, this->last_vis_time + rdftspeed - time);
		}

		if (this->video_st) {
		retry:
			if (pPictq->nb_remaining() == 0) {
				// nothing to do, no picture to display in the queue
			}
			else {
				double last_duration, duration, delay;
				Frame *vp, *lastvp;

				/* dequeue the picture */
				lastvp = pPictq->peek_last();
				vp = pPictq->peek();

				if (vp->serial != pVideoq->get_serial()) {
					pPictq->next();
					goto retry;
				}

				if (lastvp->serial != vp->serial)
					this->frame_timer = av_gettime_relative() / 1000000.0;

				if (this->paused)
					goto display;

				/* compute nominal last_duration */
				last_duration = this->vp_duration(lastvp, vp);
				delay = this->compute_target_delay(last_duration);

				time = av_gettime_relative() / 1000000.0;
				if (time < this->frame_timer + delay) {
					*remaining_time = FFMIN(this->frame_timer + delay - time, *remaining_time);
					goto display;
				}

				this->frame_timer += delay;
				if (delay > 0 && time - this->frame_timer > AV_SYNC_THRESHOLD_MAX)
					this->frame_timer = time;

				// Replaced SDL_LockMutex(pPictq.mutex)by the following
				std::unique_lock<std::mutex> locker(pPictq->get_mutex());
				if (!isnan(vp->pts))
					this->update_video_pts(vp->pts, vp->pos, vp->serial);
				// Replaced SDL_UnlockMutex(this->pPictq.mutex) by
				locker.unlock();

				if (pPictq->nb_remaining() > 1) {
					Frame *nextvp = this->pPictq->peek_next();
					duration = this->vp_duration(vp, nextvp);
					if (!this->step && (framedrop>0 || (framedrop && this->get_master_sync_type() != AV_SYNC_VIDEO_MASTER)) && time > this->frame_timer + duration) {
						this->frame_drops_late++;
						this->pPictq->next();
						goto retry;
					}
				}

				if (this->subtitle_st) {
					while (pSubpq->nb_remaining() > 0) {
						sp = pSubpq->peek();

						if (pSubpq->nb_remaining() > 1)
							sp2 = pSubpq->peek_next();
						else
							sp2 = NULL;

						//TODO add getter for pts
						if (sp->serial != pSubtitleq->get_serial()
							|| (pVidclk->get_pts() > (sp->pts + ((float)sp->sub.end_display_time / 1000)))
							|| (sp2 && pVidclk->get_pts() > (sp2->pts + ((float)sp2->sub.start_display_time / 1000))))
						{
							if (sp->uploaded) {
								int i;
								for (i = 0; i < sp->sub.num_rects; i++) {
									AVSubtitleRect *sub_rect = sp->sub.rects[i];
									uint8_t *pixels;
									int pitch, j;

									if (!SDL_LockTexture(this->sub_texture, (SDL_Rect *)sub_rect, (void **)&pixels, &pitch)) {
										for (j = 0; j < sub_rect->h; j++, pixels += pitch)
											memset(pixels, 0, sub_rect->w << 2);
										SDL_UnlockTexture(this->sub_texture);
									}
								}
							}
							pSubpq->next();
						}
						else {
							break;
						}
					}
				}

				pPictq->next();
				this->force_refresh = 1;

				if (this->step && !this->paused)
					this->stream_toggle_pause();
			}
		display:
			/* display picture */
			if (!display_disable 
				&& this->force_refresh 
				&& (show_mode == SHOW_MODE_VIDEO) 
				&& pPictq->get_rindex_shown())
				this->video_display();
		}
		this->force_refresh = 0;
		if (show_status) {
			static int64_t last_time;
			int64_t cur_time;
			int aqsize, vqsize, sqsize;
			double av_diff;

			cur_time = av_gettime_relative();
			if (!last_time || (cur_time - last_time) >= 30000) {
				aqsize = 0;
				vqsize = 0;
				sqsize = 0;
				if (this->audio_st)
					aqsize = pAudioq->get_size();
				if (this->video_st)
					vqsize = pVideoq->get_size();
				if (this->subtitle_st)
					sqsize = pSubtitleq->get_size();
				av_diff = 0;
				if (this->audio_st && this->video_st)
					av_diff = this->pAudclk->get_clock() - this->pVidclk->get_clock();
				else if (this->video_st)
					av_diff = this->get_master_clock() - this->pVidclk->get_clock();
				else if (this->audio_st)
					av_diff = this->get_master_clock() - this->pAudclk->get_clock();
				av_log(NULL, AV_LOG_INFO,
					"%7.2f %s:%7.3f fd=%4d aq=%5dKB vq=%5dKB sq=%5dB f=%f /%f   \r",
					this->get_master_clock(),
					(this->audio_st && this->video_st) ? "A-V" : (this->video_st ? "M-V" : (this->audio_st ? "M-A" : "   ")),
					av_diff,
					this->frame_drops_early + this->frame_drops_late,
					aqsize / 1024,
					vqsize / 1024,
					sqsize,
					this->video_st ? this->viddec.avctx->pts_correction_num_faulty_dts : 0,
					this->video_st ? this->viddec.avctx->pts_correction_num_faulty_pts : 0);
				fflush(stdout);
				last_time = cur_time;
			}
		}
	}

	void stream_toggle_pause()
	{
		if (this->paused) {
			//TODO add getter for last_updated, serial and paused
			this->frame_timer += av_gettime_relative() / 1000000.0 - pVidclk->get_lastUpdated();
			if (this->read_pause_return != AVERROR(ENOSYS)) {
				// TODO Add getter for paused
				pVidclk->setPaused(0);
			}
			pVidclk->set_clock(pVidclk->get_clock(), pVidclk->get_serial());
		}
		pExtclk->set_clock(pExtclk->get_clock(), pExtclk->get_serial());

		this->paused = !this->paused;
		pAudclk->setPaused(!this->paused);
		pVidclk->setPaused(!this->paused);
		pExtclk->setPaused(!this->paused);
	}

	// Function Called from the event loop
	void refresh_loop_wait_event(SDL_Event *event) {
		double remaining_time = 0.0;
		SDL_PumpEvents();
		while (!SDL_PeepEvents(event, 1, SDL_GETEVENT, SDL_FIRSTEVENT, SDL_LASTEVENT)) {
			if (!cursor_hidden && av_gettime_relative() - cursor_last_shown > CURSOR_HIDE_DELAY) {
				SDL_ShowCursor(0);
				cursor_hidden = 1;
			}
			if (remaining_time > 0.0)
				av_usleep((int64_t)(remaining_time * 1000000.0));
			remaining_time = REFRESH_RATE;
			if (show_mode != SHOW_MODE_NONE && (!this->paused || this->force_refresh))
				this->video_refresh(&remaining_time);
			SDL_PumpEvents();
		}
	}

	// Function Called from the event loop
	void toggle_full_screen()
	{
		is_full_screen = !is_full_screen;
		SDL_SetWindowFullscreen(window, is_full_screen ? SDL_WINDOW_FULLSCREEN_DESKTOP : 0);
	}

	// Function Called from the event loop
	void toggle_pause()
	{
		this->stream_toggle_pause();
		this->step = 0;
	}

	// Function Called from the event loop
	void toggle_mute()
	{
		this->muted = !this->muted;
	}

	// Function Called from the event loop
	void update_volume(int sign, double step)
	{
		double volume_level = this->audio_volume ? (20 * log(this->audio_volume / (double)SDL_MIX_MAXVOLUME) / log(10)) : -1000.0;
		int new_volume = lrint(SDL_MIX_MAXVOLUME * pow(10.0, (volume_level + sign * step) / 20.0));
		this->audio_volume = av_clip(this->audio_volume == new_volume ? (this->audio_volume + sign) : new_volume, 0, SDL_MIX_MAXVOLUME);
	}

	// Function Called from the event loop
	void step_to_next_frame()
	{
		/* if the stream is paused unpause it, then step */
		if (this->paused)
			this->stream_toggle_pause();
		this->step = 1;
	}
	// Function Called from the event loop
	/* seek in the stream */
	void stream_seek(int64_t pos, int64_t rel, int seek_by_bytes)
	{
		if (!this->seek_req) {
			this->seek_pos = pos;
			this->seek_rel = rel;
			this->seek_flags &= ~AVSEEK_FLAG_BYTE;
			if (seek_by_bytes)
				this->seek_flags |= AVSEEK_FLAG_BYTE;
			this->seek_req = 1;
			SDL_CondSignal(this->continue_read_thread);
		}
	}

	// Function Called from the event loop
	void stream_cycle_channel(int codec_type)
	{
		AVFormatContext *ic = this->ic;
		int start_index, stream_index;
		int old_index;
		AVStream *st;
		AVProgram *p = NULL;
		int nb_streams = this->ic->nb_streams;

		if (codec_type == AVMEDIA_TYPE_VIDEO) {
			start_index = this->last_video_stream;
			old_index = this->video_stream;
		}
		else if (codec_type == AVMEDIA_TYPE_AUDIO) {
			start_index = this->last_audio_stream;
			old_index = this->audio_stream;
		}
		else {
			start_index = this->last_subtitle_stream;
			old_index = this->subtitle_stream;
		}
		stream_index = start_index;

		if (codec_type != AVMEDIA_TYPE_VIDEO && this->video_stream != -1) {
			p = av_find_program_from_stream(ic, NULL, this->video_stream);
			if (p) {
				nb_streams = p->nb_stream_indexes;
				for (start_index = 0; start_index < nb_streams; start_index++)
					if (p->stream_index[start_index] == stream_index)
						break;
				if (start_index == nb_streams)
					start_index = -1;
				stream_index = start_index;
			}
		}

		for (;;) {
			if (++stream_index >= nb_streams)
			{
				if (codec_type == AVMEDIA_TYPE_SUBTITLE)
				{
					stream_index = -1;
					this->last_subtitle_stream = -1;
					goto the_end;
				}
				if (start_index == -1)
					return;
				stream_index = 0;
			}
			if (stream_index == start_index)
				return;
			st = this->ic->streams[p ? p->stream_index[stream_index] : stream_index];
			if (st->codecpar->codec_type == codec_type) {
				/* check that parameters are OK */
				switch (codec_type) {
				case AVMEDIA_TYPE_AUDIO:
					if (st->codecpar->sample_rate != 0 &&
						st->codecpar->channels != 0)
						goto the_end;
					break;
				case AVMEDIA_TYPE_VIDEO:
				case AVMEDIA_TYPE_SUBTITLE:
					goto the_end;
				default:
					break;
				}
			}
		}
	the_end:
		if (p && stream_index != -1)
			stream_index = p->stream_index[stream_index];
		av_log(NULL, AV_LOG_INFO, "Switch %s stream from #%d to #%d\n",
			av_get_media_type_string(static_cast<AVMediaType>(codec_type)),
			old_index,
			stream_index);

		this->stream_component_close(old_index);
		this->stream_component_open(stream_index);
	}

	// Function Called from the event loop
	void toggle_audio_display()
	{
		int next = show_mode;
		do {
			next = (next + 1) % SHOW_MODE_NB;
		} while (next != show_mode && (next == SHOW_MODE_VIDEO && !this->video_st || next != SHOW_MODE_VIDEO && !this->audio_st));
		if (show_mode != next) {
			force_refresh = 1;
			show_mode = static_cast<ShowMode>(next);
		}
	}

	// Function Called from the event loop
	void seek_chapter(int incr)
	{
		int64_t pos = this->get_master_clock() * AV_TIME_BASE;
		int i;

		if (!this->ic->nb_chapters)
			return;

		/* find the current chapter */
		for (i = 0; i < this->ic->nb_chapters; i++) {
			AVChapter *ch = this->ic->chapters[i];
			if (av_compare_ts(pos, MY_AV_TIME_BASE_Q, ch->start, ch->time_base) < 0) {
				i--;
				break;
			}
		}

		i += incr;
		i = FFMAX(i, 0);
		if (i >= this->ic->nb_chapters)
			return;

		av_log(NULL, AV_LOG_VERBOSE, "Seeking to chapter %d.\n", i);
		this->stream_seek(av_rescale_q(this->ic->chapters[i]->start, this->ic->chapters[i]->time_base, MY_AV_TIME_BASE_Q), 0, 0);
	}

	void set_default_window_size(int width, int height, AVRational sar)
	{
		SDL_Rect rect;
		calculate_display_rect(&rect, 0, 0, INT_MAX, height, width, height, sar);
		default_width = rect.w;
		default_height = rect.h;
	}

	/*ported this function from cmdutils*/
	int check_stream_specifier(AVFormatContext *s, AVStream *st, const char *spec)
	{
		int ret = avformat_match_stream_specifier(s, st, spec);
		if (ret < 0)
			av_log(s, AV_LOG_ERROR, "Invalid stream specifier: %s.\n", spec);
		return ret;
	}
	//static void(*program_exit)(int ret);

	/*
	void exit_program(int ret)
	{
		if (program_exit)
			program_exit(ret);

		exit(ret);
	}*/

	/*ported this function from cmdutils*/
	AVDictionary *filter_codec_opts(AVDictionary *opts, enum AVCodecID codec_id,
		AVFormatContext *s, AVStream *st, AVCodec *codec) {
		AVDictionary    *ret = NULL;
		AVDictionaryEntry *t = NULL;
		int            flags = s->oformat ? AV_OPT_FLAG_ENCODING_PARAM
			: AV_OPT_FLAG_DECODING_PARAM;
		char          prefix = 0;
		const AVClass    *cc = avcodec_get_class();

		if (!codec)
			codec = s->oformat ? avcodec_find_encoder(codec_id)
			: avcodec_find_decoder(codec_id);

		switch (st->codecpar->codec_type) {
		case AVMEDIA_TYPE_VIDEO:
			prefix = 'v';
			flags |= AV_OPT_FLAG_VIDEO_PARAM;
			break;
		case AVMEDIA_TYPE_AUDIO:
			prefix = 'a';
			flags |= AV_OPT_FLAG_AUDIO_PARAM;
			break;
		case AVMEDIA_TYPE_SUBTITLE:
			prefix = 's';
			flags |= AV_OPT_FLAG_SUBTITLE_PARAM;
			break;
		}

		while (t = av_dict_get(opts, "", t, AV_DICT_IGNORE_SUFFIX)) {
			char *p = strchr(t->key, ':');

			/* check stream specification in opt name */
			if (p)
				switch (check_stream_specifier(s, st, p + 1)) {
				case  1: *p = 0; break;
				case  0:         continue;
				default:         exit(1);
				}

			if (av_opt_find(&cc, t->key, NULL, flags, AV_OPT_SEARCH_FAKE_OBJ) ||
				!codec ||
				(codec->priv_class &&
					av_opt_find(&codec->priv_class, t->key, NULL, flags,
						AV_OPT_SEARCH_FAKE_OBJ)))
				av_dict_set(&ret, t->key, t->value, 0);
			else if (t->key[0] == prefix &&
				av_opt_find(&cc, t->key + 1, NULL, flags,
					AV_OPT_SEARCH_FAKE_OBJ))
				av_dict_set(&ret, t->key + 1, t->value, 0);

			if (p)
				*p = ':';
		}
		return ret;
	}

	/* From cmd utils*/
	AVDictionary **setup_find_stream_info_opts(AVFormatContext *s,
		AVDictionary *codec_opts)
	{
		int i;
		AVDictionary **opts;

		if (!s->nb_streams)
			return NULL;
		opts = (AVDictionary**) av_mallocz_array(s->nb_streams, sizeof(*opts));
		if (!opts) {
			av_log(NULL, AV_LOG_ERROR,
				"Could not alloc memory for stream options.\n");
			return NULL;
		}
		for (i = 0; i < s->nb_streams; i++)
			opts[i] = filter_codec_opts(codec_opts, s->streams[i]->codecpar->codec_id,
				s, s->streams[i], NULL);
		return opts;
	}

	int stream_has_enough_packets(AVStream *st, int stream_id, PacketQueue *queue) {
		return stream_id < 0 ||
			queue->is_abort_request() ||
			(st->disposition & AV_DISPOSITION_ATTACHED_PIC) ||
			queue->get_nb_packets() > MIN_FRAMES && (!queue->get_duration() || av_q2d(st->time_base) * queue->get_duration() > 1.0);
	}

	int is_realtime(AVFormatContext *s)
	{
		if (!strcmp(s->iformat->name, "rtp")
			|| !strcmp(s->iformat->name, "rtsp")
			|| !strcmp(s->iformat->name, "sdp")
			)
			return 1;

		if (s->pb && (!strncmp(s->url, "rtp:", 4)
			|| !strncmp(s->url, "udp:", 4)
			)
			)
			return 1;
		return 0;
	}

	/* return the wanted number of samples to get better sync if sync_type is video
	* or external master clock */
	int synchronize_audio(int nb_samples) {
		int wanted_nb_samples = nb_samples;

		/* if not master, then we try to remove or add samples to correct the clock */
		if (get_master_sync_type() != AV_SYNC_AUDIO_MASTER) {
			double diff, avg_diff;
			int min_nb_samples, max_nb_samples;

			diff = pAudclk->get_clock() - get_master_clock();

			if (!isnan(diff) && fabs(diff) < AV_NOSYNC_THRESHOLD) {
				this->audio_diff_cum = diff + this->audio_diff_avg_coef * this->audio_diff_cum;
				if (this->audio_diff_avg_count < AUDIO_DIFF_AVG_NB) {
					/* not enough measures to have a correct estimate */
					this->audio_diff_avg_count++;
				}
				else {
					/* estimate the A-V difference */
					avg_diff = this->audio_diff_cum * (1.0 - this->audio_diff_avg_coef);

					if (fabs(avg_diff) >= this->audio_diff_threshold) {
						wanted_nb_samples = nb_samples + (int)(diff * this->audio_src.freq);
						min_nb_samples = ((nb_samples * (100 - SAMPLE_CORRECTION_PERCENT_MAX) / 100));
						max_nb_samples = ((nb_samples * (100 + SAMPLE_CORRECTION_PERCENT_MAX) / 100));
						wanted_nb_samples = av_clip(wanted_nb_samples, min_nb_samples, max_nb_samples);
					}
					av_log(NULL, AV_LOG_TRACE, "diff=%f adiff=%f sample_diff=%d apts=%0.3f %f\n",
						diff, avg_diff, wanted_nb_samples - nb_samples,
						this->audio_clock, this->audio_diff_threshold);
				}
			}
			else {
				/* too big difference : may be initial PTS errors, so
				reset A-V filter */
				this->audio_diff_avg_count = 0;
				this->audio_diff_cum = 0;
			}
		}
		return wanted_nb_samples;
	}

	/**
	* Decode one audio frame and return its uncompressed size.
	*
	* The processed audio frame is decoded, converted if required, and
	* stored in is->audio_buf, with size in bytes given by the return
	* value.
	*/
	int audio_decode_frame() {
		int data_size, resampled_data_size;
		int64_t dec_channel_layout;
		av_unused double audio_clock0;
		int wanted_nb_samples;
		Frame *af;

		if (this->paused)
			return -1;

		do {
#if defined(_WIN32)
			
			while (pSampq->nb_remaining() == 0) {
				if ((av_gettime_relative() - audio_callback_time) > 1000000LL * this->audio_hw_buf_size / this->audio_tgt.bytes_per_sec / 2)
					return -1;
				av_usleep(1000);
			}
#endif
			if (!(af = pSampq->peek_readable()))
				return -1;
			pSampq->next();
		} while (af->serial != pAudioq->get_serial());
		

		data_size = av_samples_get_buffer_size(NULL, af->frame->channels,
			af->frame->nb_samples,
			static_cast<AVSampleFormat>(af->frame->format), 1);

		dec_channel_layout =
			(af->frame->channel_layout && af->frame->channels == av_get_channel_layout_nb_channels(af->frame->channel_layout)) ?
			af->frame->channel_layout : av_get_default_channel_layout(af->frame->channels);
		wanted_nb_samples = synchronize_audio(af->frame->nb_samples);

		if (af->frame->format != this->audio_src.fmt ||
			dec_channel_layout != this->audio_src.channel_layout ||
			af->frame->sample_rate != this->audio_src.freq ||
			(wanted_nb_samples != af->frame->nb_samples && !this->swr_ctx)) {
			swr_free(&this->swr_ctx);
			this->swr_ctx = swr_alloc_set_opts(NULL,
				this->audio_tgt.channel_layout, this->audio_tgt.fmt, this->audio_tgt.freq,
				dec_channel_layout, static_cast<AVSampleFormat>(af->frame->format), af->frame->sample_rate,
				0, NULL);
			if (!this->swr_ctx || swr_init(this->swr_ctx) < 0) {
				av_log(NULL, AV_LOG_ERROR,
					"Cannot create sample rate converter for conversion of %d Hz %s %d channels to %d Hz %s %d channels!\n",
					af->frame->sample_rate, av_get_sample_fmt_name(static_cast<AVSampleFormat>(af->frame->format)), af->frame->channels,
					this->audio_tgt.freq, av_get_sample_fmt_name(this->audio_tgt.fmt), this->audio_tgt.channels);
				swr_free(&this->swr_ctx);
				return -1;
			}
			this->audio_src.channel_layout = dec_channel_layout;
			this->audio_src.channels = af->frame->channels;
			this->audio_src.freq = af->frame->sample_rate;
			this->audio_src.fmt = static_cast<AVSampleFormat>(af->frame->format);
		}

		if (this->swr_ctx) {
			const uint8_t **in = (const uint8_t **)af->frame->extended_data;
			uint8_t **out = &this->audio_buf1;
			int out_count = (int64_t)wanted_nb_samples * this->audio_tgt.freq / af->frame->sample_rate + 256;
			int out_size = av_samples_get_buffer_size(NULL, this->audio_tgt.channels, out_count, this->audio_tgt.fmt, 0);
			int len2;
			if (out_size < 0) {
				av_log(NULL, AV_LOG_ERROR, "av_samples_get_buffer_size() failed\n");
				return -1;
			}
			if (wanted_nb_samples != af->frame->nb_samples) {
				if (swr_set_compensation(this->swr_ctx, (wanted_nb_samples - af->frame->nb_samples) * this->audio_tgt.freq / af->frame->sample_rate,
					wanted_nb_samples * this->audio_tgt.freq / af->frame->sample_rate) < 0) {
					av_log(NULL, AV_LOG_ERROR, "swr_set_compensation() failed\n");
					return -1;
				}
			}
			av_fast_malloc(&this->audio_buf1, &this->audio_buf1_size, out_size);
			if (!this->audio_buf1)
				return AVERROR(ENOMEM);
			len2 = swr_convert(this->swr_ctx, out, out_count, in, af->frame->nb_samples);
			if (len2 < 0) {
				av_log(NULL, AV_LOG_ERROR, "swr_convert() failed\n");
				return -1;
			}
			if (len2 == out_count) {
				av_log(NULL, AV_LOG_WARNING, "audio buffer is probably too small\n");
				if (swr_init(this->swr_ctx) < 0)
					swr_free(&this->swr_ctx);
			}
			this->audio_buf = this->audio_buf1;
			resampled_data_size = len2 * this->audio_tgt.channels * av_get_bytes_per_sample(this->audio_tgt.fmt);
		}
		else {
			this->audio_buf = af->frame->data[0];
			resampled_data_size = data_size;
		}

		audio_clock0 = this->audio_clock;
		/* update the audio clock with the pts */
		if (!isnan(af->pts))
			this->audio_clock = af->pts + (double)af->frame->nb_samples / af->frame->sample_rate;
		else
			this->audio_clock = NAN;
		this->audio_clock_serial = af->serial;
		//#ifdef DEBUG
		//	{
		//		static double last_clock;
		//		printf("audio: delay=%0.3f clock=%0.3f clock0=%0.3f\n",
		//			is->audio_clock - last_clock,
		//			is->audio_clock, audio_clock0);
		//		last_clock = is->audio_clock;
		//	}
		//#endif
		return resampled_data_size;
	}

	/* copy samples for viewing in editor window */
	void update_sample_display(short *samples, int samples_size) {
		int size, len;

		size = samples_size / sizeof(short);
		while (size > 0) {
			len = SAMPLE_ARRAY_SIZE - this->sample_array_index;
			if (len > size)
				len = size;
			memcpy(this->sample_array + this->sample_array_index, samples, len * sizeof(short));
			samples += len;
			this->sample_array_index += len;
			if (this->sample_array_index >= SAMPLE_ARRAY_SIZE)
				this->sample_array_index = 0;
			size -= len;
		}
	}

public:
	VideoState() :
		pVideoq(new PacketQueue()),
		pAudioq(new PacketQueue()),
		pSubtitleq(new PacketQueue()),
		pPictq(new FrameQueue(pVideoq, VIDEO_PICTURE_QUEUE_SIZE, 1)),
		pSubpq(new FrameQueue(pSubtitleq, SUBPICTURE_QUEUE_SIZE, 0)),
		pSampq(new FrameQueue(pAudioq, SAMPLE_QUEUE_SIZE, 1)),
		pVidclk(new Clock(pVideoq->get_p_serial())),
		pAudclk(new Clock(pAudioq->get_p_serial())),
		//TODO fix serial for the external clock
		pExtclk(new Clock()) {
		// For the external clock the serial is set to itself (never triggers)		
	}

	~VideoState() {
		delete(pVideoq);
		delete(pAudioq);
		delete(pSubtitleq);
		delete(pPictq);
		delete(pSubpq);
		delete(pSampq);
		delete(pVidclk);
		delete(pAudclk);
		delete(pExtclk);
	}

	int read_thread();

	int audio_thread() {
		AVFrame *frame = av_frame_alloc();
		Frame *af;

#if CONFIG_AVFILTER
		int last_serial = -1;
		int64_t dec_channel_layout;
		int reconfigure;
#endif
		int got_frame = 0;
		AVRational tb;
		int ret = 0;

		if (!frame)
			return AVERROR(ENOMEM);

		do {
			if ((got_frame = decoder_decode_frame(&this->auddec, frame, NULL)) < 0)
				goto the_end;

			if (got_frame) {
				tb = av_make_q(1, frame->sample_rate);

#if CONFIG_AVFILTER
				dec_channel_layout = get_valid_channel_layout(frame->channel_layout, frame->channels);

				reconfigure =
					cmp_audio_fmts(is->audio_filter_src.fmt, is->audio_filter_src.channels,
						frame->format, frame->channels) ||
					is->audio_filter_src.channel_layout != dec_channel_layout ||
					is->audio_filter_src.freq != frame->sample_rate ||
					is->auddec.pkt_serial != last_serial;

				if (reconfigure) {
					char buf1[1024], buf2[1024];
					av_get_channel_layout_string(buf1, sizeof(buf1), -1, this->audio_filter_src.channel_layout);
					av_get_channel_layout_string(buf2, sizeof(buf2), -1, dec_channel_layout);
					av_log(NULL, AV_LOG_DEBUG,
						"Audio frame changed from rate:%d ch:%d fmt:%s layout:%s serial:%d to rate:%d ch:%d fmt:%s layout:%s serial:%d\n",
						this->audio_filter_src.freq, this->audio_filter_src.channels, av_get_sample_fmt_name(this->audio_filter_src.fmt), buf1, last_serial,
						frame->sample_rate, frame->channels, av_get_sample_fmt_name(frame->format), buf2, this->auddec.pkt_serial);

					this->audio_filter_src.fmt = frame->format;
					this->audio_filter_src.channels = frame->channels;
					this->audio_filter_src.channel_layout = dec_channel_layout;
					this->audio_filter_src.freq = frame->sample_rate;
					last_serial = this->auddec.pkt_serial;

					if ((ret = configure_audio_filters(this, afilters, 1)) < 0)
						goto the_end;
				}

				if ((ret = av_buffersrc_add_frame(this->in_audio_filter, frame)) < 0)
					goto the_end;

				while ((ret = av_buffersink_get_frame_flags(is->out_audio_filter, frame, 0)) >= 0) {
					tb = av_buffersink_get_time_base(is->out_audio_filter);
#endif
					if (!(af = pSampq->peek_writable()))
						goto the_end;

					af->pts = (frame->pts == AV_NOPTS_VALUE) ? NAN : frame->pts * av_q2d(tb);
					af->pos = frame->pkt_pos;
					af->serial = this->auddec.pkt_serial;
					af->duration = av_q2d(av_make_q(frame->nb_samples, frame->sample_rate));

					av_frame_move_ref(af->frame, frame);
					pSampq->push();

#if CONFIG_AVFILTER
					if (this->audioq.serial != this->auddec.pkt_serial)
						break;
				}
				if (ret == AVERROR_EOF)
					this->auddec.finished = this->auddec.pkt_serial;
#endif
			}
		} while (ret >= 0 || ret == AVERROR(EAGAIN) || ret == AVERROR_EOF);
	the_end:
#if CONFIG_AVFILTER
		avfilter_graph_free(&is->agraph);
#endif
		av_frame_free(&frame);
		return ret;
	}

	int video_thread()
	{
		AVFrame *frame = av_frame_alloc();
		double pts;
		double duration;
		int ret;
		AVRational tb = this->video_st->time_base;
		AVRational frame_rate = av_guess_frame_rate(this->ic, this->video_st, NULL);

#if CONFIG_AVFILTER
		AVFilterGraph *graph = avfilter_graph_alloc();
		AVFilterContext *filt_out = NULL, *filt_in = NULL;
		int last_w = 0;
		int last_h = 0;
		enum AVPixelFormat last_format = -2;
		int last_serial = -1;
		int last_vfilter_idx = 0;
		if (!graph) {
			av_frame_free(&frame);
			return AVERROR(ENOMEM);
		}

#endif

		if (!frame) {
#if CONFIG_AVFILTER
			avfilter_graph_free(&graph);
#endif
			return AVERROR(ENOMEM);
		}

		for (;;) {
			ret = this->get_video_frame(frame);
			if (ret < 0)
				goto the_end;
			if (!ret)
				continue;

#if CONFIG_AVFILTER
			if (last_w != frame->width
				|| last_h != frame->height
				|| last_format != frame->format
				|| last_serial != is->viddec.pkt_serial
				|| last_vfilter_idx != this->vfilter_idx) {
				av_log(NULL, AV_LOG_DEBUG,
					"Video frame changed from size:%dx%d format:%s serial:%d to size:%dx%d format:%s serial:%d\n",
					last_w, last_h,
					(const char *)av_x_if_null(av_get_pix_fmt_name(last_format), "none"), last_serial,
					frame->width, frame->height,
					(const char *)av_x_if_null(av_get_pix_fmt_name(frame->format), "none"), this->viddec.pkt_serial);
				avfilter_graph_free(&graph);
				graph = avfilter_graph_alloc();
				if ((ret = configure_video_filters(graph, this, vfilters_list ? vfilters_list[this->vfilter_idx] : NULL, frame)) < 0) {
					SDL_Event event;
					event.type = FF_QUIT_EVENT;
					event.user.data1 = this;
					SDL_PushEvent(&event);
					goto the_end;
				}
				filt_in = this->in_video_filter;
				filt_out = this->out_video_filter;
				last_w = frame->width;
				last_h = frame->height;
				last_format = frame->format;
				last_serial = this->viddec.pkt_serial;
				last_vfilter_idx = this->vfilter_idx;
				frame_rate = av_buffersink_get_frame_rate(filt_out);
			}

			ret = av_buffersrc_add_frame(filt_in, frame);
			if (ret < 0)
				goto the_end;

			while (ret >= 0) {
				this->frame_last_returned_time = av_gettime_relative() / 1000000.0;

				ret = av_buffersink_get_frame_flags(filt_out, frame, 0);
				if (ret < 0) {
					if (ret == AVERROR_EOF)
						this->viddec.finished = this->viddec.pkt_serial;
					ret = 0;
					break;
				}

				this->frame_last_filter_delay = av_gettime_relative() / 1000000.0 - this->frame_last_returned_time;
				if (fabs(this->frame_last_filter_delay) > AV_NOSYNC_THRESHOLD / 10.0)
					this->frame_last_filter_delay = 0;
				tb = av_buffersink_get_time_base(filt_out);
#endif
				duration = (frame_rate.num && frame_rate.den) ? av_q2d(av_make_q(frame_rate.den, frame_rate.num)) : 0;
				pts = (frame->pts == AV_NOPTS_VALUE) ? NAN : frame->pts * av_q2d(tb);
				ret = this->queue_picture(frame, pts, duration, frame->pkt_pos, this->viddec.pkt_serial);
				av_frame_unref(frame);
#if CONFIG_AVFILTER
			}
#endif

			if (ret < 0)
				goto the_end;
		}
	the_end:
#if CONFIG_AVFILTER
		avfilter_graph_free(&graph);
#endif
		av_frame_free(&frame);
		return 0;
	}

	int subtitle_thread()
	{
		Frame *sp;
		int got_subtitle;
		double pts;

		for (;;) {
			if (!(sp = pSubpq->peek_writable()))
				return 0;

			if ((got_subtitle = decoder_decode_frame(&this->subdec, NULL, &sp->sub)) < 0)
				break;

			pts = 0;

			if (got_subtitle && sp->sub.format == 0) {
				if (sp->sub.pts != AV_NOPTS_VALUE)
					pts = sp->sub.pts / (double)AV_TIME_BASE;
				sp->pts = pts;
				sp->serial = this->subdec.pkt_serial;
				sp->width = this->subdec.avctx->width;
				sp->height = this->subdec.avctx->height;
				sp->uploaded = 0;

				/* now we can update the picture count */
				this->pSubpq->push();
			}
			else if (got_subtitle) {
				avsubtitle_free(&sp->sub);
			}
		}
		return 0;
	}

	inline int decode_interrupt_cb() const {
		return abort_request;
	}

	void do_exit() {
		// close the VideoState Stream and and destroy SDL window
		if (this) {
			//Clean-up memory  
			this->stream_close();
		}
		if (renderer)
			SDL_DestroyRenderer(renderer);
		if (window)
			SDL_DestroyWindow(window);
		//uninit_opts();
#if CONFIG_AVFILTER
		av_freep(&vfilters_list);
#endif
		avformat_network_deinit();
		if (show_status)
			printf("\n");
		SDL_Quit();
		av_log(NULL, AV_LOG_QUIET, "%s", "");
		exit(0);
	}

	void event_loop() {
		// SDL: The event loop for the SDL window
		SDL_Event event;
		double incr, pos, frac;

		for (;;) {
			double x;
			this->refresh_loop_wait_event(&event);
			switch (event.type) {
			case SDL_KEYDOWN:
				if (exit_on_keydown) {
					this->do_exit();
					break;
				}
				switch (event.key.keysym.sym) {
				case SDLK_ESCAPE:
				case SDLK_q:
					this->do_exit();
					break;
				case SDLK_f:
					this->toggle_full_screen();
					this->force_refresh = 1;
					break;
				case SDLK_p:
				case SDLK_SPACE:
					this->toggle_pause();
					break;
				case SDLK_m:
					this->toggle_mute();
					break;
				case SDLK_KP_MULTIPLY:
				case SDLK_0:
					this->update_volume(1, SDL_VOLUME_STEP);
					break;
				case SDLK_KP_DIVIDE:
				case SDLK_9:
					this->update_volume(-1, SDL_VOLUME_STEP);
					break;
				case SDLK_s: // S: Step to next frame
					this->step_to_next_frame();
					break;
				case SDLK_a:
					this->stream_cycle_channel(AVMEDIA_TYPE_AUDIO);
					break;
				case SDLK_v:
					this->stream_cycle_channel(AVMEDIA_TYPE_VIDEO);
					break;
				case SDLK_c:
					this->stream_cycle_channel(AVMEDIA_TYPE_VIDEO);
					this->stream_cycle_channel(AVMEDIA_TYPE_AUDIO);
					this->stream_cycle_channel(AVMEDIA_TYPE_SUBTITLE);
					break;
				case SDLK_t:
					this->stream_cycle_channel(AVMEDIA_TYPE_SUBTITLE);
					break;
				case SDLK_w:
#if CONFIG_AVFILTER
					if (this->->show_mode == SHOW_MODE_VIDEO && this->->vfilter_idx < nb_vfilters - 1) {
						if (++this->->vfilter_idx >= nb_vfilters)
							this->->vfilter_idx = 0;
					}
					else {
						this->->vfilter_idx = 0;
						this->toggle_audio_display();
					}
#else
					this->toggle_audio_display();
#endif
					break;
				case SDLK_PAGEUP:
					if (this->ic->nb_chapters <= 1) {
						incr = 600.0;
						goto do_seek;
					}
					this->seek_chapter(1);
					break;
				case SDLK_PAGEDOWN:
					if (this->ic->nb_chapters <= 1) {
						incr = -600.0;
						goto do_seek;
					}
					this->seek_chapter(-1);
					break;
				case SDLK_LEFT:
					incr = -10.0;
					goto do_seek;
				case SDLK_RIGHT:
					incr = 10.0;
					goto do_seek;
				case SDLK_UP:
					incr = 60.0;
					goto do_seek;
				case SDLK_DOWN:
					incr = -60.0;
				do_seek:
					if (seek_by_bytes) {
						pos = -1;
						if (pos < 0 && this->video_stream >= 0)
							pos = pPictq->last_pos();
						if (pos < 0 && this->audio_stream >= 0)
							pos = pSampq->last_pos();
						if (pos < 0)
							pos = avio_tell(this->ic->pb);
						if (this->ic->bit_rate)
							incr *= this->ic->bit_rate / 8.0;
						else
							incr *= 180000.0;
						pos += incr;
						this->stream_seek(pos, incr, 1);
					}
					else {
						pos = this->get_master_clock();
						if (isnan(pos))
							pos = (double)this->seek_pos / AV_TIME_BASE;
						pos += incr;
						if (this->ic->start_time != AV_NOPTS_VALUE && pos < this->ic->start_time / (double)AV_TIME_BASE)
							pos = this->ic->start_time / (double)AV_TIME_BASE;
						this->stream_seek((int64_t)(pos * AV_TIME_BASE), (int64_t)(incr * AV_TIME_BASE), 0);
					}
					break;
				default:
					break;
				}
				break;
			case SDL_MOUSEBUTTONDOWN:
				if (exit_on_mousedown) {
					this->do_exit();
					break;
				}
				if (event.button.button == SDL_BUTTON_LEFT) {
					static int64_t last_mouse_left_click = 0;
					if (av_gettime_relative() - last_mouse_left_click <= 500000) {
						this->toggle_full_screen();
						this->force_refresh = 1;
						last_mouse_left_click = 0;
					}
					else {
						last_mouse_left_click = av_gettime_relative();
					}
				}
			case SDL_MOUSEMOTION:
				if (cursor_hidden) {
					SDL_ShowCursor(1);
					cursor_hidden = 0;
				}
				cursor_last_shown = av_gettime_relative();
				if (event.type == SDL_MOUSEBUTTONDOWN) {
					if (event.button.button != SDL_BUTTON_RIGHT)
						break;
					x = event.button.x;
				}
				else {
					if (!(event.motion.state & SDL_BUTTON_RMASK))
						break;
					x = event.motion.x;
				}
				if (seek_by_bytes || this->ic->duration <= 0) {
					uint64_t size = avio_size(this->ic->pb);
					this->stream_seek(size*x / this->width, 0, 1);
				}
				else {
					int64_t ts;
					int ns, hh, mm, ss;
					int tns, thh, tmm, tss;
					tns = this->ic->duration / 1000000LL;
					thh = tns / 3600;
					tmm = (tns % 3600) / 60;
					tss = (tns % 60);
					frac = x / this->width;
					ns = frac * tns;
					hh = ns / 3600;
					mm = (ns % 3600) / 60;
					ss = (ns % 60);
					av_log(NULL, AV_LOG_INFO,
						"Seek to %2.0f%% (%2d:%02d:%02d) of total duration (%2d:%02d:%02d)       \n", frac * 100,
						hh, mm, ss, thh, tmm, tss);
					ts = frac * this->ic->duration;
					if (this->ic->start_time != AV_NOPTS_VALUE)
						ts += this->ic->start_time;
					this->stream_seek(ts, 0, 0);
				}
				break;
			case SDL_WINDOWEVENT:
				switch (event.window.event) {
				case SDL_WINDOWEVENT_RESIZED:
					screen_width = this->width = event.window.data1;
					screen_height = this->height = event.window.data2;
					if (this->vis_texture) {
						SDL_DestroyTexture(this->vis_texture);
						this->vis_texture = NULL;
					}
				case SDL_WINDOWEVENT_EXPOSED:
					this->force_refresh = 1;
				}
				break;
			case SDL_QUIT:
			case FF_QUIT_EVENT:
				this->do_exit();
				break;
			default:
				break;
			}
		}
	}

	VideoState *stream_open(const char *filename, AVInputFormat *iformat);

	/* prepare a new audio buffer */
	void sdl_audio_callback(Uint8 *stream, int len) {
		int audio_size, len1;

		audio_callback_time = av_gettime_relative();

		while (len > 0) {
			if (this->audio_buf_index >= this->audio_buf_size) {
				audio_size = audio_decode_frame();
				if (audio_size < 0) {
					/* if error, just output silence */
					this->audio_buf = NULL;
					this->audio_buf_size = SDL_AUDIO_MIN_BUFFER_SIZE / this->audio_tgt.frame_size * this->audio_tgt.frame_size;
				}
				else {
					if (show_mode != SHOW_MODE_VIDEO)
						update_sample_display((int16_t *)this->audio_buf, audio_size);
					this->audio_buf_size = audio_size;
				}
				this->audio_buf_index = 0;
			}
			len1 = this->audio_buf_size - this->audio_buf_index;
			if (len1 > len)
				len1 = len;
			if (!this->muted && this->audio_buf && this->audio_volume == SDL_MIX_MAXVOLUME)
				memcpy(stream, (uint8_t *)this->audio_buf + this->audio_buf_index, len1);
			else {
				memset(stream, 0, len1);
				if (!this->muted && this->audio_buf)
					SDL_MixAudioFormat(stream, (uint8_t *)this->audio_buf + this->audio_buf_index, AUDIO_S16SYS, len1, this->audio_volume);
			}
			len -= len1;
			stream += len1;
			this->audio_buf_index += len1;
		}
		this->audio_write_buf_size = this->audio_buf_size - this->audio_buf_index;
		/* Let's assume the audio driver that is used by SDL has two periods. */
		if (!isnan(this->audio_clock)) {
			pAudclk->set_clock_at(this->audio_clock 
				- (double)(2 * this->audio_hw_buf_size + this->audio_write_buf_size) 
					/ this->audio_tgt.bytes_per_sec, this->audio_clock_serial, audio_callback_time / 1000000.0);
			Clock::sync_clock_to_slave(this->pExtclk, this->pAudclk);
		}
	}
};

// Note, this bridge is necessary to interface with the low-level c interface of the SDL callback
static int decode_interrupt_cb_bridge(void *vs) {
	return ((VideoState*)vs)->decode_interrupt_cb();
}

// TODO(fraudies): Remove bridge and change interface of the start method in Decoder
static int audio_thread_bridge(void *vs) {
	return ((VideoState*)vs)->audio_thread();
}

// TODO(fraudies): Remove bridge and change interface of the start method in Decoder
static int video_thread_bridge(void* vs) {
	return ((VideoState*)vs)->video_thread();
}

// TODO(fraudies): Remove bridge and change interface of the start method in Decoder
static int subtitle_thread_bridge(void* vs) {
	return ((VideoState*)vs)->subtitle_thread();
}

static int read_thread_bridge(void* vs) {
	return ((VideoState*)vs)->read_thread();
}

static void sdl_audio_callback_bridge(void* vs, Uint8 *stream, int len) {
	((VideoState*)vs)->sdl_audio_callback(stream, len);
}


VideoState *VideoState::stream_open(const char *filename, AVInputFormat *iformat) {
	VideoState *is;

	is = (VideoState*)av_mallocz(sizeof(VideoState));
	if (!is)
		return NULL;
	is->filename = av_strdup(filename);
	if (!is->filename)
		goto fail;
	is->iformat = iformat;
	is->ytop = 0;
	is->xleft = 0;

	/* start video display */
	//if (this->pPictq->frame_queue_init(&this->pVideoq, VIDEO_PICTURE_QUEUE_SIZE, 1) < 0)
	//	goto fail;
	//if (this->pSubpq->frame_queue_init(&this->pSubtitleq, SUBPICTURE_QUEUE_SIZE, 0) < 0)
	//	goto fail;
	//if (this->pSampq->frame_queue_init(&this->pAudioq, SAMPLE_QUEUE_SIZE, 1) < 0)
	//	goto fail;

	// The PacketQueut init is part of the constructor
	//if (this->pVideoq->packet_queue_init() < 0 ||
	//	this->pAudioq->packet_queue_init() < 0 ||
	//	this->pSubtitleq->packet_queue_init() < 0)
	//	goto fail;

	if (!(this->continue_read_thread = SDL_CreateCond())) {
		av_log(NULL, AV_LOG_FATAL, "SDL_CreateCond(): %s\n", SDL_GetError());
		goto fail;
	}

	// Moved init_clock to the constructor of VideoState
	//init_clock(&is->pVidclk, &is->pVideoq.serial);
	//init_clock(&is->pAudclk, &is->pAudioq.serial);
	//init_clock(&is->pExtclk, &is->pExtclk.serial);
	is->audio_clock_serial = -1;
	if (startup_volume < 0)
		av_log(NULL, AV_LOG_WARNING, "-volume=%d < 0, setting to 0\n", startup_volume);
	if (startup_volume > 100)
		av_log(NULL, AV_LOG_WARNING, "-volume=%d > 100, setting to 100\n", startup_volume);
	startup_volume = av_clip(startup_volume, 0, 100);
	startup_volume = av_clip(SDL_MIX_MAXVOLUME * startup_volume / 100, 0, SDL_MIX_MAXVOLUME);
	this->audio_volume = startup_volume;
	this->muted = 0;
	this->av_sync_type = av_sync_type;
	this->read_tid = SDL_CreateThread(read_thread_bridge, "read_thread", is);
	if (!this->read_tid) {
		av_log(NULL, AV_LOG_FATAL, "SDL_CreateThread(): %s\n", SDL_GetError());
	fail:
		this->stream_close();
		return NULL;
	}
	return is;
}

int VideoState::audio_open(int64_t wanted_channel_layout, int wanted_nb_channels, int wanted_sample_rate,
	struct AudioParams *audio_hw_params) {
	SDL_AudioSpec wanted_spec, spec;
	const char *env;
	static const int next_nb_channels[] = { 0, 0, 1, 6, 2, 6, 4, 6 };
	static const int next_sample_rates[] = { 0, 44100, 48000, 96000, 192000 };
	int next_sample_rate_idx = FF_ARRAY_ELEMS(next_sample_rates) - 1;

	env = SDL_getenv("SDL_AUDIO_CHANNELS");
	if (env) {
		wanted_nb_channels = atoi(env);
		wanted_channel_layout = av_get_default_channel_layout(wanted_nb_channels);
	}
	if (!wanted_channel_layout || wanted_nb_channels != av_get_channel_layout_nb_channels(wanted_channel_layout)) {
		wanted_channel_layout = av_get_default_channel_layout(wanted_nb_channels);
		wanted_channel_layout &= ~AV_CH_LAYOUT_STEREO_DOWNMIX;
	}
	wanted_nb_channels = av_get_channel_layout_nb_channels(wanted_channel_layout);
	wanted_spec.channels = wanted_nb_channels;
	wanted_spec.freq = wanted_sample_rate;
	if (wanted_spec.freq <= 0 || wanted_spec.channels <= 0) {
		av_log(NULL, AV_LOG_ERROR, "Invalid sample rate or channel count!\n");
		return -1;
	}
	while (next_sample_rate_idx && next_sample_rates[next_sample_rate_idx] >= wanted_spec.freq)
		next_sample_rate_idx--;
	wanted_spec.format = AUDIO_S16SYS;
	wanted_spec.silence = 0;
	wanted_spec.samples = FFMAX(SDL_AUDIO_MIN_BUFFER_SIZE, 2 << av_log2(wanted_spec.freq / SDL_AUDIO_MAX_CALLBACKS_PER_SEC));
	wanted_spec.callback = sdl_audio_callback_bridge;
	//wanted_spec.userdata = opaque;
	while (!(audio_dev = SDL_OpenAudioDevice(NULL, 0, &wanted_spec, &spec, SDL_AUDIO_ALLOW_FREQUENCY_CHANGE | SDL_AUDIO_ALLOW_CHANNELS_CHANGE))) {
		av_log(NULL, AV_LOG_WARNING, "SDL_OpenAudio (%d channels, %d Hz): %s\n",
			wanted_spec.channels, wanted_spec.freq, SDL_GetError());
		wanted_spec.channels = next_nb_channels[FFMIN(7, wanted_spec.channels)];
		if (!wanted_spec.channels) {
			wanted_spec.freq = next_sample_rates[next_sample_rate_idx--];
			wanted_spec.channels = wanted_nb_channels;
			if (!wanted_spec.freq) {
				av_log(NULL, AV_LOG_ERROR,
					"No more combinations to try, audio open failed\n");
				return -1;
			}
		}
		wanted_channel_layout = av_get_default_channel_layout(wanted_spec.channels);
	}
	if (spec.format != AUDIO_S16SYS) {
		av_log(NULL, AV_LOG_ERROR,
			"SDL advised audio format %d is not supported!\n", spec.format);
		return -1;
	}
	if (spec.channels != wanted_spec.channels) {
		wanted_channel_layout = av_get_default_channel_layout(spec.channels);
		if (!wanted_channel_layout) {
			av_log(NULL, AV_LOG_ERROR,
				"SDL advised channel count %d is not supported!\n", spec.channels);
			return -1;
		}
	}

	audio_hw_params->fmt = AV_SAMPLE_FMT_S16;
	audio_hw_params->freq = spec.freq;
	audio_hw_params->channel_layout = wanted_channel_layout;
	audio_hw_params->channels = spec.channels;
	audio_hw_params->frame_size = av_samples_get_buffer_size(NULL, audio_hw_params->channels, 1, audio_hw_params->fmt, 1);
	audio_hw_params->bytes_per_sec = av_samples_get_buffer_size(NULL, audio_hw_params->channels, audio_hw_params->freq, audio_hw_params->fmt, 1);
	if (audio_hw_params->bytes_per_sec <= 0 || audio_hw_params->frame_size <= 0) {
		av_log(NULL, AV_LOG_ERROR, "av_samples_get_buffer_size failed\n");
		return -1;
	}
	return spec.size;
}

//* this thread gets the stream from the disk or the network */
int VideoState::read_thread() {
	AVFormatContext *ic = NULL;
	int err, i, ret;
	int st_index[AVMEDIA_TYPE_NB];
	AVPacket pkt1, *pkt = &pkt1;
	int64_t stream_start_time;
	int pkt_in_play_range = 0;
	AVDictionaryEntry *t;
	SDL_mutex *wait_mutex = SDL_CreateMutex();
	int scan_all_pmts_set = 0;
	int64_t pkt_ts;

	AVDictionary *format_opts = NULL;

	AVDictionary *codec_opts = NULL;

	if (!wait_mutex) {
		av_log(NULL, AV_LOG_FATAL, "SDL_CreateMutex(): %s\n", SDL_GetError());
		ret = AVERROR(ENOMEM);
		goto fail;
	}

	memset(st_index, -1, sizeof(st_index));
	this->last_video_stream = this->video_stream = -1;
	this->last_audio_stream = this->audio_stream = -1;
	this->last_subtitle_stream = this->subtitle_stream = -1;
	this->eof = 0;

	ic = avformat_alloc_context();
	if (!ic) {
		av_log(NULL, AV_LOG_FATAL, "Could not allocate context.\n");
		ret = AVERROR(ENOMEM);
		goto fail;
	}
	//TODO need to fix the call pack function 
	ic->interrupt_callback.callback = decode_interrupt_cb_bridge;
	ic->interrupt_callback.opaque = this;
	if (!av_dict_get(format_opts, "scan_all_pmts", NULL, AV_DICT_MATCH_CASE)) {
		av_dict_set(&format_opts, "scan_all_pmts", "1", AV_DICT_DONT_OVERWRITE);
		scan_all_pmts_set = 1;
	}
	err = avformat_open_input(&ic, this->filename, this->iformat, &format_opts);
	if (err < 0) {
		//print_error(is->filename, err);
		ret = -1;
		goto fail;
	}
	if (scan_all_pmts_set)
		av_dict_set(&format_opts, "scan_all_pmts", NULL, AV_DICT_MATCH_CASE);

	if ((t = av_dict_get(format_opts, "", NULL, AV_DICT_IGNORE_SUFFIX))) {
		av_log(NULL, AV_LOG_ERROR, "Option %s not found.\n", t->key);
		ret = AVERROR_OPTION_NOT_FOUND;
		goto fail;
	}
	this->ic = ic;

	if (genpts)
		ic->flags |= AVFMT_FLAG_GENPTS;

	av_format_inject_global_side_data(ic);

	if (find_stream_info) {
		AVDictionary **opts = setup_find_stream_info_opts(ic, codec_opts);
		int orig_nb_streams = ic->nb_streams;

		err = avformat_find_stream_info(ic, opts);

		for (i = 0; i < orig_nb_streams; i++)
			av_dict_free(&opts[i]);
		av_freep(&opts);

		if (err < 0) {
			av_log(NULL, AV_LOG_WARNING,
				"%s: could not find codec parameters\n", this->filename);
			ret = -1;
			goto fail;
		}
	}

	if (ic->pb)
		ic->pb->eof_reached = 0; // FIXME hack, ffplay maybe should not use avio_feof() to test for the end

	if (seek_by_bytes < 0)
		seek_by_bytes = !!(ic->iformat->flags & AVFMT_TS_DISCONT) && strcmp("ogg", ic->iformat->name);

	this->max_frame_duration = (ic->iformat->flags & AVFMT_TS_DISCONT) ? 10.0 : 3600.0;

	if (!window_title && (t = av_dict_get(ic->metadata, "title", NULL, 0)))
		window_title = av_asprintf("%s - %s", t->value, input_filename);

	/* if seeking requested, we execute it */
	if (start_time != AV_NOPTS_VALUE) {
		int64_t timestamp;

		timestamp = start_time;
		/* add the stream start time */
		if (ic->start_time != AV_NOPTS_VALUE)
			timestamp += ic->start_time;
		ret = avformat_seek_file(ic, -1, INT64_MIN, timestamp, INT64_MAX, 0);
		if (ret < 0) {
			av_log(NULL, AV_LOG_WARNING, "%s: could not seek to position %0.3f\n",
				this->filename, (double)timestamp / AV_TIME_BASE);
		}
	}

	this->realtime = is_realtime(ic);

	if (show_status)
		av_dump_format(ic, 0, this->filename, 0);

	for (i = 0; i < ic->nb_streams; i++) {
		AVStream *st = ic->streams[i];
		enum AVMediaType type = st->codecpar->codec_type;
		st->discard = AVDISCARD_ALL;
		if (type >= 0 && wanted_stream_spec[type] && st_index[type] == -1)
			if (avformat_match_stream_specifier(ic, st, wanted_stream_spec[type]) > 0)
				st_index[type] = i;
	}
	for (i = 0; i < AVMEDIA_TYPE_NB; i++) {
		if (wanted_stream_spec[i] && st_index[i] == -1) {
			av_log(NULL, AV_LOG_ERROR, "Stream specifier %s does not match any %s stream\n",
				wanted_stream_spec[i],
				av_get_media_type_string(static_cast<AVMediaType>(i)));
			st_index[i] = INT_MAX;
		}
	}

	if (!video_disable)
		st_index[AVMEDIA_TYPE_VIDEO] =
		av_find_best_stream(ic, AVMEDIA_TYPE_VIDEO,
			st_index[AVMEDIA_TYPE_VIDEO], -1, NULL, 0);
	if (!audio_disable)
		st_index[AVMEDIA_TYPE_AUDIO] =
		av_find_best_stream(ic, AVMEDIA_TYPE_AUDIO,
			st_index[AVMEDIA_TYPE_AUDIO],
			st_index[AVMEDIA_TYPE_VIDEO],
			NULL, 0);
	if (!video_disable && !subtitle_disable)
		st_index[AVMEDIA_TYPE_SUBTITLE] =
		av_find_best_stream(ic, AVMEDIA_TYPE_SUBTITLE,
			st_index[AVMEDIA_TYPE_SUBTITLE],
			(st_index[AVMEDIA_TYPE_AUDIO] >= 0 ?
				st_index[AVMEDIA_TYPE_AUDIO] :
				st_index[AVMEDIA_TYPE_VIDEO]),
			NULL, 0);

	show_mode = show_mode;
	if (st_index[AVMEDIA_TYPE_VIDEO] >= 0) {
		AVStream *st = ic->streams[st_index[AVMEDIA_TYPE_VIDEO]];
		AVCodecParameters *codecpar = st->codecpar;
		AVRational sar = av_guess_sample_aspect_ratio(ic, st, NULL);
		if (codecpar->width)
			set_default_window_size(codecpar->width, codecpar->height, sar);
	}

	/* open the streams */
	if (st_index[AVMEDIA_TYPE_AUDIO] >= 0) {
		this->stream_component_open(st_index[AVMEDIA_TYPE_AUDIO]);
	}

	ret = -1;
	if (st_index[AVMEDIA_TYPE_VIDEO] >= 0) {
		ret = this->stream_component_open(st_index[AVMEDIA_TYPE_VIDEO]);
	}
	if (show_mode == SHOW_MODE_NONE)
		show_mode = ret >= 0 ? SHOW_MODE_VIDEO : SHOW_MODE_RDFT;

	if (st_index[AVMEDIA_TYPE_SUBTITLE] >= 0) {
		this->stream_component_open(st_index[AVMEDIA_TYPE_SUBTITLE]);
	}

	if (this->video_stream < 0 && this->audio_stream < 0) {
		av_log(NULL, AV_LOG_FATAL, "Failed to open file '%s' or configure filtergraph\n",
			this->filename);
		ret = -1;
		goto fail;
	}

	if (infinite_buffer < 0 && this->realtime)
		infinite_buffer = 1;

	for (;;) {
		if (this->abort_request)
			break;
		if (this->paused != this->last_paused) {
			this->last_paused = this->paused;
			if (this->paused)
				this->read_pause_return = av_read_pause(ic);
			else
				av_read_play(ic);
		}
#if CONFIG_RTSP_DEMUXER || CONFIG_MMSH_PROTOCOL
		if (this->paused &&
			(!strcmp(ic->iformat->name, "rtsp") ||
			(ic->pb && !strncmp(input_filename, "mmsh:", 5)))) {
			/* wait 10 ms to avoid trying to get another packet */
			/* XXX: horrible */
			SDL_Delay(10);
			continue;
		}
#endif
		if (this->seek_req) {
			int64_t seek_target = this->seek_pos;
			int64_t seek_min = this->seek_rel > 0 ? seek_target - this->seek_rel + 2 : INT64_MIN;
			int64_t seek_max = this->seek_rel < 0 ? seek_target - this->seek_rel - 2 : INT64_MAX;
			// FIXME the +-2 is due to rounding being not done in the correct direction in generation
			//      of the seek_pos/seek_rel variables

			ret = avformat_seek_file(this->ic, -1, seek_min, seek_target, seek_max, this->seek_flags);
			if (ret < 0) {
				av_log(NULL, AV_LOG_ERROR,
					"%s: error while seeking\n", this->ic->url);
			}
			else {
				if (this->audio_stream >= 0) {
					pAudioq->flush();
					pAudioq->put(&flush_pkt);
				}
				if (this->subtitle_stream >= 0) {
					pSubtitleq->flush();
					pSubtitleq->put(&flush_pkt);
				}
				if (this->video_stream >= 0) {
					pVideoq->flush();
					pVideoq->put(&flush_pkt);
				}
				if (this->seek_flags & AVSEEK_FLAG_BYTE) {
					pExtclk->set_clock(NAN, 0);
				}
				else {
					pExtclk->set_clock(seek_target / (double)AV_TIME_BASE, 0);
				}
			}
			this->seek_req = 0;
			this->queue_attachments_req = 1;
			this->eof = 0;
			if (this->paused)
				this->step_to_next_frame();
		}
		if (this->queue_attachments_req) {
			if (this->video_st && this->video_st->disposition & AV_DISPOSITION_ATTACHED_PIC) {
				AVPacket copy = { 0 };
				if ((ret = av_packet_ref(&copy, &this->video_st->attached_pic)) < 0)
					goto fail;
				pVideoq->put(&copy);
				pVideoq->put_null_packet(this->video_stream);
			}
			this->queue_attachments_req = 0;
		}

		/* if the queue are full, no need to read more */
		if (infinite_buffer<1 &&
			(pAudioq->get_size() + pVideoq->get_size() + pSubtitleq->get_size() > MAX_QUEUE_SIZE
				|| (stream_has_enough_packets(this->audio_st, this->audio_stream, pAudioq) &&
					stream_has_enough_packets(this->video_st, this->video_stream, pVideoq) &&
					stream_has_enough_packets(this->subtitle_st, this->subtitle_stream, pSubtitleq)))) {
			/* wait 10 ms */
			SDL_LockMutex(wait_mutex);
			SDL_CondWaitTimeout(this->continue_read_thread, wait_mutex, 10);
			SDL_UnlockMutex(wait_mutex);
			continue;
		}
		if (!this->paused &&
			(!this->audio_st || (this->auddec.finished == pAudioq->get_serial() && pSampq->nb_remaining() == 0)) &&
			(!this->video_st || (this->viddec.finished == pVideoq->get_serial() && pPictq->nb_remaining() == 0))) {
			if (loop != 1 && (!loop || --loop)) {
				this->stream_seek(start_time != AV_NOPTS_VALUE ? start_time : 0, 0, 0);
			}
			else if (autoexit) {
				ret = AVERROR_EOF;
				goto fail;
			}
		}
		ret = av_read_frame(ic, pkt);
		if (ret < 0) {
			if ((ret == AVERROR_EOF || avio_feof(ic->pb)) && !this->eof) {
				if (this->video_stream >= 0)
					pVideoq->put_null_packet(this->video_stream);
				if (this->audio_stream >= 0)
					pAudioq->put_null_packet(this->audio_stream);
				if (this->subtitle_stream >= 0)
					pSubtitleq->put_null_packet(this->subtitle_stream);
				this->eof = 1;
			}
			if (ic->pb && ic->pb->error)
				break;
			SDL_LockMutex(wait_mutex);
			SDL_CondWaitTimeout(this->continue_read_thread, wait_mutex, 10);
			SDL_UnlockMutex(wait_mutex);
			continue;
		}
		else {
			this->eof = 0;
		}
		/* check if packet is in play range specified by user, then queue, otherwise discard */
		stream_start_time = ic->streams[pkt->stream_index]->start_time;
		pkt_ts = pkt->pts == AV_NOPTS_VALUE ? pkt->dts : pkt->pts;
		pkt_in_play_range = duration == AV_NOPTS_VALUE ||
			(pkt_ts - (stream_start_time != AV_NOPTS_VALUE ? stream_start_time : 0)) *
			av_q2d(ic->streams[pkt->stream_index]->time_base) -
			(double)(start_time != AV_NOPTS_VALUE ? start_time : 0) / 1000000
			<= ((double)duration / 1000000);
		if (pkt->stream_index == this->audio_stream && pkt_in_play_range) {
			pAudioq->put(pkt);
		}
		else if (pkt->stream_index == this->video_stream && pkt_in_play_range
			&& !(this->video_st->disposition & AV_DISPOSITION_ATTACHED_PIC)) {
			pVideoq->put(pkt);
		}
		else if (pkt->stream_index == this->subtitle_stream && pkt_in_play_range) {
			pSubtitleq->put(pkt);
		}
		else {
			av_packet_unref(pkt);
		}
	}

	ret = 0;
fail:
	if (ic && !this->ic)
		avformat_close_input(&ic);

	if (ret != 0) {
		SDL_Event event;

		event.type = FF_QUIT_EVENT;
		event.user.data1 = this;
		SDL_PushEvent(&event);
	}
	SDL_DestroyMutex(wait_mutex);
	return 0;
}

int VideoState::stream_component_open(int stream_index) {
	AVFormatContext *ic = this->ic;
	AVCodecContext *avctx;
	AVCodec *codec;
	const char *forced_codec_name = NULL;
	AVDictionary *opts = NULL;
	AVDictionaryEntry *t = NULL;
	int sample_rate, nb_channels;
	int64_t channel_layout;
	int ret = 0;
	int stream_lowres = lowres;

	AVDictionary *codec_opts = NULL;

	if (stream_index < 0 || stream_index >= ic->nb_streams)
		return -1;

	avctx = avcodec_alloc_context3(NULL);
	if (!avctx)
		return AVERROR(ENOMEM);

	ret = avcodec_parameters_to_context(avctx, ic->streams[stream_index]->codecpar);
	if (ret < 0)
		goto fail;
	avctx->pkt_timebase = ic->streams[stream_index]->time_base;

	codec = avcodec_find_decoder(avctx->codec_id);

	switch (avctx->codec_type) {
	case AVMEDIA_TYPE_AUDIO: this->last_audio_stream = stream_index; forced_codec_name = audio_codec_name; break;
	case AVMEDIA_TYPE_SUBTITLE: this->last_subtitle_stream = stream_index; forced_codec_name = subtitle_codec_name; break;
	case AVMEDIA_TYPE_VIDEO: this->last_video_stream = stream_index; forced_codec_name = video_codec_name; break;
	}
	if (forced_codec_name)
		codec = avcodec_find_decoder_by_name(forced_codec_name);
	if (!codec) {
		if (forced_codec_name) av_log(NULL, AV_LOG_WARNING,
			"No codec could be found with name '%s'\n", forced_codec_name);
		else                   av_log(NULL, AV_LOG_WARNING,
			"No decoder could be found for codec %s\n", avcodec_get_name(avctx->codec_id));
		ret = AVERROR(EINVAL);
		goto fail;
	}

	avctx->codec_id = codec->id;
	if (stream_lowres > codec->max_lowres) {
		av_log(avctx, AV_LOG_WARNING, "The maximum value for lowres supported by the decoder is %d\n",
			codec->max_lowres);
		stream_lowres = codec->max_lowres;
	}
	avctx->lowres = stream_lowres;

	if (fast)
		avctx->flags2 |= AV_CODEC_FLAG2_FAST;

	opts = filter_codec_opts(codec_opts, avctx->codec_id, ic, ic->streams[stream_index], codec);
	if (!av_dict_get(opts, "threads", NULL, 0))
		av_dict_set(&opts, "threads", "auto", 0);
	if (stream_lowres)
		av_dict_set_int(&opts, "lowres", stream_lowres, 0);
	if (avctx->codec_type == AVMEDIA_TYPE_VIDEO || avctx->codec_type == AVMEDIA_TYPE_AUDIO)
		av_dict_set(&opts, "refcounted_frames", "1", 0);
	if ((ret = avcodec_open2(avctx, codec, &opts)) < 0) {
		goto fail;
	}
	if ((t = av_dict_get(opts, "", NULL, AV_DICT_IGNORE_SUFFIX))) {
		av_log(NULL, AV_LOG_ERROR, "Option %s not found.\n", t->key);
		ret = AVERROR_OPTION_NOT_FOUND;
		goto fail;
	}

	this->eof = 0;
	ic->streams[stream_index]->discard = AVDISCARD_DEFAULT;
	switch (avctx->codec_type) {
	case AVMEDIA_TYPE_AUDIO:
#if CONFIG_AVFILTER
	{
		AVFilterContext *sink;

		this->audio_filter_src.freq = avctx->sample_rate;
		this->audio_filter_src.channels = avctx->channels;
		this->audio_filter_src.channel_layout = get_valid_channel_layout(avctx->channel_layout, avctx->channels);
		this->audio_filter_src.fmt = avctx->sample_fmt;
		if ((ret = configure_audio_filters(is, afilters, 0)) < 0)
			goto fail;
		sink = is->out_audio_filter;
		sample_rate = av_buffersink_get_sample_rate(sink);
		nb_channels = av_buffersink_get_channels(sink);
		channel_layout = av_buffersink_get_channel_layout(sink);
	}
#else
		sample_rate = avctx->sample_rate;
		nb_channels = avctx->channels;
		channel_layout = avctx->channel_layout;
#endif

		/* prepare audio output */
		if ((ret = this->audio_open(channel_layout, nb_channels, sample_rate, &this->audio_tgt)) < 0)
			goto fail;
		this->audio_hw_buf_size = ret;
		this->audio_src = this->audio_tgt;
		this->audio_buf_size = 0;
		this->audio_buf_index = 0;

		/* init averaging filter */
		this->audio_diff_avg_coef = exp(log(0.01) / AUDIO_DIFF_AVG_NB);
		this->audio_diff_avg_count = 0;
		/* since we do not have a precise anough audio FIFO fullness,
		we correct audio sync only if larger than this threshold */
		this->audio_diff_threshold = (double)(this->audio_hw_buf_size) / this->audio_tgt.bytes_per_sec;

		this->audio_stream = stream_index;
		this->audio_st = ic->streams[stream_index];

		decoder_init(&this->auddec, avctx, pAudioq, this->continue_read_thread);
		if ((this->ic->iformat->flags & (AVFMT_NOBINSEARCH | AVFMT_NOGENSEARCH | AVFMT_NO_BYTE_SEEK)) && !this->ic->iformat->read_seek) {
			this->auddec.start_pts = this->audio_st->start_time;
			this->auddec.start_pts_tb = this->audio_st->time_base;
		}
		if ((ret = decoder_start(&this->auddec, audio_thread_bridge)) < 0)
			goto out;
		SDL_PauseAudioDevice(audio_dev, 0);
		break;
	case AVMEDIA_TYPE_VIDEO:
		this->video_stream = stream_index;
		this->video_st = ic->streams[stream_index];
		decoder_init(&this->viddec, avctx, pVideoq, this->continue_read_thread);
		if ((ret = decoder_start(&this->viddec, video_thread_bridge)) < 0)
			goto out;
		this->queue_attachments_req = 1;
		break;
	case AVMEDIA_TYPE_SUBTITLE:
		this->subtitle_stream = stream_index;
		this->subtitle_st = ic->streams[stream_index];

		decoder_init(&this->subdec, avctx, this->pSubtitleq, this->continue_read_thread);
		if ((ret = decoder_start(&this->subdec, subtitle_thread_bridge)) < 0)
			goto out;
		break;
	default:
		break;
	}
	goto out;

fail:
	avcodec_free_context(&avctx);
out:
	av_dict_free(&opts);

	return ret;
}



int main(int argc, char **argv)
{
	int flags;
	VideoState *is = nullptr;

	/*init_dynload();*/

	av_log_set_flags(AV_LOG_SKIP_REPEATED);
	//parse_loglevel(argc, argv, options);
	//parse_loglevel(argc, argv, NULL);

	/* register all codecs, demux and protocols */
#if CONFIG_AVDEVICE
	avdevice_register_all();
#endif
	avformat_network_init();
	av_log(NULL, AV_LOG_WARNING, "Init Network");


	//init_opts();

	//signal(SIGINT, sigterm_handler); /* Interrupt (ANSI).    */
	//signal(SIGTERM, sigterm_handler); /* Termination (ANSI).  */

	//show_banner(argc, argv, options);

	//parse_options(NULL, argc, argv, options, opt_input_file);

	input_filename = "counter.mp4";

	if (!input_filename) {
		//show_usage();
		av_log(NULL, AV_LOG_FATAL, "An input file must be specified\n");
		av_log(NULL, AV_LOG_FATAL,
			"Use -h to get full help or, even better, run 'man %s'\n", program_name);

		SDL_Delay(20000);
		exit(1);
	}

	if (display_disable) {
		video_disable = 1;
	}
	flags = SDL_INIT_VIDEO | SDL_INIT_AUDIO | SDL_INIT_TIMER;
	if (audio_disable)
		flags &= ~SDL_INIT_AUDIO;
	else {
		/* Try to work around an occasional ALSA buffer underflow issue when the
		* period size is NPOT due to ALSA resampling by forcing the buffer size. */
		if (!SDL_getenv("SDL_AUDIO_ALSA_SET_BUFFER_SIZE"))
			SDL_setenv("SDL_AUDIO_ALSA_SET_BUFFER_SIZE", "1", 1);
	}
	if (display_disable)
		flags &= ~SDL_INIT_VIDEO;
	if (SDL_Init(flags)) {
		av_log(NULL, AV_LOG_FATAL, "Could not initialize SDL - %s\n", SDL_GetError());
		av_log(NULL, AV_LOG_FATAL, "(Did you set the DISPLAY variable?)\n");
		exit(1);
	}

	SDL_EventState(SDL_SYSWMEVENT, SDL_IGNORE);
	SDL_EventState(SDL_USEREVENT, SDL_IGNORE);

	av_init_packet(&flush_pkt);
	flush_pkt.data = (uint8_t *)&flush_pkt;

	if (!display_disable) {
		int flags = SDL_WINDOW_HIDDEN;
		if (borderless)
			flags |= SDL_WINDOW_BORDERLESS;
		else
			flags |= SDL_WINDOW_RESIZABLE;
		window = SDL_CreateWindow(program_name, SDL_WINDOWPOS_UNDEFINED, SDL_WINDOWPOS_UNDEFINED, default_width, default_height, flags);
		SDL_SetHint(SDL_HINT_RENDER_SCALE_QUALITY, "linear");
		if (window) {
			renderer = SDL_CreateRenderer(window, -1, SDL_RENDERER_ACCELERATED | SDL_RENDERER_PRESENTVSYNC);
			if (!renderer) {
				av_log(NULL, AV_LOG_WARNING, "Failed to initialize a hardware accelerated renderer: %s\n", SDL_GetError());
				renderer = SDL_CreateRenderer(window, -1, 0);
			}
			if (renderer) {
				if (!SDL_GetRendererInfo(renderer, &renderer_info))
					av_log(NULL, AV_LOG_VERBOSE, "Initialized %s renderer.\n", renderer_info.name);
			}
		}
		if (!window || !renderer || !renderer_info.num_texture_formats) {
			av_log(NULL, AV_LOG_FATAL, "Failed to create window or renderer: %s", SDL_GetError());
			is->do_exit();
		}
	}

	is->stream_open(input_filename, file_iformat);
	if (!is) {
		av_log(NULL, AV_LOG_FATAL, "Failed to initialize VideoState!\n");

		is->do_exit();
	}

	is->event_loop();

	/* never returns */

	return 0;
}