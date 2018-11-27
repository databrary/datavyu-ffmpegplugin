package org.datavyu.plugins.ffmpeg.examples;


import javafx.application.Application;
import javafx.stage.Stage;
import org.datavyu.plugins.ffmpeg.MediaPlayer;
import org.datavyu.plugins.ffmpeg.MpvMediaPlayer;

import java.io.File;
import java.net.URI;

public class SimpleMpvMediaPlayer extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Define the media file
        URI mediaPath = new File("Nature_30fps_1080p.mp4").toURI();

        // Create the media player using the constructor with File
        MediaPlayer mediaPlayer = new MpvMediaPlayer(mediaPath, new Stage());

        // Initialize the player
        mediaPlayer.init();

        // Open a JFrame to control the media player through key commands
        new JMediaPlayerControlFrame(mediaPlayer);
    }
}
