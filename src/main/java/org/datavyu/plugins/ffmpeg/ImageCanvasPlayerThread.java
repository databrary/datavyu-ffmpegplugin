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
    private volatile boolean stopped = false;
    private int width;
    private int height;
    private static final double REFRESH_PERIOD = 0.01; // >= 1/fps
    private static final double TO_MILLIS = 1000.0;

    ImageCanvasPlayerThread(MediaPlayerData mediaPlayerData) {
        this.mediaPlayerData = mediaPlayerData;
        setName("Ffmpeg image canvas player thread");
        setDaemon(false);
    }

    private void updateDisplay() {
        //long time = System.nanoTime();
        do {
            do {
                Graphics graphics = strategy.getDrawGraphics();
                graphics.drawImage(image, 0, 0, canvas.getWidth(), canvas.getHeight(),  null);
                graphics.dispose();
            } while (strategy.contentsRestored());
            strategy.show();
        } while (strategy.contentsLost());
        //System.out.println("Time to display took: " + (System.nanoTime() - time)/1e6 + " ms");
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
        container.add(canvas, BorderLayout.CENTER);
        container.setBounds(0, 0, this.width, this.height);
        container.setVisible(true);
        // Make sure to make the canvas visible before creating the buffer strategy
        this.canvas.createBufferStrategy(NUM_BUFFERS);
        strategy = this.canvas.getBufferStrategy();
        container.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                launcher(() -> updateDisplay());
            }

            @Override
            public void componentMoved(ComponentEvent e) {  }

            @Override
            public void componentShown(ComponentEvent e) {	}

            @Override
            public void componentHidden(ComponentEvent e) {  }
        });

        launcher(() -> updateDisplay());
    }

    public void run() {
        while (!stopped) {
            long start = System.currentTimeMillis();
            // Get the data from the native side that matches width & height
            mediaPlayerData.updateImageData(data);
            //LOGGER.info("Presentation time is: " + mediaPlayerData.getPresentationTime() + " sec");
            // Create data buffer
            DataBufferByte dataBuffer = new DataBufferByte(data, width*height);
            // Create writable raster
            WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0, 0));
            // Create the original image
            image = new BufferedImage(cm, raster, false, properties);
            launcher(() -> updateDisplay());
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
        stopped = true;
    }

    private static void launcher(Runnable runnable) {
        if (EventQueue.isDispatchThread()){
            runnable.run();
        } else {
            try {
                EventQueue.invokeAndWait(runnable);
            } catch (InterruptedException | InvocationTargetException e) { }
        }
    }
}
