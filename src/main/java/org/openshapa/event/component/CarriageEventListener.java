package org.openshapa.event.component;

import java.util.EventListener;


/**
 * Interface for defining event handlers for events that the TrackPainter may
 * fire.
 */
public interface CarriageEventListener extends EventListener {

    /**
     * Event handler for a track's changed offset.
     *
     * @param e The event to handle.
     */
    void offsetChanged(CarriageEvent e);

    /**
     * Event handler for a track's bookmark request.
     *
     * @param e The event to handle.
     */
    void requestBookmark(CarriageEvent e);

    /**
     * Event handler for a track requesting bookmark saving.
     *
     * @param e The event to handle.
     */
    void saveBookmark(CarriageEvent e);

    /**
     * Event handler for a track's selected state change.
     *
     * @param e The event to handle.
     */
    void selectionChanged(CarriageEvent e);

}
