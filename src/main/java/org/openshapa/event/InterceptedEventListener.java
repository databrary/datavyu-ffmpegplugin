package org.openshapa.event;

import java.util.EventListener;

/**
 * Interface for defining the event handlers for events that the InterceptorPane
 * may fire.
 */
public interface InterceptedEventListener extends EventListener {

    /**
     * Event handler for an intercepted event.
     * @param e
     */
    public void eventIntercepted(InterceptedEvent e);

}
