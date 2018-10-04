package org.datavyu.plugins.ffmpeg.experimental;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.datavyu.plugins.ffmpeg.MediaPlayerData;

import javax.swing.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.util.Hashtable;

public class ImageCustomPlayerThread extends Thread{
    private final static Logger LOGGER = LogManager.getFormatterLogger(org.datavyu.plugins.ffmpeg.ImageCanvasPlayerThread.class);
    private MediaPlayerData mediaPlayerData;
    private SampleModel sm;
    private ComponentColorModel cm;
    private Hashtable<String, String> properties = new Hashtable<>();
    private BufferedImage image;
    private byte[] data;
    private DisplayPanel displayPanel;
    private static final int NUM_COLOR_CHANNELS = 3;
    private volatile boolean terminate = false;
    private int imgWidth;
    private int imgHeight;
    private static final double REFRESH_PERIOD = 0.01; // >= 1/fps
    private static final double TO_MILLIS = 1000.0;

    /** x1 and y1 are respectively the x and y coordinates of the left, upper corner of the destination rectangle. */
    private int x1, y1;

    /** x2 and y2 are respectively the x and y coordinates of the lower, right corner of the destination rectangle. */
    private int x2, y2;

    public ImageCustomPlayerThread(MediaPlayerData mediaPlayerData) {
        this.mediaPlayerData = mediaPlayerData;
        setName("Ffmpeg image displayPanel player thread");
        setDaemon(false);
    }

    public void init(ColorSpace colorSpace, int width, int height, Container container) {
        this.imgWidth = width;
        this.imgHeight = height;

        // Allocate byte buffer
        this.data = new byte[this.imgWidth *this.imgHeight *NUM_COLOR_CHANNELS];

        cm = new ComponentColorModel(colorSpace, false, false, Transparency.OPAQUE,
                DataBuffer.TYPE_BYTE);
        // Set defaults
        sm = cm.createCompatibleSampleModel(this.imgWidth, this.imgHeight);
        // Initialize an empty image
        DataBufferByte dataBuffer = new DataBufferByte(this.data, this.imgWidth *this.imgHeight);
        WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0, 0));
        // Create the original image
        image = new BufferedImage(cm, raster, false, properties);

        // Create the displayPanel and add it to the center fo the container
        this.displayPanel = new DisplayPanel();

        container.add(displayPanel, BorderLayout.CENTER);
        container.setBounds(0, 0, this.imgWidth, this.imgHeight);
        container.setVisible(true);

        this.displayPanel.repaint();
    }

    public void run() {
        while (!terminate) {
            long start = System.currentTimeMillis();
            mediaPlayerData.updateImageData(data);
            //LOGGER.info("Presentation time is: " + mediaPlayerData.getPresentationTime() + " sec");
//            System.out.println("Presentation time is: " + mediaPlayerData.getPresentationTime() + " sec");

            // Create data buffer
            DataBufferByte dataBuffer = new DataBufferByte(data, imgWidth * imgHeight);
            // Create writable raster
            WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0, 0));
            // Create the original image
            image = new BufferedImage(cm, raster, false, properties);

            this.displayPanel.repaint();
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

    public void terminate() {
        terminate = true;
    }

    class DisplayPanel extends JPanel {

        public DisplayPanel() {
            this.setBackground(Color.BLACK);
            this.setDoubleBuffered(true);
        }

        @Override
        protected void printComponent(Graphics graphics) {
            super.paintComponent(graphics);
        }

        @Override
        public void paint(Graphics graphics) {
            super.paint(graphics);
            scaleImage(); // calculate the coordinate of the target image
            graphics.drawImage(image, x1, y1, x2, y2, 0, 0, imgWidth, imgHeight, null);
        }

        private void scaleImage(){
            double imgAspectRatio = (double) imgHeight / imgWidth;

            int canvasWidth = this.getWidth();
            int canvasHeight = this.getHeight();
            double canvasAspectRatio = (double) canvasHeight / canvasWidth;

            x1 = y1 = x2 =  y2 = 0;

            if (canvasAspectRatio > imgAspectRatio) {
                y1 = canvasHeight;
                // keep image aspect ratio
                canvasHeight = (int) (canvasWidth * imgAspectRatio);
                y1 = (y1 - canvasHeight) / 2;
            } else {
                x1 = canvasWidth;
                // keep image aspect ratio
                canvasWidth = (int) (canvasHeight / imgAspectRatio);
                x1 = (x1 - canvasWidth) / 2;
            }
            x2 = canvasWidth + x1;
            y2 = canvasHeight + y1;
        }
    }
}
