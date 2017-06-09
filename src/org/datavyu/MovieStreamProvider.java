package org.datavyu;

import java.awt.Frame;
import java.awt.color.ColorSpace;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.sound.sampled.AudioFormat;

public class MovieStreamProvider extends MovieStream {
	private List<StreamListener> audioListeners;
	private List<StreamListener> videoListeners;
	private boolean running;
	private Thread audio;
	private Thread video;
	
	class AudioListenerThread extends Thread {
		@Override
		public void run() {
			byte[] buffer = new byte[getAudioBufferSize()];
			while (running) {
				if (availableAudioData()) {
					readAudioData(buffer); // blocks if no frame is available
					synchronized (audioListeners) {
						for (StreamListener listener : audioListeners) {
							listener.streamData(buffer);
						}					
					}
				}
			}			
		}
	}
	
	class VideoListenerThread extends Thread {
		@Override
		public void run() {
			while (running) {
				if (availableImageFrame()) {
					byte[] buffer = new byte[getWidthOfView()*getHeightOfView()
					                         *getNumberOfColorChannels()];
					readImageFrame(buffer); // blocks if no frame is available
					synchronized (videoListeners) {
						for (StreamListener listener : videoListeners) {
							listener.streamData(buffer);
						}					
					}					
				}
			}
		}
	}
	
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
			audio = new AudioListenerThread();
			for (StreamListener listener : audioListeners) {
				listener.streamOpened();
			}
		}
		if (hasVideoStream()) {
			video = new VideoListenerThread();
			for (StreamListener listener : videoListeners) {
				listener.streamOpened();
			}
		}
		if (hasAudioStream()) { audio.start(); }
		if (hasVideoStream()) { video.start(); }			
		running = true;
	}
	
	public void addAudioStreamListener(StreamListener streamListener) {
		synchronized (audioListeners) {
			audioListeners.add(streamListener);
		}
	}
	
	public void addVideoStreamListener(StreamListener streamListener) {
		synchronized (videoListeners) {
			videoListeners.add(streamListener);			
		}
	}
	
	@Override
	public void close() throws IOException {
		System.out.println("Calling close.");
		if (running) {
			try {
				running = false;
				System.out.println("Stop running.");
				if (hasAudioStream()) { audio.interrupt(); }
				if (hasVideoStream()) { video.interrupt(); }
				if (hasAudioStream()) {
					for (StreamListener listener : audioListeners) {
						listener.streamClosed();
					}					
					System.out.println("Joining audio thread.");
					audio.join();
				}
				if (hasVideoStream()) {
					for (StreamListener listener : videoListeners) {
						listener.streamClosed();
					}					
					System.out.println("Joining video thread.");
					video.join();
				}
				super.close();
			} catch (InterruptedException ie) {
				System.err.println("Could not close movie stream.");
			}
		}
	}
	
	public static void main(String[] args) {
		final MovieStreamProvider movieStreamProvider = new MovieStreamProvider();
		//String fileName = "C:\\Users\\Florian\\TurkishManGaitClip_KEATalk.mov";
		String fileName = "C:\\Users\\Florian\\a2002011001-e02.wav";
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
			int width = movieStreamProvider.getWidthOfView();
			int height = movieStreamProvider.getHeightOfView();
	        f.setBounds(0, 0, width, height);
	        f.setVisible(true);
		} catch (IOException io) {
			io.printStackTrace();
		}
	}
}
