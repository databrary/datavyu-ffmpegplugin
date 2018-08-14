package org.datavyu.plugins.ffmpeg;

import javax.sound.sampled.*;

public class AudioPlayerThread extends Thread {
    private final static AudioFormat MONO_FORMAT = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,
            44100,
            16,
            1,
            2,
            44100,
            false);

    private final static AudioFormat STEREO_FORMAT = new AudioFormat(
            AudioFormat.Encoding.PCM_UNSIGNED,
            44100,
            8,
            2,
            2,
            44100,
            false);
    private FloatControl volumeControl;
    private BooleanControl muteControl;

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
    // TODO(fraudies): Add volume control
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
        muteControl = (BooleanControl)soundLine.getControl(BooleanControl.Type.MUTE);

        data = new byte[bufferSize];
        soundLine.start();
    }

    public void setVolume(final float newVolume){
        // Adjust the volume on the output line.
        if (soundLine.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            // Convert from linear to decibel
            float db = (float) (20.0f * Math.log10(newVolume));
            volumeControl.setValue(db);
        }
    }

    public float getVolume() {
        if (soundLine.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            // Convert from decibel to linear
            float linear = (float) Math.pow(10.0F, volumeControl.getValue()/20.0F);
            return linear;
        }
        return 0;
    }

    public void setMute(final boolean newMute){
        if(soundLine.isControlSupported(BooleanControl.Type.MUTE)){
            muteControl.setValue(newMute);
        }
    }

    public boolean isMute(){ return muteControl.getValue(); }

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
        soundLine.drain();
        soundLine.stop();
        soundLine.close();
    }
}
