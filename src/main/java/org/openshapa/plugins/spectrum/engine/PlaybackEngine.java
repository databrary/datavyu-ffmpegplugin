package org.openshapa.plugins.spectrum.engine;


import java.io.File;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import org.gstreamer.Bin;
import org.gstreamer.Bus;
import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.GhostPad;
import org.gstreamer.Gst;
import org.gstreamer.GstObject;
import org.gstreamer.Pad;
import org.gstreamer.Pipeline;
import org.gstreamer.Structure;

import org.gstreamer.elements.DecodeBin;

import org.openshapa.plugins.spectrum.SpectrumConstants;
import org.openshapa.plugins.spectrum.swing.Spectrum;
import org.openshapa.plugins.spectrum.swing.SpectrumDialog;
import org.openshapa.plugins.spectrum.swing.SpectrumView;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;

import static org.gstreamer.Element.linkMany;
import static org.gstreamer.Element.linkPadsFiltered;

import static org.apache.commons.io.FilenameUtils.isExtension;


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
    }

    /**
     * Main engine thread.
     *
     * @see java.lang.Thread#run()
     */
    @Override public void run() {

        while (true) {
            System.out.println("Command queue: " + commandQueue);

            try {

                engineState = commandQueue.take();

                switch (engineState) {

                case INITIALIZING:
                    engineInitializing();

                    break;

                case ADJUSTING_SPEED:
                    engineAdjusting();

                    break;

                case SETTING_FPS:
                    engineSettingFPS();

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
        Gst.init();

        pipeline = new Pipeline("Pipeline");

        // Decoding bin.
        DecodeBin decodeBin = new DecodeBin("Decode bin");

        // Source is from a file.
        Element fileSource = ElementFactory.make("filesrc", "Input File");
        fileSource.set("location", audioFile.getAbsolutePath());

        // Decode queue for buffering.
        Element decodeQueue = ElementFactory.make("queue", "Decode Queue");
        pipeline.addMany(fileSource, decodeQueue, decodeBin);

        if (!linkMany(fileSource, decodeQueue, decodeBin)) {
            LOGGER.error(getName() + " : Failed to link decoding bin.");
        }

        // Audio handling bin.
        final Bin audioBin = new Bin("Audio bin");

        // Set up audio converter.
        Element audioConvert = ElementFactory.make("audioconvert", null);

        // Set up audio resampler.
        Element audioResample = ElementFactory.make("audioresample", null);

        // Auto-select audio sink.
        Element audioOutput = ElementFactory.make("autoaudiosink", "sink");

        // Set up the spectrum analyzer.
        Element spectrum = ElementFactory.make("spectrum", "spectrum");
        spectrum.set("bands", SpectrumConstants.FFT_BANDS);
        spectrum.set("threshold", SpectrumConstants.MIN_MAGNITUDE);
        spectrum.set("post-messages", true);

        // Capability filter.
        Caps caps = Caps.fromString("audio/x-raw-int, rate="
                + SpectrumConstants.SAMPLE_RATE);

        audioBin.addMany(audioConvert, audioResample, spectrum, audioOutput);

        if (!linkMany(audioConvert, audioResample)) {
            LOGGER.error(getName()
                + " : Failed to link converter to resampler.");
        }

        if (!linkPadsFiltered(audioResample, null, spectrum, null, caps)) {
            LOGGER.error(getName()
                + " : Failed to apply audio capability filter.");
        }

        if (!linkMany(spectrum, audioOutput)) {
            LOGGER.error(getName() + " : Failed to link audio output.");
        }

        audioBin.addPad(new GhostPad("sink",
                audioConvert.getStaticPad("sink")));

        pipeline.add(audioBin);

        // Video handling bin
        final Bin videoBin = new Bin("Video bin");
        Element videoOutput = ElementFactory.make("fakesink", "videosink");
        videoBin.add(videoOutput);
        videoBin.addPad(new GhostPad("sink", videoOutput.getStaticPad("sink")));

        // Only add the video handling bin if we are not dealing with audio
        // files.
        if (
            !isExtension(audioFile.getAbsolutePath(),
                    new String[] { "mp3", "wav", "ogg" })) {
            pipeline.add(videoBin);
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
                    } else if (struct.getName().startsWith("video/")) {
                        pad.link(videoBin.getStaticPad("sink"));
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
        bus.connect(new Bus.EOS() {
                public void endOfStream(final GstObject source) {
                    pipeline.setState(org.gstreamer.State.NULL);
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

        if ((getCurrentTime() < newTime) && (playbackSpeed > 0)) {
            pipeline.seek(newTime, TimeUnit.MILLISECONDS);
        } else if ((getCurrentTime() > newTime) && (playbackSpeed < 0)) {
            pipeline.seek(newTime, TimeUnit.MILLISECONDS);
        } else if (playbackSpeed == 0) {
            pipeline.seek(newTime, TimeUnit.MILLISECONDS);

            /*
             * Mark engine state with task complete so that isPlaying returns
             * false
             * while we are jogging.
             */
            engineState = EngineState.TASK_COMPLETE;
        }
    }

    /**
     * Adjust playback speed.
     */
    private void engineAdjusting() {
        engineState = EngineState.TASK_COMPLETE;
    }

    private void engineSettingFPS() {
        engineState = EngineState.TASK_COMPLETE;
    }

    /**
     * Start playing back the audio file.
     */
    private void enginePlaying() {
        pipeline.setState(org.gstreamer.State.PLAYING);
    }

    /**
     * Stop audio output.
     */
    private void engineStop() {
        pipeline.stop();
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
        return pipeline.queryPosition(TimeUnit.MILLISECONDS);
    }

    /**
     * Shutdown the engine.
     */
    public void shutdown() {
        pipeline.stop();
        pipeline.setState(org.gstreamer.State.NULL);
        pipeline.dispose();
    }

}
