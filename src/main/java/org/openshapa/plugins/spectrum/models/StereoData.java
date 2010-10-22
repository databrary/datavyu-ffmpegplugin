package org.openshapa.plugins.spectrum.models;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openshapa.plugins.spectrum.engine.Progress;

import com.google.common.collect.Lists;


/**
 * Manages stereo amplitude data by storing the data into {@link AmplitudeBlock}
 * s.
 */
public final class StereoData {

    /** Time unit of the start and end times. */
    private TimeUnit dataTimeUnit;

    /** Sampling rate. */
    private int sampleRate;

    /** Left channel data. */
    private List<AmplitudeBlock> ampData;

    /** Size of an amplitude block. */
    private final int blockSize;

    /** Total pairs of data points. */
    private int size;

    /** Progress handler. */
    private Progress progress;

    private boolean normalizeEnabled;

    /**
     * @param blockSize
     *            Number of data points in each processed block.
     * @param depth
     *            Bit depth of each data point.
     * @param progress
     *            Progress handler.
     */
    public StereoData(final int blockSize, final Progress progress) {

        if (blockSize <= 0) {
            throw new IllegalArgumentException("Invalid block size.");
        }

        if (progress == null) {
            throw new NullPointerException();
        }

        this.blockSize = blockSize;
        this.progress = progress;

        ampData = Lists.newArrayList();

        size = 0;

        normalizeEnabled = true;
    }

    /**
     * Set if block value normalization should be enabled or not.
     */
    public void setNormalizeEnabled(final boolean val) {
        normalizeEnabled = val;
    }

    /**
     * Is block value normalization enabled.
     */
    public boolean isNormalizeEnabled() {
        return normalizeEnabled;
    }

    /**
     * @return the dataTimeStart
     */
    public long getDataTimeStart() {

        if (ampData.isEmpty()) {
            return 0;
        }

        return ampData.get(0).getStartTime();
    }

    /**
     * @param dataTimeStart
     *            the dataTimeStart to set
     */
    public void setDataTimeStart(final long dataTimeStart) {

        if (ampData.isEmpty()) {
            ampData.add(new AmplitudeBlock(blockSize));
        }

        ampData.get(0).setStartTime(dataTimeStart);
    }

    /**
     * @return the dataTimeEnd
     */
    public long getDataTimeEnd() {

        if (ampData.isEmpty()) {
            return 0;
        }

        return ampData.get(ampData.size() - 1).getEndTime();
    }

    /**
     * @param dataTimeEnd
     *            the dataTimeEnd to set
     */
    public void setDataTimeEnd(final long dataTimeEnd) {
        assert !ampData.isEmpty();
        ampData.get(ampData.size() - 1).setEndTime(dataTimeEnd);
    }

    /**
     * @return the dataTimeUnit
     */
    public TimeUnit getDataTimeUnit() {
        return dataTimeUnit;
    }

    /**
     * Set the time unit to use. This will not convert timestamps.
     *
     * @param dataTimeUnit
     *            the dataTimeUnit to set
     */
    public void setDataTimeUnit(final TimeUnit dataTimeUnit) {
        this.dataTimeUnit = dataTimeUnit;

        for (AmplitudeBlock block : ampData) {
            block.setTimeUnit(dataTimeUnit);
        }
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
     * Add a data point for the left and right channel.
     *
     * @param lVal
     *            left channel data point to add.
     * @param rVal
     *            right channel data point to add.
     * @param time
     *            time stamp of the left and right data point.
     */
    public void addData(final double lVal, final double rVal, final long time) {

        // Invariant 1: new size after adding data fits within number of blocks
        // multiplied by block size.
        if ((size + 1) > (ampData.size() * blockSize)) {
            AmplitudeBlock newBlock = new AmplitudeBlock(blockSize);
            newBlock.setNormalizeAllowed(isNormalizeEnabled());

            // Invariant 2: the new block's start time is the timestamp of the
            // first data point to be added to it.
            newBlock.setStartTime(time);
            newBlock.setTimeUnit(dataTimeUnit);

            ampData.add(newBlock);
        }

        AmplitudeBlock block = ampData.get(ampData.size() - 1);
        block.addL(lVal);
        block.addR(rVal);

        // Invariant 3: end timestamp of the block is the timestamp of the last
        // data point.
        block.setEndTime(time);

        // Invariant 4: size is the total pairs of data points.
        size++;

        // Send off for external processing.
        if (block.isFull()) {
            progress.blockDone(block);
        }
    }

    public Iterable<AmplitudeBlock> getDataBlocks() {
        return ampData;
    }

    /**
     * Number of pairs.
     */
    public int getSize() {
        return size;
    }

    public double getMaxL() {
        double max = Double.MIN_VALUE;

        for (AmplitudeBlock block : getDataBlocks()) {
            max = Math.max(max, block.getMaxL());
        }

        return max;
    }

    public double getMaxR() {
        double max = Double.MIN_VALUE;

        for (AmplitudeBlock block : getDataBlocks()) {
            max = Math.max(max, block.getMaxR());
        }

        return max;
    }

}
