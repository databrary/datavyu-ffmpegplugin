package org.datavyu;

import java.awt.color.ColorSpace;

public interface VideoStream extends TimeStream {
	
	public ColorSpace getColorSpace();
	
	public int getNumberOfColorChannels();
	
	public int getHeight();
	
	public int getWidth();
		
	public void setView(int x0, int y0, int width, int height) throws IndexOutOfBoundsException;
	
	// ensure the buffer has enough space!
	public int readImageFrame(byte[] buffer) throws IndexOutOfBoundsException; // reads next image
	
	public boolean availableImageFrame();
	
}
