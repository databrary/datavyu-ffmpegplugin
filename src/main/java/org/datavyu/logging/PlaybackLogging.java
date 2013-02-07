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
package org.datavyu.logging;

import com.usermetrix.jclient.Logger;
import org.datavyu.event.PlaybackEvent;
import org.datavyu.event.PlaybackListener;

import com.usermetrix.jclient.UserMetrix;


/**
 * Implements playback usage logging.
 */
public class PlaybackLogging implements PlaybackListener {

    private final Logger logger = UserMetrix.getLogger(
            PlaybackLogging.class);

    @Override
    public void addDataEvent(final PlaybackEvent evt) {
        logger.event("Add data");
    }

    @Override
    public void findEvent(final PlaybackEvent evt) {
        logger.event("Find");
    }

    @Override
    public void forwardEvent(final PlaybackEvent evt) {
        logger.event("Fast forward");
    }

    @Override
    public void goBackEvent(final PlaybackEvent evt) {
        logger.event("Go back");
    }

    @Override
    public void jogBackEvent(final PlaybackEvent evt) {
        logger.event("Jog back");
    }

    @Override
    public void jogForwardEvent(final PlaybackEvent evt) {
        logger.event("Jog forward");
    }

    @Override
    public void newCellEvent(final PlaybackEvent evt) {
        logger.event("New cell");
    }

    @Override
    public void newCellSetOnsetEvent(final PlaybackEvent evt) {
        logger.event("New cell set onset");
    }

    @Override
    public void pauseEvent(final PlaybackEvent evt) {
        logger.event("Pause");
    }

    @Override
    public void playEvent(final PlaybackEvent evt) {
        logger.event("Play");
    }

    @Override
    public void rewindEvent(final PlaybackEvent evt) {
        logger.event("Rewind");
    }

    @Override
    public void setCellOffsetEvent(final PlaybackEvent evt) {
        logger.event("Set cell offset");
    }

    @Override
    public void setCellOnsetEvent(final PlaybackEvent evt) {
        logger.event("Set cell onset");
    }

    @Override
    public void setNewCellOffsetEvent(final PlaybackEvent evt) {
        logger.event("Set new cell offset");
    }

    @Override
    public void showTracksEvent(final PlaybackEvent evt) {
        logger.event("Show tracks");
    }

    @Override
    public void shuttleBackEvent(final PlaybackEvent evt) {
        logger.event("Shuttle back");
    }

    @Override
    public void shuttleForwardEvent(final PlaybackEvent evt) {
        logger.event("Shuttle forward");
    }

    @Override
    public void stopEvent(final PlaybackEvent evt) {
        logger.event("Stop event");
    }
}
