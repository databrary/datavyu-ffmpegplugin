package org.uispec4j;

/**
 * Used to parse timestamp (onset, offset) for elements
 *
 */
public class Timestamp {

    /**
     * Parts of timestamp.
     */
    private int hrs, mins, secs, ms;

    private static final int MS_IN_HOUR = 1000 * 60 * 60;
    private static final int MS_IN_MINS = 1000 * 60;
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
     */
    public final void setHours(int hours) {
        hrs = hours;
    }

    /**
     * Set Minutes in timestamp.
     */
    public final void setMinutes(int minutes) {
        mins = minutes;
    }

    /**
     * Set Seconds in timestamp.
     */
    public final void setSeconds(int seconds) {
        secs = seconds;
    }

    /**
     * Returns Milliseconds in timestamp.
     */
    public final void setMilliseconds(int milliseconds) {
        ms = milliseconds;
    }

    /**
     * Add another timestamp to this Timestamp.
     * @param ts timestamp
     */
    public void add(Timestamp ts) {
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

    public void subtract(Timestamp ts) {
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

    private int convertTimestampToMilliseconds(Timestamp ts) {
        return ((((ts.getHours() * 60) + ts.getMinutes()) * 60)
                + ts.getSeconds()) * 1000 + ts.getMilliseconds();
    }

    private Timestamp convertMillisecondsToTimestamp(int milliseconds) {
        int newHours = 0;
        int newMins = 0;
        int newSecs = 0;

        if (milliseconds >= MS_IN_HOUR) {
            newHours = milliseconds / MS_IN_HOUR;
            milliseconds = milliseconds - (newHours * MS_IN_HOUR);
        }

        if (milliseconds >= MS_IN_MINS) {
            newMins = milliseconds / MS_IN_MINS;
            milliseconds = milliseconds - (newMins * MS_IN_MINS);
        }

        if (milliseconds >= MS_IN_SEC) {
            newSecs = milliseconds / MS_IN_SEC;
            milliseconds = milliseconds - (newSecs * MS_IN_SEC);
        }

        String hours = "0" + newHours;
        String minutes = "0" + newMins;
        String seconds = "0" + newSecs;
        String millsecs = "00" + milliseconds;

        hours = hours.substring(hours.length() - 2);
        minutes = minutes.substring(minutes.length() - 2);
        seconds = seconds.substring(seconds.length() - 2);
        millsecs = millsecs.substring(millsecs.length() - 3);

        String timestamp = hours + ":" + minutes + ":" + seconds + ":"
                + milliseconds;
        return new Timestamp(timestamp);
    }

    public Boolean equals(Timestamp ts) {
        if (ts.getMilliseconds() == ms
                && ts.getSeconds() == secs
                && ts.getMinutes() == mins
                && ts.getHours() == hrs) {
            return true;
        }
        return false;
    }

    public Boolean equals(String stringTS) {
        if (toString().equals(stringTS)) {
            return true;
        }
        return false;
    }
}
