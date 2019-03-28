/*
     File: DrawingCanvas.m
 Abstract: Native code that draws into an AWT Canvas.
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

#import <Cocoa/Cocoa.h>
#import <jawt_md.h>
#import <JavaNativeFoundation/JavaNativeFoundation.h>

#include "com_apple_dts_samplecode_jawtexample_NativeDrawnCanvas.h"


/*
 * Class:     com_apple_dts_samplecode_jawtexample_NativeDrawnCanvas
 * Method:    nativePaintOnCanvas
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_apple_dts_samplecode_jawtexample_NativeDrawnCanvas_nativePaintOnCanvas
(JNIEnv *env, jobject canvas) {
JNF_COCOA_ENTER(env);
	
	// get the AWT
	JAWT awt;
	awt.version = JAWT_VERSION_1_4;
	jboolean result = JAWT_GetAWT(env, &awt);
	JNF_CHECK_AND_RETHROW_EXCEPTION(env);
	if (result == JNI_FALSE) return; // NSView access unsupported in future versions of Java for Mac OS X

	// get the drawing surface
	JAWT_DrawingSurface *ds = awt.GetDrawingSurface(env, canvas);
	JNF_CHECK_AND_RETHROW_EXCEPTION(env);
	assert(ds != NULL);

	// lock the drawing surface - must lock EACH TIME before drawing
	jint lock = ds->Lock(ds); 
	JNF_CHECK_AND_RETHROW_EXCEPTION(env);
	assert((lock & JAWT_LOCK_ERROR) == 0);

	// get the drawing surface info
	JAWT_DrawingSurfaceInfo *dsi = ds->GetDrawingSurfaceInfo(ds);
	JNF_CHECK_AND_RETHROW_EXCEPTION(env);

//	// Check DrawingSurfaceInfo. This can be NULL on Mac OS X if the native 
//	// component heirachy has not been made visible yet on the AppKit thread.
//	if (dsi != NULL) {
//		// Get the platform-specific drawing info
//		// This is used to get at Cocoa and CoreGraphics
//		// See <JavaVM/jawt_md.h>
//		JAWT_MacOSXDrawingSurfaceInfo *dsi_mac = (JAWT_MacOSXDrawingSurfaceInfo*)dsi->platformInfo;
//		
//		// This drawing does not stictly need to be done on the AppKit thread,
//		// but concurrent AppKit drawing may produce unexpected results as this
//		// Java thread and the AppKit thread race each other.
//		{
//			// Get the corresponding peer from the caller canvas
//			NSView *view = dsi_mac->cocoaViewRef;
//			NSWindow *window = [view window];
//			
//			// Get the CoreGraphics context from the videoController window - DO NOT CACHE
//			CGContextRef cg = [[NSGraphicsContext graphicsContextWithWindow:window] graphicsPort];
//			
//			// Match Java's ctm
//			NSRect windowRect = [[window contentView] frame];
//			CGContextConcatCTM(cg, CGAffineTransformMake(1, 0, 0, -1, dsi->bounds.x, windowRect.size.height - dsi->bounds.y));
//			
//			// Draw a pattern using CoreGraphics
//			CGContextSetRGBFillColor(cg, 1.0f, 0.0f, 0.0f, 1.0f);
//			CGContextFillRect(cg, CGRectMake(15, 15, 70, 70));
//		}
//			
//		// Free the DrawingSurfaceInfo
//		ds->FreeDrawingSurfaceInfo(dsi);
//		JNF_CHECK_AND_RETHROW_EXCEPTION(env);
//	}

	// Unlock the drawing surface - must unlock EACH TIME when done drawing
	ds->Unlock(ds); 
	JNF_CHECK_AND_RETHROW_EXCEPTION(env);

	// Free the drawing surface (if not caching it)
	awt.FreeDrawingSurface(ds);
	JNF_CHECK_AND_RETHROW_EXCEPTION(env);
	
JNF_COCOA_EXIT(env);
}
