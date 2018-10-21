#ifndef VIDEOSTATE_H_
#define VIDEOSTATE_H_

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

#define ENABLE_SHOW_STATUS 1
#define ENABLE_FAST_DECODE 0
// generate missing pts for audio if it means parsing future frames
#define ENABLE_GENERATE_PTS 0

enum {
	AV_SYNC_AUDIO_MASTER, /* default choice */
	AV_SYNC_VIDEO_MASTER,
	AV_SYNC_EXTERNAL_CLOCK, /* synchronize to an external clock */
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

static int				seek_by_bytes = 0; // seek by bytes 0=off 1=on -1=auto (Note: we disable seek_by_byte because it raises errors while seeking)
static int64_t			start_time = AV_NOPTS_VALUE; // initial start time
static int64_t			max_duration = AV_NOPTS_VALUE; // initial play time
static int				loop = 1; // loop through the video

//what will be the streamer in the future implementations
class VideoState {
private:
	int abort_request;
	bool paused; // TODO(fraudies): Check if this need to be atomic
	int last_paused;
	bool stopped; // TODO(fraudies): Check if this need to be atomic
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

	double vidclk_last_set_time;
	int video_stream;
	double max_frame_duration; // maximum duration of a frame - above this, we consider the jump a timestamp discontinuity
	int eof;
	int image_width;
	int image_height;
	double video_duration;
	AVRational image_sample_aspect_ratio;
	bool step; // TODO(fraudies): Check if this need to be atomic

	double new_rate_value;
	double rate_value;
	int new_rate_req;

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
	double audio_pts;
	int audio_serial;
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
	int muted;
	struct AudioParams audio_src;
	struct AudioParams audio_tgt;
	int frame_drops_early;

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

	inline void set_player_state_callback_func(PlayerStateCallback callback, const std::function<void()>& func) {
		player_state_callbacks[callback] = func;
	}

	inline void set_audio_open_callback(const std::function<int(int64_t, int, int, struct AudioParams*)>& func) {
		audio_open_callback = func;
	}

	inline void set_pause_audio_device_callback(const std::function<void()>& func) {
		pause_audio_device_callback = func;
	}

	inline void set_destroy_callback(const std::function<void()>& func) {
		destroy_callback = func;
	}

	inline void set_step_to_next_frame_callback(const std::function<void()>& func) {
		step_to_next_frame_callback = func;
	}

	/* Controls */
	void seek_chapter(int incr);
	inline int get_image_width() const { return image_width; }
	inline int get_image_height() const { return image_height; }
	inline AVRational get_image_sample_aspect_ratio() const { return image_sample_aspect_ratio; }
	inline bool has_audio_data() const { return last_audio_stream >= 0; }
	inline bool has_image_data() const { return last_video_stream >= 0; }
	inline double get_duration() const { return video_duration; }; // duration in sec
	inline double get_stream_time() const { return pVidclk->get_time(); } // current time in sec
	inline void toggle_mute() { muted = !muted; }
	void update_pts(double pts, int serial);
	void stream_seek(int64_t pos, int64_t rel, int seek_by_bytes);

	/* Setter and Getters */
	inline bool get_paused() const { return paused; }
	inline void set_paused(bool new_paused) { paused = new_paused; }

	inline bool get_stopped() const { return stopped; }
	inline void set_stopped(bool new_stopped) { stopped = new_stopped; }

	inline int get_step() const { return step; }
	inline void set_step(bool new_step) { step = new_step; }

	int set_rate(double new_rate);
	inline double get_rate() const { return rate_value; }

	inline int get_frame_drops_early() const { return frame_drops_early; }

	inline const char *get_filename() const { return filename; }

	inline AVStream *get_audio_st() const { return audio_st; }
	inline AVStream *get_video_st() const { return video_st; }
	inline AVStream *get_subtitle_st() const { return subtitle_st; }

	inline FrameQueue *get_pPictq() const { return pPictq; }
	inline FrameQueue *get_pSubpq() const { return pSubpq; }
	inline FrameQueue *get_pSampq() const { return pSampq; }

	inline PacketQueue *get_pVideoq() const { return pVideoq; }
	inline PacketQueue *get_pSubtitleq() const { return pSubtitleq; }
	inline PacketQueue *get_pAudioq() const { return pAudioq; }

	inline Clock *get_pVidclk() const { return pVidclk; }
	inline Clock *get_pAudclk() const { return pAudclk; }
	inline Clock *get_pExtclk() const { return pExtclk; }

	inline double get_vidclk_last_set_time() const { return vidclk_last_set_time; }

	inline AudioParams get_audio_tgt() const { return audio_tgt; }
	inline int get_muted() const { return muted; }

	inline Decoder *get_pViddec() const { return pViddec; }
	inline AVFormatContext *get_ic() const { return ic; }

	inline int64_t get_seek_pos() const { return seek_pos; }

	inline int get_video_stream() const { return video_stream; }
	inline int get_audio_stream() const { return audio_stream; }

	inline double get_max_frame_duration() const { return max_frame_duration; }

	inline int decode_interrupt_cb() const { return abort_request; }

	double compute_target_delay(double delay);

	/* get the current synchronization type */
	int get_master_sync_type() const;

	/* get the current master clock */
	Clock* get_master_clock() const;

	inline double get_fps() const { return video_st ? this->fps : 0; }

	/* prepare a new audio buffer */
	void audio_callback(uint8_t *stream, int len);

	inline int get_audio_disable() const { return audio_disable; }
	inline void set_audio_disable(const int disable) { audio_disable = disable; }

	inline int get_video_disable() const { return video_disable; }
	inline void set_video_disable(const int disable) { video_disable = disable; }

	inline int get_subtitle_disable() const { return subtitle_disable; }
	inline void set_subtitle_disable(const int disable) { subtitle_disable = disable; }
};

// Note, this bridge is necessary to interface with ffmpeg's call decode interrupt handle
static int decode_interrupt_cb_bridge(void *vs) {
	return static_cast<VideoState*>(vs)->decode_interrupt_cb();
}

#endif VIDEOSTATE_H_
