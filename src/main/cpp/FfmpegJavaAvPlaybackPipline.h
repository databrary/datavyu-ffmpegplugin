#ifndef FFMPEGJAVAAVPLAYBACKPIPELINE_H_
#define FFMPEGJAVAAVPLAYBACKPIPELINE_H_

#include "FfmpegJavaAvPlayback.h"
#include "MediaPlayerErrors.h";
#include "PipelineData.h"

class FfmpegJavaAvPlaybackPipline : public CPipelineData {
public:
  virtual uint32_t Init(const char *input_file);
  virtual void Dispose();
  FfmpegJavaAvPlaybackPipline(CPipelineOptions *p_options);
  virtual ~FfmpegJavaAvPlaybackPipline();

private:
  virtual uint32_t Play();
  virtual uint32_t Stop();
  virtual uint32_t Pause();
  virtual uint32_t StepForward();
  virtual uint32_t StepBackward();
  virtual uint32_t Finish();

  virtual uint32_t Seek(double seek_time);

  virtual uint32_t GetDuration(double *p_duration);
  virtual uint32_t GetStreamTime(double *p_stream_time);
  virtual uint32_t GetFps(double *p_fps);

  virtual uint32_t SetRate(float rate);
  virtual uint32_t GetRate(float *p_rate);

  virtual uint32_t SetVolume(float volume);
  virtual uint32_t GetVolume(float *p_volume);

  virtual uint32_t SetBalance(float balance);
  virtual uint32_t GetBalance(float *p_balance);

  virtual uint32_t SetAudioSyncDelay(long millis);
  virtual uint32_t GetAudioSyncDelay(long *p_millis);

  // Fullfill the data interface
  virtual uint32_t HasAudioData(bool *p_audio_data) const;
  virtual uint32_t HasImageData(bool *p_image_data) const;
  virtual uint32_t GetImageWidth(int *p_width) const;
  virtual uint32_t GetImageHeight(int *p_height) const;
  virtual uint32_t GetAudioFormat(AudioFormat *p_audio_params) const;
  virtual uint32_t GetPixelFormat(PixelFormat *p_pixel_format) const;
  virtual uint32_t UpdateImageBuffer(uint8_t *p_image_data, const long len);
  virtual uint32_t UpdateAudioBuffer(uint8_t *p_audio_data, const long len);

  FfmpegJavaAvPlayback *p_java_playback_;
};

#endif // !FFMPEGJAVAAVPLAYBACKPIPELINE_H_
