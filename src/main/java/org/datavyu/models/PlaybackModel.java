/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.datavyu.models;

import org.datavyu.models.component.ViewportStateImpl;

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
    private long maxDuration = ViewportStateImpl.MINIMUM_MAX_END;

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
        this.maxDuration = Math.max(maxDuration, ViewportStateImpl.MINIMUM_MAX_END);
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
