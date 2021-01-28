package org.datavyu.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Utils {
  private static final Logger logger = LogManager.getFormatterLogger(Utils.class);
  public static final char SEPARATOR;
  public static final boolean isMac;
  public static final boolean isWindows;

  static {
    // Look for Platform
    String OS = System.getProperty("os.name").toLowerCase();
    isMac = OS.contains("mac");
    isWindows = OS.contains("win");
    logger.info("Platform detected : " + (isMac ? "Mac OS" : "Windows"));

    SEPARATOR = isMac ? '.' : '-';
  }
}
