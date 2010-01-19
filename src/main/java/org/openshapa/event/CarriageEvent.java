package org.openshapa.event;

import java.util.EventObject;

/**
 * Event object for a track carriage event
 */
public class CarriageEvent extends EventObject {

	public enum EventType {
		OFFSET_CHANGE,
		BOOKMARK_REQUEST
	}
	
    /** Track identifier */
    private String trackId;
    /** New time offset, in milliseconds, for the given track */
    private long offset;
    /** Duration of the track im milliseconds */
    private long duration;
    /** What event does this represent */
    private EventType eventType;

    public CarriageEvent(Object source, String trackId, long offset, 
    		long duration, EventType eventType) {
        super(source);
        this.trackId = trackId;
        this.offset = offset;
        this.duration = duration;
        this.eventType = eventType;
    }

    /**
     * @return New time offset in milliseconds.
     */
    public long getOffset() {
        return offset;
    }

    /**
     * @return Track identifier
     */
    public String getTrackId() {
        return trackId;
    }

    public long getDuration() {
        return duration;
    }
    
    public EventType getEventType() {
    	return eventType;
    }

}
