package org.datavyu.plugins.ffmpeg;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;

public class ImageCanvasPlayerThread extends Thread {
    private final static Logger LOGGER = LogManager.getFormatterLogger(ImageCanvasPlayerThread.class);
    private MediaPlayerData mediaPlayerData;
    private SampleModel sm;
    private ComponentColorModel cm;
    private Hashtable<String, String> properties = new Hashtable<>();
    private BufferedImage image;
    private byte[] data;
    private Canvas canvas;
    private BufferStrategy strategy;
    private static final int NUM_COLOR_CHANNELS = 3;
    private static final int NUM_BUFFERS = 3;
    private volatile boolean terminate = false;
    private int width;
    private int height;
    private static final double REFRESH_PERIOD = 0.01; // >= 1/fps
    private static final double TO_MILLIS = 1000.0;

    /** x1 and y1 are respectively the x and y coordinates of the first corner of the destination rectangle. */
    private int x1, y1;

    /** x2 and y2 are respectively the x and y coordinates of the second corner of the destination rectangle. */
    private int x2, y2;

    ImageCanvasPlayerThread(MediaPlayerData mediaPlayerData) {
        this.mediaPlayerData = mediaPlayerData;
        setName("Ffmpeg image canvas player thread");
        setDaemon(false);
    }

    /**
     * Draws as much of the specified area of the specified image as is
     * currently available, scaling it on the fly to fit inside the
     * specified area of the destination drawable surface
     * specified by x1, y1, x2, y2 variables.
     */
    private synchronized void updateDisplay() {
        Dimension size;
        /** This loop is replacing the component listener used
         * to update the image while resizing the canvas, listener
         * is causing flickering during resizing
         */
        do {
            size = canvas.getSize();
            if (size == null) { return; }
            do {
                do {
                    Graphics graphics = strategy.getDrawGraphics();
                    if (graphics == null) { return; }
                    scaleImage(); // calculate the coordinate of the scaled display rectangle
                    graphics.drawImage(image, x1, y1, x2, y2, 0, 0, this.width, this.height, null);
                    graphics.dispose();
                } while (strategy.contentsRestored());
                strategy.show();
            } while (strategy.contentsLost());
            // Repeat the rendering if the target changed size
        } while (!size.equals(canvas.getSize()));
    }

    /**
     * Calculate the coordinates of the destination scaled rectangle
     * that much the original image aspect ratio
     */
    private void scaleImage(){
        double imgRatio = (double) this.height / this.width;

        int frameWidth = canvas.getWidth();
        int frameHeight = canvas.getHeight();
        double frameAspect = (double) frameHeight / frameWidth;

        x1 = y1 = x2 =  y2 = 0;

        if (frameAspect > imgRatio) {
            y1 = frameHeight;
            // keep image aspect ratio
            frameHeight = (int) (frameWidth * imgRatio);
            y1 = (y1 - frameHeight) / 2;
        } else {
            x1 = frameWidth;
            // keep image aspect ratio
            frameWidth = (int) (frameHeight / imgRatio);
            x1 = (x1 - frameWidth) / 2;
        }
        x2 = frameWidth + x1;
        y2 = frameHeight + y1;
    }


    public void init(ColorSpace colorSpace, int width, int height, Container container) {
        this.width = width;
        this.height = height;

        // Allocate byte buffer
        this.data = new byte[this.width*this.height*NUM_COLOR_CHANNELS];

        cm = new ComponentColorModel(colorSpace, false, false, Transparency.OPAQUE,
                DataBuffer.TYPE_BYTE);
        // Set defaults
        sm = cm.createCompatibleSampleModel(this.width, this.height);
        // Initialize an empty image
        DataBufferByte dataBuffer = new DataBufferByte(this.data, this.width*this.height);
        WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0, 0));
        // Create the original image
        image = new BufferedImage(cm, raster, false, properties);

        // Create the canvas and add it to the center fo the container
        this.canvas = new Canvas();

        // Add A black background to the canvas
        this.canvas.setBackground(Color.BLACK);

        container.add(canvas, BorderLayout.CENTER);
        container.setBounds(0, 0, this.width, this.height);
        container.setVisible(true);
        // Make sure to make the canvas visible before creating the buffer strategy
        this.canvas.createBufferStrategy(NUM_BUFFERS);
        strategy = this.canvas.getBufferStrategy();

        updateDisplay();
    }

    public void run() {
        while (!terminate) {
            long start = System.currentTimeMillis();
            mediaPlayerData.updateImageData(data);
            //LOGGER.info("Presentation time is: " + mediaPlayerData.getPresentationTime() + " sec");
            System.out.println("Presentation time is: " + mediaPlayerData.getPresentationTime() + " sec");

            // Create data buffer
            DataBufferByte dataBuffer = new DataBufferByte(data, width*height);
            // Create writable raster
            WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0, 0));
            // Create the original image
            image = new BufferedImage(cm, raster, false, properties);
            updateDisplay();
            // This does not measure the time to update the display
            double waitTime = REFRESH_PERIOD - (System.currentTimeMillis() - start)/TO_MILLIS;
            // If we need to wait
            if (waitTime > 0) {
                try {
                    Thread.sleep((long) (waitTime*TO_MILLIS));
                } catch (InterruptedException ie) {
                    // do nothing
                }
            }
        }
    }

    public void terminte() {
        terminate = true;
    }
}
