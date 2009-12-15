package org.openshapa.event;

import java.util.EventObject;

/**
 * Event object for wrapping an intercepted event
 */
public class InterceptedEvent extends EventObject {

    /**
     * The type of event that was intercepted
     */
    public static enum EventType {
        MOUSE_ENTERED,  /** @see MouseEvent */
        MOUSE_MOVED,    /** @see MouseEvent */
        MOUSE_PRESSED,  /** @see MouseEvent */
        MOUSE_DRAGGED,  /** @see MouseEvent */
        MOUSE_RELEASED  /** @see MouseEvent */
    };

    /** The type of event that was intercepted. */
    private EventType event;
    /** The intercepted event */
    private EventObject interceptedEvent;

    public InterceptedEvent(Object source, EventType event,
            EventObject interceptedEvent) {
        super(source);
        this.event = event;
        this.interceptedEvent = interceptedEvent;
    }

    /**
     * @return What event was intercepted
     */
    public EventType getEvent() {
        return event;
    }

    /**
     * @return The event that was intercepted
     */
    public EventObject getInterceptedEvent() {
        return interceptedEvent;
    }

}
