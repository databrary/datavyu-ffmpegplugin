#include "AudioVideoFormats.h"

extern "C" {
	#include <libavcodec/avcodec.h> // codecs
}

AudioFormat::AudioFormat() {}

void AudioFormat::toAudioParams(AudioParams* pAudioParams) const {
	pAudioParams->channels = channels;
	pAudioParams->frame_size = frameSize;
	pAudioParams->freq = sampleRate;
	pAudioParams->bytes_per_sec = frameRate;
	pAudioParams->channel_layout = av_get_default_channel_layout(channels);
	// TODO(fraudies): Support more formats (currently not necessary because of the javax SoundLine
	if (strcmp("PCM_UNSIGNED", encoding.c_str()) == 0) {
		pAudioParams->fmt = AV_SAMPLE_FMT_U8;
	}
	else if (strcmp("PCM_SIGNED", encoding.c_str()) == 0) {
		pAudioParams->fmt = AV_SAMPLE_FMT_S16;
	}
}

PixelFormat::PixelFormat() {}