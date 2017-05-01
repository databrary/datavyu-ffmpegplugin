
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * Plays an audio file using ffmpeg to transcode it: To change the codec into 
 * PCM, the sampling format, and the stereo/mono play back.
 */
public class AudioPlayer {
	static {
		// Make sure to place the ffmpeg libraries (dll's) in the java library path. I use '.'
		System.loadLibrary("./lib/AudioPlayer");
	}

	/** Size of the buffer */
	int BUFFER_SIZE = 64*1024;  // 64 kB
	
	/** Used to control the player thread */
	private boolean doPlay = false;
	
	/** True if more data was read from the buffer */
	private boolean isPlaying = true;
	
	/** Byte buffer to get data from the native side */
	private ByteBuffer buffer = null;
	
	/** Sample data buffer to copy from byte buffer to DataLine */
	private byte[] sampleData = null;
	
	/** Sound line to play the audio data */
	private SourceDataLine soundLine = null;
	
	/** Gain control for the underlying data line */
	FloatControl gainControl = null;
	
	/** Audio format for the output */
	AudioFormat outAudioFormat = null;
	
	/**
	 * Initialize and get a pointer to the audio buffer with the size of nByte.
	 * This is the pointer to the stream that is refreshed calling 
	 * loadNextFrame(). 
	 * 
	 * @param nByte The size of the buffer. 
	 * @return An initialized byte buffer.
	 */
	private native ByteBuffer getAudioBuffer(int nByte);

	/**
	 * Loads the next audio frame into the byte buffer.
	 * 
	 * @return True if there was audio data; otherwise False.
	 */
	private native boolean loadNextFrame();

	/**
	 * Initializes the transcoder but does NOT load any frame into the audio 
	 * buffer yet.
	 * 
	 * @param fileName
	 */
	private native int loadAudio(String fileName, AudioFormat audioFormat);

	/**
	 * Get the name of the sample format. The formats we can support in java are
	 * u8 for unsigned 8 bit and s16 for signed 16 bit.
	 * 
	 * @return String representation of the sample format of the output audio.
	 */
	private native String getSampleFormat();

	/**
	 * Get the codec name with sample format for the audio stream. The codecs
	 * we can support in java are: pcm_u8 and pcm_s16le. These are Pulse Code 
	 * Modulation (PCM) with unsigned 8 bit and unsigned 8 bit. For PCM see:
	 * https://en.wikipedia.org/wiki/Pulse-code_modulation
	 *  
	 * @return The name of the codec of the output audio.
	 */
	private native String getCodecName();
	
	/**
	 * Get the sample rate for the audio file. Typical values are 41 kHz, 
	 * 44 kHz, ...
	 * 
	 * @return The sample rate as float value for the output audio.
	 */
	private native float getSampleRate();

	/**
	 * Get the size of a sample in bits. Typical values are 8 bit and 16 bit.
	 * 
	 * @return The sample size in bits.
	 */
	private native int getSampleSizeInBits();

	/**
	 * Get the number of audio channels. Typical values are 1 (mono) and 
	 * 2 (stereo).
	 * 
	 * @return The number of channels for the output audio.
	 */
	private native int getNumberOfChannels();

	/**
	 * Get the size of a frame in bytes. Typical values are 1 By and 2 By.
	 * 
	 * @return The size of frame in bytes for the output audio.
	 */
	private native int getFrameSizeInBy();

	/**
	 * Get the frame rate in Hz, for instance 128 kHz.
	 * 
	 * @return Frame rate in Hz for the output audio.
	 */
	private native float getFramesPerSecond();
	
	/**
	 * Get the endianess for the audio file.
	 * 
	 * @return Endianess for the output audio.
	 */
	private native boolean bigEndian();

	/**
	 * Release the audio file resources of the native code.
	 */
	private native void release();
	
	/**
	 * Get the encoding for the output audio based on the codec.
	 * 
	 * @return Encoding for the AudioFormat of the output audio.
	 */
	private Encoding getEncoding() {
		String codecName = getCodecName().toLowerCase();
		Encoding encoding = new Encoding(codecName);
		switch (codecName) {
			case "pcm_u8": case "pcm_u16le":
				encoding = Encoding.PCM_UNSIGNED;
				break;
			case "pcm_s8": case "pcm_s16le":
				encoding = Encoding.PCM_SIGNED;
				break;
		}
		return encoding; // Return the codec with the name
	}

	/**
	 * Audio types for the audio player that we support are STEREO and MONO.
	 * The details for these supported audio formats are:
	 * MONO: PCM_SIGNED sample rate, 16 bit, mono, 2 bytes/frame, frame rate, le
	 * STEREO: PCM_UNSIGNED sample rate, 8 bit, stereo, 1 bytes/frame, frame rate,
	 * The sample rate and frame rate can vary. The other values are constrained
	 * by the supported DataLines in java.
	 */
	public enum AudioType {
	    MONO_TYPE, STEREO_TYPE
	}
	
	/** The supported mono format; blank values are from the input audio */
	private final static AudioFormat MONO_FORMAT = new AudioFormat(
			Encoding.PCM_SIGNED, 0, 0, 1, 0, 0, false);
	
	/** The supported stereo format; blank values are from the input audio */
	private final static AudioFormat STEREO_FORMAT = new AudioFormat(
			Encoding.PCM_UNSIGNED, 0, 0, 2, 0, 0, false);

	/**
	 * Open an audio file with the given audio type.
	 * 
	 * @param fileName The filename of the audio file.
	 * @param type The type to play back the file.
	 * 
	 * @return The error code. If it returns 0 no error occurred.
	 * 
	 * @throws LineUnavailableException Thrown if the line for the audio output 
	 * 			type could not be found.
	 */
	public int open(String fileName, AudioType type) throws 
		LineUnavailableException {
		
		// Select the audio format using the audio type
		AudioFormat audioFormat = MONO_FORMAT;
		switch (type) {
		case MONO_TYPE:
			audioFormat = MONO_FORMAT;
			break;
		case STEREO_TYPE:
			audioFormat = STEREO_FORMAT;
			break;
		}
		
		// Open the audio file using the native library
		int errNo = 0;
		if ((errNo = loadAudio(fileName, audioFormat)) != 0) {
			System.err.println("Error " + errNo + " occured when opening audio stream.");
			return errNo; 
		}

		// Allocate the audio buffer from the native side and a backup local one
		buffer = getAudioBuffer(BUFFER_SIZE);
		sampleData = new byte[buffer.capacity()];
		
		// When using stereo need to multiply the frameSize by number of channels
		outAudioFormat = new AudioFormat(getEncoding(), getSampleRate(), 
				getSampleSizeInBits(), getNumberOfChannels(), 
				getFrameSizeInBy()*getNumberOfChannels(), 
				(int)getFramesPerSecond(), false);
		
		// Get the data line
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, outAudioFormat);
		soundLine = (SourceDataLine) AudioSystem.getLine(info);			
		soundLine.open(outAudioFormat);
		soundLine.start();

		// Get the gain (volume) control for the sound line
		gainControl = (FloatControl) soundLine.getControl(FloatControl.Type.MASTER_GAIN);
		
		// Return the error code if any
		return errNo;
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
	 * Playing the next frame by pulling data from the native side and 
	 * transferring to the data line on the java side.
	 * 
	 * @return True if there was a next frame otherwise false.
	 */
	public boolean playNextFrame() {
		boolean hasNext = false;
		if ((hasNext = loadNextFrame())) {
			// Copy data from the buffer into sample data
			// We cannot directly copy from buffer.data() because the buffer is 
			// not backed by an array
			buffer.get(sampleData, 0, BUFFER_SIZE);
			soundLine.write(sampleData, 0, BUFFER_SIZE);
			buffer.rewind();
		}
		return hasNext;		
	}
	
	/**
	 * Closes the audio player. Call this at the end to ensure to free up 
	 * resources.
	 */
	public void close() {
		doPlay = false;
		release();
		soundLine.drain();
		soundLine.stop();
		soundLine.close();
	}
	
	/**
	 * Get the output audio format that is used to replay the sound. This format
	 * is controlled through the audio type.
	 * 
	 * @return The output audio format.
	 */
	public AudioFormat getOutputAudioFormat() {
		return outAudioFormat;
	}
	
	/**
	 * A demo of the API for this audio player.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		//String fileName = "C:\\Users\\Florian\\SleepingBag.MP4";
		//String fileName = "C:\\Users\\Florian\\WalkingVideo.mov";
		//String fileName = "C:\\Users\\Florian\\VideosForPlayer\\dvm1.mpg";
		//String fileName = "C:\\Users\\Florian\\VideosForPlayer\\Gah.mov";
		//String fileName = "C:\\Users\\Florian\\Los_Parranderos.mp3";
		String fileName = "C:\\Users\\Florian\\a2002011001-e02.wav";
		//String dirName = "C:\\Users\\Florian\\AudioCodecs\\";
		//String fileName = dirName + "audio_aac.mp4";
		//String fileName = dirName + "audio_aacplus1.mp4";
		//String fileName = dirName + "audio_aacplus2.mp4";
		//String fileName = dirName + "audio_applelossless.mov";
		//String fileName = dirName + "audio_ilbc.mov";
		//String fileName = dirName + "audio_ima4t1.mov";
		//String fileName = dirName + "audio_mp3.mp3";
		//String fileName = dirName + "audio_mpeg2.mpg";
		//String fileName = dirName + "audio_qualcomm_purevoice.mov";
		//String fileName = dirName + "audio_waveform.wav";
		
		AudioPlayer player = new AudioPlayer();
		AudioType type = AudioType.MONO_TYPE;
		//AudioType type = AudioType.STEREO_TYPE;
		try {
			int errNo = player.open(fileName, type);
			System.out.println("Max volume: " + player.getMaxVolume() 
					+ " decibel.");
			System.out.println("Min volume: " + player.getMinVolume()
					+ " decibel.");
			System.out.println("Output audio format: " 
					+ player.getOutputAudioFormat());
			
			player.setVolume(-10f);
			if (errNo != 0) {
				System.err.println("Could not open audio file: " + fileName 
						+ ", errNo: " + errNo);
			} else {
				while (player.playNextFrame()) {}				
			}
		} catch (LineUnavailableException ex) {
			ex.printStackTrace();
		} finally {
			System.out.println("Stopping player.");
			System.out.println("Closing player.");
			player.close();
		}
	}
}
