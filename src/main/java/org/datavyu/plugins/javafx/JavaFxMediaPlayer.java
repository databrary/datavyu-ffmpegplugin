package org.datavyu.plugins.javafx;

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
import org.datavyu.plugins.DatavyuMediaPlayer;
import org.datavyu.plugins.MediaException;
import org.datavyu.util.FrameRate;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.net.URI;

public class JavaFxMediaPlayer extends DatavyuMediaPlayer {
  private Stage stage;
  private Media media;
  private MediaPlayer mediaPlayer;
  private MediaView mediaView;
  private StackPane root;
  private Scene scene;

  private final Object readyLock;

  private float fps = -1;

  public JavaFxMediaPlayer(URI mediaPath, Stage stage, Object readyLock) {
    super(mediaPath);
    this.media = new Media(mediaPath.toString());
    this.readyLock = readyLock;
    this.stage = stage;
  }

  @Override
  public void init() {
    initNative();
    initStage();

    fps = FrameRate.createDefaultFrameRate(mediaPath).getFPS();
  }

  private void initStage() {
    mediaPlayer = new MediaPlayer(media);

    mediaPlayer.setOnReady(() -> {
      synchronized (readyLock){
        stage.setHeight(getImageHeight());
        stage.setWidth(getImageWidth());
        stage.show();
        readyLock.notify();
      }
    });

    mediaView = new MediaView(mediaPlayer);
    root = new StackPane(mediaView);
    scene = new Scene(root);

    DoubleProperty width = mediaView.fitWidthProperty();
    DoubleProperty height = mediaView.fitHeightProperty();

    width.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
    height.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));

    mediaView.setPreserveRatio(true);
    scene.setFill(Color.BLACK);

    stage.setScene(scene);
    stage.setTitle(media.getSource());
  }

  @Override
  protected void playerPlay() throws MediaException {
    mediaPlayer.play();
  }

  @Override
  protected void playerStop() throws MediaException {
    mediaPlayer.pause();
    if (mediaPlayer.getRate() != 1F) {
      mediaPlayer.setRate(1F);
    }
  }

  @Override
  protected void playerStepForward() throws MediaException {
    double stepSize = Math.ceil(1000F / fps);
    double time = mediaPlayer.getCurrentTime().toMillis();
    double newTime = Math.min(Math.max(time + stepSize, 0), mediaPlayer.getTotalDuration().toMillis());
    playerSeek(newTime / 1000);
  }

  @Override
  protected void playerStepBackward() throws MediaException {
    double stepSize = Math.ceil(1000F / fps);
    double time = mediaPlayer.getCurrentTime().toMillis();
    double newTime = Math.min(Math.max(time - stepSize, 0), mediaPlayer.getTotalDuration().toMillis());
    playerSeek(newTime / 1000);
  }

  @Override
  protected void playerPause() throws MediaException {
    if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING){
      mediaPlayer.pause();
    } else if (mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
      mediaPlayer.play();
    }
  }

  @Override
  protected void playerFinish() throws MediaException { }

  @Override
  protected float playerGetRate() throws MediaException {
    return (float) mediaPlayer.getRate();
  }

  @Override
  protected void playerSetRate(float rate) throws MediaException {
    mediaPlayer.setRate(rate);
  }

  @Override
  protected double playerGetPresentationTime() throws MediaException {
    return mediaPlayer.getCurrentTime().toMillis() / 1000;
  }

  @Override
  protected double playerGetFps() throws MediaException {
    return fps;
  }

  @Override
  protected float playerGetVolume() throws MediaException {
    return (float) mediaPlayer.getVolume();
  }

  @Override
  protected void playerSetVolume(float volume) throws MediaException {
    mediaPlayer.setVolume(volume);
  }

  @Override
  protected double playerGetDuration() throws MediaException {
    return mediaPlayer.getTotalDuration().toMillis() / 1000;
  }

  @Override
  protected void playerSetStartTime(double startTime) throws MediaException {
    mediaPlayer.setStartTime(Duration.millis(startTime));
  }

  @Override
  protected void playerSeek(double streamTime) throws MediaException {
    mediaPlayer.seek(Duration.millis(streamTime * 1000));
  }

  @Override
  protected void playerDispose() { mediaPlayer.dispose(); }

  @Override
  public int getImageWidth() { return media.getWidth(); }

  @Override
  public int getImageHeight() {
    return media.getHeight();
  }

  @Override
  protected float playerGetBalance() throws MediaException {
    throw new NotImplementedException();
  }

  @Override
  protected void playerSetBalance(float balance) throws MediaException {
    throw new NotImplementedException();
  }

  @Override
  protected long playerGetAudioSyncDelay() throws MediaException {
    throw new NotImplementedException();
  }

  @Override
  protected void playerSetAudioSyncDelay(long delay) throws MediaException {
    throw new NotImplementedException();
  }
}
