#ifndef FFMPEGAVPLAYBACK_H_
#define FFMPEGAVPLAYBACK_H_

#include "VideoState.h"

class FfmpegAvPlayback {
protected:
	VideoState* pVideoState;
	double frame_timer;

	int width, height;

	int force_refresh;
	int display_disable;
	
	int frame_drops_late;
	double rdftspeed;
	double last_vis_time;

	void stream_toggle_pause();
	int get_frame_timer();
	void set_frame_timer(int newFrame_timer);
	void set_force_refresh(int refresh);
	double vp_duration(Frame *vp, Frame *nextvp, double max_frame_duration);
public:
	FfmpegAvPlayback();
	int Init(const char *filename, AVInputFormat *iformat, int audio_buffer_size);
	virtual ~FfmpegAvPlayback();
	virtual void set_player_state_callback_func(PlayerStateCallback callback, const std::function<void()>& func);
	virtual void play();
	virtual void stop();
	virtual void toggle_pause();
	virtual void stream_seek(int64_t pos, int64_t rel, int seek_by_bytes);
	virtual double get_duration() const;
	virtual double get_stream_time() const;
	virtual double get_fps() const;
	virtual int set_rate(double rate);
	virtual double get_rate() const;
	int64_t get_start_time() const;
	int64_t get_seek_pos() const;
	void step_to_next_frame();
};


#endif // FFMPEGAVPLAYBACK_H_
