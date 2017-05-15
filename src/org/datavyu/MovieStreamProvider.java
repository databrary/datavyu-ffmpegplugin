package org.datavyu;

import java.awt.Canvas;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

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
				readAudioFrame(buffer); // blocks if no frame is available
				synchronized (audioListeners) {
					for (StreamListener listener : audioListeners) {
						listener.streamData(buffer);
					}					
				}
			}			
		}
	}
	
	class VideoListenerThread extends Thread {
		@Override
		public void run() {
			while (running) {
				byte[] buffer = new byte[getWidth()*getHeight()*getNumberOfColorChannels()];
				readImageFrame(buffer); // blocks if no frame is available
				synchronized (videoListeners) {
					for (StreamListener listener : videoListeners) {
						listener.streamData(buffer);
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
		audio = new AudioListenerThread();
		video = new VideoListenerThread();
		audio.start();
		video.start();
		for (StreamListener listener : audioListeners) {
			listener.streamOpened();
		}
		for (StreamListener listener : videoListeners) {
			listener.streamOpened();
		}
		running = true;
	}
	
	public void addAudioStreamListener(
			StreamListener streamListener) {
		synchronized (audioListeners) {
			audioListeners.add(streamListener);
		}
	}
	
	public void addVideoStreamListener(
			StreamListener streamListener) {
		synchronized (videoListeners) {
			videoListeners.add(streamListener);			
		}
	}
	
	@Override
	public void close() throws IOException {
		if (running) {
			try {
				audio.interrupt();
				video.interrupt();
				audio.join();
				video.join();				
				for (StreamListener listener : audioListeners) {
					listener.streamClosed();
				}
				for (StreamListener listener : videoListeners) {
					listener.streamClosed();
				}
				running = false;
				super.close();
			} catch (InterruptedException ie) {
				System.err.println("Could not close movie stream.");
			}
		}
	}
	
	public static void main(String[] args) {
		final MovieStreamProvider movieStreamProvider = new MovieStreamProvider();
		String fileName = "C:\\Users\\Florian\\WalkingVideo.mov";
		String version = "0.1.0.0";
		final ColorSpace reqColorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
		AudioFormat reqAudioFormat = AudioSound.MONO_FORMAT;
		int width = movieStreamProvider.getWidth();
		int height = movieStreamProvider.getHeight();
		final Frame f = new Frame();
        f.setBounds(0, 0, width, height);
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
        f.setVisible(true);		
		try {
			movieStreamProvider.open(fileName, version, reqColorSpace, reqAudioFormat);
			movieStreamProvider.setSpeed(1f);
			
			// Add the audio output
			movieStreamProvider.addAudioStreamListener(new StreamListener() {
				private SourceDataLine soundLine = null;				
				
				@Override
				public void streamOpened() {
					AudioFormat audioFormat = movieStreamProvider.getOutputAudioFormat();
					try {
						// Get the data line
						DataLine.Info info = new DataLine.Info(
								SourceDataLine.class, audioFormat);
						soundLine = (SourceDataLine) AudioSystem.getLine(info);			
						soundLine.open(audioFormat);
						soundLine.start();						
					} catch (LineUnavailableException lu) {
						System.err.println("Could not open line for audio "
								+ "format: " + audioFormat);
					}					
				}
				
				@Override
				public void streamData(byte[] data) {
					soundLine.write(data, 0, data.length);
				}
				
				@Override
				public void streamClosed() {
					soundLine.drain();
					soundLine.stop();
					soundLine.close();
				}
			});
			
			// Add the image output
			movieStreamProvider.addVideoStreamListener(new StreamListener() {
				private ComponentColorModel cm = null;
				private SampleModel sm = null;
				private Hashtable<String, String> properties = new Hashtable<String, String>();
				private BufferedImage image = null;
				private int nChannel = 0;
				private Canvas imageDisplay = null;
				
				@Override
				public void streamOpened() {
					int width = movieStreamProvider.getWidth();
					int height = movieStreamProvider.getHeight();
					nChannel = movieStreamProvider.getNumberOfColorChannels();
					cm = new ComponentColorModel(reqColorSpace, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
					sm = cm.createCompatibleSampleModel(width, height);
					// Initialize an empty image
					DataBufferByte dataBuffer = new DataBufferByte(new byte[width*height*nChannel], width*height);
					WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0,0));
					image = new BufferedImage(cm, raster, false, properties);
					imageDisplay = new Canvas() {
						private static final long serialVersionUID = 5471924216942753555L;

						@Override
			        	public void paint(Graphics g) {
			        		g.drawImage(image, 0, 0, null);
			        	}
						public void update(Graphics g){
						    paint(g);
						}			
			        };
					// Add canvas with the buffered image to the frame
			        f.add(imageDisplay);					
				}
				
				@Override
				public void streamData(byte[] data) {
					int width = movieStreamProvider.getWidth(); // width and height could have changed due to the view
					int height = movieStreamProvider.getHeight();
					data = new byte[width*height*nChannel];	// Allocate the bytes in java.
					DataBufferByte dataBuffer = new DataBufferByte(data, width*height); // Create data buffer.
					WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0, 0)); // Create writable raster.
					image = new BufferedImage(cm, raster, false, properties); // Create buffered image.
					imageDisplay.repaint();
				}
				
				@Override
				public void streamClosed() {
					// Anything to clean-up maybe show white image?
				}
			});
		} catch (IOException io) {
			
		}
	}
}
