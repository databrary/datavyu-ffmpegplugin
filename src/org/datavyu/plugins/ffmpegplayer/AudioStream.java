package org.datavyu.plugins.ffmpegplayer;

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
	
	/**
	 * Read the the next audio data (not necessarily audio frame).
	 * 
	 * This method assumes that the buffer has been allocated for 
	 * getAudioBufferSize many bytes. 
	 * 
	 * This method blocks if called and there is no available data.
	 * 
	 * @param buffer The buffer where to put the data.
	 * @return 1 if data was read otherwise 0.
	 */
	int readAudioData(byte[] buffer);

	/**
	 * Get the audio format for the read data.
	 * 
	 * @return The audio format.
	 */
	AudioFormat getAudioFormat();

	/**
	 * Get the size of the audio buffer in bytes.
	 * 
	 * @return The size of the buffer in bytes.
	 */
	int getAudioBufferSize();
}
