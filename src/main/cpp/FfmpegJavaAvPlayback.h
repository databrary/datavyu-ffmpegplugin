#ifndef FFMPEGJAVAAVPLAYBACK_H_
#define FFMPEGJAVAAVPLAYBACK_H_

#include "FfmpegAVPlayback.h"
#include "VideoState.h"

class FfmpegJavaAvPlayback : public FfmpegAvPlayback {
private:
  const AudioFormat *pAudioFormat;
  const PixelFormat *pPixelFormat;
  const int audioBufferSizeInBy;
  struct SwsContext *img_convert_ctx;
  double remaining_time_to_display;

public:
  FfmpegJavaAvPlayback(const AudioFormat *pAudioFormat,
                       const PixelFormat *pPixelFormat,
                       const int audioBufferSizeInBy);
  virtual ~FfmpegJavaAvPlayback();

  int Init(const char *filename, AVInputFormat *iformat);

  int audio_open(int64_t wanted_channel_layout, int wanted_nb_channels,
                 int wanted_sample_rate, struct AudioParams *audio_hw_params);

  VideoState *get_VideoState();

  void destroy();

  int start_stream();

  void set_balance(float fBalance);
  float get_balance();

  void set_audioSyncDelay(long lMillis);
  long get_audioSyncDelay();

  int get_image_width() const;
  int get_image_height() const;

  bool has_image_data() const;
  bool has_audio_data() const;

  bool do_display(double *remaining_time);

  void update_image_buffer(uint8_t *pImageData, const long len);
  void update_audio_buffer(uint8_t *pAudioData, const long len);

  void get_audio_format(AudioFormat *pAudioFormat);
  void get_pixel_format(PixelFormat *pPixelFormat);
};

#endif // end of FFMPEGJAVAAVPLAYBACK_H_
