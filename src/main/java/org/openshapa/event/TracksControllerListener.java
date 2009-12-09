package org.openshapa.event;

import java.util.EventListener;

/**
 * Interface for defining events that TracksController may fire.
 */
public interface TracksControllerListener extends EventListener {

    public void tracksControllerChanged(TracksControllerEvent e);

}
