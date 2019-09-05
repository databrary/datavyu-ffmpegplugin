package org.datavyu.plugins.ffmpeg;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.datavyu.plugins.MediaPlayer;
import org.datavyu.plugins.PlayerStateEvent;
import org.datavyu.plugins.PlayerStateListener;

public class MediaPlayerSync {
  private static final Logger logger = LogManager.getFormatterLogger(MediaPlayerSync.class);

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
        // Happens if someone interrupts your thread
      }
    }
  }

  public MediaPlayer getMediaPlayer() {
    return mediaPlayer;
  }

  public static MediaPlayerSync createMediaPlayerSync(MediaPlayer mediaPlayer) {
    return new MediaPlayerSync(mediaPlayer);
  }
}
