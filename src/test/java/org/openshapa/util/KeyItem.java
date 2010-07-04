package org.openshapa.util;

import org.fest.swing.fixture.ComponentFixture;

/**
 * TextItem of type key.
 */
public class KeyItem extends TextItem {
    /**
     * The Key.
     */
    private int key;

    /**
     * TextVector constructor.
     * @param k key
     * @seealso KeyEvent
     */
    public KeyItem(final int k) {
        key = k;
    }

    @Override
    public final void enterItem(final ComponentFixture cf) {
        cf.robot.pressAndReleaseKey(key);
    }
}
