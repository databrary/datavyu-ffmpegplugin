package org.datavyu.plugins.ffmpeg;

import java.awt.Container;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.datavyu.plugins.PlayerStateEvent;
import org.datavyu.plugins.PlayerStateListener;
import org.datavyu.util.Utils;

public class FfmpegSdlAwtMediaPlayer extends FfmpegSdlMediaPlayer {
  private static final Logger logger = LogManager.getFormatterLogger(FfmpegSdlAwtMediaPlayer.class);
  private Container container;

  public FfmpegSdlAwtMediaPlayer(URI mediaPath) {
    super(mediaPath);
    this.container = null;
  }

  public FfmpegSdlAwtMediaPlayer(URI mediaPath, Container container) {
    super(mediaPath);
    this.container = container;
  }


  @Override
  public void init() {
    addMediaPlayerStateListener(new PlayerStateListenerImpl());

    initNative(); // start the event queue, make sure to register all state/error listeners before
    long[] newNativeMediaRef = new long[1];
    long wid = 0;
    if (container != null) {
      try {
        wid = Utils.getHWnd(container);
      } catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
        logger.error("Error getting window handle: " + e.getMessage());
      }
    }

    int rc = ffmpegInitPlayer(newNativeMediaRef, mediaPath, wid);
    if (0 != rc) {
      throwMediaErrorException(rc, null);
    }

    nativeMediaRef = newNativeMediaRef[0];
  }

  private class PlayerStateListenerImpl implements PlayerStateListener {

    @Override
    public void onReady(PlayerStateEvent evt) {}

    @Override
    public void onPlaying(PlayerStateEvent evt) {}

    @Override
    public void onPause(PlayerStateEvent evt) {}

    @Override
    public void onStop(PlayerStateEvent evt) {}

    @Override
    public void onStall(PlayerStateEvent evt) {}

    @Override
    public void onFinish(PlayerStateEvent evt) {}

    @Override
    public void onHalt(PlayerStateEvent evt) {}
  }
}
