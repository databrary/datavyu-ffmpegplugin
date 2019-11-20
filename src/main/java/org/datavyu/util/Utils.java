package org.datavyu.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Utils {
  private static final Logger logger = LogManager.getFormatterLogger(Utils.class);
  public static final int JAVA_VERSION;
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

    // Look for Java version
    Pattern p = Pattern.compile("^(?:1[.])?([1-9][0-9]*)[.-]");
    Matcher m = p.matcher(System.getProperty("java.version"));

    if (!m.find()) {
      throw new IllegalStateException("Failed to parse java.version");
    }

    JAVA_VERSION = Integer.parseInt(m.group(1));
    logger.info("Java Version detected : " + JAVA_VERSION);
  }
}
