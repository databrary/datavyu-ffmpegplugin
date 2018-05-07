package org.datavyu.benchmark;

import javafx.stage.Stage;
import org.datavyu.plugins.ffmpegplayer.AudioSoundStreamListener;
import org.datavyu.plugins.ffmpegplayer.MoviePlayer;
import org.datavyu.plugins.ffmpegplayer.VideoStreamListenerStage;

import javax.sound.sampled.AudioFormat;
import java.awt.color.ColorSpace;
import java.io.IOException;

/**
 * Note this is one suggestion
 */
public class MoviePlayerStage implements MoviePlayerControl {

    private MoviePlayer moviePlayer;

    VideoStreamListenerStage videoStreamListenerStage;

    public MoviePlayerStage(ColorSpace colorSpace, AudioFormat audioFormat, String movieFileName) {
        this.moviePlayer = MoviePlayer.newBuilder()
                .setFileName(movieFileName)
                .setColorSpace(colorSpace)
                .setAudioFormat(audioFormat)
                .build();

        if (moviePlayer.hasError()) {
            throw moviePlayer.getError();
        }
        this.videoStreamListenerStage = new VideoStreamListenerStage(moviePlayer, colorSpace);
        moviePlayer.addAudioStreamListener(new AudioSoundStreamListener(moviePlayer));
        moviePlayer.addVideoStreamListener(videoStreamListenerStage);
    }

    @Override
    public void setScale(float scale) {
/*
        if (stage != null) {
            videoStreamListenerStage.s(scale * moviePlayer.getWidthOfView());
            videoStreamListenerStage.setMaxHeight(scale * moviePlayer.getHeightOfView());
        }
*/
    }

    @Override
    public void setRate(float rate) {
        moviePlayer.setSpeed(rate);
    }

    @Override
    public void start() {
        new Thread() {
            @Override
            public void run() {
                videoStreamListenerStage.start(new Stage());
            }
        }.start();
        moviePlayer.play();
    }

    @Override
    public void stop() {
        moviePlayer.stop();
    }

    @Override
    public void close() {
        moviePlayer.close();
    }

    @Override
    public double getTimeInSeconds() {
        return moviePlayer.getCurrentTime();
    }

    @Override
    public void setTimeInSeconds(double timeInSeconds) {
        moviePlayer.seek(timeInSeconds);
    }
}
