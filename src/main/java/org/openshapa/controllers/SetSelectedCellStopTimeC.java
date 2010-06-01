package org.openshapa.controllers;

import com.usermetrix.jclient.Logger;
import org.openshapa.OpenSHAPA;
import org.openshapa.models.db.DataCell;
import org.openshapa.models.db.SystemErrorException;
import org.openshapa.models.db.TimeStamp;
import org.openshapa.util.Constants;
import org.openshapa.views.discrete.SpreadsheetPanel;

import com.usermetrix.jclient.UserMetrix;

/**
 * Controller for setting all selected cells to have the specified stop time /
 * offset.
 */
public class SetSelectedCellStopTimeC {
    /** The logger for this class. */
    private Logger logger = UserMetrix.getLogger(SetSelectedCellStopTimeC.class);

    /**
     * Sets all selected cells to have the specified stop time / offset.
     * 
     * @param milliseconds
     *            The time in milliseconds to use for all selected cells offset
     *            / stop time.
     */
    public SetSelectedCellStopTimeC(final long milliseconds) {

        logger.usage("set selected cell offset");

        // Get the view for this controller (the main spreadsheet panel.
        SpreadsheetPanel view =
                (SpreadsheetPanel) OpenSHAPA.getApplication().getMainView()
                        .getComponent();

        try {
            for (DataCell c : view.getSelectedCells()) {
                c.setOffset(new TimeStamp(Constants.TICKS_PER_SECOND,
                        milliseconds));
                OpenSHAPA.getProjectController().getDB().replaceCell(c);
            }
        } catch (SystemErrorException se) {
            logger.error("Unable to set selected cell onset", se);
        }
    }
}
