import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Plays the first 2 seconds of an audio file and then stops and destroys all threads for this player:
 * These are a producer thread in c/c++ and an consumer thread in java.
 * @author Florian Raudies
 * @date 07/03/2016
 */
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
		Encoding encoding = Encoding.PCM_UNSIGNED; // TODO: Get this info from ffmpeg.
		audioFormat = new AudioFormat(encoding, sampleRate, sampleSizeInBits, 
				channels, frameSize, frameRate, bigEndian);
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
		soundLine = (SourceDataLine) AudioSystem.getLine(info);
		soundLine.open(audioFormat);
		soundLine.start();
		sampleData = new byte[buffer.capacity()]; // could be too large, let's see.
		playerThread = new PlayerThread();
		playerThread.setDaemon(true); // can be shutdown by JVM.
	}
	
	class PlayerThread extends Thread {
		@Override
		public void run() { // TODO: Stop and restart of player.
			while (doPlay && loadNextFrame()) {
				System.out.println("Got next audio frame.");
				buffer.get(sampleData, 0, BUFFER_SIZE);
				System.out.println("First byte: " + buffer.get(0) + " and last byte " + buffer.get(BUFFER_SIZE-1));
				soundLine.write(sampleData, 0, BUFFER_SIZE);
				buffer.rewind();
				System.out.println("Rewound buffer.");
				System.out.flush();
			}
			System.out.println("Stopped player loop.");
		}
	}
	
	public void restart() {
		doPlay = true;
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
		System.out.println("State of player thread: " + playerThread.getState());
		//playerThread.notify();
		release();
		System.out.println("Player thread alive? " + playerThread.isAlive());
		System.out.println("Player thread is deamon? " + playerThread.isDaemon());
		soundLine.drain();
		soundLine.stop();
		soundLine.close();
	}
	
	public static void main(String[] args) {
		String fileName = "C:\\Users\\Florian\\TakeKeys.wav";
		PlaySoundFromJNI player = new PlaySoundFromJNI();
		// Set up an audio input stream piped from the sound file.
		try {
			player.open(fileName);
			System.out.println("Opened audio file!");
			player.play();
			System.out.println("Started player thread!");
			Thread.sleep(2000); // Play the file for 2 sec and then shut down.
		} catch (InterruptedException ex) {
			ex.printStackTrace();			
		} catch (UnsupportedAudioFileException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (LineUnavailableException ex) {
			ex.printStackTrace();
		} finally {
			System.out.println("Stopping player.");
			player.stop();
			System.out.println("Closing player.");
			player.close();
			System.out.println("Closed player.");
			// Now get that Java Sound event dispatcher thread to close (THIS WAS NOT THE PROBLEM)
			// http://bugs.java.com/bugdatabase/view_bug.do?bug_id=4365713
		}		
	}
}
