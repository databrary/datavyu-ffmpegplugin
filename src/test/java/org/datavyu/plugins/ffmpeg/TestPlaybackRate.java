package org.datavyu.plugins.ffmpeg;

import javafx.util.Pair;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import org.datavyu.plugins.MediaPlayer;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.datavyu.plugins.ffmpeg.MediaPlayerBuilder.*;

public class TestPlaybackRate {

  /** The LOGGER for this class */
  private static final Logger LOGGER = LogManager.getFormatterLogger(TestPlaybackRate.class);

  private static final float LOWER_PERCENTAGE = -5f; // -5 percent

  private static final float UPPER_PERCENTAGE = +5f; // +5 percent

  private static final double TO_MILLI = 1000;

  private static final double TO_PERCENT = 100;

  private static final double START_TIME = 0;

  private static final double END_TIME = 20;

  private List<Pair<TimeInterval, Float>> parameters = new ArrayList<Pair<TimeInterval, Float>>();

  private static double diffInPercent(double actual, double expected) {
    return (expected - actual) / expected * TO_PERCENT;
  }

  @BeforeTest
  public void setup() {
    Configurator.setRootLevel(Level.INFO);
    for (Rate rate : Rate.values()) {
      parameters.add(new Pair<>(new TimeInterval(START_TIME, END_TIME), rate.value));
    }
  }

  @Test(
      dataProvider = "players",
      dataProviderClass = MediaPlayerBuilder.class,
      groups = {"ALL", "playbackRateGroup"})
  public void testRates(Movie movie, PlayerType playerType) {
    long startTime;
    for (Pair<TimeInterval, Float> parameter : parameters) {
      double start = parameter.getKey().start;
      double stop = parameter.getKey().stop;
      double duration = stop - start;
      float rate = parameter.getValue();

      LOGGER.info("Test rate: " + rate + " with start: " + start + " sec and end " + stop + " sec");

      MediaPlayer mediaPlayer = MediaPlayerBuilder.build(movie.path, playerType);

      // Set time and rate
      startTime = System.nanoTime();
      mediaPlayer.init();
      LOGGER.info("The frame rate is: " + mediaPlayer.getFps() + " Hz");
      LOGGER.info("Initializing the player took: " + (System.nanoTime() - startTime) / 1e6 + " ms");

      mediaPlayer.setStartTime(start);
      startTime = System.nanoTime();
      mediaPlayer.setRate(rate);
      mediaPlayer.play();
      LOGGER.info(
          "Setting the rate and starting the player took: "
              + (System.nanoTime() - startTime) / 1e6
              + " ms");
      try {
        Thread.sleep((long) (duration * TO_MILLI));
      } catch (InterruptedException ie) {
      }
      double actualDuration = mediaPlayer.getPresentationTime() - start;
      double expectedDuration = Math.abs(rate) * duration;
      LOGGER.info("Measured: " + actualDuration + " sec; Expected: " + expectedDuration + " sec");

      double diffInPercent = diffInPercent(actualDuration, expectedDuration);
      mediaPlayer.stop();
      mediaPlayer.dispose();

      System.out.println("Measured difference " + diffInPercent + " percent");

      // Check that the difference is within bounds
      Assert.assertTrue(diffInPercent > LOWER_PERCENTAGE && diffInPercent < UPPER_PERCENTAGE);
    }
  }
}
