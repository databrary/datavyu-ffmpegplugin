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
		return 1;

	_mpvHandle = _mpvCreate();
	if (!_mpvHandle)
		return 1;

	_mpvInitialize(_mpvHandle);
	_mpvSetOptionString(_mpvHandle, "keep-open", "always");
	// prevent the player from playing at start-up
	_mpvSetOptionString(_mpvHandle, "pause", "yes");
	int windowId = windowID;
	_mpvSetOption(_mpvHandle, "wid", MPV_FORMAT_INT64, &windowId);

	const char *cmd[] = { "loadfile", filename, NULL };
	DoMpvCommand(cmd);

	//TODO(Reda): find a way to thrutle this in order to get the duration 
	// Need to wait for the file to load before requesting a duration

	//if (_mpvGetProperty(_mpvHandle, "duration", MPV_FORMAT_DOUBLE, &_streamDuration) < 0) {
	//	return 1;
	//}
	
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

void MpvAvPlayback::Play()
{
	if (!_mpvHandle)
		return;

	const char* porpertyValue = "no";
	_mpvSetProperty(_mpvHandle, "pause", MpvFormatString, &porpertyValue);

}

void MpvAvPlayback::Stop()
{
	Pause();
	SetRate(0);
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

void MpvAvPlayback::SetRate(double newRate)
{
	if (!_mpvHandle)
		return;

	_mpvSetOption(_mpvHandle, "speed", MPV_FORMAT_DOUBLE, &newRate);
}

double MpvAvPlayback::GetRate()
{
	if (!_mpvHandle)
		return 1;

	double currentRate;
	if (_mpvGetProperty(_mpvHandle, "speed", MPV_FORMAT_DOUBLE, &currentRate) > 0) {
		return 1;
	}

	return currentRate;
}

void MpvAvPlayback::StepBackward()
{
	if (!_mpvHandle)
		return;
	const char *cmd[] = { "frame-back-step"};
	DoMpvCommand(cmd);
}

void MpvAvPlayback::StepForward()
{
	if (!_mpvHandle)
		return;
	const char *cmd[] = { "frame-step" };
	DoMpvCommand(cmd);
}

void MpvAvPlayback::DoMpvCommand(const char **cmd)
{
	_mpvCommand(_mpvHandle, cmd);
}

void MpvAvPlayback::Destroy()
{
	_mpvTerminateDestroy(_mpvHandle);
}

void MpvAvPlayback::Pause()
{
	if (!_mpvHandle)
		return;

	const char * propertyValue = "yes";
	_mpvSetProperty(_mpvHandle, "pause", MpvFormatString, &propertyValue);
}

bool MpvAvPlayback::IsPaused()
{
	if (!_mpvHandle)
		return true;

	const char* isPausedProperty = _mpvGetPropertyString(_mpvHandle, "pause");
	bool isPaused = isPausedProperty == "yes";
	return isPaused;
}

void MpvAvPlayback::SetTime(double value)
{
	if (!_mpvHandle)
		return;

	std::string timeString = std::to_string(value);

	const char * cmd[] = { "seek", timeString.c_str(), "absolute" };
	DoMpvCommand(cmd);
}