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

import java.util.EventListener;


/**
 * Interface for defining event handlers for events that the TrackPainter may
 * fire.
 */
public interface CarriageEventListener extends EventListener {

    /**
     * Event handler for a track's changed offset.
     *
     * @param e The event to handle.
     */
    void offsetChanged(CarriageEvent e);

    /**
     * Event handler for a track's bookmark request.
     *
     * @param e The event to handle.
     */
    void requestBookmark(CarriageEvent e);

    /**
     * Event handler for a track requesting bookmark saving.
     *
     * @param e The event to handle.
     */
    void saveBookmark(CarriageEvent e);

    /**
     * Event handler for a track's selected state change.
     *
     * @param e The event to handle.
     */
    void selectionChanged(CarriageEvent e);

    /**
     * Event handler for a track's lock state change.
     *
     * @param e
     */
    void lockStateChanged(CarriageEvent e);

}
