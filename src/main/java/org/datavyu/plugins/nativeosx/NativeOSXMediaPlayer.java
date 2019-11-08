package org.datavyu.plugins.nativeosx;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.datavyu.plugins.MediaException;
import org.datavyu.plugins.DatavyuMediaPlayer;
import org.datavyu.plugins.PlayerStateEvent;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.awt.*;
import java.net.URI;

abstract class NativeOSXMediaPlayer extends DatavyuMediaPlayer {

  private static Logger logger = LogManager.getLogger(NativeOSXMediaPlayer.class);

  // The time seeked to will be within the bound [time-0, time+0]
  protected static final int PRECISE_SEEK_FLAG = 0;
  // The time seeked to will be within the bound [time-0.5, time+0.5]
  protected static final int MODERATE_SEEK_FLAG = 1;
  // The time seeked to will be within the bound [time-INFINITE, time+INFINITE]
  protected static final int NORMAL_SEEK_FLAG = 2;

  protected static final float INITIAL_VOLUME = 1F;

  protected double startTime = 0.0;

  protected NativeOSXPlayer mediaPlayer;

  protected final int id;
  private static int playerCount = 0;
  protected double fps = -1;
  protected double duration = -1;
  private float volume = INITIAL_VOLUME;
  private boolean seeking = false;
  private float prevRate = 1F;

  protected NativeOSXMediaPlayer(URI mediaPath) {
    super(mediaPath);
    this.mediaPlayer = new NativeOSXPlayer(mediaPath);
    this.id = playerCount;
  }

  protected static void incPlayerCount() {
    playerCount++;
  }

  protected static void decPlayerCount() {
    playerCount--;
  }

  @Override
  protected void playerPlay() throws MediaException {
    logger.debug("Start Player " + this.id);
    EventQueue.invokeLater(
        () -> {
          if (getState() == PlayerStateEvent.PlayerState.READY
                  || getState() == PlayerStateEvent.PlayerState.STOPPED
                  || getState() == PlayerStateEvent.PlayerState.PAUSED) {
            mediaPlayer.setRate(prevRate, id);
            sendPlayerStateEvent(eventPlayerPlaying, 0);
          }
        });
  }

  @Override
  protected void playerStop() throws MediaException {
    EventQueue.invokeLater(
        () -> {
          if (getState() != PlayerStateEvent.PlayerState.STOPPED
              && getState() != PlayerStateEvent.PlayerState.READY) {
            prevRate = 1F;
            mediaPlayer.stop(id);
            sendPlayerStateEvent(eventPlayerStopped, 0);
          }
        });
  }

  /**
   * Stops the player and save the rate, this method is used when Pausing the player. Note:
   * AVFoundation player set the rate to 0 when stopped, and there is no Pause feature.
   *
   * @param rate
   */
  private void playerStop(final float rate) {
    EventQueue.invokeLater(
        () -> {
          prevRate = rate == 0 ? 1F : rate;
          mediaPlayer.stop(id);
          logger.debug("Player Pausing at " + rate + "X");
          sendPlayerStateEvent(eventPlayerPaused, 0);
        });
  }

  @Override
  protected void playerStepForward() throws MediaException {
    double stepSize = Math.ceil(1000F / mediaPlayer.getFPS(id));
    double time = mediaPlayer.getCurrentTime(id);
    long newTime = (long) Math.min(Math.max(time + stepSize, 0), mediaPlayer.getDuration(id));
    logger.info("Stepping Forward from " + time + " sec, to " + newTime + " sec");
    mediaPlayer.setTimePrecise(newTime, id);
  }

  @Override
  protected void playerStepBackward() throws MediaException {
    double stepSize = Math.ceil(1000F / mediaPlayer.getFPS(id));
    double time = mediaPlayer.getCurrentTime(id);
    long newTime = (long) Math.min(Math.max(time - stepSize, 0), mediaPlayer.getDuration(id));
    logger.info(
        "Stepping Backward from " + (time / 1000.0) + " sec, to " + (newTime / 1000.0) + " sec");
    mediaPlayer.setTimePrecise(newTime, id);
  }

  @Override
  protected void playerPause() throws MediaException {
    if (getState() != PlayerStateEvent.PlayerState.PAUSED
        && getState() != PlayerStateEvent.PlayerState.READY) {
      playerStop(mediaPlayer.getRate(id));
    }
  }

  @Override
  protected void playerFinish() throws MediaException {}

  @Override
  protected float playerGetRate() throws MediaException {
    return mediaPlayer.getRate(id);
  }

  @Override
  protected void playerSetRate(float rate) throws MediaException {
    logger.info("Setting Rate to : " + rate + "X");
    EventQueue.invokeLater(
        () -> {
          if (rate != 0) {
            prevRate = rate;
            playerPlay();
          } else {
            playerStop();
          }
        });
  }

  @Override
  protected double playerGetStartTime() throws MediaException {
    return startTime;
  }

  @Override
  protected void playerSetStartTime(double startTime) throws MediaException {
    this.startTime = startTime;
    playerSeek((long) (startTime * 1000), PRECISE_SEEK_FLAG);
  }

  @Override
  protected double playerGetPresentationTime() throws MediaException {
    return (mediaPlayer.getCurrentTime(id) / 1000.0);
  }

  @Override
  protected double playerGetFps() throws MediaException {
    return mediaPlayer.getFPS(id);
  }

  @Override
  protected float playerGetVolume() throws MediaException {
    synchronized (this) {
      if (muteEnabled) return mutedVolume;
    }
    return volume;
  }

  @Override
  protected synchronized void playerSetVolume(float volume) throws MediaException {
    logger.info("Setting Volume to " + volume);
    if (!muteEnabled) {
      EventQueue.invokeLater(() -> mediaPlayer.setVolume(volume, id));
      this.volume = mutedVolume = volume;
    } else {
      mutedVolume = volume;
    }
  }

  @Override
  protected double playerGetDuration() throws MediaException {
    return (mediaPlayer.getDuration(id) / 1000.0);
  }

  @Override
  protected void playerSeek(double streamTime) throws MediaException {
    if (!seeking) {
      seeking = true;
      EventQueue.invokeLater(
          () -> {
            boolean wasPlaying = isPlaying();
            if (wasPlaying) {
              playerPause();
            }
            if (!wasPlaying || prevRate >= 0 && prevRate <= 8) {
              logger.debug("Precise seek to position: " + streamTime);
              playerSeek(streamTime * 1000, PRECISE_SEEK_FLAG);
            } else if (prevRate < 0 && prevRate > -8) {
              logger.debug("Moderate seek to position: " + streamTime);
              playerSeek(streamTime * 1000, MODERATE_SEEK_FLAG);
            } else {
              logger.debug("Seek to position: " + streamTime);
              playerSeek(streamTime * 1000, NORMAL_SEEK_FLAG);
            }
            if (wasPlaying) {
              playerPlay();
            }
            mediaPlayer.repaint();
            seeking = false;
          });
    }
  }

  @Override
  protected void playerDispose() {
    logger.info("Disposing the player");
    // Nothing to do here
    // IMPORTANT: can't release player resources (Bug!)
    // and to keep the correct player id can't decrement the
    // player count
    // FIXME properly release player instance resources
  }

  @Override
  protected void playerShowSDLWindow() {
    throw new NotImplementedException();
  }

  @Override
  protected void playerHideSDLWindow() {
    throw new NotImplementedException();
  }

  @Override
  protected boolean playerIsSeekPlaybackEnabled() {
    return false;
  }

  @Override
  protected boolean playerRateIsSupported(final float rate) {
    return 0F <= rate && rate <= 8F;
  }

  @Override
  public int playerGetImageWidth() {
    return (int) mediaPlayer.getMovieWidth(id);
  }

  @Override
  public int playerGetImageHeight() {
    return (int) mediaPlayer.getMovieHeight(id);
  }

  protected boolean isPlaying() {
    return !mediaPlayer.isPlaying(id); // the native os plugin return false when is playing
  }

  @Override
  protected float playerGetBalance() throws MediaException {
    throw new NotImplementedException();
  }

  @Override
  protected void playerSetBalance(float balance) throws MediaException {
    throw new NotImplementedException();
  }

  @Override
  protected long playerGetAudioSyncDelay() throws MediaException {
    throw new NotImplementedException();
  }

  @Override
  protected void playerSetAudioSyncDelay(long delay) throws MediaException {
    throw new NotImplementedException();
  }

  protected abstract void playerSeek(double streamTime, int flags) throws MediaException;
}
