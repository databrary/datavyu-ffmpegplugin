package org.openshapa.views.continuous.quicktime;

import com.usermetrix.jclient.Logger;

import java.awt.Dimension;

import java.io.File;

import org.openshapa.util.Constants;

import quicktime.QTException;
import quicktime.QTSession;

import quicktime.app.view.QTFactory;

import quicktime.io.OpenMovieFile;
import quicktime.io.QTFile;

import quicktime.qd.QDRect;

import quicktime.std.StdQTConstants;
import quicktime.std.StdQTException;

import quicktime.std.clocks.TimeRecord;

import quicktime.std.movies.Movie;
import quicktime.std.movies.TimeInfo;
import quicktime.std.movies.Track;
import quicktime.std.movies.media.Media;

import com.usermetrix.jclient.UserMetrix;

import java.util.concurrent.TimeUnit;

/**
 * The viewer for a quicktime video file.
 */
public final class QTJavaDataViewer extends BaseQuickTimeDataViewer {
    /** The quicktime movie this viewer is displaying. */
    private Movie movie;

    /** The visual track for the above quicktime movie. */
    private Track visualTrack;

    /** The visual media for the above visual track. */
    private Media visualMedia;

    /** The logger for this class. */
    private Logger logger = UserMetrix.getLogger(getClass());
	
    public QTJavaDataViewer(final java.awt.Frame parent, final boolean modal) {
    	super(parent, modal);

    	movie = null;
    	
        try {
            // Initalise QTJava.
            QTSession.open();
        } catch (Throwable e) {
            logger.error("Unable to create " + this.getClass().getName(), e);
        }
    }
    
    protected void setQTVolume(float volume) {
    	if (movie == null) {
    		return;
    	}
    	
        try {
        	movie.setVolume(volume);
        } catch (StdQTException ex) {
            logger.error("Unable to set volume", ex);
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
            logger.error("Unable to determine QT movie duration", ex);
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


            visualMedia = visualTrack.getMedia();
            this.add(QTFactory.makeQTComponent(movie).asComponent());
        } catch (QTException e) {
            logger.error("Unable to setVideoFile", e);
        }
    }
    
    protected Dimension getQTVideoSize() {
    	try {
	    	QDRect bounds = (movie.getBox().getHeight() > movie.getBounds().getHeight()) ? movie.getBox() : movie.getBounds();
	    	return new Dimension(bounds.getWidth(), bounds.getHeight());
    	} catch (QTException e) {
            logger.error("Unable to getQTNativeVideoSize", e);
    	}
    	return new Dimension(1, 1);
    }
    
    protected float getQTFPS() {
    	float fps = 0;
    	try {
	        // BugzID:928 - FPS calculations will fail when using H264.
	        // Apparently the Quicktime for Java API does not support a whole
	        // bunch of methods with H264.
	        fps = (float) visualMedia.getSampleCount()
	            / visualMedia.getDuration() * visualMedia.getTimeScale();
	
	        if ((visualMedia.getSampleCount() == 1.0)
	                || (visualMedia.getSampleCount() == 1)) {
	            fps = correctFPS();
	        }
    	} catch (QTException e) {
    		logger.error("Unable to calculate FPS", e);
    	}
        
        return fps;
    }
    
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
        /** How many frames to check when correcting the FPS. */
        final int CORRECTIONFRAMES = 5;
        float minFrameLength = TimeUnit.MILLISECONDS.convert(1, TimeUnit.SECONDS); // Set this to one second, as the "worst"
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
                logger.error("Error getting time", e);
            }
        }

        return TimeUnit.MILLISECONDS.convert(1, TimeUnit.SECONDS) / minFrameLength;
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
            logger.error("Unable to play", e);
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
            logger.error("Unable to stop", e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void seekTo(final long position) {
        try {
            if (movie != null) {
                TimeRecord time = new TimeRecord(Constants.TICKS_PER_SECOND,
                        position);
                movie.setTime(time);
            }
        } catch (QTException e) {
            logger.error("Unable to find", e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public long getCurrentTime() {
    	try {
    		return movie.getTime();
    	} catch (QTException e) {
    		logger.error("Unable to get time", e);
    	}
    	return 0;
    }
    
    protected void cleanUp() {
    	//TODO
    }


}
