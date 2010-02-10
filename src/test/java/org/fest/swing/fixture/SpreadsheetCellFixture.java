/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fest.swing.fixture;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.ImageObserver;

import javax.swing.JLabel;

import org.fest.swing.core.Robot;
import org.openshapa.views.discrete.SpreadsheetCell;
import org.openshapa.views.discrete.datavalues.MatrixRootView;
import org.openshapa.views.discrete.datavalues.TimeStampTextField;

/**
 * @author mmuthukrishna
 */
public class SpreadsheetCellFixture extends JPanelFixture {

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
}
