package org.fest.swing.fixture;

import java.awt.Frame;

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

    @Override
    public JMenuItemFixture menuItemWithPath(final String... path) {
        JMenuItemFixture result = super.menuItemWithPath(path);
        this.click();
        return result;
    }

}
