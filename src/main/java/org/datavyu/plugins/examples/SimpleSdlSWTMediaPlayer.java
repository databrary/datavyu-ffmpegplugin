package org.datavyu.plugins.examples;

import java.io.File;
import java.net.URI;
import org.datavyu.plugins.MediaPlayer;
import org.datavyu.plugins.ffmpeg.FfmpegSdlSwtMediaPlayer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

//TODO(Reda): Fix SDL Create Windows from SWT Handle
//Display must be created on main thread due to Cocoa restrictions. Use vmarg -XstartOnFirstThread
public class SimpleSdlSWTMediaPlayer {
  public static void main(String[] args) {
    // Define the media file
    URI mediaPath = new File("Nature_30fps_1080p.mp4").toURI();
    Display display = new Display ();
    Shell shell = new Shell (display);

    // Create the media player using the constructor with File
    MediaPlayer mediaPlayer = new FfmpegSdlSwtMediaPlayer(mediaPath, shell);

    // Initialize the player
    display.asyncExec(new Runnable() {
      @Override
      public void run() {
        mediaPlayer.init();
      }
    });
    // Open a JFrame to control the media player through key commands
    new JMediaPlayerControlFrame(mediaPlayer);
    while (!shell.isDisposed()) {
      if (!shell.getDisplay().readAndDispatch ()) shell.getDisplay().sleep();
    }
  }
}
