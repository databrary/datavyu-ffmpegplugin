package org.fest.swing.fixture;

import java.awt.Point;
import org.fest.swing.core.MouseButton;
import org.fest.swing.core.Robot;
import org.openshapa.controllers.component.TrackController;
import org.openshapa.util.UIUtils;
import org.openshapa.views.component.TrackPainter;

/**
 * Fixture for OpenSHAPA TrackController.
 */
public class TrackFixture extends ComponentFixture {
    /** The underlying mixercontroller. */
    private TrackController trackC;

    /**
     * Constructor.
     * @param robot mainframe robot
     * @param target TracksEditorController
     */
    public TrackFixture(final Robot robot,final TrackController target) {
        super(robot, target.getView());
        trackC = target;
    }

    /**
     * @return Duration of track as a long.
     */
    public long getDurationTimeAsLong() {
        return trackC.getDuration();
    }

    /**
     * @return Duration of track as a timestamp.
     */
    public String getDurationTimeAsTimeStamp() {
        return UIUtils.millisecondsToTimestamp(getDurationTimeAsLong());
    }

    /**
     * @return Offset of track as a long.
     */
    public long getOffsetTimeAsLong() {
        return trackC.getOffset();
    }

    /**
     * @return Offset of track as a timestamp.
     */
    public String getOffsetTimeAsTimeStamp() {
        return UIUtils.millisecondsToTimestamp(getOffsetTimeAsLong());
    }

    /**
     * @return Offset of track as a long.
     */
    public long getBookmarkTimeAsLong() {
        return trackC.getBookmark();
    }

    /**
     * @return Offset of track as a timestamp.
     */
    public String getBookmarkTimeAsTimeStamp() {
        return UIUtils.millisecondsToTimestamp(getBookmarkTimeAsLong());
    }

    /**
     * @return true if track is selected.
     */
    public boolean isSelected() {
        return trackC.isSelected();
    }

    /**
     * @return true if track is selected.
     */
    public boolean isLocked() {
        return trackC.isLocked();
    }

    /**
     * Press the lock/unlock button.
     */
    public void pressLockButton() {
        new JButtonFixture(robot, "lockUnlockButton").click();
    }

    /**
     * @return track label
     */
    public String getTrackName() {
        return new JLabelFixture(robot, "trackLabel").text();
    }
    
    /**
     * Drag number of pixels left (negative) or right (positive)
     * @param pixels
     */
    public void drag(int pixels) {
        //Hold down left mouse button
        //Start position should leave enough room to move pixels
        Point topLeft = ((TrackPainter)target).getLocationOnScreen();
        Point startClick;
        if (pixels >= 0) {
            startClick = new Point (topLeft.x + 5, topLeft.y + 5);
        } else {
            startClick = new Point (topLeft.x +
                    ((TrackPainter)target).getWidth() - 5,
                    topLeft.y +
                    ((TrackPainter)target).getHeight() - 5);
        }
        robot.pressMouse(startClick, MouseButton.LEFT_BUTTON);

        //Move mouse to new position
        Point to = new Point(startClick.x,startClick.y + pixels);
        robot.moveMouse(to);

        //Release mouse
        robot.releaseMouse(MouseButton.LEFT_BUTTON);
    }

}
