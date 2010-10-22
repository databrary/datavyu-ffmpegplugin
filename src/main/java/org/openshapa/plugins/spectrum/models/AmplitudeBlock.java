package org.openshapa.plugins.spectrum.models;

import gnu.trove.TDoubleArrayList;

import java.util.concurrent.TimeUnit;

import org.openshapa.plugins.spectrum.SpectrumConstants;


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

    /**
     * Normalize all amplitude values with respect to {@link #getMaxVal()}.
     * <p>
     * <b>WARNING:</b> this is not an idempotent operation.
     * </p>
     */
    public void normalize() {
        assert sizeL() == sizeR();

        if (!normalized) {

            // -1 halves the value, halve the value because the bit depth is the
            // number of levels unsigned.
            double max = 1 << (ProcessorConstants.DEPTH - 1);

            for (int i = 0; i < ampDataL.size(); i++) {

                // ampDataL.setQuick(i, ampDataL.getQuick(i) / localMaxL);
                // ampDataR.setQuick(i, ampDataR.getQuick(i) / localMaxR);
                ampDataL.setQuick(i, ampDataL.getQuick(i) / max);
                ampDataR.setQuick(i, ampDataR.getQuick(i) / max);
            }

            normalized = true;
        }
    }

    /**
     * True if the data has already been normalized.
     */
    public boolean isNormalized() {
        return normalized;
    }

}
