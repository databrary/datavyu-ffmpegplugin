package org.openshapa.plugins.spectrum;

import java.io.File;

import java.util.concurrent.TimeUnit;

import org.gstreamer.ElementFactory;
import org.gstreamer.State;

import org.gstreamer.elements.PlayBin;


/**
 * Utility functions.
 */
public final class SpectrumUtils {

    /**
     * Get media file duration.
     * <a href="http://groups.google.com/group/gstreamer-java/browse_thread/thread/3218a4d3c15047a3/2edb9b1d212b18bf?lnk=gst&q=duration#2edb9b1d212b18bf"
     * >Source</a>.
     *
     * @param file
     *            Media file.
     * @return Duration in milliseconds.
     */
    public static long getDuration(final File file) {
        PlayBin playBin = new PlayBin("DurationFinder");
        playBin.setAudioSink(ElementFactory.make("fakesink", "audiosink"));
        playBin.setVideoSink(ElementFactory.make("fakesink", "videosink"));
        playBin.setInputFile(file);
        playBin.setState(State.PAUSED);

        long duration = -1;
        long startTime = System.currentTimeMillis();

        do {
            duration = playBin.queryDuration(TimeUnit.MILLISECONDS);

            if ((duration > 0)
                    || ((System.currentTimeMillis() - startTime) > 5000)) {
                playBin.stop();
                playBin.setState(State.NULL);
                playBin.dispose();

                break;
            }

        } while (true);

        return duration;
    }

    /**
     * Calculates frequencies to display and returns their indices. The
     * frequencies to display are chosen logarithmically. Exponential regression
     * is performed to calculate the frequency values to display.
     *
     * @param totalBins
     * @param numIndices
     *            Number of frequency values to pick.
     * @return Indices associated with the picked frequency values.
     */
    public static int[] findIndices(final int totalBins, final int numIndices) {

        if (numIndices <= 1) {
            throw new IllegalArgumentException("numIndices must be > 1");
        }

        // Exponential regression.
        final double a = Math.pow(totalBins, 1D / (numIndices - 1));

        // Get numIndices frequencies that are log spaced.
        int[] indices = new int[numIndices];

        indices[0] = 0;

        for (int i = 1; i < numIndices; i++) {
            indices[i] = (int) Math.pow(a, i);

            if (indices[i] < indices[i - 1]) {
                indices[i] = indices[i - 1] + 1;
            }
        }

        // double[] frequencies = new double[numIndices];

        // for (int i = 0; i < numIndices; i++) {
        // frequencies[i] = minFreq * Math.pow(a, i);
        // }
        //
        // int[] result = new int[numIndices];
        // final double step = maxFreq / ((numSamples / 2D) + 1);
        //
        // // Map frequency values back into index values
        // result[0] = (int) Math.floor(frequencies[0] / step);
        //
        // for (int i = 1; i < numIndices; i++) {
        // result[i] = (int) Math.floor(frequencies[i] / step);
        //
        // if (result[i] <= result[i - 1]) {
        // result[i] = result[i - 1] + 1;
        // }
        // }

        return indices;
    }

}
