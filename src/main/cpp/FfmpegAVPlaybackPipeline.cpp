#include "FfmpegAVPlaybackPipeline.h"

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
	// TODO(fraudies): Check for nullptrs
	pPlayer->get_VideoState()->toggle_pause();
	// TODO(fraudies): Send a state transition event

	return 0; // no error
}

uint32_t FfmpegAVPlaybackPipeline::Stop() {
	return 0; // no error
}

uint32_t FfmpegAVPlaybackPipeline::Pause() {

}

uint32_t FfmpegAVPlaybackPipeline::Finish() {

}

uint32_t FfmpegAVPlaybackPipeline::Seek(double dSeekTime) {

}

uint32_t FfmpegAVPlaybackPipeline::GetDuration(double* pdDuration) {

}

uint32_t FfmpegAVPlaybackPipeline::GetStreamTime(double* pdStreamTime) {

}

uint32_t FfmpegAVPlaybackPipeline::SetRate(float fRate) {

}

uint32_t FfmpegAVPlaybackPipeline::GetRate(float* pfRate) {

}

uint32_t FfmpegAVPlaybackPipeline::SetVolume(float fVolume) {

}

uint32_t FfmpegAVPlaybackPipeline::GetVolume(float* pfVolume) {

}

uint32_t FfmpegAVPlaybackPipeline::SetBalance(float fBalance) {

}

uint32_t FfmpegAVPlaybackPipeline::GetBalance(float* pfBalance) {

}

uint32_t FfmpegAVPlaybackPipeline::SetAudioSyncDelay(long lMillis) {

}

uint32_t FfmpegAVPlaybackPipeline::GetAudioSyncDelay(long* plMillis) {

}


// TODO(fraudies): Wire up state transitions and decide whether to implement a halt state event
/*
void CGstAudioPlaybackPipeline::UpdatePlayerState(GstState newState, GstState oldState)
{
	m_StateLock->Enter();

	PlayerState newPlayerState = m_PlayerState;
	bool        bSilent = false;

	switch (m_PlayerState)
	{
	case Unknown:
		if ((GST_STATE_READY == oldState && GST_STATE_PAUSED == newState) || (GST_STATE_PAUSED == oldState && GST_STATE_PAUSED == newState))
		{
			newPlayerState = Ready;
		}
		break;

	case Ready:
		if (GST_STATE_PAUSED == oldState)
		{
			if (GST_STATE_READY == newState)
				newPlayerState = Unknown;
			else if (GST_STATE_PLAYING == newState)
				newPlayerState = Playing;
		}
		break;

	case Playing:
		if (GST_STATE_PLAYING == oldState)
		{
			if (GST_STATE_PAUSED == newState)
			{
				if (m_PlayerPendingState == Stopped)
				{
					m_StallOnPause = false;
					m_PlayerPendingState = Unknown;
					newPlayerState = Stopped;
				}
				else if (m_StallOnPause && m_PlayerPendingState != Paused)
				{
					m_StallOnPause = false;
					newPlayerState = Stalled;
				}
				else if (m_PlayerPendingState == Paused)
				{
					m_StallOnPause = false;
					m_PlayerPendingState = Unknown;
					newPlayerState = Paused;
				}
				else
				{
					newPlayerState = Finished;
				}
			}
		}
		else if (GST_STATE_PAUSED == oldState) // May happen during seek
		{
			if (GST_STATE_PAUSED == newState)
			{
				if (m_PlayerPendingState == Stopped)
				{
					m_StallOnPause = false;
					m_PlayerPendingState = Unknown;
					newPlayerState = Stopped;
				}
				else if (m_StallOnPause && m_PlayerPendingState != Paused)
				{
					m_StallOnPause = false;
					newPlayerState = Stalled;
				}
				else if (m_PlayerPendingState == Paused)
				{
					m_StallOnPause = false;
					m_PlayerPendingState = Unknown;
					newPlayerState = Paused;
				}
			}
		}
		break;

	case Paused:
		if (GST_STATE_PAUSED == oldState)
		{
			if (m_PlayerPendingState == Stopped)
			{
				m_PlayerPendingState = Unknown;
				newPlayerState = Stopped;
			}
			else
			{
				if (GST_STATE_PLAYING == newState)
					newPlayerState = Playing;
				else if (GST_STATE_READY == newState)
					newPlayerState = Unknown;
			}
		}
		break;

	case Stopped:
		if (GST_STATE_PAUSED == oldState)
		{
			if (m_PlayerPendingState == Paused && GST_STATE_PAUSED == newState)
			{
				m_PlayerPendingState = Unknown;
				newPlayerState = Paused;
			}
			else if (GST_STATE_PLAYING == newState)
			{
				newPlayerState = Playing;
			}
			else if (GST_STATE_READY == newState)
			{
				newPlayerState = Unknown;
			}
		}
		break;

	case Stalled:
	{
		if (GST_STATE_PAUSED == oldState && GST_STATE_PLAYING == newState)
			newPlayerState = Playing;
		else if (GST_STATE_PAUSED == oldState && GST_STATE_PAUSED == newState)
		{
			if (m_PlayerPendingState == Stopped)
			{
				m_PlayerPendingState = Unknown;
				newPlayerState = Stopped;
			}
			else if (m_PlayerPendingState == Paused)
			{
				m_PlayerPendingState = Unknown;
				newPlayerState = Paused;
			}
		}
		break;
	}

	case Finished:
		if (GST_STATE_PLAYING == oldState)
		{
			if (GST_STATE_PAUSED == newState)
			{
				if (m_PlayerPendingState == Stopped)
				{
					m_PlayerPendingState = Unknown;
					m_bSeekInvoked = false;
					newPlayerState = Stopped;
				}
				// No need to switch to paused state, since Pause is not valid in Finished state
			}
		}
		else if (GST_STATE_PAUSED == oldState)
		{
			if (GST_STATE_PLAYING == newState)
			{
				// We can go from Finished to Playing only when seek happens (or repeat)
				// This state change should be silent.
				newPlayerState = Playing;
				m_bSeekInvoked = false;
				bSilent = true;
			}
			else if (GST_STATE_PAUSED == newState)
			{
				if (m_PlayerPendingState == Stopped)
				{
					m_PlayerPendingState = Unknown;
					m_bSeekInvoked = false;
					newPlayerState = Stopped;
				}
				else
				{
					m_bSeekInvoked = false;
					newPlayerState = Paused;
				}
			}
		}
		break;

	case Error:
		break;
	}

	SetPlayerState(newPlayerState, bSilent);
	m_StateLock->Exit();
}
*/