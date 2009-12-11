package org.openshapa.event;

import java.util.EventObject;

/**
 * Event object used to inform listeners about child component events
 */
public class TracksControllerEvent extends EventObject {

    public static enum TracksEvent {
        NEEDLE_EVENT,   /** @see NeedleEvent */
        MARKER_EVENT    /** @see MarkerEvent */
    }

    /** Needle event from child component */
    private EventObject eventObject;
    /** Type of track event that happened */
    private TracksEvent tracksEvent;

    public TracksControllerEvent(Object source, TracksEvent tracksEvent, 
            EventObject eventObject) {
        super(source);
        this.eventObject = eventObject;
        this.tracksEvent = tracksEvent;
    }

    /**
     * @return Needle event from child component
     */
    public EventObject getEventObject() {
        return eventObject;
    }

    /**
     * @return Type of track event that happened
     */
    public TracksEvent getTracksEvent() {
        return tracksEvent;
    }

}
