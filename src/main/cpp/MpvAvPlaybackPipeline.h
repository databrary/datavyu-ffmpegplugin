#ifndef _MPV_AV_PLAYBACK_PIPELINE_H_
#define _MPV_AV_PLAYBACK_PIPELINE_H_

#include "MpvAvPlayback.h"
#include "Pipeline.h"
#include "PipelineOptions.h"

/**
 * class MpvAvPlaybackPipeline
 *
 * Class representing a MPV audio-video pipeline.
 *
 * This pipeline is using MPV API "client.api" to render the
 * and audio video stream in a any window that is referenced by
 * a window ID, Note that we are using a Java Frame.
 */
class MpvAvPlaybackPipeline : public CPipeline {
public:
  virtual uint32_t Init(const char *input_file);
  virtual void Dispose();

  MpvAvPlaybackPipeline(CPipelineOptions *pOptions, const intptr_t windowID);
  virtual ~MpvAvPlaybackPipeline();

  intptr_t window_id_;

private:
  virtual uint32_t Play();
  virtual uint32_t Stop();
  virtual uint32_t Pause();
  virtual uint32_t StepForward();
  virtual uint32_t StepBackward();
  virtual uint32_t Finish();

  virtual uint32_t Seek(double seek_time, int seek_flags);
  virtual uint32_t SeekToFrame(int frame_nb);

  virtual uint32_t GetDuration(double *p_duration);
  virtual uint32_t GetStreamTime(double *p_stream_time);
  virtual uint32_t GetFps(double *pdFps);
  virtual uint32_t GetImageWidth(int *p_width) const;
  virtual uint32_t GetImageHeight(int *p_height) const;

  virtual uint32_t SetRate(float rate);
  virtual uint32_t GetRate(float *p_rate);

  virtual uint32_t SetVolume(float volume);
  virtual uint32_t GetVolume(float *p_volume);

  virtual uint32_t SetBalance(float balance);
  virtual uint32_t GetBalance(float *p_balance);

  virtual uint32_t SetAudioSyncDelay(long millis);
  virtual uint32_t GetAudioSyncDelay(long *p_millis);

  MpvAvPlayback *p_mpv_playback_;
};

#endif //_MPV_AV_PLAYBACK_PIPELINE_H_
