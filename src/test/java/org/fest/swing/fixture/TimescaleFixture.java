package org.fest.swing.fixture;

import java.awt.Point;

import org.fest.swing.core.MouseButton;
import org.fest.swing.core.Robot;

import org.openshapa.controllers.component.TimescaleController;

import org.openshapa.models.component.MixerModel;

import org.openshapa.util.UIUtils;

import static org.fest.reflect.core.Reflection.*;


/**
 * Fixture for OpenSHAPA TimescalePainter.
 */
public class TimescaleFixture extends ComponentFixture {

    /** The underlying timescale controller. */
    private TimescaleController timescaleC;

    private MixerModel mixerView;

    /**
     * Constructor.
     * @param robot mainframe robot
     * @param target TimescaleController
     */
    public TimescaleFixture(final Robot robot,
        final TimescaleController target) {
        super(robot, target.getView());
        timescaleC = target;

        mixerView = field("mixer").ofType(MixerModel.class).in(timescaleC).get();
    }

    /**
     * @return End time represented as a long.
     */
    public final long getEndTimeAsLong() {
        return mixerView.getViewportModel().getViewport().getMaxEnd();
    }

    /**
     * @return End time represented as a timestamp.
     */
    public final String getEndTimeAsTimeStamp() {
        return UIUtils.millisecondsToTimestamp(getEndTimeAsLong());
    }

    /**
     * @return Zoom end time represented as a long.
     */
    public final long getZoomEndTimeAsLong() {
        return mixerView.getViewportModel().getViewport().getViewEnd();
    }

    /**
     * @return Zoom end time represented as a timestamp.
     */
    public final String getZoomEndTimeAsTimeStamp() {
        return UIUtils.millisecondsToTimestamp(getZoomEndTimeAsLong());
    }

    /**
     * @return Zoom end time represented as a long.
     */
    public final long getZoomStartTimeAsLong() {
        return mixerView.getViewportModel().getViewport().getViewStart();
    }

    /**
     * @return Zoom end time represented as a timestamp.
     */
    public final String getZoomStartTimeAsTimeStamp() {
        return UIUtils.millisecondsToTimestamp(getZoomStartTimeAsLong());
    }

    /**
     * Single click at pixel in component.
     * @param pixel to click relative to component ie. 0 to width
     */
    public final void singleClickAt(final int pixel) {
        final int timescaleYOffset = 20;
        Point click = null;
        int effectiveWidth = getEffectiveWidth();
        int x = ((pixel > effectiveWidth) ? effectiveWidth : pixel);
        click = new Point(x, timescaleYOffset);

        robot.click(target, click, MouseButton.LEFT_BUTTON, 1);
    }

    public int getEffectiveWidth() {
        return target.getWidth();
    }
}
