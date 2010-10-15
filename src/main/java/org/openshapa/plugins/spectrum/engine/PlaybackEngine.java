package org.openshapa.plugins.spectrum.engine;


import java.io.File;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

import javax.swing.SwingUtilities;

import org.gstreamer.Bin;
import org.gstreamer.Buffer;
import org.gstreamer.Bus;
import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Format;
import org.gstreamer.GhostPad;
import org.gstreamer.GstObject;
import org.gstreamer.Pad;
import org.gstreamer.Pipeline;
import org.gstreamer.SeekFlags;
import org.gstreamer.SeekType;
import org.gstreamer.Structure;

import org.gstreamer.elements.AppSink;
import org.gstreamer.elements.DecodeBin;
import org.gstreamer.elements.AppSink.NEW_BUFFER;

import static org.openshapa.plugins.spectrum.SpectrumConstants.SPECTRUM_MSG_INTERVAL;
import org.openshapa.plugins.spectrum.SpectrumConstants;
import org.openshapa.plugins.spectrum.swing.Spectrum;
import org.openshapa.plugins.spectrum.swing.SpectrumDialog;
import org.openshapa.plugins.spectrum.swing.SpectrumView;

import com.sun.jna.Pointer;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;

import static org.gstreamer.Element.linkMany;
import static org.gstreamer.Element.linkPadsFiltered;


/**
 * Audio playback engine.
 */
public final class PlaybackEngine extends Thread {

    private static final Logger LOGGER = UserMetrix.getLogger(
            PlaybackEngine.class);

    /** Current engine state. */
    private volatile EngineState engineState;

    /** Seek time. */
    private long newTime;

    /** Queue for engine commands. */
    private volatile BlockingQueue<EngineState> commandQueue;

    /** Audio file being handled. */
    private File audioFile;

    /** Dialog for showing the spectral data. */
    private SpectrumDialog dialog;

    /** Audio playback speed. */
    private double playbackSpeed;

    /** Output pipeline. */
    private Pipeline pipeline;

    /** Current time in the stream; reported by the pipeline. */
    private long currentTime;

    /** Length of the media file in milliseconds. */
    private long mediaLength;

    /** Current volume level. */
    private double volume;

    /** The volume element. */
    private Element volumeElement;

    /**
     * Creates a new engine thread.
     *
     * @param audioFile
     *            The audio file to handle.
     * @param dialog
     *            The dialog used to display the spectrum.
     */
    public PlaybackEngine(final File audioFile, final SpectrumDialog dialog) {
        this.audioFile = audioFile;
        commandQueue = new LinkedBlockingQueue<EngineState>();
        commandQueue.add(EngineState.INITIALIZING);

        setDaemon(true);
        setName("AudioEngine-" + getName());

        this.dialog = dialog;

        volume = 1;
    }

    /**
     * Main engine thread.
     *
     * @see java.lang.Thread#run()
     */
    @Override public void run() {

        while (true) {

            try {

                engineState = commandQueue.take();

                switch (engineState) {

                case INITIALIZING:
                    engineInitializing();

                    break;

                case ADJUSTING_SPEED:
                    engineAdjusting();

                    break;

                case SEEKING:

                    // Just want to seek to the latest time.
                    while (commandQueue.peek() == EngineState.SEEKING) {
                        commandQueue.take();
                    }

                    engineSeeking();

                    break;

                case PLAYING:

                    /*
                     * Don't want start-stop-start-stop playback because
                     * processing loop is interrupted.
                     */
                    while (commandQueue.peek() == EngineState.PLAYING) {
                        commandQueue.take();
                    }

                    enginePlaying();

                    break;

                case STOPPING:
                    engineStop();

                    break;

                default:
                    break;
                }
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * Initialize the playback engine.
     */
    private void engineInitializing() {

        // Set up Gstreamer.
        setupGst();

        engineState = EngineState.TASK_COMPLETE;
    }

    /**
     * Set up GStreamer.
     */
    private void setupGst() {
        pipeline = new Pipeline("Pipeline");

        Element fileSource = ElementFactory.make("filesrc", "src");
        fileSource.set("location", audioFile.getAbsolutePath());

        // Decoding bin.
        DecodeBin decodeBin = new DecodeBin("decodebin");

        pipeline.addMany(fileSource, decodeBin);

        // Audio bin.
        final Bin audioBin = new Bin("audiobin");

        Element tee = ElementFactory.make("tee", "tee");

        volumeElement = ElementFactory.make("volume", "audiovolume");
        volumeElement.set("volume", volume);

        Element audioConvert = ElementFactory.make("audioconvert",
                "audioconvert1");
        Element audioResample = ElementFactory.make("audioresample",
                "audioresample");
        Element queue = ElementFactory.make("queue", "queue");
        Element audioOutput = ElementFactory.make("autoaudiosink", "audiosink");

        Element audioConvert2 = ElementFactory.make("audioconvert",
                "audioconvert2");
        Element audioResample2 = ElementFactory.make("audioresample",
                "audioresample2");
        Element queue2 = ElementFactory.make("queue", "queue2");
        Element spectrum = ElementFactory.make("spectrum", "spectrum");
        spectrum.set("bands", SpectrumConstants.FFT_BANDS);
        spectrum.set("threshold", SpectrumConstants.MIN_MAGNITUDE);
        spectrum.set("post-messages", true);

        Element spectrumSink = ElementFactory.make("fakesink", "spectrumsink");
        spectrumSink.set("sync", true);

        Element queue3 = ElementFactory.make("queue", "queue3");
        Element appSink = ElementFactory.make("appsink", "timestampListener");
        final AppSink timestamp = (AppSink) appSink;
        timestamp.set("emit-signals", true);
        timestamp.connect(new NEW_BUFFER() {
                @Override public void newBuffer(final Element elem,
                    final Pointer userData) {
                    Buffer buf = timestamp.pullBuffer();

                    if (buf != null) {
                        currentTime = buf.getTimestamp().toMillis();
                    }

                    buf.dispose();
                }
            });

        Caps spectrumCaps = Caps.fromString("audio/x-raw-int, rate="
                + SpectrumConstants.SAMPLE_RATE);

        audioBin.addMany(tee, volumeElement, audioConvert, audioResample, queue,
            audioOutput, audioConvert2, audioResample2, queue2, spectrum,
            spectrumSink, queue3, appSink);

        audioBin.addPad(new GhostPad("sink", tee.getStaticPad("sink")));

        pipeline.add(audioBin);

        if (!linkMany(fileSource, decodeBin)) {
            LOGGER.error("Link failed: filesrc ! decodebin\n");
        }

        Pad pad1 = tee.getRequestPad("src%d");
        pad1.link(volumeElement.getStaticPad("sink"));

        if (
            !linkMany(volumeElement, audioConvert, audioResample, queue,
                    audioOutput)) {
            LOGGER.error(
                "Link failed: tee ! volume ! audioconvert ! audioresample ! queue ! autoaudiosink");
        }

        Pad pad2 = tee.getRequestPad("src%d");
        pad2.link(audioConvert2.getStaticPad("sink"));

        if (!linkMany(audioConvert2, audioResample2)) {
            LOGGER.error("Link failed: tee -> audioconvert -> audioresample");
        }

        if (!linkPadsFiltered(audioResample2, null, queue2, null,
                    spectrumCaps)) {
            LOGGER.error(
                "Link failed: audioresample ! audio/x-raw-int, rate=48000 ! queue");
        }

        if (!linkMany(queue2, spectrum, spectrumSink)) {
            LOGGER.error("Link failed: queue ! spectrum ! fakesink");
        }

        Pad pad3 = tee.getRequestPad("src%d");
        pad3.link(queue3.getStaticPad("sink"));

        if (!linkMany(queue3, appSink)) {
            LOGGER.error("Link failed: tee ! queue ! appsink");
        }

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

        final Bus bus = pipeline.getBus();

        bus.connect(new Bus.ERROR() {
                public void errorMessage(final GstObject source, final int code,
                    final String message) {
                    LOGGER.error(
                        "PlaybackEngine Gstreamer Error: code=" + code
                        + " message=" + message);
                }
            });

        Runnable edtTask = new Runnable() {
                @Override public void run() {
                    final SpectrumView spectrum = new Spectrum();
                    dialog.getContentPane().removeAll();
                    dialog.setSpectrum((Spectrum) spectrum);

                    SpectrumMessage spectrumMessage = new SpectrumMessage(
                            spectrum);
                    bus.connect(spectrumMessage);
                }
            };
        SwingUtilities.invokeLater(edtTask);
    }

    /**
     * Handles seeking through the current audio file.
     */
    private void engineSeeking() {

        if (playbackSpeed != 0) {

            if (newTime < currentTime) {

                // Backward seek
                pipeline.seek(playbackSpeed, Format.TIME,
                    SeekFlags.FLUSH | SeekFlags.SEGMENT, SeekType.SET, 0,
                    SeekType.SET, NANOSECONDS.convert(newTime, MILLISECONDS));
            } else {

                // Forward seek
                pipeline.seek(playbackSpeed, Format.TIME,
                    SeekFlags.FLUSH | SeekFlags.SEGMENT, SeekType.SET,
                    NANOSECONDS.convert(newTime, MILLISECONDS), SeekType.SET,
                    NANOSECONDS.convert(mediaLength, MILLISECONDS));
            }

        } else {
            pipeline.pause();
            pipeline.getState(50, MILLISECONDS);

            if (newTime < currentTime) {

                // Backward seek
                pipeline.seek(-1D, Format.TIME,
                    SeekFlags.FLUSH | SeekFlags.SEGMENT | SeekFlags.ACCURATE,
                    SeekType.SET, NANOSECONDS.convert(newTime, MILLISECONDS),
                    SeekType.SET,
                    NANOSECONDS.convert(newTime, MILLISECONDS)
                    + SPECTRUM_MSG_INTERVAL);
            } else {
                long start = NANOSECONDS.convert(newTime, MILLISECONDS)
                    - SPECTRUM_MSG_INTERVAL;
                start = Math.max(0, start);

                // Forward seek
                pipeline.seek(1D, Format.TIME,
                    SeekFlags.FLUSH | SeekFlags.SEGMENT | SeekFlags.ACCURATE,
                    SeekType.SET, start, SeekType.SET,
                    NANOSECONDS.convert(newTime, MILLISECONDS));
            }
        }
    }

    /**
     * Adjust playback speed.
     */
    private void engineAdjusting() {

        if (playbackSpeed != 0) {
            pipeline.seek(playbackSpeed, Format.TIME,
                SeekFlags.FLUSH | SeekFlags.SEGMENT | SeekFlags.ACCURATE,
                SeekType.NONE, -1, SeekType.NONE, -1);
        } else {
            pipeline.pause();
        }

        engineState = EngineState.TASK_COMPLETE;
    }

    /**
     * Start playing back the audio file.
     */
    private void enginePlaying() {
        pipeline.play();
    }

    /**
     * Stop audio output.
     */
    private void engineStop() {
        pipeline.pause();
        engineState = EngineState.TASK_COMPLETE;
    }

    /**
     * Queue up a command to start audio playback.
     */
    public void startPlayback() {
        commandQueue.offer(EngineState.PLAYING);
    }

    /**
     * Queue up a command to stop audio playback.
     */
    public void stopPlayback() {
        commandQueue.offer(EngineState.STOPPING);
    }

    /**
     * @return True if the engine is initializing.
     */
    public boolean isInitializing() {
        return engineState == EngineState.INITIALIZING;
    }

    /**
     * @return True if the engine is playing back the audio file.
     */
    public boolean isPlaying() {
        return (engineState == EngineState.PLAYING)
            || (engineState == EngineState.SEEKING);
    }

    /**
     * Queue up a command to seek to the given time in milliseconds.
     *
     * @param time
     *            time to seek to in milliseconds.
     */
    public void seek(final long time) {
        newTime = time;

        commandQueue.offer(EngineState.SEEKING);
    }

    public void adjustSpeed(final double speed) {
        playbackSpeed = speed;
        commandQueue.offer(EngineState.ADJUSTING_SPEED);
    }

    /**
     * @return Current time in the audio file.
     */
    public long getCurrentTime() {
        return currentTime;
    }

    /**
     * @param mediaLength
     *            the mediaLength to set
     */
    public void setMediaLength(final long mediaLength) {
        this.mediaLength = mediaLength;
    }

    /**
     * @return the current volume level. 0 = mute, 100% = max.
     */
    public double getVolume() {
        return volume * 100D;
    }

    /**
     * @param volume
     *            The volume to set. Must be between 0 and 100%.
     */
    public void setVolume(final double volume) {
        assert (0 <= volume) && (volume <= 100);

        if ((volume < 0) || (volume > 100)) {
            throw new IllegalArgumentException(
                "Volume must be: 0 <= volume <= 100");
        }

        this.volume = volume / 100D;

        if (volumeElement != null) {
            volumeElement.set("volume", this.volume);
        }
    }

    /**
     * Shutdown the engine.
     */
    public void shutdown() {
        pipeline.stop();
    }

}
