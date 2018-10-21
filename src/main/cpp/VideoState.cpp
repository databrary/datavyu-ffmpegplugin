#include "VideoState.h"

/* Private Members */
int VideoState::stream_component_open(int stream_index) {
	AVCodecContext *avctx;
	AVCodec *codec;
	const char *forced_codec_name = NULL;
	AVDictionary *opts = NULL;
	AVDictionaryEntry *t = NULL;
	int sample_rate, nb_channels;
	int64_t channel_layout;
	int ret = 0;

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
			last_audio_stream = stream_index;
			break;
		case AVMEDIA_TYPE_SUBTITLE:
			last_subtitle_stream = stream_index;
			break;
		case AVMEDIA_TYPE_VIDEO:
			last_video_stream = stream_index;
			break;
	}

	avctx->codec_id = codec->id;

	if (ENABLE_FAST_DECODE)
		avctx->flags2 |= AV_CODEC_FLAG2_FAST;

	opts = filter_codec_opts(codec_opts, avctx->codec_id, ic, ic->streams[stream_index], codec);
	if (!av_dict_get(opts, "threads", NULL, 0))
		av_dict_set(&opts, "threads", "auto", 0);
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

		sample_rate = avctx->sample_rate;
		nb_channels = avctx->channels;
		channel_layout = avctx->channel_layout;

		/* prepare audio output */
		if (!audio_open_callback)
			goto fail;

		if ((ret = audio_open_callback(channel_layout, nb_channels, sample_rate, &audio_tgt) < 0))
			goto fail;
		audio_hw_buf_size = ret;
		audio_src = audio_tgt;
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
		if ((ret = pAuddec->start([this] { audio_thread(); })) < 0)
			goto out;
		if (pause_audio_device_callback)
			pause_audio_device_callback();
		break;
	case AVMEDIA_TYPE_VIDEO:
		image_width = avctx->width;
		image_height = avctx->height;
		image_sample_aspect_ratio = avctx->sample_aspect_ratio;

		// TODO(fraudies): Alignment for the source does not seem to be necessary, but test with more res
		// avcodec_align_dimensions(avctx, &avctx->width, &avctx->height);

		video_stream = stream_index;
		video_st = ic->streams[stream_index];

		// Calculate the Frame rate (FPS) of the video stream
		if (video_st) {
			AVRational f = av_guess_frame_rate(ic, video_st, NULL);
			AVRational rational = video_st->avg_frame_rate;
			if(rational.den == rational.num == 0)
				rational = video_st->r_frame_rate;

			fps = rational.num / rational.den;
		}

		pViddec = new Decoder(avctx, pVideoq, &continue_read_thread);
		if ((ret = pViddec->start([this] { video_thread(); })) < 0)
			goto out;
		queue_attachments_req = 1;
		break;
	case AVMEDIA_TYPE_SUBTITLE:
		subtitle_stream = stream_index;
		subtitle_st = ic->streams[stream_index];
		pSubdec = new Decoder(avctx, pSubtitleq, &continue_read_thread);
		if ((ret = pSubdec->start([this] { subtitle_thread(); })) < 0)
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
		double time = frame->pts != AV_NOPTS_VALUE ? av_q2d(video_st->time_base) * frame->pts : NAN;

		frame->sample_aspect_ratio = av_guess_sample_aspect_ratio(ic, video_st, frame);

		if (get_master_sync_type() != AV_SYNC_VIDEO_MASTER) {
			if (frame->pts != AV_NOPTS_VALUE) {
				double diff = time - get_master_clock()->get_time();

				av_log(NULL, AV_LOG_TRACE, "diff=%f time=%f np=%d\n", diff, time, pVideoq->get_nb_packets());

				if (!isnan(diff) && fabs(diff) < AV_NOSYNC_THRESHOLD &&
					diff < 0 &&
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
		delete pAuddec;
		swr_free(&swr_ctx);
		av_freep(&audio_buf1);
		audio_buf1_size = 0;
		audio_buf = NULL;
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

/* Ported from cmdutils */
int VideoState::check_stream_specifier(AVFormatContext *s, AVStream *st, const char *spec) {
	int ret = avformat_match_stream_specifier(s, st, spec);
	if (ret < 0)
		av_log(s, AV_LOG_ERROR, "Invalid stream specifier: %s.\n", spec);
	return ret;
}

/* Ported from cmdutils*/
AVDictionary *VideoState::filter_codec_opts(AVDictionary *opts, enum AVCodecID codec_id, AVFormatContext *s,
	AVStream *st, AVCodec *codec) {
	AVDictionary *ret = NULL;
	AVDictionaryEntry *t = NULL;
	int flags = s->oformat ? AV_OPT_FLAG_ENCODING_PARAM : AV_OPT_FLAG_DECODING_PARAM;
	char prefix = 0;
	const AVClass *cc = avcodec_get_class();

	if (!codec) {
		codec = s->oformat ? avcodec_find_encoder(codec_id) : avcodec_find_decoder(codec_id);
	}

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
		if (p) {
			switch (check_stream_specifier(s, st, p + 1)) {
			case  1: *p = 0; break;
			case  0:         continue;
			default:         exit(1); // TODO(fraudies): We need to handle this differently
			}
		}

		if (av_opt_find(&cc, t->key, NULL, flags, AV_OPT_SEARCH_FAKE_OBJ) ||
			!codec ||
			(codec->priv_class &&
				av_opt_find(&codec->priv_class, t->key, NULL, flags,
					AV_OPT_SEARCH_FAKE_OBJ))) {
			av_dict_set(&ret, t->key, t->value, 0);
		}
		else if (t->key[0] == prefix &&
			av_opt_find(&cc, t->key + 1, NULL, flags,
				AV_OPT_SEARCH_FAKE_OBJ)) {
			av_dict_set(&ret, t->key + 1, t->value, 0);
		}

		if (p) {
			*p = ':';
		}
	}
	return ret;
}

AVDictionary **VideoState::setup_find_stream_info_opts(AVFormatContext *s, AVDictionary *codec_opts) {
	AVDictionary **opts;
	if (!s->nb_streams) { 
		return NULL; 
	}
	opts = (AVDictionary**)av_mallocz_array(s->nb_streams, sizeof(*opts));
	if (!opts) {
		av_log(NULL, AV_LOG_ERROR, "Could not alloc memory for stream options.\n");
		return NULL;
	}
	for (int i = 0; i < s->nb_streams; i++) {
		opts[i] = filter_codec_opts(codec_opts, s->streams[i]->codecpar->codec_id,
			s, s->streams[i], NULL);
	}
	return opts;
}

int VideoState::is_realtime(AVFormatContext *s) {
	return !strcmp(s->iformat->name, "rtp")
		|| !strcmp(s->iformat->name, "rtsp")
		|| !strcmp(s->iformat->name, "sdp")
		|| (s->pb && (!strncmp(s->url, "rtp:", 4) || !strncmp(s->url, "udp:", 4)));
}

int VideoState::synchronize_audio(int nb_samples) {
	int wanted_nb_samples = nb_samples;

	/* if not master, then we try to remove or add samples to correct the clock */
	if (get_master_sync_type() != AV_SYNC_AUDIO_MASTER) {
		double diff, avg_diff;
		int min_nb_samples, max_nb_samples;

		diff = pAudclk->get_time() - get_master_clock()->get_time();

		if (!isnan(diff) && fabs(diff) < AV_NOSYNC_THRESHOLD) {
			audio_diff_cum = diff + audio_diff_avg_coef * audio_diff_cum;
			if (audio_diff_avg_count < AUDIO_DIFF_AVG_NB) {
				/* not enough measures to have a correct estimate */
				audio_diff_avg_count++;
			}
			else {
				/* estimate the A-V difference */
				avg_diff = audio_diff_cum * (1.0 - audio_diff_avg_coef);

				if (fabs(avg_diff) >= audio_diff_threshold) {
					wanted_nb_samples = nb_samples + (int)(diff * audio_src.freq);
					min_nb_samples = ((nb_samples * (100 - SAMPLE_CORRECTION_PERCENT_MAX) / 100));
					max_nb_samples = ((nb_samples * (100 + SAMPLE_CORRECTION_PERCENT_MAX) / 100));
					wanted_nb_samples = av_clip(wanted_nb_samples, min_nb_samples, max_nb_samples);
				}
				av_log(NULL, AV_LOG_TRACE, "diff=%f adiff=%f sample_diff=%d apts=%0.3f %f\n",
					diff, avg_diff, wanted_nb_samples - nb_samples,
					audio_pts, audio_diff_threshold);
			}
		}
		else {
			/* too big difference : may be initial PTS errors, so reset A-V filter */
			audio_diff_avg_count = 0;
			audio_diff_cum = 0;
		}
	}
	return wanted_nb_samples;
}

int VideoState::audio_decode_frame() {
	int data_size, resampled_data_size;
	int64_t dec_channel_layout;
	int wanted_nb_samples;
	Frame *af;
	double original_sample_rate;

	if (paused)
		return -1;

	do {
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

	original_sample_rate = af->frame->sample_rate;
	// Change the sample_rate by the playback rate
	af->frame->sample_rate *= rate_value;

	if (af->frame->format != audio_src.fmt ||
		dec_channel_layout != audio_src.channel_layout ||
		af->frame->sample_rate != audio_src.freq ||
		(wanted_nb_samples != af->frame->nb_samples && !swr_ctx)) {
		swr_free(&swr_ctx);
		swr_ctx = swr_alloc_set_opts(NULL,
			audio_tgt.channel_layout, audio_tgt.fmt, audio_tgt.freq,
			dec_channel_layout, static_cast<AVSampleFormat>(af->frame->format), af->frame->sample_rate,
			0, NULL);
		if (!swr_ctx || swr_init(swr_ctx) < 0) {
			av_log(NULL, AV_LOG_ERROR,
				"Cannot create sample rate converter for conversion of %d Hz %s %d channels to %d Hz %s %d channels!\n",
				af->frame->sample_rate, av_get_sample_fmt_name(static_cast<AVSampleFormat>(af->frame->format)), af->frame->channels,
				audio_tgt.freq, av_get_sample_fmt_name(audio_tgt.fmt), audio_tgt.channels);
			swr_free(&swr_ctx);
			return -1;
		}
		audio_src.channel_layout = dec_channel_layout;
		audio_src.channels = af->frame->channels;
		audio_src.freq = af->frame->sample_rate;
		audio_src.fmt = static_cast<AVSampleFormat>(af->frame->format);
	}

	if (swr_ctx) {
		const uint8_t **in = (const uint8_t **)af->frame->extended_data;
		uint8_t **out = &audio_buf1;
		int out_count = (int64_t)wanted_nb_samples * audio_tgt.freq / af->frame->sample_rate + 256;
		int out_size = av_samples_get_buffer_size(NULL, audio_tgt.channels, out_count, audio_tgt.fmt, 0);
		int len2;
		if (out_size < 0) {
			av_log(NULL, AV_LOG_ERROR, "av_samples_get_buffer_size() failed\n");
			return -1;
		}
		if (wanted_nb_samples != af->frame->nb_samples) {
			if (swr_set_compensation(swr_ctx, (wanted_nb_samples - af->frame->nb_samples) * audio_tgt.freq / af->frame->sample_rate,
				wanted_nb_samples * audio_tgt.freq / af->frame->sample_rate) < 0) {
				av_log(NULL, AV_LOG_ERROR, "swr_set_compensation() failed\n");
				return -1;
			}
		}
		av_fast_malloc(&audio_buf1, &audio_buf1_size, out_size);
		if (!audio_buf1)
			return AVERROR(ENOMEM);
		len2 = swr_convert(swr_ctx, out, out_count, in, af->frame->nb_samples);
		if (len2 < 0) {
			av_log(NULL, AV_LOG_ERROR, "swr_convert() failed\n");
			return -1;
		}
		if (len2 == out_count) {
			av_log(NULL, AV_LOG_WARNING, "audio buffer is probably too small\n");
			if (swr_init(swr_ctx) < 0)
				swr_free(&swr_ctx);
		}
		audio_buf = audio_buf1;
		resampled_data_size = len2 * audio_tgt.channels * av_get_bytes_per_sample(audio_tgt.fmt);
	}
	else {
		audio_buf = af->frame->data[0];
		resampled_data_size = data_size;
	}

	/* update the audio clock with the pts */
	audio_pts = isnan(af->pts) ? NAN : af->pts + (double)af->frame->nb_samples / original_sample_rate;
	audio_serial = af->serial;

	return resampled_data_size;
}

// Note, queues and clocks get initialized in the create_video_state function
// The initialization order is correct now, but it is not garuanteed that some
// of these might not be null; hence, we initialize this in the create function
VideoState::VideoState(int audio_buffer_size) : 
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
	vidclk_last_set_time(0),
	video_stream(0),
	max_frame_duration(0),
	eof(0),
	video_duration(0),
	image_width(0),
	image_height(0),
	image_sample_aspect_ratio(av_make_q(0, 0)),
	step(false),
	new_rate_req(0),
	new_rate_value(1.0),
	rate_value(1.0),
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
	audio_pts(0.0),
	audio_serial(0),
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
	frame_drops_early(0)
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

VideoState::~VideoState() {

	// From stream close
	abort_request = 1;
	if (read_tid) {
		read_tid->join();
		delete read_tid;
		read_tid = nullptr;
	}

	if (audio_stream >= 0) {
		stream_component_close(audio_stream);
	}
	if (video_stream >= 0) {
		stream_component_close(video_stream);
	}
	if (subtitle_stream >= 0) {
		stream_component_close(subtitle_stream);
	}

	if (ic) {
		avformat_close_input(&ic);
	}
	av_free(filename);
	// End stream close

	// From close
	if (pVideoq) delete(pVideoq);
	if (pAudioq) delete(pAudioq);
	if (pSubtitleq) delete(pSubtitleq);

	if (pPictq) delete(pPictq);
	if (pSubpq) delete(pSubpq);
	if (pSampq) delete(pSampq);
	
	if (pVidclk) delete(pVidclk);
	if (pAudclk) delete(pAudclk);
	if (pExtclk) delete(pExtclk);
}

//* Gets the stream from the disk or the network */
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
		if (abort_request)
			break;
		if (paused != last_paused) {
			last_paused = paused;
			if (paused) {
				if (stopped) {
					if (player_state_callbacks[TO_STOPPED]) {
						player_state_callbacks[TO_STOPPED]();
					}
				}
				else {
					if (player_state_callbacks[TO_PAUSED]) {
						player_state_callbacks[TO_PAUSED]();
					}
				}
				read_pause_return = av_read_pause(ic);
			}
			else {
				av_read_play(ic); // Start Playing a network based stream
				if (player_state_callbacks[TO_PLAYING]) {
					player_state_callbacks[TO_PLAYING]();
				}
			}
		}
		if (was_stalled) {
			if (paused) {
				if (stopped) {
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
			rate_value = new_rate_value;
			new_rate_req = 0;
			queue_attachments_req = 1;
		}

		if (seek_req) {
			if (player_state_callbacks[TO_STALLED]) {
				player_state_callbacks[TO_STALLED]();
				was_stalled = true;
			}

			int64_t seek_target = seek_pos;
			int64_t seek_min = seek_rel > 0 ? seek_target - seek_rel + 2 : INT64_MIN;
			int64_t seek_max = seek_rel < 0 ? seek_target - seek_rel - 2 : INT64_MAX;
			// FIXME the +-2 is due to rounding being not done in the correct direction in generation
			//      of the seek_pos/seek_rel variables
			ret = avformat_seek_file(ic, -1, seek_min, seek_target, seek_max, seek_flags);
			if (ret < 0) {
				av_log(NULL, AV_LOG_ERROR, "%s: error while seeking\n", ic->url);
			}
			else {
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
				if (seek_flags & AVSEEK_FLAG_BYTE) {
					pExtclk->set_time(NAN, 0); // 0 != -1 which will return NAN for interim time 
				}
				else {
					pExtclk->set_time(seek_target / (double)AV_TIME_BASE, 0); // 0 != -1 which will return NAN for interim time 
				}
			}
			seek_req = 0;
			queue_attachments_req = 1;
			eof = 0;
			av_log(NULL, AV_LOG_INFO,
				"Seek: ext: %7.2f sec - aud : %7.2f sec - vid : %7.2f sec - Error : %7.2f sec\n",
				get_pExtclk()->get_time(),
				get_pAudclk()->get_time(),
				get_pVidclk()->get_time(),
				fabs(get_pExtclk()->get_time() - get_pAudclk()->get_time()));

			if (paused) {
				step_to_next_frame_callback(); // Assume that the step callback is set -- otherwise fail hard here
			} else {
				if (player_state_callbacks[TO_PLAYING]) {
					player_state_callbacks[TO_PLAYING]();
				}
				was_stalled = false;
			}
		}
		if (queue_attachments_req) {
			if (video_st && video_st->disposition & AV_DISPOSITION_ATTACHED_PIC) {
				AVPacket copy = { 0 };
				if ((ret = av_packet_ref(&copy, &video_st->attached_pic)) < 0)
					goto fail;
				pVideoq->put(&copy);
				pVideoq->put_null_packet(video_stream);
			}
			queue_attachments_req = 0;
		}

		/* if the queues are full, no need to read more */
		if (!realtime &&
			(pAudioq->get_size() + pVideoq->get_size() + pSubtitleq->get_size() > MAX_QUEUE_SIZE
				|| (stream_has_enough_packets(audio_st, audio_stream, pAudioq) &&
					stream_has_enough_packets(video_st, video_stream, pVideoq) &&
					stream_has_enough_packets(subtitle_st, subtitle_stream, pSubtitleq)))) {
			/* wait 10 ms */
			std::unique_lock<std::mutex> locker(wait_mutex);
			continue_read_thread.wait_for(locker, std::chrono::milliseconds(10));
			locker.unlock();
			continue;
		}
		if (!paused &&
			(!audio_st || (pAuddec->is_finished() == pAudioq->get_serial() && pSampq->nb_remaining() == 0)) &&
			(!video_st || (pViddec->is_finished() == pVideoq->get_serial() && pPictq->nb_remaining() == 0))) {
			if (loop != 1 && (!loop || --loop)) {
				stream_seek(start_time != AV_NOPTS_VALUE ? start_time : 0, 0, 0);
			}
		}
		ret = av_read_frame(ic, pkt);
		if (ret < 0) {
			if ((ret == AVERROR_EOF || avio_feof(ic->pb)) && !eof) {
				if (video_stream >= 0)
					pVideoq->put_null_packet(video_stream);
				if (audio_stream >= 0)
					pAudioq->put_null_packet(audio_stream);
				if (subtitle_stream >= 0)
					pSubtitleq->put_null_packet(subtitle_stream);
				eof = 1;

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
			eof = 0;
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

		if (pkt->stream_index == audio_stream && pkt_in_play_range) {
			pAudioq->put(pkt);
		}
		else if (pkt->stream_index == video_stream && pkt_in_play_range
			&& !(video_st->disposition & AV_DISPOSITION_ATTACHED_PIC)) {
			pVideoq->put(pkt);
		}
		else if (pkt->stream_index == subtitle_stream && pkt_in_play_range) {
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

			if (!(af = pSampq->peek_writable()))
				goto the_end;

			av_log(NULL, AV_LOG_TRACE, "Audio frame pts = %I64d, tb = %2.7f\n", frame->pts, av_q2d(tb));

			af->pts = (frame->pts == AV_NOPTS_VALUE) ? NAN : frame->pts * av_q2d(tb);
			af->pos = frame->pkt_pos;
			af->serial = pAuddec->get_pkt_serial();
			af->duration = av_q2d(av_make_q(frame->nb_samples, frame->sample_rate));

			av_frame_move_ref(af->frame, frame);
			pSampq->push();
		}
	} while (ret >= 0 || ret == AVERROR(EAGAIN) || ret == AVERROR_EOF);
the_end:
	av_frame_free(&frame);
	return ret;
}

/* Called when the stream is opened */
int VideoState::video_thread() {
	AVFrame *frame = av_frame_alloc();
	double pts;
	double duration;
	int ret;
	AVRational tb = video_st->time_base;
	AVRational frame_rate = av_guess_frame_rate(ic, video_st, NULL);

	if (!frame) {
		return AVERROR(ENOMEM);
	}

	for (;;) {
		ret = get_video_frame(frame);
		if (ret < 0)
			goto the_end;
		if (!ret)
			continue;

		av_log(NULL, AV_LOG_TRACE, " Video frame pts = %I64d, tb = %2.7f\n", frame->pts, av_q2d(tb));

		duration = (frame_rate.num && frame_rate.den) ? av_q2d(av_make_q(frame_rate.den, frame_rate.num)) : 0;
		pts = (frame->pts == AV_NOPTS_VALUE) ? NAN : frame->pts * av_q2d(tb);
		ret = queue_picture(frame, pts, duration, frame->pkt_pos, pViddec->get_pkt_serial());
		av_frame_unref(frame);

		if (ret < 0)
			goto the_end;
	}
the_end:
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
			pSubpq->push();
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
	if (!is) {
		return NULL;
	}

	is->filename = av_strdup(filename);
	if (!is->filename) {
		return NULL;
	}

	is->iformat = iformat;

	is->audio_serial = -1;

	is->av_sync_type = AV_SYNC_AUDIO_MASTER;

	return is;
}

int VideoState::stream_start() {
	int i, ret;
	int st_index[AVMEDIA_TYPE_NB];
	AVDictionaryEntry *t;
	int scan_all_pmts_set = 0;
	AVDictionary *format_opts = NULL;
	AVDictionary *codec_opts = NULL;
	AVDictionary **opts;

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

	if (ENABLE_GENERATE_PTS) {
		ic->flags |= AVFMT_FLAG_GENPTS;
	}

	av_format_inject_global_side_data(ic);

	// Find the stream information
	opts = setup_find_stream_info_opts(ic, codec_opts);
	ret = avformat_find_stream_info(ic, opts);
	for (i = 0; i < ic->nb_streams; i++) {
		av_dict_free(&opts[i]);
	}
	av_freep(&opts);

	if (ret < 0) {
		av_log(NULL, AV_LOG_WARNING, "%s: could not find codec parameters\n", filename);
		goto fail;
	}
	
	if (ic->pb)
		ic->pb->eof_reached = 0; // FIXME hack, ffplay maybe should not use avio_feof() to test for the end

	if (seek_by_bytes < 0)
		seek_by_bytes = !!(ic->iformat->flags & AVFMT_TS_DISCONT) && strcmp("ogg", ic->iformat->name);

	max_frame_duration = (ic->iformat->flags & AVFMT_TS_DISCONT) ? 10.0 : 3600.0;

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

	if (ENABLE_SHOW_STATUS) {
		av_dump_format(ic, 0, filename, 0);
	}

	for (i = 0; i < ic->nb_streams; i++) {
		AVStream *st = ic->streams[i];
		enum AVMediaType type = st->codecpar->codec_type;
		st->discard = AVDISCARD_ALL;
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
	// Set the video duration
	video_duration = ic->duration / (double)AV_TIME_BASE;

	ret = -1;
	if (st_index[AVMEDIA_TYPE_VIDEO] >= 0) {
		ret = stream_component_open(st_index[AVMEDIA_TYPE_VIDEO]);
	}

	if (st_index[AVMEDIA_TYPE_SUBTITLE] >= 0) {
		stream_component_open(st_index[AVMEDIA_TYPE_SUBTITLE]);
	}

	if (video_stream < 0 && audio_stream < 0) {
		av_log(NULL, AV_LOG_FATAL, "Failed to open file '%s' or configure filtergraph\n", filename);
		ret = AVERROR_STREAM_NOT_FOUND;
		goto fail;
	}

	if (player_state_callbacks[TO_READY]) {
		player_state_callbacks[TO_READY]();
	}

	read_tid = new (std::nothrow) std::thread([this] { read_thread(); });
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

// Function Called from the event loop
void VideoState::seek_chapter(int incr) {
	int64_t pos = get_master_clock()->get_time() * AV_TIME_BASE;
	int i;

	if (!ic->nb_chapters)
		return;

	/* find the current chapter */
	for (i = 0; i < ic->nb_chapters; i++) {
		AVChapter *ch = ic->chapters[i];
		if (av_compare_ts(pos, MY_AV_TIME_BASE_Q, ch->start, ch->time_base) < 0) {
			i--;
			break;
		}
	}

	i += incr;
	i = FFMAX(i, 0);
	if (i >= ic->nb_chapters)
		return;

	av_log(NULL, AV_LOG_VERBOSE, "Seeking to chapter %d.\n", i);
	stream_seek(av_rescale_q(ic->chapters[i]->start, ic->chapters[i]->time_base, MY_AV_TIME_BASE_Q), 0, 0);
}

void VideoState::update_pts(double pts, int serial) {
	get_pVidclk()->set_time(pts, serial);
	vidclk_last_set_time = av_gettime_relative() / 1000000.0;
	// Sync external clock to video clock
	Clock::sync_slave_to_master(get_pExtclk(), get_pVidclk());
}

/* seek in the stream */
void VideoState::stream_seek(int64_t pos, int64_t rel, int seek_by_bytes) {
	if (!seek_req) {
		seek_pos = pos;
		seek_rel = rel;
		seek_flags &= ~AVSEEK_FLAG_BYTE;
		if (seek_by_bytes)
			seek_flags |= AVSEEK_FLAG_BYTE;
		seek_req = 1;
		continue_read_thread.notify_one();
	}
}

// Lot's of discussion around big endian (may have to clean this up)
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

double VideoState::compute_target_delay(double delay) {
	double sync_threshold, diff = 0;

	/* update delay to follow master synchronisation source */
	if (get_master_sync_type() != AV_SYNC_VIDEO_MASTER) {
		/* if video is slave, we try to correct big delays by
		duplicating or deleting a frame */
		diff = pVidclk->get_time() - get_master_clock()->get_time();

		/* skip or repeat frame. We take into account the
		delay to compute the threshold. I still don't know
		if it is the best guess */
		sync_threshold = FFMAX(AV_SYNC_THRESHOLD_MIN, FFMIN(AV_SYNC_THRESHOLD_MAX, delay));
		if (!isnan(diff) && fabs(diff) < max_frame_duration) {
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

Clock* VideoState::get_master_clock() const {
	int master = get_master_sync_type();
	if (master == AV_SYNC_VIDEO_MASTER) {
		return pVidclk;
	}
	if (master == AV_SYNC_AUDIO_MASTER) {
		return pAudclk;
	}
	return pExtclk;
}

/* prepare a new audio buffer */
void VideoState::audio_callback(uint8_t *stream, int len) {
	int audio_size, len1;

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
	if (!isnan(audio_pts) && !muted) {
		/* Let's assume the audio driver that is used by SDL has two periods. */
		pAudclk->set_time(
			audio_pts - (double)(2 * audio_hw_buf_size + audio_write_buf_size) / audio_tgt.bytes_per_sec,
			audio_serial);
		// Sync external clock to audio clock
		Clock::sync_slave_to_master(pExtclk, pAudclk);
	}
}

int VideoState::set_rate(double new_rate) {
	// If we request a different rates
	if (rate_value != new_rate) {
		new_rate_value = new_rate;
		new_rate_req = 1;
		continue_read_thread.notify_one();
	}
	return 0;
}
