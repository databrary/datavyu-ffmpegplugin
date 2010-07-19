package org.openshapa.plugins.spectrum.swing;

/**
 * Provides an interface to a spectrum viewer.
 */
public interface SpectrumView {

    /**
     * Set the magnitude values to display on the viewer.
     *
     * @param dbVals
     *            Relative magnitude values, where the peak value is 0 dB.
     */
    void setMagnitudelVals(final double[] dbVals);

    /**
     * Set the frequency values corresponding to the magnitude values being
     * displayed on the viewer.
     *
     * @param freqVals
     *            Frequency values.
     */
    void setFreqVals(final double[] freqVals);
}
