package org.openshapa.event;

import java.util.EventListener;

/**
 * Interface for defining event handlers for events that the TrackPainter may
 * fire.
 */
public interface CarriageEventListener extends EventListener {

    /**
     * Event handler for a track's changed offset
     * @param e
     */
    public void offsetChanged(CarriageEvent e);

}
