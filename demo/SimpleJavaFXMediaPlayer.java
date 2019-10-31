import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.datavyu.plugins.MediaPlayer;
import org.datavyu.plugins.ffmpeg.FfmpegSdlMediaPlayer;

import java.io.File;
import java.net.URI;

/**
 * WARNING: This class is tested only on Windows platform
 */
public class SimpleJavaFXMediaPlayer extends Application {
  private static Logger logger = LogManager.getLogger(SimpleJavaFXMediaPlayer.class);
  private JMediaPlayerControlFrame controller;

  @Override
  public void start(Stage primaryStage) {
    URI mediaPath = new File("counter.mp4").toURI();
    MediaPlayer mediaPlayer = new FfmpegSdlMediaPlayer(mediaPath);

    mediaPlayer.addMediaErrorListener(
        // Handle error thrown by The Media Player
        (source, errorCode, message) -> logger.error(errorCode + ": " + message));

    mediaPlayer.init();

    // Open a simple JFrame to control the media player through key commands
    // Be creative and create your own controller in JavaFX
    Platform.runLater(
        () -> {
          controller = new JMediaPlayerControlFrame(mediaPlayer);
        });

    // Handle Window Key events triggered from SDL window (Working only on Windows platform)
    mediaPlayer.addSdlKeyEventListener(
            (source, nativeMediaRef, javaKeyCode) ->
                    controller.handleKeyEvents(javaKeyCode));
  }

  public static void main(String[] args) {
    Application.launch(args);
  }
}
