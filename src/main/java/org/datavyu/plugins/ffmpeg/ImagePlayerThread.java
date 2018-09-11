package org.datavyu.plugins.ffmpeg;

import javax.swing.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import java.util.concurrent.locks.ReentrantLock;

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
    private BufferStrategy strategy;
    private static final int NUM_COLOR_CHANNELS = 3;
    private static final int NUM_BUFFERS = 3;
    private volatile boolean stopped = false;
    private int width;
    private int height;
    private static final double REFRESH_PERIOD = 0.01; // >= 1/fps
    private static final double TO_MILLIS = 1000.0;

    private int imgWidth;
    private int imgHeight;
    private int x1, y1, x2, y2;

    ImagePlayerThread(MediaPlayerData mediaPlayerData) {
        this.mediaPlayerData = mediaPlayerData;
        setName("Ffmpeg image player thread");
        setDaemon(false);
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

        renderImage();
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

            renderImage();
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

    private synchronized void renderImage() {
        Dimension size;
        do {
            size = frame.getSize();
            if (size == null) {
                return;
            }
            do {
                do {
                    Graphics graphics = strategy.getDrawGraphics();
                    if (graphics == null) {
                        return;
                    }

                    scaleImage();

                    graphics.drawImage(image, x1, y1, x2, y2, 0, 0, imgWidth, imgHeight, null);
                    graphics.dispose();
                } while (strategy.contentsRestored());
                strategy.show();
            } while (strategy.contentsLost());
            // Repeat the rendering if the target changed size
        } while (!size.equals(frame.getSize()));
    }

    private void scaleImage(){
        imgWidth = image.getWidth();
        imgHeight = image.getHeight();
        double imgRatio = (double) imgHeight / imgWidth;
        int frameWidth = frame.getWidth();
        int frameHeight = frame.getHeight();
        double frameAspect = (double) frameHeight / frameWidth;
        x1 = 0;
        y1 = 0;
        x2 = 0;
        y2 = 0;
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
}

