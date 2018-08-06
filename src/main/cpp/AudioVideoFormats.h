#ifndef _AUDIO_VIDEO_FORMATS_H_
#define _AUDIO_VIDEO_FORMATS_H_
#include <string>

extern "C" {
	#include "libavutil/samplefmt.h"
	#include "libavutil/pixfmt.h"
}

// TODO(fraudies): Consolidate AudioParams and AudioFormat
typedef struct AudioParams {
	int					freq;
	int					channels;
	int64_t				channel_layout;
	enum AVSampleFormat fmt;
	int					frame_size;
	int					bytes_per_sec;
} AudioParams;


class AudioFormat {
public:
	std::string encoding;
	float sampleRate;
	int sampleSizeInBits;
	int channels;
	int frameSize;
	float frameRate;
	bool bigEndian;
	AudioFormat();
	void toAudioParams(AudioParams* pAudioParams) const;
};

class PixelFormat {
public:
	AVPixelFormat type;
	int numComponents;
	PixelFormat();
};

#endif // _AUDIO_VIDEO_FORMATS_H_

