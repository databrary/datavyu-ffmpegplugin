package org.uispec4j;

import java.awt.Component;
import org.uispec4j.utils.KeyUtils;

/**
 * TextItem of type key.
 *
 */
public class StringItem extends TextItem {

    /**
     * The Key.
     */
    private String str;

    /**
     * TextVector constructor.
     * @param s String
     */
    public StringItem(final String s) {
        str = s;
    }

    @Override
    public void enterItem(final Component c) {
        KeyUtils.enterString(c, str);
    }
}
