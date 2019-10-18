#ifndef FFMPEGAVPLAYBACK_H_
#define FFMPEGAVPLAYBACK_H_

#include "VideoState.h"
#include "PlayerState.h"

class FfmpegAvPlayback {
public:
  FfmpegAvPlayback();
  int OpenVideo(const char *filename, AVInputFormat *p_input_format,
                int audio_buffer_size);
  virtual void
  SetUpdatePlayerStateCallbackFunction(PlayerState::State state,
                                 const std::function<void()> &func);  

  virtual void Play();
  virtual void Stop();
  virtual void Pause();
  virtual void TogglePauseAndStopStep();

  inline virtual void Seek(int64_t time, int64_t difference,
                           int seek_flags = VideoState::kSeekPreciseFlag) {
	if (update_player_state_callbacks[PlayerState::State::Stalled]) {
	  update_player_state_callbacks[PlayerState::State::Stalled]();
	}

    p_video_state_->Seek(time, difference, seek_flags);

	if (set_pending_player_state_callback) {
	  set_pending_player_state_callback();
	}
  }
  inline virtual void SeekToFrame(int frame_nb) {
    p_video_state_->SeekToFrame(frame_nb);
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
    // if the stream is paused/stopped unpause it, then step
    if (IsPaused() || IsStopped()) {
	  // Mute player 
      TogglePause(true, true);
	}

    p_video_state_->SetStepping(true);
  }

  inline void StepToPreviousFrame() {

	if (!IsPaused() || !IsStopped()) {
	  TogglePause(true, true);
	}

	// Get the current time and seek if it time is not NaN and equal 0
    double time = GetTime();
    if (!isnan(time) && time > 0) {
      double difference = -1.0 / GetFrameRate();
      p_video_state_->Seek((int64_t)((time + difference) * AV_TIME_BASE),
                           (int64_t)(difference * AV_TIME_BASE),
                           VideoState::kSeekPreciseFlag);
    }
  }

  inline void SetPlayerStateCallbackFunction(const std::function<bool(PlayerState::State)> &func) {
	is_player_state_callback = func;
  }

  inline void SetPendingPlayerStateCallbackFunction(const std::function<void()> &func) {
	set_pending_player_state_callback = func;
  }

protected:
  // The video state used for this playback
  VideoState *p_video_state_;

  int frame_width_;
  int frame_height_;

  // Force a refresh of the display
  bool force_refresh_;

  // Disable the display
  bool display_disabled_;

  // Counts the number of early frame drops
  int num_frame_drops_late_;

  std::function<bool(PlayerState::State)> is_player_state_callback;
  std::function<void()> set_pending_player_state_callback;
  std::function<void()>
	  update_player_state_callbacks[PlayerState::Error]; // Error is the last in the enum item of the enum

  // Enable the showing of the status
  static bool kEnableShowStatus;

  void TogglePause(bool update_state = true, bool mute = false);

  inline void SetForceReferesh(bool refresh) { force_refresh_ = refresh; }
  double ComputeFrameDuration(Frame *vp, Frame *nextvp,
                              double max_frame_duration);

  /* Setter and Getters */
  inline bool IsReady() const {
	if (is_player_state_callback) {
	  return is_player_state_callback(PlayerState::State::Ready) && p_video_state_->IsPaused();
	}
	return false;
  }

  inline bool IsPaused() const {
	if (is_player_state_callback) {
	  return is_player_state_callback(PlayerState::State::Paused) && p_video_state_->IsPaused();
	}
	return p_video_state_->IsPaused();
  }

  inline void SetPaused() {
	if (update_player_state_callbacks[PlayerState::State::Paused] && p_video_state_->IsPaused()) {
	  update_player_state_callbacks[PlayerState::State::Paused]();
	}
  }

  inline bool IsStopped() const {
	if (is_player_state_callback) {
	  return is_player_state_callback(PlayerState::State::Stopped) && p_video_state_->IsPaused();
	}
	return false;
  }

  inline void SetStopped() {
	if (update_player_state_callbacks[PlayerState::State::Stopped] && p_video_state_->IsPaused()) {
	  update_player_state_callbacks[PlayerState::State::Stopped]();
	}
  }

  inline bool IsPlaying() const {
	if (is_player_state_callback) {
	  return is_player_state_callback(PlayerState::State::Playing) && !p_video_state_->IsPaused();
	}
	return !p_video_state_->IsPaused();
  }

  inline void SetPlaying() {
	if (update_player_state_callbacks[PlayerState::State::Playing] && !p_video_state_->IsPaused()) {
	  update_player_state_callbacks[PlayerState::State::Playing]();
	}
  }
};

#endif // FFMPEGAVPLAYBACK_H_
