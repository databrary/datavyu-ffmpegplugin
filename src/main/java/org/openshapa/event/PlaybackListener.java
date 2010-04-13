package org.openshapa.event;

/**
 * Defines an interface for listening to events from a data playback controller.
 */
public interface PlaybackListener {

    /**
     * Add data button was used.
     *
     * @param The event to handle.
     */
    void addDataEvent(PlaybackEvent evt);

    /**
     * Set cell onset button was used.
     *
     * @param The event to handle.
     */
    void setCellOnsetEvent(PlaybackEvent evt);

    /**
     * Set cell offset button was used.
     *
     * @param The event to handle.
     */
    void setCellOffsetEvent(PlaybackEvent evt);

    /**
     * Go back button was used.
     *
     * @param The event to handle.
     */
    void goBackEvent(PlaybackEvent evt);

    /**
     * Rewind button was used.
     *
     * @param The event to handle.
     */
    void rewindEvent(PlaybackEvent evt);

    /**
     * Play button was used.
     *
     * @param The event to handle.
     */
    void playEvent(PlaybackEvent evt);

    /**
     * Forward button was used.
     *
     * @param The event to handle.
     */
    void forwardEvent(PlaybackEvent evt);

    /**
     * Shuttle back button was used.
     *
     * @param The event to handle.
     */
    void shuttleBackEvent(PlaybackEvent evt);

    /**
     * Stop button was used.
     *
     * @param The event to handle.
     */
    void stopEvent(PlaybackEvent evt);

    /**
     * Shuttle forward button was used.
     *
     * @param The event to handle.
     */
    void shuttleForwardEvent(PlaybackEvent evt);

    /**
     * Find button was used.
     *
     * @param The event to handle.
     */
    void findEvent(PlaybackEvent evt);

    /**
     * Jog back button was used.
     *
     * @param The event to handle.
     */
    void jogBackEvent(PlaybackEvent evt);

    /**
     * Pause button was used.
     *
     * @param The event to handle.
     */
    void pauseEvent(PlaybackEvent evt);

    /**
     * Jog forward button was used.
     *
     * @param The event to handle.
     */
    void jogForwardEvent(PlaybackEvent evt);

    /**
     * Create new cell and set onset button was used.
     *
     * @param The event to handle.
     */
    void newCellSetOnsetEvent(PlaybackEvent evt);

    /**
     * Set new cell offset button was used.
     *
     * @param The event to handle.
     */
    void setNewCellOffsetEvent(PlaybackEvent evt);

    /**
     * New cell button was used.
     *
     * @param The event to handle.
     */
    void newCellEvent(PlaybackEvent evt);

    /**
     * Show tracks button was used.
     *
     * @param The event to handle.
     */
    void showTracksEvent(PlaybackEvent evt);

}
