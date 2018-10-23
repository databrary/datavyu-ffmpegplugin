#include "FfmpegJavaAvPlayback.h"
#include "MediaPlayerErrors.h"
#include "FfmpegErrorUtils.h"

FfmpegJavaAvPlayback::FfmpegJavaAvPlayback(const AudioFormat *pAudioFormat,
	const PixelFormat *pPixelFormat,
	const int audioBufferSizeInBy) : FfmpegAvPlayback(),
		pAudioFormat(pAudioFormat), 
		pPixelFormat(pPixelFormat),
		audioBufferSizeInBy(audioBufferSizeInBy),
		img_convert_ctx(nullptr),
		remaining_time_to_display(0)
{ }

FfmpegJavaAvPlayback::~FfmpegJavaAvPlayback()
{ }

int FfmpegJavaAvPlayback::Init(const char * filename, AVInputFormat * iformat) {

	/* register all codecs, demux and protocols */
#if CONFIG_AVDEVICE
	avdevice_register_all();
#endif
	avformat_network_init();

	int err = FfmpegAvPlayback::Init(filename, iformat, audioBufferSizeInBy);  // initializes the video state
	if (err) {
		return err;
	}

	pVideoState->set_destroy_callback([this] {
		destroy();
	});

	// TODO: Clean-up this callback as the first three parameters are not used here
	pVideoState->set_audio_open_callback([this](int64_t wanted_channel_layout, int wanted_nb_channels,
		int wanted_sample_rate, struct AudioParams *audio_hw_params) {
		return audio_open(wanted_channel_layout, wanted_nb_channels, wanted_sample_rate, audio_hw_params);
	});


	pVideoState->set_step_to_next_frame_callback([this] {
		this->step_to_next_frame();
	});

	return ERROR_NONE;
}

VideoState * FfmpegJavaAvPlayback::get_VideoState() { return pVideoState; }

int FfmpegJavaAvPlayback::audio_open(int64_t wanted_channel_layout, int wanted_nb_channels,
	int wanted_sample_rate, struct AudioParams *audio_hw_params) {
	// TODO(fraudies): If we need to change the audio format overwrite pAudioFormat here
	pAudioFormat->toAudioParams(audio_hw_params);
	return audioBufferSizeInBy;
}

void FfmpegJavaAvPlayback::destroy() {

	sws_freeContext(img_convert_ctx);

	delete pVideoState;

#if CONFIG_VIDEO_FILTER
	//av_freep(&vfilters_list);
#endif
	avformat_network_deinit();

	av_log(NULL, AV_LOG_QUIET, "%s", "");
}

int FfmpegJavaAvPlayback::start_stream() {
	return ffmpegToJavaErrNo(pVideoState->stream_start());
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

int FfmpegJavaAvPlayback::get_image_width() const {
	return pVideoState->get_image_width();
}

int FfmpegJavaAvPlayback::get_image_height() const {
	return pVideoState->get_image_height();
}

bool FfmpegJavaAvPlayback::has_image_data() const {
	return pVideoState->has_image_data();
}

bool FfmpegJavaAvPlayback::has_audio_data() const {
	return pVideoState->has_audio_data();
}

bool FfmpegJavaAvPlayback::do_display(double *remaining_time) {
	bool display = false;
	
	double time;

	Frame *sp, *sp2;

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

			if (pVideoState->get_paused() && !force_refresh)
				goto display;

			/* compute nominal last_duration */
			last_duration = vp_duration(lastvp, vp, pVideoState->get_max_frame_duration());
			delay = pVideoState->compute_target_delay(last_duration);

			time = av_gettime_relative() / 1000000.0;
			if (time < frame_timer + delay) {
				*remaining_time = FFMIN(frame_timer + delay - time, *remaining_time);
				goto display;
			}

			frame_timer += delay;
			if (delay > 0 && time - frame_timer > AV_SYNC_THRESHOLD_MAX)
				frame_timer = time;

			std::unique_lock<std::mutex> locker(pVideoState->get_pPictq()->get_mutex());
			if (!isnan(vp->pts))
				pVideoState->update_pts(vp->pts, vp->serial);
			locker.unlock();

			if (pVideoState->get_pPictq()->nb_remaining() > 1) {
				Frame *nextvp = pVideoState->get_pPictq()->peek_next();
				duration = vp_duration(vp, nextvp, pVideoState->get_max_frame_duration());
				if (!pVideoState->get_step() && time > frame_timer + duration) {
					frame_drops_late++;
					pVideoState->get_pPictq()->next();
					goto retry;
				}
			}

			pVideoState->get_pPictq()->next();
			force_refresh = 1;
		}
	display:
		/* display picture */
		if (!display_disable
			&& force_refresh
			&& pVideoState->get_pPictq()->get_rindex_shown()) {
			display = true;
			force_refresh = 0;
			if (pVideoState->get_step() && !pVideoState->get_paused())
				stream_toggle_pause();
		}
	}

	if (kEnableShowStatus) {
		static int64_t last_time;
		int64_t cur_time;
		int aqsize, vqsize, sqsize;
		double av_diff;

		cur_time = av_gettime_relative();
		if (!last_time || (cur_time - last_time) >= 30000) {
			aqsize = 0;
			vqsize = 0;
			sqsize = 0;
			if (pVideoState->get_audio_st())
				aqsize = pVideoState->get_pAudioq()->get_size();
			if (pVideoState->get_video_st())
				vqsize = pVideoState->get_pVideoq()->get_size();
			av_diff = 0;
			if (pVideoState->get_audio_st() && pVideoState->get_video_st())
				av_diff = pVideoState->get_pAudclk()->get_time() - pVideoState->get_pVidclk()->get_time();
			else if (pVideoState->get_video_st())
				av_diff = pVideoState->get_master_clock()->get_time() - pVideoState->get_pVidclk()->get_time();
			else if (pVideoState->get_audio_st())
				av_diff = pVideoState->get_master_clock()->get_time() - pVideoState->get_pAudclk()->get_time();
			av_log(NULL, AV_LOG_INFO,
				"m %7.2f, a %7.2f, v %7.2f at %1.3fX %s:%7.3f de=%4d dl=%4d re=%7.2f aq=%5dKB vq=%5dKB sq=%5dB f=%f /%f   \r",
				pVideoState->get_master_clock()->get_time(),
				pVideoState->get_pAudclk() != nullptr ? pVideoState->get_pAudclk()->get_time() : 0,
				pVideoState->get_pVidclk() != nullptr ? pVideoState->get_pVidclk()->get_time() : 0,
				pVideoState->get_rate(),
				(pVideoState->get_audio_st() && pVideoState->get_video_st()) ? "A-V" : (pVideoState->get_video_st() ? "M-V" : (pVideoState->get_audio_st() ? "M-A" : "   ")),
				av_diff,
				pVideoState->get_frame_drops_early(),
				frame_drops_late,
				*remaining_time,
				aqsize / 1024,
				vqsize / 1024,
				sqsize,
				pVideoState->get_video_st() ? pVideoState->get_pViddec()->get_avctx()->pts_correction_num_faulty_dts : 0,
				pVideoState->get_video_st() ? pVideoState->get_pViddec()->get_avctx()->pts_correction_num_faulty_pts : 0);
			fflush(stdout);
			last_time = cur_time;
		}
	}

	return display;
}

void FfmpegJavaAvPlayback::update_image_buffer(uint8_t* pImageData, const long len) {
	bool doUpdate = do_display(&remaining_time_to_display);
	if (doUpdate) {
		Frame *vp = pVideoState->get_pPictq()->peek_last();
		img_convert_ctx = sws_getCachedContext(
			img_convert_ctx,
			vp->frame->width, vp->frame->height, static_cast<AVPixelFormat>(vp->frame->format), 
			vp->frame->width, vp->frame->height, pPixelFormat->type,
			SWS_BICUBIC, NULL, NULL, NULL);
		if (img_convert_ctx != NULL) {
			// TODO(fraudies): Add switch case statement for the different pixel formats
			// Left the pixels allocation/free here to support resizing through sws_scale natively
			uint8_t* pixels[4];
			int pitch[4];
			av_image_alloc(pixels, pitch, vp->width, vp->height, AV_PIX_FMT_RGB24, 1);
			sws_scale(img_convert_ctx, (const uint8_t * const *) vp->frame->data, vp->frame->linesize,
				0, vp->frame->height, pixels, pitch);
			// Maybe check that we have 3 components
			memcpy(pImageData, pixels[0], vp->frame->width * vp->frame->height * 3 * sizeof(uint8_t));
			av_freep(&pixels[0]);
		}
	}
}

void FfmpegJavaAvPlayback::update_audio_buffer(uint8_t* pAudioData, const long len) {
	pVideoState->audio_callback(pAudioData, len);
}

void FfmpegJavaAvPlayback::get_audio_format(AudioFormat* pAudioFormat) {
	*pAudioFormat = *this->pAudioFormat;
}

void FfmpegJavaAvPlayback::get_pixel_format(PixelFormat* pPixelFormat) {
	*pPixelFormat = *this->pPixelFormat;
}
