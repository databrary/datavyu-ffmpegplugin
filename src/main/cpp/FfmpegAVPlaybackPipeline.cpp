#include "FfmpegAVPlaybackPipeline.h"
#include "FfmpegMediaErrors.h"
#include "JavaPlayerEventDispatcher.h"

FfmpegAVPlaybackPipeline::FfmpegAVPlaybackPipeline(CPipelineOptions* pOptions) 
	: CPipeline(pOptions), pPlayer(nullptr) 
{	
}

FfmpegAVPlaybackPipeline::~FfmpegAVPlaybackPipeline() {
	// Clean-up done in dispose that is called from the destructor of the super-class
}

uint32_t FfmpegAVPlaybackPipeline::Init() {
	// TODO: Proper error handling and wiring up of input arguments
	av_log_set_flags(AV_LOG_SKIP_REPEATED);
	av_log(NULL, AV_LOG_WARNING, "Init Network\n");
	static const char* input_filename = "counter.mp4";
	AVInputFormat *file_iformat = nullptr;
	pPlayer = new SDLPlayData(input_filename, file_iformat);
	VideoState* pVideoState = pPlayer->get_VideoState();
	if (!pVideoState) {
		av_log(NULL, AV_LOG_FATAL, "Failed to initialize VideoState!\n");
		pPlayer->destroy();
		delete pPlayer;
	}
	pPlayer->init();

	// Assign the callback functions
	pVideoState->set_player_state_callback_func(TO_UNKNOWN, [this] {
		this->UpdatePlayerState(Unknown);
	});
	pVideoState->set_player_state_callback_func(TO_READY, [this] {
		this->UpdatePlayerState(Ready);
	});
	pVideoState->set_player_state_callback_func(TO_PLAYING, [this] {
		this->UpdatePlayerState(Playing);
	});
	pVideoState->set_player_state_callback_func(TO_PAUSED, [this] {
		this->UpdatePlayerState(Paused);
	});
	pVideoState->set_player_state_callback_func(TO_STOPPED, [this] {
		this->UpdatePlayerState(Stopped);
	});
	pVideoState->set_player_state_callback_func(TO_STALLED, [this] {
		this->UpdatePlayerState(Stalled);
	});
	pVideoState->set_player_state_callback_func(TO_FINISHED, [this] {
		this->UpdatePlayerState(Finished);
	});

	pPlayer->start_display_loop();
	UpdatePlayerState(Ready);

	return ERROR_NONE;
}

void FfmpegAVPlaybackPipeline::Dispose() {
	pPlayer->stop_display_loop();
	pPlayer->destroy();
	delete pPlayer;
	pPlayer = nullptr;
}

uint32_t FfmpegAVPlaybackPipeline::Play() {
	if (pPlayer == nullptr) {
		return ERROR_PLAYER_NULL;
	}

	VideoState* pVideoState = pPlayer->get_VideoState();
	if (pVideoState == nullptr) {
		return ERROR_VIDEO_STATE_NULL;
	}
	pVideoState->play();
	UpdatePlayerState(Playing);

	return ERROR_NONE; // no error
}

uint32_t FfmpegAVPlaybackPipeline::Stop() {
	if (pPlayer == nullptr) {
		return ERROR_PLAYER_NULL;
	}

	VideoState* pVideoState = pPlayer->get_VideoState();
	if (pVideoState == nullptr) {
		return ERROR_VIDEO_STATE_NULL;
	}
	pVideoState->stop();
	UpdatePlayerState(Stopped);

	return ERROR_NONE; // no error
}

uint32_t FfmpegAVPlaybackPipeline::Pause() {
	if (pPlayer == nullptr) {
		return ERROR_PLAYER_NULL;
	}

	VideoState* pVideoState = pPlayer->get_VideoState();
	if (pVideoState == nullptr) {
		return ERROR_VIDEO_STATE_NULL;
	}
	pVideoState->pause();
	UpdatePlayerState(Paused);

	return ERROR_NONE; // no error
}

uint32_t FfmpegAVPlaybackPipeline::Finish() {
	// TODO(fraudies): Stalling and finish need to be set from the video player
	return ERROR_NONE;
}

uint32_t FfmpegAVPlaybackPipeline::Seek(double dSeekTime) {
	if (pPlayer == nullptr) {
		return ERROR_PLAYER_NULL;
	}

	VideoState* pVideoState = pPlayer->get_VideoState();
	if (pVideoState == nullptr) {
		return ERROR_VIDEO_STATE_NULL;
	}
	double pos = pVideoState->get_master_clock();
	if (isnan(pos))
		pos = (double)pVideoState->get_seek_pos() / AV_TIME_BASE;
	double incr = dSeekTime - pos;
	if (pVideoState->get_ic()->start_time != AV_NOPTS_VALUE && dSeekTime < pVideoState->get_ic()->start_time / (double)AV_TIME_BASE)
		dSeekTime = pVideoState->get_ic()->start_time / (double)AV_TIME_BASE;

	// TODO(fraudies): Need to report back from ffmpeg once done with seeking and we can switch back to playing etc.
	UpdatePlayerState(Stalled);

	pVideoState->stream_seek((int64_t)(dSeekTime * AV_TIME_BASE), (int64_t)(incr * AV_TIME_BASE), 0);


	return ERROR_NONE; // no error
}

uint32_t FfmpegAVPlaybackPipeline::GetDuration(double* pdDuration) {
	if (pPlayer == nullptr) {
		return ERROR_PLAYER_NULL;
	}

	VideoState* pVideoState = pPlayer->get_VideoState();
	if (pVideoState == nullptr) {
		return ERROR_VIDEO_STATE_NULL;
	}

	*pdDuration = pVideoState->get_duration();

	return ERROR_NONE; // no error
}

uint32_t FfmpegAVPlaybackPipeline::GetStreamTime(double* pdStreamTime) {
	if (pPlayer == nullptr) {
		return ERROR_PLAYER_NULL;
	}

	VideoState* pVideoState = pPlayer->get_VideoState();
	if (pVideoState == nullptr) {
		return ERROR_VIDEO_STATE_NULL;
	}

	*pdStreamTime = pVideoState->get_master_clock();

	return ERROR_NONE; // no error
}

uint32_t FfmpegAVPlaybackPipeline::SetRate(float fRate) {
	// TODO(fraudies): Implement this once ready
	// At the moment we don't have a way of setting this
	return ERROR_NONE;
}

uint32_t FfmpegAVPlaybackPipeline::GetRate(float* pfRate) {
	// TODO(fraudies): Implement this once ready
	// At the moment we don't have a way of setting this
	return ERROR_NONE;
}

uint32_t FfmpegAVPlaybackPipeline::SetVolume(float fVolume) {
	if (pPlayer == nullptr) {
		return ERROR_PLAYER_NULL;
	}

	VideoState* pVideoState = pPlayer->get_VideoState();
	if (pVideoState == nullptr) {
		return ERROR_VIDEO_STATE_NULL;
	}

	pVideoState->update_volume(signbit(fVolume), fVolume * SDL_MIX_MAXVOLUME);
}

uint32_t FfmpegAVPlaybackPipeline::GetVolume(float* pfVolume) {
	if (pPlayer == nullptr) {
		return ERROR_PLAYER_NULL;
	}

	VideoState* pVideoState = pPlayer->get_VideoState();
	if (pVideoState == nullptr) {
		return ERROR_VIDEO_STATE_NULL;
	}

	*pfVolume = pVideoState->get_audio_volume() / (double)SDL_MIX_MAXVOLUME;

	return ERROR_NONE; // no error
}

uint32_t FfmpegAVPlaybackPipeline::SetBalance(float fBalance) {
	// TODO(fraudies): Not sure how to wire this
	return ERROR_NONE;
}

uint32_t FfmpegAVPlaybackPipeline::GetBalance(float* pfBalance) {
	// TODO(fraudies): Not sure how to wire this
	return ERROR_NONE;
}

uint32_t FfmpegAVPlaybackPipeline::SetAudioSyncDelay(long lMillis) {
	// TODO(fraudies): Implement this
	return ERROR_NONE; // no error
}

uint32_t FfmpegAVPlaybackPipeline::GetAudioSyncDelay(long* plMillis) {
	// TODO(fraudies): Implement this
	return ERROR_NONE; // no error
}

uint32_t FfmpegAVPlaybackPipeline::HasAudioData(bool* bAudioData) const {
	if (pPlayer == nullptr) {
		return ERROR_PLAYER_NULL;
	}

	VideoState* pVideoState = pPlayer->get_VideoState();
	if (pVideoState == nullptr) {
		return ERROR_VIDEO_STATE_NULL;
	}

	*bAudioData = pVideoState->has_audio_data();

	return ERROR_NONE;
}

uint32_t FfmpegAVPlaybackPipeline::HasImageData(bool* bImageData) const {
	if (pPlayer == nullptr) {
		return ERROR_PLAYER_NULL;
	}

	VideoState* pVideoState = pPlayer->get_VideoState();
	if (pVideoState == nullptr) {
		return ERROR_VIDEO_STATE_NULL;
	}

	*bImageData = pVideoState->has_image_data();

	return ERROR_NONE;
}

uint32_t FfmpegAVPlaybackPipeline::GetImageWidth(int* width) const {
	if (pPlayer == nullptr) {
		return ERROR_PLAYER_NULL;
	}

	VideoState* pVideoState = pPlayer->get_VideoState();
	if (pVideoState == nullptr) {
		return ERROR_VIDEO_STATE_NULL;
	}

	*width = pVideoState->get_image_width();

	return ERROR_NONE;
}

uint32_t FfmpegAVPlaybackPipeline::GetImageHeight(int* iHeight) const {
	if (pPlayer == nullptr) {
		return ERROR_PLAYER_NULL;
	}

	VideoState* pVideoState = pPlayer->get_VideoState();
	if (pVideoState == nullptr) {
		return ERROR_VIDEO_STATE_NULL;
	}

	*iHeight = pVideoState->get_image_height();

	return ERROR_NONE;
}

uint32_t FfmpegAVPlaybackPipeline::GetAudioFormat(AudioFormat* pAudioFormat) const {
	if (pPlayer == nullptr) {
		return ERROR_PLAYER_NULL;
	}

	VideoState* pVideoState = pPlayer->get_VideoState();
	if (pVideoState == nullptr) {
		return ERROR_VIDEO_STATE_NULL;
	}

	*pAudioFormat = pVideoState->get_audio_format();

	return ERROR_NONE;
}

uint32_t FfmpegAVPlaybackPipeline::GetPixelFormat(PixelFormat* pPixelFormat) const {
	if (pPlayer == nullptr) {
		return ERROR_PLAYER_NULL;
	}

	VideoState* pVideoState = pPlayer->get_VideoState();
	if (pVideoState == nullptr) {
		return ERROR_VIDEO_STATE_NULL;
	}
	// TODO(fraudies): Implement the pixel format for the ouput
	return ERROR_NONE;
}

uint32_t FfmpegAVPlaybackPipeline::GetImageBuffer(uint8_t** ppImageBuffer) {
	if (pPlayer == nullptr) {
		return ERROR_PLAYER_NULL;
	}

	VideoState* pVideoState = pPlayer->get_VideoState();
	if (pVideoState == nullptr) {
		return ERROR_VIDEO_STATE_NULL;
	}
	// TODO(fraudies): Implement the image buffer data
	return ERROR_NONE;
}

uint32_t FfmpegAVPlaybackPipeline::GetAudioBuffer(uint8_t** ppAudioBuffer) {
	if (pPlayer == nullptr) {
		return ERROR_PLAYER_NULL;
	}

	VideoState* pVideoState = pPlayer->get_VideoState();
	if (pVideoState == nullptr) {
		return ERROR_VIDEO_STATE_NULL;
	}
	// TODO(fraudies): Implement the audio buffer data
	return ERROR_NONE;
}

void FfmpegAVPlaybackPipeline::UpdatePlayerState(PlayerState newState) {
	stateLock.lock();
	PlayerState newPlayerState = m_PlayerState;	// If we assign the same state again
	bool bSilent = false;

	switch (m_PlayerState)
	{
	case Unknown:
		if (Ready == newState)
		{
			newPlayerState = Ready;
		}
		break;

	case Ready:
		if (Playing == newState)
		{
			newPlayerState = Playing;
		}
		break;

	case Playing:
		if (Stalled == newState || Paused == newState || Stopped == newState || Finished == newState) {
			newPlayerState = newState;
		}
		break;

	case Paused:
		if (Stopped == newState || Playing == newState)
		{
			newPlayerState = newState;
		}
		break;

	case Stopped:
		if (Playing == newState || Paused == newState)
		{
			newPlayerState = newState;
		}
		break;

	case Stalled:
	{
		if (Stopped == newState || Paused == newState || Playing == newState) {
			newPlayerState = newState;
		}
		break;
	}

	case Finished:
		if (Playing == newState) {
			// We can go from Finished to Playing only when seek happens (or repeat)
			// This state change should be silent.
			newPlayerState = Playing;
			bSilent = true;
		}
		if (Stopped == newState) {
			newPlayerState = Stopped;
		}

		break;

	case Error:
		break;
	}

	// The same thread can acquire the same lock several times
	SetPlayerState(newPlayerState, bSilent);
	stateLock.unlock();
}


void FfmpegAVPlaybackPipeline::SetPlayerState(PlayerState newPlayerState, bool bSilent) {
	stateLock.lock();

	// Determine if we need to send an event out
	bool updateState = newPlayerState != m_PlayerState;
	if (updateState)
	{
		if (NULL != m_pEventDispatcher && !bSilent)
		{
			m_PlayerState = newPlayerState;

			if (!m_pEventDispatcher->SendPlayerStateEvent(newPlayerState, pPlayer->get_VideoState()->get_master_clock()))
			{
				m_pEventDispatcher->SendPlayerMediaErrorEvent(ERROR_JNI_SEND_PLAYER_STATE_EVENT);
			}
		}
		else
		{
			m_PlayerState = newPlayerState;
		}
	}

	stateLock.unlock();

	if (updateState && newPlayerState == Stalled) { // Try to play
		Play();
	}
}