package org.datavyu.plugins.ffmpegplayer;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BarPanel extends JPanel {

    private int pixelPerTick = 50;
    private int tickLengthInPixel = 10;
    private int borderWidthInPixel = 50;

    static class Bar {
        private int width;
        private int height;

        Bar(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    static class Range {
        private float minValue;
        private float maxValue;

        Range(float minValue, float maxValue) {
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        int toPixels(float value, int numPixelsForRange) {
            return (int) ((value - minValue )/ getRange() * numPixelsForRange);
        }

        float toValue(int pixel, int numPixelsForRange) {
            return ((float) pixel) / numPixelsForRange * getRange() + minValue;
        }

        float getRange() {
            return maxValue - minValue;
        }
    }

    private List<Bar> bars;
    private Range xRange;
    private Range yRange;
    private int width;
    private int height;
    private int nXTick = 15;
    private int nYTick = 10;

    public BarPanel(int width, int height) {
        this.width = width;
        this.height = height;

        setPreferredSize(new Dimension(width, height));
    }

    public void update(List<Bar> bars, Range xRange, Range yRange) {
        this.bars = bars;
        this.xRange = xRange;
        this.yRange = yRange;
        repaint();
    }

    @Override
    public void paint(Graphics g) {

        // Plot the bars
        int x = 0;
        int y = 0;
        float value = 0f;
        for (Bar bar : bars) {
            // Scale values to pixels
            int w = xRange.toPixels(bar.width, width);
            int h = yRange.toPixels(bar.height, height);
            // Draw bar
            g.drawRect(x, y, w, h);
            x += w;
        }

        // Plot the x ticks
        for (int iXTick = 0; iXTick < nXTick; ++iXTick) {
            x = iXTick*pixelPerTick;
            value = xRange.toValue(x, width);
            g.drawLine(x, 0, x, tickLengthInPixel);
            g.drawString(String.format("%2.2f", value), x, 2*tickLengthInPixel);
        }

        // Plot the y ticks
        for (int iYTick = 0; iYTick < nYTick; ++iYTick) {
            y = iYTick*pixelPerTick;
            value = yRange.toValue(y, height);
            g.drawLine(0, y, tickLengthInPixel, y);
            g.drawString(String.format("%2.2f", value), tickLengthInPixel, y);
        }
    }

    private static void createAndShowGUI() {
        // Create and set up the window
        JFrame frame = new JFrame("Example for the bar panel");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        BarPanel barPanel = new BarPanel(600, 400);
        frame.getContentPane().add(barPanel);
        //frame.add(barPanel, BorderLayout.CENTER);

        // Display the window
        frame.pack();
        frame.setVisible(true);

        List<BarPanel.Bar> bars = new ArrayList<>(Arrays.asList(
                new BarPanel.Bar(10, 10),
                new BarPanel.Bar(10, 20),
                new BarPanel.Bar(10, 30),
                new BarPanel.Bar(10, 40)
        ));
        BarPanel.Range xRange = new BarPanel.Range(0, 45);
        BarPanel.Range yRange = new BarPanel.Range(0, 50);
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
