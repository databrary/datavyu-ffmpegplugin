package org.datavyu.plugins.ffmpeg.examples;

import org.datavyu.plugins.ffmpeg.*;
import org.datavyu.plugins.ffmpegplayer.AudioSoundStreamListener;

import javax.sound.sampled.AudioFormat;
import javax.swing.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.io.File;
import java.net.URI;

public class SimpleJavaMediaPlayer {

    public static void main(String[] args) {
        // Define the media file
        URI mediaPath = new File("Nature_30fps_1080p.mp4").toURI();

        //Use ImageCanvasPlayerThread class
        JDialog container = new JDialog();
        container.setVisible(true);
        MediaPlayerData mediaPlayer = new FfmpegJavaMediaPlayer(mediaPath,
                (Container)container
                ,AudioSoundStreamListener.getNewMonoFormat()
                ,ColorSpace.getInstance(ColorSpace.CS_sRGB));

        // Create the media player using the constructor with URI and JFrame
        // Test imagePlayerThread Class
//        MediaPlayerData mediaPlayer = new FfmpegJavaMediaPlayer(mediaPath, new JFrame());

        // Initialize the player
        mediaPlayer.init();

        // Open a JFrame to control the media player through key commands
        new JMediaPlayerControlFrame(mediaPlayer);
    }
}
