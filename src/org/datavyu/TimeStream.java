package org.datavyu;

import java.io.IOException;

public interface TimeStream {
	
	public double getStartTime();
	
	public double getEndTime();
	
	public double getDuration();
	
	public double getCurrentTime();
	
	public void seek(double time) throws IndexOutOfBoundsException; // set time to continue play back
	
	public void setSpeed(float speed) throws IndexOutOfBoundsException; // play back speed for the movie
	
	public void reset(); // rewinds
	
	public void close() throws IOException; // closes and frees resources
}
