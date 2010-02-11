/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fest.swing.fixture;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.ImageObserver;

import javax.swing.JLabel;
import javax.swing.text.BadLocationException;

import org.fest.swing.core.Robot;
import org.openshapa.views.discrete.SpreadsheetCell;
import org.openshapa.views.discrete.datavalues.MatrixRootView;
import org.openshapa.views.discrete.datavalues.TimeStampTextField;

/**
 * @author mmuthukrishna
 */
public class SpreadsheetCellFixture extends JPanelFixture {
    public static final int VALUE = 0;
    public static final int ONSET = 1;
    public static final int OFFSET = 2;

    public SpreadsheetCellFixture(Robot robot, String panelName) {
        super(robot, panelName);
    }

    public SpreadsheetCellFixture(Robot robot, SpreadsheetCell target) {
        super(robot, target);
    }

    public void selectCell() {
        JLabel ordinalLabel = ordinalLabel().target;
        Point labelPosition = ordinalLabel.getLocation();
        Point clickPosition =
                new Point(labelPosition.x + ImageObserver.WIDTH + 25,
                        labelPosition.y + 5);
        robot.pressKey(KeyEvent.VK_SHIFT);
        robot.click(component(), clickPosition);
        robot.releaseKey(KeyEvent.VK_SHIFT);
    }

    public void clickToCharPos(int component, int charPos, int times)
            throws BadLocationException {
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

    public JLabelFixture ordinalLabel() {
        return new JLabelFixture(robot, findByType(JLabel.class));
    }

    public JTextComponentFixture onsetTimestamp() {
        return new JTextComponentFixture(robot, findByName("onsetTextField",
                TimeStampTextField.class));
    }

    public JTextComponentFixture offsetTimestamp() {
        return new JTextComponentFixture(robot, findByName("offsetTextField",
                TimeStampTextField.class));
    }

    public JTextComponentFixture cellValue() {
        return new JTextComponentFixture(robot, findByName("cellValue",
                MatrixRootView.class));
    }

    private Point centerOf(Rectangle r) {
        return new Point(r.x + r.width / 2, r.y + r.height / 2);
    }
}
