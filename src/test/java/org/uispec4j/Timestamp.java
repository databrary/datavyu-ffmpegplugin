package org.uispec4j;

/**
 * Used to parse timestamp (onset, offset) for elements.
 *
 */
public class Timestamp {

    /**
     * Parts of timestamp.
     */
    private int hrs, mins, secs, ms;

    /**
     * Milliseconds in an hour.
     */
    private static final int MS_IN_HOUR = 1000 * 60 * 60;

    /**
     * Milliseconds in a minute.
     */
    private static final int MS_IN_MINS = 1000 * 60;

     /**
     * Milliseconds in a second.
     */
    private static final int MS_IN_SEC = 1000;

    /**
     * Timestamp constructor.
     * @param time timestamp (onset, offset)
     */
    public Timestamp(final String time) {
        String[] timeparts = time.split(":");
        hrs = Integer.parseInt(timeparts[0]);
        mins = Integer.parseInt(timeparts[1]);
        secs = Integer.parseInt(timeparts[2]);
        ms = Integer.parseInt(timeparts[3]);
    }

    @Override
    public final String toString() {
        String hours = "0" + hrs;
        String minutes = "0" + mins;
        String seconds = "0" + secs;
        String milliseconds = "00" + ms;

        hours = hours.substring(hours.length() - 2);
        minutes = minutes.substring(minutes.length() - 2);
        seconds = seconds.substring(seconds.length() - 2);
        milliseconds = milliseconds.substring(milliseconds.length() - 3);

        String timestamp = hours + ":" + minutes + ":" + seconds + ":"
                + milliseconds;
        return timestamp;
    }

    /**
     * Returns Hours in timestamp.
     * @return Integer hours
     */
    public final int getHours() {
        return hrs;
    }

    /**
     * Returns Minutes in timestamp.
     * @return Integer minutes
     */
    public final int getMinutes() {
        return mins;
    }

    /**
     * Returns Seconds in timestamp.
     * @return Integer seconds
     */
    public final int getSeconds() {
        return secs;
    }

    /**
     * Returns Milliseconds in timestamp.
     * @return Integer milliseconds
     */
    public final int getMilliseconds() {
        return ms;
    }

    /**
     * Set Hours in timestamp.
     * @param hours hours
     */
    public final void setHours(final int hours) {
        hrs = hours;
    }

    /**
     * Set Minutes in timestamp.
     * @param minutes minutes
     */
    public final void setMinutes(final int minutes) {
        mins = minutes;
    }

    /**
     * Set Seconds in timestamp.
     * @param seconds seconds
     */
    public final void setSeconds(final int seconds) {
        secs = seconds;
    }

    /**
     * Returns Milliseconds in timestamp.
     * @param milliseconds milliseconds
     */
    public final void setMilliseconds(final int milliseconds) {
        ms = milliseconds;
    }

    /**
     * Add another timestamp to this Timestamp.
     * @param ts timestamp
     */
    public final void add(final Timestamp ts) {
        //Convert to milliseconds
        int thisMS = convertTimestampToMilliseconds(this);
        int subtractMS = convertTimestampToMilliseconds(ts);

        Timestamp temp = convertMillisecondsToTimestamp(thisMS
                + subtractMS);
        ms = temp.getMilliseconds();
        secs = temp.getSeconds();
        mins = temp.getMinutes();
        hrs = temp.getHours();
    }

    /**
     * Subtract another timestamp from this Timestamp.
     * @param ts timestamp
     */
    public final void subtract(final Timestamp ts) {
        //Convert to milliseconds
        int thisMS = convertTimestampToMilliseconds(this);
        int subtractMS = convertTimestampToMilliseconds(ts);

        if (subtractMS >= thisMS) {
            ms = 0;
            secs = 0;
            mins = 0;
            hrs = 0;
        } else {
            Timestamp temp = convertMillisecondsToTimestamp(thisMS
                    - subtractMS);
            ms = temp.getMilliseconds();
            secs = temp.getSeconds();
            mins = temp.getMinutes();
            hrs = temp.getHours();
        }

    }

    /**
     * Converts Timestamp to milliseconds.
     * @param ts Timestamp to convert
     * @return number of milliseconds
     */
    private int convertTimestampToMilliseconds(final Timestamp ts) {
        return ((((ts.getHours() * 60) + ts.getMinutes()) * 60)
                + ts.getSeconds()) * 1000 + ts.getMilliseconds();
    }

    /**
     * Converts milliseconds to Timestamp.
     * @param milliseconds milliseconds to convert
     * @return Timestamp
     */
    private Timestamp convertMillisecondsToTimestamp(final int milliseconds) {
        int newHours = 0;
        int newMins = 0;
        int newSecs = 0;
        int msecs = milliseconds;

        if (msecs >= MS_IN_HOUR) {
            newHours = msecs / MS_IN_HOUR;
            msecs = msecs - (newHours * MS_IN_HOUR);
        }

        if (msecs >= MS_IN_MINS) {
            newMins = msecs / MS_IN_MINS;
            msecs = msecs - (newMins * MS_IN_MINS);
        }

        if (msecs >= MS_IN_SEC) {
            newSecs = msecs / MS_IN_SEC;
            msecs = msecs - (newSecs * MS_IN_SEC);
        }

        String hours = "0" + newHours;
        String minutes = "0" + newMins;
        String seconds = "0" + newSecs;
        String millsecs = "00" + msecs;

        hours = hours.substring(hours.length() - 2);
        minutes = minutes.substring(minutes.length() - 2);
        seconds = seconds.substring(seconds.length() - 2);
        millsecs = millsecs.substring(millsecs.length() - 3);

        String timestamp = hours + ":" + minutes + ":" + seconds + ":"
                + msecs;
        return new Timestamp(timestamp);
    }

    /**
     * Returns true if Timestamps are equal, else false.
     * @param ts Timestamp to compare to
     * @return true if equal, else false
     */
    public final Boolean equals(final Timestamp ts) {
        if (ts.getMilliseconds() == ms
                && ts.getSeconds() == secs
                && ts.getMinutes() == mins
                && ts.getHours() == hrs) {
            return true;
        }
        return false;
    }

    /**
     * Returns true if Timestamp strings are equal, else false.
     * No checking is done, so please use properly formatted timestamp
     * @param stringTS string Timestamp to compare to.
     * @return true if equal, else false
     */
    public final Boolean equals(final String stringTS) {
        if (toString().equals(stringTS)) {
            return true;
        }
        return false;
    }
}
