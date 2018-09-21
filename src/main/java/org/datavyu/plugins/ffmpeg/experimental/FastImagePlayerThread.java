package org.datavyu.plugins.ffmpeg.experimental;

import org.datavyu.plugins.ffmpeg.MediaPlayerData;

import javax.swing.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.util.Hashtable;

/**
 * Note, that this has the rolling-effect between images,
 * especially when played back fast
 */
public class FastImagePlayerThread extends Thread {

    /** The color component model */
    private ComponentColorModel cm = null;

    /** The properties */
    private Hashtable<String, String> properties = new Hashtable<>();

    /** The buffered image to display*/
    private BufferedImage image = null;

    /** The media player data to get data, width, height, channel info */
    private MediaPlayerData mediaPlayerData;

    /** The color space for this the data provided */
    private byte[] data;
    private SampleModel sm;
    private JFrame frame;
    private static final int NUM_COLOR_CHANNELS = 3;
    private int width;
    private int height;
    private static final double REFRESH_PERIOD = 0.01; // >= 1/fps
    private static final double TO_MILLIS = 1000.0;
    private boolean stopped = false;
    private VolatileImage vImg = null;
    private Canvas imageDisplay = null;
    private volatile boolean forceUpdate = true;

    private void renderOffscreen() {
        long time = System.nanoTime();
        do {
            if (vImg.validate(frame.getGraphicsConfiguration()) ==
                    VolatileImage.IMAGE_INCOMPATIBLE) {
                // old vImg doesn't work with new GraphicsConfig; re-create it
                vImg = frame.createVolatileImage(width, height);
            }
            Graphics2D g = vImg.createGraphics();
            g.drawImage(image, 0, 0, frame.getWidth(), frame.getHeight(),  null);
            g.dispose();
        } while (vImg.contentsLost());
        System.out.println("Time to display took: " + (System.nanoTime() - time)/1e6 + " ms");
    }

    private void updateDisplay(Graphics gScreen) {
        // copying from the image (here, gScreen is the Graphics object for the onscreen window)
        do {
            int returnCode = vImg.validate(frame.getGraphicsConfiguration());
            if (returnCode == VolatileImage.IMAGE_RESTORED || forceUpdate) {
                // Contents need to be restored
                renderOffscreen();      // restore contents
            } else if (returnCode == VolatileImage.IMAGE_INCOMPATIBLE) {
                // old vImg doesn't work with new GraphicsConfig; re-create it
                vImg = frame.createVolatileImage(width, height);
                renderOffscreen();
            }
            gScreen.drawImage(vImg, 0, 0, null);
            forceUpdate = false;
        } while (vImg.contentsLost());
    }

    /**
     * Creates a video display through a stream listener. The display is added
     * to the container.
     *
     * @param mediaPlayerData The underlying movie stream that provides data.
     */
    public FastImagePlayerThread(MediaPlayerData mediaPlayerData) {
        this.mediaPlayerData = mediaPlayerData;
    }

    public void init(ColorSpace colorSpace, int width, int height, JFrame frame) {
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
        this.frame = frame;
        imageDisplay = new Canvas() {
            private static final long serialVersionUID = 5471924216942753555L;
            @Override
            public void paint(Graphics g) {
                updateDisplay(g);
            }
            public void update(Graphics g){
                paint(g);
            }
        };
        imageDisplay.setBounds(0, 0, this.width, this.height);
        frame.add(imageDisplay);
        this.frame.setBounds(0, 0, this.width, this.height);
        this.frame.setVisible(true);
        vImg = imageDisplay.createVolatileImage(width, height);
        imageDisplay.repaint();
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
            forceUpdate = true;
            imageDisplay.repaint();
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
}
