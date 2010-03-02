package org.fest.swing.fixture;

import java.awt.Frame;
import java.awt.Point;

import org.fest.swing.core.Robot;

/**
 * @author dteoh
 */
public class OpenSHAPAFrameFixture extends FrameFixture {

    public OpenSHAPAFrameFixture(final Frame target) {
        super(target);
    }

    public OpenSHAPAFrameFixture(final String name) {
        super(name);
    }

    public OpenSHAPAFrameFixture(final Robot robot, final Frame target) {
        super(robot, target);
    }

    public OpenSHAPAFrameFixture(final Robot robot, final String name) {
        super(robot, name);
    }

    public JMenuItemFixture clickMenuItemWithPath(final String... path) {
        JMenuItemFixture result = super.menuItemWithPath(path).click();
        Point edgeOfWindow = new Point(this.component().getWidth() - 25,
                this.component().getHeight() - 25);
        this.robot.click(target, edgeOfWindow);
        return result;
    }

}
