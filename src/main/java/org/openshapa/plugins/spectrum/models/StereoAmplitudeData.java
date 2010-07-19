package org.openshapa.plugins.spectrum.models;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * For collecting stereo amplitude data.
 */
public final class StereoAmplitudeData {

    /** Left channel data. */
    private List<Double> ampDataL;

    /** Right channel data. */
    private List<Double> ampDataR;

    /** Time interval between data points. */
    private long timeInterval;

    /** Time unit of the interval. */
    private TimeUnit timeUnit;

    /** Maximum amplitude value for the left channel. */
    private Double maxL;

    /** Maximum amplitude value for the right channel. */
    private Double maxR;

    public StereoAmplitudeData() {
        ampDataL = new ArrayList<Double>();
        ampDataR = new ArrayList<Double>();
        timeInterval = -1;
        maxL = Double.MIN_VALUE;
        maxR = Double.MIN_VALUE;
    }

    /**
     * Set the time interval between data points.
     *
     * @param interval
     *            interval value.
     * @param unit
     *            time unit.
     */
    public void setTimeInterval(final long interval, final TimeUnit unit) {

        if (interval < 1) {
            throw new IllegalArgumentException("Expecting positive interval.");
        }

        timeInterval = interval;
        timeUnit = unit;
    }

    /**
     * Check if the time interval has been set.
     *
     * @return true if it has been set.
     */
    public boolean isTimeIntervalSet() {
        return timeInterval > 0;
    }

    /**
     * @return Time interval between data points.
     */
    public long getTimeInterval() {
        return timeInterval;
    }

    /**
     * @return Time unit of the interval.
     */
    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    /**
     * Add a data point for the left channel.
     *
     * @param amp
     *            data point to add.
     */
    public void addDataL(final double amp) {
        ampDataL.add(amp);

        if (amp > maxL) {
            maxL = amp;
        }
    }

    /**
     * @return Number of data points for the left channel.
     */
    public int sizeL() {
        return ampDataL.size();
    }

    /**
     * @return The data points.
     */
    public Iterable<Double> getDataL() {
        return ampDataL;
    }

    /**
     * Normalize the left channel values with respect to the maximum amplitude
     * value. <b>WARNING:</b> this is not an idempotent operation.
     */
    public void normalizeL() {

        for (int i = 0; i < ampDataL.size(); i++) {
            ampDataL.set(i, ampDataL.get(i) / maxL);
        }
    }

    /**
     * Add a data point for the right channel.
     *
     * @param amp
     *            data point to add.
     */
    public void addDataR(final double amp) {
        ampDataR.add(amp);

        if (amp > maxR) {
            maxR = amp;
        }
    }

    /**
     * @return Number of data points for the right channel.
     */
    public int sizeR() {
        return ampDataR.size();
    }

    /**
     * @return The data points.
     */
    public Iterable<Double> getDataR() {
        return ampDataR;
    }

    /**
     * Normalize the right channel values with respect to the maximum amplitude
     * value. <b>WARNING:</b> this is not an idempotent operation.
     */
    public void normalizeR() {

        for (int i = 0; i < ampDataR.size(); i++) {
            ampDataR.set(i, ampDataR.get(i) / maxR);
        }
    }

}
