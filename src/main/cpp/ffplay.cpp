#include "FfmpegSdlAvPlayback.h"

int main(int argc, char **argv) {
	av_log_set_flags(AV_LOG_SKIP_REPEATED);
	av_log(NULL, AV_LOG_WARNING, "Init Network\n");
	static const char* input_filename = "Nature_30fps_1080p.mp4"; //"Nature_30fps_1080p.mp4" // "DatavyuSampleVideo.mp4" // "counter.mp4";
	AVInputFormat *file_iformat = nullptr;
	FfmpegSdlAvPlayback* pPlayer = new FfmpegSdlAvPlayback();
	int err = pPlayer->Init(input_filename, file_iformat);
	if (err) {
		fprintf(stderr, "Error %d when opening input file %s", err, input_filename);
		return err;
	}
	pPlayer->init_and_event_loop();
	return 0;
}
