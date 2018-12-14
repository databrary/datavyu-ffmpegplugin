package org.datavyu.plugins.nativeosx;


import org.datavyu.plugins.MediaError;
import org.datavyu.plugins.MediaException;
import org.datavyu.plugins.PlayerStateEvent;
import org.datavyu.plugins.PlayerStateListener;

import java.awt.*;
import java.io.File;
import java.net.URI;

public class AVFoundationMediaPlayer extends NativeOSXMediaPlayer {

  private Container container;
  private final Object readyLock = new Object();

  public AVFoundationMediaPlayer(URI mediaPath, Container container) {
    super(mediaPath);
    this.addMediaPlayerStateListener(new AVFoundationMediaPlayer.PlayerStateListenerImpl());
    this.container = container;
  }

  @Override
  public void init() {
    initNative();

    // Test if he file exists
    File mediaFile = new File(mediaPath);
    if (!mediaFile.exists()){
      throwMediaErrorException(MediaError.ERROR_MEDIA_INVALID.code(), null);
    }

    container.addNotify();
    container.add(nativePlayerCanvas, BorderLayout.CENTER);

    incPlayerCount();
    sendPlayerStateEvent(eventPlayerReady, 0);

    synchronized (readyLock) {
      try {
        readyLock.wait();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  protected void playerSeek(double streamTime, int flags) throws MediaException {
    switch (flags){
      case NORMAL_SEEK_FLAG:
        EventQueue.invokeLater(() -> nativePlayerCanvas.setTime((long) streamTime * 1000, id));
        break;
      case PRECISE_SEEK_FLAG:
        EventQueue.invokeLater(() -> nativePlayerCanvas.setTimePrecise((long) streamTime * 1000, id));
        break;
      case MODERATE_SEEK_FLAG:
        EventQueue.invokeLater(() -> nativePlayerCanvas.setTimeModerate((long) streamTime * 1000, id));
        break;
    }
  }

  private class PlayerStateListenerImpl implements PlayerStateListener {

    @Override
    public void onReady(PlayerStateEvent evt) {
      synchronized (readyLock){
        container.setSize(getImageWidth(), getImageHeight());
        container.setVisible(true);
        playerSetStartTime(0);
        readyLock.notify();
      }
    }

    @Override
    public void onPlaying(PlayerStateEvent evt) { }

    @Override
    public void onPause(PlayerStateEvent evt) { }

    @Override
    public void onStop(PlayerStateEvent evt) { }

    @Override
    public void onStall(PlayerStateEvent evt) { }

    @Override
    public void onFinish(PlayerStateEvent evt) { }

    @Override
    public void onHalt(PlayerStateEvent evt) { }
  }
}
