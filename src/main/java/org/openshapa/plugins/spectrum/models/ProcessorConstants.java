package org.openshapa.plugins.spectrum.models;

public interface ProcessorConstants {

    /** Total number of points to pick for each channel. */
    static final int NUM_POINTS = 5000;

    /** Size of a processed block. Must divide {@link #NUM_POINTS} evenly. */
    static final int BLOCK_SZ = 500;

    /** Bit depth of the audio to process. */
    static final int DEPTH = 16;

}
