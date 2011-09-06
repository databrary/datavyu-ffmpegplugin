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


import org.fest.swing.core.Robot;

import org.openshapa.util.UIUtils;

import org.openshapa.views.component.SnapMarkerPainter;


/**
 * Fixture for OpenSHAPA NeedlePainter.
 */
public class SnapMarkerFixture extends ComponentFixture {

    /**
     * Constructor.
     * @param robot
     *            mainframe robot
     * @param target
     *            NeedleController
     */
    public SnapMarkerFixture(final Robot robot,
        final SnapMarkerPainter target) {
        super(robot, target);
    }

    /**
     * @return SnapMarker time as long
     */
    public long getMarkerTimeAsLong() {
        return ((SnapMarkerPainter) target).getSnapMarkerModel()
            .getMarkerTime();
    }

    /**
     * @return SnapMarker time as Timestamp string
     */
    public String getMarkerTimeAsTimeStamp() {
        return UIUtils.millisecondsToTimestamp(((SnapMarkerPainter) target)
                .getSnapMarkerModel().getMarkerTime());
    }

    /**
     * @return true if SnapMarker is visible, else false.
     */
    public boolean isVisible() {
        return !(((SnapMarkerPainter) target).getSnapMarkerModel()
                .getMarkerTime() == -1);
    }


}
