package org.datavyu.plugins.ffmpegplayer;

/**
 * The stream listener interface provides 
 * 
 * @author Florian Raudies, Mountain View, CA.
 *
 */
public interface StreamListener {
	/**
	 * This method is called when the stream is opened.
	 */
	void streamOpened();
	
	/**
	 * This method is called with the data from the stream. Notice the byte 
	 * array is READ-ONLY.
	 * 
	 * @param data Filled data to be copied & consumed by the listener. 
	 */
	void streamData(byte[] data);
	
	/**
	 * This method is called when the stream is closed.
	 */
	@SuppressWarnings("unused") // API method
	void streamClosed();
	
	/**
	 * This method is called if the stream is stopped.
	 */
	void streamStopped();
	
	/**
	 * This method is called when the stream is started.
	 */
	void streamStarted();
}
