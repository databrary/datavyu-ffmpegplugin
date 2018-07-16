#include "SDLPlayData.h"

int main(int argc, char **argv) {
	av_log_set_flags(AV_LOG_SKIP_REPEATED);
	av_log(NULL, AV_LOG_WARNING, "Init Network\n");
	static const char* input_filename = "counter.mp4";
	AVInputFormat *file_iformat = nullptr;
	SDLPlayData* pPlayer = new SDLPlayData(input_filename, file_iformat);
	pPlayer->init();
	pPlayer->event_loop();
	pPlayer->destroy();

	return 0;
}
