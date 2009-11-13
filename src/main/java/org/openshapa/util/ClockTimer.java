package org.openshapa.util;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * ClockTime is a class which can be used as a time marshall to keep multiple
 * objects in sync.
 */
public final class ClockTimer {

    //--------------------------------------------------------------------------
    //
    //

    /** Clock clockTick period. */
    private static final long CLOCK_TICK = 10L;

    /** Clock initial delay. */
    private static final long CLOCK_DELAY = 0L;

    /** Used to convert between nanoseconds and milliseconds. */
    private static final long NANO_IN_MILLI = 1000000L;


    //--------------------------------------------------------------------------
    //
    //

    /**
     * Listener interface for clock 'ticks'.
     */
    public interface ClockListener {

        /**
         * @param time Current time in milliseconds
         */
        void clockTick(long time);

        /**
         * @param time Current time in milliseconds
         */
        void clockStart(long time);

        /**
         * @param time Current time in milliseconds
         */
        void clockStop(long time);

        /**
         * @param rate Current (updated) rate.
         */
        void clockRate(float rate);

        /**
         * @param time Current time in milliseconds
         */
        void clockStep(long time);
    }

    //--------------------------------------------------------------------------
    //
    //

    /** Current time of the clock. */
    private double time;

    /** Used to calculate elapsed time. */
    private long nanoTime;

    /** */
    private Timer clock;

    /** */
    private boolean stopClock;

    /** */
    private boolean updateRate;

    /** */
    private boolean isStopped;

    /** */
    private boolean absTime;

    /** */
    private long stepTime;

    /** Update multiplier. */
    private float rate = 1F;

    /** */
    private Set<ClockListener> clockListeners = new HashSet<ClockListener>();


    //--------------------------------------------------------------------------
    // initialization
    //

    /**
     *
     */
    public ClockTimer() { this(0L); }

    /**
     * @param initialTime Intial clock time.
     */
    public ClockTimer(final long initialTime) {
        time = initialTime;
        isStopped = true;
    }

    //--------------------------------------------------------------------------
    //
    //

    /**
     * @param newTime Millisecond time to set clock to.
     */
    public void setTime(final long newTime) {
        if (isStopped) {
            absTime = false;
            stepTime = 0L;
            time = newTime;
            notifyStep();
        } else {
            absTime = true;
            stepTime = newTime;
            stop();
        }
    }

    /**
     * @return Current clock time.
     */
    public long getTime() { return (long) time; }

    /**
     * @param newRate Multiplier for CLOCK_TICK.
     */
    public void setRate(final float newRate) {
        rate = newRate;

        if (isStopped) { notifyRate(); }
        else           { updateRate = true; }
    }

    /**
     * @return Current clock multipler.
     */
    public float getRate() { return rate; }

    /**
     * Initiate starting of clock.
     */
    public void start() {
        if (isStopped) {
            startClock();
            notifyStart();
        }
    }

    /**
     * Set flag to stop clock at next time update (boundary).
     */
    public void stop() {
        stopClock = true;
        rate = 0;
        notifyRate();
    }

    /**
     * @param ms Time step to apply to current time when clock stopped.
     */
    public void stepTime(final long ms) {
        if (0 == ms) { return; }
        if (isStopped) {
            stepTime = 0L;
            time += ms;
            notifyStep();
        } else {
            stepTime = ms;
            stop();
        }
    }

    /**
     * @return True if clock is stopped.
     */
    public boolean isStopped() { return isStopped; }

    /**
     * @param listener Listener requiring clockTick updates.
     */
    public void registerListener(final ClockListener listener) {
        clockListeners.add(listener);
    }

    //--------------------------------------------------------------------------
    // [private] implementation
    //

    /**
     * The "tick" of the clock - updates listeners of changes in time.
     */
    private void tick() {
        long currentNano = System.nanoTime();
        time += rate * (currentNano - nanoTime) / NANO_IN_MILLI;
        nanoTime = currentNano;

        // BugzID:466 - Prevent rewind wrapping the clock past zero.
        if (time <= 0) {
            time = 0;
            stopClock = true;
        }

        if (stopClock) {
            stopClock = false;
            stopClock();
            notifyStop();

            if (absTime) {
                setTime(stepTime);
            } else {
                stepTime(stepTime);
            }

        } else if (updateRate) {
            updateRate = false;
            stopClock();
            startClock();
            notifyTick();
            notifyRate();

        } else {
            notifyTick();
        }
    }

    /**
     * Start the clock.
     */
    private void startClock() {
        nanoTime = System.nanoTime();

        clock = new Timer();
        clock.scheduleAtFixedRate(
                new TimerTask() { @Override public void run() { tick(); } },
                CLOCK_DELAY,
                CLOCK_TICK);
        isStopped = false;
    }

    /**
     * Stop the clock.
     */
    private void stopClock() {
        clock.cancel();
        clock = null;
        isStopped = true;
    }

    //
    // emit clock signals to registered listeners
    //

    /**
     * Notify clock listeners of tick event.
     */
    private void notifyTick() {
        for (ClockListener l : clockListeners) { l.clockTick((long) time); }
    }

    /**
     * Notify clock listeners of rate update event.
     */
    private void notifyRate() {
        for (ClockListener l : clockListeners) { l.clockRate(rate); }
    }

    /**
     * Notify clock listeners of start event.
     */
    private void notifyStart() {
        for (ClockListener l : clockListeners) { l.clockStart((long) time); }
    }

    /**
     * Notify clock listeners of stop event.
     */
    private void notifyStop() {
        for (ClockListener l : clockListeners) { l.clockStop((long) time); }
    }

    /**
     * Notify clock listeners of time step event.
     */
    private void notifyStep() {
        for (ClockListener l : clockListeners) { l.clockStep((long) time); }
    }
}
