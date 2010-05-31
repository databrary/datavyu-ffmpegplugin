package org.openshapa.event.component;

import java.util.EventObject;


/**
 * Event object for timescale event.
 */
public class TimescaleEvent extends EventObject {

    /** Jump time associated with event. */
    private long time;

    public TimescaleEvent(final Object source, final long time) {
        super(source);
        this.time = time;
    }

    /**
     * @return New time represented by the needle
     */
    public long getTime() {
        return time;
    }
}
