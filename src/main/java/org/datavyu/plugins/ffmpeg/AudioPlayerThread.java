package org.datavyu.plugins.ffmpeg;

import javax.sound.sampled.*;
import java.nio.ByteBuffer;

public class AudioPlayerThread extends Thread {
    private MediaPlayerData mediaPlayerData;
    private SourceDataLine soundLine = null;
    //private FloatControl gainControl = null; // TODO: Check if we need to hock this up
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
        //gainControl = (FloatControl) soundLine.getControl(FloatControl.Type.MASTER_GAIN);
        data = new byte[bufferSize];
    }

    @Override
    public void run() {
        while (!stopped) {
            ByteBuffer buffer = mediaPlayerData.getAudioData();
            buffer.get(data, 0, data.length); // TODO(fraudies): See if we can remove this copy here
            // Write blocks when data can't be consumed fast enough
            soundLine.write(data, 0, data.length);
        }
    }

    public void terminate() {
        stopped = false;
        soundLine.drain();
        soundLine.stop();
        soundLine.close();
    }
}
