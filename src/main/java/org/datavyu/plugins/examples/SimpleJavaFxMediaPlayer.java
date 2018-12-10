package org.datavyu.plugins.examples;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.datavyu.plugins.MediaPlayer;
import org.datavyu.plugins.javafx.JavaFxMediaPlayer;

import java.io.File;
import java.net.URI;

public class SimpleJavaFxMediaPlayer extends Application {
  MediaPlayer mediaPlayer;

  @Override
  public void start(Stage primaryStage) throws Exception {
    URI mediaPath = new File("Nature_30fps_1080p.mp4").toURI();
    mediaPlayer = new JavaFxMediaPlayer(mediaPath, primaryStage);
    mediaPlayer.init();
    Platform.runLater(()-> new JMediaPlayerControlFrame(mediaPlayer));
  }

  public static void main(String[] args) {
    Application.launch(args);
  }
}
