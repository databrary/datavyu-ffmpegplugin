#ifndef FFMPEGJAVAAVPLAYBACK_H_
#define FFMPEGJAVAAVPLAYBACK_H_

#include "VideoState.h"
#include "FfmpegAVPlayback.h"

class FfmpegJavaAvPlayback : public FfmpegAvPlayback {
private:
	void init();
public:
	FfmpegJavaAvPlayback(const char *filename, AVInputFormat *iformat);
	~FfmpegJavaAvPlayback();

	VideoState* get_VideoState();

	void destroy();

	void init_and_start_stream();

	void set_balance(float fBalance);
	float get_balance();

	void set_audioSyncDelay(long lMillis);
	long get_audioSyncDelay();

	int get_image_width();
	int get_image_height();

	bool has_image_data() const;
	bool has_audio_data() const;

	bool do_display();

	void get_image_buffer(uint8_t** ppImageData, long* pLen);
	void get_audio_buffer(uint8_t** ppAudioData, long* pLen);

	AudioFormat get_audio_format();
	PixelFormat get_pixel_format();
};

#endif // end of FFMPEGJAVAAVPLAYBACK_H_

