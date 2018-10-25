#include "FfmpegSdlAvPlayback.h"
#include "FfmpegErrorUtils.h"
#include "MediaPlayerErrors.h"

int FfmpegSdlAvPlayback::kDefaultWidth = 640;
int FfmpegSdlAvPlayback::kDefaultHeight = 480;
unsigned FfmpegSdlAvPlayback::kSwsFlags = SWS_BICUBIC;
const char *FfmpegSdlAvPlayback::kDefaultWindowTitle = "Ffmpeg SDL player";
int FfmpegSdlAvPlayback::kWindowResizable = 1;

/* Minimum SDL audio buffer size, in samples. */
int FfmpegSdlAvPlayback::kAudioMinBufferSize = 512;
/* Calculate actual buffer size keeping in mind not cause too frequent audio
 * callbacks */
int FfmpegSdlAvPlayback::kAudioMaxCallbackPerSec = 30;
/* Step size for volume control in dB */
double FfmpegSdlAvPlayback::kVolumeStepInDecibel = 0.75;
/* polls for possible required screen refresh at least this often, should be
 * less than 1/fps */
double FfmpegSdlAvPlayback::kRefreshRate = 0.01;
int FfmpegSdlAvPlayback::kCursorHideDelayInMillis = 1000000;
/* initialize the texture format map */
const FfmpegSdlAvPlayback::TextureFormatEntry
    FfmpegSdlAvPlayback::kTextureFormatMap[] = {
        {AV_PIX_FMT_RGB8, SDL_PIXELFORMAT_RGB332},
        {AV_PIX_FMT_RGB444, SDL_PIXELFORMAT_RGB444},
        {AV_PIX_FMT_RGB555, SDL_PIXELFORMAT_RGB555},
        {AV_PIX_FMT_BGR555, SDL_PIXELFORMAT_BGR555},
        {AV_PIX_FMT_RGB565, SDL_PIXELFORMAT_RGB565},
        {AV_PIX_FMT_BGR565, SDL_PIXELFORMAT_BGR565},
        {AV_PIX_FMT_RGB24, SDL_PIXELFORMAT_RGB24},
        {AV_PIX_FMT_BGR24, SDL_PIXELFORMAT_BGR24},
        {AV_PIX_FMT_0RGB32, SDL_PIXELFORMAT_RGB888},
        {AV_PIX_FMT_0BGR32, SDL_PIXELFORMAT_BGR888},
        {AV_PIX_FMT_NE(RGB0, 0BGR), SDL_PIXELFORMAT_RGBX8888},
        {AV_PIX_FMT_NE(BGR0, 0RGB), SDL_PIXELFORMAT_BGRX8888},
        {AV_PIX_FMT_RGB32, SDL_PIXELFORMAT_ARGB8888},
        {AV_PIX_FMT_RGB32_1, SDL_PIXELFORMAT_RGBA8888},
        {AV_PIX_FMT_BGR32, SDL_PIXELFORMAT_ABGR8888},
        {AV_PIX_FMT_BGR32_1, SDL_PIXELFORMAT_BGRA8888},
        {AV_PIX_FMT_YUV420P, SDL_PIXELFORMAT_IYUV},
        {AV_PIX_FMT_YUYV422, SDL_PIXELFORMAT_YUY2},
        {AV_PIX_FMT_UYVY422, SDL_PIXELFORMAT_UYVY},
        {AV_PIX_FMT_NONE, SDL_PIXELFORMAT_UNKNOWN},
};

void FfmpegSdlAvPlayback::SetSize(int width, int height) {
  screen_width_ = frame_width_ = width;
  screen_height_ = frame_height_ = height;
  if (p_vis_texture_) {
    SDL_DestroyTexture(p_vis_texture_);
    p_vis_texture_ = NULL;
  }
}

void FfmpegSdlAvPlayback::CalculateRectangleForDisplay(
    SDL_Rect *rect, int scr_xleft, int scr_ytop, int scr_width, int scr_height,
    int frame_width, int frame_height, AVRational frame_aspcet_ratio) {
  int width, height, x, y;
  float aspect_ratio =
      frame_aspcet_ratio.num == 0 ? 0 : av_q2d(frame_aspcet_ratio);

  if (aspect_ratio <= 0.0) {
    aspect_ratio = 1.0;
  }
  aspect_ratio *= (float)frame_width / (float)frame_height;

  // We suppose the screen has a 1.0 pixel ratio
  height = scr_height;
  width = lrint(height * aspect_ratio) & ~1;
  if (width > scr_width) {
    width = scr_width;
    height = lrint(width / aspect_ratio) & ~1;
  }
  x = (scr_width - width) / 2;
  y = (scr_height - height) / 2;
  rect->x = scr_xleft + x;
  rect->y = scr_ytop + y;
  rect->w = FFMAX(width, 1);
  rect->h = FFMAX(height, 1);
}

FfmpegSdlAvPlayback::FfmpegSdlAvPlayback(int startup_volume)
    : FfmpegAvPlayback(), y_top_(0), x_left_(0), p_window_(nullptr),
      p_renderer_(nullptr), p_img_convert_ctx_(nullptr),
      p_vis_texture_(nullptr), p_vid_texture_(nullptr), screen_width_(0),
      screen_height_(0), enabled_full_screen_(0), audio_volume_(0),
      cursor_last_shown_time_(0), is_cursor_hidden_(false),
      renderer_info_({0}) {

  if (startup_volume < 0) {
    av_log(NULL, AV_LOG_WARNING, "-volume=%d < 0, setting to 0\n",
           startup_volume);
  }
  if (startup_volume > 100) {
    av_log(NULL, AV_LOG_WARNING, "-volume=%d > 100, setting to 100\n",
           startup_volume);
  }
  audio_volume_ =
      av_clip(SDL_MIX_MAXVOLUME * av_clip(startup_volume, 0, 100) / 100, 0,
              SDL_MIX_MAXVOLUME);
}

FfmpegSdlAvPlayback::~FfmpegSdlAvPlayback() {

  StopDisplayLoop();

  if (audio_dev_) {
    CloseAudio();
  }

  delete p_video_state_;

  // Cleanup textures
  if (p_vis_texture_) {
    SDL_DestroyTexture(p_vis_texture_);
  }

  if (p_vid_texture_) {
    SDL_DestroyTexture(p_vid_texture_);
  }

  // Cleanup resampling
  sws_freeContext(p_img_convert_ctx_);

  // Cleanup SDL components
  if (p_renderer_) {
    SDL_DestroyRenderer(p_renderer_);
  }

  if (p_window_) {
    SDL_DestroyWindow(p_window_);
  }

  avformat_network_deinit();

  av_free(p_window_title_);

  SDL_Quit();

  av_log(NULL, AV_LOG_QUIET, "%s", "");
}

int FfmpegSdlAvPlayback::OpenVideo(const char *filename,
                                   AVInputFormat *iformat) {
  /* register all codecs, demux and protocols */
#if CONFIG_AVDEVICE
  avdevice_register_all();
#endif
  avformat_network_init();

  int err = FfmpegAvPlayback::OpenVideo(filename, iformat, kAudioMinBufferSize);
  if (err) {
    return err;
  }

  if (!p_window_title_) {
    p_window_title_ = av_asprintf("%s", filename);
  }

  // Set callback functions
  p_video_state_->SetAudioOpenCallback(
      [this](int64_t wanted_channel_layout, int wanted_nb_channels,
             int wanted_sample_rate, struct AudioParams *audio_hw_params) {
        return this->OpenAudio(wanted_channel_layout, wanted_nb_channels,
                               wanted_sample_rate, audio_hw_params);
      });
  p_video_state_->SetPauseAudioDeviceCallback(
      [this] { this->PauseAudio(); });
  p_video_state_->SetDestroyCallback([this] { delete this; });
  p_video_state_->SetStepToNextFrameCallback(
      [this] { this->StepToNextFrame(); });

  return ERROR_NONE;
}

/* Public Members */
int FfmpegSdlAvPlayback::OpenAudio(int64_t wanted_channel_layout,
                                   int wanted_nb_channels,
                                   int wanted_sample_rate,
                                   struct AudioParams *audio_hw_params) {
  SDL_AudioSpec wanted_spec, spec;
  const char *env;
  static const int next_nb_channels[] = {0, 0, 1, 6, 2, 6, 4, 6};
  static const int next_sample_rates[] = {0, 44100, 48000, 96000, 192000};
  int next_sample_rate_idx = FF_ARRAY_ELEMS(next_sample_rates) - 1;

  env = SDL_getenv("SDL_AUDIO_CHANNELS");
  if (env) {
    wanted_nb_channels = atoi(env);
    wanted_channel_layout = av_get_default_channel_layout(wanted_nb_channels);
  }
  if (!wanted_channel_layout ||
      wanted_nb_channels !=
          av_get_channel_layout_nb_channels(wanted_channel_layout)) {
    wanted_channel_layout = av_get_default_channel_layout(wanted_nb_channels);
    wanted_channel_layout &= ~AV_CH_LAYOUT_STEREO_DOWNMIX;
  }
  wanted_nb_channels = av_get_channel_layout_nb_channels(wanted_channel_layout);
  wanted_spec.channels = wanted_nb_channels;
  wanted_spec.freq = wanted_sample_rate;
  if (wanted_spec.freq <= 0 || wanted_spec.channels <= 0) {
    av_log(NULL, AV_LOG_ERROR, "Invalid sample rate or channel count!\n");
    return -1;
  }
  while (next_sample_rate_idx &&
         next_sample_rates[next_sample_rate_idx] >= wanted_spec.freq)
    next_sample_rate_idx--;
  wanted_spec.format = AUDIO_S16SYS;
  wanted_spec.silence = 0;
  wanted_spec.samples =
      FFMAX(kAudioMinBufferSize,
            2 << av_log2(wanted_spec.freq / kAudioMaxCallbackPerSec));
  wanted_spec.callback = sdl_audio_callback_bridge;
  wanted_spec.userdata = this;
  while (!(audio_dev_ =
               SDL_OpenAudioDevice(NULL, 0, &wanted_spec, &spec,
                                   SDL_AUDIO_ALLOW_FREQUENCY_CHANGE |
                                       SDL_AUDIO_ALLOW_CHANNELS_CHANGE))) {
    av_log(NULL, AV_LOG_WARNING, "SDL_OpenAudio (%d channels, %d Hz): %s\n",
           wanted_spec.channels, wanted_spec.freq, SDL_GetError());
    wanted_spec.channels = next_nb_channels[FFMIN(7, wanted_spec.channels)];
    if (!wanted_spec.channels) {
      wanted_spec.freq = next_sample_rates[next_sample_rate_idx--];
      wanted_spec.channels = wanted_nb_channels;
      if (!wanted_spec.freq) {
        av_log(NULL, AV_LOG_ERROR,
               "No more combinations to try, audio open failed\n");
        return -1;
      }
    }
    wanted_channel_layout = av_get_default_channel_layout(wanted_spec.channels);
  }
  if (spec.format != AUDIO_S16SYS) {
    av_log(NULL, AV_LOG_ERROR,
           "SDL advised audio format %d is not supported!\n", spec.format);
    return -1;
  }
  if (spec.channels != wanted_spec.channels) {
    wanted_channel_layout = av_get_default_channel_layout(spec.channels);
    if (!wanted_channel_layout) {
      av_log(NULL, AV_LOG_ERROR,
             "SDL advised channel count %d is not supported!\n", spec.channels);
      return -1;
    }
  }

  audio_hw_params->fmt = AV_SAMPLE_FMT_S16;
  audio_hw_params->freq = spec.freq;
  audio_hw_params->channel_layout = wanted_channel_layout;
  audio_hw_params->channels = spec.channels;
  audio_hw_params->frame_size = av_samples_get_buffer_size(
      NULL, audio_hw_params->channels, 1, audio_hw_params->fmt, 1);
  audio_hw_params->bytes_per_sec = av_samples_get_buffer_size(
      NULL, audio_hw_params->channels, audio_hw_params->freq,
      audio_hw_params->fmt, 1);
  if (audio_hw_params->bytes_per_sec <= 0 || audio_hw_params->frame_size <= 0) {
    av_log(NULL, AV_LOG_ERROR, "av_samples_get_buffer_size failed\n");
    return -1;
  }
  return spec.size;
}

int FfmpegSdlAvPlayback::OpenWindow(const char *window_name) {

  if (IsScreenSizeSet()) {
    frame_width_ = screen_width_;
    frame_height_ = screen_height_;
  } else {
    frame_width_ = kDefaultWidth;
    frame_height_ = kDefaultHeight;
  }
  p_window_title_ = av_strdup(window_name);

  SDL_SetWindowTitle(p_window_, p_window_title_);
  SDL_SetWindowSize(p_window_, frame_width_, frame_height_);
  SDL_SetWindowPosition(p_window_, SDL_WINDOWPOS_CENTERED,
                        SDL_WINDOWPOS_CENTERED);
  if (enabled_full_screen_) {
    SDL_SetWindowFullscreen(p_window_, SDL_WINDOW_FULLSCREEN_DESKTOP);
  }
  SDL_ShowWindow(p_window_);

  return 0;
}

void FfmpegSdlAvPlayback::SetDefaultWindowSize(int width, int height,
                                               AVRational sar) {
  SDL_Rect rect;
  CalculateRectangleForDisplay(&rect, 0, 0, INT_MAX, height, width, height,
                               sar);
  kDefaultWidth = rect.w;
  kDefaultHeight = rect.h;
}

int FfmpegSdlAvPlayback::UploadTexture(SDL_Texture **tex, AVFrame *frame,
                                       struct SwsContext **img_convert_ctx) {
  int ret = 0;
  Uint32 sdl_pix_fmt;
  SDL_BlendMode sdl_blendmode;
  GetPixelFormatAndBlendmode(frame->format, &sdl_pix_fmt, &sdl_blendmode);
  if (ReallocateTexture(tex,
                        sdl_pix_fmt == SDL_PIXELFORMAT_UNKNOWN
                            ? SDL_PIXELFORMAT_ARGB8888
                            : sdl_pix_fmt,
                        frame->width, frame->height, sdl_blendmode, 0) < 0)
    return -1;
  switch (sdl_pix_fmt) {
  case SDL_PIXELFORMAT_UNKNOWN:
    /* This should only happen if we are not using avfilter... */
    *img_convert_ctx = sws_getCachedContext(
        *img_convert_ctx, frame->width, frame->height,
        static_cast<AVPixelFormat>(frame->format), frame->width, frame->height,
        AV_PIX_FMT_BGRA, kSwsFlags, NULL, NULL, NULL);
    if (*img_convert_ctx != NULL) {
      uint8_t *pixels[4];
      int pitch[4];
      if (!SDL_LockTexture(*tex, NULL, (void **)pixels, pitch)) {
        sws_scale(*img_convert_ctx, (const uint8_t *const *)frame->data,
                  frame->linesize, 0, frame->height, pixels, pitch);
        SDL_UnlockTexture(*tex);
      }
    } else {
      av_log(NULL, AV_LOG_FATAL, "Cannot initialize the conversion context\n");
      ret = -1;
    }
    break;
  case SDL_PIXELFORMAT_IYUV:
    if (frame->linesize[0] > 0 && frame->linesize[1] > 0 &&
        frame->linesize[2] > 0) {
      ret = SDL_UpdateYUVTexture(*tex, NULL, frame->data[0], frame->linesize[0],
                                 frame->data[1], frame->linesize[1],
                                 frame->data[2], frame->linesize[2]);
    } else if (frame->linesize[0] < 0 && frame->linesize[1] < 0 &&
               frame->linesize[2] < 0) {
      ret = SDL_UpdateYUVTexture(
          *tex, NULL, frame->data[0] + frame->linesize[0] * (frame->height - 1),
          -frame->linesize[0],
          frame->data[1] +
              frame->linesize[1] * (AV_CEIL_RSHIFT(frame->height, 1) - 1),
          -frame->linesize[1],
          frame->data[2] +
              frame->linesize[2] * (AV_CEIL_RSHIFT(frame->height, 1) - 1),
          -frame->linesize[2]);
    } else {
      av_log(NULL, AV_LOG_ERROR,
             "Mixed negative and positive linesizes are not supported.\n");
      return -1;
    }
    break;
  default:
    if (frame->linesize[0] < 0) {
      ret = SDL_UpdateTexture(
          *tex, NULL, frame->data[0] + frame->linesize[0] * (frame->height - 1),
          -frame->linesize[0]);
    } else {
      ret = SDL_UpdateTexture(*tex, NULL, frame->data[0], frame->linesize[0]);
    }
    break;
  }
  return ret;
}

void FfmpegSdlAvPlayback::GetPixelFormatAndBlendmode(
    int format, Uint32 *sdl_pix_fmt, SDL_BlendMode *sdl_blendmode) {
  int i;
  *sdl_blendmode = SDL_BLENDMODE_NONE;
  *sdl_pix_fmt = SDL_PIXELFORMAT_UNKNOWN;
  if (format == AV_PIX_FMT_RGB32 || format == AV_PIX_FMT_RGB32_1 ||
      format == AV_PIX_FMT_BGR32 || format == AV_PIX_FMT_BGR32_1)
    *sdl_blendmode = SDL_BLENDMODE_BLEND;
  for (i = 0; i < FF_ARRAY_ELEMS(kTextureFormatMap) - 1; i++) {
    if (format == kTextureFormatMap[i].format) {
      *sdl_pix_fmt = kTextureFormatMap[i].texture_fmt;
      return;
    }
  }
}

int FfmpegSdlAvPlayback::ReallocateTexture(SDL_Texture **texture,
                                           Uint32 new_format, int new_width,
                                           int new_height,
                                           SDL_BlendMode blendmode,
                                           int init_texture) {
  Uint32 format;
  int access, w, h;
  if (!*texture || SDL_QueryTexture(*texture, &format, &access, &w, &h) < 0 ||
      new_width != w || new_height != h || new_format != format) {
    void *pixels;
    int pitch;
    if (*texture)
      SDL_DestroyTexture(*texture);
    if (!(*texture = SDL_CreateTexture(p_renderer_, new_format,
                                       SDL_TEXTUREACCESS_STREAMING, new_width,
                                       new_height)))
      return -1;
    if (SDL_SetTextureBlendMode(*texture, blendmode) < 0)
      return -1;
    if (init_texture) {
      if (SDL_LockTexture(*texture, NULL, &pixels, &pitch) < 0)
        return -1;
      memset(pixels, 0, pitch * new_height);
      SDL_UnlockTexture(*texture);
    }
    av_log(NULL, AV_LOG_VERBOSE, "Created %dx%d texture with %s.\n", new_width,
           new_height, SDL_GetPixelFormatName(new_format));
  }
  return 0;
}

void FfmpegSdlAvPlayback::DisplayVideoFrame() {
  if (!frame_width_) {
    OpenWindow(p_video_state_->get_filename());
  }

  SDL_SetRenderDrawColor(p_renderer_, 0, 0, 0, 255);
  SDL_RenderClear(p_renderer_);
  if (p_video_state_->get_video_st()) {
    GetAndDisplayVideoFrame();
  }
  SDL_RenderPresent(p_renderer_);
}

void FfmpegSdlAvPlayback::GetAndDisplayVideoFrame() {
  Frame *p_frame;
  SDL_Rect rect;

  p_frame = p_video_state_->get_pPictq()->peek_last();

  CalculateRectangleForDisplay(&rect, this->x_left_, this->y_top_, this->frame_width_,
                               this->frame_height_, p_frame->width, p_frame->height,
                               p_frame->aspect_ration);

  if (!p_frame->uploaded) {
    if (UploadTexture(&p_vid_texture_, p_frame->frame, &p_img_convert_ctx_) < 0)
      return;
    p_frame->uploaded = 1;
    p_frame->flip_v = p_frame->frame->linesize[0] < 0;
  }

  SDL_RenderCopyEx(p_renderer_, p_vid_texture_, NULL, &rect, 0, NULL,
                   p_frame->flip_v ? SDL_FLIP_VERTICAL : SDL_FLIP_NONE);
}

int FfmpegSdlAvPlayback::GetVolumeStep() const {
  return audio_volume_
             ? (20 * log(audio_volume_ / (double)SDL_MIX_MAXVOLUME) / log(10))
             : -1000.0;
}

void FfmpegSdlAvPlayback::StepVolume(double stepInDecibel) {
  double volume_level = GetVolumeStep();
  int new_volume = lrint(SDL_MIX_MAXVOLUME *
                         pow(10.0, (volume_level + stepInDecibel) / 20.0));
  audio_volume_ =
      av_clip(audio_volume_ == new_volume ? audio_volume_ : new_volume, 0,
              SDL_MIX_MAXVOLUME);
}

void FfmpegSdlAvPlayback::SetVolume(double volume) { audio_volume_ = FFMAX(0, volume); }

void FfmpegSdlAvPlayback::DisplayAndProcessEvent(SDL_Event *event) {
  double remaining_time = 0.0;
  SDL_PumpEvents();
  while (
      !SDL_PeepEvents(event, 1, SDL_GETEVENT, SDL_FIRSTEVENT, SDL_LASTEVENT)) {
    if (!is_cursor_hidden_ && av_gettime_relative() - cursor_last_shown_time_ >
                                  kCursorHideDelayInMillis) {
      SDL_ShowCursor(0);
      is_cursor_hidden_ = true;
    }
    if (remaining_time > 0.0)
      av_usleep((int64_t)(remaining_time * 1000000.0));
    remaining_time = kRefreshRate;
    if (!p_video_state_->get_paused() || force_refresh_)
      UpdateFrame(&remaining_time);
    SDL_PumpEvents();
  }
}

void FfmpegSdlAvPlayback::UpdateFrame(double *remaining_time) {
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

      // Force refresh overrides paused
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
      force_refresh_ = 1;
    }
  display:
    /* display picture */
    if (!display_disabled_ && force_refresh_ &&
        p_video_state_->get_pPictq()->get_rindex_shown()) {
      DisplayVideoFrame();
      force_refresh_ = 0; // only reset force refresh when displayed
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
      if (p_video_state_->get_audio_st()) {
        aqsize = p_video_state_->get_pAudioq()->get_size();
      }
      if (p_video_state_->get_video_st()) {
        vqsize = p_video_state_->get_pVideoq()->get_size();
      }
      av_diff = 0;
      if (p_video_state_->get_audio_st() && p_video_state_->get_video_st()) {
        av_diff = p_video_state_->get_pAudclk()->get_time() -
                  p_video_state_->get_pVidclk()->get_time();
      } else if (p_video_state_->get_video_st()) {
        av_diff = p_video_state_->get_master_clock()->get_time() -
                  p_video_state_->get_pVidclk()->get_time();
      } else if (p_video_state_->get_audio_st()) {
        av_diff = p_video_state_->get_master_clock()->get_time() -
                  p_video_state_->get_pAudclk()->get_time();
      }
      av_log(
          NULL, AV_LOG_INFO,
          "%7.2f at %1.3fX vc=%5.2f %s:%7.3f de=%4d dl=%4d aq=%5dKB "
          "vq=%5dKB sq=%5dB f=%f /%f   \r",
          p_video_state_->get_master_clock()->get_time(),
          p_video_state_->GetSpeed(), p_video_state_->get_pVidclk()->get_time(),
          (p_video_state_->get_audio_st() && p_video_state_->get_video_st())
              ? "A-V"
              : (p_video_state_->get_video_st()
                     ? "M-V"
                     : (p_video_state_->get_audio_st() ? "M-A" : "   ")),
          av_diff, p_video_state_->get_frame_drops_early(), num_frame_drops_late_,
          aqsize / 1024, vqsize / 1024, sqsize,
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
}

void FfmpegSdlAvPlayback::Initialize() {

  if (p_video_state_->GetFrameWidth()) {
    FfmpegSdlAvPlayback::SetDefaultWindowSize(
        p_video_state_->GetFrameWidth(), p_video_state_->GetFrameHeight(),
        p_video_state_->GetFrameAspectRatio());
  }

  if (display_disabled_) {
    p_video_state_->SetVideoDisabled(1);
  }
  int flags = SDL_INIT_VIDEO | SDL_INIT_AUDIO | SDL_INIT_TIMER;
  if (p_video_state_->GetAudioDisabled()) {
    flags &= ~SDL_INIT_AUDIO;
  } else {
    /* Try to work around an occasional ALSA buffer underflow issue when the
     * period size is NPOT due to ALSA resampling by forcing the buffer size. */
    if (!SDL_getenv("SDL_AUDIO_ALSA_SET_BUFFER_SIZE")) {
      SDL_setenv("SDL_AUDIO_ALSA_SET_BUFFER_SIZE", "1", 1);
    }
  }
  if (display_disabled_) {
    flags &= ~SDL_INIT_VIDEO;
  }
  if (SDL_Init(flags)) {
    av_log(NULL, AV_LOG_FATAL, "Could not initialize SDL - %s\n",
           SDL_GetError());
    av_log(NULL, AV_LOG_FATAL, "(Did you set the DISPLAY variable?)\n");
    exit(1);
  }

  SDL_EventState(SDL_SYSWMEVENT, SDL_IGNORE);
  SDL_EventState(SDL_USEREVENT, SDL_IGNORE);

  if (!display_disabled_) {
    int flags = SDL_WINDOW_HIDDEN;
    if (kWindowResizable) {
      flags |= SDL_WINDOW_RESIZABLE;
    } else {
      flags |= SDL_WINDOW_BORDERLESS;
    }
    p_window_ = SDL_CreateWindow(kDefaultWindowTitle, SDL_WINDOWPOS_UNDEFINED,
                                 SDL_WINDOWPOS_UNDEFINED, kDefaultWidth,
                                 kDefaultHeight, flags);
    SDL_SetHint(SDL_HINT_RENDER_SCALE_QUALITY, "linear");
    if (p_window_) {
      p_renderer_ = SDL_CreateRenderer(
          p_window_, -1, SDL_RENDERER_ACCELERATED | SDL_RENDERER_PRESENTVSYNC);
      if (!p_renderer_) {
        av_log(NULL, AV_LOG_WARNING,
               "Failed to initialize a hardware accelerated renderer: %s\n",
               SDL_GetError());
        p_renderer_ = SDL_CreateRenderer(p_window_, -1, 0);
      }
      if (p_renderer_) {
        if (!SDL_GetRendererInfo(p_renderer_, &renderer_info_))
          av_log(NULL, AV_LOG_VERBOSE, "Initialized %s renderer.\n",
                 renderer_info_.name);
      }
    }
    if (!p_window_ || !p_renderer_ || !renderer_info_.num_texture_formats) {
      av_log(NULL, AV_LOG_FATAL, "Failed to create window or renderer: %s",
             SDL_GetError());
      if (p_renderer_)
        SDL_DestroyRenderer(p_renderer_);
      if (p_window_)
        SDL_DestroyWindow(p_window_);
    }
  }
}

void FfmpegSdlAvPlayback::InitializeAndListenForEvents(
    FfmpegSdlAvPlayback *p_player) {
  SDL_Event event;
  double incr, pos, frac, rate;

  // Initialize before starting the stream
  p_player->Initialize();
  VideoState *p_video_state = nullptr;
  p_player->GetVideoState(&p_video_state);
  p_video_state->StartStream();
  rate = 1;

  if (p_video_state->GetFrameWidth()) {
    FfmpegSdlAvPlayback::SetDefaultWindowSize(
        p_video_state->GetFrameWidth(), p_video_state->GetFrameHeight(),
        p_video_state->GetFrameAspectRatio());
  }

  for (;;) {
    double x;
    p_player->DisplayAndProcessEvent(&event);
    switch (event.type) {
    case SDL_KEYDOWN:
      switch (event.key.keysym.sym) {
      case SDLK_ESCAPE:
      case SDLK_q:
        delete p_player;
        exit(0); // need to exit here to avoid joinable exception
        break;
      case SDLK_f:
        p_player->ToggleFullscreen();
        p_player->set_force_refresh(true);
        break;
      case SDLK_KP_8:
        p_player->Play();
        break;
      case SDLK_KP_5:
        p_player->Stop();
        break;
      case SDLK_KP_2:
        p_player->TogglePauseAndStopStep();
        break;
      case SDLK_p:
      case SDLK_SPACE:
        p_player->TogglePauseAndStopStep();
        break;
      case SDLK_m:
        p_video_state->toggle_mute();
        break;
      case SDLK_KP_MULTIPLY:
      case SDLK_0:
        p_player->StepVolume(+kVolumeStepInDecibel);
        break;
      case SDLK_KP_DIVIDE:
      case SDLK_9:
        p_player->StepVolume(-kVolumeStepInDecibel);
        break;
      case SDLK_s: // S: Step to next frame
        p_player->StepToNextFrame();
        break;
      case SDLK_KP_PLUS:
        if (p_video_state->SetSpeed(rate * 2)) {
          av_log(NULL, AV_LOG_ERROR, "Rate %f unavailable\n", rate * 2);
        } else {
          rate *= 2;
        }
        break;
      case SDLK_KP_MINUS:
        if (p_video_state->SetSpeed(rate / 2)) {
          av_log(NULL, AV_LOG_ERROR, "Rate %f unavailable\n", rate / 2);
        } else {
          rate /= 2;
        }
        break;
      case SDLK_LEFT:
        incr = -1.0;
        goto do_seek;
      case SDLK_RIGHT:
        incr = 1.0;
        goto do_seek;
      case SDLK_UP:
        incr = 5.0;
        goto do_seek;
      case SDLK_DOWN:
        incr = -5.0;
      do_seek:
        // TODO FIX SEEK BY BYTES BUG
        if (VideoState::kEnableSeekByBytes) {
          pos = -1;
          if (pos < 0 && p_video_state->get_video_stream() >= 0)
            pos = p_video_state->get_pPictq()->last_pos();
          if (pos < 0 && p_video_state->get_audio_stream() >= 0)
            pos = p_video_state->get_pSampq()->last_pos();
          if (pos < 0)
            pos = avio_tell(p_video_state->get_ic()->pb);
          if (p_video_state->get_ic()->bit_rate)
            incr *= p_video_state->get_ic()->bit_rate / 8.0;
          else
            incr *= 180000.0;
          pos += incr;
          p_video_state->stream_seek(pos, incr, 1);
        } else {
          pos = p_video_state->get_master_clock()->get_time();
          if (isnan(pos)) {
            pos = (double)p_video_state->get_seek_pos() / AV_TIME_BASE;
          }
          pos += incr;
          if (p_video_state->get_ic()->start_time != AV_NOPTS_VALUE &&
              pos < p_video_state->get_ic()->start_time / (double)AV_TIME_BASE)
            pos = p_video_state->get_ic()->start_time / (double)AV_TIME_BASE;
          p_video_state->stream_seek((int64_t)(pos * AV_TIME_BASE),
                                     (int64_t)(incr * AV_TIME_BASE), 0);
        }
        break;
      default:
        break;
      }
      break;
    case SDL_MOUSEBUTTONDOWN:
      if (event.button.button == SDL_BUTTON_LEFT) {
        static int64_t last_mouse_left_click = 0;
        if (av_gettime_relative() - last_mouse_left_click <= 500000) {
          p_player->ToggleFullscreen();
          p_player->set_force_refresh(true);
          last_mouse_left_click = 0;
        } else {
          last_mouse_left_click = av_gettime_relative();
        }
      }
    case SDL_MOUSEMOTION:
      if (p_player->IsCursorHidden()) {
        SDL_ShowCursor(1);
        p_player->SetIsCursorHidden(false);
      }
      p_player->SetCursorLastShownTime(av_gettime_relative());
      int width;
      int height;
      p_player->GetSize(&width, &height);
      if (event.type == SDL_MOUSEBUTTONDOWN) {
        if (event.button.button != SDL_BUTTON_RIGHT)
          break;
        x = event.button.x;
      } else {
        if (!(event.motion.state & SDL_BUTTON_RMASK))
          break;
        x = event.motion.x;
      }
      if (VideoState::kEnableSeekByBytes ||
          p_video_state->get_ic()->duration <= 0) {
        uint64_t size = avio_size(p_video_state->get_ic()->pb);
        p_video_state->stream_seek(size * x / width, 0, 1);
      } else {
        int64_t ts;
        int ns, hh, mm, ss;
        int tns, thh, tmm, tss;
        tns = p_video_state->get_ic()->duration / 1000000LL;
        thh = tns / 3600;
        tmm = (tns % 3600) / 60;
        tss = (tns % 60);
        frac = x / width;
        ns = frac * tns;
        hh = ns / 3600;
        mm = (ns % 3600) / 60;
        ss = (ns % 60);
        av_log(NULL, AV_LOG_INFO,
               "Seek to %2.0f%% (%2d:%02d:%02d) of total duration "
               "(%2d:%02d:%02d)       \n",
               frac * 100, hh, mm, ss, thh, tmm, tss);
        ts = frac * p_video_state->get_ic()->duration;
        if (p_video_state->get_ic()->start_time != AV_NOPTS_VALUE)
          ts += p_video_state->get_ic()->start_time;
        p_video_state->stream_seek(ts, 0, 0);
      }
      break;
    case SDL_WINDOWEVENT:
      switch (event.window.event) {
      case SDL_WINDOWEVENT_RESIZED:
        p_player->SetSize(event.window.data1, event.window.data2);
      case SDL_WINDOWEVENT_EXPOSED:
        p_player->set_force_refresh(true);
      }
      break;
    case SDL_QUIT:
    case FF_QUIT_EVENT:
      delete p_player;
      exit(0); // need to exit here to avoid joinable exception
      break;
    default:
      break;
    }
  }
}

int FfmpegSdlAvPlayback::InitializeAndStartDisplayLoop() {
  std::mutex mtx;
  std::condition_variable cv;
  bool is_initialized = false;

  p_display_thread_id_ =
      new (std::nothrow) std::thread([this, &is_initialized, &cv] {
        Initialize();
        is_initialized = true;
        cv.notify_all();

        SDL_Event event;
        while (!is_stopped_) {
          DisplayAndProcessEvent(&event);
          // Add handling of resizing the window
          switch (event.type) {
          case SDL_WINDOWEVENT:
            switch (event.window.event) {
            case SDL_WINDOWEVENT_RESIZED:
              screen_width_ = frame_width_ = event.window.data1;
              screen_height_ = frame_height_ = event.window.data2;
              if (p_vis_texture_) {
                SDL_DestroyTexture(p_vis_texture_);
                p_vis_texture_ = NULL;
              }
            case SDL_WINDOWEVENT_EXPOSED:
              force_refresh_ = 1;
            }
            break;
          default:
            break;
          }
        }
      });

  if (!p_display_thread_id_) {
    av_log(NULL, AV_LOG_ERROR, "Unable to create playback thread");
    return -1;
  }

  std::unique_lock<std::mutex> lck(mtx);
  cv.wait(lck, [&is_initialized] { return is_initialized; });
  int err = ffmpegToJavaErrNo(p_video_state_->StartStream());
  if (err)
    return err;

  if (p_video_state_->GetFrameWidth()) {
    FfmpegSdlAvPlayback::SetDefaultWindowSize(
        p_video_state_->GetFrameWidth(), p_video_state_->GetFrameHeight(),
        p_video_state_->GetFrameAspectRatio());
  }

  return 0;
}
