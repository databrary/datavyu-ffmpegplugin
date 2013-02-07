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
package org.datavyu.event.component;

/**
 * Abstract class for handling carriage events.
 */
public abstract class CarriageEventAdapter implements CarriageEventListener {

    /**
     * @see org.datavyu.event.component.CarriageEventListener
     * #offsetChanged(org.datavyu.event.component.CarriageEvent)
     */
    @Override
    public void offsetChanged(final CarriageEvent e) {
        // Blank implementation.
    }

    /**
     * @see org.datavyu.event.component.CarriageEventListener
     * #requestBookmark(org.datavyu.event.component.CarriageEvent)
     */
    @Override
    public void requestBookmark(final CarriageEvent e) {
        // Blank implementation.
    }

    /**
     * @see org.datavyu.event.component.CarriageEventListener
     * #saveBookmark(org.datavyu.event.component.CarriageEvent)
     */
    @Override
    public void saveBookmark(final CarriageEvent e) {
        // Blank implementation.
    }

    /**
     * @see org.datavyu.event.component.CarriageEventListener
     * #selectionChanged(org.datavyu.event.component.CarriageEvent)
     */
    @Override
    public void selectionChanged(final CarriageEvent e) {
        // Blank implementation.
    }

    /**
     * @see org.datavyu.event.component.CarriageEventListener
     * #lockStateChanged(org.datavyu.event.component.CarriageEvent)
     */
    @Override
    public void lockStateChanged(final CarriageEvent e) {
        // Blank implementation.
    }

}
