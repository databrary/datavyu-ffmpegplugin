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
package org.datavyu.event;

/**
 * Defines an interface for listening to events from a data playback controller.
 */
public interface PlaybackListener {

    /**
     * Add data button was used.
     *
     * @param The event to handle.
     */
    void addDataEvent(PlaybackEvent evt);

    /**
     * Set cell onset button was used.
     *
     * @param The event to handle.
     */
    void setCellOnsetEvent(PlaybackEvent evt);

    /**
     * Set cell offset button was used.
     *
     * @param The event to handle.
     */
    void setCellOffsetEvent(PlaybackEvent evt);

    /**
     * Go back button was used.
     *
     * @param The event to handle.
     */
    void goBackEvent(PlaybackEvent evt);

    /**
     * Rewind button was used.
     *
     * @param The event to handle.
     */
    void rewindEvent(PlaybackEvent evt);

    /**
     * Play button was used.
     *
     * @param The event to handle.
     */
    void playEvent(PlaybackEvent evt);

    /**
     * Forward button was used.
     *
     * @param The event to handle.
     */
    void forwardEvent(PlaybackEvent evt);

    /**
     * Shuttle back button was used.
     *
     * @param The event to handle.
     */
    void shuttleBackEvent(PlaybackEvent evt);

    /**
     * Stop button was used.
     *
     * @param The event to handle.
     */
    void stopEvent(PlaybackEvent evt);

    /**
     * Shuttle forward button was used.
     *
     * @param The event to handle.
     */
    void shuttleForwardEvent(PlaybackEvent evt);

    /**
     * Find button was used.
     *
     * @param The event to handle.
     */
    void findEvent(PlaybackEvent evt);

    /**
     * Jog back button was used.
     *
     * @param The event to handle.
     */
    void jogBackEvent(PlaybackEvent evt);

    /**
     * Pause button was used.
     *
     * @param The event to handle.
     */
    void pauseEvent(PlaybackEvent evt);

    /**
     * Jog forward button was used.
     *
     * @param The event to handle.
     */
    void jogForwardEvent(PlaybackEvent evt);

    /**
     * Create new cell and set onset button was used.
     *
     * @param The event to handle.
     */
    void newCellSetOnsetEvent(PlaybackEvent evt);

    /**
     * Set new cell offset button was used.
     *
     * @param The event to handle.
     */
    void setNewCellOffsetEvent(PlaybackEvent evt);

    /**
     * New cell button was used.
     *
     * @param The event to handle.
     */
    void newCellEvent(PlaybackEvent evt);

    /**
     * Show tracks button was used.
     *
     * @param The event to handle.
     */
    void showTracksEvent(PlaybackEvent evt);

}
