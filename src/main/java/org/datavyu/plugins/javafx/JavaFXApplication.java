package org.datavyu.plugins.javafx;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;

/**
 * Created by jesse on 10/21/14.
 */
public class JavaFXApplication extends Application {

    File dataFile;
    boolean init = false;
    MediaPlayer mp;

    public JavaFXApplication(File file) {
        dataFile = file;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void seek(long time) {
        mp.seek(Duration.millis(time));
    }

    public void pause() {
        mp.pause();
    }

    public void play() {
        mp.play();
    }

    public void stop() {
        mp.stop();
    }

    public long getCurrentTime() {
        return (long) mp.getCurrentTime().toMillis();
    }

    public float getFrameRate() {
        return (float) 30;
    }

    public long getDuration() {
        return (long) mp.getTotalDuration().toMillis();
    }

    public boolean isInit() {
        return init;
    }

    public void start(Stage primaryStage) {
        String workingDir = System.getProperty("user.dir");

        final Media m = new Media(dataFile.toURI().toString());
        mp = new MediaPlayer(m);
        final MediaView mv = new MediaView(mp);

        final DoubleProperty width = mv.fitWidthProperty();
        final DoubleProperty height = mv.fitHeightProperty();

        width.bind(Bindings.selectDouble(mv.sceneProperty(), "width"));
        height.bind(Bindings.selectDouble(mv.sceneProperty(), "height"));

        mv.setPreserveRatio(true);

        StackPane root = new StackPane();
        root.getChildren().add(mv);

        final Scene scene = new Scene(root, 960, 540);
        scene.setFill(Color.BLACK);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Full Screen Video Player");
//        primaryStage.setFullScreen(true);
        primaryStage.show();


        System.out.println("Setting init to true");
        init = true;
        System.out.println(init);

    }

}
