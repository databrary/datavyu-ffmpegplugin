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
package org.openshapa.models.layout;

import java.awt.Window;

import java.util.Comparator;


public final class WindowComparator implements Comparator<Window> {

    @Override public int compare(final Window w1, final Window w2) {
        return (w2.getWidth() * w2.getHeight())
            - (w1.getWidth() * w1.getHeight());
    }

}
