package org.datavyu.plugins.ffmpeg.examples;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

public class SimpleJavafxPlayerExample extends Application {
    private static final String MEDIA_URL = "file:///C:/Users/Florian/datavyu/datavyu-ffmpegplugin/counter.mp4";

    @Override
    public void start(Stage primaryStage) {
        // create media player
        Media media = new Media(MEDIA_URL);
        MediaPlayer mediaPlayer = new MediaPlayer(media);

        primaryStage.setTitle("Media Player");
        Group root = new Group();
        Scene scene = new Scene(root, 1920, 1080);

        MediaView mediaView = new MediaView(mediaPlayer);
        Pane pane = new Pane() {};
        pane.getChildren().add(mediaView);
        pane.setStyle("-fx-background-color: black;");
        scene.setRoot(pane);
        //mediaPlayer.setAutoPlay(true);

        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
