package org.datavyu.benchmark;

import javafx.stage.Stage;
import org.datavyu.plugins.ffmpegplayer.AudioSoundStreamListener;
import org.datavyu.plugins.ffmpegplayer.MovieStreamProvider;
import org.datavyu.plugins.ffmpegplayer.VideoStreamListenerStage;

import javax.sound.sampled.AudioFormat;
import java.awt.color.ColorSpace;
import java.io.IOException;

public class MoviePlayerStage extends Stage implements MoviePlayer {

    private MovieStreamProvider movieStreamProvider = new MovieStreamProvider();

    private ColorSpace colorSpace;

    private AudioFormat audioFormat;

    public MoviePlayerStage(ColorSpace colorSpace, AudioFormat audioFormat) {
        this.colorSpace = colorSpace;
        this.audioFormat = audioFormat;

        movieStreamProvider.addAudioStreamListener(new AudioSoundStreamListener(movieStreamProvider));
        movieStreamProvider.addVideoStreamListener(new VideoStreamListenerStage(movieStreamProvider, this,
                colorSpace));
    }

    @Override
    public void stepForward() {
        movieStreamProvider.stepForward();
    }

    @Override
    public void setScale(float scale) {

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
    public void openFile(String file, String version) throws IOException {
        movieStreamProvider.open(file, version, colorSpace, audioFormat);
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
