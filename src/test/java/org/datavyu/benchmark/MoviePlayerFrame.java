package org.datavyu.benchmark;

import org.datavyu.plugins.ffmpegplayer.AudioSoundStreamListener;
import org.datavyu.plugins.ffmpegplayer.MediaPlayer;
import org.datavyu.plugins.ffmpegplayer.ImageStreamListenerContainer;

import javax.sound.sampled.AudioFormat;
import java.awt.*;
import java.awt.color.ColorSpace;

public class MoviePlayerFrame extends Frame implements MoviePlayerControl {

    private MediaPlayer mediaPlayer;

    private ImageStreamListenerContainer imageStreamListenerContainer;

    MoviePlayerFrame(ColorSpace colorSpace, AudioFormat audioFormat, String movieFileName) {
        imageStreamListenerContainer = new ImageStreamListenerContainer(this, null, colorSpace);
        this.mediaPlayer = MediaPlayer.newBuilder()
                .setFileName(movieFileName)
                .setColorSpace(colorSpace)
                .setAudioFormat(audioFormat)
                .addAudioStreamListener(new AudioSoundStreamListener(audioFormat))
                .addImageStreamListener(imageStreamListenerContainer)
                .build();
        if (mediaPlayer.hasError()) {
            throw mediaPlayer.getError();
        }
        setVisible(true);
    }

    @Override
    public void setScale(float scale) {
        imageStreamListenerContainer.setScale(scale);
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
