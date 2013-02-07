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
import org.datavyu.Datavyu;

import com.usermetrix.jclient.UserMetrix;
import javax.swing.undo.UndoableEdit;
import org.datavyu.models.db.Cell;
import org.datavyu.models.db.Datastore;
import org.datavyu.undoableedits.ChangeCellEdit.Granularity;
import org.datavyu.undoableedits.ChangeOnsetCellEdit;

/**
 * Controller for setting all selected cells to have the specified start time /
 * onset.
 */
public class SetSelectedCellStartTimeC {

    /** The logger for this class. */
    private static Logger LOGGER = UserMetrix.getLogger(SetSelectedCellStartTimeC.class);

    /**
     * Sets all selected cells to have the specified start time / onset.
     * 
     * @param milliseconds The time in milliseconds to use for all selected
     * cells onset / start time.
     */
    public SetSelectedCellStartTimeC(final long milliseconds) {
        LOGGER.event("set selected cell onset");

        // Get the datastore that we are manipulating.
        Datastore datastore = Datavyu.getProjectController().getDB();

        for (Cell c : datastore.getSelectedCells()) {
            // record the effect
            UndoableEdit edit = new ChangeOnsetCellEdit(c, c.getOnset(), milliseconds, Granularity.FINEGRAINED);
            Datavyu.getView().getUndoSupport().postEdit(edit);

            c.setOnset(milliseconds);
        }
    }
}
