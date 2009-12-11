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
import org.openshapa.event.NeedleEvent;
import org.openshapa.event.NeedleEventListener;

/**
 * This class paints a timing needle.
 */
public class NeedlePainter extends Component implements InterceptedEventListener {

    /** Amount of padding for this component from the top */
    private int paddingTop;
    /** Amount of padding for this component from the left */
    private int paddingLeft;
    /** Current time represented by the needle */
    private long currentTime;
    /** Current start window visible to the needle */
    private long windowStart;
    /** Current end window visible to the needle */
    private long windowEnd;

    /** Width of one timing interval */
    private float intervalWidth;
    /** Amount of time represented by one timing interval */
    private float intervalTime;

    /** Listeners interested in needle painter events */
    private EventListenerList listenerList;

    /** Inner listener used to handle intercepted events */
    private NeedleListener needleListener;

    /** Polygon region for the needle marker */
    private Polygon needleMarker;

    public NeedlePainter()  {
        super();
        listenerList = new EventListenerList();
        needleListener = new NeedleListener();
//        this.addMouseListener(needleListener);
//        this.addMouseMotionListener(needleListener);
    }
    
    /**
     * Register the listener to be notified of needle events
     * @param listener 
     */
    public synchronized void addNeedleEventListener(NeedleEventListener listener) {
        listenerList.add(NeedleEventListener.class, listener);
    }

    /**
     * Removed the listener from being notified of needle events
     * @param listener 
     */
    public synchronized void removeNeedleEventListener(NeedleEventListener listener) {
        listenerList.remove(NeedleEventListener.class, listener);
    }

    /**
     * @return Current time represented by the needle.
     */
    public long getCurrentTime() {
        return currentTime;
    }

    /**
     * Set the current time to be represented by the needle.
     * @param currentTime
     */
    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    /**
     * @return The end of the visible timing window.
     */
    public long getWindowEnd() {
        return windowEnd;
    }

    /**
     * Set the end of the visible timing window.
     * @param windowEnd
     */
    public void setWindowEnd(long windowEnd) {
        this.windowEnd = windowEnd;
    }

    /**
     * @return The start of the visible timing window.
     */
    public long getWindowStart() {
        return windowStart;
    }

    /**
     * @param windowStart Set the start of the visible timing window.
     */
    public void setWindowStart(long windowStart) {
        this.windowStart = windowStart;
    }    

    /**
     * @return Time represented by an interval width used to calculate needle
     * position in pixels.
     */
    public float getIntervalTime() {
        return intervalTime;
    }

    /**
     * Set the time represented by an interval width used to calculate needle 
     * position in pixels.
     * @param intervalTime
     */
    public void setIntervalTime(float intervalTime) {
        this.intervalTime = intervalTime;
    }

    /**
     * @return Interval width used to calculate needle position in pixels.
     */
    public float getIntervalWidth() {
        return intervalWidth;
    }

    /**
     * Set the interval width used to calculate needle position in pixels.
     * @param intervalWidth 
     */
    public void setIntervalWidth(float intervalWidth) {
        this.intervalWidth = intervalWidth;
    }

    /**
     * @return Amount of pixels this component is being padded from the left
     */
    public int getPaddingLeft() {
        return paddingLeft;
    }

    /**
     * Set the amount of pixels this component is being padded from the left
     * @param paddingLeft
     */
    public void setPaddingLeft(int paddingLeft) {
        assert(paddingLeft >= 0);
        this.paddingLeft = paddingLeft;
    }

    /**
     * @return Amount of pixels this component is being padded from the top
     */
    public int getPaddingTop() {
        return paddingTop;
    }

    /**
     * Set the amount of pixels this component is being padded from the top
     * @param paddingTop
     */
    public void setPaddingTop(int paddingTop) {
        assert(paddingTop >= 0);
        this.paddingTop = paddingTop;
    }

    @Override
    public void paint(Graphics g) {
        // Don't paint if the needle is out of the current window
        if ((currentTime < windowStart) || (windowEnd < currentTime)) {
            return;
        }

        Dimension size = this.getSize();

        g.setColor(new Color(250, 0, 0, 100));

        // Calculate the needle position based on the selected time
        float ratio = intervalWidth / intervalTime;
        int pos = Math.round(currentTime * ratio
                - windowStart * ratio) + paddingLeft;

        needleMarker = new Polygon();
        needleMarker.addPoint(pos - 10, paddingTop);
        needleMarker.addPoint(pos + 11, paddingTop);
        needleMarker.addPoint(pos + 1, 19);
        needleMarker.addPoint(pos, 19);

        g.fillPolygon(needleMarker);
        
        g.setColor(Color.red);
        g.drawPolygon(needleMarker);

        // Draw the timing needle
        int x1 = pos;
        int y1 = paddingTop + 19;
        int x2 = pos + 1;
        int y2 = size.height;

        g.drawLine(x1, y1, x1, y2);
        g.drawLine(x2, y1, x2, y2);

    }

    /**
     * Used to fire a new event informing listeners about the new needle time.
     * @param newTime
     */
    private synchronized void fireNeedleEvent(long newTime) {
        NeedleEvent e = new NeedleEvent(this, newTime);
        Object[] listeners = listenerList.getListenerList();
        /* The listener list contains the listening class and then the listener
         * instance.
         */
        for (int i = 0; i < listeners.length; i += 2) {
           if (listeners[i] == NeedleEventListener.class) {
               ((NeedleEventListener)listeners[i+1]).needleMoved(e);
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
                    needleListener.mouseEntered(mouseEvent);
                    break;
                case MOUSE_MOVED: 
                    needleListener.mouseMoved(mouseEvent);
                    break;
                case MOUSE_PRESSED:
                    needleListener.mousePressed(mouseEvent);
                    break;
                case MOUSE_DRAGGED: 
                    needleListener.mouseDragged(mouseEvent);
                    break;
                case MOUSE_RELEASED:
                    needleListener.mouseReleased(mouseEvent);
                    break;
                default: break;
            }
        }
    }
    
    /**
     * Inner class used to handle intercepted events.
     */
    private class NeedleListener {
        private boolean onNeedle;

        public NeedleListener() {
            onNeedle = false;
        }

        public void mouseEntered(MouseEvent e) {
            Component source = (Component)e.getSource();
            if (needleMarker.contains(e.getPoint())) {
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
            if (needleMarker.contains(e.getPoint())) {
                // Mouse is pressed on the needle.
                onNeedle = true;
                source.setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
            } else {
                onNeedle = false;
                source.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        }

        public void mouseDragged(MouseEvent e) {
            if (onNeedle) {
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
                fireNeedleEvent(Math.round(newTime));
            }
        }

        public void mouseReleased(MouseEvent e) {
            onNeedle = false;
            Component source = (Component)e.getSource();
            source.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

    }

}
