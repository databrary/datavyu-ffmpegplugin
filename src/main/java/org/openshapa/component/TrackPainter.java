package org.openshapa.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputAdapter;

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
    // Is there an error with track information
    private boolean error;

    public TrackPainter() {
        super();
        error = false;
        TrackPainterListener tpl = new TrackPainterListener();
        this.addMouseListener(tpl);
        this.addMouseMotionListener(tpl);
    }

    public boolean isError() {
        return error;
    }

    /**
     * @param error Set to true if there is an error with trying to determine
     * any aspect of track information. If true, the carriage will not be
     * painted and an error message will be printed in place.
     */
    public void setError(boolean error) {
        this.error = error;
    }

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


        // If there is an error with track information, don't paint the carriage
        if (error) {
            g.setColor(Color.red);
            FontMetrics fm = g.getFontMetrics();
            String errorMessage = "Track timing information could not be calculated.";
            int width = fm.stringWidth(errorMessage);
            g.drawString(errorMessage, (size.width / 2) - (width / 2),
                    (size.height / 2) - (fm.getAscent() / 2));
            return;
        }

        // Calculate effective start and end points for the carriage
//        long effectiveStart;
//        long effectiveEnd;
        float effectiveXOffset;
        /* Calculating carriage width by deleting offsets and remainders because
         * using the displayed scale's measurements will sometimes result in a
         * carriage with a visually inaccurate representation (gap from the
         * displayed end of the carriage to the carriage holder's border)
         */
        float carriageWidth = size.width;

        if (start + offset >= zoomWindowStart) {
//            effectiveStart = start + offset;
            effectiveXOffset = ((offset*1F / intervalTime) * intervalWidth);
            carriageWidth -= effectiveXOffset;
        } else {
//            effectiveStart = zoomWindowStart;
            effectiveXOffset = 0;
        }

        if (end + offset <= zoomWindowEnd) {
//            effectiveEnd = end + offset;
            carriageWidth -= (zoomWindowEnd - (end + offset)) / intervalTime * intervalWidth;
        } else {
//            effectiveEnd = zoomWindowEnd;
        }

        int carriageHeight = (int)(size.getHeight() * 8D / 10D);
//        float carriageWidth = (((effectiveEnd - effectiveStart)*1F) / intervalTime) * intervalWidth;
        int carriageYOffset = (int)(size.getHeight() / 10D);

        // Paint the carriage
        g.setColor(new Color(130,190,255)); // Light blue
        g.fillRect(Math.round(effectiveXOffset), carriageYOffset, Math.round(carriageWidth), carriageHeight);

        // Paint the carriage top and bottom outline
        g.setColor(Color.BLUE);
        g.drawLine(Math.round(effectiveXOffset),
                carriageYOffset,
                Math.round(effectiveXOffset + carriageWidth - 1),
                carriageYOffset);
        g.drawLine(Math.round(effectiveXOffset),
                carriageYOffset + carriageHeight,
                Math.round(effectiveXOffset + carriageWidth - 1),
                carriageYOffset + carriageHeight);

        // Determine if the left outline should be painted
        if (start + offset >= zoomWindowStart) {
            g.drawLine(Math.round(effectiveXOffset), 
                    carriageYOffset,
                    Math.round(effectiveXOffset),
                    carriageYOffset + carriageHeight);
        }

        // Determine if the right outline should be painted
        if (end + offset <= zoomWindowEnd) {
            g.drawLine(Math.round(effectiveXOffset + carriageWidth - 1),
                    carriageYOffset,
                    Math.round(effectiveXOffset + carriageWidth - 1),
                    carriageYOffset + carriageHeight);
        }

    }

    private class TrackPainterListener extends MouseInputAdapter {

        @Override
        public void mouseEntered(MouseEvent e) {
            System.out.println("Entered the track painter");
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            System.out.println("Mouse moved in the track painter");
        }

    }

}
