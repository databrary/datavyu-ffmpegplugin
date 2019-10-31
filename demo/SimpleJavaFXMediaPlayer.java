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

  @Override
  public void start(Stage primaryStage) {
    URI mediaPath = new File("counter.mp4").toURI();
    MediaPlayer mediaPlayer = new FfmpegSdlMediaPlayer(mediaPath);

    mediaPlayer.addMediaErrorListener(
        // Handle error thrown by The Media Player
        (source, errorCode, message) -> logger.error(errorCode + ": " + message));

    mediaPlayer.addSdlKeyEventListener(
        (source, nativeMediaRef, javaKeyCode) ->
            // Handle key events triggered on SDL window (Working only on Windows platform)
            logger.info("SDL Media " + nativeMediaRef + " event " + javaKeyCode));

    mediaPlayer.init();
    Platform.runLater(
        // Video Controller
        () -> new JMediaPlayerControlFrame(mediaPlayer));
  }

  public static void main(String[] args) {
    Application.launch(args);
  }
}
