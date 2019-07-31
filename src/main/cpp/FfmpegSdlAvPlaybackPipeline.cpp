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
  p_sdl_playback_->SetPlayerStateCallbackFunction(
      VideoState::PlayerStateCallback::TO_UNKNOWN,
      [this] { this->UpdatePlayerState(Unknown); });
  p_sdl_playback_->SetPlayerStateCallbackFunction(
      VideoState::PlayerStateCallback::TO_READY,
      [this] { this->UpdatePlayerState(Ready); });
  p_sdl_playback_->SetPlayerStateCallbackFunction(
      VideoState::PlayerStateCallback::TO_PLAYING,
      [this] { this->UpdatePlayerState(Playing); });
  p_sdl_playback_->SetPlayerStateCallbackFunction(
      VideoState::PlayerStateCallback::TO_PAUSED,
      [this] { this->UpdatePlayerState(Paused); });
  p_sdl_playback_->SetPlayerStateCallbackFunction(
      VideoState::PlayerStateCallback::TO_STOPPED,
      [this] { this->UpdatePlayerState(Stopped); });
  p_sdl_playback_->SetPlayerStateCallbackFunction(
      VideoState::PlayerStateCallback::TO_STALLED,
      [this] { this->UpdatePlayerState(Stalled); });
  p_sdl_playback_->SetPlayerStateCallbackFunction(
      VideoState::PlayerStateCallback::TO_FINISHED,
      [this] { this->UpdatePlayerState(Finished); });
  p_sdl_playback_->SetKeyEventKeyDispatcherCallback(
      [this](int sdlkeyCode) { this->MapSdlToJavaKey(sdlkeyCode); });

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

  UpdatePlayerState(Playing);

  return ERROR_NONE; // no error
}

uint32_t FfmpegSdlAvPlaybackPipeline::Stop() {
  if (p_sdl_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }
  p_sdl_playback_->Stop();

  UpdatePlayerState(Stopped);

  return ERROR_NONE; // no error
}

uint32_t FfmpegSdlAvPlaybackPipeline::Pause() {
  if (p_sdl_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  p_sdl_playback_->Pause();

  UpdatePlayerState(Paused);

  return ERROR_NONE; // no error
}

uint32_t FfmpegSdlAvPlaybackPipeline::StepForward() {
  if (p_sdl_playback_ == nullptr)
    return ERROR_PLAYBACK_NULL;

  p_sdl_playback_->StepToNextFrame();

  return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::StepBackward() {
  if (p_sdl_playback_ == nullptr)
    return ERROR_PLAYBACK_NULL;

  p_sdl_playback_->StepToPreviousFrame();

  return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::Finish() {
  // TODO(fraudies): Stalling and finish need to be set from the video player
  return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::Seek(double dSeekTime, int seek_flags) {
  if (p_sdl_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  double pos = p_sdl_playback_->GetTime();

  if (isnan(pos)) {
    pos = (double)p_sdl_playback_->GetSeekTime() / (double)AV_TIME_BASE;
  }

  if (p_sdl_playback_->GetStartTime() != AV_NOPTS_VALUE &&
      dSeekTime < p_sdl_playback_->GetStartTime() / (double)AV_TIME_BASE) {
    dSeekTime = p_sdl_playback_->GetStartTime() / (double)AV_TIME_BASE;
  } else if (p_sdl_playback_->GetDuration() != AV_NOPTS_VALUE &&
             dSeekTime >= p_sdl_playback_->GetDuration()) {
    // FIXME Remove the 0.1 sec difference when seeking to end of stream is
    // fixed
    dSeekTime = p_sdl_playback_->GetDuration() - 0.1;
  }

  double incr = dSeekTime - pos;

  p_sdl_playback_->Seek((int64_t)(dSeekTime * AV_TIME_BASE),
                        (int64_t)(incr * AV_TIME_BASE), seek_flags);

  return ERROR_NONE; // no error
}

uint32_t FfmpegSdlAvPlaybackPipeline::SeekToFrame(int frame_nb) {
  if (p_sdl_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  p_sdl_playback_->SeekToFrame(frame_nb);

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

  // The master clock (Audio Clock by default) could return NaN and affect
  // performance while seeking. However returning the external clock should
  // resolve this issue (Note that the timestamp return by the external is not
  // as accurate as the audio clock  (Master))
  //*pdStreamTime = pSdlPlayback->get_master_clock();

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

uint32_t FfmpegSdlAvPlaybackPipeline::GetWindowSize(int *iWidth, int *iHeight) const {
	if (p_sdl_playback_ == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}
	p_sdl_playback_->GetWindowSize(iWidth, iHeight);
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

uint32_t FfmpegSdlAvPlaybackPipeline::ShowWindow() {
  if (p_sdl_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }
  p_sdl_playback_->ShowWindow();
  return ERROR_NONE;
}
