package org.datavyu.plugins.ffmpeg;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.swing.*;

/**
 * Tests the MPV player using a AWT window for display
 */
public class MpvMediaPlayerTest extends MediaPlayerTest {

    @DataProvider(name = "mpvPlayers")
    public Object[][] createPlayerWithMedia() {
        // TODO(fraudies): Work with all video files
        MediaInformation mediaInformation = MEDIA.stream().findFirst().get();
        MediaPlayerSync mediaPlayerSync = MediaPlayerSync.createMediaPlayerSync(
                new MpvAwtMediaPlayer(mediaInformation.getLocalPath(), new JDialog()));
        return new Object[][] {{ mediaPlayerSync, mediaInformation}};
    }

    @Test(dataProvider = "mpvPlayers")
    public void testReadyState(MediaPlayerSync player, MediaInformation mediaInformation) {
        super.testReadyState(player, mediaInformation);
    }

    @Test(dataProvider = "mpvPlayers")
    public void testMetadata(MediaPlayerSync player, MediaInformation mediaInformation) {
        super.testMetadata(player, mediaInformation);
    }


    @Test(dataProvider = "mpvPlayers")
    public void testSeek(MediaPlayerSync player, MediaInformation mediaInformation) {
        super.testSeek(player, mediaInformation);
    }

    @Test(dataProvider = "mpvPlayers")
    public void testStepForward(MediaPlayerSync player, MediaInformation mediaInformation) {
        super.testStepForward(player, mediaInformation);
    }

    @Test(dataProvider = "mpvPlayers")
    public void testStepForwardAtEnd(MediaPlayerSync player, MediaInformation mediaInformation) {
        super.testStepForwardAtEnd(player, mediaInformation);
    }

    @Test(dataProvider = "mpvPlayers")
    public void testStepBackward(MediaPlayerSync player, MediaInformation mediaInformation) {
        super.testStepBackward(player, mediaInformation);
    }

    @Test(dataProvider = "mpvPlayers")
    public void testStepBackwardAtStart(MediaPlayerSync player, MediaInformation mediaInformation) {
        super.testStepBackwardAtStart(player, mediaInformation);
    }
}
