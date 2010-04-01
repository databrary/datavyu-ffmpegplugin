package org.openshapa.util;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Rectangle;

import java.io.File;
import java.io.IOException;

import javax.swing.JComponent;

import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

import javax.imageio.ImageIO;

import org.testng.Assert;


/**
 * Image utilities.
 * Example: capturing screenshot of component, comparing images.
 */
public final class UIImageUtils {

    /** Maximum distance one pixel can be away from another */
    public static final double MAX_PIXEL_DISTANCE = Math.sqrt(255 ^ (2 + 255)
            ^ (2 + 255) ^ 2);


    public static Boolean areImagesEqual(final RenderedImage uiImage,
        final File refFile) throws IOException {

        // CONSTANTS
        // Pixel threshold as a percentage
        final double PIXEL_THRESHOLD = 0.1;
        final double ERROR_THRESHOLD = 0.1;
        final String tempFolder = System.getProperty("java.io.tmpdir");
        System.err.println("temp: " + tempFolder);


        // Load image from file
        RenderedImage refImage = ImageIO.read(refFile);

        // Check that images are the same size
        if (!(uiImage.getHeight() == refImage.getHeight())
                || !(uiImage.getWidth() == refImage.getWidth())) {
            ImageIO.write(uiImage, "bmp", new File(tempFolder + "/areImagesEqual.bmp"));
            Assert.assertEquals(uiImage.getHeight(), refImage.getHeight());
            Assert.assertEquals(uiImage.getWidth(), refImage.getWidth());
        }
        

        int totalPixels = uiImage.getHeight() * uiImage.getWidth();

        // Number of pixels incorrect
        int errorPixels = 0;

        // For each pixel, calculate distance
        for (int i = 0; i < totalPixels; i++) {
            int r1 = uiImage.getColorModel().getRed(i);
            int g1 = uiImage.getColorModel().getGreen(i);
            int b1 = uiImage.getColorModel().getBlue(i);
            int r2 = refImage.getColorModel().getRed(i);
            int g2 = refImage.getColorModel().getGreen(i);
            int b2 = refImage.getColorModel().getBlue(i);
            double pixelDistance = Math.sqrt(((r1 - r2) * (r1 - r2))
                    + ((g1 - g2)
                        * (g1 - g2)) + ((b1 - b2) * (b1 - b2)));

            // Check if correct within threshold
            if (pixelDistance > (PIXEL_THRESHOLD * MAX_PIXEL_DISTANCE)) {
                errorPixels++;
            }
        }

        // Check if number of error pixels > threshold
        boolean withinThreshold = (errorPixels / totalPixels) < ERROR_THRESHOLD;
        if (!withinThreshold) {
            ImageIO.write(uiImage, "bmp", new File(tempFolder + "/areImagesEqual.bmp"));
        }
        return withinThreshold;
    }

    /**
     * Captures screenshot of component and saves to a file.
     * @param component JComponent to capture screenshot
     * @param saveAs file name
     */
    public static void captureAsScreenshot(final JComponent component,
        final File saveAs) {

        try {
            Robot robot = new Robot();

            // Create Rectangle around component
            Point locOnScreen = component.getLocationOnScreen();
            Rectangle bounds = component.getBounds();
            bounds.setLocation(locOnScreen);

            BufferedImage bi = robot.createScreenCapture(bounds);
            ImageIO.write(bi, "bmp", saveAs);
        } catch (AWTException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
    * Captures screenshot of component nd returns as BufferedImage
    * @param component JComponent to capture screenshot
    * @param saveAs file name
    */
    public static BufferedImage captureAsScreenshot(
        final JComponent component) {
        BufferedImage bi = null;

        try {
            Robot robot = new Robot();

            // Create Rectangle around component
            Point locOnScreen = component.getLocationOnScreen();
            Rectangle bounds = component.getBounds();
            bounds.setLocation(locOnScreen);

            bi = robot.createScreenCapture(bounds);
        } catch (AWTException e) {
            e.printStackTrace();
        }

        return bi;
    }
}
