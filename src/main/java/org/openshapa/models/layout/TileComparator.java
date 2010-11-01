package org.openshapa.models.layout;

import java.util.Comparator;


public final class TileComparator implements Comparator<Tile> {

    @Override public int compare(final Tile t1, final Tile t2) {
        return (t2.getWidth() * t2.getHeight())
            - (t1.getWidth() * t1.getHeight());
    }

}
