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

import static org.fest.reflect.core.Reflection.field;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.geom.GeneralPath;

import org.fest.swing.core.MouseButton;
import org.fest.swing.core.Robot;

import org.openshapa.controllers.component.NeedleController;

import org.openshapa.util.UIUtils;

import org.openshapa.views.component.NeedlePainter;


/**
 * Fixture for OpenSHAPA NeedlePainter.
 */
public class NeedleFixture extends ComponentFixture {

    /** The underlying mixercontroller. */
    private NeedleController needleC;

    /**
     * Constructor.
     * @param robot
     *            mainframe robot
     * @param target
     *            NeedleController
     */
    public NeedleFixture(final Robot robot, final NeedleController target) {
        super(robot, target.getView());
        needleC = target;
    }

    /**
     * @return Current time represented by the needle as a long.
     */
    public long getCurrentTimeAsLong() {
        return needleC.getNeedleModel().getCurrentTime();
    }

    /**
     * @return Current time represented by the needle as a timestamp.
     */
    public String getCurrentTimeAsTimeStamp() {
        return UIUtils.millisecondsToTimestamp(needleC.getNeedleModel().getCurrentTime());
    }

    public boolean isMouseOnNeedleHead() {
        return ((NeedlePainter) target).contains(MouseInfo.getPointerInfo()
                .getLocation());
    }

    /**
     * Drag number of pixels left (negative) or right (positive).
     * @param pixels
     */
    public void drag(final int pixels) {

        // Hold down left mouse button
        robot.pressMouse(getCenterOfMarker(), MouseButton.LEFT_BUTTON);

        // Move mouse to new position
        Point to = new Point(getCenterOfMarker().x + pixels,
                getCenterOfMarker().y);
        robot.moveMouse(to);

        // Release mouse
        robot.releaseMouse(MouseButton.LEFT_BUTTON);
    }

    /**
     * @return a point at the centre of the needle head.
     */
    private Point getCenterOfMarker() {

        /*
         * There are actually 4 points that define the needle polygon triangle,
         * because it has a slightly flat base (1pixel). We ignore the 4th
         * point.
         */
        GeneralPath needleMarker =
                field("needleMarker").ofType(GeneralPath.class).in(target).get();

        return new Point((int) needleMarker.getBounds().getCenterX() + ((NeedlePainter) target).getLocationOnScreen().x, (int) needleMarker.getBounds().getCenterY() + ((NeedlePainter) target).getLocationOnScreen().y);
    }
}
