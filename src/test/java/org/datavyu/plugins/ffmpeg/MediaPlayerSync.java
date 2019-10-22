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
  private static final int LOCK_TIMOUT = 100; // ms
  private static final int COUNTDOWNLATCH_COUNT = 1;
  private CountDownLatch startSignal;
  private CountDownLatch endSignal;
  static {
    Configurator.setRootLevel(Level.DEBUG);
  }

  private MediaPlayer mediaPlayer;
  private final Object readyLock = new Object();
  private final Object playingLock = new Object();
  private final Object stoppedLock = new Object();
  private final Object pausedLock = new Object();
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
        public void onPlaying(PlayerStateEvent evt) {
          synchronized (playingLock) {
            playingLock.notify();
            if (startSignal != null) {
              logger.trace("Start Signal Count down");
              startSignal.countDown();
            }
            logger.debug("Player is Playing");
          }
        }

        @Override
        public void onPause(PlayerStateEvent evt) {
          synchronized (pausedLock) {
            pausedLock.notify();
            logger.debug("Player is Paused");
          }
        }

        @Override
        public void onStop(PlayerStateEvent evt) {
          synchronized (stoppedLock) {
            stoppedLock.notify();
            if (endSignal != null) {
              logger.trace("End Signal Count down");
              endSignal.countDown();
            }
            logger.debug("Player is Stopped");
          }
        }

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

        logger.trace("Waiting for Ready state");
        readyLock.wait();
      } catch (InterruptedException e) {
        logger.error(e.getMessage());
      }
    }
  }

  void waitForPlaying() {
    synchronized (playingLock) {
      try {
        logger.debug("Play Media Player");
        mediaPlayer.play();

        logger.trace("Waiting for Playing state");
        playingLock.wait(LOCK_TIMOUT);
      } catch (InterruptedException e) {
        logger.error(e.getMessage());
      }
    }
  }

  void waitForStopped() {
    synchronized (stoppedLock) {
      try {
        logger.debug("Stop Media Player");
        mediaPlayer.stop();

        logger.trace("Waiting for Stopped state");
        stoppedLock.wait(LOCK_TIMOUT);
      } catch (InterruptedException e) {
        logger.error(e.getMessage());
      }
    }
  }


  void waitForPaused() {
    synchronized (pausedLock) {
      try {
        logger.debug("Pause Media Player");
        mediaPlayer.pause();

        logger.trace("Waiting for Paused state");
        pausedLock.wait(LOCK_TIMOUT);
      } catch (InterruptedException e) {
        logger.error(e.getMessage());
      }
    }
  }

  /**
   * Play video for a certain duration and return elapsed time for the playback.
   * @param start time in seconds
   * @param end time in seconds
   * @return elapsed time
   */
  public double playTo(final double start, final double end) {
    startSignal = new CountDownLatch(COUNTDOWNLATCH_COUNT);
    endSignal = new CountDownLatch(COUNTDOWNLATCH_COUNT);

    Worker playWorker = new Worker(end);
    new Thread(playWorker).start();

    mediaPlayer.seek(start);

    try {
      Thread.sleep(200);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    waitForPlaying();
    try {
      endSignal.await(5, TimeUnit.MINUTES);
    } catch (InterruptedException e) {
      logger.error(e.getMessage());
    }

    return playWorker.getElapsedTime();
  }

  public MediaPlayer getMediaPlayer() {
    return mediaPlayer;
  }

  public static MediaPlayerSync createMediaPlayerSync(MediaPlayer mediaPlayer) {
    return new MediaPlayerSync(mediaPlayer);
  }

  public void disposeMediaPlayerSync() {
    if (mediaPlayer != null) {
      mediaPlayer.dispose();
    }

    if (startSignal != null) {
      startSignal = null;
    }

    if (endSignal != null) {
      endSignal = null;
    }
  }

  class Worker implements Runnable {
    private double elapsedTime = 0;
    private final double stop;
    private boolean terminate = false;

    Worker(final double stop) {
      this.stop = stop;
    }

    public void run() {
      try {
        logger.trace("Start Signal is waiting");
        startSignal.await();
        long startTime = System.currentTimeMillis();
        logger.trace("Play Worker started at " + startTime + " ms");
        logger.trace("Play Worker is waiting to reach " + stop + " sec");
        //TODO(Reda): Implement markers to wait for event in the native
        while (!terminate && mediaPlayer.getPresentationTime() < stop) {}
        elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
        waitForStopped();
        logger.trace("Play Worker is done elapsed time " + elapsedTime + " sec");
      } catch (InterruptedException ex) {
        logger.error(ex.getMessage());
      }
    }

    void terminate() {
      terminate = true;
    }

    double getElapsedTime() {
      return elapsedTime;
    }
  }

}
