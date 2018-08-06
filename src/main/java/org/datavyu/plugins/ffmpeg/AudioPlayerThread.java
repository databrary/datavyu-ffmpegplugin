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
        data = new byte[bufferSize];
        soundLine.start();
    }

    @Override
    public void run() {
        while (!stopped) {
            ByteBuffer buffer = mediaPlayerData.getAudioData();
            buffer.get(data, 0, data.length); // TODO(fraudies): See if we can remove this copy here
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
