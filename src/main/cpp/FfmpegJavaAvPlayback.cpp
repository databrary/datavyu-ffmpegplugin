#include "FfmpegJavaAvPlayback.h"
#include "FfmpegErrorUtils.h"
#include "MediaPlayerErrors.h"

FfmpegJavaAvPlayback::FfmpegJavaAvPlayback(const AudioFormat *pAudioFormat,
                                           const PixelFormat *pPixelFormat,
                                           const int audioBufferSizeInBy)
    : FfmpegAvPlayback(), kPtrAudioFormat(pAudioFormat),
      kPtrPixelFormat(pPixelFormat), kAudioBufferSizeInBy(audioBufferSizeInBy),
      img_convert_ctx_(nullptr), remaining_time_to_display_(0) {}

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
  kPtrAudioFormat->toAudioParams(audio_hw_params);
  return kAudioBufferSizeInBy;
}

void FfmpegJavaAvPlayback::Destroy() {

  sws_freeContext(img_convert_ctx_);

  delete p_video_state_;
  avformat_network_deinit();

  av_log(NULL, AV_LOG_QUIET, "%s", "");
}

int FfmpegJavaAvPlayback::StartStream() {
  return ffmpegToJavaErrNo(p_video_state_->StartStream());
}

void FfmpegJavaAvPlayback::SetBalance(float fBalance) {}

float FfmpegJavaAvPlayback::GetBalance() { return 0.0f; }

void FfmpegJavaAvPlayback::SetAudioSyncDelay(long lMillis) {}

long FfmpegJavaAvPlayback::getAudioSyncDelay() { return 0; }

int FfmpegJavaAvPlayback::GetImageWidth() const {
  return p_video_state_->GetFrameWidth();
}

int FfmpegJavaAvPlayback::GetImageHeight() const {
  return p_video_state_->GetFrameHeight();
}

bool FfmpegJavaAvPlayback::HasImageData() const {
  return p_video_state_->has_image_data();
}

bool FfmpegJavaAvPlayback::HasAudioData() const {
  return p_video_state_->has_audio_data();
}

bool FfmpegJavaAvPlayback::DoDisplay(double *remaining_time) {
  bool display = false;

  double time;

  Frame *sp, *sp2;

  if (p_video_state_->get_video_st()) {
  retry:
    if (p_video_state_->get_pPictq()->nb_remaining() == 0) {
      // nothing to do, no picture to display in the queue
    } else {
      double last_duration, duration, delay;
      Frame *vp, *lastvp;

      /* dequeue the picture */
      lastvp = p_video_state_->get_pPictq()->peek_last();
      vp = p_video_state_->get_pPictq()->peek();

      if (vp->serial != p_video_state_->get_pVideoq()->get_serial()) {
        p_video_state_->get_pPictq()->next();
        goto retry;
      }

      if (lastvp->serial != vp->serial)
        frame_last_shown_time_ = av_gettime_relative() / 1000000.0;

      if (p_video_state_->get_paused() && !force_refresh_)
        goto display;

      /* compute nominal last_duration */
      last_duration =
          ComputeFrameDuration(lastvp, vp, p_video_state_->get_max_frame_duration());
      delay = p_video_state_->compute_target_delay(last_duration);

      time = av_gettime_relative() / 1000000.0;
      if (time < frame_last_shown_time_ + delay) {
        *remaining_time = FFMIN(frame_last_shown_time_ + delay - time, *remaining_time);
        goto display;
      }

      frame_last_shown_time_ += delay;
      if (delay > 0 && time - frame_last_shown_time_ > VideoState::kAvSyncThresholdMax)
        frame_last_shown_time_ = time;

      std::unique_lock<std::mutex> locker(
          p_video_state_->get_pPictq()->get_mutex());
      if (!isnan(vp->pts))
        p_video_state_->update_pts(vp->pts, vp->serial);
      locker.unlock();

      if (p_video_state_->get_pPictq()->nb_remaining() > 1) {
        Frame *nextvp = p_video_state_->get_pPictq()->peek_next();
        duration =
            ComputeFrameDuration(vp, nextvp, p_video_state_->get_max_frame_duration());
        if (!p_video_state_->IsStepping() && time > frame_last_shown_time_ + duration) {
          num_frame_drops_late_++;
          p_video_state_->get_pPictq()->next();
          goto retry;
        }
      }

      p_video_state_->get_pPictq()->next();
      force_refresh_ = true;
    }
  display:
    /* display picture */
    if (!display_disabled_ && force_refresh_ &&
        p_video_state_->get_pPictq()->get_rindex_shown()) {
      display = true;
      force_refresh_ = false;
      if (p_video_state_->IsStepping() && !p_video_state_->get_paused())
        TogglePause();
    }
  }

  if (kEnableShowStatus) {
    static int64_t last_time;
    int64_t cur_time;
    int aqsize, vqsize, sqsize;
    double av_diff;

    cur_time = av_gettime_relative();
    if (!last_time || (cur_time - last_time) >= 30000) {
      aqsize = 0;
      vqsize = 0;
      sqsize = 0;
      if (p_video_state_->get_audio_st())
        aqsize = p_video_state_->get_pAudioq()->get_size();
      if (p_video_state_->get_video_st())
        vqsize = p_video_state_->get_pVideoq()->get_size();
      av_diff = 0;
      if (p_video_state_->get_audio_st() && p_video_state_->get_video_st())
        av_diff = p_video_state_->get_pAudclk()->get_time() -
                  p_video_state_->get_pVidclk()->get_time();
      else if (p_video_state_->get_video_st())
        av_diff = p_video_state_->get_master_clock()->get_time() -
                  p_video_state_->get_pVidclk()->get_time();
      else if (p_video_state_->get_audio_st())
        av_diff = p_video_state_->get_master_clock()->get_time() -
                  p_video_state_->get_pAudclk()->get_time();
      av_log(NULL, AV_LOG_INFO,
             "m %7.2f, a %7.2f, v %7.2f at %1.3fX %s:%7.3f de=%4d dl=%4d "
             "re=%7.2f aq=%5dKB vq=%5dKB sq=%5dB f=%f /%f   \r",
             p_video_state_->get_master_clock()->get_time(),
             p_video_state_->get_pAudclk() != nullptr
                 ? p_video_state_->get_pAudclk()->get_time()
                 : 0,
             p_video_state_->get_pVidclk() != nullptr
                 ? p_video_state_->get_pVidclk()->get_time()
                 : 0,
             p_video_state_->GetSpeed(),
             (p_video_state_->get_audio_st() && p_video_state_->get_video_st())
                 ? "A-V"
                 : (p_video_state_->get_video_st()
                        ? "M-V"
                        : (p_video_state_->get_audio_st() ? "M-A" : "   ")),
             av_diff, p_video_state_->get_frame_drops_early(), num_frame_drops_late_,
             *remaining_time, aqsize / 1024, vqsize / 1024, sqsize,
             p_video_state_->get_video_st() ? p_video_state_->get_pViddec()
                                               ->get_avctx()
                                               ->pts_correction_num_faulty_dts
                                         : 0,
             p_video_state_->get_video_st() ? p_video_state_->get_pViddec()
                                               ->get_avctx()
                                               ->pts_correction_num_faulty_pts
                                         : 0);
      fflush(stdout);
      last_time = cur_time;
    }
  }

  return display;
}

void FfmpegJavaAvPlayback::UpdateImageBuffer(uint8_t *pImageData,
                                               const long len) {
  bool doUpdate = DoDisplay(&remaining_time_to_display_);
  if (doUpdate) {
    Frame *vp = p_video_state_->get_pPictq()->peek_last();
    img_convert_ctx_ = sws_getCachedContext(
        img_convert_ctx_, vp->frame->width, vp->frame->height,
        static_cast<AVPixelFormat>(vp->frame->format), vp->frame->width,
        vp->frame->height, kPtrPixelFormat->type, SWS_BICUBIC, NULL, NULL, NULL);
    if (img_convert_ctx_ != NULL) {
      // TODO(fraudies): Add switch case statement for the different pixel
      // formats Left the pixels allocation/free here to support resizing
      // through sws_scale natively
      uint8_t *pixels[4];
      int pitch[4];
      av_image_alloc(pixels, pitch, vp->width, vp->height, AV_PIX_FMT_RGB24, 1);
      sws_scale(img_convert_ctx_, (const uint8_t *const *)vp->frame->data,
                vp->frame->linesize, 0, vp->frame->height, pixels, pitch);
      // Maybe check that we have 3 components
      memcpy(pImageData, pixels[0],
             vp->frame->width * vp->frame->height * 3 * sizeof(uint8_t));
      av_freep(&pixels[0]);
    }
  }
}

void FfmpegJavaAvPlayback::UpdateAudioBuffer(uint8_t *pAudioData,
                                               const long len) {
  p_video_state_->audio_callback(pAudioData, len);
}

void FfmpegJavaAvPlayback::GetAudioFormat(AudioFormat *pAudioFormat) {
  *pAudioFormat = *this->kPtrAudioFormat;
}

void FfmpegJavaAvPlayback::GetPixelFormat(PixelFormat *pPixelFormat) {
  *pPixelFormat = *this->kPtrPixelFormat;
}
