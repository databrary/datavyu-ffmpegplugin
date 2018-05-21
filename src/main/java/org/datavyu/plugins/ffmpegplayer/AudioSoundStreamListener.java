package org.datavyu.plugins.ffmpegplayer;

import javax.sound.sampled.*;

/**
 * This class implements a stream listener for the audio stream. It receives byte data from the stream to forward it to
 * the sound system through the javax.sound framework.
 * 
 * @author Florian Raudies, Mountain View, CA.
 */
public class AudioSoundStreamListener implements StreamListener {

	/** The supported mono format; blank values are from the input audio */
	private final static AudioFormat MONO_FORMAT = new AudioFormat(
			AudioFormat.Encoding.PCM_SIGNED, 0, 0, 1, 0, 0, false);

	/** The supported stereo format; blank values are from the input audio */
	private final static AudioFormat STEREO_FORMAT = new AudioFormat(
			AudioFormat.Encoding.PCM_UNSIGNED, 0, 0, 2, 0, 0, false);

	/** The underlying movie stream, used to get the audio format */
	private AudioFormat audioFormat;
	
	/** The sound line to write the data */
	private SourceDataLine soundLine = null;

	/** Gain control for volume on the sound line */
	private FloatControl gainControl = null;

	/**
	 * Creates an audio stream listener that plays the sound.
	 * 
	 * @param audioFormat The audio format for this stream.
	 */
	public AudioSoundStreamListener(AudioFormat audioFormat) {
		this.audioFormat = audioFormat;
	}

	@Override
	public void streamOpened() {
		try {
			// Get the data line
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
			soundLine = (SourceDataLine) AudioSystem.getLine(info);			
			soundLine.open(audioFormat);
			gainControl = (FloatControl) soundLine.getControl(FloatControl.Type.MASTER_GAIN);
		} catch (LineUnavailableException lu) {
			System.err.println("Could not open line for audio format: " + audioFormat);
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

	@SuppressWarnings("unused") // API method
    public void setVolume(float volume) {
        if (gainControl != null) {
            gainControl.setValue(volume);
        }
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
}
