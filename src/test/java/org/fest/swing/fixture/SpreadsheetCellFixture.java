package org.fest.swing.fixture;

import java.awt.Component;
import java.awt.Point;
import java.awt.image.ImageObserver;

import javax.swing.JLabel;
import javax.swing.text.BadLocationException;

import org.fest.swing.core.MouseButton;
import org.fest.swing.core.Robot;
import org.fest.swing.util.Platform;

import org.openshapa.util.UIUtils;

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
     * Fill selects the cell.
     */
    public final void fillSelectCell(final boolean select) {

        if ((select && !((SpreadsheetCell) target).isFilled())
                || (!select && ((SpreadsheetCell) target).isFilled())) {
            JLabel ordinalLabel = ordinalLabel().target;
            Point labelPosition = ordinalLabel.getLocation();
            Point clickPosition = new Point(labelPosition.x
                    + ImageObserver.WIDTH + 25,
                    labelPosition.y + 5);
            robot.pressKey(Platform.controlOrCommandKey());
            robot.click(component(), clickPosition);
            robot.releaseKey(Platform.controlOrCommandKey());
        }
    }

    /**
     * Border selects the cell.
     */
    public final void borderSelectCell(final boolean select) {

        if ((select && !((SpreadsheetCell) target).isHighlighted())
                || (!select && ((SpreadsheetCell) target).isHighlighted())) {
            cellValue().click();
        }
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
            charPoint = UIUtils.centerOf(((SpreadsheetCell) target)
                    .getDataView().modelToView(charPos));
            c = ((SpreadsheetCell) target).getDataView();

            break;

        case ONSET:
            charPoint = UIUtils.centerOf(((SpreadsheetCell) target).getOnset()
                    .modelToView(charPos));
            c = ((SpreadsheetCell) target).getOnset();

            break;

        case OFFSET:
            charPoint = UIUtils.centerOf(((SpreadsheetCell) target).getOffset()
                    .modelToView(charPos));
            c = ((SpreadsheetCell) target).getOffset();

            break;

        default:
            charPoint = UIUtils.centerOf(((SpreadsheetCell) target)
                    .getDataView().modelToView(charPos));
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
        return new JTextComponentFixture(robot,
                findByName("onsetTextField",
                    TimeStampTextField.class));
    }

    /**
     * @return JTextComponentFixture for offset timestamp.
     */
    public final JTextComponentFixture offsetTimestamp() {
        return new JTextComponentFixture(robot,
                findByName("offsetTextField",
                    TimeStampTextField.class));
    }

    /**
     * @return JTextComponentFixture for cell value.
     */
    public final JTextComponentFixture cellValue() {
        return new JTextComponentFixture(robot,
                findByName("cellValue",
                    MatrixRootView.class));
    }

    /**
     * Selects text in component from start position to end position.
     * @param component to select text in
     * @param startPos start postion (0 is beginning)
     * @param endPos end pos
     * @throws BadLocationException on bad location exception
     */
    public void select(final int component, final int startPos,
        final int endPos) throws BadLocationException {
        Point startPoint, endPoint;
        Component c;

        switch (component) {

        case VALUE:
            startPoint = UIUtils.centerOf(((SpreadsheetCell) target)
                    .getDataView().modelToView(startPos));
            endPoint = UIUtils.centerOf(((SpreadsheetCell) target).getDataView()
                    .modelToView(endPos));
            c = ((SpreadsheetCell) target).getDataView();

            break;

        case ONSET:
            startPoint = UIUtils.centerOf(((SpreadsheetCell) target).getOnset()
                    .modelToView(startPos));
            endPoint = UIUtils.centerOf(((SpreadsheetCell) target).getOnset()
                    .modelToView(endPos));
            c = ((SpreadsheetCell) target).getOnset();

            break;

        case OFFSET:
            startPoint = UIUtils.centerOf(((SpreadsheetCell) target).getOffset()
                    .modelToView(startPos));
            endPoint = UIUtils.centerOf(((SpreadsheetCell) target).getOffset()
                    .modelToView(endPos));
            c = ((SpreadsheetCell) target).getOffset();

            break;

        default:
            startPoint = UIUtils.centerOf(((SpreadsheetCell) target)
                    .getDataView().modelToView(startPos));
            endPoint = UIUtils.centerOf(((SpreadsheetCell) target).getDataView()
                    .modelToView(endPos));
            c = ((SpreadsheetCell) target).getDataView();

            break;
        }

        // First line is required to get focus on component
        robot.click(c, endPoint);

        // Click on start point and hold mouse
        robot.moveMouse(c, startPoint);
        robot.pressMouse(c, startPoint, MouseButton.LEFT_BUTTON);

        // Drag to end pos and release mouse
        robot.moveMouse(c, endPoint);
        robot.releaseMouse(MouseButton.LEFT_BUTTON);
    }

    /**
     * @return true if cell is highlight or fill selected, else false
     */
    public boolean isSelected() {
        return ((SpreadsheetCell) target).isSelected();
    }

    /**
     * @return cell ID.
     */
    public int getID() {
        return Integer.parseInt(ordinalLabel().text());
    }


}
