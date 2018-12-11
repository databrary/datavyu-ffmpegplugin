package org.datavyu.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.javacpp.avformat.AVFormatContext;
import org.bytedeco.javacpp.avformat.AVStream;
import org.bytedeco.javacpp.avutil;


import java.io.File;

import static org.bytedeco.javacpp.avcodec.avcodec_register_all;
import static org.bytedeco.javacpp.avdevice.avdevice_register_all;
import static org.bytedeco.javacpp.avformat.av_dump_format;
import static org.bytedeco.javacpp.avformat.av_register_all;
import static org.bytedeco.javacpp.avformat.avformat_find_stream_info;
import static org.bytedeco.javacpp.avformat.avformat_network_init;
import static org.bytedeco.javacpp.avformat.avformat_open_input;
import static org.bytedeco.javacpp.avutil.AV_LOG_ERROR;
import static org.bytedeco.javacpp.avutil.av_log_set_level;

public class FrameRate {

  private String file;

  private AVFormatContext pFormatCtx;
  private AVStream videoAVStream;

  private static Logger logger = LogManager.getFormatterLogger(FrameRate.class);

  //Load bytedeco FFmpeg Libraries
  static {
    try {
      Loader.load(org.bytedeco.javacpp.avutil.class);
      Loader.load(org.bytedeco.javacpp.swresample.class);
      Loader.load(org.bytedeco.javacpp.avcodec.class);
      Loader.load(org.bytedeco.javacpp.avformat.class);
      Loader.load(org.bytedeco.javacpp.swscale.class);

      // Register all formats and codecs
      avcodec_register_all();
      av_register_all();
      avformat_network_init();

      Loader.load(org.bytedeco.javacpp.avdevice.class);
      avdevice_register_all();
      //Log only AV errors
      av_log_set_level(AV_LOG_ERROR);
    } catch (Exception e) {
      logger.error("Failed to load " + FrameRate.class + " " + e);
    }
  }

  public static FrameRate createDefaultFrameRate(String file){ return new FrameRate(file); }

  private FrameRate(String mediaPath){
    this.file = mediaPath;
    init();
  }

  private void init() {
    /** stores information about the file format in the AVFormatContext structure */
    pFormatCtx = new AVFormatContext(null);

    int videoStream = -1;

    /** Reads the file header and stores information about the file format in the AVFormatContext */
    if(avformat_open_input(pFormatCtx, file.toString(), null, null) < 0){
      logger.error("Can't read the file header");
      throw new IllegalStateException("Can't read the file: "+ file.toString());
    }

    /** Retrieve Stream information */
    if(avformat_find_stream_info(pFormatCtx, (PointerPointer) null) < 0){
      logger.error("Cannot retrieve stream information");
      throw new IllegalStateException("Cannot retrieve stream information "+ file.toString());
    }

    /** Dump information about file onto standard error */
    av_dump_format(pFormatCtx, 0, file.toString(), 0);

    /** Find the first video stream */
    for (int i = 0; i < pFormatCtx.nb_streams(); i++) {
      videoAVStream = pFormatCtx.streams(i);
      if (videoAVStream.codecpar().codec_type() == avutil.AVMEDIA_TYPE_VIDEO) {
        videoStream = i;
        break;
      }
    }

    if (videoStream == -1) {
      logger.error("Didn't find a video stream");
      throw new IllegalStateException("Didn't find a video stream in "+ file.toString());
    }
  }

  public float getFPS() {
    if(videoAVStream == null) {
      return 30;
    } else {
      avutil.AVRational rational = videoAVStream.avg_frame_rate();
      if(rational.num() == 0 && rational.den() == 0) {
        rational = videoAVStream.r_frame_rate();
      }
      logger.info("File: " + file.toString() + " FPS: "+(float) rational.num() / rational.den());
      return (float) rational.num() / rational.den();
    }
  }
}
