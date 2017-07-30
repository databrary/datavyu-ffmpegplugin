package org.datavyu.plugins.ffmpegplayer;

import java.awt.Canvas;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

/**
 * This class implements a stream listener for the video data. It receives
 * the byte data from the provider typically a MoieStream and displays it as 
 * image.
 * 
 * @author Florian Raudies, Mountain View, CA.
 */
public class VideoDisplayStreamListener implements StreamListener {
	
	/** The color component model */
	private ComponentColorModel cm = null;
	
	/** The properties */
	private Hashtable<String, String> properties = new Hashtable<String, String>();
	
	/** The buffered image to display*/
	private BufferedImage image = null;
	
	/** The number of channels of the image */
	private int nChannel = 0;
	
	/** The canvas that we draw the image in */
	private Canvas imageDisplay = null;
	
	/** The movie stream to get width, height, channel info */
	private MovieStream movieStream = null;
	
	/** The color space for this the data provided */
	private ColorSpace colorSpace = null;
	
	/** 
	 * The stream has stopped and no updates to the image should be made 
	 * We had to introduce this flag because the java event manager is lacking 
	 * and another frame is displayed with considerable lag after the stopping 
	 * occurred triggered by the java event manager which is not real-time.
	 * 
	 * The idea of stopped is that we prevent the java event manager from 
	 * updating a frame that should not be displayed anymore because we stopped
	 * the this video listener. 
	 */
	private boolean stopped = true;
	
	/**
	 * Initialized the image display and adds it to the supplied container with 
	 * the supplied constraint. If the constraint is null it uses the add method
	 * on the container without constraint.
	 * 
	 * @param container The container that the display is added to
	 * @param constraint The constraint used when adding
	 */
	private void initImageDisplay(Container container, Object constraints) {
		imageDisplay = new Canvas() {
			private static final long serialVersionUID = 5471924216942753555L;

			@Override
        	public void paint(Graphics g) {
				// If not stopped display the next image
				if (!stopped) {
	        		g.drawImage(image, 0, 0, null);					
				}
        	}
			public void update(Graphics g){
			    paint(g);
			}
        };
        if (constraints != null) {
        	container.add(imageDisplay, constraints);
        } else {
            container.add(imageDisplay);        	
        }		
	}

	/**
	 * Creates a video display through a stream listener. The display is added
	 * to the container.
	 * 
	 * @param movieStream The underlying movie stream that provides data.
	 * @param container The container we add the image display.
	 * @param colorSpace The color space for the image data.
	 */
	public VideoDisplayStreamListener(MovieStream movieStream, 
			Container container, ColorSpace colorSpace) {
		this(movieStream, container, null, colorSpace);
	}
	
	/**
	 * Creates a video display through a stream listener. The display is added 
	 * to the container using the constraint.
	 * 
	 * @param movieStream
	 * @param container
	 * @param constraints
	 * @param colorSpace
	 */
	public VideoDisplayStreamListener(MovieStream movieStream, 
			Container container, Object constraints, ColorSpace colorSpace) {
		this.movieStream = movieStream;
		this.colorSpace = colorSpace;
		initImageDisplay(container, constraints);
	}

	@Override
	public void streamOpened() {
		int width = movieStream.getWidthOfView();
		int height = movieStream.getHeightOfView();		
		nChannel = movieStream.getNumberOfColorChannels();
		cm = new ComponentColorModel(colorSpace, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
		SampleModel sm = cm.createCompatibleSampleModel(width, height);
		// Initialize an empty image
		DataBufferByte dataBuffer = new DataBufferByte(new byte[width*height*nChannel], width*height);
		WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0,0));
		image = new BufferedImage(cm, raster, false, properties);
        stopped = false; // display
	}

	@Override
	public void streamData(byte[] data) {
		// Width and height could have changed due to the view
		int width = movieStream.getWidthOfView(); 
		int height = movieStream.getHeightOfView();
		SampleModel sm = cm.createCompatibleSampleModel(width, height);
		DataBufferByte dataBuffer = new DataBufferByte(data, width*height); // Create data buffer.
		WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0, 0)); // Create writable raster.
		image = new BufferedImage(cm, raster, false, properties); // Create buffered image.
		imageDisplay.repaint();
	}

	@Override
	public void streamClosed() { 
		stopped = true;
	}
	
	@Override
	public void streamStarted() {
		stopped = false; // start displaying
		imageDisplay.repaint(); // display the current frame
	}
	
	@Override
	public void streamStopped() {
		stopped = true; // stop displaying
	}
}
