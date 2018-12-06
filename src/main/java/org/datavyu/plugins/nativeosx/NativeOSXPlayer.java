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
  public native void addNativeOSXCoreAnimationLayer(String path);
  public native void stop(int id);
  public native void play(int id);
  public native void setTime(long time, int id);
  public native void setTimePrecise(long time, int id);
  public native void setTimeModerate(long time, int id);
  public native void setVolume(float time, int id);
  public native void release(int id);
  public native double getMovieHeight(int id);
  public native double getMovieWidth(int id);
  public native long getCurrentTime(int id);
  public native long getDuration(int id);
  public native float getRate(int id);
  public native void setRate(float rate, int id);
  public native boolean isPlaying(int id);
  public native float getFPS(int id);
}
