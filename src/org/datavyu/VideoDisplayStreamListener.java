package org.datavyu;

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
	
	/** The sample model */
	private SampleModel sm = null;
	
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
	
	/** The container to which we can add the image display */
	private Container container = null;
	
	/** Constraints for how to add to the container */
	private Object constraints = null;

	/**
	 * Creates a video display through a stream listener.
	 * 
	 * @param movieStream The underlying movie stream that provides data.
	 * @param container The container we add the image display.
	 * @param colorSpace The color space for the image data.
	 */
	public VideoDisplayStreamListener(MovieStream movieStream, 
			Container container, ColorSpace colorSpace) {
		this.movieStream = movieStream;
		this.container = container;
		this.constraints = null;
		this.colorSpace = colorSpace;
	}
	
	public VideoDisplayStreamListener(MovieStream movieStream, 
			Container container, Object constraints, ColorSpace colorSpace) {
		this.movieStream = movieStream;
		this.container = container;
		this.constraints = constraints;
		this.colorSpace = colorSpace;
	}

	@Override
	public void streamOpened() {
		int width = movieStream.getWidthOfView();
		int height = movieStream.getHeightOfView();
		nChannel = movieStream.getNumberOfColorChannels();
		cm = new ComponentColorModel(colorSpace, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
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
        if (constraints != null) {
        	container.add(imageDisplay, constraints);
        } else {
            container.add(imageDisplay);        	
        }
	}

	@Override
	public void streamData(byte[] data) {
		int width = movieStream.getWidthOfView(); // width and height could have changed due to the view
		int height = movieStream.getHeightOfView();
		DataBufferByte dataBuffer = new DataBufferByte(data, width*height); // Create data buffer.
		WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0, 0)); // Create writable raster.
		image = new BufferedImage(cm, raster, false, properties); // Create buffered image.
		//imageDisplay.repaint();
		container.repaint();
	}

	@Override
	public void streamClosed() { /* Nothing to do here */ }
	
	@Override
	public void streamStarted() {
		// display the current frame
		imageDisplay.repaint();	
	}
	
	@Override
	public void streamStopped() { /* Nothing to do here */ }
}
