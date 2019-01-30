package org.datavyu.plugins.examples;

import org.datavyu.plugins.MediaErrorListener;
import org.datavyu.plugins.MediaPlayerData;
import org.datavyu.plugins.ffmpeg.*;

import javax.swing.*;
import java.io.File;
import java.net.URI;

public class SimpleJavaMediaPlayer {
  public static void main(String[] args) {
    // Define the media file
    URI mediaPath = new File("Nature_30fps_1080p.mp4").toURI();

    // Create the media player using the constructor with File
    MediaPlayerData mediaPlayer = new FfmpegJavaMediaPlayer(mediaPath, new JDialog());

    mediaPlayer.addMediaErrorListener(
        new MediaErrorListener() {
          @Override
          public void onError(Object source, int errorCode, String message) {
            System.err.println(errorCode + ": " + message);
          }
        });

    // Initialize the player
    mediaPlayer.init();

    // Open a JFrame to control the media player through key commands
    new JMediaPlayerControlFrame(mediaPlayer);
  }
}
