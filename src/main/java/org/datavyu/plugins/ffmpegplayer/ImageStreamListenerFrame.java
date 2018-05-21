package org.datavyu.plugins.ffmpegplayer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.util.Hashtable;

/**
 * This class implements a stream listener for the video data. It receives byte data from the provider, which is
 * typically a MediaPlayer, and displays it as image.
 * 
 * @author Florian Raudies, Mountain View, CA.
 */
public class ImageStreamListenerFrame implements ImageStreamListener {
    private final static int INITIAL_NUM_CHANNEL = 3;
    private final static int INITIAL_WIDTH = 640;
    private final static int INITIAL_HEIGHT = 480;

    /** The logger for this class */
    private static Logger logger = LogManager.getFormatterLogger(ImageStreamListenerFrame.class);

	/** The color component model */
	private ComponentColorModel cm;
	
	/** The properties */
	private Hashtable<String, String> properties = new Hashtable<>();
	
	/** This is the original image that is been read from the native side */
	private BufferedImage image;

	private int width = INITIAL_WIDTH;

	private int height = INITIAL_HEIGHT;
	
	/** 
	 * The stream has doPaint = false no updates to the image should be made
	 * We had to introduce this flag because the java event manager is lacking 
	 * and another frame is displayed with considerable lag after the stopping 
	 * occurred triggered by the java event manager which is not real-time.
	 * 
	 * The idea of doPaint is that we prevent the java event manager from
	 * updating a frame that should not be displayed anymore because we doPaint
	 * the this video listener. 
	 */
	private boolean doPaint = false;

	private BufferStrategy strategy;

	private void updateDisplay() {
	    // See https://docs.oracle.com/javase/7/docs/api/java/awt/image/BufferStrategy.html
        // Render single frame
        do {
            // The following loop ensures that the contents of the drawing buffer
            // are consistent in case the underlying surface was recreated
            do {
                // Get a new graphics context every time through the loop
                // to make sure the strategy is validated
                Graphics graphics = strategy.getDrawGraphics();

                // Render to graphics
                // ...
                if (image != null && doPaint) {
                    graphics.drawImage(image, 0, 0, width, height,  null);
                }

                // Dispose the graphics
                graphics.dispose();

                // Repeat the rendering if the drawing buffer contents
                // were restored
            } while (strategy.contentsRestored());

            // Display the buffer
            strategy.show();

            // Repeat the rendering if the drawing buffer was lost
        } while (strategy.contentsLost());
    }
	
	/**
	 * Creates a video display through a stream listener. The display is added 
	 * to the container using the constraint.
	 *
     * @param frame The frame that is used for drawing the image on.
	 * @param colorSpace The color space for the image data.
	 */
	public ImageStreamListenerFrame(Frame frame, ColorSpace colorSpace) {
		this.cm = new ComponentColorModel(colorSpace, false, false, Transparency.OPAQUE,
				DataBuffer.TYPE_BYTE);
        // TODO: Change the implementation to the triple buffering in the Canvas and resize there directly
        // Set defaults
        SampleModel sm = cm.createCompatibleSampleModel(width, height);
        // Initialize an empty image
        DataBufferByte dataBuffer = new DataBufferByte(new byte[width* height *INITIAL_NUM_CHANNEL],
                width* height);
        WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0,0));
        // Create the original image
        image = new BufferedImage(cm, raster, false, properties);
        // Make the canvas visible
        frame.setBounds(0, 0, width, height);
        frame.setVisible(true);
        // Create a buffer strategy
        frame.createBufferStrategy(2);
        strategy = frame.getBufferStrategy();
    }

	@Override
	public void streamOpened() {
		// Paint the image
        doPaint = true;
        // Update the display with the image content
        updateDisplay();
	}

    @Override
    public void streamNewImageSize(int newWidth, int newHeight) {
        this.width = newWidth;
        this.height = newHeight;

        // TODO: Need to update the buffer strategy here
    }

    @Override
	public void streamData(byte[] data) {
        logger.debug("Received " + data.length + " By for image: " + width + " x " + height + " pixels.");
		SampleModel sm = cm.createCompatibleSampleModel(width, height);
		// Create data buffer
		DataBufferByte dataBuffer = new DataBufferByte(data, width*height);
		// Create writable raster
		WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0, 0));
		// Create the original image
        image = new BufferedImage(cm, raster, false, properties);
        // Paint the image
		updateDisplay();
	}

	@Override
	public void streamClosed() { 
		doPaint = false;
	}
	
	@Override
	public void streamStarted() {
		// play displaying
		doPaint = true;
		// display the current frame
		updateDisplay();
	}
	
	@Override
	public void streamStopped() {
		// stop displaying
		doPaint = false;
	}

	@SuppressWarnings("unused") // API method
	public void setScale(float newScale) {
        // We rescale the image if the scale is strongly update the image
        // TODO: Implement this differently
    }
}
