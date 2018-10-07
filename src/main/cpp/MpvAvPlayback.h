#ifndef MPVSDLAVPLAYBACK_H_
#define MPVSDLAVPLAYBACK_H_

#include <Windows.h>
#include <atomic>
#include <cstdint>
#include <string.h>  
#include <MPV/client.h> // This include is just for the MPV_FORMAT usage
#include "FfmpegAVPlayback.h"

// MPV functtions signature
typedef intptr_t(*MpvCreate)();
typedef int(*MpvInitialize)(intptr_t);
typedef int(*MpvCommand)(intptr_t, const char**);
typedef int(*MpvTerminateDestroy)(intptr_t);
typedef int(*MpvSetOption)(intptr_t, const char *, int,void *);
typedef int(*MpvSetOptionString)(intptr_t, const char *, const char *);
typedef char*(*MpvGetPropertystring)(intptr_t, const char *);
typedef int(*MpvGetProperty)(intptr_t, const char *,int,void *);
typedef int(*MpvSetProperty)(intptr_t, const char *,int,void *);
typedef void(*MpvFree)(intptr_t);

/* The MpvAvPlayback class will the mpv-1.dll (must be copied 
* in the working directory) and will extract needed functions
* for the proper useage of the player.
* Note: No need to add the mpv-1.lib to the linker since 
* we are relying on the function poiter extracted for the dll 
* and the include of the client.h is just to use the type defined
* in the API; mpv_format for instance
*/
// TODO(Reda) Much naming convention of the ffmpeg plugin and error returned
class MpvAvPlayback : public FfmpegAvPlayback {

private:
	const int MpvFormatString = 1;

	HINSTANCE _libMpvDll;
	intptr_t _mpvHandle;

	//Function pointers to mpv api functions
	MpvCreate _mpvCreate;
	MpvInitialize _mpvInitialize;
	MpvCommand _mpvCommand;
	MpvTerminateDestroy _mpvTerminateDestroy;
	MpvSetOption _mpvSetOption;
	MpvSetOptionString _mpvSetOptionString;
	MpvGetPropertystring _mpvGetPropertyString;
	MpvGetProperty _mpvGetProperty;
	MpvSetProperty _mpvSetProperty;
	MpvFree _mpvFree;

	double _streamDuration;
	
	void LoadMpvDynamic();

	void DoMpvCommand(const char **cmd);
	void Pause();

public:
	MpvAvPlayback();
	~MpvAvPlayback();

	int Init(const char *filename, const long windowID);

	void Destroy();

	bool IsPaused();
	void Play();
	void Stop();
	void toggle_pause();
	void SetRate(const double newRate);
	double GetRate();
	void StepBackward();
	void StepForward();
	void SetTime(double value);
};

#endif MPVAVPLAYBACK_H_

