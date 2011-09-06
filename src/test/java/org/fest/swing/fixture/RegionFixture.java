/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fest.swing.fixture;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.geom.GeneralPath;

import org.fest.swing.core.MouseButton;
import org.fest.swing.core.Robot;

import org.openshapa.controllers.component.RegionController;

import org.openshapa.util.UIUtils;

import org.openshapa.views.component.RegionView;


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
        return regionC.getModel().getRegion().getRegionStart();
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
        return regionC.getModel().getRegion().getRegionEnd();
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
        return ((RegionView) target).contains(
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
        GeneralPath startMarker = ((RegionView) target).getStartMarkerPolygon();
        return new Point(
                (int) startMarker.getBounds().getCenterX() + ((RegionView) target).getLocationOnScreen().x,
                (int) startMarker.getBounds().getCenterY() + ((RegionView) target).getLocationOnScreen().y
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
        GeneralPath endMarker = ((RegionView) target).getEndMarkerPolygon();
        return new Point(
                (int) endMarker.getBounds().getCenterX() + ((RegionView) target).getLocationOnScreen().x,
                (int) endMarker.getBounds().getCenterY() + ((RegionView) target).getLocationOnScreen().y
                );
    }
}
