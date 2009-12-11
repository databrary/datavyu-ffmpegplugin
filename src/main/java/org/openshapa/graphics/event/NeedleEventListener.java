package org.openshapa.graphics.event;

import java.util.EventListener;

/**
 * Interface for defining the events that the NeedlePainter may fire.
 */
public interface NeedleEventListener extends EventListener {

    /**
     * Event for the timing needle being moved by the mouse
     * @param e Event object associated with this event
     */
    public void needleMoved(NeedleEvent e);

}
