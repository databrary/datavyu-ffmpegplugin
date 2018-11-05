#ifndef MPVSDLAVPLAYBACK_H_
#define MPVSDLAVPLAYBACK_H_

#include "FfmpegAVPlayback.h"
#include "MpvErrorUtils.h"
#include <MPV/client.h> // This include is just for the MPV_FORMAT usage
#include <Windows.h>
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
class MpvAvPlayback : public FfmpegAvPlayback {

private:
  HINSTANCE _libMpvDll;
  intptr_t _mpvHandle;

  // Function pointers to mpv api functions
  MpvCreate _mpvCreate;
  MpvInitialize _mpvInitialize;
  MpvCommand _mpvCommand; // Always terminate a command with NULL like so const
                          // char * cmd = {"seek", "10", "absolute", NULL}
  MpvCommandAsync
      _mpvCommandAsync; // Always terminate a command with NULL like so const
                        // char * cmd = {"seek", "10", "absolute", NULL}
  MpvCommandString _mpvCommandString;
  MpvTerminateDestroy _mpvTerminateDestroy;
  MpvSetOption _mpvSetOption;
  MpvSetOptionString _mpvSetOptionString;
  MpvGetPropertystring _mpvGetPropertyString;
  MpvGetProperty _mpvGetProperty;
  MpvSetProperty _mpvSetProperty;
  MpvSetPropertyAsync _mpvSetPropertyAsync;
  MpvFree _mpvFree;
  MpvWaitEvent _mpvWaitEvent;

  void LoadMpvDynamic();

  int DoMpvCommand(const char **cmd);
  int Pause();

  bool _initialPlay;
  const char *_kContainerFpsProperty = "container-fps";
  const char *_kWidthProperty = "width";
  const char *_kHeightProperty = "height";
  const char *_kDurationProperty = "duration";
  const char *_kSpeedProperty = "speed";
  const char *_kPauseProperty = "pause";
  const char *_kPlaybackTimeProperty = "playback-time";
  const char *_kAoVolumeProperty = "ao-volume";
  const char *_kFrameBackStepCommand = "frame-back-step";
  const char *_kFrameStepCommand = "frame-step";
  const char *_kSeekCommand = "seek";

public:
  MpvAvPlayback();
  ~MpvAvPlayback();

  int Init(const char *filename, const intptr_t windowID);
  int Destroy();
  void init_and_event_loop(const char *filename);
  int IsPaused(bool *isPaused);
  int Play();
  int Stop();
  int TogglePause();
  int SetRate(double newRate);
  int GetRate(double *currentRate);
  int GetFps(double *steamFps);
  int GetImageWidth(int64_t *imageWidth);
  int GetImageHeight(int64_t *imageHeight);
  int GetDuration(double *streamDuration);
  int StepBackward();
  int StepForward();
  int SetTime(double value);
  int GetPresentationTime(double *presentationTime);
  int SetVolume(double pfVolume);
  int GetVolume(double *pfVolume);
};

#endif MPVAVPLAYBACK_H_
