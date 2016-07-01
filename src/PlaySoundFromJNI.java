import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;


public class PlaySoundFromJNI {
	static {
		// Make sure to place the ffmpeg libraries (dll's) in the java library path. I use '.'
		System.loadLibrary("./lib/PlaySoundFromJNI");
	}
	
	private PlayerThread playerThread = null;
	private boolean isOpen = false;
	private boolean doPlay = false;
	private ByteBuffer buffer = null;
	int BUFFER_SIZE = 8*1024;  // 8 KB
	private SourceDataLine soundLine = null;
	private AudioFormat audioFormat = null;
	private byte[] sampleData = null;
	
		
	private native ByteBuffer getAudioBuffer(int nByte); // provides pointer to stream.
	
	private native boolean loadNextFrame(); // frees memory advances pointer
	
	private native void loadAudio(String fileName); // does NOT load any frame into the audio buffer.
	
	private native String getSampleFormat();
	
	private native float getSampleRate();
	
	private native int getSampleSizeInBits();
	
	private native int getNumberOfChannels();
	
	private native int getFrameSizeInBy();
	
	private native float getFramesPerSecond();
	
	private native boolean bigEndian();
		
	private native void release();
	
	
	public void open(String fileName) throws IOException, 
			UnsupportedAudioFileException, LineUnavailableException {
		
		loadAudio(fileName);
		buffer = getAudioBuffer(BUFFER_SIZE);
		isOpen = true;
		// Create the audio format.
		String sampleFormat = getSampleFormat();
		float sampleRate = getSampleRate();
		int sampleSizeInBits = getSampleSizeInBits();
		int channels = getNumberOfChannels();
		int frameSize = getFrameSizeInBy();
		float frameRate = getFramesPerSecond();
		boolean bigEndian = bigEndian();
		
		System.out.println("sample format = " + sampleFormat);
		System.out.println("sample rate = " + sampleRate);
		System.out.println("sample size in bits = " + sampleSizeInBits);
		System.out.println("channels = " + channels);
		System.out.println("frameSize = " + frameSize);
		System.out.println("frameRate = " + frameRate);
		System.out.println("bigEndian = " + bigEndian);
		
		// PCM_UNSIGNED 22050.0 Hz, 8 bit, mono, 1 bytes/frame, 
		Encoding encoding = Encoding.PCM_UNSIGNED; // TODO: Get this info from ffmpeg.
		audioFormat = new AudioFormat(encoding, sampleRate, sampleSizeInBits, 
				channels, frameSize, frameRate, bigEndian);
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
		soundLine = (SourceDataLine) AudioSystem.getLine(info);
		soundLine.open(audioFormat);
		soundLine.start();
		sampleData = new byte[buffer.capacity()]; // could be too large, let's see.
		playerThread = new PlayerThread();
	}
	
	class PlayerThread extends Thread {
		@Override
		public void run() {
			while (doPlay && loadNextFrame()) {
				buffer.get(sampleData, 0, BUFFER_SIZE);
				soundLine.write(sampleData, 0, BUFFER_SIZE);
			}
		}
	}
	
	public void play()  {
		doPlay = true;
		playerThread.start();
	}
	
	public void stop() {
		doPlay = false;
	}
	
	public void close() {
		doPlay = false;		
		isOpen = false;
		soundLine.drain();
		soundLine.stop();
		soundLine.close();
		release();
	}
	
	public static void main(String[] args) {
		String fileName = "C:\\Users\\Florian\\TakeKeys.wav";
		PlaySoundFromJNI player = new PlaySoundFromJNI();
		// Set up an audio input stream piped from the sound file.
		try {
			player.open(fileName);
			//player.play();
		} catch (UnsupportedAudioFileException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (LineUnavailableException ex) {
			ex.printStackTrace();
		} finally {
			player.stop();
			player.close();
		}		
	}

}
