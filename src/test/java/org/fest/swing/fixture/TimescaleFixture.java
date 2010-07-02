package org.fest.swing.fixture;

import static org.fest.reflect.core.Reflection.method;
import static org.fest.reflect.core.Reflection.field;

import java.awt.Point;

import org.fest.swing.core.MouseButton;
import org.fest.swing.core.Robot;
import org.openshapa.OpenSHAPA;

import org.openshapa.controllers.component.TimescaleController;
import org.openshapa.models.component.TimescaleModel;

import org.openshapa.util.UIUtils;
import org.openshapa.views.OpenSHAPAFileChooser;


/**
 * Fixture for OpenSHAPA TimescalePainter.
 */
public class TimescaleFixture extends ComponentFixture {

    /** The underlying timescale controller. */
    private TimescaleController timescaleC;

    /**
     * Constructor.
     * @param robot mainframe robot
     * @param target TimescaleController
     */
    public TimescaleFixture(final Robot robot,
        final TimescaleController target) {
        super(robot, target.getView());
        timescaleC = target;
    }

    /**
     * @return End time represented as a long.
     */
    public final long getEndTimeAsLong() {
        return timescaleC.getViewableModel().getEnd();
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
        return timescaleC.getViewableModel().getZoomWindowEnd();
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
        return timescaleC.getViewableModel().getZoomWindowStart();
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
        TimescaleModel tm = field("timescaleModel").ofType(TimescaleModel.class)
               .in(timescaleC).get();
        int effectiveWidth = getEffectiveWidth();
        int x = tm.getPaddingLeft() + (pixel > effectiveWidth ? effectiveWidth : pixel);
        click = new Point(x, timescaleYOffset);

        robot.click(target, click, MouseButton.LEFT_BUTTON, 1);
    }

    public int getEffectiveWidth() {
        TimescaleModel tm = field("timescaleModel").ofType(TimescaleModel.class)
               .in(timescaleC).get();

        return target.getWidth() - tm.getPaddingLeft() - tm.getPaddingRight();
    }
}
