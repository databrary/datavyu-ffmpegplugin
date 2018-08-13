#include "FfmpegSdlAvPlayback.h"

int main(int argc, char **argv) {
	av_log_set_flags(AV_LOG_SKIP_REPEATED);
	av_log(NULL, AV_LOG_WARNING, "Init Network\n");
	static const char* input_filename = "Nature_30fps_1080p.mp4";
	AVInputFormat *file_iformat = nullptr;
	FfmpegSdlAvPlayback* pPlayer = new FfmpegSdlAvPlayback(input_filename, file_iformat);
	pPlayer->init_and_event_loop();
	return 0;
}
