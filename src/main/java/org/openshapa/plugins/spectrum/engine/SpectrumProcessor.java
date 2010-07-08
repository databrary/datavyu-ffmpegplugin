package org.openshapa.plugins.spectrum.engine;

import java.nio.ShortBuffer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.SwingUtilities;

import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.transform.FastFourierTransformer;

import org.openshapa.plugins.spectrum.swing.Spectrum;
import org.openshapa.plugins.spectrum.swing.SpectrumDialog;

import com.xuggle.xuggler.IAudioSamples;


/**
 * Used to calculate power spectrum from audio file.
 *
 * @author Douglas Teoh
 */
public final class SpectrumProcessor extends Thread {

    /** */
    private static final int NUM_BANDS = 30;

    private BlockingQueue<AudioSample> incoming;

    /** Table of window function values. */
    private double[] window;

    /** Table of indices to use for selected frequencies. */
    private int[] indices;

    private double[] resultBuffer;
    private double[] tempCalcBuffer;
    private double[] inputSamples;
    private double[] freqs;

    private FastFourierTransformer fft;

    private Spectrum spectrumView;

    private double scaler = 17.127D;

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

                    int len = (int) Math.ceil(Math.log(
                                (samplesSize / 2)
                                / (double) samples.getChannels())
                            / Math.log(2));
                    len = (int) Math.pow(2, len);
                    inputSamples = new double[len];
                    tempCalcBuffer = new double[len];
                    resultBuffer = new double[len];

                    scaler = scaler / (double) len;
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

                // 2. Perform FFT on random segments of the buffer.

                for (int i = 0; i < inputSamples.length; i++) {
                    tempCalcBuffer[i] = (scaler * inputSamples[i]) * window[i];
                }

                Complex[] freqMags = fft.transform(tempCalcBuffer);

                for (int i = 0; i < resultBuffer.length; i++) {
                    resultBuffer[i] = freqMags[i].abs() * freqMags[i].abs();
                }

                // 3. Convert results into spectral power.
                for (int i = 0; i < ((resultBuffer.length / 2) + 1); i++) {
                    resultBuffer[i] = 10 * Math.log10(resultBuffer[i]);

                    // Record peak power.
                    peakPower = Math.max(peakPower, resultBuffer[i]);
                }

                // 4. Normalize with respect to maximum spectral power.
                for (int i = 0; i < ((resultBuffer.length / 2) + 1); i++) {
                    resultBuffer[i] -= peakPower;
                }

                // 5. Display spectrum.
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

                current.delete();

            } catch (InterruptedException e) {
                return;
            }

        }
    }

    public void giveSample(final AudioSample sample) {
        incoming.offer(sample);
    }

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

    private void firstChannel(final ShortBuffer src, final int srcLength,
        final double[] dest, final int numChannels) {

        int len = (int) Math.ceil(Math.log(srcLength / (double) numChannels)
                / Math.log(2));
        len = (int) Math.pow(2, len);

        for (int i = 0; i < srcLength; i += numChannels) {
            dest[i / numChannels] = src.get(i);
        }
    }

}
