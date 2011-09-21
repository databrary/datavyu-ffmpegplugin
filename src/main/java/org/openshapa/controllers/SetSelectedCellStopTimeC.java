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
import database.DataCell;
import database.SystemErrorException;
import database.TimeStamp;
import org.openshapa.util.Constants;

import com.usermetrix.jclient.UserMetrix;
import org.openshapa.models.db.Cell;
import org.openshapa.models.db.Datastore;
import org.openshapa.models.db.DeprecatedCell;

/**
 * Controller for setting all selected cells to have the specified stop time /
 * offset.
 */
public class SetSelectedCellStopTimeC {
    /** The logger for this class. */
    private static Logger LOGGER = UserMetrix.getLogger(SetSelectedCellStopTimeC.class);

    /**
     * Sets all selected cells to have the specified stop time / offset.
     * 
     * @param milliseconds
     *            The time in milliseconds to use for all selected cells offset
     *            / stop time.
     */
    public SetSelectedCellStopTimeC(final long milliseconds) {

        LOGGER.event("set selected cell offset");

        // Get the datastore that we are manipulating.
        Datastore datastore = OpenSHAPA.getProjectController().getDB();

        try {
            for (Cell c : datastore.getSelectedCells()) {
                DataCell dc = ((DeprecatedCell) c).getLegacyCell();
                dc.setOffset(new TimeStamp(Constants.TICKS_PER_SECOND,
                        milliseconds));
                OpenSHAPA.getProjectController().getLegacyDB().getDatabase().replaceCell(dc);
            }
        } catch (SystemErrorException se) {
            LOGGER.error("Unable to set selected cell onset", se);
        }
    }
}
