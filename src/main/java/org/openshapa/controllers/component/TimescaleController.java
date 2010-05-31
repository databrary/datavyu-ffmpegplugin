package org.openshapa.controllers.component;

import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.event.EventListenerList;
import javax.swing.event.MouseInputAdapter;

import org.openshapa.event.component.TimescaleEvent;
import org.openshapa.event.component.TimescaleListener;

import org.openshapa.models.component.TimescaleModel;
import org.openshapa.models.component.ViewableModel;

import org.openshapa.views.component.TimescalePainter;


/**
 * Timescale controller is responsible for managing a TimescalePainter
 */
public final class TimescaleController {

    /** View */
    private final TimescalePainter view;

    /** Models */
    private final TimescaleModel timescaleModel;
    private final ViewableModel viewableModel;

    /** Listeners interested in needle painter events */
    private final EventListenerList listenerList;

    public TimescaleController() {
        view = new TimescalePainter();

        timescaleModel = new TimescaleModel();
        timescaleModel.setMajorIntervals(6);
        timescaleModel.setPaddingLeft(101);
        timescaleModel.setPaddingRight(20);

        viewableModel = new ViewableModel();

        final TimescaleEventListener listener = new TimescaleEventListener();
        view.addMouseListener(listener);

        listenerList = new EventListenerList();
    }

    /**
     * @param start
     *            The start time, in milliseconds, of the scale to display
     * @param end
     *            The end time, in milliseconds, of the scale to display
     * @param intervals
     *            The total number of intervals between two major intervals,
     *            this value is inclusive of the major intervals. i.e. if
     *            intervals is 10, then 2 (start and end interval marking) of
     *            them are major intervals and 8 are minor intervals.
     */
    public void setConstraints(final long start, final long end,
        final int intervals) {
        viewableModel.setZoomWindowStart(start);
        viewableModel.setZoomWindowEnd(end);

        final int effectiveWidth = view.getWidth()
            - timescaleModel.getPaddingLeft()
            - timescaleModel.getPaddingRight();
        timescaleModel.setEffectiveWidth(effectiveWidth);

        final int majorWidth = effectiveWidth
            / timescaleModel.getMajorIntervals();
        timescaleModel.setMajorWidth(majorWidth);

        viewableModel.setIntervalWidth((float) majorWidth / (float) intervals);
        viewableModel.setIntervalTime((float) (end - start)
            / (float) (timescaleModel.getMajorIntervals() * intervals));

        view.setViewableModel(viewableModel);
        view.setTimescaleModel(timescaleModel);
    }

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
     * @return a clone of the viewable model in use
     */
    public ViewableModel getViewableModel() {

        // return a clone to avoid model tainting
        return viewableModel.clone();
    }

    /**
     * @return View used by the controller
     */
    public JComponent getView() {
        return view;
    }

    public void addTimescaleEventListener(final TimescaleListener listener) {

        synchronized (this) {
            listenerList.add(TimescaleListener.class, listener);
        }
    }

    public void removeTimescaleEventListener(final TimescaleListener listener) {

        synchronized (this) {
            listenerList.remove(TimescaleListener.class, listener);
        }
    }

    /**
     * Used to fire a new event informing listeners about the new needle time.
     *
     * @param newTime
     */
    private void fireJumpEvent(final long jumpTime) {

        synchronized (this) {
            TimescaleEvent e = new TimescaleEvent(this, jumpTime);
            Object[] listeners = listenerList.getListenerList();

            /*
             * The listener list contains the listening class and then the
             * listener instance.
             */
            for (int i = 0; i < listeners.length; i += 2) {

                if (listeners[i] == TimescaleListener.class) {
                    ((TimescaleListener) listeners[i + 1]).jumpToTime(e);
                }
            }
        }
    }

    /**
     * Inner class used to handle intercepted events.
     */
    private class TimescaleEventListener extends MouseInputAdapter {
        @Override public void mouseClicked(final MouseEvent e) {

            if (e.getClickCount() == 2) {
                int x = e.getX();

                // Bound the x values
                if (x < 0) {
                    x = 0;
                }

                if (x > view.getSize().width) {
                    x = view.getSize().width;
                }

                // Calculate the time represented by the new location
                float ratio = viewableModel.getIntervalWidth()
                    / viewableModel.getIntervalTime();
                float newTime = (x - timescaleModel.getPaddingLeft()
                        + (viewableModel.getZoomWindowStart() * ratio)) / ratio;

                if (newTime < 0) {
                    newTime = 0;
                }

                if (newTime > viewableModel.getZoomWindowEnd()) {
                    newTime = viewableModel.getZoomWindowEnd();
                }

                fireJumpEvent(Math.round(newTime));

            }
        }
    }

}
