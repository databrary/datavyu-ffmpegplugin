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
package org.datavyu.controllers;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;

import org.datavyu.Datavyu;
import java.util.List;
import org.datavyu.views.discrete.SpreadsheetPanel;

import org.datavyu.models.db.Datastore;
import org.datavyu.models.db.Variable;

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
        SpreadsheetPanel view = (SpreadsheetPanel) Datavyu.getView().getComponent();
        Datastore datastore = Datavyu.getProjectController().getDB();

        // Deselect everything.
        view.deselectAll();

        for (Variable var : colsToDelete) {
            datastore.removeVariable(var);
            view.revalidate();
            view.repaint();
        }
    }
}
