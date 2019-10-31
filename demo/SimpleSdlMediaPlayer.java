import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.datavyu.plugins.MediaPlayer;
import org.datavyu.plugins.ffmpeg.FfmpegSdlMediaPlayer;

import java.io.File;
import java.net.URI;

public class SimpleSdlMediaPlayer {
  private static Logger logger = LogManager.getLogger(SimpleSdlMediaPlayer.class);

  public static void main(String[] args) {
    // Define the media file
    URI mediaPath = new File("Nature_30fps_1080p.mp4").toURI();

    // Create the media player using the constructor with File
    MediaPlayer mediaPlayer = new FfmpegSdlMediaPlayer(mediaPath);

    mediaPlayer.addMediaErrorListener(
        (source, errorCode, message) -> logger.error(errorCode + ": " + message));

    mediaPlayer.addSdlKeyEventListener(
        (source, nativeMediaRef, javaKeyCode) -> logger.info("SDL Media " + nativeMediaRef + " event " + javaKeyCode));

    // Initialize the player
    mediaPlayer.init();
    // Open a JFrame to control the media player through key commands
    new JMediaPlayerControlFrame(mediaPlayer);
  }
}
