package org.uispec4j;

import java.awt.Component;
import org.uispec4j.utils.KeyUtils;

/**
 * TextItem of type key.
 *
 */
public class KeyItem extends TextItem {
    /**
     * The Key.
     */
    private Key key;

    /**
     * TextVector constructor.
     * @param k Key
     */
    public KeyItem(final Key k) {
        key = k;
    }

   @Override
    public void enterItem(final Component c) {
        KeyUtils.enterKey(c, key);
    }
}
