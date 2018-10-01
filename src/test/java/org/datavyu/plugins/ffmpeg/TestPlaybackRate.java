package org.datavyu.plugins.ffmpeg;

import javafx.util.Pair;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.core.config.Configurator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TestPlaybackRate {

    /** The LOGGER for this class */
    private final static Logger LOGGER = LogManager.getFormatterLogger(TestPlaybackRate.class);

    /** Resource folder for video files used during testing */
    private static final String TEST_RESOURCE_PATH = "test/resources";

    // This movie is about 30MB has 640 x 360 pixels @ 29.97 fps with 2.5 min play time
    private static final String TEST_MOVIE_PATH = "http://www.html5videoplayer.net/videos/toystory.mp4";

    private static final float LOWER_PERCENTAGE = -2f; // -2 percent

    private static final float UPPER_PERCENTAGE = +2f; // +2 percent

    private static final double TO_MILLI = 1000;

    private static final double TO_PERCENT = 100;

    private final class TimeInterval {

        double start; // start time in sec
        double stop; // stop time in sec

        public TimeInterval(double start, double stop) {
            this.start = start;
            this.stop = stop;
        }
    }

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
            LOGGER.info("Copied resource from " + url + " to " + outPath);
        } else {
            LOGGER.info("Found existing resource " + outPath);
        }
        return outPath.getAbsolutePath();
    }

    // A list of all movie files to test
    private List<String> movieFiles = new ArrayList<>();

    private List<MediaPlayerBuilder.PlayerType> moviePlayerTypes = new ArrayList<MediaPlayerBuilder.PlayerType>(){{
        //add(MediaPlayerBuilder.PlayerType.SDL);
        add(MediaPlayerBuilder.PlayerType.JAVA_JDIALOG);
    }};

    private List<Pair<TimeInterval, Float>> parameters = new ArrayList<Pair<TimeInterval, Float>>(){{
        add(new Pair<>(new TimeInterval(0, 20), 0.5f));
        add(new Pair<>(new TimeInterval(0, 20), 1f));
        add(new Pair<>(new TimeInterval(0, 20), 2f));
        add(new Pair<>(new TimeInterval(0, 20), 4f));
    }};

    private static double diffInPercent(double actual, double expected) {
        return (expected - actual)/expected * TO_PERCENT;
    }

    @BeforeMethod
    public void setup() throws IOException {
        Configurator.setRootLevel(Level.INFO);
        //movieFiles.add(copyToLocalTmp(new URL(TEST_MOVIE_PATH)));
        movieFiles.add("C:\\Users\\Florian\\Nature_30fps_1080p.mp4");
    }

    @Test
    public void testRates() {
        long startTime;
        for (String movieFile : movieFiles) {
            for (MediaPlayerBuilder.PlayerType playerType: moviePlayerTypes) {
                for (Pair<TimeInterval, Float> parameter : parameters) {
                    double start = parameter.getKey().start;
                    double stop = parameter.getKey().stop;
                    double duration = stop - start;
                    float rate = parameter.getValue();

                    LOGGER.info("Test rate: " + rate + " with start: " + start + " sec and end " + stop + " sec");

                    MediaPlayer mediaPlayer = MediaPlayerBuilder.build(movieFile, playerType);

                    // Set time and rate
                    startTime = System.nanoTime();
                    mediaPlayer.init();
                    LOGGER.info("The frame rate is: " + mediaPlayer.getFps() + " Hz");
                    LOGGER.info("Initializing the player took: "
                            + (System.nanoTime() - startTime)/1e6 + " ms");

                    mediaPlayer.setStartTime(start);
                    startTime = System.nanoTime();
                    mediaPlayer.setRate(rate);
                    mediaPlayer.play();
                    LOGGER.info("Setting the rate and starting the player took: "
                            + (System.nanoTime() - startTime)/1e6 + " ms");
                    try {
                        Thread.sleep((long) (duration * TO_MILLI));
                    } catch (InterruptedException ie) { }
                    double actualDuration = mediaPlayer.getPresentationTime() - start;
                    double expectedDuration = Math.abs(rate) * duration;
                    LOGGER.info("Measured: " + actualDuration + " sec; Expected: " + expectedDuration + " sec");

                    double diffInPercent = diffInPercent(actualDuration, expectedDuration);
                    mediaPlayer.stop();
                    mediaPlayer.dispose();

                    System.out.println("Measured difference " + diffInPercent + " percent");

                    // Check that the difference is within bounds
                    assert (diffInPercent > LOWER_PERCENTAGE && diffInPercent < UPPER_PERCENTAGE);
                }
            }
        }
    }
}
