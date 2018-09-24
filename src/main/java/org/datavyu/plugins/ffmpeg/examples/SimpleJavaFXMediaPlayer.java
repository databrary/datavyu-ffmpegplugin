package org.datavyu.plugins.ffmpeg.examples;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.datavyu.plugins.ffmpeg.FfmpegJavaMediaPlayer;
import org.datavyu.plugins.ffmpeg.MediaPlayerData;

import java.io.File;
import java.net.URI;

public class SimpleJavaFXMediaPlayer extends Application {
    private Stage primaryStage;
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        // Define the media file
        URI mediaPath = new File("Nature_30fps_1080p.mp4").toURI();
        MediaPlayerData mediaPlayer = new FfmpegJavaMediaPlayer(mediaPath, primaryStage);
        mediaPlayer.init();
        Platform.runLater(() -> new JMediaPlayerControlFrame(mediaPlayer));

    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}