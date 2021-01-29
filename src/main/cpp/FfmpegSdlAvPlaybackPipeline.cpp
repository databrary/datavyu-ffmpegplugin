#include "FfmpegSdlAvPlaybackPipeline.h"
#include "MediaPlayerErrors.h"

FfmpegSdlAvPlaybackPipeline::FfmpegSdlAvPlaybackPipeline(
    CPipelineOptions *pOptions)
    : CPipeline(pOptions), p_sdl_playback_(nullptr) {}

FfmpegSdlAvPlaybackPipeline::~FfmpegSdlAvPlaybackPipeline() {
  // Clean-up done in dispose that is called from the destructor of the
  // super-class
}

uint32_t FfmpegSdlAvPlaybackPipeline::Init(const char *input_file) {
  // TODO: Proper error handling and wiring up of input arguments
  av_log_set_flags(AV_LOG_SKIP_REPEATED);
  av_log(NULL, AV_LOG_WARNING, "Init Network\n");
  AVInputFormat *file_iformat = nullptr;

  p_sdl_playback_ = new (std::nothrow) FfmpegSdlAvPlayback();

  if (!p_sdl_playback_) {
    return ERROR_PIPELINE_NULL;
  }

  int err = p_sdl_playback_->OpenVideo(input_file, file_iformat);
  if (err) {
    delete p_sdl_playback_;
    return err;
  }

  // Assign the callback functions
  p_sdl_playback_->SetUpdatePlayerStateCallbackFunction(
      PlayerState::State::Unknown,
      [this] { this->UpdatePlayerState(PlayerState::State::Unknown); });
  p_sdl_playback_->SetUpdatePlayerStateCallbackFunction(
      PlayerState::State::Ready,
      [this] { this->UpdatePlayerState(PlayerState::State::Ready); });
  p_sdl_playback_->SetUpdatePlayerStateCallbackFunction(
      PlayerState::State::Playing,
      [this] { this->UpdatePlayerState(PlayerState::State::Playing); });
  p_sdl_playback_->SetUpdatePlayerStateCallbackFunction(
      PlayerState::State::Paused,
      [this] { this->UpdatePlayerState(PlayerState::State::Paused); });
  p_sdl_playback_->SetUpdatePlayerStateCallbackFunction(
      PlayerState::State::Stopped,
      [this] { this->UpdatePlayerState(PlayerState::State::Stopped); });
  p_sdl_playback_->SetUpdatePlayerStateCallbackFunction(
      PlayerState::State::Stalled,
      [this] { this->UpdatePlayerState(PlayerState::State::Stalled); });
  p_sdl_playback_->SetUpdatePlayerStateCallbackFunction(
      PlayerState::State::Finished,
      [this] { this->UpdatePlayerState(PlayerState::State::Finished); });
  p_sdl_playback_->SetKeyEventKeyDispatcherCallback(
      [this](int sdlkeyCode) { this->MapSdlToJavaKey(sdlkeyCode); });
  p_sdl_playback_->SetPendingPlayerStateCallbackFunction(
      [this] { this->SetPendingPlayerState(); });
  p_sdl_playback_->SetPlayerStateCallbackFunction(
      [this](PlayerState::State state) { return this->IsPlayerState(state); });

  err = p_sdl_playback_->InitializeAndStartDisplayLoop();
  if (err) {
    return err;
  }

  return ERROR_NONE;
}

void FfmpegSdlAvPlaybackPipeline::Dispose() {
  delete p_sdl_playback_;
  p_sdl_playback_ = nullptr;
}

uint32_t FfmpegSdlAvPlaybackPipeline::Play() {
  if (p_sdl_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  p_sdl_playback_->Play();

  return ERROR_NONE; // no error
}

uint32_t FfmpegSdlAvPlaybackPipeline::Stop() {
  if (p_sdl_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  p_sdl_playback_->Stop();

  return ERROR_NONE; // no error
}

uint32_t FfmpegSdlAvPlaybackPipeline::Pause() {
  if (p_sdl_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  p_sdl_playback_->Pause();

  return ERROR_NONE; // no error
}

uint32_t FfmpegSdlAvPlaybackPipeline::StepForward() {
  if (p_sdl_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  p_sdl_playback_->StepToNextFrame();

  return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::StepBackward() {
  if (p_sdl_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  p_sdl_playback_->StepToPreviousFrame();

  return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::Finish() {
  // TODO(fraudies): Stalling and finish need to be set from the video player
  return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::Seek(double dSeekTime) {
  if (p_sdl_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  p_sdl_playback_->Seek(dSeekTime);

  return ERROR_NONE; // no error
}

uint32_t FfmpegSdlAvPlaybackPipeline::GetDuration(double *pdDuration) {
  if (p_sdl_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  *pdDuration = p_sdl_playback_->GetDuration();

  return ERROR_NONE; // no error
}

uint32_t FfmpegSdlAvPlaybackPipeline::GetStreamTime(double *pdStreamTime) {
  if (p_sdl_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  *pdStreamTime = p_sdl_playback_->GetTime();

  return ERROR_NONE; // no error
}

uint32_t FfmpegSdlAvPlaybackPipeline::GetFps(double *pdFps) {
  if (p_sdl_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  *pdFps = p_sdl_playback_->GetFrameRate();

  return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::SetRate(float fRate) {
  if (p_sdl_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  return p_sdl_playback_->SetSpeed(fRate);
}

uint32_t FfmpegSdlAvPlaybackPipeline::GetRate(float *pfRate) {
  if (p_sdl_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  *pfRate = p_sdl_playback_->GetSpeed();

  return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::SetVolume(float fVolume) {
  if (p_sdl_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  p_sdl_playback_->SetVolume(fVolume * SDL_MIX_MAXVOLUME);

  return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::GetVolume(float *pfVolume) {
  if (p_sdl_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  *pfVolume = p_sdl_playback_->GetVolume() / (double)SDL_MIX_MAXVOLUME;

  return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::SetBalance(float fBalance) {
  // TODO(fraudies): Not sure how to wire this
  return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::GetBalance(float *pfBalance) {
  // TODO(fraudies): Not sure how to wire this
  return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::SetAudioSyncDelay(long lMillis) {
  // TODO(fraudies): Implement this
  return ERROR_NONE; // no error
}

uint32_t FfmpegSdlAvPlaybackPipeline::GetAudioSyncDelay(long *plMillis) {
  // TODO(fraudies): Implement this
  return ERROR_NONE; // no error
}

uint32_t FfmpegSdlAvPlaybackPipeline::GetImageWidth(int *iWidth) const {
  if (p_sdl_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  *iWidth = p_sdl_playback_->GetImageWidth();

  return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::GetImageHeight(int *iHeight) const {
  if (p_sdl_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  *iHeight = p_sdl_playback_->GetImageHeight();

  return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::GetWindowWidth(int *iWidth) const {
  if (p_sdl_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  *iWidth = p_sdl_playback_->GetWindowWidth();

  return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::GetWindowHeight(int *iHeight) const {
  if (p_sdl_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  *iHeight = p_sdl_playback_->GetWindowHeight();

  return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::SetWindowSize(int width, int height) {
  if (p_sdl_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  p_sdl_playback_->SetWindowSize(width, height);

  return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::HideWindow() {
  if (p_sdl_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  p_sdl_playback_->HideWindow();

  return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::IsVisible(int *visible) const {
	if (p_sdl_playback_ == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}

	*visible = p_sdl_playback_->IsVisible();

	return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::ShowWindow() {
  if (p_sdl_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  p_sdl_playback_->ShowWindow();

  return ERROR_NONE;
}
