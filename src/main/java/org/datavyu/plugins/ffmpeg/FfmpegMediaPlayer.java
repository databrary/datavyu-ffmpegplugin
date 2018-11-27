package org.datavyu.plugins.ffmpeg;

import org.datavyu.util.MasterClock;
import org.datavyu.util.MediaTimerTask;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.net.URI;

/**
 * Uses ffmpeg to decode and transcode (optional) image and audio data
 */
public abstract class FfmpegMediaPlayer extends NativeMediaPlayer {

    private PlayerStateListener stateListener;

    float mutedVolume = 1.0f;  // last volume before mute
    boolean muteEnabled = false;

    MediaTimerTask mediaTimerTask = null;
    boolean isUpdateTimeEnabled = false;
    // The current time is not the presentation time, the current
    // time is used to periodically synchronize to a master a clock.
    double playerCurrentTime;
    double playerPreviousTime = -1.0;
    double masterCurrentTime;

    MasterClock masterClock;

    /**
     * Create an ffmpeg media player instance
     *
     * @param mediaPath The path to the media
     */
    public FfmpegMediaPlayer(URI mediaPath) {
        super(mediaPath);
        stateListener = new FfmpegPlayerStateListener();
        this.addMediaPlayerStateListener(stateListener);
    }

    void throwMediaErrorException(int code, Throwable cause)
            throws MediaException {
        MediaError me = MediaError.getFromCode(code);
        throw new MediaException(me.description(), cause, me);
    }

    @Override
    protected boolean playerGetMute() throws MediaException {
        return muteEnabled;
    }

    @Override
    protected synchronized void playerSetMute(boolean enable) throws MediaException {
        if (enable != muteEnabled) {
            if (enable) {
                // Cache the current volume.
                float currentVolume = getVolume();

                // Set the volume to zero.
                playerSetVolume(0);

                // Set the mute flag. It is necessary to do this after
                // calling setVolume() as otherwise the volume will not
                // be set to zero.
                muteEnabled = true;

                // Save the pre-mute volume.
                mutedVolume = currentVolume;
            } else {
                // Unset the mute flag. It is necessary to do this before
                // calling setVolume() as otherwise the volume will not
                // be set to the cached value.
                muteEnabled = false;

                // Set the volume to the cached value.
                playerSetVolume(mutedVolume);
            }
        }
    }

    @Override
    protected synchronized void playerUpdateCurrentTime() {
        double presentationTime = playerGetPresentationTime();

        if(presentationTime >= 0.0
            && (Double.compare(presentationTime, playerPreviousTime) != 0
            || isUpdateTimeEnabled)) {

            if(Math.abs(presentationTime - masterCurrentTime) >= SYNC_THRESHOLD) {
                seek(masterCurrentTime);
                playerCurrentTime = masterCurrentTime;
            } else {
                playerCurrentTime = presentationTime;
            }
            playerPreviousTime = presentationTime;
        }
    }

    protected void createMediaTimer() {
        synchronized (MediaTimerTask.timerLock) {
            if (mediaTimerTask == null) {
                mediaTimerTask = new MediaTimerTask(this);
                mediaTimerTask.start();
                isUpdateTimeEnabled = true;
            }
        }
    }

    protected void destroyMediaTimer() {
        synchronized (MediaTimerTask.timerLock) {
            if (mediaTimerTask != null) {
                isUpdateTimeEnabled = false;
                mediaTimerTask.stop();
                mediaTimerTask = null;
            }
        }
    }

    @Override
    public void updateMasterTime() {
        double masterClockTime = masterClock.getTimeUpdate(this);
        if(masterClockTime != masterCurrentTime){
            masterCurrentTime = masterClockTime/1000;
        }
    }

    @Override
    public void updateMasterMinTime() {
        //TODO: Add a marker to the media
        throw new NotImplementedException();
    }

    @Override
    public void updateMasterMaxTime() {
        //TODO: Add a marker to the media
        throw new NotImplementedException();
    }

    @Override
    public void setMasterClock(MasterClock masterMasterClock) {
        this.masterClock = masterMasterClock;
        createMediaTimer();
    }

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
        public void onStall(PlayerStateEvent evt) { }

        @Override
        public void onFinish(PlayerStateEvent evt) { }

        @Override
        public void onHalt(PlayerStateEvent evt) { }
    }
}
