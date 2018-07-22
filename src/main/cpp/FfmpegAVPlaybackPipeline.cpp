#include "FfmpegAVPlaybackPipeline.h"
#include "FfmpegMediaErrors.h"
#include "JavaPlayerEventDispatcher.h"
#include "FfmpegSdlAvPlayback.h"

FfmpegAVPlaybackPipeline::FfmpegAVPlaybackPipeline(CPipelineOptions* pOptions) 
	: CPipeline(pOptions), pSdlPlayback(nullptr) 
{	
}

FfmpegAVPlaybackPipeline::~FfmpegAVPlaybackPipeline() {
	// Clean-up done in dispose that is called from the destructor of the super-class
}

uint32_t FfmpegAVPlaybackPipeline::Init(const char * filename) {
	// TODO: Proper error handling and wiring up of input arguments
	av_log_set_flags(AV_LOG_SKIP_REPEATED);
	av_log(NULL, AV_LOG_WARNING, "Init Network\n");
	AVInputFormat *file_iformat = nullptr;
	pSdlPlayback = new FfmpegSdlAvPlayback(filename, file_iformat);

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

void FfmpegAVPlaybackPipeline::Dispose() {
	pSdlPlayback->destroy();
	delete pSdlPlayback;
	pSdlPlayback = nullptr;
}

uint32_t FfmpegAVPlaybackPipeline::Play() {
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYER_NULL;
	}
	pSdlPlayback->play();

	return ERROR_NONE; // no error
}

uint32_t FfmpegAVPlaybackPipeline::Stop() {
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYER_NULL;
	}
	pSdlPlayback->stop();

	return ERROR_NONE; // no error
}

uint32_t FfmpegAVPlaybackPipeline::Pause() {
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYER_NULL;
	}
	pSdlPlayback->toggle_pause();

	return ERROR_NONE; // no error
}

uint32_t FfmpegAVPlaybackPipeline::Finish() {
	// TODO(fraudies): Stalling and finish need to be set from the video player
	return ERROR_NONE;
}

uint32_t FfmpegAVPlaybackPipeline::Seek(double dSeekTime) {
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

uint32_t FfmpegAVPlaybackPipeline::GetDuration(double* pdDuration) {
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYER_NULL;
	}
	*pdDuration = pSdlPlayback->get_duration();

	return ERROR_NONE; // no error
}

uint32_t FfmpegAVPlaybackPipeline::GetStreamTime(double* pdStreamTime) {
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYER_NULL;
	}

	*pdStreamTime = pSdlPlayback->get_master_clock();

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
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYER_NULL;
	}
	// TODO(fraudies): Implement this once ready
	//pSdlPlayback->update_volume(signbit(fVolume), fVolume * SDL_MIX_MAXVOLUME);
}

uint32_t FfmpegAVPlaybackPipeline::GetVolume(float* pfVolume) {
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYER_NULL;
	}
	// TODO(fraudies): Implement this once ready
	//*pfVolume = pSdlPlayback->get_audio_volume() / (double)SDL_MIX_MAXVOLUME;

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
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYER_NULL;
	}
	// TODO(fraudies): Implement this
	//*bAudioData = pSdlPlayback->has_audio_data();

	return ERROR_NONE;
}

uint32_t FfmpegAVPlaybackPipeline::HasImageData(bool* bImageData) const {
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYER_NULL;
	}
	// TODO(fraudies): Implement this
	//*bImageData = pSdlPlayback->has_image_data();

	return ERROR_NONE;
}

uint32_t FfmpegAVPlaybackPipeline::GetImageWidth(int* width) const {
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYER_NULL;
	}
	// TODO(fraudies): Implement this
	//*width = pSdlPlayback->get_image_width();

	return ERROR_NONE;
}

uint32_t FfmpegAVPlaybackPipeline::GetImageHeight(int* iHeight) const {
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYER_NULL;
	}
	// TODO(fraudies): Implement this
	//*iHeight = pSdlPlayback->get_image_height();

	return ERROR_NONE;
}

uint32_t FfmpegAVPlaybackPipeline::GetAudioFormat(AudioFormat* pAudioFormat) const {
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYER_NULL;
	}
	// TODO(fraudies): Implement this
	//*pAudioFormat = pSdlPlayback->get_audio_format();

	return ERROR_NONE;
}

uint32_t FfmpegAVPlaybackPipeline::GetPixelFormat(PixelFormat* pPixelFormat) const {
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYER_NULL;
	}
	// TODO(fraudies): Implement the pixel format for the ouput
	return ERROR_NONE;
}

uint32_t FfmpegAVPlaybackPipeline::GetImageBuffer(uint8_t** ppImageBuffer) {
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYER_NULL;
	}
	// TODO(fraudies): Implement the image buffer data
	return ERROR_NONE;
}

uint32_t FfmpegAVPlaybackPipeline::GetAudioBuffer(uint8_t** ppAudioBuffer) {
	if (pSdlPlayback == nullptr) {
		return ERROR_PLAYER_NULL;
	}
	// TODO(fraudies): Implement the audio buffer data
	return ERROR_NONE;
}

void FfmpegAVPlaybackPipeline::UpdatePlayerState(PlayerState newState) {
	// Don't need state lock anymore because we update it only from one thread (in the read_thread)
	//stateLock.lock();
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
	//stateLock.unlock();
}


void FfmpegAVPlaybackPipeline::SetPlayerState(PlayerState newPlayerState, bool bSilent) {
	//stateLock.lock();

	// Determine if we need to send an event out
	bool updateState = newPlayerState != m_PlayerState;
	if (updateState)
	{
		if (NULL != m_pEventDispatcher && !bSilent)
		{
			m_PlayerState = newPlayerState;

			if (!m_pEventDispatcher->SendPlayerStateEvent(newPlayerState, 0))
			{
				m_pEventDispatcher->SendPlayerMediaErrorEvent(ERROR_JNI_SEND_PLAYER_STATE_EVENT);
			}
		}
		else
		{
			m_PlayerState = newPlayerState;
		}
	}

	//stateLock.unlock();

	if (updateState && newPlayerState == Stalled) { // Try to play
		Play();
	}
}