#ifndef FFMPEGJAVAAVPLAYBACKPIPELINE_H_
#define FFMPEGJAVAAVPLAYBACKPIPELINE_H_

#include "Pipeline.h"
#include "FfmpegJavaAvPlayback.h"
#include "FfmpegMediaErrors.h";


// TODO(Reda): Implement this interface
class FfmpegJavaAvPlaybackPipline : public CPipeline {
public:
	virtual uint32_t    Init(const char * input_file);
	virtual void        Dispose();
	FfmpegJavaAvPlaybackPipline(CPipelineOptions* pOptions);
	virtual ~FfmpegJavaAvPlaybackPipline();

private:
	virtual uint32_t        Play();
	virtual uint32_t        Stop();
	virtual uint32_t        Pause();
	virtual uint32_t        StepForward();
	virtual uint32_t        Finish();

	virtual uint32_t        Seek(double dSeekTime);

	virtual uint32_t        GetDuration(double* pdDuration);
	virtual uint32_t        GetStreamTime(double* pdStreamTime);
	virtual uint32_t        GetFPS(double* pdFps);

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
	virtual uint32_t		UpdateImageBuffer(uint8_t* pImageData, const long len);
	virtual uint32_t		UpdateAudioBuffer(uint8_t* pAudioData, const long len);

	FfmpegJavaAvPlayback* pJavaPlayback;
};

#endif // !FFMPEGJAVAAVPLAYBACKPIPELINE_H_

