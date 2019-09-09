package org.datavyu.plugins.ffmpeg;

import org.datavyu.plugins.MediaException;
import org.datavyu.plugins.MediaPlayer;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/** Tests the ffmpeg player using an AWT window for display */
public class FfmpegSdlMediaPlayerTest extends MediaPlayerTest {

  public static class FfmpegBuilder implements Builder {
    private MediaInformation mediaInformation;

    FfmpegBuilder() {}

    FfmpegBuilder withMedia(MediaInformation mediaInformation) {
      this.mediaInformation = mediaInformation;
      return this;
    }

    @Override
    public MediaPlayerSync build() {
      MediaPlayer mediaPlayer =
          new FfmpegSdlMediaPlayer(mediaInformation.getLocalPath());
      return MediaPlayerSync.createMediaPlayerSync(mediaPlayer);
    }
  }

  @DataProvider(name = "shortMedia")
  public Object[][] createPlayerWithShortMedia() {
    return new Object[][] {
      {new FfmpegBuilder().withMedia(SHORT_MEDIA), SHORT_MEDIA}
    };
  }

  @DataProvider(name = "longMedia")
  public Object[][] createPlayerWithLongMedia() {
    return new Object[][] {
      {new FfmpegBuilder().withMedia(LONG_MEDIA), LONG_MEDIA}
    };
  }

  @DataProvider(name = "wrongMedia")
  public Object[][] createPlayerWithWrongMedia() {
    return new Object[][] {
      {new FfmpegBuilder().withMedia(WRONG_MEDIA), WRONG_MEDIA}
    };
  }

  @Test(dataProvider = "shortMedia")
  public void testReadyState(Builder builder, MediaInformation mediaInformation) {
    super.testReadyState(builder, mediaInformation);
  }

  @Test(dataProvider = "shortMedia")
  public void testStateTransition(Builder builder, MediaInformation mediaInformation) {
    super.testStateTransition(builder, mediaInformation);
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

  @Test(dataProvider = "longMedia")
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
