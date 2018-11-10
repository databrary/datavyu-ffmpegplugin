package org.datavyu.plugins.ffmpeg.examples;

import org.datavyu.plugins.ffmpeg.*;

import javax.swing.*;
import java.io.File;
import java.net.URI;

public class SimpleJavaMediaPlayer {

    public static void main(String[] args) {
        // Define the media file
        URI mediaPath = new File("Nature_30fps_1080p.mp4").toURI();
        //URI mediaPath = new File("counter.mp4").toURI();
        //URI mediaPath = new File("DatavyuSampleVideo.mp4").toURI();

        // Create the media player using the constructor with File
        MediaPlayerData mediaPlayer = new FfmpegJavaMediaPlayer(mediaPath, new JDialog());

        // Initialize the player
        mediaPlayer.init();

        // Set initial time and rate (which in the past triggered a flush on the queue and thus inaccurate timing)
        mediaPlayer.setStartTime(0);
        mediaPlayer.setRate(1f);

        // Open a JFrame to control the media player through key commands
        new JMediaPlayerControlFrame(mediaPlayer);
    }
}
