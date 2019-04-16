package org.datavyu.plugins.mpv;

import java.lang.reflect.InvocationTargetException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.datavyu.plugins.MediaException;
import org.datavyu.util.Utils;
import sun.awt.windows.WComponentPeer;

import org.datavyu.plugins.PlayerStateEvent;
import org.datavyu.plugins.PlayerStateListener;

import java.awt.*;
import java.net.URI;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class MpvAwtMediaPlayer extends MpvMediaPlayer {
  private static final Logger logger = LogManager.getFormatterLogger(MpvAwtMediaPlayer.class);
  private Container container;

  /**
   * Create an MPV media player instance and play through java framework
   *
   * @param mediaPath The media path
   * @param container The container to display the frame in
   */
  public MpvAwtMediaPlayer(URI mediaPath, Container container) {
    super(mediaPath);
    this.container = container;
  }

  @Override
  public void init() {
    addMediaPlayerStateListener(new PlayerStateListenerImpl());
    initNative(); // starts the event queue, make sure to register all state/error listeners before
    long wid = 0;
    if (container != null) {
      try {
        wid = Utils.getConateinerId(container);
      } catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
        logger.error("Error getting window handle: " + e.getMessage());
      }
    }

    long[] newNativeMediaRef = new long[1];
    int rc = mpvInitPlayer(newNativeMediaRef, mediaPath, wid);
    if (0 != rc) {
      throwMediaErrorException(rc, null);
    }

    nativeMediaRef = newNativeMediaRef[0];
  }

  private long getWindowID(Container container) {
    if (container.getPeer() == null) {
      throw new RuntimeException("Unable to retrieve window id");
    }

    return ((WComponentPeer) container.getPeer()).getHWnd();
  }

  @Override
  protected void playerSeekToFrame(int frameNumber) throws MediaException {
    throw new NotImplementedException();
  }

  private class PlayerStateListenerImpl implements PlayerStateListener {

    @Override
    public void onReady(PlayerStateEvent evt) {
      container.setSize(getImageWidth(), getImageHeight());
    }

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
