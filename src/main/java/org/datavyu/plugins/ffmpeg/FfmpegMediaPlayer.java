package org.datavyu.plugins.ffmpeg;

import org.datavyu.util.MediaTimerTask;
import org.datavyu.util.Subject;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.net.URI;

/**
 * Uses ffmpeg to decode and transcode (optional) image and audio data
 */
public abstract class FfmpegMediaPlayer extends NativeMediaPlayer {
    float mutedVolume = 1.0f;  // last volume before mute
    boolean muteEnabled = false;

    MediaTimerTask mediaTimerTask = null;
    boolean isUpdateTimeEnabled = false;
    // The current time is not the presentation time, the current
    // time is used to periodically synchronize to a master a clock.
    double streamCurrentTime;
    double streamPrevTime = -1.0;
    double masterCurrentTime;

    Subject masterClock;

    /**
     * Create an ffmpeg media player instance
     *
     * @param mediaPath The path to the media
     */
    public FfmpegMediaPlayer(URI mediaPath) {
        super(mediaPath);
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
        if (isUpdateTimeEnabled
                || Double.compare(playerGetPresentationTime(), streamPrevTime) != 0 ) {
            double presentationTime = playerGetPresentationTime();
            if (presentationTime >= 0.0) {
                double newTime = presentationTime;
                if (Double.compare(newTime, streamPrevTime) != 0) {
                    if(Math.abs(newTime - masterCurrentTime) >= SYNC_THRESHOLD){
                        seek(masterCurrentTime);
                        streamCurrentTime = masterCurrentTime;
                    } else {
                        streamCurrentTime = newTime;
                    }
                    streamPrevTime = newTime;
                }
            }
        }
    }

    protected void createMediaTimer() {
        synchronized (MediaTimerTask.timerLock) {
            if (mediaTimerTask == null) {
                mediaTimerTask = new MediaTimerTask(this);
                mediaTimerTask.start();
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
        double masterClockTime = (double) masterClock.getTimeUpdate(this);
        if(masterClockTime != 0.0){
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
    public void setSubject(Subject masterClock) {
        this.masterClock = masterClock;
    }
}
