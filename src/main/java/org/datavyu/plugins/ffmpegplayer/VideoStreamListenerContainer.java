package org.datavyu.plugins.ffmpegplayer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.util.Hashtable;

/**
 * This class implements a stream listener for the video data. It receives byte data from the provider, which is
 * typically a MoviePlayer, and displays it as originalImage.
 * 
 * @author Florian Raudies, Mountain View, CA.
 */
public class VideoStreamListenerContainer implements StreamListener {

    /** This is the threshold at which a resize occurs */
    private static final float SCALE_RESIZE_THRESHOLD = 0.01f;

    /** The logger for this class */
    private static Logger logger = LogManager.getFormatterLogger(VideoStreamListenerContainer.class);

	/** The color component model */
	private ComponentColorModel cm = null;
	
	/** The properties */
	private Hashtable<String, String> properties = new Hashtable<>();
	
	/** This is the original image that is been read from the native side */
	private BufferedImage originalImage = null;

	/** This is the scaled image that is displayed */
	private BufferedImage scaledImage = null;
	
	/** The canvas that we draw the originalImage in */
	private Canvas canvas = null;
	
	/** The movie stream to get width, height, channel info */
	private MoviePlayer moviePlayer = null;
	
	/** The color space for this the data provided */
	private ColorSpace colorSpace = null;

	/** The current scale of the play back originalImage, e.g. 2.0f magnifies the original originalImage by 2x */
	private float scale = 1f;
	
	/** 
	 * The stream has doPaint = false no updates to the originalImage should be made
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
	 * Initialized the originalImage display and adds it to the supplied container with
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
				// If not doPaint display the next originalImage
				if (doPaint) {
	        		g.drawImage(scaledImage, 0, 0, null);
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
	 * to the container.
	 * 
	 * @param moviePlayer The underlying movie stream that provides data.
	 * @param container The container we add the originalImage display.
	 * @param colorSpace The color space for the originalImage data.
	 */
	public VideoStreamListenerContainer(MoviePlayer moviePlayer,
										Container container, ColorSpace colorSpace) {
		this(moviePlayer, container, null, colorSpace);
	}
	
	/**
	 * Creates a video display through a stream listener. The display is added 
	 * to the container using the constraint.
	 * 
	 * @param moviePlayer The underlying movie stream that provides data.
	 * @param container The container we add the originalImage display.
	 * @param constraints Constraint where to add this video display into the container.
	 * @param colorSpace The color space for the originalImage data.
	 */
	public VideoStreamListenerContainer(MoviePlayer moviePlayer,
										Container container, Object constraints, ColorSpace colorSpace) {
		this.moviePlayer = moviePlayer;
		this.colorSpace = colorSpace;
		initImageDisplay(container, constraints);
	}

	@Override
	public void streamOpened() {
		int width = moviePlayer.getWidth();
		int height = moviePlayer.getHeight();
		int nChannel = moviePlayer.getNumberOfColorChannels();
		cm = new ComponentColorModel(colorSpace, false, false, Transparency.OPAQUE,
				DataBuffer.TYPE_BYTE);
		SampleModel sm = cm.createCompatibleSampleModel(width, height);
		// Initialize an empty originalImage
		DataBufferByte dataBuffer = new DataBufferByte(new byte[width*height*nChannel], width*height);
		WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0,0));
		// Create the original image
		originalImage = new BufferedImage(cm, raster, false, properties);
		// Resize that image according to the scale
		scaledImage = resizeImage(originalImage, scale);
		// Paint the image
        doPaint = true;
	}

	@Override
	public void streamData(byte[] data) {
		// Width and height could have changed due to the view
		int width = moviePlayer.getWidth();
		int height = moviePlayer.getHeight();
        logger.debug("Received " + data.length + " By for originalImage: " + width + " x " + height + " pixels.");
		SampleModel sm = cm.createCompatibleSampleModel(width, height);
		// Create data buffer
		DataBufferByte dataBuffer = new DataBufferByte(data, width*height);
		// Create writable raster
		WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0, 0));
		// Create the original image
        originalImage = new BufferedImage(cm, raster, false, properties);
        // Resize the original image
        scaledImage = resizeImage(originalImage, scale);
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

    /**
     * Resizes the buffered originalImage and returns the resized originalImage
     * @param image
     * @return
     */
	private BufferedImage resizeImage(BufferedImage image, float scale) {
	    if (Math.abs(scale - 1.0f) > Math.ulp(1.0f)) {
            int oldWidth = moviePlayer.getWidth();
            int oldHeight = moviePlayer.getHeight();
            int newWidth = (int) Math.floor(scale*oldWidth);
            int newHeight = (int) Math.floor(scale*oldHeight);
            Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = newImage.createGraphics();
            g2d.drawImage(scaledImage, 0, 0, null);
            g2d.dispose();
            return newImage;
        } else {
	        return originalImage;
        }
    }

	@SuppressWarnings("unused") // API method
	public void setScale(float newScale) {
        // We rescale the originalImage if the scale is strongly update the originalImage
        if (Math.abs(newScale - scale) > SCALE_RESIZE_THRESHOLD) {
            logger.info("Changing from %2.3f scale to %2.3f.", scale, newScale);
            scale = newScale;
            scaledImage = resizeImage(originalImage, newScale);
            // Must set doPaint to true, it's fine not to set it back
            doPaint = true;
            canvas.repaint();
        }
    }
}
