package org.datavyu.plugins.ffmpegplayer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * This class implements a stream listener for the audio stream. It receives the 
 * byte data from the stream to forward it to the sound system through the
 * javax.sound framework. 
 * 
 * @author Florian Raudies, Mountain View, CA.
 */
public class AudioSoundStreamListener implements StreamListener {
	
	/** The underlying movie stream, used to get the audio format */
	private MovieStream movieStream = null;
	
	/** The sound line to write the data */
	private SourceDataLine soundLine = null;

	/**
	 * Create an audio stream listener that plays the sound.
	 * 
	 * @param movieStream The underlying movie stream.
	 */
	public AudioSoundStreamListener(MovieStream movieStream) {
		this.movieStream = movieStream;
	}

	@Override
	public void streamOpened() {
		AudioFormat audioFormat = movieStream.getAudioFormat();
		try {
			// Get the data line
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, 
					audioFormat);
			soundLine = (SourceDataLine) AudioSystem.getLine(info);			
			soundLine.open(audioFormat);
		} catch (LineUnavailableException lu) {
			System.err.println("Could not open line for audio format: " 
					+ audioFormat);
		}		
	}

	@Override
	public void streamData(byte[] data) {
		soundLine.write(data, 0, data.length);		
	}

	@Override
	public void streamClosed() {
		soundLine.drain();
		soundLine.stop();
		soundLine.close();
	}
	
	@Override
	public void streamStopped() {
		soundLine.stop();
	}
	
	@Override
	public void streamStarted() {
		soundLine.start();		
	}
}
