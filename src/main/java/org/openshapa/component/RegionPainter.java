package org.openshapa.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.event.EventListenerList;
import org.openshapa.event.InterceptedEvent;
import org.openshapa.event.InterceptedEventListener;
import org.openshapa.event.MarkerEvent;
import org.openshapa.event.MarkerEvent.Marker;
import org.openshapa.event.MarkerEventListener;

/**
 * This class paints the custom playback region.
 */
public class RegionPainter extends Component implements InterceptedEventListener {

    /** Amount of padding for this component from the top */
    private int paddingTop;
    /** Amount of padding for this component from the left */
    private int paddingLeft;
    /** Current start window visible to the region */
    private long windowStart;
    /** Current end window visible to the region */
    private long windowEnd;
    /** Width of one timing interval */
    private float intervalWidth;
    /** Amount of time represented by one timing interval */
    private float intervalTime;
    /** The earliest start time of a data viewer */
    private long minStart;
    /** The latest end time of a data viewer */
    private long maxEnd;
    /** Start of the custom playback region */
    private long regionStart;
    /** End of the custom playback region */
    private long regionEnd;
    /** Polygon region for the start marker */
    private Polygon startMarkerPolygon;
    /** Polygon region for the end marker */
    private Polygon endMarkerPolygon;
    /** Inner listener used to handle intercepted events */
    private RegionMarkerListener markerListener;
    /** Listeners interested in custom playback region events */
    private EventListenerList listenerList;

    public RegionPainter() {
        super();
        markerListener = new RegionMarkerListener();
        listenerList = new EventListenerList();
    }

    /**
     * @return the latest end time of a data viewer
     */
    public long getMaxEnd() {
        return maxEnd;
    }

    /**
     * Set the latest end time of a data viewer
     * @param maxEnd
     */
    public void setMaxEnd(long maxEnd) {
        this.maxEnd = maxEnd;
    }

    /**
     * @return The earliest start time of a data viewer
     */
    public long getMinStart() {
        return minStart;
    }

    /**
     * Set the earliest start time of a data viewer
     * @param minStart
     */
    public void setMinStart(long minStart) {
        this.minStart = minStart;
    }

    /**
     * @return End of the custom playback region
     */
    public long getRegionEnd() {
        return regionEnd;
    }

    /**
     * Set the end of the custom playback region
     * @param regionEnd
     */
    public void setRegionEnd(long regionEnd) {
        this.regionEnd = regionEnd;
    }

    /**
     * @return Start of the custom playback region
     */
    public long getRegionStart() {
        return regionStart;
    }

    /**
     * Set start of the custom playback region
     * @param regionStart
     */
    public void setRegionStart(long regionStart) {
        this.regionStart = regionStart;
    }

    /**
     * @return Amount of time represented by one timing interval
     */
    public float getIntervalTime() {
        return intervalTime;
    }

    /**
     * Set the amount of time represented by one timing interval
     * @param intervalTime
     */
    public void setIntervalTime(float intervalTime) {
        this.intervalTime = intervalTime;
    }

    /**
     * @return Width of one timing interval
     */
    public float getIntervalWidth() {
        return intervalWidth;
    }

    /**
     * Set the width of one timing interval
     * @param intervalWidth
     */
    public void setIntervalWidth(float intervalWidth) {
        this.intervalWidth = intervalWidth;
    }

    /**
     * @return current end window visible to the region
     */
    public long getWindowEnd() {
        return windowEnd;
    }

    /**
     * Set the current end window visible to the region
     * @param windowEnd
     */
    public void setWindowEnd(long windowEnd) {
        this.windowEnd = windowEnd;
    }

    /**
     * @return Current start window visible to the region
     */
    public long getWindowStart() {
        return windowStart;
    }

    /**
     * Set the current start window visible to the region
     * @param windowStart
     */
    public void setWindowStart(long windowStart) {
        this.windowStart = windowStart;
    }

    /**
     * @return amount of padding for this component from the left
     */
    public int getPaddingLeft() {
        return paddingLeft;
    }

    /**
     * Set the amount of padding for this component from the left
     * @param paddingLeft
     */
    public void setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    /**
     * @return amount of padding for this component from the top
     */
    public int getPaddingTop() {
        return paddingTop;
    }

    /**
     * Set the amount of padding for this component from the top
     * @param paddingTop
     */
    public void setPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
    }

    /**
     * Register the listener to be notified of region marker events
     * @param listener
     */
    public synchronized void addMarkerEventListener(MarkerEventListener listener) {
        listenerList.add(MarkerEventListener.class, listener);
    }

    /**
     * Removed the listener from being notified of region marker events
     * @param listener
     */
    public synchronized void removeMarkerEventListener(MarkerEventListener listener) {
        listenerList.remove(MarkerEventListener.class, listener);
    }

    /**
     * Used to inform listeners about the change of a region marker.
     * @param marker
     * @param time
     */
    private synchronized void fireMarkerEvent(Marker marker, long time) {
        MarkerEvent e = new MarkerEvent(this, marker, time);
        Object[] listeners = listenerList.getListenerList();
        /* The listener list contains the listening class and then the listener
         * instance.
         */
        for (int i = 0; i < listeners.length; i += 2) {
           if (listeners[i] == MarkerEventListener.class) {
               ((MarkerEventListener)listeners[i+1]).markerMoved(e);
           }
        }
    }

    /**
     * Handle intercepted events.
     * @param e
     */
    public void eventIntercepted(InterceptedEvent e) {
        EventObject event = e.getInterceptedEvent();
        if (event instanceof MouseEvent) {
            MouseEvent mouseEvent = (MouseEvent) event;
            switch(e.getEvent()) {
                case MOUSE_ENTERED:
                    markerListener.mouseEntered(mouseEvent);
                    break;
                case MOUSE_MOVED:
                    markerListener.mouseMoved(mouseEvent);
                    break;
                case MOUSE_PRESSED:
                    markerListener.mousePressed(mouseEvent);
                    break;
                case MOUSE_DRAGGED:
                    markerListener.mouseDragged(mouseEvent);
                    break;
                case MOUSE_RELEASED:
                    markerListener.mouseReleased(mouseEvent);
                    break;
                default: break;
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        Dimension size = this.getSize();

        final float ratio = intervalWidth / intervalTime;

        // If the left region marker is visible, paint the marker
        if (regionStart >= windowStart) {
            g.setColor(new Color(15, 135, 0, 100)); // Semi-transparent green
            // The polygon tip
            int pos = Math.round(regionStart * ratio
                    - windowStart * ratio) + paddingLeft;

            // Make an arrow
            startMarkerPolygon = new Polygon();
            startMarkerPolygon.addPoint(pos - 10, paddingTop);
            startMarkerPolygon.addPoint(pos, 19 + paddingTop);
            startMarkerPolygon.addPoint(pos, 37 + paddingTop);
            startMarkerPolygon.addPoint(pos - 10, 37 + paddingTop);
            g.fillPolygon(startMarkerPolygon);

            // Draw outline
            g.setColor(new Color(15, 135, 0));
            g.drawPolygon(startMarkerPolygon);

            // Draw drop down line
            g.drawLine(pos, 37, pos, size.height);
        }

        // If the right region marker is visible, paint the marker
        if (regionEnd <= windowEnd) {
            g.setColor(new Color(15, 135, 0, 100)); // Semi-transparent green

            // The polygon tip
            int pos = Math.round(regionEnd * ratio
                    - windowStart * ratio) + paddingLeft;
            endMarkerPolygon = new Polygon();
            endMarkerPolygon.addPoint(pos + 1, 19 + paddingTop);
            endMarkerPolygon.addPoint(pos + 11, paddingTop);
            endMarkerPolygon.addPoint(pos + 11, 37 + paddingTop);
            endMarkerPolygon.addPoint(pos + 1, 37 + paddingTop);


            g.fillPolygon(endMarkerPolygon);

            // Draw outline
            g.setColor(new Color(15, 135, 0));
            g.drawPolygon(endMarkerPolygon);

            // Draw drop down line
            g.drawLine(pos + 1, 37, pos + 1, size.height);
        }

        /* Check if the selected region is not the maximum viewing window,
         * if it is not the maximum, highlight the areas over the tracks.
         */
        if ((regionStart > minStart) || (regionEnd < maxEnd)) {
            long visibleStartRegion = regionStart >= windowStart 
                    ? regionStart
                    : windowStart;
            long visibleEndRegion = regionEnd <= windowEnd
                    ? regionEnd
                    : windowEnd;

            int startPos = Math.round(visibleStartRegion * ratio -
                    windowStart * ratio) + paddingLeft;
            int endPos = Math.round(visibleEndRegion * ratio -
                    windowStart * ratio) + paddingLeft + 1;

            g.setColor(new Color(15, 135, 0, 100));
            g.fillRect(startPos, 37, endPos - startPos, size.height);
        }

    }

    /**
     * Inner class used to handle intercepted events.
     */
    private class RegionMarkerListener {
        private boolean onStartMarker;
        private boolean onEndMarker;

        public RegionMarkerListener() {
            onStartMarker = false;
            onEndMarker = false;
        }

        public void mouseEntered(MouseEvent e) {
            Component source = (Component)e.getSource();
            if (startMarkerPolygon.contains(e.getPoint())) {
                source.setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
            } else if (endMarkerPolygon.contains(e.getPoint())) {
                source.setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
            } else {
                source.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        }

        public void mouseMoved(MouseEvent e) {
            mouseEntered(e);
        }

        public void mousePressed(MouseEvent e) {
            Component source = (Component)e.getSource();
            if (startMarkerPolygon.contains(e.getPoint())) {
                // Mouse is pressed on the needle.
                onStartMarker = true;
                source.setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
            } else if (endMarkerPolygon.contains(e.getPoint())){
                onEndMarker = true;
                source.setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
            } else {
                onStartMarker = false;
                onEndMarker = false;
                source.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        }

        public void mouseDragged(MouseEvent e) {
            if (onStartMarker) {
                int x = e.getX();
                // Bound the x values
                if (x < 0) {
                    x = 0;
                }
                if (x > getSize().width) {
                    x = getSize().width;
                }

                // Calculate the time represented by the new location
                float ratio = intervalWidth / intervalTime;
                float newTime = (x-paddingLeft + windowStart*ratio)/ratio;
                if (newTime < 0) {
                    newTime = 0;
                }
                if (newTime > windowEnd) {
                    newTime = windowEnd;
                }
                if (newTime > regionEnd) {
                    newTime = regionEnd;
                }
                fireMarkerEvent(Marker.START_MARKER, Math.round(newTime));
            } else if (onEndMarker) {
                int x = e.getX();
                // Bound the x values
                if (x < 0) {
                    x = 0;
                }
                if (x > getSize().width) {
                    x = getSize().width;
                }

                // Calculate the time represented by the new location
                float ratio = intervalWidth / intervalTime;
                float newTime = (x-paddingLeft + windowStart*ratio)/ratio;
                if (newTime < regionStart) {
                    newTime = regionStart;
                }
                if (newTime > windowEnd) {
                    newTime = windowEnd;
                }
                fireMarkerEvent(Marker.END_MARKER, Math.round(newTime));
            }
        }

        public void mouseReleased(MouseEvent e) {
            onStartMarker = false;
            onEndMarker = false;
            Component source = (Component)e.getSource();
            source.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

}
