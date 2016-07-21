
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

// TODO: stop/restart, fast/slow play back, change the volume through gain control on data line
// For faster/slower play back see this post
// http://stackoverflow.com/questions/5760128/increase-playback-speed-of-sound-file-in-java
// Essentially drop/repeat samples (look at this sample rate converter) which encapsulates that
// functionally into a class   http://www.jsresources.org/examples/SampleRateConverter.html

// Transcode the audio from anything (especially ACC) into PCM! 
// https://www.ffmpeg.org/doxygen/2.4/transcoding_8c_source.html
// https://www.ffmpeg.org/doxygen/2.2/transcode_aac_8c-example.html

/**
 * Plays the first 2 seconds of an audio file and then stops and destroys all threads for this player:
 * These are a producer thread in c/c++ and an consumer thread in java.
 * @author Florian Raudies
 * @date 07/20/2016
 */
public class PlaySoundTranscodedFromJNI {
	static {
		// Make sure to place the ffmpeg libraries (dll's) in the java library path. I use '.'
		System.loadLibrary("./lib/PlaySoundTranscodedFromJNI");
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
	
	// use this codec:  AV_CODEC_ID_PCM_U8 (see libavcodec/avcodec.h L411)
	// use this sample format:  AV_SAMPLE_FMT_U8 (see libavutil/samplefmt.c)
	// use this sample rate: 22050.0
	// use this sample size in bits: 8
	// use this number of channels: 2
	// use this frame size in bytes: 1
	// use this frames per second (frame rate): 22050
	
	private native void loadAudio(String fileName); // does NOT load any frame into the audio buffer.
		
	private native void release();	
	
	public void open(String fileName) throws IOException, 
			UnsupportedAudioFileException, LineUnavailableException {
		
		loadAudio(fileName);
		buffer = getAudioBuffer(BUFFER_SIZE);		
		isOpen = true;
		audioFormat = new AudioFormat(Encoding.PCM_UNSIGNED, 22050.0f, 8, 1, 1, 22050, false);
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
		
		sampleData = new byte[buffer.capacity()]; // could be too large, let's see.
		playerThread = new PlayerThread();
		playerThread.setDaemon(true); // can be shutdown by JVM.
	}
	
	class PlayerThread extends Thread {
		@Override
		public void run() { // TODO: Stop and restart of player.
			while (doPlay && loadNextFrame()) {
				//System.out.println("Got next audio frame.");
				buffer.get(sampleData, 0, BUFFER_SIZE);
				//System.out.println("First byte: " + buffer.get(0) + " and last byte " + buffer.get(BUFFER_SIZE-1));
				soundLine.write(sampleData, 0, BUFFER_SIZE);
				buffer.rewind();
				//System.out.println("Rewound buffer.");
				//System.out.flush();
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
		playerThread.interrupt();
		System.out.println("State of player thread: " + playerThread.getState());
		release();
		System.out.println("Player thread alive? " + playerThread.isAlive());
		System.out.println("Player thread is deamon? " + playerThread.isDaemon());
		soundLine.drain();
		soundLine.stop();
		soundLine.close();
	}
	
	// Nice overview:  https://docs.oracle.com/javase/tutorial/sound/sampled-overview.html
	public static void main(String[] args) {
		//String fileName = "C:\\Users\\Florian\\TakeKeys.wav";
		String fileName = "C:\\Users\\Florian\\SleepingBag.MP4";
		//String fileName = "C:\\Users\\Florian\\WalkingVideo.mov";
		PlaySoundTranscodedFromJNI player = new PlaySoundTranscodedFromJNI();
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
