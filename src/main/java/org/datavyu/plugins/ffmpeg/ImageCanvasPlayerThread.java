package org.datavyu.plugins.ffmpeg;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;

public class ImageCanvasPlayerThread extends Thread {
    private MediaPlayerData mediaPlayerData;
    private SampleModel sm;
    private ComponentColorModel cm;
    private Hashtable<String, String> properties = new Hashtable<>();
    private BufferedImage image;
    private byte[] data;
    private Container container;
    private Canvas canvas;
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
    private boolean updateScale = true;

    ImageCanvasPlayerThread(MediaPlayerData mediaPlayerData) {
        this.mediaPlayerData = mediaPlayerData;
        setName("Ffmpeg image player thread");
        setDaemon(false);
    }

    private void updateDisplay() {
        do {
            do {
                Graphics graphics = strategy.getDrawGraphics();
                if(updateScale){
                    scaleImage();
                }
                graphics.drawImage(image,x1, y1, x2, y2, 0, 0, imgWidth, imgHeight, null);
                graphics.dispose();
            } while (strategy.contentsRestored());
            strategy.show();
        } while (strategy.contentsLost());
    }


    public void init(ColorSpace colorSpace, int width, int height, Container container) {

        this.container = container;
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

        initContainer();

        launcher(() -> updateDisplay());
    }

    private void initContainer(){
        this.canvas = new Canvas();

        this.container.add(canvas, BorderLayout.CENTER);

        this.container.setBounds(0, 0, this.width, this.height);
        this.container.setVisible(true);

        // Make sure to make the canvas visible before creating the buffer strategy
        this.canvas.createBufferStrategy(NUM_BUFFERS);
        strategy = this.canvas.getBufferStrategy();

        this.container.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateScale = true;
                launcher(() -> updateDisplay());
            }

            @Override
            public void componentMoved(ComponentEvent e) {  }

            @Override
            public void componentShown(ComponentEvent e) {	}

            @Override
            public void componentHidden(ComponentEvent e) {  }
        });
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

    private void scaleImage(){
        imgWidth = width;
        imgHeight = height;

        double imgRatio = (double) imgHeight / imgWidth;

        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        double canvasAspect = (double) canvasHeight / canvasWidth;

        x1 = 0;
        y1 = 0;
        x2 = 0;
        y2 = 0;

        if (canvasAspect > imgRatio) {
            y1 = canvasHeight;
            // keep image aspect ratio
            canvasHeight = (int) (canvasWidth * imgRatio);
            y1 = (y1 - canvasHeight) / 2;
        } else {
            x1 = canvasWidth;
            // keep image aspect ratio
            canvasWidth = (int) (canvasHeight / imgRatio);
            x1 = (x1 - canvasWidth) / 2;
        }
        x2 = canvasWidth + x1;
        y2 = canvasHeight + y1;

        updateScale = false;
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
