package org.datavyu.plugins.ffmpegplayer;

import java.io.IOException;

/**
 * This interface associates time to a stream. It provides the start time, 
 * end time, duration, current time, setCurrentTime of a time, and a speed. The idea is
 * to have same interface as for a stream but with methods that control time
 * within that stream. The unit of time is seconds.
 * 
 * @author Florian Raudies, Mountain View, CA.
 *
 */
public interface TimeStream {

    /**
     * Start the time stream.
     */
	void start();

    /**
     * Stop the time stream.
     */
	void stop();

	/**
	 * Get the start time of the stream. Typically that will be 0.0.
	 * 
	 * @return The start time in seconds.
	 */
	double getStartTime();

	/**
	 * Get the end time of the stream.
	 * 
	 * @return The end time in seconds.
	 */
	double getEndTime();

	/**
	 * Get the duration of the stream.
	 * 
	 * @return The duration in seconds.
	 */
	double getDuration();

	/**
	 * Get the current time position within the stream.
	 * 
	 * @return The current time in seconds.
	 */
	double getCurrentTime();

	/**
	 * Sets time within the stream. The implementation restricts the time to the
	 * earliest and latest time in the stream.
	 *
	 * @param time The time point to setCurrentTime in seconds.
	 *
	 */
	void setCurrentTime(double time); // set time to continue play back

	/**
	 * Set the play back speed as multiple of the native play back and also 
	 * allows to control the direction of play back. For instance, a value of 
	 * -1x plays the stream backwards if the underlying class implements such 
	 * play back.
	 * 
	 * @param speed The speed, e.g. 0.5x or -4x.
	 */
	void setSpeed(float speed);
	
	/**
	 * Rewinds the stream to the starting position. In forward play back the 
	 * starting position is the start time. In backward play back the starting
	 * position is the end time.
	 */
	void reset();
	
	/**
	 * Closes the stream and frees all associated resources.
	 * 
	 * @throws IOException if the resources cannot be cleanly shut down.
	 */
	void close() throws IOException;
}
