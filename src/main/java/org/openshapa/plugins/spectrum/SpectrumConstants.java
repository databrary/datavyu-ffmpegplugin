package org.openshapa.plugins.spectrum;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;


/**
 * OpenSHAPA and GStreamer spectrum plugin constants.
 */
public class SpectrumConstants {

    /**
     * Precalculate sweet spot. The first frequency value is 10Hz, incrementing
     * by 20Hz.
     */
    public static final int FFT_BANDS = 1200;

    /**
     * Number of spectrum bands to display.
     */
    public static final int SPECTRUM_BANDS = 40;

    /**
     * Minimum magnitude value to display.
     */
    public static final int MIN_MAGNITUDE = -80;

    /**
     * Resample all audio input to this rate.
     */
    public static final int SAMPLE_RATE = 48000;

    /**
     * Number of spectrum updates to display in a second. Must divide 1000
     * evenly.
     */
    public static final int FPS = 25;

    /**
     * GStreamer spectrum plugin message interval.
     */
    public static final long SPECTRUM_MSG_INTERVAL = NANOSECONDS.convert(1000
            / FPS, MILLISECONDS);

}
