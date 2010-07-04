package org.fest.swing.fixture;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.geom.GeneralPath;

import org.fest.swing.core.MouseButton;
import org.fest.swing.core.Robot;

import org.openshapa.controllers.component.RegionController;

import org.openshapa.util.UIUtils;

import org.openshapa.views.component.RegionPainter;


/**
 * Fixture for OpenSHAPA RegionPainter.
 */
public class RegionFixture extends ComponentFixture {

    /** The underlying mixercontroller. */
    private RegionController regionC;

    /**
     * Constructor.
     * @param robot mainframe robot
     * @param target NeedleController
     */
    public RegionFixture(final Robot robot, final RegionController target) {
        super(robot, target.getView());
        regionC = target;
    }

    /**
     * @return Start time of the region as a long.
     */
    public long getStartTimeAsLong() {
        return regionC.getRegionModel().getRegionStart();
    }

    /**
     * @return Start time of the region as a timestamp.
     */
    public String getStartTimeAsTimeStamp() {
        return UIUtils.millisecondsToTimestamp(getStartTimeAsLong());
    }

    /**
     * @return End time of the region as a long.
     */
    public long getEndTimeAsLong() {
        return regionC.getRegionModel().getRegionEnd();
    }

    /**
     * @return End time of the region as a timestamp.
     */
    public String getEndTimeAsTimeStamp() {
        return UIUtils.millisecondsToTimestamp(getEndTimeAsLong());
    }

    /**
     * @return true if mouse is on the region head.
     */
    public boolean isMouseOnRegionHead() {
        return ((RegionPainter) target).contains(
                MouseInfo.getPointerInfo().getLocation());
    }

    /**
     * Drags the start marker the specified number of pixels left (-ve) or
     * right (+ve).
     * @param pixels number of pixels to drag
     */
    public void dragStartMarker(int pixels) {

        // Hold down left mouse button
        robot.pressMouse(getPointInStartMarker(), MouseButton.LEFT_BUTTON);

        // Move mouse to new position
        Point to = new Point(getPointInStartMarker().x + pixels,
                getPointInStartMarker().y);
        robot.moveMouse(to);

        // Release mouse
        robot.releaseMouse(MouseButton.LEFT_BUTTON);
    }

    /**
     * @return Point in the centre of the start marker head
     */
    private Point getPointInStartMarker() {
        GeneralPath startMarker = ((RegionPainter) target).getStartMarkerPolygon();
        return new Point(
                (int) startMarker.getBounds().getCenterX() + ((RegionPainter) target).getLocationOnScreen().x,
                (int) startMarker.getBounds().getCenterY() + ((RegionPainter) target).getLocationOnScreen().y
                );
    }

    /**
    * Drags the end marker the specified number of pixels left (-ve) or
    * right (+ve).
    * @param pixels number of pixels to drag
    */
    public void dragEndMarker(int pixels) {

        // Hold down left mouse button
        robot.pressMouse(getPointInEndMarker(), MouseButton.LEFT_BUTTON);

        // Move mouse to new position
        Point to = new Point(getPointInEndMarker().x + pixels,
                getPointInEndMarker().y);
        robot.moveMouse(to);

        // Release mouse
        robot.releaseMouse(MouseButton.LEFT_BUTTON);
    }

    /**
     * @return a Point in the EndMarker.
     */
    public Point getPointInEndMarker() {
        GeneralPath endMarker = ((RegionPainter) target).getEndMarkerPolygon();
        return new Point(
                (int) endMarker.getBounds().getCenterX() + ((RegionPainter) target).getLocationOnScreen().x,
                (int) endMarker.getBounds().getCenterY() + ((RegionPainter) target).getLocationOnScreen().y
                );
    }
}
