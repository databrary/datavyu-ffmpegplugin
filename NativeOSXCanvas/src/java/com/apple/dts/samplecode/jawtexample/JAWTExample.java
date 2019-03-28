/*
     File: JAWTExample.java
 Abstract: Simple Java app that uses native drawing.
  Version: 2.0
 
 Disclaimer: IMPORTANT:  This Apple software is supplied to you by Apple
 Inc. ("Apple") in consideration of your agreement to the following
 terms, and your use, installation, modification or redistribution of
 this Apple software constitutes acceptance of these terms.  If you do
 not agree with these terms, please do not use, install, modify or
 redistribute this Apple software.
 
 In consideration of your agreement to abide by the following terms, and
 subject to these terms, Apple grants you a personal, non-exclusive
 license, under Apple's copyrights in this original Apple software (the
 "Apple Software"), to use, reproduce, modify and redistribute the Apple
 Software, with or without modifications, in source and/or binary forms;
 provided that if you redistribute the Apple Software in its entirety and
 without modifications, you must retain this notice and the following
 text and disclaimers in all such redistributions of the Apple Software.
 Neither the name, trademarks, service marks or logos of Apple Inc. may
 be used to endorse or promote products derived from the Apple Software
 without specific prior written permission from Apple.  Except as
 expressly stated in this notice, no other rights or licenses, express or
 implied, are granted by Apple herein, including but not limited to any
 patent rights that may be infringed by your derivative works or by other
 works in which the Apple Software may be incorporated.
 
 The Apple Software is provided by Apple on an "AS IS" basis.  APPLE
 MAKES NO WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
 THE IMPLIED WARRANTIES OF NON-INFRINGEMENT, MERCHANTABILITY AND FITNESS
 FOR A PARTICULAR PURPOSE, REGARDING THE APPLE SOFTWARE OR ITS USE AND
 OPERATION ALONE OR IN COMBINATION WITH YOUR PRODUCTS.
 
 IN NO EVENT SHALL APPLE BE LIABLE FOR ANY SPECIAL, INDIRECT, INCIDENTAL
 OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 INTERRUPTION) ARISING IN ANY WAY OUT OF THE USE, REPRODUCTION,
 MODIFICATION AND/OR DISTRIBUTION OF THE APPLE SOFTWARE, HOWEVER CAUSED
 AND WHETHER UNDER THEORY OF CONTRACT, TORT (INCLUDING NEGLIGENCE),
 STRICT LIABILITY OR OTHERWISE, EVEN IF APPLE HAS BEEN ADVISED OF THE
 POSSIBILITY OF SUCH DAMAGE.
 
 Copyright (C) 2011 Apple Inc. All Rights Reserved.
 
 */

package com.apple.dts.samplecode.jawtexample;

import java.awt.*;
import java.lang.*;

import javax.swing.JFrame;

public class JAWTExample {
    
    private static LayerBackedCanvas layerBackedCanvas;
    
	public static void main(final String args[]) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				final JAWTFrame frame = new JAWTFrame("JAWTExample");
				frame.setBackground(Color.white);
				frame.setLayout(new BorderLayout(10, 20));
				frame.setLocation(50, 50);
				frame.addNotify();
				frame.pack();
				frame.setVisible(true);
                
			}
		});
        
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                System.out.println("HEIGHT: " + layerBackedCanvas.getMovieHeight());
                System.out.println("WIDTH: " + layerBackedCanvas.getMovieWidth());
                System.out.println("DURATION: " + layerBackedCanvas.getDuration());
                System.out.println("FPS: " + layerBackedCanvas.getFPS());
                
            }
        });
        
        
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                layerBackedCanvas.setVolume(0f);
            }
        });
        
        
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                    layerBackedCanvas.play();
            }
        });
        
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            
        }
        
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                layerBackedCanvas.setTime(15000L);
                System.out.println(layerBackedCanvas.getCurrentTime());
            }
        });
        
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                layerBackedCanvas.release();
            }
        });
	}

	static class JAWTFrame extends JFrame {
		public JAWTFrame(final String title) {
			super(title);
		}
		
		public void addNotify() {
			super.addNotify(); // ensures native component hierarchy is setup
			
			// add the NSView-based drawing demo canvas
//			final NativeDrawnCanvas nativeDrawingCanvas = new NativeDrawnCanvas();
//			nativeDrawingCanvas.setPreferredSize(new Dimension(100, 100));
//			add(nativeDrawingCanvas, BorderLayout.NORTH);
			
			// add the CoreAnimation layer-backed demo canvas
			layerBackedCanvas = new LayerBackedCanvas();
			layerBackedCanvas.setPreferredSize(new Dimension(400, 200));
			add(layerBackedCanvas, BorderLayout.CENTER);
            
            System.out.println("CREATED STUFF");

			invalidate();
            
		}
	}
}