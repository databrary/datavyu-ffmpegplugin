#include "SDLPlayData.h"

/* Private Members */
void SDLPlayData::update_video_pts(VideoState *is, double pts, int64_t pos, int serial) {
	/* update current video pts */
	is->get_pVidclk()->set_clock(pts, serial);
	Clock::sync_clock_to_slave(is->get_pExtclk(), is->get_pVidclk());
}

inline int SDLPlayData::compute_mod(int a, int b) {
	return a < 0 ? a % b + b : a % b;
}

inline void SDLPlayData::fill_rectangle(int x, int y, int w, int h) {
	SDL_Rect rect;
	rect.x = x;
	rect.y = y;
	rect.w = w;
	rect.h = h;
	if (w && h)
		SDL_RenderFillRect(renderer, &rect);
}

void SDLPlayData::calculate_display_rect(SDL_Rect *rect,
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

double SDLPlayData::vp_duration(Frame *vp, Frame *nextvp, double max_frame_duration) {
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

/* Constructor */
SDLPlayData::SDLPlayData(const char *filename, AVInputFormat *iformat) :
	ytop(0),
	xleft(0),
	width(0),
	height(0),
	rdftspeed(0.02) {
	pVideoState = VideoState::stream_open(filename, iformat);
	event_loop(pVideoState);
}

/* Destructor */
SDLPlayData::~SDLPlayData() {
	sws_freeContext(this->img_convert_ctx);
	sws_freeContext(this->sub_convert_ctx);
};

/* Public Members */
int SDLPlayData::audio_open(VideoState* is, int64_t wanted_channel_layout, int wanted_nb_channels, int wanted_sample_rate,
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
	wanted_spec.samples = FFMAX(SDL_AUDIO_MIN_BUFFER_SIZE, 2 << av_log2(wanted_spec.freq / SDL_AUDIO_MAX_CALLBACKS_PER_SEC));
	wanted_spec.callback = sdl_audio_callback_bridge;
	wanted_spec.userdata = is; // need to add the object instance here
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

int SDLPlayData::video_open(VideoState *is) {
	int w, h;

	if (screen_width) {
		w = screen_width;
		h = screen_height;
	}
	else {
		w = default_width;
		h = default_height;
	}

	if (!window_title)
		window_title = input_filename;
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

void SDLPlayData::set_force_refresh(int refresh) {
	force_refresh = refresh;
}

int SDLPlayData::get_frame_timer() {
	return frame_timer;
}

void SDLPlayData::set_frame_timer(int newFrame_timer) {
	frame_timer = newFrame_timer;
}

void SDLPlayData::destroyTextures() {
	if (vis_texture)
		SDL_DestroyTexture(vis_texture);
	if (vid_texture)
		SDL_DestroyTexture(vid_texture);
	if (sub_texture)
		SDL_DestroyTexture(sub_texture);
}

void SDLPlayData::set_default_window_size(int width, int height, AVRational sar) {
	SDL_Rect rect;
	calculate_display_rect(&rect, 0, 0, INT_MAX, height, width, height, sar);
	default_width = rect.w;
	default_height = rect.h;
}

void SDLPlayData::closeAudioDevice() { 
	SDL_CloseAudioDevice(audio_dev); 
}

void SDLPlayData::pauseAudioDevice() { 
	SDL_PauseAudioDevice(audio_dev, 0); 
}

VideoState* SDLPlayData::get_VideoState() { return pVideoState; }

void SDLPlayData::toggle_full_screen() {
	is_full_screen = !is_full_screen;
	SDL_SetWindowFullscreen(window, is_full_screen ? SDL_WINDOW_FULLSCREEN_DESKTOP : 0);
}

int SDLPlayData::upload_texture(SDL_Texture **tex, AVFrame *frame, struct SwsContext **img_convert_ctx) {
	int ret = 0;
	Uint32 sdl_pix_fmt;
	SDL_BlendMode sdl_blendmode;
	get_sdl_pix_fmt_and_blendmode(frame->format, &sdl_pix_fmt, &sdl_blendmode);
	if (SDLPlayData::realloc_texture(tex, sdl_pix_fmt == SDL_PIXELFORMAT_UNKNOWN ? SDL_PIXELFORMAT_ARGB8888 : sdl_pix_fmt, frame->width, frame->height, sdl_blendmode, 0) < 0)
		return -1;
	switch (sdl_pix_fmt) {
	case SDL_PIXELFORMAT_UNKNOWN:
		/* This should only happen if we are not using avfilter... */
		*img_convert_ctx = sws_getCachedContext(*img_convert_ctx,
			frame->width, frame->height, static_cast<AVPixelFormat>(frame->format), frame->width, frame->height,
			AV_PIX_FMT_BGRA, sws_flags, NULL, NULL, NULL);
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

void SDLPlayData::get_sdl_pix_fmt_and_blendmode(int format, Uint32 *sdl_pix_fmt, SDL_BlendMode *sdl_blendmode) {
	int i;
	*sdl_blendmode = SDL_BLENDMODE_NONE;
	*sdl_pix_fmt = SDL_PIXELFORMAT_UNKNOWN;
	if (format == AV_PIX_FMT_RGB32 ||
		format == AV_PIX_FMT_RGB32_1 ||
		format == AV_PIX_FMT_BGR32 ||
		format == AV_PIX_FMT_BGR32_1)
		*sdl_blendmode = SDL_BLENDMODE_BLEND;
	for (i = 0; i < FF_ARRAY_ELEMS(sdl_texture_format_map) - 1; i++) {
		if (format == sdl_texture_format_map[i].format) {
			*sdl_pix_fmt = sdl_texture_format_map[i].texture_fmt;
			return;
		}
	}
}

int SDLPlayData::realloc_texture(SDL_Texture **texture, Uint32 new_format, int new_width, int new_height, SDL_BlendMode blendmode, int init_texture) {
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

void SDLPlayData::video_display(VideoState *is) {
	if (!width)
		video_open(is);

	SDL_SetRenderDrawColor(renderer, 0, 0, 0, 255);
	SDL_RenderClear(renderer);
	if (is->get_audio_st() && is->get_show_mode() != SHOW_MODE_VIDEO)
		this->video_audio_display(is);
	else if (is->get_video_st())
		this->video_image_display(is);
	SDL_RenderPresent(renderer);
}

void SDLPlayData::video_image_display(VideoState *is) {
	Frame *vp;
	Frame *sp = NULL;
	SDL_Rect rect;

	vp = is->get_pPictq()->peek_last();
	if (is->get_subtitle_st()) {
		if (is->get_pSubpq()->nb_remaining() > 0) {
			sp = is->get_pSubpq()->peek();

			if (vp->pts >= sp->pts + ((float)sp->sub.start_display_time / 1000)) {
				if (!sp->uploaded) {
					uint8_t* pixels[4];
					int pitch[4];
					int i;
					if (!sp->width || !sp->height) {
						sp->width = vp->width;
						sp->height = vp->height;
					}
					if (realloc_texture(&sub_texture, SDL_PIXELFORMAT_ARGB8888, sp->width, sp->height, SDL_BLENDMODE_BLEND, 1) < 0)
						return;

					for (i = 0; i < sp->sub.num_rects; i++) {
						AVSubtitleRect *sub_rect = sp->sub.rects[i];

						sub_rect->x = av_clip(sub_rect->x, 0, sp->width);
						sub_rect->y = av_clip(sub_rect->y, 0, sp->height);
						sub_rect->w = av_clip(sub_rect->w, 0, sp->width - sub_rect->x);
						sub_rect->h = av_clip(sub_rect->h, 0, sp->height - sub_rect->y);

						sub_convert_ctx = sws_getCachedContext(sub_convert_ctx,
							sub_rect->w, sub_rect->h, AV_PIX_FMT_PAL8,
							sub_rect->w, sub_rect->h, AV_PIX_FMT_BGRA,
							0, NULL, NULL, NULL);
						if (!sub_convert_ctx) {
							av_log(NULL, AV_LOG_FATAL, "Cannot initialize the conversion context\n");
							return;
						}
						if (!SDL_LockTexture(sub_texture, (SDL_Rect *)sub_rect, (void **)pixels, pitch)) {
							sws_scale(sub_convert_ctx, (const uint8_t * const *)sub_rect->data, sub_rect->linesize,
								0, sub_rect->h, pixels, pitch);
							SDL_UnlockTexture(sub_texture);
						}
					}
					sp->uploaded = 1;
				}
			}
			else
				sp = NULL;
		}
	}

	calculate_display_rect(&rect, this->xleft, this->ytop, this->width, this->height, vp->width, vp->height, vp->sar);

	if (!vp->uploaded) {
		if (upload_texture(&vid_texture, vp->frame, &img_convert_ctx) < 0)
			return;
		vp->uploaded = 1;
		vp->flip_v = vp->frame->linesize[0] < 0;
	}

	SDL_RenderCopyEx(renderer, vid_texture, NULL, &rect, 0, NULL, vp->flip_v ? SDL_FLIP_VERTICAL : SDL_FLIP_NONE);
	if (sp) {
#if USE_ONEPASS_SUBTITLE_RENDER
		SDL_RenderCopy(renderer, sub_texture, NULL, &rect);
#else
		int i;
		double xratio = (double)rect.w / (double)sp->width;
		double yratio = (double)rect.h / (double)sp->height;
		for (i = 0; i < sp->sub.num_rects; i++) {
			SDL_Rect *sub_rect = (SDL_Rect*)sp->sub.rects[i];
			SDL_Rect target = {}
				target.x = rect.x + sub_rect->x * xratio,
				.y = rect.y + sub_rect->y * yratio,
				.w = sub_rect->w * xratio,
				.h = sub_rect->h * yratio
		};
		SDL_RenderCopy(renderer, sub_texture, sub_rect, &target);
		}
#endif
	}	
}

void SDLPlayData::update_sample_display(short *samples, int samples_size) {
	int size, len;

	size = samples_size / sizeof(short);
	while (size > 0) {
		len = SAMPLE_ARRAY_SIZE - sample_array_index;
		if (len > size)
			len = size;
		memcpy(sample_array + sample_array_index, samples, len * sizeof(short));
		samples += len;
		sample_array_index += len;
		if (sample_array_index >= SAMPLE_ARRAY_SIZE)
			sample_array_index = 0;
		size -= len;
	}
}

void SDLPlayData::video_audio_display(VideoState *is) {
	int i, i_start, x, y1, y, ys, delay, n, nb_display_channels;
	int ch, channels, h, h2;
	int64_t time_diff;
	int rdft_bits, nb_freq;

	for (rdft_bits = 1; (1 << rdft_bits) < 2 * height; rdft_bits++)
		;
	nb_freq = 1 << (rdft_bits - 1);

	/* compute display index : center on currently output samples */
	channels = is->get_audio_tgt().channels;
	nb_display_channels = channels;
	if (!is->isPaused()) {
		int data_used = is->get_show_mode() == SHOW_MODE_WAVES ? width : (2 * nb_freq);
		n = 2 * channels;
		delay = is->get_audio_write_buf_size();
		delay /= n;

		/* to be more precise, we take into account the time spent since
		the last buffer computation */
		if (audio_callback_time) {
			time_diff = av_gettime_relative() - audio_callback_time;
			delay -= (time_diff * is->get_audio_tgt().freq) / 1000000;
		}

		delay += 2 * data_used;
		if (delay < data_used)
			delay = data_used;

		i_start = x = compute_mod(sample_array_index - delay * channels, SAMPLE_ARRAY_SIZE);
		if (is->get_show_mode() == SHOW_MODE_WAVES) {
			h = INT_MIN;
			for (i = 0; i < 1000; i += channels) {
				int idx = (SAMPLE_ARRAY_SIZE + x - i) % SAMPLE_ARRAY_SIZE;
				int a = sample_array[idx];
				int b = sample_array[(idx + 4 * channels) % SAMPLE_ARRAY_SIZE];
				int c = sample_array[(idx + 5 * channels) % SAMPLE_ARRAY_SIZE];
				int d = sample_array[(idx + 9 * channels) % SAMPLE_ARRAY_SIZE];
				int score = a - d;
				if (h < score && (b ^ c) < 0) {
					h = score;
					i_start = idx;
				}
			}
		}

		last_i_start = i_start;
	}
	else {
		i_start = last_i_start;
	}

	if (is->get_show_mode() == SHOW_MODE_WAVES) {
		SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

		/* total height for one channel */
		h = height / nb_display_channels;
		/* graph height / 2 */
		h2 = (h * 9) / 20;
		for (ch = 0; ch < nb_display_channels; ch++) {
			i = i_start + ch;
			y1 = this->ytop + ch * h + (h / 2); /* position of center line */
			for (x = 0; x < width; x++) {
				y = (sample_array[i] * h2) >> 15;
				if (y < 0) {
					y = -y;
					ys = y1 - y;
				}
				else {
					ys = y1;
				}
				fill_rectangle(this->xleft + x, ys, 1, y);
				i += channels;
				if (i >= SAMPLE_ARRAY_SIZE)
					i -= SAMPLE_ARRAY_SIZE;
			}
		}

		SDL_SetRenderDrawColor(renderer, 0, 0, 255, 255);

		for (ch = 1; ch < nb_display_channels; ch++) {
			y = this->ytop + ch * h;
			fill_rectangle(this->xleft, y, width, 1);
		}
	}
	else {
		if (realloc_texture(&vis_texture, SDL_PIXELFORMAT_ARGB8888, width, height, SDL_BLENDMODE_NONE, 1) < 0)
			return;

		nb_display_channels = FFMIN(nb_display_channels, 2);
		if (rdft_bits != is->get_rdft_bits()) {
			av_rdft_end(is->get_rdft());
			av_free(is->get_rdft_data());
			is->set_rdft(av_rdft_init(rdft_bits, DFT_R2C));
			is->set_rdft_bits(rdft_bits);
			is->set_rdft_data((FFTSample*)av_malloc_array(nb_freq, 4 * sizeof(*is->get_rdft_data())));
		}
		if (!is->get_rdft() || !is->get_rdft_data()) {
			av_log(NULL, AV_LOG_ERROR, "Failed to allocate buffers for RDFT, switching to waves display\n");
			show_mode = SHOW_MODE_WAVES;
		}
		else {
			FFTSample *data[2];
			SDL_Rect rect = {}; // {.x = s->xpos, .y = 0, .w = 1, .h = s->height };
			rect.x = xpos;
			rect.y = 0;
			rect.w = 1;
			rect.h = height;
			uint32_t *pixels;
			int pitch;
			for (ch = 0; ch < nb_display_channels; ch++) {
				data[ch] = is->get_rdft_data() + 2 * nb_freq * ch;
				i = i_start + ch;
				for (x = 0; x < 2 * nb_freq; x++) {
					double w = (x - nb_freq) * (1.0 / nb_freq);
					data[ch][x] = sample_array[i] * (1.0 - w * w);
					i += channels;
					if (i >= SAMPLE_ARRAY_SIZE)
						i -= SAMPLE_ARRAY_SIZE;
				}
				av_rdft_calc(is->get_rdft(), data[ch]);
			}
			/* Least efficient way to do this, we should of course
			* directly access it but it is more than fast enough. */
			if (!SDL_LockTexture(vis_texture, &rect, (void **)&pixels, &pitch)) {
				pitch >>= 2;
				pixels += pitch * height;
				for (y = 0; y < height; y++) {
					double w = 1 / sqrt(nb_freq);
					int a = sqrt(w * sqrt(data[0][2 * y + 0] * data[0][2 * y + 0] + data[0][2 * y + 1] * data[0][2 * y + 1]));
					int b = (nb_display_channels == 2) ? sqrt(w * hypot(data[1][2 * y + 0], data[1][2 * y + 1]))
						: a;
					a = FFMIN(a, 255);
					b = FFMIN(b, 255);
					pixels -= pitch;
					*pixels = (a << 16) + (b << 8) + ((a + b) >> 1);
				}
				SDL_UnlockTexture(vis_texture);
			}
			SDL_RenderCopy(renderer, vis_texture, NULL, NULL);
		}
		if (!is->isPaused())
			xpos++;
		if (xpos >= width)
			xpos = this->xleft;
	}
}

void SDLPlayData::refresh_loop_wait_event(VideoState *is, SDL_Event *event) {
	double remaining_time = 0.0;
	SDL_PumpEvents();
	while (!SDL_PeepEvents(event, 1, SDL_GETEVENT, SDL_FIRSTEVENT, SDL_LASTEVENT)) {
		if (!cursor_hidden && av_gettime_relative() - cursor_last_shown > CURSOR_HIDE_DELAY) {
			SDL_ShowCursor(0);
			cursor_hidden = 1;
		}
		if (remaining_time > 0.0)
			av_usleep((int64_t)(remaining_time * 1000000.0));
		remaining_time = REFRESH_RATE;
		if (is->get_show_mode() != SHOW_MODE_NONE && (!is->isPaused() || force_refresh))
			video_refresh(is, &remaining_time);
		SDL_PumpEvents();
	}
}

void SDLPlayData::video_refresh(VideoState *is, double *remaining_time) {
	double time;

	Frame *sp, *sp2;

	if (!is->isPaused() && is->get_master_sync_type() == AV_SYNC_EXTERNAL_CLOCK && is->get_realtime())
		is->check_external_clock_speed();

	if (!display_disable && is->get_show_mode() != SHOW_MODE_VIDEO && is->get_audio_st()) {
		time = av_gettime_relative() / 1000000.0;
		if (force_refresh || last_vis_time + rdftspeed < time) {
			video_display(is);
			last_vis_time = time;
		}
		*remaining_time = FFMIN(*remaining_time, last_vis_time + rdftspeed - time);
	}

	if (is->get_video_st()) {
	retry:
		if (is->get_pPictq()->nb_remaining() == 0) {
			// nothing to do, no picture to display in the queue
		}
		else {
			double last_duration, duration, delay;
			Frame *vp, *lastvp;

			/* dequeue the picture */
			lastvp = is->get_pPictq()->peek_last();
			vp = is->get_pPictq()->peek();

			if (vp->serial != is->get_pVideoq()->get_serial()) {
				is->get_pPictq()->next();
				goto retry;
			}

			if (lastvp->serial != vp->serial)
				frame_timer = av_gettime_relative() / 1000000.0;

			if (is->isPaused())
				goto display;

			/* compute nominal last_duration */
			last_duration = vp_duration(lastvp, vp, is->get_max_frame_duration());
			delay = is->compute_target_delay(last_duration);

			time = av_gettime_relative() / 1000000.0;
			if (time < frame_timer + delay) {
				*remaining_time = FFMIN(frame_timer + delay - time, *remaining_time);
				goto display;
			}

			frame_timer += delay;
			if (delay > 0 && time - frame_timer > AV_SYNC_THRESHOLD_MAX)
				frame_timer = time;

			// Replaced SDL_LockMutex(pPictq.mutex)by the following
			std::unique_lock<std::mutex> locker(is->get_pPictq()->get_mutex());
			if (!isnan(vp->pts))
				update_video_pts(is, vp->pts, vp->pos, vp->serial);
			// Replaced SDL_UnlockMutex(this->pPictq.mutex) by
			locker.unlock();

			if (is->get_pPictq()->nb_remaining() > 1) {
				Frame *nextvp = is->get_pPictq()->peek_next();
				duration = vp_duration(vp, nextvp, is->get_max_frame_duration());
				if (!is->get_step() && (framedrop>0 || (framedrop && is->get_master_sync_type() != AV_SYNC_VIDEO_MASTER)) && time > frame_timer + duration) {
					frame_drops_late++;
					is->get_pPictq()->next();
					goto retry;
				}
			}

			if (is->get_subtitle_st()) {
				while (is->get_pSubpq()->nb_remaining() > 0) {
					sp = is->get_pSubpq()->peek();

					if (is->get_pSubpq()->nb_remaining() > 1)
						sp2 = is->get_pSubpq()->peek_next();
					else
						sp2 = NULL;

					if (sp->serial != is->get_pSubtitleq()->get_serial()
						|| (is->get_pVidclk()->get_pts() > (sp->pts + ((float)sp->sub.end_display_time / 1000)))
						|| (sp2 && is->get_pVidclk()->get_pts() > (sp2->pts + ((float)sp2->sub.start_display_time / 1000))))
					{
						if (sp->uploaded) {
							int i;
							for (i = 0; i < sp->sub.num_rects; i++) {
								AVSubtitleRect *sub_rect = sp->sub.rects[i];
								uint8_t *pixels;
								int pitch, j;

								//TODO: Review this
								if (!SDL_LockTexture(sub_texture, (SDL_Rect *)sub_rect, (void **)&pixels, &pitch)) {
									for (j = 0; j < sub_rect->h; j++, pixels += pitch)
										memset(pixels, 0, sub_rect->w << 2);
									SDL_UnlockTexture(sub_texture);
								}
							}
						}
						is->get_pSubpq()->next();
					}
					else {
						break;
					}
				}
			}

			is->get_pPictq()->next();
			force_refresh = 1;

			if (is->get_step() && !is->isPaused())
				is->stream_toggle_pause();
		}
	display:
		/* display picture */
		if (!display_disable
			&& force_refresh
			&& (is->get_show_mode() == SHOW_MODE_VIDEO)
			&& is->get_pPictq()->get_rindex_shown())
			video_display(is);
	}
	force_refresh = 0;
	if (show_status) {
		static int64_t last_time;
		int64_t cur_time;
		int aqsize, vqsize, sqsize;
		double av_diff;

		cur_time = av_gettime_relative();
		if (!last_time || (cur_time - last_time) >= 30000) {
			aqsize = 0;
			vqsize = 0;
			sqsize = 0;
			if (is->get_audio_st())
				aqsize = is->get_pAudioq()->get_size();
			if (is->get_video_st())
				vqsize = is->get_pVideoq()->get_size();
			if (is->get_subtitle_st())
				sqsize = is->get_pSubtitleq()->get_size();
			av_diff = 0;
			if (is->get_audio_st() && is->get_video_st())
				av_diff = is->get_pAudclk()->get_clock() - is->get_pVidclk()->get_clock();
			else if (is->get_video_st())
				av_diff = is->get_master_clock() - is->get_pVidclk()->get_clock();
			else if (is->get_audio_st())
				av_diff = is->get_master_clock() - is->get_pAudclk()->get_clock();
			av_log(NULL, AV_LOG_INFO,
				"%7.2f %s:%7.3f fd=%4d aq=%5dKB vq=%5dKB sq=%5dB f=%f /%f   \r",
				is->get_master_clock(),
				(is->get_audio_st() && is->get_video_st()) ? "A-V" : (is->get_video_st() ? "M-V" : (is->get_audio_st() ? "M-A" : "   ")),
				av_diff,
				is->get_frame_drops_early() + frame_drops_late,
				aqsize / 1024,
				vqsize / 1024,
				sqsize,
				is->get_video_st() ? is->get_pViddec()->get_avctx()->pts_correction_num_faulty_dts : 0,
				is->get_video_st() ? is->get_pViddec()->get_avctx()->pts_correction_num_faulty_pts : 0);
			fflush(stdout);
			last_time = cur_time;
		}
	}
}

void SDLPlayData::do_exit(VideoState* is) {
	// close the VideoState Stream and and destroy SDL window
	if (is) {
		//Clean-up memory  
		is->stream_close();
	}
	if (renderer)
		SDL_DestroyRenderer(renderer);
	if (window)
		SDL_DestroyWindow(window);
	//uninit_opts();
#if CONFIG_AVFILTER
	av_freep(&vfilters_list);
#endif
	avformat_network_deinit();
	if (show_status)
		printf("\n");
	SDL_Quit();
	av_log(NULL, AV_LOG_QUIET, "%s", "");
	exit(0);
}

void SDLPlayData::event_loop(VideoState *is) {
	// SDL: The event loop for the SDL window
	SDL_Event event;
	double incr, pos, frac;

	for (;;) {
		double x;
		refresh_loop_wait_event(is, &event);
		switch (event.type) {
		case SDL_KEYDOWN:
			if (exit_on_keydown) {
				SDLPlayData::do_exit(is);
				break;
			}
			switch (event.key.keysym.sym) {
			case SDLK_ESCAPE:
			case SDLK_q:
				SDLPlayData::do_exit(is);
				break;
			case SDLK_f:
				toggle_full_screen();
				force_refresh = 1;
				break;
			case SDLK_p:
			case SDLK_SPACE:
				is->toggle_pause();
				break;
			case SDLK_m:
				is->toggle_mute();
				break;
			case SDLK_KP_MULTIPLY:
			case SDLK_0:
				is->update_volume(1, SDL_VOLUME_STEP);
				break;
			case SDLK_KP_DIVIDE:
			case SDLK_9:
				is->update_volume(-1, SDL_VOLUME_STEP);
				break;
			case SDLK_s: // S: Step to next frame
				is->step_to_next_frame();
				break;
			case SDLK_a:
				is->stream_cycle_channel(AVMEDIA_TYPE_AUDIO);
				break;
			case SDLK_v:
				is->stream_cycle_channel(AVMEDIA_TYPE_VIDEO);
				break;
			case SDLK_c:
				is->stream_cycle_channel(AVMEDIA_TYPE_VIDEO);
				is->stream_cycle_channel(AVMEDIA_TYPE_AUDIO);
				is->stream_cycle_channel(AVMEDIA_TYPE_SUBTITLE);
				break;
			case SDLK_t:
				is->stream_cycle_channel(AVMEDIA_TYPE_SUBTITLE);
				break;
			case SDLK_w:
#if CONFIG_AVFILTER
				if (is->get_show_mode() == SHOW_MODE_VIDEO && is->->vfilter_idx < nb_vfilters - 1) {
					if (++is->->vfilter_idx >= nb_vfilters)
						is->->vfilter_idx = 0;
				}
				else {
					is->->vfilter_idx = 0;
					is->toggle_audio_display();
				}
#else
				is->toggle_audio_display();
#endif
				break;
			case SDLK_PAGEUP:
				if (is->get_ic()->nb_chapters <= 1) {
					incr = 600.0;
					goto do_seek;
				}
				is->seek_chapter(1);
				break;
			case SDLK_PAGEDOWN:
				if (is->get_ic()->nb_chapters <= 1) {
					incr = -600.0;
					goto do_seek;
				}
				is->seek_chapter(-1);
				break;
			case SDLK_LEFT:
				incr = -10.0;
				goto do_seek;
			case SDLK_RIGHT:
				incr = 10.0;
				goto do_seek;
			case SDLK_UP:
				incr = 60.0;
				goto do_seek;
			case SDLK_DOWN:
				incr = -60.0;
			do_seek:
				//TODO FIX SEEK BY BYTES BUG
				//if (seek_by_bytes) {
				//	pos = -1;
				//	if (pos < 0 && is->get_video_stream() >= 0)
				//		pos = is->get_pPictq()->last_pos();
				//	if (pos < 0 && is->get_audio_stream() >= 0)
				//		pos = is->get_pSampq()->last_pos();
				//	if (pos < 0)
				//		pos = avio_tell(is->get_ic()->pb);
				//	if (is->get_ic()->bit_rate)
				//		incr *= is->get_ic()->bit_rate / 8.0;
				//	else
				//		incr *= 180000.0;
				//	pos += incr;
				//	is->stream_seek(pos, incr, 1);
				//}
				//else {
					pos = is->get_master_clock();
					if (isnan(pos))
						pos = (double)is->get_seek_pos() / AV_TIME_BASE;
					pos += incr;
					if (is->get_ic()->start_time != AV_NOPTS_VALUE && pos < is->get_ic()->start_time / (double)AV_TIME_BASE)
						pos = is->get_ic()->start_time / (double)AV_TIME_BASE;
					is->stream_seek((int64_t)(pos * AV_TIME_BASE), (int64_t)(incr * AV_TIME_BASE), 0);
				//}
				break;
			default:
				break;
			}
			break;
		case SDL_MOUSEBUTTONDOWN:
			if (exit_on_mousedown) {
				SDLPlayData::do_exit(is);
				break;
			}
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
			if (seek_by_bytes || is->get_ic()->duration <= 0) {
				uint64_t size = avio_size(is->get_ic()->pb);
				is->stream_seek(size*x / width, 0, 1);
			}
			else {
				int64_t ts;
				int ns, hh, mm, ss;
				int tns, thh, tmm, tss;
				tns = is->get_ic()->duration / 1000000LL;
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
				ts = frac * is->get_ic()->duration;
				if (is->get_ic()->start_time != AV_NOPTS_VALUE)
					ts += is->get_ic()->start_time;
				is->stream_seek(ts, 0, 0);
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
			SDLPlayData::do_exit(is);
			break;
		default:
			break;
		}
	}
}

int main(int argc, char **argv) {
	int flags;

	av_log_set_flags(AV_LOG_SKIP_REPEATED);

	/* register all codecs, demux and protocols */
#if CONFIG_AVDEVICE
	avdevice_register_all();
#endif
	avformat_network_init();
	av_log(NULL, AV_LOG_WARNING, "Init Network\n");

	input_filename = "counter.mp4";

	if (!input_filename) {
		//show_usage();
		av_log(NULL, AV_LOG_FATAL, "An input file must be specified\n");
		av_log(NULL, AV_LOG_FATAL,
			"Use -h to get full help or, even better, run 'man %s'\n", program_name);

		SDL_Delay(20000);
		exit(1);
	}

	if (display_disable) {
		video_disable = 1;
	}
	flags = SDL_INIT_VIDEO | SDL_INIT_AUDIO | SDL_INIT_TIMER;
	if (audio_disable)
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
	}

	//VideoState* is = VideoState::stream_open(input_filename, file_iformat);
	SDLPlayData* pPlayer = new SDLPlayData(input_filename, file_iformat);
	if (!pPlayer->get_VideoState()) {
		av_log(NULL, AV_LOG_FATAL, "Failed to initialize VideoState!\n");
		SDLPlayData::do_exit(pPlayer->get_VideoState());
	}

	return 0;
}