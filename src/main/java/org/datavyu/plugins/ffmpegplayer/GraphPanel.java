package org.datavyu.plugins.ffmpegplayer;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GraphPanel extends JPanel {

    private int tickLengthInPixel = 10;

    static class Point {
        private float x;
        private float y;

        Point(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    static class Range {
        private float minValue;
        private float maxValue;
        private float deltaTickValue;
        private int nTick;

        Range(float minValue, float maxValue, float deltaTickValue) {
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.deltaTickValue = deltaTickValue;
            this.nTick = 1 + (int)(getRange()/deltaTickValue);
        }

        int toPixels(float value, int numPixelsForRange) {
            return (int) ((value - minValue )/ getRange() * numPixelsForRange);
        }

        float getRange() {
            return maxValue - minValue;
        }

        int getNumTick() {
            return nTick;
        }

        float getMinValue() {
            return minValue;
        }

        float getDeltaTickValue() {
            return deltaTickValue;
        }
    }

    private List<Point> points = new ArrayList<>(1);
    private Range xRange = new Range(0, 1, 1);
    private Range yRange = new Range(0, 1, 1);
    private int width;
    private int height;

    public GraphPanel(int width, int height) {
        this.width = width;
        this.height = height;
        points.add(new Point(0, 0)); // add dummy
        setPreferredSize(new Dimension(width, height));
    }

    public void update(final List<Point> points, final Range xRange, final Range yRange) {
        this.points = points;
        this.xRange = xRange;
        this.yRange = yRange;
        repaint();
    }

    @Override
    public void paint(Graphics g) {

        // Get the first point
        Point first = points.remove(0);
        int xOld = xRange.toPixels(first.x, width);
        int yOld = yRange.toPixels(first.y, height);

        for (Point point : points) {
            // Scale
            int x = xRange.toPixels(point.x, width);
            int y = yRange.toPixels(point.y, height);

            // Draw
            g.drawLine(xOld, yOld, x, y);

            // Update
            xOld = x;
            yOld = y;
        }

        // Plot the x ticks
        for (int iXTick = 0; iXTick < xRange.getNumTick(); ++iXTick) {
            float value = iXTick*xRange.getDeltaTickValue() + xRange.getMinValue();
            int x = xRange.toPixels(value, width);
            g.drawLine(x, 0, x, tickLengthInPixel);
            g.drawString(String.format("%2.2f", value), x, 2*tickLengthInPixel);
        }

        // Plot the y ticks
        for (int iYTick = 0; iYTick < yRange.getNumTick(); ++iYTick) {
            float value = iYTick*yRange.getDeltaTickValue() + yRange.getMinValue();
            int y = yRange.toPixels(value, height);
            g.drawLine(0, y, tickLengthInPixel, y);
            g.drawString(String.format("%2.2f", value), tickLengthInPixel, y);
        }
    }

    private static void createAndShowGUI() {
        // Create and set up the window
        JFrame frame = new JFrame("Example for the bar panel");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        GraphPanel barPanel = new GraphPanel(600, 400);
        frame.getContentPane().add(barPanel);
        //frame.add(barPanel, BorderLayout.CENTER);

        // Display the window
        frame.pack();
        frame.setVisible(true);

        List<Point> bars = new ArrayList<>(Arrays.asList(
                new Point(-45, -50),
                new Point(-25, 20),
                new Point(10, 30),
                new Point(30, 50)
        ));
        GraphPanel.Range xRange = new GraphPanel.Range(-45, 45, 5);
        GraphPanel.Range yRange = new GraphPanel.Range(-50, 50, 20);
        barPanel.update(bars, xRange, yRange);
    }

    public static void main(String[] args) {

        /* Turn off metal's use of bold fonts */
        UIManager.put("swing.boldMetal", Boolean.FALSE);

        // Log info only
        Configurator.setRootLevel(Level.INFO);

        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(()->createAndShowGUI());
    }


}
