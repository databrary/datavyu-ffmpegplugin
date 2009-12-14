package org.openshapa.controllers;

import org.openshapa.OpenSHAPA;
import org.openshapa.db.DataCell;
import org.openshapa.db.Database;
import org.openshapa.db.SystemErrorException;
import org.openshapa.db.TimeStamp;
import org.openshapa.util.Constants;
import org.apache.log4j.Logger;

/**
 * Controller for setting the stop time (offset) of a new cell.
 */
public final class SetNewCellStartTimeC {

    /**
     * Sets the stop time of the last cell that was created.
     *
     * @param milliseconds The number of milliseconds since the origin of the
     * spreadsheet to set the stop time for.
     */
    public SetNewCellStartTimeC(final long milliseconds) {
        try {
            Database model = OpenSHAPA.getDB();

            DataCell cell = (DataCell) model.getCell(OpenSHAPA
                                                       .getLastCreatedCellId());
            cell.setOnset(new TimeStamp(Constants.TICKS_PER_SECOND,
                                         milliseconds));
            model.replaceCell(cell);
        } catch (SystemErrorException e) {
            logger.error("Unable to set new cell stop time.", e);
        }
    }

    /** The logger for this class. */
    private static Logger logger = Logger.getLogger(SetNewCellStartTimeC.class);
}
