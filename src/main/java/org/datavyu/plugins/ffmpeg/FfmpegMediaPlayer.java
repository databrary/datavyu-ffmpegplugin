package org.datavyu.plugins.ffmpeg;

import org.datavyu.plugins.DatavyuMediaPlayer;
import org.datavyu.plugins.MediaException;
import org.datavyu.plugins.PlayerStateEvent;
import org.datavyu.plugins.PlayerStateEvent.PlayerState;
import org.datavyu.plugins.PlayerStateListener;
import java.net.URI;

/** Uses ffmpeg to decode and transcode (optional) image and audio data */
abstract class FfmpegMediaPlayer extends DatavyuMediaPlayer {

  protected static final int SEEK_ACCURATE_FLAG = 0x01;
  protected static final int SEEK_FAST_FLAG = 0x10;

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
  protected void playerSeek(double streamTime) throws MediaException {
    // In most cases seek accurate, with the exception of large backward playback rates
    int seek_flag = (!isStartTimeUpdated && getRate() < -1) ? SEEK_FAST_FLAG : SEEK_ACCURATE_FLAG;
    // Mute player when seeking and not playing
    if (getState() != PlayerState.PLAYING) {
      setMute(true);
    }
    playerSeek(streamTime, seek_flag);
    setMute(false);
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
    playerSeek(startTime, SEEK_ACCURATE_FLAG);
  }

  @Override
  protected boolean playerIsSeekPlaybackEnabled() {
    return playBackRate < 0F ;
  }

  protected abstract void playerSeek(double streamTime, int seek_flag) throws MediaException;

  protected abstract void playerSeek(int frameNumber) throws MediaException;

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
