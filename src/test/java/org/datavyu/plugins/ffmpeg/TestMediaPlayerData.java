package org.datavyu.plugins.ffmpeg;


import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import static org.datavyu.plugins.ffmpeg.MediaPlayerBuilder.*;
import static org.datavyu.plugins.ffmpeg.MediaPlayerBuilder.Rate.*;

/**
 * Tests players defined in {@link PlayerType} with a list of movies in {@link MediaPlayerBuilder#movieFiles} to
 * make sure that all the players match the requirements of a media player, this suite cover calls made
 * from Java to the native player through Java Native Interface and assert if the behavior of the native
 * side is adequate to the sent command.
 * The player state {@link org.datavyu.plugins.ffmpeg.PlayerStateEvent.PlayerState} must be checked
 * after each action and its value should match the current state of the player, note that we have to
 * sleep the current thread for {@link #SLEEP_DURATION_IN_MILLIS} in order to wait for the native side to
 * update its state
 * @see NativeMediaPlayer
 * @see MediaPlayer
 * @see MediaPlayerData
 */
public class TestMediaPlayerData {

    /** The LOGGER for this class */
    private final static Logger LOGGER = LogManager.getFormatterLogger(TestPlaybackRate.class);

    /** The total number of seeks that need to be processed during the test **/
    private static final int NUMBER_OF_SEEK = 100;

    /** The Seek tolerance in seconds**/
    private static final double SEEK_TOLERANCE_IN_SECONDS = 0.100; // 100 ms

    private static final long SLEEP_DURATION_IN_MILLIS = 100; // 100 ms

    private static final int TO_MILLIS = 1000;

    /**
     * Calls {@link Thread#sleep(long)} to causes the currently executing thread to sleep
     * @param timeInMillis time to sleep in milliseconds
     *
     *
     * @param log
     * @throws  InterruptedException
     */
    private void sleep(final long timeInMillis, final boolean log) {
        try {
            if(log)
                LOGGER.info("Sleep for " +timeInMillis+ " ms");
            Thread.sleep(timeInMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Round a number to n decimal place
     * @param value to be rounded in double
     * @param places decimal places
     * @return a rounded number
     */
    private double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

    @BeforeClass
    public void setup() {
        Configurator.setRootLevel(Level.INFO);
    }

    @Test(dataProvider = "wrongFile",
            dataProviderClass = MediaPlayerBuilder.class,
            expectedExceptions = MediaException.class,
            groups = {"ALL", "mediaPlayerGroup"})
    public void testUnknownFile(Movie movie, PlayerType playerType) {
        System.out.println("////////////////////////////////////////////////////////////");
        System.out.println("//                   Unknown File Test                    //");
        System.out.println("////////////////////////////////////////////////////////////");
        MediaPlayer mediaPlayer = MediaPlayerBuilder.build(movie.path, playerType);
        mediaPlayer.init();
    }

    /**
     * Tests {@link MediaPlayer#init()} and {@link MediaPlayer#dispose()} methods that will initialize and destroy
     * resources for the media player.
     * All commands after a dispose call must be ignored and {@link MediaPlayer#getState()} should return
     * null if the player is disposed
     */
    @Test (dataProvider = "players",
            dataProviderClass = MediaPlayerBuilder.class,
            groups = {"ALL", "mediaPlayerGroup"})
    public void testInitDispose(Movie movie, PlayerType playerType){
        // Creating two instance of the SDL player crashes the JVM when trying to
        // Dispose the second player
        //TODO(Reda): Fix SDL Dispose
        System.out.println("////////////////////////////////////////////////////////////");
        System.out.println("//                   Init Dispose Test                    //");
        System.out.println("////////////////////////////////////////////////////////////");
        MediaPlayer mediaPlayer = MediaPlayerBuilder.build(movie.path, playerType);

        LOGGER.info("Initializing the " + playerType + " player with the " + movie.path + " stream");
        mediaPlayer.init();

        sleep(SLEEP_DURATION_IN_MILLIS, true);

        LOGGER.info("Checking the " + playerType + " player state");
        Assert.assertEquals(mediaPlayer.getState(), PlayerStateEvent.PlayerState.READY);

        LOGGER.info("Dispose the " + playerType + " player");
        mediaPlayer.dispose();
        Assert.assertEquals(mediaPlayer.getState(), null);

        LOGGER.info("Start the " + playerType + " player");
        mediaPlayer.play(); // Calling player after dispose crashes the JVM
        Assert.assertEquals(mediaPlayer.getState(), null);
    }

    /**
     * Tests {@link MediaPlayer#play()}  and {@link MediaPlayer#stop()} methods that will start and stop, respectively
     * the media player.
     */
    @Test (dataProvider = "players",
            dataProviderClass = MediaPlayerBuilder.class,
            groups = {"ALL", "mediaPlayerGroup"})
    public void testPlayStop(Movie movie, PlayerType playerType){
        //TODO(Reda): find a better way to be notified when an event occurs
        // Need to fix SDL player bug
        System.out.println("////////////////////////////////////////////////////////////");
        System.out.println("//                   Play Stop Test                       //");
        System.out.println("////////////////////////////////////////////////////////////");

        MediaPlayer mediaPlayer = MediaPlayerBuilder.build(movie.path, playerType);

        LOGGER.info("Initializing the " + playerType + " player with the " + movie.path + " stream");
        mediaPlayer.init();

        sleep(SLEEP_DURATION_IN_MILLIS, true);

        // Need to wait for the Ready state otherwise getPresentationTime will return NaN
        double startTime = mediaPlayer.getPresentationTime();

        LOGGER.info("Start the " + playerType + " player");
        mediaPlayer.play();

        sleep(SLEEP_DURATION_IN_MILLIS, true);
        double now = mediaPlayer.getPresentationTime();

        Assert.assertEquals(mediaPlayer.getState(), PlayerStateEvent.PlayerState.PLAYING);
        Assert.assertTrue(now > startTime);

        LOGGER.info("Stop the " + playerType + " player");
        mediaPlayer.stop();
        double endTime = mediaPlayer.getPresentationTime();

        sleep(SLEEP_DURATION_IN_MILLIS, true);
        now = mediaPlayer.getPresentationTime();

        Assert.assertEquals(mediaPlayer.getState(), PlayerStateEvent.PlayerState.STOPPED);
        Assert.assertEquals(now, endTime);

        LOGGER.info("Dispose the " + playerType + " player");
        mediaPlayer.dispose();
    }

    /**
     * Tests {@link MediaPlayer#pause()} method that will toggle between pause and play.
     * The player state {@link org.datavyu.plugins.ffmpeg.PlayerStateEvent.PlayerState} must be
     * set to PAUSED when the player is paused.
     */
    @Test (dataProvider = "players",
            dataProviderClass = MediaPlayerBuilder.class,
            groups = {"ALL", "mediaPlayerGroup"})
    public void testTogglePause(Movie movie, PlayerType playerType){
        System.out.println("////////////////////////////////////////////////////////////");
        System.out.println("//                   Toggle Pause Test                    //");
        System.out.println("////////////////////////////////////////////////////////////");

        MediaPlayer mediaPlayer = MediaPlayerBuilder.build(movie.path, playerType);

        LOGGER.info("Initializing the " + playerType + " player with the " + movie.path + " stream");
        mediaPlayer.init();

        LOGGER.info("Start the " + playerType + " player");
        mediaPlayer.play();

        sleep(SLEEP_DURATION_IN_MILLIS, true);

        LOGGER.info("Pause the " + playerType + " player");
        mediaPlayer.pause();
        double pauseTime = mediaPlayer.getPresentationTime();

        sleep(SLEEP_DURATION_IN_MILLIS, true);
        double now = mediaPlayer.getPresentationTime();

        Assert.assertEquals(mediaPlayer.getState(), PlayerStateEvent.PlayerState.PAUSED);
        Assert.assertEquals(now, pauseTime);


        LOGGER.info("Un-Pause the " + playerType + " player");
        double startTime = now;
        mediaPlayer.pause();

        sleep(SLEEP_DURATION_IN_MILLIS, true);
        now = mediaPlayer.getPresentationTime();

        Assert.assertEquals(mediaPlayer.getState(), PlayerStateEvent.PlayerState.PLAYING);
        Assert.assertTrue(now > startTime);

        LOGGER.info("Dispose the " + playerType + " player");
        mediaPlayer.dispose();
    }

    /**
     * Tests if the New Rate is persisted correctly in the native side and tests the behavior of the media player
     * after a pause and stop actions where the rate should be set to 1x after a stop and don't change after a pause
     * Note: this test will not cover the different playback rate speeds, please check {@link TestPlaybackRate}
     * for this matter.
     * @see TestPlaybackRate
     */
    @Test (dataProvider = "players",
            dataProviderClass = MediaPlayerBuilder.class,
            groups = {"ALL", "mediaPlayerGroup"})
    public void testSetGetRate(Movie movie, PlayerType playerType){
        // TODO(Reda): Test for negative values
        System.out.println("////////////////////////////////////////////////////////////");
        System.out.println("//                   Set Get Rate Test                    //");
        System.out.println("////////////////////////////////////////////////////////////");
        MediaPlayer mediaPlayer = MediaPlayerBuilder.build(movie.path, playerType);

        LOGGER.info("Initializing the " + playerType + " player with the " + movie.path + " stream");
        mediaPlayer.init();

        sleep(SLEEP_DURATION_IN_MILLIS, true);
        // Player Rate should always be initialized to 1X
        Assert.assertEquals(mediaPlayer.getRate(), X1.value);

        LOGGER.info("Start the " + playerType + " player");
        mediaPlayer.play();
        Assert.assertEquals(mediaPlayer.getRate(), X1.value);
        sleep(SLEEP_DURATION_IN_MILLIS, true);

        LOGGER.info("Set the " + playerType + " player rate to " + X2.value + "X");
        mediaPlayer.setRate(X2.value);
        sleep(SLEEP_DURATION_IN_MILLIS, true);
        Assert.assertEquals(mediaPlayer.getRate(), X2.value);

        LOGGER.info("Pause the " + playerType + " player");
        // Rate after a pause must stay equal to the rate before the pause action
        mediaPlayer.pause();
        Assert.assertEquals(mediaPlayer.getRate(), X2.value);

        LOGGER.info("Start the " + playerType + " player");
        mediaPlayer.play();
        Assert.assertEquals(mediaPlayer.getRate(), X2.value);

        LOGGER.info("Stop the " + playerType + " player");
        // The rate should be set back to 1x when the stream is stopped
        mediaPlayer.stop();
        sleep(SLEEP_DURATION_IN_MILLIS, true);
        Assert.assertEquals(mediaPlayer.getRate(), X1.value);

        LOGGER.info("Start the " + playerType + " player");
        mediaPlayer.play();
        Assert.assertEquals(mediaPlayer.getRate(), X1.value);

        // Players must not be affected by negative values
        mediaPlayer.setRate(-X1.value);
        Assert.assertNotEquals(mediaPlayer.getRate(), -X1.value);

        LOGGER.info("Dispose the " + playerType + " player");
        mediaPlayer.dispose();
    }

    /**
     * Tests if {@link MediaPlayer#getFps()} method matches the pre-defined fps value
     * in {@link MediaPlayerBuilder.Movie#fps }.
     */
    @Test (dataProvider = "players",
            dataProviderClass = MediaPlayerBuilder.class,
            groups = {"ALL", "mediaPlayerGroup"})
    public void testGetFps(Movie movie, PlayerType playerType){
        System.out.println("////////////////////////////////////////////////////////////");
        System.out.println("//                        Get Fps Test                    //");
        System.out.println("////////////////////////////////////////////////////////////");

        MediaPlayer mediaPlayer = MediaPlayerBuilder.build(movie.path, playerType);

        LOGGER.info("Initialize the " + playerType + " player with the " + movie.path + " stream");
        mediaPlayer.init();

        sleep(SLEEP_DURATION_IN_MILLIS, true);
        LOGGER.info(playerType + " player FPS " + mediaPlayer.getFps() + " Movie FPS " + movie.fps);
        Assert.assertEquals(round(mediaPlayer.getFps(),2), movie.fps);

        LOGGER.info("Dispose the " + playerType + " player");
        mediaPlayer.dispose();
    }

    /**
     * Tests if {@link MediaPlayer#getImageWidth()} and {@link MediaPlayer#getImageHeight()}
     * methods match the pre-defined {@link MediaPlayerBuilder.Movie#width }
     * and {@link MediaPlayerBuilder.Movie#height}, respectively.
     */
    @Test (dataProvider = "players",
            dataProviderClass = MediaPlayerBuilder.class,
            groups = {"ALL", "mediaPlayerGroup"})
    public void testGetWidthHeight(Movie movie, PlayerType playerType){
        System.out.println("////////////////////////////////////////////////////////////");
        System.out.println("//                   Get Width Height Test                //");
        System.out.println("////////////////////////////////////////////////////////////");

        MediaPlayer mediaPlayer = MediaPlayerBuilder.build(movie.path, playerType);

        LOGGER.info("Initializing the " + playerType + " player with the " + movie.path + " stream");
        mediaPlayer.init();

        sleep(SLEEP_DURATION_IN_MILLIS, true);
        Assert.assertEquals(mediaPlayer.getImageWidth(), movie.width);
        Assert.assertEquals(mediaPlayer.getImageHeight(), movie.height);

        LOGGER.info("Dispose the " + playerType + " player");
        mediaPlayer.dispose();
    }

    /**
     * Tests if {@link MediaPlayer#getImageWidth()} and {@link MediaPlayer#getImageHeight()}
     * methods match the pre-defined {@link MediaPlayerBuilder.Movie#width }
     * and {@link MediaPlayerBuilder.Movie#height}, respectively.
     */
    @Test (dataProvider = "players",
            dataProviderClass = MediaPlayerBuilder.class,
            groups = {"ALL", "mediaPlayerGroup"})
    public void testGetDuration(Movie movie, PlayerType playerType){
        System.out.println("////////////////////////////////////////////////////////////");
        System.out.println("//                   Get Duration Test                    //");
        System.out.println("////////////////////////////////////////////////////////////");
        MediaPlayer mediaPlayer = MediaPlayerBuilder.build(movie.path, playerType);

        LOGGER.info("Initializing the " + playerType + " player with the " + movie.path + " stream");
        mediaPlayer.init();

        sleep(SLEEP_DURATION_IN_MILLIS, true);
        Assert.assertEquals(mediaPlayer.getDuration(), movie.duration);

        LOGGER.info("Dispose the " + playerType + " player");
        mediaPlayer.dispose();
    }

    /**
     * Tests the players seek feature {@link MediaPlayer#seek(double)}, the test will perform a number
     * of seeks {@link #NUMBER_OF_SEEK} to a random timestamp and check
     * if the the player accuracy is within a threshold {@link #SEEK_TOLERANCE_IN_SECONDS}
     *
     * The FFmpeg Players don't perform well while seeking, testing are showing a tolerance
     * of 6 sec in order to pass all the Asserts, we will keep it as a soft assert for now,
     * However we won't tolerate more than 30% failed seek with 100 ms as threshold.
     */
    @Test (dataProvider = "players",
            dataProviderClass = MediaPlayerBuilder.class,
            groups = {"ALL", "mediaPlayerGroup"})
    public void testSeek(Movie movie, PlayerType playerType) {
        System.out.println("////////////////////////////////////////////////////////////");
        System.out.println("//                        Seek Test                       //");
        System.out.println("////////////////////////////////////////////////////////////");
        SoftAssert softAssert = new SoftAssert();

        MediaPlayer mediaPlayer = MediaPlayerBuilder.build(movie.path, playerType);

        LOGGER.info("Initializing the " + playerType + " player with the " + movie.path + " stream");
        mediaPlayer.init();

        sleep(SLEEP_DURATION_IN_MILLIS, false);
        double minDuration = 0.0;
        double maxDuration = mediaPlayer.getDuration();
        double now = 0, randomTime = 0;
        LOGGER.info("Total duration " + mediaPlayer.getDuration() + " sec");
        for (int i = 0; i < NUMBER_OF_SEEK ; i++) {
            randomTime = (minDuration + (Math.random() * (maxDuration - minDuration)));
            Assert.assertTrue(minDuration <= randomTime && randomTime <= maxDuration);

            mediaPlayer.seek(randomTime);

            sleep(SLEEP_DURATION_IN_MILLIS, false);
            now = mediaPlayer.getPresentationTime();
            if (playerType == PlayerType.JAVA_JDIALOG
                    || playerType == PlayerType.SDL) {
                LOGGER.info(" Requested Time " + randomTime + " sec, current time " + now +" sec");
                softAssert.assertTrue(Math.abs(now - randomTime) <= SEEK_TOLERANCE_IN_SECONDS);
            } else {
//                 MPV Players offers accurate seeks within a 100 ms tolerance
                LOGGER.info(" Requested Time " + randomTime + " sec, current time " + now +" sec");
                Assert.assertTrue(Math.abs(now - randomTime) <= SEEK_TOLERANCE_IN_SECONDS);
            }
        }

        LOGGER.info("Dispose the " + playerType + " player");
        mediaPlayer.dispose();
    }

    /**
     * Tests Step Forward feature {@link MediaPlayer#stepForward()}
     *
     * IMPORTANT: Only available for the MPV player, the ffmpeg SDL and Java players can
     * step forward. however, the reported time after a seek is NaN which make the test fails
     */
    @Test (dataProvider = "players",
            dataProviderClass = MediaPlayerBuilder.class,
            groups = {"ALL", "mediaPlayerGroup"})
    public void testStepForward(Movie movie, PlayerType playerType){
        System.out.println("////////////////////////////////////////////////////////////");
        System.out.println("//              Step Forward Test                        //");
        System.out.println("////////////////////////////////////////////////////////////");
        SoftAssert softAssert = new SoftAssert();
        double minDuration = 0.0;

        MediaPlayer mediaPlayer = MediaPlayerBuilder.build(movie.path, playerType);

        LOGGER.info("Initializing the " + playerType + " player with the " + movie.path + " stream");
        mediaPlayer.init();

        sleep(SLEEP_DURATION_IN_MILLIS, true);
        double randomTime = (minDuration + (Math.random() * (mediaPlayer.getDuration() - minDuration)));
        mediaPlayer.seek(randomTime);

        sleep(SLEEP_DURATION_IN_MILLIS, true);
        if(playerType == PlayerType.MPV) {
            Assert.assertTrue(Math.abs(mediaPlayer.getPresentationTime() - randomTime) < SEEK_TOLERANCE_IN_SECONDS);
        } else {
            softAssert.assertTrue(Math.abs(mediaPlayer.getPresentationTime() - randomTime) < SEEK_TOLERANCE_IN_SECONDS);
        }
        double now = mediaPlayer.getPresentationTime();
        mediaPlayer.stepForward();

        sleep(SLEEP_DURATION_IN_MILLIS, true);
        double currentTime = Math.abs(mediaPlayer.getPresentationTime() - now);
        double expectedTime = (TO_MILLIS / mediaPlayer.getFps()) / TO_MILLIS;
        LOGGER.info("Step Forward current time " + currentTime + " sec, expected time " + expectedTime + " sec");
        if(playerType == PlayerType.MPV) {
            Assert.assertEquals(round(currentTime,2), round(expectedTime,2));
        } else {
            softAssert.assertEquals(round(currentTime,2), round(expectedTime,2));
        }
        LOGGER.info("Dispose the " + playerType + " player");
        mediaPlayer.dispose();
    }

    /**
     * Tests Step Backward feature {@link MediaPlayer#stepForward()}
     *
     * IMPORTANT: Only available for the MPV player, the ffmpeg SDL and Java players cannot
     * step backward. however, the reported time after a seek is NaN which make the test fails
     */
    @Test (dataProvider = "players",
            dataProviderClass = MediaPlayerBuilder.class,
            groups = {"ALL", "mediaPlayerGroup"})
    public void testStepBackward(Movie movie, PlayerType playerType){
        System.out.println("////////////////////////////////////////////////////////////");
        System.out.println("//              Step Backward Test                        //");
        System.out.println("////////////////////////////////////////////////////////////");
        SoftAssert softAssert = new SoftAssert();
        double minDuration = 0.0;
        MediaPlayer mediaPlayer = MediaPlayerBuilder.build(movie.path, playerType);

        LOGGER.info("Initializing the " + playerType + " player with the " + movie.path + " stream");
        mediaPlayer.init();

        sleep(SLEEP_DURATION_IN_MILLIS, true);
        double randomTime = (minDuration + (Math.random() * (mediaPlayer.getDuration() - minDuration)));
        mediaPlayer.seek(randomTime);

        sleep(SLEEP_DURATION_IN_MILLIS, true);
        if(playerType == PlayerType.MPV) {
            Assert.assertTrue(Math.abs(mediaPlayer.getPresentationTime() - randomTime) < SEEK_TOLERANCE_IN_SECONDS);
        } else {
            softAssert.assertTrue(Math.abs(mediaPlayer.getPresentationTime() - randomTime) < SEEK_TOLERANCE_IN_SECONDS);
        }

        double now = mediaPlayer.getPresentationTime();
        mediaPlayer.stepBackward();

        sleep(SLEEP_DURATION_IN_MILLIS, true);
        double currentTime = Math.abs(mediaPlayer.getPresentationTime() - now);
        double expectedTime = (TO_MILLIS / mediaPlayer.getFps()) / TO_MILLIS;
        LOGGER.info("Step Backward current time " + currentTime + " sec, expected time " + expectedTime + " sec");
        if(playerType == PlayerType.MPV) {
            Assert.assertEquals(round(currentTime,2), round(expectedTime,2));
        } else {
            softAssert.assertEquals(round(currentTime,2), round(expectedTime,2));
        }

        LOGGER.info("Dispose the " + playerType + " player");
        mediaPlayer.dispose();
    }
}
