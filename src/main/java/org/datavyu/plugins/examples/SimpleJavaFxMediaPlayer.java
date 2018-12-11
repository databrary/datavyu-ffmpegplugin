package org.datavyu.plugins.examples;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import org.datavyu.plugins.MediaPlayer;
import org.datavyu.plugins.javafx.JavaFxMediaPlayer;

import java.io.File;
import java.net.URI;

public class SimpleJavaFxMediaPlayer extends Application {
  private MediaPlayer mediaPlayer;

  private final Object readyLock = new Object();

  @Override
  public void start(Stage primaryStage) throws Exception {
    URI mediaPath = new File("counter.mp4").toURI();
    mediaPlayer = new JavaFxMediaPlayer(mediaPath, primaryStage, readyLock);
    mediaPlayer.init();
    Task<Void> waitingTask = new Task<Void>() {
      @Override
      protected Void call() throws Exception {

        synchronized (readyLock){
          readyLock.wait();
        }

        Platform.runLater(() -> {
          new JMediaPlayerControlFrame(mediaPlayer);
        });
        return null;
      }
    };
    new Thread(waitingTask).start();
  }

  public static void main(String[] args) {
    Application.launch(args);
  }
}
