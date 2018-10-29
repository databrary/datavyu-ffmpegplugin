package org.datavyu.plugins.ffmpeg;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TestMediaPlayerData {

    /** The LOGGER for this class */
    private final static Logger LOGGER = LogManager.getFormatterLogger(TestPlaybackRate.class);

    /** Resource folder for video files used during testing */
    private static final String TEST_RESOURCE_PATH = "test/resources";

    /** The total number of seeks that need to be processed during the test **/
    private static final int NUMBER_OF_SEEK = 100;

    // This movie is about 30MB has 640 x 360 pixels @ 29.97 fps with 2.5 min play time
    private static final String TEST_MOVIE_PATH = "http://www.html5videoplayer.net/videos/toystory.mp4";

    private static final long TIMEOUT_MILLIS = 1000; // 1sec

    // A list of all movie files to test
    private List<Movie> movieFiles = new ArrayList<>();

    private List<MediaPlayerBuilder.PlayerType> moviePlayerTypes = new ArrayList<MediaPlayerBuilder.PlayerType>(){{
        add(MediaPlayerBuilder.PlayerType.SDL);
        add(MediaPlayerBuilder.PlayerType.JAVA_JDIALOG);
        add(MediaPlayerBuilder.PlayerType.MPV);
    }};

    private class Movie {
        String path;
        // duration in Seconds
        int width, height;
        double duration,fps;

        Movie(final String path,
              final double duration,
              final int width,
              final int height,
              final double fps){
            this.path = path;
            this.duration = duration;
            this.width = width;
            this.height = height;
            this.fps = fps;
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

    @BeforeTest
    public void setup() throws IOException {
        // TODO(Reda) add url for a movie path
        Configurator.setRootLevel(Level.INFO);
//        movieFiles.add(copyToLocalTmp(new URL(TEST_MOVIE_PATH)));
        movieFiles.add(new Movie("C:\\Users\\DatavyuTests\\Documents\\Resources\\Videos\\Test1080p.mp4",
                142.107, // Duration in Seconds
                1920, // Width
                1080, // Height
                25 ));// Frame Per Second
    }

    @Test
    public void testInitDispose(){
        // Creating two instance of the SDL player crashes the JVM when trying to
        // Dispose the second player
        //TODO(Reda): Fix SDL Dispose
        //TODO(Reda) Add safeguard to avoid any action after a dispose
        for (Movie movieFile : movieFiles) {
            for (MediaPlayerBuilder.PlayerType playerType: moviePlayerTypes) {
                MediaPlayer mediaPlayer = MediaPlayerBuilder.build(movieFile.path, playerType);
                LOGGER.info("Initializing the " + playerType + " player with the "+movieFile.path+ " stream");
                mediaPlayer.init();
                // wait 1 Sec
                LOGGER.info("Wait for the " + playerType + " player to be READY");
                try {
                    Thread.sleep(TIMEOUT_MILLIS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                LOGGER.info("Checking the " + playerType + " player state");
                Assert.assertEquals(mediaPlayer.getState(), PlayerStateEvent.PlayerState.READY);
                LOGGER.info("Dispose the " + playerType + " player");
                mediaPlayer.dispose();
//                mediaPlayer.play() // Calling player after dispose crashes the JVM
            }
        }
    }

    @Test
    public void testPlayStop(){
        //TODO(Reda): find a better way to be notified when an event occurs
        // Need to fix SDL player bug
        for (Movie movieFile : movieFiles) {
            for (MediaPlayerBuilder.PlayerType playerType : moviePlayerTypes) {
                MediaPlayer mediaPlayer = MediaPlayerBuilder.build(movieFile.path, playerType);

                LOGGER.info("Initializing the " + playerType + " player with the "+movieFile.path+ " stream");
                mediaPlayer.init();
                LOGGER.info("Wait for the " + playerType + " player to be READY");
                try {
                    Thread.sleep(TIMEOUT_MILLIS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Need to wait for the Ready state otherwise getPresentationTime will return NaN
                double startTime = mediaPlayer.getPresentationTime();
                LOGGER.info("Start the " + playerType + " player");
                mediaPlayer.play();
                LOGGER.info("Wait for the " + playerType + " player to change its state");
                try {
                    Thread.sleep(TIMEOUT_MILLIS * 10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                double now = mediaPlayer.getPresentationTime();

                Assert.assertEquals(mediaPlayer.getState(), PlayerStateEvent.PlayerState.PLAYING);
                Assert.assertTrue(now > startTime);

                LOGGER.info("Stop the " + playerType + " player");
                mediaPlayer.stop();
                double endTime = mediaPlayer.getPresentationTime();
                LOGGER.info("Wait for the " + playerType + " player to change its state");
                try {
                    Thread.sleep(TIMEOUT_MILLIS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                now = mediaPlayer.getPresentationTime();

                Assert.assertEquals(mediaPlayer.getState(), PlayerStateEvent.PlayerState.STOPPED);
                Assert.assertEquals(now, endTime);

                LOGGER.info("Dispose the " + playerType + " player");
                mediaPlayer.dispose();
            }
        }
    }

    @Test
    public void testTogglePause(){
        // TODO(Reda): MPV Player fail the test moving from PLAYING to PAUSED
        for (Movie movieFile : movieFiles) {
            for (MediaPlayerBuilder.PlayerType playerType : moviePlayerTypes) {
                MediaPlayer mediaPlayer = MediaPlayerBuilder.build(movieFile.path, playerType);

                LOGGER.info("Initializing the " + playerType + " player with the "+movieFile.path+ " stream");
                mediaPlayer.init();

                LOGGER.info("Start the " + playerType + " player");
                mediaPlayer.play();

                LOGGER.info("Wait for the " + playerType + " player to play");
                try {
                    Thread.sleep(TIMEOUT_MILLIS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                double pauseTime = mediaPlayer.getPresentationTime();
                LOGGER.info("Pause the " + playerType + " player");
                mediaPlayer.pause();
                LOGGER.info("Wait for the " + playerType + " player to change its state");
                try {
                    Thread.sleep(TIMEOUT_MILLIS*3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                double now = mediaPlayer.getPresentationTime();

                Assert.assertEquals(mediaPlayer.getState(), PlayerStateEvent.PlayerState.PAUSED);
                Assert.assertEquals(now, pauseTime);


                LOGGER.info("Un-Pause the " + playerType + " player");
                double startTime = now;
                mediaPlayer.pause();
                LOGGER.info("Wait for the " + playerType + " player to change its state");
                try {
                    Thread.sleep(TIMEOUT_MILLIS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                now = mediaPlayer.getPresentationTime();

                Assert.assertEquals(mediaPlayer.getState(), PlayerStateEvent.PlayerState.PLAYING);
                Assert.assertTrue(now > startTime);

                LOGGER.info("Dispose the " + playerType + " player");
                mediaPlayer.dispose();
            }
        }
    }

    @Test
    public void testSetGetRate(){
        // TODO(Reda): Change SDL/JAVA player to 1x after a stop (MPV is fine coded in the native side)
        // TODO(Reda): Test for negative values
        for (Movie movieFile : movieFiles) {
            for (MediaPlayerBuilder.PlayerType playerType : moviePlayerTypes) {
                MediaPlayer mediaPlayer = MediaPlayerBuilder.build(movieFile.path, playerType);
                LOGGER.info("Initializing the " + playerType + " player with the "+movieFile.path+ " stream");
                mediaPlayer.init();
                LOGGER.info("Wait for the " + playerType + " player to change its state");
                try {
                    Thread.sleep(TIMEOUT_MILLIS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Player Rate should always be initialized to 1X
                Assert.assertEquals(mediaPlayer.getRate(), 1.0f);

                LOGGER.info("Start the " + playerType + " player");
                mediaPlayer.play();
                Assert.assertEquals(mediaPlayer.getRate(), 1.0f);
                LOGGER.info("Wait for the " + playerType + " player to play");
                try {
                    Thread.sleep(TIMEOUT_MILLIS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mediaPlayer.setRate(2.0f);
                try {
                    Thread.sleep(TIMEOUT_MILLIS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Assert.assertEquals(mediaPlayer.getRate(), 2.0f);

                // Rate after a pause must stay equal to the rate before the pause action
                mediaPlayer.pause();
                Assert.assertEquals(mediaPlayer.getRate(), 2.0f);

                mediaPlayer.play();
                Assert.assertEquals(mediaPlayer.getRate(), 2.0f);


                // The rate should be set back to 1x when the stream is stopped
                mediaPlayer.stop();
                try {
                    Thread.sleep(TIMEOUT_MILLIS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Assert.assertEquals(mediaPlayer.getRate(), 1.0f);

                mediaPlayer.play();
                Assert.assertEquals(mediaPlayer.getRate(), 1.0f);

                // Players must not be affected by negative values
                mediaPlayer.setRate(-1);
                Assert.assertEquals(mediaPlayer.getRate(), 1.0f);

                LOGGER.info("Dispose the " + playerType + " player");
                mediaPlayer.dispose();
            }
        }
    }

    @Test
    public void testGetFps(){
        for (Movie movieFile : movieFiles) {
            for (MediaPlayerBuilder.PlayerType playerType : moviePlayerTypes) {
                MediaPlayer mediaPlayer = MediaPlayerBuilder.build(movieFile.path, playerType);
                LOGGER.info("Initializing the " + playerType + " player with the " + movieFile.path + " stream");
                mediaPlayer.init();
                LOGGER.info("Wait for the " + playerType + " player to change its state");
                try {
                    Thread.sleep(TIMEOUT_MILLIS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Assert.assertEquals(mediaPlayer.getFps(), movieFile.fps);

                LOGGER.info("Dispose the " + playerType + " player");
                mediaPlayer.dispose();
            }
        }
    }

    @Test
    public void testGetWidthHeight(){
        for (Movie movieFile : movieFiles) {
            for (MediaPlayerBuilder.PlayerType playerType : moviePlayerTypes) {
                MediaPlayer mediaPlayer = MediaPlayerBuilder.build(movieFile.path, playerType);
                LOGGER.info("Initializing the " + playerType + " player with the " + movieFile.path + " stream");
                mediaPlayer.init();
                LOGGER.info("Wait for the " + playerType + " player to change its state");
                try {
                    Thread.sleep(TIMEOUT_MILLIS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    Assert.assertEquals(mediaPlayer.getImageWidth(), movieFile.width);
                    Assert.assertEquals(mediaPlayer.getImageHeight(), movieFile.height);
                } catch (UnsupportedOperationException e){
                    LOGGER.info("SDL Player doesn't support this functionality");
                }

                LOGGER.info("Dispose the " + playerType + " player");
                mediaPlayer.dispose();
            }
        }
    }

    @Test
    public void testGetDuration(){
        for (Movie movieFile : movieFiles) {
            for (MediaPlayerBuilder.PlayerType playerType : moviePlayerTypes) {
                MediaPlayer mediaPlayer = MediaPlayerBuilder.build(movieFile.path, playerType);
                LOGGER.info("Initializing the " + playerType + " player with the " + movieFile.path + " stream");
                mediaPlayer.init();
                LOGGER.info("Wait for the " + playerType + " player to change its state");
                try {
                    Thread.sleep(TIMEOUT_MILLIS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Assert.assertEquals(mediaPlayer.getDuration(), movieFile.duration);

                LOGGER.info("Dispose the " + playerType + " player");
                mediaPlayer.dispose();
            }
        }
    }

    @Test
    public void testSeek() {}

    @Test
    public void testStepForwardBackward(){}

}
