package org.datavyu.plugins.ffmpeg;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.swing.*;
import java.awt.*;

/**
 * Tests the MPV player using a AWT window for display
 */
public class MpvMediaPlayerTest extends MediaPlayerTest {

    public static class MpvBuilder implements Builder {
        private MediaInformation mediaInformation;
        private Container container;

        MpvBuilder() { }

        MpvBuilder withMedia(MediaInformation mediaInformation) {
            this.mediaInformation = mediaInformation;
            return this;
        }

        MpvBuilder withContainer(Container container) {
            this.container = container;
            return this;
        }

        @Override
        public MediaPlayerSync build() {
            MediaPlayer mediaPlayer = new MpvAwtMediaPlayer(mediaInformation.getLocalPath(), container);
            return MediaPlayerSync.createMediaPlayerSync(mediaPlayer);
        }

    }

    @DataProvider(name = "shortMedia")
    public Object[][] createPlayerWithMedia() {
        return new Object[][] {{
            new MpvBuilder().withMedia(SHORT_MEDIA).withContainer(new JDialog()).build(), SHORT_MEDIA}};
    }

    @Test(dataProvider = "shortMedia")
    public void testReadyState(Builder builder, MediaInformation mediaInformation) {
        super.testReadyState(builder, mediaInformation);
    }

    @Test(dataProvider = "shortMedia")
    public void testMetadata(Builder builder, MediaInformation mediaInformation) {
        super.testMetadata(builder, mediaInformation);
    }


    @Test(dataProvider = "shortMedia")
    public void testSeek(Builder builder, MediaInformation mediaInformation) {
        super.testSeek(builder, mediaInformation);
    }

    @Test(dataProvider = "shortMedia")
    public void testStepForward(Builder builder, MediaInformation mediaInformation) {
        super.testStepForward(builder, mediaInformation);
    }

    @Test(dataProvider = "shortMedia")
    public void testStepForwardAtEnd(Builder builder, MediaInformation mediaInformation) {
        super.testStepForwardAtEnd(builder, mediaInformation);
    }

    @Test(dataProvider = "shortMedia")
    public void testStepBackward(Builder builder, MediaInformation mediaInformation) {
        super.testStepBackward(builder, mediaInformation);
    }

    @Test(dataProvider = "shortMedia")
    public void testStepBackwardAtStart(Builder builder, MediaInformation mediaInformation) {
        super.testStepBackwardAtStart(builder, mediaInformation);
    }
}
