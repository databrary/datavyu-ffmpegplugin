package org.datavyu;

import java.awt.Frame;
import java.awt.color.ColorSpace;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.LineUnavailableException;

public class MovieStream implements VideoStream, AudioStream {
	
	static {
		System.loadLibrary("./lib/MovieStream");
	}
	
	public final static float MIN_SPEED = -4f;
	public final static float MAX_SPEED = +4f;
	public final static int AUDIO_BUFFER_SIZE = 64*1024; // 64 kB
	
	// Initialize these variables when opening a video file
	double duration = 0;
	int width = 0;
	int height = 0;
	int nChannels = 0;
	double startTime = 0;
	double endTime = 0;
	protected ByteBuffer imageBuffer = null;
	protected ByteBuffer audioBuffer = null;
	AudioFormat outAudioFormat = null;
	ColorSpace colorSpace = null;
	boolean isOpen = false;
	
	public native boolean hasImageStream();
	
	public native boolean hasAudioStream();
	
	private native double getStartTime0();
	
	private native double getEndTime0();
	
	private native double getDuration0();
	
	@Override
	public ColorSpace getColorSpace() {
		return colorSpace;
	}
	
	@Override
	public double getStartTime() {
		return startTime;
	}

	@Override
	public double getEndTime() {
		return endTime;
	}

	@Override
	public double getDuration() {
		return duration;
	}

	@Override
	public native double getCurrentTime();
	
	private native void setTime0(double time);
	
	private native void setPlaybackSpeed0(float speed);
	
	@Override
	public void seek(double time) throws IndexOutOfBoundsException {
		if (time < getStartTime() || time > getEndTime()) {
			throw new IndexOutOfBoundsException("Time " + time + " is not in " 
					+ "range [" + getStartTime() + ", " + getEndTime() + "]");
		} else {
			setTime0(time);
		}
	}

	@Override
	public void setSpeed(float speed) throws IndexOutOfBoundsException {
		if (Math.abs(speed) < Math.ulp(1f)) {
			throw new IndexOutOfBoundsException("Speed " + speed 
					+ " is not allowed.");
		} else if (speed < MIN_SPEED || speed > MAX_SPEED) {
			throw new IndexOutOfBoundsException("Speed " + speed + "[" 
					+ MIN_SPEED + ", " + MAX_SPEED + "]");
		} else {
			setPlaybackSpeed0(speed);
		}		
	}
	
	@Override
	public native void reset();
	
	public native void close0() throws IOException;
	
	@Override
	public void close() throws IOException {
		if (isOpen) {
			close0();
		}
		isOpen = false;
	}
	
	@Override
	public int getAudioBufferSize() {
		return AUDIO_BUFFER_SIZE;
	}
	
	@Override
	public native boolean availableAudioFrame();
	
	@Override
	public native boolean availableImageFrame();
		
	private native boolean loadNextAudioFrame();

	@Override
	public int readAudioFrame(byte[] buffer) {
		if (loadNextAudioFrame()) {
			audioBuffer.get(buffer, 0, AUDIO_BUFFER_SIZE);
			audioBuffer.rewind();
			return 1;
		}
		return 0;
	}
	
	private native ByteBuffer getAudioBuffer(int nByte);
	
	private native String getSampleFormat();

	private native String getCodecName();

	private native float getSampleRate();

	private native int getSampleSizeInBits();
	
	private native int getNumberOfSoundChannels();

	private native int getFrameSize();
	
	private native float getFrameRate();
	
	private native boolean bigEndian();
	
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
		return encoding; // Return codec with name
	}
	
	private native int open0(String fileName, String version, 
			AudioFormat audioFormat);
	
	@Override
	public AudioFormat getOutputAudioFormat() {
		return outAudioFormat;
	}

	public void open(String fileName, String version, ColorSpace reqColorSpace, 
			AudioFormat reqAudioFormat) throws IOException {
		int error = 0;		
		if (reqColorSpace != ColorSpace.getInstance(ColorSpace.CS_sRGB)) {
			throw new IOException("Color space " + reqColorSpace + " not supported!");
		} else if (!(reqAudioFormat.getChannels()==1 
				&& reqAudioFormat.getEncoding()==Encoding.PCM_SIGNED) 
				&& !(reqAudioFormat.getChannels()==2 
				&& reqAudioFormat.getEncoding()==Encoding.PCM_UNSIGNED)) {
			throw new IOException("Requested audio format " + reqAudioFormat 
					+ " not supported!");
		} else if ((error = open0(fileName, version, reqAudioFormat)) != 0) {
			throw new IOException("Error " + error + " occured while " 
					+ "opening " + fileName + ".");
		}		
		// Get all the information about the video/audio and cache it here
		colorSpace = reqColorSpace;
		duration = getDuration0();
		startTime = getStartTime0();
		width = getWidth0();
		height = getHeight0();
		nChannels = getNumberOfColorChannels0();
		startTime = getStartTime0();
		endTime = getEndTime0();
		audioBuffer = getAudioBuffer(AUDIO_BUFFER_SIZE);
		// When using stereo need to multiply the frameSize by number of channels
		outAudioFormat = new AudioFormat(getEncoding(), getSampleRate(), 
				getSampleSizeInBits(), getNumberOfSoundChannels(), 
				getFrameSize() * getNumberOfSoundChannels(), 
				(int) getFrameRate(), false);
		isOpen = true;
	}

	private native int getNumberOfColorChannels0();
	
	private native int getHeight0();
	
	private native int getWidth0();
	
	@Override
	public int getNumberOfColorChannels() {
		return nChannels;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getWidth() {
		return width;
	}
	
	private native boolean view(int x0, int y0, int width, int height);

	@Override
	public void setView(int x0, int y0, int width, int height)
			throws IndexOutOfBoundsException {
		if (x0 < 0 || x0+width > getWidth() || y0 < 0 || y0+height > getHeight()) {
			throw new IndexOutOfBoundsException("The viewing window [" + x0 
					+ ", " + x0+width + "] x [" + y0 + ", " + y0+height + "] "
					+ "is ouside the bounds [0, " + getWidth() + "] x [0, " 
					+ getHeight() + "]");
		} else {
			view(x0, y0, width, height);
		}
	}
	
	private native ByteBuffer getFrameBuffer();
	
	private native int loadNextImageFrame();
		
	@Override
	public int readImageFrame(byte[] buffer)
			throws IndexOutOfBoundsException {
		int nFrame = 0;
		// Check if we loaded at least one image frame
		if ((nFrame = loadNextImageFrame()) > 0) {
			// Load the image frame into the buffer
			imageBuffer = getFrameBuffer();
			imageBuffer.get(buffer, 0, imageBuffer.capacity());
		}
		// Return the number of loaded image frames
		return nFrame;
	}
	
	public static void main(String[] args) {
		final MovieStream movieStream = new MovieStream();
		//String fileName = "C:\\Users\\Florian\\WalkingVideo.mov";
		//String fileName = "C:\\Users\\Florian\\TurkishManGaitClip_KEATalk.mov";
		String fileName = "C:\\Users\\Florian\\a2002011001-e02.wav";
		String version = "0.1.0.0";
		ColorSpace reqColorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
		AudioFormat reqAudioFormat = AudioSound.MONO_FORMAT;
		try {
			movieStream.open(fileName, version, reqColorSpace, reqAudioFormat);
			List<Thread> threads = new ArrayList<>(2);
			//movieStream.setSpeed(1f);
			if (movieStream.hasImageStream()) {
				final Frame f = new Frame();
				final MovieCanvas movieCanvas = new MovieCanvas(movieStream);
				int width = movieStream.getWidth();
				int height = movieStream.getHeight();
		        f.setBounds(0, 0, width, height);
		        f.add(movieCanvas);
		        f.addWindowListener( new WindowAdapter() {
		            public void windowClosing(WindowEvent ev) {
		            	try {
							movieStream.close();
						} catch (IOException io) {
							io.printStackTrace();
						}
		                System.exit(0);
		            }
		        } );
		        f.setVisible(true);
		        // Create a thread to play the images
		    	class ImagePlayerThread extends Thread {
		    		@Override
		    		public void run() {
		    			while (movieStream.availableImageFrame()) {
		    				movieCanvas.showNextFrame();	    				
		    			}
		    		}
		    	}	    	
		    	// Create a thread to play the audio
		    	threads.add(new ImagePlayerThread());
			}
			if (movieStream.hasAudioStream()) {
				final AudioSound audioSound = new AudioSound(movieStream);
		    	class AudioPlayerThread extends Thread {
			    	@Override
			    	public void run() {
			    		while (movieStream.availableAudioFrame()) {
				    		audioSound.playNextFrame();
			    		}			    		
			    		audioSound.close();
			    	}
		    	}
		    	threads.add(new AudioPlayerThread());
			}
			// Start all threads
			for (Thread thread : threads) { thread.start(); }
			// Wait for all threads to be finished
			for (Thread thread : threads) {
				try {
					thread.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		} catch (IOException io) {
			System.err.println(io.getLocalizedMessage());
		} catch (LineUnavailableException lu) {
			System.err.println(lu.getLocalizedMessage());
		}
	}
}
