package org.openshapa.plugins.spectrum.engine;

import java.io.File;

import java.util.List;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import static org.gstreamer.Element.linkMany;
import static org.gstreamer.Element.linkPadsFiltered;

import javax.swing.SwingWorker;

import org.gstreamer.Bin;
import org.gstreamer.Bus;
import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Format;
import org.gstreamer.GhostPad;
import org.gstreamer.Gst;
import org.gstreamer.GstObject;
import org.gstreamer.Pad;
import org.gstreamer.Pipeline;
import org.gstreamer.SeekFlags;
import org.gstreamer.SeekType;
import org.gstreamer.State;
import org.gstreamer.Structure;

import org.gstreamer.elements.AppSink;
import org.gstreamer.elements.DecodeBin;
import org.gstreamer.elements.AppSink.NEW_BUFFER;

import org.openshapa.plugins.spectrum.models.StereoAmplitudeData;
import org.openshapa.plugins.spectrum.swing.Amplitude;
import org.openshapa.plugins.spectrum.swing.Progress;

import com.sun.jna.Pointer;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;


/**
 * Worker thread for processing audio amplitude data. Only processes audio
 * channels one and two. Assumes 16-bit audio.
 */
public final class AmplitudeProcessor
    extends SwingWorker<StereoAmplitudeData, Integer> {

    /** Logger for this class. */
    private static final Logger LOGGER = UserMetrix.getLogger(
            AmplitudeProcessor.class);

    public enum Strategy {

        // Options to this strategy is the number of points to pick per channel.
        FIXED_HIGH_LOW
    }

    /** Media file to process. */
    private File mediaFile;

    /** Processed data handler. */
    private Amplitude dataHandler;

    /** Number of channels in the audio file. */
    private final int numChannels;

    /** The processed amplitude data. */
    private StereoAmplitudeData data;

    /** The processing strategy to use. */
    private Strategy strat;

    /** Options to the procesing strategy. */
    private int opts;

    private Progress progHandler;

    /**
     * Creates a new worker thread.
     *
     * @param mediaFile
     *            Media file to process.
     * @param handler
     *            Processed data handler.
     * @param numChannels
     *            number of channels in the audio file.
     */
    public AmplitudeProcessor(final File mediaFile, final Amplitude handler,
        final int numChannels) {
        this.mediaFile = mediaFile;
        this.dataHandler = handler;
        this.numChannels = numChannels;
        data = new StereoAmplitudeData();
    }

    /**
     * Set the time segment to process.
     *
     * @param start
     *            Start time
     * @param end
     *            End time
     * @param unit
     *            Start and end time units.
     */
    public void setDataTimeSegment(final long start, final long end,
        final TimeUnit unit) {

        if (end < start) {
            throw new IllegalArgumentException("Invalid time segment: start="
                + start + " end=" + end);
        }

        data.setDataTimeStart(start);
        data.setDataTimeEnd(end);
        data.setDataTimeUnit(unit);
    }

    /**
     * Set the processing strategy to use.
     *
     * @param strategy
     *            Strategy to use.
     * @param options
     *            Options to the strategy.
     */
    public void setStrategy(final Strategy strategy, final int options) {
        strat = strategy;
        opts = options;
    }

    public void setProgressHandler(final Progress handler) {
        progHandler = handler;
    }

    /**
     * Process amplitude data.
     *
     * @see javax.swing.SwingWorker#doInBackground()
     */
    @Override protected StereoAmplitudeData doInBackground() throws Exception {
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

        if (progHandler != null) {
            appSink.connect(new NEW_BUFFER() {
                    @Override public void newBuffer(final Element elem,
                        final Pointer userData) {
                        publish(data.sizeL());
                    }
                });
        }

        switch (strat) {

        case FIXED_HIGH_LOW:
            appSink.connect(new FixedHiLoBufferProcessor(appSink, numChannels,
                    data, opts));

            break;

        default:
            pipeline.dispose();
            throw new IllegalStateException("Processing strategy unset.");
        }

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


        pipeline.pause();

        if (pipeline.getState(2, SECONDS) != State.PAUSED) {
            return null;
        }

        pipeline.seek(1D, Format.TIME, SeekFlags.FLUSH | SeekFlags.ACCURATE,
            SeekType.SET,
            NANOSECONDS.convert(data.getDataTimeStart(),
                data.getDataTimeUnit()), SeekType.SET,
            NANOSECONDS.convert(data.getDataTimeEnd(), data.getDataTimeUnit()));

        pipeline.play();


        synchronized (data) {

            try {
                data.wait();
            } catch (InterruptedException e) {

                if (isCancelled()) {
                    pipeline.stop();
                    pipeline.dispose();

                    return null;
                }
            }
        }

        pipeline.stop();
        pipeline.dispose();

        data.normalizeL();
        data.normalizeR();

        // Calculate the time interval between points.
        long end = MILLISECONDS.convert(data.getDataTimeEnd(),
                data.getDataTimeUnit());
        long start = MILLISECONDS.convert(data.getDataTimeStart(),
                data.getDataTimeUnit());

        double interval = ((end - start) / (double) data.sizeL());
        data.setTimeInterval(interval, MILLISECONDS);

        Gst.deinit();

        return data;
    }

    @Override protected void process(final List<Integer> ints) {
        progHandler.setProgress(ints.get(ints.size() - 1) / (double) opts);
    }

    /**
     * Update the track data.
     *
     * @see javax.swing.SwingWorker#done()
     */
    @Override protected void done() {

        try {

            if (progHandler != null) {
                progHandler.setProgress(1);
            }

            StereoAmplitudeData result = get();

            if (result != null) {
                dataHandler.setData(result);
            }

        } catch (Exception e) {
            /*
             * Do not log; the exception that is generated is normal
             * (thread interruptions and subsequently, task cancellation.).
             */
        }

    }

}
