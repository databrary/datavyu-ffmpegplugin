/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openshapa.graphics;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import javax.swing.event.EventListenerList;
import javax.swing.event.MouseInputAdapter;
import org.openshapa.graphics.event.NeedleEvent;
import org.openshapa.graphics.event.NeedleEventListener;

/**
 * This class paints a timing needle.
 */
public class NeedlePainter extends Component {

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

    /** The top left x coodinate of the needle */
    private int x1;
    /** The top left y coordinate of the needle */
    private int y1;
    /** The bottom right x coordinate of the needle */
    private int x2;
    /** The bottom right y coordinate of the needle */
    private int y2;

    /** Listeners interested in needle painter events */
    private EventListenerList listenerList;

    public NeedlePainter() {
        super();
        listenerList = new EventListenerList();
        NeedleListener needleListener = new NeedleListener();
        this.addMouseListener(needleListener);
        this.addMouseMotionListener(needleListener);
    }

    /**
     * @param listener Register the listener to be notified of needle events
     */
    public synchronized void addNeedleEventListener(NeedleEventListener listener) {
        listenerList.add(NeedleEventListener.class, listener);
    }

    /**
     * @param listener De-register the listener of needle events
     */
    public synchronized void removeNeedleEventListener(NeedleEventListener listener) {
        listenerList.remove(NeedleEventListener.class, listener);
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public long getWindowEnd() {
        return windowEnd;
    }

    public void setWindowEnd(long windowEnd) {
        this.windowEnd = windowEnd;
    }

    public long getWindowStart() {
        return windowStart;
    }

    public void setWindowStart(long windowStart) {
        this.windowStart = windowStart;
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

    public void setPadding(final int paddingTop, final int paddingLeft) {
        assert(paddingTop >= 0);
        assert(paddingLeft >= 0);
        this.paddingTop = paddingTop;
        this.paddingLeft = paddingLeft;
    }

    @Override
    public void paint(Graphics g) {
        // Don't paint if the needle is out of the current window
        if ((currentTime < windowStart) || (windowEnd < currentTime)) {
            return;
        }

        Dimension size = this.getSize();

        g.setColor(Color.red);

        float ratio = intervalWidth / intervalTime;
        int pos = Math.round(currentTime * ratio - windowStart * ratio);

        x1 = pos + paddingLeft;
        y1 = paddingTop;
        x2 = pos + paddingLeft + 1;
        y2 = size.height;

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
     * Inner class used to listen and parse mouse events to support needle
     * movement by mouse.
     */
    private class NeedleListener extends MouseInputAdapter {
        private boolean onNeedle;

        public NeedleListener() {
            super();
            onNeedle = false;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if (x1 <= e.getX() && e.getX() <= x2 &&
                    y1 <= e.getY() && e.getY() <= y2) {
                setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
            } else {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            mouseEntered(e);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (x1 <= e.getX() && e.getX() <= x2 &&
                    y1 <= e.getY() && e.getY() <= y2) {
                // Mouse is pressed on the needle.
                onNeedle = true;
                setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
            } else {
                onNeedle = false;
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        }

        @Override
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

        @Override
        public void mouseReleased(MouseEvent e) {
            onNeedle = false;
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

    }

}
