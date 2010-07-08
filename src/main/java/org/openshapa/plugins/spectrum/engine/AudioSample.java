package org.openshapa.plugins.spectrum.engine;

import java.util.concurrent.TimeUnit;

import com.xuggle.xuggler.IAudioSamples;


public final class AudioSample {

    private final IAudioSamples samples;
    private final long timestamp;
    private final TimeUnit timeUnit;

    public AudioSample(final IAudioSamples samples, final long timestamp,
        final TimeUnit timeUnit) {
        this.timestamp = timestamp;
        this.samples = samples;
        this.timeUnit = timeUnit;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public IAudioSamples getSamples() {
        return samples;
    }

    public void delete() {

        if (samples != null) {
            samples.delete();
        }
    }

    public AudioSample copy() {
        return new AudioSample(samples.copyReference(), timestamp, timeUnit);
    }

}
