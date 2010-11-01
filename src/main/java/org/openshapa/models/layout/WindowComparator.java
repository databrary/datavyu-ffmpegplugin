package org.openshapa.models.layout;

import java.awt.Window;

import java.util.Comparator;


public final class WindowComparator implements Comparator<Window> {

    @Override public int compare(final Window w1, final Window w2) {
        return (w1.getWidth() * w1.getHeight())
            - (w2.getWidth() * w2.getHeight());
    }

}
