package org.uispec4j;

import java.awt.event.KeyEvent;

import org.fest.swing.fixture.ComponentFixture;
import org.openshapa.util.TextItem;

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
     * 
     * @param k
     *            Keys array
     * @see KeyEvent
     */
    public KeysItem(final int[] k) {
        keys = k;
    }

    @Override
    public void enterItem(final ComponentFixture cf) {
        for (int key = 0; key < keys.length; key++) {
            cf.robot.pressAndReleaseKey(keys[key]);
        }
    }
}
