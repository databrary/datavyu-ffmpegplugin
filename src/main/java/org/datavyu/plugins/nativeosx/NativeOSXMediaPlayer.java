package org.datavyu.plugins.nativeosx;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.datavyu.plugins.MediaException;
import org.datavyu.plugins.ffmpeg.DatavyuMediaPlayer;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.awt.*;
import java.net.URI;

abstract class NativeOSXMediaPlayer extends DatavyuMediaPlayer {

  private static Logger logger = LogManager.getLogger(AVFoundationMediaPlayer.class);

  protected enum SeekFlags{
    PRECISE_SEEK,
    MODERATE_SEEK,
    NORMAL_SEEK
  }

  private static final int INITIAL_VOLUME = 100;

  protected NativeOSXPlayer nativePlayerCanvas;

  private Container container;

  protected final int id;
  private static int playerCount = 0;

  private float volume = INITIAL_VOLUME;
  private double duration = -1;
  private boolean seeking = false;
  private float prevRate;

  protected NativeOSXMediaPlayer(URI mediaPath, Container container) {
    super(mediaPath);
    this.nativePlayerCanvas = new NativeOSXPlayer(mediaPath);
    this.id = playerCount;
    this.container = container;
  }

  public static void incPlayerCount() {
    playerCount++;
  }

  public static void decPlayerCount() {
    playerCount--;
  }

  @Override
  public void init() {
    container.addNotify();
    container.add(nativePlayerCanvas, BorderLayout.CENTER);
    container.setSize(getImageWidth(), getImageHeight());
    container.setVisible(true);
    incPlayerCount();
    sendPlayerStateEvent(eventPlayerReady, 0);
  }

  @Override
  protected void playerPlay() throws MediaException {
    EventQueue.invokeLater(() -> nativePlayerCanvas.play(id));
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
    //TODO(Reda) Implement stepping
  }

  @Override
  protected void playerStepBackward() throws MediaException {
    //TODO(Reda) Implement stepping
  }

  @Override
  protected void playerPause() throws MediaException {
    // AVFoundation will change the rate to 0 when
    // we need to save the rate before a stop
    if (!isPlaying()){
      playerPlay();
      playerSetRate(prevRate);
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
    avFoundatioPlayerSeek(startTime, SeekFlags.PRECISE_SEEK);
  }

  @Override
  protected double playerGetPresentationTime() throws MediaException {
    return nativePlayerCanvas.getCurrentTime(id) / 1000L;
  }

  @Override
  protected double playerGetFps() throws MediaException {
    return nativePlayerCanvas.getFPS(id);
  }

  @Override
  protected float playerGetVolume() throws MediaException {
    return volume;
  }

  @Override
  protected void playerSetVolume(float volume) throws MediaException {
    EventQueue.invokeLater(() -> nativePlayerCanvas.setVolume(volume, id));
  }

  @Override
  protected double playerGetDuration() throws MediaException {
    if (duration == -1) {
      return nativePlayerCanvas.getDuration(id) / 1000F;
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
          avFoundatioPlayerSeek(streamTime, SeekFlags.PRECISE_SEEK);
        } else if(prevRate < 0 && prevRate > - 8) {
          avFoundatioPlayerSeek(streamTime, SeekFlags.MODERATE_SEEK);
        } else {
          avFoundatioPlayerSeek(streamTime, SeekFlags.NORMAL_SEEK);
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
    return ! nativePlayerCanvas.isPlaying(id); // the native os plugin return false when is playing
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

  protected abstract void avFoundatioPlayerSeek(double streamTime, SeekFlags flag) throws MediaException;
}
