#include "FfmpegJavaAvPlayback.h"
#include "FfmpegErrorUtils.h"
#include "MediaPlayerErrors.h"

FfmpegJavaAvPlayback::FfmpegJavaAvPlayback(const AudioFormat *pAudioFormat,
                                           const PixelFormat *pPixelFormat,
                                           const int audioBufferSizeInBy)
    : FfmpegAvPlayback(), kPtrAudioFormat(pAudioFormat),
      kPtrPixelFormat(pPixelFormat), kAudioBufferSizeInBy(audioBufferSizeInBy),
      p_img_convert_ctx_(nullptr), remaining_time_to_display_(0) {}

FfmpegJavaAvPlayback::~FfmpegJavaAvPlayback() {}

int FfmpegJavaAvPlayback::Init(const char *filename, AVInputFormat *iformat) {

  /* register all codecs, demux and protocols */
#if CONFIG_AVDEVICE
  avdevice_register_all();
#endif
  avformat_network_init();

  int err = FfmpegAvPlayback::OpenVideo(
      filename, iformat, kAudioBufferSizeInBy); // initializes the video state
  if (err) {
    return err;
  }

  p_video_state_->SetDestroyCallback([this] { Destroy(); });

  // TODO: Clean-up this callback as the first three parameters are not used
  // here
  p_video_state_->SetAudioOpenCallback(
      [this](int64_t wanted_channel_layout, int wanted_nb_channels,
             int wanted_sample_rate, struct AudioParams *audio_hw_params) {
        return AudioOpen(wanted_channel_layout, wanted_nb_channels,
                         wanted_sample_rate, audio_hw_params);
      });

  p_video_state_->SetStepToNextFrameCallback(
      [this] { this->StepToNextFrame(); });

  return ERROR_NONE;
}

VideoState *FfmpegJavaAvPlayback::GetVideoState() { return p_video_state_; }

int FfmpegJavaAvPlayback::AudioOpen(int64_t wanted_channel_layout,
                                    int wanted_nb_channels,
                                    int wanted_sample_rate,
                                    struct AudioParams *audio_hw_params) {
  // TODO(fraudies): If we need to change the audio format overwrite
  // pAudioFormat here
  kPtrAudioFormat->ToAudioParams(audio_hw_params);
  return kAudioBufferSizeInBy;
}

void FfmpegJavaAvPlayback::Destroy() {

  sws_freeContext(p_img_convert_ctx_);

  delete p_video_state_;
  avformat_network_deinit();

  av_log(NULL, AV_LOG_QUIET, "%s", "");
}

int FfmpegJavaAvPlayback::StartStream() {
  return FfmpegToJavaErrNo(p_video_state_->StartStream());
}

void FfmpegJavaAvPlayback::SetBalance(float balance) {}

float FfmpegJavaAvPlayback::GetBalance() { return 0.0f; }

void FfmpegJavaAvPlayback::SetAudioSyncDelay(long millis) {}

long FfmpegJavaAvPlayback::getAudioSyncDelay() { return 0; }

int FfmpegJavaAvPlayback::GetImageWidth() const {
  return p_video_state_->GetFrameWidth();
}

int FfmpegJavaAvPlayback::GetImageHeight() const {
  return p_video_state_->GetFrameHeight();
}

bool FfmpegJavaAvPlayback::HasImageData() const {
  return p_video_state_->HasImageStream();
}

bool FfmpegJavaAvPlayback::HasAudioData() const {
  return p_video_state_->HasAudioStream();
}

bool FfmpegJavaAvPlayback::DoDisplay(double *remaining_time) {
  bool display = false;

  double time;

  Frame *sp, *sp2;
  FrameQueue *frame_queue = nullptr;
  p_video_state_->GetImageFrameQueue(&frame_queue);

  if (p_video_state_->HasImageStream()) {
  retry:
    if (frame_queue->GetNumToDisplay() == 0) {
      // nothing to do, no picture to display in the queue
    } else {
      double last_duration, duration, delay;
      Frame *vp = nullptr;
      Frame *lastvp = nullptr;
      PacketQueue *packet_queue = nullptr;
      p_video_state_->GetImagePacketQueue(&packet_queue);

      /* dequeue the picture */
      frame_queue->PeekLast(&lastvp);
      frame_queue->Peek(&vp);

      if (vp->serial_ != packet_queue->GetSerial()) {
        frame_queue->Next();
        goto retry;
      }

      if (lastvp->serial_ != vp->serial_)
        frame_last_shown_time_ = av_gettime_relative() / 1000000.0;

      if (p_video_state_->IsPaused() && !force_refresh_)
        goto display;

      /* compute nominal last_duration */
      last_duration = ComputeFrameDuration(
          lastvp, vp, p_video_state_->GetMaxFrameDuration());
      delay = p_video_state_->ComputeTargetDelay(last_duration);

      time = av_gettime_relative() / 1000000.0;
      if (time < frame_last_shown_time_ + delay) {
        *remaining_time =
            FFMIN(frame_last_shown_time_ + delay - time, *remaining_time);
        goto display;
      }

      frame_last_shown_time_ += delay;
      if (delay > 0 &&
          time - frame_last_shown_time_ > VideoState::kAvSyncThresholdMax)
        frame_last_shown_time_ = time;

      std::unique_lock<std::mutex> locker(frame_queue->GetMutex());
      if (!isnan(vp->pts_)) {
        p_video_state_->SetPts(vp->pts_, vp->serial_);
      }
      locker.unlock();

      if (frame_queue->GetNumToDisplay() > 1) {
        Frame *nextvp = nullptr;
        frame_queue->PeekNext(&nextvp);
        duration = ComputeFrameDuration(vp, nextvp,
                                        p_video_state_->GetMaxFrameDuration());
        if (!p_video_state_->IsStepping() &&
            time > frame_last_shown_time_ + duration) {
          num_frame_drops_late_++;
          frame_queue->Next();
          goto retry;
        }
      }

      frame_queue->Next();
      force_refresh_ = true;
    }
  display:
    /* display picture */
    if (!display_disabled_ && force_refresh_ && frame_queue->HasShownFrame()) {
      display = true;
      force_refresh_ = false;
      if (p_video_state_->IsStepping() && !p_video_state_->IsPaused())
        TogglePause();
    }
  }

  if (kEnableShowStatus) {
    static int64_t last_time;
    int64_t cur_time;
    int aqsize, vqsize, sqsize;
    double av_diff;
    PacketQueue *image_packet_queue = nullptr;
    PacketQueue *audio_packet_queue = nullptr;
    p_video_state_->GetImagePacketQueue(&image_packet_queue);
    p_video_state_->GetAudioPacketQueue(&audio_packet_queue);
    Clock *p_master_clock = nullptr;
    Clock *p_image_clock = nullptr;
    Clock *p_audio_clock = nullptr;
    p_video_state_->GetMasterClock(&p_master_clock);
    p_video_state_->GetImageClock(&p_image_clock);
    p_video_state_->GetAudioClock(&p_audio_clock);
    Decoder *p_decoder = nullptr;
    p_video_state_->GetImageDecoder(&p_decoder);

    cur_time = av_gettime_relative();
    if (!last_time || (cur_time - last_time) >= 30000) {
      aqsize = 0;
      vqsize = 0;
      sqsize = 0;
      if (p_video_state_->HasAudioStream())
        aqsize = audio_packet_queue->GetSize();
      if (p_video_state_->HasImageStream())
        vqsize = image_packet_queue->GetSize();
      av_diff = 0;
      if (p_video_state_->HasAudioStream() && p_video_state_->HasImageStream())
        av_diff = p_audio_clock->GetTime() - p_image_clock->GetTime();
      else if (p_video_state_->HasImageStream())
        av_diff = p_master_clock->GetTime() - p_image_clock->GetTime();
      else if (p_video_state_->HasAudioStream())
        av_diff = p_master_clock->GetTime() - p_audio_clock->GetTime();
      av_log(
          NULL, AV_LOG_INFO,
          "m %7.2f, a %7.2f, v %7.2f at %1.3fX %s:%7.3f de=%4d dl=%4d "
          "re=%7.2f aq=%5dKB vq=%5dKB sq=%5dB f=%f /%f   \r",
          p_master_clock->GetTime(),
          p_audio_clock != nullptr ? p_audio_clock->GetTime() : 0,
          p_image_clock != nullptr ? p_image_clock->GetTime() : 0,
          p_video_state_->GetSpeed(),
          (p_video_state_->HasAudioStream() && p_video_state_->HasImageStream())
              ? "A-V"
              : (p_video_state_->HasImageStream()
                     ? "M-V"
                     : (p_video_state_->HasAudioStream() ? "M-A" : "   ")),
          av_diff, p_video_state_->GetNumFrameDropsEarly(),
          num_frame_drops_late_, *remaining_time, aqsize / 1024, vqsize / 1024,
          sqsize,
          p_video_state_->HasImageStream()
              ? p_decoder->GetNumberOfIncorrectDtsValues()
              : 0,
          p_video_state_->HasImageStream()
              ? p_decoder->GetNumberOfIncorrectPtsValues()
              : 0);
      fflush(stdout);
      last_time = cur_time;
    }
  }

  return display;
}

void FfmpegJavaAvPlayback::UpdateImageBuffer(uint8_t *p_image_data,
                                             const long len) {
  bool doUpdate = DoDisplay(&remaining_time_to_display_);
  FrameQueue *queue = nullptr;
  p_video_state_->GetImageFrameQueue(&queue);
  if (doUpdate) {
    Frame *vp = nullptr;
    queue->PeekLast(&vp);
    p_img_convert_ctx_ = sws_getCachedContext(
        p_img_convert_ctx_, vp->p_frame_->width, vp->p_frame_->height,
        static_cast<AVPixelFormat>(vp->p_frame_->format), vp->p_frame_->width,
        vp->p_frame_->height, kPtrPixelFormat->pixel_format_, SWS_BICUBIC, NULL,
        NULL, NULL);
    if (p_img_convert_ctx_ != NULL) {
      // TODO(fraudies): Add switch case statement for the different pixel
      // formats Left the pixels allocation/free here to support resizing
      // through sws_scale natively
      uint8_t *pixels[4];
      int pitch[4];
      av_image_alloc(pixels, pitch, vp->width_, vp->height_, AV_PIX_FMT_RGB24,
                     1);
      sws_scale(p_img_convert_ctx_, (const uint8_t *const *)vp->p_frame_->data,
                vp->p_frame_->linesize, 0, vp->p_frame_->height, pixels, pitch);
      // Maybe check that we have 3 components
      memcpy(p_image_data, pixels[0],
             vp->p_frame_->width * vp->p_frame_->height * 3 * sizeof(uint8_t));
      av_freep(&pixels[0]);
    }
  }
}

void FfmpegJavaAvPlayback::UpdateAudioBuffer(uint8_t *p_audio_data,
                                             const long len) {
  p_video_state_->GetAudioCallback(p_audio_data, len);
}

void FfmpegJavaAvPlayback::GetAudioFormat(AudioFormat *p_audio_format) {
  *p_audio_format = *this->kPtrAudioFormat;
}

void FfmpegJavaAvPlayback::GetPixelFormat(PixelFormat *p_pixel_format) {
  *p_pixel_format = *this->kPtrPixelFormat;
}
