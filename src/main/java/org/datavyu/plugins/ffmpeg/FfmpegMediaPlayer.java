package org.datavyu.plugins.ffmpeg;

import org.datavyu.plugins.MediaException;
import org.datavyu.plugins.PlayerStateEvent;
import org.datavyu.plugins.PlayerStateListener;
import java.net.URI;

/**
 * Uses ffmpeg to decode and transcode (optional) image and audio data
 */
abstract class FfmpegMediaPlayer extends DatavyuMediaPlayer {

    protected static final int SEEK_ACCURATE_FLAG = 0x01;
    protected static final int SEEK_FAST_FLAG = 0x10;

    private PlayerStateListener stateListener;

    /**
     * Create an ffmpeg media player instance
     *
     * @param mediaPath The path to the media
     */
    protected FfmpegMediaPlayer(URI mediaPath) {
        super(mediaPath);
        stateListener = new FfmpegPlayerStateListener();
        this.addMediaPlayerStateListener(stateListener);
    }


    @Override
    protected void playerPlay() throws MediaException {
    }

    @Override
    protected void playerSeek(double streamTime) throws MediaException {
        // In most cases seek accurate, with the exception of large backward playback rates
        int seek_flag = (!isStartTimeUpdated && getRate() < -1) ? SEEK_FAST_FLAG : SEEK_ACCURATE_FLAG;
        ffmpegPlayerSeek(streamTime, seek_flag);
    }

    @Override
    protected void playerSetStartTime(double startTime) throws MediaException {
        ffmpegPlayerSeek(startTime, SEEK_ACCURATE_FLAG);
    }

    protected abstract void ffmpegPlayerSeek(double streamTime, int seek_flag) throws MediaException;

    class FfmpegPlayerStateListener implements PlayerStateListener {

        @Override
        public void onReady(PlayerStateEvent evt) { }

        @Override
        public void onPlaying(PlayerStateEvent evt) {
            isUpdateTimeEnabled = true;
        }

        @Override
        public void onPause(PlayerStateEvent evt) {
            isUpdateTimeEnabled = false;
        }

        @Override
        public void onStop(PlayerStateEvent evt) {
            isUpdateTimeEnabled = false;
        }

        @Override
        public void onStall(PlayerStateEvent evt) {
        }

        @Override
        public void onFinish(PlayerStateEvent evt) {
        }

        @Override
        public void onHalt(PlayerStateEvent evt) {
        }
    }
}
