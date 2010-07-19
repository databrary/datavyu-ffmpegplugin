package org.openshapa.plugins.spectrum.events;

/**
 * Interface for notifying listeners about a time.
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
