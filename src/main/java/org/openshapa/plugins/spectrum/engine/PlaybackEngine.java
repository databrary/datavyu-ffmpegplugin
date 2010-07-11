package org.openshapa.plugins.spectrum.engine;


import java.io.File;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import org.openshapa.plugins.spectrum.events.TimestampListener;
import org.openshapa.plugins.spectrum.mediatools.AudioPlaybackTool;
import org.openshapa.plugins.spectrum.mediatools.VolumeTool;
import org.openshapa.plugins.spectrum.swing.SpectrumDialog;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.ToolFactory;

import com.xuggle.xuggler.IContainer;

import static java.util.concurrent.TimeUnit.MILLISECONDS;


/**
 * Audio playback engine.
 */
public final class PlaybackEngine extends Thread implements TimestampListener {

    /** Number of microseconds in one millisecond. */
    private static final long MILLISECOND = 1000;

    /** Frame seeking tolerance. */
    private static final long TOLERANCE = 5 * MILLISECOND;

    /** Handles reading the audio file. */
    private IMediaReader mediaReader;

    /** Current engine state. */
    private volatile EngineState engineState;

    /** Current time being played. */
    private long currentTime;

    /** Seek time. */
    private long newTime;

    /** Queue for engine commands. */
    private volatile BlockingQueue<EngineState> commandQueue;

    /** Audio file being handled. */
    private File audioFile;

    /** Handles audio playback. */
    private AudioPlaybackTool playbackTool;

    /** Dialog for showing the spectral data. */
    private SpectrumDialog dialog;

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
            // System.out.println("Command queue: " + commandQueue);

            try {

                engineState = commandQueue.take();

                switch (engineState) {

                case INITIALIZING:
                    engineInitializing();

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

                case STOP:
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

        // Set up media reader.
        setupMediaReader();

        /*
         * Buffer some packets so that threads are started and spectrum
         * display is showing. One millisecond chosen through trial-and-error.
         * Anything less and nothing happens visually. Too much and the updating
         * spectrum display gives the illusion that it has started playing.
         */
        while (getCurrentTime()
                < MILLISECONDS.convert(1000, TimeUnit.MICROSECONDS)) {
            mediaReader.readPacket();
        }

        /*
         * Cannot start the Java sound system output lines in stopped state;
         * audio won't play. Stop the output here after buffering so that the
         * user doesn't hear any output from the sound system.
         */
        engineStop();

        engineState = EngineState.TASK_COMPLETE;
    }

    /**
     * Set up the media reader.
     */
    private void setupMediaReader() {

        Runnable edtTask = new Runnable() {
                @Override public void run() {
                    dialog.getContentPane().removeAll();
                }
            };
        SwingUtilities.invokeLater(edtTask);

        // Set up media reader.
        mediaReader = ToolFactory.makeReader(audioFile.getAbsolutePath());
        mediaReader.open();

        // Set up tool chain.
        VolumeTool volumeTool = new VolumeTool(1D);
        playbackTool = new AudioPlaybackTool(dialog);
        playbackTool.addTimestampListener(this);
        volumeTool.addListener(playbackTool);
        mediaReader.addListener(volumeTool);
    }

    /**
     * Handles seeking through the current audio file.
     */
    private void engineSeeking() {

        if (!mediaReader.isOpen()) {
            mediaReader.open();
        }

        IContainer container = mediaReader.getContainer();

        if (container.isOpened()) {

            long seekTime = Math.max(0, newTime * 1000);
            seekTime = Math.min(seekTime, container.getDuration());

            long minTime = Math.max(seekTime - TOLERANCE, 0);
            long maxTime = Math.min(seekTime + TOLERANCE,
                    container.getDuration());

            playbackTool.clearWaitBuffer();

            if (MILLISECONDS.convert(seekTime, TimeUnit.MICROSECONDS)
                    >= currentTime) {

                /*
                 * Don't bother with the seek API when moving forwards, it
                 * either overshoots or undershoots the target time frame,
                 * resulting in many incoming seek requests.
                 */

                // System.out.println("FORWARDS "
                // + MILLISECONDS.convert(seekTime, TimeUnit.MICROSECONDS)
                // + " FROM " + currentTime);

                while ((getCurrentTime()
                            < MILLISECONDS.convert(minTime,
                                TimeUnit.MICROSECONDS))
                        && commandQueue.isEmpty()) {

                    if (mediaReader.readPacket() != null) {
                        return;
                    }

                    /*
                     * Update these calculations because a new seek command
                     * might have been given even before we are done.
                     */
                    seekTime = Math.max(0, newTime * 1000);
                    seekTime = Math.min(seekTime, container.getDuration());
                    minTime = Math.max(seekTime - TOLERANCE, 0);
                }

                /*
                 * Clear the buffer because we probably have packets that we do
                 * not want after seeking manually.
                 */
                playbackTool.clearWaitBuffer();
            } else {
                /*
                 * For seeking backwards, it is easier to let it overshoot then
                 * seek forward when the next seek command arrives.
                 */

                // System.out.println("BACKWARDS "
                // + MILLISECONDS.convert(seekTime, TimeUnit.MICROSECONDS)
                // + " FROM " + currentTime);

                mediaReader.getContainer().seekKeyFrame(-1, minTime, seekTime,
                    maxTime, IContainer.SEEK_FLAG_BACKWARDS);

                playbackTool.clearWaitBuffer();
            }

            /*
             * Just read one packet ahead of the possibly incoming play command.
             * This also ensures that we can jog and see an update on the
             * spectrum.
             */
            mediaReader.readPacket();
        }

        /*
         * Mark engine state with task complete so that isPlaying returns false
         * while we are jogging.
         */
        engineState = EngineState.TASK_COMPLETE;
    }

    /**
     * Start playing back the audio file.
     */
    private void enginePlaying() {

        playbackTool.startOutput();

        while (commandQueue.isEmpty()) {

            if (mediaReader.readPacket() != null) {
                return;
            }
        }
    }

    /**
     * Stop audio output.
     */
    private void engineStop() {
        playbackTool.stopOutput();
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
        commandQueue.offer(EngineState.STOP);
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

        if (engineState != EngineState.SEEKING) {
            commandQueue.offer(EngineState.SEEKING);
        }
    }

    /**
     * @return Current time in the audio file.
     */
    public long getCurrentTime() {
        return currentTime;
    }

    /**
     * Notify the engine about the temporal position in the audio file being
     * played back.
     *
     * @see org.openshapa.plugins.spectrum.events.TimestampListener#notifyTime(long)
     */
    @Override public void notifyTime(final long time) {
        currentTime = time;
    }

    /**
     * Shutdown the engine.
     */
    public void shutdown() {
        playbackTool.stopOutput();
        playbackTool.shutdown();
        mediaReader.close();
    }

}
