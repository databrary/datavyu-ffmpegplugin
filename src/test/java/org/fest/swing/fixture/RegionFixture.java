package org.fest.swing.fixture;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Polygon;
import org.fest.swing.core.MouseButton;
import org.fest.swing.core.Robot;
import org.openshapa.controllers.component.RegionController;
import org.openshapa.util.UIUtils;
import org.openshapa.views.component.NeedlePainter;
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
    public RegionFixture(final Robot robot,final RegionController target) {
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
        return ((RegionPainter)target).contains(MouseInfo.getPointerInfo().getLocation());
    }

    public void dragStartMarker(int pixels) {
        //Hold down left mouse button
        robot.pressMouse(getPointInStartMarker(), MouseButton.LEFT_BUTTON);

        //Move mouse to new position
        Point to = new Point(getPointInStartMarker().x + pixels,
                getPointInStartMarker().y);
        robot.moveMouse(to);

        //Release mouse
        robot.releaseMouse(MouseButton.LEFT_BUTTON);
    }

    public Point getPointInStartMarker() {
        /*
         * The start marker is a trapezoid, where the first point is the top
         * left, and all other points go around clockwise.
         */
        Polygon startMarker = ((RegionPainter)target).getStartMarkerPolygon();

        Point locationOfPolygon = startMarker.getBounds().getLocation();

        // Find middle x position
        int xPos =
                (startMarker.xpoints[1] - startMarker.xpoints[0]) / 2
                        + locationOfPolygon.x
                        + ((RegionPainter) target).getLocationOnScreen().x;
        // Find middle y position
        int yPos =
                (startMarker.ypoints[3] - startMarker.ypoints[0]) / 2
                        + locationOfPolygon.y
                        + ((RegionPainter) target).getLocationOnScreen().y;

        Point centrePoint = new Point(xPos, yPos);

        return centrePoint;
    }

    public void dragEndMarker(int pixels) {
        //Hold down left mouse button
        robot.pressMouse(getPointInEndMarker(), MouseButton.LEFT_BUTTON);

        //Move mouse to new position
        Point to = new Point(getPointInEndMarker().x + pixels,
                getPointInEndMarker().y);
        robot.moveMouse(to);

        //Release mouse
        robot.releaseMouse(MouseButton.LEFT_BUTTON);
    }

    public Point getPointInEndMarker() {
        /*
         * The start marker is a trapezoid, where the first point is the top
         * left, and all other points go around clockwise.
         */
        Polygon endMarker = ((RegionPainter)target).getEndMarkerPolygon();

        Point locationOfPolygon = endMarker.getBounds().getLocation();

         // Find middle x position
        int xPos =
                (endMarker.xpoints[1] - endMarker.xpoints[0]) / 2
                        + locationOfPolygon.x
                         + ((RegionPainter) target).getLocationOnScreen().x;
        // Find middle y position
        int yPos =
                (endMarker.ypoints[2] - endMarker.ypoints[1]) / 2
                        + locationOfPolygon.y
                        + ((RegionPainter) target).getLocationOnScreen().y;

        Point centrePoint = new Point(xPos, yPos);

        return centrePoint;
    }
}
