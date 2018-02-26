package org.datavyu.benchmark;


import javafx.application.Application;
import javafx.stage.Stage;
import org.datavyu.plugins.ffmpegplayer.AudioSoundStreamListener;
import org.datavyu.plugins.ffmpegplayer.MovieStreamProvider;
import org.datavyu.plugins.ffmpegplayer.VideoStreamListenerStage;

import javax.sound.sampled.AudioFormat;
import java.awt.color.ColorSpace;
import java.io.IOException;

public class MoviePlayerStage extends Application implements MoviePlayer {

    private MovieStreamProvider movieStreamProvider = new MovieStreamProvider();

    private ColorSpace colorSpace;

    private AudioFormat audioFormat;

    private Stage stage;

    public MoviePlayerStage() {
        this(ColorSpace.getInstance(ColorSpace.CS_sRGB), AudioSoundStreamListener.getNewMonoFormat());
    }

    public MoviePlayerStage(ColorSpace colorSpace, AudioFormat audioFormat) {
        this.colorSpace = colorSpace;
        this.audioFormat = audioFormat;
    }

    @Override
    public void init() throws Exception {
        super.init();
        movieStreamProvider.addAudioStreamListener(new AudioSoundStreamListener(movieStreamProvider));
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        movieStreamProvider.addVideoStreamListener(new VideoStreamListenerStage(movieStreamProvider, stage, colorSpace));
        stage.show();
    }

    @Override
    public void stepForward() {
        movieStreamProvider.stepForward();
    }

    @Override
    public void setScale(float scale) {
        stage.setMaxWidth(scale * movieStreamProvider.getWidthOfView());
        stage.setMaxHeight(scale * movieStreamProvider.getHeightOfView());
    }

    @Override
    public void setRate(float rate) {
        movieStreamProvider.setSpeed(rate);
    }

    @Override
    public void start() {
        movieStreamProvider.start();
        launch(new String[]{});
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
