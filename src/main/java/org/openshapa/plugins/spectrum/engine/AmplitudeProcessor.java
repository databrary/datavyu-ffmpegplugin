package org.openshapa.plugins.spectrum.engine;

import java.io.File;

import java.nio.ShortBuffer;

import static java.util.concurrent.TimeUnit.MICROSECONDS;

import static org.gstreamer.Element.linkMany;
import static org.gstreamer.Element.linkPadsFiltered;

import javax.swing.SwingWorker;

import org.gstreamer.Bin;
import org.gstreamer.Buffer;
import org.gstreamer.Bus;
import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.GhostPad;
import org.gstreamer.Gst;
import org.gstreamer.GstObject;
import org.gstreamer.Pad;
import org.gstreamer.Pipeline;
import org.gstreamer.State;
import org.gstreamer.Structure;

import org.gstreamer.elements.AppSink;
import org.gstreamer.elements.DecodeBin;
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

        final Pipeline pipeline = new Pipeline("Processor");

        // Decoding bin.
        DecodeBin decodeBin = new DecodeBin("Decode bin");

        // Source is from a file.
        Element fileSource = ElementFactory.make("filesrc", "Input File");
        fileSource.set("location", mediaFile.getAbsolutePath());

        pipeline.addMany(fileSource, decodeBin);

        if (!linkMany(fileSource, decodeBin)) {
            LOGGER.error("Link failed: filesrc -> decodebin");
        }

        // Audio handling bin.
        final Bin audioBin = new Bin("Audio bin");

        // Set up audio converter.
        Element audioConvert = ElementFactory.make("audioconvert", null);

        // Set up audio resampler.
        Element audioResample = ElementFactory.make("audioresample", null);

        // Set up audio sink.
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

        audioBin.addMany(audioConvert, audioResample, audioOutput);

        if (!linkMany(audioConvert, audioResample)) {
            LOGGER.error("Link failed: audioconvert -> audioresample");
        }

        Caps caps = Caps.fromString(
                "audio/x-raw-int, width=16, depth=16, signed=true");

        if (!linkPadsFiltered(audioResample, null, audioOutput, null, caps)) {
            LOGGER.error("Link failed: audioresample -> appsink");
        }

        audioBin.addPad(new GhostPad("sink",
                audioConvert.getStaticPad("sink")));

        pipeline.add(audioBin);

        decodeBin.connect(new DecodeBin.NEW_DECODED_PAD() {

                @Override public void newDecodedPad(final Element element,
                    final Pad pad, final boolean last) {

                    if (pad.isLinked()) {
                        return;
                    }

                    Caps caps = pad.getCaps();
                    Structure struct = caps.getStructure(0);

                    if (struct.getName().startsWith("audio/")) {
                        pad.link(audioBin.getStaticPad("sink"));
                    }
                }
            });

        Bus bus = pipeline.getBus();
        bus.connect(new Bus.EOS() {
                public void endOfStream(final GstObject source) {

                    synchronized (data) {
                        data.notifyAll();
                    }
                }
            });

        pipeline.setState(State.PLAYING);

        synchronized (data) {
            data.wait();
        }

        pipeline.setState(State.NULL);
        pipeline.dispose();

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
