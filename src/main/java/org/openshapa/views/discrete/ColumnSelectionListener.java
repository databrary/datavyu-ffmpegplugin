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
package org.openshapa.views.discrete;

/**
 * A listener for listening to column selection changes.
 */
public interface ColumnSelectionListener {

    /**
     * Adds a column to the selected columns.
     *
     * @param column The cell to add to the selection.
     */
    void addColumnToSelection(SpreadsheetColumn column);

    /**
     * Notifiers the listeners to clear all the currently selected columns.
     */
    void clearColumnSelection();
}
