package org.datavyu.plugins.ffmpeg;

import java.awt.Container;
import javax.swing.JDialog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.datavyu.plugins.MediaException;
import org.datavyu.plugins.MediaPlayer;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/** Tests the ffmpeg player using an AWT window for display */
public class FfmpegJavaMediaPlayerTest extends MediaPlayerTest {
  private static final Logger logger = LogManager.getFormatterLogger(FfmpegJavaMediaPlayerTest.class);

  public static class FfmpegBuilder implements Builder {
    private MediaInformation mediaInformation;
    private Container container;

    FfmpegBuilder() {}

    FfmpegBuilder withMedia(MediaInformation mediaInformation) {
      this.mediaInformation = mediaInformation;
      return this;
    }

    FfmpegBuilder withContainer(Container container) {
      this.container = container;
      return this;
    }

    @Override
    public MediaPlayerSync build() {
      MediaPlayer mediaPlayer =
          new FfmpegJavaMediaPlayer(mediaInformation.getLocalPath(), container);
      return MediaPlayerSync.createMediaPlayerSync(mediaPlayer);
    }
  }

  @DataProvider(name = "shortMedia")
  public Object[][] createPlayerWithShortMedia() {
    return new Object[][] {
      {new FfmpegBuilder().withMedia(SHORT_MEDIA).withContainer(new JDialog()), SHORT_MEDIA}
    };
  }

  @DataProvider(name = "longMedia")
  public Object[][] createPlayerWithLongMedia() {
    return new Object[][] {
      {new FfmpegBuilder().withMedia(LONG_MEDIA).withContainer(new JDialog()), LONG_MEDIA}
    };
  }

  @DataProvider(name = "wrongMedia")
  public Object[][] createPlayerWithWrongMedia() {
    return new Object[][] {
      {new FfmpegBuilder().withMedia(WRONG_MEDIA).withContainer(new JDialog()), WRONG_MEDIA}
    };
  }

  @Test(dataProvider = "shortMedia")
  public void testReadyState(Builder builder, MediaInformation mediaInformation) {
    logger.debug("******** Test Ready State ********");
    super.testReadyState(builder, mediaInformation);
  }

  @Test(dataProvider = "shortMedia")
  public void testPlayingState(Builder builder, MediaInformation mediaInformation) {
    logger.debug("******** Test Playing State ********");
    super.testPlayingState(builder, mediaInformation);
  }

  @Test(dataProvider = "shortMedia")
  public void testStoppedState(Builder builder, MediaInformation mediaInformation) {
    logger.debug("******** Test Stopped State ********");
    super.testStoppedState(builder, mediaInformation);
  }

  @Test(dataProvider = "shortMedia")
  public void testPausedState(Builder builder, MediaInformation mediaInformation) {
    logger.debug("******** Test Paused State ********");
    super.testPausedState(builder, mediaInformation);
  }

  @Test(dataProvider = "shortMedia")
  public void testMetadata(Builder builder, MediaInformation mediaInformation) {
    super.testMetadata(builder, mediaInformation);
  }

  @Test(dataProvider = "wrongMedia", expectedExceptions = MediaException.class)
  public void testWrongFilename(Builder builder, MediaInformation mediaInformation) {
    super.testWrongFile(builder, mediaInformation);
  }

  @Test(dataProvider = "shortMedia")
  public void testTimeAtStart(Builder builder, MediaInformation mediaInformation) {
    super.testTimeAtStart(builder, mediaInformation);
  }

  @Test(dataProvider = "shortMedia")
  public void testSeek(Builder builder, MediaInformation mediaInformation) {
    super.testSeek(builder, mediaInformation);
  }

  @Test(dataProvider = "shortMedia")
  public void testSeekAtStart(Builder builder, MediaInformation mediaInformation) {
    super.testSeekAtStart(builder, mediaInformation);
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

  @Test(dataProvider = "longMedia")
  public void testRates(Builder builder, MediaInformation mediaInformation) {
    super.testRates(builder, mediaInformation);
  }
}
