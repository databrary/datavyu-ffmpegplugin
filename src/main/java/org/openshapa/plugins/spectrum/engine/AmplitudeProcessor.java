package org.openshapa.plugins.spectrum.engine;

import java.io.File;

import java.util.List;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import static org.gstreamer.Element.linkMany;
import static org.gstreamer.Element.linkPadsFiltered;

import javax.swing.SwingUtilities;
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

import org.openshapa.plugins.spectrum.models.AmplitudeBlock;
import org.openshapa.plugins.spectrum.models.ProcessorConstants;
import org.openshapa.plugins.spectrum.models.StereoData;

import com.sun.jna.Pointer;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;


/**
 * Worker thread for processing audio amplitude data. Only processes audio
 * channels one and two. Assumes 16-bit audio.
 */
public final class AmplitudeProcessor
    extends SwingWorker<StereoData, AmplitudeBlock> {

    /** Logger for this class. */
    private static final Logger LOGGER = UserMetrix.getLogger(
            AmplitudeProcessor.class);

    static {
        Gst.init();
    }

    /** Media file to process. */
    private final File mediaFile;

    /** Number of channels in the audio file. */
    private final int numChannels;

    /** The processed amplitude data. */
    private StereoData data;

    private Progress progressHandler;

    /** Internal progress handler is used to put things in the right thread. */
    private final Progress internalHandler = new Progress() {

            @Override public void overallProgress(final double percentage) {
                SwingUtilities.invokeLater(new Runnable() {
                        @Override public void run() {

                            if (progressHandler != null) {
                                progressHandler.overallProgress(percentage);
                            }
                        }
                    });
            }

            @Override public void blockDone(final AmplitudeBlock block) {
                block.normalize();
                publish(block);
            }

            @Override public void allDone(final StereoData allData) {
            }
        };

    private FixedHiLoBufferProcessor bufProc;

    private Pipeline pipeline;

    private Bin audioBin;

    private Element fileSource;

    private DecodeBin decodeBin;

    /**
     * Creates a new worker thread.
     *
     * @param mediaFile
     *            Media file to process.
     * @param numChannels
     *            Number of channels in the audio file.
     * @param progressHandler
     *            Progress handler.
     */
    public AmplitudeProcessor(final File mediaFile, final int numChannels,
        final Progress progressHandler) {
        this.mediaFile = mediaFile;
        this.numChannels = numChannels;
        this.progressHandler = progressHandler;
        data = new StereoData(ProcessorConstants.BLOCK_SZ, internalHandler);
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

        setup();
    }

    private void setup() {
        pipeline = new Pipeline("Processor");

        // Decoding bin.
        decodeBin = new DecodeBin("Decode bin");

        // Source is from a file.
        fileSource = ElementFactory.make("filesrc", "Input File");
        fileSource.set("location", mediaFile.getAbsolutePath());

        pipeline.addMany(fileSource, decodeBin);

        if (!linkMany(fileSource, decodeBin)) {
            LOGGER.error("Link failed: filesrc -> decodebin");
        }

        // Audio handling bin.
        audioBin = new Bin("Audio bin");

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

                    if (data != null) {
                        double p = data.getSize()
                            / (double) ProcessorConstants.NUM_POINTS;
                        internalHandler.overallProgress(p);
                    }
                }
            });

        bufProc = new FixedHiLoBufferProcessor(appSink, numChannels, data,
                ProcessorConstants.NUM_POINTS);
        appSink.connect(bufProc);

        audioBin.addMany(audioConvert, audioResample, audioOutput);

        if (!linkMany(audioConvert, audioResample)) {
            LOGGER.error("Link failed: audioconvert -> audioresample");
        }

        Caps caps = Caps.fromString("audio/x-raw-int, width=16, depth="
                + ProcessorConstants.DEPTH + ", signed=true");

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
                        Bin bin = audioBin;

                        if (bin != null) {
                            pad.link(bin.getStaticPad("sink"));
                        }
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
    }

    /**
     * Process amplitude data.
     *
     * @see javax.swing.SwingWorker#doInBackground()
     */
    @Override protected StereoData doInBackground() throws Exception {

        if (isCancelled()) {
            return null;
        }

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
                    return null;
                }
            }
        }

        // Normalize any blocks that haven't been normalized.
        for (AmplitudeBlock block : data.getDataBlocks()) {

            if (!block.isNormalized()) {
                block.normalize();
            }
        }

        return data;
    }

    @Override protected void process(final List<AmplitudeBlock> chunks) {

        // Thread got cancelled.
        if (progressHandler == null) {
            return;
        }

        for (AmplitudeBlock block : chunks) {
            progressHandler.blockDone(block);
        }
    }

    /**
     * Update the track data.
     *
     * @see javax.swing.SwingWorker#done()
     */
    @Override protected void done() {

        try {

            StereoData result = get();

            if (result != null) {
                progressHandler.overallProgress(1);
                progressHandler.allDone(result);
            }

        } catch (Exception e) {
            // Do not log; the exception that is generated is normal
            // (thread interruptions and subsequently, task cancellation.).
        } finally {

            // Dispose Gstreamer stuff.
            audioBin.setState(State.NULL);
            fileSource.setState(State.NULL);
            pipeline.setState(State.NULL);

            // Have to explicitly delete all refs or we leak memory because
            // JVM is still holding onto these threads.
            data = null;

            if (bufProc != null) {
                bufProc.clearRefs();
                bufProc = null;
            }

            progressHandler = null;
        }

    }

}
