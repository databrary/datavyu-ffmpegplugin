package org.uispec4j;

import java.awt.Component;
import org.uispec4j.utils.KeyUtils;

/**
 * TextItem of type key array.
 *
 */
public class KeysItem extends TextItem {
    /**
     * The array of keys.
     */
    private Key [] keys;

    /**
     * TextVector constructor.
     * @param k Keys array
     */
    public KeysItem(final Key [] k) {
        keys = k;
    }

   @Override
    public void enterItem(final Component c) {
        KeyUtils.enterKeys(c, keys);
    }
}
