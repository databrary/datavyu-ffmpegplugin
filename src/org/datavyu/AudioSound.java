package org.datavyu;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.AudioFormat.Encoding;

public class AudioSound {
	
	/** The supported mono format; blank values are from the input audio */
	public final static AudioFormat MONO_FORMAT = new AudioFormat(
			Encoding.PCM_SIGNED, 0, 0, 1, 0, 0, false);
	
	/** The supported stereo format; blank values are from the input audio */
	public final static AudioFormat STEREO_FORMAT = new AudioFormat(
			Encoding.PCM_UNSIGNED, 0, 0, 2, 0, 0, false);
	
	/** Sample data buffer to copy from byte buffer to DataLine */
	private byte[] sampleData = null;
	
	/** Sound line to play the audio data */
	private SourceDataLine soundLine = null;
	
	/** Gain control for the underlying data line */
	FloatControl gainControl = null;
	
	/** Audio format for the output */
	AudioFormat outAudioFormat = null;
	
	private int bufferSize = 0;
	
	AudioStream audioStream = null;
	
	/**
	 * 
	 * @param audioStream assumes that the audio stream is open
	 * @throws LineUnavailableException 
	 */
	public AudioSound(AudioStream audioStream) throws LineUnavailableException {		
		this.audioStream = audioStream;
		bufferSize = audioStream.getAudioBufferSize();
		sampleData = new byte[bufferSize];
				
		// When using stereo need to multiply the frameSize by number of channels
		outAudioFormat = audioStream.getOutputAudioFormat();
		
		// Get the data line
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, outAudioFormat);
		soundLine = (SourceDataLine) AudioSystem.getLine(info);			
		soundLine.open(outAudioFormat);
		soundLine.start();

		// Get the gain (volume) control for the sound line
		gainControl = (FloatControl) soundLine.getControl(FloatControl.Type.MASTER_GAIN);
	}
	
	public void playNextFrame() {
		if (audioStream.readAudioFrame(sampleData) > 0) {
			soundLine.write(sampleData, 0, bufferSize);			
		}
	}
	
	/**
	 * Set the volume on the data line.
	 * 
	 * @param volume The volume in Decibel.
	 */
	public void setVolume(float volume) {
		gainControl.setValue(volume);
	}
	
	/**
	 * Get the maximum volume for the data line.
	 * 
	 * @return The maximum volume in Decibel.
	 */
	public float getMaxVolume() {
		return gainControl.getMaximum();
	}
	
	/**
	 * Get the minimum volume for the data line.
	 * 
	 * @return The minimum valume in Decibel.
	 */
	public float getMinVolume() {
		return gainControl.getMinimum();
	}
	
	public void close() {
		soundLine.drain();
		soundLine.stop();
		soundLine.close();		
	}
}
