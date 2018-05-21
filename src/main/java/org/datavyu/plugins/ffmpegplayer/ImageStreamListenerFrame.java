package org.datavyu.plugins.ffmpegplayer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_ProfileRGB;
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

	private BufferStrategy strategy;

	/** Parent JFrame */
	private JFrame frame;

	private ColorSpace colorSpace;

	private Canvas canvas;

	/**
	 * Creates a video display through a stream listener. The display is added 
	 * to the container using the constraint.
	 *
     * @param frame The frame that is used for drawing the image on.
	 * @param colorSpace The color space for the image data.
	 */
	public ImageStreamListenerFrame(JFrame frame, ColorSpace colorSpace) {
        // TODO: Change the implementation to the triple buffering in the Canvas and resize there directly
		this.frame = frame;
		this.colorSpace = colorSpace;
		init();

    }

    private void init(){
		Runnable init = () -> {
			//Creating an empty image may not be useful
			if (colorSpace == null){
				colorSpace = getDefaultSrceenDevice().getDefaultConfiguration().getColorModel().getColorSpace();
			}
			cm = new ComponentColorModel(colorSpace, false, false, Transparency.OPAQUE,
					DataBuffer.TYPE_BYTE);
			// Set defaults
			SampleModel sm = cm.createCompatibleSampleModel(width, height);
			// Initialize an empty image
			DataBufferByte dataBuffer = new DataBufferByte(new byte[width* height *INITIAL_NUM_CHANNEL],
					width* height);
			WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0,0));
			// Create the original image
			image = new BufferedImage(cm, raster, false, properties);


			frame.setBounds(0, 0, width, height);
			frame.setVisible(true);

			//Initialize the Canvas
			initCanvas();
		};
		if(EventQueue.isDispatchThread()){
			init.run();
		}else {
			try {
				EventQueue.invokeAndWait(init);
			} catch (InterruptedException e) {
				logger.warn(e);
			} catch (InvocationTargetException e) {
				logger.warn(e);
			}
		}
	}

	private void initCanvas() {
		canvas = new Canvas() {
			private static final long serialVersionUID = 5471924216942753555L;

			@Override
			public void update(Graphics g){ paint(g);}
			@Override
			public void paint(Graphics g) {
				// Calling BufferStrategy.show() could throws
				// exceptions
				try {
					BufferStrategy strategy = canvas.getBufferStrategy();
					if(strategy == null){
						createBufferStrategy(3);
					}
					do {
						do {
							g = strategy.getDrawGraphics();
							if (image != null) {
								// If not doPaint display the next originalImage
								if(doPaint){
									g.drawImage(image, 0, 0,getWidth(), getHeight(),  null);
								}
							}
							g.dispose();
						} while (strategy.contentsRestored());
						strategy.show();
					} while (strategy.contentsLost());
				} catch (Exception e) { logger.warn("Buffer Strategy Exception: " + e); }
			}
		};

		//TODO ADD an initial resize boolean to see if we need to resize after the first frame

		if (frame != null) {
			frame.getContentPane().add(canvas);
		}
		canvas.setVisible(true);
	}


	@Override
	public void streamOpened() {
		// Paint the image
        doPaint = true;
        // Update the display with the image content
		canvas.paint(null);
	}

    @Override
    public void streamNewImageSize(int newWidth, int newHeight) {
        this.width = newWidth;
        this.height = newHeight;

        // TODO: Need to update the buffer strategy here
		setCanvasSize(newWidth,newHeight);
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

	@SuppressWarnings("unused") // API method
	public void setScale(float newScale) {
        // We rescale the image if the scale is strongly update the image
        // TODO: Implement this differently
    }

    public Dimension getCanvasSize() { return canvas.getSize(); }

    /** Change the Canvas size */
    public void setCanvasSize(final int width, final int height){
		Dimension canvasDimension = getCanvasSize();
		if (canvasDimension.width == width
				&& canvasDimension.height == height){
			return;
		}
		Runnable resizeCanvas = () -> {
			logger.info("Change Canvas Size to: width: " +width + " Height: "+height);
			canvas.setSize(width, height);
			frame.pack();
		};

		if (EventQueue.isDispatchThread()){
			resizeCanvas.run();
		}else{
			try {
				EventQueue.invokeAndWait(resizeCanvas);
			} catch (InterruptedException e) {
				logger.warn(e);
			} catch (InvocationTargetException e) {
				logger.warn(e);
			}
		}
	}

	public GraphicsDevice getDefaultSrceenDevice(){
    	return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	}
}
