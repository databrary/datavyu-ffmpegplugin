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
import com.usermetrix.jclient.UserMetrix;

import org.openshapa.OpenSHAPA;
import database.Cell;
import database.DataColumn;
import database.MacshapaDatabase;
import database.SystemErrorException;
import java.util.List;
import org.openshapa.views.discrete.SpreadsheetPanel;


import org.openshapa.models.db.Datastore;
import org.openshapa.models.db.DeprecatedDatabase;
import org.openshapa.models.db.DeprecatedVariable;
import org.openshapa.models.db.Variable;

/**
 * Controller for deleting cells from the database.
 */
public final class DeleteColumnC {

    /** The logger for this class. */
    private static Logger LOGGER = UserMetrix.getLogger(DeleteColumnC.class);

    /**
     * Constructor.
     * 
     * @param colsToDelete The columns to remove from the database/spreadsheet.
     */
    public DeleteColumnC(final List<Variable> colsToDelete) {
        LOGGER.event("delete columns");

        // The spreadsheet is the view for this controller.
        SpreadsheetPanel view =
                (SpreadsheetPanel) OpenSHAPA.getApplication().getMainView()
                        .getComponent();
        MacshapaDatabase model = OpenSHAPA.getProjectController().getLegacyDB().getDatabase();
        Datastore datastore =  OpenSHAPA.getProjectController().getDB();
        
        try {
            // Deselect everything.
            view.deselectAll();

            for (Variable var : colsToDelete) {
                DataColumn dc = ((DeprecatedVariable) var).getLegacyVariable();

                // All cells in the column removed - now delete the column.
                // Must remove cells from the data column before removing it.
                while (dc.getNumCells() > 0) {
                    Cell c = model.getCell(dc.getID(), 1);
                    // Check if the cell we are deleting is the last created
                    // cell... Default this back to 0.
                    if (c.getID() == OpenSHAPA.getProjectController().getLastCreatedCellId()) {
                        OpenSHAPA.getProjectController().setLastCreatedCellId(0);
                    }
                    model.removeCell(c.getID());
                    dc = model.getDataColumn(dc.getID());
                }
                // Check if the column we are deleting was the last created
                // column... Default this back to 0 if it is.
                if (dc.getID() == OpenSHAPA.getProjectController().getLastCreatedColId()) {
                    OpenSHAPA.getProjectController().setLastCreatedColId(0);
                }
                
                model.removeColumn(dc.getID());
                
                ((DeprecatedDatabase)datastore).removeVariable(dc.getID());
                
                view.revalidate();
                view.repaint();
            }
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to delete columns.", e);
        }
    }
}
