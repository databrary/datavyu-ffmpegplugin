package org.datavyu.plugins.ffmpeg;

import javax.swing.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.*;
import java.util.Hashtable;

// Currently this uses swing components to display the buffered image
// TODO(fraudies): Switch this to javafx for new GUI
public class ImagePlayerThread extends Thread {
    private MediaPlayerData mediaPlayerData;
    private SampleModel sm;
    private ComponentColorModel cm;
    private Hashtable<String, String> properties = new Hashtable<>();
    private BufferedImage image;
    private byte[] data;
    private JFrame frame;
    private boolean doPaint = false;
    private BufferStrategy strategy;
    private static final int NUM_COLOR_CHANNELS = 3;
    private static final int NUM_BUFFERS = 3;
    private volatile boolean stopped = false;
    private int width;
    private int height;
    private static final double REFRESH_PERIOD = 0.01; // >= 1/fps
    private static final double TO_MILLIS = 1000.0;

    ImagePlayerThread(MediaPlayerData mediaPlayerData) {
        this.mediaPlayerData = mediaPlayerData;
        setName("Ffmpeg image player thread");
        setDaemon(false);
    }

    private void updateDisplay() {
        do {
            do {
                Graphics graphics = strategy.getDrawGraphics();
                if (doPaint) {
                    graphics.drawImage(image, 0, 0, frame.getWidth(), frame.getHeight(),  null);
                }
                graphics.dispose();
            } while (strategy.contentsRestored());
            strategy.show();
        } while (strategy.contentsLost());
    }

    public void init(ColorSpace colorSpace, int width, int height, JFrame frame) {

        this.frame = frame;
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

        this.frame.setBounds(0, 0, this.width, this.height);
        this.frame.setVisible(true);

        // Make sure to make the canvas visible before creating the buffer strategy
        this.frame.createBufferStrategy(NUM_BUFFERS);
        strategy = this.frame.getBufferStrategy();
        launcher(() -> updateDisplay());
    }

    public void run() {
        while (!stopped) {
            long start = System.currentTimeMillis();
            // Get the data from the native side that matches width & height
            mediaPlayerData.updateImageData(data);
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
            } catch (InterruptedException | InvocationTargetException e) {
            }
        }
    }
}
