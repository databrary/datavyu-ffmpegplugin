package org.datavyu.plugins.ffmpeg;

import org.datavyu.plugins.DatavyuMediaPlayer;
import org.datavyu.plugins.MediaException;
import org.datavyu.plugins.PlayerStateEvent;

import org.datavyu.plugins.PlayerStateListener;
import java.net.URI;

/** Uses ffmpeg to decode and transcode (optional) image and audio data */
abstract class FfmpegMediaPlayer extends DatavyuMediaPlayer {

  private PlayerStateListener stateListener;

  protected final Object initLock = new Object();

  protected double startTime = 0.0;

  /**
   * Create an ffmpeg media player instance
   *
   * @param mediaPath The path to the media
   */
  protected FfmpegMediaPlayer(URI mediaPath) {
    super(mediaPath);
    stateListener = new FfmpegPlayerStateListener();
    this.addMediaPlayerStateListener(stateListener);
  }

  @Override
  protected void playerSeekToFrame(int frameNumber) throws MediaException {
    playerSeek(frameNumber);
  }

  @Override
  protected double playerGetStartTime() throws MediaException {
    return startTime;
  }

  @Override
  protected void playerSetStartTime(double startTime) throws MediaException {
    this.startTime = startTime;
    playerSeek(startTime);
  }

  @Override
  protected boolean playerIsSeekPlaybackEnabled() {
    return playBackRate < 0F || playBackRate > 32F;
  }

  @Override
  protected boolean playerRateIsSupported(final float rate) {
    return 0F <= rate && rate <= 4F;
  }

  class FfmpegPlayerStateListener implements PlayerStateListener {

    @Override
    public void onReady(PlayerStateEvent evt) {
      synchronized (initLock) {
        try {
          // wait for the initialization of the Java Side
          // This will make sure to have a correct PTS
          initLock.wait(100);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
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
  }
}
