package org.datavyu;

import javax.sound.sampled.AudioFormat;

/**
 * Extends the time stream through audio play back functionality. Data is 
 * provided in chunks. The size of these does not directly correspond to the 
 * size of the underlying audio frames. 
 * 
 * @author Florian Raudies, Mountain View, CA.
 *
 */
public interface AudioStream extends TimeStream {
	
	// ensure the buffer has the right capacity
	/**
	 * Read the the next audio frame
	 * 
	 * @param buffer
	 * @return
	 */
	public int readAudioData(byte[] buffer);
	
	public AudioFormat getOutputAudioFormat();
	
	public int getAudioBufferSize();
	
	public boolean availableAudioData();
}
