package org.openshapa.plugins.spectrum.models;

import gnu.trove.TDoubleArrayList;

import java.util.concurrent.TimeUnit;


/**
 * Represents a block of amplitude data.
 */
public final class AmplitudeBlock {

    /** Time stamp for first data point. */
    private long startTime;

    /** Time stamp for last data point. */
    private long endTime;

    /** Time unit of the start and end times. */
    private TimeUnit timeUnit;

    /** Left channel data. */
    private TDoubleArrayList ampDataL;

    /** Right channel data. */
    private TDoubleArrayList ampDataR;

    /** Block size. */
    private final int blockSize;

    private double localMaxL;
    private double localMaxR;

    private boolean normalized;
    private boolean allowNormalize;

    /**
     * Creates a new amplitude data block.
     *
     * @param blockSize
     *            Initial size of the amplitude block.
     * @param maxVal
     *            Maximum data point value; used to normalize data points
     *            into [-1, 1].
     */
    public AmplitudeBlock(final int blockSize) {
        ampDataL = new TDoubleArrayList(blockSize);
        ampDataR = new TDoubleArrayList(blockSize);
        this.blockSize = blockSize;

        normalized = false;
        allowNormalize = true;

        localMaxL = Double.MIN_VALUE;
        localMaxR = Double.MIN_VALUE;
    }

    /**
     * Add a left channel amplitude value. Will only add the value if
     * {@link #isFull()} evaluates to false.
     *
     * @param val
     */
    public void addL(final double val) {

        if (sizeL() != blockSize) {
            ampDataL.add(val);

            if (Math.abs(val) > localMaxL) {
                localMaxL = Math.abs(val);
            }
        }
    }

    /**
     * Add a right channel amplitude value. Will only add the value if
     * {@link #isFull()} evaluates to false.
     *
     * @param val
     */
    public void addR(final double val) {

        if (sizeR() != blockSize) {
            ampDataR.add(val);

            if (Math.abs(val) > localMaxR) {
                localMaxR = Math.abs(val);
            }
        }
    }

    /**
     * Number of left channel values.
     */
    public int sizeL() {
        return ampDataL.size();
    }

    /**
     * Number of right channel values.
     */
    public int sizeR() {
        return ampDataR.size();
    }

    /**
     * Returns a copy of the array of left channel values.
     */
    public double[] getLArray() {
        return ampDataL.toNativeArray();
    }

    /**
     * Returns a copy of the array of right channel values.
     */
    public double[] getRArray() {
        return ampDataR.toNativeArray();
    }

    /**
     * Set the time stamp for the first data point.
     *
     * @param time
     */
    public void setStartTime(final long time) {
        startTime = time;
    }

    /**
     * Time stamp for the first data point.
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Set the time stamp for the last data point.
     *
     * @param time
     */
    public void setEndTime(final long time) {
        endTime = time;
    }

    /**
     * Time stamp for the last data point.
     */
    public long getEndTime() {
        return endTime;
    }

    /**
     * Set the time unit of the start and end time.
     *
     * @param unit
     */
    public void setTimeUnit(final TimeUnit unit) {
        timeUnit = unit;
    }

    /**
     * Time unit of the start and end time.
     */
    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    /**
     * Compute the time interval between data points.
     */
    public double computeInterval() {
        return (endTime - startTime) / (double) sizeL();
    }

    /**
     * True if the block is full.
     */
    public boolean isFull() {
        return (sizeL() == sizeR()) && (sizeL() == blockSize);
    }

    public double getMaxL() {
        return localMaxL;
    }

    public double getMaxR() {
        return localMaxR;
    }

    /**
     * Normalize amplitude data against the given value.
     * <p>
     * <b>WARNING:</b> this is not an idempotent operation.
     * </p>
     *
     * @param lVal
     * @param rVal
     */
    public void normalizeAgainst(final double lVal, final double rVal) {
        assert sizeL() == sizeR();

        if (!normalized && allowNormalize) {

            for (int i = 0; i < ampDataL.size(); i++) {
                ampDataL.setQuick(i, ampDataL.getQuick(i) / lVal);
                ampDataR.setQuick(i, ampDataR.getQuick(i) / rVal);
            }

            normalized = true;
        }
    }

    /**
     * Normalize amplitude data against {@link ProcessorConstants.LEVELS} for
     * both channels.
     */
    public void normalize() {
        normalizeAgainst(ProcessorConstants.LEVELS, ProcessorConstants.LEVELS);
    }

    public void setNormalizeAllowed(final boolean allow) {
        allowNormalize = allow;
    }

    public boolean isNormalizeAllowed() {
        return allowNormalize;
    }

    /**
     * True if the data has already been normalized.
     */
    public boolean isNormalized() {
        return normalized;
    }

}
