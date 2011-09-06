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
