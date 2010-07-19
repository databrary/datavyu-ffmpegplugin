package org.openshapa.plugins.spectrum.engine;

import java.io.File;

import java.nio.ShortBuffer;

import static java.util.concurrent.TimeUnit.MICROSECONDS;

import javax.swing.SwingWorker;

import org.gstreamer.Buffer;
import org.gstreamer.Bus;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Gst;
import org.gstreamer.GstObject;
import org.gstreamer.State;

import org.gstreamer.elements.AppSink;
import org.gstreamer.elements.PlayBin;
import org.gstreamer.elements.AppSink.NEW_BUFFER;

import org.openshapa.plugins.spectrum.models.StereoAmplitudeData;
import org.openshapa.plugins.spectrum.swing.AmplitudeTrack;

import com.sun.jna.Pointer;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;


/**
 * Worker thread for processing audio amplitude data. Only processes audio
 * channels one and two. Assumes 16-bit audio.
 */
public final class AmplitudeProcessor
    extends SwingWorker<StereoAmplitudeData, StereoAmplitudeData> {

    private static final Logger LOGGER = UserMetrix.getLogger(
            AmplitudeProcessor.class);

    /** Media file to process. */
    private File mediaFile;

    /** Track to send processed data to. */
    private AmplitudeTrack track;

    /** Number of channels in the audio file. */
    private final int numChannels;

    /**
     * Creates a new worker thread.
     *
     * @param mediaFile
     *            Media file to process.
     * @param track
     *            Track to send processed data to.
     * @param numChannels
     *            number of channels in the audio file.
     */
    public AmplitudeProcessor(final File mediaFile, final AmplitudeTrack track,
        final int numChannels) {
        this.mediaFile = mediaFile;
        this.track = track;
        this.numChannels = numChannels;
    }

    /**
     * Process amplitude data.
     *
     * @see javax.swing.SwingWorker#doInBackground()
     */
    @Override protected StereoAmplitudeData doInBackground() throws Exception {
        final StereoAmplitudeData data = new StereoAmplitudeData();

        Gst.init();

        final PlayBin pb = new PlayBin("Processor");

        pb.setInputFile(mediaFile);

        pb.setVideoSink(ElementFactory.make("fakesink", "videosink"));

        Element audioOutput = ElementFactory.make("appsink", "audiosink");
        final AppSink appSink = (AppSink) audioOutput;
        appSink.set("emit-signals", true);
        appSink.setSync(false);
        appSink.connect(new NEW_BUFFER() {
                @Override public void newBuffer(final Element elem,
                    final Pointer userData) {
                    Buffer buf = appSink.pullBuffer();

                    if (buf != null) {

                        int size = buf.getSize() / 2;

                        if (!data.isTimeIntervalSet()) {

                            long timestamp = buf.getTimestamp().convertTo(
                                    MICROSECONDS);

                            if (timestamp != 0) {
                                data.setTimeInterval(timestamp / 2,
                                    MICROSECONDS);
                            }
                        }

                        ShortBuffer sb = buf.getByteBuffer().asShortBuffer();

                        if (numChannels >= 1) {
                            data.addDataL(sb.get(0));
                            data.addDataL(sb.get((size / numChannels) - 1));
                        }

                        if (numChannels >= 2) {
                            data.addDataR(sb.get(1));
                            data.addDataR(sb.get(size / numChannels));
                        }
                    }
                }
            });
        pb.setAudioSink(appSink);

        Bus bus = pb.getBus();
        bus.connect(new Bus.EOS() {
                public void endOfStream(final GstObject source) {
                    pb.setState(State.NULL);

                    synchronized (AmplitudeProcessor.class) {
                        AmplitudeProcessor.class.notifyAll();
                    }
                }
            });

        pb.setState(State.PLAYING);

        synchronized (AmplitudeProcessor.class) {
            AmplitudeProcessor.class.wait();
        }

        pb.dispose();

        data.normalizeL();
        data.normalizeR();

        return data;
    }


    /**
     * Update the track data.
     *
     * @see javax.swing.SwingWorker#done()
     */
    @Override protected void done() {

        try {
            track.setData(get());
            track.repaint();
        } catch (Exception e) {
            LOGGER.error(e);
        }

    }

}
