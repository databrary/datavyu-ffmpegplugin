#include "FfmpegAvPlayback.h"


void FfmpegAvPlayback::stream_toggle_pause() {
	
	// Get all the clocks
	Clock* pExtclk = pVideoState->get_pExtclk();
	Clock* pVidclk = pVideoState->get_pVidclk();
	Clock* pAudclk = pVideoState->get_pAudclk();

	// Update the video clock
	if (pVideoState->get_paused()) {
		set_frame_timer(get_frame_timer() + av_gettime_relative() / 1000000.0 - pVidclk->get_lastUpdated());
		if (pVideoState->get_read_pause_return() != AVERROR(ENOSYS)) {
			pVidclk->setPaused(0);
		}
		pVidclk->set_clock(pVidclk->get_clock(), pVidclk->get_serial());
	}
	// Update the external clock
	pExtclk->set_clock(pExtclk->get_clock(), pExtclk->get_serial());

	// Flip the paused flag on the clocks
	bool flipped = !pVideoState->get_paused();
	pVideoState->set_paused(flipped);
	pAudclk->setPaused(flipped);
	pVidclk->setPaused(flipped);
	pExtclk->setPaused(flipped);
}

FfmpegAvPlayback::FfmpegAvPlayback(const char *filename, AVInputFormat *iformat) :
	pVideoState(VideoState::stream_open(filename, iformat)),
	display_disable(0),
	width(0),
	height(0) 
{ }

FfmpegAvPlayback::~FfmpegAvPlayback() {}

void FfmpegAvPlayback::set_player_state_callback_func(PlayerStateCallback callback, const std::function<void()>& func) {
	pVideoState->set_player_state_callback_func(callback, func);
}

void FfmpegAvPlayback::play() {
	if (pVideoState->get_paused()) {
		toggle_pause();
		pVideoState->set_stopped(false);
	}
}

// Stop and put the playback speed to 0x
// Note the playback speed is not implemetnted yet
void FfmpegAvPlayback::stop() {
	if (!pVideoState->get_paused()) {
		toggle_pause();
		pVideoState->set_stopped(true);
	}
	// Stop playback and seek to the start of the stream 
	/*double pos = get_master_clock();
	double start = pVideoState->get_ic()->start_time / (double)AV_TIME_BASE;
	double incr = start - pos;
	stream_seek((int64_t)(start * AV_TIME_BASE), (int64_t)(incr * AV_TIME_BASE), 0);*/
}

// pause and keep the playback speed.
// Note the playback speed is not implemetnted yet
void FfmpegAvPlayback::toggle_pause() {
	stream_toggle_pause();
	pVideoState->set_step(false);
}


void FfmpegAvPlayback::stream_seek(int64_t pos, int64_t rel, int seek_by_bytes) {
	pVideoState->stream_seek(pos, rel, seek_by_bytes);
}

double FfmpegAvPlayback::get_duration() const {
	return pVideoState->get_duration();
}

double FfmpegAvPlayback::get_master_clock() const {
	return pVideoState->get_master_clock();
}

double FfmpegAvPlayback::get_fps() const {
	return pVideoState->get_fps();
}

void FfmpegAvPlayback::set_rate(double rate) {
	// TODO(fraudies): Here is a miss-match between the API's (rate/vs step)
	pVideoState->set_rate(rate);
}

double FfmpegAvPlayback::get_rate() const {
	return pVideoState->get_rate();
}

int64_t FfmpegAvPlayback::get_start_time() const {
	return pVideoState->get_ic()->start_time;
}

int64_t FfmpegAvPlayback::get_seek_pos() const {
	return pVideoState->get_seek_pos();
}

int FfmpegAvPlayback::get_frame_timer() {
	return frame_timer;
}

void FfmpegAvPlayback::set_frame_timer(int newFrame_timer) {
	frame_timer = newFrame_timer;
}

void FfmpegAvPlayback::set_force_refresh(int refresh) {
	force_refresh = refresh;
}

double FfmpegAvPlayback::vp_duration(Frame * vp, Frame * nextvp, double max_frame_duration) {
	if (vp->serial == nextvp->serial) {
		double duration = nextvp->pts - vp->pts;
		if (isnan(duration) || duration <= 0 || duration > max_frame_duration)
			return vp->duration;
		else
			return duration;
	}
	else {
		return 0.0;

	}
}

void FfmpegAvPlayback::step_to_next_frame() {
	/* if the stream is paused unpause it, then step */
	if (pVideoState->get_paused())
		stream_toggle_pause();

	pVideoState->set_step(1);
}