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

import java.awt.event.InputEvent;
import java.util.EventObject;


/**
 * Contains information about a playback control event.
 */
public final class PlaybackEvent extends EventObject {

    /**
     * Modifiers associated with this event.
     */
    private final int modifiers;
    /**
     * Type of playback control.
     */
    private final PlaybackType type;
    /**
     * The go time.
     */
    private final long goTime;
    /**
     * Onset time.
     */
    private final long onsetTime;
    /**
     * Offset time.
     */
    private final long offsetTime;

    /**
     * Constructs a new playback event.
     *
     * @param source     Source object.
     * @param type       Event type.
     * @param goTime     Go field time represented by the event.
     * @param onsetTime  Onset time represented by the event.
     * @param offsetTime Offset time represented by the event.
     * @param modifiers  Event modifiers.
     */
    public PlaybackEvent(final Object source, final PlaybackType type,
                         final long goTime, final long onsetTime, final long offsetTime,
                         final int modifiers) {
        super(source);
        this.type = type;
        this.goTime = goTime;
        this.onsetTime = onsetTime;
        this.offsetTime = offsetTime;
        this.modifiers = modifiers;
    }

    /**
     * @return Type of playback event.
     */
    public PlaybackType getType() {
        return type;
    }

    /**
     * @return The Go field time.
     */
    public long getGoTime() {
        return goTime;
    }

    /**
     * @return The Onset field time.
     */
    public long getOnsetTime() {
        return onsetTime;
    }

    /**
     * @return The Offset field time.
     */
    public long getOffsetTime() {
        return offsetTime;
    }

    /**
     * @return Modifier masks associated with this event.
     * @see {@link InputEvent#getModifiers()}
     */
    public int getModifiers() {
        return modifiers;
    }

    /**
     * Enumeration of playback types.
     */
    public enum PlaybackType {
        ADD_DATA, SET_CELL_ONSET, SET_CELL_OFFSET, GO_BACK,
        REWIND, PLAY, FORWARD, SHUTTLE_BACK, STOP, SHUTTLE_FORWARD, FIND,
        JOG_BACK, PAUSE, JOG_FORWARD, NEW_CELL_SET_ONSET, NEW_CELL_OFFSET,
        NEW_CELL, SHOW_TRACKS, SEEK
    }

}
