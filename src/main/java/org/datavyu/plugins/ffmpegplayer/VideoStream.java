package org.datavyu.plugins.ffmpegplayer;

import java.awt.color.ColorSpace;

/**
 * A video stream extends a time stream by providing bytes in chunks of image
 * frames in certain color model. Image frames are supplied at the speed as set
 * through the time stream.
 *
 * @author Florian Raudies, Mountain View, CA.
 */
public interface VideoStream extends TimeStream {

    /**
     * Get the color space of this video stream. This allows us to interpret the
     * data from the readImageData method correctly.
     *
     * @return ColorSpace of this image stream.
     */
    ColorSpace getColorSpace();

    /**
     * Get the number of color channels.
     *
     * @return The number of color channels.
     */
    int getNumberOfColorChannels();

    /**
     * Get the width of the images in the video stream.
     *
     * @return The width of the image in pixels.
     */
    int getWidth();

    /**
     * Get the height of the image in the video stream.
     *
     * @return The height of the image in pixels.
     */
    int getHeight();

	/**
	 * Reads the next image frame from the stream. Blocks if there is now such
	 * image frame.
	 *
	 * @param buffer The byte buffer that will hold the image frame. Notice that
	 * 		  this method ASSUMES that this buffer has been allocated and is at
	 * 		  least width x height x (number of channels) bytes.
	 *
	 * @return The number of image frames that were read in the underlying
	 * 		   stream. If we cannot display frames fast enough and we have a
	 * 		   long lag this method may drop frames from the underlying stream
	 * 		   to catch up with the set speed. For instance, if the method
	 * 		   returns 2 that means that one frame was skipped. If the method returns
	 * 		   0 that means that no frames were read.
	 */
	int readImageData(byte[] buffer); // reads next image
}
