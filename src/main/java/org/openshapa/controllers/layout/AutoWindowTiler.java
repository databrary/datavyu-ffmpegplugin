/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openshapa.controllers.layout;

import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Window;

import java.util.PriorityQueue;

import org.openshapa.OpenSHAPA;

import org.openshapa.models.layout.Tile;
import org.openshapa.models.layout.TileComparator;
import org.openshapa.models.layout.WindowComparator;

import org.openshapa.plugins.DataViewer;


/**
 * Automatic window tiling using first-fit decreasing greedy algorithm.
 * OpenSHAPA's data controller will not be tiled as it is unresizable.
 */
public final class AutoWindowTiler {

    private final PriorityQueue<Tile> tiles;
    private final PriorityQueue<Window> windows;

    public AutoWindowTiler() {
        tiles = new PriorityQueue<Tile>(2, new TileComparator());
        windows = new PriorityQueue<Window>(2, new WindowComparator());
    }

    public void tile() {

        // Add the main screen as our initial tile.
        Rectangle scrBounds = GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getMaximumWindowBounds();
        Tile screen = new Tile(scrBounds);
        tiles.add(screen);

        // Add all resizable OpenSHAPA windows to our list of windows.
        windows.add(OpenSHAPA.getView().getFrame());

        for (DataViewer dv : OpenSHAPA.getDataController().getDataViewers()) {
            windows.add(dv.getParentJDialog());
        }

        while (!windows.isEmpty() && !tiles.isEmpty()) {
            Window window = windows.poll();

            Tile tile = tiles.poll();

            Rectangle newBounds = tile.fitToTile(window.getBounds().getSize());
            window.setBounds(newBounds);

            for (Tile leftover : tile.subtract(newBounds.getSize())) {
                tiles.add(leftover);
            }
        }
    }


}
