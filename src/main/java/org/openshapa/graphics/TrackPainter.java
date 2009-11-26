package org.openshapa.graphics;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

/**
 * This class is used to paint a track and its information
 */
public class TrackPainter extends Component {

    // The start time of the track in milliseconds
    private long start;
    // The end time of the track in milliseconds
    private long end;
    // The offset of the track in milliseconds
    private long offset;
    // The pixel width of an interval
    private float intervalWidth;
    // The time represented by an interval
    private float intervalTime;
    // The start time of the zoomed window
    private long zoomWindowStart;
    // The end time of the zoomed window
    private long zoomWindowEnd;

    public float getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(float intervalTime) {
        this.intervalTime = intervalTime;
    }

    public float getIntervalWidth() {
        return intervalWidth;
    }

    public void setIntervalWidth(float intervalWidth) {
        this.intervalWidth = intervalWidth;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getZoomWindowEnd() {
        return zoomWindowEnd;
    }

    public void setZoomWindowEnd(long zoomWindowEnd) {
        this.zoomWindowEnd = zoomWindowEnd;
    }

    public long getZoomWindowStart() {
        return zoomWindowStart;
    }

    public void setZoomWindowStart(long zoomWindowStart) {
        this.zoomWindowStart = zoomWindowStart;
    }

    @Override
    public void paint(Graphics g) {
        Dimension size = getSize();

        // Paints the background
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, size.width, size.height);

        // Calculate effective start and end points for the carriage
        long effectiveStart;
        long effectiveEnd;
        int effectiveXOffset;

        if (start + offset >= zoomWindowStart) {
            effectiveStart = start + offset;
            effectiveXOffset = (int)((offset*1F / intervalTime) * intervalWidth);
        } else {
            effectiveStart = zoomWindowStart;
            effectiveXOffset = 0;
        }

        if (end + offset <= zoomWindowEnd) {
            effectiveEnd = end + offset;
        } else {
            effectiveEnd = zoomWindowEnd;
        }

        int carriageHeight = (int)(size.getHeight() * 8D / 10D);
        int carriageWidth = (int)(((effectiveEnd - effectiveStart)*1F /
                intervalTime) * intervalWidth);
        int carriageYOffset = (int)(size.getHeight() / 10D);

        // Paint the carriage
        g.setColor(new Color(130,190,255)); // Light blue
        g.fillRect(effectiveXOffset, carriageYOffset, carriageWidth, carriageHeight);

        // Paint the carriage top and bottom outline
        g.setColor(Color.BLUE);
        g.drawLine(effectiveXOffset, carriageYOffset,
                effectiveXOffset + carriageWidth - 1, carriageYOffset);
        g.drawLine(effectiveXOffset, carriageYOffset + carriageHeight,
                effectiveXOffset + carriageWidth - 1,
                carriageYOffset + carriageHeight);

        // Determine if the left outline should be painted
        if (start + offset >= zoomWindowStart) {
            g.drawLine(effectiveXOffset, carriageYOffset, effectiveXOffset,
                    carriageYOffset + carriageHeight);
        }

        // Determine if the right outline should be painted
        if (end + offset <= zoomWindowEnd) {
            g.drawLine(effectiveXOffset + carriageWidth - 1, carriageYOffset,
                    effectiveXOffset + carriageWidth - 1, 
                    carriageYOffset + carriageHeight);
        }

    }

}
