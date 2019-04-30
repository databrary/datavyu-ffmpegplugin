package org.datavyu.util;

import com.sun.javafx.tk.TKStage;
import java.awt.Container;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Utils {
  private static final Logger logger = LogManager.getFormatterLogger(Utils.class);
  private static final int JAVA_VERSION;
  private static final boolean isMac;
  private static final boolean isWindows;

  static {
    // Look for Platform
    String OS = System.getProperty("os.name").toLowerCase();
    isMac = OS.contains("mac");
    isWindows = OS.contains("win");

    // Look for Java version
    Pattern p = Pattern.compile("^(?:1[.])?([1-9][0-9]*)[.-]");
    Matcher m = p.matcher(System.getProperty("java.version"));

    if (!m.find()) {
      throw new IllegalStateException("Failed to parse java.version");
    }

    JAVA_VERSION = Integer.parseInt(m.group(1));
  }

  public static long getHWnd(Object obj) throws InvocationTargetException, IllegalAccessException {
    if (obj instanceof Container) {
      return getContainerHWnd((Container) obj);
    }
    if (obj instanceof Stage) {
      return getStageHWnd((Stage) obj);
    }
    return 0;
  }

  static long getContainerHWnd(Container container)
      throws InvocationTargetException, IllegalAccessException, IllegalArgumentException {

    if (!container.isVisible()) {
      container.setVisible(true);
    }
    // The reflection code below does the same as this
    // long handle = container.getPeer() != null ? ((WComponentPeer) container.getPeer()).getHWnd()
    // : 0;

    Long hwnd = Long.valueOf(0);
    if (isWindows) {
      Object wComponentPeer = invokeMethod(container, "getPeer");
      hwnd = (Long) invokeMethod(wComponentPeer, "getHWnd");
    } else if (isMac) {
      throw new NotImplementedException();
    }
    if (hwnd == 0) {
      new IllegalArgumentException("Unable to retrieve Container Handler");
    }
    return hwnd;
  }

  /**
   * Get the window id for a stage
   *
   * <p>This seems only to work on windows
   *
   * <p>Code adopted from https://github.com/java-native-access/jna/issues/706
   *
   * @param stage The stage
   * @return The window Hwnd
   * @throws RuntimeException if the window id can't be retrieved
   */
  static long getStageHWnd(Stage stage) {
    Class<?> stageClazz = stage.getClass();
    try {
      Method tkStageGetter =
          JAVA_VERSION == 9
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

  static Object invokeMethod(Object o, String methodName)
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
}
