package org.openshapa.controllers.layout;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openshapa.OpenSHAPA;

import org.openshapa.models.layout.Tile;
import org.openshapa.models.layout.TileComparator;
import org.openshapa.models.layout.WindowComparator;

import org.openshapa.plugins.DataViewer;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


/**
 * Automatic window tiling using first-fit decreasing greedy algorithm.
 * OpenSHAPA's data controller will not be tiled as it is unresizable.
 */
public final class AutoWindowTiler {

    private final Set<Tile> tiles;
    private final List<Window> windows;

    public AutoWindowTiler() {
        tiles = Sets.newTreeSet(new TileComparator());
        windows = Lists.newArrayList();
    }

    public void tile() {

        // Add the main screen as our initial tile.
        Dimension scrDim = Toolkit.getDefaultToolkit().getScreenSize();
        Tile screen = new Tile(scrDim.width, scrDim.height, 0, 0);
        tiles.add(screen);

        // Add all resizable OpenSHAPA windows to our list of windows.
        windows.add(OpenSHAPA.getView().getFrame());

        for (DataViewer dv : OpenSHAPA.getDataController().getDataViewers()) {
            windows.add(dv.getParentJDialog());
        }

        Collections.sort(windows, new WindowComparator());

        while (!windows.isEmpty() && !tiles.isEmpty()) {

            // Remove the last window because it is the largest. We could have
            // sorted it in descending order but this would cause our remove
            // operation to be O(n).
            Window window = windows.remove(windows.size() - 1);

            Iterator<Tile> it = tiles.iterator();
            Tile tile = it.next();
            it.remove();

            Rectangle newBounds = tile.fitToTile(window.getBounds().getSize());
            window.setBounds(newBounds);

            for (Tile leftover : tile.remainder(newBounds.getSize())) {
                tiles.add(leftover);
            }
        }
    }


}
