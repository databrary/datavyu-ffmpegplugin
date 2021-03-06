package org.datavyu.plugins.ffmpeg;

import org.datavyu.plugins.MediaPlayerData;

import javax.sound.sampled.*;

class AudioPlayerThread extends Thread {
  private static final AudioFormat MONO_FORMAT =
      new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 1, 2, 44100, false);

  private static final AudioFormat STEREO_FORMAT =
      new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, 44100, 8, 2, 2, 44100, false);
  private FloatControl volumeControl;
  private BooleanControl muteControl;
  private boolean isInit = false;

  /**
   * Get new audio format for mono playback.
   *
   * @return AudioFormat for mono playback.
   */
  public static AudioFormat getMonoFormat() {
    return MONO_FORMAT;
  }

  /**
   * Get new audio format for stereo playback.
   *
   * @return AudioFormat for stereo playback.
   */
  public static AudioFormat getStereoFormat() {
    return STEREO_FORMAT;
  }

  private MediaPlayerData mediaPlayerData;
  private SourceDataLine soundLine = null;
  private volatile boolean stopped = false;
  private byte[] data;

  AudioPlayerThread(MediaPlayerData mediaPlayerData) {
    this.mediaPlayerData = mediaPlayerData;
    setName("FFmpeg audio player thread");
    setDaemon(false);
  }

  public void init(AudioFormat audioFormat, int bufferSize) throws LineUnavailableException {
    // Get the data line
    DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
    soundLine = (SourceDataLine) AudioSystem.getLine(info);
    soundLine.open(audioFormat);

    volumeControl = (FloatControl) soundLine.getControl(FloatControl.Type.MASTER_GAIN);
    muteControl = (BooleanControl) soundLine.getControl(BooleanControl.Type.MUTE);

    data = new byte[bufferSize];
    soundLine.start();

    isInit = true;
  }

  public boolean isInit() {
    return isInit;
  }

  public void setVolume(final float newVolume) {
    // Adjust the volume on the output line.
    if (volumeControl != null) {
      volumeControl.setValue((float) (20.0f * Math.log10(newVolume)));
    }
  }

  public float getVolume() {
    if (volumeControl != null) {
      // Convert from decibel to linear
      return (float) Math.pow(10.0F, volumeControl.getValue() / 20.0F);
    }
    return 0;
  }

  public void setMute(final boolean newMute) {
    if (muteControl != null) {
      muteControl.setValue(newMute);
    }
  }

  public boolean isMute() {
    return muteControl != null && muteControl.getValue();
  }

  @Override
  public void run() {
    while (!stopped) {
      mediaPlayerData.updateAudioData(data);
      // Write blocks when data can't be consumed fast enough
      // Since we handle the pause natively we don't have to buffer non-written bytes here
      soundLine.write(data, 0, data.length);
    }
  }

  public void terminate() {
    stopped = true;
    isInit = false;
    soundLine.drain();
    soundLine.stop();
    soundLine.close();
  }
}
