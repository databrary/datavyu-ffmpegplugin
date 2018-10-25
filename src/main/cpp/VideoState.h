#ifndef VIDEOSTATE_H_
#define VIDEOSTATE_H_

#include <inttypes.h>
#include <limits.h>
#include <math.h>
#include <signal.h>
#include <stdint.h>

#include "AudioVideoFormats.h"
#include "Clock.h"
#include "Decoder.h"
#include "FrameQueue.h"
#include "PacketQueue.h"

extern "C" {
#include "libavcodec/avfft.h"
#include "libavdevice/avdevice.h"
#include "libavformat/avformat.h"
#include "libavutil/avassert.h"
#include "libavutil/avstring.h"
#include "libavutil/dict.h"
#include "libavutil/eval.h"
#include "libavutil/imgutils.h"
#include "libavutil/log.h"
#include "libavutil/mathematics.h"
#include "libavutil/opt.h"
#include "libavutil/parseutils.h"
#include "libavutil/pixdesc.h"
#include "libavutil/samplefmt.h"
#include "libavutil/time.h"
#include "libswresample/swresample.h"
#include "libswscale/swscale.h"
#include <assert.h>
}

// Video state holds the state of the video player for both
// the image stream and audio stream
// It also provides an interface to change that state, e.g.
// playing, stopping, pausing the playback
//
// TODO(fraudies): Encapsulate audio and image stream decoding
class VideoState {
public:
  enum AvSyncType {
    AV_SYNC_AUDIO_MASTER, /* default choice */
    AV_SYNC_VIDEO_MASTER,
    AV_SYNC_EXTERNAL_CLOCK, /* synchronize to an external clock */
  };
  enum PlayerStateCallback {
    TO_UNKNOWN = 0, // 1
    TO_READY,       // 2
    TO_PLAYING,     // 3
    TO_PAUSED,      // 4
    TO_STOPPED,     // 5
    TO_STALLED,     // 6
    TO_FINISHED,    // 7
    NUM_PLAYER_STATE_CALLBACKS
  };

private:
  bool abort_request_;
  bool is_paused_; // TODO(fraudies): Check if this need to be atomic
  bool last_is_paused_;
  bool is_stopped_; // TODO(fraudies): Check if this need to be atomic
  bool queue_attachments_request_;

  bool seek_request_;
  int seek_flags_;
  int64_t seek_time_;
  int64_t seek_distance_; // Signed distance between the current time and the
                          // seek time
  AvSyncType sync_type_;  // default is AV_SYNC_AUDIO_MASTER
  int frame_rate_;        // Frame rate in Hz (frames per second)

  double video_clock_last_set_time_;
  int image_stream_index_;
  double max_frame_duration_; // maximum duration of a frame - above this, we
                              // consider the jump a timestamp discontinuity
  bool end_of_file_;
  int frame_width_;
  int frame_height_;
  double duration_;
  AVRational frame_aspect_ratio_;
  bool is_stepping_; // TODO(fraudies): Check if this need to be atomic

  // Playback speed variables
  double requested_speed_;
  double current_speed_;
  bool speed_request_;

  bool audio_disabled_;
  bool video_disabled_;

  int last_video_stream_;
  int last_audio_stream_;

  std::condition_variable continue_read_thread_;

  char *filename_;

  PacketQueue *p_audio_packet_queue_;
  PacketQueue *p_image_packet_queue_;

  FrameQueue *p_audio_frame_queue_;
  FrameQueue *p_image_frame_queue_;

  Clock *p_audio_clock_;
  Clock *p_image_clock_;
  Clock *p_external_clock_;

  Decoder *p_audio_decoder_;
  Decoder *p_frame_decoder_;

  std::thread *p_reader_thread_;
  AVInputFormat *p_input_format_;
  AVFormatContext *p_format_context;
  struct SwrContext *swr_ctx;

  AVStream *p_audio_stream_;
  AVStream *p_image_stream_;

  int audio_stream_index_;
  double audio_pts_;
  int audio_serial_;
  double audio_diff_cum_; /* used for AV difference average computation */
  double audio_diff_avg_coef_;
  double audio_diff_threshold_;
  int audio_diff_avg_count_;
  int audio_hw_buffer_size_;
  int audio_default_buffer_size_;
  uint8_t *p_audio_buffer_;
  uint8_t *p_audio_buffer1_;
  unsigned int audio_buffer_size_; // in bytes
  unsigned int audio_buffer1_size_;
  int audio_buffer_index_; // in bytes
  int audio_write_buffer_size_;
  bool is_muted_;
  struct AudioParams audio_parms_source_;
  struct AudioParams audio_params_target_;
  int num_frame_drops_early_;
  int64_t start_time_;   // initial start time
  int64_t max_duration_; // initial play time
  int num_loop_;         // loop through the video

  inline int CompareAudioFormats(enum AVSampleFormat fmt1,
                                 int64_t channel_count1,
                                 enum AVSampleFormat fmt2,
                                 int64_t channel_count2) {
    /* If channel count == 1, planar and non-planar formats are the same */
    return (channel_count1 == 1 && channel_count2 == 1)
               ? av_get_packed_sample_fmt(fmt1) !=
                     av_get_packed_sample_fmt(fmt2)
               : channel_count1 != channel_count2 || fmt1 != fmt2;
  }

  inline int64_t GetValidChannelLayout(int64_t channel_layout, int channels) {
    return (channel_layout &&
            av_get_channel_layout_nb_channels(channel_layout) == channels)
               ? channel_layout
               : 0;
  }

  /* open a given stream. Return 0 if OK */
  int OpenStreamComponent(int stream_index);
  int GetImageFrame(AVFrame *frame);

  int QueueImage(AVFrame *image_frame, double pts, double duration, int64_t pos,
                 int serial);
  void CloseStreamComponent(int stream_index);
  bool StreamHasEnoughPackets(const AVStream &p_stream, int stream_index,
                              const PacketQueue &packet_queue);

  // pointers are left because the low level c routines require a pointer
  // that they can modify
  int CheckStreamSpecifier(AVFormatContext *p_format_contex, AVStream *p_stream,
                           const char *specifier);

  int GetFilterCodecOptions(AVDictionary *p_opts_out,
                            const AVDictionary &opts_in, AVCodecID codec_id,
                            AVFormatContext *p_format_context,
                            AVStream *p_stream, AVCodec *p_codec);

  int SetupStreamOptions(AVDictionary ***opts, AVFormatContext *s,
                         AVDictionary *codec_opts);

  // return the wanted number of samples to get better sync if sync_type is
  // video or external master clock
  int SynchronizeAudio(int nb_samples);

  // Decode one audio frame and return its uncompressed size.
  //
  // The processed audio frame is decoded, converted if required, and
  // stored in is->audio_buf, with size in bytes given by the return
  // value.
  int DecodeAudioFrame();

  std::function<void()>
      player_state_callbacks[VideoState::NUM_PLAYER_STATE_CALLBACKS];
  std::function<int(int64_t, int, int, struct AudioParams *)>
      audio_open_callback;
  std::function<void()> pause_audio_device_callback;
  std::function<void()>
      destroy_callback; // TODO(fraudies): Possibly clean-up through destructor
  std::function<void()> step_to_next_frame_callback;
  VideoState(int audio_buffer_size);

  // get the current synchronization type
  AvSyncType GetMasterSyncType() const;

  static bool kEnableShowFormat;
  static bool kEnableFastDecode;
  static bool kEnableGeneratePts;
  static int kMaxQueueSize;
  static int kMinFrames;
  static double kAvSyncThresholdMin;
  static double kAvSyncFrameDupThreshold;
  static double kAvNoSyncThreshold;
  static int kSampleCorrectionMaxPercent;
  static int kAudioDiffAvgNum;
  static int kVideoPictureQueueSize;
  static int kSampleQueueSize;

  static int CreateVideoState(VideoState **pp_video_state,
                              int audio_buffer_size);

public:
  static double kAvSyncThresholdMax;
  static int kEnableSeekByBytes;
  virtual ~VideoState();

  int ReadPacketsToQueues();
  int DecodeAudioPacketsToFrames();
  int DecodeImagePacketsToFrames();
  int StartStream();
  static int OpenStream(VideoState **pp_video_state, const char *filename,
                        AVInputFormat *iformat, int audio_buffer_size);

  inline void
  SetPlayerStateCallbackFunction(PlayerStateCallback callback,
                                 const std::function<void()> &func) {
    player_state_callbacks[callback] = func;
  }

  inline void SetAudioOpenCallback(
      const std::function<int(int64_t, int, int, struct AudioParams *)> &func) {
    audio_open_callback = func;
  }

  inline void SetPauseAudioDeviceCallback(const std::function<void()> &func) {
    pause_audio_device_callback = func;
  }

  inline void SetDestroyCallback(const std::function<void()> &func) {
    destroy_callback = func;
  }

  inline void SetStepToNextFrameCallback(const std::function<void()> &func) {
    step_to_next_frame_callback = func;
  }

  /* Controls */
  inline int GetFrameWidth() const { return frame_width_; }
  inline int GetFrameHeight() const { return frame_height_; }
  inline AVRational GetFrameAspectRatio() const { return frame_aspect_ratio_; }
  inline bool has_audio_data() const { return last_audio_stream_ >= 0; }
  inline bool has_image_data() const { return last_video_stream_ >= 0; }
  inline double get_duration() const { return duration_; }; // duration in sec
  inline double get_stream_time() const {
    return p_image_clock_->get_time();
  } // current time in sec
  inline void toggle_mute() { is_muted_ = !is_muted_; }
  void update_pts(double pts, int serial);
  void stream_seek(int64_t pos, int64_t rel, int seek_by_bytes);

  /* Setter and Getters */
  inline bool get_paused() const { return is_paused_; }
  inline void set_paused(bool new_paused) { is_paused_ = new_paused; }

  inline bool get_stopped() const { return is_stopped_; }
  inline void set_stopped(bool new_stopped) { is_stopped_ = new_stopped; }

  inline int IsStepping() const { return is_stepping_; }
  inline void SetStepping(bool stepping) { is_stepping_ = stepping; }

  int SetSpeed(double requested_speed);
  inline double GetSpeed() const { return current_speed_; }

  inline int get_frame_drops_early() const { return num_frame_drops_early_; }

  inline const char *get_filename() const { return filename_; }

  inline AVStream *get_audio_st() const { return p_audio_stream_; }
  inline AVStream *get_video_st() const { return p_image_stream_; }

  inline FrameQueue *get_pPictq() const { return p_image_frame_queue_; }
  inline FrameQueue *get_pSampq() const { return p_audio_frame_queue_; }

  inline PacketQueue *get_pVideoq() const { return p_image_packet_queue_; }
  inline PacketQueue *get_pAudioq() const { return p_audio_packet_queue_; }

  inline Clock *get_pVidclk() const { return p_image_clock_; }
  inline Clock *get_pAudclk() const { return p_audio_clock_; }
  inline Clock *get_pExtclk() const { return p_external_clock_; }

  inline double get_vidclk_last_set_time() const {
    return video_clock_last_set_time_;
  }

  inline AudioParams get_audio_tgt() const { return audio_params_target_; }
  inline int get_muted() const { return is_muted_; }

  inline Decoder *get_pViddec() const { return p_frame_decoder_; }
  inline AVFormatContext *get_ic() const { return p_format_context; }

  inline int64_t get_seek_pos() const { return seek_time_; }

  inline int get_video_stream() const { return image_stream_index_; }
  inline int get_audio_stream() const { return audio_stream_index_; }

  inline double get_max_frame_duration() const { return max_frame_duration_; }

  inline int decode_interrupt_cb() const { return abort_request_; }

  double compute_target_delay(double delay);

  /* get the current master clock */
  Clock *get_master_clock() const;

  inline double GetFrameRate() const {
    return p_image_stream_ ? this->frame_rate_ : 0;
  }

  /* prepare a new audio buffer */
  void audio_callback(uint8_t *stream, int len);

  inline bool GetAudioDisabled() const { return audio_disabled_; }
  inline void SetAudioDisabled(bool disabled) { audio_disabled_ = disabled; }

  inline bool GetVideoDisabled() const { return video_disabled_; }
  inline void SetVideoDisabled(bool disabled) { video_disabled_ = disabled; }
};

// Note, this bridge is necessary to interface with ffmpeg's call decode
// interrupt handle
static int decode_interrupt_cb_bridge(void *vs) {
  return static_cast<VideoState *>(vs)->decode_interrupt_cb();
}

#endif VIDEOSTATE_H_
