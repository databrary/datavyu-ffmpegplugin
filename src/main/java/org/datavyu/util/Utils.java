package org.datavyu.util;

import com.sun.javafx.tk.TKStage;
import java.awt.Container;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.datavyu.plugins.mpv.MpvFxMediaPlayer;

public class Utils {
  private static final Logger logger = LogManager.getFormatterLogger(MpvFxMediaPlayer.class);
  private static final int JAVA_9 = 9;

  public static long getConateinerId(Container container)
      throws InvocationTargetException, IllegalAccessException, IllegalArgumentException {

    if (!container.isVisible()) {
      container.setVisible(true);
    }
    // The reflection code below does the same as this
    // long handle = container.getPeer() != null ? ((WComponentPeer) container.getPeer()).getHWnd() : 0;

    Object wComponentPeer = invokeMethod(container, "getPeer");
    Long hwnd = (Long) invokeMethod(wComponentPeer, "getHWnd");
    if (hwnd == 0) {
      new IllegalArgumentException("Window ID Invalid");
    }
    return hwnd;
  }

  private static Object invokeMethod(Object o, String methodName)
      throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    Class c = o.getClass();
    for (Method m : c.getMethods()) {
      if (m.getName().equals(methodName)) {
        Object ret = m.invoke(o);
        return ret;
      }
    }
    throw new RuntimeException("Could not find method named '"+methodName+"' on class " + c);
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
  public static long getStageId(Stage stage) {
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
      logger.error("Unable to retrieve window id, due to Error: " + ex);
      throw new RuntimeException("Unable to retrieve window id");
    }
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
}
