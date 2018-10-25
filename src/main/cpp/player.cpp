#include "FfmpegSdlAvPlayback.h"
#include "MpvAvPlayback.h"

int runFFmpegPlayer(const char *input_filename, AVInputFormat *file_iformat) {

  av_log_set_flags(AV_LOG_SKIP_REPEATED);
  av_log(NULL, AV_LOG_WARNING, "Init Network\n");
  FfmpegSdlAvPlayback *p_player = new FfmpegSdlAvPlayback();
  int err = p_player->OpenVideo(input_filename, file_iformat);
  if (err) {
    fprintf(stderr, "Error %d when opening input file %s", err, input_filename);
    return err;
  }
  FfmpegSdlAvPlayback::InitializeAndListenForEvents(p_player);
}

int runMpvPlayer(const char *input_filename) {
  MpvAvPlayback *pPlayer = new MpvAvPlayback();
  pPlayer->init_and_event_loop(input_filename);
  return 0;
}

int main(int argc, char **argv) {
  static const char *input_filename =
      "Nature_30fps_1080p.mp4"; //"Nature_30fps_1080p.mp4" //
                                //"DatavyuSampleVideo.mp4" // "counter.mp4" //
                                //"http://www.html5videoplayer.net/videos/toystory.mp4";
  AVInputFormat *file_iformat = nullptr;

  /******************************************************
   * Uncomment this part to test the ffmpeg player
   ******************************************************/
  runFFmpegPlayer(input_filename, file_iformat);

  /******************************************************
   * Uncomment this part to test the mpv player
   ******************************************************/
  //runMpvPlayer(input_filename);

  return 0;
}
