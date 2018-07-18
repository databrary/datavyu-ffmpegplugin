package org.datavyu.plugins.ffmpeg.examples;

import org.datavyu.plugins.ffmpeg.MediaErrorListener;
import org.datavyu.plugins.ffmpeg.MediaPlayer;
import org.datavyu.plugins.ffmpeg.FfmpegMediaPlayer;
import org.datavyu.plugins.ffmpegplayer.AudioSoundStreamListener;

import javax.sound.sampled.AudioFormat;
import java.awt.color.ColorSpace;
import java.net.URI;

public class SimpleMediaPlayerExample {
    public static void main(String[] args) {
        String movieFileName = "";
        MediaPlayer mediaPlayer = new FfmpegMediaPlayer(URI.create(movieFileName));
        mediaPlayer.addMediaErrorListener(new MediaErrorListener() {
            @Override
            public void onError(Object source, int errorCode, String message) {
                System.err.println("Error " + errorCode + ": " + message);
            }
        });
        final ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        AudioFormat audioFormat = AudioSoundStreamListener.getNewMonoFormat();
        mediaPlayer.init(audioFormat, colorSpace);
        mediaPlayer.play();
        try {
            Thread.sleep(60*1000);
        } catch (InterruptedException ie) {
            System.out.println("Stopping playback after 60 secs");
        }
    }
}
