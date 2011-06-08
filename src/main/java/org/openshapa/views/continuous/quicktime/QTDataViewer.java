package org.openshapa.views.continuous.quicktime;

import com.usermetrix.jclient.Logger;

import java.awt.Dimension;

import java.io.File;

import org.openshapa.plugins.quicktime.BaseQuickTimeDataViewer;

import org.openshapa.util.Constants;

import quicktime.QTException;
import quicktime.QTSession;

import quicktime.app.view.QTFactory;

import quicktime.io.OpenMovieFile;
import quicktime.io.QTFile;

import quicktime.qd.QDDimension;

import quicktime.std.StdQTConstants;
import quicktime.std.StdQTException;

import quicktime.std.clocks.TimeRecord;

import quicktime.std.movies.Movie;
import quicktime.std.movies.TimeInfo;
import quicktime.std.movies.Track;
import quicktime.std.movies.media.Media;

import com.usermetrix.jclient.UserMetrix;


/**
 * The viewer for a quicktime video file.
 * <b>Do not move this class, this is for backward compatibility with 1.07.</b>
 */
public final class QTDataViewer extends BaseQuickTimeDataViewer {

    /** The quicktime movie this viewer is displaying. */
    private Movie movie;

    /** The visual track for the above quicktime movie. */
    private Track visualTrack;

    /** The visual media for the above visual track. */
    private Media visualMedia;

    /** The logger for this class. */
    private static Logger LOGGER = UserMetrix.getLogger(QTDataViewer.class);

    public QTDataViewer(final java.awt.Frame parent, final boolean modal) {
        super(parent, modal);

        movie = null;

        try {

            // Initalise QTJava.
            QTSession.open();
        } catch (Throwable e) {
            LOGGER.error("Unable to create " + this.getClass().getName(), e);
        }
    }

    protected void setQTVolume(final float volume) {

        if (movie == null) {
            return;
        }

        try {
            movie.setVolume(volume);
        } catch (StdQTException ex) {
            LOGGER.error("Unable to set volume", ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    public long getDuration() {

        try {

            if (movie != null) {
                return (long) Constants.TICKS_PER_SECOND
                    * (long) movie.getDuration() / movie.getTimeScale();
            }
        } catch (StdQTException ex) {
            LOGGER.error("Unable to determine QT movie duration", ex);
        }

        return -1;
    }

    protected void setQTDataFeed(final File videoFile) {

        try {
            OpenMovieFile omf = OpenMovieFile.asRead(new QTFile(videoFile));
            movie = Movie.fromFile(omf);
            movie.setVolume(0.7F);

            // Set the time scale for our movie to milliseconds (i.e. 1000 ticks
            // per second.
            movie.setTimeScale(Constants.TICKS_PER_SECOND);

            visualTrack = movie.getIndTrackType(1,
                    StdQTConstants.visualMediaCharacteristic,
                    StdQTConstants.movieTrackCharacteristic);
            visualMedia = visualTrack != null ? visualTrack.getMedia() : null;
            
            // WARNING there seems to be a bug in QTJava where the video will be
            // rendered as blank if the QT component is added before the window
            // is displayable/visible
            add(QTFactory.makeQTComponent(movie).asComponent());
        } catch (QTException e) {
            LOGGER.error("Unable to setVideoFile", e);
        }
    }

    protected Dimension getQTVideoSize() {
        try {
        	if (visualTrack != null) {
	            QDDimension vtDim = visualTrack.getSize();
	            return new Dimension(vtDim.getWidth(), vtDim.getHeight());
        	}
        } catch (QTException e) {
            LOGGER.error("Unable to getQTNativeVideoSize", e);
        }

        return new Dimension(1, 1);
    }

    protected float getQTFPS() {
        float fps = 0;

        try {
        	if (visualMedia != null) {
	            // BugzID:928 - FPS calculations will fail when using H264.
	            // Apparently the Quicktime for Java API does not support a whole
	            // bunch of methods with H264.
	            fps = (float) visualMedia.getSampleCount()
	                / visualMedia.getDuration() * visualMedia.getTimeScale();
	
	            if ((visualMedia.getSampleCount() == 1.0)
	                    || (visualMedia.getSampleCount() == 1)) {
	                fps = correctFPS();
	            }
        	}
        } catch (QTException e) {
            LOGGER.error("Unable to calculate FPS", e);
        }

        return fps;
    }

    /** How many milliseconds in a second? */
    private static final int MILLI = 1000;

    /** How many frames to check when correcting the FPS. */
    private static final int CORRECTIONFRAMES = 5;

    /**
     * If there was a problem getting the fps, we use this method to fix it. The
     * first few frames (number of which is specified by CORRECTIONFRAMES) are
     * inspected, with the delay between each measured; the two frames with the
     * smallest delay between them are assumed to represent the fps of the
     * entire movie.
     *
     * @return The best fps found in the first few frames.
     */
    private float correctFPS() {        
        float minFrameLength = MILLI; // Set this to one second, as the "worst"
        float curFrameLen = 0;
        int curTime = 0;

        for (int i = 0; i < CORRECTIONFRAMES; i++) {

            try {
                TimeInfo timeObj = visualTrack.getNextInterestingTime(
                        StdQTConstants.nextTimeStep, curTime, 1);
                float candidateFrameLen = timeObj.time - curFrameLen;
                curFrameLen = timeObj.time;
                curTime += curFrameLen;

                if (candidateFrameLen < minFrameLength) {
                    minFrameLength = candidateFrameLen;
                }
            } catch (QTException e) {
                LOGGER.error("Error getting time", e);
            }
        }

        return MILLI / minFrameLength;
    }

    /**
     * {@inheritDoc}
     */
    public void play() {
        super.play();

        try {

            if (movie != null) {
                movie.setRate(getPlaybackSpeed());
            }
        } catch (QTException e) {
            LOGGER.error("Unable to play", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {
        super.stop();

        try {

            if (movie != null) {
                movie.stop();
            }
        } catch (QTException e) {
            LOGGER.error("Unable to stop", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void seekTo(final long position) {

        try {

            if (movie != null) {
                TimeRecord time = new TimeRecord(Constants.TICKS_PER_SECOND,
                    Math.min(Math.max(position, 0), getDuration() - 1));
                movie.setTime(time);
            }
        } catch (QTException e) {
            LOGGER.error("Unable to find", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public long getCurrentTime() {

        try {
            return movie.getTime();
        } catch (QTException e) {
            LOGGER.error("Unable to get time", e);
        }

        return 0;
    }

    protected void cleanUp() {
        //TODO
    }


}
