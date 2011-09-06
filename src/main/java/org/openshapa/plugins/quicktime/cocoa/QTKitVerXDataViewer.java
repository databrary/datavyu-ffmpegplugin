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

import java.io.File;

import java.util.concurrent.Semaphore;

import org.rococoa.Foundation;
import org.rococoa.Rococoa;
import org.rococoa.cocoa.foundation.NSArray;
import org.rococoa.cocoa.foundation.NSAutoreleasePool;
import org.rococoa.cocoa.foundation.NSDictionary;
import org.rococoa.cocoa.foundation.NSNotification;
import org.rococoa.cocoa.foundation.NSNotificationCenter;
import org.rococoa.cocoa.foundation.NSNumber;
import org.rococoa.cocoa.foundation.NSObject;
import org.rococoa.cocoa.foundation.NSString;
import org.rococoa.cocoa.qtkit.MovieComponent;
import org.rococoa.cocoa.qtkit.QTMedia;
import org.rococoa.cocoa.qtkit.QTMovie;
import org.rococoa.cocoa.qtkit.QTMovieView;
import org.rococoa.cocoa.qtkit.QTTrack;


/**
 * Data viewer for QuickTime X using the Cocoa QTKit API. Requires Mac OS X 10.6 or later.
 */
public final class QTKitVerXDataViewer extends BaseQTKitDataViewer {
    public QTKitVerXDataViewer(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
    }

    @Override
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
	                NSNumber.CLASS.numberWithBool(false)
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
            final NSArray mpegTracks = movie.tracksOfMediaType(QTMedia.QTMediaTypeMPEG);
	        if (tracks.count() >= 1) {
                visualTrack = Rococoa.cast(tracks.objectAtIndex(0), QTTrack.class);
	        } else if (mpegTracks.count() >=1) {
                visualTrack = Rococoa.cast(mpegTracks.objectAtIndex(0), QTTrack.class);
            } else {
            	// no video tracks
	        }

	        movieView.setMovie(movie);
	        movie.gotoBeginning();
	        
	        this.add(new MovieComponent(movieView));
    	} finally {
    		pool.drain();
    	}
    }
    
    private static void waitForMovieToLoad(final QTMovie movie) {
    	//TODO WARNING this code is not reliable - it can sometimes hang indefinitely. 
    	NSAutoreleasePool pool = NSAutoreleasePool.new_();
    	try {
			final NSNumber state = Rococoa.cast(movie.attributeForKey(QTMovie.QTMovieLoadStateAttribute), NSNumber.class);;
			if (state.longValue() != QTMovie.QTMovieLoadStateLoading) {
				return;
			}
			
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
    
    @Override
    protected void cleanUp() { 
    	//TODO
    }
}
