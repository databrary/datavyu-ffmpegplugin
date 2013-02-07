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
