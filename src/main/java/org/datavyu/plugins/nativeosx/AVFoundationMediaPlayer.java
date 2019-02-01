package org.datavyu.plugins.nativeosx;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.datavyu.plugins.MediaError;
import org.datavyu.plugins.MediaException;
import org.datavyu.plugins.PlayerStateEvent;
import org.datavyu.plugins.PlayerStateListener;

import java.awt.*;
import java.io.File;
import java.net.URI;

public class AVFoundationMediaPlayer extends NativeOSXMediaPlayer {

  private static Logger logger = LogManager.getLogger(AVFoundationMediaPlayer.class);

  private Container container;
  private final Object readyLock = new Object();

  private long startInitTime;
  private long endInitTime;

  public AVFoundationMediaPlayer(URI mediaPath, Container container) {
    super(mediaPath);
    this.addMediaPlayerStateListener(new AVFoundationMediaPlayer.PlayerStateListenerImpl());
    this.container = container;
    sendPlayerStateEvent(eventPlayerUnknown, 0);
  }

  @Override
  public void init() {
    logger.info("Initializing " + this.getClass().getName());

    startInitTime = System.currentTimeMillis();

    initNative();

    // Test if the file exists
    File mediaFile = new File(mediaPath);
    if (!mediaFile.exists()) {
      throwMediaErrorException(MediaError.ERROR_MEDIA_INVALID.code(), null);
    }

    container.addNotify();
    container.add(mediaPlayer, BorderLayout.CENTER);

    incPlayerCount();
    Runnable waitForReady =
        () -> {
          while (playerGetFps() <= 0) {
            try {
              Thread.sleep(100);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
            sendPlayerStateEvent(eventPlayerReady, 0);
        };

    new Thread(waitForReady).start();

    synchronized (readyLock) {
      try {
        logger.info("Waiting for Ready state");
        readyLock.wait();
      } catch (InterruptedException e) {
        logger.error(e.getMessage());
      }
    }

    endInitTime = System.currentTimeMillis();
    logger.debug("Time to initialize : " + (endInitTime - startInitTime) + " ms");
  }

  @Override
  protected void playerSeek(double streamTime, int flags) throws MediaException {
    switch (flags) {
      case NORMAL_SEEK_FLAG:
        EventQueue.invokeLater(() -> mediaPlayer.setTime((long) streamTime, id));
        break;
      case PRECISE_SEEK_FLAG:
        EventQueue.invokeLater(() -> mediaPlayer.setTimePrecise((long) streamTime, id));
        break;
      case MODERATE_SEEK_FLAG:
        EventQueue.invokeLater(() -> mediaPlayer.setTimeModerate((long) streamTime, id));
        break;
    }
  }

  private class PlayerStateListenerImpl implements PlayerStateListener {

    @Override
    public void onReady(PlayerStateEvent evt) {
      synchronized (readyLock) {
        container.setSize(getImageWidth(), getImageHeight());
        container.setVisible(true);
        readyLock.notify();
        logger.info("Player is Ready");
      }
    }

    @Override
    public void onPlaying(PlayerStateEvent evt) {
      logger.info("Player is Playing");
    }

    @Override
    public void onPause(PlayerStateEvent evt) {
      logger.info("Player is Paused");
    }

    @Override
    public void onStop(PlayerStateEvent evt) {
      logger.info("Player is Stopped");
    }

    @Override
    public void onStall(PlayerStateEvent evt) {}

    @Override
    public void onFinish(PlayerStateEvent evt) {}

    @Override
    public void onHalt(PlayerStateEvent evt) {}
  }
}
