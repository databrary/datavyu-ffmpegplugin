#include "VideoState.h"

/* Private Members */
int VideoState::stream_component_open(int stream_index) {
	AVFormatContext *ic = this->ic;
	AVCodecContext *avctx;
	AVCodec *codec;
	const char *forced_codec_name = NULL;
	AVDictionary *opts = NULL;
	AVDictionaryEntry *t = NULL;
	int sample_rate, nb_channels;
	int64_t channel_layout;
	int ret = 0;
	int stream_lowres = lowres;

	AVDictionary *codec_opts = NULL;

	if (stream_index < 0 || stream_index >= ic->nb_streams)
		return -1;

	avctx = avcodec_alloc_context3(NULL);
	if (!avctx)
		return AVERROR(ENOMEM);

	ret = avcodec_parameters_to_context(avctx, ic->streams[stream_index]->codecpar);
	if (ret < 0)
		goto fail;
	avctx->pkt_timebase = ic->streams[stream_index]->time_base;

	codec = avcodec_find_decoder(avctx->codec_id);

	switch (avctx->codec_type) {
		case AVMEDIA_TYPE_AUDIO:
			this->last_audio_stream = stream_index;
			forced_codec_name = audio_codec_name;
			break;
		case AVMEDIA_TYPE_SUBTITLE:
			this->last_subtitle_stream = stream_index;
			forced_codec_name = subtitle_codec_name;
			break;
		case AVMEDIA_TYPE_VIDEO:
			this->last_video_stream = stream_index;
			forced_codec_name = video_codec_name;
			break;
	}

	if (forced_codec_name)
		codec = avcodec_find_decoder_by_name(forced_codec_name);
	if (!codec) {
		if (forced_codec_name) av_log(NULL, AV_LOG_WARNING,
			"No codec could be found with name '%s'\n", forced_codec_name);
		else                   av_log(NULL, AV_LOG_WARNING,
			"No decoder could be found for codec %s\n", avcodec_get_name(avctx->codec_id));
		ret = AVERROR(EINVAL);
		goto fail;
	}

	avctx->codec_id = codec->id;
	if (stream_lowres > codec->max_lowres) {
		av_log(avctx, AV_LOG_WARNING, "The maximum value for lowres supported by the decoder is %d\n",
			codec->max_lowres);
		stream_lowres = codec->max_lowres;
	}
	avctx->lowres = stream_lowres;

	if (fast)
		avctx->flags2 |= AV_CODEC_FLAG2_FAST;

	opts = filter_codec_opts(codec_opts, avctx->codec_id, ic, ic->streams[stream_index], codec);
	if (!av_dict_get(opts, "threads", NULL, 0))
		av_dict_set(&opts, "threads", "auto", 0);
	if (stream_lowres)
		av_dict_set_int(&opts, "lowres", stream_lowres, 0);
	if (avctx->codec_type == AVMEDIA_TYPE_VIDEO || avctx->codec_type == AVMEDIA_TYPE_AUDIO)
		av_dict_set(&opts, "refcounted_frames", "1", 0);
	if ((ret = avcodec_open2(avctx, codec, &opts)) < 0) {
		goto fail;
	}
	if ((t = av_dict_get(opts, "", NULL, AV_DICT_IGNORE_SUFFIX))) {
		av_log(NULL, AV_LOG_ERROR, "Option %s not found.\n", t->key);
		ret = AVERROR_OPTION_NOT_FOUND;
		goto fail;
	}

	eof = 0;
	ic->streams[stream_index]->discard = AVDISCARD_DEFAULT;
	switch (avctx->codec_type) {
	case AVMEDIA_TYPE_AUDIO:
#if CONFIG_AUDIO_FILTER
	{
		AVFilterContext *sink;

		audio_filter_src.freq = avctx->sample_rate;
		audio_filter_src.channels = avctx->channels;
		audio_filter_src.channel_layout = get_valid_channel_layout(avctx->channel_layout, avctx->channels);
		audio_filter_src.fmt = avctx->sample_fmt;
		if ((ret = configure_audio_filters(afilters, 0)) < 0) {
			goto fail;
		}
			//av_log(NULL, AV_LOG_INFO, "stream_component_open function cannot configure the audio filter %s",afilters);
		sink = out_audio_filter;
		sample_rate = av_buffersink_get_sample_rate(sink);
		nb_channels = av_buffersink_get_channels(sink);
		channel_layout = av_buffersink_get_channel_layout(sink);
	}
#else
		sample_rate = avctx->sample_rate;
		nb_channels = avctx->channels;
		channel_layout = avctx->channel_layout;
#endif

		/* prepare audio output */
		if (!audio_open_callback)
			goto fail;

		if ((ret = audio_open_callback(channel_layout, nb_channels, sample_rate, &this->audio_tgt) < 0))
			goto fail;
		audio_hw_buf_size = ret;
		audio_src = this->audio_tgt;
		audio_buf_size = 0;
		audio_buf_index = 0;

		/* init averaging filter */
		audio_diff_avg_coef = exp(log(0.01) / AUDIO_DIFF_AVG_NB);
		audio_diff_avg_count = 0;
		/* since we do not have a precise anough audio FIFO fullness,
		we correct audio sync only if larger than this threshold */
		audio_diff_threshold = (double)(audio_hw_buf_size) / audio_tgt.bytes_per_sec;

		audio_stream = stream_index;
		audio_st = ic->streams[stream_index];
		pAuddec = new Decoder(avctx, pAudioq, &continue_read_thread);
		if ((ic->iformat->flags & (AVFMT_NOBINSEARCH | AVFMT_NOGENSEARCH | AVFMT_NO_BYTE_SEEK)) && !ic->iformat->read_seek) {
			pAuddec->set_start_pts(audio_st->start_time);
			pAuddec->set_start_pts_tb(audio_st->time_base);
		}
		//if ((ret = pAuddec->start(audio_thread_bridge, this)) < 0)
		if ((ret = pAuddec->start([this] { this->audio_thread(); })) < 0)
			goto out;
		if (pause_audio_device_callback)
			pause_audio_device_callback();
		break;
	case AVMEDIA_TYPE_VIDEO:
		this->image_width = avctx->width;
		this->image_height = avctx->height;
		this->image_sample_aspect_ratio = avctx->sample_aspect_ratio;

		// TODO(fraudies): Alignment for the source does not seem to be necessary, but test with more res
		// avcodec_align_dimensions(avctx, &avctx->width, &avctx->height);

		this->video_stream = stream_index;
		this->video_st = ic->streams[stream_index];

		// Calculate the Frame rate (FPS) of the video stream
		if (this->video_st) {
			AVRational f = av_guess_frame_rate(ic, video_st, NULL);
			AVRational rational = this->video_st->avg_frame_rate;
			if(rational.den == rational.num == 0)
				rational = this->video_st->r_frame_rate;

			this->fps = rational.num / rational.den;
		}

		pViddec = new Decoder(avctx, pVideoq, &continue_read_thread);
		//if ((ret = pViddec->start(video_thread_bridge, this)) < 0)
		if ((ret = pViddec->start([this] { this->video_thread(); })) < 0)
			goto out;
		queue_attachments_req = 1;
		break;
	case AVMEDIA_TYPE_SUBTITLE:
		subtitle_stream = stream_index;
		subtitle_st = ic->streams[stream_index];
		pSubdec = new Decoder(avctx, pSubtitleq, &continue_read_thread);
		//if ((ret = pSubdec->start(subtitle_thread_bridge, this)) < 0)
		if ((ret = pSubdec->start([this] {this->subtitle_thread(); })) < 0)
			goto out;
		break;
	default:
		break;
	}
	goto out;

fail:
	avcodec_free_context(&avctx);
out:
	av_dict_free(&opts);

	return ret;
}

int VideoState::get_video_frame(AVFrame *frame) {
	int got_picture;

	if ((got_picture = pViddec->decode_frame(frame, NULL)) < 0)
		return -1;

	if (got_picture) {
		double dpts = NAN;

		if (frame->pts != AV_NOPTS_VALUE)
			dpts = av_q2d(video_st->time_base) * frame->pts; // *pts_speed;

		frame->sample_aspect_ratio = av_guess_sample_aspect_ratio(ic, video_st, frame);

		if (framedrop>0 || (framedrop && get_master_sync_type() != AV_SYNC_VIDEO_MASTER)) {
			if (frame->pts != AV_NOPTS_VALUE) {
				double diff = dpts - get_master_clock();
				if (!isnan(diff) && fabs(diff) < AV_NOSYNC_THRESHOLD &&
					diff - frame_last_filter_delay < 0 &&
					pViddec->get_pkt_serial() == pVidclk->get_serial() &&
					pVideoq->get_nb_packets()) {
					frame_drops_early++;
					av_frame_unref(frame);
					got_picture = 0;
				}
			}
		}
	}

	return got_picture;
}

int VideoState::queue_picture(AVFrame *src_frame, double pts, double duration, int64_t pos, int serial) {
	Frame *vp;

#if defined(DEBUG_SYNC)
	printf("frame_type=%c pts=%0.3f\n",
		av_get_picture_type_char(src_frame->pict_type), pts);
#endif

	if (!(vp = pPictq->peek_writable()))
		return -1;

	vp->sar = src_frame->sample_aspect_ratio;
	vp->uploaded = 0;

	vp->width = src_frame->width;
	vp->height = src_frame->height;
	vp->format = src_frame->format;

	vp->pts = pts;
	vp->duration = duration;
	vp->pos = pos;
	vp->serial = serial;

	//FfmpegSdlAvPlayback::set_default_window_size(vp->width, vp->height, vp->sar);

	av_frame_move_ref(vp->frame, src_frame);
	pPictq->push();
	return 0;
}

void VideoState::stream_component_close(int stream_index) {
	AVCodecParameters *codecpar;

	if (stream_index < 0 || stream_index >= ic->nb_streams)
		return;
	codecpar = ic->streams[stream_index]->codecpar;

	switch (codecpar->codec_type) {
	case AVMEDIA_TYPE_AUDIO:
		pAuddec->abort(pSampq);
		//pPlayer->closeAudioDevice(); Moved to destroy in FfmpegSdlAvPlayback
		delete pAuddec;
		swr_free(&swr_ctx);
		av_freep(&audio_buf1);
		audio_buf1_size = 0;
		audio_buf = NULL;

		if (rdft) {
			av_rdft_end(rdft);
			av_freep(&rdft_data);
			rdft = NULL;
			rdft_bits = 0;
		}
		break;
	case AVMEDIA_TYPE_VIDEO:
		pViddec->abort(pPictq);
		delete pViddec;
		break;
	case AVMEDIA_TYPE_SUBTITLE:
		pSubdec->abort(pSubpq);
		delete pSubdec;
		break;
	default:
		break;
	}

	ic->streams[stream_index]->discard = AVDISCARD_ALL;
	switch (codecpar->codec_type) {
	case AVMEDIA_TYPE_AUDIO:
		audio_st = NULL;
		audio_stream = -1;
		break;
	case AVMEDIA_TYPE_VIDEO:
		video_st = NULL;
		video_stream = -1;
		break;
	case AVMEDIA_TYPE_SUBTITLE:
		subtitle_st = NULL;
		subtitle_stream = -1;
		break;
	default:
		break;
	}
}

int VideoState::stream_has_enough_packets(AVStream *st, int stream_id, PacketQueue *queue) {
	return stream_id < 0 ||
		queue->is_abort_request() ||
		(st->disposition & AV_DISPOSITION_ATTACHED_PIC) ||
		queue->get_nb_packets() > MIN_FRAMES && (!queue->get_duration() || av_q2d(st->time_base) * queue->get_duration() > 1.0);
}

/* Ported this function from cmdutils */
int VideoState::check_stream_specifier(AVFormatContext *s, AVStream *st, const char *spec) {
	int ret = avformat_match_stream_specifier(s, st, spec);
	if (ret < 0)
		av_log(s, AV_LOG_ERROR, "Invalid stream specifier: %s.\n", spec);
	return ret;
	return ret;
}

/*ported this function from cmdutils*/
AVDictionary *VideoState::filter_codec_opts(AVDictionary *opts, enum AVCodecID codec_id, AVFormatContext *s,
	AVStream *st, AVCodec *codec) {
	AVDictionary    *ret = NULL;
	AVDictionaryEntry *t = NULL;
	int            flags = s->oformat ? AV_OPT_FLAG_ENCODING_PARAM
		: AV_OPT_FLAG_DECODING_PARAM;
	char          prefix = 0;
	const AVClass    *cc = avcodec_get_class();

	if (!codec)
		codec = s->oformat ? avcodec_find_encoder(codec_id)
		: avcodec_find_decoder(codec_id);

	switch (st->codecpar->codec_type) {
	case AVMEDIA_TYPE_VIDEO:
		prefix = 'v';
		flags |= AV_OPT_FLAG_VIDEO_PARAM;
		break;
	case AVMEDIA_TYPE_AUDIO:
		prefix = 'a';
		flags |= AV_OPT_FLAG_AUDIO_PARAM;
		break;
	case AVMEDIA_TYPE_SUBTITLE:
		prefix = 's';
		flags |= AV_OPT_FLAG_SUBTITLE_PARAM;
		break;
	}

	while (t = av_dict_get(opts, "", t, AV_DICT_IGNORE_SUFFIX)) {
		char *p = strchr(t->key, ':');

		/* check stream specification in opt name */
		if (p)
			switch (check_stream_specifier(s, st, p + 1)) {
			case  1: *p = 0; break;
			case  0:         continue;
			default:         exit(1);
			}

		if (av_opt_find(&cc, t->key, NULL, flags, AV_OPT_SEARCH_FAKE_OBJ) ||
			!codec ||
			(codec->priv_class &&
				av_opt_find(&codec->priv_class, t->key, NULL, flags,
					AV_OPT_SEARCH_FAKE_OBJ)))
			av_dict_set(&ret, t->key, t->value, 0);
		else if (t->key[0] == prefix &&
			av_opt_find(&cc, t->key + 1, NULL, flags,
				AV_OPT_SEARCH_FAKE_OBJ))
			av_dict_set(&ret, t->key + 1, t->value, 0);

		if (p)
			*p = ':';
	}
	return ret;
}

/* From cmd utils*/
AVDictionary **VideoState::setup_find_stream_info_opts(AVFormatContext *s, AVDictionary *codec_opts) {
	int i;
	AVDictionary **opts;

	if (!s->nb_streams)
		return NULL;
	opts = (AVDictionary**)av_mallocz_array(s->nb_streams, sizeof(*opts));
	if (!opts) {
		av_log(NULL, AV_LOG_ERROR,
			"Could not alloc memory for stream options.\n");
		return NULL;
	}
	for (i = 0; i < s->nb_streams; i++)
		opts[i] = filter_codec_opts(codec_opts, s->streams[i]->codecpar->codec_id,
			s, s->streams[i], NULL);
	return opts;
}

int VideoState::is_realtime(AVFormatContext *s) {
	if (!strcmp(s->iformat->name, "rtp")
		|| !strcmp(s->iformat->name, "rtsp")
		|| !strcmp(s->iformat->name, "sdp"))
		return 1;

	if (s->pb && (!strncmp(s->url, "rtp:", 4) || !strncmp(s->url, "udp:", 4)))
		return 1;
	return 0;
}

int VideoState::synchronize_audio(int nb_samples) {
	int wanted_nb_samples = nb_samples;

	/* if not master, then we try to remove or add samples to correct the clock */
	if (get_master_sync_type() != AV_SYNC_AUDIO_MASTER) {
		double diff, avg_diff;
		int min_nb_samples, max_nb_samples;

		diff = pAudclk->get_clock() - get_master_clock();

		if (!isnan(diff) && fabs(diff) < AV_NOSYNC_THRESHOLD) {
			this->audio_diff_cum = diff + this->audio_diff_avg_coef * this->audio_diff_cum;
			if (this->audio_diff_avg_count < AUDIO_DIFF_AVG_NB) {
				/* not enough measures to have a correct estimate */
				this->audio_diff_avg_count++;
			}
			else {
				/* estimate the A-V difference */
				avg_diff = this->audio_diff_cum * (1.0 - this->audio_diff_avg_coef);

				if (fabs(avg_diff) >= this->audio_diff_threshold) {
					wanted_nb_samples = nb_samples + (int)(diff * this->audio_src.freq);
					min_nb_samples = ((nb_samples * (100 - SAMPLE_CORRECTION_PERCENT_MAX) / 100));
					max_nb_samples = ((nb_samples * (100 + SAMPLE_CORRECTION_PERCENT_MAX) / 100));
					wanted_nb_samples = av_clip(wanted_nb_samples, min_nb_samples, max_nb_samples);
				}
				av_log(NULL, AV_LOG_TRACE, "diff=%f adiff=%f sample_diff=%d apts=%0.3f %f\n",
					diff, avg_diff, wanted_nb_samples - nb_samples,
					this->audio_clock, this->audio_diff_threshold);
			}
		}
		else {
			/* too big difference : may be initial PTS errors, so
			reset A-V filter */
			this->audio_diff_avg_count = 0;
			this->audio_diff_cum = 0;
		}
	}
	return wanted_nb_samples;
}

int VideoState::audio_decode_frame() {
	int data_size, resampled_data_size;
	int64_t dec_channel_layout;
	av_unused double audio_clock0;
	int wanted_nb_samples;
	Frame *af;

	if (this->paused)
		return -1;

	do {
#if defined(_WIN32)

		while (pSampq->nb_remaining() == 0) {
			if ((av_gettime_relative() - audio_callback_time) > 1000000LL * this->audio_hw_buf_size / this->audio_tgt.bytes_per_sec / 2)
				return -1;
			av_usleep(1000);
		}
#endif
		if (!(af = pSampq->peek_readable()))
			return -1;
		pSampq->next();
	} while (af->serial != pAudioq->get_serial());


	data_size = av_samples_get_buffer_size(NULL, af->frame->channels,
		af->frame->nb_samples,
		static_cast<AVSampleFormat>(af->frame->format), 1);

	dec_channel_layout =
		(af->frame->channel_layout && af->frame->channels == av_get_channel_layout_nb_channels(af->frame->channel_layout)) ?
		af->frame->channel_layout : av_get_default_channel_layout(af->frame->channels);
	wanted_nb_samples = synchronize_audio(af->frame->nb_samples);

	af->frame->sample_rate /= pts_speed;

	if (af->frame->format != this->audio_src.fmt ||
		dec_channel_layout != this->audio_src.channel_layout ||
		af->frame->sample_rate != this->audio_src.freq ||
		(wanted_nb_samples != af->frame->nb_samples && !this->swr_ctx)) {
		swr_free(&this->swr_ctx);
		this->swr_ctx = swr_alloc_set_opts(NULL,
			this->audio_tgt.channel_layout, this->audio_tgt.fmt, this->audio_tgt.freq,
			dec_channel_layout, static_cast<AVSampleFormat>(af->frame->format), af->frame->sample_rate,
			0, NULL);
		if (!this->swr_ctx || swr_init(this->swr_ctx) < 0) {
			av_log(NULL, AV_LOG_ERROR,
				"Cannot create sample rate converter for conversion of %d Hz %s %d channels to %d Hz %s %d channels!\n",
				af->frame->sample_rate, av_get_sample_fmt_name(static_cast<AVSampleFormat>(af->frame->format)), af->frame->channels,
				this->audio_tgt.freq, av_get_sample_fmt_name(this->audio_tgt.fmt), this->audio_tgt.channels);
			swr_free(&this->swr_ctx);
			return -1;
		}
		this->audio_src.channel_layout = dec_channel_layout;
		this->audio_src.channels = af->frame->channels;
		this->audio_src.freq = af->frame->sample_rate;
		this->audio_src.fmt = static_cast<AVSampleFormat>(af->frame->format);
	}

	if (this->swr_ctx) {
		const uint8_t **in = (const uint8_t **)af->frame->extended_data;
		uint8_t **out = &this->audio_buf1;
		int out_count = (int64_t)wanted_nb_samples * this->audio_tgt.freq / af->frame->sample_rate + 256;
		int out_size = av_samples_get_buffer_size(NULL, this->audio_tgt.channels, out_count, this->audio_tgt.fmt, 0);
		int len2;
		if (out_size < 0) {
			av_log(NULL, AV_LOG_ERROR, "av_samples_get_buffer_size() failed\n");
			return -1;
		}
		if (wanted_nb_samples != af->frame->nb_samples) {
			if (swr_set_compensation(this->swr_ctx, (wanted_nb_samples - af->frame->nb_samples) * this->audio_tgt.freq / af->frame->sample_rate,
				wanted_nb_samples * this->audio_tgt.freq / af->frame->sample_rate) < 0) {
				av_log(NULL, AV_LOG_ERROR, "swr_set_compensation() failed\n");
				return -1;
			}
		}
		av_fast_malloc(&this->audio_buf1, &this->audio_buf1_size, out_size);
		if (!this->audio_buf1)
			return AVERROR(ENOMEM);
		len2 = swr_convert(this->swr_ctx, out, out_count, in, af->frame->nb_samples);
		if (len2 < 0) {
			av_log(NULL, AV_LOG_ERROR, "swr_convert() failed\n");
			return -1;
		}
		if (len2 == out_count) {
			av_log(NULL, AV_LOG_WARNING, "audio buffer is probably too small\n");
			if (swr_init(this->swr_ctx) < 0)
				swr_free(&this->swr_ctx);
		}
		this->audio_buf = this->audio_buf1;
		resampled_data_size = len2 * this->audio_tgt.channels * av_get_bytes_per_sample(this->audio_tgt.fmt);
	}
	else {
		this->audio_buf = af->frame->data[0];
		resampled_data_size = data_size;
	}

	audio_clock0 = this->audio_clock;
	/* update the audio clock with the pts */
	if (!isnan(af->pts))
		this->audio_clock = af->pts + (double)af->frame->nb_samples / af->frame->sample_rate;
	else
		this->audio_clock = NAN;
	this->audio_clock_serial = af->serial;
	return resampled_data_size;
}

// Note, queues and clocks get initialized in the create_video_state function
// The initialization order is correct now, but it is not garuanteed that some
// of these might not be null; hence, we initialize this in the create function
VideoState::VideoState(int audio_buffer_size) : 
	show_mode(SHOW_MODE_NONE),
	abort_request(0),
	paused(true), // TRUE
	last_paused(0),
	stopped(false),
	queue_attachments_req(0),
	seek_req(0),
	seek_flags(AVSEEK_FLAG_BACKWARD), // AV_SEEK_BACKWARD
	seek_pos(0),
	seek_rel(0),
	read_pause_return(0),
	realtime(0),
	av_sync_type(0),
	fps(0),
	subtitle_stream(0),
	frame_last_returned_time(0),
	frame_last_filter_delay(0),
	video_stream(0),
	max_frame_duration(0),
	eof(0),
	video_duration(0),
	image_width(0),
	image_height(0),
	image_sample_aspect_ratio(av_make_q(0, 0)),
	step(false),
	new_rate_req(0),
	rate(1.0),
	pts_speed(1.0),
	audio_disable(0),
	video_disable(0),
	subtitle_disable(0),
	last_video_stream(0),
	last_audio_stream(0),
	last_subtitle_stream(0),
	filename(nullptr),
	pAudioq(nullptr),
	pVideoq(nullptr),
	pSubtitleq(nullptr),
	pSampq(nullptr),
	pPictq(nullptr),
	pSubpq(nullptr),
	pAudclk(nullptr),
	pVidclk(nullptr),
	pExtclk(nullptr),
	pAuddec(nullptr),
	pViddec(nullptr),
	pSubdec(nullptr),
	read_tid(nullptr),
	iformat(nullptr),
	ic(nullptr),
	swr_ctx(nullptr),
	audio_st(nullptr),
	subtitle_st(nullptr),
	video_st(nullptr),
	audio_stream(0),
	audio_clock(0.0),
	audio_clock_serial(0),
	audio_diff_cum(0.0),
	audio_diff_avg_coef(0.0),
	audio_diff_threshold(0.0),
	audio_diff_avg_count(0),
	audio_hw_buf_size(0),
	audio_buffer_size(audio_buffer_size),
	audio_buf(nullptr),
	audio_buf1(nullptr),
	audio_buf_size(0), /* in bytes */
	audio_buf1_size(0),
	audio_buf_index(0), /* in bytes */
	audio_write_buf_size(0),
	muted(0),
	frame_drops_early(0),
	rdft(nullptr),
	rdft_bits(0),
	rdft_data(nullptr)
#if CONFIG_AUDIO_FILTER
	,
	afilters(nullptr), // audio filter Note: audio filter is not working
	in_audio_filter(nullptr),   // the first filter in the audio chain
	out_audio_filter(nullptr),  // the last filter in the audio chain
	agraph(nullptr)            // audio filter graph
#endif
#if CONFIG_VIDEO_FILTER
	,
	vfilter_idx(0),
	vfilters_list(nullptr),
	vfilters(nullptr), // video filter
	nb_vfilters(0),
	in_video_filter(nullptr),   // the first filter in the video chain
	out_video_filter(nullptr),  // the last filter in the video chain
	sws_dict(nullptr),			// From cmdutils
	swr_opts(nullptr)			// From cmdutils
#endif
{}

VideoState* VideoState::create_video_state(int audio_buffer_size) {
	// Create the video state
	VideoState* vs = new (std::nothrow) VideoState(audio_buffer_size);
	if (!vs) {
		av_log(NULL, AV_LOG_ERROR, "Unable to create new video state");
		return nullptr;
	}

	// Initialize packet queues
	vs->pAudioq = new (std::nothrow) PacketQueue();
	if (!vs->pAudioq) {
		av_log(NULL, AV_LOG_ERROR, "Unable to create packet queue for audio");
		delete vs;
		return nullptr;
	}
	vs->pVideoq = new (std::nothrow) PacketQueue();
	if (!vs->pVideoq) {
		av_log(NULL, AV_LOG_ERROR, "Unable to create packet queue for video");
		delete vs;
		return nullptr;
	}
	vs->pSubtitleq = new (std::nothrow) PacketQueue();
	if (!vs->pSubtitleq) {
		av_log(NULL, AV_LOG_ERROR, "Unable to create packet queue for subtitles");
		delete vs;
		return nullptr;
	}

	// Handle frame queues
	vs->pSampq = FrameQueue::create_frame_queue(vs->pAudioq, SAMPLE_QUEUE_SIZE, 1);
	if (!vs->pSampq) {
		av_log(NULL, AV_LOG_ERROR, "Unable to create frame queue for audio");
		delete vs;
		return nullptr;
	}
	vs->pPictq = FrameQueue::create_frame_queue(vs->pVideoq, VIDEO_PICTURE_QUEUE_SIZE, 1);
	if (!vs->pPictq) {
		av_log(NULL, AV_LOG_ERROR, "Unable to create frame queue for video");
		delete vs;
		return nullptr;
	}
	vs->pSubpq = FrameQueue::create_frame_queue(vs->pSubtitleq, SUBPICTURE_QUEUE_SIZE, 0);
	if (!vs->pSubpq) {
		av_log(NULL, AV_LOG_ERROR, "Unable to create frame queue for subtitle");
		delete vs;
		return nullptr;
	}

	// Create clocks
	vs->pAudclk = new (std::nothrow) Clock(vs->pAudioq->get_p_serial());
	if (!vs->pAudclk) {
		av_log(NULL, AV_LOG_ERROR, "Unable to create clock for audio");
		delete vs;
		return nullptr;
	}
	vs->pVidclk = new (std::nothrow) Clock(vs->pVideoq->get_p_serial());
	if (!vs->pVidclk) {
		av_log(NULL, AV_LOG_ERROR, "Unable to create clock for video");
		delete vs;
		return nullptr;
	}
	vs->pExtclk = new (std::nothrow) Clock();
	if (!vs->pExtclk) {
		av_log(NULL, AV_LOG_ERROR, "Unable to create clock for external");
		delete vs;
		return nullptr;
	}

	return vs;
}


/* Destructor */
VideoState::~VideoState() {
	if (pVideoq) delete(pVideoq);
	if (pAudioq) delete(pAudioq);
	if (pSubtitleq) delete(pSubtitleq);

	if (pPictq) delete(pPictq);
	if (pSubpq) delete(pSubpq);
	if (pSampq) delete(pSampq);
	
	if (pVidclk) delete(pVidclk);
	if (pAudclk) delete(pAudclk);
	if (pExtclk) delete(pExtclk);

#if CONFIG_VIDEO_FILTER
	av_dict_free(&swr_opts);
	av_dict_free(&sws_dict);
#endif
	// Note, that the decoders get freed in the stream close function
}

//* this thread gets the stream from the disk or the network */
int VideoState::read_thread() {
	int ret;
	AVPacket pkt1, *pkt = &pkt1;
	bool was_stalled = false;
	std::mutex wait_mutex;
	int64_t stream_start_time;
	int pkt_in_play_range = 0;
	int64_t pkt_ts;

	// TODO: Need to work in TO_STOPPED state
	// Reda: added bool stopped when we trigger stop; when stopped both is stopped and paused are set to 1
	// So each time we check for pause we need to check inside the condition if it is also stopped 

	for (;;) {
		if (this->abort_request)
			break;
		if (this->paused != this->last_paused) {
			this->last_paused = this->paused;
			if (this->paused) {
				if (this->stopped) {
					if (player_state_callbacks[TO_STOPPED]) {
						player_state_callbacks[TO_STOPPED]();
					}
				}
				else {
					if (player_state_callbacks[TO_PAUSED]) {
						player_state_callbacks[TO_PAUSED]();
					}
				}
				this->read_pause_return = av_read_pause(ic);
			}
			else {
				av_read_play(ic); // Start Playing a network based stream
				if (player_state_callbacks[TO_PLAYING]) {
					player_state_callbacks[TO_PLAYING]();
				}
			}
		}
		if (was_stalled) {
			if (this->paused) {
				if (this->stopped) {
					if (player_state_callbacks[TO_STOPPED]) {
						player_state_callbacks[TO_STOPPED]();
					}
				}
				else {
					if (player_state_callbacks[TO_PAUSED]) {
						player_state_callbacks[TO_PAUSED]();
					}
				}
			}
			else {
				if (player_state_callbacks[TO_PLAYING]) {
					player_state_callbacks[TO_PLAYING]();
				}
			}
			was_stalled = false;
		}

#if CONFIG_RTSP_DEMUXER || CONFIG_MMSH_PROTOCOL
		if (paused &&
			(!strcmp(ic->iformat->name, "rtsp") ||
			(ic->pb && !strncmp(input_filename, "mmsh:", 5)))) {
			/* wait 10 ms to avoid trying to get another packet */
			/* XXX: horrible */
			SDL_Delay(10);
			continue;
		}
#endif
		if (new_rate_req) {
			pts_speed = 1/rate;
			if (audio_stream >= 0) {
				pAudioq->flush();
				pAudioq->put_flush_packet();
			}
			if (subtitle_stream >= 0) {
				pSubtitleq->flush();
				pSubtitleq->put_flush_packet();
			}
			if (video_stream >= 0) {
				pVideoq->flush();
				pVideoq->put_flush_packet();
			}
			// TODO(fraudies): Check here what we need to do to reset the clocks so they don't get stuck
			// When toggeling the pause the external clock is set but that did not work here
			new_rate_req = 0;
			queue_attachments_req = 1;
			eof = 0;
		}

		if (this->seek_req) {
			if (player_state_callbacks[TO_STALLED]) {
				player_state_callbacks[TO_STALLED]();
				was_stalled = true;
			}

			int64_t seek_target = this->seek_pos;
			int64_t seek_min = this->seek_rel > 0 ? seek_target - this->seek_rel + 2 : INT64_MIN;
			int64_t seek_max = this->seek_rel < 0 ? seek_target - this->seek_rel - 2 : INT64_MAX;
			// FIXME the +-2 is due to rounding being not done in the correct direction in generation
			//      of the seek_pos/seek_rel variables
			ret = avformat_seek_file(this->ic, -1, seek_min, seek_target, seek_max, this->seek_flags);
			if (ret < 0) {
				av_log(NULL, AV_LOG_ERROR, "%s: error while seeking\n", this->ic->url);
			}
			else {
				if (this->audio_stream >= 0) {
					pAudioq->flush();
					pAudioq->put_flush_packet();
				}
				if (this->subtitle_stream >= 0) {
					pSubtitleq->flush();
					pSubtitleq->put_flush_packet();
				}
				if (this->video_stream >= 0) {
					pVideoq->flush();
					pVideoq->put_flush_packet();
				}
				if (this->seek_flags & AVSEEK_FLAG_BYTE) {
					pExtclk->set_clock(NAN, 0);
				}
				else {
					pExtclk->set_clock(seek_target / (double)AV_TIME_BASE, 0);
				}
			}
			this->seek_req = 0;
			this->queue_attachments_req = 1;
			this->eof = 0;
#if _DEBUG
			printf("Clocks After Seek: Ext : %7.2f sec - Aud : %7.2f sec - Vid : %7.2f sec - Error : %7.2f sec\n",
				get_pExtclk()->get_clock(),
				get_pAudclk()->get_clock(),
				get_pVidclk()->get_clock(),
				abs(get_pExtclk()->get_clock() - get_pAudclk()->get_clock()));
#endif // _DEBUG

			if (this->paused){
				step_to_next_frame_callback(); // Assume that is set--otherwise fail hard here
			}
			else {
				if (player_state_callbacks[TO_PLAYING]) {
					player_state_callbacks[TO_PLAYING]();
				}
				if (was_stalled)
					was_stalled = false;
			}
		}
		if (this->queue_attachments_req) {
			if (this->video_st && this->video_st->disposition & AV_DISPOSITION_ATTACHED_PIC) {
				AVPacket copy = { 0 };
				if ((ret = av_packet_ref(&copy, &this->video_st->attached_pic)) < 0)
					goto fail;
				pVideoq->put(&copy);
				pVideoq->put_null_packet(this->video_stream);
			}
			this->queue_attachments_req = 0;
		}

		/* if the queue are full, no need to read more */
		if (infinite_buffer<1 &&
			(pAudioq->get_size() + pVideoq->get_size() + pSubtitleq->get_size() > MAX_QUEUE_SIZE
				|| (stream_has_enough_packets(this->audio_st, this->audio_stream, pAudioq) &&
					stream_has_enough_packets(this->video_st, this->video_stream, pVideoq) &&
					stream_has_enough_packets(this->subtitle_st, this->subtitle_stream, pSubtitleq)))) {
			/* wait 10 ms */
			std::unique_lock<std::mutex> locker(wait_mutex);
			continue_read_thread.wait_for(locker, std::chrono::milliseconds(10));
			locker.unlock();
			continue;
		}
		if (!this->paused &&
			(!this->audio_st || (pAuddec->is_finished() == pAudioq->get_serial() && pSampq->nb_remaining() == 0)) &&
			(!this->video_st || (pViddec->is_finished() == pVideoq->get_serial() && pPictq->nb_remaining() == 0))) {
			if (loop != 1 && (!loop || --loop)) {
				this->stream_seek(start_time != AV_NOPTS_VALUE ? start_time : 0, 0, 0);
			}
			else if (autoexit) {
				ret = AVERROR_EOF;
				goto fail;
			}
		}
		ret = av_read_frame(ic, pkt);
		if (ret < 0) {
			if ((ret == AVERROR_EOF || avio_feof(ic->pb)) && !this->eof) {
				if (this->video_stream >= 0)
					pVideoq->put_null_packet(this->video_stream);
				if (this->audio_stream >= 0)
					pAudioq->put_null_packet(this->audio_stream);
				if (this->subtitle_stream >= 0)
					pSubtitleq->put_null_packet(this->subtitle_stream);
				this->eof = 1;

				// Set the player state to finished
				if (player_state_callbacks[TO_FINISHED]) {
					player_state_callbacks[TO_FINISHED]();
				}
			}
			if (ic->pb && ic->pb->error)
				break;
			/* wait 10 ms */
			std::unique_lock<std::mutex> locker(wait_mutex);
			continue_read_thread.wait_for(locker, std::chrono::milliseconds(10));
			locker.unlock();
			continue;
		}
		else {
			this->eof = 0;
			// TODO(fraudies): Set the player state from stalled to ready here (if ready don't do anything)
		}
		/* check if packet is in play range specified by user, then queue, otherwise discard */
		stream_start_time = ic->streams[pkt->stream_index]->start_time;
		pkt_ts = pkt->pts == AV_NOPTS_VALUE ? pkt->dts : pkt->pts;
		
		pkt_in_play_range = max_duration == AV_NOPTS_VALUE ||
			(pkt_ts - (stream_start_time != AV_NOPTS_VALUE ? stream_start_time : 0)) *
			av_q2d(ic->streams[pkt->stream_index]->time_base) -
			(double)(start_time != AV_NOPTS_VALUE ? start_time : 0) / 1000000
			<= ((double)max_duration / 1000000);

		if (pkt->stream_index == this->audio_stream && pkt_in_play_range) {
			pAudioq->put(pkt);
		}
		else if (pkt->stream_index == this->video_stream && pkt_in_play_range
			&& !(this->video_st->disposition & AV_DISPOSITION_ATTACHED_PIC)) {
			pVideoq->put(pkt);
		}
		else if (pkt->stream_index == this->subtitle_stream && pkt_in_play_range) {
			pSubtitleq->put(pkt);
		}
		else {
			av_packet_unref(pkt);
		}
	}

	ret = 0;
fail:
	if (ret != 0 && ic)
		avformat_close_input(&ic);

	if (ret != 0 && destroy_callback) {
		destroy_callback();
	}

	return 0;
}

/* Called when the stream is opened */
int VideoState::audio_thread() {
	AVFrame *frame = av_frame_alloc();
	Frame *af;

#if CONFIG_AUDIO_FILTER
	int last_serial = -1;
	int64_t dec_channel_layout;
	int reconfigure;
#endif
	int got_frame = 0;
	AVRational tb;
	int ret = 0;

	if (!frame)
		return AVERROR(ENOMEM);

	do {
		if ((got_frame = pAuddec->decode_frame(frame, NULL)) < 0)
			goto the_end;

		if (got_frame) {
			tb = av_make_q(1, frame->sample_rate);

#if CONFIG_AUDIO_FILTER
			dec_channel_layout = get_valid_channel_layout(frame->channel_layout, frame->channels);

			reconfigure =
				cmp_audio_fmts(audio_filter_src.fmt, audio_filter_src.channels, static_cast<AVSampleFormat>(frame->format), frame->channels)
				|| audio_filter_src.channel_layout != dec_channel_layout
				|| audio_filter_src.freq != frame->sample_rate
				|| pAuddec->get_pkt_serial() != last_serial;

			if (reconfigure) {
				char buf1[1024], buf2[1024];
				av_get_channel_layout_string(buf1, sizeof(buf1), -1, audio_filter_src.channel_layout);
				av_get_channel_layout_string(buf2, sizeof(buf2), -1, dec_channel_layout);
				av_log(NULL, AV_LOG_DEBUG,
					"Audio frame changed from rate:%d ch:%d fmt:%s layout:%s serial:%d to rate:%d ch:%d fmt:%s layout:%s serial:%d\n",
					audio_filter_src.freq, audio_filter_src.channels, av_get_sample_fmt_name(audio_filter_src.fmt), buf1, last_serial,
					frame->sample_rate, frame->channels, av_get_sample_fmt_name(static_cast<AVSampleFormat>(frame->format)), buf2, pAuddec->get_pkt_serial());

				audio_filter_src.fmt = static_cast<AVSampleFormat>(frame->format);
				audio_filter_src.channels = frame->channels;
				audio_filter_src.channel_layout = dec_channel_layout;
				audio_filter_src.freq = frame->sample_rate;
				last_serial = pAuddec->get_pkt_serial();

				if ((ret = configure_audio_filters(afilters, 1)) < 0) {
					//av_log(NULL, AV_LOG_INFO, "audio_thread function cannot configure the audio filter");
					goto the_end;
				}
			}

			if ((ret = av_buffersrc_add_frame(in_audio_filter, frame)) < 0)
				goto the_end;

			while ((ret = av_buffersink_get_frame_flags(out_audio_filter, frame, 0)) >= 0) {
				tb = av_buffersink_get_time_base(out_audio_filter);
#endif
				if (!(af = pSampq->peek_writable()))
					goto the_end;

				af->pts = (frame->pts == AV_NOPTS_VALUE) ? NAN : frame->pts * av_q2d(tb);
				af->pos = frame->pkt_pos;
				af->serial = pAuddec->get_pkt_serial();
				af->duration = av_q2d(av_make_q(frame->nb_samples, frame->sample_rate));
				// TODO(fraudies): Check why this does not affect the sample_rate
				//af->frame->sample_rate = frame->sample_rate / pts_speed;

				av_frame_move_ref(af->frame, frame);
				pSampq->push();

#if CONFIG_AUDIO_FILTER
				if (pAudioq->get_serial() != pAuddec->get_pkt_serial())
					break;
			}
			if (ret == AVERROR_EOF)
				pAuddec->setFinished(pAuddec->get_pkt_serial());
#endif
		}
	} while (ret >= 0 || ret == AVERROR(EAGAIN) || ret == AVERROR_EOF);
the_end:
#if CONFIG_AUDIO_FILTER
	avfilter_graph_free(&agraph);
#endif
	av_frame_free(&frame);
	return ret;
}

/* Called when the stream is opened */
int VideoState::video_thread() {
	AVFrame *frame = av_frame_alloc();
	double pts;
	double duration;
	int ret;
	AVRational tb = this->video_st->time_base;
	AVRational frame_rate = av_guess_frame_rate(this->ic, this->video_st, NULL);

#if CONFIG_VIDEO_FILTER
	AVFilterGraph *graph = avfilter_graph_alloc();
	AVFilterContext *filt_out = NULL, *filt_in = NULL;
	int last_w = 0;
	int last_h = 0;
	enum AVPixelFormat last_format = AV_PIX_FMT_NONE;
	int last_serial = -1;
	int last_vfilter_idx = 0;
	if (!graph) {
		av_frame_free(&frame);
		return AVERROR(ENOMEM);
	}

#endif

	if (!frame) {
#if CONFIG_VIDEO_FILTER
		avfilter_graph_free(&graph);
#endif
		return AVERROR(ENOMEM);
	}

	for (;;) {
		ret = this->get_video_frame(frame);
		if (ret < 0)
			goto the_end;
		if (!ret)
			continue;

#if CONFIG_VIDEO_FILTER
		if ( new_rate_req
			||last_w != frame->width
			|| last_h != frame->height
			|| last_format != frame->format
			|| last_serial != pViddec->get_pkt_serial()
			|| last_vfilter_idx != vfilter_idx) {
			av_log(NULL, AV_LOG_DEBUG,
				"Video frame changed from size:%dx%d format:%s serial:%d to size:%dx%d format:%s serial:%d\n",
				last_w, last_h,
				(const char *)av_x_if_null(av_get_pix_fmt_name(last_format), "none"), last_serial,
				frame->width, frame->height,
				(const char *)av_x_if_null(av_get_pix_fmt_name(static_cast<AVPixelFormat>(frame->format)), "none"), pViddec->get_pkt_serial());
			avfilter_graph_free(&graph);
			graph = avfilter_graph_alloc();
			//if ((ret = configure_video_filters(graph, this, vfilters_list ? vfilters_list[vfilter_idx] : NULL, frame)) < 0) {
			if ((ret = configure_video_filters(graph, vfilters ? vfilters : NULL, frame)) < 0) {
				// TODO(fraudies): Need to call exit hook
				//SDLPlayData::do_exit(this);
				goto the_end;
			}
			filt_in = in_video_filter;
			filt_out = out_video_filter;
			last_w = frame->width;
			last_h = frame->height;
			last_format = static_cast<AVPixelFormat>(frame->format);
			last_serial = pViddec->get_pkt_serial();
			last_vfilter_idx = vfilter_idx;
			frame_rate = av_buffersink_get_frame_rate(filt_out);
		}

		ret = av_buffersrc_add_frame(filt_in, frame);
		if (ret < 0)
			goto the_end;

		while (ret >= 0) {
			this->frame_last_returned_time = av_gettime_relative() / 1000000.0;

			ret = av_buffersink_get_frame_flags(filt_out, frame, 0);
			if (ret < 0) {
				if (ret == AVERROR_EOF)
					pViddec->setFinished(pViddec->get_pkt_serial());
				ret = 0;
				break;
			}

			frame_last_filter_delay = av_gettime_relative() / 1000000.0 - frame_last_returned_time;
			if (fabs(frame_last_filter_delay) > AV_NOSYNC_THRESHOLD / 10.0)
				frame_last_filter_delay = 0;
			tb = av_buffersink_get_time_base(filt_out);
#endif
			// Set clock speed
			duration = (frame_rate.num && frame_rate.den) ? av_q2d(av_make_q(frame_rate.den, frame_rate.num)) /** pts_speed*/ : 0;
#if CONFIG_VIDEO_FILTER
			pts = (frame->pts == AV_NOPTS_VALUE) ? NAN : frame->pts * av_q2d(tb); // *pts_speed;
#else
			// Set clock speed
			pts = (frame->pts == AV_NOPTS_VALUE) ? NAN : frame->pts * av_q2d(tb) * pts_speed; //here
#endif
			ret = queue_picture(frame, pts, duration, frame->pkt_pos, pViddec->get_pkt_serial());
			av_frame_unref(frame);
#if CONFIG_VIDEO_FILTER
		}
#endif
		if (ret < 0)
			goto the_end;
	}
the_end:
#if CONFIG_VIDEO_FILTER
	avfilter_graph_free(&graph);
#endif
	av_frame_free(&frame);
	return 0;
}

/* Called when the stream is opened */
int VideoState::subtitle_thread() {
	Frame *sp;
	int got_subtitle;
	double pts;

	for (;;) {
		if (!(sp = pSubpq->peek_writable()))
			return 0;

		if ((got_subtitle = pSubdec->decode_frame(NULL, &sp->sub)) < 0)
			break;

		pts = 0;

		if (got_subtitle && sp->sub.format == 0) {
			if (sp->sub.pts != AV_NOPTS_VALUE)
				pts = sp->sub.pts / (double)AV_TIME_BASE;
			sp->pts = pts;
			sp->serial = pSubdec->get_pkt_serial();
			sp->width = pSubdec->get_avctx()->width;
			sp->height = pSubdec->get_avctx()->height;
			sp->uploaded = 0;

			/* now we can update the picture count */
			this->pSubpq->push();
		}
		else if (got_subtitle) {
			avsubtitle_free(&sp->sub);
		}
	}
	return 0;
}

/* Public Members*/
VideoState *VideoState::stream_open(const char *filename, AVInputFormat *iformat, int audio_buffer_size) {
	VideoState *is = VideoState::create_video_state(audio_buffer_size);
	if (!is)
		return NULL;

	is->filename = av_strdup(filename);
	if (!is->filename)
		return NULL;

	is->iformat = iformat;

	is->audio_clock_serial = -1;

	is->av_sync_type = av_sync_type_input;

	return is;
}

int VideoState::stream_start() {
	int i, ret;
	int st_index[AVMEDIA_TYPE_NB];
	AVDictionaryEntry *t;
	int scan_all_pmts_set = 0;
	AVDictionary *format_opts = NULL;
	AVDictionary *codec_opts = NULL;

	memset(st_index, -1, sizeof(st_index));
	last_video_stream = video_stream = -1;
	last_audio_stream = audio_stream = -1;
	last_subtitle_stream = subtitle_stream = -1;
	eof = 0;

	ic = avformat_alloc_context();
	if (!ic) {
		av_log(NULL, AV_LOG_FATAL, "Could not allocate context.\n");
		ret = AVERROR(ENOMEM);
		goto fail;
	}
	ic->interrupt_callback.callback = decode_interrupt_cb_bridge;
	ic->interrupt_callback.opaque = this;
	if (!av_dict_get(format_opts, "scan_all_pmts", NULL, AV_DICT_MATCH_CASE)) {
		av_dict_set(&format_opts, "scan_all_pmts", "1", AV_DICT_DONT_OVERWRITE);
		scan_all_pmts_set = 1;
	}
	ret = avformat_open_input(&ic, filename, iformat, &format_opts);
	if (ret < 0) {
		goto fail;
	}
	if (scan_all_pmts_set)
		av_dict_set(&format_opts, "scan_all_pmts", NULL, AV_DICT_MATCH_CASE);

	if ((t = av_dict_get(format_opts, "", NULL, AV_DICT_IGNORE_SUFFIX))) {
		av_log(NULL, AV_LOG_ERROR, "Option %s not found.\n", t->key);
		ret = AVERROR_OPTION_NOT_FOUND;
		goto fail;
	}

	if (genpts)
		ic->flags |= AVFMT_FLAG_GENPTS;

	av_format_inject_global_side_data(ic);

	if (find_stream_info) {
		AVDictionary **opts = setup_find_stream_info_opts(ic, codec_opts);
		int orig_nb_streams = ic->nb_streams;

		ret = avformat_find_stream_info(ic, opts);

		for (i = 0; i < orig_nb_streams; i++)
			av_dict_free(&opts[i]);
		av_freep(&opts);

		if (ret < 0) {
			av_log(NULL, AV_LOG_WARNING, "%s: could not find codec parameters\n", filename);
			goto fail;
		}
	}

	if (ic->pb)
		ic->pb->eof_reached = 0; // FIXME hack, ffplay maybe should not use avio_feof() to test for the end

	if (seek_by_bytes < 0)
		seek_by_bytes = !!(ic->iformat->flags & AVFMT_TS_DISCONT) && strcmp("ogg", ic->iformat->name);

	this->max_frame_duration = (ic->iformat->flags & AVFMT_TS_DISCONT) ? 10.0 : 3600.0;

	if (!window_title && (t = av_dict_get(ic->metadata, "title", NULL, 0)))
		window_title = av_asprintf("%s - %s", t->value, filename);

	/* if seeking requested, we execute it */
	if (start_time != AV_NOPTS_VALUE) {
		int64_t timestamp;

		timestamp = start_time;
		/* add the stream start time */
		if (ic->start_time != AV_NOPTS_VALUE)
			timestamp += ic->start_time;
		ret = avformat_seek_file(ic, -1, INT64_MIN, timestamp, INT64_MAX, 0);
		if (ret < 0) {
			av_log(NULL, AV_LOG_WARNING, "%s: could not seek to position %0.3f\n",
				filename, (double)timestamp / AV_TIME_BASE);
		}
	}

	realtime = is_realtime(ic);

	if (show_status)
		av_dump_format(ic, 0, filename, 0);

	for (i = 0; i < ic->nb_streams; i++) {
		AVStream *st = ic->streams[i];
		enum AVMediaType type = st->codecpar->codec_type;
		st->discard = AVDISCARD_ALL;
		if (type >= 0 && wanted_stream_spec[type] && st_index[type] == -1)
			if (avformat_match_stream_specifier(ic, st, wanted_stream_spec[type]) > 0)
				st_index[type] = i;
	}
	for (i = 0; i < AVMEDIA_TYPE_NB; i++) {
		if (wanted_stream_spec[i] && st_index[i] == -1) {
			av_log(NULL, AV_LOG_ERROR, "Stream specifier %s does not match any %s stream\n",
				wanted_stream_spec[i],
				av_get_media_type_string(static_cast<AVMediaType>(i)));
			st_index[i] = INT_MAX;
		}
	}

	if (!video_disable)
		st_index[AVMEDIA_TYPE_VIDEO] =
		av_find_best_stream(ic, AVMEDIA_TYPE_VIDEO,
			st_index[AVMEDIA_TYPE_VIDEO], -1, NULL, 0);
	if (!audio_disable)
		st_index[AVMEDIA_TYPE_AUDIO] =
		av_find_best_stream(ic, AVMEDIA_TYPE_AUDIO,
			st_index[AVMEDIA_TYPE_AUDIO],
			st_index[AVMEDIA_TYPE_VIDEO],
			NULL, 0);
	if (!video_disable && !subtitle_disable)
		st_index[AVMEDIA_TYPE_SUBTITLE] =
		av_find_best_stream(ic, AVMEDIA_TYPE_SUBTITLE,
			st_index[AVMEDIA_TYPE_SUBTITLE],
			(st_index[AVMEDIA_TYPE_AUDIO] >= 0 ?
				st_index[AVMEDIA_TYPE_AUDIO] :
				st_index[AVMEDIA_TYPE_VIDEO]),
			NULL, 0);

	/* open the streams */
	if (st_index[AVMEDIA_TYPE_AUDIO] >= 0) {
		stream_component_open(st_index[AVMEDIA_TYPE_AUDIO]);
	}
	// Set the video duration (return and also clamp the audio clock to this)
	video_duration = ic->duration / (double)AV_TIME_BASE;

	ret = -1;
	if (st_index[AVMEDIA_TYPE_VIDEO] >= 0) {
		ret = stream_component_open(st_index[AVMEDIA_TYPE_VIDEO]);
	}
	if (show_mode == SHOW_MODE_NONE)
		show_mode = ret >= 0 ? SHOW_MODE_VIDEO : SHOW_MODE_RDFT;

	if (st_index[AVMEDIA_TYPE_SUBTITLE] >= 0) {
		stream_component_open(st_index[AVMEDIA_TYPE_SUBTITLE]);
	}

	if (video_stream < 0 && audio_stream < 0) {
		av_log(NULL, AV_LOG_FATAL, "Failed to open file '%s' or configure filtergraph\n", filename);
		ret = AVERROR_STREAM_NOT_FOUND;
		goto fail;
	}

	if (infinite_buffer < 0 && realtime)
		infinite_buffer = 1;

	if (player_state_callbacks[TO_READY]) {
		player_state_callbacks[TO_READY]();
	}

	read_tid = new (std::nothrow) std::thread([this] { this->read_thread(); });
	if (!read_tid) {
		av_log(NULL, AV_LOG_FATAL, "Unable to create reader thread\n");
		ret = AVERROR(ENOMEM);
		goto fail;
	}

	return 0;

fail:
	if (ret != 0 && ic)
		avformat_close_input(&ic);

	if (ret != 0 && destroy_callback) {
		destroy_callback();
	}

	return ret;
}

void VideoState::set_player_state_callback_func(PlayerStateCallback callback, const std::function<void()>& func) {
	player_state_callbacks[callback] = func;
}

void VideoState::set_audio_open_callback(const std::function<int(int64_t, int, int, struct AudioParams*)>& func) {
	audio_open_callback = func;
}

void VideoState::set_pause_audio_device_callback(const std::function<void()>& func) {
	pause_audio_device_callback = func;
}

void VideoState::set_destroy_callback(const std::function<void()>& func) {
	destroy_callback = func;
}

void VideoState::set_step_to_next_frame_callback(const std::function<void()>& func) {
	step_to_next_frame_callback = func;
}

// Function Called from the event loop
void VideoState::seek_chapter(int incr) {
	int64_t pos = this->get_master_clock() * AV_TIME_BASE;
	int i;

	if (!this->ic->nb_chapters)
		return;

	/* find the current chapter */
	for (i = 0; i < this->ic->nb_chapters; i++) {
		AVChapter *ch = this->ic->chapters[i];
		if (av_compare_ts(pos, MY_AV_TIME_BASE_Q, ch->start, ch->time_base) < 0) {
			i--;
			break;
		}
	}

	i += incr;
	i = FFMAX(i, 0);
	if (i >= this->ic->nb_chapters)
		return;

	av_log(NULL, AV_LOG_VERBOSE, "Seeking to chapter %d.\n", i);
	this->stream_seek(av_rescale_q(this->ic->chapters[i]->start, this->ic->chapters[i]->time_base, MY_AV_TIME_BASE_Q), 0, 0);
}

int VideoState::get_image_width() const {
	return image_width;
}

int VideoState::get_image_height() const {
	return image_height;
}

AVRational VideoState::get_image_sample_aspect_ratio() const {
	return image_sample_aspect_ratio;
}

bool VideoState::has_audio_data() const {
	return last_audio_stream >= 0;
}

bool VideoState::has_image_data() const {
	return last_video_stream >= 0;
}

double VideoState::get_duration() const {
	return video_duration;
}

double VideoState::get_stream_time() const {
	// TODO: Maybe other places need to handle NAN; instead of picking the external clock
	// Could also use the last time -- NAN happens after a seek
	double time = get_master_clock();
	if (isnan(time)) {
		time = pExtclk->get_clock();
	}
	return time;
}

void VideoState::toggle_mute() {
	this->muted = !this->muted;
}

void VideoState::update_pts(double pts, int64_t pos, int serial) {
	// TODO: Revisit this
	get_pVidclk()->set_clock(pts / pts_speed, serial);
	Clock::sync_clock_to_slave(get_pExtclk(), get_pVidclk());
}

/* seek in the stream */
void VideoState::stream_seek(int64_t pos, int64_t rel, int seek_by_bytes) {
	if (!this->seek_req) {
		this->seek_pos = pos;
		this->seek_rel = rel;
		this->seek_flags &= ~AVSEEK_FLAG_BYTE;
		if (seek_by_bytes)
			this->seek_flags |= AVSEEK_FLAG_BYTE;
		this->seek_req = 1;
		continue_read_thread.notify_one();
	}
}

// TODO(fraudies): Can we remove this? This could help simplify the memory management
void VideoState::stream_cycle_channel(int codec_type) {
	AVFormatContext *ic = this->ic;
	int start_index, stream_index;
	int old_index;
	AVStream *st;
	AVProgram *p = NULL;
	int nb_streams = this->ic->nb_streams;

	if (codec_type == AVMEDIA_TYPE_VIDEO) {
		start_index = this->last_video_stream;
		old_index = this->video_stream;
	}
	else if (codec_type == AVMEDIA_TYPE_AUDIO) {
		start_index = this->last_audio_stream;
		old_index = this->audio_stream;
	}
	else {
		start_index = this->last_subtitle_stream;
		old_index = this->subtitle_stream;
	}
	stream_index = start_index;

	if (codec_type != AVMEDIA_TYPE_VIDEO && this->video_stream != -1) {
		p = av_find_program_from_stream(ic, NULL, this->video_stream);
		if (p) {
			nb_streams = p->nb_stream_indexes;
			for (start_index = 0; start_index < nb_streams; start_index++)
				if (p->stream_index[start_index] == stream_index)
					break;
			if (start_index == nb_streams)
				start_index = -1;
			stream_index = start_index;
		}
	}

	for (;;) {
		if (++stream_index >= nb_streams)
		{
			if (codec_type == AVMEDIA_TYPE_SUBTITLE)
			{
				stream_index = -1;
				this->last_subtitle_stream = -1;
				goto the_end;
			}
			if (start_index == -1)
				return;
			stream_index = 0;
		}
		if (stream_index == start_index)
			return;
		st = this->ic->streams[p ? p->stream_index[stream_index] : stream_index];
		if (st->codecpar->codec_type == codec_type) {
			/* check that parameters are OK */
			switch (codec_type) {
			case AVMEDIA_TYPE_AUDIO:
				if (st->codecpar->sample_rate != 0 &&
					st->codecpar->channels != 0)
					goto the_end;
				break;
			case AVMEDIA_TYPE_VIDEO:
			case AVMEDIA_TYPE_SUBTITLE:
				goto the_end;
			default:
				break;
			}
		}
	}
the_end:
	if (p && stream_index != -1)
		stream_index = p->stream_index[stream_index];
	av_log(NULL, AV_LOG_INFO, "Switch %s stream from #%d to #%d\n",
		av_get_media_type_string(static_cast<AVMediaType>(codec_type)),
		old_index,
		stream_index);

	this->stream_component_close(old_index);
	this->stream_component_open(stream_index);
}

int VideoState::get_read_pause_return() const {
	return read_pause_return;
}

bool VideoState::get_paused() const { return paused; }

void VideoState::set_paused(bool new_paused) {
	paused = new_paused;
}

bool VideoState::get_stopped() const { return stopped; }

void VideoState::set_stopped(bool new_stopped) {
	stopped = new_stopped;
}

int VideoState::get_step() const { return step; }

void VideoState::set_step(bool new_step) {
	step = new_step;
}

int VideoState::get_frame_drops_early() const { return frame_drops_early; }

const char* VideoState::get_filename() const { return filename; }

AVStream *VideoState::get_audio_st() const { return audio_st; }
AVStream *VideoState::get_video_st() const { return video_st; }
AVStream *VideoState::get_subtitle_st() const { return subtitle_st; }

ShowMode VideoState::get_show_mode() const { return show_mode; }
void VideoState::set_show_mode(ShowMode new_show_mode) {
	show_mode = new_show_mode;
}

FrameQueue *VideoState::get_pPictq() const { return pPictq; }
FrameQueue *VideoState::get_pSubpq() const { return pSubpq; }
FrameQueue *VideoState::get_pSampq() const { return pSampq; }

PacketQueue *VideoState::get_pVideoq() const { return pVideoq; }
PacketQueue *VideoState::get_pSubtitleq() const { return pSubtitleq; }
PacketQueue *VideoState::get_pAudioq() const { return pAudioq; }

Clock *VideoState::get_pVidclk() const { return pVidclk; }
Clock *VideoState::get_pAudclk() const { return pAudclk; }
Clock *VideoState::get_pExtclk() const { return pExtclk; }

AudioParams VideoState::get_audio_tgt() const { return audio_tgt; }

// Lot's of discussion around this (may have to clean this up)
// See https://stackoverflow.com/questions/280162/is-there-a-way-to-do-a-c-style-compile-time-assertion-to-determine-machines-e
// and https://stackoverflow.com/questions/1001307/detecting-endianness-programmatically-in-a-c-program
int isBigEndian() {
	union {
		long int l;
		char c[sizeof(long int)];
	} u;
	u.l = 1;
	return (u.c[sizeof(long int) - 1] == 1);
}

Decoder* VideoState::get_pViddec() { return pViddec; }

AVFormatContext* VideoState::get_ic() const { return ic; }

int64_t VideoState::get_seek_pos() const { return seek_pos; }

int VideoState::get_video_stream() const { return video_stream; }
int VideoState::get_audio_stream() const { return audio_stream; }

double VideoState::get_max_frame_duration() { return max_frame_duration; }

int VideoState::get_audio_write_buf_size() const { return audio_write_buf_size; }

RDFTContext *VideoState::get_rdft() { return rdft; }
void VideoState::set_rdft(RDFTContext *newRDFT) { rdft = newRDFT; }

int VideoState::get_rdft_bits() { return rdft_bits; }
void VideoState::set_rdft_bits(int newRDF_bits) { rdft_bits = newRDF_bits; }

FFTSample *VideoState::get_rdft_data() { return rdft_data; }
void VideoState::set_rdft_data(FFTSample *newRDFT_data) { rdft_data = newRDFT_data; }

int VideoState::get_realtime() const { return realtime; }

inline int VideoState::decode_interrupt_cb() const {
	return abort_request;
}

double VideoState::compute_target_delay(double delay) {
	double sync_threshold, diff = 0;

	/* update delay to follow master synchronisation source */
	if (this->get_master_sync_type() != AV_SYNC_VIDEO_MASTER) {
		/* if video is slave, we try to correct big delays by
		duplicating or deleting a frame */
		diff = this->pVidclk->get_clock() - this->get_master_clock();

		/* skip or repeat frame. We take into account the
		delay to compute the threshold. I still don't know
		if it is the best guess */
		sync_threshold = FFMAX(AV_SYNC_THRESHOLD_MIN, FFMIN(AV_SYNC_THRESHOLD_MAX, delay));
		if (!isnan(diff) && fabs(diff) < this->max_frame_duration) {
			if (diff <= -sync_threshold)
				delay = FFMAX(0, delay + diff);
			else if (diff >= sync_threshold && delay > AV_SYNC_FRAMEDUP_THRESHOLD)
				delay = delay + diff;
			else if (diff >= sync_threshold)
				delay = 2 * delay;
		}
	}

	av_log(NULL, AV_LOG_TRACE, "video: delay=%0.3f A-V=%f\n", delay, -diff);

	return delay;
}

/* check the speed of the external clock */
void VideoState::check_external_clock_speed() {
	if (this->video_stream >= 0 && pVideoq->get_nb_packets() <= EXTERNAL_CLOCK_MIN_FRAMES ||
		this->audio_stream >= 0 && pAudioq->get_nb_packets() <= EXTERNAL_CLOCK_MIN_FRAMES) {
		pExtclk->set_clock_speed(FFMAX(EXTERNAL_CLOCK_SPEED_MIN, pExtclk->get_clock_speed() - EXTERNAL_CLOCK_SPEED_STEP));
	}
	else if ((this->video_stream < 0 || pVideoq->get_nb_packets() > EXTERNAL_CLOCK_MAX_FRAMES) &&
		(this->audio_stream < 0 || pAudioq->get_nb_packets() > EXTERNAL_CLOCK_MAX_FRAMES)) {
		pExtclk->set_clock_speed(FFMIN(EXTERNAL_CLOCK_SPEED_MAX, pExtclk->get_clock_speed() + EXTERNAL_CLOCK_SPEED_STEP));
	}
	else {
		double speed = pExtclk->get_clock_speed();
		if (speed != 1.0)
			pExtclk->set_clock_speed(speed + EXTERNAL_CLOCK_SPEED_STEP * (1.0 - speed) / fabs(1.0 - speed));
	}
}

/* get the current synchronization type */
int VideoState::get_master_sync_type() const {
	if (av_sync_type == AV_SYNC_VIDEO_MASTER) {
		if (video_st != nullptr)
			return AV_SYNC_VIDEO_MASTER;
		else
			return AV_SYNC_AUDIO_MASTER;
	}
	else if (av_sync_type == AV_SYNC_AUDIO_MASTER) {
		if (audio_st != nullptr)
			return AV_SYNC_AUDIO_MASTER;
		else
			return AV_SYNC_EXTERNAL_CLOCK;
	}
	else {
		return AV_SYNC_EXTERNAL_CLOCK;
	}
}

/* get the current master clock value */
double VideoState::get_master_clock() const {
	double val;
	switch (get_master_sync_type()) {
		case AV_SYNC_VIDEO_MASTER:
			val = pVidclk->get_clock();
			break;
		case AV_SYNC_AUDIO_MASTER:
			val = pAudclk->get_clock();
			break;
		default:
			val = pExtclk->get_clock();
			break;
	}
	return val;
}

double VideoState::get_fps() const {
	return video_st ? this->fps : 0;
}

double VideoState::get_pts_speed() const {
	return pts_speed;
}

void VideoState::stream_close() {
	/* XXX: use a special url_shutdown call to abort parse cleanly */

	abort_request = 1;
	if (read_tid) {
		read_tid->join();
		delete read_tid;
		read_tid = nullptr;
	}

	/* close each stream */
	if (audio_stream >= 0)
		stream_component_close(this->audio_stream);
	if (video_stream >= 0)
		stream_component_close(this->video_stream);
	if (subtitle_stream >= 0)
		stream_component_close(this->subtitle_stream);

	if (ic)
		avformat_close_input(&ic);

	av_free(this->filename);
}

/* prepare a new audio buffer */
void VideoState::audio_callback(uint8_t *stream, int len) {
	int audio_size, len1;
	audio_callback_time = av_gettime_relative();

	while (len > 0) {
		if (audio_buf_index >= audio_buf_size) {
			audio_size = audio_decode_frame();
			if (audio_size < 0) {
				/* if error, just output silence */
				audio_buf = NULL;
				audio_buf_size = audio_buffer_size / audio_tgt.frame_size * audio_tgt.frame_size;
			}
			else {
				audio_buf_size = audio_size;
			}
			audio_buf_index = 0;
		}
		len1 = audio_buf_size - audio_buf_index;
		if (len1 > len)
			len1 = len;
		if (!muted && audio_buf)
			memcpy(stream, (uint8_t *)audio_buf + audio_buf_index, len1);
		else {
			memset(stream, 0, len1);
		}
		len -= len1;
		stream += len1;
		audio_buf_index += len1;
	}
	audio_write_buf_size = audio_buf_size - audio_buf_index;
	/* Let's assume the audio driver that is used by SDL has two periods. */
	if (!isnan(audio_clock) && !muted) {
		pAudclk->set_clock_at(
			audio_clock - (double)(2 * audio_hw_buf_size + audio_write_buf_size) / audio_tgt.bytes_per_sec * pts_speed, 
			audio_clock_serial, 
			audio_callback_time / 1000000.0
		);
		Clock::sync_clock_to_slave(pExtclk, pAudclk);
	}
}

void VideoState::set_rate(double new_rate) {
	bool rate_found = false;
	char * rate_command = nullptr;
	for (int i = 0; i < FF_ARRAY_ELEMS(rate_speed_map); i++) {
		if (new_rate == rate_speed_map[i].clock_speed) {
			rate_found = true;
			rate_command = rate_speed_map[i].command;
		}
	}

	if (!rate_found) {
		return; // TODO(Reda): We can return and int != 0 as an error
	}
#if CONFIG_VIDEO_FILTER
	std::unique_lock<std::mutex> locker(mutex);
	vfilters = rate_command;
	vfilter_idx++;
	locker.unlock();
#endif
	rate = new_rate;
	new_rate_req = 1;
	continue_read_thread.notify_one();
}

double VideoState::get_rate() const {
	return rate;
}

int VideoState::get_master_clock_speed() {
	int speed = 0;
	if (this->video_stream >= 0
		&& this->audio_stream >= 0) {
		switch (this->get_master_sync_type()) {
		case AV_SYNC_VIDEO_MASTER:
			speed = pVidclk->get_clock_speed();
			break;
		case AV_SYNC_AUDIO_MASTER:
			speed = pAudclk->get_clock_speed();
			break;
		default:
			speed = pExtclk->get_clock_speed();
			break;
		}
	}
	return speed;
}

int VideoState::get_audio_disable() const { return audio_disable; }
void VideoState::set_audio_disable(const int disable) { audio_disable = disable; }

int VideoState::get_video_disable() const {	return video_disable; }
void VideoState::set_video_disable(const int disable) { video_disable = disable; }

int VideoState::get_subtitle_disable() const { return subtitle_disable; }
void VideoState::set_subtitle_disable(const int disable) { subtitle_disable = disable; }

#if CONFIG_VIDEO_FILTER
// From cmdutil class
double VideoState::get_rotation(AVStream *st) {
	uint8_t* displaymatrix = av_stream_get_side_data(st,
		AV_PKT_DATA_DISPLAYMATRIX, NULL);
	double theta = 0;
	if (displaymatrix)
		theta = -av_display_rotation_get((int32_t*)displaymatrix);

	theta -= 360 * floor(theta / 360 + 0.9 / 360);

	if (fabs(theta - 90 * round(theta / 90)) > 2)
		av_log(NULL, AV_LOG_WARNING, "Odd rotation angle.\n"
			"If you want to help, upload a sample "
			"of this file to ftp://upload.ffmpeg.org/incoming/ "
			"and contact the ffmpeg-devel mailing list. (ffmpeg-devel@ffmpeg.org)");

	return theta;
}
double VideoState::av_display_rotation_get(const int32_t matrix[9]) {
	double rotation, scale[2];

	scale[0] = hypot(CONV_FP(matrix[0]), CONV_FP(matrix[3]));
	scale[1] = hypot(CONV_FP(matrix[1]), CONV_FP(matrix[4]));

	if (scale[0] == 0.0 || scale[1] == 0.0)
		return NAN;

	rotation = atan2(CONV_FP(matrix[1]) / scale[1],
		CONV_FP(matrix[0]) / scale[0]) * 180 / M_PI;

	return -rotation;
}

int VideoState::configure_video_filters(AVFilterGraph *graph, const char *vfilters, AVFrame *frame)
{
	//enum AVPixelFormat pix_fmts[FF_ARRAY_ELEMS(sdl_texture_format_map)];
	char sws_flags_str[512] = "";
	char buffersrc_args[256];
	int ret;
	AVFilterContext *filt_src = NULL, *filt_out = NULL, *last_filter = NULL;
	AVCodecParameters *codecpar = video_st->codecpar;
	AVRational fr = av_guess_frame_rate(ic, video_st, NULL);
	AVDictionaryEntry *e = NULL;

	/*
	int nb_pix_fmts = 0;
	int i, j;

	for (i = 0; i < renderer_info.num_texture_formats; i++) {
	for (j = 0; j < FF_ARRAY_ELEMS(sdl_texture_format_map) - 1; j++) {
	if (renderer_info.texture_formats[i] == sdl_texture_format_map[j].texture_fmt) {
	pix_fmts[nb_pix_fmts++] = sdl_texture_format_map[j].format;
	break;
	}
	}
	}
	pix_fmts[nb_pix_fmts] = AV_PIX_FMT_NONE;
	*/

	while ((e = av_dict_get(sws_dict, "", e, AV_DICT_IGNORE_SUFFIX))) {
		if (!strcmp(e->key, "sws_flags")) {
			av_strlcatf(sws_flags_str, sizeof(sws_flags_str), "%s=%s:", "flags", e->value);
		}
		else
			av_strlcatf(sws_flags_str, sizeof(sws_flags_str), "%s=%s:", e->key, e->value);
	}
	if (strlen(sws_flags_str))
		sws_flags_str[strlen(sws_flags_str) - 1] = '\0';

	graph->scale_sws_opts = av_strdup(sws_flags_str);

	snprintf(buffersrc_args, sizeof(buffersrc_args),
		"video_size=%dx%d:pix_fmt=%d:time_base=%d/%d:pixel_aspect=%d/%d",
		frame->width, frame->height, frame->format,
		video_st->time_base.num, video_st->time_base.den,
		codecpar->sample_aspect_ratio.num, FFMAX(codecpar->sample_aspect_ratio.den, 1));
	if (fr.num && fr.den)
		av_strlcatf(buffersrc_args, sizeof(buffersrc_args), ":frame_rate=%d/%d", fr.num, fr.den);

	if ((ret = avfilter_graph_create_filter(&filt_src,
		avfilter_get_by_name("buffer"),
		"in", buffersrc_args, NULL,
		graph)) < 0)
		goto fail;

	ret = avfilter_graph_create_filter(&filt_out,
		avfilter_get_by_name("buffersink"),
		"out", NULL, NULL, graph);
	if (ret < 0)
		goto fail;

	//if ((ret = av_opt_set_int_list(filt_out, "pix_fmts", pix_fmts, AV_PIX_FMT_NONE, AV_OPT_SEARCH_CHILDREN)) < 0)
	//	goto fail;

	last_filter = filt_out;

	/* Note: this macro adds a filter before the lastly added filter, so the
	* processing order of the filters is in reverse */
#define INSERT_FILT(name, arg) do {                                          \
    AVFilterContext *filt_ctx;                                               \
                                                                             \
    ret = avfilter_graph_create_filter(&filt_ctx,                            \
                                       avfilter_get_by_name(name),           \
                                       name, arg, NULL, graph);    \
    if (ret < 0)                                                             \
        goto fail;                                                           \
                                                                             \
    ret = avfilter_link(filt_ctx, 0, last_filter, 0);                        \
    if (ret < 0)                                                             \
        goto fail;                                                           \
                                                                             \
    last_filter = filt_ctx;                                                  \
} while (0)

	if (autorotate) {
		double theta = get_rotation(video_st);

		if (fabs(theta - 90) < 1.0) {
			INSERT_FILT("transpose", "clock");
		}
		else if (fabs(theta - 180) < 1.0) {
			INSERT_FILT("hflip", NULL);
			INSERT_FILT("vflip", NULL);
		}
		else if (fabs(theta - 270) < 1.0) {
			INSERT_FILT("transpose", "cclock");
		}
		else if (fabs(theta) > 1.0) {
			char rotate_buf[64];
			snprintf(rotate_buf, sizeof(rotate_buf), "%f*PI/180", theta);
			INSERT_FILT("rotate", rotate_buf);
		}
	}

	if ((ret = configure_filtergraph(graph, vfilters, filt_src, last_filter)) < 0)
		goto fail;

	in_video_filter = filt_src;
	out_video_filter = filt_out;

fail:
	return ret;
}

int VideoState::get_vfilter_idx() {
	return vfilter_idx;
}

void VideoState::set_vfilter_idx(int idx) {
	vfilter_idx = idx;
}

int VideoState::get_nb_vfilters() const {
	return nb_vfilters;
}

int VideoState::opt_add_vfilter(const char *arg) {

	*vfilters_list = (const char *)grow_array(vfilters_list, sizeof(**vfilters_list), &nb_vfilters, nb_vfilters + 1);
	vfilters_list[nb_vfilters - 1] = arg;
	return 0;
}

#endif


#if CONFIG_AUDIO_FILTER || CONFIG_VIDEO_FILTER
int VideoState::configure_filtergraph(AVFilterGraph *graph, const char *filtergraph,
	AVFilterContext *source_ctx, AVFilterContext *sink_ctx)
{
	int ret, i;
	int nb_filters = graph->nb_filters;
	AVFilterInOut *outputs = NULL, *inputs = NULL;

	if (filtergraph) {
		outputs = avfilter_inout_alloc();
		inputs = avfilter_inout_alloc();
		if (!outputs || !inputs) {
			ret = AVERROR(ENOMEM);
			goto fail;
		}

		outputs->name = av_strdup("in");
		outputs->filter_ctx = source_ctx;
		outputs->pad_idx = 0;
		outputs->next = NULL;

		inputs->name = av_strdup("out");
		inputs->filter_ctx = sink_ctx;
		inputs->pad_idx = 0;
		inputs->next = NULL;

		if ((ret = avfilter_graph_parse_ptr(graph, filtergraph, &inputs, &outputs, NULL)) < 0)
			goto fail;
	}
	else {
		if ((ret = avfilter_link(source_ctx, 0, sink_ctx, 0)) < 0)
			goto fail;
	}

	/* Reorder the filters to ensure that inputs of the custom filters are merged first */
	for (i = 0; i < graph->nb_filters - nb_filters; i++)
		FFSWAP(AVFilterContext*, graph->filters[i], graph->filters[i + nb_filters]);

	ret = avfilter_graph_config(graph, NULL);
fail:
	avfilter_inout_free(&outputs);
	avfilter_inout_free(&inputs);
	return ret;
}

void *VideoState::grow_array(void *array, int elem_size, int *size, int new_size)
{
	if (new_size >= INT_MAX / elem_size) {
		av_log(NULL, AV_LOG_ERROR, "Array too big.\n");
		exit(1);
	}
	if (*size < new_size) {
		uint8_t *tmp = (uint8_t*)av_realloc_array(array, new_size, elem_size);
		if (!tmp) {
			av_log(NULL, AV_LOG_ERROR, "Could not alloc buffer.\n");
			exit(1);
		}
		memset(tmp + *size*elem_size, 0, (new_size - *size) * elem_size);
		*size = new_size;
		return tmp;
	}
	return array;
}
#endif  /* CONFIG_AUDIO_FILTER || CONFIG_VIDEO_FILTER */


#if CONFIG_AUDIO_FILTER
int VideoState::configure_audio_filters(const char *afilters, int force_output_format){
	static const enum AVSampleFormat sample_fmts[] = { AV_SAMPLE_FMT_S16, AV_SAMPLE_FMT_NONE };
	int sample_rates[2] = { 0, -1 };
	int64_t channel_layouts[2] = { 0, -1 };
	int channels[2] = { 0, -1 };
	AVFilterContext *filt_asrc = NULL, *filt_asink = NULL;
	char aresample_swr_opts[512] = "";
	AVDictionaryEntry *e = NULL;
	const AVFilter  *abuffer;
	const AVFilter  *abuffersink;
	char asrc_args[256];
	int ret;
	avfilter_graph_free(&agraph);
	agraph = avfilter_graph_alloc();

	if (!agraph) {
		av_log(NULL, AV_LOG_ERROR, "Unable to create filter graph.\n");
		return AVERROR(ENOMEM);
	}
	//while ((e = av_dict_get(swr_opts, "", e, AV_DICT_IGNORE_SUFFIX)))
	//	av_strlcatf(aresample_swr_opts, sizeof(aresample_swr_opts), "%s=%s:", e->key, e->value);

	//if (force_output_format) {
	//  // This does not work
	//	char aresample_value[64];
	//	sprintf(aresample_value, "%d", (int)(audio_tgt.freq * pts_speed) );
	//	av_strlcatf(aresample_swr_opts, sizeof(aresample_swr_opts), "%s=%s:", "aresample", aresample_value);
	//}
	
	if (strlen(aresample_swr_opts))
		aresample_swr_opts[strlen(aresample_swr_opts) - 1] = '\0';
	av_opt_set(agraph, "aresample_swr_opts", aresample_swr_opts, 0);

	ret = snprintf(asrc_args, sizeof(asrc_args),
		"sample_rate=%d:sample_fmt=%s:channels=%d:time_base=%d/%d",
		audio_filter_src.freq, av_get_sample_fmt_name(audio_filter_src.fmt),
		audio_filter_src.channels, 1, audio_filter_src.freq);

	if (audio_filter_src.channel_layout) {
		snprintf(asrc_args + ret, sizeof(asrc_args) - ret, ":channel_layout =0x%llx", audio_filter_src.channel_layout);
	}

	abuffer = avfilter_get_by_name("abuffer");
	if (!abuffer) {
		av_log(NULL, AV_LOG_ERROR, "Could not find the abuffer filter.\n");
		goto end;
	}

	ret = avfilter_graph_create_filter(&filt_asrc, abuffer, "ffplay_abuffer", asrc_args, NULL, agraph);
	if (ret < 0) {
		av_log(NULL, AV_LOG_ERROR, "Cannot create audio buffer source\n");
		goto end;
	}

	abuffersink = avfilter_get_by_name("abuffersink");
	if (!abuffersink) {
		av_log(NULL, AV_LOG_ERROR, "Cannot create audio buffer sink\n");
		goto end;
	}
	ret = avfilter_graph_create_filter(&filt_asink,	abuffersink, "ffplay_abuffersink", NULL, NULL, agraph);
	if (ret < 0) {
		av_log(NULL, AV_LOG_ERROR, "Cannot create audio buffer sink\n");
		goto end;
	}

	if ((ret = av_opt_set_int_list(filt_asink, "sample_fmts", sample_fmts, AV_SAMPLE_FMT_NONE, AV_OPT_SEARCH_CHILDREN)) < 0) {
		av_log(NULL, AV_LOG_ERROR, "configure_audio_filter, av_opt_set_int_list function failed\n");
		goto end;
	}

	if ((ret = av_opt_set_int(filt_asink, "all_channel_counts", 1, AV_OPT_SEARCH_CHILDREN)) < 0) {
		av_log(NULL, AV_LOG_ERROR, "configure_audio_filter, av_opt_set_int function failed\n");
		goto end;
	}

	if (force_output_format) {
		av_log(NULL, AV_LOG_INFO, "Is forcing the output format.\n");
		channel_layouts[0] = audio_tgt.channel_layout;
		channels[0] = audio_tgt.channels;
		sample_rates[0] = audio_tgt.freq;
		if ((ret = av_opt_set_int(filt_asink, "all_channel_counts", 0, AV_OPT_SEARCH_CHILDREN)) < 0)
			goto end;
		if ((ret = av_opt_set_int_list(filt_asink, "channel_layouts", channel_layouts, -1, AV_OPT_SEARCH_CHILDREN)) < 0)
			goto end;
		if ((ret = av_opt_set_int_list(filt_asink, "channel_counts", channels, -1, AV_OPT_SEARCH_CHILDREN)) < 0)
			goto end;
		if ((ret = av_opt_set_int_list(filt_asink, "sample_rates", sample_rates, -1, AV_OPT_SEARCH_CHILDREN)) < 0)
			goto end;

		//if ((ret = av_opt_set_double(filt_asink, "atempo", pts_speed, AV_OPT_SEARCH_CHILDREN)) < 0)
		//	goto end;
	}

	if ((ret = configure_filtergraph(agraph, afilters, filt_asrc, filt_asink)) < 0) {
		av_log(NULL, AV_LOG_INFO, "configure_audio_filter: Unable to configure agraph.\n");
		goto end;
	}

	in_audio_filter = filt_asrc;
	out_audio_filter = filt_asink;

end:
	if (ret < 0) {
		avfilter_graph_free(&agraph);
	}

	return ret;
}
#endif  /* CONFIG_AUDIO_FILTER */

