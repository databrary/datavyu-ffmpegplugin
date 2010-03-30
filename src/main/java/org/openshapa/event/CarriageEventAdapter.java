package org.openshapa.event;

/**
 * Abstract class for handling carriage events.
 */
public abstract class CarriageEventAdapter implements CarriageEventListener {

    /**
     * @see org.openshapa.event.CarriageEventListener
     * #offsetChanged(org.openshapa.event.CarriageEvent)
     */
    public void offsetChanged(final CarriageEvent e) {
        // Blank implementation.
    }

    /**
     * @see org.openshapa.event.CarriageEventListener
     * #requestBookmark(org.openshapa.event.CarriageEvent)
     */
    public void requestBookmark(final CarriageEvent e) {
        // Blank implementation.
    }

    /**
     * @see org.openshapa.event.CarriageEventListener
     * #saveBookmark(org.openshapa.event.CarriageEvent)
     */
    public void saveBookmark(final CarriageEvent e) {
        // Blank implementation.
    }

    /**
     * @see org.openshapa.event.CarriageEventListener
     * #selectionChanged(org.openshapa.event.CarriageEvent)
     */
    public void selectionChanged(final CarriageEvent e) {
        // Blank implementation.
    }

}
