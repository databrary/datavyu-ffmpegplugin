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

int FfmpegJavaAvPlayback::get_image_width() {
	return pVideoState->get_image_width();
}

int FfmpegJavaAvPlayback::get_image_height() {
	return pVideoState->get_image_height();
}

//TODO(Reda): implement get audio format function
AudioFormat FfmpegJavaAvPlayback::get_audio_format() {
	return AudioFormat();
}

//TODO(Reda): implement get audio format function
PixelFormat FfmpegJavaAvPlayback::get_pixel_format() {
	return PixelFormat();
}
