package org.datavyu.plugins.ffmpeg;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import java.util.List;

public class MediaPlayerBuilder {

    /** The LOGGER for this class */
    private final static Logger LOGGER = LogManager.getFormatterLogger(MediaPlayerBuilder.class);

    /** Resource folder for video files used during testing */
    private static final String TEST_RESOURCE_PATH = "test/resources";

    private static List<Movie> movieFiles = new ArrayList<>();

    // Data Providers
    private static Object[][] playersProvider;
    private static Object[][] mpvProvider;
    private static Object[][] ffmpegProvider;

    // This movie is about 30MB has 640 x 360 pixels @ 29.97 fps with 2.5 min play time
    static final String TEST_MOVIE_PATH = "http://www.html5videoplayer.net/videos/toystory.mp4";

    public enum PlayerType {
        SDL,
        JAVA_JDIALOG,
        MPV
    }

    public enum  Rate{
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

        Rate(float value){
            this.value = value;
        }
    }

    static {
        // Add all your movies here
        movieFiles.add(new Movie("C:\\Users\\DatavyuTests\\Documents\\Resources\\Videos\\Test1080p.mp4",
                142.107, // Duration in Seconds
                1920, // Width
                1080, // Height
                25 ));// Frame Per Second
        //build the list of parameters (Movie, PlayerType)  for the data provider
        playersProvider =  new Object[movieFiles.size() * PlayerType.values().length][2];
        mpvProvider =  new Object[movieFiles.size()][2];
        ffmpegProvider =  new Object[movieFiles.size() * 2][2];
        int j = 0;
        for (int i = 1; i <= movieFiles.size(); i++){
            for (PlayerType type : PlayerType.values()){
                if(type == PlayerType.MPV){
                    mpvProvider[i-1][0] = movieFiles.get(i-1);
                    mpvProvider[i-1][1] = type;
                } else {
                    //we have only two players
                    ffmpegProvider[(i-1)*2][0] = movieFiles.get(i-1);
                    ffmpegProvider[(i-1)*2][1] = type;
                }
                playersProvider[i*j][0] = movieFiles.get(i-1);
                playersProvider[i*j][1] = type;
                j++;
            }
        }
    }

    @DataProvider(name="players")
    public static Object[][] getPlayers(Method method, ITestContext context){
        List<ITestNGMethod> testMethods = context.getSuite().getAllMethods();
        String testName = testMethods.get(0).getTestClass().getName();
        if("testInitDispose".equals(method.getName())){
            return playersProvider;
        }
        //  Only MPV player need to be returned for now
        if(testName.contains("TestPlaybackRate")
                || testName.contains("TestMediaPlayerData")){
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
                return new MpvMediaPlayer(movie, new JDialog());
            default:
                throw new IllegalArgumentException("Could not build player for type " + type);
        }
    }

    static class Movie {
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

    static class TimeInterval {

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

}
