package org.datavyu.benchmark;

import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.datavyu.plugins.ffmpegplayer.AudioSoundStreamListener;
import org.datavyu.plugins.ffmpegplayer.MovieStreamProvider;
import org.datavyu.plugins.ffmpegplayer.VideoStreamListenerStage;

import javax.sound.sampled.AudioFormat;
import java.awt.color.ColorSpace;
import java.io.IOException;

public class MoviePlayerStage implements MoviePlayer {


    /** The logger for this class */
    private static Logger logger = LogManager.getFormatterLogger(MoviePlayerStage.class);

    private MovieStreamProvider movieStreamProvider = new MovieStreamProvider();

    private ColorSpace colorSpace;

    private AudioFormat audioFormat;

    VideoStreamListenerStage videoStreamListenerStage;

    public MoviePlayerStage(ColorSpace colorSpace, AudioFormat audioFormat) {
        this.colorSpace = colorSpace;
        this.audioFormat = audioFormat;
        this.videoStreamListenerStage = new VideoStreamListenerStage(movieStreamProvider, colorSpace);
        movieStreamProvider.addAudioStreamListener(new AudioSoundStreamListener(movieStreamProvider));
        movieStreamProvider.addVideoStreamListener(videoStreamListenerStage);
    }

    @Override
    public void stepForward() {
        movieStreamProvider.stepForward();
    }

    @Override
    public void setScale(float scale) {
/*
        if (stage != null) {
            videoStreamListenerStage.s(scale * movieStreamProvider.getWidthOfView());
            videoStreamListenerStage.setMaxHeight(scale * movieStreamProvider.getHeightOfView());
        }
*/
    }

    @Override
    public void setRate(float rate) {
        movieStreamProvider.setSpeed(rate);
    }

    @Override
    public void start() {
        new Thread() {
            @Override
            public void run() {
                videoStreamListenerStage.start(new Stage());
            }
        }.start();
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
