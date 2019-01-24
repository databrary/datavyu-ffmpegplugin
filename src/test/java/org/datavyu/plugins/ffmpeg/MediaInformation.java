package org.datavyu.plugins.ffmpeg;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Optional;

public class MediaInformation {
    private static final String RESOURCE_NAME = "resources";
    private static final String RESOURCE_DIR = "src/test"; // System.getProperty("java.io.tmpdir")
    private static final String LOCAL_RESOURCE_DIR = createLocalResourcePath(RESOURCE_NAME);
    private final static Logger LOGGER = LogManager.getFormatterLogger(MediaInformation.class);
    static {
        Configurator.setRootLevel(Level.INFO);
    }

    MediaInformation(URI localPath, double startTime, double duration, int imageWidth, int imageHeight,
                     double framesPerSecond) {
        this.localPath = localPath;
        this.startTime = startTime;
        this.duration = duration;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.framesPerSecond = framesPerSecond;
    }

    public static Optional<MediaInformation> create(String remoteName, double startTime, double duration,
                                                    int width, int height, double framesPerSecond) {
        try {
            return Optional.of(new MediaInformation(
                    new File(copyToLocal(LOCAL_RESOURCE_DIR, new URL(remoteName))).toURI(),
                    startTime,
                    duration,
                    width,
                    height,
                    framesPerSecond));
        } catch (IOException e) {
            LOGGER.error(e);
        }
        return Optional.empty();
    }

    private URI localPath;
    private double startTime;
    private double duration;
    private int imageWidth;
    private int imageHeight;
    private double framesPerSecond;

    public URI getLocalPath() {
        return localPath;
    }

    public double getStartTime() {
        return startTime;
    }

    public double getDuration() {
        return duration;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public double getFramesPerSecond() {
        return framesPerSecond;
    }

    /**
     * Copies the resource from the URL url to a local resource directory and returns while
     * preserving the original file name
     *
     * @param remoteUrl The URL to the resource
     *
     * @return The file path to the copied resource locally
     *
     * @throws IOException if the directory can't be created or the resource can't be downloaded
     */
    private static String copyToLocal(String localResourceDir, URL remoteUrl) throws IOException {

        // Get the file name
        String fileName = new File(remoteUrl.toString()).getName();

        // Define the output file url
        File outPath = new File(localResourceDir, fileName);

        // If the file does not exist yet copy it from the www
        if (!outPath.exists()) {
            FileUtils.copyURLToFile(remoteUrl, outPath);
            LOGGER.info("Copied resource from " + remoteUrl + " to " + outPath);
        } else {
            LOGGER.info("Found existing resource " + outPath);
        }
        return outPath.getAbsolutePath();
    }

    private static String createLocalResourcePath(String resourceName) {
        try {
            File resourcePath = new File( RESOURCE_DIR + "/" + resourceName);
            FileUtils.forceMkdir(resourcePath);
            return resourcePath.getAbsolutePath();
        } catch (IOException io) {
            LOGGER.error(io);
        }
        return "./";
    }
}
