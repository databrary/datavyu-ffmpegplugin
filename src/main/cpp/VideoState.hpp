#include <inttypes.h>
#include <math.h>
#include <limits.h>
#include <signal.h>
#include <stdint.h>

#include "Clock.hpp"
#include "PacketQueue.hpp"
#include "FrameQueue.hpp"
#include "Decoder.hpp"

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
//Could be moved to ffplay.hpp 
#include <SDL2/SDL.h>
#include <SDL2/SDL_thread.h>

#include <assert.h>
}

#ifndef VIDEOSTATE_H_
#define VIDEOSTATE_H_

/* Minimum SDL audio buffer size, in samples. */
#define SDL_AUDIO_MIN_BUFFER_SIZE 512

#define MAX_QUEUE_SIZE (15 * 1024 * 1024)
#define MIN_FRAMES 25
#define EXTERNAL_CLOCK_MIN_FRAMES 2
#define EXTERNAL_CLOCK_MAX_FRAMES 10

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

enum ShowMode {
	SHOW_MODE_NONE = -1,
	SHOW_MODE_VIDEO = 0,
	SHOW_MODE_WAVES,
	SHOW_MODE_RDFT,
	SHOW_MODE_NB
};

/* options specified by the user */

static ShowMode show_mode = SHOW_MODE_NONE;
static AVInputFormat *file_iformat;
static const char *input_filename;
static const char *window_title;
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
static int av_sync_type_input = AV_SYNC_AUDIO_MASTER;
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
static const char *audio_codec_name;
static const char *subtitle_codec_name;
static const char *video_codec_name;
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
static int64_t audio_callback_time;


class ffplay;

//what will be the streamer in the future implementations
class VideoState {
private:
	ffplay *pPlayer;
	std::thread *read_tid;
	AVInputFormat *iformat;
	int abort_request;
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

	Decoder *pAuddec;
	Decoder *pViddec;
	Decoder *pSubdec;

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

	RDFTContext *rdft;
	int rdft_bits;
	FFTSample *rdft_data;

	int subtitle_stream;
	AVStream *subtitle_st;
	PacketQueue *pSubtitleq;

	double frame_last_returned_time;
	double frame_last_filter_delay;
	int video_stream;
	AVStream *video_st;
	PacketQueue *pVideoq;
	double max_frame_duration;      // maximum duration of a frame - above this, we consider the jump a timestamp discontinuity
	int eof;

	char *filename;

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

	std::condition_variable continue_read_thread;

	/* open a given stream. Return 0 if OK */
	int stream_component_open(int stream_index);

	int get_video_frame(AVFrame *frame) {
		int got_picture;

		if ((got_picture = pViddec->decode_frame(frame, NULL)) < 0)
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
						pViddec->get_pkt_serial() == pVidclk->get_serial() &&
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

	// TODO REVIEW THIS FUNCTION
	int queue_picture(AVFrame *src_frame, double pts, double duration, int64_t pos, int serial) {
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

		pPlayer->set_default_window_size(vp->width, vp->height, vp->sar);

		av_frame_move_ref(vp->frame, src_frame);
		pPictq->push();
		return 0;
	}

	void stream_component_close(int stream_index) {
		AVFormatContext *ic = this->ic;
		AVCodecParameters *codecpar;

		if (stream_index < 0 || stream_index >= ic->nb_streams)
			return;
		codecpar = ic->streams[stream_index]->codecpar;

		switch (codecpar->codec_type) {
		case AVMEDIA_TYPE_AUDIO:
			pAuddec->abort(pSampq);
			pPlayer->closeAudioDevice();
			delete pAuddec; // TODO(fraudies): Move this to the destructor of VideoState
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
			pViddec->abort(pPictq);
			delete pViddec; // TODO(fraudies): Move this to the destructor of VideoState
			break;
		case AVMEDIA_TYPE_SUBTITLE:
			pSubdec->abort(pSubpq);
			delete pSubdec; // TODO(fraudies): Move this to the destructor of VideoState
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

	int stream_has_enough_packets(AVStream *st, int stream_id, PacketQueue *queue) {
		return stream_id < 0 ||
			queue->is_abort_request() ||
			(st->disposition & AV_DISPOSITION_ATTACHED_PIC) ||
			queue->get_nb_packets() > MIN_FRAMES && (!queue->get_duration() || av_q2d(st->time_base) * queue->get_duration() > 1.0);
	}

	/* Ported this function from cmdutils */
	int check_stream_specifier(AVFormatContext *s, AVStream *st, const char *spec) {
		int ret = avformat_match_stream_specifier(s, st, spec);
		if (ret < 0)
			av_log(s, AV_LOG_ERROR, "Invalid stream specifier: %s.\n", spec);
		return ret;
	}

	/*ported this function from cmdutils*/
	AVDictionary *filter_codec_opts(
		AVDictionary *opts,
		enum AVCodecID codec_id,
		AVFormatContext *s,
		AVStream *st,
		AVCodec *codec)	{
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
	AVDictionary **setup_find_stream_info_opts(AVFormatContext *s, AVDictionary *codec_opts) {
		int i;
		AVDictionary **opts;

		if (!s->nb_streams)
			return NULL;
		opts = (AVDictionary**)av_mallocz_array(s->nb_streams, sizeof(*opts));
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

	int is_realtime(AVFormatContext *s) {
		if (!strcmp(s->iformat->name, "rtp")
			|| !strcmp(s->iformat->name, "rtsp")
			|| !strcmp(s->iformat->name, "sdp"))
			return 1;

		if (s->pb && (!strncmp(s->url, "rtp:", 4) || !strncmp(s->url, "udp:", 4)))
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
#ifdef DEBUG
		{
			static double last_clock;
			printf("audio: delay=%0.3f clock=%0.3f clock0=%0.3f\n",
				this->audio_clock - last_clock,
				this->audio_clock, audio_clock0);
			last_clock = this->audio_clock;
		}
#endif
		return resampled_data_size;
	}

public:
	VideoState() :
		pVideoq(new PacketQueue()),
		pAudioq(new PacketQueue()),
		pSubtitleq(new PacketQueue()),
		pPictq(nullptr),
		pSubpq(nullptr),
		pSampq(nullptr),
		pVidclk(nullptr),
		pAudclk(nullptr),
		pExtclk(new Clock()), // For the external clock the serial is set to itself (never triggers)
		pViddec(nullptr), // Decoder's are created in the stream_component_open and destroyed in stream_component_close
		pSubdec(nullptr),
		pAuddec(nullptr) {
		// Frame queues depend on the packet queues that have not been initialized in initializer
		pPictq = new FrameQueue(pVideoq, VIDEO_PICTURE_QUEUE_SIZE, 1);
		pSubpq = new FrameQueue(pSubtitleq, SUBPICTURE_QUEUE_SIZE, 0);
		pSampq = new FrameQueue(pAudioq, SAMPLE_QUEUE_SIZE, 1);
		// Clocks depend on packet queues that have not been initialized in initializer
		pVidclk = new Clock(pVideoq->get_p_serial());
		pAudclk = new Clock(pAudioq->get_p_serial());
		// TODO(fraudies): Define these attributes in the right order in the class, then move all back to the initializers
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
		// Note, that the decoders get freed in the stream close function
	}

	int read_thread();
	int audio_thread();
	int video_thread();
	int subtitle_thread();

	static VideoState *stream_open(const char *filename, AVInputFormat *iformat);

	/* Controls */

	void seek_chapter(int incr);
	void stream_toggle_pause();
	//Moved to ffplay.cpp
	//void toggle_full_screen();
	void toggle_pause();
	void toggle_mute();
	void update_volume(int sign, double step);
	void step_to_next_frame();
	void stream_seek(int64_t pos, int64_t rel, int seek_by_bytes);
	void stream_cycle_channel(int codec_type);
	void toggle_audio_display();

	/* Setter and Getters */

	bool isPaused() const { return paused; }

	int get_step() const { return step; }
	int get_frame_drops_early() const { return frame_drops_early; }

	AVStream *get_audio_st() const { return audio_st; }
	AVStream *get_video_st() const { return video_st; }
	AVStream *get_subtitle_st() const { return subtitle_st; }

	ShowMode get_show_mode() const { return show_mode; }

	FrameQueue *get_pPictq() const { return pPictq; }
	FrameQueue *get_pSubpq() const { return pSubpq; }
	FrameQueue *get_pSampq() const { return pSampq; }

	PacketQueue *get_pVideoq() const { return pVideoq; }
	PacketQueue *get_pSubtitleq() const { return pSubtitleq; }
	PacketQueue *get_pAudioq() const { return pAudioq; }

	Clock *get_pVidclk() const { return pVidclk; }
	Clock *get_pAudclk() const { return pAudclk; }
	Clock *get_pExtclk() const { return pExtclk; }

	AudioParams get_audio_tgt() { return audio_tgt; }

	Decoder *get_pViddec() { return pViddec; }

	AVFormatContext *get_ic() { return ic; }

	int64_t get_seek_pos() const { return seek_pos; }

	int get_video_stream() const { return video_stream; }
	int get_audio_stream() const { return audio_stream; }

	double get_max_frame_duration() { return max_frame_duration; }
	
	int get_audio_write_buf_size() const { return audio_write_buf_size; }

	RDFTContext *get_rdft() { return rdft; }
	void set_rdft(RDFTContext *newRDFT) { rdft = newRDFT; }

	int get_rdft_bits() { return rdft_bits; }
	void set_rdft_bits(int newRDF_bits) { rdft_bits = newRDF_bits; }

	FFTSample *get_rdft_data() { return rdft_data; }
	void set_rdft_data(FFTSample *newRDFT_data) { rdft_data = newRDFT_data; }

	void set_player(ffplay *player) { pPlayer = player;  }

	int get_realtime() const { return realtime; }

	inline int decode_interrupt_cb() const {
		return abort_request;
	}

	double compute_target_delay(double delay) {
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

	/* check the speed of the external clock */
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
			double speed = pExtclk->get_clock_speed();
			if (speed != 1.0)
				pExtclk->set_clock_speed(speed + EXTERNAL_CLOCK_SPEED_STEP * (1.0 - speed) / fabs(1.0 - speed));
		}
	}

	/* get the current synchronization type */
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
	double get_master_clock() {
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

	// Clean up memory
	// TODO: need to review this function 
	void stream_close() {
		/* XXX: use a special url_shutdown call to abort parse cleanly */
		this->abort_request = 1;
		read_tid->join();
		delete read_tid;
		read_tid = nullptr;

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
		//SDL_DestroyCond(this->continue_read_thread);
		//delete this->continue_read_thread; // TODO(fraudies): remove allocation & deallocation
		
		//Moved to the destructor of ffplay.cpp
		//sws_freeContext(this->img_convert_ctx);
		//sws_freeContext(this->sub_convert_ctx);
		av_free(this->filename);
		pPlayer->destroyTextures();
	}

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
						pPlayer->update_sample_display((int16_t *)this->audio_buf, audio_size);
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

/* Controls */
void VideoState::stream_toggle_pause() {
	if (this->paused) {
		pPlayer->set_frame_timer(pPlayer->get_frame_timer() + av_gettime_relative() / 1000000.0 - pVidclk->get_lastUpdated());
		if (this->read_pause_return != AVERROR(ENOSYS)) {
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

void VideoState::toggle_pause() {
	this->stream_toggle_pause();
	this->step = 0;
}

void VideoState::toggle_mute() { this->muted = !this->muted; }

void VideoState::update_volume(int sign, double step) {
	double volume_level = this->audio_volume ? (20 * log(this->audio_volume / (double)SDL_MIX_MAXVOLUME) / log(10)) : -1000.0;
	int new_volume = lrint(SDL_MIX_MAXVOLUME * pow(10.0, (volume_level + sign * step) / 20.0));
	this->audio_volume = av_clip(this->audio_volume == new_volume ? (this->audio_volume + sign) : new_volume, 0, SDL_MIX_MAXVOLUME);
}

void VideoState::step_to_next_frame() {
	/* if the stream is paused unpause it, then step */
	if (this->paused)
		this->stream_toggle_pause();
	this->step = 1;
}

/* seek in the stream */
void VideoState::stream_seek(int64_t pos, int64_t rel, int seek_by_bytes) {
	if (!this->seek_req) {
		this->seek_pos = pos;
		this->seek_rel = rel;
		this->seek_flags &= ~AVSEEK_FLAG_BYTE;
		if (seek_by_bytes)
			this->seek_flags |= AVSEEK_FLAG_BYTE;
		this->seek_req = 1;
		continue_read_thread.notify_one();
	}
}

// TODO(fraudies): Can we remove this? This could help simplify the memory management
void VideoState::stream_cycle_channel(int codec_type) {
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

void VideoState::toggle_audio_display() {
	int next = show_mode;
	do {
		next = (next + 1) % SHOW_MODE_NB;
	} while (next != show_mode && (next == SHOW_MODE_VIDEO && !this->video_st || next != SHOW_MODE_VIDEO && !this->audio_st));
	if (show_mode != next) {
		pPlayer->set_force_refresh(1);
		show_mode = static_cast<ShowMode>(next);
	}
}

// Function Called from the event loop
void VideoState::seek_chapter(int incr) {
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

VideoState *VideoState::stream_open(const char *filename, AVInputFormat *iformat) {
	VideoState *is = new VideoState();

	//is = (VideoState*) av_mallocz(sizeof(VideoState));
	if (!is)
		return NULL;
	is->filename = av_strdup(filename);
	if (!is->filename)
		goto fail;
	is->iformat = iformat;

	//if (!(is->continue_read_thread = new std::condition_variable())) {
	//	av_log(NULL, AV_LOG_FATAL, "SDL_CreateCond(): %s\n", SDL_GetError());
	//	goto fail;
	//}

	is->audio_clock_serial = -1;
	if (startup_volume < 0)
		av_log(NULL, AV_LOG_WARNING, "-volume=%d < 0, setting to 0\n", startup_volume);
	if (startup_volume > 100)
		av_log(NULL, AV_LOG_WARNING, "-volume=%d > 100, setting to 100\n", startup_volume);
	startup_volume = av_clip(startup_volume, 0, 100);
	startup_volume = av_clip(SDL_MIX_MAXVOLUME * startup_volume / 100, 0, SDL_MIX_MAXVOLUME);
	is->audio_volume = startup_volume;
	is->muted = 0;
	is->av_sync_type = av_sync_type_input;
	is->read_tid = new std::thread(read_thread_bridge, is);
	if (!is->read_tid) {
		av_log(NULL, AV_LOG_FATAL, "Unable to create reader thread\n");
	fail:
		is->stream_close();
		return NULL;
	}

	return is;
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
	std::mutex wait_mutex;
	int scan_all_pmts_set = 0;
	int64_t pkt_ts;

	AVDictionary *format_opts = NULL;
	AVDictionary *codec_opts = NULL;

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
	ic->interrupt_callback.callback = decode_interrupt_cb_bridge;
	ic->interrupt_callback.opaque = this;
	if (!av_dict_get(format_opts, "scan_all_pmts", NULL, AV_DICT_MATCH_CASE)) {
		av_dict_set(&format_opts, "scan_all_pmts", "1", AV_DICT_DONT_OVERWRITE);
		scan_all_pmts_set = 1;
	}
	err = avformat_open_input(&ic, this->filename, this->iformat, &format_opts);
	if (err < 0) {
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
			pPlayer->set_default_window_size(codecpar->width, codecpar->height, sar);
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
		if (this>paused &&
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
					pAudioq->put_flush_packet();
				}
				if (this->subtitle_stream >= 0) {
					pSubtitleq->flush();
					pSubtitleq->put_flush_packet();
				}
				if (this->video_stream >= 0) {
					pVideoq->flush();
					pVideoq->put_flush_packet();
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
			std::unique_lock<std::mutex> locker(wait_mutex);
			continue_read_thread.wait_for(locker, std::chrono::milliseconds(10));
			locker.unlock();
			continue;
		}
		if (!this->paused &&
			(!this->audio_st || (pAuddec->is_finished() == pAudioq->get_serial() && pSampq->nb_remaining() == 0)) &&
			(!this->video_st || (pViddec->is_finished() == pVideoq->get_serial() && pPictq->nb_remaining() == 0))) {
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
			/* wait 10 ms */
			std::unique_lock<std::mutex> locker(wait_mutex);
			continue_read_thread.wait_for(locker, std::chrono::milliseconds(10));
			locker.unlock();
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
		ffplay::do_exit(this);
	}
	return 0;
}

int VideoState::audio_thread() {
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
		if ((got_frame = pAuddec->decode_frame(frame, NULL)) < 0)
			goto the_end;

		if (got_frame) {
			tb = av_make_q(1, frame->sample_rate);

#if CONFIG_AVFILTER
			dec_channel_layout = get_valid_channel_layout(frame->channel_layout, frame->channels);

			reconfigure =
				cmp_audio_fmts(is->audio_filter_src.fmt, audio_filter_src.channels,
					frame->format, frame->channels) ||
				audio_filter_src.channel_layout != dec_channel_layout ||
				audio_filter_src.freq != frame->sample_rate ||
				auddec.pkt_serial != last_serial;

			if (reconfigure) {
				char buf1[1024], buf2[1024];
				av_get_channel_layout_string(buf1, sizeof(buf1), -1, audio_filter_src.channel_layout);
				av_get_channel_layout_string(buf2, sizeof(buf2), -1, dec_channel_layout);
				av_log(NULL, AV_LOG_DEBUG,
					"Audio frame changed from rate:%d ch:%d fmt:%s layout:%s serial:%d to rate:%d ch:%d fmt:%s layout:%s serial:%d\n",
					audio_filter_src.freq, audio_filter_src.channels, av_get_sample_fmt_name(audio_filter_src.fmt), buf1, last_serial,
					frame->sample_rate, frame->channels, av_get_sample_fmt_name(frame->format), buf2, auddec.pkt_serial);

				audio_filter_src.fmt = frame->format;
				audio_filter_src.channels = frame->channels;
				audio_filter_src.channel_layout = dec_channel_layout;
				audio_filter_src.freq = frame->sample_rate;
				last_serial = auddec.pkt_serial;

				if ((ret = configure_audio_filters(this, afilters, 1)) < 0)
					goto the_end;
			}

			if ((ret = av_buffersrc_add_frame(in_audio_filter, frame)) < 0)
				goto the_end;

			while ((ret = av_buffersink_get_frame_flags(out_audio_filter, frame, 0)) >= 0) {
				tb = av_buffersink_get_time_base(out_audio_filter);
#endif
				if (!(af = pSampq->peek_writable()))
					goto the_end;

				af->pts = (frame->pts == AV_NOPTS_VALUE) ? NAN : frame->pts * av_q2d(tb);
				af->pos = frame->pkt_pos;
				af->serial = this->pAuddec->get_pkt_serial();
				af->duration = av_q2d(av_make_q(frame->nb_samples, frame->sample_rate));

				av_frame_move_ref(af->frame, frame);
				pSampq->push();

#if CONFIG_AVFILTER
				if (audioq.serial != auddec.pkt_serial)
					break;
			}
			if (ret == AVERROR_EOF)
				auddec.finished = auddec.pkt_serial;
#endif
		}
	} while (ret >= 0 || ret == AVERROR(EAGAIN) || ret == AVERROR_EOF);
the_end:
#if CONFIG_AVFILTER
	avfilter_graph_free(&agraph);
#endif
	av_frame_free(&frame);
	return ret;
}

int VideoState::video_thread() {
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
			|| last_serial != viddec.pkt_serial
			|| last_vfilter_idx != vfilter_idx) {
			av_log(NULL, AV_LOG_DEBUG,
				"Video frame changed from size:%dx%d format:%s serial:%d to size:%dx%d format:%s serial:%d\n",
				last_w, last_h,
				(const char *)av_x_if_null(av_get_pix_fmt_name(last_format), "none"), last_serial,
				frame->width, frame->height,
				(const char *)av_x_if_null(av_get_pix_fmt_name(frame->format), "none"), viddec.pkt_serial);
			avfilter_graph_free(&graph);
			graph = avfilter_graph_alloc();
			if ((ret = configure_video_filters(graph, this, vfilters_list ? vfilters_list[vfilter_idx] : NULL, frame)) < 0) {
				ffplay::do_exit(this);
				goto the_end;
			}
			filt_in = in_video_filter;
			filt_out = out_video_filter;
			last_w = frame->width;
			last_h = frame->height;
			last_format = frame->format;
			last_serial = viddec.pkt_serial;
			last_vfilter_idx = vfilter_idx;
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
					viddec.finished = viddec.pkt_serial;
				ret = 0;
				break;
			}

			frame_last_filter_delay = av_gettime_relative() / 1000000.0 - frame_last_returned_time;
			if (fabs(frame_last_filter_delay) > AV_NOSYNC_THRESHOLD / 10.0)
				frame_last_filter_delay = 0;
			tb = av_buffersink_get_time_base(filt_out);
#endif
			duration = (frame_rate.num && frame_rate.den) ? av_q2d(av_make_q(frame_rate.den, frame_rate.num)) : 0;
			pts = (frame->pts == AV_NOPTS_VALUE) ? NAN : frame->pts * av_q2d(tb);
			ret = queue_picture(frame, pts, duration, frame->pkt_pos, pViddec->get_pkt_serial());
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

int VideoState::subtitle_thread() {
	Frame *sp;
	int got_subtitle;
	double pts;

	for (;;) {
		if (!(sp = pSubpq->peek_writable()))
			return 0;

		if ((got_subtitle = pSubdec->decode_frame(NULL, &sp->sub)) < 0)
			break;

		pts = 0;

		if (got_subtitle && sp->sub.format == 0) {
			if (sp->sub.pts != AV_NOPTS_VALUE)
				pts = sp->sub.pts / (double)AV_TIME_BASE;
			sp->pts = pts;
			sp->serial = pSubdec->get_pkt_serial();
			sp->width = pSubdec->get_avctx()->width;
			sp->height = pSubdec->get_avctx()->height;
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
		if ((ret = ffplay::audio_open(this, channel_layout, nb_channels, sample_rate, &this->audio_tgt)) < 0)
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
		pAuddec = new Decoder(avctx, pAudioq, &continue_read_thread);
		//pAuddec->init(avctx, pAudioq, &continue_read_thread);
		//decoder_init(&this->auddec, avctx, pAudioq, this->continue_read_thread);
		if ((this->ic->iformat->flags & (AVFMT_NOBINSEARCH | AVFMT_NOGENSEARCH | AVFMT_NO_BYTE_SEEK)) && !this->ic->iformat->read_seek) {
			pAuddec->set_start_pts(this->audio_st->start_time);
			pAuddec->set_start_pts_tb(this->audio_st->time_base);
		}
		if ((ret = pAuddec->start(audio_thread_bridge, this)) < 0)
			goto out;
		pPlayer->pauseAudioDevice();
		break;
	case AVMEDIA_TYPE_VIDEO:
		this->video_stream = stream_index;
		this->video_st = ic->streams[stream_index];
		pViddec = new Decoder(avctx, pVideoq, &continue_read_thread);
		//pViddec->init(avctx, pVideoq, &continue_read_thread);
		//decoder_init(&this->viddec, avctx, pVideoq, this->continue_read_thread);
		if ((ret = pViddec->start(video_thread_bridge, this)) < 0)
			goto out;
		this->queue_attachments_req = 1;
		break;
	case AVMEDIA_TYPE_SUBTITLE:
		this->subtitle_stream = stream_index;
		this->subtitle_st = ic->streams[stream_index];
		pSubdec = new Decoder(avctx, pSubtitleq, &continue_read_thread);
		//decoder_init(&this->subdec, avctx, this->pSubtitleq, this->continue_read_thread);
		//pSubdec->init(avctx, pSubtitleq, &continue_read_thread);
		if ((ret = pSubdec->start(subtitle_thread_bridge, this)) < 0)
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

#endif VIDEOSTATE_H_
