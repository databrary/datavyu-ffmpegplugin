#ifndef _PIPELINEDATA_H_
#define _PIPELINEDATA_H_

#include "AudioVideoFormats.h"
#include "Pipeline.h"
#include "PipelineOptions.h"
#include <stdint.h>
#include <sys/stat.h>

class CPipelineData : public CPipeline {
public:
  CPipelineData(CPipelineOptions *p_options = NULL);
  virtual ~CPipelineData();

  virtual uint32_t Init(const char *filename) = 0;
  virtual void Dispose();

  virtual uint32_t Play() = 0;
  virtual uint32_t Stop() = 0;
  virtual uint32_t Pause() = 0;
  virtual uint32_t StepForward() = 0;
  virtual uint32_t Finish() = 0;

  virtual uint32_t Seek(double seek_time) = 0;

  virtual uint32_t GetDuration(double *p_duration) = 0;
  virtual uint32_t GetStreamTime(double *p_stream_time) = 0;
  virtual uint32_t GetFps(double *p_fps) = 0;

  virtual uint32_t SetRate(float rate) = 0;
  virtual uint32_t GetRate(float *p_rate) = 0;

  virtual uint32_t SetVolume(float volume) = 0;
  virtual uint32_t GetVolume(float *p_volume) = 0;

  virtual uint32_t SetBalance(float balance) = 0;
  virtual uint32_t GetBalance(float *p_balance) = 0;

  virtual uint32_t SetAudioSyncDelay(long millis) = 0;
  virtual uint32_t GetAudioSyncDelay(long *p_millis) = 0;

  virtual uint32_t HasAudioData(bool *p_audio_data) const = 0;
  virtual uint32_t HasImageData(bool *b_image_data) const = 0;
  virtual uint32_t GetImageWidth(int *p_width) const = 0;
  virtual uint32_t GetImageHeight(int *p_height) const = 0;
  virtual uint32_t GetAudioFormat(AudioFormat *p_audio_format) const = 0;
  virtual uint32_t GetPixelFormat(PixelFormat *p_pixel_format) const = 0;
  virtual uint32_t UpdateImageBuffer(uint8_t *p_image_buffer,
                                     const long len) = 0;
  virtual uint32_t UpdateAudioBuffer(uint8_t *p_audio_buffer,
                                     const long len) = 0;
};

#endif //_PIPELINEDATA_H_
