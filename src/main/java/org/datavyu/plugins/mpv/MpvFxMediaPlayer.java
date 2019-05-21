package org.datavyu.plugins.mpv;

import java.lang.reflect.InvocationTargetException;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.datavyu.plugins.MediaException;
import org.datavyu.plugins.PlayerStateEvent;
import org.datavyu.plugins.PlayerStateListener;

import java.net.URI;
import org.datavyu.util.Utils;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public final class MpvFxMediaPlayer extends MpvMediaPlayer {
  private Stage stage;
  private static final Logger logger = LogManager.getFormatterLogger(MpvFxMediaPlayer.class);

  public MpvFxMediaPlayer(URI mediaPath, Stage stage) {
    super(mediaPath);
    this.stage = stage;
  }

  @Override
  public void init() {
    addMediaPlayerStateListener(new PlayerStateListenerImpl());
    initNative(); // starts the event queue, make sure to register all state/error listeners before
    long wid =0;
    if (stage != null) {
      try {
        wid = Utils.getHWnd(stage);
      } catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
        logger.error("Error getting window handle: " + e.getMessage());
      }
    }
    long[] newNativeMediaRef = new long[1];
    int rc = mpvInitPlayer(newNativeMediaRef, mediaPath, wid);
    if (0 != rc) {
      throwMediaErrorException(rc, null);
    }

    nativeMediaRef = newNativeMediaRef[0];
  }



  @Override
  protected void playerSeekToFrame(int frameNumber) throws MediaException {
    throw new NotImplementedException();
  }

  private class PlayerStateListenerImpl implements PlayerStateListener {

    @Override
    public void onReady(PlayerStateEvent evt) {
      stage.setWidth(getImageWidth());
      stage.setHeight(getImageHeight());
    }

    @Override
    public void onPlaying(PlayerStateEvent evt) {}

    @Override
    public void onPause(PlayerStateEvent evt) {}

    @Override
    public void onStop(PlayerStateEvent evt) {}

    @Override
    public void onStall(PlayerStateEvent evt) {}

    @Override
    public void onFinish(PlayerStateEvent evt) {}

    @Override
    public void onHalt(PlayerStateEvent evt) {}
  }
}
