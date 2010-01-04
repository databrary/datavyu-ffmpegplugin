package org.openshapa.component.controller;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.event.EventListenerList;
import org.openshapa.component.NeedlePainter;
import org.openshapa.component.model.NeedleModel;
import org.openshapa.component.model.ViewableModel;
import org.openshapa.event.InterceptedEvent;
import org.openshapa.event.InterceptedEventListener;
import org.openshapa.event.NeedleEvent;
import org.openshapa.event.NeedleEventListener;

/**
 * NeedleController is responsible for managing a NeedlePainter
 */
public class NeedleController implements InterceptedEventListener {
    /** View */
    private NeedlePainter view;
    /** Models */
    private NeedleModel needleModel;
    private ViewableModel viewableModel;
    /** Inner listener for handling intercepted events */
    private NeedleListener needleListener;
    /** Listeners interested in needle painter events */
    private EventListenerList listenerList;
    
    public NeedleController() {
        view = new NeedlePainter();

        needleModel = new NeedleModel();
        needleModel.setPaddingTop(0);
        needleModel.setPaddingLeft(101);

        viewableModel = new ViewableModel();
        
        view.setViewableModel(viewableModel);
        view.setNeedleModel(needleModel);

        needleListener = new NeedleListener();

        listenerList = new EventListenerList();
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
     * Set the current time to be represented by the needle.
     * @param currentTime
     */
    public void setCurrentTime(long currentTime) {
        needleModel.setCurrentTime(currentTime);
        view.setNeedleModel(needleModel);
    }

    /**
     * @return Current time represented by the needle
     */
    public long getCurrentTime() {
        return needleModel.getCurrentTime();
    }

    /**
     * @return a clone of the viewable model
     */
    public ViewableModel getViewableModel() {
        // return a clone to avoid model tainting
        return (ViewableModel)viewableModel.clone();
    }

    /**
     * Copies the given viewable model
     * @param viewableModel
     */
    public void setViewableModel(ViewableModel viewableModel) {
        /* Just copy the values, do not spread references all over the place to
         * avoid model tainting.
         */
        this.viewableModel.setEnd(viewableModel.getEnd());
        this.viewableModel.setIntervalTime(viewableModel.getIntervalTime());
        this.viewableModel.setIntervalWidth(viewableModel.getIntervalWidth());
        this.viewableModel.setZoomWindowEnd(viewableModel.getZoomWindowEnd());
        this.viewableModel.setZoomWindowStart(viewableModel.getZoomWindowStart());
        view.setViewableModel(this.viewableModel);
    }

    /**
     * @return View used by the controller
     */
    public Component getView() {
        return view;
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

        private final Cursor eastResizeCursor = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
        private final Cursor defaultCursor = Cursor.getDefaultCursor();

        public NeedleListener() {
            onNeedle = false;
        }

        public void mouseEntered(MouseEvent e) {
            Polygon p = view.getNeedleMarker();
            Component source = (Component)e.getSource();
            if (p.contains(e.getPoint())) {
                source.setCursor(eastResizeCursor);
            } else {
                source.setCursor(defaultCursor);
            }
        }

        public void mouseMoved(MouseEvent e) {
            mouseEntered(e);
        }

        public void mousePressed(MouseEvent e) {
            Polygon p = view.getNeedleMarker();
            Component source = (Component)e.getSource();
            if (p.contains(e.getPoint())) {
                // Mouse is pressed on the needle.
                onNeedle = true;
                source.setCursor(eastResizeCursor);
            } else {
                onNeedle = false;
                source.setCursor(defaultCursor);
            }
        }

        public void mouseDragged(MouseEvent e) {
            if (onNeedle) {
                int x = e.getX();
                // Bound the x values
                if (x < 0) {
                    x = 0;
                }
                if (x > view.getSize().width) {
                    x = view.getSize().width;
                }

                // Calculate the time represented by the new location
                float ratio = viewableModel.getIntervalWidth() / viewableModel.getIntervalTime();
                float newTime = (x - needleModel.getPaddingLeft() + viewableModel.getZoomWindowStart() * ratio) / ratio;
                if (newTime < 0) {
                    newTime = 0;
                }
                if (newTime > viewableModel.getZoomWindowEnd()) {
                    newTime = viewableModel.getZoomWindowEnd();
                }
                fireNeedleEvent(Math.round(newTime));
            }
        }

        public void mouseReleased(MouseEvent e) {
            onNeedle = false;
            Component source = (Component)e.getSource();
            source.setCursor(defaultCursor);
        }

    }

}
