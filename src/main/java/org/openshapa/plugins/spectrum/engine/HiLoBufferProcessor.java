package org.openshapa.plugins.spectrum.engine;

import java.nio.ShortBuffer;

import org.gstreamer.Buffer;
import org.gstreamer.Element;

import org.gstreamer.elements.AppSink;
import org.gstreamer.elements.AppSink.NEW_BUFFER;

import org.openshapa.plugins.spectrum.models.StereoAmplitudeData;

import com.sun.jna.Pointer;


public class HiLoBufferProcessor implements NEW_BUFFER {

    private AppSink sink;

    private int mediaChannels;

    private StereoAmplitudeData data;

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
