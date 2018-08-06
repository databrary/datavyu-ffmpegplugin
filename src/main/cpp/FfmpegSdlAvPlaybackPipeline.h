#ifndef _FFMPEG_AV_PLAYBACK_PIPELINE_H_
#define _FFMPEG_AV_PLAYBACK_PIPELINE_H_

#include "PipelineOptions.h"
#include "Pipeline.h"
#include "FfmpegSdlAvPlayback.h"

/**
* class FfmpegAVPlaybackPipeline
*
* Class representing a Ffmpeg audio-video pipeline.
*
* Note, currently this pipeline uses SDL to play audio/image
*/
class FfmpegSdlAvPlaybackPipeline : public CPipeline
{
public:
	virtual uint32_t    Init(const char * input_file);
	virtual void        Dispose();
	FfmpegSdlAvPlaybackPipeline(CPipelineOptions* pOptions);
	virtual ~FfmpegSdlAvPlaybackPipeline();

private:
	virtual uint32_t        Play();
	virtual uint32_t        Stop();
	virtual uint32_t        Pause();
	virtual uint32_t        Finish();

	virtual uint32_t        Seek(double dSeekTime);

	virtual uint32_t        GetDuration(double* pdDuration);
	virtual uint32_t        GetStreamTime(double* pdStreamTime);

	virtual uint32_t        SetRate(float fRate);
	virtual uint32_t        GetRate(float* pfRate);

	virtual uint32_t        SetVolume(float fVolume);
	virtual uint32_t        GetVolume(float* pfVolume);

	virtual uint32_t        SetBalance(float fBalance);
	virtual uint32_t        GetBalance(float* pfBalance);

	virtual uint32_t        SetAudioSyncDelay(long lMillis);
	virtual uint32_t        GetAudioSyncDelay(long* plMillis);

	// Fullfill the data interface
	virtual uint32_t		HasAudioData(bool* bAudioData) const;
	virtual uint32_t		HasImageData(bool* bImageData) const;
	virtual uint32_t		GetImageWidth(int* iWidth) const;
	virtual uint32_t		GetImageHeight(int* iHeight) const;
	virtual uint32_t		GetAudioFormat(AudioFormat* pAudioParams) const;
	virtual uint32_t		GetPixelFormat(PixelFormat* pPixelFormat) const;
	virtual uint32_t		GetImageBuffer(uint8_t** ppImageBuffer);
	virtual uint32_t		GetAudioBuffer(uint8_t** ppAudioBuffer, const int len);

	FfmpegSdlAvPlayback* pSdlPlayback;
};

#endif  //_FFMPEG_AV_PLAYBACK_PIPELINE_H_
