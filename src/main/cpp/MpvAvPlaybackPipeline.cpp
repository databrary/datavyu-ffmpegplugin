#include "MpvAvPlaybackPipeline.h"
#include "MediaPlayerErrors.h"

uint32_t MpvAvPlaybackPipeline::Init(const char *input_file) {
  p_mpv_playback_ = new (std::nothrow) MpvAvPlayback();

  if (!p_mpv_playback_) {
    return ERROR_PIPELINE_NULL;
  }

  int err = p_mpv_playback_->Init(input_file, window_id_);
  if (err) {
    delete p_mpv_playback_;
    return err;
  }

  UpdatePlayerState(Ready);

  return ERROR_NONE;
}

void MpvAvPlaybackPipeline::Dispose() {
  p_mpv_playback_->Destroy();
  delete p_mpv_playback_;
  p_mpv_playback_ = nullptr;
}

MpvAvPlaybackPipeline::MpvAvPlaybackPipeline(CPipelineOptions *p_options,
                                             intptr_t window_id)
    : CPipeline(p_options), window_id_(window_id), p_mpv_playback_(nullptr) {}

MpvAvPlaybackPipeline::~MpvAvPlaybackPipeline() {}

uint32_t MpvAvPlaybackPipeline::Play() {
  if (p_mpv_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  int err = p_mpv_playback_->Play();
  if (err < 0) {
    return ERROR_FFMPEG_UNKNOWN;
  }

  UpdatePlayerState(Playing);

  return ERROR_NONE; // no error
}

uint32_t MpvAvPlaybackPipeline::Stop() {
  if (p_mpv_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  int err = p_mpv_playback_->Stop();
  if (err < 0) {
    return ERROR_FFMPEG_UNKNOWN;
  }

  UpdatePlayerState(Stopped);
  return ERROR_NONE; // no error
}

uint32_t MpvAvPlaybackPipeline::Pause() {
  if (p_mpv_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }
  int err = p_mpv_playback_->Pause();
  if (err < 0) {
    return err;
  }

  UpdatePlayerState(Paused);

  return ERROR_NONE; // no error
}

uint32_t MpvAvPlaybackPipeline::StepForward() {
  if (p_mpv_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  int err = p_mpv_playback_->StepForward();
  if (err < 0) {
    return err;
  }

  UpdatePlayerState(Paused);
  return ERROR_NONE;
}

uint32_t MpvAvPlaybackPipeline::StepBackward() {
  if (p_mpv_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  int err = p_mpv_playback_->StepBackward();
  if (err < 0) {
    return err;
  }

  UpdatePlayerState(Paused);

  return ERROR_NONE;
}

uint32_t MpvAvPlaybackPipeline::Finish() {
  UpdatePlayerState(Finished);

  return ERROR_NONE;
}

uint32_t MpvAvPlaybackPipeline::Seek(double seek_time, int seek_flags) {
  if (p_mpv_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  int err = p_mpv_playback_->SetTime(seek_time);
  if (err < 0) {
    return err;
  }

  return ERROR_NONE;
}

uint32_t MpvAvPlaybackPipeline::GetDuration(double *p_duration) {
  if (p_mpv_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  double duration;
  int err = p_mpv_playback_->GetDuration(&duration);
  if (err != 0) {
    return err;
  }

  *p_duration = duration;

  return ERROR_NONE; // no error
}

uint32_t MpvAvPlaybackPipeline::GetStreamTime(double *p_time) {
  if (p_mpv_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  double time;
  int err = p_mpv_playback_->GetPresentationTime(&time);
  if (err != 0) {
    return err;
  }

  *p_time = time;

  return ERROR_NONE; // no error
}

uint32_t MpvAvPlaybackPipeline::GetFps(double *p_fps) {
  if (p_mpv_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  double fps;
  int err = p_mpv_playback_->GetFps(&fps);
  if (err != 0) {
    return err;
  }

  *p_fps = fps;

  return ERROR_NONE;
}

uint32_t MpvAvPlaybackPipeline::GetImageWidth(int *p_width) const {
  if (p_mpv_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  int64_t width;
  int err = p_mpv_playback_->GetImageWidth(&width);
  if (err != 0) {
    return err;
  }

  *p_width = width;

  return ERROR_NONE;
}

uint32_t MpvAvPlaybackPipeline::GetImageHeight(int *p_height) const {
  if (p_mpv_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  int64_t height;
  int err = p_mpv_playback_->GetImageHeight(&height);
  if (err != 0) {
    return err;
  }

  *p_height = height;

  return ERROR_NONE;
}

uint32_t MpvAvPlaybackPipeline::SetRate(float rate) {
  if (p_mpv_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  int err = p_mpv_playback_->SetRate((double)rate);
  if (err != 0) {
    return err;
  }

  return ERROR_NONE;
}

uint32_t MpvAvPlaybackPipeline::GetRate(float *p_rate) {
  if (p_mpv_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  double rate;
  int err = p_mpv_playback_->GetRate(&rate);
  if (err != 0) {
    return err;
  }

  *p_rate = rate;

  return ERROR_NONE;
}

uint32_t MpvAvPlaybackPipeline::SetVolume(float volume) {
  if (p_mpv_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  int err = p_mpv_playback_->SetVolume((double)volume);
  if (err < 0) {
    return err;
  }

  return ERROR_NONE;
}

uint32_t MpvAvPlaybackPipeline::GetVolume(float *p_volume) {
  if (p_mpv_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  double volume;
  int err = p_mpv_playback_->GetVolume(&volume);
  if (err < 0) {
    return err;
  }

  *p_volume = volume;

  return ERROR_NONE;
}

uint32_t MpvAvPlaybackPipeline::SetBalance(float balance) {
  return ERROR_NONE;
}

uint32_t MpvAvPlaybackPipeline::GetBalance(float *p_balance) {
  return ERROR_NONE;
}

uint32_t MpvAvPlaybackPipeline::SetAudioSyncDelay(long millis) {
  return ERROR_NONE;
}

uint32_t MpvAvPlaybackPipeline::GetAudioSyncDelay(long *p_millis) {
  return ERROR_NONE;
}
