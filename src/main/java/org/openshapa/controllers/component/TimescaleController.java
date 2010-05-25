package org.openshapa.controllers.component;

import javax.swing.JComponent;

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

    public TimescaleController() {
        view = new TimescalePainter();

        timescaleModel = new TimescaleModel();
        timescaleModel.setMajorIntervals(6);
        timescaleModel.setPaddingLeft(101);
        timescaleModel.setPaddingRight(20);

        viewableModel = new ViewableModel();
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

}
