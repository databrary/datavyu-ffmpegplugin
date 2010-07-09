package org.openshapa.plugins.spectrum.models;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public final class StereoAmplitudeData {

    private List<Double> ampDataL;
    private List<Double> ampDataR;
    private long timeInterval;
    private TimeUnit timeUnit;

    private Double maxL;
    private Double maxR;

    public StereoAmplitudeData() {
        ampDataL = new ArrayList<Double>();
        ampDataR = new ArrayList<Double>();
        timeInterval = -1;
        maxL = Double.MIN_VALUE;
        maxR = Double.MIN_VALUE;
    }

    public void setTimeInterval(final long interval, final TimeUnit unit) {

        if (interval < 1) {
            throw new IllegalArgumentException("Expecting positive interval.");
        }

        timeInterval = interval;
        timeUnit = unit;
    }

    public boolean isTimeIntervalSet() {
        return timeInterval > 0;
    }

    public long getTimeInterval() {
        return timeInterval;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void addDataL(final double amp) {
        ampDataL.add(amp);

        if (amp > maxL) {
            maxL = amp;
        }
    }

    /**
     * @return Number of data points.
     */
    public int sizeL() {
        return ampDataL.size();
    }

    public Iterable<Double> getDataL() {
        return ampDataL;
    }

    /**
     *
     */
    public void normalizeL() {

        for (int i = 0; i < ampDataL.size(); i++) {
            ampDataL.set(i, ampDataL.get(i) / maxL);
        }
    }

    public void addDataR(final double amp) {
        ampDataR.add(amp);

        if (amp > maxR) {
            maxR = amp;
        }
    }

    /**
     * @return Number of data points.
     */
    public int sizeR() {
        return ampDataR.size();
    }

    public Iterable<Double> getDataR() {
        return ampDataR;
    }

    /**
    *
    */
    public void normalizeR() {

        for (int i = 0; i < ampDataR.size(); i++) {
            ampDataR.set(i, ampDataR.get(i) / maxR);
        }
    }

}
