package org.datavyu.plugins.ffmpeg;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.swing.*;

/**
 * Tests the ffmpeg player using an AWT window for display
 */
public class FfmpegMediaPlayerTest extends MediaPlayerTest {

    @DataProvider(name = "ffmpegPlayers")
    public Object[][] createPlayerWithMedia() {
        // TODO(fraudies): Work with all video files
        MediaInformation mediaInformation = MEDIA.stream().findFirst().get();
        MediaPlayer mediaPlayer = new FfmpegJavaMediaPlayer(mediaInformation.getLocalPath(), new JDialog());
        MediaPlayerSync mediaPlayerSync = MediaPlayerSync.createMediaPlayerSync(mediaPlayer);
        return new Object[][] {{ mediaPlayerSync, mediaInformation}};
    }

    @Test(dataProvider = "ffmpegPlayers")
    public void testReadyState(MediaPlayerSync player, MediaInformation mediaInformation) {
        super.testReadyState(player, mediaInformation);
    }

    @Test(dataProvider = "ffmpegPlayers")
    public void testMetadata(MediaPlayerSync player, MediaInformation mediaInformation) {
        super.testMetadata(player, mediaInformation);
    }


    @Test(dataProvider = "ffmpegPlayers")
    public void testSeek(MediaPlayerSync player, MediaInformation mediaInformation) {
        super.testSeek(player, mediaInformation);
    }

    @Test(dataProvider = "ffmpegPlayers")
    public void testStepForward(MediaPlayerSync player, MediaInformation mediaInformation) {
        super.testStepForward(player, mediaInformation);
    }

    @Test(dataProvider = "ffmpegPlayers")
    public void testStepForwardAtEnd(MediaPlayerSync player, MediaInformation mediaInformation) {
        super.testStepForwardAtEnd(player, mediaInformation);
    }

    @Test(dataProvider = "ffmpegPlayers")
    public void testStepBackward(MediaPlayerSync player, MediaInformation mediaInformation) {
        super.testStepBackward(player, mediaInformation);
    }

    @Test(dataProvider = "ffmpegPlayers")
    public void testStepBackwardAtStart(MediaPlayerSync player, MediaInformation mediaInformation) {
        super.testStepBackwardAtStart(player, mediaInformation);
    }
}
