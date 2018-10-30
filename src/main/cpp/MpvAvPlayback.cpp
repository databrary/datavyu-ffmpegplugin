#include "MpvAvPlayback.h"
#include "MediaPlayerErrors.h"

MpvAvPlayback::MpvAvPlayback() : initial_play_(true) {}

MpvAvPlayback::~MpvAvPlayback() {}

int MpvAvPlayback::Init(const char *p_filename, const intptr_t windowID) {
  std::setlocale(LC_NUMERIC, "C");
  int err;

  LoadMpvDynamic();
  if (!lib_mpv_dll_) {
    return MpvToJavaErrNo(MPV_ERROR_GENERIC);
  }

  mpv_handle_ = mpv_create_();
  if (!mpv_handle_) {
    mpv_terminate_destroy_(mpv_handle_);
  }

  err = MpvToJavaErrNo(
      mpv_set_option_string_(mpv_handle_, "keep-open", "always"));
  if (err != 0) {
    return err;
  }

  intptr_t windowId = windowID;
  err = MpvToJavaErrNo(
      mpv_set_option_(mpv_handle_, "wid", MPV_FORMAT_INT64, &windowId));
  if (err != 0) {
    return err;
  }

  double _startUpVolume = 100;
  err = MpvToJavaErrNo(mpv_set_option_(mpv_handle_, "volume", MPV_FORMAT_DOUBLE,
                                       &_startUpVolume));
  if (err != 0) {
    return err;
  }

  err = MpvToJavaErrNo(Pause());
  if (err != 0) {
    return err;
  }

  err = MpvToJavaErrNo(mpv_initialize_(mpv_handle_));
  if (err != 0) {
    return err;
  }

  const char *cmd[] = {"loadfile", p_filename, NULL};
  err = MpvToJavaErrNo(mpv_command_async_(mpv_handle_, 1, cmd));
  if (err != 0) {
    return err;
  }

  // Wait for the mpv file loaded event
  while (1) {
    mpv_event *event = mpv_wait_event_(mpv_handle_, 1000);
    if (event->event_id == MPV_EVENT_FILE_LOADED) {
      break;
    }
    if (event->event_id == MPV_EVENT_IDLE) {
      return ERROR_MPV_LOADING_FAILED;
    }
  }

  return ERROR_NONE;
}

void MpvAvPlayback::LoadMpvDynamic() {
  lib_mpv_dll_ =
      LoadLibrary(L"mpv-1.dll"); // The dll is included in the DEV builds by
                                 // lachs0r: https://mpv.srsfckn.biz/
  mpv_create_ = (MpvCreate)GetProcAddress(lib_mpv_dll_, "mpv_create");
  mpv_initialize_ =
      (MpvInitialize)GetProcAddress(lib_mpv_dll_, "mpv_initialize");
  mpv_terminate_destroy_ = (MpvTerminateDestroy)GetProcAddress(
      lib_mpv_dll_, "mpv_terminate_destroy");
  mpv_command_ = (MpvCommand)GetProcAddress(lib_mpv_dll_, "mpv_command");
  mpv_command_async_ =
      (MpvCommandAsync)GetProcAddress(lib_mpv_dll_, "mpv_command_async");
  mpv_command_string_ =
      (MpvCommandString)GetProcAddress(lib_mpv_dll_, "mpv_command_string");
  mpv_set_option_ =
      (MpvSetOption)GetProcAddress(lib_mpv_dll_, "mpv_set_option");
  mpv_set_option_string_ =
      (MpvSetOptionString)GetProcAddress(lib_mpv_dll_, "mpv_set_option_string");
  mpv_get_property_string_ =
      (MpvGetPropertystring)GetProcAddress(lib_mpv_dll_, "mpv_get_property");
  mpv_get_property_ =
      (MpvGetProperty)GetProcAddress(lib_mpv_dll_, "mpv_get_property");
  mpv_set_property_ =
      (MpvSetProperty)GetProcAddress(lib_mpv_dll_, "mpv_set_property");
  mpv_set_property_async_ = (MpvSetPropertyAsync)GetProcAddress(
      lib_mpv_dll_, "mpv_set_property_async");
  mpv_free_ = (MpvFree)GetProcAddress(lib_mpv_dll_, "mpv_free");
  mpv_wait_event_ =
      (MpvWaitEvent)GetProcAddress(lib_mpv_dll_, "mpv_wait_event");
}

int MpvAvPlayback::Play() {
  if (!mpv_handle_) {
    return ERROR_MPV_GENERIC;
  }

  // Workaround to set the volume at start up none of the volume option
  // or ao-volume property seem to affect the player volume during
  // initialization
  if (initial_play_) {
    SetVolume(100);
    initial_play_ = false;
  }

  const char *porpertyValue = "no";
  int err = MpvToJavaErrNo(mpv_set_property_(
      mpv_handle_, "pause", MPV_FORMAT_STRING, &porpertyValue));
  if (err != 0) {
    return err;
  }

  return ERROR_NONE;
}

int MpvAvPlayback::Stop() {

  if (Pause() < 0) {
    return ERROR_MPV_PROPERTY_ERROR;
  }
  if (SetRate(1) < 0) {
    return ERROR_MPV_COMMAND;
  }

  return ERROR_NONE;
}

int MpvAvPlayback::TogglePause() {
  int err;
  bool isPaused;
  IsPaused(&isPaused);
  if (isPaused) {
    err = Play();
    if (err < 0) {
      return err;
    }
  } else {
    err = Pause();
    if (err < 0) {
      return err;
    }
  }

  return ERROR_NONE;
}

int MpvAvPlayback::SetRate(double rate) {
  if (!mpv_handle_) {
    return ERROR_MPV_GENERIC;
  }

  if (rate < 0) {
    return ERROR_MPV_PROPERTY_FORMAT;
  }

  int err = MpvToJavaErrNo(
      mpv_set_option_(mpv_handle_, kSpeedProperty, MPV_FORMAT_DOUBLE, &rate));
  if (err != 0) {
    return err;
  }

  return ERROR_NONE;
}

int MpvAvPlayback::GetRate(double *p_rate) {
  if (!mpv_handle_) {
    return ERROR_MPV_GENERIC;
  }

  int err = MpvToJavaErrNo(mpv_get_property_(mpv_handle_, kSpeedProperty,
                                             MPV_FORMAT_DOUBLE, p_rate));
  if (err != 0) {
    return err;
  }

  return ERROR_NONE;
}

int MpvAvPlayback::GetFps(double *p_fps) {
  if (!mpv_handle_) {
    return ERROR_MPV_GENERIC;
  }

  int err = MpvToJavaErrNo(mpv_get_property_(mpv_handle_, kContainerFpsProperty,
                                             MPV_FORMAT_DOUBLE, p_fps));
  if (err != 0) {
    return err;
  }

  return ERROR_NONE;
}

int MpvAvPlayback::GetImageWidth(int64_t *p_width) {
  if (!mpv_handle_) {
    return ERROR_MPV_GENERIC;
  }

  int err = MpvToJavaErrNo(mpv_get_property_(mpv_handle_, kWidthProperty,
                                             MPV_FORMAT_INT64, p_width));
  if (err != 0) {
    return err;
  }

  return ERROR_NONE;
}

int MpvAvPlayback::GetImageHeight(int64_t *p_height) {
  if (!mpv_handle_) {
    return ERROR_MPV_GENERIC;
  }

  int err = MpvToJavaErrNo(mpv_get_property_(mpv_handle_, kHeightProperty,
                                             MPV_FORMAT_INT64, p_height));
  if (err != 0) {
    return err;
  }

  return ERROR_NONE;
}

int MpvAvPlayback::GetDuration(double *p_duration) {
  if (!mpv_handle_) {
    return ERROR_MPV_GENERIC;
  }

  int err = MpvToJavaErrNo(mpv_get_property_(mpv_handle_, kDurationProperty,
                                             MPV_FORMAT_DOUBLE, p_duration));
  if (err != 0) {
    return err;
  }

  return ERROR_NONE;
}

int MpvAvPlayback::StepBackward() {
  if (!mpv_handle_) {
    return ERROR_MPV_GENERIC;
  }

  const char *cmd[] = {kFrameBackStepCommand, NULL};

  int err = MpvToJavaErrNo(DoMpvCommand(cmd));
  if (err != 0) {
    return err;
  }

  return ERROR_NONE;
}

int MpvAvPlayback::StepForward() {
  if (!mpv_handle_) {
    return ERROR_MPV_GENERIC;
  }

  const char *cmd[] = {kFrameStepCommand, NULL};

  int err = MpvToJavaErrNo(DoMpvCommand(cmd));
  if (err != 0) {
    return err;
  }

  return MPV_ERROR_SUCCESS;
}

int MpvAvPlayback::DoMpvCommand(const char **pp_command_str) {
  if (!mpv_handle_) {
    return ERROR_MPV_GENERIC;
  }

  int err = MpvToJavaErrNo(mpv_command_(mpv_handle_, pp_command_str));
  if (err != 0) {
    return err;
  }

  return ERROR_NONE;
}

int MpvAvPlayback::Destroy() {
  int err = 0;
  err = MpvToJavaErrNo(mpv_terminate_destroy_(mpv_handle_));
  if (err != 0) {
    return err;
  }

  return ERROR_NONE;
}

void MpvAvPlayback::InitAndEventLoop(const char *p_filename) {
  if (SDL_Init(SDL_INIT_VIDEO) < 0) {
    printf("SDL init failed");
    exit(1);
  }

  SDL_Window *window = SDL_CreateWindow(
      p_filename, SDL_WINDOWPOS_CENTERED, SDL_WINDOWPOS_CENTERED, 1024, 720,
      SDL_WINDOW_SHOWN | SDL_WINDOW_RESIZABLE);

  if (!window) {
    printf("failed to create SDL window");
    exit(1);
  }

  SDL_SysWMinfo wm_info;
  SDL_VERSION(&wm_info.version);
  SDL_GetWindowWMInfo(window, &wm_info);
  HWND hwnd = wm_info.info.win.window;

  MpvAvPlayback *pPlayer = new MpvAvPlayback();
  int err = pPlayer->Init(p_filename, (intptr_t)hwnd);
  if (err) {
    fprintf(stderr, "Error %d when opening input file %s", err, p_filename);
  }

  double incr, pos, frac;
  double rate, currentVolume;

  while (1) {
    SDL_Event event;
    if (SDL_WaitEvent(&event) != 1)
      printf("event loop error");
    int redraw = 0;
    switch (event.type) {
    case SDL_KEYDOWN:
      switch (event.key.keysym.sym) {
      case SDLK_ESCAPE:
        pPlayer->Destroy();
        exit(0); // need to exit here to avoid joinable exception
        break;
      case SDLK_q:
        pPlayer->Destroy();
        exit(0); // need to exit here to avoid joinable exception
        break;
      case SDLK_KP_8:
        pPlayer->Play();
        break;
      case SDLK_KP_5:
        pPlayer->Stop();
        break;
      case SDLK_KP_2:
        pPlayer->TogglePause();
        break;
      case SDLK_p:
      case SDLK_SPACE:
        pPlayer->TogglePause();
        break;
      case SDLK_KP_MULTIPLY:
      case SDLK_0:
        pPlayer->GetVolume(&currentVolume);
        // MPV Audio Range is from 0 - 100
        pPlayer->SetVolume(currentVolume - 10.0);
        break;
      case SDLK_KP_DIVIDE:
      case SDLK_9:
        pPlayer->GetVolume(&currentVolume);
        // MPV Audio Range is from 0 - 100
        pPlayer->SetVolume(currentVolume + 10.0);
        break;
      case SDLK_s: // S: Step to next frame
        pPlayer->StepForward();
        break;
      case SDLK_b: // S: Step to next frame
        pPlayer->StepBackward();
        break;
      case SDLK_KP_PLUS:
        pPlayer->GetRate(&rate);
        err = pPlayer->SetRate(rate * 2);
        if (err != 0) {
          av_log(NULL, AV_LOG_ERROR, "Rate %f unavailable\n", rate * 2);
        } else {
          rate *= 2;
        }
        break;
      case SDLK_KP_MINUS:
        pPlayer->GetRate(&rate);
        err = pPlayer->SetRate(rate / 2);
        if (err != 0) {
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
        pPlayer->GetPresentationTime(&pos);
        pos += incr;
        pPlayer->SetTime(pos);
        break;
      default:
        break;
      }
      break;
    default:
      break;
    }
  }
}

int MpvAvPlayback::Pause() {
  if (!mpv_handle_) {
    return ERROR_MPV_GENERIC;
  }

  const char *propertyValue = "yes";

  int err = MpvToJavaErrNo(mpv_set_property_(
      mpv_handle_, kPauseProperty, MPV_FORMAT_STRING, &propertyValue));
  if (err != 0) {
    return err;
  }

  return ERROR_NONE;
}

int MpvAvPlayback::IsPaused(bool *p_is_paused) {
  if (!mpv_handle_) {
    return ERROR_MPV_GENERIC;
  }

  char *isPausedProperty;
  int err = MpvToJavaErrNo(mpv_get_property_(
      mpv_handle_, kPauseProperty, MPV_FORMAT_STRING, &isPausedProperty));
  if (err != 0) {
    return err;
  }

  std::string isPausedString(isPausedProperty);
  *p_is_paused = isPausedString == "yes";
  mpv_free_(isPausedProperty);

  return ERROR_NONE;
}

int MpvAvPlayback::SetTime(double value) {
  if (!mpv_handle_) {
    return ERROR_MPV_GENERIC;
  }

  std::string timeString = std::to_string(value);

  const char *cmd[] = {kSeekCommand, timeString.c_str(), "absolute", NULL};

  int err = MpvToJavaErrNo(DoMpvCommand(cmd));
  if (err != 0) {
    return err;
  }

  return ERROR_NONE;
}

int MpvAvPlayback::GetPresentationTime(double *p_time) {
  if (!mpv_handle_) {
    return ERROR_MPV_GENERIC;
  }

  // check difference between playback-time and timr-pod
  // https://mpv.io/manual/master/#command-interface-playback-time
  int err = MpvToJavaErrNo(mpv_get_property_(mpv_handle_, kPlaybackTimeProperty,
                                             MPV_FORMAT_DOUBLE, p_time));
  if (err != 0) {
    return err;
  }

  return ERROR_NONE;
}

int MpvAvPlayback::SetVolume(double volume) {
  if (!mpv_handle_) {
    return ERROR_MPV_GENERIC;
  }

  int err = MpvToJavaErrNo(mpv_set_property_(mpv_handle_, kAoVolumeProperty,
                                             MPV_FORMAT_DOUBLE, &volume));
  if (err != 0) {
    return err;
  }

  return ERROR_NONE;
}

int MpvAvPlayback::GetVolume(double *p_volume) {
  if (!mpv_handle_) {
    return ERROR_MPV_GENERIC;
  }

  int err = MpvToJavaErrNo(mpv_get_property_(mpv_handle_, kAoVolumeProperty,
                                             MPV_FORMAT_DOUBLE, p_volume));
  if (err != 0) {
    return err;
  }

  return ERROR_NONE;
}
