package org.openshapa.controllers;

import com.usermetrix.jclient.Logger;
import org.openshapa.OpenSHAPA;
import database.DataCell;
import database.SystemErrorException;
import database.TimeStamp;
import org.openshapa.util.Constants;
import org.openshapa.views.discrete.SpreadsheetPanel;

import com.usermetrix.jclient.UserMetrix;

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
     * @param milliseconds
     *            The time in milliseconds to use for all selected cells onset /
     *            start time.
     */
    public SetSelectedCellStartTimeC(final long milliseconds) {

        LOGGER.event("set selected cell onset");

        // Get the view for this controller (the main spreadsheet panel.
        SpreadsheetPanel view =
                (SpreadsheetPanel) OpenSHAPA.getApplication().getMainView()
                        .getComponent();

        try {
            for (DataCell c : view.getSelectedCells()) {
                c.setOnset(new TimeStamp(Constants.TICKS_PER_SECOND,
                        milliseconds));
                OpenSHAPA.getProjectController().getLegacyDB().getDatabase().replaceCell(c);
            }
        } catch (SystemErrorException se) {
            LOGGER.error("Unable to set selected cell onset", se);
        }
    }
}
