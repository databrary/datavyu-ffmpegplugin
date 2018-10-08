#include "MpvAvPlaybackPipeline.h"
#include "FfmpegMediaErrors.h"

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

	return ERROR_NONE;
}

void MpvAvPlaybackPipeline::Dispose()
{
	pMpvPlayback->Destroy();
	delete pMpvPlayback;
	pMpvPlayback = nullptr;
}

MpvAvPlaybackPipeline::MpvAvPlaybackPipeline(CPipelineOptions * pOptions, 
												long windowID)
	: CPipeline(pOptions), windowID(windowID),pMpvPlayback(nullptr) {
}

MpvAvPlaybackPipeline::~MpvAvPlaybackPipeline()
{}

uint32_t MpvAvPlaybackPipeline::Play()
{
	if (pMpvPlayback == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}
	pMpvPlayback->Play();

	return ERROR_NONE; // no error
}

uint32_t MpvAvPlaybackPipeline::Stop()
{
	if (pMpvPlayback == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}
	pMpvPlayback->Stop();

	return ERROR_NONE; // no error
}

uint32_t MpvAvPlaybackPipeline::Pause()
{
	if (pMpvPlayback == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}
	pMpvPlayback->toggle_pause();

	return ERROR_NONE; // no error
}

uint32_t MpvAvPlaybackPipeline::StepForward()
{
	if (pMpvPlayback == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}

	pMpvPlayback->StepForward();
	return ERROR_NONE;
}

uint32_t MpvAvPlaybackPipeline::StepBackward()
{
	if (pMpvPlayback == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}

	pMpvPlayback->StepBackward();

	return ERROR_NONE;
}

uint32_t MpvAvPlaybackPipeline::Finish()
{
	return ERROR_NONE;
}

uint32_t MpvAvPlaybackPipeline::Seek(double dSeekTime)
{
	if (pMpvPlayback == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}
	//TODO(Reda): return error codes
	 pMpvPlayback->SetTime(dSeekTime);

	 return ERROR_NONE;
}

uint32_t MpvAvPlaybackPipeline::GetDuration(double * pdDuration)
{
	if (pMpvPlayback == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}
	*pdDuration = pMpvPlayback->GetDuration();

	return ERROR_NONE; // no error
}

uint32_t MpvAvPlaybackPipeline::GetStreamTime(double * pdStreamTime)
{
	if (pMpvPlayback == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}

	*pdStreamTime = pMpvPlayback->GetPresentationTime();

	return ERROR_NONE; // no error
}

uint32_t MpvAvPlaybackPipeline::GetFps(double * pdFps)
{
	return ERROR_NONE;
}

uint32_t MpvAvPlaybackPipeline::SetRate(float fRate)
{
	if (pMpvPlayback == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}

	pMpvPlayback->SetRate(fRate);

	return ERROR_NONE;
}

uint32_t MpvAvPlaybackPipeline::GetRate(float * pfRate)
{
	if (pMpvPlayback == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}

	// TODO(Reda): implement this
	*pfRate = pMpvPlayback->GetRate();

	return ERROR_NONE;
}

uint32_t MpvAvPlaybackPipeline::SetVolume(float fVolume)
{
	return ERROR_NONE;
}

uint32_t MpvAvPlaybackPipeline::GetVolume(float * pfVolume)
{
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
