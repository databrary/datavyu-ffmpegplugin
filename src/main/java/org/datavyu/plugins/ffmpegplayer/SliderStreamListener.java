package org.datavyu.plugins.ffmpegplayer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;

/**
 * Updates the slider with the range from the movie stream and the current time
 * from the movie stream through the event streamData.
 *
 * @author Florian Raudies, Mountain View, CA.
 *
 */
public class SliderStreamListener implements StreamListener {

    /** The logger for this class */
    private static Logger logger = LogManager.getLogger(SliderStreamListener.class);

    /** The slider that is updated */
    private JSlider jSlider;

    /** The movie stream that we use as reference to update the slider */
    private MoviePlayer moviePlayer;

    /** Time base for the slider from time in seconds to slider time */
    private static final int SLIDER_TIME_BASE = 1000;

    /**
     * Convert from time in seconds into the time base for the slider.
     *
     * @param timeInSec The stream time in seconds.
     *
     * @return The stream time in the time base for the slider.
     */
    private static int toSliderTime(double timeInSec) {
        return (int) (timeInSec * SLIDER_TIME_BASE);
    }

    /**
     * Converts the stream time from the time base of the slider into seconds.
     *
     * @param sliderTime The stream time in units of the slider time base.
     *
     * @return The stream time in seconds.
     */
    public static double toStreamTime(int sliderTime) {
        return ((double) sliderTime)/SLIDER_TIME_BASE;
    }

    /**
     * Create a slider stream listener.
     *
     * @param jSlider The slider.
     * @param moviePlayer The move stream.
     */
    public SliderStreamListener(JSlider jSlider, MoviePlayer moviePlayer) {
        this.jSlider = jSlider;
        this.moviePlayer = moviePlayer;
    }

    @Override
    public void streamOpened() {
        logger.info("Set minimum time to: " + moviePlayer.getStartTime() + "seconds");
        logger.info("Set maximum time to: " + moviePlayer.getEndTime() + "seconds");
        jSlider.setMinimum(toSliderTime(moviePlayer.getStartTime()));
        jSlider.setMaximum(toSliderTime(moviePlayer.getEndTime()));
    }

    @Override
    public void streamData(byte[] data) {
        jSlider.setValue(toSliderTime(moviePlayer.getCurrentTime()));
    }

    @Override
    public void streamStopped() {
        // Nothing to do here
    }

    @Override
    public void streamStarted() {
        // Nothing to do here
    }

    @Override
    public void streamClosed() {
        // Nothing to do here
    }

}
