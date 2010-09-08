package org.openshapa.views.continuous.quicktime;

import java.awt.Frame;
import java.io.File;

import org.rococoa.Rococoa;
import org.rococoa.cocoa.foundation.NSArray;
import org.rococoa.cocoa.foundation.NSAutoreleasePool;
import org.rococoa.cocoa.foundation.NSDictionary;
import org.rococoa.cocoa.foundation.NSNumber;
import org.rococoa.cocoa.foundation.NSString;
import org.rococoa.cocoa.qtkit.MovieComponent;
import org.rococoa.cocoa.qtkit.QTMedia;
import org.rococoa.cocoa.qtkit.QTMovie;
import org.rococoa.cocoa.qtkit.QTMovieView;
import org.rococoa.cocoa.qtkit.QTTrack;

public class QTKitVer7DataViewer extends BaseQTKitDataViewer {
	public QTKitVer7DataViewer(Frame parent, boolean modal) {
		super(parent, modal);
	}

    protected void setQTDataFeed(final File videoFile) {
    	NSAutoreleasePool pool = NSAutoreleasePool.new_();
    	try {
	        movieView = QTMovieView.CLASS.create();
	        movieView.setControllerVisible(false);
	        movieView.setPreservesAspectRatio(true);
	
	        NSArray objects = NSArray.CLASS.arrayWithObjects(
	                NSString.stringWithString(videoFile.getAbsolutePath()),
	                NSNumber.CLASS.numberWithBool(false),
	                NSNumber.CLASS.numberWithBool(false),
	                NSNumber.CLASS.numberWithBool(false),
	                NSNumber.CLASS.numberWithBool(true)
	                );
	        NSArray keys = NSArray.CLASS.arrayWithObjects(
	                NSString.stringWithString(QTMovie.QTMovieFileNameAttribute),
	                NSString.stringWithString(QTMovie.QTMovieOpenForPlaybackAttribute),
	                NSString.stringWithString(QTMovie.QTMovieOpenAsyncRequiredAttribute),
	                NSString.stringWithString(QTMovie.QTMovieOpenAsyncOKAttribute),
	                NSString.stringWithString(QTMovie.QTMovieRateChangesPreservePitchAttribute)
	                );
	        NSDictionary dictionary = NSDictionary.dictionaryWithObjects_forKeys(objects, keys);
	  
	        movie = QTMovie.movieWithAttributes_error(dictionary, null);

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
    
}
