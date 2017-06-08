package org.datavyu;

import java.awt.Canvas;
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
 * Interfaces the movie stream with the BufferedImage in java.awt package. 
 * 
 * @author Florian Raudies, Mountain View, CA.
 *
 */
public class VideoDisplay extends Canvas {
	
	/** RGB sample model. */
	protected ColorSpace cs;
	
	/** Samples components without transparency using a byte format. */
	protected ComponentColorModel cm;
	
	/** These properties are used to create the buffered image. */
	protected Hashtable<String, String> properties = 
			new Hashtable<String, String>();
	
	/** The number of channels, typically 3. */
	protected int nChannel = 0;
	
	/** This image buffer holds the image. */
	protected BufferedImage image = null;
	
	/** A copy of the raw data used to be wrapped by the data byte buffer. */
	protected byte[] data = null;
	
	/** Used to buffer the image. */
	protected DataBufferByte dataBuffer = null;
	
	/** Used to create the buffered image. */
	protected SampleModel sm = null;
	
	/** Movie stream that backs this canvas. */
	protected MovieStream movieStream = null;
	
	/** Unique id for serialization of this class. */
	private static final long serialVersionUID = 8365021112734430014L;

	/**
	 * Create a video display given a OPEN movie stream. We need this stream to
	 * be open to get the width, height, and number of channels to allocate the
	 * internal buffers correctly. 
	 * 
	 * @param movieStream ASSUMES this is an OPEN movie stream.
	 */
	public VideoDisplay(MovieStream movieStream) {
		// Set the movie stream
		this.movieStream = movieStream;
		// Get the color space from the movie stream
		cs = movieStream.getColorSpace();
		// Get the width, height, number of channels in the stream
		int width = movieStream.getWidth();
		int height = movieStream.getHeight();
		nChannel = movieStream.getNumberOfColorChannels();	
		// Construct the component model that interprets bytes in the channels
		cm = new ComponentColorModel(cs, false, false, Transparency.OPAQUE, 
				DataBuffer.TYPE_BYTE);
		// Create a sampling model for the given width and height of the image
		sm = cm.createCompatibleSampleModel(width, height);
		// Allocate the data buffer for the width, height, channels in bytes
		data = new byte[width*height*nChannel];	
		// Wrap the byte buffer in a data buffer
		DataBufferByte dataBuffer = new DataBufferByte(data, width*height);
		// Use the byte buffer in a writable raster for the buffered image
		WritableRaster raster = WritableRaster.createWritableRaster(sm, 
				dataBuffer, new Point(0,0));
		// Create the buffered image with the color model and raster
		image = new BufferedImage(cm, raster, false, properties);
		// Set the bounds for this canvas according to the width and height
		setBounds(0, 0, width, height);
	}
	
	/**
	 * Show the next image frame on this canvas.
	 * 
	 * @return The number of frames, typically one, but this could be larger 
	 * than one if we skipped a frame.
	 */
	public int showNextFrame() {
		// Get the width and height which could have changed due to the view
		int width = movieStream.getWidth(); 
		int height = movieStream.getHeight();
		// Re-allocate data buffer for the new width and height
		data = new byte[width*height*nChannel];
		// Read the data from the movie stream
		int nFrame = movieStream.readImageFrame(data);		
		// Create a data buffer for the new width and height with the data
		DataBufferByte dataBuffer = new DataBufferByte(data, width*height);
		// Create the writable raster that wraps the new data buffer
		WritableRaster raster = WritableRaster.createWritableRaster(sm, 
				dataBuffer, new Point(0, 0));
		// Create the buffered image for the new raster
		image = new BufferedImage(cm, raster, false, properties);
		// Trigger a re-paint of the image in the canvas
		repaint();
		 // Return the number of frames
		return nFrame;
	}	
	
	/**
	 * Override of the paint method in canvas that draws the image at (0, 0).
	 */
	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, null);
	}
	
	/**
	 * Override the update method in canvas. Here we paint directly to avoid 
	 * flickering.
	 */
	public void update(Graphics g){
	    paint(g);
	}	
}
