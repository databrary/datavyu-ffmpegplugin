package org.datavyu.plugins.examples;

import org.datavyu.plugins.nativeosx.AVFoundationMediaPlayer;
import org.datavyu.plugins.MediaPlayer;

import javax.swing.*;
import java.io.File;
import java.net.URI;

public class SimpleAVFoundationMediaPlayer {
    public static void main(String[] args) {
        // Define the media file
        URI mediaPath = new File("Nature_30fps_1080p.mp4").toURI();

        // Create the media player using the constructor with File
        MediaPlayer mediaPlayer = new AVFoundationMediaPlayer(mediaPath, new JDialog());

        mediaPlayer.addMediaErrorListener(
            (source, errorCode, message)
                -> System.err.println(errorCode + ": " + message));

        // Initialize the player
        mediaPlayer.init();

        // Open a JFrame to control the media player through key commands
        new JMediaPlayerControlFrame(mediaPlayer);
    }
}
