#include "FfmpegJavaAvPlaybackPipline.h"

uint32_t FfmpegJavaAvPlaybackPipline::Init(const char *input_file) {

  av_log_set_flags(AV_LOG_SKIP_REPEATED);
  av_log(NULL, AV_LOG_WARNING, "Init Network\n");
  AVInputFormat *file_iformat = nullptr;

  p_java_playback_ = new (std::nothrow) FfmpegJavaAvPlayback(
      m_pOptions->GetAudioFormat(), m_pOptions->GetPixelFormat(),
      m_pOptions->GetAudioBufferSizeInBy());

  if (!p_java_playback_) {
    av_log(NULL, AV_LOG_ERROR,
           "Unable to initialize the java playback pipeline");
    return ERROR_PIPELINE_NULL;
  }

  int err = p_java_playback_->Init(input_file, file_iformat);
  if (err) {
    delete p_java_playback_;
    return err;
  }

  // Assign the callback functions
  p_java_playback_->SetPlayerStateCallbackFunction(
      VideoState::PlayerStateCallback::TO_UNKNOWN,
      [this] { this->UpdatePlayerState(Unknown); });
  p_java_playback_->SetPlayerStateCallbackFunction(
      VideoState::PlayerStateCallback::TO_READY,
      [this] { this->UpdatePlayerState(Ready); });
  p_java_playback_->SetPlayerStateCallbackFunction(
      VideoState::PlayerStateCallback::TO_PLAYING,
      [this] { this->UpdatePlayerState(Playing); });
  p_java_playback_->SetPlayerStateCallbackFunction(
      VideoState::PlayerStateCallback::TO_PAUSED,
      [this] { this->UpdatePlayerState(Paused); });
  p_java_playback_->SetPlayerStateCallbackFunction(
      VideoState::PlayerStateCallback::TO_STOPPED,
      [this] { this->UpdatePlayerState(Stopped); });
  p_java_playback_->SetPlayerStateCallbackFunction(
      VideoState::PlayerStateCallback::TO_STALLED,
      [this] { this->UpdatePlayerState(Stalled); });
  p_java_playback_->SetPlayerStateCallbackFunction(
      VideoState::PlayerStateCallback::TO_FINISHED,
      [this] { this->UpdatePlayerState(Finished); });

  return p_java_playback_->StartStream();
}

void FfmpegJavaAvPlaybackPipline::Dispose() {
  p_java_playback_->Destroy();
  delete p_java_playback_;
  p_java_playback_ = nullptr;
}

FfmpegJavaAvPlaybackPipline::FfmpegJavaAvPlaybackPipline(
    CPipelineOptions *pOptions)
    : CPipelineData(pOptions), p_java_playback_(nullptr) {}

FfmpegJavaAvPlaybackPipline::~FfmpegJavaAvPlaybackPipline() {
  // Clean-up done in dispose that is called from the destructor of the
  // super-class
}

uint32_t FfmpegJavaAvPlaybackPipline::Play() {
  if (p_java_playback_ == nullptr)
    return ERROR_PLAYBACK_NULL;

  p_java_playback_->Play();

  return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::Stop() {
  if (p_java_playback_ == nullptr)
    return ERROR_PLAYBACK_NULL;

  p_java_playback_->Stop();

  return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::Pause() {
  if (p_java_playback_ == nullptr)
    return ERROR_PLAYBACK_NULL;

  p_java_playback_->TogglePauseAndStopStep();

  return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::StepForward() {
  if (p_java_playback_ == nullptr)
    return ERROR_PLAYBACK_NULL;

  p_java_playback_->StepToNextFrame();

  return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::StepBackward() { return ERROR_NONE; }

uint32_t FfmpegJavaAvPlaybackPipline::Finish() {
  // TODO(fraudies): Stalling and finish need to be set from the video player
  return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::Seek(double dSeekTime) {
  if (p_java_playback_ == nullptr)
    return ERROR_PLAYBACK_NULL;

  double pos = p_java_playback_->GetTime();

  if (isnan(pos))
    pos = (double)p_java_playback_->GetSeekTime() / AV_TIME_BASE;

  double incr = dSeekTime - pos;

  if (p_java_playback_->GetStartTime() != AV_NOPTS_VALUE &&
      dSeekTime < p_java_playback_->GetStartTime() / (double)AV_TIME_BASE) {
    dSeekTime = p_java_playback_->GetStartTime() / (double)AV_TIME_BASE;
  }

  p_java_playback_->Seek((int64_t)(dSeekTime * AV_TIME_BASE),
                             (int64_t)(incr * AV_TIME_BASE), 0);

  return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::GetDuration(double *pdDuration) {
  if (p_java_playback_ == nullptr)
    return ERROR_PLAYBACK_NULL;

  *pdDuration = p_java_playback_->GetDuration();

  return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::GetStreamTime(double *pdStreamTime) {
  if (p_java_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  // The master clock (Audio Clock by default) could return NaN and affect
  // performance while seeking. However returning the external clock should
  // resolve this issue (Note that the timestamp return by the external is not
  // as accurate as the audio clock  (Master))

  *pdStreamTime = p_java_playback_->GetTime();

  return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::GetFps(double *pdFps) {
  if (p_java_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  *pdFps = p_java_playback_->GetFrameRate();

  return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::SetRate(float fRate) {
  if (p_java_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  return p_java_playback_->SetSpeed(fRate);
}

uint32_t FfmpegJavaAvPlaybackPipline::GetRate(float *pfRate) {
  if (p_java_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  *pfRate = (float)p_java_playback_->GetSpeed();

  return ERROR_NONE;
}

// Note this function is available only when streaming through SDL pipline
uint32_t FfmpegJavaAvPlaybackPipline::SetVolume(float fVolume) {
  if (p_java_playback_ == nullptr)
    return ERROR_PLAYBACK_NULL;

  // TODO(fraudies): Implement this once ready
  // pSdlPlayback->update_volume(signbit(fVolume), fVolume * SDL_MIX_MAXVOLUME);
}

// Note this function is available only when streaming through SDL pipline
uint32_t FfmpegJavaAvPlaybackPipline::GetVolume(float *pfVolume) {
  if (p_java_playback_ == nullptr)
    return ERROR_PLAYBACK_NULL;

  // TODO(fraudies): Implement this once ready
  //*pfVolume = pSdlPlayback->get_audio_volume() / (double)SDL_MIX_MAXVOLUME;

  return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::SetBalance(float fBalance) {
  // TODO(fraudies): Not sure how to wire this
  if (p_java_playback_ == nullptr)
    return ERROR_PLAYBACK_NULL;

  p_java_playback_->SetBalance(fBalance);

  return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::GetBalance(float *pfBalance) {
  // TODO(fraudies): Not sure how to wire this
  if (p_java_playback_ == nullptr)
    return ERROR_PLAYBACK_NULL;

  *pfBalance = p_java_playback_->GetBalance();

  return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::SetAudioSyncDelay(long lMillis) {
  if (p_java_playback_ == nullptr)
    return ERROR_PLAYBACK_NULL;

  p_java_playback_->SetAudioSyncDelay(lMillis);

  return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::GetAudioSyncDelay(long *plMillis) {
  if (p_java_playback_ == nullptr)
    return ERROR_PLAYBACK_NULL;

  *plMillis = p_java_playback_->getAudioSyncDelay();

  return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::HasAudioData(bool *bAudioData) const {
  if (p_java_playback_ == nullptr)
    return ERROR_PLAYBACK_NULL;

  *bAudioData = p_java_playback_->HasAudioData();

  return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::HasImageData(bool *bImageData) const {
  if (p_java_playback_ == nullptr)
    return ERROR_PLAYBACK_NULL;

  *bImageData = p_java_playback_->HasImageData();

  return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::GetImageWidth(int *iWidth) const {
  if (p_java_playback_ == nullptr)
    return ERROR_PLAYBACK_NULL;

  *iWidth = p_java_playback_->GetImageWidth();

  return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::GetImageHeight(int *iHeight) const {
  if (p_java_playback_ == nullptr)
    return ERROR_PLAYBACK_NULL;

  *iHeight = p_java_playback_->GetImageHeight();

  return ERROR_NONE;
}

uint32_t
FfmpegJavaAvPlaybackPipline::GetAudioFormat(AudioFormat *pAudioFormat) const {
  if (p_java_playback_ == nullptr)
    return ERROR_PLAYBACK_NULL;

  p_java_playback_->GetAudioFormat(pAudioFormat);

  return ERROR_NONE;
}

uint32_t
FfmpegJavaAvPlaybackPipline::GetPixelFormat(PixelFormat *pPixelFormat) const {
  if (p_java_playback_ == nullptr)
    return ERROR_PLAYBACK_NULL;

  p_java_playback_->GetPixelFormat(pPixelFormat);

  return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::UpdateImageBuffer(uint8_t *pImageData,
                                                        const long len) {
  if (p_java_playback_ == nullptr)
    return ERROR_PLAYBACK_NULL;

  p_java_playback_->UpdateImageBuffer(pImageData, len);

  return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::UpdateAudioBuffer(uint8_t *pAudioData,
                                                        const long len) {
  if (p_java_playback_ == nullptr)
    return ERROR_PLAYBACK_NULL;

  p_java_playback_->UpdateAudioBuffer(pAudioData, len);

  return ERROR_NONE;
}
