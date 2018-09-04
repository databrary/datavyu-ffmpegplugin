package org.datavyu.plugins.ffmpeg;

import org.datavyu.plugins.ffmpeg.MoviePlayerControl;
import org.datavyu.plugins.ffmpegplayer.AudioSoundStreamListener;
import org.datavyu.plugins.ffmpegplayer.MediaPlayer;
import org.datavyu.plugins.ffmpegplayer.ImageStreamListenerFrame;

import javax.sound.sampled.AudioFormat;
import javax.swing.*;
import java.awt.*;
import java.awt.color.ColorSpace;

public class MoviePlayerFrame extends JFrame implements MoviePlayerControl {

    private MediaPlayer mediaPlayer;

    private ImageStreamListenerFrame imageStreamListenerFrame;

    MoviePlayerFrame(ColorSpace colorSpace, AudioFormat audioFormat, String movieFileName) {
        imageStreamListenerFrame = new ImageStreamListenerFrame(this, colorSpace);
        this.mediaPlayer = MediaPlayer.newBuilder()
                .setFileName(movieFileName)
                .setColorSpace(colorSpace)
                .setAudioFormat(audioFormat)
                .addAudioStreamListener(new AudioSoundStreamListener(audioFormat))
                .addImageStreamListener(imageStreamListenerFrame)
                .build();
        if (mediaPlayer.hasError()) {
            throw mediaPlayer.getError();
        }
        setVisible(true);
    }

    @Override
    public void setScale(float scale) {
        setSize(new Dimension((int) scale* mediaPlayer.getWidth(),
                (int) scale* mediaPlayer.getHeight()));
    }

    @Override
    public void setRate(float rate) {
        mediaPlayer.setSpeed(rate);
    }

    @Override
    public void start() {
        mediaPlayer.play();
    }

    @Override
    public void stop() {
        mediaPlayer.stop();
    }

    @Override
    public void close() {
        mediaPlayer.close();
    }

    @Override
    public double getTimeInSeconds() {
        return mediaPlayer.getCurrentTime();
    }

    @Override
    public void setTimeInSeconds(double timeInSeconds) {
        mediaPlayer.seek(timeInSeconds);
    }
}
