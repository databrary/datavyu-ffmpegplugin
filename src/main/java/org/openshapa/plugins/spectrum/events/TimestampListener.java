package org.openshapa.plugins.spectrum.events;

/**
 * Interface for notifying listeners about a time.
 * TODO review for removal.
 */
public interface TimestampListener {

    /**
     * Notify listener of the given time in milliseconds.
     *
     * @param time
     *            Time in milliseconds.
     */
    void notifyTime(long time);

}
