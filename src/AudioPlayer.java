
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

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
	int BUFFER_SIZE = 512*1024;  // 512 kB
	
	/** Used to control the player thread */
	private boolean doPlay = false;
	
	/** True if more data was read from the buffer */
	private boolean isPlaying = true;
	
	/** Byte buffer to get data from the native side */
	private ByteBuffer buffer = null;
	
	/** Sample data buffer to copy from byte buffer to DataLine*/
	private byte[] sampleData = null;
	
	/** Sound line to play the audio data */
	private SourceDataLine soundLine = null;
	
	/**
	 * Initialize and get a pointer to the audio buffer with the size of nByte. 
	 * 
	 * @param nByte The size of the buffer. 
	 * @return An initialized byte buffer.
	 */
	private native ByteBuffer getAudioBuffer(int nByte); // provides pointer to stream.

	/**
	 * Loads the next audio frame into the byte buffer.
	 * @return True if there was audio data; otherwise False.
	 */
	private native boolean loadNextFrame(); // advances pointer

	/**
	 * Initializes the transcoder but does NOT load any frame into the audio 
	 * buffer.
	 * 
	 * @param fileName
	 */
	private native int loadAudio(String fileName, AudioFormat audioFormat);

	private native String getSampleFormat();
	
	private native String getCodecName();
	
	private native float getSampleRate();
	
	private native int getSampleSizeInBits();
	
	private native int getNumberOfChannels();
	
	private native int getFrameSizeInBy();
	
	private native float getFramesPerSecond();
	
	private native boolean bigEndian();
		
	private native void release();
	
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
		return encoding;
	}
	
	public enum AudioType {
	    MONO_TYPE, STEREO_TYPE
	}
	
	private final static AudioFormat MONO_FORMAT = new AudioFormat(
			Encoding.PCM_SIGNED, 0, 0, 1, 0, 0, false);
	private final static AudioFormat STEREO_FORMAT = new AudioFormat(
			Encoding.PCM_UNSIGNED, 0, 0, 2, 0, 0, false);
	
	public int open(String fileName, AudioType type) throws IOException, 
			UnsupportedAudioFileException, LineUnavailableException {
		AudioFormat audioFormat = MONO_FORMAT;
		switch (type) {
		case MONO_TYPE:
			audioFormat = MONO_FORMAT;
			break;
		case STEREO_TYPE:
			audioFormat = STEREO_FORMAT;
			break;
		}
				
		int errNo = 0;
		if ((errNo = loadAudio(fileName, audioFormat)) != 0) {
			System.err.println("Error " + errNo + " occured when opening audio stream.");
			return errNo; 
		}
		System.out.println("Audio format: " + audioFormat);
		
		buffer = getAudioBuffer(BUFFER_SIZE);	
		sampleData = new byte[buffer.capacity()];
		
		// When using stereo need to multiply the frameSize by number of channels
		audioFormat = new AudioFormat(getEncoding(), getSampleRate(), 
				getSampleSizeInBits(), getNumberOfChannels(), 
				getFrameSizeInBy()*getNumberOfChannels(), 
				(int)getFramesPerSecond(), false);
		
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
		soundLine = (SourceDataLine) AudioSystem.getLine(info);			
		soundLine.open(audioFormat);
		soundLine.start();
		
		// Get all controls for this data line.
		Control controls[] = soundLine.getControls();
		for (Control control : controls) {
			System.out.println(control);
		}
		
		// We noticed that it offers a gain control which we use to change the volume by -10dB.
		FloatControl gainControl = (FloatControl) soundLine.getControl(FloatControl.Type.MASTER_GAIN);
		gainControl.setValue(-10.0f);
		System.out.println("max volume: " + gainControl.getMaximum());
		System.out.println("min volume: " + gainControl.getMinimum());
		
		return errNo;
	}
	
	public boolean playNextFrame() {
		boolean hasNext = false;
		if ((hasNext = loadNextFrame())) {
			System.out.println("Loaded next frame.");
			// Copy data from the buffer into sample data
			buffer.get(sampleData, 0, BUFFER_SIZE);
			soundLine.write(sampleData, 0, BUFFER_SIZE);
			buffer.rewind();
		}
		return hasNext;		
	}
	
	public void close() {
		doPlay = false;
		release();
		soundLine.drain();
		soundLine.stop();
		soundLine.close();
	}
	
	// Nice overview:  https://docs.oracle.com/javase/tutorial/sound/sampled-overview.html
	public static void main(String[] args) {
		//String fileName = "C:\\Users\\Florian\\SleepingBag.MP4";
		//String fileName = "C:\\Users\\Florian\\WalkingVideo.mov";
		//String fileName = "C:\\Users\\Florian\\VideosForPlayer\\dvm1.mpg";
		//String fileName = "C:\\Users\\Florian\\VideosForPlayer\\Gah.mov";
		//String fileName = "C:\\Users\\Florian\\Los_Parranderos.mp3";
		//String fileName = "C:\\Users\\Florian\\a2002011001-e02.wav";
		String dirName = "C:\\Users\\Florian\\AudioCodecs\\";
		//String fileName = dirName + "audio_aac.mp4";
		//String fileName = dirName + "audio_aacplus1.mp4";
		//String fileName = dirName + "audio_aacplus2.mp4";
		//String fileName = dirName + "audio_applelossless.mov";
		//String fileName = dirName + "audio_ilbc.mov";
		//String fileName = dirName + "audio_ima4t1.mov";
		//String fileName = dirName + "audio_mp3.mp3";
		//String fileName = dirName + "audio_mpeg2.mpg";
		//String fileName = dirName + "audio_qualcomm_purevoice.mov";
		String fileName = dirName + "audio_waveform.wav";
		AudioPlayer player = new AudioPlayer();
		AudioType type = AudioType.MONO_TYPE;
		//AudioType type = AudioType.STEREO_TYPE;
		// Set up an audio input stream piped from the sound file.
		try {
			int errNo = player.open(fileName, type);
			if (errNo != 0) {
				System.err.println("Could not open audio file: " + fileName 
						+ ", errNo: " + errNo);
			} else {
				while (player.playNextFrame()) {
					System.out.println("Playing.");
				}				
			}			
		} catch (UnsupportedAudioFileException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (LineUnavailableException ex) {
			ex.printStackTrace();
		} finally {
			System.out.println("Stopping player.");
			System.out.println("Closing player.");
			//player.close();
		}
	}
}
