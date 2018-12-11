/*

 
 */

package com.apple.dts.samplecode.jawtexample;

import java.awt.Canvas;

public class LayerBackedCanvas extends Canvas {
	static {
		// Standard JNI: load the native library
		System.loadLibrary("JAWTExample");
	}

	public void addNotify() {
		super.addNotify();
		addNativeCoreAnimationLayer("file:///Users/jesse/Desktop/minecraft.mp4");
	}

	// This method is implemented in native code. See NativeCanvas.m
	public native void addNativeCoreAnimationLayer(String path);
    
    public native void stop();
    
    public native void play();
    
    public native void setTime(long time);
    
    public native void setVolume(float time);
    
    public native void release();
    
    public native double getMovieHeight();
    
    public native double getMovieWidth();
    
    public native long getCurrentTime();
    
    public native long getDuration();
    
    public native float getRate();
    
    public native boolean isPlaying();
    
    public native void setRate(float rate);
    
    public native float getFPS();
    
}