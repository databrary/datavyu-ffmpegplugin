package org.openshapa.graphics;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.text.SimpleDateFormat;
import java.util.SimpleTimeZone;

/**
 * This class is used to paint a timescale for a given range of times.
 */
public class TimescalePainter extends Component {

    // The start time of the scale in milliseconds
    private long start;
    // The end time of the scale in milliseconds
    private long end;
    /* The amount of time represented from interval marking x to interval
     * marking x+1
     */
    private float intervalTime;
    // This is the width in pixels between interval markings
    private float intervalWidth;
    // This is the width in pixels between major interval markings
    private float majorWidth;
    // This is the effective width of the scale, i.e. after padding applied
    private int effectiveWidth;

    // The number of major intervals to paint on the scale
    private static final int MAJOR_INTERVALS = 6;
    // Pad the scale from the left by this many pixels
    private static final int PADDING_LEFT = 101;
    // Pad the scale from the right by this many pixels
    private static final int PADDING_RIGHT = 20;

    /**
     *
     * @param start The start time, in milliseconds, of the scale to display
     * @param end The end time, in milliseconds, of the scale to display
     * @param intervals The total number of intervals between two major intervals,
     * this value is inclusive of the major intervals. i.e. if intervals is 10,
     * then 2 (start and end interval marking) of them are major intervals and 8
     * are minor intervals.
     */
    public void setConstraints(final long start, final long end, final int intervals) {
        this.start = start;
        this.end = end;
        effectiveWidth = this.getWidth() - PADDING_LEFT - PADDING_RIGHT;
        majorWidth = effectiveWidth / MAJOR_INTERVALS;
        intervalWidth = majorWidth / intervals;
        intervalTime = (end - start) / (MAJOR_INTERVALS * intervals);
    }

    // standard date format for clock display.
    private SimpleDateFormat clockFormat;
        
    public TimescalePainter() {
        super();
        clockFormat = new SimpleDateFormat("HH:mm:ss:SSS");
        clockFormat.setTimeZone(new SimpleTimeZone(0, "NO_ZONE"));
    }

    /**
     * @return The end time of the scale in milliseconds
     */
    public long getEnd() {
        return end;
    }

    /**
     * @return The start time of the scale in milliseconds
     */
    public long getStart() {
        return start;
    }
    
    public float getIntervalTime() {
        return intervalTime;
    }

    public float getIntervalWidth() {
        return intervalWidth;
    }
    
    /**
     * This method paints the timing scale.
     * @param g
     */
    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Dimension size = getSize();

        // Used for calculating string dimensions and offsets
        FontMetrics fm = g.getFontMetrics();
        final int ascent = fm.getAscent();

        // Interval printing and labelling
        for (float x = 0; x <= effectiveWidth; x += majorWidth) {
            g2d.drawLine(Math.round(x + PADDING_LEFT), 0,
                    Math.round(x + PADDING_LEFT), 25);
            g2d.drawLine(Math.round(x + 1 + PADDING_LEFT), 0,
                    Math.round(x + 1 + PADDING_LEFT), 25);

            // What time does this interval represent
            float time = start + intervalTime*(x/intervalWidth);
            String strTime = clockFormat.format(time);
            // Don't print if the string will be outside of the panel bounds
            if ((x + PADDING_LEFT + fm.stringWidth(strTime) + 3)
                    < (size.width - PADDING_RIGHT)) {
                g.drawString(strTime, Math.round(x + 3 + PADDING_LEFT),
                        35 - ascent);
            }
        }

        /* Draw the minor intervals separately because mixing minor and major
         * intervals with floating point precision means some major intervals
         * do not get drawn.
         */
        for (float x = 0; x <= effectiveWidth; x += intervalWidth) {
             g2d.drawLine(Math.round(x + PADDING_LEFT), 0,
                     Math.round(x + PADDING_LEFT), 10);
        }

        super.paint(g);

        // Draw the padding
//        g.setColor(Color.WHITE);
//        g.fillRect(0, 0, paddingLeft, size.height);
//        g.fillRect(size.width - paddingRight, 0, paddingRight, size.height);
    }
    
}
