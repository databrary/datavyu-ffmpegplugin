package org.openshapa.controllers.layout;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;

import java.util.List;
import java.util.Stack;

import org.openshapa.OpenSHAPA;

import org.openshapa.models.layout.Tile;

import org.openshapa.plugins.DataViewer;

import com.google.common.collect.Lists;


public final class SingleWindowTiler {

    private final List<Tile> tiles;

    public SingleWindowTiler() {
        tiles = Lists.newLinkedList();
    }

    public void tile(final Window w) {

        // Add the main screen as our initial tile.
        Dimension scrDim = Toolkit.getDefaultToolkit().getScreenSize();

        // Add all OpenSHAPA windows except the given window to our list of
        // tiles.
        tiles.add(new Tile(OpenSHAPA.getView().getFrame().getBounds()));
        tiles.add(new Tile(OpenSHAPA.getDataController().getBounds()));

        for (DataViewer viewer : OpenSHAPA.getDataController().getDataViewers()) {

            if (viewer != w) {
                tiles.add(new Tile(viewer.getParentJDialog().getBounds()));
            }
        }

        Tile best = null;
        int[] cache = new int[scrDim.height];
        Stack<Pair> s = new Stack<Pair>();

        for (int x = scrDim.width - 1; x >= 0; x--) {
            updateCache(cache, x);

            int width = 0;

            for (int y = 0; y < scrDim.height; y++) {

                if (cache[y] > width) {
                    s.push(new Pair(y, width));
                    width = cache[y];
                }


                if (cache[y] < width) {
                    Pair p = null;

                    do {
                        p = s.pop();

                        if ((width * (y - p.y)) > area(best)) {
                            best = new Tile(width, y - p.y, x, p.y);
                        }

                        width = p.width;
                    } while (cache[y] < width);

                    width = cache[y];

                    if (width != 0) {
                        s.push(new Pair(p.y, width));
                    }
                }
            }
        }

        if (best != null) {
            Rectangle newBounds = best.fitToTile(w.getBounds().getSize());
            w.setBounds(newBounds);
        }

    }


    private void updateCache(final int[] cache, final int x) {

        for (int y = 0; y < cache.length; y++) {

            if (outsideAll(x, y)) {
                cache[y] = cache[y] + 1;
            } else {
                cache[y] = 0;
            }
        }
    }


    private int area(final Tile t) {

        if (t == null) {
            return 0;
        }

        return t.area();
    }

    private boolean outsideAll(final int x, final int y) {

        for (Tile t : tiles) {

            if (t.inside(x, y)) {
                return false;
            }
        }

        return true;
    }

    private static class Pair {
        int y;
        int width;

        Pair(final int y, final int width) {
            this.y = y;
            this.width = width;
        }
    }

}
