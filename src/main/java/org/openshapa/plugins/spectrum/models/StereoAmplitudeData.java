package org.openshapa.plugins.spectrum.models;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * For collecting stereo amplitude data.
 */
public final class StereoAmplitudeData {

    /** Start time of the data in the buffer. */
    private long dataTimeStart;

    /** End time of the data in the buffer. */
    private long dataTimeEnd;

    /** Time unit of the start and end times. */
    private TimeUnit dataTimeUnit;

    /** Sampling rate. */
    private int sampleRate;

    /** Left channel data. */
    private List<Double> ampDataL;

    /** Right channel data. */
    private List<Double> ampDataR;

    /** Time interval between data points. */
    private double timeInterval;

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
     * @return the dataTimeStart
     */
    public long getDataTimeStart() {
        return dataTimeStart;
    }

    /**
     * @param dataTimeStart
     *            the dataTimeStart to set
     */
    public void setDataTimeStart(final long dataTimeStart) {
        this.dataTimeStart = dataTimeStart;
    }

    /**
     * @return the dataTimeEnd
     */
    public long getDataTimeEnd() {
        return dataTimeEnd;
    }

    /**
     * @param dataTimeEnd
     *            the dataTimeEnd to set
     */
    public void setDataTimeEnd(final long dataTimeEnd) {
        this.dataTimeEnd = dataTimeEnd;
    }

    /**
     * @return the dataTimeUnit
     */
    public TimeUnit getDataTimeUnit() {
        return dataTimeUnit;
    }

    /**
     * @param dataTimeUnit
     *            the dataTimeUnit to set
     */
    public void setDataTimeUnit(final TimeUnit dataTimeUnit) {
        this.dataTimeUnit = dataTimeUnit;
    }

    /**
     * @return the sampleRate
     */
    public int getSampleRate() {
        return sampleRate;
    }

    /**
     * @param sampleRate
     *            the sampleRate to set
     */
    public void setSampleRate(final int sampleRate) {
        this.sampleRate = sampleRate;
    }

    /**
     * Set the time interval between data points.
     *
     * @param interval
     *            interval value.
     * @param unit
     *            time unit.
     */
    public void setTimeInterval(final double interval, final TimeUnit unit) {
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
    public double getTimeInterval() {
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

    @Override public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("StereoAmplitudeData [ampDataL=");
        builder.append(ampDataL);
        builder.append(", ampDataR=");
        builder.append(ampDataR);
        builder.append(", dataTimeEnd=");
        builder.append(dataTimeEnd);
        builder.append(", dataTimeStart=");
        builder.append(dataTimeStart);
        builder.append(", dataTimeUnit=");
        builder.append(dataTimeUnit);
        builder.append(", maxL=");
        builder.append(maxL);
        builder.append(", maxR=");
        builder.append(maxR);
        builder.append(", sampleRate=");
        builder.append(sampleRate);
        builder.append(", timeInterval=");
        builder.append(timeInterval);
        builder.append(", timeUnit=");
        builder.append(timeUnit);
        builder.append("]");

        return builder.toString();
    }

}
