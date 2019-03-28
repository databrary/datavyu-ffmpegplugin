package org.datavyu.plugins.nativeosx;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.datavyu.util.LibraryLoader;

import java.awt.*;
import java.net.URI;

public class NativeOSXPlayer extends Canvas {

  private static Logger logger = LogManager.getLogger(NativeOSXPlayer.class);
  private final URI mediaPath;

  static {
    try {
      LibraryLoader.extractAndLoad("NativeOSXCanvas");
    } catch (Exception e) {
      logger.error("Unable to load the native library: ", e);
    }
  }

  public NativeOSXPlayer(URI mediaPath) {
    this.mediaPath = mediaPath;
  }

  public void addNotify() {
    super.addNotify();
    addNativeOSXCoreAnimationLayer("file://" + mediaPath.getPath());
  }

  // This method is implemented in native code. See NativeOSXCanvas.m
  native void addNativeOSXCoreAnimationLayer(String path);

  native void stop(int id);

  native void play(int id);

  native void setTime(long time, int id);

  native void setTimePrecise(long time, int id);

  native void setTimeModerate(long time, int id);

  native void setVolume(float time, int id);

  native void release(int id);

  native double getMovieHeight(int id);

  native double getMovieWidth(int id);

  native long getCurrentTime(int id);

  native long getDuration(int id);

  native float getRate(int id);

  native void setRate(float rate, int id);

  native boolean isPlaying(int id);

  native float getFPS(int id);
}
