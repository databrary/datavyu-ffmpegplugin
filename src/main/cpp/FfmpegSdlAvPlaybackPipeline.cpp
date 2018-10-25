#include "FfmpegSdlAvPlaybackPipeline.h"
#include "MediaPlayerErrors.h"

FfmpegSdlAvPlaybackPipeline::FfmpegSdlAvPlaybackPipeline(
    CPipelineOptions *pOptions)
    : CPipeline(pOptions), pSdlPlayback(nullptr) {}

FfmpegSdlAvPlaybackPipeline::~FfmpegSdlAvPlaybackPipeline() {
  // Clean-up done in dispose that is called from the destructor of the
  // super-class
}

uint32_t FfmpegSdlAvPlaybackPipeline::Init(const char *input_file) {
  // TODO: Proper error handling and wiring up of input arguments
  av_log_set_flags(AV_LOG_SKIP_REPEATED);
  av_log(NULL, AV_LOG_WARNING, "Init Network\n");
  AVInputFormat *file_iformat = nullptr;

  pSdlPlayback = new (std::nothrow) FfmpegSdlAvPlayback();

  if (!pSdlPlayback) {
    return ERROR_PIPELINE_NULL;
  }

  int err = pSdlPlayback->OpenVideo(input_file, file_iformat);
  if (err) {
    delete pSdlPlayback;
    return err;
  }

  // Assign the callback functions
  pSdlPlayback->SetPlayerStateCallbackFunction(
      VideoState::PlayerStateCallback::TO_UNKNOWN,
      [this] { this->UpdatePlayerState(Unknown); });
  pSdlPlayback->SetPlayerStateCallbackFunction(
      VideoState::PlayerStateCallback::TO_READY,
      [this] { this->UpdatePlayerState(Ready); });
  pSdlPlayback->SetPlayerStateCallbackFunction(
      VideoState::PlayerStateCallback::TO_PLAYING,
      [this] { this->UpdatePlayerState(Playing); });
  pSdlPlayback->SetPlayerStateCallbackFunction(
      VideoState::PlayerStateCallback::TO_PAUSED,
      [this] { this->UpdatePlayerState(Paused); });
  pSdlPlayback->SetPlayerStateCallbackFunction(
      VideoState::PlayerStateCallback::TO_STOPPED,
      [this] { this->UpdatePlayerState(Stopped); });
  pSdlPlayback->SetPlayerStateCallbackFunction(
      VideoState::PlayerStateCallback::TO_STALLED,
      [this] { this->UpdatePlayerState(Stalled); });
  pSdlPlayback->SetPlayerStateCallbackFunction(
      VideoState::PlayerStateCallback::TO_FINISHED,
      [this] { this->UpdatePlayerState(Finished); });

  err = pSdlPlayback->InitializeAndStartDisplayLoop();
  if (err) {
    return err;
  }

  return ERROR_NONE;
}

void FfmpegSdlAvPlaybackPipeline::Dispose() {
  delete pSdlPlayback;
  pSdlPlayback = nullptr;
}

uint32_t FfmpegSdlAvPlaybackPipeline::Play() {
  if (pSdlPlayback == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }
  pSdlPlayback->Play();

  return ERROR_NONE; // no error
}

uint32_t FfmpegSdlAvPlaybackPipeline::Stop() {
  if (pSdlPlayback == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }
  pSdlPlayback->Stop();

  return ERROR_NONE; // no error
}

uint32_t FfmpegSdlAvPlaybackPipeline::Pause() {
  if (pSdlPlayback == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }
  pSdlPlayback->TogglePauseAndStopStep();

  return ERROR_NONE; // no error
}

uint32_t FfmpegSdlAvPlaybackPipeline::StepForward() {
  if (pSdlPlayback == nullptr)
    return ERROR_PLAYBACK_NULL;

  pSdlPlayback->StepToNextFrame();

  return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::StepBackward() { return ERROR_NONE; }

uint32_t FfmpegSdlAvPlaybackPipeline::Finish() {
  // TODO(fraudies): Stalling and finish need to be set from the video player
  return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::Seek(double dSeekTime) {
  if (pSdlPlayback == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }
  double pos = pSdlPlayback->GetTime();
  if (isnan(pos))
    pos = (double)pSdlPlayback->GetSeekTime() / AV_TIME_BASE;
  double incr = dSeekTime - pos;
  if (pSdlPlayback->GetStartTime() != AV_NOPTS_VALUE &&
      dSeekTime < pSdlPlayback->GetStartTime() / (double)AV_TIME_BASE)
    dSeekTime = pSdlPlayback->GetStartTime() / (double)AV_TIME_BASE;

  pSdlPlayback->Seek((int64_t)(dSeekTime * AV_TIME_BASE),
                            (int64_t)(incr * AV_TIME_BASE), 0);

  return ERROR_NONE; // no error
}

uint32_t FfmpegSdlAvPlaybackPipeline::GetDuration(double *pdDuration) {
  if (pSdlPlayback == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }
  *pdDuration = pSdlPlayback->GetDuration();

  return ERROR_NONE; // no error
}

uint32_t FfmpegSdlAvPlaybackPipeline::GetStreamTime(double *pdStreamTime) {
  if (pSdlPlayback == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  // The master clock (Audio Clock by default) could return NaN and affect
  // performance while seeking. However returning the external clock should
  // resolve this issue (Note that the timestamp return by the external is not
  // as accurate as the audio clock  (Master))
  //*pdStreamTime = pSdlPlayback->get_master_clock();

  *pdStreamTime = pSdlPlayback->GetTime();

  return ERROR_NONE; // no error
}

uint32_t FfmpegSdlAvPlaybackPipeline::GetFps(double *pdFps) {
  if (pSdlPlayback == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  *pdFps = pSdlPlayback->GetFrameRate();

  return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::SetRate(float fRate) {
  if (pSdlPlayback == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  return pSdlPlayback->SetSpeed(fRate);
}

uint32_t FfmpegSdlAvPlaybackPipeline::GetRate(float *pfRate) {
  if (pSdlPlayback == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  *pfRate = pSdlPlayback->GetSpeed();

  return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::SetVolume(float fVolume) {
  if (pSdlPlayback == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }
  pSdlPlayback->SetVolume(fVolume * SDL_MIX_MAXVOLUME);
  return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::GetVolume(float *pfVolume) {
  if (pSdlPlayback == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }
  *pfVolume = pSdlPlayback->GetVolume() / (double)SDL_MIX_MAXVOLUME;
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
  return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::GetImageHeight(int *iHeight) const {
  return ERROR_NONE;
}
