#include "FfmpegJavaAvPlaybackPipline.h"

uint32_t FfmpegJavaAvPlaybackPipline::Init(const char * input_file) {

	av_log_set_flags(AV_LOG_SKIP_REPEATED);
	av_log(NULL, AV_LOG_WARNING, "Init Network\n");
	AVInputFormat *file_iformat = nullptr;

	pJavaPlayback = new (std::nothrow) FfmpegJavaAvPlayback(
		m_pOptions->GetAudioFormat(), 
		m_pOptions->GetPixelFormat(), 
		m_pOptions->GetAudioBufferSizeInBy());

	if (!pJavaPlayback) {
		av_log(NULL, AV_LOG_ERROR, "Unable to initialize the java playback pipeline");
		return ERROR_PIPELINE_NULL;
	}

	int err = pJavaPlayback->Init(input_file, file_iformat);
	if (err) {
		delete pJavaPlayback;
		return err;
	}

	// Assign the callback functions
	pJavaPlayback->set_player_state_callback_func(TO_UNKNOWN, [this] {
		this->UpdatePlayerState(Unknown);
	});
	pJavaPlayback->set_player_state_callback_func(TO_READY, [this] {
		this->UpdatePlayerState(Ready);
	});
	pJavaPlayback->set_player_state_callback_func(TO_PLAYING, [this] {
		this->UpdatePlayerState(Playing);
	});
	pJavaPlayback->set_player_state_callback_func(TO_PAUSED, [this] {
		this->UpdatePlayerState(Paused);
	});
	pJavaPlayback->set_player_state_callback_func(TO_STOPPED, [this] {
		this->UpdatePlayerState(Stopped);
	});
	pJavaPlayback->set_player_state_callback_func(TO_STALLED, [this] {
		this->UpdatePlayerState(Stalled);
	});
	pJavaPlayback->set_player_state_callback_func(TO_FINISHED, [this] {
		this->UpdatePlayerState(Finished);
	});

	return pJavaPlayback->start_stream();
}

void FfmpegJavaAvPlaybackPipline::Dispose() {
	pJavaPlayback->destroy();
	delete pJavaPlayback;
	pJavaPlayback = nullptr;
}

FfmpegJavaAvPlaybackPipline::FfmpegJavaAvPlaybackPipline(CPipelineOptions * pOptions) 
	: CPipelineData(pOptions), pJavaPlayback(nullptr) 
{ }

FfmpegJavaAvPlaybackPipline::~FfmpegJavaAvPlaybackPipline() {
	// Clean-up done in dispose that is called from the destructor of the super-class
}

uint32_t FfmpegJavaAvPlaybackPipline::Play() {
	if (pJavaPlayback == nullptr)
		return ERROR_PLAYBACK_NULL;

	pJavaPlayback->play();

	return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::Stop() {
	if (pJavaPlayback == nullptr)
		return ERROR_PLAYBACK_NULL;

	pJavaPlayback->stop();

	return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::Pause() {
	if (pJavaPlayback == nullptr)
		return ERROR_PLAYBACK_NULL;

	pJavaPlayback->toggle_pause();

	return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::StepForward() {
	if (pJavaPlayback == nullptr)
		return ERROR_PLAYBACK_NULL;

	pJavaPlayback->step_to_next_frame();

	return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::Finish() {
	// TODO(fraudies): Stalling and finish need to be set from the video player
	return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::Seek(double dSeekTime) {
	if (pJavaPlayback == nullptr)
		return ERROR_PLAYBACK_NULL;

	double pos = pJavaPlayback->get_stream_time();

	if (isnan(pos))
		pos = (double)pJavaPlayback->get_seek_pos() / AV_TIME_BASE;

	double incr = dSeekTime - pos;

	if (pJavaPlayback->get_start_time() != AV_NOPTS_VALUE && dSeekTime < pJavaPlayback->get_start_time() / (double)AV_TIME_BASE)
		dSeekTime = pJavaPlayback->get_start_time() / (double)AV_TIME_BASE;

	pJavaPlayback->stream_seek((int64_t)(dSeekTime * AV_TIME_BASE), (int64_t)(incr * AV_TIME_BASE), 0);

	return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::GetDuration(double* pdDuration) {
	if (pJavaPlayback == nullptr)
		return ERROR_PLAYBACK_NULL;

	*pdDuration = pJavaPlayback->get_duration();

	return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::GetStreamTime(double* pdStreamTime) {
	if (pJavaPlayback == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}

	// The master clock (Audio Clock by default) could return NaN and affect 
	// performance while seeking. However returning the external clock should 
	// resolve this issue (Note that the timestamp return by the external is not as
	// accurate as the audio clock  (Master))

	*pdStreamTime = pJavaPlayback->get_stream_time();

	return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::GetFps(double* pdFps) {
	if (pJavaPlayback == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}

	*pdFps = pJavaPlayback->get_fps();

	return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::SetRate(float fRate) {
	if (pJavaPlayback == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}

	pJavaPlayback->set_rate(fRate);

	return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::GetRate(float* pfRate) {
	if (pJavaPlayback == nullptr) {
		return ERROR_PLAYBACK_NULL;
	}

	*pfRate = (float) pJavaPlayback->get_rate();

	return ERROR_NONE;
}

// Note this function is available only when streaming through SDL pipline
uint32_t FfmpegJavaAvPlaybackPipline::SetVolume(float fVolume) {
	if (pJavaPlayback == nullptr)
		return ERROR_PLAYBACK_NULL;

	// TODO(fraudies): Implement this once ready
	//pSdlPlayback->update_volume(signbit(fVolume), fVolume * SDL_MIX_MAXVOLUME);
}

// Note this function is available only when streaming through SDL pipline
uint32_t FfmpegJavaAvPlaybackPipline::GetVolume(float* pfVolume) {
	if (pJavaPlayback == nullptr)
		return ERROR_PLAYBACK_NULL;

	// TODO(fraudies): Implement this once ready
	//*pfVolume = pSdlPlayback->get_audio_volume() / (double)SDL_MIX_MAXVOLUME;

	return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::SetBalance(float fBalance) {
	// TODO(fraudies): Not sure how to wire this
	if (pJavaPlayback == nullptr)
		return ERROR_PLAYBACK_NULL;

	pJavaPlayback->set_balance(fBalance);

	return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::GetBalance(float* pfBalance) {
	// TODO(fraudies): Not sure how to wire this
	if (pJavaPlayback == nullptr)
		return ERROR_PLAYBACK_NULL;

	*pfBalance = pJavaPlayback->get_balance();

	return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::SetAudioSyncDelay(long lMillis) {
	if (pJavaPlayback == nullptr)
		return ERROR_PLAYBACK_NULL;

	pJavaPlayback->set_audioSyncDelay(lMillis);

	return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::GetAudioSyncDelay(long* plMillis) {
	if (pJavaPlayback == nullptr)
		return ERROR_PLAYBACK_NULL;

	*plMillis = pJavaPlayback->get_audioSyncDelay();

	return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::HasAudioData(bool* bAudioData) const {
	if (pJavaPlayback == nullptr)
		return ERROR_PLAYBACK_NULL;

	*bAudioData = pJavaPlayback->has_audio_data();

	return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::HasImageData(bool* bImageData) const {
	if (pJavaPlayback == nullptr)
		return ERROR_PLAYBACK_NULL;

	*bImageData = pJavaPlayback->has_image_data();

	return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::GetImageWidth(int* iWidth) const {
	if (pJavaPlayback == nullptr)
		return ERROR_PLAYBACK_NULL;

	*iWidth = pJavaPlayback->get_image_width();

	return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::GetImageHeight(int* iHeight) const {
	if (pJavaPlayback == nullptr)
		return ERROR_PLAYBACK_NULL;

	*iHeight = pJavaPlayback->get_image_height();

	return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::GetAudioFormat(AudioFormat* pAudioFormat) const {
	if (pJavaPlayback == nullptr)
		return ERROR_PLAYBACK_NULL;

	pJavaPlayback->get_audio_format(pAudioFormat);

	return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::GetPixelFormat(PixelFormat* pPixelFormat) const {
	if (pJavaPlayback == nullptr)
		return ERROR_PLAYBACK_NULL;

	pJavaPlayback->get_pixel_format(pPixelFormat);

	return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::UpdateImageBuffer(uint8_t* pImageData, const long len) {
	if (pJavaPlayback == nullptr)
		return ERROR_PLAYBACK_NULL;

	pJavaPlayback->update_image_buffer(pImageData, len);

	return ERROR_NONE;
}

uint32_t FfmpegJavaAvPlaybackPipline::UpdateAudioBuffer(uint8_t* pAudioData, const long len) {
	if (pJavaPlayback == nullptr)
		return ERROR_PLAYBACK_NULL;

	pJavaPlayback->update_audio_buffer(pAudioData, len);

	return ERROR_NONE;
}



