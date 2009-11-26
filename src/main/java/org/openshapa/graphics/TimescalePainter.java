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
    // The number of intervals to paint on the scale
    private int intervals;
    // Pad the time scale from the left by this amount
    private int paddingLeft;

    // Pad the time scale from the right by this amount
    private int paddingRight;

    /**
     * @return the total number of intervals printed on the panel.
     */
    public int getIntervals() {
        return intervals;
    }

    /**
     * @param intervals the total number of intervals to print on the panel.
     */
    public void setIntervals(int intervals) {
        assert(intervals >= 3);
        this.intervals = intervals;
    }

    // standard date format for clock display.
    private SimpleDateFormat clockFormat;
        
    public TimescalePainter() {
        super();
        clockFormat = new SimpleDateFormat("HH:mm:ss:SSS");
        clockFormat.setTimeZone(new SimpleTimeZone(0, "NO_ZONE"));
        // Default draw scales
        intervals = 20;
        paddingLeft = 101;
        paddingRight = 20;
    }

    /**
     * @return The end time of the scale in milliseconds
     */
    public long getEnd() {
        return end;
    }

    /**
     * @param end The end time of the scale in milliseconds
     */
    public void setEnd(long end) {
        assert(end > start);
        this.end = end;
    }

    /**
     * @return The start time of the scale in milliseconds
     */
    public long getStart() {
        return start;
    }
    
    /**
     * @param start The start time of the scale in milliseconds
     */
    public void setStart(long start) {
        assert(start >= 0);
        this.start = start;
    }

        public int getPaddingLeft() {
        return paddingLeft;
    }

    public void setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    public int getPaddingRight() {
        return paddingRight;
    }

    public void setPaddingRight(int paddingRight) {
        this.paddingRight = paddingRight;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(TimescalePainter.WIDTH, 35);
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

        // Major intervals occur every so often
        String strMax = clockFormat.format(end);
        int majorWidth = 2 * fm.stringWidth(strMax);

        final int effectiveWidth = size.width - paddingLeft - paddingRight;

        // How many major intervals will there be
        final int major = (int)Math.floor((1D * effectiveWidth) / (1D * majorWidth));

        // Usable width - this is where the start to end is
        final int usableWidth = major * majorWidth;

        // Pixel width between intervals
        final float intervalWidth = (majorWidth * 1F) / (intervals - 2F);

         // How many time units is an interval
        final float interval = (end - start) / (usableWidth / intervalWidth);

        // Interval printing and labelling
        for (float x = 0; x <= effectiveWidth; x += majorWidth) {
            g2d.drawLine((int)x + paddingLeft, 0, (int)x + paddingLeft, 25);
            g2d.drawLine((int)x + 1 + paddingLeft, 0, (int)x + 1 + paddingLeft, 25);

            // What time does this interval represent
            float time = start + interval*(x/intervalWidth);
            String strTime = clockFormat.format(time);
            // Don't print if the string will be outside of the panel bounds
            if ((x + paddingLeft + fm.stringWidth(strTime) + 3) < (size.width - paddingRight)) {
                g.drawString(strTime, (int)x + 3 + paddingLeft, 35 - ascent);
            }
        }

        /* Draw the minor intervals separately because mixing minor and major
         * intervals with floating point precision means some major intervals
         * do not get drawn.
         */
        for (float x = 0; x <= effectiveWidth; x += intervalWidth) {
             g2d.drawLine(Math.round(x + paddingLeft), 0, Math.round(x + paddingLeft), 10);
        }

        // Draw the padding
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, paddingLeft, size.height);
        g.fillRect(size.width - paddingRight, 0, paddingRight, size.height);
    }
    
}
