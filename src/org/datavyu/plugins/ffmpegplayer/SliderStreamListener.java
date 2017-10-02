package org.datavyu.plugins.ffmpegplayer;

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JSlider;

/**
 * Updates the slider according to the current time wrt start time in the movie 
 * stream. In this case the streamData method is not consuming the data but 
 * rather asking the stream for the current time to update the slider.
 * 
 * @author Florian Raudies, Mountain View, CA.
 *
 */
public class SliderStreamListener implements StreamListener {
	
	/** The slider that is updated */
	private JSlider slider;
	
	/** The movie stream that is asked for the start time and current time */
	private MovieStream movieStream;
	
	/** The start time of the movie stream */
	private double startTime;
	
	/** The stream has been stopped. We keep the state here to control the display */
	private boolean stopped;

	/**
	 * Create a slider stream listener to update a JSlider.
	 * 
	 * @param slider The JSlider display object.
	 * @param movieStream The movie stream that is used to update the slider.
	 */
	public SliderStreamListener(JSlider slider, MovieStream movieStream) {
		this.slider = slider;
		this.movieStream = movieStream;
		this.stopped = true;
		this.startTime = 0;
	}
	
	/**
	 * Converts the stream time into an int to update the slider.
	 * 
	 * @return The current time as int.
	 */
	private int getStreamTime() {
		return (int)(1000*(-startTime+movieStream.getCurrentTime()));
	}
	
	private void updateSliderTime() {
		if (!stopped) {
			slider.setValue(getStreamTime());
		}
	}

	@Override
	public void streamOpened() {
        // Assign a range of 0 to 1 to the slider.
        slider.setModel(new DefaultBoundedRangeModel(0, 1, 0, 
        		(int)(1000*movieStream.getDuration())));
        // Set the current time to the slider
		stopped = false;
		updateSliderTime();
	}

	@Override
	public void streamData(byte[] data) {
		updateSliderTime();
	}

	@Override
	public void streamClosed() { /* nothing to do here */ }

	@Override
	public void streamStopped() {
		stopped = true;
	}

	@Override
	public void streamStarted() {
		stopped = false;
		updateSliderTime();
	}
}
