package org.openshapa.event;

import java.util.EventListener;

/**
 * Interface for defining the event handlers for events that the RegionPainter
 * may fire.
 */
public interface MarkerEventListener extends EventListener {

    /**
     * Event handler for the region marker being moved.
     * @param e
     */
    public void markerMoved(MarkerEvent e);
    
}
