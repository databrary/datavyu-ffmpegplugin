package org.fest.swing.fixture;

import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import org.fest.swing.core.Robot;
import org.openshapa.views.DataControllerV;

/**
 * Fixture for OpenSHAPA DataController.
 */
public class DataControllerFixture extends DialogFixture {
    /**
     * Constructor.
     * @param robot main frame fixture robot
     * @param target data controller class
     */
    public DataControllerFixture(final Robot robot,
            final DataControllerV target) {
        super(robot, target);
    }

    /**
     * Current time.
     * @return String of currentTime.
     */
    public final String getCurrentTime() {
        return new JLabelFixture(robot,
            findByName("timestampLabel", JLabel.class)).text();
    }

    /**
     * Press set offset button.
     */
    public final void pressSetOffsetButton() {
        new JButtonFixture(robot,
            findByName("setCellOffsetButton", JButton.class)).click();
    }

    /**
     * Press find button.
     */
    public final void pressFindButton() {
        new JButtonFixture(robot,
            findByName("findButton", JButton.class)).click();
    }

    /**
     * Press Shift + Find Button.
     */
    public final void pressShiftFindButton() {
        robot.pressModifiers(KeyEvent.SHIFT_MASK);
        pressFindButton();
        robot.releaseModifiers(KeyEvent.SHIFT_MASK);
    }

    /**
     * Returns findOnset time.
     * @return String of find onset time.
     */
    public final String getFindOnset() {
        return new JTextComponentFixture(robot,
            findByName("findOnsetLabel", JTextField.class)).text();
    }

    /**
     * Returns findOffset time.
     * @return String of find offset time.
     */
    public final String getFindOffset() {
        return new JTextComponentFixture(robot,
            findByName("findOffsetLabel", JTextField.class)).text();
    }
}
