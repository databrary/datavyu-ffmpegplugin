#ifndef VIDEOSTATE_H_
#define VIDEOSTATE_H_

#define CONFIG_AUDIO_FILTER 0
#define CONFIG_VIDEO_FILTER 0

#include <inttypes.h>
#include <math.h>
#include <limits.h>
#include <signal.h>
#include <stdint.h>

#include "AudioVideoFormats.h"
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

	#if CONFIG_AUDIO_FILTER || CONFIG_VIDEO_FILTER
		# include "libavfilter/avfilter.h"
		# include "libavfilter/buffersink.h"
		# include "libavfilter/buffersrc.h"
	#endif

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

// fixed point to double
#define CONV_FP(x) ((double) (x)) / (1 << 16)

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

enum PlayerStateCallback {
	TO_UNKNOWN = 0,		// 1
	TO_READY,			// 2
	TO_PLAYING,			// 3
	TO_PAUSED,			// 4
	TO_STOPPED,			// 5
	TO_STALLED,			// 6
	TO_FINISHED,		// 7
	NUM_PLAYER_STATE_CALLBACKS
};

static ShowMode			show_mode = SHOW_MODE_NONE;
static const char		*window_title;
static const char		*wanted_stream_spec[AVMEDIA_TYPE_NB] = { 0 };
static int				seek_by_bytes = 0; // seek by bytes 0=off 1=on -1=auto (Note: we disable seek_by_byte because it raises errors while seeking)
static int				borderless;
static int				show_status = 1;
static int				av_sync_type_input = AV_SYNC_AUDIO_MASTER;
static int64_t			start_time = AV_NOPTS_VALUE;
static int64_t			duration = AV_NOPTS_VALUE;
static int				fast = 0;
static int				genpts = 0;
static int				lowres = 0;
static int				decoder_reorder_pts = -1;
static int				autoexit = 0; // No auto exit
static int				exit_on_keydown;
static int				exit_on_mousedown;
static int				loop = 1;
static int				framedrop = -1;
static int				infinite_buffer = -1;
static const char		*audio_codec_name;
static const char		*subtitle_codec_name;
static const char		*video_codec_name;
static int64_t			cursor_last_shown;
static int				cursor_hidden = 0;
static int				autorotate = 1;
static int				find_stream_info = 1;

/* current context */
static int64_t			audio_callback_time;

//what will be the streamer in the future implementations
class VideoState {
private:
	int abort_request;
	bool paused; // TODO(fraudies): Check if this need to be atomic
	int last_paused;
	bool stopped; // TODO(fraudies): Need atomic here for thread safety
	int queue_attachments_req;
	int seek_req;
	int seek_flags;
	int64_t	seek_pos;
	int64_t seek_rel;
	int	read_pause_return;
	int realtime;
	int	av_sync_type;
	int fps;
	int subtitle_stream;

	double frame_last_returned_time;
	double frame_last_filter_delay;
	int video_stream;
	double max_frame_duration;      // maximum duration of a frame - above this, we consider the jump a timestamp discontinuity
	int eof;
	int image_width;
	int image_height;
	AVRational image_sample_aspect_ratio;
	bool step; // TODO(fraudies): Check if this need to be atomic

	double rate;
	int new_rate_req;
	double pts_speed;

	int audio_disable;
	int video_disable;
	int subtitle_disable;

	int last_video_stream;
	int last_audio_stream;
	int last_subtitle_stream;

	std::condition_variable continue_read_thread;

	char *filename;

	PacketQueue *pAudioq;
	PacketQueue	*pVideoq;
	PacketQueue *pSubtitleq;

	FrameQueue *pSampq;
	FrameQueue *pPictq;
	FrameQueue *pSubpq;

	Clock *pAudclk;
	Clock *pVidclk;
	Clock *pExtclk;

	Decoder *pAuddec;
	Decoder *pViddec;
	Decoder *pSubdec;

	std::thread *read_tid;
	AVInputFormat *iformat;
	AVFormatContext	*ic;
	struct SwrContext *swr_ctx;

	AVStream *audio_st;
	AVStream *subtitle_st;
	AVStream *video_st;

	int audio_stream;
	double audio_clock;
	int audio_clock_serial;
	double audio_diff_cum; /* used for AV difference average computation */
	double audio_diff_avg_coef;
	double audio_diff_threshold;
	int audio_diff_avg_count;
	int	audio_hw_buf_size;
	int audio_buffer_size;
	uint8_t *audio_buf;
	uint8_t *audio_buf1;
	unsigned int audio_buf_size; /* in bytes */
	unsigned int audio_buf1_size;
	int audio_buf_index; /* in bytes */
	int	audio_write_buf_size;
	//int	audio_volume;
	int muted;
	struct AudioParams audio_src;

#if CONFIG_AUDIO_FILTER
	struct AudioParams audio_filter_src;
#endif

	struct AudioParams audio_tgt;
	int frame_drops_early;

	RDFTContext *rdft;
	int rdft_bits;
	FFTSample *rdft_data;

#if CONFIG_AUDIO_FILTER
	char *afilters = NULL;	// audio filter Note: audio filter is not working
	AVFilterContext *in_audio_filter;   // the first filter in the audio chain
	AVFilterContext *out_audio_filter;  // the last filter in the audio chain
	AVFilterGraph *agraph;            // audio filter graph
#endif

#if CONFIG_VIDEO_FILTER
	int vfilter_idx;
	const char **vfilters_list = NULL;
	char *vfilters = NULL;	// video filter
	int nb_vfilters = 0;

	AVFilterContext *in_video_filter;   // the first filter in the video chain
	AVFilterContext *out_video_filter;  // the last filter in the video chain
	
	AVDictionary *sws_dict;			// From cmdutils
	AVDictionary *swr_opts;			// From cmdutils
	std::mutex mutex;				// From cmdutils
#endif

	inline int cmp_audio_fmts(enum AVSampleFormat fmt1, int64_t channel_count1,
			enum AVSampleFormat fmt2, int64_t channel_count2) {
		/* If channel count == 1, planar and non-planar formats are the same */
		return (channel_count1 == 1 && channel_count2 == 1)
				? av_get_packed_sample_fmt(fmt1) != av_get_packed_sample_fmt(fmt2)
				: channel_count1 != channel_count2 || fmt1 != fmt2;
	}

	inline int64_t get_valid_channel_layout(int64_t channel_layout, int channels) {
		return (channel_layout && av_get_channel_layout_nb_channels(channel_layout) == channels)
					? channel_layout : 0;
	}

	double get_rotation(AVStream * st);
	double av_display_rotation_get(const int32_t matrix[9]);

	/* open a given stream. Return 0 if OK */
	int stream_component_open(int stream_index);
	int get_video_frame(AVFrame *frame);

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

	std::function<void()> player_state_callbacks[NUM_PLAYER_STATE_CALLBACKS];
	// int audio_open(int64_t wanted_channel_layout, int wanted_nb_channels, int wanted_sample_rate,
	//struct AudioParams *audio_hw_params)
	std::function<int(int64_t, int, int, struct AudioParams*)> audio_open_callback;
	std::function<void()> pause_audio_device_callback;
	std::function<void()> destroy_callback; // TODO(fraudies): Possibly clean-up through destructor
	std::function<void()> step_to_next_frame_callback;
	VideoState(int audio_buffer_size);
public:
	static VideoState* create_video_state(int audio_buffer_size);

	~VideoState();

	int read_thread();
	int audio_thread();
	int video_thread();
	int subtitle_thread();
	int stream_start();
	static VideoState *stream_open(const char *filename, AVInputFormat *iformat, int audio_buffer_size);

	void set_player_state_callback_func(PlayerStateCallback callback, const std::function<void()>& func);
	void set_audio_open_callback(const std::function<int(int64_t, int, int, struct AudioParams*)>& func);
	void set_pause_audio_device_callback(const std::function<void()>& func);
	void set_destroy_callback(const std::function<void()>& func);
	void set_step_to_next_frame_callback(const std::function<void()>& func);

	/* Controls */
	void seek_chapter(int incr);
	int get_image_width() const; // height as coming from stream
	int get_image_height() const; // width as coming from stream
	AVRational get_image_sample_aspect_ratio() const;
	bool has_audio_data() const;
	bool has_image_data() const;
	double get_duration() const; // returns the duration in sec
	//int get_audio_volume() const;
	//void set_audio_volume(int new_audio_volume);
	void toggle_mute();
	void update_pts(double pts, int64_t pos, int serial);
	void stream_seek(int64_t pos, int64_t rel, int seek_by_bytes);
	void stream_cycle_channel(int codec_type);
	int get_read_pause_return() const;

	/* Setter and Getters */
	bool get_paused() const;
	void set_paused(bool new_paused);

	bool get_stopped() const;
	void set_stopped(bool new_stopped);

	int get_step() const;
	void set_step(bool new_step);

	int get_frame_drops_early() const;

	const char *get_filename() const;

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

	//AudioFormat get_audio_format() const;
	AudioParams get_audio_tgt() const;
	inline int get_muted() const { return muted; }

	Decoder *get_pViddec();
	AVFormatContext *get_ic() const;

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

	int get_realtime() const;
	inline int decode_interrupt_cb() const;

	double compute_target_delay(double delay);

	/* check the speed of the external clock */
	void check_external_clock_speed();

	/* get the current synchronization type */
	int get_master_sync_type() const;

	/* get the current master clock value */
	double get_master_clock() const;

	double get_fps() const;
	double get_pts_speed() const;

	void stream_close();

	/* prepare a new audio buffer */
	void audio_callback(uint8_t *stream, int len);

	void set_rate(double rate);
	double get_rate() const;

	int get_master_clock_speed();

	int get_audio_disable() const;
	void set_audio_disable(const int disable);

	int get_video_disable() const;
	void set_video_disable(const int disable);

	int get_subtitle_disable() const;
	void set_subtitle_disable(const int disable);

#if CONFIG_VIDEO_FILTER
	int configure_video_filters(AVFilterGraph * graph, const char * vfilters, AVFrame * frame);
	int get_vfilter_idx();
	void set_vfilter_idx(int idx);

	int get_nb_vfilters() const;

	int opt_add_vfilter(const char *arg);
#endif

#if CONFIG_AUDIO_FILTER
	int configure_filtergraph(AVFilterGraph * graph, const char * filtergraph, AVFilterContext * source_ctx, AVFilterContext * sink_ctx);
	int configure_audio_filters(const char * afilters, int force_output_format);
	void *grow_array(void *array, int elem_size, int *size, int new_size);
#endif
};

// Note, this bridge is necessary to interface with ffmpeg's call decode interrupt handle
static int decode_interrupt_cb_bridge(void *vs) {
	return static_cast<VideoState*>(vs)->decode_interrupt_cb();
}

#endif VIDEOSTATE_H_
