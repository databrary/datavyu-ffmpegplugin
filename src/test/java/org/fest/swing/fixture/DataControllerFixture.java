package org.fest.swing.fixture;

import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import org.fest.swing.core.Robot;
import org.openshapa.views.DataControllerV;

public class DataControllerFixture extends DialogFixture {

    DataControllerV dataController;

    public DataControllerFixture(Robot robot, DataControllerV target) {
        super(robot, target);
        dataController = target;
    }

    public String getCurrentTime() {
        return new JLabelFixture(robot,
            findByName("timestampLabel", JLabel.class)).text();
    }

    public void pressSetOffsetButton() {
        new JButtonFixture(robot,
            findByName("setCellOffsetButton", JButton.class)).click();
    }

    public void pressFindButton() {
        new JButtonFixture(robot,
            findByName("findButton", JButton.class)).click();
    }

    public void pressShiftFindButton() {
        robot.pressModifiers(KeyEvent.SHIFT_MASK);
        pressFindButton();
        robot.releaseModifiers(KeyEvent.SHIFT_MASK);
    }

    public String getFindOnset() {
        return new JTextComponentFixture(robot,
            findByName("findOnsetLabel", JTextField.class)).text();
    }

    public String getFindOffset() {
        return new JTextComponentFixture(robot,
            findByName("findOffsetLabel", JTextField.class)).text();
    }
}
