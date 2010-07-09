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

    private Map<Integer, AudioThread> audioThreads;

    private AudioThread authThread;

    private SourceDataLine authLine;

    private SpectrumDialog dialog;

    public AudioPlaybackTool(final SpectrumDialog dialog) {
        listeners = new HashSet<TimestampListener>();
        audioThreads = new HashMap<Integer, AudioThread>();
        this.dialog = dialog;
    }

    public void addTimestampListener(final TimestampListener listener) {

        synchronized (this) {
            listeners.add(listener);

            if (authThread != null) {
                authThread.addTimestampListener(listener);
            }
        }
    }

    public void removeTimestampListener(final TimestampListener listener) {

        synchronized (this) {
            listeners.remove(listener);

            if (authThread != null) {
                authThread.removeTimestampListener(listener);
            }
        }
    }

    public void startOutput() {

        synchronized (this) {

            for (AudioThread at : audioThreads.values()) {
                at.startAudioOutput();
            }
        }
    }

    public void stopOutput() {

        synchronized (this) {

            for (AudioThread at : audioThreads.values()) {
                at.stopAudioOutput();
            }
        }
    }

    /**
     * @param seekTime
     *            Time in microseconds.
     */
    public void seeking(final long seekTime) {

        // synchronized (this) {
        //
        // for (AudioThread at : audioThreads.values()) {
        // at.clearInputBuffer(seekTime);
        // at.clearAudioBuffer();
        // }
        // }
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
            at.interrupt();
            at.close();
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

                if (authLine == null) {
                    authLine = line;
                }

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
