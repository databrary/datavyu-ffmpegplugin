package org.openshapa.component.controller;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import javax.swing.event.EventListenerList;
import javax.swing.event.MouseInputAdapter;
import org.openshapa.component.RegionPainter;
import org.openshapa.component.model.RegionModel;
import org.openshapa.component.model.ViewableModel;
import org.openshapa.event.MarkerEvent;
import org.openshapa.event.MarkerEvent.Marker;
import org.openshapa.event.MarkerEventListener;

/**
 * RegionController is responsible for managing a RegionPainter
 */
public class RegionController {
    /** View */
    private RegionPainter view;
    /** Models */
    private RegionModel regionModel;
    private ViewableModel viewableModel;
    /** Inner listener for handling intercepted events */
    private RegionMarkerListener regionMarkerListener;
    /** Listeners interested in changes to the playback region */
    private EventListenerList listenerList;

    public RegionController() {
        view = new RegionPainter();

        regionModel = new RegionModel();
        regionModel.setPaddingTop(0);
        regionModel.setPaddingLeft(101);

        viewableModel = new ViewableModel();

        view.setViewableModel(viewableModel);
        view.setRegionModel(regionModel);

        regionMarkerListener = new RegionMarkerListener();
        view.addMouseListener(regionMarkerListener);
        view.addMouseMotionListener(regionMarkerListener);

        listenerList = new EventListenerList();
    }

    /**
     * Sets the playback region to visualise
     * @param start start of the playback region in milliseconds
     * @param end end of the playback region in milliseconds
     */
    public void setPlaybackRegion(long start, long end) {
        regionModel.setRegionStart(start);
        regionModel.setRegionEnd(end);
        view.setRegionModel(regionModel);
    }

    /**
     * Sets the start of the playback region to visualise
     * @param start
     */
    public void setPlaybackRegionStart(long start) {
        regionModel.setRegionStart(start);
        view.setRegionModel(regionModel);
    }

    /**
     * Sets the end of the playback region to visualise
     * @param end
     */
    public void setPlaybackRegionEnd(long end) {
        regionModel.setRegionEnd(end);
        view.setRegionModel(regionModel);
    }

    /**
     * @return View used by the controller
     */
    public Component getView() {
        return view;
    }

    /**
     * @return returns a clone of the viewable model
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
     * Register the listener to be notified of region marker events
     * @param listener
     */
    public synchronized void addMarkerEventListener(MarkerEventListener listener) {
        listenerList.add(MarkerEventListener.class, listener);
    }

    /**
     * Remove the listener from being notified of region marker events
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
     * Inner class used to handle intercepted events.
     */
    private class RegionMarkerListener extends MouseInputAdapter {
        private boolean onStartMarker;
        private boolean onEndMarker;

        private final Cursor eastResizeCursor = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
        private final Cursor defaultCursor = Cursor.getDefaultCursor();

        public RegionMarkerListener() {
            onStartMarker = false;
            onEndMarker = false;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            Component source = (Component)e.getSource();
            final Polygon startMarker = view.getStartMarkerPolygon();
            final Polygon endMarker = view.getEndMarkerPolygon();
            if (startMarker.contains(e.getPoint())) {
                source.setCursor(eastResizeCursor);
            } else if (endMarker.contains(e.getPoint())) {
                source.setCursor(eastResizeCursor);
            } else {
                source.setCursor(defaultCursor);
            }
            System.out.printf("In the region controller!");
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            mouseEntered(e);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            Component source = (Component)e.getSource();
            final Polygon startMarker = view.getStartMarkerPolygon();
            final Polygon endMarker = view.getEndMarkerPolygon();
            if (startMarker.contains(e.getPoint())) {
                // Mouse is pressed on the needle.
                onStartMarker = true;
                source.setCursor(eastResizeCursor);
            } else if (endMarker.contains(e.getPoint())){
                onEndMarker = true;
                source.setCursor(eastResizeCursor);
            } else {
                onStartMarker = false;
                onEndMarker = false;
                source.setCursor(defaultCursor);
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (onStartMarker) {
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
                float newTime = (x - regionModel.getPaddingLeft() + viewableModel.getZoomWindowStart() * ratio)/ratio;
                if (newTime < 0) {
                    newTime = 0;
                }
                // Make sure the marker doesn't get dragged out of view
                if (newTime > viewableModel.getZoomWindowEnd()) {
                    newTime = viewableModel.getZoomWindowEnd();
                }
                // Make sure the marker doesn't get dragged past the end marker
                if (newTime > regionModel.getRegionEnd()) {
                    newTime = regionModel.getRegionEnd();
                }
                fireMarkerEvent(Marker.START_MARKER, Math.round(newTime));
            } else if (onEndMarker) {
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
                float newTime = (x - regionModel.getPaddingLeft() + viewableModel.getZoomWindowStart() * ratio)/ratio;
                // Make sure the marker doesn't get dragged before the start marker
                if (newTime < regionModel.getRegionStart()) {
                    newTime = regionModel.getRegionStart();
                }
                // Make sure the marker doesn't get dragged out of view
                if (newTime > viewableModel.getZoomWindowEnd()) {
                    newTime = viewableModel.getZoomWindowEnd();
                }
                fireMarkerEvent(Marker.END_MARKER, Math.round(newTime));
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            onStartMarker = false;
            onEndMarker = false;
            Component source = (Component)e.getSource();
            source.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }
}
