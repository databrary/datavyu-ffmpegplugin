package org.openshapa.event.component;

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
