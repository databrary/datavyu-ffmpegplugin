package org.openshapa.logging;

import org.openshapa.event.PlaybackEvent;
import org.openshapa.event.PlaybackListener;

import com.usermetrix.jclient.UserMetrix;


/**
 * Implements playback usage logging.
 */
public class PlaybackLogging implements PlaybackListener {

    private final UserMetrix logger = UserMetrix.getInstance(
            PlaybackLogging.class);

    public void addDataEvent(final PlaybackEvent evt) {
        logger.usage("Add data");
    }

    public void findEvent(final PlaybackEvent evt) {
        logger.usage("Find");
    }

    public void forwardEvent(final PlaybackEvent evt) {
        logger.usage("Fast forward");
    }

    public void goBackEvent(final PlaybackEvent evt) {
        logger.usage("Go back");
    }

    public void jogBackEvent(final PlaybackEvent evt) {
        logger.usage("Jog back");
    }

    public void jogForwardEvent(final PlaybackEvent evt) {
        logger.usage("Jog forward");
    }

    public void newCellEvent(final PlaybackEvent evt) {
        logger.usage("New cell");
    }

    public void newCellSetOnsetEvent(final PlaybackEvent evt) {
        logger.usage("New cell set onset");
    }

    public void pauseEvent(final PlaybackEvent evt) {
        logger.usage("Pause");
    }

    public void playEvent(final PlaybackEvent evt) {
        logger.usage("Play");
    }

    public void rewindEvent(final PlaybackEvent evt) {
        logger.usage("Rewind");
    }

    public void setCellOffsetEvent(final PlaybackEvent evt) {
        logger.usage("Set cell offset");
    }

    public void setCellOnsetEvent(final PlaybackEvent evt) {
        logger.usage("Set cell onset");
    }

    public void setNewCellOffsetEvent(final PlaybackEvent evt) {
        logger.usage("Set new cell offset");
    }

    public void showTracksEvent(final PlaybackEvent evt) {
        logger.usage("Show tracks");
    }

    public void shuttleBackEvent(final PlaybackEvent evt) {
        logger.usage("Shuttle back");
    }

    public void shuttleForwardEvent(final PlaybackEvent evt) {
        logger.usage("Shuttle forward");
    }

    public void stopEvent(final PlaybackEvent evt) {
        logger.usage("Stop event");
    }

}
