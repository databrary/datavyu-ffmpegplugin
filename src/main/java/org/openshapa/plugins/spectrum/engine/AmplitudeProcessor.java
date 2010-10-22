package org.openshapa.plugins.spectrum.engine;

import java.io.File;

import java.util.List;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

import org.openshapa.plugins.spectrum.SpectrumPlugin;
import org.openshapa.plugins.spectrum.models.AmplitudeBlock;
import org.openshapa.plugins.spectrum.models.ProcessorConstants;
import org.openshapa.plugins.spectrum.models.StereoData;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;


/**
 * Worker thread for processing audio amplitude data. Only processes audio
 * channels one and two. Assumes 16-bit audio.
 */
public final class AmplitudeProcessor
    extends SwingWorker<StereoData, AmplitudeBlock> {

    /** Logger for this class. */
    private static final Logger LOGGER = UserMetrix.getLogger(
            AmplitudeProcessor.class);

    /** Media file to process. */
    private final File mediaFile;

    /** Number of channels in the audio file. */
    private final int numChannels;

    /** The processed amplitude data. */
    private StereoData data;

    /** External progress handler. */
    private Progress progressHandler;

    /** Internal progress handler is used to put things in the right thread. */
    private final Progress internalHandler = new Progress() {

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

    private double lValNorm;
    private double rValNorm;

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
    public AmplitudeProcessor(final File mediaFile, final int numChannels,
        final Progress progressHandler) {
        this.mediaFile = mediaFile;
        this.numChannels = numChannels;
        this.progressHandler = progressHandler;
        data = new StereoData(ProcessorConstants.BLOCK_SZ, internalHandler);
        lValNorm = ProcessorConstants.LEVELS;
        rValNorm = ProcessorConstants.LEVELS;
    }

    public void disableAutoNormalize() {
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
    public void autoNormalizeAgainst(final double lVal, final double rVal) {
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
    public void setDataTimeSegment(final long start, final long end,
        final TimeUnit unit) {

        if (end < start) {
            throw new IllegalArgumentException("Invalid time segment: start="
                + start + " end=" + end);
        }

        data.setDataTimeStart(start);
        data.setDataTimeEnd(end);
        data.setDataTimeUnit(unit);
    }

    /**
     * Process amplitude data.
     *
     * @see javax.swing.SwingWorker#doInBackground()
     */
    @Override protected StereoData doInBackground() throws Exception {
        File addon = SpectrumPlugin.getAddonFile();

        if (addon == null) {

            // Undefined addon.
            return null;
        }

        // Set up our external process.
        long start = NANOSECONDS.convert(data.getDataTimeStart(),
                data.getDataTimeUnit());
        long end = NANOSECONDS.convert(data.getDataTimeEnd(),
                data.getDataTimeUnit());
        ProcessBuilder pb = new ProcessBuilder(addon.getAbsolutePath(), "-f",
                mediaFile.getAbsolutePath(), "-c",
                Integer.toString(numChannels), "-p",
                Integer.toString(ProcessorConstants.NUM_POINTS), "-s",
                Long.toString(start), "-e", Long.toString(end));

        Process audioPoints = pb.start();
        LineIterator it = IOUtils.lineIterator(audioPoints.getInputStream(),
                "UTF-8");

        try {

            int lineCount = 0;

            while (it.hasNext()) {

                if (isCancelled()) {
                    audioPoints.destroy();

                    return null;
                }

                String line = it.nextLine();
                String[] strData = line.split(";", 3);

                if (strData.length != 3) {

                    // Not a valid line.
                    System.err.println(line);

                    continue;
                }

                int left = Integer.parseInt(strData[0]);
                int right = Integer.parseInt(strData[1]);
                long time = Long.parseLong(strData[2]);
                time = data.getDataTimeUnit().convert(time, NANOSECONDS);

                data.addData(left, right, time);

                lineCount++;

                internalHandler.overallProgress(lineCount
                    / (double) ProcessorConstants.NUM_POINTS);
            }
        } finally {
            LineIterator.closeQuietly(it);

            int retVal = audioPoints.exitValue();

            if (retVal != 0) {

                // Process ended abnormally.
                return null;
            }
        }

        if (isCancelled()) {
            return null;
        }

        // Normalize any blocks that haven't been normalized.
        for (AmplitudeBlock block : data.getDataBlocks()) {

            if (!block.isNormalized()) {
                block.normalize();
            }
        }

        return data;
    }

    @Override protected void process(final List<AmplitudeBlock> chunks) {

        // Thread got cancelled.
        if (progressHandler == null) {
            return;
        }

        for (AmplitudeBlock block : chunks) {
            progressHandler.blockDone(block);
        }
    }

    /**
     * Update the track data.
     *
     * @see javax.swing.SwingWorker#done()
     */
    @Override protected void done() {

        try {

            StereoData result = get();

            if (result != null) {
                progressHandler.overallProgress(1);
                progressHandler.allDone(result);
            }

        } catch (Exception e) {
            // Do not log; the exception that is generated is normal
            // (thread interruptions and subsequently, task cancellation.).
        } finally {

            // Have to explicitly delete all refs or we leak memory because
            // JVM is still holding onto these threads.
            data = null;

            progressHandler = null;
        }

    }

}
