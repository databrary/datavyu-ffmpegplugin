#include "FfmpegJavaAvPlayback.h"

void FfmpegJavaAvPlayback::init() {
	/* register all codecs, demux and protocols */
#if CONFIG_AVDEVICE
	avdevice_register_all();
#endif
	avformat_network_init();

	//if (display_disable) {
	//	pVideoState->set_video_disable(1);
	//}
}

FfmpegJavaAvPlayback::FfmpegJavaAvPlayback(
	const char * filename,
	AVInputFormat * iformat):
FfmpegAvPlayback(filename, iformat) {
	// TODO(fraudies): Add audio open callback

	pVideoState->set_destroy_callback([this] {
		this->destroy();
	});

	pVideoState->set_step_to_next_frame_callback([this] {
		this->step_to_next_frame();
	});
}

FfmpegJavaAvPlayback::~FfmpegJavaAvPlayback()
{ }

VideoState * FfmpegJavaAvPlayback::get_VideoState() { return pVideoState; }

void FfmpegJavaAvPlayback::destroy() {
	// only necessary when using as library -- has no effect otherwise
	// stop_display_loop();

	// close the VideoState Stream
	if (pVideoState)
		pVideoState->stream_close();

#if CONFIG_AVFILTER
	//av_freep(&vfilters_list);
#endif
	avformat_network_deinit();

	if (show_status)
		printf("\n");

	av_log(NULL, AV_LOG_QUIET, "%s", "");

	exit(0);
}

void FfmpegJavaAvPlayback::init_and_start_stream() {
	init();
	pVideoState->stream_start();
}

void FfmpegJavaAvPlayback::set_balance(float fBalance) {
}

float FfmpegJavaAvPlayback::get_balance() {
	return 0.0f;
}

void FfmpegJavaAvPlayback::set_audioSyncDelay(long lMillis){
}

long FfmpegJavaAvPlayback::get_audioSyncDelay() {
	return 0;
}

int FfmpegJavaAvPlayback::get_image_width() {
	return pVideoState->get_image_width();
}

int FfmpegJavaAvPlayback::get_image_height() {
	return pVideoState->get_image_height();
}

bool FfmpegJavaAvPlayback::has_image_data() const {
	return pVideoState->has_image_data();
}

bool FfmpegJavaAvPlayback::has_audio_data() const {
	return pVideoState->has_audio_data();
}

bool FfmpegJavaAvPlayback::do_display() {
	bool display = false;
	
	double time;

	Frame *sp, *sp2;

	if (!pVideoState->get_paused() && pVideoState->get_master_sync_type() == AV_SYNC_EXTERNAL_CLOCK && pVideoState->get_realtime())
		pVideoState->check_external_clock_speed();

	if (pVideoState->get_video_st()) {
	retry:
		if (pVideoState->get_pPictq()->nb_remaining() == 0) {
			// nothing to do, no picture to display in the queue
		}
		else {
			double last_duration, duration, delay;
			Frame *vp, *lastvp;

			/* dequeue the picture */
			lastvp = pVideoState->get_pPictq()->peek_last();
			vp = pVideoState->get_pPictq()->peek();

			if (vp->serial != pVideoState->get_pVideoq()->get_serial()) {
				pVideoState->get_pPictq()->next();
				goto retry;
			}

			if (lastvp->serial != vp->serial)
				frame_timer = av_gettime_relative() / 1000000.0;

			if (pVideoState->get_paused())
				goto display;

			/* compute nominal last_duration */
			last_duration = vp_duration(lastvp, vp, pVideoState->get_max_frame_duration());
			delay = pVideoState->compute_target_delay(last_duration);

			time = av_gettime_relative() / 1000000.0;
			if (time < frame_timer + delay) {
				//*remaining_time = FFMIN(frame_timer + delay - time, *remaining_time);
				goto display;
			}

			frame_timer += delay;
			if (delay > 0 && time - frame_timer > AV_SYNC_THRESHOLD_MAX)
				frame_timer = time;

			std::unique_lock<std::mutex> locker(pVideoState->get_pPictq()->get_mutex());
			if (!isnan(vp->pts))
				pVideoState->update_pts(vp->pts, vp->pos, vp->serial);
			locker.unlock();

			if (pVideoState->get_pPictq()->nb_remaining() > 1) {
				Frame *nextvp = pVideoState->get_pPictq()->peek_next();
				duration = vp_duration(vp, nextvp, pVideoState->get_max_frame_duration());
				if (!pVideoState->get_step() && (framedrop>0 || (framedrop && pVideoState->get_master_sync_type() != AV_SYNC_VIDEO_MASTER)) && time > frame_timer + duration) {
					frame_drops_late++;
					pVideoState->get_pPictq()->next();
					goto retry;
				}
			}

			pVideoState->get_pPictq()->next();
			force_refresh = 1;

			if (pVideoState->get_step() && !pVideoState->get_paused())
				stream_toggle_pause();
		}
	display:
		/* display picture */
		if (!display_disable
			&& force_refresh
			&& (pVideoState->get_show_mode() == SHOW_MODE_VIDEO)
			&& pVideoState->get_pPictq()->get_rindex_shown())
			display = true;
	}
	force_refresh = 0;

	return display;
}

void FfmpegJavaAvPlayback::update_image_buffer(uint8_t* pImageData, const long len) {
	bool doUpdate = do_display();
	if (doUpdate) {
		Frame *vp = pVideoState->get_pPictq()->peek_last();
		long srcLen = vp->frame->width * vp->frame->height * 3 * sizeof(uint8_t);
		if (srcLen == len) {
			memcpy(pImageData, vp->frame->data[0], vp->frame->width * vp->frame->height);
		}
	}
}

void FfmpegJavaAvPlayback::update_audio_buffer(uint8_t* pAudioData, const long len) {
	pVideoState->sdl_audio_callback(pAudioData, len);
}

//TODO(Reda): implement get audio format function
AudioFormat FfmpegJavaAvPlayback::get_audio_format() {
	return AudioFormat();
}

//TODO(Reda): implement get audio format function
PixelFormat FfmpegJavaAvPlayback::get_pixel_format() {
	return PixelFormat();
}
