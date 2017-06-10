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
	/** 
	 * Load the native library that interfaces to ffmpeg. This load assumes
	 * that dependent dll's are within the the JVM's classpath. In our example 
	 * this is the directory above the directory 'src'.
	 */
	static {
		System.loadLibrary("./lib/MovieStream");
	}

	/** The minimum play back speed */
	public final static float MIN_SPEED = -4f;
	
	/** The maximum play back speed */
	public final static float MAX_SPEED = +4f;
	
	/** The size of the audio buffer */
	public final static int AUDIO_BUFFER_SIZE = 64*1024; // 64 kB
	
	/** The duration of the video/audio. Initialized at opening. */
	protected double duration = 0;
	
	/** The width of the image in the stream. Changes with the file. */
	protected int widthOfStream = 0;
	
	/** The height of the image in the stream. Changes with the file. */
	protected int heightOfStream = 0;
	
	/** The width of the current view. Changes with the view. */
	protected int widthOfView = 0;
	
	/** The height of the current view. Changes with the view. */
	protected int heightOfView = 0;
	
	/** The number of channels. Initialized at opening. */
	protected int nChannels = 0;
	
	/** The start time of the audio/video streams. Initialized at opening. */
	protected double startTime = 0;
	
	/** The end time of the audio/video streams. Initialized at opening. */
	protected double endTime = 0;
	
	/** The byte buffer for the images. Initialized at opening. */
	protected ByteBuffer imageBuffer = null;
	
	/** The byte buffer for the audio. Initialized at opening. */
	protected ByteBuffer audioBuffer = null;
	
	/** The audio format of the audio stream. Initialized at opening. */
	protected AudioFormat audioFormat = null;
	
	/** The color space of the image stream. Initialized at opening. */
	protected ColorSpace colorSpace = null;
	
	/** Indicates that a image/audio stream is open. */
	boolean isOpen = false;
	
	/**
	 * Find out if this movie stream contains an image stream.
	 * 
	 * @return True if there is an image stream; otherwise false.
	 */
	public native boolean hasVideoStream();
	
	/**
	 * Find out if this movie stream contains an audio stream.
	 * 
	 * @return True if there is an audio stream; otherwise false.
	 */
	public native boolean hasAudioStream();
	
	/**
	 * Native method to get the start time of the streams. Typically, this will 
	 * be 0 seconds.
	 * 
	 * @return The start time in seconds.
	 */
	private native double getStartTime0();
	
	/**
	 * Native method to get the end time of the streams.
	 * 
	 * @return The end time in seconds.
	 */
	private native double getEndTime0();
	
	/**
	 * Native method to get the duration of the streams.
	 * 
	 * @return The duration in seconds.
	 */
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
	
	/**
	 * Native method to set the time in the stream. This method is often called 
	 * to seek within the stream.
	 * 
	 * @param time The time in seconds.
	 */
	private native void setTime0(double time);

	/**
	 * Native method to set the play back speed as multiple of the native play
	 * back speed. E.g. 0.5x plays at half of the native speed.
	 * 
	 * @param speed The new play back speed. 
	 */
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
	
	/**
	 * Native method to close all the streams that this movie has.
	 * 
	 * @throws IOException If errors appear during closing.
	 */
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
	public native boolean availableAudioData();
	
	@Override
	public native boolean availableImageFrame();

	/**
	 * Native method to load the next audio data into the buffer.
	 * 
	 * @return True if data could be loaded; otherwise false.
	 */
	private native boolean loadNextAudioData();

	@Override
	public int readAudioData(byte[] buffer) {
		if (loadNextAudioData()) {
			audioBuffer.get(buffer, 0, AUDIO_BUFFER_SIZE);
			audioBuffer.rewind();
			return 1;
		}
		return 0;
	}
	
	/**
	 * Initialized a byte buffer of the size nByte in the native code and 
	 * returns the instance of that initialized byte buffer.
	 * 
	 * @param nByte The number of bytes that the buffered is initialized with.
	 * 
	 * @return An instance of the byte buffer.
	 */
	private native ByteBuffer getAudioBuffer(int nByte);

	/**
	 * Get the sample format name.
	 * 
	 * @return The name of the sample format.
	 */
	private native String getSampleFormat();

	/**
	 * Get the codec name. Examples are: 'pcm_u8' and 'pcm_s8'.
	 * 
	 * @return The codec name.
	 */
	private native String getCodecName();

	/**
	 * Get the sample rate of the audio stream.
	 * 
	 * @return The sample rate in Hertz.
	 */
	private native float getSampleRate();

	/**
	 * Get the sample size in bits.
	 * 
	 * @return The sample size in bits.
	 */
	private native int getSampleSizeInBits();

	/**
	 * Get the number of sound channels. For mono, this method returns 1. For 
	 * stereo this method returns 2. Notice, that more channels are possible 
	 * too, depending on the stream. E.g. surround sound can have five channels.
	 * 
	 * @return The number of sound channels.
	 */
	private native int getNumberOfSoundChannels();

	/**
	 * Get the frame size of audio frames.
	 * 
	 * @return The frame size in bytes.
	 */
	private native int getFrameSize();

	/**
	 * Get the frame rate of the audio stream.
	 * 
	 * @return The frame rate in Hertz.
	 */
	private native float getFrameRate();

	/**
	 * Get the data encoding for the audio stream; either big or little endian.
	 * 
	 * @return True for big endian and false for little endian.
	 */
	private native boolean bigEndian();

	/**
	 * Get the encoding for the audio stream.
	 * 
	 * @return The encoding javax.sound.sampled.AudioFormat.Encoding.
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
		return encoding; // Return codec with name
	}
	
	/**
	 * Native method to open a movie file.
	 * 
	 * @param fileName The name of the movie file.
	 * @param version A version string for the log file.
	 * @param audioFormat The requested audio format. This audio format is used
	 * 					  to transcode the input audio format into.
	 * @return The error code when opening this stream, 0 for no error.
	 */
	private native int open0(String fileName, String version, 
			AudioFormat audioFormat);
	
	@Override
	public AudioFormat getAudioFormat() {
		return audioFormat;
	}

	/**
	 * Method to open a movie file.
	 *  
	 * @param fileName The name of the movie file.
	 * @param version A version string to log the file. 
	 * @param reqColorSpace The requested color space.
	 * @param reqAudioFormat The requested audio format.
	 * @throws IOException Thrown if the color space is not supported.
	 * 					   Thrown if the audio format is not supported.
	 * 					   Thrown if the file cannot be found.
	 */
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
		// Get all the information about the video/audio and cache it
		colorSpace = reqColorSpace;
		duration = getDuration0();
		startTime = getStartTime0();
		if (hasVideoStream()) {
			widthOfView = widthOfStream = getWidth0();
			heightOfView = heightOfStream = getHeight0();		
			nChannels = getNumberOfColorChannels0();			
		}
		startTime = getStartTime0();
		endTime = getEndTime0();
		if (hasAudioStream()) {
			audioBuffer = getAudioBuffer(AUDIO_BUFFER_SIZE);
			// When using stereo need to multiply the frameSize by number of channels
			audioFormat = new AudioFormat(getEncoding(), getSampleRate(), 
					getSampleSizeInBits(), getNumberOfSoundChannels(), 
					getFrameSize() * getNumberOfSoundChannels(), 
					(int) getFrameRate(), false);			
		}
		isOpen = true;
	}

	/**
	 * Native method to get the number of color channels of the images.
	 * 
	 * @return The number of color channels.
	 */
	private native int getNumberOfColorChannels0();

	/**
	 * Native method to get the height of the original images.
	 * 
	 * @return The height in pixels.
	 */
	private native int getHeight0();
	
	/**
	 * Native method to get the width of the original images.
	 * 
	 * @return The width in pixels.
	 */
	private native int getWidth0();
	
	@Override
	public int getNumberOfColorChannels() {
		return nChannels;
	}

	@Override
	public int getHeightOfStream() {
		return heightOfStream;
	}

	@Override
	public int getWidthOfStream() {
		return widthOfStream;
	}
	
	@Override
	public int getHeightOfView() {
		return heightOfView;
	}
	
	@Override
	public int getWidthOfView() {
		return widthOfView;
	}
	
	/**
	 * Native method to set the viewing window. This changes the height and 
	 * width of the view -- unless set to the same values.
	 * 
	 * This method can not be called while the stream is playing.
	 * 
	 * @param x0 The horizontal starting position in pixels.
	 * @param y0 The vertical starting position in pixels.
	 * @param width The width of the view in pixels.
	 * @param height The height of the view in pixels.
	 * @return Return true if the window was set; otherwise false.
	 */
	private native boolean view(int x0, int y0, int width, int height);

	@Override
	public void setView(int x0, int y0, int width, int height)
			throws IndexOutOfBoundsException {
		if (x0 < 0 || x0+width > getWidthOfStream() 
				|| y0 < 0 || y0+height > getHeightOfStream()) {
			throw new IndexOutOfBoundsException("The viewing window [" + x0 
					+ ", " + x0+width + "] x [" + y0 + ", " + y0+height + "] "
					+ "is ouside the bounds [0, " + getWidthOfStream() + "] x [0, " 
					+ getHeightOfStream() + "]");
		} else {
			heightOfView = height;
			widthOfView = width;
			view(x0, y0, width, height);
		}
	}
	
	/**
	 * Native method creates a byte buffer for the image frames with for the 
	 * width, height, and number of channels as given in the movie stream.
	 * 
	 * When we change the view the size of this buffer will not change! We just
	 * don't fill it up all the way.
	 * 
	 * @return The instance of the byte buffer that has been created in the 
	 * 		   native code.
	 */
	private native ByteBuffer getFrameBuffer();
	
	/**
	 * Loads the next image frame into the frame buffer. This method blocks if
	 * there is no such frame available.
	 * 
	 * @return An integer number that defines the number of frames that were 
	 * 		   loaded. To fulfill the play back speed frames may be skipped. 
	 */
	private native int loadNextImageFrame();
		
	@Override
	public int readImageFrame(byte[] buffer) {
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
	
	/**
	 * This is an example on how to use the API.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		// Create the movie stream
		final MovieStream movieStream = new MovieStream();
		//String fileName = "C:\\Users\\Florian\\TurkishManGaitClip_KEATalk.mov";
		//String fileName = "C:\\Users\\Florian\\a2002011001-e02.wav";
		String fileName = "C:\\Users\\Florian\\NoAudio\\TurkishCrawler_NoAudio.mov";
		String version = "0.1.0.0";
		// The requested color space RGB
		ColorSpace reqColorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
		// The requested audio format is MONO
		AudioFormat reqAudioFormat = AudioSound.MONO_FORMAT;
		// Try opening the stream and attach an image/audio stream
		try {
			movieStream.open(fileName, version, reqColorSpace, reqAudioFormat);
			List<Thread> threads = new ArrayList<>(2);
			//movieStream.setSpeed(2f);
			if (movieStream.hasVideoStream()) {
				final Frame f = new Frame();
				final VideoDisplay videoDisplay = new VideoDisplay(movieStream);
				int width = movieStream.getWidthOfStream();
				int height = movieStream.getHeightOfStream();
		        f.setBounds(0, 0, width, height);
		        f.add(videoDisplay);
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
		    				videoDisplay.showNextFrame();	    				
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
			    		while (movieStream.availableAudioData()) {
				    		audioSound.playNextData();
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
