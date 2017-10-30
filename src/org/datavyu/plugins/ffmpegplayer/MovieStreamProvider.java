package org.datavyu.plugins.ffmpegplayer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.Frame;
import java.awt.color.ColorSpace;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.sound.sampled.AudioFormat;

/**
 * The movie stream provider allows feeding an audio stream to multiple listeners and feeding an image stream to
 * multiple listeners.
 * 
 * @author Florian Raudies, Mountain View, CA.
 *
 */
public class MovieStreamProvider extends MovieStream {

    /** The logger for this class */
    private static Logger logger = LogManager.getLogger(MovieStreamProvider.class);

	/** The list of audio listeners */
	private final List<StreamListener> audioListeners;
	
	/** The list of video listeners */
	private final List<StreamListener> videoListeners;
	
	/** Indicates that audio listeners are running */
	private boolean runAudio;
	
	/** Indicates that video listeners are running. */
	private boolean runVideo;
	
	/** This thread instance fulfills all audio play back */
	private Thread audio;
	
	/** This thread instance fulfills all video play back */
	private Thread video;

	/**
	 * This thread reads the binary audio data from the movie stream and forwards it to all listeners.
	 */
	class AudioListenerThread extends Thread {
		@Override
		public void run() {
			// Allocate the buffer for the audio data
			byte[] buffer = new byte[getAudioBufferSize()];
			// Start the play back loop
			while (runAudio) {				
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
	 * Consumes the next image frame without forwarding it to the listeners.
	 * 
	 * @return True if at least one frame was read; otherwise false. 
	 */
	boolean dropImageFrame() {
		int nFrame = 0;
		if (availableImageFrame()) {
			// Allocate space for a byte buffer
			byte[] buffer = new byte[getWidthOfView()*getHeightOfView()*getNumberOfColorChannels()];
			// Read the next image frame -- blocks if none is available
			nFrame = readImageFrame(buffer);
		}		
		return nFrame > 0;
	}
	
	/**
	 * Consumes the next image frame if there is one.
	 * 
	 * @return True if an image frame was consumed; otherwise false.
	 */
	boolean nextImageFrame() {
		if (availableImageFrame()) {
			// Allocate space for a byte buffer
			byte[] buffer = new byte[getWidthOfView()*getHeightOfView()*getNumberOfColorChannels()];
			// Read the next image frame -- blocks if none is available
			readImageFrame(buffer);
			// Fulfill all listeners
			synchronized (videoListeners) {
				for (StreamListener listener : videoListeners) {
					listener.streamData(buffer);
				}					
			}					
			return true;
		}
		return false;
	}

	/**
	 * This thread reads the binary video data from the movie stream and forwards it to all listeners.
	 */
	class VideoListenerThread extends Thread {
		@Override
		public void run() {
			// Start the play back loop
			while (runVideo) {
				// If there is image frame available
				if (!nextImageFrame()) {
					// Throttle this loop when we have no available data
					try { Thread.sleep(250); } catch (InterruptedException ie) {}					
				}
			}
		}
	}
	
	/**
	 * Creates a movie stream provider.
	 */
	MovieStreamProvider() {
		audioListeners = new LinkedList<>();
		videoListeners = new LinkedList<>();
		runAudio = false;
		runVideo = false;
	}
	
	@Override
	public void open(String fileName, String version, ColorSpace reqColorSpace, 
			AudioFormat reqAudioFormat) throws IOException {
		if (runVideo || runAudio) {
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
	
	/**
	 * Start playing audio.  Audio is only started if the movie has an audio 
	 * stream and audio is not playing already.  Calls stream started on all 
	 * audio listeners. Thread safe.
	 */
	void startAudio() {
		setPlaySound(true);
		if (hasAudioStream() && !runAudio) {
		    logger.info("StreamId " + getStreamId() + ": Starting audio play back thread.");
			runAudio = true;
			audio = new AudioListenerThread();
			synchronized (audioListeners) {
				for (StreamListener listener : audioListeners) {
					listener.streamStarted();
				}				
			}
			audio.start();
		}		
	}

	/**
	 * Start playing video images. Images are only played if the movie has a 
	 * video stream and is not playing already.  Calls stream started on all 
	 * video listeners. Thread safe.
	 */
	void startVideo() {
		if (hasVideoStream() && !runVideo) {
		    logger.info("StreamId "+ getStreamId() + ": Starting video play back thread.");
			runVideo = true;
			video = new VideoListenerThread();
			synchronized (videoListeners) {
				for (StreamListener listener : videoListeners) {
					listener.streamStarted();
				}		
			}
			video.start();
		}
	}
	
	/**
	 * Starts the video listener to enable updates to all registered stream 
	 * listeners for the video.
	 * 
	 * This is used to enable stepping when not using the internal video player
	 * thread that pulls and displays images.
	 */
	void startVideoListeners() {
		synchronized (videoListeners) {
			for (StreamListener listener : videoListeners) {
				listener.streamStarted();
			}		
		}		
	}
	
	/**
	 * Start the audio and video playing if there is an audio stream and a video 
	 * stream.  Calls stream started on all stream listeners. Thread safe.
	 */
	public void start() {
		startAudio();
		startVideo();
	}
	
	/**
	 * Stops playing the video if a video is running and there is a video 
	 * stream.  Calls stream stopped on all video listeners.
	 */
	private void stopVideo() {
		if (runVideo && hasVideoStream()) {
			runVideo = false;
			if (video != null) {
				video.interrupt();
				try {
					video.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}				
			}
			synchronized (videoListeners) {
				for (StreamListener listener : videoListeners) {
					listener.streamStopped();
				}
			}
		}		
	}
	
	/**
	 * Stops playing the audio if a audio is running and there is an audio stream.  Calls stream stopped on all audio
     * listeners.
	 */
	void stopAudio() {
		setPlaySound(false);
		if (runAudio && hasAudioStream()) {
			runAudio = false;
			if (audio != null) {
				audio.interrupt();
				try {
					audio.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}				
				synchronized (audioListeners) {
					for (StreamListener listener : audioListeners) {
						listener.streamStopped();
					}
				}					
			}			
		}		
	}
	
	/**
	 * Stops playing the video if a video is running and there is a video 
	 * stream.  Calls stream stopped on all video listeners. 
	 */
	public void stop() {
		stopVideo();
		stopAudio();
	}	
	
	/**
	 * Adds an audio stream listener. If this stream provider is already running
	 * the added listener is opened immediately after being added and before any
	 * data is fed.
	 * 
	 * @param streamListener The stream listener that is added.
	 */
	void addAudioStreamListener(StreamListener streamListener) {
		// A lock on the list of audio listeners to avoid concurrent access with the thread that is feeding data
		synchronized (audioListeners) {
			// Add the listener to the list
			audioListeners.add(streamListener);
			// If this stream provider is already running open the listener
			if (runAudio) {
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
	void addVideoStreamListener(StreamListener streamListener) {
		// A lock on the list of video listeners to avoid concurrent access with the thread that is feeding data
		synchronized (videoListeners) {
			// Add the listener to the list
			videoListeners.add(streamListener);
			// If this stream provider is already running open the listener
			if (runVideo) {
				streamListener.streamOpened();
				streamListener.streamStarted();
			}
		}
	}
	
	@Override
	public void close() throws IOException {
		stop();
		super.close();
	}

	/**
	 * Only intended for testing in the main method.
	 */
	private final static class MoviePlayer {
        MovieStreamProvider movieStreamProvider;
        final Frame frame;

        public MoviePlayer(String movieFileName) throws IOException {
            this(movieFileName, "0.0.0.1");
        }

        public MoviePlayer(String movieFileName, String version) throws IOException {
            movieStreamProvider = new MovieStreamProvider();
            final ColorSpace reqColorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
            AudioFormat reqAudioFormat = AudioSound.getNewMonoFormat();
            frame = new Frame();
            frame.addWindowListener( new WindowAdapter() {
                public void windowClosing(WindowEvent ev) {
                    try {
                        movieStreamProvider.close();
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                    frame.setVisible(false);
                }
            } );
            // Add the audio sound listener
            movieStreamProvider.addAudioStreamListener(new AudioSoundStreamListener(movieStreamProvider));
            // Add video display
            movieStreamProvider.addVideoStreamListener(new VideoDisplayStreamListener(movieStreamProvider, frame,
                    reqColorSpace));
            // Open the movie stream provider
            movieStreamProvider.open(movieFileName, version, reqColorSpace, reqAudioFormat);
            movieStreamProvider.start();
            int width = movieStreamProvider.getWidthOfView();
            int height = movieStreamProvider.getHeightOfView();
            frame.setBounds(0, 0, width, height);
            frame.setVisible(true);
        }
    }
	
	public static void main(String[] args) {
        String folderName = "C:\\Users\\Florian";
        List<String> fileNames = Arrays.asList(new String[]{"DatavyuSampleVideo.mp4", "TurkishManGaitClip_KEATalk.mov"});
        for (String fileName : fileNames) {
            try {
                new MoviePlayer(new File(folderName, fileName).toString());
            } catch (IOException io) {
                System.err.println(io);
            }
        }
	}
}
