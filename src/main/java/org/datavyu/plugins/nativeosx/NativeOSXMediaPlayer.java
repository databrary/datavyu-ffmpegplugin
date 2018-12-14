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

  private static Logger logger = LogManager.getLogger(AVFoundationMediaPlayer.class);

  // The time seeked to will be within the bound [time-0, time+0]
  protected static final int PRECISE_SEEK_FLAG = 0;
  // The time seeked to will be within the bound [time-0.5, time+0.5]
  protected static final int MODERATE_SEEK_FLAG = 1;
  // The time seeked to will be within the bound [time-INFINITE, time+INFINITE]
  protected static final int NORMAL_SEEK_FLAG = 2;

  protected static final int INITIAL_VOLUME = 100;

  protected NativeOSXPlayer nativePlayerCanvas;

  protected final int id;
  private static int playerCount = 0;

  private float volume = INITIAL_VOLUME;
  private double duration = -1;
  private boolean seeking = false;
  private float prevRate;

  protected NativeOSXMediaPlayer(URI mediaPath) {
    super(mediaPath);
    this.nativePlayerCanvas = new NativeOSXPlayer(mediaPath);
    this.id = playerCount;
    this.prevRate = 1F;
    sendPlayerStateEvent(eventPlayerUnknown, 0);
  }

  protected static void incPlayerCount() {
    playerCount++;
  }
  protected static void decPlayerCount() {
    playerCount--;
  }

  @Override
  protected void playerPlay() throws MediaException {
    if (getState() == PlayerStateEvent.PlayerState.PAUSED){
      EventQueue.invokeLater(() -> nativePlayerCanvas.setRate(prevRate, id));
    } else {
      EventQueue.invokeLater(() -> nativePlayerCanvas.play(id));
    }

    sendPlayerStateEvent(eventPlayerPlaying, 0);
  }

  @Override
  protected void playerStop() throws MediaException {
    if (isPlaying()) {
      EventQueue.invokeLater(() -> nativePlayerCanvas.stop(id));

      sendPlayerStateEvent(eventPlayerStopped, 0);
    }
  }

  @Override
  protected void playerStepForward() throws MediaException {
    double stepSize = Math.ceil(1000F / nativePlayerCanvas.getFPS(id));
    double time = nativePlayerCanvas.getCurrentTime(id);
    long newTime = (long) Math.min(Math.max(time + stepSize, 0), nativePlayerCanvas.getDuration(id));
    nativePlayerCanvas.setTimePrecise(newTime, id);
  }

  @Override
  protected void playerStepBackward() throws MediaException {
    double stepSize = Math.ceil(1000F / nativePlayerCanvas.getFPS(id));
    double time = nativePlayerCanvas.getCurrentTime(id);
    long newTime = (long) Math.min(Math.max(time - stepSize, 0), nativePlayerCanvas.getDuration(id));
    nativePlayerCanvas.setTimePrecise(newTime, id);
  }

  @Override
  protected void playerPause() throws MediaException {
    // AVFoundation will change the rate to 0 when
    // we need to save the rate before a stop
    if (!isPlaying()){
      playerPlay();
    } else {
      prevRate = playerGetRate();
      playerStop();

      // Override the stopped state
      sendPlayerStateEvent(eventPlayerPaused,0);
    }
  }

  @Override
  protected void playerFinish() throws MediaException { }

  @Override
  protected float playerGetRate() throws MediaException {
    return nativePlayerCanvas.getRate(id);
  }

  @Override
  protected void playerSetRate(float rate) throws MediaException {
    nativePlayerCanvas.setRate(rate, id);
  }


  @Override
  protected void playerSetStartTime(double startTime) throws MediaException {
    playerSeek(startTime, PRECISE_SEEK_FLAG);
  }

  @Override
  protected double playerGetPresentationTime() throws MediaException {
    return (nativePlayerCanvas.getCurrentTime(id) / 1000F);
  }

  @Override
  protected double playerGetFps() throws MediaException {
    return nativePlayerCanvas.getFPS(id);
  }

  @Override
  protected float playerGetVolume() throws MediaException {
    synchronized(this) {
      if (muteEnabled)
        return mutedVolume;
    }
    return volume;
  }

  @Override
  protected synchronized void playerSetVolume(float volume) throws MediaException {
    if (!muteEnabled) {
      if (volume == 0) {
        EventQueue.invokeLater(() -> nativePlayerCanvas.setVolume(0, id));
      } else {
        EventQueue.invokeLater(() -> nativePlayerCanvas.setVolume(volume * 10, id));
        this.volume = mutedVolume = volume;
      }
    } else {
      mutedVolume = volume;
    }
  }

  @Override
  protected double playerGetDuration() throws MediaException {
    if (duration <= -1) {
      return duration = nativePlayerCanvas.getDuration(id) / 1000F;
    }
    return duration;
  }

  @Override
  protected void playerSeek(double streamTime) throws MediaException {
    if (!seeking) {
      seeking = true;
      EventQueue.invokeLater(() -> {
        logger.info("Seeking to position: " + streamTime + " Is playing: " + isPlaying());
        boolean wasPlaying = isPlaying();
        prevRate = playerGetRate();
        if (isPlaying()) {
          nativePlayerCanvas.stop(id);
        }
        if (!wasPlaying || prevRate >= 0 && prevRate <= 8) {
          playerSeek(streamTime, PRECISE_SEEK_FLAG);
        } else if(prevRate < 0 && prevRate > - 8) {
          playerSeek(streamTime, MODERATE_SEEK_FLAG);
        } else {
          playerSeek(streamTime, NORMAL_SEEK_FLAG);
        }
        if (wasPlaying) {
          playerSetRate(prevRate);
        }
        nativePlayerCanvas.repaint();
        seeking = false;
      });
    }
  }

  @Override
  protected void playerDispose() {
    decPlayerCount();
  }

  @Override
  public int getImageWidth() {
    return (int) nativePlayerCanvas.getMovieWidth(id);
  }

  @Override
  public int getImageHeight() {
    return (int) nativePlayerCanvas.getMovieHeight(id);
  }

  protected boolean isPlaying() {
    return !nativePlayerCanvas.isPlaying(id); // the native os plugin return false when is playing
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
