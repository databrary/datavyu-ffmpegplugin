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
