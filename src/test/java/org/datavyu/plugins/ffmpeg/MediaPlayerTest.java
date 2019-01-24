package org.datavyu.plugins.ffmpeg;

import org.datavyu.plugins.ffmpeg.PlayerStateEvent.PlayerState;
import org.testng.Assert;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MediaPlayerTest {
    /** The total number of seeks that need to be processed during the test **/
    private static final int NUMBER_OF_SEEKS = 10;

    /** The Seek tolerance in seconds**/
    private static final double SEEK_TOLERANCE_IN_SECONDS = 0.100; // 100 ms

    interface Builder {
        MediaPlayerSync build();
    }

    static final MediaInformation SHORT_MEDIA = MediaInformation.create(
                "http://www.html5videoplayer.net/videos/toystory.mp4",
                0.0,
                149.95,
                640,
                360,
                29.97).get(); // fail hard if the link is malformed
    static final MediaInformation LONG_MEDIA = MediaInformation.create(
            "http://www.html5videoplayer.net/videos/big_buck_bunny.mp4",
            0.0,
            596.5,
            640,
            360,
            24).get();

    static final MediaInformation WRONG_MEDIA = new MediaInformation(
            new File("wrongFileName").toURI(),0.0, 0.0, 0, 0, 0.0);

    protected void testReadyState(Builder builder, MediaInformation mediaInformation) {
        MediaPlayerSync player = builder.build();
        Assert.assertTrue(player.getMediaPlayer().getState() == PlayerStateEvent.PlayerState.READY);
    }

    protected void testStateTransition(Builder builder, MediaInformation mediaInformation) {
        MediaPlayerSync player = builder.build();
        Assert.assertTrue(player.getMediaPlayer().getState() == PlayerState.READY);

        // Test READY -> PLAYING
        player.getMediaPlayer().play();
        sleep(200);
        Assert.assertTrue(player.getMediaPlayer().getState() == PlayerState.PLAYING);

        // Test PLAYING -> STOPPED
        player.getMediaPlayer().stop();
        sleep(200);
        Assert.assertTrue(player.getMediaPlayer().getState() == PlayerState.STOPPED);

        // Test STOPPED -> PLAYING
        player.getMediaPlayer().play();
        sleep(200);
        Assert.assertTrue(player.getMediaPlayer().getState() == PlayerState.PLAYING);

        // Test PLAYING -> PAUSED
        player.getMediaPlayer().pause();
        sleep(200);
        Assert.assertTrue(player.getMediaPlayer().getState() == PlayerState.PAUSED);

        // Test PAUSED -> PLAYING
        player.getMediaPlayer().play();
        sleep(200);
        Assert.assertTrue(player.getMediaPlayer().getState() == PlayerState.PLAYING);

        // Test PAUSED -> STOPPED
        player.getMediaPlayer().pause();
        player.getMediaPlayer().stop();
        sleep(200);
        Assert.assertTrue(player.getMediaPlayer().getState() == PlayerState.STOPPED);

        // Test STOPPED -> PAUSED
        player.getMediaPlayer().pause();
        sleep(200);
        Assert.assertTrue(player.getMediaPlayer().getState() == PlayerState.PAUSED);
    }

    protected void testMetadata(Builder builder, MediaInformation mediaInformation) {
        MediaPlayerSync player = builder.build();
        MediaPlayer mediaPlayer = player.getMediaPlayer();
        Assert.assertEquals(mediaPlayer.getDuration(), mediaInformation.getDuration(), 0.01);
        Assert.assertEquals(mediaPlayer.getImageWidth(), mediaInformation.getImageWidth());
        Assert.assertEquals(mediaPlayer.getImageHeight(), mediaInformation.getImageHeight());
        Assert.assertEquals(mediaPlayer.getFps(), mediaInformation.getFramesPerSecond(), 0.01);
    }

    protected void testWrongFile(Builder builder, MediaInformation mediaInformation) throws MediaException {
        builder.build();
    }

    protected void testSeek(Builder builder, MediaInformation mediaInformation) {
        MediaPlayerSync player = builder.build();
        double startTime = mediaInformation.getStartTime();
        double duration = mediaInformation.getDuration();
        createSeekTimes(startTime, duration).forEach(expectedTime -> {
            player.getMediaPlayer().seek(expectedTime);
            // TODO: Need to see how we can remove this sleeping
            sleep(100);
            double actualTime = player.getMediaPlayer().getPresentationTime();
            Assert.assertEquals(actualTime, expectedTime, SEEK_TOLERANCE_IN_SECONDS);
        });
    }

    protected void testSeekAtStart(Builder builder, MediaInformation mediaInformation) {
        MediaPlayerSync player = builder.build();
        Assert.assertNotEquals(player.getMediaPlayer().getPresentationTime(), Double.NaN);
        double duration = mediaInformation.getDuration();
        // Get a random time from the second half
        double startTime =  duration / 2;
        double seekTime = randomTime(startTime, duration);

        player.getMediaPlayer().seek(seekTime);
        sleep(100);

        double actualTime = player.getMediaPlayer().getPresentationTime();
        Assert.assertEquals(actualTime, seekTime, SEEK_TOLERANCE_IN_SECONDS);
    }

    protected void testStepForward(Builder builder, MediaInformation mediaInformation) {
        MediaPlayerSync player = builder.build();
        double startTime = mediaInformation.getStartTime();
        double duration = mediaInformation.getDuration();
        double seekTime = randomTime(startTime, duration);
        double delta = 1.0/mediaInformation.getFramesPerSecond();
        // Seek to random position
        player.getMediaPlayer().seek(seekTime);
        sleep(100);
        // We may not seek to the exact time
        double expectedTime = player.getMediaPlayer().getPresentationTime() + delta;
        //Assert.assertNotEquals(expectedTime, Double.NaN);

        player.getMediaPlayer().stepForward();
        sleep(200);
        double actualTime = player.getMediaPlayer().getPresentationTime();
        Assert.assertEquals(actualTime, expectedTime, 0.001);
    }

    protected void testStepForwardAtEnd(Builder builder, MediaInformation mediaInformation) {
        MediaPlayerSync player = builder.build();
        double startTime = mediaInformation.getStartTime();
        double duration = mediaInformation.getDuration();
        // TODO(fraudies): Need to fix seeking to end, then remove -1
        double seekTime = startTime + duration - 1;
        double delta = 1.0/mediaInformation.getFramesPerSecond();

        player.getMediaPlayer().seek(seekTime);
        sleep(300);
        double expectedTime = player.getMediaPlayer().getPresentationTime();
        //Assert.assertNotEquals(expectedTime, Double.NaN);

        player.getMediaPlayer().stepForward();
        sleep(100);
        double actualTime = player.getMediaPlayer().getPresentationTime();
        Assert.assertEquals(actualTime, expectedTime, delta);
    }

    protected void testStepBackward(Builder builder, MediaInformation mediaInformation) {
        MediaPlayerSync player = builder.build();
        double startTime = mediaInformation.getStartTime();
        double duration = mediaInformation.getDuration();
        double seekTime = randomTime(startTime, duration);
        double delta = 1.0/mediaInformation.getFramesPerSecond();

        player.getMediaPlayer().seek(seekTime + delta);
        sleep(300);
        double expectedTime = player.getMediaPlayer().getPresentationTime();
        //Assert.assertNotEquals(expectedTime, Double.NaN);

        player.getMediaPlayer().stepBackward();
        double actualTime = player.getMediaPlayer().getPresentationTime();
        Assert.assertEquals(actualTime, expectedTime, 0.001);
    }

    protected void testStepBackwardAtStart(Builder builder, MediaInformation mediaInformation) {
        MediaPlayerSync player = builder.build();
        double startTime = mediaInformation.getStartTime();
        player.getMediaPlayer().seek(startTime);
        sleep(300);
        double expectedTime = player.getMediaPlayer().getPresentationTime();
        //Assert.assertNotEquals(expectedTime, Double.NaN);

        player.getMediaPlayer().stepBackward();
        double actualTime = player.getMediaPlayer().getPresentationTime();
        Assert.assertEquals(actualTime, expectedTime, 0.001);
    }

    // Create seek times including the upper/lower bound in the stream
    private static List<Double> createSeekTimes(double startTime, double duration) {
        return IntStream.rangeClosed(0, NUMBER_OF_SEEKS-1).asDoubleStream()
                .map(seek -> {
                    if (seek == 0) {
                        return startTime;
                    }
                    if (seek == NUMBER_OF_SEEKS - 1) {
                        // TODO(fraudies): Remove the -1 once we removed relying on forward playback
                        return startTime + duration - 1;
                    }
                    return randomTime(startTime, duration);
                }).boxed().collect(Collectors.toList());
    }

    private static double randomTime(double startTime, double duration) {
        return (startTime + (Math.random() * (duration - startTime)));
    }

    private void sleep(final long timeInMillis) {
        try {
            Thread.sleep(timeInMillis);
        } catch (InterruptedException e) { /* normal */ }
    }
}
