package org.datavyu.plugins.ffmpeg;

import org.datavyu.plugins.MediaPlayerData;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.util.Hashtable;

/**
 * This Class is responsible of displaying an a buffered image into a canvas container, note that
 * the resizing of the container/image is performed through a while loop and not a component
 * listener, because is causing flickering while resizing the canvas
 *
 * <p>Performance: Video Resolution 1080p and no resizing during playback, the experiments was
 * conducted for the entire video duration: Display Avg Time: ~52 ms Display Max Time: 72 ms Display
 * Min Time: 50 ms Same Video with resizing (tried to resize the JFrame during the entire stream)
 * Display Avg Time: ~64 ms Display Max Time: 2 s (Noticed that the thread is not updating the image
 * while resizing) Display Min Time: 44 ms (displaying the frame is faster; small aea to draw)
 */
class ImageCanvasPlayerThread extends Thread {
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
  private int imgWidth;
  private int imgHeight;
  private static final double REFRESH_PERIOD = 0.01; // >= 1/fps
  private static final double TO_MILLIS = 1000.0;

  /**
   * x1 and y1 are respectively the x and y coordinates of the left, upper corner of the destination
   * rectangle.
   */
  private int x1, y1;

  /**
   * x2 and y2 are respectively the x and y coordinates of the lower, right corner of the
   * destination rectangle.
   */
  private int x2, y2;

  ImageCanvasPlayerThread(MediaPlayerData mediaPlayerData) {
    this.mediaPlayerData = mediaPlayerData;
    setName("Ffmpeg image canvas player thread");
    setDaemon(false);
  }

  /**
   * Draws as much of the specified area of the specified image as is currently available, scaling
   * it on the fly to fit inside the specified area of the destination drawable surface specified by
   * x1, y1, x2, y2 variables.
   */
  private synchronized void updateDisplay() {
    Dimension size;
    do {
      size = canvas.getSize();
      if (size == null) {
        return;
      }
      do {
        do {
          Graphics graphics = strategy.getDrawGraphics();
          if (graphics == null) {
            return;
          }
          scaleImage(); // calculate the coordinate of the target image
          graphics.drawImage(image, x1, y1, x2, y2, 0, 0, this.imgWidth, this.imgHeight, null);
          graphics.dispose();
        } while (strategy.contentsRestored());
        strategy.show();
      } while (strategy.contentsLost());
      // Repeat the rendering if the target changed size
    } while (!size.equals(canvas.getSize()));
  }

  /**
   * Scale an image with keeping the aspect ratio of the original image, by calculating the
   * coordinates (upper left and lower right) of the target image to be rendered in the canvas.
   */
  private void scaleImage() {
    double imgAspectRatio = (double) this.imgHeight / this.imgWidth;

    int canvasWidth = canvas.getWidth();
    int canvasHeight = canvas.getHeight();
    double canvasAspectRatio = (double) canvasHeight / canvasWidth;

    x1 = y1 = x2 = y2 = 0;

    if (canvasAspectRatio > imgAspectRatio) {
      y1 = canvasHeight;
      // Maintain aspect ratio
      canvasHeight = (int) (canvasWidth * imgAspectRatio);
      y1 = (y1 - canvasHeight) / 2;
    } else {
      x1 = canvasWidth;
      // Maintain aspect ratio
      canvasWidth = (int) (canvasHeight / imgAspectRatio);
      x1 = (x1 - canvasWidth) / 2;
    }
    x2 = canvasWidth + x1;
    y2 = canvasHeight + y1;
  }

  public void init(ColorSpace colorSpace, int width, int height, Container container) {
    this.imgWidth = width;
    this.imgHeight = height;
    // Allocate byte buffer
    this.data = new byte[this.imgWidth * this.imgHeight * NUM_COLOR_CHANNELS];
    // Update the Image buffer to Pull a frame from the queue and update
    // the PTS from NaN to 0.0 sec
    mediaPlayerData.updateImageData(data);
    // Set defaults
    cm = new ComponentColorModel(
            colorSpace, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
    sm = cm.createCompatibleSampleModel(this.imgWidth, this.imgHeight);
    // Initialize an empty image
    DataBufferByte dataBuffer = new DataBufferByte(this.data, this.imgWidth * this.imgHeight);
    WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0, 0));
    // Create the original image
    image = new BufferedImage(cm, raster, false, properties);
    // Create the canvas and add it to the center of the Container
    this.canvas = new Canvas();
    // Add a black background to the canvas
    this.canvas.setBackground(Color.BLACK);
    container.add(canvas, BorderLayout.CENTER);
    container.setBounds(0, 0, this.imgWidth, this.imgHeight);
    container.setVisible(true);
    // Make sure to make the canvas visible before creating the buffer strategy
    this.canvas.createBufferStrategy(NUM_BUFFERS);
    strategy = this.canvas.getBufferStrategy();
    // Update the display
    updateDisplay();
  }

  public void run() {
    while (!terminate) {
      long start = System.currentTimeMillis();
      // Get the next image data -- may return the same data if no newer data is available
      mediaPlayerData.updateImageData(data);
      // Create data buffer
      DataBufferByte dataBuffer = new DataBufferByte(data, imgWidth * imgHeight);
      // Create writable raster
      WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0, 0));
      // Create the original image
      image = new BufferedImage(cm, raster, false, properties);
      // Update the display
      updateDisplay();
      // Compute the wait time as update time - time taken
      double waitTime = REFRESH_PERIOD - (System.currentTimeMillis() - start) / TO_MILLIS;
      // If we need to wait
      if (waitTime > 0) {
        try {
          Thread.sleep((long) (waitTime * TO_MILLIS));
        } catch (InterruptedException ie) {
          // do nothing
        }
      }
    }
  }

  public void terminate() {
    terminate = true;
  }
}
