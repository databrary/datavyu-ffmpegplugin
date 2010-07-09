package org.openshapa.plugins.spectrum.mediatools;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.openshapa.plugins.spectrum.engine.AudioSample;
import org.openshapa.plugins.spectrum.engine.AudioThread;
import org.openshapa.plugins.spectrum.events.TimestampListener;
import org.openshapa.plugins.spectrum.swing.SpectrumDialog;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;

import com.xuggle.mediatool.IMediaCoder;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.event.IAudioSamplesEvent;
import com.xuggle.mediatool.event.ICloseEvent;

import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;


/**
 * Tool for playing back audio through the Java sound system.
 */
public final class AudioPlaybackTool extends MediaListenerAdapter {

    private static final Logger LOGGER = UserMetrix.getLogger(
            AudioPlaybackTool.class);

    /** The container which is to be viewed */
    private IContainer mContainer;

    /** Listeners interested in current timestamp. */
    private Set<TimestampListener> listeners;

    /** Mapping from stream indices to audio threads. */
    private Map<Integer, AudioThread> audioThreads;

    /** Authoritative audio thread. */
    private AudioThread authThread;

    /** Dialog for showing the spectrum data. */
    private SpectrumDialog dialog;

    /**
     * @param dialog
     *            Dialog for showing the spectrum data.
     */
    public AudioPlaybackTool(final SpectrumDialog dialog) {
        listeners = new HashSet<TimestampListener>();
        audioThreads = new HashMap<Integer, AudioThread>();
        this.dialog = dialog;
    }

    /**
     * Add a listener interested in timestamp events.
     *
     * @param listener
     *            Listener to add.
     */
    public void addTimestampListener(final TimestampListener listener) {

        synchronized (this) {
            listeners.add(listener);

            if (authThread != null) {
                authThread.addTimestampListener(listener);
            }
        }
    }

    /**
     * Remove listener from being notified of timestamp events.
     *
     * @param listener
     *            Listener to remove.
     */
    public void removeTimestampListener(final TimestampListener listener) {

        synchronized (this) {
            listeners.remove(listener);

            if (authThread != null) {
                authThread.removeTimestampListener(listener);
            }
        }
    }

    /**
     * Start audio output.
     */
    public void startOutput() {

        synchronized (this) {

            for (AudioThread at : audioThreads.values()) {
                at.startAudioOutput();
            }
        }
    }

    /**
     * Stop audio output.
     */
    public void stopOutput() {

        synchronized (this) {

            for (AudioThread at : audioThreads.values()) {
                at.stopAudioOutput();
            }
        }
    }

    public void clearWaitBuffer() {

        synchronized (this) {

            for (AudioThread at : audioThreads.values()) {
                at.clearInputBuffer();
                // at.clearAudioBuffer();
            }
        }
    }

    @Override public void onAudioSamples(final IAudioSamplesEvent event) {

        if (null == mContainer) {

            // If source does not posses a container then throw exception
            if (!(event.getSource() instanceof IMediaCoder)) {
                throw new UnsupportedOperationException();
            }

            // Establish container
            mContainer = ((IMediaCoder) event.getSource()).getContainer();
        }

        // Get samples, push it into playback thread.
        IAudioSamples sample = event.getAudioSamples();
        IStream stream = mContainer.getStream(event.getStreamIndex());

        AudioThread thread = getAudioThread(stream);

        if (thread != null) {

            /*
             * Copy the reference because the underlying library uses reference
             * counting for GC.
             */
            thread.giveSample(new AudioSample(sample.copyReference(),
                    event.getTimeStamp(), event.getTimeUnit()));
        }

        super.onAudioSamples(event);
    }

    @Override public void onClose(final ICloseEvent event) {

        for (AudioThread at : audioThreads.values()) {
            at.stopAudioOutput();
            at.clearInputBuffer();
            at.clearAudioBuffer();
        }

        super.onClose(event);
    }

    private AudioThread getAudioThread(final IStream stream) {
        IStreamCoder audioCoder = stream.getStreamCoder();
        int streamIndex = stream.getIndex();
        AudioThread thread = audioThreads.get(streamIndex);

        if (thread == null) {

            try {
                AudioFormat audioFormat = new AudioFormat(
                        audioCoder.getSampleRate(),
                        (int) IAudioSamples.findSampleBitDepth(
                            audioCoder.getSampleFormat()),
                        audioCoder.getChannels(), true, false);

                // create the audio line out
                DataLine.Info info = new DataLine.Info(SourceDataLine.class,
                        audioFormat);

                SourceDataLine line = (SourceDataLine) AudioSystem.getLine(
                        info);
                line.open(audioFormat);
                line.start();

                thread = new AudioThread(line, dialog);

                if (authThread == null) {
                    authThread = thread;

                    for (TimestampListener listener : listeners) {
                        authThread.addTimestampListener(listener);
                    }
                }

                audioThreads.put(streamIndex, thread);
                thread.begin();

            } catch (LineUnavailableException e) {
                LOGGER.error("WARNING: No audio line out available.", e);
            }
        }

        return thread;
    }

}
