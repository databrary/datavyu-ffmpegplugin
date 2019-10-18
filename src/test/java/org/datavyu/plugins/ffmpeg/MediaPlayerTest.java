package org.datavyu.plugins.ffmpeg;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.datavyu.plugins.MediaException;
import org.datavyu.plugins.MediaPlayer;
import org.datavyu.plugins.PlaybackRateController;
import org.datavyu.plugins.PlayerStateEvent.PlayerState;

import java.io.File;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MediaPlayerTest {
  /** The LOGGER for this class */
  private static final Logger logger = LogManager.getFormatterLogger(MediaPlayerTest.class);

  /** The total number of seeks that need to be processed during the test * */
  private static final int NUMBER_OF_SEEKS = 10;

  /** The total number of steps that need to be processed during the test * */
  private static final int NUMBER_OF_STEPS = 10;

  /** The start time of the playback rate interval */
  private static final double START_TIME = 0;

  /** The end time in sec for playback rates between 1X and 8X */
  private static final double END_TIME = 20; // 20 sec

  /** The end time in sec for playback rates < 1X */
  private static final double END_TIME_LOW_RATES = 2; // 2 sec

  /** The end time in sec for playback rates > 8X */
  private static final double END_TIME_HIGH_RATES = 190; // 1 m, 30sec

  /** The Seek tolerance in seconds* */
  private static final double SEEK_TOLERANCE_IN_SECONDS = 0.100; // 100 ms

  /** Playback rates duration tolerance */
  private static final double RATES_TOLERANCE_IN_SECONDS = 1;

  interface Builder {
    MediaPlayerSync build();
  }

  static class TimeInterval {

    double start; // start time in sec
    double stop; // stop time in sec

    public TimeInterval(double start, double stop) {
      this.start = start;
      this.stop = stop;
    }
  }

  static final MediaInformation LONG_MEDIA =
      MediaInformation.create(
              "http://www.datavyu.org/releases_pre/resources/counter.mp4",
              0.0,
              600,
              1920,
              1080,
              25)
          .get();

  static final MediaInformation SHORT_MEDIA =
      MediaInformation.create(
              "http://www.datavyu.org/releases_pre/resources/Nature_30fps_1080p.mp4",
              0.0,
              142.13,
              1920,
              1080,
              25)
          .get(); // fail hard if the link is malformed

  static final MediaInformation WRONG_MEDIA =
      new MediaInformation(new File("wrongFileName").toURI(), 0.0, 0.0, 0, 0, 0.0);

  protected void testReadyState(Builder builder, MediaInformation mediaInformation) {
    logger.info("Ready state Test");
    MediaPlayerSync player = builder.build();
    assertTrue(player.getMediaPlayer().getState() == PlayerState.READY);
  }

  protected void testStateTransition(Builder builder, MediaInformation mediaInformation) {
    logger.info("State transitions Test");
    MediaPlayerSync player = builder.build();
    logger.info("Test Ready State");
    assertTrue(player.getMediaPlayer().getState() == PlayerState.READY);

    // Test Seek When Player is Ready
    logger.info("Test Ready State after a seek");
    player.getMediaPlayer().seek(0.0);
    sleep(200);
    assertTrue(player.getMediaPlayer().getState() == PlayerState.READY);

    // Test READY -> PLAYING
    player.getMediaPlayer().play();
    sleep(200);
    logger.info("Test State Transition READY -> PLAYING");
    assertTrue(player.getMediaPlayer().getState() == PlayerState.PLAYING);

    // Test STALLED -> PLAYING
    player.getMediaPlayer().seek(10.0);
    sleep(200);
    logger.info("Test State Transition STALLED -> PLAYING");
    assertTrue(player.getMediaPlayer().getState() == PlayerState.PLAYING);

    // Test PLAYING -> STOPPED
    player.getMediaPlayer().stop();
    sleep(200);
    logger.info("Test State Transition PLAYING -> STOPPED");
    assertTrue(player.getMediaPlayer().getState() == PlayerState.STOPPED);

    // Test STALLED -> STOPPED
    player.getMediaPlayer().seek(10.0);
    sleep(200);
    logger.info("Test State Transition STALLED -> STOPPED");
    assertTrue(player.getMediaPlayer().getState() == PlayerState.STOPPED);

    // Test STOPPED -> PLAYING
    player.getMediaPlayer().play();
    sleep(200);
    logger.info("Test State Transition STOPPED -> PLAYING");
    assertTrue(player.getMediaPlayer().getState() == PlayerState.PLAYING);

    // Test PLAYING -> PAUSED
    player.getMediaPlayer().pause();
    sleep(200);
    logger.info("Test State Transition PLAYING -> PAUSED");
    assertTrue(player.getMediaPlayer().getState() == PlayerState.PAUSED);

    // Test STALLED -> PAUSED
    player.getMediaPlayer().seek(10.0);
    sleep(200);
    logger.info("Test State Transition STALLED -> PAUSED");
    assertTrue(player.getMediaPlayer().getState() == PlayerState.PAUSED);

    // Test PAUSED -> PLAYING
    player.getMediaPlayer().play();
    sleep(200);
    logger.info("Test State Transition PAUSED -> PLAYING");
    assertTrue(player.getMediaPlayer().getState() == PlayerState.PLAYING);

    // Test PAUSED -> STOPPED
    player.getMediaPlayer().pause();
    player.getMediaPlayer().stop();
    sleep(200);
    logger.info("Test State Transition PAUSED -> STOPPED");
    assertTrue(player.getMediaPlayer().getState() == PlayerState.STOPPED);

    // Test STOPPED -> PAUSED
    player.getMediaPlayer().pause();
    sleep(200);
    logger.info("Test State Transition STOPPED -> PAUSED+-");
    assertTrue(player.getMediaPlayer().getState() == PlayerState.PAUSED);
  }

  protected void testMetadata(Builder builder, MediaInformation mediaInformation) {
    logger.info("Meta data Test");
    MediaPlayerSync player = builder.build();
    MediaPlayer mediaPlayer = player.getMediaPlayer();
    assertEquals(mediaPlayer.getDuration(), mediaInformation.getDuration(), 0.01);
    assertEquals(mediaPlayer.getImageWidth(), mediaInformation.getImageWidth());
    assertEquals(mediaPlayer.getImageHeight(), mediaInformation.getImageHeight());
    assertEquals(mediaPlayer.getFps(), mediaInformation.getFramesPerSecond(), 0.01);
  }

  protected void testWrongFile(Builder builder, MediaInformation mediaInformation)
      throws MediaException {
    logger.info("Wrong file Test");
    builder.build();
  }

  protected void testTimeAtStart(Builder builder, MediaInformation mediaInformation) {
    logger.info("Presentation time at launch Test");
    MediaPlayerSync player = builder.build();
    assertNotEquals(player.getMediaPlayer().getPresentationTime(), Double.NaN);
  }

  protected void testSeek(Builder builder, MediaInformation mediaInformation) {
    logger.info("Seek Test");
    MediaPlayerSync player = builder.build();
    double startTime = mediaInformation.getStartTime();
    double duration = mediaInformation.getDuration();
    createSeekTimes(startTime, duration)
        .forEach(
            expectedTime -> {
              player.getMediaPlayer().seek(expectedTime);
              // TODO: Need to see how we can remove this sleeping
              sleep(100);
              double actualTime = player.getMediaPlayer().getPresentationTime();
              logger.debug("Seek to " + expectedTime + " s - Actual Time " + actualTime + " s");
              assertEquals(actualTime, expectedTime, SEEK_TOLERANCE_IN_SECONDS);
            });
  }

  protected void testSeekAtStart(Builder builder, MediaInformation mediaInformation) {
    logger.info("Seek at start Test");
    MediaPlayerSync player = builder.build();
    double duration = mediaInformation.getDuration();
    // Get a random time from the second half
    double startTime = duration / 2;
    double seekTime = randomTime(startTime, duration);

    player.getMediaPlayer().seek(seekTime);
    sleep(100);

    double actualTime = player.getMediaPlayer().getPresentationTime();
    assertEquals(actualTime, seekTime, SEEK_TOLERANCE_IN_SECONDS);
  }

  protected void testStepForward(Builder builder, MediaInformation mediaInformation) {
    logger.info("Step forward Test");
    MediaPlayerSync player = builder.build();
    double startTime = mediaInformation.getStartTime();
    double duration = mediaInformation.getDuration();

    double seekTime = (duration - startTime) / 2.0;
    double delta = 1.0 / mediaInformation.getFramesPerSecond();

    // Seek
    player.getMediaPlayer().seek(seekTime);
    sleep(100);

    // We may not seek to the exact time
    double actualTime = player.getMediaPlayer().getPresentationTime();
    assertEquals(actualTime, seekTime, SEEK_TOLERANCE_IN_SECONDS);

    for (int i = 0; i < NUMBER_OF_STEPS; i++) {
      actualTime = player.getMediaPlayer().getPresentationTime();
      player.getMediaPlayer().stepForward();
      sleep(100);
      double afterStepTime = player.getMediaPlayer().getPresentationTime();
      double expectedTime = actualTime + delta;
      logger.info("Step Forward - Actual: " + afterStepTime +  " - Expected: " + expectedTime + " - Diff: " + (Math.abs(expectedTime - afterStepTime)));
      assertTrue(Math.abs(afterStepTime - expectedTime) < delta);
    }
  }

  protected void testStepForwardAtEnd(Builder builder, MediaInformation mediaInformation) {
    logger.info("Step forward at end Test");
    MediaPlayerSync player = builder.build();
    double startTime = mediaInformation.getStartTime();
    double duration = mediaInformation.getDuration();
    // TODO(fraudies): Need to fix seeking to end, then remove -1
    double seekTime = startTime + duration;
    double delta = 1.0 / mediaInformation.getFramesPerSecond();

    player.getMediaPlayer().seek(seekTime);
    sleep(300);
    double actualTime = player.getMediaPlayer().getPresentationTime();
    logger.info("Seek to the end of the stream - Time: " + actualTime + " - Duration: " +player.getMediaPlayer().getDuration());
    assertEquals(actualTime, player.getMediaPlayer().getDuration(), SEEK_TOLERANCE_IN_SECONDS);

    player.getMediaPlayer().stepForward();
    sleep(100);
    actualTime = player.getMediaPlayer().getPresentationTime();
    assertEquals(actualTime, player.getMediaPlayer().getDuration());
  }

  protected void testStepBackward(Builder builder, MediaInformation mediaInformation) {
    logger.info("Step backward Test");
    MediaPlayerSync player = builder.build();
    double startTime = mediaInformation.getStartTime();
    double duration = mediaInformation.getDuration();

    double seekTime = (duration - startTime) / 2.0;
    double delta = 1.0 / mediaInformation.getFramesPerSecond();

    // Seek
    player.getMediaPlayer().seek(seekTime);
    sleep(100);

    // We may not seek to the exact time
    double actualTime = player.getMediaPlayer().getPresentationTime();
    assertEquals(actualTime, seekTime, SEEK_TOLERANCE_IN_SECONDS);

    for (int i = 0; i < NUMBER_OF_STEPS; i++) {

      actualTime = player.getMediaPlayer().getPresentationTime();
      player.getMediaPlayer().stepBackward();
      sleep(100);
      double afterStepTime = player.getMediaPlayer().getPresentationTime();
      double expectedTime = actualTime - delta;
      logger.info("Step Backward - Actual: " + afterStepTime +  " - Expected: " + expectedTime + " - Diff: " + (Math.abs(expectedTime - afterStepTime)));
      assertTrue(Math.abs(afterStepTime - expectedTime) < delta);
    }
  }

  protected void testStepBackwardAtStart(Builder builder, MediaInformation mediaInformation) {
    logger.info("Step backward at start Test");
    MediaPlayerSync player = builder.build();
    double startTime = mediaInformation.getStartTime();
    player.getMediaPlayer().seek(startTime);
    sleep(300);
    double expectedTime = player.getMediaPlayer().getPresentationTime();
    // Assert.assertNotEquals(expectedTime, Double.NaN);

    player.getMediaPlayer().stepBackward();
    double actualTime = player.getMediaPlayer().getPresentationTime();
    assertEquals(actualTime, expectedTime, 0.001);
  }

  protected void testRates(Builder builder, MediaInformation mediaInformation) {
    logger.info("Playback speeds Test");
    MediaPlayerSync player = builder.build();
    createRatesIntervals()
        .forEach(
            parameter -> {
              double start = parameter.getKey().start;
              double stop = parameter.getKey().stop;
              double duration = stop - start;
              float rate = parameter.getValue();
              if (player.getMediaPlayer().isRateSupported(rate)) {
                logger.debug(
                    "Start: "
                        + start
                        + " - Stop: "
                        + stop
                        + " - Duration: "
                        + duration
                        + " - Rate "
                        + rate);
                player.getMediaPlayer().setRate(rate);

                double actualDuration = player.playTo(start, stop);
                double expectedDuration = (1 / Math.abs(rate)) * duration;
                logger.debug(
                    "Duration: Actual = " + actualDuration + " - Expected = " + expectedDuration);

                assertEquals(actualDuration, expectedDuration, RATES_TOLERANCE_IN_SECONDS);
              }
            });
  }

  // Create seek times including the upper/lower bound in the stream
  private static List<Double> createSeekTimes(double startTime, double duration) {
    return IntStream.rangeClosed(0, NUMBER_OF_SEEKS - 1)
        .asDoubleStream()
        .map(
            seek -> {
              if (seek == 0) {
                return startTime;
              }
              if (seek == NUMBER_OF_SEEKS - 1) {
                // TODO(fraudies): Remove the -1 once we removed relying on forward playback
                return startTime + duration - 1;
              }
              return randomTime(startTime, duration);
            })
        .boxed()
        .collect(Collectors.toList());
  }

  private static List<Pair<TimeInterval, Float>> createRatesIntervals() {
    return EnumSet.range(
            PlaybackRateController.Rate.PLUS_1_DIV_32, PlaybackRateController.Rate.PLUS_32)
        .stream()
        .map(rate -> new Pair<>(new TimeInterval(START_TIME, rate.getValue() < 1 ? END_TIME_LOW_RATES : rate.getValue() > 8 ? END_TIME_HIGH_RATES : END_TIME), rate.getValue()))
        .collect(Collectors.toList());
  }

  private static double randomTime(double startTime, double duration) {
    return (startTime + (Math.random() * (duration - startTime)));
  }

  private void sleep(final long timeInMillis) {
    try {
      Thread.sleep(timeInMillis);
    } catch (InterruptedException e) {
      /* normal */
    }
  }
}
