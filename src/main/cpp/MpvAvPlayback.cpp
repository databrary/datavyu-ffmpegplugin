#include "MpvAvPlayback.h"

MpvAvPlayback::MpvAvPlayback():
_streamDuration(0)
{}

MpvAvPlayback::~MpvAvPlayback()
{}

int MpvAvPlayback::Init(const char * filename, const long windowID)
{
	if (!_mpvHandle)
		_mpvTerminateDestroy(_mpvHandle);

	LoadMpvDynamic();
	if (!_libMpvDll)
		return NULL;

	_mpvHandle = _mpvCreate();
	if (!_mpvHandle)
		return NULL;

	_mpvInitialize(_mpvHandle);
	_mpvSetOptionString(_mpvHandle, "keep-open", "always");
	// prevent the player from playing at start-up
	_mpvSetOptionString(_mpvHandle, "pause", "yes");
	int windowId = windowID;
	_mpvSetOption(_mpvHandle, "wid", MPV_FORMAT_INT64, &windowId);

	const char *cmd[] = { "loadfile", filename, NULL };
	DoMpvCommand(cmd);
	
	return 0;
}

void MpvAvPlayback::LoadMpvDynamic()
{
	_libMpvDll = LoadLibrary(L"mpv-1.dll"); // The dll is included in the DEV builds by lachs0r: https://mpv.srsfckn.biz/
	_mpvCreate = (MpvCreate)GetProcAddress(_libMpvDll, "mpv_create");
	_mpvInitialize = (MpvInitialize)GetProcAddress(_libMpvDll, "mpv_initialize");
	_mpvTerminateDestroy = (MpvTerminateDestroy)GetProcAddress(_libMpvDll, "mpv_terminate_destroy");
	_mpvCommand = (MpvCommand)GetProcAddress(_libMpvDll, "mpv_command");
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
	int err = _mpvSetProperty(_mpvHandle, "pause", MpvFormatString, &porpertyValue);
	if (err != 0) {
		return err;
	}

	return MPV_ERROR_SUCCESS;
}

int MpvAvPlayback::Stop()
{
	if (Pause() != 0) {
		return MPV_ERROR_PROPERTY_ERROR;
	}
	if (SetRate(0.0) != 0) {
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
	if ( err != 0) {
		return err;
	}

	return MPV_ERROR_SUCCESS;
}

double MpvAvPlayback::GetRate()
{
	if (!_mpvHandle)
		return NULL;

	double currentRate;

	int err = _mpvGetProperty(_mpvHandle, "speed", MPV_FORMAT_DOUBLE, &currentRate);
	if (err != 0) {
		return NULL;
	}

	return currentRate;
}

double MpvAvPlayback::GetDuration()
{
	if (!_streamDuration || _streamDuration == 0 || _streamDuration == NULL) {
		int err = _mpvGetProperty(_mpvHandle, "duration", MPV_FORMAT_DOUBLE, &_streamDuration);
		if ( err != 0) {
			return NULL;
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
	if (err != 0) {
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
	if (err != 0) {
		return err;
	}

	return MPV_ERROR_SUCCESS;
}

int MpvAvPlayback::DoMpvCommand(const char **cmd)
{
	if (_mpvCommand(_mpvHandle, cmd) != 0) {
		return MPV_ERROR_COMMAND;
	}

	return MPV_ERROR_SUCCESS;
}

void MpvAvPlayback::Destroy()
{
	_mpvTerminateDestroy(_mpvHandle);
}

int MpvAvPlayback::Pause()
{
	if (!_mpvHandle)
		return MPV_ERROR_GENERIC;

	const char * propertyValue = "yes";

	int err = _mpvSetProperty(_mpvHandle, "pause", MpvFormatString, &propertyValue);
	if (err != 0) {
		return err;
	}

	return MPV_ERROR_SUCCESS;
}

bool MpvAvPlayback::IsPaused()
{
	if (!_mpvHandle)
		return true;

	const char* isPausedProperty = _mpvGetPropertyString(_mpvHandle, "pause");
	bool isPaused = isPausedProperty == "yes";
	return isPaused;
}

int MpvAvPlayback::SetTime(double value)
{
	if (!_mpvHandle)
		return MPV_ERROR_GENERIC;

	std::string timeString = std::to_string(value);

	const char * cmd[] = { "seek", timeString.c_str(), "absolute" };

	int err = DoMpvCommand(cmd);
	if (err != 0) {
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
	if(_mpvGetProperty(_mpvHandle, "playback-time", MPV_FORMAT_DOUBLE, &presentationTime) != 0){
		return NULL;
	}

	return presentationTime;
}
