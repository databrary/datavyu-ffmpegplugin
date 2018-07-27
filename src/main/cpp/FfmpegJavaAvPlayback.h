#ifndef FFMPEGJAVAAVPLAYBACK_H_
#define FFMPEGJAVAAVPLAYBACK_H_

#include "VideoState.h"
#include "FfmpegAVPlayback.h"

// TODO(Reda): Implement this interface (Most of it is implemented in the super-class)
// Only the definition for the methods for the java hock-up go here
class FfmpegJavaAvPlayback : public FfmpegAvPlayback {
private:
	void init();
public:
	FfmpegJavaAvPlayback(const char *filename, AVInputFormat *iformat);
	~FfmpegJavaAvPlayback();

	VideoState* get_VideoState();

	void destroy();

	void init_and_start_display_loop();

	void set_balance(float fBalance);
	float get_balance();

	void set_audioSyncDelay(long lMillis);
	long get_audioSyncDelay();

	int get_image_width();
	int get_image_height();

	AudioFormat get_audio_format();
	PixelFormat get_pixel_format();
};

#endif // end of FFMPEGJAVAAVPLAYBACK_H_

