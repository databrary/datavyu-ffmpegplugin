package org.datavyu.plugins.ffmpegplayer;

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

	/*
	 * Load the native library that interfaces to ffmpeg. This load assumes
	 * that dependent dll's are within the the JVM's classpath. In our example 
	 * this is the directory above the directory 'src'.
	 */
	static {
		System.loadLibrary("./lib/MovieStream");
	}

	/** The stream id for this movie stream */
	private int streamId;

	/** The minimum play back speed */
	private final static float MIN_SPEED = -16f;
	
	/** The maximum play back speed */
	private final static float MAX_SPEED = +16f;
	
	/** The size of the audio buffer */
	private final static int AUDIO_BUFFER_SIZE = 64*1024; // 64 kB
	
	/** The duration of the video/audio. Initialized at opening. */
	protected double duration = 0;
	
	/** The width of the image in the stream. Changes with the file. */
	private int widthOfStream = 0;
	
	/** The height of the image in the stream. Changes with the file. */
	private int heightOfStream = 0;
	
	/** The width of the current view. Changes with the view. */
	private int widthOfView = 0;
	
	/** The height of the current view. Changes with the view. */
	private int heightOfView = 0;
	
	/** The number of channels. Initialized at opening. */
	private int nChannels = 0;
	
	/** The start time of the audio/video streams. Initialized at opening. */
	private double startTime = 0;
	
	/** The end time of the audio/video streams. Initialized at opening. */
	private double endTime = 0;
	
	/** The byte buffer for the audio. Initialized at opening. */
	private ByteBuffer audioBuffer = null;
	
	/** The audio format of the audio stream. Initialized at opening. */
	protected AudioFormat audioFormat = null;
	
	/** The color space of the image stream. Initialized at opening. */
	private ColorSpace colorSpace = null;
	
	/** Indicates that a image/audio stream is open. */
	private boolean isOpen = false;
	
	/**
	 * Find out if this movie stream contains an image stream.
     *
	 * @param streamId Identifier for this stream.
	 * @return True if there is an image stream; otherwise false.
	 */
	private static native boolean hasVideoStream0(int streamId);

    /**
     * Find out if this movie stream contains an image stream.
     *
     * @return True if there is an image stream; otherwise false.
     */
	public boolean hasVideoStream() {
	    return hasVideoStream0(streamId);
    }
	
	/**
	 * Find out if this movie stream contains an audio stream.
     *
	 * @param streamId Identifier for this stream.
	 * @return True if there is an audio stream; otherwise false.
	 */
	private static native boolean hasAudioStream0(int streamId);

    /**
     * Find out if this movie stream contains an audio stream.
     *
     * @return True if there is an audio stream; otherwise false.
     */
    public boolean hasAudioStream() {
	    return hasAudioStream0(streamId);
    }

	/**
	 * Native method to get the start time of the streams. Typically, this will 
	 * be 0 seconds.
     *
	 * @param streamId Identifier for this stream.
	 * @return The start time in seconds.
	 */
	private static native double getStartTime0(int streamId);
	
	/**
	 * Native method to get the end time of the streams.
	 *
     * @param streamId Identifier for this stream.
	 * @return The end time in seconds.
	 */
	private static native double getEndTime0(int streamId);
	
	/**
	 * Native method to get the duration of the streams.
	 *
     * @param streamId Identifier for this stream.
	 * @return The duration in seconds.
	 */
	private static native double getDuration0(int streamId);

    /**
     * Get the current time in the stream (either audio or video).
     *
     * @param streamId Identifier for this stream.
     * @return The current time in seconds.
     */
    private static native double getCurrentTime0(int streamId);


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
    public double getCurrentTime() {
        return getCurrentTime0(streamId);
    }

	/**
	 * Native method to set the time in the stream.
	 *
     * @param streamId Identifier of this stream.
	 * @param time The time in seconds.
	 */
	private static native void setTime0(int streamId, double time);

    /**
     * Set the time in the streams. This method is often called
     * to seek within the stream.
     *
     * @param time The time in seconds.
     */
	public void setTime(double time) {
	    setTime0(streamId, time);
    }

	/**
	 * Native method to set the play back speed as multiple of the native play
	 * back speed. E.g. 0.5x plays at half of the native speed.
     *
	 * @param streamId Identifier of this stream.
	 * @param speed The new play back speed. 
	 */
	private static native void setPlaybackSpeed0(int streamId, float speed);

    /**
     * Set the play back speed as multiple of the native play back speed.
     *
     * @param speed The new play back speed.
     */
	public void setPlaybackSpeed(float speed) {
	    setPlaybackSpeed0(streamId, speed);
    }
	
	@Override
	public void seek(double time) throws IndexOutOfBoundsException {
		if (time < getStartTime() || time > getEndTime()) {
			throw new IndexOutOfBoundsException("Time " + time + " is not in " 
					+ "range [" + getStartTime() + ", " + getEndTime() + "]");
		} else {
			setTime0(streamId, time);
		}
	}

	@Override
	public void setSpeed(float speed) throws IndexOutOfBoundsException {
		if (Math.abs(speed) < Math.ulp(1f)) {
			throw new IndexOutOfBoundsException("Speed " + speed + " is not allowed.");
		} else if (speed < MIN_SPEED || speed > MAX_SPEED) {
			throw new IndexOutOfBoundsException("Speed " + speed + "[" + MIN_SPEED + ", " + MAX_SPEED + "]");
		} else {
		    setPlaybackSpeed0(streamId, speed);
		}
	}

    private static native void reset0(int streamId);

	@Override
    public void reset() {
	    reset0(streamId);
    }

	/**
	 * Native method to close all the streams that this movie has.
	 *
     * @param streamId Identifier of this stream.
     *
	 * @throws IOException If errors appear during closing.
	 */
	private static native void close0(int streamId) throws IOException;
	
	@Override
	public void close() throws IOException {
		if (isOpen) {
			close0(streamId);
		}
		isOpen = false;
	}
	
	@Override
	public int getAudioBufferSize() {
		return AUDIO_BUFFER_SIZE;
	}
	
	private static native boolean availableAudioData0(int streamId);

    @Override
    public boolean availableAudioData() {
        return availableAudioData0(streamId);
    }

    private static native boolean availableImageFrame0(int streamId);

	@Override
	public boolean availableImageFrame() {
	    return availableImageFrame0(streamId);
    }

    private static native boolean loadNextAudioData0(int playerId);

	/**
	 * Native method to load the next audio data into the buffer.
	 * 
	 * @return True if data could be loaded; otherwise false.
	 */
	private boolean loadNextAudioData() {
	    return loadNextAudioData0(streamId);
    }

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
     * @param streamId Identifier of this movie stream.
	 * @param nByte The number of bytes that the buffered is initialized with.
	 * 
	 * @return An instance of the byte buffer.
	 */
	private static native ByteBuffer getAudioBuffer0(int streamId, int nByte);

    /**
     * Get an audio buffer that has been allocated for nByte bytes.
     *
     * @param nByte The size of the buffer in bytes.
     *
     * @return A newly allocated audio buffer.
     */
    public ByteBuffer getAudioBuffer(int nByte) {
        return getAudioBuffer0(streamId, nByte);
    }

    private static native String getSampleFormat0(int streamId);

    /**
	 * Get the sample format name.
	 * 
	 * @return The name of the sample format.
	 */
	private String getSampleFormat() {
	    return getSampleFormat0(streamId);
    }

    private static native String getCodecName0(int streamId);

	/**
	 * Get the codec name. Examples are: 'pcm_u8' and 'pcm_s8'.
	 * 
	 * @return The codec name.
	 */
	private String getCodecName() {
	    return getCodecName0(streamId);
    }

    private static native float getSampleRate0(int streamId);

	/**
	 * Get the sample rate of the audio stream.
	 * 
	 * @return The sample rate in Hertz.
	 */
	private float getSampleRate() {
	    return getSampleRate0(streamId);
    }

    private static native int getSampleSizeInBits0(int streamId);

	/**
	 * Get the sample size in bits.
	 * 
	 * @return The sample size in bits.
	 */
	private int getSampleSizeInBits() {
	    return getSampleSizeInBits0(streamId);
    }

    private static native int getNumberOfSoundChannels0(int streamId);

	/**
	 * Get the number of sound channels. For mono, this method returns 1. For 
	 * stereo this method returns 2. Notice, that more channels are possible 
	 * too, depending on the stream. E.g. surround sound can have five channels.
	 * 
	 * @return The number of sound channels.
	 */
	private int getNumberOfSoundChannels() {
	    return getNumberOfSoundChannels0(streamId);
    }

    private static native int getFrameSize0(int streamId);

	/**
	 * Get the frame size of audio frames.
	 * 
	 * @return The frame size in bytes.
	 */
	private int getFrameSize() {
	    return getFrameSize0(streamId);
    }

    private static native float getFrameRate0(int streamId);

	/**
	 * Get the frame rate of the audio stream.
	 * 
	 * @return The frame rate in Hertz.
	 */
	private float getFrameRate() {
	    return getFrameRate0(streamId);
    }

    private static native boolean bigEndian0(int streamId);

	/**
	 * Get the data encoding for the audio stream; either big or little endian.
	 * 
	 * @return True for big endian and false for little endian.
	 */
	private boolean bigEndian() {
	    return bigEndian0(streamId);
    }

    private static native boolean setPlaySound0(int streamId, boolean playSound);

	/**
	 * Set the boolean that determines whether sound should be played or not.
	 * 
	 * @param playSound If set to false no sound will be pulled if set to true 
	 * sound will pulled out of the stream.
	 * 
	 * @return The original state of playing before updating.
	 */
	public boolean setPlaySound(boolean playSound) {
	    return setPlaySound0(streamId, playSound);
    }

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
	 * @return The error code when opening this stream, 0 for no error and the streamId.
	 */
	private static native int[] open0(String fileName, String version, AudioFormat audioFormat);
	
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

		if (reqColorSpace != ColorSpace.getInstance(ColorSpace.CS_sRGB)) {
			throw new IOException("Color space " + reqColorSpace + " not supported!");
		}

		if (!(reqAudioFormat.getChannels()==1 && reqAudioFormat.getEncoding()==Encoding.PCM_SIGNED)
				&& !(reqAudioFormat.getChannels()==2 && reqAudioFormat.getEncoding()==Encoding.PCM_UNSIGNED)) {
			throw new IOException("Requested audio format " + reqAudioFormat 
					+ " not supported!");
		}

        int[] errNoAndStreamId = open0(fileName, version, reqAudioFormat);
		int errNo = errNoAndStreamId[0];

		if (errNo != 0) {
			throw new IOException("Error " + errNo + " occurred while opening " + fileName + ".");
		}

        streamId = errNoAndStreamId[1];
		// Get all the information about the video/audio and cache it
		colorSpace = reqColorSpace;
        startTime = getStartTime0(streamId);
        endTime = getEndTime0(streamId);
		duration = getDuration0(streamId);
		if (hasVideoStream()) {
			widthOfView = widthOfStream = getWidth0(streamId);
			heightOfView = heightOfStream = getHeight0(streamId);
			nChannels = getNumberOfColorChannels0(streamId);
		}
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
     * @param streamId Identifier of this stream.
	 * @return The number of color channels.
	 */
	private static native int getNumberOfColorChannels0(int streamId);

	/**
	 * Native method to get the height of the original images.
	 *
     * @param streamId Identifier of this stream.
	 * @return The height in pixels.
	 */
	private static native int getHeight0(int streamId);
	
	/**
	 * Native method to get the width of the original images.
	 *
     * @param streamId Identifier of tis stream.
	 * @return The width in pixels.
	 */
	private static native int getWidth0(int streamId);
	
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
	private static native boolean view0(int streamId, int x0, int y0, int width, int height);

	@Override
	public void setView(int x0, int y0, int width, int height) throws IndexOutOfBoundsException {
		if (x0 < 0 || x0+width > getWidthOfStream() 
				|| y0 < 0 || y0+height > getHeightOfStream()) {
			throw new IndexOutOfBoundsException("The viewing window [" + x0 
					+ ", " + x0+width + "] x [" + y0 + ", " + y0+height + "] "
					+ "is ouside the bounds [0, " + getWidthOfStream() + "] x [0, " 
					+ getHeightOfStream() + "]");
		} else {
			widthOfView = width;
			heightOfView = height;
			view0(streamId, x0, y0, width, height);
		}
	}
	
	private static native ByteBuffer getFrameBuffer0(int streamId);

    /**
     * Creates a byte buffer for the image frames with for the
     * width, height, and number of channels as given in the movie stream.
     *
     * When we change the view the size of this buffer will not change! We just
     * don't fill it up all the way.
     *
     * @return The instance of the byte buffer that has been created in the
     * 		   native code.
     */
	private ByteBuffer getFrameBuffer() {
	    return getFrameBuffer0(streamId);
    }

    private static native int loadNextImageFrame0(int streamId);

	/**
	 * Loads the next image frame into the frame buffer. This method blocks if
	 * there is no such frame available.
	 * 
	 * @return An integer number that defines the number of frames that were 
	 * 		   loaded. To fulfill the play back speed frames may be skipped. 
	 */
	private int loadNextImageFrame() {
	    return loadNextImageFrame0(streamId);
    }

	@Override
	public int readImageFrame(byte[] buffer) {
		int nFrame;
		// Check if we loaded at least one image frame
		if ((nFrame = loadNextImageFrame()) > 0) {
			// Load the image frame into the buffer
			ByteBuffer imageBuffer = getFrameBuffer();
			imageBuffer.get(buffer, 0, imageBuffer.capacity());
		}
		// Return the number of loaded image frames
		return nFrame;
	}
	
	/**
	 * This is an example on how to use the API. Only closes cleanly if the 
	 * video / audio has played until the end.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		// Create the movie stream
		final MovieStream movieStream = new MovieStream();
		String fileName = "C:\\Users\\Florian\\DatavyuSampleVideo.mp4";
		//String fileName = "C:\\Users\\Florian\\TurkishManGaitClip_KEATalk.mov";
		//String fileName = "C:\\Users\\Florian\\a2002011001-e02.wav";
		//String fileName = "C:\\Users\\Florian\\NoAudio\\TurkishCrawler_NoAudio.mov";
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
		} catch (IOException | LineUnavailableException io) {
            System.err.println(io.getLocalizedMessage());
        }
	}
}
