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
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class AVFoundationMediaPlayer extends NativeOSXMediaPlayer {

  private static Logger logger = LogManager.getLogger(AVFoundationMediaPlayer.class);

  private Container container;
  private final Object readyLock = new Object();

  private PlayerStateListener stateListener;

  private long startInitTime;
  private long endInitTime;

  public AVFoundationMediaPlayer(URI mediaPath, Container container) {
    super(mediaPath);
    stateListener = new PlayerStateListenerImpl();
    this.addMediaPlayerStateListener(stateListener);
    this.container = container;
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
    container.setBackground(Color.BLACK);

    Runnable waitForReady =
        () -> {
          //TODO(Reda): Timeout this loop and throw an exception
          while (playerGetFps() <= 0 && Float.compare((float) playerGetDuration(),0) < 0) {
            try {
              Thread.sleep(100);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
          logger.debug("Sending Ready State");
          sendPlayerStateEvent(eventPlayerReady, 0);
        };

    new Thread(waitForReady).start();

    synchronized (readyLock) {
      try {
        logger.debug("Waiting for Ready state");
        readyLock.wait();
        // Always increment the player count after creating an instance
        // Important: don't decrement the player count when releasing resources
        incPlayerCount();
        endInitTime = System.currentTimeMillis();
        logger.debug("Time to initialize : " + (endInitTime - startInitTime) + " ms");
      } catch (InterruptedException e) {
        logger.error(e.getMessage());
      }
    }
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

  @Override
  protected int playerGetWindowWidth() throws MediaException {
    throw new NotImplementedException();
  }

  @Override
  protected int playerGetWindowHeight() throws MediaException {
    throw new NotImplementedException();
  }

  @Override
  protected void playerSetWindowSize(int width, int height) throws MediaException {
    throw new NotImplementedException();
  }

  private class PlayerStateListenerImpl implements PlayerStateListener {

    @Override
    public void onReady(PlayerStateEvent evt) {
      synchronized (readyLock) {
        container.setSize(getImageWidth(), getImageHeight());
        container.setVisible(true);
        startTime = getPresentationTime();
        readyLock.notify();
        logger.debug("Player is Ready - FPS " + playerGetFps() + " - Duration " + playerGetDuration());
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
