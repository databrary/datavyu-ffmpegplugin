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
package org.openshapa.util;

/**
 * Direction for navigating arrays (left or right).
 */
public enum ArrayDirection {
    /** LEFT create direction. */
    LEFT (-1),

    /** RIGHt create direction. */
    RIGHT (1);


    /** The modifier for navigating arrays in the desired direction. */
    private final int modifier;

    /**
     * Constructor.
     *
     * @param mod The direction modifier (for navigating arrays). LEFT =
     * backwards, RIGHT = forwards.
     */
    ArrayDirection(final int mod) {
        this.modifier = mod;
    }

    /**
     * @return The direction modifier (for navigating arrays).
     */
    public int getModifier() {
        return this.modifier;
    }
}
