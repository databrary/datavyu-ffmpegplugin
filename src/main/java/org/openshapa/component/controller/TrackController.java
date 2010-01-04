package org.openshapa.component.controller;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import javax.swing.event.EventListenerList;
import javax.swing.event.MouseInputAdapter;
import org.openshapa.component.TrackPainter;
import org.openshapa.component.model.TrackModel;
import org.openshapa.component.model.ViewableModel;
import org.openshapa.event.CarriageEvent;
import org.openshapa.event.CarriageEventListener;

/**
 * TrackPainterController is responsible for managing a TrackPainter.
 */
public class TrackController {
    /** View */
    private TrackPainter view;
    /** Models */
    private ViewableModel viewableModel;
    private TrackModel trackModel;
    /** Listens to mouse events */
    private TrackPainterListener trackPainterListener;
    /** Listeners interested in custom playback region events */
    private EventListenerList listenerList;

    public TrackController() {
        view = new TrackPainter();

        viewableModel = new ViewableModel();
        trackModel = new TrackModel();

        view.setViewableModel(viewableModel);
        view.setTrackModel(trackModel);

        listenerList = new EventListenerList();

        trackPainterListener = new TrackPainterListener();
        view.addMouseListener(trackPainterListener);
        view.addMouseMotionListener(trackPainterListener);
    }

    /**
     * Sets the track information to use.
     * @param trackId Absolute path to the track's data feed
     * @param duration Duration of the data feed in milliseconds
     * @param offset Offset of the data feed in milliseconds
     */
    public void setTrackInformation(final String trackId, final long duration,
            final long offset) {
        trackModel.setTrackId(trackId);
        trackModel.setDuration(duration);
        trackModel.setOffset(offset);
        trackModel.setErroneous(false);
        view.setTrackModel(trackModel);
    }

    /**
     * Sets the track offset in milliseconds.
     * @param offset Offset of the data feed in milliseconds
     */
    public void setTrackOffset(final long offset) {
        trackModel.setOffset(offset);
        view.setTrackModel(trackModel);
    }

    /**
     * Indicate that the track's information cannot be resolved.
     * @param erroneous
     */
    public void setErroneous(boolean erroneous) {
        trackModel.setErroneous(erroneous);
        view.setTrackModel(trackModel);
    }

    /**
     * @return View used by the controller
     */
    public Component getView() {
        return view;
    }

    /**
     * @return a clone of the viewable model used by the controller
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
     * Register the listener to be notified of carriage events
     * @param listener
     */
    public synchronized void addCarriageEventListener(CarriageEventListener listener) {
        listenerList.add(CarriageEventListener.class, listener);
    }

    /**
     * Remove the listener from being notified of carriage events
     * @param listener
     */
    public synchronized void removeCarriageEventListener(CarriageEventListener listener) {
        listenerList.remove(CarriageEventListener.class, listener);
    }

    /**
     * Used to inform listeners about a new carriage event
     * @param offset
     */
    private synchronized void fireCarriageEvent(long offset) {
        CarriageEvent e = new CarriageEvent(this,
                trackModel.getTrackId(), offset,
                trackModel.getDuration());
        Object[] listeners = listenerList.getListenerList();
        /* The listener list contains the listening class and then the listener
         * instance.
         */
        for (int i = 0; i < listeners.length; i += 2) {
           if (listeners[i] == CarriageEventListener.class) {
               ((CarriageEventListener)listeners[i+1]).offsetChanged(e);
           }
        }
    }

    /**
     * Inner listener used to handle mouse eventss
     */
    private class TrackPainterListener extends MouseInputAdapter {

        private long offsetInit;
        private boolean inCarriage;
        private int xInit;

        @Override
        public void mousePressed(MouseEvent e) {
            if (TrackController.this.view.getCarriagePolygon().contains(e.getPoint())) {
                inCarriage = true;
                xInit = e.getX();
                offsetInit = trackModel.getOffset();
//                Component source = (Component) e.getSource();
//                TrackController.this.view.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
//                System.out.printf("Contains: %b\n", view.contains(e.getPoint()));
//                source.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (inCarriage) {
                int xNet = e.getX() - xInit;
                // Calculate the total amount of time we offset by
                float newOffset = (xNet * 1F) /
                        viewableModel.getIntervalWidth() *
                        viewableModel.getIntervalTime() + offsetInit;
                TrackController.this.view.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                TrackController.this.fireCarriageEvent(Math.round(newOffset));

            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            inCarriage = false;
//            Component source = (Component) e.getSource();
//            source.setCursor(Cursor.getDefaultCursor());
        }
    }

}
