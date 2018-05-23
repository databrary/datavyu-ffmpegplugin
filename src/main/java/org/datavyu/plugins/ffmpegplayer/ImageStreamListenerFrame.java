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

	/** Parent JFrame */
	private JFrame frame;

	private Canvas canvas;

	/** A flag to see if we the canvas need to be resized */
	private boolean needResize = true;

	/** The scale of the canvas, default value is 1.0 */
	private double scale = 1.0;

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
            SampleModel sm = cm.createCompatibleSampleModel(width, height);
            // Initialize an empty image
            DataBufferByte dataBuffer = new DataBufferByte(new byte[width * height * INITIAL_NUM_CHANNEL],
                    width * height);
            WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0, 0));
            // Create the original image
            image = new BufferedImage(cm, raster, false, properties);

            frame.setBounds(0, 0, width, height);
            frame.setVisible(true);

            canvas = new Canvas() {
                @Override
                public void update(Graphics graphics){
                    paint(graphics);
                }
                @Override
                public void paint(Graphics graphics) {
                    BufferStrategy strategy = canvas.getBufferStrategy();
                    do {
                        do {
                            // Make sure to create the buffer strategy before using it!
                            graphics = strategy.getDrawGraphics();
                            if (image != null && doPaint) {
                                graphics.drawImage(image, 0, 0,getWidth(), getHeight(),  null);
                            }
                            graphics.dispose();
                        } while (strategy.contentsRestored());
                        strategy.show();
                    } while (strategy.contentsLost());
                }
            };
            // Make sure to add the canvas to the frame that is visible
            frame.getContentPane().add(canvas);
            canvas.setSize(INITIAL_WIDTH,INITIAL_HEIGHT);
            // Make sure to make the canvas visible before creating the buffer strategy
            canvas.setVisible(true);
            canvas.createBufferStrategy(3);
            needResize = true;
        });
    }

	@Override
	public void streamOpened() {
		// Paint the image
        doPaint = true;
        if (frame.isResizable() && needResize) {
			setSize((int)Math.round(image.getWidth(null)* scale),
                    (int) Math.round(image.getHeight(null)* scale));
		}
        // Update the display with the image content
		canvas.paint(null);
	}

    @Override
    public void streamNewImageSize(int newWidth, int newHeight) {
        this.width = newWidth;
        this.height = newHeight;
		setSize(newWidth, newHeight);
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

        // Check if we need to resize the canvas
		if (frame.isResizable() && needResize) {
			int width = (int)Math.round(image.getWidth(null)* scale);
			int height = (int) Math.round(image.getHeight(null)* scale);
			setSize(width,height);
		}

        // Paint the image
		canvas.paint(null);
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
		canvas.paint(null);
	}
	
	@Override
	public void streamStopped() {
		// stop displaying
		doPaint = false;
	}

    /** Change the Canvas size */
    private void setSize(final int width, final int height){
		Dimension old = canvas.getSize();
		if (old.width != width || old.height != height){
            launcher(() -> {
                logger.info("Change Canvas Size to width: " +width + " Height: "+height);
                canvas.setSize(width, height);
                frame.pack();
                needResize = false;
            });
		}
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

	public void setScale(double scale) {
		this.scale = scale;
		this.needResize = true;
	}
}
