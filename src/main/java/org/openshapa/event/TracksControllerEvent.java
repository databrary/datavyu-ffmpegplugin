package org.openshapa.event;

import java.util.EventObject;
import org.openshapa.graphics.event.NeedleEvent;

/**
 * Event object used to inform listeners about child component events
 */
public class TracksControllerEvent extends EventObject {

    /** Needle event from child component */
    private NeedleEvent needleEvent;

    public TracksControllerEvent(Object source, NeedleEvent needleEvent) {
        super(source);
        this.needleEvent = needleEvent;
    }

    /**
     * @return Needle event from child component
     */
    public NeedleEvent getNeedleEvent() {
        return needleEvent;
    }

}
