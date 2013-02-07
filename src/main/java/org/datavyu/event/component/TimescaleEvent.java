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
package org.datavyu.event.component;

import java.util.EventObject;


/**
 * Event object for timescale event.
 */
public class TimescaleEvent extends EventObject {

    /** Jump time associated with event. */
    private long time;

    /** Starts playback if it is currently stopped (and vice versa). */
    private boolean togglePlaybackMode;
    
    public TimescaleEvent(final Object source, final long time, boolean togglePlaybackMode) {
        super(source);
        this.time = time;
        this.togglePlaybackMode = togglePlaybackMode;
    }

    /**
     * @return New time represented by the needle
     */
    public long getTime() {
        return time;
    }
    
    /**
     * @return True if the current playback mode should be toggled after performing the jump
     */
    public boolean getTogglePlaybackMode() {
    	return togglePlaybackMode;
    }
}
