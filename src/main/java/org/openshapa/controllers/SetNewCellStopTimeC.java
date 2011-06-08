package org.openshapa.controllers;

import com.usermetrix.jclient.Logger;
import org.openshapa.OpenSHAPA;
import org.openshapa.models.db.legacy.DataCell;
import org.openshapa.models.db.legacy.Database;
import org.openshapa.models.db.legacy.SystemErrorException;
import org.openshapa.models.db.legacy.TimeStamp;
import org.openshapa.util.Constants;

import com.usermetrix.jclient.UserMetrix;

/**
 * Controller for setting the stop time (offset) of a new cell.
 */
public final class SetNewCellStopTimeC {

    /**
     * Sets the stop time of the last cell that was created.
     * 
     * @param milliseconds
     *            The number of milliseconds since the origin of the spreadsheet
     *            to set the stop time for.
     */
    public SetNewCellStopTimeC(final long milliseconds) {
        try {
            LOGGER.event("set new cell offset");
            Database model = OpenSHAPA.getProjectController().getLegacyDB().getDatabase();

            DataCell cell =
                    (DataCell) model.getCell(OpenSHAPA.getProjectController()
                            .getLastCreatedCellId());
            cell.setOffset(new TimeStamp(Constants.TICKS_PER_SECOND,
                    milliseconds));
            model.replaceCell(cell);
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to set new cell stop time.", e);
        }
    }

    /** The logger for this class. */
    private static Logger LOGGER = UserMetrix.getLogger(SetNewCellStopTimeC.class);
}
