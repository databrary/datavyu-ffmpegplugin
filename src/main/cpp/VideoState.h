#ifndef VIDEOSTATE_H_
#define VIDEOSTATE_H_

#include <inttypes.h>
#include <math.h>
#include <limits.h>
#include <signal.h>
#include <stdint.h>

#include "Clock.h"
#include "PacketQueue.h"
#include "FrameQueue.h"
#include "Decoder.h"

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
static int audio_disable; // TODO(fraudies): Move this into video state
static int video_disable; // TODO(fraudies): Move this into video state
static int subtitle_disable; // TODO(fraudies): Move this into video state
static const char* wanted_stream_spec[AVMEDIA_TYPE_NB] = { 0 };
static int seek_by_bytes = -1;
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


class SDLPlayData;

//what will be the streamer in the future implementations
class VideoState {
private:
	SDLPlayData *pPlayer;
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

	int get_video_frame(AVFrame *frame);

	// TODO REVIEW THIS FUNCTION
	int queue_picture(AVFrame *src_frame, double pts, double duration, int64_t pos, int serial);

	void stream_component_close(int stream_index);

	int stream_has_enough_packets(AVStream *st, int stream_id, PacketQueue *queue);

	/* Ported this function from cmdutils */
	int check_stream_specifier(AVFormatContext *s, AVStream *st, const char *spec);

	/*ported this function from cmdutils*/
	AVDictionary *filter_codec_opts(AVDictionary *opts, enum AVCodecID codec_id, AVFormatContext *s,
		AVStream *st, AVCodec *codec);

	/* From cmd utils*/
	AVDictionary **setup_find_stream_info_opts(AVFormatContext *s, AVDictionary *codec_opts);

	int is_realtime(AVFormatContext *s);

	/* return the wanted number of samples to get better sync if sync_type is video
	* or external master clock */
	int synchronize_audio(int nb_samples);

	/**
	* Decode one audio frame and return its uncompressed size.
	*
	* The processed audio frame is decoded, converted if required, and
	* stored in is->audio_buf, with size in bytes given by the return
	* value.
	*/
	int audio_decode_frame();

public:
	VideoState();
	~VideoState();

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

	bool isPaused() const;

	int get_step() const;
	int get_frame_drops_early() const;

	AVStream *get_audio_st() const;
	AVStream *get_video_st() const;
	AVStream *get_subtitle_st() const;

	ShowMode get_show_mode() const;

	FrameQueue *get_pPictq() const;
	FrameQueue *get_pSubpq() const;
	FrameQueue *get_pSampq() const;

	PacketQueue *get_pVideoq() const;
	PacketQueue *get_pSubtitleq() const;
	PacketQueue *get_pAudioq() const;

	Clock *get_pVidclk() const;
	Clock *get_pAudclk() const;
	Clock *get_pExtclk() const;

	AudioParams get_audio_tgt();

	Decoder *get_pViddec();

	AVFormatContext *get_ic();

	int64_t get_seek_pos() const;

	int get_video_stream() const;
	int get_audio_stream() const;

	double get_max_frame_duration();
	
	int get_audio_write_buf_size() const;

	RDFTContext *get_rdft();
	void set_rdft(RDFTContext *newRDFT);

	int get_rdft_bits();
	void set_rdft_bits(int newRDF_bits);

	FFTSample *get_rdft_data();
	void set_rdft_data(FFTSample *newRDFT_data);

	void set_player(SDLPlayData *player);

	int get_realtime() const;

	inline int decode_interrupt_cb() const;

	double compute_target_delay(double delay);

	/* check the speed of the external clock */
	void check_external_clock_speed();

	/* get the current synchronization type */
	int get_master_sync_type();

	/* get the current master clock value */
	double get_master_clock();

	// Clean up memory
	// TODO: need to review this function 
	void stream_close();

	/* prepare a new audio buffer */
	void sdl_audio_callback(Uint8 *stream, int len);
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

#endif VIDEOSTATE_H_
