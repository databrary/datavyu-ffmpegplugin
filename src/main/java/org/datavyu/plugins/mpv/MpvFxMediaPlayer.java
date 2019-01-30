package org.datavyu.plugins.mpv;

import com.sun.javafx.tk.TKStage;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.datavyu.plugins.PlayerStateEvent;
import org.datavyu.plugins.PlayerStateListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;

public final class MpvFxMediaPlayer extends MpvMediaPlayer {
  private Stage stage;
  private static final Logger LOGGER = LogManager.getFormatterLogger(MpvMediaPlayer.class);
  private static final int JAVA_9 = 9;

  public MpvFxMediaPlayer(URI mediaPath, Stage stage) {
    super(mediaPath);
    this.stage = stage;
  }

  @Override
  public void init() {
    addMediaPlayerStateListener(new PlayerStateListenerImpl());
    initNative(); // starts the event queue, make sure to register all state/error listeners before

    stage.show();

    long[] newNativeMediaRef = new long[1];
    int rc = mpvInitPlayer(newNativeMediaRef, mediaPath, getWindowId(stage));
    if (0 != rc) {
      throwMediaErrorException(rc, null);
    }

    nativeMediaRef = newNativeMediaRef[0];
  }

  /**
   * Get the minor version of the java version triplet
   *
   * @return The minor version
   */
  private static int getMinorJavaVersion() {
    String version = System.getProperty("java.version");
    String[] triplet = version.split("\\.");
    String minor = triplet[1];
    return Integer.parseInt(minor);
  }

  /**
   * Get the window id for a stage
   *
   * <p>This seems only to work on windows
   *
   * <p>Code adopted from https://github.com/java-native-access/jna/issues/706
   *
   * @param stage The stage
   * @return The window id
   * @throws RuntimeException if the window id can't be retrieved
   */
  private static long getWindowId(Stage stage) {
    int minor = getMinorJavaVersion();
    Class<?> stageClazz = stage.getClass();
    try {
      Method tkStageGetter =
          minor == JAVA_9
              ? stageClazz.getSuperclass().getDeclaredMethod("getPeer")
              : stageClazz.getMethod("impl_getPeer");
      tkStageGetter.setAccessible(true);
      TKStage tkStage = (TKStage) tkStageGetter.invoke(stage);

      Method getPlatformWindow = tkStage.getClass().getDeclaredMethod("getPlatformWindow");
      getPlatformWindow.setAccessible(true);
      Object platformWindow = getPlatformWindow.invoke(tkStage);

      Method getNativeHandle = platformWindow.getClass().getMethod("getNativeHandle");
      getNativeHandle.setAccessible(true);

      Object nativeHandle = getNativeHandle.invoke(platformWindow);
      return (long) nativeHandle;
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
      LOGGER.error("Unable to retrieve window id, due to Error: " + ex);
      throw new RuntimeException("Unable to retrieve window id");
    }
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
