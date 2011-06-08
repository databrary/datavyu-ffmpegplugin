package org.openshapa.logging;

import com.usermetrix.jclient.Logger;
import org.openshapa.event.PlaybackEvent;
import org.openshapa.event.PlaybackListener;

import com.usermetrix.jclient.UserMetrix;


/**
 * Implements playback usage logging.
 */
public class PlaybackLogging implements PlaybackListener {

    private final Logger logger = UserMetrix.getLogger(
            PlaybackLogging.class);

    public void addDataEvent(final PlaybackEvent evt) {
        logger.event("Add data");
    }

    public void findEvent(final PlaybackEvent evt) {
        logger.event("Find");
    }

    public void forwardEvent(final PlaybackEvent evt) {
        logger.event("Fast forward");
    }

    public void goBackEvent(final PlaybackEvent evt) {
        logger.event("Go back");
    }

    public void jogBackEvent(final PlaybackEvent evt) {
        logger.event("Jog back");
    }

    public void jogForwardEvent(final PlaybackEvent evt) {
        logger.event("Jog forward");
    }

    public void newCellEvent(final PlaybackEvent evt) {
        logger.event("New cell");
    }

    public void newCellSetOnsetEvent(final PlaybackEvent evt) {
        logger.event("New cell set onset");
    }

    public void pauseEvent(final PlaybackEvent evt) {
        logger.event("Pause");
    }

    public void playEvent(final PlaybackEvent evt) {
        logger.event("Play");
    }

    public void rewindEvent(final PlaybackEvent evt) {
        logger.event("Rewind");
    }

    public void setCellOffsetEvent(final PlaybackEvent evt) {
        logger.event("Set cell offset");
    }

    public void setCellOnsetEvent(final PlaybackEvent evt) {
        logger.event("Set cell onset");
    }

    public void setNewCellOffsetEvent(final PlaybackEvent evt) {
        logger.event("Set new cell offset");
    }

    public void showTracksEvent(final PlaybackEvent evt) {
        logger.event("Show tracks");
    }

    public void shuttleBackEvent(final PlaybackEvent evt) {
        logger.event("Shuttle back");
    }

    public void shuttleForwardEvent(final PlaybackEvent evt) {
        logger.event("Shuttle forward");
    }

    public void stopEvent(final PlaybackEvent evt) {
        logger.event("Stop event");
    }

}
