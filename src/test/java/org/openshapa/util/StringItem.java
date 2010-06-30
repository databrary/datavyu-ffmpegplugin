package org.openshapa.util;

import org.fest.swing.fixture.ComponentFixture;

/**
 * TextItem of type key.
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
    public final void enterItem(final ComponentFixture cf) {
        cf.robot.enterText(str);
    }
}
