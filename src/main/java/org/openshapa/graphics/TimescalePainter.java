package org.openshapa.graphics;

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
    // How often do we have a major interval
    private int major;

    /**
     * @return frequency at which a major interval is printed, i.e. a value of 5
     * means every 5th interval is a major interval.
     */
    public int getMajor() {
        return major;
    }

    /**
     * @param major how often to print a major interval, i.e. if major = 5, then
     * every 5th interval is a major interval and timing information will also
     * be printed.
     */
    public void setMajor(int major) {
        assert(major > 0);
        this.major = major;
    }

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
        assert(intervals > 0);
        this.intervals = intervals;
    }

    // standard date format for clock display.
    private SimpleDateFormat clockFormat;
        
    public TimescalePainter() {
        super();
        clockFormat = new SimpleDateFormat("HH:mm:ss:SSS");
        clockFormat.setTimeZone(new SimpleTimeZone(0, "NO_ZONE"));
        // Default draw scales
        intervals = 100;
        major = 10;
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

    public Dimension getPreferredSize() {
        return new Dimension(this.WIDTH, 50);
    }

    /**
     * This method paints the timing scale.
     * @param g
     */
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Dimension size = getSize();

        // Used for calculating string dimensions
        FontMetrics fm = g.getFontMetrics();

        // Pixel width between intervals
        final int intervalWidth = size.width / intervals;

         // How many time units is an interval
        final long interval = (end - start) / intervals;

        for (int x = 0; x <= size.width; x += intervalWidth) {
            // Is this a major interval
            if (x%(intervalWidth*major) == 0) {
                g2d.drawLine(x, 0, x, 20);
                // What time does this interval represent
                long time = start + interval*(x/intervalWidth);
                String strTime = clockFormat.format(time);
                int strX = x - (fm.stringWidth(strTime)/2);
                // Don't print if the string will be outside of the panel bounds
                if ((strX > 0) &&
                        ((x + (fm.stringWidth(strTime)/2)) < size.width)) {
                    g.drawString(strTime, strX, 35);
                }
            } else {
                g2d.drawLine(x, 0, x, 10);
            }
        }
    }
    
}
