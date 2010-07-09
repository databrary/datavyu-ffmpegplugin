package org.openshapa.plugins.spectrum.engine;

import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javax.sound.sampled.SourceDataLine;

import org.openshapa.plugins.spectrum.events.TimestampListener;
import org.openshapa.plugins.spectrum.swing.SpectrumDialog;

import com.xuggle.xuggler.IAudioSamples;


/**
 * Handles playing back audio samples.
 */
public final class AudioThread extends Thread {

    /** Standard time unit used by the audio samples. */
    private static final TimeUnit TIME_UNIT = MICROSECONDS;

    /** Buffer up to x seconds of data. */
    private static final long BUFFER_CAPACITY = TIME_UNIT.convert(1, SECONDS);

    /** Buffer for storing audio samples. */
    private final Queue<AudioSample> incoming;

    /** Data line used to play audio. */
    private final SourceDataLine output;

    /** Are we closing the stream. */
    private boolean closing;

    private boolean keepGoing;

    private Set<TimestampListener> listeners;

    private ReentrantLock lock;
    private Condition condition;

    private boolean threadInitialized;

    private SpectrumProcessor spectrumProcessor;

    public AudioThread(final SourceDataLine output,
        final SpectrumDialog dialog) {

        incoming = new LinkedList<AudioSample>();
        listeners = new HashSet<TimestampListener>();
        lock = new ReentrantLock(true);
        condition = lock.newCondition();

        this.output = output;

        setDaemon(true);
        setName("AudioOutput-" + getName());

        closing = false;
        keepGoing = true;

        threadInitialized = false;

        spectrumProcessor = new SpectrumProcessor(dialog);
    }

    public void addTimestampListener(final TimestampListener listener) {
        listeners.add(listener);
    }

    public void removeTimestampListener(final TimestampListener listener) {
        listeners.remove(listener);
    }

    @Override public void run() {

        synchronized (this) {
            threadInitialized = true;
            this.notifyAll();
        }

        AudioSample current = null;

        while (keepGoing) {
            lock.lock();

            try {

                while (keepGoing && ((current = incoming.poll()) == null)) {

                    // Block until input thread gives some samples.
                    condition.await();
                }
            } catch (InterruptedException e) {
                return;
            } finally {
                condition.signalAll();
                lock.unlock();
            }

            if (current != null) {

                try {
                    lock.lock();

                    spectrumProcessor.giveSample(current.copy());
                    playAudio(current);

                    // Give a chance for input thread to give sample.
                    condition.signalAll();
                    lock.unlock();
                } finally {
                    current.delete();
                }
            }
        }

    }

    public void begin() {

        synchronized (this) {
            this.start();
            spectrumProcessor.start();

            try {

                while (!threadInitialized) {
                    this.wait();
                }
            } catch (InterruptedException e) {

                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

    public void giveSample(final AudioSample sample) {

        lock.lock();

        try {

            while (keepGoing && !incoming.isEmpty()
                    && ((sample.getTimestamp()
                            - incoming.peek().getTimestamp())
                        > BUFFER_CAPACITY)) {

                try {
                    condition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();

                    return;
                }
            }

            if (keepGoing) {
                incoming.offer(sample);
            }

            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void stopAudioOutput() {
        lock.lock();

        if (output != null) {
            output.stop();
        }

        condition.signalAll();
        lock.unlock();
    }

    public void startAudioOutput() {
        lock.lock();

        if (output != null) {
            output.start();
        }

        condition.signalAll();
        lock.unlock();
    }

    /**
     * Remove samples from the input buffer that have time stamp < minTime.
     *
     * @param minTime
     *            Time in microseconds.
     */
    public void clearInputBuffer(final long minTime) {
        lock.lock();

        AudioSample s = null;

        while (!incoming.isEmpty()) {
            s = incoming.peek();

            if (s.getTimestamp() < minTime) {
                incoming.remove();
            } else {
                break;
            }
        }

        condition.signalAll();
        lock.unlock();
    }

    /**
     * Remove all samples from the input buffer.
     */
    public void clearInputBuffer() {
        lock.lock();

        incoming.clear();

        condition.signalAll();
        lock.unlock();
    }

    public void clearAudioBuffer() {
        lock.lock();

        // output.drain();
        output.flush();

        condition.signalAll();
        lock.unlock();
    }

    public void interrupt() {
        super.interrupt();

        lock.lock();

        condition.signalAll();
        lock.unlock();
    }

    public void close() {
        lock.lock();

        try {
            closing = true;

            keepGoing = false;

            // Close output line.
            output.stop();
            output.close();

            // Stop the spectrum processing thread.
            spectrumProcessor.interrupt();

            closing = false;

            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private void playAudio(final AudioSample sample) {

        IAudioSamples xuggSample = sample.getSamples();

        if (!closing) {
            int size = xuggSample.getSize();
            output.write(xuggSample.getData().getByteArray(0, size), 0, size);

            // long lineTime = MILLISECONDS.convert(output
            // .getMicrosecondPosition(), MICROSECONDS);
            long lineTime = MILLISECONDS.convert(sample.getTimestamp(),
                    sample.getTimeUnit());

            for (TimestampListener listener : listeners) {
                listener.notifyTime(lineTime);
            }
        }
    }


}
