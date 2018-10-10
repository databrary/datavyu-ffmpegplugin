#include "MpvAvPlayback.h"

MpvAvPlayback::MpvAvPlayback() :
	_streamDuration(0.0),
	_streamFps(0.0),
	_imageHeight(0),
	_imageWidth(0)
{}

MpvAvPlayback::~MpvAvPlayback()
{}

int MpvAvPlayback::Init(const char * filename, const intptr_t windowID)
{
	int err;

	LoadMpvDynamic();
	if (!_libMpvDll)
		return MPV_ERROR_GENERIC;

	_mpvHandle = _mpvCreate();
	if (!_mpvHandle)
		_mpvTerminateDestroy(_mpvHandle);

	_mpvInitialize(_mpvHandle);

	
	_mpvSetOptionString(_mpvHandle, "keep-open", "always");
	// prevent the player from playing at start-up
	_mpvSetOptionString(_mpvHandle, "pause", "yes");

	//double _startUpVolume = 100;
	//err = _mpvSetOption(_mpvHandle, "volume", MPV_FORMAT_DOUBLE, &_startUpVolume);
	//if (err < 0) {
	//	return err;
	//}	

	intptr_t windowId = windowID;
	err = _mpvSetOption(_mpvHandle, "wid", MPV_FORMAT_INT64, &windowId);
	if (err < 0) {
		return err;
	}

	const char *cmd[] = { "loadfile", filename, NULL };
	err = DoMpvCommand(cmd);
	if (err < 0) {
		return err;
	}

	return MPV_ERROR_SUCCESS;
}

void MpvAvPlayback::LoadMpvDynamic()
{
	_libMpvDll = LoadLibrary(L"mpv-1.dll"); // The dll is included in the DEV builds by lachs0r: https://mpv.srsfckn.biz/
	_mpvCreate = (MpvCreate)GetProcAddress(_libMpvDll, "mpv_create");
	_mpvInitialize = (MpvInitialize)GetProcAddress(_libMpvDll, "mpv_initialize");
	_mpvTerminateDestroy = (MpvTerminateDestroy)GetProcAddress(_libMpvDll, "mpv_terminate_destroy");
	_mpvCommand = (MpvCommand)GetProcAddress(_libMpvDll, "mpv_command");
	_mpvCommandString = (MpvCommandString)GetProcAddress(_libMpvDll, "mpv_command_string");
	_mpvSetOption = (MpvSetOption)GetProcAddress(_libMpvDll, "mpv_set_option");
	_mpvSetOptionString = (MpvSetOptionString)GetProcAddress(_libMpvDll, "mpv_set_option_string");
	_mpvGetPropertyString = (MpvGetPropertystring)GetProcAddress(_libMpvDll, "mpv_get_property");
	_mpvGetProperty = (MpvGetProperty)GetProcAddress(_libMpvDll, "mpv_get_property");
	_mpvSetProperty = (MpvSetProperty)GetProcAddress(_libMpvDll, "mpv_set_property");
	_mpvFree = (MpvFree)GetProcAddress(_libMpvDll, "mpv_free");
}

int MpvAvPlayback::Play()
{
	if (!_mpvHandle)
		return MPV_ERROR_GENERIC;

	const char* porpertyValue = "no";
	int err = _mpvSetProperty(_mpvHandle, "pause", MPV_FORMAT_STRING, &porpertyValue);
	if (err < 0) {
		return err;
	}

	return MPV_ERROR_SUCCESS;
}

int MpvAvPlayback::Stop()
{
	if (Pause() < 0) {
		return MPV_ERROR_PROPERTY_ERROR;
	}
	if (SetRate(1) < 0) {
		return MPV_ERROR_COMMAND;
	}

	return MPV_ERROR_SUCCESS;
}

void MpvAvPlayback::toggle_pause()
{
	if (IsPaused()) {
		Play();
	}
	else {
		Pause();
	}
}

int MpvAvPlayback::SetRate(double newRate)
{
	if (!_mpvHandle)
		return MPV_ERROR_GENERIC;

	int err = _mpvSetOption(_mpvHandle, "speed", MPV_FORMAT_DOUBLE, &newRate);
	if ( err < 0) {
		return err;
	}

	return MPV_ERROR_SUCCESS;
}

double MpvAvPlayback::GetRate()
{
	if (!_mpvHandle)
		return MPV_ERROR_GENERIC;

	double currentRate;

	int err = _mpvGetProperty(_mpvHandle, "speed", MPV_FORMAT_DOUBLE, &currentRate);
	if (err < 0) {
		return err;
	}

	return currentRate;
}

float MpvAvPlayback::GetFps()
{
	if (!_mpvHandle)
		return MPV_ERROR_GENERIC;

	if (!_streamFps || _streamFps <= 0 || _streamFps == NULL) {
		int err = _mpvGetProperty(_mpvHandle, "container-fps", MPV_FORMAT_DOUBLE, &_streamFps);
		if (err < 0) {
			return err;
		}
	}

	return _streamFps;
}

int MpvAvPlayback::GetImageWidth()
{
	if (!_mpvHandle)
		return MPV_ERROR_GENERIC;

	if (!_imageWidth || _imageWidth <= 0 || _imageWidth == NULL) {
		int err = _mpvGetProperty(_mpvHandle, "width", MPV_FORMAT_INT64, &_imageWidth);
		if (err < 0) {
			return err;
		}
	}

	return _imageWidth;
}

int MpvAvPlayback::GetImageHeight()
{
	if (!_mpvHandle)
		return MPV_ERROR_GENERIC;

	if (!_imageHeight|| _imageHeight <= 0 || _imageHeight == NULL) {
		int err = _mpvGetProperty(_mpvHandle, "height", MPV_FORMAT_INT64, &_imageHeight);
		if (err < 0) {
			return err;
		}
	}

	return _imageHeight;
}

double MpvAvPlayback::GetDuration()
{
	if (!_mpvHandle)
		return MPV_ERROR_GENERIC;

	if (!_streamDuration || _streamDuration <= 0 || _streamDuration == NULL) {
		// TODO(Reda) Remove this, it is just for testing, need to go through async calls

		while (_streamDuration <= 0) {
			int err = _mpvGetProperty(_mpvHandle, "duration", MPV_FORMAT_DOUBLE, &_streamDuration);
			if (err < 0) {
				return err;
			}
		}
	}

	return _streamDuration;
}

int MpvAvPlayback::StepBackward()
{
	if (!_mpvHandle)
		return MPV_ERROR_GENERIC;

	const char *cmd[] = { "frame-back-step", NULL, NULL };

	int err = DoMpvCommand(cmd);
	if (err < 0) {
		return err;
	}

	return MPV_ERROR_SUCCESS;
}

int MpvAvPlayback::StepForward()
{
	if (!_mpvHandle)
		return MPV_ERROR_GENERIC;

	const char *cmd[] = { "frame-step", NULL, NULL };

	int err = DoMpvCommand(cmd);
	if (err < 0) {
		return err;
	}

	return MPV_ERROR_SUCCESS;
}

int MpvAvPlayback::DoMpvCommand(const char **cmd)
{
	int err = _mpvCommand(_mpvHandle, cmd);
	if (err < 0) {
		printf("Cannot execute the command");
		return MPV_ERROR_COMMAND;
	}

	return MPV_ERROR_SUCCESS;
}

void MpvAvPlayback::Destroy()
{
	_mpvTerminateDestroy(_mpvHandle);
}

void MpvAvPlayback::init_and_event_loop(const char *filename)
{
	if (SDL_Init(SDL_INIT_VIDEO) < 0) {
		printf("SDL init failed");
		exit(1);
	}

	SDL_Window *window =
		SDL_CreateWindow("hi", SDL_WINDOWPOS_CENTERED, SDL_WINDOWPOS_CENTERED,
			1000, 500, SDL_WINDOW_SHOWN |
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
	int err = pPlayer->Init(filename, (long)hwnd);
	if (err) {
		fprintf(stderr, "Error %d when opening input file %s", err, filename);
	}

	double incr, pos, frac, rate, currentVolume;

	while (1) {
		SDL_Event event;
		if (SDL_WaitEvent(&event) != 1)
			printf("event loop error");
		int redraw = 0;
		rate = pPlayer->GetRate();
		currentVolume = pPlayer->GetVolume();
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
				pPlayer->toggle_pause();
				break;
			case SDLK_p:
			case SDLK_SPACE:
				pPlayer->toggle_pause();
				break;
			case SDLK_m:
				//pVideoState->toggle_mute();
				break;
			case SDLK_KP_MULTIPLY:
			case SDLK_0:
				// MPV Audio Range is from 0 - 100
				pPlayer->SetVolume(currentVolume - 10.0);
				break;
			case SDLK_KP_DIVIDE:
			case SDLK_9:
				// MPV Audio Range is from 0 - 100
				pPlayer->SetVolume(currentVolume + 10.0);
				break;
			case SDLK_s: // S: Step to next frame
				pPlayer->StepForward();
				break;
			case SDLK_a:
				//pVideoState->stream_cycle_channel(AVMEDIA_TYPE_AUDIO);
				break;
			case SDLK_KP_PLUS:
				if (pPlayer->SetRate(rate * 2)) {
					av_log(NULL, AV_LOG_ERROR, "Rate %f unavailable\n", rate * 2);
				}
				else {
					rate *= 2;
				}
				break;
			case SDLK_KP_MINUS:
				if (pPlayer->SetRate(rate / 2)) {
					av_log(NULL, AV_LOG_ERROR, "Rate %f unavailable\n", rate / 2);
				}
				else {
					rate /= 2;
				}
				break;
			case SDLK_v:
				//pVideoState->stream_cycle_channel(AVMEDIA_TYPE_VIDEO);
				break;
			case SDLK_c:
				//pVideoState->stream_cycle_channel(AVMEDIA_TYPE_VIDEO);
				//pVideoState->stream_cycle_channel(AVMEDIA_TYPE_AUDIO);
				//pVideoState->stream_cycle_channel(AVMEDIA_TYPE_SUBTITLE);
				break;
			case SDLK_t:
				//pVideoState->stream_cycle_channel(AVMEDIA_TYPE_SUBTITLE);
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
				pos = pPlayer->GetPresentationTime();
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
	if (!_mpvHandle)
		return MPV_ERROR_GENERIC;

	const char * propertyValue = "yes";

	int err = _mpvSetProperty(_mpvHandle, "pause", MPV_FORMAT_STRING, &propertyValue);
	if (err < 0) {
		return err;
	}

	return MPV_ERROR_SUCCESS;
}

bool MpvAvPlayback::IsPaused()
{
	if (!_mpvHandle)
		return true;

	char* isPausedProperty;
	_mpvGetProperty(_mpvHandle, "pause", MPV_FORMAT_STRING, &isPausedProperty);
	std::string isPausedString(isPausedProperty);
	_mpvFree(isPausedProperty);
	bool isPaused = isPausedString == "yes";

	return isPaused;
}

int MpvAvPlayback::SetTime(double value)
{
	if (!_mpvHandle)
		return MPV_ERROR_GENERIC;

	std::string timeString = std::to_string(value);


	const char * cmd[] = { "seek", timeString.c_str(), "absolute", NULL };

	int err = DoMpvCommand(cmd);
	if (err < 0) {
		return err;
	}

	return MPV_ERROR_SUCCESS;
}

double MpvAvPlayback::GetPresentationTime()
{
	if (!_mpvHandle)
		return NULL;
	double presentationTime;
	// check difference between playback-time and timr-pod 
	// https://mpv.io/manual/master/#command-interface-playback-time
	if(_mpvGetProperty(_mpvHandle, "playback-time", MPV_FORMAT_DOUBLE, &presentationTime) < 0){
		return NULL;
	}

	return presentationTime;
}

int MpvAvPlayback::SetVolume(float fVolume)
{
	if (!_mpvHandle)
		return MPV_ERROR_GENERIC;

	double propertyValue = fVolume;

	int err = _mpvSetProperty(_mpvHandle, "ao-volume", MPV_FORMAT_DOUBLE, &propertyValue);
	if (err < 0) {
		return err;
	}

	return MPV_ERROR_SUCCESS;
}

double MpvAvPlayback::GetVolume()
{
	if (!_mpvHandle)
		return MPV_ERROR_GENERIC;

	double currentVolume;

	int err = _mpvGetProperty(_mpvHandle, "ao-volume", MPV_FORMAT_DOUBLE, &currentVolume);
	if (err < 0) {
		return err;
	}

	return currentVolume;
}
