package org.datavyu.plugins.ffmpeg;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import javax.swing.*;
import java.io.File;
import java.net.URI;

public class TestFfmpegJavaMediaPlayer {

    /** The logger for this class */
    private static Logger logger = LogManager.getFormatterLogger(TestPlaybackRate.class);

    @Test(expectedExceptions = MediaException.class, expectedExceptionsMessageRegExp = "No such file or directory")
    public void testUnknownFile() {
        URI mediaPath = new File("wrongFileName.mp4").toURI();
        logger.info("Try opening " + mediaPath);
        FfmpegJavaMediaPlayer player = new FfmpegJavaMediaPlayer(mediaPath, new JFrame());
        player.init();
    }
}
