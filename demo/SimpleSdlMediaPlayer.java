import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.datavyu.plugins.MediaPlayerWindow;
import org.datavyu.plugins.ffmpeg.FfmpegSdlMediaPlayer;

import java.io.File;
import java.net.URI;

public class SimpleSdlMediaPlayer {
  private static Logger logger = LogManager.getLogger(SimpleSdlMediaPlayer.class);

  public static void main(String[] args) {
    // Define the media file
    URI mediaPath = new File("Nature_30fps_1080p.mp4").toURI();

    // Create the media player using the constructor with File
    MediaPlayerWindow mediaPlayer = new FfmpegSdlMediaPlayer(mediaPath);

    // Handle Media Player errors
    mediaPlayer.addMediaErrorListener(
        (source, errorCode, message) -> logger.error(errorCode + ": " + message));

    // Initialize the player
    mediaPlayer.init();

    // Open a simple JFrame to control the media player through key commands
    // Be creative and create your own controller
    JMediaPlayerControlFrame controller = new JMediaPlayerControlFrame(mediaPlayer);

    // Handle Window Key events
    mediaPlayer.addSdlKeyEventListener(
            (source, nativeMediaRef, javaKeyCode) -> controller.handleKeyEvents(javaKeyCode));
  }
}
