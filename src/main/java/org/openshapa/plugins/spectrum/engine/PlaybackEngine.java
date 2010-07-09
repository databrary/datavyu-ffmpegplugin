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


public final class PlaybackEngine extends Thread implements TimestampListener {

    /** Number of microseconds in one millisecond. */
    private static final long MILLISECOND = 1000;

    /** Frame seeking tolerance. */
    private static final long TOLERANCE = 25 * MILLISECOND;

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

    public PlaybackEngine(final File audioFile, final SpectrumDialog dialog) {
        this.audioFile = audioFile;
        commandQueue = new LinkedBlockingQueue<EngineState>();
        commandQueue.add(EngineState.INITIALIZING);

        setDaemon(true);
        setName("AudioEngine-" + getName());

        this.dialog = dialog;
    }

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
    }

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

    private void engineSeeking() {

        if (!mediaReader.isOpen()) {
            setupMediaReader();
        }

        IContainer container = mediaReader.getContainer();

        if (container.isOpened()) {

            long seekTime = Math.max(0, newTime * 1000);
            seekTime = Math.min(seekTime, container.getDuration());

            long minTime = Math.max(seekTime - TOLERANCE, 0);
            long maxTime = Math.min(seekTime + TOLERANCE,
                    container.getDuration());

            mediaReader.getContainer().seekKeyFrame(-1, minTime, seekTime,
                maxTime, IContainer.SEEK_FLAG_ANY);
        }
    }

    private void enginePlaying() {

        playbackTool.startOutput();

        while (commandQueue.isEmpty()) {

            if (mediaReader.readPacket() != null) {
                return;
            }
        }
    }

    private void engineStop() {
        playbackTool.stopOutput();
    }

    public void startPlayback() {
        commandQueue.offer(EngineState.PLAYING);
    }

    public void stopPlayback() {
        commandQueue.offer(EngineState.STOP);
    }

    public boolean isInitializing() {
        return engineState == EngineState.INITIALIZING;
    }

    public boolean isPlaying() {
        return engineState == EngineState.PLAYING;
    }

    public void seek(final long time) {
        newTime = time;
        commandQueue.offer(EngineState.SEEKING);
    }

    public long getCurrentTime() {
        return currentTime;
    }

    @Override public void notifyTime(final long time) {
        currentTime = time;
    }

}
