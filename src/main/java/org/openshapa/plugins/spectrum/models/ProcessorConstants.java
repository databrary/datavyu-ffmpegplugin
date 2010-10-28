package org.openshapa.plugins.spectrum.models;

public interface ProcessorConstants {

    /** Total number of points to pick for each channel. */
    static final int NUM_POINTS = 5000;

    /** Size of a processed block. Must divide {@link #NUM_POINTS} evenly. */
    static final int BLOCK_SZ = 500;

    /** Unsigned bit depth of the audio to process. */
    static final int DEPTH = 16;

    /** Number of discretization levels. */
    static final int LEVELS = 1 << (DEPTH - 1);

    /** Zoom threshold at which audio data is processed again. */
    static final double ZOOM_REPROCESS_THRESHOLD = 0.05D;

}
