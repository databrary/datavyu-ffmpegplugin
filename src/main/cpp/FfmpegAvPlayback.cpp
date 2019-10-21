#include "FfmpegAvPlayback.h"
#include "MediaPlayerErrors.h"

bool FfmpegAvPlayback::kEnableShowStatus = true;

void FfmpegAvPlayback::TogglePauseUpdateStateAndMute(bool update_state,
                                                     bool mute) {
  p_video_state_->TogglePauseAndMute(mute);
  if (update_state) {
    if (p_video_state_->IsPaused()) {
      SetPaused();
    } else {
      SetPlaying();
    }
  }
}

FfmpegAvPlayback::FfmpegAvPlayback()
    : p_video_state_(nullptr), display_disabled_(false), frame_width_(0),
      frame_height_(0), force_refresh_(true), num_frame_drops_late_(0) {}

int FfmpegAvPlayback::OpenVideo(const char *filename,
                                AVInputFormat *p_input_format,
                                int audio_buffer_size) {
  return VideoState::OpenStream(&p_video_state_, filename, p_input_format,
                                audio_buffer_size);
}

void FfmpegAvPlayback::SetUpdatePlayerStateCallbackFunction(
    PlayerState::State state, const std::function<void()> &func) {
  update_player_state_callbacks[state] = func;
  p_video_state_->SetUpdatePlayerStateCallbackFunction(state, func);
}

void FfmpegAvPlayback::Play() {
  if (IsPaused() || IsStopped() || IsReady()) {
    TogglePauseAndStopStep();
  }
}

void FfmpegAvPlayback::Pause() {
  if (!IsPaused() && !IsReady()) {
    if (IsStopped()) {
      SetPaused();
    } else {
      TogglePauseAndStopStep();
    }
  }
}

// Stop and put the playback speed to 0x
void FfmpegAvPlayback::Stop() {
  if (!IsStopped() && !IsReady()) {
    if (!IsPaused()) {
      TogglePauseAndStopStep();
    }
    SetStopped();
    SetSpeed(1);
  }
}

// Pause and keep the playback speed
void FfmpegAvPlayback::TogglePauseAndStopStep() {
  TogglePauseUpdateStateAndMute();
  p_video_state_->SetStepping(false);
}

int FfmpegAvPlayback::SetSpeed(double speed) {
  int err = ERROR_NONE;
  is_fake_playback_ = speed < 0;
  if (speed < std::numeric_limits<double>::epsilon()) {
    Pause();
  } else {
    err = p_video_state_->SetSpeed(speed);
  }
  return err ? ERROR_FFMPEG_FILTER_NOT_FOUND : ERROR_NONE;
}

double FfmpegAvPlayback::ComputeFrameDuration(Frame *vp, Frame *nextvp,
                                              double max_frame_duration) {
  if (vp->serial_ == nextvp->serial_) {
    double duration = (nextvp->pts_ - vp->pts_) / p_video_state_->GetSpeed();
    if (isnan(duration) || duration <= 0 || duration > max_frame_duration)
      return vp->duration_;
    else
      return duration;
  } else {
    return 0.0;
  }
}
