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
package org.openshapa.controllers;

import com.usermetrix.jclient.Logger;

import org.openshapa.OpenSHAPA;


import org.openshapa.views.discrete.SpreadsheetPanel;

import com.usermetrix.jclient.UserMetrix;
import java.util.List;
import org.openshapa.models.db.Cell;

/**
 * Controller for deleting cells from the database.
 */
public final class DeleteCellC {

    /** The logger for this class. */
    private static final Logger LOGGER = UserMetrix.getLogger(DeleteColumnC.class);

    /**
     * Constructor.
     *
     * @param cellsToDelete The cells to delete from the spreadsheet.
     */
    public DeleteCellC(final List<Cell> cellsToDelete) {
        LOGGER.event("delete cells");

        // The spreadsheet is the view for this controller.
        SpreadsheetPanel view = (SpreadsheetPanel) OpenSHAPA.getApplication()
                                                   .getMainView().getComponent();
        view.deselectAll();

        for (Cell c : cellsToDelete) {

            // Check if the cell we are deleting is the last created cell.
            // Default this back to 0 if it is.
            if (c.equals(OpenSHAPA.getProjectController().getLastCreatedCell())) {
                OpenSHAPA.getProjectController().setLastCreatedCell(null);
            }

            // Check if the cell we are deleting is the last selected cell.
            // Default this back to 0 if it is.
            if (c.equals(OpenSHAPA.getProjectController().getLastSelectedCell())) {
                OpenSHAPA.getProjectController().setLastSelectedCell(null);
            }

            OpenSHAPA.getProjectController().getDB().removeCell(c);
        }
    }
}
