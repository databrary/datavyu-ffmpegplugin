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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.datavyu.Datavyu;
import org.datavyu.models.db.Cell;
import org.datavyu.models.db.Datastore;
import org.datavyu.undoableedits.ChangeCellEdit;
import org.datavyu.undoableedits.ChangeOffsetCellEdit;

import javax.swing.undo.UndoableEdit;

/**
 * Controller for setting all selected cells to have the specified stop time /
 * offset.
 */
public class SetSelectedCellStopTimeC {

    /**
     * The logger for this class.
     */
    private static Logger LOGGER = LogManager.getLogger(SetSelectedCellStopTimeC.class);

    /**
     * Sets all selected cells to have the specified stop time / offset.
     *
     * @param milliseconds The time in milliseconds to use for all selected
     *                     cells offset / stop time.
     */
    public SetSelectedCellStopTimeC(final long milliseconds) {
        LOGGER.info("set selected cell offset");

        // Get the datastore that we are manipulating.
        Datastore datastore = Datavyu.getProjectController().getDB();

        for (Cell c : datastore.getSelectedCells()) {
            // record the effect
            UndoableEdit edit = new ChangeOffsetCellEdit(c, c.getOffset(), milliseconds, ChangeCellEdit.Granularity.FINEGRAINED);
            Datavyu.getView().getUndoSupport().postEdit(edit);

            c.setOffset(milliseconds);
        }
    }
}
