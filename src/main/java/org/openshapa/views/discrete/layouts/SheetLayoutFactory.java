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
package org.openshapa.views.discrete.layouts;

import org.openshapa.util.Constants;

/**
 * Factory for SheetLayouts.
 */
public final class SheetLayoutFactory {

    /** Types of SheetLayouts for a spreadsheet. */
    public enum SheetLayoutType {
        /**
         * Ordinal style layout. Cells stack underneath each other with
         * no temporal relation to each other across the spreadsheet.
         */
        Ordinal,
        /** Weak temporal order layout.  Cells distribute down and across
         * the spreadsheet preserving their temporal order but in the
         * least space required to show this.
         */
        WeakTemporal,
        /** Strong temporal order layout.  Cells distribute down and across
         * the spreadsheet preserving their temporal order and with their
         * sizes and gaps between them relative to their onsets and offsets.
         */
        StrongTemporal
    }

    /**
     * Creates the SheetLayout requested by type.
     * @param type Type of layout to create.
     * @param cols SpreadsheetColumns to lay out.
     * @return SheetLayout to use.
     */
    public static SheetLayout createLayout(final SheetLayoutType type) {
        switch (type) {
            case Ordinal:
                return new SheetLayoutOrdinal(Constants.BORDER_SIZE);
            case WeakTemporal:
                return new SheetLayoutWeakTemporal(Constants.BORDER_SIZE);
            case StrongTemporal:
                // Using the same layout for now.
                return new SheetLayoutWeakTemporal(Constants.BORDER_SIZE);
            default:
                throw new IllegalArgumentException("Unknown layout - " + type);
        }
    }

    /**
     * Private constructor.
     */
    private SheetLayoutFactory() {
    }
}
