package org.datavyu.plugins.ffmpeg;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.datavyu.plugins.MediaPlayer;
import org.datavyu.plugins.PlayerStateEvent;
import org.datavyu.plugins.PlayerStateListener;

public class MediaPlayerSync {
  private static final Logger logger = LogManager.getFormatterLogger(MediaPlayerSync.class);
  private static final int COUNTDOWNLATCH_COUNT = 1;
  static {
    Configurator.setRootLevel(Level.DEBUG);
  }

  private MediaPlayer mediaPlayer;
  private final Object readyLock = new Object();
  private final PlayerStateListener playerStateListener =
      new PlayerStateListener() {
        @Override
        public void onReady(PlayerStateEvent evt) {
          synchronized (readyLock) {
            readyLock.notify();
            logger.debug("Player is Ready");
          }
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
      };

  MediaPlayerSync(MediaPlayer mediaPlayer) {
    this.mediaPlayer = mediaPlayer;
    this.mediaPlayer.addMediaPlayerStateListener(playerStateListener);
    waitForInit();
  }

  void waitForInit() {
    synchronized (readyLock) {
      try {
        logger.debug("Init Media Player");
        mediaPlayer.init();

        logger.debug("Waiting for Ready state");
        readyLock.wait();
      } catch (InterruptedException e) {
        logger.error(e.getMessage());
      }
    }
  }

  /**
   * Play video for a certain duration and return time elapsed for that duration.
   * @param duration in seconds
   * @return
   */
  public double playFor(final double duration) {
    CountDownLatch startSignal = new CountDownLatch(COUNTDOWNLATCH_COUNT);
    CountDownLatch endSignal = new CountDownLatch(COUNTDOWNLATCH_COUNT);
    Worker playWorker = new Worker(startSignal, endSignal, duration);
    new Thread(playWorker).start();
    mediaPlayer.play();
    startSignal.countDown();
    try {
      endSignal.await();
    } catch (InterruptedException e) {
      logger.error(e.getMessage());
    }
    return mediaPlayer.getPresentationTime();
  }

  public MediaPlayer getMediaPlayer() {
    return mediaPlayer;
  }

  public static MediaPlayerSync createMediaPlayerSync(MediaPlayer mediaPlayer) {
    return new MediaPlayerSync(mediaPlayer);
  }

  class Worker implements Runnable {
    private final CountDownLatch startSignal;
    private final CountDownLatch endSignal;
    private final double duration;

    Worker(CountDownLatch startSignal, CountDownLatch endSignal,double duration) {
      this.startSignal = startSignal;
      this.endSignal = endSignal;
      this.duration = duration;
    }

    public void run() {
      try {
        startSignal.await();
        long startTime = System.currentTimeMillis();
        while ((System.currentTimeMillis() - startTime) / 1000 < duration) {}
        mediaPlayer.stop();
        endSignal.countDown();
      } catch (InterruptedException ex) {
        logger.error(ex.getMessage());
      }
    }
  }

}
