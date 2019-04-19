#include "FfmpegSdlAvPlayback.h"
#include "FfmpegErrorUtils.h"
#include "MediaPlayerErrors.h"
#ifdef _WIN32
#include <Basetsd.h>
#elif __APPLE__
#endif


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
    : FfmpegAvPlayback(), y_top_(0), x_left_(0), p_window_(nullptr), p_dummy_window_(nullptr),
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
	p_vis_texture_ = nullptr;
  }

  if (p_vid_texture_) {
    SDL_DestroyTexture(p_vid_texture_);
	p_vid_texture_ = nullptr;
  }

  // Cleanup resampling
  sws_freeContext(p_img_convert_ctx_);
  
  SDL_SetHint(SDL_HINT_VIDEO_WINDOW_SHARE_PIXEL_FORMAT, nullptr);
  if (p_dummy_window_) {
	  SDL_DestroyWindow(p_dummy_window_);
  }

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

int FfmpegSdlAvPlayback::OpenVideo(const char *p_filename,
                                   AVInputFormat *p_input_format) {
  /* register all codecs, demux and protocols */
#if CONFIG_AVDEVICE
  avdevice_register_all();
#endif
  avformat_network_init();

  int err = FfmpegAvPlayback::OpenVideo(p_filename, p_input_format,
                                        kAudioMinBufferSize);
  if (err) {
    return err;
  }

  if (!p_window_title_) {
    p_window_title_ = av_asprintf("%s", p_filename);
  }

  // Set callback functions
  p_video_state_->SetAudioOpenCallback(
      [this](int64_t wanted_channel_layout, int wanted_nb_channels,
             int wanted_sample_rate, struct AudioParams *audio_hw_params) {
        return this->OpenAudio(wanted_channel_layout, wanted_nb_channels,
                               wanted_sample_rate, audio_hw_params);
      });
  p_video_state_->SetPauseAudioDeviceCallback([this] { this->PauseAudio(); });
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

  audio_hw_params->sample_format_ = AV_SAMPLE_FMT_S16;
  audio_hw_params->frequency_ = spec.freq;
  audio_hw_params->channel_layout_ = wanted_channel_layout;
  audio_hw_params->num_channels_ = spec.channels;
  audio_hw_params->frame_size_ =
      av_samples_get_buffer_size(NULL, audio_hw_params->num_channels_, 1,
                                 audio_hw_params->sample_format_, 1);
  audio_hw_params->bytes_per_sec_ = av_samples_get_buffer_size(
      NULL, audio_hw_params->num_channels_, audio_hw_params->frequency_,
      audio_hw_params->sample_format_, 1);
  if (audio_hw_params->bytes_per_sec_ <= 0 ||
      audio_hw_params->frame_size_ <= 0) {
    av_log(NULL, AV_LOG_ERROR, "av_samples_get_buffer_size failed\n");
    return -1;
  }
  return spec.size;
}

int FfmpegSdlAvPlayback::OpenWindow(const char *p_window_name) {

  if (IsScreenSizeSet()) {
    frame_width_ = screen_width_;
    frame_height_ = screen_height_;
  } else {
    frame_width_ = kDefaultWidth;
    frame_height_ = kDefaultHeight;
  }

  // Adjust window size to the display usable bounds 
  SDL_Rect r;
  if (SDL_GetDisplayUsableBounds(0, &r) == 0) {
	if (frame_height_ > r.h) {
	  frame_height_ = r.h;
	} else if (frame_width_ > r.w) {
	  frame_width_ = r.w;
	}
  } else {
	av_log(NULL, AV_LOG_WARNING, "Get Usable Display Failed %s\n", SDL_GetError());
  }

  p_window_title_ = av_strdup(p_window_name);

  SDL_SetWindowTitle(p_window_, p_window_title_);
  SDL_SetWindowSize(p_window_, frame_width_, frame_height_);
  SDL_SetWindowPosition(p_window_, SDL_WINDOWPOS_CENTERED,
                        SDL_WINDOWPOS_CENTERED);
  if (enabled_full_screen_) {
    SDL_SetWindowFullscreen(p_window_, SDL_WINDOW_FULLSCREEN_DESKTOP);
  }
  SDL_ShowWindow(p_window_);

  FfmpegSdlAvPlayback::SetSize(frame_width_, frame_height_);

  return 0;
}

void FfmpegSdlAvPlayback::SetDefaultWindowSize(int width, int height,
                                               AVRational aspect_ratio) {
  SDL_Rect rect;
  CalculateRectangleForDisplay(&rect, 0, 0, INT_MAX, height, width, height,
                               aspect_ratio);
  kDefaultWidth = rect.w;
  kDefaultHeight = rect.h;
}

int FfmpegSdlAvPlayback::UploadTexture(SDL_Texture **pp_texture,
                                       AVFrame *p_frame,
                                       struct SwsContext **pp_img_convert_ctx) {
  int ret = 0;
  Uint32 sdl_pix_fmt;
  SDL_BlendMode sdl_blendmode;
  GetPixelFormatAndBlendmode(p_frame->format, &sdl_pix_fmt, &sdl_blendmode);
  if (ReallocateTexture(
          pp_texture,
          sdl_pix_fmt == SDL_PIXELFORMAT_UNKNOWN ? SDL_PIXELFORMAT_ARGB8888
                                                 : sdl_pix_fmt,
          p_frame->width, p_frame->height, sdl_blendmode, false) < 0)
    return -1;
  switch (sdl_pix_fmt) {
  case SDL_PIXELFORMAT_UNKNOWN:
    /* This should only happen if we are not using avfilter... */
    *pp_img_convert_ctx = sws_getCachedContext(
        *pp_img_convert_ctx, p_frame->width, p_frame->height,
        static_cast<AVPixelFormat>(p_frame->format), p_frame->width,
        p_frame->height, AV_PIX_FMT_BGRA, kSwsFlags, NULL, NULL, NULL);
    if (*pp_img_convert_ctx != NULL) {
      uint8_t *pixels[4];
      int pitch[4];
      if (!SDL_LockTexture(*pp_texture, NULL, (void **)pixels, pitch)) {
        sws_scale(*pp_img_convert_ctx, (const uint8_t *const *)p_frame->data,
                  p_frame->linesize, 0, p_frame->height, pixels, pitch);
        SDL_UnlockTexture(*pp_texture);
      }
    } else {
      av_log(NULL, AV_LOG_FATAL, "Cannot initialize the conversion context\n");
      ret = -1;
    }
    break;
  case SDL_PIXELFORMAT_IYUV:
    if (p_frame->linesize[0] > 0 && p_frame->linesize[1] > 0 &&
        p_frame->linesize[2] > 0) {
      ret = SDL_UpdateYUVTexture(*pp_texture, NULL, p_frame->data[0],
                                 p_frame->linesize[0], p_frame->data[1],
                                 p_frame->linesize[1], p_frame->data[2],
                                 p_frame->linesize[2]);
    } else if (p_frame->linesize[0] < 0 && p_frame->linesize[1] < 0 &&
               p_frame->linesize[2] < 0) {
      ret = SDL_UpdateYUVTexture(
          *pp_texture, NULL,
          p_frame->data[0] + p_frame->linesize[0] * (p_frame->height - 1),
          -p_frame->linesize[0],
          p_frame->data[1] +
              p_frame->linesize[1] * (AV_CEIL_RSHIFT(p_frame->height, 1) - 1),
          -p_frame->linesize[1],
          p_frame->data[2] +
              p_frame->linesize[2] * (AV_CEIL_RSHIFT(p_frame->height, 1) - 1),
          -p_frame->linesize[2]);
    } else {
      av_log(NULL, AV_LOG_ERROR,
             "Mixed negative and positive linesizes are not supported.\n");
      return -1;
    }
    break;
  default:
    if (p_frame->linesize[0] < 0) {
      ret = SDL_UpdateTexture(*pp_texture, NULL,
                              p_frame->data[0] +
                                  p_frame->linesize[0] * (p_frame->height - 1),
                              -p_frame->linesize[0]);
    } else {
      ret = SDL_UpdateTexture(*pp_texture, NULL, p_frame->data[0],
                              p_frame->linesize[0]);
    }
    break;
  }
  return ret;
}

void FfmpegSdlAvPlayback::GetPixelFormatAndBlendmode(
    int format, Uint32 *p_pixel_format, SDL_BlendMode *p_blendmode) {
  int i;
  *p_blendmode = SDL_BLENDMODE_NONE;
  *p_pixel_format = SDL_PIXELFORMAT_UNKNOWN;
  if (format == AV_PIX_FMT_RGB32 || format == AV_PIX_FMT_RGB32_1 ||
      format == AV_PIX_FMT_BGR32 || format == AV_PIX_FMT_BGR32_1)
    *p_blendmode = SDL_BLENDMODE_BLEND;
  for (i = 0; i < FF_ARRAY_ELEMS(kTextureFormatMap) - 1; i++) {
    if (format == kTextureFormatMap[i].format) {
      *p_pixel_format = kTextureFormatMap[i].texture_fmt;
      return;
    }
  }
}

int FfmpegSdlAvPlayback::ReallocateTexture(SDL_Texture **pp_texture,
                                           Uint32 new_format, int new_width,
                                           int new_height,
                                           SDL_BlendMode blendmode,
                                           bool init_texture) {
  Uint32 format;
  int access, w, h;
  if (!*pp_texture ||
      SDL_QueryTexture(*pp_texture, &format, &access, &w, &h) < 0 ||
      new_width != w || new_height != h || new_format != format) {
    void *pixels;
    int pitch;
    if (*pp_texture)
      SDL_DestroyTexture(*pp_texture);
    if (!(*pp_texture = SDL_CreateTexture(p_renderer_, new_format,
                                          SDL_TEXTUREACCESS_STREAMING,
                                          new_width, new_height)))
      return -1;
    if (SDL_SetTextureBlendMode(*pp_texture, blendmode) < 0)
      return -1;
    if (init_texture) {
      if (SDL_LockTexture(*pp_texture, NULL, &pixels, &pitch) < 0)
        return -1;
      memset(pixels, 0, pitch * new_height);
      SDL_UnlockTexture(*pp_texture);
    }
    av_log(NULL, AV_LOG_VERBOSE, "Created %dx%d texture with %s.\n", new_width,
           new_height, SDL_GetPixelFormatName(new_format));
  }
  return 0;
}

void FfmpegSdlAvPlayback::DisplayVideoFrame() {
  if (!frame_width_) {
    char *p_filename = nullptr;
    p_video_state_->GetFilename(&p_filename);
    OpenWindow(p_filename);
  }

  SDL_SetRenderDrawColor(p_renderer_, 0, 0, 0, 255);
  SDL_RenderClear(p_renderer_);
  if (p_video_state_->HasImageStream()) {
    GetAndDisplayVideoFrame();
  }
  SDL_RenderPresent(p_renderer_);
}

void FfmpegSdlAvPlayback::GetAndDisplayVideoFrame() {
  Frame *p_frame = nullptr;
  SDL_Rect rect;
  FrameQueue *queue = nullptr;
  p_video_state_->GetImageFrameQueue(&queue);
  queue->PeekLast(&p_frame);

  CalculateRectangleForDisplay(&rect, this->x_left_, this->y_top_,
                               this->frame_width_, this->frame_height_,
                               p_frame->width_, p_frame->height_,
                               p_frame->aspect_ratio_);

  if (!p_frame->is_uploaded_) {
    if (UploadTexture(&p_vid_texture_, p_frame->p_frame_, &p_img_convert_ctx_) < 0)
      return;
    p_frame->is_uploaded_ = true;
  }

  SDL_RenderCopyEx(p_renderer_, p_vid_texture_, NULL, &rect, 0, NULL,
                   SDL_FLIP_NONE);
}

int FfmpegSdlAvPlayback::GetImageWidth() const {
	return p_video_state_->GetFrameWidth();
}

int FfmpegSdlAvPlayback::GetImageHeight() const {
	return p_video_state_->GetFrameHeight();
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

void FfmpegSdlAvPlayback::SetVolume(double volume) {
  audio_volume_ = FFMAX(0, volume);
}

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
	if (remaining_time > 0.0) {
	  av_usleep((int64_t)(remaining_time * 1000000.0));
	}
    remaining_time = kRefreshRate;
	if (!p_video_state_->IsPaused() || force_refresh_) {
	  UpdateFrame(&remaining_time);
	}
    SDL_PumpEvents();
  }
}

void FfmpegSdlAvPlayback::UpdateFrame(double *remaining_time) {
  double time;

  Frame *sp, *sp2;
  FrameQueue *queue = nullptr;
  p_video_state_->GetImageFrameQueue(&queue);

  if (p_video_state_->HasImageStream()) {
  retry:
    if (queue->GetNumToDisplay() == 0) {
      // nothing to do, no picture to display in the queue
    } else {
      double last_duration, duration, delay;
      Frame *vp = nullptr;
      Frame *lastvp = nullptr;
      PacketQueue *packet_queue = nullptr;
      p_video_state_->GetImagePacketQueue(&packet_queue);

      /* dequeue the picture */
      queue->PeekLast(&lastvp);
      queue->Peek(&vp);

      if (vp->serial_ != packet_queue->GetSerial()) {
        queue->Next();
        goto retry;
      }

      if (lastvp->serial_ != vp->serial_)
        frame_last_shown_time_ = av_gettime_relative() / 1000000.0;

      // Force refresh overrides paused
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

      std::unique_lock<std::mutex> locker(queue->GetMutex());
      if (!isnan(vp->pts_))
        p_video_state_->SetPts(vp->pts_, vp->serial_);
      locker.unlock();

      if (queue->GetNumToDisplay() > 1) {
        Frame *nextvp = nullptr;
        queue->PeekNext(&nextvp);
        duration = ComputeFrameDuration(vp, nextvp,
                                        p_video_state_->GetMaxFrameDuration());
        if (!p_video_state_->IsStepping() &&
            time > frame_last_shown_time_ + duration) {
          num_frame_drops_late_++;
          queue->Next();
          goto retry;
        }
      }

      queue->Next();
      force_refresh_ = 1;
    }
  display:
    /* display picture */
    if (!display_disabled_ && force_refresh_ && queue->HasShownFrame()) {
      DisplayVideoFrame();
      force_refresh_ = 0; // only reset force refresh when displayed
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
      if (p_video_state_->HasAudioStream()) {
        aqsize = audio_packet_queue->GetSize();
      }
      if (p_video_state_->HasImageStream()) {
        vqsize = image_packet_queue->GetSize();
      }
      av_diff = 0;
      if (p_video_state_->HasAudioStream() &&
          p_video_state_->HasImageStream()) {
        av_diff = p_audio_clock->GetTime() - p_image_clock->GetTime();
      } else if (p_video_state_->HasImageStream()) {
        av_diff = p_master_clock->GetTime() - p_image_clock->GetTime();
      } else if (p_video_state_->HasAudioStream()) {
        av_diff = p_master_clock->GetTime() - p_audio_clock->GetTime();
      }
#if _DEBUG
	  av_log(
		  NULL, AV_LOG_INFO,
		  "%7.2f at %1.3fX vc=%5.2f %s:%7.3f de=%4d dl=%4d aq=%5dKB "
		  "vq=%5dKB sq=%5dB f=%f /%f   \r",
		  p_master_clock->GetTime(), p_video_state_->GetSpeed(),
		  p_image_clock->GetTime(),
		  (p_video_state_->HasAudioStream() && p_video_state_->HasImageStream())
		  ? "A-V"
		  : (p_video_state_->HasImageStream()
			  ? "M-V"
			  : (p_video_state_->HasAudioStream() ? "M-A" : "   ")),
		  av_diff, p_video_state_->GetNumFrameDropsEarly(),
		  num_frame_drops_late_, aqsize / 1024, vqsize / 1024, sqsize,
		  p_video_state_->HasImageStream()
		  ? p_decoder->GetNumberOfIncorrectDtsValues()
		  : 0,
		  p_video_state_->HasImageStream()
		  ? p_decoder->GetNumberOfIncorrectPtsValues()
		  : 0);
	  fflush(stdout);
#endif // _DEBUG

      last_time = cur_time;
    }
  }
}

void FfmpegSdlAvPlayback::Initialize(long window_id) {

  if (p_video_state_->GetFrameWidth()) {
    FfmpegSdlAvPlayback::SetDefaultWindowSize(
        p_video_state_->GetFrameWidth(), p_video_state_->GetFrameHeight(),
        p_video_state_->GetFrameAspectRatio());
  }
#ifdef __APPLE__
    SDL_SetMainReady();
#endif
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

    if (window_id != 0) {
        SDL_InitSubSystem(SDL_INIT_VIDEO);
        // Need a dummy window with OpenGL to share pixel format
        //https://discourse.libsdl.org/t/sdl-textureaccess-streaming-window-resize-crash/21761
        p_dummy_window_ = SDL_CreateWindow("", 0, 0, 1, 1, SDL_WINDOW_OPENGL | SDL_WINDOW_HIDDEN);
        char sBuf[32];
#ifdef _WIN32
        sprintf_s<32>(sBuf, "%p", p_dummy_window_);
#elif __APPLE__
        sprintf(sBuf,"%p", p_dummy_window_);
#endif
        SDL_SetHint(SDL_HINT_VIDEO_WINDOW_SHARE_PIXEL_FORMAT, sBuf);
        // Use OpenGL renderer instead of D3D
        //https://stackoverflow.com/questions/40312553/sdl2-crashes-on-window-resize
        SDL_SetHint(SDL_HINT_RENDER_DRIVER, "opengl");
        SDL_EventState(SDL_SYSWMEVENT, SDL_ENABLE);
#ifdef _WIN32
        p_window_ = SDL_CreateWindowFrom(LongToHandle(window_id));
#elif __APPLE__
        intptr_t wid = window_id;
        p_window_ = SDL_CreateWindowFrom(&window_id);
#endif
        char *p_filename = nullptr;
        p_video_state_->GetFilename(&p_filename);
        SDL_SetWindowTitle(p_window_, p_filename);
        SDL_SetWindowResizable(p_window_, SDL_TRUE);
        SDL_SetWindowSize(p_window_, kDefaultWidth, kDefaultHeight);
        SDL_SetWindowPosition(p_window_, SDL_WINDOWPOS_UNDEFINED, SDL_WINDOWPOS_UNDEFINED);
    } else {
      p_window_ = SDL_CreateWindow(kDefaultWindowTitle, SDL_WINDOWPOS_UNDEFINED,
                                 SDL_WINDOWPOS_UNDEFINED, kDefaultWidth,
                                 kDefaultHeight, flags);
    }
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
      if (p_renderer_) {
        SDL_DestroyRenderer(p_renderer_);
      }
      if (p_dummy_window_) {
        SDL_DestroyWindow(p_dummy_window_);
      }
      if (p_window_) {
        SDL_DestroyWindow(p_window_);
      }
    }
  }
}

void FfmpegSdlAvPlayback::InitializeAndListenForEvents(
    FfmpegSdlAvPlayback *p_player, long window_id) {
  SDL_Event event;
  double incr, pos, frac, rate;
  Clock *p_master_clock = nullptr;

  // Initialize before starting the stream
  p_player->Initialize(window_id);
  VideoState *p_video_state = nullptr;
  p_player->GetVideoState(&p_video_state);
  p_video_state->StartStream();
  rate = 1;
  p_video_state->GetMasterClock(&p_master_clock);
  AVFormatContext *p_format_context = nullptr;
  p_video_state->GetFormatContext(&p_format_context);

  if (p_video_state->GetFrameWidth()) {
    FfmpegSdlAvPlayback::SetDefaultWindowSize(
        p_video_state->GetFrameWidth(), p_video_state->GetFrameHeight(),
        p_video_state->GetFrameAspectRatio());
  }

  // Match Datavyu Key event
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
        p_player->SetForceReferesh(true);
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
        p_video_state->ToggleMute();
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
	    case SDLK_b: // S: Step to next frame
        p_player->StepToPreviousFrame();
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
          if (pos < 0 && p_video_state->HasImageStream()) {
            FrameQueue *queue = nullptr;
            p_video_state->GetImageFrameQueue(&queue);
            pos = queue->GetBytePosOfLastFrame();
          }
          if (pos < 0 && p_video_state->HasAudioStream()) {
            FrameQueue *queue = nullptr;
            p_video_state->GetAudioFrameQueue(&queue);
            pos = queue->GetBytePosOfLastFrame();
          }
          if (pos < 0) {
            pos = avio_tell(p_format_context->pb);
          }
          if (p_format_context->bit_rate) {
            incr *= p_format_context->bit_rate / 8.0;
          } else {
            incr *= 180000.0;
          }
          pos += incr;
          p_video_state->Seek(pos, incr, true);
        } else {
          pos = p_master_clock->GetTime();
          if (isnan(pos)) {
            pos = (double)p_video_state->GetSeekTime() / AV_TIME_BASE;
          }
          pos += incr;
          if (p_format_context->start_time != AV_NOPTS_VALUE &&
              pos < p_format_context->start_time / (double)AV_TIME_BASE)
            pos = p_format_context->start_time / (double)AV_TIME_BASE;
          p_video_state->Seek((int64_t)(pos * AV_TIME_BASE),
                              (int64_t)(incr * AV_TIME_BASE), false);
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
          p_player->SetForceReferesh(true);
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
      if (VideoState::kEnableSeekByBytes || p_format_context->duration <= 0) {
        uint64_t size = avio_size(p_format_context->pb);
        p_video_state->Seek(size * x / width, 0, true);
      } else {
        int64_t ts;
        int ns, hh, mm, ss;
        int tns, thh, tmm, tss;
        tns = p_format_context->duration / 1000000LL;
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
        ts = frac * p_format_context->duration;
        if (p_format_context->start_time != AV_NOPTS_VALUE) {
          ts += p_format_context->start_time;
        }
        p_video_state->Seek(ts, 0, false);
      }
      break;
    case SDL_WINDOWEVENT:
      switch (event.window.event) {
      case SDL_WINDOWEVENT_RESIZED:
		p_player->SetSize(event.window.data1, event.window.data2);
      case SDL_WINDOWEVENT_EXPOSED:
        p_player->SetForceReferesh(true);
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

void FfmpegSdlAvPlayback::SystemEventHandler(SDL_Event &event) {
#ifdef _WIN32
	// Add handling of resizing the window
	LPRECT lrpc;
	int width, height;
	switch (event.type) {
	case SDL_SYSWMEVENT:
            switch (event.syswm.msg->msg.win.msg) {
                    case WM_SIZE:
                    lrpc = (LPRECT)event.syswm.msg->msg.win.lParam;
                    width = LOWORD(lrpc);
                    height = HIWORD(lrpc);
                    av_log(NULL, AV_LOG_VERBOSE, "System Resizing WM_SIZE: Width %d and Height %d \n", width, height);
                    SetSize(width, height);
                    if ((WPARAM)event.syswm.msg->msg.win.wParam != SIZE_MINIMIZED) {
                        av_log(NULL, AV_LOG_VERBOSE, "Force Refresh\n");
                        force_refresh_ = 1;
                    }
            }
            break;
        default:
            break;
    }
#elif __APPLE__
#endif

}


void FfmpegSdlAvPlayback::EventHandler(SDL_Event &event) {
	// Add handling of resizing the window
	switch (event.type) {
	case SDL_WINDOWEVENT:
		switch (event.window.event) {
		case SDL_WINDOWEVENT_RESIZED:
			SetSize(event.window.data1, event.window.data2);
		case SDL_WINDOWEVENT_EXPOSED:
			force_refresh_ = 1;
		}
		break;
	default:
		break;
	}
}


int FfmpegSdlAvPlayback::InitializeAndStartDisplayLoop(long window_id) {
  std::mutex mtx;
  std::condition_variable cv;
  bool is_initialized = false;
  intptr_t wid = window_id;

  p_display_thread_id_ =
      new (std::nothrow) std::thread([this, &is_initialized, &cv, &wid] {
        Initialize(wid);
        is_initialized = true;
        cv.notify_all();

        SDL_Event event;
        while (!is_stopped_) {
          DisplayAndProcessEvent(&event);
          if (SDL_EventState(SDL_SYSWMEVENT, SDL_QUERY) == SDL_ENABLE) {
              SystemEventHandler(event);
          } else {
              EventHandler(event);
          }
        }
      });

  if (!p_display_thread_id_) {
    av_log(NULL, AV_LOG_ERROR, "Unable to create playback thread\n");
    return -1;
  }

  std::unique_lock<std::mutex> lck(mtx);
  cv.wait(lck, [&is_initialized] { return is_initialized; });
  int err = FfmpegToJavaErrNo(p_video_state_->StartStream());
  if (err) {
    av_log(NULL, AV_LOG_ERROR, "Unable to start the stream\n");
    return err;
  }

  if (p_video_state_->GetFrameWidth()) {
    FfmpegSdlAvPlayback::SetDefaultWindowSize(
        p_video_state_->GetFrameWidth(), p_video_state_->GetFrameHeight(),
        p_video_state_->GetFrameAspectRatio());
  }

  return 0;
}
