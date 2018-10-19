#include "MpvAvPlayback.h"
#include "FfmpegMediaErrors.h"

MpvAvPlayback::MpvAvPlayback() :
	_initialPlay(true)
{}

MpvAvPlayback::~MpvAvPlayback()
{}

int MpvAvPlayback::Init(const char * filename, const intptr_t windowID)
{
	std::setlocale(LC_NUMERIC, "C");
	int err;

	LoadMpvDynamic();
	if (!_libMpvDll) {
		return mpvToJavaErrNo(MPV_ERROR_GENERIC);
	}

	_mpvHandle = _mpvCreate();
	if (!_mpvHandle) {
		_mpvTerminateDestroy(_mpvHandle);
	}
	
	err = mpvToJavaErrNo(_mpvSetOptionString(_mpvHandle, "keep-open", "always"));
	if (err != 0) {
		return err;
	}

	intptr_t windowId = windowID;
	err = mpvToJavaErrNo(_mpvSetOption(_mpvHandle, "wid", MPV_FORMAT_INT64, &windowId));
	if (err != 0) {
		return err;
	}

	double _startUpVolume = 100;
	err = mpvToJavaErrNo(_mpvSetOption(_mpvHandle, "volume", MPV_FORMAT_DOUBLE, &_startUpVolume));
	if (err != 0) {
		return err;
	}

	err = mpvToJavaErrNo(Pause());
	if (err != 0) {
		return err;
	}

	err = mpvToJavaErrNo(_mpvInitialize(_mpvHandle));
	if (err != 0) {
		return err;
	}

	const char *cmd[] = { "loadfile", filename, NULL };
	err = mpvToJavaErrNo(_mpvCommandAsync(_mpvHandle, 1, cmd));
	if (err != 0) {
		return err;
	}

	// Wait for the mpv file loaded event
	while (1)
	{
		mpv_event *event = _mpvWaitEvent(_mpvHandle, 1000);
		if (event->event_id == MPV_EVENT_FILE_LOADED) {
			break;
		}
	}

	return ERROR_NONE;
}

void MpvAvPlayback::LoadMpvDynamic()
{
	_libMpvDll = LoadLibrary(L"mpv-1.dll"); // The dll is included in the DEV builds by lachs0r: https://mpv.srsfckn.biz/
	_mpvCreate = (MpvCreate)GetProcAddress(_libMpvDll, "mpv_create");
	_mpvInitialize = (MpvInitialize)GetProcAddress(_libMpvDll, "mpv_initialize");
	_mpvTerminateDestroy = (MpvTerminateDestroy)GetProcAddress(_libMpvDll, "mpv_terminate_destroy");
	_mpvCommand = (MpvCommand)GetProcAddress(_libMpvDll, "mpv_command");
	_mpvCommandAsync = (MpvCommandAsync)GetProcAddress(_libMpvDll, "mpv_command_async");
	_mpvCommandString = (MpvCommandString)GetProcAddress(_libMpvDll, "mpv_command_string");
	_mpvSetOption = (MpvSetOption)GetProcAddress(_libMpvDll, "mpv_set_option");
	_mpvSetOptionString = (MpvSetOptionString)GetProcAddress(_libMpvDll, "mpv_set_option_string");
	_mpvGetPropertyString = (MpvGetPropertystring)GetProcAddress(_libMpvDll, "mpv_get_property");
	_mpvGetProperty = (MpvGetProperty)GetProcAddress(_libMpvDll, "mpv_get_property");
	_mpvSetProperty = (MpvSetProperty)GetProcAddress(_libMpvDll, "mpv_set_property");
	_mpvSetPropertyAsync = (MpvSetPropertyAsync)GetProcAddress(_libMpvDll, "mpv_set_property_async");
	_mpvFree = (MpvFree)GetProcAddress(_libMpvDll, "mpv_free");
	_mpvWaitEvent = (MpvWaitEvent)GetProcAddress(_libMpvDll, "mpv_wait_event");
}

int MpvAvPlayback::Play()
{
	if (!_mpvHandle) {
		return ERROR_MPV_GENERIC;
	}

	// Workaround to set the volume at start up none of the volume option
	// or ao-volume property seem to affect the player volume during initialization
	if (_initialPlay) {
		SetVolume(100);
		_initialPlay = false;
	}

	const char* porpertyValue = "no";
	int err = mpvToJavaErrNo(_mpvSetProperty(_mpvHandle, "pause", MPV_FORMAT_STRING, &porpertyValue));
	if (err != 0) {
		return err;
	}

	return ERROR_NONE;
}

int MpvAvPlayback::Stop()
{

	if (Pause() < 0) {
		return ERROR_MPV_PROPERTY_ERROR;
	}
	if (SetRate(1) < 0) {
		return ERROR_MPV_COMMAND;
	}

	return ERROR_NONE;
}

int MpvAvPlayback::TogglePause()
{
	int err;
	bool isPaused;
	IsPaused(&isPaused);
	if (isPaused) {
		err = Play();
		if (err < 0) {
			return err;
		}
	}
	else {
		err = Pause();
		if (err < 0) {
			return err;
		}
	}

	return ERROR_NONE;
}

int MpvAvPlayback::SetRate(double newRate)
{
	if (!_mpvHandle) {
		return ERROR_MPV_GENERIC;
	}

	if (newRate < 0) {
		return ERROR_MPV_PROPERTY_FORMAT;
	}

	int err = mpvToJavaErrNo(_mpvSetOption(_mpvHandle, _speedProperty, MPV_FORMAT_DOUBLE, &newRate));
	if ( err != 0) {
		return err;
	}

	return ERROR_NONE;
}

int MpvAvPlayback::GetRate(double *currentRate)
{
	if (!_mpvHandle) {
		return ERROR_MPV_GENERIC;
	}

	int err = mpvToJavaErrNo(_mpvGetProperty(_mpvHandle, _speedProperty, MPV_FORMAT_DOUBLE, currentRate));
	if (err != 0) {
		return err;
	}

	return ERROR_NONE;
}

int MpvAvPlayback::GetFps(double *streamFps)
{
	if (!_mpvHandle) {
		return ERROR_MPV_GENERIC;
	}

	int err = mpvToJavaErrNo(_mpvGetProperty(_mpvHandle, _containerFpsProperty, MPV_FORMAT_DOUBLE, streamFps));
	if (err != 0) {
		return err;
	}

	return ERROR_NONE;
}

int MpvAvPlayback::GetImageWidth(int64_t *imageWidth)
{
	if (!_mpvHandle) {
		return ERROR_MPV_GENERIC;
	}

	int err = mpvToJavaErrNo(_mpvGetProperty(_mpvHandle, _widthProperty, MPV_FORMAT_INT64, imageWidth));
	if (err != 0) {
		return err;
	}

	return ERROR_NONE;
}

int MpvAvPlayback::GetImageHeight(int64_t *imageHeight)
{
	if (!_mpvHandle) {
		return ERROR_MPV_GENERIC;
	}

	int err = mpvToJavaErrNo(_mpvGetProperty(_mpvHandle, _heightProperty, MPV_FORMAT_INT64, imageHeight));
	if (err != 0) {
		return err;
	}
	
	return ERROR_NONE;
}

int MpvAvPlayback::GetDuration(double *streamDuration)
{
	if (!_mpvHandle) {
		return ERROR_MPV_GENERIC;
	}

	int err = mpvToJavaErrNo(_mpvGetProperty(_mpvHandle, _durationProperty, MPV_FORMAT_DOUBLE, streamDuration));
	if (err != 0) {
		return err;
	}
	
	return ERROR_NONE;
}

int MpvAvPlayback::StepBackward()
{
	if (!_mpvHandle) {
		return ERROR_MPV_GENERIC;
	}

	const char *cmd[] = { _frameBackStepCommand, NULL };

	int err = mpvToJavaErrNo(DoMpvCommand(cmd));
	if (err != 0) {
		return err;
	}

	return ERROR_NONE;
}

int MpvAvPlayback::StepForward()
{
	if (!_mpvHandle) {
		return ERROR_MPV_GENERIC;
	}

	const char *cmd[] = { _frameStepCommand, NULL };

	int err = mpvToJavaErrNo(DoMpvCommand(cmd));
	if (err != 0) {
		return err;
	}

	return MPV_ERROR_SUCCESS;
}

int MpvAvPlayback::DoMpvCommand(const char **cmd)
{
	if (!_mpvHandle) {
		return ERROR_MPV_GENERIC;
	}

	int err = mpvToJavaErrNo(_mpvCommand(_mpvHandle, cmd));
	if (err != 0) {
		return err;
	}

	return ERROR_NONE;
}

int MpvAvPlayback::Destroy()
{
	int err = 0;
	err = mpvToJavaErrNo(_mpvTerminateDestroy(_mpvHandle));
	if (err != 0) {
		return err;
	}

	return ERROR_NONE;
}

void MpvAvPlayback::init_and_event_loop(const char *filename)
{
	if (SDL_Init(SDL_INIT_VIDEO) < 0) {
		printf("SDL init failed");
		exit(1);
	}

	SDL_Window *window =
		SDL_CreateWindow(filename, SDL_WINDOWPOS_CENTERED, SDL_WINDOWPOS_CENTERED,
			1024, 720, SDL_WINDOW_SHOWN |
			SDL_WINDOW_RESIZABLE);

	if (!window) {
		printf("failed to create SDL window");
		exit(1);
	}

	SDL_SysWMinfo wmInfo;
	SDL_VERSION(&wmInfo.version);
	SDL_GetWindowWMInfo(window, &wmInfo);
	HWND hwnd = wmInfo.info.win.window;

	MpvAvPlayback* pPlayer = new MpvAvPlayback();
	int err = pPlayer->Init(filename, (intptr_t)hwnd);
	if (err) {
		fprintf(stderr, "Error %d when opening input file %s", err, filename);
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
			if (exit_on_keydown) {
				pPlayer->Destroy();
				exit(0); // need to exit here to avoid joinable exception
				break;
			}
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
				}
				else {
					rate *= 2;
				}
				break;
			case SDLK_KP_MINUS:
				pPlayer->GetRate(&rate);
				err = pPlayer->SetRate(rate / 2);
				if (err != 0) {
					av_log(NULL, AV_LOG_ERROR, "Rate %f unavailable\n", rate / 2);
				}
				else {
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

int MpvAvPlayback::Pause()
{
	if (!_mpvHandle) {
		return ERROR_MPV_GENERIC;
	}

	const char * propertyValue = "yes";

	int err = mpvToJavaErrNo(_mpvSetProperty(_mpvHandle, _pauseProperty, MPV_FORMAT_STRING, &propertyValue));
	if (err != 0) {
		return err;
	}

	return ERROR_NONE;
}

int MpvAvPlayback::IsPaused(bool *isPaused)
{
	if (!_mpvHandle) {
		return ERROR_MPV_GENERIC;
	}

	char* isPausedProperty;
	int err = mpvToJavaErrNo(_mpvGetProperty(_mpvHandle, _pauseProperty, MPV_FORMAT_STRING, &isPausedProperty));
	if (err != 0) {
		return err;
	}

	std::string isPausedString(isPausedProperty);
	*isPaused = isPausedString == "yes";
	_mpvFree(isPausedProperty);

	return ERROR_NONE;
}

int MpvAvPlayback::SetTime(double value)
{
	if (!_mpvHandle) {
		return ERROR_MPV_GENERIC;
	}

	std::string timeString = std::to_string(value);


	const char * cmd[] = { _seekCommand, timeString.c_str(), "absolute", NULL };

	int err = mpvToJavaErrNo(DoMpvCommand(cmd));
	if (err != 0) {
		return err;
	}

	return ERROR_NONE;
}

int MpvAvPlayback::GetPresentationTime(double *presentationTime)
{
	if (!_mpvHandle) {
		return ERROR_MPV_GENERIC;
	}

	// check difference between playback-time and timr-pod 
	// https://mpv.io/manual/master/#command-interface-playback-time
	int err = mpvToJavaErrNo(_mpvGetProperty(_mpvHandle, _playbackTimeProperty, MPV_FORMAT_DOUBLE, presentationTime));
	if (err != 0){
		return err;
	}

	return ERROR_NONE;
}

int MpvAvPlayback::SetVolume(double fVolume)
{
	if (!_mpvHandle) {
		return ERROR_MPV_GENERIC;
	}

	int err = mpvToJavaErrNo(_mpvSetProperty(_mpvHandle, _aoVolumeProperty, MPV_FORMAT_DOUBLE, &fVolume));
	if (err != 0) {
		return err;
	}

	return ERROR_NONE;
}

int MpvAvPlayback::GetVolume(double *pfVolume)
{
	if (!_mpvHandle) {
		return ERROR_MPV_GENERIC;
	}

	int err = mpvToJavaErrNo(_mpvGetProperty(_mpvHandle, _aoVolumeProperty, MPV_FORMAT_DOUBLE, pfVolume));
	if (err != 0) {
		return err;
	}

	return ERROR_NONE;
}
