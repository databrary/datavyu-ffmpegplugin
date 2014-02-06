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
package org.datavyu.views.discrete.datavalues;

import org.datavyu.views.discrete.EditorComponent;

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;

/**
 * NoEditor is used in EditorTracker when there is no sensible current Editor.
 */
public final class NoEditor extends EditorComponent {

    /**
     * Constructor.
     */
    public NoEditor() {
    }

    /**
     * Subclass overrides to handle keyPressed events.
     *
     * @param e KeyEvent details.
     */
    @Override
    public void keyPressed(final KeyEvent e) {
    }

    /**
     * Subclass overrides to handle keyTyped events.
     *
     * @param e KeyEvent details.
     */
    @Override
    public void keyTyped(final KeyEvent e) {
    }

    /**
     * Subclass overrides to handle keyReleased events.
     *
     * @param e KeyEvent details.
     */
    @Override
    public void keyReleased(final KeyEvent e) {
    }

    /**
     * Subclass overrides to handle focusSet state.
     *
     * @param fe FocusEvent details.
     */
    @Override
    public void focusGained(final FocusEvent fe) {
    }
}