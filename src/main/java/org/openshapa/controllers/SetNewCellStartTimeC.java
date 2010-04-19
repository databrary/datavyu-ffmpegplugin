package org.openshapa.controllers;

import org.openshapa.OpenSHAPA;
import org.openshapa.models.db.DataCell;
import org.openshapa.models.db.Database;
import org.openshapa.models.db.SystemErrorException;
import org.openshapa.models.db.TimeStamp;
import org.openshapa.util.Constants;

import com.usermetrix.jclient.UserMetrix;

/**
 * Controller for setting the stop time (offset) of a new cell.
 */
public final class SetNewCellStartTimeC {

    /**
     * Sets the stop time of the last cell that was created.
     * 
     * @param milliseconds
     *            The number of milliseconds since the origin of the spreadsheet
     *            to set the stop time for.
     */
    public SetNewCellStartTimeC(final long milliseconds) {
        try {
            Database model = OpenSHAPA.getProjectController().getDB();

            DataCell cell =
                    (DataCell) model.getCell(OpenSHAPA.getProjectController()
                            .getLastCreatedCellId());
            cell.setOnset(new TimeStamp(Constants.TICKS_PER_SECOND,
                    milliseconds));
            model.replaceCell(cell);
        } catch (SystemErrorException e) {
            logger.error("Unable to set new cell stop time.", e);
        }
    }

    /** The logger for this class. */
    private UserMetrix logger =
            UserMetrix.getInstance(SetNewCellStartTimeC.class);
}
