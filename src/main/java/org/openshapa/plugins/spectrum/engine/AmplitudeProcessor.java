package org.openshapa.plugins.spectrum.engine;

import java.io.File;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.openshapa.plugins.spectrum.models.AmplitudeBlock;
import org.openshapa.plugins.spectrum.models.ProcessorConstants;
import org.openshapa.plugins.spectrum.models.StereoData;


/**
 * Process amplitude data for a given media file.
 */
public abstract class AmplitudeProcessor
    extends SwingWorker<StereoData, AmplitudeBlock> {

    /** Media file to process. */
    protected final File mediaFile;

    /** Number of channels in the audio file. */
    protected final int numChannels;

    /** The processed amplitude data. */
    protected StereoData data;

    /** Progress handler. */
    protected Progress progressHandler;

    /** Internal progress handler is used to put things in the right thread. */
    protected final Progress internalHandler = new Progress() {

            @Override public void overallProgress(final double percentage) {
                SwingUtilities.invokeLater(new Runnable() {
                        @Override public void run() {

                            if (progressHandler != null) {
                                progressHandler.overallProgress(percentage);
                            }
                        }
                    });
            }

            @Override public void blockDone(final AmplitudeBlock block) {
                block.normalizeAgainst(lValNorm, rValNorm);
                publish(block);
            }

            @Override public void allDone(final StereoData allData) {
            }
        };

    /** Left value to normalize against. */
    protected double lValNorm;

    /** Right value to normalize against. */
    protected double rValNorm;

    /**
     * Creates a new worker thread.
     *
     * @param mediaFile
     *            Media file to process.
     * @param numChannels
     *            Number of channels in the audio file.
     * @param progressHandler
     *            Progress handler.
     */
    protected AmplitudeProcessor(final File mediaFile, final int numChannels,
        final Progress progressHandler) {
        this.mediaFile = mediaFile;
        this.numChannels = numChannels;
        this.progressHandler = progressHandler;
        data = new StereoData(ProcessorConstants.BLOCK_SZ, internalHandler);

        lValNorm = ProcessorConstants.LEVELS;
        rValNorm = ProcessorConstants.LEVELS;
    }

    public static AmplitudeProcessor create(final File mediaFile,
        final int numChannels, final Progress progressHandler) {
        return new JavaAmplitudeProcessor(mediaFile, numChannels,
                progressHandler);
    }

    /**
     * Disable block auto-normalization.
     */
    public final void disableAutoNormalize() {
        data.setNormalizeEnabled(false);
    }

    /**
     * Auto normalize block data against the given values.
     *
     * @param lVal
     *            Normalize left channel against this value.
     * @param rVal
     *            Normalize right channel against this value.
     */
    public final void autoNormalizeAgainst(final double lVal,
        final double rVal) {
        data.setNormalizeEnabled(true);
        lValNorm = lVal;
        rValNorm = rVal;
    }

    /**
     * Set the time segment to process.
     *
     * @param start
     *            Start time
     * @param end
     *            End time
     * @param unit
     *            Start and end time units.
     */
    public final void setDataTimeSegment(final long start, final long end,
        final TimeUnit unit) {

        if (end < start) {
            throw new IllegalArgumentException("Invalid time segment: start="
                + start + " end=" + end);
        }

        data.setDataTimeStart(start);
        data.setDataTimeEnd(end);
        data.setDataTimeUnit(unit);

        setup();
    }

    protected abstract void setup();

    @Override protected void process(final List<AmplitudeBlock> chunks) {

        // Thread got cancelled.
        if (progressHandler == null) {
            return;
        }

        for (AmplitudeBlock block : chunks) {
            progressHandler.blockDone(block);
        }
    }

}
