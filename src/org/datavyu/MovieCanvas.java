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

public class MovieCanvas extends Canvas {
	
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
	 * 
	 * @param movieStream Assumes this is an open movie stream.
	 */
	public MovieCanvas(MovieStream movieStream) {
		this.movieStream = movieStream;
		cs = movieStream.getColorSpace();
		int width = movieStream.getWidth();
		int height = movieStream.getHeight();
		cm = new ComponentColorModel(cs, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
		nChannel = movieStream.getNumberOfColorChannels();	
		sm = cm.createCompatibleSampleModel(width, height);
		data = new byte[width*height*nChannel];	// Allocate the bytes in java.
		DataBufferByte dataBuffer = new DataBufferByte(data, width*height);
		WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0,0));
		image = new BufferedImage(cm, raster, false, properties); // Create buffered image.
		setBounds(0, 0, width, height);
	}
	
	public int showNextFrame() {
		int width = movieStream.getWidth(); // width and height could have changed due to the view
		int height = movieStream.getHeight();
		data = new byte[width*height*nChannel];	// Allocate the bytes in java.
		int nFrame = movieStream.readImageFrame(data);		
		DataBufferByte dataBuffer = new DataBufferByte(data, width*height); // Create data buffer.
		WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0, 0)); // Create writable raster.
		image = new BufferedImage(cm, raster, false, properties); // Create buffered image.
		repaint();
		return nFrame; // Return the number of frames.
	}	
	
	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, null);
	}
	
	public void update(Graphics g){
	    paint(g); // Instead of resetting, paint directly. 
	}	
}
