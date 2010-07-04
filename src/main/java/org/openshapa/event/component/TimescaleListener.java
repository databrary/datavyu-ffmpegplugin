package org.openshapa.event.component;

import java.util.EventListener;


/**
 * Interface for defining the event handlers for events that the Timescale
 * may fire.
 */
public interface TimescaleListener extends EventListener {

    void jumpToTime(TimescaleEvent e);
    
}
