#include "FfmpegJavaAvPlaybackPipline.h"

uint32_t FfmpegJavaAvPlaybackPipline::Init(const char *input_file) {

  av_log_set_flags(AV_LOG_SKIP_REPEATED);
  av_log(NULL, AV_LOG_WARNING, "Init Network\n");
  AVInputFormat *file_iformat = nullptr;

  p_java_playback_ = new (std::nothrow) FfmpegJavaAvPlayback(
      p_options_->GetAudioFormat(), p_options_->GetPixelFormat(),
      p_options_->GetAudioBufferSizeInBy());

  if (!p_java_playback_) {
    av_log(NULL, AV_LOG_ERROR,
           "Unable to initialize the java playback pipeline");
    return ERROR_PIPELINE_NULL;
  }
  
  UpdatePlayerState(PlayerState::Unknown);

  int err = p_java_playback_->Init(input_file, file_iformat);
  if (err) {
    delete p_java_playback_;
    return err;
  }

  // Assign the callback functions
  p_java_playback_->SetUpdatePlayerStateCallbackFunction(
	  PlayerState::Unknown,
      [this] { this->UpdatePlayerState(PlayerState::Unknown); });
  p_java_playback_->SetUpdatePlayerStateCallbackFunction(
	  PlayerState::Ready,
      [this] { this->UpdatePlayerState(PlayerState::Ready); });
  p_java_playback_->SetUpdatePlayerStateCallbackFunction(
	  PlayerState::Playing,
      [this] { this->UpdatePlayerState(PlayerState::Playing); });
  p_java_playback_->SetUpdatePlayerStateCallbackFunction(
	  PlayerState::Paused,
      [this] { this->UpdatePlayerState(PlayerState::Paused); });
  p_java_playback_->SetUpdatePlayerStateCallbackFunction(
	  PlayerState::Stopped,
      [this] { this->UpdatePlayerState(PlayerState::Stopped); });
  p_java_playback_->SetUpdatePlayerStateCallbackFunction(
	  PlayerState::Stalled,
      [this] { this->UpdatePlayerState(PlayerState::Stalled); });
  p_java_playback_->SetUpdatePlayerStateCallbackFunction(
	  PlayerState::Finished,
      [this] { this->UpdatePlayerState(PlayerState::Finished); });

  return p_java_playback_->StartStream();
}

void FfmpegJavaAvPlaybackPipline::Dispose() {
  p_java_playback_->Destroy();
  delete p_java_playback_;
  p_java_playback_ = nullptr;
}

FfmpegJavaAvPlaybackPipline::FfmpegJavaAvPlaybackPipline(
    CPipelineOptions *p_options)
    : CPipelineData(p_options), p_java_playback_(nullptr) {}

FfmpegJavaAvPlaybackPipline::~FfmpegJavaAvPlaybackPipline() {
  // Clean-up done in dispose that is called from the destructor of the
  // super-class
}

uint32_t FfmpegJavaAvPlaybackPipline::Play() {
  if (p_java_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;  
	}

  p_java_playback_->Play();
  
  return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::Stop() {
  if (p_java_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  p_java_playback_->Stop();
    
  return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::Pause() {
  if (p_java_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  p_java_playback_->Pause();

  return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::StepForward() {
  if (p_java_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  p_java_playback_->StepToNextFrame();

  return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::StepBackward() { 
  if (p_java_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  p_java_playback_->StepToPreviousFrame();

  return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::Finish() {
  // TODO(fraudies): Stalling and finish need to be set from the video player
  return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::Seek(double time, int seek_flags) {
  if (p_java_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  double pos = p_java_playback_->GetTime();

  if (isnan(pos)) {
    pos = (double)p_java_playback_->GetSeekTime() / AV_TIME_BASE;  
  }

  if (p_java_playback_->GetStartTime() != AV_NOPTS_VALUE &&
      time < p_java_playback_->GetStartTime() / (double)AV_TIME_BASE) {
    time = p_java_playback_->GetStartTime() / (double)AV_TIME_BASE;
  } else if (p_java_playback_->GetDuration() != AV_NOPTS_VALUE &&
             time >= p_java_playback_->GetDuration()) {
      //FIXME Remove the 0.1 sec difference when seeking to end of stream is fixed
      time = p_java_playback_->GetDuration() - 0.1;
  }

  double difference = time - pos;

  p_java_playback_->Seek((int64_t)(time * AV_TIME_BASE),
                         (int64_t)(difference * AV_TIME_BASE), seek_flags);

  return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::SeekToFrame(int frame_nb) {
    if (p_java_playback_ == nullptr) {
        return ERROR_PLAYBACK_NULL;
    }
    
    p_java_playback_->SeekToFrame(frame_nb);
    
    return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::GetDuration(double *p_duration) {
  if (p_java_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;  
	}

  *p_duration = p_java_playback_->GetDuration();

  return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::GetStreamTime(double *p_stream_time) {
  if (p_java_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  // The master clock (Audio Clock by default) could return NaN and affect
  // performance while seeking. However returning the external clock should
  // resolve this issue (Note that the timestamp return by the external is not
  // as accurate as the audio clock  (Master))

  *p_stream_time = p_java_playback_->GetTime();

  return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::GetFps(double *p_fps) {
  if (p_java_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  *p_fps = p_java_playback_->GetFrameRate();

  return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::SetRate(float rate) {
  if (p_java_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  return p_java_playback_->SetSpeed(rate);
}

uint32_t FfmpegJavaAvPlaybackPipline::GetRate(float *p_rate) {
  if (p_java_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  *p_rate = (float)p_java_playback_->GetSpeed();

  return ERROR_NONE;
}

// Note this function is available only when streaming through SDL pipline
uint32_t FfmpegJavaAvPlaybackPipline::SetVolume(float volume) {
  if (p_java_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
	}

  // TODO(fraudies): Implement this once ready
  // pSdlPlayback->update_volume(signbit(fVolume), fVolume * SDL_MIX_MAXVOLUME);
    
    return ERROR_NONE;
}

// Note this function is available only when streaming through SDL pipline
uint32_t FfmpegJavaAvPlaybackPipline::GetVolume(float *p_volume) {
  if (p_java_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  // TODO(fraudies): Implement this once ready
  //*pfVolume = pSdlPlayback->get_audio_volume() / (double)SDL_MIX_MAXVOLUME;

  return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::SetBalance(float balance) {
  // TODO(fraudies): Not sure how to wire this
  if (p_java_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;  
	}

  p_java_playback_->SetBalance(balance);

  return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::GetBalance(float *p_balance) {
  // TODO(fraudies): Not sure how to wire this
  if (p_java_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  *p_balance = p_java_playback_->GetBalance();

  return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::SetAudioSyncDelay(long millis) {
  if (p_java_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;  
	}

  p_java_playback_->SetAudioSyncDelay(millis);

  return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::GetAudioSyncDelay(long *p_millis) {
  if (p_java_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;  
	}

  *p_millis = p_java_playback_->getAudioSyncDelay();

  return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::HasAudioData(bool *p_has_audio_data) const {
  if (p_java_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;  
	}

  *p_has_audio_data = p_java_playback_->HasAudioData();

  return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::HasImageData(bool *p_has_image_data) const {
  if (p_java_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;  
	}

  *p_has_image_data = p_java_playback_->HasImageData();

  return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::GetImageWidth(int *p_width) const {
  if (p_java_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  *p_width = p_java_playback_->GetImageWidth();

  return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::GetImageHeight(int *p_height) const {
  if (p_java_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;
  }

  *p_height = p_java_playback_->GetImageHeight();

  return ERROR_NONE;
}

uint32_t
FfmpegJavaAvPlaybackPipline::GetAudioFormat(AudioFormat *p_audio_format) const {
  if (p_java_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;  
	}

  p_java_playback_->GetAudioFormat(p_audio_format);

  return ERROR_NONE;
}

uint32_t
FfmpegJavaAvPlaybackPipline::GetPixelFormat(PixelFormat *p_pixel_format) const {
  if (p_java_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;  
	}

  p_java_playback_->GetPixelFormat(p_pixel_format);

  return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::UpdateImageBuffer(uint8_t *p_image_data,
                                                        const long len) {
  if (p_java_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;  
	}

  p_java_playback_->UpdateImageBuffer(p_image_data, len);

  return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::UpdateAudioBuffer(uint8_t *p_audio_data,
                                                        const long len) {
  if (p_java_playback_ == nullptr) {
    return ERROR_PLAYBACK_NULL;  
	}

  p_java_playback_->UpdateAudioBuffer(p_audio_data, len);

  return ERROR_NONE;
}
