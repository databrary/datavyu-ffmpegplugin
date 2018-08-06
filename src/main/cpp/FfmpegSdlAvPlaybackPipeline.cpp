#include "FfmpegSdlAvPlaybackPipeline.h"
#include "FfmpegMediaErrors.h"

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
	pSdlPlayback = new FfmpegSdlAvPlayback(input_file, file_iformat);

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
	
	pSdlPlayback->init_and_start_display_loop();

	return ERROR_NONE;
}

void FfmpegSdlAvPlaybackPipeline::Dispose() {
	pSdlPlayback->destroy();
	delete pSdlPlayback;
	pSdlPlayback = nullptr;
}

uint32_t FfmpegSdlAvPlaybackPipeline::Play() {
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYER_NULL;
	}
	pSdlPlayback->play();

	return ERROR_NONE; // no error
}

uint32_t FfmpegSdlAvPlaybackPipeline::Stop() {
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYER_NULL;
	}
	pSdlPlayback->stop();

	return ERROR_NONE; // no error
}

uint32_t FfmpegSdlAvPlaybackPipeline::Pause() {
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYER_NULL;
	}
	pSdlPlayback->toggle_pause();

	return ERROR_NONE; // no error
}

uint32_t FfmpegSdlAvPlaybackPipeline::Finish() {
	// TODO(fraudies): Stalling and finish need to be set from the video player
	return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::Seek(double dSeekTime) {
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYER_NULL;
	}
	double pos = pSdlPlayback->get_master_clock();
	if (isnan(pos))
		pos = (double)pSdlPlayback->get_seek_pos() / AV_TIME_BASE;
	double incr = dSeekTime - pos;
	if (pSdlPlayback->get_start_time() != AV_NOPTS_VALUE && dSeekTime < pSdlPlayback->get_start_time() / (double)AV_TIME_BASE)
		dSeekTime = pSdlPlayback->get_start_time() / (double)AV_TIME_BASE;

	pSdlPlayback->stream_seek((int64_t)(dSeekTime * AV_TIME_BASE), (int64_t)(incr * AV_TIME_BASE), 0);

	return ERROR_NONE; // no error
}

uint32_t FfmpegSdlAvPlaybackPipeline::GetDuration(double* pdDuration) {
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYER_NULL;
	}
	*pdDuration = pSdlPlayback->get_duration();

	return ERROR_NONE; // no error
}

uint32_t FfmpegSdlAvPlaybackPipeline::GetStreamTime(double* pdStreamTime) {
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYER_NULL;
	}

	*pdStreamTime = pSdlPlayback->get_master_clock();

	return ERROR_NONE; // no error
}

uint32_t FfmpegSdlAvPlaybackPipeline::SetRate(float fRate) {
	// TODO(fraudies): Implement this once ready
	// At the moment we don't have a way of setting this
	return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::GetRate(float* pfRate) {
	// TODO(fraudies): Implement this once ready
	// At the moment we don't have a way of setting this
	return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::SetVolume(float fVolume) {
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYER_NULL;
	}
	// TODO(fraudies): Implement this once ready
	//pSdlPlayback->update_volume(signbit(fVolume), fVolume * SDL_MIX_MAXVOLUME);
}

uint32_t FfmpegSdlAvPlaybackPipeline::GetVolume(float* pfVolume) {
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYER_NULL;
	}
	// TODO(fraudies): Implement this once ready
	//*pfVolume = pSdlPlayback->get_audio_volume() / (double)SDL_MIX_MAXVOLUME;

	return ERROR_NONE; // no error
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

uint32_t FfmpegSdlAvPlaybackPipeline::HasAudioData(bool* bAudioData) const {
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYER_NULL;
	}
	// TODO(fraudies): Implement this
	//*bAudioData = pSdlPlayback->has_audio_data();

	return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::HasImageData(bool* bImageData) const {
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYER_NULL;
	}
	// TODO(fraudies): Implement this
	//*bImageData = pSdlPlayback->has_image_data();

	return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::GetImageWidth(int* width) const {
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYER_NULL;
	}
	// TODO(fraudies): Implement this
	//*width = pSdlPlayback->get_image_width();

	return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::GetImageHeight(int* iHeight) const {
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYER_NULL;
	}
	// TODO(fraudies): Implement this
	//*iHeight = pSdlPlayback->get_image_height();

	return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::GetAudioFormat(AudioFormat* pAudioFormat) const {
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYER_NULL;
	}
	// TODO(fraudies): Implement this
	//*pAudioFormat = pSdlPlayback->get_audio_format();

	return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::GetPixelFormat(PixelFormat* pPixelFormat) const {
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYER_NULL;
	}
	// TODO(fraudies): Implement the pixel format for the ouput
	return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::GetImageBuffer(uint8_t** ppImageBuffer) {
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYER_NULL;
	}
	// TODO(fraudies): Implement the image buffer data
	return ERROR_NONE;
}

uint32_t FfmpegSdlAvPlaybackPipeline::GetAudioBuffer(uint8_t** ppAudioBuffer, const int len) {
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYER_NULL;
	}
	// TODO(fraudies): Implement the audio buffer data
	return ERROR_NONE;
}