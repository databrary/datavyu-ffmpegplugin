package org.datavyu;

import java.awt.Frame;
import java.awt.color.ColorSpace;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.sound.sampled.AudioFormat;

/**
 * The movie stream provider allows for the feeding of an audio stream to 
 * multiple listeners as well as the feeding of an video stream to multiple
 * listeners. 
 * 
 * @author Florian Raudies, Mountain View, CA.
 *
 */
public class MovieStreamProvider extends MovieStream {
	
	/** The list of audio listeners */
	private List<StreamListener> audioListeners;
	
	/** The list of video listeners */
	private List<StreamListener> videoListeners;
	
	/** Indicates that BOTH the audio and video listener are running */
	private boolean running;
	
	/** This thread instance fulfills all audio play back */
	private Thread audio;
	
	/** This thread instance fulfills all video play back */
	private Thread video;

	/**
	 * This thread reads the binary audio data from the movie stream and 
	 * forwards it to all listeners.
	 */
	class AudioListenerThread extends Thread {
		@Override
		public void run() {
			// Allocate the buffer for the audio data
			byte[] buffer = new byte[getAudioBufferSize()];
			// Start the play back loop
			while (running) {
				// If there is audio data available
				if (availableAudioData()) {
					// Read audio data -- blocks if none is available
					readAudioData(buffer);
					// Fulfill all listeners
					// This is lock allows to add listeners
					synchronized (audioListeners) {
						// For a listeners forward this data
						for (StreamListener listener : audioListeners) {
							listener.streamData(buffer);
						}					
					}
				} else {
					// Throttle this loop when we have no available data
					try { Thread.sleep(250); } catch (InterruptedException ie) {}
				}
			}
		}
	}

	/**
	 * This thread reads the binary video data from the movie stream and
	 * forwards it to all listeners.
	 */
	class VideoListenerThread extends Thread {
		@Override
		public void run() {
			// Start the play back loop
			while (running) {
				// If there is image frame available
				if (availableImageFrame()) {
					// Allocate space for a byte buffer
					byte[] buffer = new byte[getWidthOfView()*getHeightOfView()
					                         *getNumberOfColorChannels()];
					// Read the next image frame -- blocks if none is available
					readImageFrame(buffer);
					// Fulfill all listeners
					synchronized (videoListeners) {
						for (StreamListener listener : videoListeners) {
							listener.streamData(buffer);
						}					
					}					
				} else {
					// Throttle this loop when we have no available data
					try { Thread.sleep(250); } catch (InterruptedException ie) {}					
				}
			}
		}
	}
	
	/**
	 * Creates a movie stream provider.
	 */
	public MovieStreamProvider() {
		audioListeners = new LinkedList<StreamListener>();
		videoListeners = new LinkedList<StreamListener>();
		running = false;
	}
	
	@Override
	public void open(String fileName, String version, ColorSpace reqColorSpace, 
			AudioFormat reqAudioFormat) throws IOException {
		if (running) {
			close();
		}
		super.open(fileName, version, reqColorSpace, reqAudioFormat);
		if (hasAudioStream()) {
			for (StreamListener listener : audioListeners) {
				listener.streamOpened();
			}
		}
		if (hasVideoStream()) {
			for (StreamListener listener : videoListeners) {
				listener.streamOpened();
			}
		}
	}
	
	public void start() {
		if (hasAudioStream()) {
			audio = new AudioListenerThread();
			synchronized (audioListeners) {
				for (StreamListener listener : audioListeners) {
					listener.streamStarted();
				}				
			}
		}
		if (hasVideoStream()) {
			video = new VideoListenerThread();
			synchronized (videoListeners) {
				for (StreamListener listener : videoListeners) {
					listener.streamStarted();
				}		
			}
		}
		running = true;
		if (hasAudioStream()) { audio.start(); }
		if (hasVideoStream()) { video.start(); }		
	}
	
	public void stop() {
		running = false;
		if (hasAudioStream()) {
			audio.interrupt();
			try {
				audio.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			synchronized (audioListeners) {
				for (StreamListener listener : audioListeners) {
					listener.streamStopped();
				}
			}
		}
		if (hasVideoStream()) {
			video.interrupt();
			try {
				video.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			synchronized (videoListeners) {
				for (StreamListener listener : videoListeners) {
					listener.streamStopped();
				}
			}
		}
	}
	
	/**
	 * Adds an audio stream listener. If this stream provider is already running
	 * the added listener is opened immediately after being added and before any
	 * data is fed.
	 * 
	 * @param streamListener The stream listener that is added.
	 */
	public void addAudioStreamListener(StreamListener streamListener) {
		// A lock on the list of audio listeners to avoid concurrent access with 
		// the thread that is feeding data
		synchronized (audioListeners) {
			// Add the listener to the list
			audioListeners.add(streamListener);
			// If this stream provider is already running open the listener
			if (running) {
				streamListener.streamOpened();
				streamListener.streamStarted();
			}
		}
	}
	
	/**
	 * Adds a video stream listener. If this stream provider is already running
	 * the added listener is opened immediately after being added and before any
	 * data is fed.
	 * 
	 * @param streamListener The stream listener that is added.
	 */
	public void addVideoStreamListener(StreamListener streamListener) {
		// A lock on the list of video listeners to avoid concurrent access with
		// the thread that is feeding data
		synchronized (videoListeners) {
			// Add the listener to the list
			videoListeners.add(streamListener);
			// If this stream provider is already running open the listener
			if (running) {
				streamListener.streamOpened();
				streamListener.streamStarted();
			}
		}
	}
	
	@Override
	public void close() throws IOException {
		if (running) {
			stop();
		}
		super.close();
	}
	
	public static void main(String[] args) {
		final MovieStreamProvider movieStreamProvider = new MovieStreamProvider();
		String fileName = "C:\\Users\\Florian\\TurkishManGaitClip_KEATalk.mov";
		//String fileName = "C:\\Users\\Florian\\a2002011001-e02.wav";
		//String fileName = "C:\\Users\\Florian\\NoAudio\\TurkishCrawler_NoAudio.mov";
		String version = "0.1.0.0";
		final ColorSpace reqColorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
		AudioFormat reqAudioFormat = AudioSound.MONO_FORMAT;
		final Frame f = new Frame();
        f.addWindowListener( new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
            	try {
            		movieStreamProvider.close();
				} catch (IOException io) {
					io.printStackTrace();
				}
                System.exit(0);
            }
        } );
		try {			
			// Add the audio sound listener
			movieStreamProvider.addAudioStreamListener(
					new AudioSoundStreamListener(movieStreamProvider));
			// Add video display
			movieStreamProvider.addVideoStreamListener(
					new VideoDisplayStreamListener(movieStreamProvider, f, reqColorSpace));
			// Open the movie stream provider
			movieStreamProvider.open(fileName, version, reqColorSpace, reqAudioFormat);
			movieStreamProvider.start();
			int width = movieStreamProvider.getWidthOfView();
			int height = movieStreamProvider.getHeightOfView();
	        f.setBounds(0, 0, width, height);
	        f.setVisible(true);
		} catch (IOException io) {
			io.printStackTrace();
		}
	}
}
