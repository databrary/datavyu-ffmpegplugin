/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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

    /** Largest absolute value for the left channel in this block. */
    private double localMaxL;

    /** Largest absolute value for the right channel in this block. */
    private double localMaxR;

    /** The last block's left channel value to connect with. */
    private double linkL;

    /** The timestamp of the last block's left channel value. */
    private long linkLTime;

    /** The last block's right channel value to connect with. */
    private double linkR;

    /** The timestamp of the last block's right channel value. */
    private long linkRTime;

    /** Has the block been normalized. */
    private boolean normalized;

    /** Can the block be normalized. */
    private boolean allowNormalize;

    /**
     * Creates a new amplitude data block.
     *
     * @param blockSize
     *            Initial size of the amplitude block.
     */
    public AmplitudeBlock(final int blockSize) {
        ampDataL = new TDoubleArrayList(blockSize);
        ampDataR = new TDoubleArrayList(blockSize);
        this.blockSize = blockSize;

        normalized = false;
        allowNormalize = true;

        localMaxL = Double.MIN_VALUE;
        localMaxR = Double.MIN_VALUE;

        linkL = 0;
        linkLTime = 0;
        linkR = 0;
        linkRTime = 0;
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

        if (!isNormalized() && isNormalizeAllowed()) {

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

    /**
     * Set if the block can be normalized.
     *
     * @param allow
     */
    public void setNormalizeAllowed(final boolean allow) {
        allowNormalize = allow;
    }

    /**
     * Can the block be normalized.
     */
    public boolean isNormalizeAllowed() {
        return allowNormalize;
    }

    /**
     * True if the data has already been normalized.
     */
    public boolean isNormalized() {
        return normalized;
    }

    /**
     * Set the last block's left channel data to connect with.
     *
     * @param val
     *            The amplitude value.
     * @param timestamp
     *            The timestamp of the amplitude value.
     */
    public void setLinkL(final double val, final long timestamp) {
        linkL = val;
        linkLTime = timestamp;
    }

    /**
     * The last block's left channel value to connect with.
     */
    public double getLinkL() {
        return linkL;
    }

    /**
     * The timestamp of the last block's left channel value.
     */
    public long getLinkLTime() {
        return linkLTime;
    }

    /**
     * Set the last block's right channel data to connect with.
     *
     * @param val
     *            The amplitude value.
     * @param timestamp
     *            The timestamp of the amplitude value.
     */
    public void setLinkR(final double val, final long timestamp) {
        linkR = val;
        linkRTime = timestamp;
    }

    /**
     * The last block's right channel value to connect with.
     */
    public double getLinkR() {
        return linkR;
    }

    /**
     * The timestamp of the last block's right channel value.
     */
    public long getLinkRTime() {
        return linkRTime;
    }

    /**
     * The last left channel value in this block.
     */
    public double getLastL() {

        if (!ampDataL.isEmpty()) {
            return ampDataL.getQuick(ampDataL.size() - 1);
        }

        return 0;
    }

    /**
     * The last right channel value in this block.
     */
    public double getLastR() {

        if (!ampDataR.isEmpty()) {
            return ampDataR.getQuick(ampDataR.size() - 1);
        }

        return 0;
    }

}
