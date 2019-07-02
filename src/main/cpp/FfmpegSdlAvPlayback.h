#ifndef FFMPEGSDLAVPLAYBACK_H_
#define FFMPEGSDLAVPLAYBACK_H_

#include "FfmpegAVPlayback.h"
#include "VideoState.h"
#include <atomic>
#include <iostream>

#ifdef _WIN32
#include <Basetsd.h>
#elif __APPLE__
#include <dispatch/dispatch.h>
#endif

extern "C" {
#include <SDL2/SDL.h>
#include <SDL2/SDL_syswm.h>
#include <SDL2/SDL_thread.h>
#include <SDL2/SDL_version.h>
}

#define FF_QUIT_EVENT (SDL_USEREVENT + 2)

class FfmpegSdlAvPlayback : public FfmpegAvPlayback {
public:
  FfmpegSdlAvPlayback(int startup_volume = SDL_MIX_MAXVOLUME);
  ~FfmpegSdlAvPlayback();

  int OpenVideo(const char *filename, AVInputFormat *iformat);

  inline void GetVideoState(VideoState **pp_video_state) const {
    *pp_video_state = p_video_state_;
  }

  inline void PauseAudio() { SDL_PauseAudioDevice(audio_dev_, 0); }

  // Toggle full screen mode
  inline void ToggleFullscreen() {
    enabled_full_screen_ = !enabled_full_screen_;
    SDL_SetWindowFullscreen(
        p_window_, enabled_full_screen_ ? SDL_WINDOW_FULLSCREEN_DESKTOP : 0);
  }

  // Get the volume
  inline double GetVolume() const {
    return av_clip(100 * av_clip(audio_volume_, 0, SDL_MIX_MAXVOLUME) /
                       SDL_MIX_MAXVOLUME,
                   0, 100);
  }

  inline void ShowWindow() {
#ifdef _WIN32
    SDL_Window *window = SDL_GetWindowFromID(window_id_);
    if (window) {
      SDL_ShowWindow(p_window_);
      SDL_RaiseWindow(p_window_);
    }
#elif __APPLE__
    dispatch_async(dispatch_get_main_queue(), ^{
      SDL_Window *window = SDL_GetWindowFromID(window_id_);
      if (window) {
        SDL_ShowWindow(p_window_);
        SDL_RaiseWindow(p_window_);
      }
    });
#endif
  }

  inline void HideWindow() {
#ifdef _WIN32
    SDL_Window *window = SDL_GetWindowFromID(window_id_);
    if (window) {
      SDL_HideWindow(p_window_);
    }
#elif __APPLE__
    dispatch_async(dispatch_get_main_queue(), ^{
      SDL_Window *window = SDL_GetWindowFromID(window_id_);
      if (window) {
        SDL_HideWindow(window);
      }
    });
#endif
  }

  inline int GetWindowID() { return window_id_; }

  // Get Image Width
  int GetImageWidth() const;

  // Get image Height
  int GetImageHeight() const;

  // Set the volume
  void SetVolume(double volume);

  // Initializes the SDL ecosystem and starts the display loop
  int InitializeAndStartDisplayLoop();

  void SetSize(int width, int height);

  // Initializes the SDL ecosystem and starts the event loop to process
  // events from the SDL window
  static void InitializeAndListenForEvents(FfmpegSdlAvPlayback *p_player);

  inline void SetKeyEventKeyDispatcherCallback(
      const std::function<void(SDL_Keycode)> &func) {
    dispatch_keyEvent_callback_ = func;
  }

private:
  SDL_Window *p_window_;
  SDL_Renderer *p_renderer_;
  SDL_AudioDeviceID audio_dev_ = 0;
  int x_left_;
  int y_top_;
  int x_pos_;
  int window_id_;

  struct SwsContext *p_img_convert_ctx_;

  SDL_Texture *p_vis_texture_;
  SDL_Texture *p_vid_texture_;

  SDL_Rect display_rect_;

  int screen_width_;
  int screen_height_;
  int enabled_full_screen_;

  int audio_volume_;

  double remaining_time_;

  int64_t cursor_last_shown_time_;
  bool is_cursor_hidden_;
  char *p_window_title_;
  SDL_RendererInfo renderer_info_;
#ifdef __APPLE__
  std::atomic<bool> is_stopped_ = {false};
#elif _WIN32
  std::atomic<bool> is_stopped_ = false;
#endif
  std::thread *p_display_thread_id_ = nullptr;
  std::function<void(SDL_Keycode)> dispatch_keyEvent_callback_;

  static int kDefaultWidth;
  static int kDefaultHeight;
  static unsigned kSwsFlags;
  static const char *kDefaultWindowTitle; // assumed if the file can't be opened
  static int kWindowResizable;
  static int kAudioMinBufferSize;
  static int kAudioMaxCallbackPerSec;
  static double kVolumeStepInDecibel;
  static double kRefreshRate;
  static int kCursorHideDelayInMillis;
  static int kWindowCount;

  struct TextureFormatEntry {
    enum AVPixelFormat format;
    int texture_fmt;
  };

  inline void GetSize(int *p_width, int *p_height) const {
    *p_width = frame_width_;
    *p_height = frame_height_;
  }

  inline void SetCursorLastShownTime(int64_t time) {
    int64_t cursor_last_shown_time_ = time;
  }

  inline void SetIsCursorHidden(bool hidden) { is_cursor_hidden_ = hidden; }

  inline bool IsCursorHidden() const { return is_cursor_hidden_; }

  inline bool IsScreenSizeSet() const {
    return screen_width_ != 0 && screen_height_ != 0;
  }

  inline static int ComputeCustomModulus(int a, int b) {
    return a < 0 ? a % b + b : a % b;
  }

  inline void FillRectangle(int x, int y, int w, int h) {
    SDL_Rect rect = {x, y, w, h};
    if (w >= 0 && h >= 0) {
      SDL_RenderFillRect(p_renderer_, &rect);
    }
  }

  // Calculates the rectangle to display taking into account the source position
  // and the frame width, height, and aspect ratio
  static void CalculateRectangleForDisplay(SDL_Rect *rect, int scr_xleft,
                                           int scr_ytop, int scr_width,
                                           int scr_height, int frame_width,
                                           int frame_height,
                                           AVRational frame_aspect_ratio);

  inline void StopDisplayLoop() { is_stopped_ = true; }

  // Initialize the SDL ecosystem
  void InitializeSDL();

  // Initialize the SDL ecosystem
  void InitializeSDLWindow();

  // Initialize the SDL window
  void InitializeRenderer();

  int OpenWindow(const char *window_name);
  inline void CloseAudio() { SDL_CloseAudioDevice(audio_dev_); }

  // helper method to DisplayVideoFrame
  void GetAndDisplayVideoFrame();

  // display the current picture, if any
  void DisplayVideoFrame();

  static const TextureFormatEntry kTextureFormatMap[];

  int UploadTexture(SDL_Texture **tex, AVFrame *frame,
                    struct SwsContext **img_convert_ctx);

  int OpenAudio(int64_t wanted_channel_layout, int wanted_nb_channels,
                int wanted_sample_rate, struct AudioParams *audio_hw_params);

  static void SetDefaultWindowSize(int width, int height, AVRational sar);

  static void GetPixelFormatAndBlendmode(int format, Uint32 *sdl_pix_fmt,
                                         SDL_BlendMode *sdl_blendmode);

  int ReallocateTexture(SDL_Texture **texture, Uint32 new_format, int new_width,
                        int new_height, SDL_BlendMode blendmode,
                        bool init_texture);

  // called to display each frame
  void UpdateFrame(double *remaining_time);

  // Function Called from the event loop
  void DisplayAndProcessEvent(SDL_Event *event);
};

static void sdl_audio_callback_bridge(void *vs, Uint8 *stream, int len) {
  FfmpegSdlAvPlayback *pFfmpegSdlAvPlayback =
      static_cast<FfmpegSdlAvPlayback *>(vs);
  VideoState *p_video_state = nullptr;
  pFfmpegSdlAvPlayback->GetVideoState(&p_video_state);
  int volume = pFfmpegSdlAvPlayback->GetVolume();
#ifdef SDL_ENABLED
  p_video_state->GetAudioCallback(stream, len, volume);
#endif // SDL_ENABLED

  // SDL_MixAudioFormat need to be called from the video state class
  // Note, the mixer can work inplace using the same stream as src and dest, see
  // source code here
  // https://github.com/davidsiaw/SDL2/blob/c315c99d46f89ef8dbb1b4eeab0fe38ea8a8b6c5/src/audio/SDL_mixer.c
  // if (!p_video_state->IsMuted() && stream)
  // SDL_MixAudioFormat(stream, stream, AUDIO_S16SYS, len,
  // pFfmpegSdlAvPlayback->GetVolume());
}

static int resizingEventHandler(void *p_player, SDL_Event *event) {
  FfmpegSdlAvPlayback *pFfmpegSdlAvPlayback =
      static_cast<FfmpegSdlAvPlayback *>(p_player);
  if (event->type == SDL_WINDOWEVENT &&
      event->window.event == SDL_WINDOWEVENT_RESIZED) {
    if (pFfmpegSdlAvPlayback->GetWindowID() == event->window.windowID) {
      pFfmpegSdlAvPlayback->SetSize(event->window.data1, event->window.data2);
    }
  }
  return 0;
}

#endif FFMPEGSDLAVPLAYBACK_H_
