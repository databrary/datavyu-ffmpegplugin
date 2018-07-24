#include "FfmpegJavaAvPlayback.h"

void FfmpegJavaAvPlayback::init() {
	/* register all codecs, demux and protocols */
#if CONFIG_AVDEVICE
	avdevice_register_all();
#endif
	avformat_network_init();

	//if (display_disable) {
	//	pVideoState->set_video_disable(1);
	//}
}

FfmpegJavaAvPlayback::FfmpegJavaAvPlayback(
	const char * filename,
	AVInputFormat * iformat):
FfmpegAvPlayback(filename, iformat) {
	pVideoState->set_destroy_callback([this] {
		this->destroy();
	});
	pVideoState->set_step_to_next_frame_callback([this] {
		this->step_to_next_frame();
	});
}

FfmpegJavaAvPlayback::~FfmpegJavaAvPlayback()
{ }

VideoState * FfmpegJavaAvPlayback::get_VideoState() { return pVideoState; }

void FfmpegJavaAvPlayback::destroy() {
	// only necessary when using as library -- has no effect otherwise
	// stop_display_loop();

	// close the VideoState Stream
	if (pVideoState)
		pVideoState->stream_close();

#if CONFIG_AVFILTER
	//av_freep(&vfilters_list);
#endif
	avformat_network_deinit();

	if (show_status)
		printf("\n");

	av_log(NULL, AV_LOG_QUIET, "%s", "");

	exit(0);
}


//TODO(Reda): should refactor the name of this funct
void FfmpegJavaAvPlayback::init_and_start_display_loop() {
	init();

	pVideoState->stream_start();

	//Loop and event should be implmented in the Java side
}

void FfmpegJavaAvPlayback::set_balance(float fBalance) {
}

float FfmpegJavaAvPlayback::get_balance() {
	return 0.0f;
}

void FfmpegJavaAvPlayback::set_audioSyncDelay(long lMillis){
}

long FfmpegJavaAvPlayback::get_audioSyncDelay() {
	return 0;
}

// Has image data to display by checking the video frameque
// different than has_image_data of the VideoState
bool FfmpegJavaAvPlayback::has_image_data() {
	if (pVideoState->get_video_st()) {
		if (pVideoState->get_pPictq()->nb_remaining() > 0) {
			return true;
		}
	}

	return false;
}

// Has image data to display by checking the audio frameque
// different than has_audio_data of the VideoState
bool FfmpegJavaAvPlayback::has_audio_data() {
	if (pVideoState->get_audio_st()) {
		if (pVideoState->get_pSampq()->nb_remaining() > 0) {
			return true;
		}
	}

	return false;
}

int FfmpegJavaAvPlayback::get_image_width() {
	return pVideoState->get_video_st() ? pVideoState->get_image_width() : 0;
}

int FfmpegJavaAvPlayback::get_image_height() {
	return pVideoState->get_video_st() ? pVideoState->get_image_height() : 0;
}

AudioFormat FfmpegJavaAvPlayback::get_audio_format() {
	return AudioFormat();
}

PixelFormat FfmpegJavaAvPlayback::get_pixel_format() {
	return PixelFormat();
}
