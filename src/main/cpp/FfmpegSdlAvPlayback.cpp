#include "FfmpegSdlAvPlayback.h"
#include "MediaPlayerErrors.h"
#include "FfmpegErrorUtils.h"


int FfmpegSdlAvPlayback::kDefaultWidth = 640;
int FfmpegSdlAvPlayback::kDefaultHeight = 480;
unsigned FfmpegSdlAvPlayback::kSwsFlags = SWS_BICUBIC;
const char* FfmpegSdlAvPlayback::kDefaultWindowTitle = "Ffmpeg SDL player";
int FfmpegSdlAvPlayback::kWindowResizable = 1;

/* Minimum SDL audio buffer size, in samples. */
int FfmpegSdlAvPlayback::kAudioMinBufferSize = 512;
/* Calculate actual buffer size keeping in mind not cause too frequent audio callbacks */
int FfmpegSdlAvPlayback::kAudioMaxCallbackPerSec = 30;
/* Step size for volume control in dB */
double FfmpegSdlAvPlayback::kVolumeStepInDecibel = 0.75;
/* polls for possible required screen refresh at least this often, should be less than 1/fps */
double FfmpegSdlAvPlayback::kRefreshRate = 0.01;
int FfmpegSdlAvPlayback::kCursorHideDelayInMillis = 1000000;
/* initialize the texture format map */
const FfmpegSdlAvPlayback::TextureFormatEntry FfmpegSdlAvPlayback::kTextureFormatMap[] = {
	{ AV_PIX_FMT_RGB8,           SDL_PIXELFORMAT_RGB332 },
	{ AV_PIX_FMT_RGB444,         SDL_PIXELFORMAT_RGB444 },
	{ AV_PIX_FMT_RGB555,         SDL_PIXELFORMAT_RGB555 },
	{ AV_PIX_FMT_BGR555,         SDL_PIXELFORMAT_BGR555 },
	{ AV_PIX_FMT_RGB565,         SDL_PIXELFORMAT_RGB565 },
	{ AV_PIX_FMT_BGR565,         SDL_PIXELFORMAT_BGR565 },
	{ AV_PIX_FMT_RGB24,          SDL_PIXELFORMAT_RGB24 },
	{ AV_PIX_FMT_BGR24,          SDL_PIXELFORMAT_BGR24 },
	{ AV_PIX_FMT_0RGB32,         SDL_PIXELFORMAT_RGB888 },
	{ AV_PIX_FMT_0BGR32,         SDL_PIXELFORMAT_BGR888 },
	{ AV_PIX_FMT_NE(RGB0, 0BGR), SDL_PIXELFORMAT_RGBX8888 },
	{ AV_PIX_FMT_NE(BGR0, 0RGB), SDL_PIXELFORMAT_BGRX8888 },
	{ AV_PIX_FMT_RGB32,          SDL_PIXELFORMAT_ARGB8888 },
	{ AV_PIX_FMT_RGB32_1,        SDL_PIXELFORMAT_RGBA8888 },
	{ AV_PIX_FMT_BGR32,          SDL_PIXELFORMAT_ABGR8888 },
	{ AV_PIX_FMT_BGR32_1,        SDL_PIXELFORMAT_BGRA8888 },
	{ AV_PIX_FMT_YUV420P,        SDL_PIXELFORMAT_IYUV },
	{ AV_PIX_FMT_YUYV422,        SDL_PIXELFORMAT_YUY2 },
	{ AV_PIX_FMT_UYVY422,        SDL_PIXELFORMAT_UYVY },
	{ AV_PIX_FMT_NONE,           SDL_PIXELFORMAT_UNKNOWN },
};


inline int FfmpegSdlAvPlayback::compute_mod(int a, int b) {
	return a < 0 ? a % b + b : a % b;
}

inline void FfmpegSdlAvPlayback::fill_rectangle(int x, int y, int w, int h) {
	SDL_Rect rect;
	rect.x = x;
	rect.y = y;
	rect.w = w;
	rect.h = h;
	if (w && h)
		SDL_RenderFillRect(renderer, &rect);
}

void FfmpegSdlAvPlayback::calculate_display_rect(SDL_Rect *rect,
	int scr_xleft, int scr_ytop, int scr_width, int scr_height,
	int pic_width, int pic_height, AVRational pic_sar) {
	float aspect_ratio;
	int width, height, x, y;

	if (pic_sar.num == 0)
		aspect_ratio = 0;
	else
		aspect_ratio = av_q2d(pic_sar);

	if (aspect_ratio <= 0.0)
		aspect_ratio = 1.0;
	aspect_ratio *= (float)pic_width / (float)pic_height;

	/* XXX: we suppose the screen has a 1.0 pixel ratio */
	height = scr_height;
	width = lrint(height * aspect_ratio) & ~1;
	if (width > scr_width) {
		width = scr_width;
		height = lrint(width / aspect_ratio) & ~1;
	}
	x = (scr_width - width) / 2;
	y = (scr_height - height) / 2;
	rect->x = scr_xleft + x;
	rect->y = scr_ytop + y;
	rect->w = FFMAX(width, 1);
	rect->h = FFMAX(height, 1);
}

FfmpegSdlAvPlayback::FfmpegSdlAvPlayback(int startup_volume) : 
	FfmpegAvPlayback(),
	ytop(0),
	xleft(0),
	window(nullptr),
	renderer(nullptr),
	img_convert_ctx(nullptr),
	vis_texture(nullptr),
	vid_texture(nullptr),
	last_i_start(0),
	screen_width(0),
	screen_height(0),
	is_full_screen(0), 
	audio_volume(0),
	cursor_last_shown(0),
	cursor_hidden(0),
	renderer_info({ 0 }) {

	if (startup_volume < 0) {
		av_log(NULL, AV_LOG_WARNING, "-volume=%d < 0, setting to 0\n", startup_volume);
	}
	if (startup_volume > 100) {
		av_log(NULL, AV_LOG_WARNING, "-volume=%d > 100, setting to 100\n", startup_volume);
	}
	audio_volume = av_clip(SDL_MIX_MAXVOLUME * av_clip(startup_volume, 0, 100) / 100, 0, SDL_MIX_MAXVOLUME);
}

FfmpegSdlAvPlayback::~FfmpegSdlAvPlayback() {
	av_free(window_title);
}

int FfmpegSdlAvPlayback::Init(const char *filename, AVInputFormat *iformat) {
	/* register all codecs, demux and protocols */
#if CONFIG_AVDEVICE
	avdevice_register_all();
#endif
	avformat_network_init();

	int err = FfmpegAvPlayback::Init(filename, iformat, kAudioMinBufferSize);
	if (err) {
		return err;
	}

	if (!window_title)
		window_title = av_asprintf("%s", filename);

	// Set callback functions
	pVideoState->set_audio_open_callback([this](int64_t wanted_channel_layout, int wanted_nb_channels,
		int wanted_sample_rate, struct AudioParams *audio_hw_params) {
		return this->audio_open(wanted_channel_layout, wanted_nb_channels, wanted_sample_rate,
			audio_hw_params);
	});
	pVideoState->set_pause_audio_device_callback([this] {
		this->pauseAudioDevice();
	});
	pVideoState->set_destroy_callback([this] {
		this->destroy();
	});
	pVideoState->set_step_to_next_frame_callback([this] {
		this->step_to_next_frame();
	});

	return ERROR_NONE;
}

/* Public Members */
int FfmpegSdlAvPlayback::audio_open(int64_t wanted_channel_layout, int wanted_nb_channels, int wanted_sample_rate,
	struct AudioParams *audio_hw_params) {
	SDL_AudioSpec wanted_spec, spec;
	const char *env;
	static const int next_nb_channels[] = { 0, 0, 1, 6, 2, 6, 4, 6 };
	static const int next_sample_rates[] = { 0, 44100, 48000, 96000, 192000 };
	int next_sample_rate_idx = FF_ARRAY_ELEMS(next_sample_rates) - 1;

	env = SDL_getenv("SDL_AUDIO_CHANNELS");
	if (env) {
		wanted_nb_channels = atoi(env);
		wanted_channel_layout = av_get_default_channel_layout(wanted_nb_channels);
	}
	if (!wanted_channel_layout || wanted_nb_channels != av_get_channel_layout_nb_channels(wanted_channel_layout)) {
		wanted_channel_layout = av_get_default_channel_layout(wanted_nb_channels);
		wanted_channel_layout &= ~AV_CH_LAYOUT_STEREO_DOWNMIX;
	}
	wanted_nb_channels = av_get_channel_layout_nb_channels(wanted_channel_layout);
	wanted_spec.channels = wanted_nb_channels;
	wanted_spec.freq = wanted_sample_rate;
	if (wanted_spec.freq <= 0 || wanted_spec.channels <= 0) {
		av_log(NULL, AV_LOG_ERROR, "Invalid sample rate or channel count!\n");
		return -1;
	}
	while (next_sample_rate_idx && next_sample_rates[next_sample_rate_idx] >= wanted_spec.freq)
		next_sample_rate_idx--;
	wanted_spec.format = AUDIO_S16SYS;
	wanted_spec.silence = 0;
	wanted_spec.samples = FFMAX(kAudioMinBufferSize, 2 << av_log2(wanted_spec.freq / kAudioMaxCallbackPerSec));
	wanted_spec.callback = sdl_audio_callback_bridge;
	wanted_spec.userdata = this;
	while (!(audio_dev = SDL_OpenAudioDevice(NULL, 0, &wanted_spec, &spec, SDL_AUDIO_ALLOW_FREQUENCY_CHANGE | SDL_AUDIO_ALLOW_CHANNELS_CHANGE))) {
		av_log(NULL, AV_LOG_WARNING, "SDL_OpenAudio (%d channels, %d Hz): %s\n",
			wanted_spec.channels, wanted_spec.freq, SDL_GetError());
		wanted_spec.channels = next_nb_channels[FFMIN(7, wanted_spec.channels)];
		if (!wanted_spec.channels) {
			wanted_spec.freq = next_sample_rates[next_sample_rate_idx--];
			wanted_spec.channels = wanted_nb_channels;
			if (!wanted_spec.freq) {
				av_log(NULL, AV_LOG_ERROR,
					"No more combinations to try, audio open failed\n");
				return -1;
			}
		}
		wanted_channel_layout = av_get_default_channel_layout(wanted_spec.channels);
	}
	if (spec.format != AUDIO_S16SYS) {
		av_log(NULL, AV_LOG_ERROR,
			"SDL advised audio format %d is not supported!\n", spec.format);
		return -1;
	}
	if (spec.channels != wanted_spec.channels) {
		wanted_channel_layout = av_get_default_channel_layout(spec.channels);
		if (!wanted_channel_layout) {
			av_log(NULL, AV_LOG_ERROR,
				"SDL advised channel count %d is not supported!\n", spec.channels);
			return -1;
		}
	}

	audio_hw_params->fmt = AV_SAMPLE_FMT_S16;
	audio_hw_params->freq = spec.freq;
	audio_hw_params->channel_layout = wanted_channel_layout;
	audio_hw_params->channels = spec.channels;
	audio_hw_params->frame_size = av_samples_get_buffer_size(NULL, audio_hw_params->channels, 1, audio_hw_params->fmt, 1);
	audio_hw_params->bytes_per_sec = av_samples_get_buffer_size(NULL, audio_hw_params->channels, audio_hw_params->freq, audio_hw_params->fmt, 1);
	if (audio_hw_params->bytes_per_sec <= 0 || audio_hw_params->frame_size <= 0) {
		av_log(NULL, AV_LOG_ERROR, "av_samples_get_buffer_size failed\n");
		return -1;
	}
	return spec.size;
}

int FfmpegSdlAvPlayback::video_open(const char* filename) {
	int w, h;

	if (screen_width) {
		w = screen_width;
		h = screen_height;
	}
	else {
		w = kDefaultWidth;
		h = kDefaultHeight;
	}

	window_title = av_strdup(filename);

	SDL_SetWindowTitle(window, window_title);
	SDL_SetWindowSize(window, w, h);
	SDL_SetWindowPosition(window, SDL_WINDOWPOS_CENTERED, SDL_WINDOWPOS_CENTERED);
	if (is_full_screen)
		SDL_SetWindowFullscreen(window, SDL_WINDOW_FULLSCREEN_DESKTOP);
	SDL_ShowWindow(window);

	width = w;
	height = h;

	return 0;
}

void FfmpegSdlAvPlayback::set_default_window_size(int width, int height, AVRational sar) {
	SDL_Rect rect;
	calculate_display_rect(&rect, 0, 0, INT_MAX, height, width, height, sar);
	kDefaultWidth = rect.w;
	kDefaultHeight = rect.h;
}

void FfmpegSdlAvPlayback::closeAudioDevice() {
	SDL_CloseAudioDevice(audio_dev);
}

void FfmpegSdlAvPlayback::pauseAudioDevice() {
	SDL_PauseAudioDevice(audio_dev, 0);
}

VideoState* FfmpegSdlAvPlayback::get_VideoState() { return pVideoState; }

void FfmpegSdlAvPlayback::toggle_full_screen() {
	is_full_screen = !is_full_screen;
	SDL_SetWindowFullscreen(window, is_full_screen ? SDL_WINDOW_FULLSCREEN_DESKTOP : 0);
}

int FfmpegSdlAvPlayback::upload_texture(SDL_Texture **tex, AVFrame *frame, struct SwsContext **img_convert_ctx) {
	int ret = 0;
	Uint32 sdl_pix_fmt;
	SDL_BlendMode sdl_blendmode;
	get_sdl_pix_fmt_and_blendmode(frame->format, &sdl_pix_fmt, &sdl_blendmode);
	if (realloc_texture(tex, sdl_pix_fmt == SDL_PIXELFORMAT_UNKNOWN ? SDL_PIXELFORMAT_ARGB8888 : sdl_pix_fmt, frame->width, frame->height, sdl_blendmode, 0) < 0)
		return -1;
	switch (sdl_pix_fmt) {
	case SDL_PIXELFORMAT_UNKNOWN:
		/* This should only happen if we are not using avfilter... */
		*img_convert_ctx = sws_getCachedContext(*img_convert_ctx,
			frame->width, frame->height, static_cast<AVPixelFormat>(frame->format), frame->width, frame->height,
			AV_PIX_FMT_BGRA, kSwsFlags, NULL, NULL, NULL);
		if (*img_convert_ctx != NULL) {
			uint8_t *pixels[4];
			int pitch[4];
			if (!SDL_LockTexture(*tex, NULL, (void **)pixels, pitch)) {
				sws_scale(*img_convert_ctx, (const uint8_t * const *)frame->data, frame->linesize,
					0, frame->height, pixels, pitch);
				SDL_UnlockTexture(*tex);
			}
		}
		else {
			av_log(NULL, AV_LOG_FATAL, "Cannot initialize the conversion context\n");
			ret = -1;
		}
		break;
	case SDL_PIXELFORMAT_IYUV:
		if (frame->linesize[0] > 0 && frame->linesize[1] > 0 && frame->linesize[2] > 0) {
			ret = SDL_UpdateYUVTexture(*tex, NULL, frame->data[0], frame->linesize[0],
				frame->data[1], frame->linesize[1],
				frame->data[2], frame->linesize[2]);
		}
		else if (frame->linesize[0] < 0 && frame->linesize[1] < 0 && frame->linesize[2] < 0) {
			ret = SDL_UpdateYUVTexture(*tex, NULL, frame->data[0] + frame->linesize[0] * (frame->height - 1), -frame->linesize[0],
				frame->data[1] + frame->linesize[1] * (AV_CEIL_RSHIFT(frame->height, 1) - 1), -frame->linesize[1],
				frame->data[2] + frame->linesize[2] * (AV_CEIL_RSHIFT(frame->height, 1) - 1), -frame->linesize[2]);
		}
		else {
			av_log(NULL, AV_LOG_ERROR, "Mixed negative and positive linesizes are not supported.\n");
			return -1;
		}
		break;
	default:
		if (frame->linesize[0] < 0) {
			ret = SDL_UpdateTexture(*tex, NULL, frame->data[0] + frame->linesize[0] * (frame->height - 1), -frame->linesize[0]);
		}
		else {
			ret = SDL_UpdateTexture(*tex, NULL, frame->data[0], frame->linesize[0]);
		}
		break;
	}
	return ret;
}

void FfmpegSdlAvPlayback::get_sdl_pix_fmt_and_blendmode(int format, Uint32 *sdl_pix_fmt, SDL_BlendMode *sdl_blendmode) {
	int i;
	*sdl_blendmode = SDL_BLENDMODE_NONE;
	*sdl_pix_fmt = SDL_PIXELFORMAT_UNKNOWN;
	if (format == AV_PIX_FMT_RGB32 ||
		format == AV_PIX_FMT_RGB32_1 ||
		format == AV_PIX_FMT_BGR32 ||
		format == AV_PIX_FMT_BGR32_1)
		*sdl_blendmode = SDL_BLENDMODE_BLEND;
	for (i = 0; i < FF_ARRAY_ELEMS(kTextureFormatMap) - 1; i++) {
		if (format == kTextureFormatMap[i].format) {
			*sdl_pix_fmt = kTextureFormatMap[i].texture_fmt;
			return;
		}
	}
}

int FfmpegSdlAvPlayback::realloc_texture(SDL_Texture **texture, Uint32 new_format, int new_width, int new_height, SDL_BlendMode blendmode, int init_texture) {
	Uint32 format;
	int access, w, h;
	if (!*texture || SDL_QueryTexture(*texture, &format, &access, &w, &h) < 0 || new_width != w || new_height != h || new_format != format) {
		void *pixels;
		int pitch;
		if (*texture)
			SDL_DestroyTexture(*texture);
		if (!(*texture = SDL_CreateTexture(renderer, new_format, SDL_TEXTUREACCESS_STREAMING, new_width, new_height)))
			return -1;
		if (SDL_SetTextureBlendMode(*texture, blendmode) < 0)
			return -1;
		if (init_texture) {
			if (SDL_LockTexture(*texture, NULL, &pixels, &pitch) < 0)
				return -1;
			memset(pixels, 0, pitch * new_height);
			SDL_UnlockTexture(*texture);
		}
		av_log(NULL, AV_LOG_VERBOSE, "Created %dx%d texture with %s.\n", new_width, new_height, SDL_GetPixelFormatName(new_format));
	}
	return 0;
}

void FfmpegSdlAvPlayback::video_display() {
	if (!width)
		video_open(pVideoState->get_filename());

	SDL_SetRenderDrawColor(renderer, 0, 0, 0, 255);
	SDL_RenderClear(renderer);
	if (pVideoState->get_video_st())
		video_image_display();
	SDL_RenderPresent(renderer);
}

void FfmpegSdlAvPlayback::video_image_display() {
	Frame *vp;
	SDL_Rect rect;

	vp = pVideoState->get_pPictq()->peek_last();

	calculate_display_rect(&rect, this->xleft, this->ytop, this->width, this->height, vp->width, vp->height, vp->sar);

	if (!vp->uploaded) {
		if (upload_texture(&vid_texture, vp->frame, &img_convert_ctx) < 0)
			return;
		vp->uploaded = 1;
		vp->flip_v = vp->frame->linesize[0] < 0;
	}

	SDL_RenderCopyEx(renderer, vid_texture, NULL, &rect, 0, NULL, vp->flip_v ? SDL_FLIP_VERTICAL : SDL_FLIP_NONE);
}

int FfmpegSdlAvPlayback::get_audio_volume() const {
	return audio_volume;
}

void FfmpegSdlAvPlayback::update_volume(int sign, double step) {
	int audio_volume = get_audio_volume();
	double volume_level = audio_volume ? (20 * log(audio_volume / (double)SDL_MIX_MAXVOLUME) / log(10)) : -1000.0;
	int new_volume = lrint(SDL_MIX_MAXVOLUME * pow(10.0, (volume_level + sign * step) / 20.0));
	audio_volume = av_clip(audio_volume == new_volume ? (audio_volume + sign) : new_volume, 0, SDL_MIX_MAXVOLUME);
}

void FfmpegSdlAvPlayback::refresh_loop_wait_event(SDL_Event *event) {
	double remaining_time = 0.0;
	SDL_PumpEvents();
	while (!SDL_PeepEvents(event, 1, SDL_GETEVENT, SDL_FIRSTEVENT, SDL_LASTEVENT)) {
		if (!cursor_hidden && av_gettime_relative() - cursor_last_shown > kCursorHideDelayInMillis) {
			SDL_ShowCursor(0);
			cursor_hidden = 1;
		}
		if (remaining_time > 0.0)
			av_usleep((int64_t)(remaining_time * 1000000.0));
		remaining_time = kRefreshRate;
		if (!pVideoState->get_paused() || force_refresh)
			video_refresh(&remaining_time);
		SDL_PumpEvents();
	}
}

void FfmpegSdlAvPlayback::video_refresh(double *remaining_time) {
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

			// Force refresh overrides paused
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
			if (delay > 0 && time - frame_timer > VideoState::kAvSyncThresholdMax)
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
			video_display();
			force_refresh = 0; // only reset force refresh when displayed
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
			if (pVideoState->get_audio_st()) {
				aqsize = pVideoState->get_pAudioq()->get_size();
			}
			if (pVideoState->get_video_st()) {
				vqsize = pVideoState->get_pVideoq()->get_size();
			}
			av_diff = 0;
			if (pVideoState->get_audio_st() && pVideoState->get_video_st()) {
				av_diff = pVideoState->get_pAudclk()->get_time() - pVideoState->get_pVidclk()->get_time();
			}
			else if (pVideoState->get_video_st()) {
				av_diff = pVideoState->get_master_clock()->get_time() - pVideoState->get_pVidclk()->get_time();
			}
			else if (pVideoState->get_audio_st()) {
				av_diff = pVideoState->get_master_clock()->get_time() - pVideoState->get_pAudclk()->get_time();
			}
			av_log(NULL, AV_LOG_INFO,
				"%7.2f at %1.3fX vc=%5.2f %s:%7.3f de=%4d dl=%4d aq=%5dKB vq=%5dKB sq=%5dB f=%f /%f   \r",
				pVideoState->get_master_clock()->get_time(),
				pVideoState->get_rate(),
				pVideoState->get_pVidclk()->get_time(),
				(pVideoState->get_audio_st() && pVideoState->get_video_st()) ? "A-V" : (pVideoState->get_video_st() ? "M-V" : (pVideoState->get_audio_st() ? "M-A" : "   ")),
				av_diff,
				pVideoState->get_frame_drops_early(),
				frame_drops_late,
				aqsize / 1024,
				vqsize / 1024,
				sqsize,
				pVideoState->get_video_st() ? pVideoState->get_pViddec()->get_avctx()->pts_correction_num_faulty_dts : 0,
				pVideoState->get_video_st() ? pVideoState->get_pViddec()->get_avctx()->pts_correction_num_faulty_pts : 0
			);
			fflush(stdout);
			last_time = cur_time;
		}
	}
}

void FfmpegSdlAvPlayback::InitSdl() {

	if (pVideoState->get_image_width()) {
		FfmpegSdlAvPlayback::set_default_window_size(
			pVideoState->get_image_width(),
			pVideoState->get_image_height(),
			pVideoState->get_image_sample_aspect_ratio());
	}

	if (display_disable) {
		pVideoState->set_video_disable(1);
	}
	int flags = SDL_INIT_VIDEO | SDL_INIT_AUDIO | SDL_INIT_TIMER;
	if (pVideoState->get_audio_disable())
		flags &= ~SDL_INIT_AUDIO;
	else {
		/* Try to work around an occasional ALSA buffer underflow issue when the
		* period size is NPOT due to ALSA resampling by forcing the buffer size. */
		if (!SDL_getenv("SDL_AUDIO_ALSA_SET_BUFFER_SIZE"))
			SDL_setenv("SDL_AUDIO_ALSA_SET_BUFFER_SIZE", "1", 1);
	}
	if (display_disable)
		flags &= ~SDL_INIT_VIDEO;
	if (SDL_Init(flags)) {
		av_log(NULL, AV_LOG_FATAL, "Could not initialize SDL - %s\n", SDL_GetError());
		av_log(NULL, AV_LOG_FATAL, "(Did you set the DISPLAY variable?)\n");
		exit(1);
	}

	SDL_EventState(SDL_SYSWMEVENT, SDL_IGNORE);
	SDL_EventState(SDL_USEREVENT, SDL_IGNORE);

	if (!display_disable) {
		int flags = SDL_WINDOW_HIDDEN;
		if (kWindowResizable) {
			flags |= SDL_WINDOW_RESIZABLE;
		}
		else {
			flags |= SDL_WINDOW_BORDERLESS;
		}
		window = SDL_CreateWindow(kDefaultWindowTitle, SDL_WINDOWPOS_UNDEFINED, SDL_WINDOWPOS_UNDEFINED, kDefaultWidth, kDefaultHeight, flags);
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
			if (renderer)
				SDL_DestroyRenderer(renderer);
			if (window)
				SDL_DestroyWindow(window);
		}
	}
}

void FfmpegSdlAvPlayback::destroy() {

	stop_display_loop();

	if (audio_dev) {
		closeAudioDevice();
	}

	delete pVideoState;

	// Cleanup textures
	if (vis_texture) {
		SDL_DestroyTexture(vis_texture);
	}

	if (vid_texture) {
		SDL_DestroyTexture(vid_texture);
	}

	// Cleanup resampling
	sws_freeContext(img_convert_ctx);

	// Cleanup SDL components
	if (renderer) {
		SDL_DestroyRenderer(renderer);
	}

	if (window) {
		SDL_DestroyWindow(window);
	}

	avformat_network_deinit();

	SDL_Quit();
	
	av_log(NULL, AV_LOG_QUIET, "%s", "");
}

void FfmpegSdlAvPlayback::init_and_event_loop() {
	SDL_Event event;
	double incr, pos, frac, rate;
	// Initialize first before starting the stream
	InitSdl();
	pVideoState->stream_start();
	rate = 1;

	if (pVideoState->get_image_width()) {
		FfmpegSdlAvPlayback::set_default_window_size(
			pVideoState->get_image_width(),
			pVideoState->get_image_height(),
			pVideoState->get_image_sample_aspect_ratio());
	}

	for (;;) {
		double x;
		refresh_loop_wait_event(&event);
		switch (event.type) {
		case SDL_KEYDOWN:
			switch (event.key.keysym.sym) {
			case SDLK_ESCAPE:
			case SDLK_q:
				destroy();
				exit(0); // need to exit here to avoid joinable exception
				break;
			case SDLK_f:
				toggle_full_screen();
				force_refresh = 1;
				break;
			case SDLK_KP_8:
				play();
				break;
			case SDLK_KP_5:
				stop();
				break;
			case SDLK_KP_2:
				toggle_pause();
				break;
			case SDLK_p:
			case SDLK_SPACE:
				toggle_pause();
				break;
			case SDLK_m:
				pVideoState->toggle_mute();
				break;
			case SDLK_KP_MULTIPLY:
			case SDLK_0:
				update_volume(1, kVolumeStepInDecibel);
				break;
			case SDLK_KP_DIVIDE:
			case SDLK_9:
				update_volume(-1, kVolumeStepInDecibel);
				break;
			case SDLK_s: // S: Step to next frame
				step_to_next_frame();
				break;
			case SDLK_KP_PLUS:
				if (pVideoState->set_rate(rate * 2)) {
					av_log(NULL, AV_LOG_ERROR, "Rate %f unavailable\n", rate * 2);
				} else {
					rate *= 2;
				}
				break;
			case SDLK_KP_MINUS:
				if (pVideoState->set_rate(rate / 2)) {
					av_log(NULL, AV_LOG_ERROR, "Rate %f unavailable\n", rate / 2);
				} else {
					rate /= 2;
				}
				break;
			case SDLK_LEFT:
				incr = -1.0;
				goto do_seek;
			case SDLK_RIGHT:
				incr = 1.0;
				goto do_seek;
			case SDLK_UP:
				incr = 5.0;
				goto do_seek;
			case SDLK_DOWN:
				incr = -5.0;
			do_seek:
				//TODO FIX SEEK BY BYTES BUG
				if (VideoState::kEnableSeekByBytes) {
					pos = -1;
					if (pos < 0 && pVideoState->get_video_stream() >= 0)
						pos = pVideoState->get_pPictq()->last_pos();
					if (pos < 0 && pVideoState->get_audio_stream() >= 0)
						pos = pVideoState->get_pSampq()->last_pos();
					if (pos < 0)
						pos = avio_tell(pVideoState->get_ic()->pb);
					if (pVideoState->get_ic()->bit_rate)
						incr *= pVideoState->get_ic()->bit_rate / 8.0;
					else
						incr *= 180000.0;
					pos += incr;
					pVideoState->stream_seek(pos, incr, 1);
				}
				else {
					pos = pVideoState->get_master_clock()->get_time();
					if (isnan(pos)) {
						pos = (double)pVideoState->get_seek_pos() / AV_TIME_BASE;
					}
					pos += incr;
					if (pVideoState->get_ic()->start_time != AV_NOPTS_VALUE && pos < pVideoState->get_ic()->start_time / (double)AV_TIME_BASE)
						pos = pVideoState->get_ic()->start_time / (double)AV_TIME_BASE;
					pVideoState->stream_seek((int64_t)(pos * AV_TIME_BASE), (int64_t)(incr * AV_TIME_BASE), 0);
				}
				break;
			default:
				break;
			}
			break;
		case SDL_MOUSEBUTTONDOWN:
			if (event.button.button == SDL_BUTTON_LEFT) {
				static int64_t last_mouse_left_click = 0;
				if (av_gettime_relative() - last_mouse_left_click <= 500000) {
					toggle_full_screen();
					force_refresh = 1;
					last_mouse_left_click = 0;
				}
				else {
					last_mouse_left_click = av_gettime_relative();
				}
			}
		case SDL_MOUSEMOTION:
			if (cursor_hidden) {
				SDL_ShowCursor(1);
				cursor_hidden = 0;
			}
			cursor_last_shown = av_gettime_relative();
			if (event.type == SDL_MOUSEBUTTONDOWN) {
				if (event.button.button != SDL_BUTTON_RIGHT)
					break;
				x = event.button.x;
			}
			else {
				if (!(event.motion.state & SDL_BUTTON_RMASK))
					break;
				x = event.motion.x;
			}
			if (VideoState::kEnableSeekByBytes || pVideoState->get_ic()->duration <= 0) {
				uint64_t size = avio_size(pVideoState->get_ic()->pb);
				pVideoState->stream_seek(size*x / width, 0, 1);
			}
			else {
				int64_t ts;
				int ns, hh, mm, ss;
				int tns, thh, tmm, tss;
				tns = pVideoState->get_ic()->duration / 1000000LL;
				thh = tns / 3600;
				tmm = (tns % 3600) / 60;
				tss = (tns % 60);
				frac = x / width;
				ns = frac * tns;
				hh = ns / 3600;
				mm = (ns % 3600) / 60;
				ss = (ns % 60);
				av_log(NULL, AV_LOG_INFO,
					"Seek to %2.0f%% (%2d:%02d:%02d) of total duration (%2d:%02d:%02d)       \n", frac * 100,
					hh, mm, ss, thh, tmm, tss);
				ts = frac * pVideoState->get_ic()->duration;
				if (pVideoState->get_ic()->start_time != AV_NOPTS_VALUE)
					ts += pVideoState->get_ic()->start_time;
				pVideoState->stream_seek(ts, 0, 0);
			}
			break;
		case SDL_WINDOWEVENT:
			switch (event.window.event) {
			case SDL_WINDOWEVENT_RESIZED:
				screen_width = width = event.window.data1;
				screen_height = height = event.window.data2;
				if (vis_texture) {
					SDL_DestroyTexture(vis_texture);
					vis_texture = NULL;
				}
			case SDL_WINDOWEVENT_EXPOSED:
				force_refresh = 1;
			}
			break;
		case SDL_QUIT:
		case FF_QUIT_EVENT:
			destroy();
			exit(0);  // need to exit here to avoid joinable exception
			break;
		default:
			break;
		}
	}
}

int FfmpegSdlAvPlayback::init_and_start_display_loop() {
	std::mutex mtx;
	std::condition_variable cv;
	bool initialized = false;

	// TODO(fraudies): Check for the case when the thread can't be initialized and return appropriate error (change method signature)
	display_tid = new (std::nothrow) std::thread([this, &initialized, &cv] {

		InitSdl();
		initialized = true;
		cv.notify_all();

		SDL_Event event;
		while (!stopped) {
			refresh_loop_wait_event(&event);
			// Add handling of resizing the window
			switch (event.type) {
			case SDL_WINDOWEVENT:
				switch (event.window.event) {
				case SDL_WINDOWEVENT_RESIZED:
					screen_width = width = event.window.data1;
					screen_height = height = event.window.data2;
					if (vis_texture) {
						SDL_DestroyTexture(vis_texture);
						vis_texture = NULL;
					}
				case SDL_WINDOWEVENT_EXPOSED:
					force_refresh = 1;
				}
				break;
			default:
				break;
			}
		}
	});

	if (!display_tid) {
		av_log(NULL, AV_LOG_ERROR, "Unable to create playback thread");
		return -1;
	}

	std::unique_lock<std::mutex> lck(mtx);
	cv.wait(lck, [&initialized] {return initialized; });
	int err = ffmpegToJavaErrNo(pVideoState->stream_start());
	if (err) return err;

	if (pVideoState->get_image_width()) {
		FfmpegSdlAvPlayback::set_default_window_size(
			pVideoState->get_image_width(),
			pVideoState->get_image_height(),
			pVideoState->get_image_sample_aspect_ratio());
	}

	return 0;
}

void FfmpegSdlAvPlayback::stop_display_loop() {
	stopped = true;
}
