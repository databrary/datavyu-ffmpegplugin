#ifndef FFMPEGJAVAAVPLAYBACK_H_
#define FFMPEGJAVAAVPLAYBACK_H_

#include "FfmpegAVPlayback.h"
#include "VideoState.h"

class FfmpegJavaAvPlayback : public FfmpegAvPlayback {
private:
  const AudioFormat *kPtrAudioFormat;
  const PixelFormat *kPtrPixelFormat;
  const int kAudioBufferSizeInBy;

  struct SwsContext *img_convert_ctx_;
  double remaining_time_to_display_;

public:
  FfmpegJavaAvPlayback(const AudioFormat *kPtrAudioFormat,
                       const PixelFormat *kPtrPixelFormat,
                       const int kAudioBufferSizeInBy);
  virtual ~FfmpegJavaAvPlayback();

  int Init(const char *filename, AVInputFormat *iformat);

  int AudioOpen(int64_t wanted_channel_layout, int wanted_nb_channels,
                int wanted_sample_rate, struct AudioParams *audio_hw_params);

  VideoState *GetVideoState();

  void Destroy();

  int StartStream();

  void SetBalance(float fBalance);
  float GetBalance();

  void SetAudioSyncDelay(long lMillis);
  long getAudioSyncDelay();

  int GetImageWidth() const;
  int GetImageHeight() const;

  bool HasImageData() const;
  bool HasAudioData() const;

  bool DoDisplay(double *remaining_time);

  void UpdateImageBuffer(uint8_t *pImageData, const long len);
  void UpdateAudioBuffer(uint8_t *pAudioData, const long len);

  void GetAudioFormat(AudioFormat *pAudioFormat);
  void GetPixelFormat(PixelFormat *pPixelFormat);
};

#endif // end of FFMPEGJAVAAVPLAYBACK_H_
