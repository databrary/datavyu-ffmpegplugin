package org.datavyu.benchmark;

import org.datavyu.plugins.ffmpegplayer.AudioSoundStreamListener;
import org.datavyu.plugins.ffmpegplayer.MovieStreamProvider;
import org.datavyu.plugins.ffmpegplayer.VideoStreamListenerContainer;

import javax.sound.sampled.AudioFormat;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.io.IOException;

public class MoviePlayerFrame extends Frame implements MoviePlayer {

    private MovieStreamProvider movieStreamProvider = new MovieStreamProvider();

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
        setSize(new Dimension((int) scale*movieStreamProvider.getWidthOfView(),
                (int) scale*movieStreamProvider.getHeightOfView()));
    }

    @Override
    public void setRate(float rate) {
        movieStreamProvider.setSpeed(rate);
    }

    @Override
    public void start() {
        movieStreamProvider.start();
    }

    @Override
    public void stop() {
        movieStreamProvider.stop();
    }

    @Override
    public void stepForward() {
        movieStreamProvider.stepForward();
    }

    @Override
    public void openFile(String file, String version) throws IOException {
        movieStreamProvider.open(file, version, colorSpace, audioFormat);
        setSize(new Dimension(movieStreamProvider.getWidthOfView(), movieStreamProvider.getHeightOfView()));
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
        movieStreamProvider.setCurrentTime(timeInSeconds);
    }
}
