package org.openshapa.plugins.quicktime;

import com.sun.jna.Platform;

import java.awt.Dimension;
import java.io.File;

import org.openshapa.util.Constants;

import org.rococoa.Rococoa;
import org.rococoa.cocoa.foundation.NSNumber;
import org.rococoa.cocoa.foundation.NSSize;
import org.rococoa.cocoa.foundation.NSValue;
import org.rococoa.cocoa.qtkit.QTKit;
import org.rococoa.cocoa.qtkit.QTMedia;
import org.rococoa.cocoa.qtkit.QTMovie;
import org.rococoa.cocoa.qtkit.QTMovieView;
import org.rococoa.cocoa.qtkit.QTTime;
import org.rococoa.cocoa.qtkit.QTTrack;


/**
 * Data viewer for QuickTime using the Cocoa QTKit API. 
 */
public abstract class BaseQTKitDataViewer extends BaseQuickTimeDataViewer {
	protected QTMovieView movieView;
    protected QTMovie movie;
    protected QTTrack visualTrack;
        
    public BaseQTKitDataViewer(java.awt.Frame parent, boolean modal) {
        super(parent, modal);

    	assert Platform.isMac();
    	if (!Platform.isMac()) {
    		throw new UnsupportedOperationException("only supported on Mac OS X");
    	}
        
        movie = null;

        // load library
       @SuppressWarnings("unused")
       QTKit instance = QTKit.instance;
    }

    protected void setQTVolume(float volume) {
        if (movie != null) {
            movie.setVolume(volume);
        }
    }
    
    public long getDuration() {
        if (movie != null) {
        	final QTTime duration = movie.duration();
            return (long) Constants.TICKS_PER_SECOND * duration.timeValue / duration.timeScale.longValue();
        }
        return -1;
    }
    
    protected abstract void setQTDataFeed(final File videoFile);
    
    protected Dimension getQTVideoSize() {
        final NSSize videoSize = Rococoa.cast(movie.attributeForKey("QTMovieNaturalSizeAttribute"), NSValue.class).sizeValue();
        return new Dimension(videoSize.width.intValue(), videoSize.height.intValue()); 
    }
        
    protected float getQTFPS() {
    	final float fps;
        final QTMedia visualMedia = visualTrack.media();
        if (visualMedia != null) {
	        final long sampleCount = Rococoa.cast(visualMedia.attributeForKey(QTMedia.QTMediaSampleCountAttribute), NSNumber.class).longValue();
	        final QTTime qtDuration = movie.duration();
	        final long duration = qtDuration.timeValue;
	        final long timescale = qtDuration.timeScale.longValue();
	        fps = (float) sampleCount / duration * timescale; 
        } else {
            fps = correctFPS();
        }
        return fps;
    }
    
    private float correctFPS() {
    	//TODO
    	return 25;
    }

    /**
     * {@inheritDoc}
     */
    public void play() {
    	super.play();
        if (movie != null) {
            movie.setRate(getPlaybackSpeed());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {
    	super.stop();
        if (movie != null) {
            movie.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void seekTo(final long position) {
        if (movie != null) {
        	System.out.println("seekTo(" + position + "), drift=" + (position - getCurrentTime()));
        	QTTime time = new QTTime(position, Constants.TICKS_PER_SECOND);
            movie.setCurrentTime(time);
        }
    }

    /**
     * {@inheritDoc}
     */
    public long getCurrentTime() {
    	final QTTime currentTime = movie.currentTime();
        final long time = currentTime.timeValue * Constants.TICKS_PER_SECOND / currentTime.timeScale.longValue();
        return time;
    }
    
    protected void cleanUp() { 
    	//TODO
    }
}
