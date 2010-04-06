package org.openshapa.models;

/**
 * Model representing playback data.
 */
public final class PlaybackModel {

    /** Stores the highest frame rate for all available viewers. */
    private float currentFPS = 1F;

    /** Index of current shuttle rate. */
    private int shuttleRate;

    /** The rate to use when resumed from pause. */
    private float pauseRate;

    /** The time the last sync was performed. */
    private long lastSync;

    /** The maximum duration out of all data being played. */
    private long maxDuration;

    /** Are we currently faking playback of the viewers? */
    private boolean fakePlayback = false;

    /** The start time of the playback window. */
    private long windowPlayStart;

    /** The end time of the playback window. */
    private long windowPlayEnd;

    public float getCurrentFPS() {
        return currentFPS;
    }

    public void setCurrentFPS(final float currentFPS) {
        this.currentFPS = currentFPS;
    }

    public int getShuttleRate() {
        return shuttleRate;
    }

    public void setShuttleRate(final int shuttleRate) {
        this.shuttleRate = shuttleRate;
    }

    public float getPauseRate() {
        return pauseRate;
    }

    public void setPauseRate(final float pauseRate) {
        this.pauseRate = pauseRate;
    }

    public long getLastSync() {
        return lastSync;
    }

    public void setLastSync(final long lastSync) {
        this.lastSync = lastSync;
    }

    public long getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(final long maxDuration) {
        this.maxDuration = maxDuration;
    }

    public boolean isFakePlayback() {
        return fakePlayback;
    }

    public void setFakePlayback(final boolean fakePlayback) {
        this.fakePlayback = fakePlayback;
    }

    public long getWindowPlayStart() {
        return windowPlayStart;
    }

    public void setWindowPlayStart(final long windowPlayStart) {
        this.windowPlayStart = windowPlayStart;
    }

    public long getWindowPlayEnd() {
        return windowPlayEnd;
    }

    public void setWindowPlayEnd(final long windowPlayEnd) {
        this.windowPlayEnd = windowPlayEnd;
    }

}
