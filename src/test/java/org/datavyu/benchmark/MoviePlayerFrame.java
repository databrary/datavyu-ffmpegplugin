package org.datavyu.benchmark;

import org.datavyu.plugins.ffmpegplayer.AudioSoundStreamListener;
import org.datavyu.plugins.ffmpegplayer.VideoStreamListenerContainer;

import javax.sound.sampled.AudioFormat;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.io.IOException;

public class MoviePlayerFrame extends Frame implements org.datavyu.benchmark.MoviePlayer {

    private org.datavyu.plugins.ffmpegplayer.MoviePlayer movieStreamProvider = new org.datavyu.plugins.ffmpegplayer.MoviePlayer();

    private VideoStreamListenerContainer videoStreamListenerContainer;

    private ColorSpace colorSpace;

    private AudioFormat audioFormat;

    MoviePlayerFrame(ColorSpace colorSpace, AudioFormat audioFormat) {
        this.colorSpace = colorSpace;
        this.audioFormat = audioFormat;

        videoStreamListenerContainer = new VideoStreamListenerContainer(movieStreamProvider, this, colorSpace);

        movieStreamProvider.addAudioStreamListener(new AudioSoundStreamListener(movieStreamProvider));
        movieStreamProvider.addVideoStreamListener(videoStreamListenerContainer);
        setVisible(true);
    }

    @Override
    public void setScale(float scale) {
        videoStreamListenerContainer.setScale(scale);
        setSize(new Dimension((int) scale*movieStreamProvider.getWidth(),
                (int) scale*movieStreamProvider.getHeight()));
    }

    @Override
    public void setRate(float rate) {
        movieStreamProvider.setSpeed(rate);
    }

    @Override
    public void start() {
        movieStreamProvider.play();
    }

    @Override
    public void stop() {
        movieStreamProvider.stop();
    }

    @Override
    public void openFile(String file, String version) throws IOException {
        movieStreamProvider.open(file, version, colorSpace, audioFormat);
        setSize(new Dimension(movieStreamProvider.getWidth(), movieStreamProvider.getHeight()));
    }

    @Override
    public void closeFile() throws IOException {
        movieStreamProvider.close();
    }

    @Override
    public double getTimeInSeconds() {
        return movieStreamProvider.getCurrentTime();
    }

    @Override
    public void setTimeInSeconds(double timeInSeconds) {
        movieStreamProvider.seek(timeInSeconds);
    }
}
