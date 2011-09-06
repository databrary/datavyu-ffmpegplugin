/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openshapa.plugins.quicktime.cocoa;

import com.sun.jna.Platform;

import java.awt.Dimension;
import java.io.File;

import org.openshapa.plugins.quicktime.BaseQuickTimeDataViewer;
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
    	if (visualTrack != null) {
	        final NSSize videoSize = Rococoa.cast(movie.attributeForKey("QTMovieNaturalSizeAttribute"), NSValue.class).sizeValue();
	        return new Dimension(videoSize.width.intValue(), videoSize.height.intValue()); 
    	} else {
    		return new Dimension(50, 50);
    	}
    }
        
    protected float getQTFPS() {
    	if (visualTrack != null) {
	        final QTMedia visualMedia = visualTrack.media();
	        if (visualMedia != null) {
		        final long sampleCount = Rococoa.cast(visualMedia.attributeForKey(QTMedia.QTMediaSampleCountAttribute), NSNumber.class).longValue();
		        final QTTime qtDuration = movie.duration();
		        final long duration = qtDuration.timeValue;
		        final long timescale = qtDuration.timeScale.longValue();
		        final float fps = (float) sampleCount / duration * timescale; 
		        return fps;
	        }
    	}
        return correctFPS();
    }
    
    private float correctFPS() {
    	//TODO - implement this more intelligently like in QTDataViewer
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
    
    /** Maximum time that the movie can be out of sync before we try to correct it (milliseconds). */
    private final long MAX_SEEK_DRIFT_TIME = 250;

    /**
     * {@inheritDoc}
     */
    public void seekTo(final long position) {
        if (movie != null) {
        	final long drift = position - getCurrentTime();
        	if (!isPlaying() || drift >= MAX_SEEK_DRIFT_TIME) {
	        	QTTime time = new QTTime(Math.min(Math.max(position, 0), getDuration() - 1), Constants.TICKS_PER_SECOND);
	            movie.setCurrentTime(time);
        	}
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
