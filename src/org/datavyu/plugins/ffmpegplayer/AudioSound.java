package org.datavyu.plugins.ffmpegplayer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.AudioFormat.Encoding;

/**
 * Interfaces the audio stream with the access control of the sound system as 
 * provided through the javax.sound framework. 
 * 
 * @author Florian Raudies, Mountain View, CA.
 *
 */
public class AudioSound {
	
	/** The supported mono format; blank values are from the input audio */
	private final static AudioFormat MONO_FORMAT = new AudioFormat(
			Encoding.PCM_SIGNED, 0, 0, 1, 0, 0, false);
	
	/** The supported stereo format; blank values are from the input audio */
	private final static AudioFormat STEREO_FORMAT = new AudioFormat(
			Encoding.PCM_UNSIGNED, 0, 0, 2, 0, 0, false);
	
	/** Sample data buffer to copy from byte buffer to DataLine */
	private byte[] sampleData = null;
	
	/** Sound line to play the audio data */
	private SourceDataLine soundLine = null;
	
	/** Gain control for the underlying data line */
	private FloatControl gainControl = null;
	
	/** The audio buffer size */
	private int bufferSize = 0;

	/** The audio stream from where we pull data */
	private AudioStream audioStream = null;
	
	/**
	 * Create the audio sound with an audio stream. This assumes that the audio
	 * stream is OPEN to get the proper buffer size in bytes.
	 * 
	 * @param audioStream The audio stream that provides the audio frames that 
	 *		  are played back.
	 * 
	 * @throws LineUnavailableException If the audio format is not supported by 
	 * 		   the javax.sound framework this exception is thrown. 
	 */
	public AudioSound(AudioStream audioStream) throws LineUnavailableException {
		this.audioStream = audioStream;
		// Create a buffer for the audio frames
		bufferSize = audioStream.getAudioBufferSize();
		sampleData = new byte[bufferSize];
		// When using stereo need to multiply the frameSize by number of channels
		AudioFormat audioFormat = audioStream.getAudioFormat();
		// Get the data line and sound line
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
		soundLine = (SourceDataLine) AudioSystem.getLine(info);
		soundLine.open(audioFormat);
		soundLine.start();
		// Get the gain (volume) control for the sound line
		gainControl = (FloatControl) soundLine.getControl(FloatControl.Type.MASTER_GAIN);
	}

	/**
	 * Get new audio format for mono playback.
	 *
	 * @return AudioFormat for mono playback.
	 */
	public static AudioFormat getNewMonoFormat() {
		return new AudioFormat(
				MONO_FORMAT.getEncoding(),
				MONO_FORMAT.getSampleRate(),
				MONO_FORMAT.getSampleSizeInBits(),
				MONO_FORMAT.getChannels(),
				MONO_FORMAT.getFrameSize(),
				MONO_FORMAT.getFrameRate(),
				MONO_FORMAT.isBigEndian());
	}

	/**
	 * Get new audio format for stereo playback.
	 *
	 * @return AudioFormat for stereo playback.
	 */
	public static AudioFormat getNewStereoFormat() {
		return new AudioFormat(
				STEREO_FORMAT.getEncoding(),
				STEREO_FORMAT.getSampleRate(),
				STEREO_FORMAT.getSampleSizeInBits(),
				STEREO_FORMAT.getChannels(),
				STEREO_FORMAT.getFrameSize(),
				STEREO_FORMAT.getFrameRate(),
				STEREO_FORMAT.isBigEndian());
	}

	/**
	 * Plays the next audio frame by pulling the byte data from the audio stream
	 * and writing it to a sound line.
	 */
	public void playNextData() {
		// Read the audio frame into the sample data buffer
		if (audioStream.readAudioData(sampleData) > 0) {
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
	
	/**
	 * Drains, stops, and closes the sound line.
	 */
	public void close() {
		soundLine.drain();
		soundLine.stop();
		soundLine.close();		
	}
}
