package org.datavyu;

import javax.sound.sampled.AudioFormat;

public interface AudioStream extends TimeStream {
	
	// ensure the buffer has the right capacity
	public int readAudioFrame(byte[] buffer);
	
	public AudioFormat getOutputAudioFormat();
	
	public int getAudioBufferSize();
	
	public boolean availableAudioFrame();
}
