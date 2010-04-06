package org.openshapa.util;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

import java.io.File;
import java.io.IOException;

import javax.swing.JComponent;

import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;

import java.util.Hashtable;

import javax.imageio.ImageIO;

import org.testng.Assert;


/**
 * Image utilities.
 * Example: capturing screenshot of component, comparing images.
 */
public final class UIImageUtils {

    /** Maximum distance one pixel can be away from another */
    public static final double MAX_PIXEL_DISTANCE = Math.sqrt((255*255) + (255*255) + (255*255));


    public static Boolean areImagesEqual(final BufferedImage uiImage,
        final File refFile) throws IOException {

        // CONSTANTS
        // Pixel threshold as a percentage
        final double PIXEL_THRESHOLD = 0.1;
        final double ERROR_THRESHOLD = 0.1;
        final String tempFolder = System.getProperty("java.io.tmpdir");
        System.err.println("temp: " + tempFolder);

        // Load image from file
        BufferedImage refImage = ImageIO.read(refFile);

        // Check that images are the same size
        if (!(uiImage.getHeight() == refImage.getHeight())
                || !(uiImage.getWidth() == refImage.getWidth())) {
            ImageIO.write(uiImage, "png",
                new File(tempFolder + "/areImagesEqual.png"));
        }

        Assert.assertEquals(uiImage.getHeight(), refImage.getHeight());
        Assert.assertEquals(uiImage.getWidth(), refImage.getWidth());

        int totalPixels = uiImage.getHeight() * uiImage.getWidth();

        // Number of pixels incorrect
        int errorPixels = 0;

        // For each pixel, calculate distance
        for (int x = 0; x < uiImage.getWidth(); x++) {

            for (int y = 0; y < uiImage.getHeight(); y++) {
                Color col1 = new Color(uiImage.getRGB(x, y));
                Color col2 = new Color(refImage.getRGB(x, y));
                double pixelDistance = pixelDistance(col1, col2);

                // Check if correct within threshold
                if (pixelDistance > (PIXEL_THRESHOLD * MAX_PIXEL_DISTANCE)) {
                    errorPixels++;
                }
            }
        }

        // Check if number of error pixels > threshold
        double error = (double)errorPixels / (double)totalPixels;
        boolean withinThreshold = error < ERROR_THRESHOLD;
        System.err.println("Error=" + error);

        if (!withinThreshold) {
            ImageIO.write(maskImage(uiImage, refImage), "png",
                new File(tempFolder + "/areImagesEqual.png"));
        }

        return withinThreshold;
    }

    private static double pixelDistance(Color col1, Color col2) {
        int r1 = col1.getRed();
        int g1 = col1.getGreen();
        int b1 = col1.getBlue();
        int r2 = col2.getRed();
        int g2 = col2.getGreen();
        int b2 = col2.getBlue();

        double pixelDistance = Math.sqrt(((r1 - r2) * (r1 - r2))
                + ((g1 - g2)
                * (g1 - g2)) + ((b1 - b2) * (b1 - b2)));
        return pixelDistance;
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
            ImageIO.write(bi, "png", saveAs);
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


    /**
     * Masks second image over the first.
     * From: http://stackoverflow.com/questions/221830/set-bufferedimage-alpha-mask-in-java
     * If images are not the same size, asserts false.
     * @param img1 an image to mask
     * @param img2 a mask image to lay over first
     * @return masked image
     */
    public static BufferedImage maskImage(BufferedImage img1,
        BufferedImage img2) {

        // Assert false if images are not an equal size.
        Assert.assertEquals(img1.getHeight(), img2.getHeight());
        Assert.assertEquals(img1.getWidth(), img2.getWidth());

        BufferedImage result = new BufferedImage(img1.getWidth(),
                img1.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Color i1Color, i2Color;

        for (int x = 0; x < img1.getWidth(); x++) {

            for (int y = 0; y < img1.getHeight(); y++) {
                i1Color = new Color(img1.getRGB(x, y));
                i2Color = new Color(img2.getRGB(x, y));
                //Get normalized difference
                double normDistance = pixelDistance(i1Color, i2Color) / MAX_PIXEL_DISTANCE * 255;

                if (normDistance > 0) {
                    System.err.println(normDistance);
                }

                int color = i1Color.getRGB() & 0x00FFFFFF; // mask away any alpha

                int mask = (int)normDistance << 24; // shift blue (normed pixelDistance) into alpha bits

                color |= mask;
                result.setRGB(x, y, color);
            }
        }

        return result;        
    }

    /**
     * Subtracts 2 images.
     * If images are not the same size, asserts false.
     * @param img1 an image to subtract from.
     * @param img2 an image to subtract.
     * @return difference image
     */
    public static BufferedImage subtractImage(BufferedImage img1,
        BufferedImage img2) {

        // Assert false if images are not an equal size.
        Assert.assertEquals(img1.getHeight(), img2.getHeight());
        Assert.assertEquals(img1.getWidth(), img2.getWidth());

        BufferedImage result = new BufferedImage(img1.getWidth(),
                img1.getHeight(), BufferedImage.TYPE_INT_RGB);
        int i1Color, i2Color;

        for (int x = 0; x < img1.getWidth(); x++) {

            for (int y = 0; y < img1.getHeight(); y++) {
                i1Color = img1.getRGB(x, y);
                i2Color = img2.getRGB(x, y);
                result.setRGB(x, y, subtractColors(i1Color, i2Color));
            }
        }

        return result;

    }

    /**
      * Subtracts 2 images.
      * If images are not the same size, asserts false.
     * @param img1 an image to subtract from.
     * @param img2 an image to subtract.
     * @return difference image
     */
    public static BufferedImage subtractImage(RenderedImage img1,
        RenderedImage img2) {
        return subtractImage(convertRenderedImage(img1),
                convertRenderedImage(img2));
    }

    private static int subtractColors(int rgb1, int rgb2) {
        Color color1 = new Color(rgb1);
        Color color2 = new Color(rgb2);
        int red = subtractColor(color1.getRed(), color2.getRed());
        int green = subtractColor(color1.getGreen(), color2.getGreen());
        int blue = subtractColor(color1.getBlue(), color2.getBlue());

        return (new Color(red, green, blue).getRGB());
    }

    private static int subtractColor(int color1, int color2) {

        if (color1 >= color2) {
            return (color1 - color2);
        } else {
            return (color1 - color2 + 0Xff);
        }
    }

    private static BufferedImage convertRenderedImage(RenderedImage img) {

        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        ColorModel cm = img.getColorModel();
        int width = img.getWidth();
        int height = img.getHeight();
        WritableRaster raster = cm.createCompatibleWritableRaster(width,
                height);
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        Hashtable properties = new Hashtable();
        String[] keys = img.getPropertyNames();

        if (keys != null) {

            for (int i = 0; i < keys.length; i++) {
                properties.put(keys[i], img.getProperty(keys[i]));
            }
        }

        BufferedImage result = new BufferedImage(cm, raster,
                isAlphaPremultiplied, properties);
        img.copyData(raster);

        return result;
    }


}
