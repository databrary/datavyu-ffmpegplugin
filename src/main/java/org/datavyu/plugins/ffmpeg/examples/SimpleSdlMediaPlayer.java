package org.datavyu.plugins.ffmpeg.examples;

import org.datavyu.plugins.ffmpeg.*;

import java.io.File;

public class SimpleSdlMediaPlayer {
    public static void main(String[] args) {
        String movieFileName = "Nature_30fps_1080p.mp4";

        // Create the media player using the constructor with File
        MediaPlayer mediaPlayer = new FfmpegSdlMediaPlayer(new File(movieFileName));

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
