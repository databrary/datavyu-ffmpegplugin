package org.openshapa.views.continuous.quicktime;

import com.sun.jna.Platform;
import com.usermetrix.jclient.Logger;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.File;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import org.openshapa.models.id.Identifier;
import org.openshapa.util.Constants;

import org.openshapa.views.component.DefaultTrackPainter;
import org.openshapa.views.component.TrackPainter;
import org.openshapa.views.continuous.CustomActions;
import org.openshapa.views.continuous.CustomActionsAdapter;
import org.openshapa.views.continuous.DataController;
import org.openshapa.views.continuous.DataViewer;

import com.usermetrix.jclient.UserMetrix;

import java.awt.event.ActionListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import java.util.Properties;
import java.util.concurrent.Semaphore;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.openshapa.views.OpenSHAPADialog;
import org.openshapa.views.continuous.ViewerStateListener;
import org.rococoa.Foundation;
import org.rococoa.Rococoa;
import org.rococoa.cocoa.foundation.NSArray;
import org.rococoa.cocoa.foundation.NSAutoreleasePool;
import org.rococoa.cocoa.foundation.NSDictionary;
import org.rococoa.cocoa.foundation.NSNotification;
import org.rococoa.cocoa.foundation.NSNotificationCenter;
import org.rococoa.cocoa.foundation.NSNumber;
import org.rococoa.cocoa.foundation.NSObject;
import org.rococoa.cocoa.foundation.NSSize;
import org.rococoa.cocoa.foundation.NSString;
import org.rococoa.cocoa.foundation.NSValue;
import org.rococoa.cocoa.qtkit.MovieComponent;
import org.rococoa.cocoa.qtkit.QTKit;
import org.rococoa.cocoa.qtkit.QTMedia;
import org.rococoa.cocoa.qtkit.QTMovie;
import org.rococoa.cocoa.qtkit.QTMovieView;
import org.rococoa.cocoa.qtkit.QTTime;
import org.rococoa.cocoa.qtkit.QTTrack;


/**
 * The viewer for a quicktime video file.
 */
public final class QTKitDataViewer extends BaseQuickTimeDataViewer {
    private QTMovieView movieView;

	/** The quicktime movie this viewer is displaying. */
    private QTMovie movie;
	
    /** The visual track for the above quicktime movie. */
    private QTTrack visualTrack;
    
    /** The logger for this class. */
    private Logger logger = UserMetrix.getLogger(getClass());
    
    public QTKitDataViewer(java.awt.Frame parent, boolean modal) {
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
    	//TODO
    }
    
    public long getDuration() {
        if (movie != null) {
        	final QTTime duration = movie.duration();
            return (long) Constants.TICKS_PER_SECOND * duration.timeValue / duration.timeScale.longValue();
        }
        return -1;
    }
    
    protected void setQTDataFeed(final File videoFile) {
    	NSAutoreleasePool pool = NSAutoreleasePool.new_();
    	try {
	        movieView = QTMovieView.CLASS.create();
	        movieView.setControllerVisible(false);
	        movieView.setPreservesAspectRatio(true);
	
	        NSArray objects = NSArray.CLASS.arrayWithObjects(
	                NSString.stringWithString(videoFile.getAbsolutePath()),
	                NSNumber.CLASS.numberWithBool(true),
	                NSNumber.CLASS.numberWithBool(true),
	                NSNumber.CLASS.numberWithBool(true)
	                );
	        NSArray keys = NSArray.CLASS.arrayWithObjects(
	                NSString.stringWithString(QTMovie.QTMovieFileNameAttribute),
	                NSString.stringWithString(QTMovie.QTMovieOpenForPlaybackAttribute),
	                NSString.stringWithString(QTMovie.QTMovieOpenAsyncRequiredAttribute),
	                NSString.stringWithString(QTMovie.QTMovieRateChangesPreservePitchAttribute)
	                );
	        NSDictionary dictionary = NSDictionary.dictionaryWithObjects_forKeys(objects, keys);
	  
	        movie = QTMovie.movieWithAttributes_error(dictionary, null);
	        waitForMovieToLoad(movie);

	        final NSArray tracks = movie.tracksOfMediaType(QTMedia.QTMediaTypeVideo);
	        if (tracks.count() >= 1) {
	        	visualTrack = Rococoa.cast(tracks.objectAtIndex(0), QTTrack.class);
	        } else {
	        	throw new RuntimeException("media file does not contain any video tracks");
	        }
	        
			final NSNumber state = Rococoa.cast(movie.attributeForKey(QTMovie.QTMovieLoadStateAttribute), NSNumber.class);;
			System.out.println("current movie loading state=" + state.longValue());
	        
	        movieView.setMovie(movie);
	        movie.gotoBeginning();
	        
	        this.add(new MovieComponent(movieView));
    	} finally {
    		pool.drain();
    	}
    }
    
    protected Dimension getQTVideoSize() {
        final NSSize videoSize = Rococoa.cast(movie.attributeForKey("QTMovieNaturalSizeAttribute"), NSValue.class).sizeValue();
        return new Dimension(videoSize.width.intValue(), videoSize.height.intValue()); 
    }
    
    private static void waitForMovieToLoad(final QTMovie movie) {
    	NSAutoreleasePool pool = NSAutoreleasePool.new_();
    	try {
    		final Semaphore available = new Semaphore(1);
    		available.acquire();
            NSNotificationCenter notificationCentre = NSNotificationCenter.CLASS.defaultCenter();
    		Object listener = new Object() {
    			public void loadStateChanged(NSNotification notification) {
    				final NSNumber state = Rococoa.cast(movie.attributeForKey(QTMovie.QTMovieLoadStateAttribute), NSNumber.class);;
    				if (state.longValue() == QTMovie.QTMovieLoadStateLoading) {
    					// still loading
    					return;
    				}
    				available.release();    				
    			}
    		};
            NSObject proxy = Rococoa.proxy(listener, NSObject.class);
   	       notificationCentre.addObserver_selector_name_object(
   	               proxy, 
   	               Foundation.selector("loadStateChanged:"),
   	               "QTMovieLoadStateDidChangeNotification",
   	               movie);
   	       available.acquire();
		   notificationCentre.removeObserver(proxy);
    	} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
    		pool.release();
    	}
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
