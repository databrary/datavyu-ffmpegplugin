#include "VideoState.h"

int VideoState::kSeekPreciseFlag = 0x01;
int VideoState::kSeekFastFlag = 0x10;
int VideoState::kSeekFrameFlag = 0x11;
bool VideoState::kEnableShowFormat = true; // Show the format information
bool VideoState::kEnableFastDecode = false;
bool VideoState::kEnableGeneratePts =
    false; // generate missing pts for audio if it means parsing future frames
int VideoState::kEnableSeekByBytes =
    0; // seek by bytes 0=off 1=on -1=auto (Note: we disable seek_by_byte
       // because it raises errors while seeking)
int VideoState::kMaxQueueSize = (15 * 1024 * 1024);
int VideoState::kMinFrames = 25;

/* no AV sync correction is done if below the minimum AV sync threshold */
double VideoState::kAvSyncThresholdMin = 0.04;
/* AV sync correction is done if above the maximum AV sync threshold */
double VideoState::kAvSyncThresholdMax = 0.1;
/* If a frame duration is longer than this, it will not be duplicated to
 * compensate AV sync */
double VideoState::kAvSyncFrameDupThreshold = 0.1;
/* no AV correction is done if too big error */
double VideoState::kAvNoSyncThreshold = 10.0;
/* maximum audio speed change to get correct sync */
int VideoState::kSampleCorrectionMaxPercent = 10; // int
/* we use about AUDIO_DIFF_AVG_NB A-V differences to make the average */
int VideoState::kAudioDiffAvgNum = 20; // int

int VideoState::kVideoPictureQueueSize = 3; // int
int VideoState::kSampleQueueSize = 9;       // int

int VideoState::OpenStreamComponent(int stream_index) {
  AVCodecContext *p_codec_context;
  AVCodec *p_codec;
  AVDictionary *p_dict = nullptr;
  AVDictionaryEntry *p_entry = nullptr;
  int sample_rate;
  int num_channels;
  int64_t channel_layout;
  int ret = 0;

  AVDictionary *codec_opts = nullptr;

  if (stream_index < 0 || stream_index >= p_format_context->nb_streams) {
    return -1;
  }

  p_codec_context = avcodec_alloc_context3(nullptr);
  if (!p_codec_context) {
    return AVERROR(ENOMEM);
  }

  ret = avcodec_parameters_to_context(
      p_codec_context, p_format_context->streams[stream_index]->codecpar);
  if (ret < 0)
    goto fail;
  p_codec_context->pkt_timebase =
      p_format_context->streams[stream_index]->time_base;

  p_codec = avcodec_find_decoder(p_codec_context->codec_id);

  switch (p_codec_context->codec_type) {
  case AVMEDIA_TYPE_AUDIO:
    last_audio_stream_ = stream_index;
    break;
  case AVMEDIA_TYPE_VIDEO:
    last_video_stream_ = stream_index;
    break;
  }

  p_codec_context->codec_id = p_codec->id;

  if (kEnableFastDecode) {
    p_codec_context->flags2 |= AV_CODEC_FLAG2_FAST;
  }

  ret = GetFilterCodecOptions(p_dict, *codec_opts, p_codec_context->codec_id,
                              p_format_context,
                              p_format_context->streams[stream_index], p_codec);
  if (ret) {
    goto fail;
  }

  if (!av_dict_get(p_dict, "threads", NULL, 0)) {
    av_dict_set(&p_dict, "threads", "auto", 0);
  }
  if (p_codec_context->codec_type == AVMEDIA_TYPE_VIDEO ||
      p_codec_context->codec_type == AVMEDIA_TYPE_AUDIO) {
    av_dict_set(&p_dict, "refcounted_frames", "1", 0);
  }
  if ((ret = avcodec_open2(p_codec_context, p_codec, &p_dict)) < 0) {
    goto fail;
  }
  if ((p_entry = av_dict_get(p_dict, "", NULL, AV_DICT_IGNORE_SUFFIX))) {
    av_log(NULL, AV_LOG_ERROR, "Option %s not found.\n", p_entry->key);
    ret = AVERROR_OPTION_NOT_FOUND;
    goto fail;
  }

  end_of_file_ = false; // Is initilized in the constructor
  p_format_context->streams[stream_index]->discard = AVDISCARD_DEFAULT;

  switch (p_codec_context->codec_type) {
  case AVMEDIA_TYPE_AUDIO:

    sample_rate = p_codec_context->sample_rate;
    num_channels = p_codec_context->channels;
    channel_layout = p_codec_context->channel_layout;

    // prepare audio output
    if (!audio_open_callback) {
      goto fail;
    }

    if ((ret = audio_open_callback(channel_layout, num_channels, sample_rate,
                                   &audio_params_target_) < 0)) {
      goto fail;
    }
    audio_hw_buffer_size_ = ret;
    audio_parms_source_ = audio_params_target_;
    audio_buffer_size_ = 0;
    audio_buffer_index_ = 0;

    // initialize the averaging filter
    audio_diff_avg_coef_ = exp(log(0.01) / kAudioDiffAvgNum);
    audio_diff_avg_count_ = 0;

    // Since we do not have a precise enough audio FIFO fullness,
    // we correct audio sync only if larger than this threshold
    audio_diff_threshold_ =
        (double)(audio_hw_buffer_size_) / audio_params_target_.bytes_per_sec_;

    audio_stream_index_ = stream_index;
    p_audio_stream_ = p_format_context->streams[stream_index];
    p_audio_decoder_ = new Decoder(p_codec_context, p_audio_packet_queue_,
                                   &continue_read_thread_);
    if ((p_format_context->iformat->flags &
         (AVFMT_NOBINSEARCH | AVFMT_NOGENSEARCH | AVFMT_NO_BYTE_SEEK)) &&
        !p_format_context->iformat->read_seek) {
      p_audio_decoder_->SetStartPts(p_audio_stream_->start_time);
      p_audio_decoder_->SetStartPtsTimebase(p_audio_stream_->time_base);
    }
    if ((ret = p_audio_decoder_->Start(
             [this] { DecodeAudioPacketsToFrames(); })) < 0) {
      goto out;
    }
    if (pause_audio_device_callback) {
      pause_audio_device_callback();
    }
    break;
  case AVMEDIA_TYPE_VIDEO:
    frame_width_ = p_codec_context->width;
    frame_height_ = p_codec_context->height;
    frame_aspect_ratio_ = p_codec_context->sample_aspect_ratio;

    // TODO(fraudies): Alignment for the source does not seem to be necessary,
    // but test with more res avcodec_align_dimensions(avctx, &avctx->width,
    // &avctx->height);

    image_stream_index_ = stream_index;
    p_image_stream_ = p_format_context->streams[stream_index];

    // Calculate the Frame rate (FPS) of the video stream
    if (p_image_stream_) {
      AVRational f =
          av_guess_frame_rate(p_format_context, p_image_stream_, NULL);
      AVRational rational = p_image_stream_->avg_frame_rate;
      if (rational.den == rational.num == 0)
        rational = p_image_stream_->r_frame_rate;

      frame_rate_ = (float)rational.num / rational.den;
    }

    p_image_decoder_ = new Decoder(p_codec_context, p_image_packet_queue_,
                                   &continue_read_thread_);
    if ((ret = p_image_decoder_->Start(
             [this] { DecodeImagePacketsToFrames(); })) < 0) {
      goto out;
    }
    queue_attachments_request_ = true;
    break;
  default:
    break;
  }
  goto out;

fail:
  avcodec_free_context(&p_codec_context);
out:
  av_dict_free(&p_dict);

  return ret;
}

int VideoState::GetImageFrame(AVFrame *frame) {
  int got_frame;
  Clock *p_master_clock = nullptr;
  GetMasterClock(&p_master_clock);

  if ((got_frame = p_image_decoder_->Decode(frame)) < 0) {
    return -1;
  }

  if (got_frame) {
    double time = frame->pts != AV_NOPTS_VALUE
                      ? av_q2d(p_image_stream_->time_base) * frame->pts
                      : NAN;

    frame->sample_aspect_ratio =
        av_guess_sample_aspect_ratio(p_format_context, p_image_stream_, frame);

    if (GetMasterSyncType() != AV_SYNC_VIDEO_MASTER) {
      if (frame->pts != AV_NOPTS_VALUE) {
        double diff = time - p_master_clock->GetTime();

        av_log(NULL, AV_LOG_TRACE, "diff=%f time=%f np=%d\n", diff, time,
               p_image_packet_queue_->getNumberOfPackets());

        if (!isnan(diff) && fabs(diff) < kAvNoSyncThreshold && diff < 0 &&
            p_image_decoder_->GetSerial() == p_image_clock_->GetSerial() &&
            p_image_packet_queue_->getNumberOfPackets()) {
          num_frame_drops_early_++;
          av_frame_unref(frame);
          got_frame = 0;
        }
      }
    }
  }

  return got_frame;
}

int VideoState::QueueImage(AVFrame *p_image_frame, double pts, double duration,
                           int64_t pos, int frame_pos, int serial) {
  Frame *p_frame = nullptr;
  p_image_frame_queue_->PeekWritable(&p_frame);
  if (!p_frame) {
    return -1;
  }

  p_frame->aspect_ratio_ = p_image_frame->sample_aspect_ratio;
  p_frame->is_uploaded_ = false;

  p_frame->width_ = p_image_frame->width;
  p_frame->height_ = p_image_frame->height;
  p_frame->format_ = p_image_frame->format;

  p_frame->pts_ = pts;
  p_frame->duration_ = duration;
  p_frame->byte_pos_ = pos;
  p_frame->serial_ = serial;
  p_frame->frame_pos_ = frame_pos;

  av_frame_move_ref(p_frame->p_frame_, p_image_frame);
  p_image_frame_queue_->Push();
  return 0;
}

void VideoState::CloseStreamComponent(int stream_index) {
  AVCodecParameters *p_codec_parameters;

  if (stream_index < 0 || stream_index >= p_format_context->nb_streams) {
    return;
  }
  p_codec_parameters = p_format_context->streams[stream_index]->codecpar;

  switch (p_codec_parameters->codec_type) {
  case AVMEDIA_TYPE_AUDIO:
    p_audio_decoder_->Stop(p_audio_frame_queue_);
    delete p_audio_decoder_;
    swr_free(&swr_ctx);
    av_freep(&p_audio_buffer1_);
    audio_buffer1_size_ = 0;
    p_audio_buffer_ = nullptr;
    break;
  case AVMEDIA_TYPE_VIDEO:
    p_image_decoder_->Stop(p_image_frame_queue_);
    delete p_image_decoder_;
    break;
  default:
    break;
  }

  p_format_context->streams[stream_index]->discard = AVDISCARD_ALL;
  switch (p_codec_parameters->codec_type) {
  case AVMEDIA_TYPE_AUDIO:
    p_audio_stream_ = nullptr;
    audio_stream_index_ = -1;
    break;
  case AVMEDIA_TYPE_VIDEO:
    p_image_stream_ = nullptr;
    image_stream_index_ = -1;
    break;
  default:
    break;
  }
}

bool VideoState::StreamHasEnoughPackets(const AVStream &stream, int stream_id,
                                        const PacketQueue &packet_queue) {
  return stream_id < 0 || packet_queue.IsAbortRequested() ||
         (stream.disposition & AV_DISPOSITION_ATTACHED_PIC) ||
         packet_queue.getNumberOfPackets() > kMinFrames &&
             (!packet_queue.GetDuration() ||
              av_q2d(stream.time_base) * packet_queue.GetDuration() > 1.0);
}

int VideoState::CheckStreamSpecifier(AVFormatContext *p_format_context,
                                     AVStream *p_stream,
                                     const char *specifier) {
  int ret =
      avformat_match_stream_specifier(p_format_context, p_stream, specifier);
  if (ret < 0) {
    av_log(p_format_context, AV_LOG_ERROR, "Invalid stream specifier: %s.\n",
           specifier);
  }
  return ret;
}

int VideoState::GetFilterCodecOptions(AVDictionary *p_opts_out,
                                      const AVDictionary &opts_in,
                                      AVCodecID codec_id,
                                      AVFormatContext *p_format_context,
                                      AVStream *p_stream, AVCodec *p_codec) {
  AVDictionaryEntry *dict_entry = nullptr;
  int flags = p_format_context->oformat ? AV_OPT_FLAG_ENCODING_PARAM
                                        : AV_OPT_FLAG_DECODING_PARAM;
  char prefix = 0;
  const AVClass *clazz = avcodec_get_class();

  if (!p_codec) {
    p_codec = p_format_context->oformat ? avcodec_find_encoder(codec_id)
                                        : avcodec_find_decoder(codec_id);
  }

  switch (p_stream->codecpar->codec_type) {
  case AVMEDIA_TYPE_VIDEO:
    prefix = 'v';
    flags |= AV_OPT_FLAG_VIDEO_PARAM;
    break;
  case AVMEDIA_TYPE_AUDIO:
    prefix = 'a';
    flags |= AV_OPT_FLAG_AUDIO_PARAM;
    break;
  }

  while (dict_entry =
             av_dict_get(&opts_in, "", dict_entry, AV_DICT_IGNORE_SUFFIX)) {
    char *param = strchr(dict_entry->key, ':');

    /* check stream specification in opt name */
    if (param) {
      switch (CheckStreamSpecifier(p_format_context, p_stream, param + 1)) {
      case 1:
        *param = 0;
        break;
      case 0:
        continue;
      default:
        av_log(NULL, AV_LOG_ERROR, "Undefined stream specifier");
        return AVERROR_INVALIDDATA;
      }
    }

    if (av_opt_find(&clazz, dict_entry->key, NULL, flags,
                    AV_OPT_SEARCH_FAKE_OBJ) ||
        !p_codec ||
        (p_codec->priv_class &&
         av_opt_find(&p_codec->priv_class, dict_entry->key, NULL, flags,
                     AV_OPT_SEARCH_FAKE_OBJ))) {
      av_dict_set(&p_opts_out, dict_entry->key, dict_entry->value, 0);
    } else if (dict_entry->key[0] == prefix &&
               av_opt_find(&clazz, dict_entry->key + 1, NULL, flags,
                           AV_OPT_SEARCH_FAKE_OBJ)) {
      av_dict_set(&p_opts_out, dict_entry->key + 1, dict_entry->value, 0);
    }

    if (param) {
      *param = ':';
    }
  }

  return 0; // No error
}

int VideoState::SetupStreamOptions(AVDictionary ***opts,
                                   AVFormatContext *p_frame_context,
                                   AVDictionary *p_codec_opts) {
  int err = 0;

  if (!p_frame_context->nb_streams) {
    return 0; // nothing to allocate here
  }
  *opts = (AVDictionary **)av_mallocz_array(p_frame_context->nb_streams,
                                            sizeof(**opts));
  if (!(*opts)) {
    av_log(NULL, AV_LOG_ERROR, "Could not alloc memory for stream options.\n");
    return AVERROR(ENOMEM);
  }
  for (int i = 0; i < p_frame_context->nb_streams; i++) {
    // TODO: Handle error here
    err = GetFilterCodecOptions((*opts)[i], *p_codec_opts,
                                p_frame_context->streams[i]->codecpar->codec_id,
                                p_frame_context, p_frame_context->streams[i],
                                NULL);
    if (err) {
      return err;
    }
  }
  return 0; // no error
}

int VideoState::SynchronizeAudio(int nb_samples) {
  int wanted_nb_samples = nb_samples;

  /* if not master, then we try to remove or add samples to correct the clock */
  if (GetMasterSyncType() != AV_SYNC_AUDIO_MASTER) {
    double diff, avg_diff;
    int min_nb_samples, max_nb_samples;
    Clock *p_master_clock = nullptr;
    GetMasterClock(&p_master_clock);

    diff = p_audio_clock_->GetTime() - p_master_clock->GetTime();

    if (!isnan(diff) && fabs(diff) < kAvNoSyncThreshold) {
      audio_diff_cum_ = diff + audio_diff_avg_coef_ * audio_diff_cum_;
      if (audio_diff_avg_count_ < kAudioDiffAvgNum) {
        /* not enough measures to have a correct estimate */
        audio_diff_avg_count_++;
      } else {
        /* estimate the A-V difference */
        avg_diff = audio_diff_cum_ * (1.0 - audio_diff_avg_coef_);

        if (fabs(avg_diff) >= audio_diff_threshold_) {
          wanted_nb_samples =
              nb_samples + (int)(diff * audio_parms_source_.frequency_);
          min_nb_samples =
              ((nb_samples * (100 - kSampleCorrectionMaxPercent) / 100));
          max_nb_samples =
              ((nb_samples * (100 + kSampleCorrectionMaxPercent) / 100));
          wanted_nb_samples =
              av_clip(wanted_nb_samples, min_nb_samples, max_nb_samples);
        }
        av_log(NULL, AV_LOG_TRACE,
               "diff=%f adiff=%f sample_diff=%d apts=%0.3f %f\n", diff,
               avg_diff, wanted_nb_samples - nb_samples, audio_pts_,
               audio_diff_threshold_);
      }
    } else {
      /* too big difference : may be initial PTS errors, so reset A-V filter */
      audio_diff_avg_count_ = 0;
      audio_diff_cum_ = 0;
    }
  }
  return wanted_nb_samples;
}

int VideoState::DecodeAudioFrame() {
  int data_size;
  int resampled_data_size;
  int64_t dec_channel_layout;
  int wanted_nb_samples;
  Frame *p_audio_frame = nullptr;
  double original_sample_rate;

  if (is_paused_) {
    return -1;
  }

  do {
    p_audio_frame_queue_->PeekReadable(&p_audio_frame);
    if (!p_audio_frame) {
      return -1;
    }
    p_audio_frame_queue_->Next();
  } while (p_audio_frame->serial_ != p_audio_packet_queue_->GetSerial());

  data_size = av_samples_get_buffer_size(
      NULL, p_audio_frame->p_frame_->channels,
      p_audio_frame->p_frame_->nb_samples,
      static_cast<AVSampleFormat>(p_audio_frame->p_frame_->format), 1);

  dec_channel_layout =
      (p_audio_frame->p_frame_->channel_layout &&
       p_audio_frame->p_frame_->channels ==
           av_get_channel_layout_nb_channels(
               p_audio_frame->p_frame_->channel_layout))
          ? p_audio_frame->p_frame_->channel_layout
          : av_get_default_channel_layout(p_audio_frame->p_frame_->channels);
  wanted_nb_samples = SynchronizeAudio(p_audio_frame->p_frame_->nb_samples);

  original_sample_rate = p_audio_frame->p_frame_->sample_rate;
  // Change the sample_rate by the playback rate
  p_audio_frame->p_frame_->sample_rate *= current_speed_;

  if (p_audio_frame->p_frame_->format != audio_parms_source_.sample_format_ ||
      dec_channel_layout != audio_parms_source_.channel_layout_ ||
      p_audio_frame->p_frame_->sample_rate != audio_parms_source_.frequency_ ||
      (wanted_nb_samples != p_audio_frame->p_frame_->nb_samples && !swr_ctx)) {
    swr_free(&swr_ctx);
    swr_ctx = swr_alloc_set_opts(
        NULL, audio_params_target_.channel_layout_,
        audio_params_target_.sample_format_, audio_params_target_.frequency_,
        dec_channel_layout,
        static_cast<AVSampleFormat>(p_audio_frame->p_frame_->format),
        p_audio_frame->p_frame_->sample_rate, 0, NULL);
    if (!swr_ctx || swr_init(swr_ctx) < 0) {
      av_log(NULL, AV_LOG_ERROR,
             "Cannot create sample rate converter for conversion of %d Hz %s "
             "%d channels to %d Hz %s %d channels!\n",
             p_audio_frame->p_frame_->sample_rate,
             av_get_sample_fmt_name(
                 static_cast<AVSampleFormat>(p_audio_frame->p_frame_->format)),
             p_audio_frame->p_frame_->channels, audio_params_target_.frequency_,
             av_get_sample_fmt_name(audio_params_target_.sample_format_),
             audio_params_target_.num_channels_);
      swr_free(&swr_ctx);
      return -1;
    }
    audio_parms_source_.channel_layout_ = dec_channel_layout;
    audio_parms_source_.num_channels_ = p_audio_frame->p_frame_->channels;
    audio_parms_source_.frequency_ = p_audio_frame->p_frame_->sample_rate;
    audio_parms_source_.sample_format_ =
        static_cast<AVSampleFormat>(p_audio_frame->p_frame_->format);
  }

  if (swr_ctx) {
    const uint8_t **in =
        (const uint8_t **)p_audio_frame->p_frame_->extended_data;
    uint8_t **out = &p_audio_buffer1_;
    int out_count = (int64_t)wanted_nb_samples *
                        audio_params_target_.frequency_ /
                        p_audio_frame->p_frame_->sample_rate +
                    256;
    int out_size = av_samples_get_buffer_size(
        NULL, audio_params_target_.num_channels_, out_count,
        audio_params_target_.sample_format_, 0);
    int len2;
    if (out_size < 0) {
      av_log(NULL, AV_LOG_ERROR, "av_samples_get_buffer_size() failed\n");
      return -1;
    }
    if (wanted_nb_samples != p_audio_frame->p_frame_->nb_samples) {
      if (swr_set_compensation(
              swr_ctx,
              (wanted_nb_samples - p_audio_frame->p_frame_->nb_samples) *
                  audio_params_target_.frequency_ /
                  p_audio_frame->p_frame_->sample_rate,
              wanted_nb_samples * audio_params_target_.frequency_ /
                  p_audio_frame->p_frame_->sample_rate) < 0) {
        av_log(NULL, AV_LOG_ERROR, "swr_set_compensation() failed\n");
        return -1;
      }
    }
    av_fast_malloc(&p_audio_buffer1_, &audio_buffer1_size_, out_size);
    if (!p_audio_buffer1_) {
      return AVERROR(ENOMEM);
    }
    len2 = swr_convert(swr_ctx, out, out_count, in,
                       p_audio_frame->p_frame_->nb_samples);
    if (len2 < 0) {
      av_log(NULL, AV_LOG_ERROR, "swr_convert() failed\n");
      return -1;
    }
    if (len2 == out_count) {
      av_log(NULL, AV_LOG_WARNING, "audio buffer is probably too small\n");
      if (swr_init(swr_ctx) < 0)
        swr_free(&swr_ctx);
    }
    p_audio_buffer_ = p_audio_buffer1_;
    resampled_data_size =
        len2 * audio_params_target_.num_channels_ *
        av_get_bytes_per_sample(audio_params_target_.sample_format_);
  } else {
    p_audio_buffer_ = p_audio_frame->p_frame_->data[0];
    resampled_data_size = data_size;
  }

  /* update the audio clock with the pts */
  audio_pts_ =
      isnan(p_audio_frame->pts_)
          ? NAN
          : p_audio_frame->pts_ + (double)p_audio_frame->p_frame_->nb_samples /
                                      original_sample_rate;
  audio_serial_ = p_audio_frame->serial_;
  return resampled_data_size;
}

// Note, queues and clocks get initialized in the create_video_state function
// The initialization order is correct now, but it is not garuanteed that some
// of these might not be null; hence, we initialize this in the create function
VideoState::VideoState(int audio_buffer_size)
    : abort_request_(false), is_paused_(true),
      queue_attachments_request_(false), seek_done_(false),
      seek_request_(false), seek_flags_(AVSEEK_FLAG_BACKWARD), seek_time_(0),
      seek_frame_(0), seek_distance_(0), sync_type_(AV_SYNC_AUDIO_MASTER),
      frame_rate_(0.0), image_clock_last_set_time_(0), image_stream_index_(0),
      max_frame_duration_(0), end_of_file_(false), duration_(0),
      frame_width_(0), frame_height_(0), frame_aspect_ratio_(av_make_q(0, 0)),
      is_stepping_(false), speed_request_(false), requested_speed_(1.0),
      current_speed_(1.0), audio_disabled_(false), video_disabled_(false),
      last_video_stream_(0), last_audio_stream_(0), filename_(nullptr),
      p_audio_packet_queue_(nullptr), p_image_packet_queue_(nullptr),
      p_audio_frame_queue_(nullptr), p_image_frame_queue_(nullptr),
      p_audio_clock_(nullptr), p_image_clock_(nullptr),
      p_external_clock_(nullptr), p_audio_decoder_(nullptr),
      p_image_decoder_(nullptr), p_reader_thread_(nullptr),
      p_input_format_(nullptr), p_format_context(nullptr), swr_ctx(nullptr),
      p_audio_stream_(nullptr), p_image_stream_(nullptr),
      audio_stream_index_(0), audio_pts_(0.0), audio_serial_(0),
      audio_diff_cum_(0.0), audio_diff_avg_coef_(0.0),
      audio_diff_threshold_(0.0), audio_diff_avg_count_(0),
      audio_hw_buffer_size_(0), audio_default_buffer_size_(audio_buffer_size),
      p_audio_buffer_(nullptr), p_audio_buffer1_(nullptr),
      audio_buffer_size_(0),                          /* in bytes */
      audio_buffer1_size_(0), audio_buffer_index_(0), /* in bytes */
      audio_write_buffer_size_(0), is_muted_(false), num_frame_drops_early_(0),
      start_time_(AV_NOPTS_VALUE), max_duration_(AV_NOPTS_VALUE), num_loop_(1),
      frame_last_shown_time_(0.0) {}

int VideoState::CreateVideoState(VideoState **pp_video_state,
                                 int audio_buffer_size) {
  // Create the video state
  *pp_video_state = new (std::nothrow) VideoState(audio_buffer_size);
  if (!*pp_video_state) {
    av_log(NULL, AV_LOG_ERROR, "Unable to create new video state");
    return ENOMEM;
  }

  // Initialize packet queues
  (*pp_video_state)->p_audio_packet_queue_ = new (std::nothrow) PacketQueue();
  if (!(*pp_video_state)->p_audio_packet_queue_) {
    av_log(NULL, AV_LOG_ERROR, "Unable to create packet queue for audio");
    delete *pp_video_state;
    return ENOMEM;
  }
  (*pp_video_state)->p_image_packet_queue_ = new (std::nothrow) PacketQueue();
  if (!(*pp_video_state)->p_image_packet_queue_) {
    av_log(NULL, AV_LOG_ERROR, "Unable to create packet queue for video");
    delete *pp_video_state;
    return ENOMEM;
  }

  // Handle frame queues
  if (FrameQueue::CreateFrameQueue(&(*pp_video_state)->p_audio_frame_queue_,
                                   (*pp_video_state)->p_audio_packet_queue_,
                                   kSampleQueueSize, true)) {
    av_log(NULL, AV_LOG_ERROR, "Unable to create frame queue for audio");
    delete *pp_video_state;
    return ENOMEM;
  }

  if (FrameQueue::CreateFrameQueue(&(*pp_video_state)->p_image_frame_queue_,
                                   (*pp_video_state)->p_image_packet_queue_,
                                   kVideoPictureQueueSize, true)) {
    av_log(NULL, AV_LOG_ERROR, "Unable to create frame queue for video");
    delete *pp_video_state;
    return ENOMEM;
  }

  // Create clocks
  int *p_audio_serial = nullptr;
  (*pp_video_state)->p_audio_packet_queue_->GetPtrSerial(&p_audio_serial);
  (*pp_video_state)->p_audio_clock_ = new (std::nothrow) Clock(p_audio_serial);
  if (!(*pp_video_state)->p_audio_clock_) {
    av_log(NULL, AV_LOG_ERROR, "Unable to create clock for audio");
    delete *pp_video_state;
    return ENOMEM;
  }

  int *p_image_serial = nullptr;
  (*pp_video_state)->p_image_packet_queue_->GetPtrSerial(&p_image_serial);
  (*pp_video_state)->p_image_clock_ = new (std::nothrow) Clock(p_image_serial);
  if (!(*pp_video_state)->p_image_clock_) {
    av_log(NULL, AV_LOG_ERROR, "Unable to create clock for video");
    delete *pp_video_state;
    return ENOMEM;
  }
  (*pp_video_state)->p_external_clock_ = new (std::nothrow) Clock();
  if (!(*pp_video_state)->p_external_clock_) {
    av_log(NULL, AV_LOG_ERROR, "Unable to create clock for external");
    delete *pp_video_state;
    return ENOMEM;
  }

  return 0; // no error
}

VideoState::~VideoState() {
  // From stream close
  abort_request_ = true;
  if (p_reader_thread_) {
    p_reader_thread_->join();
    delete p_reader_thread_;
    p_reader_thread_ = nullptr;
  }

  if (audio_stream_index_ >= 0) {
    CloseStreamComponent(audio_stream_index_);
  }
  if (image_stream_index_ >= 0) {
    CloseStreamComponent(image_stream_index_);
  }

  if (p_format_context) {
    avformat_close_input(&p_format_context);
  }

  av_free(filename_);
  // End stream close

  // From close method
  if (p_image_packet_queue_) {
    delete p_image_packet_queue_;
  }

  if (p_audio_packet_queue_) {
    delete p_audio_packet_queue_;
  }

  if (p_image_frame_queue_) {
    delete p_image_frame_queue_;
  }

  if (p_audio_frame_queue_) {
    delete p_audio_frame_queue_;
  }

  if (p_image_clock_) {
    delete p_image_clock_;
  }

  if (p_audio_clock_) {
    delete p_audio_clock_;
  }

  if (p_external_clock_) {
    delete p_external_clock_;
  }
}

//* Gets the stream from the disk or the network */
int VideoState::ReadPacketsToQueues() {
  int ret;
  AVPacket pkt1, *pkt = &pkt1;
  std::mutex wait_mutex;
  int64_t stream_start_time;
  bool pkt_in_play_range = false;
  int64_t pkt_ts;

  for (;;) {
    if (abort_request_) {
      break;
    }

    if (speed_request_) {
      current_speed_ = requested_speed_;
      speed_request_ = false;
      queue_attachments_request_ = true;
    }

    if (seek_request_) {
      if (seek_flags_ & AVSEEK_FLAG_BACKWARD) {
        ret = av_seek_frame(p_format_context, -1, seek_time_,
                            AVSEEK_FLAG_BACKWARD);
      } else {
        int64_t seek_min =
            seek_distance_ > 0 ? seek_time_ - seek_distance_ + 2 : INT64_MIN;
        int64_t seek_max =
            seek_distance_ < 0 ? seek_time_ - seek_distance_ - 2 : INT64_MAX;

        // FIXME the +-2 is due to rounding being not done in the correct
        // direction in generation
        //      of the seek_pos/seek_rel variables
        ret = avformat_seek_file(p_format_context, -1, seek_min, seek_time_,
                                 seek_max, seek_flags_);
      }

      if (ret < 0) {
        av_log(NULL, AV_LOG_ERROR, "%s: error while seeking\n",
               p_format_context->url);
      } else {
        if (audio_stream_index_ >= 0) {
          p_audio_packet_queue_->Flush();
          p_audio_packet_queue_->PutFlushPacket();
        }
        if (image_stream_index_ >= 0) {
          p_image_packet_queue_->Flush();
          p_image_packet_queue_->PutFlushPacket();
        }
        p_external_clock_->SetTime(
            seek_time_ / (double)AV_TIME_BASE,
            0); // 0 != -1 which will return NAN for interim time
      }
      seek_request_ = false;
      seek_done_ =
          true; // Seek is done here, not in the Audio and Frame packet threads
      queue_attachments_request_ = true;
      end_of_file_ = false;

      if (is_paused_) {
        step_to_next_frame_callback(); // Assume that the step callback is set
                                       // -- otherwise fail hard here
      }
    }
    if (queue_attachments_request_) {
      if (p_image_stream_ &&
          p_image_stream_->disposition & AV_DISPOSITION_ATTACHED_PIC) {
        AVPacket copy = {0};
        if ((av_packet_ref(&copy, &p_image_stream_->attached_pic)) < 0) {
          goto fail;
        }
        p_image_packet_queue_->Put(&copy);
        p_image_packet_queue_->PutNullPacket(image_stream_index_);
      }
      queue_attachments_request_ = false;
    }

    /* if the queues are full, no need to read more */
    if (p_audio_packet_queue_->GetSize() + p_image_packet_queue_->GetSize() >
            kMaxQueueSize ||
        (StreamHasEnoughPackets(*p_audio_stream_, audio_stream_index_,
                                *p_audio_packet_queue_) &&
         StreamHasEnoughPackets(*p_image_stream_, image_stream_index_,
                                *p_image_packet_queue_))) {
      /* wait 10 ms */
      std::unique_lock<std::mutex> locker(wait_mutex);
      continue_read_thread_.wait_for(locker, std::chrono::milliseconds(10));
      locker.unlock();
      continue;
    }
    if (!is_paused_ &&
        (!p_audio_decoder_ || !p_audio_stream_ ||
         (p_audio_decoder_->IsFinished() ==
              p_audio_packet_queue_->GetSerial() &&
          p_audio_frame_queue_->GetNumToDisplay() == 0)) &&
        (!p_image_decoder_ || !p_image_stream_ ||
         (p_image_decoder_->IsFinished() ==
              p_image_packet_queue_->GetSerial() &&
          p_image_frame_queue_->GetNumToDisplay() == 0))) {
      if (num_loop_ != 1 && (!num_loop_ || --num_loop_)) {
        Seek(GetStartTime(), 0);
      }
    }
    ret = av_read_frame(p_format_context, pkt);
    if (ret < 0) {
      if ((ret == AVERROR_EOF || avio_feof(p_format_context->pb)) &&
          !end_of_file_) {
        if (image_stream_index_ >= 0) {
          p_image_packet_queue_->PutNullPacket(image_stream_index_);
        }
        if (audio_stream_index_ >= 0) {
          p_audio_packet_queue_->PutNullPacket(audio_stream_index_);
        }
        end_of_file_ = true;
      }
      if (p_format_context->pb && p_format_context->pb->error) {
        break;
      }
      /* wait 10 ms */
      std::unique_lock<std::mutex> locker(wait_mutex);
      continue_read_thread_.wait_for(locker, std::chrono::milliseconds(10));
      locker.unlock();
      continue;
    } else {
      end_of_file_ = false;
      // TODO(fraudies): Set the player state from stalled to ready here (if
      // ready don't do anything)
    }
    /* check if packet is in play range specified by user, then queue, otherwise
     * discard */
    stream_start_time =
        p_format_context->streams[pkt->stream_index]->start_time;
    pkt_ts = pkt->pts == AV_NOPTS_VALUE ? pkt->dts : pkt->pts;

    pkt_in_play_range =
        max_duration_ == AV_NOPTS_VALUE ||
        (pkt_ts -
         (stream_start_time != AV_NOPTS_VALUE ? stream_start_time : 0)) *
                    av_q2d(p_format_context->streams[pkt->stream_index]
                               ->time_base) -
                (double)GetStartTime() / 1000000 <=
            ((double)max_duration_ / 1000000);

    if (pkt->stream_index == audio_stream_index_ && pkt_in_play_range) {
      p_audio_packet_queue_->Put(pkt);
    } else if (pkt->stream_index == image_stream_index_ && pkt_in_play_range &&
               !(p_image_stream_->disposition & AV_DISPOSITION_ATTACHED_PIC)) {
      p_image_packet_queue_->Put(pkt);
    } else {
      av_packet_unref(pkt);
    }
  }

  ret = 0;
fail:
  if (ret != 0 && p_format_context) {
    avformat_close_input(&p_format_context);
  }

  if (ret != 0 && destroy_callback) {
    destroy_callback();
  }

  return 0;
}

/* Called when the stream is opened */
int VideoState::DecodeAudioPacketsToFrames() {
  AVFrame *p_frame = av_frame_alloc();
  Frame *p_audio_frame = nullptr;

  int got_frame = 0;
  AVRational tb;
  int ret = 0;
  double audio_time_base = av_q2d(p_audio_stream_->time_base);

  if (!p_frame) {
    return AVERROR(ENOMEM);
  }

  do {
    if ((got_frame = p_audio_decoder_->Decode(p_frame)) < 0) {
      goto the_end;
    }

    if (got_frame) {
      int64_t audio_seek_pts =
          seek_time_ / (audio_time_base * (double)AV_TIME_BASE);
      if ((seek_flags_ & AVSEEK_FLAG_BACKWARD) &&
          p_frame->pts < audio_seek_pts) {
        av_frame_unref(p_frame);
        continue;
      }

      if (seek_done_) {
        queue_attachments_request_ = true;
        continue_after_seek_.notify_one();
      }

      tb = av_make_q(1, p_frame->sample_rate);
      p_audio_frame_queue_->PeekWritable(&p_audio_frame);
      if (!p_audio_frame) {
        goto the_end;
      }

      av_log(NULL, AV_LOG_TRACE, "Audio frame pts = %I64d, tb = %2.7f\n",
             p_frame->pts, av_q2d(tb));

      p_audio_frame->pts_ =
          (p_frame->pts == AV_NOPTS_VALUE) ? NAN : p_frame->pts * av_q2d(tb);
      p_audio_frame->byte_pos_ = p_frame->pkt_pos;
      p_audio_frame->serial_ = p_audio_decoder_->GetSerial();
      p_audio_frame->duration_ =
          av_q2d(av_make_q(p_frame->nb_samples, p_frame->sample_rate));

      av_frame_move_ref(p_audio_frame->p_frame_, p_frame);
      p_audio_frame_queue_->Push();
    }
  } while (ret >= 0 || ret == AVERROR(EAGAIN) || ret == AVERROR_EOF);
the_end:
  av_frame_free(&p_frame);
  return ret;
}

/* Called when the stream is opened */
int VideoState::DecodeImagePacketsToFrames() {
  AVFrame *p_frame = av_frame_alloc();
  double pts;
  double duration;
  int ret;
  int frame_pos = 0;
  AVRational time_base = p_image_stream_->time_base;
  AVRational frame_rate =
      av_guess_frame_rate(p_format_context, p_image_stream_, NULL);
  bool request_attachement_ = false;
  double image_time_base = av_q2d(p_image_stream_->time_base);

  if (!p_frame) {
    return AVERROR(ENOMEM);
  }

  for (;;) {
    ret = GetImageFrame(p_frame);

    if (ret < 0) {
      goto the_end;
    }

    if (!ret) {
      continue;
    }

    int64_t image_seek_pts =
        seek_time_ / (image_time_base * (double)AV_TIME_BASE);
    // Calculate the frame position in the file
    frame_pos =
        (p_frame->pts * time_base.num * p_image_stream_->r_frame_rate.num) /
        (time_base.den * p_image_stream_->r_frame_rate.den);

    if (((seek_flags_ & AVSEEK_FLAG_BACKWARD) &&
         p_frame->pts < image_seek_pts) ||
        ((seek_flags_ & AVSEEK_FLAG_BACKWARD) && frame_pos < seek_frame_)) {
      av_frame_unref(p_frame);
      continue;
    }

    av_log(NULL, AV_LOG_TRACE, " Video frame pts = %I64d, tb = %2.7f\n",
           p_frame->pts, av_q2d(time_base));

    duration = (frame_rate.num && frame_rate.den)
                   ? av_q2d(av_make_q(frame_rate.den, frame_rate.num))
                   : 0;
    pts = (p_frame->pts == AV_NOPTS_VALUE) ? NAN
                                           : p_frame->pts * av_q2d(time_base);
    ret = QueueImage(p_frame, pts, duration, p_frame->pkt_pos, frame_pos,
                     p_image_decoder_->GetSerial());

    // Seek complete processed after we request a seek and enqueued an image
    // Actually, it would be best after we displayed an image but
    // we don't have that signal here
    if (seek_done_) {
      queue_attachments_request_ = true;
      continue_after_seek_.notify_one();
    }

    av_frame_unref(p_frame);

    if (ret < 0) {
      goto the_end;
    }
  }
the_end:
  av_frame_free(&p_frame);
  return 0;
}

/* Public Members*/
int VideoState::OpenStream(VideoState **pp_video_state, const char *filename,
                           AVInputFormat *iformat, int audio_buffer_size) {
  int err;

  err = VideoState::CreateVideoState(pp_video_state, audio_buffer_size);
  if (err) {
    return err;
  }

  (*pp_video_state)->filename_ = av_strdup(filename);
  if (!(*pp_video_state)->filename_) {
    return ENOMEM;
  }

  (*pp_video_state)->p_input_format_ = iformat;

  (*pp_video_state)->audio_serial_ = -1;

  return 0; // no error
}

int VideoState::StartStream() {
  int i, ret;
  int st_index[AVMEDIA_TYPE_NB];
  AVDictionaryEntry *p_entry = nullptr;
  int scan_all_pmts_set = 0;
  AVDictionary *p_format_opts = nullptr;
  AVDictionary *codec_opts = nullptr;
  AVDictionary **pp_opts_dict = nullptr;

  memset(st_index, -1, sizeof(st_index));
  last_video_stream_ = image_stream_index_ = -1;
  last_audio_stream_ = audio_stream_index_ = -1;
  end_of_file_ = false; // Is initialized in the constructor

  p_format_context = avformat_alloc_context();
  if (!p_format_context) {
    av_log(NULL, AV_LOG_FATAL, "Could not allocate context.\n");
    ret = AVERROR(ENOMEM);
    goto fail;
  }
  p_format_context->interrupt_callback.callback = DecodeInterruptBridge;
  p_format_context->interrupt_callback.opaque = this;
  if (!av_dict_get(p_format_opts, "scan_all_pmts", NULL, AV_DICT_MATCH_CASE)) {
    av_dict_set(&p_format_opts, "scan_all_pmts", "1", AV_DICT_DONT_OVERWRITE);
    scan_all_pmts_set = 1;
  }
  ret = avformat_open_input(&p_format_context, filename_, p_input_format_,
                            &p_format_opts);
  if (ret < 0) {
    goto fail;
  }
  if (scan_all_pmts_set)
    av_dict_set(&p_format_opts, "scan_all_pmts", NULL, AV_DICT_MATCH_CASE);

  if ((p_entry = av_dict_get(p_format_opts, "", NULL, AV_DICT_IGNORE_SUFFIX))) {
    av_log(NULL, AV_LOG_ERROR, "Option %s not found.\n", p_entry->key);
    ret = AVERROR_OPTION_NOT_FOUND;
    goto fail;
  }

  if (kEnableGeneratePts) {
    p_format_context->flags |= AVFMT_FLAG_GENPTS;
  }

  av_format_inject_global_side_data(p_format_context);

  // Find the stream information
  ret = SetupStreamOptions(&pp_opts_dict, p_format_context, codec_opts);
  if (ret) {
    // TODO(fraudies): Clean up dictionary memory
    goto fail;
  }

  ret = avformat_find_stream_info(p_format_context, pp_opts_dict);
  for (i = 0; i < p_format_context->nb_streams; i++) {
    av_dict_free(&pp_opts_dict[i]);
  }
  av_freep(&pp_opts_dict);

  if (ret < 0) {
    av_log(NULL, AV_LOG_WARNING, "%s: could not find codec parameters\n",
           filename_);
    goto fail;
  }

  if (p_format_context->pb) {
    p_format_context->pb->eof_reached =
        0; // FIXME hack, ffplay maybe should not use
           // avio_feof() to test for the end
  }

  if (kEnableSeekByBytes < 0) {
    kEnableSeekByBytes =
        !!(p_format_context->iformat->flags & AVFMT_TS_DISCONT) &&
        strcmp("ogg", p_format_context->iformat->name);
  }

  max_frame_duration_ =
      (p_format_context->iformat->flags & AVFMT_TS_DISCONT) ? 10.0 : 3600.0;

  /* if seeking requested, we execute it */
  if (start_time_ != AV_NOPTS_VALUE) {
    int64_t timestamp;

    timestamp = start_time_;
    /* add the stream start time */
    if (p_format_context->start_time != AV_NOPTS_VALUE) {
      timestamp += p_format_context->start_time;
    }
    ret = avformat_seek_file(p_format_context, -1, INT64_MIN, timestamp,
                             INT64_MAX, 0);
    if (ret < 0) {
      av_log(NULL, AV_LOG_WARNING, "%s: could not seek to position %0.3f\n",
             filename_, (double)timestamp / AV_TIME_BASE);
    }
  }

  if (kEnableShowFormat) {
    av_dump_format(p_format_context, 0, filename_, 0);
  }

  for (i = 0; i < p_format_context->nb_streams; i++) {
    AVStream *st = p_format_context->streams[i];
    enum AVMediaType type = st->codecpar->codec_type;
    st->discard = AVDISCARD_ALL;
  }

  if (!video_disabled_) {
    st_index[AVMEDIA_TYPE_VIDEO] =
        av_find_best_stream(p_format_context, AVMEDIA_TYPE_VIDEO,
                            st_index[AVMEDIA_TYPE_VIDEO], -1, NULL, 0);
  }

  if (!audio_disabled_) {
    st_index[AVMEDIA_TYPE_AUDIO] = av_find_best_stream(
        p_format_context, AVMEDIA_TYPE_AUDIO, st_index[AVMEDIA_TYPE_AUDIO],
        st_index[AVMEDIA_TYPE_VIDEO], NULL, 0);
  }

  /* open the streams */
  if (st_index[AVMEDIA_TYPE_AUDIO] >= 0) {
    OpenStreamComponent(st_index[AVMEDIA_TYPE_AUDIO]);
  }
  // Set the video duration
  duration_ = p_format_context->duration / (double)AV_TIME_BASE;

  ret = -1;
  if (st_index[AVMEDIA_TYPE_VIDEO] >= 0) {
    ret = OpenStreamComponent(st_index[AVMEDIA_TYPE_VIDEO]);
  }

  if (image_stream_index_ < 0 && audio_stream_index_ < 0) {
    av_log(NULL, AV_LOG_FATAL,
           "Failed to open file '%s' or configure filtergraph\n", filename_);
    ret = AVERROR_STREAM_NOT_FOUND;
    goto fail;
  } else if (image_stream_index_ >= 0 && audio_stream_index_ < 0) {
    // Use video clock as Master when audio is not available
    sync_type_ = AV_SYNC_VIDEO_MASTER;
  }

  if (update_player_state_callbacks[PlayerState::Ready]) {
    update_player_state_callbacks[PlayerState::Ready]();
  }

  p_reader_thread_ =
      new (std::nothrow) std::thread([this] { ReadPacketsToQueues(); });
  if (!p_reader_thread_) {
    av_log(NULL, AV_LOG_FATAL, "Unable to create reader thread\n");
    ret = AVERROR(ENOMEM);
    goto fail;
  }

  return 0;

fail:
  if (ret != 0 && p_format_context) {
    avformat_close_input(&p_format_context);
  }
  if (ret != 0 && destroy_callback) {
    destroy_callback();
  }

  return ret;
}

void VideoState::TogglePauseAndMute(bool mute) {
  // Update the video clock
  if (is_paused_) {
    frame_last_shown_time_ = frame_last_shown_time_ +
                             av_gettime_relative() / 1000000.0 -
                             image_clock_last_set_time_;
    p_image_clock_->SetTime(p_image_clock_->GetTime(),
                            p_image_clock_->GetSerial());
  }
  // Update the external clock
  p_external_clock_->SetTime(p_external_clock_->GetTime(),
                             p_external_clock_->GetSerial());

  is_muted_ = mute;

  // Flip the paused flag
  SetPaused(!is_paused_);
}

void VideoState::SetPts(double pts, int serial) {
  p_image_clock_->SetTime(pts, serial);
  image_clock_last_set_time_ = av_gettime_relative() / 1000000.0;
  // Sync external clock to video clock
  Clock::SyncMasterToSlave(p_external_clock_, p_image_clock_,
                           kAvNoSyncThreshold);
}

// FIXME Seek to end of stream
/* seek in the stream */
void VideoState::Seek(int64_t time, int64_t distance, int seek_flags) {
  // Only seek if
  // - there is no seek request in progress AND
  // - this seek time is different from the last OR
  //        this seek is different than the current PTS OR
  //		the last seek was not precise (the we might not be at seek time)
  int64_t step = (int64_t)((1 / frame_rate_) * AV_TIME_BASE);
  if (!seek_request_ &&
      (fabs(time - seek_time_) >= step ||
       fabs(time - (int64_t)(GetTime() * AV_TIME_BASE)) >= step ||
       !(seek_flags_ & AVSEEK_FLAG_BACKWARD))) {
    std::mutex mtx;

    seek_time_ = time;
    seek_distance_ = distance;
    seek_flags_ &= ~AVSEEK_FLAG_BACKWARD;
    seek_flags_ &= ~AVSEEK_FLAG_BYTE;

    if (seek_flags == kSeekPreciseFlag) {
      seek_flags_ |= AVSEEK_FLAG_BACKWARD;
    }
    seek_request_ = true;
    seek_done_ = false;
    continue_read_thread_.notify_one();
    av_log(NULL, AV_LOG_INFO, "Seek Flag %d\n", (int)seek_flags_);
    std::unique_lock<std::mutex> lck(mtx);
    // Blocks until the seek request is done which we defined by
    // enquing either an image frame or audio frame
    // Important(Reda): Added a time out for the wait, to avoid dead lock while
    // stressing the jog or seek
    continue_after_seek_.wait_for(lck, std::chrono::milliseconds(10),
                                  [this] { return this->seek_done_; });
  }
}

/* seek in the stream */
void VideoState::SeekToFrame(int frame_nb) {
  // Only seek if
  // - there is no seek request in progress
  if (!seek_request_) {
    std::mutex mtx;

    seek_frame_ = frame_nb;
    seek_request_ = true;
    seek_done_ = false;
    seek_flags_ &= ~AVSEEK_FLAG_BYTE;
    seek_flags_ &= ~AVSEEK_FLAG_BACKWARD;

    seek_flags_ |= AVSEEK_FLAG_BACKWARD;
    // Need to convert the frame number into a time stamp that will be used
    // during the precise seek to land on the closest key frame before
    // requesting the frame number
    seek_time_ = (int64_t)(frame_nb * (1.0 / GetFrameRate()) * AV_TIME_BASE);
    continue_read_thread_.notify_one();
    std::unique_lock<std::mutex> lck(mtx);
    // Blocks until the seek request is done which we defined by
    // enquing either an image frame or audio frame
    // Important(Reda): Added a time out for the wait, to avoid dead lock while
    // stressing the jog or seek
    continue_after_seek_.wait_for(lck, std::chrono::milliseconds(10),
                                  [this] { return this->seek_done_; });
  }
}

// Lot's of discussion around big endian (may have to clean this up)
// See
// https://stackoverflow.com/questions/280162/is-there-a-way-to-do-a-c-style-compile-time-assertion-to-determine-machines-e
// and
// https://stackoverflow.com/questions/1001307/detecting-endianness-programmatically-in-a-c-program
int isBigEndian() {
  union {
    long int l;
    char c[sizeof(long int)];
  } u;
  u.l = 1;
  return (u.c[sizeof(long int) - 1] == 1);
}

double VideoState::ComputeTargetDelay(double delay) {
  double sync_threshold, diff = 0;

  /* update delay to follow master synchronisation source */
  if (GetMasterSyncType() != AV_SYNC_VIDEO_MASTER) {
    Clock *p_master_clock = nullptr;
    GetMasterClock(&p_master_clock);

    /* if video is slave, we try to correct big delays by
    duplicating or deleting a frame */
    diff = p_image_clock_->GetTime() - p_master_clock->GetTime();

    /* skip or repeat frame. We take into account the
    delay to compute the threshold. I still don't know
    if it is the best guess */
    sync_threshold =
        FFMAX(kAvSyncThresholdMin, FFMIN(kAvSyncThresholdMax, delay));
    if (!isnan(diff) && fabs(diff) < max_frame_duration_) {
      if (diff <= -sync_threshold) {
        delay = FFMAX(0, delay + diff);
      } else if (diff >= sync_threshold && delay > kAvSyncFrameDupThreshold) {
        delay = delay + diff;
      } else if (diff >= sync_threshold) {
        delay = 2 * delay;
      }
    }
  }

  av_log(NULL, AV_LOG_TRACE, "video: delay=%0.3f A-V=%f\n", delay, -diff);

  return delay;
}

/* get the current synchronization type */
VideoState::AvSyncType VideoState::GetMasterSyncType() const {
  if (sync_type_ == AV_SYNC_VIDEO_MASTER) {
    if (p_image_stream_ != nullptr) {
      return AV_SYNC_VIDEO_MASTER;
    } else {
      return AV_SYNC_AUDIO_MASTER;
    }
  } else if (sync_type_ == AV_SYNC_AUDIO_MASTER) {
    if (p_audio_stream_ != nullptr) {
      return AV_SYNC_AUDIO_MASTER;
    } else {
      return AV_SYNC_EXTERNAL_CLOCK;
    }
  } else {
    return AV_SYNC_EXTERNAL_CLOCK;
  }
}

void VideoState::GetMasterClock(Clock **pp_clock) const {
  AvSyncType master = GetMasterSyncType();
  if (master == AV_SYNC_VIDEO_MASTER) {
    *pp_clock = p_image_clock_;
  } else if (master == AV_SYNC_AUDIO_MASTER) {
    *pp_clock = p_audio_clock_;
  } else {
    *pp_clock = p_external_clock_;
  }
}

/* prepare a new audio buffer */
#ifdef SDL_ENABLED
void VideoState::GetAudioCallback(uint8_t *stream, int len, int audio_volume) {
#else
void VideoState::GetAudioCallback(uint8_t *stream, int len) {
#endif // SDL_ENABLED
  int audio_size, len1;

  while (len > 0) {
    if (audio_buffer_index_ >= audio_buffer_size_) {
      audio_size = DecodeAudioFrame();
      if (audio_size < 0) {
        /* if error, just output silence */
        p_audio_buffer_ = NULL;
        audio_buffer_size_ = audio_default_buffer_size_ /
                             audio_params_target_.frame_size_ *
                             audio_params_target_.frame_size_;
      } else {
        audio_buffer_size_ = audio_size;
      }
      audio_buffer_index_ = 0;
    }
    len1 = audio_buffer_size_ - audio_buffer_index_;
    if (len1 > len)
      len1 = len;
#ifdef SDL_ENABLED
    if (!is_muted_ && p_audio_buffer_ && audio_volume == SDL_MIX_MAXVOLUME) {
#else
    if (!is_muted_ && p_audio_buffer_) {
#endif // SDL_ENABLED
      memcpy(stream, (uint8_t *)p_audio_buffer_ + audio_buffer_index_, len1);
    } else {
      memset(stream, 0, len1);
#ifdef SDL_ENABLED
      if (!is_muted_ && p_audio_buffer_) {
        SDL_MixAudioFormat(stream,
                           (uint8_t *)p_audio_buffer_ + audio_buffer_index_,
                           AUDIO_S16SYS, len1, audio_volume);
      }
#endif // SDL_ENABLED
    }
    len -= len1;
    stream += len1;
    audio_buffer_index_ += len1;
  }
  audio_write_buffer_size_ = audio_buffer_size_ - audio_buffer_index_;
  if (!isnan(audio_pts_)) {
    /* Let's assume the audio driver that is used by SDL has two periods. */
    p_audio_clock_->SetTime(
        audio_pts_ -
            (double)(2 * audio_hw_buffer_size_ + audio_write_buffer_size_) /
                audio_params_target_.bytes_per_sec_,
        audio_serial_);
    // Sync external clock to audio clock
    Clock::SyncMasterToSlave(p_external_clock_, p_audio_clock_,
                             kAvNoSyncThreshold);
  }
}

int VideoState::SetSpeed(double requested_speed) {
  // If we request a different rates
  if (current_speed_ != requested_speed) {
    requested_speed_ = requested_speed;
    speed_request_ = true;
    continue_read_thread_.notify_one();
  }
  return 0;
}
