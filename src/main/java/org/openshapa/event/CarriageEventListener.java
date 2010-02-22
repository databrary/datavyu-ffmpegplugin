package org.openshapa.event;

import java.util.EventListener;

/**
 * Interface for defining event handlers for events that the TrackPainter may
 * fire.
 */
public interface CarriageEventListener extends EventListener {

    /**
     * Event handler for a track's changed offset
     * 
     * @param e
     */
    public void offsetChanged(CarriageEvent e);

    /**
     * Event handler for a track's bookmark request
     * 
     * @param e
     */
    public void requestBookmark(CarriageEvent e);

    /**
     * Event handler for a track requesting bookmark saving
     * 
     * @param e
     */
    public void saveBookmark(CarriageEvent e);

    /**
     * Event handler for a track's selected state change
     * 
     * @param e
     */
    public void selectionChanged(CarriageEvent e);

}
