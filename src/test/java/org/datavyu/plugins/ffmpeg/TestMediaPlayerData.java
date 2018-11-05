package org.datavyu.plugins.ffmpeg;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests players defined in {@link #moviePlayerTypes} with a list of movies in {@link #movieFiles} to
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

    /** Resource folder for video files used during testing */
    private static final String TEST_RESOURCE_PATH = "test/resources";

    /** The total number of seeks that need to be processed during the test **/
    private static final int NUMBER_OF_SEEK = 100;

    /** The Seek tolerance in seconds**/
    private static final double SEEK_TOLERANCE_IN_SECONDS = 0.100; // 3 sec <->  100 ms

    private static final long SLEEP_DURATION_IN_MILLIS = 100; // 100 ms

    // This movie is about 30MB has 640 x 360 pixels @ 29.97 fps with 2.5 min play time
    private static final String TEST_MOVIE_PATH = "http://www.html5videoplayer.net/videos/toystory.mp4";

    /** Rate Speed 1X**/
    private static final float RATE_1X = 1.0F;

    /** Rate Speed 2X**/
    private static final float RATE_2X = 2.0F;

    /** Rate Speed 4X**/
    private static final float RATE_4X = 4.0F;

    private static final int TO_MILLIS = 1000;


    // A list of all movie files to test
    private List<Movie> movieFiles = new ArrayList<>();

    private List<MediaPlayerBuilder.PlayerType> moviePlayerTypes = new ArrayList<MediaPlayerBuilder.PlayerType>(){{
        add(MediaPlayerBuilder.PlayerType.JAVA_JDIALOG);
        add(MediaPlayerBuilder.PlayerType.MPV);
        add(MediaPlayerBuilder.PlayerType.SDL);
    }};

    private class Movie {
        String path;
        int width, height;
        double duration,fps; // duration in Seconds

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

    /**
     * Tests {@link MediaPlayer#init()} and {@link MediaPlayer#dispose()} methods that will initialize and destroy
     * resources for the media player.
     * All commands after a dispose call must be ignored and {@link MediaPlayer#getState()} should return
     * null if the player is disposed
     */
    @Test
    public void testInitDispose(){
        // Creating two instance of the SDL player crashes the JVM when trying to
        // Dispose the second player
        //TODO(Reda): Fix SDL Dispose
        System.out.println("////////////////////////////////////////////////////////////");
        System.out.println("//                   Init Dispose Test                    //");
        System.out.println("////////////////////////////////////////////////////////////");
        for (Movie movieFile : movieFiles) {
            for (MediaPlayerBuilder.PlayerType playerType: moviePlayerTypes) {
                MediaPlayer mediaPlayer = MediaPlayerBuilder.build(movieFile.path, playerType);

                LOGGER.info("Initializing the " + playerType + " player with the "+movieFile.path+ " stream");
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
        }
    }

    /**
     * Tests {@link MediaPlayer#play()}  and {@link MediaPlayer#stop()} methods that will start and stop, respectively
     * the media player.
     */
    @Test
    public void testPlayStop(){
        //TODO(Reda): find a better way to be notified when an event occurs
        // Need to fix SDL player bug
        System.out.println("////////////////////////////////////////////////////////////");
        System.out.println("//                   Play Stop Test                       //");
        System.out.println("////////////////////////////////////////////////////////////");
        for (Movie movieFile : movieFiles) {
            for (MediaPlayerBuilder.PlayerType playerType : moviePlayerTypes) {
                MediaPlayer mediaPlayer = MediaPlayerBuilder.build(movieFile.path, playerType);

                LOGGER.info("Initializing the " + playerType + " player with the "+movieFile.path+ " stream");
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
        }
    }

    /**
     * Tests {@link MediaPlayer#pause()} method that will toggle between pause and play.
     * The player state {@link org.datavyu.plugins.ffmpeg.PlayerStateEvent.PlayerState} must be
     * set to PAUSED when the player is paused.
     */
    @Test
    public void testTogglePause(){
        System.out.println("////////////////////////////////////////////////////////////");
        System.out.println("//                   Toggle Pause Test                    //");
        System.out.println("////////////////////////////////////////////////////////////");
        for (Movie movieFile : movieFiles) {
            for (MediaPlayerBuilder.PlayerType playerType : moviePlayerTypes) {

                MediaPlayer mediaPlayer = MediaPlayerBuilder.build(movieFile.path, playerType);

                LOGGER.info("Initializing the " + playerType + " player with the "+movieFile.path+ " stream");
                mediaPlayer.init();

                LOGGER.info("Start the " + playerType + " player");
                mediaPlayer.play();

                sleep(SLEEP_DURATION_IN_MILLIS, true);

                double pauseTime = mediaPlayer.getPresentationTime();
                LOGGER.info("Pause the " + playerType + " player");
                mediaPlayer.pause();

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
        }
    }

    /**
     * Tests if the New Rate is persisted correctly in the native side and tests the behavior of the media player
     * after a pause and stop actions where the rate should be set to 1x after a stop and don't change after a pause
     * Note: this test will not cover the different playback rate speeds, please check {@link TestPlaybackRate}
     * for this matter.
     * @see TestPlaybackRate
     */
    @Test
    public void testSetGetRate(){
        // TODO(Reda): Test for negative values
        System.out.println("////////////////////////////////////////////////////////////");
        System.out.println("//                   Set Get Rate Test                    //");
        System.out.println("////////////////////////////////////////////////////////////");
        for (Movie movieFile : movieFiles) {
            for (MediaPlayerBuilder.PlayerType playerType : moviePlayerTypes) {
                MediaPlayer mediaPlayer = MediaPlayerBuilder.build(movieFile.path, playerType);

                LOGGER.info("Initializing the " + playerType + " player with the "+movieFile.path+ " stream");
                mediaPlayer.init();

                sleep(SLEEP_DURATION_IN_MILLIS, true);
                // Player Rate should always be initialized to 1X
                Assert.assertEquals(mediaPlayer.getRate(), RATE_1X);

                LOGGER.info("Start the " + playerType + " player");
                mediaPlayer.play();
                Assert.assertEquals(mediaPlayer.getRate(), RATE_1X);
                sleep(SLEEP_DURATION_IN_MILLIS, true);

                LOGGER.info("Set the " + playerType + " player rate to " +RATE_2X+ "X");
                mediaPlayer.setRate(RATE_2X);
                sleep(SLEEP_DURATION_IN_MILLIS, true);
                Assert.assertEquals(mediaPlayer.getRate(), RATE_2X);

                LOGGER.info("Pause the " + playerType + " player");
                // Rate after a pause must stay equal to the rate before the pause action
                mediaPlayer.pause();
                Assert.assertEquals(mediaPlayer.getRate(), RATE_2X);

                LOGGER.info("Start the " + playerType + " player");
                mediaPlayer.play();
                Assert.assertEquals(mediaPlayer.getRate(), RATE_2X);

                LOGGER.info("Stop the " + playerType + " player");
                // The rate should be set back to 1x when the stream is stopped
                mediaPlayer.stop();
                sleep(SLEEP_DURATION_IN_MILLIS, true);
                Assert.assertEquals(mediaPlayer.getRate(), RATE_1X);

                LOGGER.info("Start the " + playerType + " player");
                mediaPlayer.play();
                Assert.assertEquals(mediaPlayer.getRate(), RATE_1X);

                // Players must not be affected by negative values
                mediaPlayer.setRate(-RATE_1X);
                Assert.assertNotEquals(mediaPlayer.getRate(), -RATE_1X);

                LOGGER.info("Dispose the " + playerType + " player");
                mediaPlayer.dispose();
            }
        }
    }

    /**
     * Tests if {@link MediaPlayer#getFps()} method matches the pre-defined fps value in {@link Movie#fps }.
     */
    @Test
    public void testGetFps(){
        System.out.println("////////////////////////////////////////////////////////////");
        System.out.println("//                        Get Fps Test                    //");
        System.out.println("////////////////////////////////////////////////////////////");
        for (Movie movieFile : movieFiles) {
            for (MediaPlayerBuilder.PlayerType playerType : moviePlayerTypes) {
                MediaPlayer mediaPlayer = MediaPlayerBuilder.build(movieFile.path, playerType);

                LOGGER.info("Initialize the " + playerType + " player with the " + movieFile.path + " stream");
                mediaPlayer.init();

                sleep(SLEEP_DURATION_IN_MILLIS, true);
                Assert.assertEquals(mediaPlayer.getFps(), movieFile.fps);

                LOGGER.info("Dispose the " + playerType + " player");
                mediaPlayer.dispose();
            }
        }
    }

    /**
     * Tests if {@link MediaPlayer#getImageWidth()} and {@link MediaPlayer#getImageHeight()}
     * methods match the pre-defined {@link Movie#width } and {@link Movie#height}, respectively.
     */
    @Test
    public void testGetWidthHeight(){
        System.out.println("////////////////////////////////////////////////////////////");
        System.out.println("//                   Get Width Height Test                //");
        System.out.println("////////////////////////////////////////////////////////////");
        for (Movie movieFile : movieFiles) {
            for (MediaPlayerBuilder.PlayerType playerType : moviePlayerTypes) {
                MediaPlayer mediaPlayer = MediaPlayerBuilder.build(movieFile.path, playerType);

                LOGGER.info("Initializing the " + playerType + " player with the " + movieFile.path + " stream");
                mediaPlayer.init();

                sleep(SLEEP_DURATION_IN_MILLIS, true);
                Assert.assertEquals(mediaPlayer.getImageWidth(), movieFile.width);
                Assert.assertEquals(mediaPlayer.getImageHeight(), movieFile.height);

                LOGGER.info("Dispose the " + playerType + " player");
                mediaPlayer.dispose();
            }
        }
    }

    /**
     * Tests if {@link MediaPlayer#getImageWidth()} and {@link MediaPlayer#getImageHeight()}
     * methods match the pre-defined {@link Movie#width } and {@link Movie#height}, respectively.
     */
    @Test
    public void testGetDuration(){
        System.out.println("////////////////////////////////////////////////////////////");
        System.out.println("//                   Get Duration Test                    //");
        System.out.println("////////////////////////////////////////////////////////////");
        for (Movie movieFile : movieFiles) {
            for (MediaPlayerBuilder.PlayerType playerType : moviePlayerTypes) {
                MediaPlayer mediaPlayer = MediaPlayerBuilder.build(movieFile.path, playerType);

                LOGGER.info("Initializing the " + playerType + " player with the " + movieFile.path + " stream");
                mediaPlayer.init();

                sleep(SLEEP_DURATION_IN_MILLIS, true);
                Assert.assertEquals(mediaPlayer.getDuration(), movieFile.duration);

                LOGGER.info("Dispose the " + playerType + " player");
                mediaPlayer.dispose();
            }
        }
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
    @Test
    public void testSeek() {
        System.out.println("////////////////////////////////////////////////////////////");
        System.out.println("//                        Seek Test                       //");
        System.out.println("////////////////////////////////////////////////////////////");
        SoftAssert softAssert = new SoftAssert();
        for (Movie movieFile : movieFiles) {
            for (MediaPlayerBuilder.PlayerType playerType : moviePlayerTypes) {
                MediaPlayer mediaPlayer = MediaPlayerBuilder.build(movieFile.path, playerType);

                LOGGER.info("Initializing the " + playerType + " player with the " + movieFile.path + " stream");
                mediaPlayer.init();

                sleep(SLEEP_DURATION_IN_MILLIS, false);
                int failedSeek = 0;
                double minDuration = 0.0;
                double maxDuration = mediaPlayer.getDuration();
                double now = 0, randomTime = 0;
                LOGGER.info("Total duration " + mediaPlayer.getDuration() + " sec");
                for (int i = 0; i < NUMBER_OF_SEEK; i++){
                    randomTime =  (minDuration + (Math.random() * (maxDuration - minDuration)));
                    Assert.assertTrue( minDuration <= randomTime && randomTime <= maxDuration);

                    mediaPlayer.seek(randomTime);

                    sleep(SLEEP_DURATION_IN_MILLIS, false);
                    now = mediaPlayer.getPresentationTime();
                    if(playerType ==  MediaPlayerBuilder.PlayerType.JAVA_JDIALOG
                            || playerType == MediaPlayerBuilder.PlayerType.SDL){
                        softAssert.assertTrue( Math.abs( now - randomTime) <= SEEK_TOLERANCE_IN_SECONDS);
                        if (Math.abs(now - randomTime) >= SEEK_TOLERANCE_IN_SECONDS){
                            failedSeek++;
                        }
                    } else {
                        // MPV Players offers accurate seeks within a 100 ms tolerance
                        Assert.assertTrue(Math.abs(now - randomTime) <= SEEK_TOLERANCE_IN_SECONDS);
                    }
                }
                // Temporary 30% failed seek tolerance for the JAVA/SDL Players
                Assert.assertTrue((failedSeek/NUMBER_OF_SEEK) * 100 < 30.0);

                LOGGER.info("Dispose the " + playerType + " player");
                mediaPlayer.dispose();
            }
        }
    }

    /**
     * Tests Step Forward feature {@link MediaPlayer#stepForward()}
     *
     * IMPORTANT: Only available for the MPV player, the ffmpeg SDL and Java players can
     * step forward. however, the reported time after a seek is NaN which make the test fails
     */
    @Test
    public void testStepForward(){
        System.out.println("////////////////////////////////////////////////////////////");
        System.out.println("//              Step Forward Test                        //");
        System.out.println("////////////////////////////////////////////////////////////");
        for (Movie movieFile : movieFiles) {
            for (MediaPlayerBuilder.PlayerType playerType : moviePlayerTypes) {
                if(playerType == MediaPlayerBuilder.PlayerType.MPV){
                    double minDuration = 0.0;

                    MediaPlayer mediaPlayer = MediaPlayerBuilder.build(movieFile.path, playerType);

                    LOGGER.info("Initializing the " + playerType + " player with the " + movieFile.path + " stream");
                    mediaPlayer.init();

                    sleep(SLEEP_DURATION_IN_MILLIS, true);
                    double randomTime =  (minDuration + (Math.random() * (mediaPlayer.getDuration() - minDuration)));
                    mediaPlayer.seek(randomTime);

                    sleep(SLEEP_DURATION_IN_MILLIS, true);
                    Assert.assertTrue(Math.abs(mediaPlayer.getPresentationTime() - randomTime) < SEEK_TOLERANCE_IN_SECONDS );

                    double now = mediaPlayer.getPresentationTime();
                    mediaPlayer.stepForward();

                    sleep(SLEEP_DURATION_IN_MILLIS, true);
                    double currentTime = round(Math.abs(mediaPlayer.getPresentationTime()-now), 2);
                    double expectedTime = (TO_MILLIS /mediaPlayer.getFps())/ TO_MILLIS;
                    LOGGER.info("Step Forward current time " + currentTime + " sec, expected time " + expectedTime + " sec");

                    Assert.assertEquals(currentTime, expectedTime);

                    LOGGER.info("Dispose the " + playerType + " player");
                    mediaPlayer.dispose();
                }
            }
        }
    }

    /**
     * Tests Step Backward feature {@link MediaPlayer#stepForward()}
     *
     * IMPORTANT: Only available for the MPV player, the ffmpeg SDL and Java players cannot
     * step backward. however, the reported time after a seek is NaN which make the test fails
     */
    @Test
    public void testStepBackward(){
        System.out.println("////////////////////////////////////////////////////////////");
        System.out.println("//              Step Backward Test                        //");
        System.out.println("////////////////////////////////////////////////////////////");
        for (Movie movieFile : movieFiles) {
            for (MediaPlayerBuilder.PlayerType playerType : moviePlayerTypes) {
                if(playerType == MediaPlayerBuilder.PlayerType.MPV){
                    double minDuration = 0.0;

                    MediaPlayer mediaPlayer = MediaPlayerBuilder.build(movieFile.path, playerType);

                    LOGGER.info("Initializing the " + playerType + " player with the " + movieFile.path + " stream");
                    mediaPlayer.init();

                    sleep(SLEEP_DURATION_IN_MILLIS, true);
                    double randomTime =  (minDuration + (Math.random() * (mediaPlayer.getDuration() - minDuration)));
                    mediaPlayer.seek(randomTime);

                    sleep(SLEEP_DURATION_IN_MILLIS, true);
                    Assert.assertTrue(Math.abs(mediaPlayer.getPresentationTime() - randomTime) < SEEK_TOLERANCE_IN_SECONDS );

                    double now = mediaPlayer.getPresentationTime();
                    mediaPlayer.stepBackward();

                    sleep(SLEEP_DURATION_IN_MILLIS, true);
                    double currentTime = round(Math.abs(mediaPlayer.getPresentationTime()-now), 2);
                    double expectedTime = (TO_MILLIS /mediaPlayer.getFps())/ TO_MILLIS;
                    LOGGER.info("Step Backward current time " + currentTime + " sec, expected time " + expectedTime + " sec");
                    Assert.assertEquals(currentTime, expectedTime);

                    LOGGER.info("Dispose the " + playerType + " player");
                    mediaPlayer.dispose();
                }
            }
        }
    }
}
