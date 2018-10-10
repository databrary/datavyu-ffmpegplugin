package org.datavyu.plugins.ffmpeg.examples;


import org.datavyu.plugins.ffmpeg.MediaPlayer;
import org.datavyu.plugins.ffmpeg.MpvMediaPlayer;

import javax.swing.*;
import java.io.File;
import java.net.URI;

public class SimpleMpvMediaPlayer {

    public static void main(String[] args) {
        // Define the media file
        URI mediaPath = new File("C:\\Users\\DatavyuTests\\Documents\\Databrary\\datavyu-ffmpegplugin\\Nature_30fps_1080p.mp4").toURI();

        // Create the media player using the constructor with File
        MediaPlayer mediaPlayer = new MpvMediaPlayer(mediaPath, new JDialog());

        // Initialize the player
        mediaPlayer.init();

        // Open a JFrame to control the media player through key commands
        new JMediaPlayerControlFrame(mediaPlayer);
    }
}
