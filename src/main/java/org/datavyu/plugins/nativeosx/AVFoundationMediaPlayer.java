package org.datavyu.plugins.nativeosx;


import org.datavyu.plugins.MediaException;
import org.datavyu.plugins.PlayerStateEvent;
import org.datavyu.plugins.PlayerStateListener;

import java.awt.*;
import java.net.URI;

public class AVFoundationMediaPlayer extends NativeOSXMediaPlayer {

  private Container container;

  public AVFoundationMediaPlayer(URI mediaPath, Container container) {
    super(mediaPath);
    this.container = container;
  }

  @Override
  public void init() {
    addMediaPlayerStateListener(new PlayerStateListenerImpl());
    initNative();

    container.addNotify();
    container.add(nativePlayerCanvas, BorderLayout.CENTER);

    incPlayerCount();
    sendPlayerStateEvent(eventPlayerReady, 0);
  }

  @Override
  protected void avFoundationPlayerSeek(double streamTime, int flags) throws MediaException {
    switch (flags){
      case NORMAL_SEEK:
        nativePlayerCanvas.setTime((long) streamTime * 1000, id);
        break;
      case PRECISE_SEEK:
        nativePlayerCanvas.setTimePrecise((long) streamTime * 1000, id);
        break;
      case MODERATE_SEEK:
        nativePlayerCanvas.setTimeModerate((long) streamTime * 1000, id);
        break;
    }
  }

  private class PlayerStateListenerImpl implements PlayerStateListener {

    @Override
    public void onReady(PlayerStateEvent evt) {
      container.setSize(getImageWidth(), getImageHeight());
      container.setVisible(true);
      playerSetStartTime(0);
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
