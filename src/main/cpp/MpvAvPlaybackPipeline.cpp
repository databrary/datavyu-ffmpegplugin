#include "MpvAvPlaybackPipeline.h"
#include "MediaPlayerErrors.h"

uint32_t MpvAvPlaybackPipeline::Init(const char * input_file)
{
	pMpvPlayback = new (std::nothrow) MpvAvPlayback();

	if (!pMpvPlayback) {
		return ERROR_PIPELINE_NULL;	
	}

	int err = pMpvPlayback->Init(input_file, windowID);
	if (err) {
		delete pMpvPlayback;
		return err;
	}

	UpdatePlayerState(Ready);

	return ERROR_NONE;
}

void MpvAvPlaybackPipeline::Dispose()
{
	pMpvPlayback->Destroy();
	delete pMpvPlayback;
	pMpvPlayback = nullptr;
}

MpvAvPlaybackPipeline::MpvAvPlaybackPipeline(CPipelineOptions * pOptions, 
												intptr_t windowID)
	: CPipeline(pOptions), windowID(windowID),pMpvPlayback(nullptr) {
}

MpvAvPlaybackPipeline::~MpvAvPlaybackPipeline()
{}

uint32_t MpvAvPlaybackPipeline::Play()
{
	if (pMpvPlayback == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}
	
	int err = pMpvPlayback->Play();
	if (err < 0) {
		return ERROR_FFMPEG_UNKNOWN;
	}

	UpdatePlayerState(Playing);

	return ERROR_NONE; // no error
}

uint32_t MpvAvPlaybackPipeline::Stop()
{
	if (pMpvPlayback == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}

	int err = pMpvPlayback->Stop();
	if (err < 0) {
		return ERROR_FFMPEG_UNKNOWN;
	}

	UpdatePlayerState(Stopped);
	return ERROR_NONE; // no error
}

uint32_t MpvAvPlaybackPipeline::Pause()
{
	if (pMpvPlayback == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}
	int err = pMpvPlayback->TogglePause();
	if (err < 0) {
		return ERROR_FFMPEG_UNKNOWN;
	}

	bool isPaused;
	if (pMpvPlayback->IsPaused(&isPaused)) {
		UpdatePlayerState(Paused);
	}
	else {
		UpdatePlayerState(Playing);
	}

	return ERROR_NONE; // no error
}

uint32_t MpvAvPlaybackPipeline::StepForward()
{
	if (pMpvPlayback == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}

	int err = pMpvPlayback->StepForward();
	if (err < 0) {
		return err;
	}

	UpdatePlayerState(Paused);
	return ERROR_NONE;
}

uint32_t MpvAvPlaybackPipeline::StepBackward()
{
	if (pMpvPlayback == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}

	int err = pMpvPlayback->StepBackward();
	if (err < 0) {
		return err;
	}

	UpdatePlayerState(Paused);

	return ERROR_NONE;
}

uint32_t MpvAvPlaybackPipeline::Finish()
{
	UpdatePlayerState(Finished);

	return ERROR_NONE;
}

uint32_t MpvAvPlaybackPipeline::Seek(double dSeekTime)
{
	if (pMpvPlayback == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}
	
	int err = pMpvPlayback->SetTime(dSeekTime);
	if (err < 0) {
		return err;
	}

	return ERROR_NONE;
}

uint32_t MpvAvPlaybackPipeline::GetDuration(double * pdDuration)
{
	if (pMpvPlayback == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}

	double duration;
	int err = pMpvPlayback->GetDuration(&duration);
	if (err != 0) {
		return err;
	}

	*pdDuration = duration;

	return ERROR_NONE; // no error
}

uint32_t MpvAvPlaybackPipeline::GetStreamTime(double * pdStreamTime)
{
	if (pMpvPlayback == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}

	double presentationTime;
	int err = pMpvPlayback->GetPresentationTime(&presentationTime);
	if (err != 0) {
		return err;
	}

	*pdStreamTime = presentationTime;

	return ERROR_NONE; // no error
}

uint32_t MpvAvPlaybackPipeline::GetFps(double * pdFps)
{
	if (pMpvPlayback == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}

	double fps;
	int err = pMpvPlayback->GetFps(&fps);
	if (err != 0) {
		return err;
	}

	*pdFps = fps;

	return ERROR_NONE;
}

uint32_t MpvAvPlaybackPipeline::GetImageWidth(int * iWidth) const
{
	if (pMpvPlayback == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}

	int64_t imageWidth;
	int err = pMpvPlayback->GetImageWidth(&imageWidth);
	if (err != 0) {
		return err;
	}

	*iWidth = imageWidth;

	return ERROR_NONE;
}

uint32_t MpvAvPlaybackPipeline::GetImageHeight(int * iHeight) const
{
	if (pMpvPlayback == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}

	int64_t imageHeight;
	int err = pMpvPlayback->GetImageHeight(&imageHeight);
	if (err != 0) {
		return err;
	}

	*iHeight = imageHeight;

	return ERROR_NONE;
}

uint32_t MpvAvPlaybackPipeline::SetRate(float fRate)
{
	if (pMpvPlayback == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}

	int err = pMpvPlayback->SetRate((double)fRate);
	if (err != 0) {
		return err;
	}

	return ERROR_NONE;
}

uint32_t MpvAvPlaybackPipeline::GetRate(float * pfRate)
{
	if (pMpvPlayback == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}

	double rate;
	int err  = pMpvPlayback->GetRate(&rate);
	if (err != 0) {
		return err;
	}

	*pfRate = rate;

	return ERROR_NONE;
}

uint32_t MpvAvPlaybackPipeline::SetVolume(float fVolume)
{
	if (pMpvPlayback == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}

	int err = pMpvPlayback->SetVolume((double)fVolume);
	if (err < 0) {
		return err;
	}

	return ERROR_NONE;
}

uint32_t MpvAvPlaybackPipeline::GetVolume(float * pfVolume)
{
	if (pMpvPlayback == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}

	double volume;
	int err = pMpvPlayback->GetVolume(&volume);
	if (err < 0) {
		return err;
	}

	*pfVolume = volume;

	return ERROR_NONE;
}

uint32_t MpvAvPlaybackPipeline::SetBalance(float fBalance)
{
	return ERROR_NONE;
}

uint32_t MpvAvPlaybackPipeline::GetBalance(float * pfBalance)
{
	return ERROR_NONE;
}

uint32_t MpvAvPlaybackPipeline::SetAudioSyncDelay(long lMillis)
{
	return ERROR_NONE;
}

uint32_t MpvAvPlaybackPipeline::GetAudioSyncDelay(long * plMillis)
{
	return ERROR_NONE;
}
