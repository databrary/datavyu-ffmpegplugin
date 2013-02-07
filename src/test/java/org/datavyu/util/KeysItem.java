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
package org.datavyu.util;

import org.fest.swing.fixture.ComponentFixture;

/**
 * TextItem of type key array.
 */
public class KeysItem extends TextItem {
    /**
     * The array of keys.
     */
    private int[] keys;

    /**
     * TextVector constructor.
     * @param k Keys array
     * @see KeyEvent
     */
    public KeysItem(final int[] k) {
        keys = k;
    }

    @Override
    public final void enterItem(final ComponentFixture cf) {
        for (int key = 0; key < keys.length; key++) {
            cf.robot.pressAndReleaseKey(keys[key]);
        }
    }
}
