/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openshapa.views.continuous;

import java.io.File;
import javax.swing.JFrame;

/**
 * @todo    A generic definition for 'jog' is required. 'jog' is currently defined in terms of
 *          frames, which makes sense for movies, but may not for other data sources. If jog is not
 *          available for one type of data source then it will need to be precluded from all others
 *          to prevent sync issues.
 *
 * @author pwaller
 */
public interface DataViewer {


    /**
     *
     */
    JFrame getParentJFrame();

    /**
     * Sets the data feed for this viewer.
     *
     * @param dataFeed The new data feed for this viewer.
     */
    void setDataFeed(final File dataFeed);

    /**
     * Stops the playback of the continous data stream.
     */
    void stop();

    /**
     * Jogs the data stream forwards by a single unit (i.e. frame for movie).
     */
    void jogForward();

    /**
     * Jogs the data stream backwards by a single unit (i.e. frame for movie)
     */
    void jogBack();

    /**
     * Shuttles the video stream backwards by the current shuttle speed.
     * Repetative calls to shuttleBack increases the speed at which we reverse.
     */
    void shuttleBack();

    /**
     * Pauses the playback of the continous data stream.
     */
    void pause();

    /**
     * Shuttles the video stream forwards by the current shuttle speed.
     * Repetative calls to shuttleFoward increases the speed at which we fast
     * forward.
     */
    void shuttleForward();

    /**
     * Fast forwards a continous data stream at a speed 32x normal.
     */
    void forward();

    /**
     * Rewinds the continous data stream at a speed 32x normal.
     */
    void rewind();

    /**
     * Plays the continous data stream at a regular 1x normal speed.
     */
    void play();

    /**
     * Find can be used to seek within a continous data stream - allowing the
     * caller to jump to a specific time in the datastream.
     *
     * @param milliseconds The time within the continous data stream, specified
     * in milliseconds from the start of the stream.
     */
    void find(final long milliseconds);

    /**
     * Go back by the specified number of milliseconds and continue playing the
     * data stream.
     *
     * @param milliseconds The number of milliseconds to jump back by.
     */
    void goBack(final long milliseconds);

    /**
     * Jogs the movie by a specified number of frames.
     *
     * @param offset The number of frames to jog the movie by.
     *
     * @throws Exception If unable to jog the movie by the specified number
     * of frames.
     */
    void jog(final int offset) throws Exception;

    /**
     * @return The current position within the data feed in milliseconds.
     */
    long getCurrentTime() throws Exception;
}
