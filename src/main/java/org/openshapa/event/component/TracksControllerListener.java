package org.openshapa.event.component;

import java.util.EventListener;

/**
 * Interface for defining events that TracksController may fire.
 */
public interface TracksControllerListener extends EventListener {

    /**
     * Event handler for when the tracks controller changed
     * 
     * @param e
     */
    void tracksControllerChanged(TracksControllerEvent e);

}
