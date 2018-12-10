package org.datavyu.plugins.ffmpeg;

import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MediaPlayerTest {
    /** The total number of seeks that need to be processed during the test **/
    private static final int NUMBER_OF_SEEKS = 10;

    /** The Seek tolerance in seconds**/
    private static final double SEEK_TOLERANCE_IN_SECONDS = 0.100; // 100 ms

    static final List<MediaInformation> MEDIA = (new ArrayList<Optional<MediaInformation>>() {{
        add(MediaInformation.create(
                "http://www.html5videoplayer.net/videos/toystory.mp4",
                0.0,
                149.95,
                640,
                360,
                29.97
        ));
    }}).stream().flatMap(o -> o.isPresent() ? Stream.of(o.get()) : Stream.empty()).collect(Collectors.toList());

    protected void testReadyState(MediaPlayerSync player, MediaInformation mediaInformation) {
        Assert.assertTrue(player.getMediaPlayer().getState() == PlayerStateEvent.PlayerState.READY);
    }

    protected void testMetadata(MediaPlayerSync player, MediaInformation mediaInformation) {
        MediaPlayer mediaPlayer = player.getMediaPlayer();
        Assert.assertEquals(mediaPlayer.getDuration(), mediaInformation.getDuration(), 0.01);
        Assert.assertEquals(mediaPlayer.getImageWidth(), mediaInformation.getImageWidth());
        Assert.assertEquals(mediaPlayer.getImageHeight(), mediaInformation.getImageHeight());
        Assert.assertEquals(mediaPlayer.getFps(), mediaInformation.getFramesPerSecond(), 0.01);
    }

    protected void testSeek(MediaPlayerSync player, MediaInformation mediaInformation) {
        double startTime = mediaInformation.getStartTime();
        double duration = mediaInformation.getDuration();
        List<Double> seekTimes = createSeekTimes(startTime, duration);
        seekTimes.forEach(expectedTime -> {
            player.waitForSeek(expectedTime);
            // TODO: Need to see how we can remove this sleeping
            sleep(100);
            double actualTime = player.getMediaPlayer().getPresentationTime();
            Assert.assertEquals(actualTime, expectedTime, SEEK_TOLERANCE_IN_SECONDS);
        });
    }

    protected void testStepForward(MediaPlayerSync player, MediaInformation mediaInformation) {
        double startTime = mediaInformation.getStartTime();
        double duration = mediaInformation.getDuration();
        double seekTime = randomTime(startTime, duration);
        double delta = 1.0/mediaInformation.getFramesPerSecond();
        // Seek to random position
        player.waitForSeek(seekTime);
        sleep(100);
        // We may not seek to the exact time
        double expectedTime = player.getMediaPlayer().getPresentationTime() + delta;

        player.getMediaPlayer().stepForward();
        sleep(200);
        double actualTime = player.getMediaPlayer().getPresentationTime();
        Assert.assertEquals(actualTime, expectedTime, 0.001);
    }

    protected void testStepForwardAtEnd(MediaPlayerSync player, MediaInformation mediaInformation) {
        double startTime = mediaInformation.getStartTime();
        double duration = mediaInformation.getDuration();
        // TODO(fraudies): Need to fix seeking to end, then remove -1
        double seekTime = startTime + duration - 1;
        double delta = 1.0/mediaInformation.getFramesPerSecond();

        player.waitForSeek(seekTime);
        sleep(100);
        double expectedTime = player.getMediaPlayer().getPresentationTime();
        player.getMediaPlayer().stepForward();
        sleep(100);
        double actualTime = player.getMediaPlayer().getPresentationTime();
        Assert.assertEquals(actualTime, expectedTime, delta);
    }

    protected void testStepBackward(MediaPlayerSync player, MediaInformation mediaInformation) {
        double startTime = mediaInformation.getStartTime();
        double duration = mediaInformation.getDuration();
        double seekTime = randomTime(startTime, duration);
        double delta = 1.0/mediaInformation.getFramesPerSecond();

        player.waitForSeek(seekTime + delta);
        sleep(100);
        double expectedTime = player.getMediaPlayer().getPresentationTime();
        player.getMediaPlayer().stepBackward();
        sleep(200);
        double actualTime = player.getMediaPlayer().getPresentationTime();
        Assert.assertEquals(actualTime, expectedTime, 0.001);
    }

    protected void testStepBackwardAtStart(MediaPlayerSync player, MediaInformation mediaInformation) {
        double startTime = mediaInformation.getStartTime();
        double delta = 1.0/mediaInformation.getFramesPerSecond();
        player.waitForSeek(startTime);
        sleep(100);
        double expectedTime = player.getMediaPlayer().getPresentationTime();
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
        return startTime + Math.random() * duration;
    }

    private void sleep(final long timeInMillis) {
        try {
            Thread.sleep(timeInMillis);
        } catch (InterruptedException e) { /* normal */ }
    }

}
