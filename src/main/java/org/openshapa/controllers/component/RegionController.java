package org.openshapa.controllers.component;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;

import javax.swing.JComponent;
import javax.swing.event.EventListenerList;
import javax.swing.event.MouseInputAdapter;

import org.openshapa.event.component.MarkerEvent;
import org.openshapa.event.component.MarkerEventListener;
import org.openshapa.event.component.MarkerEvent.Marker;

import org.openshapa.models.component.RegionModel;
import org.openshapa.models.component.ViewableModel;

import org.openshapa.views.component.RegionPainter;


/**
 * RegionController is responsible for managing a RegionPainter.
 */
public final class RegionController {

    /** View */
    private final RegionPainter view;

    /** Models */
    private final RegionModel regionModel;
    private final ViewableModel viewableModel;

    /** Listeners interested in changes to the playback region */
    private final EventListenerList listenerList;

    public RegionController() {
        view = new RegionPainter();

        regionModel = new RegionModel();
        regionModel.setPaddingTop(0);
        regionModel.setPaddingLeft(101);

        viewableModel = new ViewableModel();

        view.setViewableModel(viewableModel);
        view.setRegionModel(regionModel);

        final RegionMarkerListener markerListener = new RegionMarkerListener();
        view.addMouseListener(markerListener);
        view.addMouseMotionListener(markerListener);

        listenerList = new EventListenerList();
    }

    /**
     * Sets the playback region to visualise
     *
     * @param start
     *            start of the playback region in milliseconds
     * @param end
     *            end of the playback region in milliseconds
     */
    public void setPlaybackRegion(final long start, final long end) {
        regionModel.setRegionStart(start);
        regionModel.setRegionEnd(end);
        view.setRegionModel(regionModel);
    }

    /**
     * Sets the start of the playback region to visualise
     *
     * @param start
     */
    public void setPlaybackRegionStart(final long start) {
        regionModel.setRegionStart(start);
        view.setRegionModel(regionModel);
    }

    /**
     * Sets the end of the playback region to visualise
     *
     * @param end
     */
    public void setPlaybackRegionEnd(final long end) {
        regionModel.setRegionEnd(end);
        view.setRegionModel(regionModel);
    }

    /**
     * @return View used by the controller
     */
    public JComponent getView() {
        return view;
    }

    /**
     * @return returns a clone of the viewable model
     */
    public ViewableModel getViewableModel() {

        // return a clone to avoid model tainting
        return viewableModel.clone();
    }

    /**
     * @return returns a clone of the region model
     */
    public RegionModel getRegionModel() {

        // return a clone to avoid model tainting
        return regionModel.clone();
    }

    /**
     * Copies the given viewable model
     *
     * @param viewableModel
     */
    public void setViewableModel(final ViewableModel viewableModel) {

        /*
         * Just copy the values, do not spread references all over the place to
         * avoid model tainting.
         */
        this.viewableModel.setEnd(viewableModel.getEnd());
        this.viewableModel.setIntervalTime(viewableModel.getIntervalTime());
        this.viewableModel.setIntervalWidth(viewableModel.getIntervalWidth());
        this.viewableModel.setZoomWindowEnd(viewableModel.getZoomWindowEnd());
        this.viewableModel.setZoomWindowStart(
            viewableModel.getZoomWindowStart());
        view.setViewableModel(this.viewableModel);
    }

    /**
     * Register the listener to be notified of region marker events
     *
     * @param listener
     */
    public void addMarkerEventListener(final MarkerEventListener listener) {

        synchronized (this) {
            listenerList.add(MarkerEventListener.class, listener);
        }
    }

    /**
     * Remove the listener from being notified of region marker events
     *
     * @param listener
     */
    public void removeMarkerEventListener(final MarkerEventListener listener) {

        synchronized (this) {
            listenerList.remove(MarkerEventListener.class, listener);
        }
    }

    /**
     * Used to inform listeners about the change of a region marker.
     *
     * @param marker
     * @param time
     */
    private void fireMarkerEvent(final Marker marker, final long time) {

        synchronized (this) {
            final MarkerEvent e = new MarkerEvent(this, marker, time);
            final Object[] listeners = listenerList.getListenerList();

            /*
             * The listener list contains the listening class and then the
             * listener instance.
             */
            for (int i = 0; i < listeners.length; i += 2) {

                if (listeners[i] == MarkerEventListener.class) {
                    ((MarkerEventListener) listeners[i + 1]).markerMoved(e);
                }
            }
        }
    }

    /**
     * Inner class used to handle intercepted events.
     */
    private final class RegionMarkerListener extends MouseInputAdapter {
        private boolean onStartMarker;
        private boolean onEndMarker;

        private final Cursor eastResizeCursor = Cursor.getPredefinedCursor(
                Cursor.E_RESIZE_CURSOR);
        private final Cursor defaultCursor = Cursor.getDefaultCursor();

        public RegionMarkerListener() {
            super();
            onStartMarker = false;
            onEndMarker = false;
        }

        @Override public void mouseEntered(final MouseEvent e) {
            final JComponent source = (JComponent) e.getSource();
            final GeneralPath startMarker = view.getStartMarkerPolygon();
            final GeneralPath endMarker = view.getEndMarkerPolygon();

            if (startMarker.contains(e.getPoint())) {
                source.setCursor(eastResizeCursor);
            } else if (endMarker.contains(e.getPoint())) {
                source.setCursor(eastResizeCursor);
            } else {
                source.setCursor(defaultCursor);
            }
        }

        @Override public void mouseMoved(final MouseEvent e) {
            mouseEntered(e);
        }

        @Override public void mousePressed(final MouseEvent e) {
            final Component source = (Component) e.getSource();
            final GeneralPath startMarker = view.getStartMarkerPolygon();
            final GeneralPath endMarker = view.getEndMarkerPolygon();

            if (startMarker.contains(e.getPoint())) {

                // Mouse is pressed on the needle.
                onStartMarker = true;
                source.setCursor(eastResizeCursor);
            } else if (endMarker.contains(e.getPoint())) {
                onEndMarker = true;
                source.setCursor(eastResizeCursor);
            } else {
                onStartMarker = false;
                onEndMarker = false;
                source.setCursor(defaultCursor);
            }
        }

        @Override public void mouseDragged(final MouseEvent e) {

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
                final float ratio = viewableModel.getIntervalWidth()
                    / viewableModel.getIntervalTime();
                float newTime = (x - regionModel.getPaddingLeft()
                        + (viewableModel.getZoomWindowStart() * ratio)) / ratio;

                if (newTime < 0) {
                    newTime = 0;
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
                final double ratio = viewableModel.getIntervalWidth() / viewableModel.getIntervalTime();
                final double newTime = (x - regionModel.getPaddingLeft() + (viewableModel.getZoomWindowStart() * ratio)) / ratio;
                fireMarkerEvent(Marker.END_MARKER, Math.round(newTime));
            }
        }

        @Override public void mouseReleased(final MouseEvent e) {
            onStartMarker = false;
            onEndMarker = false;

            final JComponent source = (JComponent) e.getSource();
            source.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }
}
