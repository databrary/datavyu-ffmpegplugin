#ifndef FFMPEGJAVAAVPLAYBACK_H_
#define FFMPEGJAVAAVPLAYBACK_H_

#include "VideoState.h"
#include "FfmpegAVPlayback.h"

class FfmpegJavaAvPlayback : public FfmpegAvPlayback {
private:
	const AudioFormat* pAudioFormat;
	const PixelFormat* pPixelFormat;
	const int audioBufferSizeInBy;
	struct SwsContext* img_convert_ctx;
	void init();
public:
	FfmpegJavaAvPlayback(const char *filename, 
		AVInputFormat *iformat, 
		const AudioFormat *pAudioFormat, 
		const PixelFormat *pPixelFormat,
		const int audioBufferSizeInBy);
	~FfmpegJavaAvPlayback();

	int audio_open(int64_t wanted_channel_layout, int wanted_nb_channels,
		int wanted_sample_rate, struct AudioParams *audio_hw_params);

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

	void get_audio_format(AudioFormat* pAudioFormat);
	void get_pixel_format(PixelFormat* pPixelFormat);
};

#endif // end of FFMPEGJAVAAVPLAYBACK_H_

