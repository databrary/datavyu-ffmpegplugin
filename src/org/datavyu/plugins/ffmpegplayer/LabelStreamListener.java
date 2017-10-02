package org.datavyu.plugins.ffmpegplayer;

import javax.swing.JLabel;

/**
 * Update a label with the current stream time in seconds using 3 digits after
 * the comma. 
 * 
 * @author Florian Raudies, Mountain View, CA.
 *
 */
public class LabelStreamListener implements StreamListener {
	
	/** The label that is used to display the time. */
	private JLabel label;
	
	/** The movie stream that is asked for the start time and current time */
	private MovieStream movieStream;
	
	/** Indicates that we have stopped (or not started) this listener. */
	private boolean stopped;
	
	LabelStreamListener(JLabel label, MovieStream movieStream) {
		this.label = label;
		this.movieStream = movieStream;
		this.stopped = true;
	}
	
	/**
	 * Updates the label with the current time using 3 digits after the comma.
	 */
	private void updateLabel() {
		if (!stopped) {
			double timeInSeconds = movieStream.getCurrentTime();
			label.setText(Math.round(timeInSeconds*1000.0)/1000.0 + " seconds");			
		}
	}

	@Override
	public void streamOpened() {
		stopped = false;
		updateLabel();
	}

	@Override
	public void streamData(byte[] data) {
		updateLabel();
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
		updateLabel();
	}

}
