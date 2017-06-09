package org.datavyu;

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
	public void streamOpened();
	
	/**
	 * This method is called with the data from the stream. Notice the byte 
	 * array is READ-ONLY.
	 * 
	 * @param data Filled data to be copied & consumed by the listener. 
	 */
	public void streamData(byte[] data);
	
	/**
	 * This method is called when the stream is closed.
	 */
	public void streamClosed();
}
