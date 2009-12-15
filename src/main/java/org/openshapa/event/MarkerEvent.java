package org.openshapa.event;

import java.util.EventObject;

/**
 * Event object for changed region marker
 */
public class MarkerEvent extends EventObject {

    public static enum Marker {
        START_MARKER,
        END_MARKER
    }

    /** The marker that was moved */
    private Marker marker;
    /** The new time represented by the moved marker */
    private long time;

    public MarkerEvent(Object source, Marker marker, long time) {
        super(source);
        this.marker = marker;
        this.time = time;
    }

    /**
     * @return the marker that was moved
     */
    public Marker getMarker() {
        return marker;
    }

    /**
     * @return the new time represented by the moved marker
     */
    public long getTime() {
        return time;
    }

}
