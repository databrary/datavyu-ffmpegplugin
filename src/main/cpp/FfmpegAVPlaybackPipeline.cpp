#include "FfmpegAVPlaybackPipeline.h"
#include "FfmpegMediaErrors.h"

FfmpegAVPlaybackPipeline::FfmpegAVPlaybackPipeline(CPipelineOptions* pOptions) 
	: CPipeline(pOptions), pPlayer(nullptr) 
{	
}

FfmpegAVPlaybackPipeline::~FfmpegAVPlaybackPipeline() {
	// Clean-up done in dispose that is called from the destructor of the super-class
}


uint32_t FfmpegAVPlaybackPipeline::Init() {
	// TODO: Proper error handling and wiring up of input arguments

	int flags;

	av_log_set_flags(AV_LOG_SKIP_REPEATED);

	/* register all codecs, demux and protocols */
#if CONFIG_AVDEVICE
	avdevice_register_all();
#endif
	avformat_network_init();
	av_log(NULL, AV_LOG_WARNING, "Init Network\n");

	input_filename = "counter.mp4";

	flags = SDL_INIT_VIDEO | SDL_INIT_AUDIO | SDL_INIT_TIMER;

	// Assumes both video and audio are ENABLED!

	/* Try to work around an occasional ALSA buffer underflow issue when the
	* period size is NPOT due to ALSA resampling by forcing the buffer size. */
	if (!SDL_getenv("SDL_AUDIO_ALSA_SET_BUFFER_SIZE"))
		SDL_setenv("SDL_AUDIO_ALSA_SET_BUFFER_SIZE", "1", 1);

	if (SDL_Init(flags)) {
		av_log(NULL, AV_LOG_FATAL, "Could not initialize SDL - %s\n", SDL_GetError());
		av_log(NULL, AV_LOG_FATAL, "(Did you set the DISPLAY variable?)\n");
		exit(1);
	}

	SDL_EventState(SDL_SYSWMEVENT, SDL_IGNORE);
	SDL_EventState(SDL_USEREVENT, SDL_IGNORE);

	int flags = SDL_WINDOW_HIDDEN;
	if (borderless)
		flags |= SDL_WINDOW_BORDERLESS;
	else
		flags |= SDL_WINDOW_RESIZABLE;
	window = SDL_CreateWindow(program_name, SDL_WINDOWPOS_UNDEFINED, SDL_WINDOWPOS_UNDEFINED, default_width, default_height, flags);
	SDL_SetHint(SDL_HINT_RENDER_SCALE_QUALITY, "linear");
	if (window) {
		renderer = SDL_CreateRenderer(window, -1, SDL_RENDERER_ACCELERATED | SDL_RENDERER_PRESENTVSYNC);
		if (!renderer) {
			av_log(NULL, AV_LOG_WARNING, "Failed to initialize a hardware accelerated renderer: %s\n", SDL_GetError());
			renderer = SDL_CreateRenderer(window, -1, 0);
		}
		if (renderer) {
			if (!SDL_GetRendererInfo(renderer, &renderer_info))
				av_log(NULL, AV_LOG_VERBOSE, "Initialized %s renderer.\n", renderer_info.name);
		}
	}
	if (!window || !renderer || !renderer_info.num_texture_formats) {
		av_log(NULL, AV_LOG_FATAL, "Failed to create window or renderer: %s", SDL_GetError());
		SDLPlayData::do_exit(nullptr);
	}

	pPlayer = new SDLPlayData(input_filename, file_iformat);
	if (!pPlayer->get_VideoState()) {
		av_log(NULL, AV_LOG_FATAL, "Failed to initialize VideoState!\n");
		SDLPlayData::do_exit(pPlayer->get_VideoState());
	}

	pPlayer->start_display_loop();
}

void FfmpegAVPlaybackPipeline::Dispose() {
	pPlayer->stop_display_loop();
	SDLPlayData::do_exit(pPlayer->get_VideoState());
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
}

uint32_t FfmpegAVPlaybackPipeline::GetRate(float* pfRate) {
	// TODO(fraudies): Implement this once ready
	// At the moment we don't have a way of setting this
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
}

uint32_t FfmpegAVPlaybackPipeline::GetBalance(float* pfBalance) {
	// TODO(fraudies): Not sure how to wire this
}

uint32_t FfmpegAVPlaybackPipeline::SetAudioSyncDelay(long lMillis) {

}

uint32_t FfmpegAVPlaybackPipeline::GetAudioSyncDelay(long* plMillis) {

}


void FfmpegAVPlaybackPipeline::UpdatePlayerState(PlayerState newState) {
	stateLock.lock();
	PlayerState newPlayerState = m_PlayerState;	// If we had have the same state assign it again
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

			if (!m_pEventDispatcher->SendPlayerStateEvent(newPlayerState, pVideoState->get_master_clock->get_clock()))
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