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
public class ImageStreamListenerContainer implements ImageStreamListener {
    private final static int INITIAL_NUM_CHANNEL = 3;
    private final static int INITIAL_WIDTH = 640;
    private final static int INITIAL_HEIGHT = 480;

    /** The logger for this class */
    private static Logger logger = LogManager.getFormatterLogger(ImageStreamListenerContainer.class);

	/** The color component model */
	private ComponentColorModel cm;
	
	/** The properties */
	private Hashtable<String, String> properties = new Hashtable<>();
	
	/** This is the original image that is been read from the native side */
	private BufferedImage image = null;

	/** The canvas that we draw the image in */
	private Canvas canvas = null;

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
	
	/**
	 * Initialized the image display and adds it to the supplied container with
	 * the supplied constraint. If the constraint is null it uses the add method
	 * on the container without constraint.
	 * 
	 * @param container The container that the display is added to
	 * @param constraints The constraint used when adding
	 */
	private void initImageDisplay(Container container, Object constraints) {
		canvas = new Canvas() {
			private static final long serialVersionUID = 5471924216942753555L;

			@Override
        	public void paint(Graphics g) {
				// If not doPaint display the next image
				if (doPaint) {
	        		g.drawImage(image, 0, 0, null);
				}
        	}
			public void update(Graphics g){
			    paint(g);
			}
		};
        if (constraints != null) {
        	container.add(canvas, constraints);
        } else {
            container.add(canvas);
        }		
	}
	
	/**
	 * Creates a video display through a stream listener. The display is added 
	 * to the container using the constraint.
	 *
	 * @param container The container we add the image display.
	 * @param constraints Constraint where to add this video display into the container.
	 * @param colorSpace The color space for the image data.
	 */
	public ImageStreamListenerContainer(Container container, Object constraints, ColorSpace colorSpace) {
		this.cm = new ComponentColorModel(colorSpace, false, false, Transparency.OPAQUE,
				DataBuffer.TYPE_BYTE);
		initImageDisplay(container, constraints);
        // TODO: Change the implementation to the triple buffering in the Canvas and resize there directly
        // Set defaults
        SampleModel sm = cm.createCompatibleSampleModel(width, height);
        // Initialize an empty image
        DataBufferByte dataBuffer = new DataBufferByte(new byte[width* height *INITIAL_NUM_CHANNEL],
                width* height);
        WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0,0));
        // Create the original image
        image = new BufferedImage(cm, raster, false, properties);
    }

	@Override
	public void streamOpened() {
		// Paint the image
        doPaint = true;
	}

    @Override
    public void streamNewImageSize(int newWidth, int newHeight) {
        this.width = newWidth;
        this.height = newHeight;
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
		canvas.repaint();
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
		canvas.repaint();
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
