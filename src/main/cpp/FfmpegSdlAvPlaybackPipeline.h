#ifndef _FFMPEG_AV_PLAYBACK_PIPELINE_H_
#define _FFMPEG_AV_PLAYBACK_PIPELINE_H_

#include "FfmpegSdlAvPlayback.h"
#include "Pipeline.h"
#include "PipelineOptions.h"

/**
 * class FfmpegAVPlaybackPipeline
 *
 * Class representing a Ffmpeg audio-video pipeline.
 *
 * Note, currently this pipeline uses SDL to play audio/image
 */
class FfmpegSdlAvPlaybackPipeline : public CPipeline {
public:
  virtual uint32_t Init(const char *input_file);
  virtual void Dispose();
  FfmpegSdlAvPlaybackPipeline(CPipelineOptions *pOptions);
  virtual ~FfmpegSdlAvPlaybackPipeline();

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

  virtual uint32_t GetImageWidth(int *p_width) const;
  virtual uint32_t GetImageHeight(int *p_height) const;

  virtual uint32_t GetWindowHeight(int *p_height) const;
  virtual uint32_t GetWindowWidth(int *p_width) const;
  virtual uint32_t SetWindowSize(int width, int height);

  virtual uint32_t ShowWindow();
  virtual uint32_t HideWindow();
  virtual uint32_t IsVisible(int *visible) const;

  FfmpegSdlAvPlayback *p_sdl_playback_;
};

#endif //_FFMPEG_AV_PLAYBACK_PIPELINE_H_
