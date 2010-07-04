package org.openshapa.util;

import org.fest.swing.fixture.ComponentFixture;

/**
 * TextItem of type key array.
 */
public class KeysItem extends TextItem {
    /**
     * The array of keys.
     */
    private int[] keys;

    /**
     * TextVector constructor.
     * @param k Keys array
     * @see KeyEvent
     */
    public KeysItem(final int[] k) {
        keys = k;
    }

    @Override
    public final void enterItem(final ComponentFixture cf) {
        for (int key = 0; key < keys.length; key++) {
            cf.robot.pressAndReleaseKey(keys[key]);
        }
    }
}
