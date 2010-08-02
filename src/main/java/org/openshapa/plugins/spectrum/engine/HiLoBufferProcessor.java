package org.openshapa.plugins.spectrum.engine;

import java.nio.ShortBuffer;

import org.gstreamer.Buffer;
import org.gstreamer.Element;

import org.gstreamer.elements.AppSink;
import org.gstreamer.elements.AppSink.NEW_BUFFER;

import org.openshapa.plugins.spectrum.models.StereoAmplitudeData;

import com.sun.jna.Pointer;


/**
 * Picks the highest and lowest value from each incoming buffer.
 */
public class HiLoBufferProcessor implements NEW_BUFFER {

    /** The sink to pull buffers from. */
    private AppSink sink;

    /** Number of audio channels in the buffer. */
    private int mediaChannels;

    /** Buffer to store picked points in. */
    private StereoAmplitudeData data;

    /**
     * Constructs a new buffer processor.
     *
     * @param sink
     *            The sink to pull data from.
     * @param mediaChannels
     *            Number of audio channels.
     * @param data
     *            Result buffer.
     */
    public HiLoBufferProcessor(final AppSink sink, final int mediaChannels,
        final StereoAmplitudeData data) {
        this.sink = sink;
        this.mediaChannels = mediaChannels;
        this.data = data;
    }

    @Override public void newBuffer(final Element elem,
        final Pointer userData) {
        Buffer buf = sink.pullBuffer();

        if (buf != null) {

            /*
             * Divide by two to convert from byte buffer length
             * to short buffer length.
             */
            int size = buf.getSize() / 2;

            ShortBuffer sb = buf.getByteBuffer().asShortBuffer();

            // System.out.println("Buffer size=" + size + ", Timestamp="
            // + buf.getTimestamp().toMillis());

            // Find largest and smallest left channel data.
            if (mediaChannels >= 1) {
                double largest = Double.MIN_VALUE;
                double smallest = Double.MAX_VALUE;

                for (int i = 0; i < size; i += mediaChannels) {
                    double val = sb.get(i);

                    largest = Math.max(val, largest);
                    smallest = Math.min(smallest, val);
                }

                data.addDataL(largest);
                data.addDataL(smallest);
            }

            // Find largest and smallest right channel data.
            if (mediaChannels >= 1) {
                double largest = Double.MIN_VALUE;
                double smallest = Double.MAX_VALUE;

                for (int i = 1; i < size; i += mediaChannels) {
                    double val = sb.get(i);

                    largest = Math.max(val, largest);
                    smallest = Math.min(smallest, val);
                }

                data.addDataR(largest);
                data.addDataR(smallest);
            }
        }
    }

}
