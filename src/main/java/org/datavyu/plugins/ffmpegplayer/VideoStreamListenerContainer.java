package org.datavyu.plugins.ffmpegplayer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;

/**
 * This class implements a stream listener for the video data. It receives byte data from the provider, which is
 * typically a MovieStream, and displays it as originalImage.
 * 
 * @author Florian Raudies, Mountain View, CA.
 */
public class VideoStreamListenerContainer implements StreamListener {

    /** The logger for this class */
    private static Logger logger = LogManager.getFormatterLogger(VideoStreamListenerContainer.class);

	/** The color component model */
	private ComponentColorModel cm = null;
	
	/** The properties */
	private Hashtable<String, String> properties = new Hashtable<>();
	
	/** This is the original image that is been read from the native side */
	private BufferedImage originalImage = null;

	/** The canvas that we draw the originalImage in */
	private Canvas canvas = new Canvas();
	
	/** The movie stream to get width, height, channel info */
	private MovieStream movieStream = null;
	
	/** The color space for this the data provided */
	private ColorSpace colorSpace = null;

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

	private final static int INITIAL_WIDTH = 640;
	private final static int INITIAL_HEIGHT = 480;
	private final static int INITIAL_NUM_CHANNEL = 3;

	private void updateDisplay() {
		BufferStrategy strategy = canvas.getBufferStrategy();
		do {
			do {
				// Make sure to create the buffer strategy before using it!
				Graphics graphics = strategy.getDrawGraphics();
				if (doPaint) {
					graphics.drawImage(originalImage, 0, 0, canvas.getWidth(), canvas.getHeight(),  null);
				}
				graphics.dispose();
			} while (strategy.contentsRestored());
			strategy.show();
		} while (strategy.contentsLost());
	}
	
	/**
	 * Initialized the originalImage display and adds it to the supplied container with
	 * the supplied constraint. If the constraint is null it uses the add method
	 * on the container without constraint.
	 * 
	 * @param container The container that the display is added to
	 * @param constraints The constraint used when adding
	 */
	private void initImageDisplay(Container container, Object constraints) {
		launcher(() -> {
			cm = new ComponentColorModel(colorSpace, false, false, Transparency.OPAQUE,
				DataBuffer.TYPE_BYTE);
			// Set defaults
			SampleModel sm = cm.createCompatibleSampleModel(INITIAL_WIDTH, INITIAL_HEIGHT);
			// Initialize an empty image
			DataBufferByte dataBuffer = new DataBufferByte(new byte[INITIAL_WIDTH * INITIAL_HEIGHT * INITIAL_NUM_CHANNEL],
					INITIAL_WIDTH * INITIAL_HEIGHT);
			WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0, 0));
			// Create the original image
			originalImage = new BufferedImage(cm, raster, false, properties);

			container.add(canvas);

			container.setBounds(0, 0, INITIAL_WIDTH, INITIAL_HEIGHT);
			container.setVisible(true);
			canvas.createBufferStrategy(3);
		});
	}

	/**
	 * Creates a video display through a stream listener. The display is added
	 * to the container.
	 * 
	 * @param movieStream The underlying movie stream that provides data.
	 * @param container The container we add the originalImage display.
	 * @param colorSpace The color space for the originalImage data.
	 */
	public VideoStreamListenerContainer(MovieStream movieStream,
										Container container, ColorSpace colorSpace) {
		this(movieStream, container, null, colorSpace);
	}
	
	/**
	 * Creates a video display through a stream listener. The display is added 
	 * to the container using the constraint.
	 * 
	 * @param movieStream The underlying movie stream that provides data.
	 * @param container The container we add the originalImage display.
	 * @param constraints Constraint where to add this video display into the container.
	 * @param colorSpace The color space for the originalImage data.
	 */
	public VideoStreamListenerContainer(MovieStream movieStream,
										Container container, Object constraints, ColorSpace colorSpace) {
		this.movieStream = movieStream;
		this.colorSpace = colorSpace;
		initImageDisplay(container, constraints);
	}

	@Override
	public void streamOpened() {
		launcher(() -> updateDisplay());
	}

	@Override
	public void streamData(byte[] data) {
		int width = movieStream.getWidthOfStream();
		int height = movieStream.getHeightOfStream();
        logger.debug("Received " + data.length + " By for originalImage: " + width + " x " + height + " pixels.");
		SampleModel sm = cm.createCompatibleSampleModel(width, height);
		// Create data buffer
		DataBufferByte dataBuffer = new DataBufferByte(data, width*height);
		// Create writable raster
		WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0, 0));
		// Create the original image
        originalImage = new BufferedImage(cm, raster, false, properties);
        // Paint the image
//		canvas.paint(null);
		launcher(() -> updateDisplay());
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
		launcher(() -> updateDisplay());
//		canvas.paint(null);
	}
	
	@Override
	public void streamStopped() {
		// stop displaying
		doPaint = false;
	}

	private static void launcher(Runnable runnable) {
		if (EventQueue.isDispatchThread()) {
			runnable.run();
		} else {
			try {
				EventQueue.invokeAndWait(runnable);
			} catch (InterruptedException | InvocationTargetException e) {
				logger.warn(e);
			}
		}
	}
}
