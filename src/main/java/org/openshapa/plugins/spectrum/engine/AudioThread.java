package org.openshapa.plugins.spectrum.engine;

import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.nio.ByteBuffer;

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

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;

import com.xuggle.xuggler.IAudioSamples;


/**
 * Handles playing back audio samples.
 */
public final class AudioThread extends Thread {

    private static final Logger LOGGER = UserMetrix.getLogger(
            AudioThread.class);

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

    /** Used to interrupt thread processing loop. */
    private boolean keepGoing;

    private Set<TimestampListener> listeners;

    /** Lock access to data. */
    private ReentrantLock lock;
    private Condition condition;

    /** Thread initializing state. */
    private boolean threadInitialized;

    /** Thread for calculating spectral data. */
    private SpectrumProcessor spectrumProcessor;

    /** Audio output speed. 1 = normal rate. */
    private double playbackSpeed;

    /** Precalculated unit time. */
    private double unitTime;

    private boolean printed = false;

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

        playbackSpeed = 1;
    }

    public void addTimestampListener(final TimestampListener listener) {
        listeners.add(listener);
    }

    public void removeTimestampListener(final TimestampListener listener) {
        listeners.remove(listener);
    }

    /**
     * Audio thread loop.
     *
     * @see java.lang.Thread#run()
     */
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

                    int delay = calculateUnitDelay();

                    if (delay >= 1) {

                        // playAudio(stretch(current, delay + 1),
                        // current.getTimestamp());
                        // System.out.println("Sleeping for "
                        // + (delay * unitTime));

                        try {
                            Thread.sleep((long) (Math.round(delay * unitTime)));
                        } catch (InterruptedException e) {
                        }
                    }

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

    /**
     * Start this thread and any other resources needed to be started.
     */
    public void begin() {

        synchronized (this) {
            this.start();
            spectrumProcessor.start();

            try {

                while (!threadInitialized) {
                    this.wait();
                }
            } catch (InterruptedException e) {
                LOGGER.error(e);
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Queue up the given audio sample. This method blocks until the sample is
     * queued up.
     *
     * @param sample
     *            The sample to queue up.
     */
    public void giveSample(final AudioSample sample) {

        lock.lock();

        try {

            /*
             * Block until the queue is empty or if the given sample is within
             * the buffered capacity.
             */
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

    public void setPlaybackSpeed(final double speed) {

        synchronized (this) {
            playbackSpeed = speed;
        }
    }

    public void setAudioFPS(final double fps) {

        if (fps <= 0) {
            throw new IllegalArgumentException("FPS value must be > 0.");
        }

        // 1000 because it is the number of milliseconds in a second.
        unitTime = 1000 / fps;
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

    /**
     * Flush the audio output line buffer.
     */
    public void clearAudioBuffer() {
        lock.lock();

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

    /**
     * Clean up and close resources used by the thread.
     */
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

    /**
     * Push the given sample to the Java sound system for playback.
     *
     * @param sample
     *            The system to play.
     */
    private void playAudio(final AudioSample sample) {

        IAudioSamples xuggSample = sample.getSamples();

        if (!closing) {
            int size = xuggSample.getSize();
            output.write(xuggSample.getData().getByteArray(0, size), 0, size);

            long lineTime = MILLISECONDS.convert(sample.getTimestamp(),
                    sample.getTimeUnit());

            for (TimestampListener listener : listeners) {
                listener.notifyTime(lineTime);
            }
        }
    }

    private void playAudio(final byte[] b, final long timeStamp) {

        if (!closing) {
            output.write(b, 0, b.length);

            for (TimestampListener listener : listeners) {
                listener.notifyTime(timeStamp);
            }
        }
    }

    /**
     * Calculate the number of units to delay playback.
     *
     * @return Units to delay.
     */
    private int calculateUnitDelay() {

        // Don't handle negative delays at this level.
        if (playbackSpeed <= 0) {
            return 0;
        }

        if (playbackSpeed < 1) {
            int units = (int) Math.floor(1 / playbackSpeed);
            units = units - 1;

            return units;
        }

        // Playback speed is greater than or equal to one; no delay.
        return 0;
    }

    /**
     * Calculate the number of units to delay playback.
     *
     * @return Units to delay.
     */
    private byte[] stretch(final AudioSample sample, final int factor) {

        final int originalSize = sample.getSamples().getSize();
        ByteBuffer bytes = sample.getSamples().getByteBuffer();

        byte[] result = new byte[originalSize * factor];

        // for (int i = 0; i < result.length; i++) {
        // result[i] = bytes.get((int) Math.floor(i / (double) factor));
        // }


        for (int j = 0; j < originalSize; j += 4) {

            for (int i = 0; i < factor; i++) {

                result[0 + j + (i * 4)] = bytes.get(j + 0);
                result[1 + j + (i * 4)] = bytes.get(j + 1);
                result[2 + j + (i * 4)] = bytes.get(j + 2);
                result[3 + j + (i * 4)] = bytes.get(j + 3);
            }

        }

        if (!printed) {
            printed = true;

            for (int i = 0; i < result.length; i++) {
                System.out.println(result[i]);
            }
        }


        return result;
    }


}
