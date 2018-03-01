package org.datavyu.benchmark;

import javafx.util.Pair;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.core.config.Configurator;
import org.datavyu.plugins.ffmpegplayer.AudioSoundStreamListener;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.sound.sampled.AudioFormat;
import java.awt.color.ColorSpace;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TestPlaybackRateScale {

    /** The logger for this class */
    private static Logger logger = LogManager.getFormatterLogger(TestPlaybackRateScale.class);

    /** Resource folder for video files used during testing */
    private static final String TEST_RESOURCE_PATH = "test/resources";

    // This movie is about 30MB has 640 x 360 pixels @ 29.97 fps with 2.5 min play time
    private static final String TEST_MOVIE_PATH = "http://www.html5videoplayer.net/videos/toystory.mp4";

    /** The default color space used for testing */
    private static final ColorSpace DEFAULT_COLOR_SPACE = ColorSpace.getInstance(ColorSpace.CS_sRGB);

    /** The default audio format used for testing */
    private static final AudioFormat DEFAULT_AUDIO_FORMAT = AudioSoundStreamListener.getNewMonoFormat();

    private static final float LOWER_PERCENTAGE = -10f; // -10 percent

    private static final float UPPER_PERCENTAGE = +10f; // +10 percent

    private static final double MEASUREMENT_DURATION_IN_SEC = 10; // 10 seconds

    private static final double START_TIME_IN_SEC = 10 * MEASUREMENT_DURATION_IN_SEC; // start time

    private static final double TO_MILLI = 1000;

    private static final double TO_PERCENT = 100;

    /**
     * Copies the resource from the URL url to a local temporary resource directory and returns while preserving the
     * original file name
     *
     * @param url The URL to the resource
     *
     * @return The file path to the copied resource locally
     *
     * @throws IOException if the directory can't be created or the resource can't be downloaded
     */
    private static String copyToLocalTmp(URL url) throws IOException {

        // Construct the resource directory in the temporary files
        File resourceDir = new File(System.getProperty("java.io.tmpdir"), TEST_RESOURCE_PATH);
        FileUtils.forceMkdir(resourceDir);

        // Get the file name
        String fileName = new File(url.toString()).getName();

        // Define the output file url
        File outPath = new File(resourceDir, fileName);

        // If the file does not exist yet copy it from the www
        if (!outPath.exists()) {
            FileUtils.copyURLToFile(url, outPath);
            logger.info("Copied resource from " + url + " to " + outPath);
        } else {
            logger.info("Found existing resource " + outPath);
        }
        return outPath.getAbsolutePath();
    }

    // A list of all movie files to test
    private List<String> movieFiles = new ArrayList<>();

    private List<MoviePlayer.PlayerType> moviePlayerTypes = new ArrayList<MoviePlayer.PlayerType>(){{
        //add(MoviePlayer.PlayerType.AWT_TYPE);
        // TODO: Fix the JFX type player
        add(MoviePlayer.PlayerType.JFX_TYPE);
    }};

    private List<Pair<Float, Float>> offsetAndRates = new ArrayList<Pair<Float, Float>>(){{
        // TODO: Work on correct conditions for reverse playback
        //add(new Pair<>(2f*60, -1f));
        //add(new Pair<>(0f, 4f));
    }};

    private List<Float> playbackScale = new ArrayList<Float>() {{
        //add(.25f);
        add(2f);
    }};

    private static double computeDiffInPercent(double isTime, double beTime) {
        return (beTime - isTime)/beTime * TO_PERCENT;
    }

    @BeforeMethod
    public void setup() throws IOException {
        Configurator.setRootLevel(Level.INFO);
        movieFiles.add(copyToLocalTmp(new URL(TEST_MOVIE_PATH)));
    }

    @AfterMethod
    public void cleanup() {
        movieFiles.clear();
    }

    @Test
    public void testRate() throws IOException {
        for (String movieFile : movieFiles) {
            for (MoviePlayer.PlayerType playerType: moviePlayerTypes) {
                for (Pair<Float, Float> offsetAndRate : offsetAndRates) {
                    float offset = offsetAndRate.getKey();
                    float rate = offsetAndRate.getValue();

                    System.out.println("Testing playback with rate: " + rate + " and offset: " + offset);

                    MoviePlayer moviePlayer = MoviePlayer.createMoviePlayer(playerType,
                            DEFAULT_COLOR_SPACE, DEFAULT_AUDIO_FORMAT);
                    moviePlayer.openFile(movieFile, "test-version");

                    // Set time and rate
                    moviePlayer.setTimeInSeconds(offset);
                    moviePlayer.setRate(rate);
                    moviePlayer.start();
                    try {
                        Thread.sleep((long) (MEASUREMENT_DURATION_IN_SEC * TO_MILLI));
                    } catch (InterruptedException ie) {
                        logger.info("Finished measurement for " + moviePlayer.getClass());
                    }
                    double diffInPercent = computeDiffInPercent(
                            moviePlayer.getTimeInSeconds() - offset,
                            Math.abs(rate) * MEASUREMENT_DURATION_IN_SEC);
                    moviePlayer.stop();
                    moviePlayer.closeFile();

                    //logger.info("Measured difference " + diffInPercent + " percent");
                    System.out.println("Measured difference " + diffInPercent + " percent");

                    // Check that the difference is within bounds
                    assert (diffInPercent > LOWER_PERCENTAGE && diffInPercent < UPPER_PERCENTAGE);
                }
            }
        }
    }

    @Test
    public void testScale() throws IOException {
        for (String movieFile : movieFiles) {
            for (MoviePlayer.PlayerType playerType: moviePlayerTypes) {
                for (float scale : playbackScale) {
                    System.out.println("Testing playback with scale: " + scale);

                    MoviePlayer moviePlayer = MoviePlayer.createMoviePlayer(playerType,
                            DEFAULT_COLOR_SPACE, AudioSoundStreamListener.getNewMonoFormat());
                    moviePlayer.openFile(movieFile, "test-version");
                    moviePlayer.setScale(scale);

                    moviePlayer.setTimeInSeconds(START_TIME_IN_SEC);
                    moviePlayer.start();
                    try {
                        Thread.sleep((long) (MEASUREMENT_DURATION_IN_SEC * TO_MILLI));
                    } catch (InterruptedException ie) {
                        logger.info("Finished measurement for " + moviePlayer.getClass());
                    }
                    double diffInPercent = computeDiffInPercent(
                            moviePlayer.getTimeInSeconds() - START_TIME_IN_SEC,
                            MEASUREMENT_DURATION_IN_SEC);
                    moviePlayer.stop();
                    moviePlayer.closeFile();

                    logger.info("Measured difference " + diffInPercent + " percent");
                    System.out.println("Measured difference " + diffInPercent + " percent");

                    // Check that the difference is within bounds
                    assert (diffInPercent > LOWER_PERCENTAGE && diffInPercent < UPPER_PERCENTAGE);
                }
            }
        }
    }
}
