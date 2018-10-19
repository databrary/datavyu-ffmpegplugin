#ifndef _MPV_AV_PLAYBACK_PIPELINE_H_
#define _MPV_AV_PLAYBACK_PIPELINE_H_

#include "PipelineOptions.h"
#include "Pipeline.h"
#include "MpvAvPlayback.h"

/**
* class MpvAvPlaybackPipeline
*
* Class representing a MPV audio-video pipeline.
*
* This pipeline is using MPV API "client.api" to render the 
* and audio video stream in a any window that is referenced by 
* a window ID, Note that we are using a Java Frame.
*/
class MpvAvPlaybackPipeline : public CPipeline
{
public:
	virtual uint32_t    Init(const char * input_file);
	virtual void        Dispose();

	MpvAvPlaybackPipeline(CPipelineOptions* pOptions, const intptr_t windowID);
	virtual ~MpvAvPlaybackPipeline();

	intptr_t windowID;
private:
	virtual uint32_t        Play();
	virtual uint32_t        Stop();
	virtual uint32_t        Pause();
	virtual uint32_t        StepForward();
	virtual uint32_t        StepBackward();
	virtual uint32_t        Finish();

	virtual uint32_t        Seek(double dSeekTime);

	virtual uint32_t        GetDuration(double* pdDuration);
	virtual uint32_t        GetStreamTime(double* pdStreamTime);
	virtual uint32_t        GetFps(double* pdFps);
	virtual uint32_t        GetImageWidth(int* iWidth) const;
	virtual uint32_t        GetImageHeight(int* iHeight) const;

	virtual uint32_t        SetRate(float fRate);
	virtual uint32_t        GetRate(float* pfRate);

	virtual uint32_t        SetVolume(float fVolume);
	virtual uint32_t        GetVolume(float* pfVolume);

	virtual uint32_t        SetBalance(float fBalance);
	virtual uint32_t        GetBalance(float* pfBalance);

	virtual uint32_t        SetAudioSyncDelay(long lMillis);
	virtual uint32_t        GetAudioSyncDelay(long* plMillis);

	MpvAvPlayback* pMpvPlayback;
};

#endif  //_MPV_AV_PLAYBACK_PIPELINE_H_
