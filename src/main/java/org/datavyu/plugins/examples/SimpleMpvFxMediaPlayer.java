package org.datavyu.plugins.examples;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.datavyu.plugins.MediaPlayer;
import org.datavyu.plugins.mpv.MpvFxMediaPlayer;

import java.io.File;
import java.net.URI;

public class SimpleMpvFxMediaPlayer extends Application {

    @Override
    public void start(Stage primaryStage) {
        URI mediaPath = new File("Nature_30fps_1080p.mp4").toURI();
        MediaPlayer mediaPlayer = new MpvFxMediaPlayer(mediaPath, primaryStage);
        mediaPlayer.init();
        Platform.runLater(() -> new JMediaPlayerControlFrame(mediaPlayer));
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}

