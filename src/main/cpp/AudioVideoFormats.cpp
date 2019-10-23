#include "AudioVideoFormats.h"
#include "AudioVideoFormats.h"

extern "C" {
#include <libavcodec/avcodec.h> // codecs
}

AudioFormat::AudioFormat() {}

void AudioFormat::ToAudioParams(AudioParams *pAudioParams) const {
  pAudioParams->num_channels_ = num_channels_;
  pAudioParams->frame_size_ = frame_size_;
  pAudioParams->frequency_ = sample_rate_;
  pAudioParams->bytes_per_sec_ = frame_rate_;
  pAudioParams->channel_layout_ = av_get_default_channel_layout(num_channels_);
  // TODO(fraudies): Support more formats (currently not necessary because of
  // the javax SoundLine does not support more formats
  if (strcmp("PCM_UNSIGNED", encoding_name_.c_str()) == 0) {
    pAudioParams->sample_format_ = AV_SAMPLE_FMT_U8;
  } else if (strcmp("PCM_SIGNED", encoding_name_.c_str()) == 0) {
    pAudioParams->sample_format_ = AV_SAMPLE_FMT_S16;
  }
}

PixelFormat::PixelFormat() {}
