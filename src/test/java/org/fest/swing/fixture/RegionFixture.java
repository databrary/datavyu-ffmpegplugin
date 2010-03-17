package org.fest.swing.fixture;

import java.awt.MouseInfo;
import java.awt.Point;
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
        Point to = new Point(getPointInStartMarker().x,
                getPointInStartMarker().y + pixels);
        robot.moveMouse(to);

        //Release mouse
        robot.releaseMouse(MouseButton.LEFT_BUTTON);
    }

    public Point getPointInStartMarker() {
        int numOfPointsInPolygon = ((RegionPainter)target)
                .getStartMarkerPolygon().npoints;
        Point startMarker = new Point(
                ((RegionPainter)target).getStartMarkerPolygon()
                .xpoints[numOfPointsInPolygon / 2],
                ((RegionPainter)target).getStartMarkerPolygon()
                .ypoints[numOfPointsInPolygon / 2]);
        return startMarker;
    }

    public void dragEndMarker(int pixels) {
        //Hold down left mouse button
        robot.pressMouse(getPointInEndMarker(), MouseButton.LEFT_BUTTON);

        //Move mouse to new position
        Point to = new Point(getPointInEndMarker().x,
                getPointInEndMarker().y + pixels);
        robot.moveMouse(to);

        //Release mouse
        robot.releaseMouse(MouseButton.LEFT_BUTTON);
    }

    public Point getPointInEndMarker() {
        int numOfPointsInPolygon = ((RegionPainter)target)
                .getEndMarkerPolygon().npoints;
        Point endMarker = new Point(
                ((RegionPainter)target).getEndMarkerPolygon()
                .xpoints[numOfPointsInPolygon / 2],
                ((RegionPainter)target).getEndMarkerPolygon()
                .ypoints[numOfPointsInPolygon / 2]);
        return endMarker;
    }

}
