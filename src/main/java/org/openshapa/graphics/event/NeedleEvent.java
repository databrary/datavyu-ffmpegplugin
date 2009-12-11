package org.openshapa.graphics.event;

import java.util.EventObject;

/**
 * Event object for needle that changed value.
 */
public class NeedleEvent extends EventObject {

    /** New time represented by the needle */
    private long time;

    public NeedleEvent(Object source, long time) {
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
