#ifndef MPVSDLAVPLAYBACK_H_
#define MPVSDLAVPLAYBACK_H_

#include "FfmpegAVPlayback.h"
#include "MpvErrorUtils.h"
#include <MPV/client.h> // This include is just for the MPV_FORMAT usage
#ifdef _WIN32
#include <Windows.h>
#elif __APPLE__
#include "TargetConditionals.h"
#include <dlfcn.h>
#endif
#include <atomic>
#include <clocale>
#include <cstdint>
#include <string.h>

extern "C" {
#include <SDL2/SDL.h>
#include <SDL2/SDL_thread.h>
#include <SDL2/SDL_syswm.h>
}

// MPV functtions signature
typedef intptr_t (*MpvCreate)();
typedef int (*MpvInitialize)(intptr_t);
typedef int (*MpvCommand)(intptr_t, const char **);
typedef int (*MpvCommandAsync)(intptr_t, uint64_t, const char **);
typedef int (*MpvCommandString)(intptr_t, const char *);
typedef int (*MpvTerminateDestroy)(intptr_t);
typedef int (*MpvSetOption)(intptr_t, const char *, int, void *);
typedef int (*MpvSetOptionString)(intptr_t, const char *, const char *);
typedef char *(*MpvGetPropertystring)(intptr_t, const char *);
typedef int (*MpvGetProperty)(intptr_t, const char *, int, void *);
typedef int (*MpvSetProperty)(intptr_t, const char *, int, void *);
typedef int (*MpvSetPropertyAsync)(intptr_t, uint64_t, const char *, int,
                                   void *);
typedef void (*MpvFree)(void *);
typedef mpv_event *(*MpvWaitEvent)(intptr_t, double);

/* The MpvAvPlayback class will the mpv-1.dll (must be copied
 * in the working directory) and will extract needed functions
 * for the proper useage of the player.
 * Note: No need to add the mpv-1.lib to the linker since
 * we are relying on the function poiter extracted for the dll
 * and the include of the client.h is just to use the type defined
 * in the API; mpv_format for instance
 * NOTE: THe mpv error codes schema:  >= 0 Succes, < 0 error
 */
// TODO(Reda) Much naming convention of the ffmpeg plugin and error returned
class MpvAvPlayback {

private:
#ifdef _WIN32
  HINSTANCE                _libMpvDll;
#elif __APPLE__
  void*                   _libMpvDylib;
#endif
  intptr_t mpv_handle_;

  // Function pointers to mpv api functions
  MpvCreate mpv_create_;
  MpvInitialize mpv_initialize_;
  MpvCommand mpv_command_; // Always terminate a command with NULL like so const
                          // char * cmd = {"seek", "10", "absolute", NULL}
  MpvCommandAsync
      mpv_command_async_; // Always terminate a command with NULL like so const
                        // char * cmd = {"seek", "10", "absolute", NULL}
  MpvCommandString mpv_command_string_;
  MpvTerminateDestroy mpv_terminate_destroy_;
  MpvSetOption mpv_set_option_;
  MpvSetOptionString mpv_set_option_string_;
  MpvGetPropertystring mpv_get_property_string_;
  MpvGetProperty mpv_get_property_;
  MpvSetProperty mpv_set_property_;
  MpvSetPropertyAsync mpv_set_property_async_;
  MpvFree mpv_free_;
  MpvWaitEvent mpv_wait_event_;

  void LoadMpvDynamic();

  int DoMpvCommand(const char **cmd);
  int Pause();

  bool initial_play_;
  const char *kContainerFpsProperty = "container-fps";
  const char *kWidthProperty = "width";
  const char *kHeightProperty = "height";
  const char *kDurationProperty = "duration";
  const char *kSpeedProperty = "speed";
  const char *kPauseProperty = "pause";
  const char *kPlaybackTimeProperty = "playback-time";
  const char *kAoVolumeProperty = "ao-volume";
  const char *kFrameBackStepCommand = "frame-back-step";
  const char *kFrameStepCommand = "frame-step";
  const char *kSeekCommand = "seek";

public:
  MpvAvPlayback();
  ~MpvAvPlayback();

  int Init(const char *filename, const intptr_t windowID);
  int Destroy();
  void InitAndEventLoop(const char *filename);
  int IsPaused(bool *isPaused);
  int Play();
  int Stop();
  int TogglePause();
  int SetRate(double newRate);
  int GetRate(double *p_rate);
  int GetFps(double *p_fps);
  int GetImageWidth(int64_t *p_width);
  int GetImageHeight(int64_t *p_height);
  int GetDuration(double *p_duration);
  int StepBackward();
  int StepForward();
  int SetTime(double time);
  int GetPresentationTime(double *p_time);
  int SetVolume(double volume);
  int GetVolume(double *p_volume);
};

#endif MPVAVPLAYBACK_H_
