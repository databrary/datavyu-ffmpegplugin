package org.datavyu.benchmark;

import org.datavyu.plugins.ffmpegplayer.AudioSoundStreamListener;
import org.datavyu.plugins.ffmpegplayer.MoviePlayer;
import org.datavyu.plugins.ffmpegplayer.VideoStreamListenerContainer;

import javax.sound.sampled.AudioFormat;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.io.IOException;

public class MoviePlayerFrame extends Frame implements MoviePlayerControl {

    private MoviePlayer moviePlayer;

    private VideoStreamListenerContainer videoStreamListenerContainer;

    MoviePlayerFrame(ColorSpace colorSpace, AudioFormat audioFormat, String movieFileName) {
        this.moviePlayer = MoviePlayer.newBuilder()
                .setFileName(movieFileName)
                .setColorSpace(colorSpace)
                .setAudioFormat(audioFormat)
                .build();

        if (moviePlayer.hasError()) {
            throw moviePlayer.getError();
        }

        videoStreamListenerContainer = new VideoStreamListenerContainer(moviePlayer, this, colorSpace);

        moviePlayer.addAudioStreamListener(new AudioSoundStreamListener(moviePlayer));
        moviePlayer.addVideoStreamListener(videoStreamListenerContainer);
        setVisible(true);
    }

    @Override
    public void setScale(float scale) {
        videoStreamListenerContainer.setScale(scale);
        setSize(new Dimension((int) scale* moviePlayer.getWidth(),
                (int) scale* moviePlayer.getHeight()));
    }

    @Override
    public void setRate(float rate) {
        moviePlayer.setSpeed(rate);
    }

    @Override
    public void start() {
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
