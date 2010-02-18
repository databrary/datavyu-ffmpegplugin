package org.fest.swing.fixture;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.ImageObserver;

import javax.swing.JLabel;
import javax.swing.text.BadLocationException;
import org.fest.swing.core.MouseButton;

import org.fest.swing.core.Robot;
import org.openshapa.views.discrete.SpreadsheetCell;
import org.openshapa.views.discrete.datavalues.MatrixRootView;
import org.openshapa.views.discrete.datavalues.TimeStampTextField;

/**
 * Fixture for a Spreadsheet Column.
 */
public class SpreadsheetCellFixture extends JPanelFixture {
    /** Cell value. */
    public static final int VALUE = 0;
    /** Cell onset. */
    public static final int ONSET = 1;
    /** Cell offset. */
    public static final int OFFSET = 2;

    /**
     * Constructor.
     * @param robot main frame fixture robot.
     * @param target underlying Spreadsheetcell class.
     */
    public SpreadsheetCellFixture(final Robot robot,
            final SpreadsheetCell target) {
        super(robot, target);
    }

    /**
     * Selects the cell.
     */
    public final void selectCell() {
        JLabel ordinalLabel = ordinalLabel().target;
        Point labelPosition = ordinalLabel.getLocation();
        Point clickPosition =
                new Point(labelPosition.x + ImageObserver.WIDTH + 25,
                        labelPosition.y + 5);
        robot.pressKey(KeyEvent.VK_SHIFT);
        robot.click(component(), clickPosition);
        robot.releaseKey(KeyEvent.VK_SHIFT);
    }

    /**
     * Clicks to the position after a particular character position in
     * either value, onset or offset.
     * @param component VALUE, ONSET, or OFFSET
     * @param charPos character position to click after.
     * @param times number of times to click
     * @throws BadLocationException on bad character location
     */
    public final void clickToCharPos(final int component, final int charPos,
            final int times) throws BadLocationException {
        Point charPoint;
        Component c;
        switch (component) {
        case VALUE:
            charPoint =
                    centerOf(((SpreadsheetCell) target).getDataView()
                            .modelToView(charPos));
            c = ((SpreadsheetCell) target).getDataView();
            break;
        case ONSET:
            charPoint =
                    centerOf(((SpreadsheetCell) target).getOnset().modelToView(
                            charPos));
            c = ((SpreadsheetCell) target).getOnset();
            break;
        case OFFSET:
            charPoint =
                    centerOf(((SpreadsheetCell) target).getOffset()
                            .modelToView(charPos));
            c = ((SpreadsheetCell) target).getOffset();
            break;
        default:
            charPoint =
                    centerOf(((SpreadsheetCell) target).getDataView()
                            .modelToView(charPos));
            c = ((SpreadsheetCell) target).getDataView();
            break;
        }
        for (int i = 0; i < times; i++) {
            robot.click(c, charPoint);
        }
    }

    /**
     * @return JLabel for ordinal id label.
     */
    public final JLabelFixture ordinalLabel() {
        return new JLabelFixture(robot, findByType(JLabel.class));
    }

    /**
     * @return JTextComponentFixture for onset timestamp.
     */
    public final JTextComponentFixture onsetTimestamp() {
        return new JTextComponentFixture(robot, findByName("onsetTextField",
                TimeStampTextField.class));
    }

    /**
     * @return JTextComponentFixture for offset timestamp.
     */
    public final JTextComponentFixture offsetTimestamp() {
        return new JTextComponentFixture(robot, findByName("offsetTextField",
                TimeStampTextField.class));
    }

    /**
     * @return JTextComponentFixture for cell value.
     */
    public final JTextComponentFixture cellValue() {
        return new JTextComponentFixture(robot, findByName("cellValue",
                MatrixRootView.class));
    }

    /**
     * @param r rectange to find centre of
     * @return point at centre of rectange.
     */
    private Point centerOf(final Rectangle r) {
        return new Point(r.x + r.width / 2, r.y + r.height / 2);
    }

    public void select(final int component, int startPos, int endPos)
            throws BadLocationException {
        Point startPoint, endPoint;
        Component c;
        switch (component) {
        case VALUE:
            startPoint = centerOf(((SpreadsheetCell) target).getDataView()
                    .modelToView(startPos));
            endPoint = centerOf(((SpreadsheetCell) target).getDataView()
                    .modelToView(endPos));
            c = ((SpreadsheetCell) target).getDataView();
            break;
        case ONSET:
            startPoint = centerOf(((SpreadsheetCell) target).getOnset()
                    .modelToView(startPos));
            endPoint = centerOf(((SpreadsheetCell) target).getOnset()
                    .modelToView(endPos));
            c = ((SpreadsheetCell) target).getOnset();
            break;
        case OFFSET:
            startPoint = centerOf(((SpreadsheetCell) target).getOffset()
                    .modelToView(startPos));
            endPoint = centerOf(((SpreadsheetCell) target).getOffset()
                    .modelToView(endPos));
            c = ((SpreadsheetCell) target).getOffset();
            break;
        default:
            startPoint = centerOf(((SpreadsheetCell) target).getDataView()
                    .modelToView(startPos));
            endPoint = centerOf(((SpreadsheetCell) target).getDataView()
                    .modelToView(endPos));
            c = ((SpreadsheetCell) target).getDataView();
            break;
        }
      
        //First line is required to get focus on component
        robot.click(c, endPoint);
        //Click on start point and hold mouse
        robot.moveMouse(c, startPoint);
        robot.pressMouse(c, startPoint, MouseButton.LEFT_BUTTON);
        //Drag to end pos and release mouse
        robot.moveMouse(c, endPoint);
        robot.releaseMouse(MouseButton.LEFT_BUTTON);
    }


}
