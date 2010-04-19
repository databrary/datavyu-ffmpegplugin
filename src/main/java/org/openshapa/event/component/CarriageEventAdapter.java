package org.openshapa.event.component;

/**
 * Abstract class for handling carriage events.
 */
public abstract class CarriageEventAdapter implements CarriageEventListener {

    /**
     * @see org.openshapa.event.component.CarriageEventListener
     * #offsetChanged(org.openshapa.event.component.CarriageEvent)
     */
    public void offsetChanged(final CarriageEvent e) {
        // Blank implementation.
    }

    /**
     * @see org.openshapa.event.component.CarriageEventListener
     * #requestBookmark(org.openshapa.event.component.CarriageEvent)
     */
    public void requestBookmark(final CarriageEvent e) {
        // Blank implementation.
    }

    /**
     * @see org.openshapa.event.component.CarriageEventListener
     * #saveBookmark(org.openshapa.event.component.CarriageEvent)
     */
    public void saveBookmark(final CarriageEvent e) {
        // Blank implementation.
    }

    /**
     * @see org.openshapa.event.component.CarriageEventListener
     * #selectionChanged(org.openshapa.event.component.CarriageEvent)
     */
    public void selectionChanged(final CarriageEvent e) {
        // Blank implementation.
    }

}
