#include "FfmpegAvPlayback.h"
#include "MediaPlayerErrors.h"

bool FfmpegAvPlayback::kEnableShowStatus = true;

void FfmpegAvPlayback::TogglePause() {

  // Get all the clocks
  Clock *pExtclk = p_video_state_->get_pExtclk();
  Clock *pVidclk = p_video_state_->get_pVidclk();
  Clock *pAudclk = p_video_state_->get_pAudclk();

  // Update the video clock
  if (p_video_state_->get_paused()) {
    SetFrameLastShownTime(GetFrameLastShownTime() +
                          av_gettime_relative() / 1000000.0 -
                          p_video_state_->get_vidclk_last_set_time());
    pVidclk->set_time(pVidclk->get_time(), pVidclk->get_serial());
  }
  // Update the external clock
  pExtclk->set_time(pExtclk->get_time(), pExtclk->get_serial());

  // Flip the paused flag on the clocks
  bool flipped = !p_video_state_->get_paused();
  p_video_state_->set_paused(flipped);
}

FfmpegAvPlayback::FfmpegAvPlayback()
    : p_video_state_(nullptr), display_disabled_(false), frame_width_(0), frame_height_(0),
      force_refresh_(1), num_frame_drops_late_(0) {}

int FfmpegAvPlayback::OpenVideo(const char *filename, AVInputFormat *iformat,
                                int audio_buffer_size) {
  return VideoState::OpenStream(&p_video_state_, filename, iformat, audio_buffer_size);
}

void FfmpegAvPlayback::SetPlayerStateCallbackFunction(
    VideoState::PlayerStateCallback callback,
    const std::function<void()> &func) {
  p_video_state_->SetPlayerStateCallbackFunction(callback, func);
}

void FfmpegAvPlayback::Play() {
  if (p_video_state_->get_paused()) {
    TogglePauseAndStopStep();
    p_video_state_->set_stopped(false);
  }
}

// Stop and put the playback speed to 0x
// Note the playback speed is not implemetnted yet
void FfmpegAvPlayback::Stop() {
  if (!p_video_state_->get_paused()) {
    TogglePauseAndStopStep();
    p_video_state_->set_stopped(true);
  }
}

// pause and keep the playback speed.
// Note the playback speed is not implemetnted yet
void FfmpegAvPlayback::TogglePauseAndStopStep() {
  TogglePause();
  p_video_state_->SetStepping(false);
}

int FfmpegAvPlayback::SetSpeed(double speed) {
  int err = p_video_state_->SetSpeed(speed);
  return err ? ERROR_FFMPEG_FILTER_NOT_FOUND : ERROR_NONE;
}

double FfmpegAvPlayback::ComputeFrameDuration(Frame *vp, Frame *nextvp,
                                              double max_frame_duration) {
  if (vp->serial == nextvp->serial) {
    double duration = (nextvp->pts - vp->pts) / p_video_state_->GetSpeed();
    if (isnan(duration) || duration <= 0 || duration > max_frame_duration)
      return vp->duration;
    else
      return duration;
  } else {
    return 0.0;
  }
}
