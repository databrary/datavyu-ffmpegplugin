package org.openshapa.plugins.spectrum.engine;

import java.nio.ShortBuffer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.SwingUtilities;

import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.transform.FastFourierTransformer;
import org.apache.commons.math.util.MathUtils;

import org.openshapa.plugins.spectrum.swing.Spectrum;
import org.openshapa.plugins.spectrum.swing.SpectrumDialog;

import com.xuggle.xuggler.IAudioSamples;


/**
 * Used to calculate power spectrum from audio file.
 */
public final class SpectrumProcessor extends Thread {

    /** Number of frequency bands to display. */
    private static final int NUM_BANDS = 30;

    /** Incoming samples for processing. */
    private BlockingQueue<AudioSample> incoming;

    /** Table of window function values. */
    private double[] window;

    /** Table of indices to use for selected frequencies. */
    private int[] indices;

    /** Buffer for storing the results. */
    private double[] resultBuffer;

    /** Buffer for temporary calculations. */
    private double[] tempCalcBuffer;

    /** Input buffer for audio samples. */
    private double[] inputSamples;

    /** Array of chosen frequency values. */
    private double[] freqs;

    /** FFT engine. */
    private FastFourierTransformer fft;

    /** The Swing output view. */
    private Spectrum spectrumView;

    /** Peak spectral magnitude. */
    private double peakPower = Double.MIN_VALUE;

    public SpectrumProcessor(final SpectrumDialog dialog) {
        incoming = new LinkedBlockingQueue<AudioSample>();
        fft = new FastFourierTransformer();

        setDaemon(true);
        setName("SpectrumProcessor-" + getName());

        Runnable edtTask = new Runnable() {
                @Override public void run() {
                    spectrumView = new Spectrum();
                    dialog.setSpectrum(spectrumView);
                }
            };
        SwingUtilities.invokeLater(edtTask);
    }

    /**
     * FFT processing loop.
     *
     * @see java.lang.Thread#run()
     */
    @Override public void run() {

        while (true) {

            if (interrupted()) {
                return;
            }

            AudioSample current = null;

            try {
                current = incoming.take();

                IAudioSamples samples = current.getSamples();

                // 0. Set up buffers, windows, chosen frequencies.
                if (resultBuffer == null) {
                    final int samplesSize = samples.getSize();
                    resultBuffer = new double[samplesSize];
                    tempCalcBuffer = new double[samplesSize];

                    window = hannWindow(samplesSize);

                    int len = (int) Math.ceil(MathUtils.log(2,
                                (samplesSize / 2)
                                / (double) samples.getChannels()));
                    len = (int) Math.pow(2, len);
                    inputSamples = new double[len];
                    tempCalcBuffer = new double[len];
                    resultBuffer = new double[len];
                }

                if (indices == null) {

                    // Choose frequencies to display.
                    double maxFreq = samples.getSampleRate() / 2;
                    final double step = maxFreq / (resultBuffer.length / 2D);
                    indices = findIndices((int) step,
                            samples.getSampleRate() / 2, resultBuffer.length,
                            NUM_BANDS);

                    freqs = new double[indices.length];

                    for (int i = 0; i < indices.length; i++) {
                        freqs[i] = (indices[i] + 1) * step;
                    }

                    Runnable edtTask = new Runnable() {
                            @Override public void run() {
                                spectrumView.setFreqVals(freqs);
                            }
                        };
                    SwingUtilities.invokeLater(edtTask);
                }

                // 1. Accumulate samples.
                ShortBuffer src = samples.getByteBuffer().asShortBuffer();
                firstChannel(src, samples.getSize() / 4, inputSamples,
                    samples.getChannels());

                // 2. Apply window function to input samples.
                for (int i = 0; i < inputSamples.length; i++) {
                    tempCalcBuffer[i] = inputSamples[i] * window[i];
                }

                // 3. Perform FFT on the buffer.
                Complex[] freqMags = fft.transform(tempCalcBuffer);

                for (int i = 0; i < resultBuffer.length; i++) {
                    resultBuffer[i] = freqMags[i].abs() * freqMags[i].abs();
                }

                // 4. Convert results into spectral power.
                for (int i = 0; i < ((resultBuffer.length / 2) + 1); i++) {
                    resultBuffer[i] = 10 * Math.log10(resultBuffer[i]);

                    // Record peak power.
                    peakPower = Math.max(peakPower, resultBuffer[i]);
                }

                // 5. Normalize with respect to maximum spectral power.
                for (int i = 0; i < ((resultBuffer.length / 2) + 1); i++) {
                    resultBuffer[i] -= peakPower;
                }

                // 6. Display spectrum.
                final double[] paintSamples = new double[NUM_BANDS];

                for (int bin = 0; bin < NUM_BANDS; bin++) {
                    paintSamples[bin] = resultBuffer[indices[bin]];
                }

                Runnable edtTask = new Runnable() {
                        @Override public void run() {
                            spectrumView.setMagnitudelVals(paintSamples);
                            spectrumView.repaint();
                        }
                    };
                SwingUtilities.invokeLater(edtTask);

                // 7. Release native resources.
                current.delete();

            } catch (InterruptedException e) {
                return;
            }

        }
    }

    /**
     * Queue up audio sample for processing.
     *
     * @param sample
     *            Audio sample to process.
     */
    public void giveSample(final AudioSample sample) {
        incoming.offer(sample);
    }

    /**
     * Calculates frequencies to display and returns their indices. The
     * frequencies to display are chosen logarithmically. Exponential regression
     * is performed to calculate the frequency values to display.
     *
     * @param minFreq
     *            Minimum frequency value.
     * @param maxFreq
     *            Maximum frequency value.
     * @param numSamples
     *            Total number of samples in the FFT buffer.
     * @param numIndices
     *            Number of frequency values to pick.
     * @return Indices associated with the picked frequency values.
     */
    private int[] findIndices(final int minFreq, final int maxFreq,
        final int numSamples, final int numIndices) {

        if (numIndices <= 1) {
            throw new IllegalArgumentException("numIndices must be > 1");
        }

        if (minFreq >= maxFreq) {
            throw new IllegalArgumentException("minFreq must be < maxFreq");
        }

        // Exponential regression.
        final double a = Math.pow((double) maxFreq / (double) minFreq,
                1D / (numIndices - 1));

        // Get numIndices frequencies that are log spaced.
        double[] frequencies = new double[numIndices];

        for (int i = 0; i < numIndices; i++) {
            frequencies[i] = minFreq * Math.pow(a, i);
        }

        int[] result = new int[numIndices];
        final double step = maxFreq / ((numSamples / 2D) + 1);

        // Map frequency values back into index values
        result[0] = (int) Math.floor(frequencies[0] / step);

        for (int i = 1; i < numIndices; i++) {
            result[i] = (int) Math.floor(frequencies[i] / step);

            if (result[i] <= result[i - 1]) {
                result[i] = result[i - 1] + 1;
            }
        }

        return result;
    }

    /**
     * Computes the coefficients of the Hann window.
     *
     * @param length
     *            Length of the window.
     * @return Coefficients of the Hann window.
     */
    private double[] hannWindow(final int length) {

        if (length < 1) {
            throw new IllegalArgumentException("Window length must be > 0");
        }

        double[] result = new double[length];

        for (int m = 0; m < length; m++) {
            result[m] = 0.5D
                - (0.5D * Math.cos((2 * Math.PI * m) / (length - 1)));
        }

        return result;
    }

    /**
     * Extract the first audio channel into a buffer.
     *
     * @param src
     *            Source buffer.
     * @param srcLength
     *            Length of source buffer.
     * @param dest
     *            Destination buffer.
     * @param numChannels
     *            Number of audio channels.
     */
    private void firstChannel(final ShortBuffer src, final int srcLength,
        final double[] dest, final int numChannels) {

        int len = (int) Math.ceil(MathUtils.log(2,
                    srcLength / (double) numChannels));
        len = (int) Math.pow(2, len);

        for (int i = 0; i < srcLength; i += numChannels) {
            dest[i / numChannels] = src.get(i);
        }
    }

}
