package org.openshapa.util;

import org.fest.swing.fixture.ComponentFixture;

/**
 * Contains a vector of Strings or an array of Keys and a type tracking vector.
 */
public abstract class TextItem {
    /**
     * TextVector constructor.
     */
    public TextItem() {
    }

    /**
     * Enters item into a component.
     * 
     * @param c
     *            component to enter text into
     */
    public abstract void enterItem(ComponentFixture cf);
}
