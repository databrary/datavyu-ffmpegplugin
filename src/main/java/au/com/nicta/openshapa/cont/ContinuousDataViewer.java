package au.com.nicta.openshapa.cont;

/**
 * Default interface for all Continuous Data Viewers.
 *
 * @author FGA
 */
public interface ContinuousDataViewer {

    void createNewCell();

    /**
     * Jogs the data stream backwards by a single unit (i.e. frame for movie)
     */
    void jogBack();

    /**
     * Stops the playback of the continous data stream.
     */
    void stop();

    /**
     * Jogs the data stream forwards by a single unit (i.e. frame for movie).
     */
    void jogForward();

    void shuttleBack();

    /**
     * Pauses the playback of the continous data stream.
     */
    void pause();

    void shuttleForward();

    /**
     * Rewinds the continous data stream at a speed 32x normal.
     */
    void rewind();

    /**
     * Plays the continous data stream at a regular 1x normal speed.
     */
    void play();

    /**
     * Fast forwards a continous data stream at a speed 32x normal.
     */
    void forward();

    void setCellOffset();

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

    void setNewCellOnset();
    void syncCtrl();
    void sync();
    void setCellOnset();
} //End of ContinuousDataViewer interface definition
