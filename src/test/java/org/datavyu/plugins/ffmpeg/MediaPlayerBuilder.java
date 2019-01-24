package org.datavyu.plugins.ffmpeg;

import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.datavyu.plugins.MediaPlayer;
import org.datavyu.plugins.mpv.MpvFxMediaPlayer;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.annotations.DataProvider;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MediaPlayerBuilder {

  /** The LOGGER for this class */
  private static final Logger LOGGER = LogManager.getFormatterLogger(MediaPlayerBuilder.class);

  /** Resource folder for video files used during testing */
  private static final String TEST_RESOURCE_PATH = "src/test/resources";

  private static List<Movie> movieFiles = new ArrayList<>();

  // Data Provider Lists
  private static List<Object[]> playersProviderList = new LinkedList<>();
  private static List<Object[]> mpvProviderList = new LinkedList<>();
  private static List<Object[]> javaProviderList = new LinkedList<>();
  private static List<Object[]> sdlProviderList = new LinkedList<>();
  private static List<Object[]> ffmpegProviderList = new LinkedList<>();
  private static List<Object[]> mpvJavaProviderList = new LinkedList<>();
  private static List<Object[]> mpvSdlProviderList = new LinkedList<>();

  // Data Provider Arrays
  private static Object[][] playersProvider;
  private static Object[][] mpvProvider;
  private static Object[][] javaProvider;
  private static Object[][] sdlProvider;
  private static Object[][] ffmpegProvider;
  private static Object[][] mpvJavaProvider;
  private static Object[][] mpvSdlProvider;

  // This movie is about 30MB has 640 x 360 pixels @ 29.97 fps with 2.5 min play time
  static final String TEST_MOVIE_PATH = "http://www.html5videoplayer.net/videos/toystory.mp4";

  // This movie is 140MB, I use this movie for the PlayBackRate Tests
  static final String TEST_MOVIE_PATH2 =
      "http://www.html5videoplayer.net/videos/big_buck_bunny.mp4";

  public enum PlayerType {
    SDL,
    JAVA_JDIALOG,
    MPV
  }

  public enum Rate {
    X1D32(0.03125f),
    X1D16(0.0625f),
    X1D8(0.125f),
    X1D4(0.25f),
    X1D2(0.5f),
    X1(1f),
    X2(2f),
    X4(4f),
    X8(8f),
    X16(16f),
    X32(32f);

    final float value;

    Rate(float value) {
      this.value = value;
    }
  }

  static {
    // Add all your movies here
    try {
      movieFiles.add(
          new Movie(
              copyToLocal(new URL(TEST_MOVIE_PATH)),
              149.95, // Duration in Seconds
              640, // Width
              360, // Height
              29.97)); // Frame Per Second
      //            movieFiles.add(new Movie(copyToLocal(new URL(TEST_MOVIE_PATH2)),
      //                    596.5, // Duration in Seconds
      //                    640, // Width
      //                    360, // Height
      //                    24));// Frame Per Second
    } catch (IOException e) {
      e.printStackTrace();
    }
    // build the list of parameters (Movie, PlayerType)  for the data providers
    for (Movie movie : movieFiles) {
      for (PlayerType type : PlayerType.values()) {
        Object[] player = new Object[] {movie, type};
        if (type == PlayerType.SDL) {
          sdlProviderList.add(player);
          mpvSdlProviderList.add(player);
          ffmpegProviderList.add(player);
        }
        if (type == PlayerType.JAVA_JDIALOG) {
          javaProviderList.add(player);
          ffmpegProviderList.add(player);
          mpvJavaProviderList.add(player);
        }
        if (type == PlayerType.MPV) {
          mpvProviderList.add(player);
          mpvJavaProviderList.add(player);
          mpvSdlProviderList.add(player);
        }
        playersProviderList.add(player);
      }
    }
    playersProvider = playersProviderList.stream().toArray(Object[][]::new);
    ffmpegProvider = ffmpegProviderList.stream().toArray(Object[][]::new);
    mpvJavaProvider = mpvJavaProviderList.stream().toArray(Object[][]::new);
    mpvSdlProvider = mpvSdlProviderList.stream().toArray(Object[][]::new);
    mpvProvider = mpvProviderList.stream().toArray(Object[][]::new);
    sdlProvider = sdlProviderList.stream().toArray(Object[][]::new);
    javaProvider = javaProviderList.stream().toArray(Object[][]::new);
  }

  @DataProvider(name = "wrongFile")
  public static Object[][] getWrongFileProvider(Method method, ITestContext context) {
    List<ITestNGMethod> testMethods = context.getSuite().getAllMethods();
    String testName = testMethods.get(0).getTestClass().getName();
    String methodName = method.getName();
    if (testName.contains("TestMediaPlayerData") && methodName.equals("testUnknownFile")) {
      //            return new Object[][]{{new Movie("wrongFileName.mp4"), PlayerType.MPV},
      //                    {new Movie("wrongFileName.mp4"), PlayerType.JAVA_JDIALOG},
      //                    {new Movie("wrongFileName.mp4"), PlayerType.SDL}};
      // Temporary SDL is crashing the JVM
      return new Object[][] {
        {new Movie("wrongFileName.mp4"), PlayerType.MPV},
        {new Movie("wrongFileName.mp4"), PlayerType.JAVA_JDIALOG}
      };
    }
    return null;
  }

  @DataProvider(name = "players")
  public static Object[][] getPlayers(Method method, ITestContext context) {
    List<ITestNGMethod> testMethods = context.getSuite().getAllMethods();
    String testName = testMethods.get(0).getTestClass().getName();

    if (testName.contains("TestPlaybackRate")) {
      return mpvProvider;
    } else if (testName.contains("TestMediaPlayerData")) {
      return mpvProvider;
    }

    return playersProvider;
  }

  static MediaPlayer build(String movieFile, PlayerType type) {
    URI movie = new File(movieFile).toURI();
    switch (type) {
      case SDL:
        return new FfmpegSdlMediaPlayer(movie);
      case JAVA_JDIALOG:
        return new FfmpegJavaMediaPlayer(movie, new JDialog());
      case MPV:
        return new MpvFxMediaPlayer(movie, new Stage());
      default:
        throw new IllegalArgumentException("Could not build player for type " + type);
    }
  }

  static class Movie {
    String path;
    int width, height;
    double duration, fps; // duration in Seconds

    Movie(
        final String path,
        final double duration,
        final int width,
        final int height,
        final double fps) {
      this.path = path;
      this.duration = duration;
      this.width = width;
      this.height = height;
      this.fps = fps;
    }

    Movie(final String path) {
      this(path, 0, 0, 0, 0);
    }
  }

  static class TimeInterval {

    double start; // start time in sec
    double stop; // stop time in sec

    public TimeInterval(double start, double stop) {
      this.start = start;
      this.stop = stop;
    }
  }

  /**
   * Copies the resource from the URL url to a local resource directory and returns while preserving
   * the original file name
   *
   * @param url The URL to the resource
   * @return The file path to the copied resource locally
   * @throws IOException if the directory can't be created or the resource can't be downloaded
   */
  private static String copyToLocal(URL url) throws IOException {

    // Construct the resource directory in the temporary files
    File resourceDir = new File(TEST_RESOURCE_PATH);
    FileUtils.forceMkdir(resourceDir);

    // Get the file name
    String fileName = new File(url.toString()).getName();

    // Define the output file url
    File outPath = new File(resourceDir, fileName);

    // If the file does not exist yet copy it from the www
    if (!outPath.exists()) {
      FileUtils.copyURLToFile(url, outPath);
      LOGGER.info("Copied resource from " + url + " to " + outPath);
    } else {
      LOGGER.info("Found existing resource " + outPath);
    }
    return outPath.getAbsolutePath();
  }
}
