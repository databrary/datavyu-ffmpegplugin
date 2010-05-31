package org.fest.swing.fixture;

import java.awt.Point;

import org.fest.swing.core.MouseButton;
import org.fest.swing.core.Robot;

import org.openshapa.controllers.component.TimescaleController;

import org.openshapa.util.UIUtils;


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
    public long getEndTimeAsLong() {
        return timescaleC.getViewableModel().getEnd();
    }

    /**
     * @return End time represented as a timestamp.
     */
    public String getEndTimeAsTimeStamp() {
        return UIUtils.millisecondsToTimestamp(getEndTimeAsLong());
    }

    /**
     * @return Zoom end time represented as a long.
     */
    public long getZoomEndTimeAsLong() {
        return timescaleC.getViewableModel().getZoomWindowEnd();
    }

    /**
     * @return Zoom end time represented as a timestamp.
     */
    public String getZoomEndTimeAsTimeStamp() {
        return UIUtils.millisecondsToTimestamp(getZoomEndTimeAsLong());
    }

    /**
     * @return Zoom end time represented as a long.
     */
    public long getZoomStartTimeAsLong() {
        return timescaleC.getViewableModel().getZoomWindowStart();
    }

    /**
     * @return Zoom end time represented as a timestamp.
     */
    public String getZoomStartTimeAsTimeStamp() {
        return UIUtils.millisecondsToTimestamp(getZoomStartTimeAsLong());
    }

    public void doubleClickAt(final int pixel) {
        Point topLeft = target.getBounds().getLocation();
        int width = target.getWidth();
        Point click = null;

        if (pixel > width) {
            click = new Point(topLeft.x + width,
                    topLeft.y + (target.getHeight() / 2));
        } else {
            click = new Point(topLeft.x + pixel,
                    topLeft.y + (target.getHeight() / 2));
        }

        robot.click(target, click, MouseButton.LEFT_BUTTON, 2);
    }
}
