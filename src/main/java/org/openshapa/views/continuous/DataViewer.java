/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openshapa.views.continuous;

import java.io.File;
import javax.swing.JFrame;

/**
 * DataViewer interface.
 */
public interface DataViewer {


    /**
     * Get the display window.
     *
     * @return A JFrame that will be displayed.
     */
    JFrame getParentJFrame();

    /**
     * Sets the data feed for this viewer.
     *
     * @param dataFeed The new data feed for this viewer.
     */
    void setDataFeed(final File dataFeed);

    /**
     * @return Frames per second.
     */
    float getFrameRate();

    /**
     * @return The current position within the data feed in milliseconds.
     * @throws Exception If an error occurs.
     */
    long getCurrentTime() throws Exception;

    /**
     * Plays the continous data stream at a regular 1x normal speed.
     */
    void play();

    /**
     * Stops the playback of the continous data stream.
     */
    void stop();

    /**
     * Set the playback speed.
     *
     * @param rate Positive implies forwards, while negative implies reverse.
     */
    void setPlaybackSpeed(float rate);

    /**
     * Move the playback postion forwards or backwards relative to current
     * positon.
     *
     * @param offset The millisecond offset from current playback position.
     */
    void seek(long offset);

    /**
     * Set the playback position to an absolute value.
     *
     * @param position The absolute millisecond playback position.
     */
    void seekTo(long position);


    //--------------------------------------------------------------------------
    // [depreciate]
    //

    /**
     * Fast forwards a continous data stream at a speed 32x normal.
     */
//    void forward();

    /**
     * Rewinds the continous data stream at a speed 32x normal.
     */
//    void rewind();

    /**
     * Jogs the data stream forwards by a single unit (i.e. frame for movie).
     */
//    void jogForward();

    /**
     * Jogs the data stream backwards by a single unit (i.e. frame for movie)
     */
//    void jogBack();

    /**
     * Shuttles the video stream forwards by the current shuttle speed.
     * Repetative calls to shuttleFoward increases the speed at which we fast
     * forward.
     */
//    void shuttleForward();

    /**
     * Shuttles the video stream backwards by the current shuttle speed.
     * Repetative calls to shuttleBack increases the speed at which we reverse.
     */
//    void shuttleBack();

    /**
     * Pauses the playback of the continous data stream.
     */
//    void pause();


    /**
     * Find can be used to seek within a continous data stream - allowing the
     * caller to jump to a specific time in the datastream.
     *
     * @param milliseconds The time within the continous data stream, specified
     * in milliseconds from the start of the stream.
     */
//    void find(final long milliseconds);

    /**
     * Go back by the specified number of milliseconds and continue playing the
     * data stream.
     *
     * @param milliseconds The number of milliseconds to jump back by.
     */
//    void goBack(final long milliseconds);

    /**
     * Jogs the movie by a specified number of frames.
     *
     * @param offset The number of frames to jog the movie by.
     *
     * @throws Exception If unable to jog the movie by the specified number
     * of frames.
     */
//    void jog(final int offset) throws Exception;

}
