#ifndef FFMPEGAVPLAYBACK_H_
#define FFMPEGAVPLAYBACK_H_

#include "VideoState.h"

class FfmpegAvPlayback {
public:
  FfmpegAvPlayback();
  int OpenVideo(const char *filename, AVInputFormat *p_input_format,
                int audio_buffer_size);
  virtual void
  SetPlayerStateCallbackFunction(VideoState::PlayerStateCallback callback,
                                 const std::function<void()> &func);
  virtual void Play();
  virtual void Stop();
  virtual void Pause();
  virtual void TogglePauseAndStopStep();

  inline virtual void Seek(int64_t time, int64_t difference,
                           int seek_flags) {
    p_video_state_->Seek(time, difference, seek_flags);
  }
  inline virtual double GetDuration() const {
    return p_video_state_->GetDuration();
  }
  inline virtual double GetTime() const { return p_video_state_->GetTime(); }
  inline virtual double GetFrameRate() const {
    return p_video_state_->GetFrameRate();
  }

  // Set the playback speed
  virtual int SetSpeed(double speed);

  // Get the playback speed
  inline virtual double GetSpeed() const { return p_video_state_->GetSpeed(); }

  inline int64_t GetStartTime() const {
    AVFormatContext *p_format_context = nullptr;
    p_video_state_->GetFormatContext(&p_format_context);
    return p_format_context->start_time;
  }

  inline int64_t GetSeekTime() const { return p_video_state_->GetSeekTime(); }

  inline void StepToNextFrame() {
    // if the stream is paused unpause it, then step
    if (p_video_state_->IsPaused()) {
      TogglePause();
    }

    p_video_state_->SetStepping(true);
  }

  inline void StepToPreviousFrame() {
		// Get the current time and seek if it is NaN
    double time = GetTime();
    if (!isnan(time)) {
      double difference = -1.0 / GetFrameRate();
      p_video_state_->Seek((int64_t)((time + difference) * AV_TIME_BASE),
                           (int64_t)(difference * AV_TIME_BASE),
                           VideoState::kSeekPreciseFlag);
    }
  }

protected:
  // The video state used for this playback
  VideoState *p_video_state_;

  // Time when the last frame was shown
  double frame_last_shown_time_;

  int frame_width_;
  int frame_height_;

  // Force a refresh of the display
  bool force_refresh_;

  // Disable the display
  bool display_disabled_;

  // Counts the number of early frame drops
  int num_frame_drops_late_;

  // Enable the showing of the status
  static bool kEnableShowStatus;

  void TogglePause();
  inline double GetFrameLastShownTime() const { return frame_last_shown_time_; }
  inline void SetFrameLastShownTime(double time) {
    frame_last_shown_time_ = time;
  }
  inline void SetForceReferesh(bool refresh) { force_refresh_ = refresh; }
  double ComputeFrameDuration(Frame *vp, Frame *nextvp,
                              double max_frame_duration);
};

#endif // FFMPEGAVPLAYBACK_H_
