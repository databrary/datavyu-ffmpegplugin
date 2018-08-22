#ifndef _PIPELINEDATA_H_
#define _PIPELINEDATA_H_

#include <stdint.h>
#include <sys/stat.h>
#include "Pipeline.h"
#include "PipelineOptions.h"
#include "AudioVideoFormats.h"

class CPipelineData : public CPipeline
{
public:
	CPipelineData(CPipelineOptions* pOptions = NULL);
	virtual ~CPipelineData();

	virtual uint32_t        Init(const char * filename) = 0;
	virtual void            Dispose();

	virtual uint32_t        Play() = 0;
	virtual uint32_t        Stop() = 0;
	virtual uint32_t        Pause() = 0;
	virtual uint32_t        StepForward() = 0;
	virtual uint32_t        Finish() = 0;

	virtual uint32_t        Seek(double dSeekTime) = 0;

	virtual uint32_t        GetDuration(double* pdDuration) = 0;
	virtual uint32_t        GetStreamTime(double* pdStreamTime) = 0;
	virtual uint32_t		GetFps(double* pdFps) = 0;

	virtual uint32_t        SetRate(float fRate) = 0;
	virtual uint32_t        GetRate(float* pfRate) = 0;

	virtual uint32_t        SetVolume(float fVolume) = 0;
	virtual uint32_t        GetVolume(float* pfVolume) = 0;

	virtual uint32_t        SetBalance(float fBalance) = 0;
	virtual uint32_t        GetBalance(float* pfBalance) = 0;

	virtual uint32_t        SetAudioSyncDelay(long lMillis) = 0;
	virtual uint32_t        GetAudioSyncDelay(long* plMillis) = 0;

	virtual uint32_t		HasAudioData(bool* bAudioData) const = 0;
	virtual uint32_t		HasImageData(bool* bImageData) const = 0;
	virtual uint32_t		GetImageWidth(int* iWidth) const = 0;
	virtual uint32_t		GetImageHeight(int* iHeight) const = 0;
	virtual uint32_t		GetAudioFormat(AudioFormat* pAudioFormat) const = 0;
	virtual uint32_t		GetPixelFormat(PixelFormat* pPixelFormat) const = 0;
	virtual uint32_t		UpdateImageBuffer(uint8_t* pImageBuffer, const long len) = 0;
	virtual uint32_t		UpdateAudioBuffer(uint8_t* pAudioBuffer, const long len) = 0;
};

#endif  //_PIPELINEDATA_H_
