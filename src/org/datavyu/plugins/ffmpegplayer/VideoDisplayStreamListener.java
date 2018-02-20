package org.datavyu.plugins.ffmpegplayer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.util.Hashtable;

/**
 * This class implements a stream listener for the video data. It receives byte data from the provider, which is
 * typically a MovieStream, and displays it as image.
 * 
 * @author Florian Raudies, Mountain View, CA.
 */
public class VideoDisplayStreamListener implements StreamListener {

    /** This is the threshold at which a resize occurs */
    private static final float SCALE_RESIZE_THRESHOLD = 0.01f;

    /** The logger for this class */
    private static Logger logger = LogManager.getFormatterLogger(VideoDisplayStreamListener.class);

	/** The color component model */
	private ComponentColorModel cm = null;
	
	/** The properties */
	private Hashtable<String, String> properties = new Hashtable<>();
	
	/** The buffered image to display*/
	private BufferedImage image = null;
	
	/** The canvas that we draw the image in */
	private Canvas imageDisplay = null;
	
	/** The movie stream to get width, height, channel info */
	private MovieStream movieStream = null;
	
	/** The color space for this the data provided */
	private ColorSpace colorSpace = null;

	/** The current scale of the play back image, e.g. 2.0f magnifies the original image by 2x */
	private float scale = 1f;
	
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
		imageDisplay = new Canvas() {
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
        	container.add(imageDisplay, constraints);
        } else {
            container.add(imageDisplay);
        }		
	}

	/**
	 * Creates a video display through a stream listener. The display is added
	 * to the container.
	 * 
	 * @param movieStream The underlying movie stream that provides data.
	 * @param container The container we add the image display.
	 * @param colorSpace The color space for the image data.
	 */
	public VideoDisplayStreamListener(MovieStream movieStream, 
			Container container, ColorSpace colorSpace) {
		this(movieStream, container, null, colorSpace);
	}
	
	/**
	 * Creates a video display through a stream listener. The display is added 
	 * to the container using the constraint.
	 * 
	 * @param movieStream The underlying movie stream that provides data.
	 * @param container The container we add the image display.
	 * @param constraints Constraint where to add this video display into the container.
	 * @param colorSpace The color space for the image data.
	 */
	public VideoDisplayStreamListener(MovieStream movieStream, 
			Container container, Object constraints, ColorSpace colorSpace) {
		this.movieStream = movieStream;
		this.colorSpace = colorSpace;
		initImageDisplay(container, constraints);
	}

	@Override
	public void streamOpened() {
		int width = movieStream.getWidthOfView();
		int height = movieStream.getHeightOfView();		
		int nChannel = movieStream.getNumberOfColorChannels();
		cm = new ComponentColorModel(colorSpace, false, false, Transparency.OPAQUE,
				DataBuffer.TYPE_BYTE);
		SampleModel sm = cm.createCompatibleSampleModel(width, height);
		// Initialize an empty image
		DataBufferByte dataBuffer = new DataBufferByte(new byte[width*height*nChannel], width*height);
		WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0,0));
		image = new BufferedImage(cm, raster, false, properties);
        doPaint = true; // display
	}

	@Override
	public void streamData(byte[] data) {
		// Width and height could have changed due to the view
		int width = movieStream.getWidthOfView(); 
		int height = movieStream.getHeightOfView();
        logger.debug("Received " + data.length + " By for image: " + width + " x " + height + " pixels.");
		SampleModel sm = cm.createCompatibleSampleModel(width, height);
		// Create data buffer
		DataBufferByte dataBuffer = new DataBufferByte(data, width*height);
		// Create writable raster
		WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0, 0));
		// Create buffered image and possibly resize it
        image = resizeImage(new BufferedImage(cm, raster, false, properties), scale);
		imageDisplay.repaint();
	}

	@Override
	public void streamClosed() { 
		doPaint = false;
	}
	
	@Override
	public void streamStarted() {
		// start displaying
		doPaint = true;
		// display the current frame
		imageDisplay.repaint();
	}
	
	@Override
	public void streamStopped() {
		// stop displaying
		doPaint = false;
	}

    /**
     * Resizes the buffered image and returns the resized image
     * @param image
     * @return
     */
	private BufferedImage resizeImage(BufferedImage image, float scale) {
	    if (Math.abs(scale - 1.0f) > Math.ulp(1.0f)) {
            int oldWidth = movieStream.getWidthOfView();
            int oldHeight = movieStream.getHeightOfView();
            int newWidth = (int) Math.floor(scale*oldWidth);
            int newHeight = (int) Math.floor(scale*oldHeight);
            Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = newImage.createGraphics();
            g2d.drawImage(scaledImage, 0, 0, null);
            g2d.dispose();
            return newImage;
        } else {
	        return image;
        }
    }

	@SuppressWarnings("unused") // API method
	public void setScale(float newScale) {
        // We rescale the image if the scale is strongly update the image
        if (Math.abs(newScale - scale) > SCALE_RESIZE_THRESHOLD) {
            logger.info("Changing from %2.3f scale to %2.3f.", scale, newScale);
            scale = newScale;
            image = resizeImage(image, newScale);
            // Must set doPaint to true, it's fine not to set it back
            doPaint = true;
            imageDisplay.repaint();
        }
    }
}
