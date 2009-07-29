package org.uispec4j;

/**
 * Used to parse timestamp (onset, offset) for elements
 *
 */
public class Timestamp {

    /**
     * the timestamp that Timestamp parses.
     */
    private String timestamp;

    /**
     * Parts of timestamp.
     */
    private Integer hrs, mins, secs, ms;

    /**
     * Timestamp constructor.
     * @param time timestamp (onset, offset)
     */
    public Timestamp(final String time) {
        this.timestamp = time;
        String[] timeparts = time.split(":");
        hrs = Integer.parseInt(timeparts[0]);
        mins = Integer.parseInt(timeparts[1]);
        secs = Integer.parseInt(timeparts[2]);
        ms = Integer.parseInt(timeparts[3]);
    }

    @Override
    public String toString() {
        return timestamp;
    }

    /**
     * Returns Hours in timestamp.
     * @return Integer hours
     */
    public final Integer getHours() {
        return hrs;
    }

    /**
     * Returns Minutes in timestamp.
     * @return Integer minutes
     */
    public final Integer getMinutes() {
        return mins;
    }

    /**
     * Returns Seconds in timestamp.
     * @return Integer seconds
     */
    public final Integer getSeconds() {
        return secs;
    }

    /**
     * Returns Milliseconds in timestamp.
     * @return Integer milliseconds
     */
    public final Integer getMilliseconds() {
        return ms;
    }

}
