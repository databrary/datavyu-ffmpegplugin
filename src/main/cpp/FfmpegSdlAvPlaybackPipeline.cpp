#include "FfmpegSdlAvPlaybackPipeline.h"
#include "MediaPlayerErrors.h"

FfmpegSdlAvPlaybackPipeline::FfmpegSdlAvPlaybackPipeline(CPipelineOptions* pOptions) 
	: CPipeline(pOptions), pSdlPlayback(nullptr) 
{}

FfmpegSdlAvPlaybackPipeline::~FfmpegSdlAvPlaybackPipeline() {
	// Clean-up done in dispose that is called from the destructor of the super-class
}

uint32_t FfmpegSdlAvPlaybackPipeline::Init(const char * input_file) {
	// TODO: Proper error handling and wiring up of input arguments
	av_log_set_flags(AV_LOG_SKIP_REPEATED);
	av_log(NULL, AV_LOG_WARNING, "Init Network\n");
	AVInputFormat *file_iformat = nullptr;

	pSdlPlayback = new (std::nothrow) FfmpegSdlAvPlayback();

	if (!pSdlPlayback) {
		return ERROR_PIPELINE_NULL;
	}

	int err = pSdlPlayback->Init(input_file, file_iformat);
	if (err) {
		delete pSdlPlayback;
		return err;
	}

	// Assign the callback functions	
	pSdlPlayback->set_player_state_callback_func(TO_UNKNOWN, [this] {
		this->UpdatePlayerState(Unknown);
	});
	pSdlPlayback->set_player_state_callback_func(TO_READY, [this] {
		this->UpdatePlayerState(Ready);
	});
	pSdlPlayback->set_player_state_callback_func(TO_PLAYING, [this] {
		this->UpdatePlayerState(Playing);
	});
	pSdlPlayback->set_player_state_callback_func(TO_PAUSED, [this] {
		this->UpdatePlayerState(Paused);
	});
	pSdlPlayback->set_player_state_callback_func(TO_STOPPED, [this] {
		this->UpdatePlayerState(Stopped);
	});
	pSdlPlayback->set_player_state_callback_func(TO_STALLED, [this] {
		this->UpdatePlayerState(Stalled);
	});
	pSdlPlayback->set_player_state_callback_func(TO_FINISHED, [this] {
		this->UpdatePlayerState(Finished);
	});
	
	err = pSdlPlayback->init_and_start_display_loop();
	if (err) {
		return err;
	}

	return ERROR_NONE;
}

void FfmpegSdlAvPlaybackPipeline::Dispose() {
	pSdlPlayback->destroy();
	delete pSdlPlayback;
	pSdlPlayback = nullptr;
}

uint32_t FfmpegSdlAvPlaybackPipeline::Play() {
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}
	pSdlPlayback->play();

	return ERROR_NONE; // no error
}

uint32_t FfmpegSdlAvPlaybackPipeline::Stop() {
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}
	pSdlPlayback->stop();

	return ERROR_NONE; // no error
}

uint32_t FfmpegSdlAvPlaybackPipeline::Pause() {
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}
	pSdlPlayback->toggle_pause();

	return ERROR_NONE; // no error
}

uint32_t FfmpegSdlAvPlaybackPipeline::StepForward()
{
	if (pSdlPlayback == nullptr)
		return ERROR_PLAYBACK_NULL;

	pSdlPlayback->step_to_next_frame();

	return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::StepBackward()
{
	return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::Finish() {
	// TODO(fraudies): Stalling and finish need to be set from the video player
	return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::Seek(double dSeekTime) {
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}
	double pos = pSdlPlayback->get_stream_time();
	if (isnan(pos))
		pos = (double)pSdlPlayback->get_seek_pos() / AV_TIME_BASE;
	double incr = dSeekTime - pos;
	if (pSdlPlayback->get_start_time() != AV_NOPTS_VALUE && dSeekTime < pSdlPlayback->get_start_time() / (double)AV_TIME_BASE)
		dSeekTime = pSdlPlayback->get_start_time() / (double)AV_TIME_BASE;
#ifdef _DEBUG
	printf("Seeking From Time %7.2f sec To %7.2f sec with Incr %7.2f sec \n",
		(pos - incr),
		dSeekTime,
		incr);
	printf("Clocks Before Seek: Ext : %7.2f sec - Aud : %7.2f sec - Vid : %7.2f sec - Error : %7.2f\n",
		pSdlPlayback->get_VideoState()->get_pExtclk()->get_clock(),
		pSdlPlayback->get_VideoState()->get_pAudclk()->get_clock(),
		pSdlPlayback->get_VideoState()->get_pVidclk()->get_clock(),
		abs(pSdlPlayback->get_VideoState()->get_pExtclk()->get_clock() - pSdlPlayback->get_VideoState()->get_pAudclk()->get_clock()));
#endif // _DEBUG

	pSdlPlayback->stream_seek((int64_t)(dSeekTime * AV_TIME_BASE), (int64_t)(incr * AV_TIME_BASE), 0);

	return ERROR_NONE; // no error
}

uint32_t FfmpegSdlAvPlaybackPipeline::GetDuration(double* pdDuration) {
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}
	*pdDuration = pSdlPlayback->get_duration();

	return ERROR_NONE; // no error
}

uint32_t FfmpegSdlAvPlaybackPipeline::GetStreamTime(double* pdStreamTime) {
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}

	// The master clock (Audio Clock by default) could return NaN and affect 
	// performance while seeking. However returning the external clock should 
	// resolve this issue (Note that the timestamp return by the external is not as
	// accurate as the audio clock  (Master))
	//*pdStreamTime = pSdlPlayback->get_master_clock();

	*pdStreamTime = pSdlPlayback->get_stream_time();

	return ERROR_NONE; // no error
}

uint32_t FfmpegSdlAvPlaybackPipeline::GetFps(double * pdFps) {
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}

	*pdFps = pSdlPlayback->get_fps();

	return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::SetRate(float fRate) {
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}

	return pSdlPlayback->set_rate(fRate);
}

uint32_t FfmpegSdlAvPlaybackPipeline::GetRate(float* pfRate) {
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}

	*pfRate = pSdlPlayback->get_rate();

	return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::SetVolume(float fVolume) {
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}
	pSdlPlayback->update_volume(signbit(fVolume), fVolume * SDL_MIX_MAXVOLUME);
	return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::GetVolume(float* pfVolume) {
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}
	*pfVolume = pSdlPlayback->get_audio_volume() / (double)SDL_MIX_MAXVOLUME;
	return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::SetBalance(float fBalance) {
	// TODO(fraudies): Not sure how to wire this
	return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::GetBalance(float* pfBalance) {
	// TODO(fraudies): Not sure how to wire this
	return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::SetAudioSyncDelay(long lMillis) {
	// TODO(fraudies): Implement this
	return ERROR_NONE; // no error
}

uint32_t FfmpegSdlAvPlaybackPipeline::GetAudioSyncDelay(long* plMillis) {
	// TODO(fraudies): Implement this
	return ERROR_NONE; // no error
}

uint32_t FfmpegSdlAvPlaybackPipeline::GetImageWidth(int * iWidth) const
{
	return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::GetImageHeight(int * iHeight) const
{
	return ERROR_NONE;
}
