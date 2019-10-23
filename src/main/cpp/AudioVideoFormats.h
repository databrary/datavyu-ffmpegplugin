#ifndef _AUDIO_VIDEO_FORMATS_H_
#define _AUDIO_VIDEO_FORMATS_H_
#include <string>

extern "C" {
#include "libavutil/pixfmt.h"
#include "libavutil/samplefmt.h"
}

// TODO(fraudies): Consolidate AudioParams and AudioFormat
typedef struct AudioParams {
  int frequency_;
  int num_channels_;
  int64_t channel_layout_;
  enum AVSampleFormat sample_format_;
  int frame_size_;
  int bytes_per_sec_;
} AudioParams;

class AudioFormat {
public:
  std::string encoding_name_;
  float sample_rate_;
  int sample_size_in_bits_;
  int num_channels_;
  int frame_size_;
  float frame_rate_;
  bool is_big_endian_;
  AudioFormat();
  void ToAudioParams(AudioParams *pAudioParams) const;
};

class PixelFormat {
public:
  AVPixelFormat pixel_format_;
  int num_components_;
  PixelFormat();
};

#endif // _AUDIO_VIDEO_FORMATS_H_
