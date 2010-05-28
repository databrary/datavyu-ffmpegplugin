package org.openshapa.controllers.component;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;

import javax.swing.JComponent;
import javax.swing.event.EventListenerList;
import javax.swing.event.MouseInputAdapter;
import org.openshapa.OpenSHAPA;

import org.openshapa.event.component.NeedleEvent;
import org.openshapa.event.component.NeedleEventListener;
import org.openshapa.models.component.NeedleModel;
import org.openshapa.models.component.ViewableModel;
import org.openshapa.util.NeedleTimeCalculator;
import org.openshapa.views.component.NeedlePainter;

/**
 * NeedleController is responsible for managing a NeedlePainter
 */
public final class NeedleController {
    /** View */
    private final NeedlePainter view;
    /** Models */
    private final NeedleModel needleModel;
    private final ViewableModel viewableModel;
    /** Listeners interested in needle painter events */
    private final EventListenerList listenerList;

    public NeedleController() {
        view = new NeedlePainter();

        needleModel = new NeedleModel();
        needleModel.setPaddingTop(0);
        needleModel.setPaddingLeft(101);

        viewableModel = new ViewableModel();

        view.setViewableModel(viewableModel);
        view.setNeedleModel(needleModel);

        final NeedleListener needleListener = new NeedleListener();
        view.addMouseListener(needleListener);
        view.addMouseMotionListener(needleListener);

        listenerList = new EventListenerList();
    }

    /**
     * Register the listener to be notified of needle events
     *
     * @param listener
     */
    public void addNeedleEventListener(final NeedleEventListener listener) {
        synchronized (this) {
            listenerList.add(NeedleEventListener.class, listener);
        }
    }

    /**
     * Removed the listener from being notified of needle events
     *
     * @param listener
     */
    public void removeNeedleEventListener(final NeedleEventListener listener) {
        synchronized (this) {
            listenerList.remove(NeedleEventListener.class, listener);
        }
    }

    /**
     * Used to fire a new event informing listeners about the new needle time.
     *
     * @param newTime
     */
    private void fireNeedleEvent(final long newTime) {
        synchronized (this) {
            NeedleEvent e = new NeedleEvent(this, newTime);
            Object[] listeners = listenerList.getListenerList();
            /*
             * The listener list contains the listening class and then the
             * listener instance.
             */
            for (int i = 0; i < listeners.length; i += 2) {
                if (listeners[i] == NeedleEventListener.class) {
                    ((NeedleEventListener) listeners[i + 1]).needleMoved(e);
                }
            }
        }
    }

    /**
     * Set the current time to be represented by the needle.
     *
     * @param currentTime
     */
    public void setCurrentTime(final long currentTime) {
        /** Format for representing time. */
        DateFormat df = new SimpleDateFormat("HH:mm:ss:SSS");
        df.setTimeZone(new SimpleTimeZone(0, "NO_ZONE"));
        needleModel.setCurrentTime(currentTime);
        view.setToolTipText(df.format(new Date(currentTime)));
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
        return viewableModel.clone();
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
        this.viewableModel.setZoomWindowStart(viewableModel
                .getZoomWindowStart());
        view.setViewableModel(this.viewableModel);
    }

    /**
     * @return View used by the controller
     */
    public JComponent getView() {
        return view;
    }

    /**
     * Inner class used to handle intercepted events.
     */
    private class NeedleListener extends MouseInputAdapter {
        private final Cursor eastResizeCursor =
                Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
        private final Cursor defaultCursor =
                Cursor.getDefaultCursor();

        @Override
        public void mouseEntered(final MouseEvent e) {
            JComponent source = (JComponent) e.getSource();
            source.setCursor(eastResizeCursor);
        }

        @Override
        public void mouseExited(final MouseEvent e) {
            JComponent source = (JComponent) e.getSource();
            source.setCursor(defaultCursor);
        }

        @Override
        public void mouseMoved(final MouseEvent e) {
            mouseEntered(e);
        }

        @Override
        public void mouseDragged(final MouseEvent e) {

            int time = NeedleTimeCalculator.getNeedleTime(e.getX(),
                    view.getSize().width, viewableModel,
                    needleModel.getPaddingLeft());

            fireNeedleEvent(time);
        }
    }



    /**
     * Checks that the needle is in a valid position and fixes it if it isn't.
     */
    public void fixNeedle() {
        RegionController rc = OpenSHAPA.getDataController().getMixerController().getRegionController();
        if (getCurrentTime() > rc.getRegionModel().getRegionEnd()) {
            setCurrentTime(rc.getRegionModel().getRegionEnd());
        } else if (getCurrentTime() < rc.getRegionModel().getRegionStart()) {
            setCurrentTime(rc.getRegionModel().getRegionStart());
        }
    }

}
