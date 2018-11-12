package org.datavyu.plugins.ffmpeg.examples;

import org.datavyu.plugins.ffmpeg.*;

import java.io.File;
import java.net.URI;

public class SimpleSdlMediaPlayer {
    public static void main(String[] args) {
        // Define the media file
        URI mediaPath = new File("/Users/RedaNezzar/Documents/source/datavyu-ffmpegplugin/src/test/resources/toystory.mp4").toURI();

        // Create the media player using the constructor with File
        MediaPlayer mediaPlayer = new FfmpegSdlMediaPlayer(mediaPath);

        // Initialize the player
        mediaPlayer.init();

        mediaPlayer.play();
        // Open a JFrame to control the media player through key commands
        new JMediaPlayerControlFrame(mediaPlayer);
    }
}
