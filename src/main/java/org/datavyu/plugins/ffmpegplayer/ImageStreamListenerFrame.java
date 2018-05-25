package org.datavyu.plugins.ffmpegplayer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.*;
import java.lang.reflect.InvocationTargetException;
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

	private int imageWidth = INITIAL_WIDTH;

	private int imageHeight = INITIAL_HEIGHT;
	
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

	/** Parent JFrame */
	private JFrame frame;

	private void updateDisplay() {
        BufferStrategy strategy = frame.getBufferStrategy();
        do {
            do {
                // Make sure to create the buffer strategy before using it!
                Graphics graphics = strategy.getDrawGraphics();
                if (doPaint) {
                    graphics.drawImage(image, 0, 0, frame.getWidth(), frame.getHeight(),  null);
                }
                graphics.dispose();
            } while (strategy.contentsRestored());
            strategy.show();
        } while (strategy.contentsLost());
    }

	/**
	 * Creates a video display through a stream listener. The display is added 
	 * to the container using the constraint.
	 *
     * @param frame The frame that is used for drawing the image on.
	 * @param colorSpace The color space for the image data.
	 */
	public ImageStreamListenerFrame(JFrame frame, ColorSpace colorSpace) {
		this.frame = frame;

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
            image = new BufferedImage(cm, raster, false, properties);

            frame.setBounds(0, 0, INITIAL_WIDTH, INITIAL_HEIGHT);
            frame.setVisible(true);

            // Make sure to make the canvas visible before creating the buffer strategy
            frame.createBufferStrategy(3);
        });
    }

	@Override
	public void streamOpened() {
		// Paint the image
        launcher(() ->{
            updateDisplay();
        });
	}

    @Override
    public void streamImageSize(int width, int height) {
        this.imageWidth = width;
        this.imageHeight = height;
    }

    @Override
	public void streamData(byte[] data) {
        logger.debug("Received " + data.length + " By for image: " + imageWidth + " x " + imageHeight + " pixels.");
		SampleModel sm = cm.createCompatibleSampleModel(imageWidth, imageHeight);
		// Create data buffer
		DataBufferByte dataBuffer = new DataBufferByte(data, imageWidth*imageHeight);

		logger.debug("Created data buffer");

		// Create writable raster
		WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0, 0));

		logger.debug("Created writeable raster");
		// Create the original image
        image = new BufferedImage(cm, raster, false, properties);

        launcher(()->{
            updateDisplay();
        });
	}

	@Override
	public void streamClosed() { 
		doPaint = false;
	}
	
	@Override
	public void streamStarted() {
		// play displaying
		doPaint = true;

		updateDisplay();
	}
	
	@Override
	public void streamStopped() {
		// stop displaying
		doPaint = false;
	}

	private static void launcher(Runnable runnable) {
        if (EventQueue.isDispatchThread()){
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
