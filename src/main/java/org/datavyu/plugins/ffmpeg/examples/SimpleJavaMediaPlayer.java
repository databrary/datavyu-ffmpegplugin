package org.datavyu.plugins.ffmpeg.examples;

import org.datavyu.plugins.ffmpeg.*;

import javax.sound.sampled.AudioFormat;
import javax.swing.*;
import java.awt.color.ColorSpace;
import java.io.File;

public class SimpleJavaMediaPlayer {

    public static void main(String[] args) {
        String movieFileName = "Nature_30fps_1080p.mp4";

        // Define the audio format and color space that we would like to have
        ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB); // This is the only one we support
        AudioFormat audioFormat = AudioPlayerThread.getMonoFormat(); // There are only two audio formats we support

        // Create the media player using the constructor with File
        MediaPlayerData mediaPlayer = new FfmpegJavaMediaPlayer(
                new File(movieFileName), new JFrame(), audioFormat, colorSpace);

        // Register an error listener
        mediaPlayer.addMediaErrorListener(new MediaErrorListener() {
            @Override
            public void onError(Object source, int errorCode, String message) {
                System.err.println("Error " + errorCode + ": " + message);
            }
        });

        // Initialize the player
        mediaPlayer.init();

        // Open a JFrame to control the media player through key commands
        new JMediaPlayerControlFrame(mediaPlayer);
    }
}
