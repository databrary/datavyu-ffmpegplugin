package org.openshapa.controllers;

import org.apache.log4j.Logger;
import org.openshapa.OpenSHAPA;
import org.openshapa.models.db.DataCell;
import org.openshapa.models.db.SystemErrorException;
import org.openshapa.models.db.TimeStamp;
import org.openshapa.util.Constants;
import org.openshapa.views.discrete.SpreadsheetPanel;

/**
 * Controller for setting all selected cells to have the specified start time /
 * onset.
 */
public class SetSelectedCellStartTimeC {
    /** The logger for this class. */
    private static Logger logger = Logger
                                   .getLogger(SetSelectedCellStartTimeC.class);

    /**
     * Sets all selected cells to have the specified start time / onset.
     *
     * @param milliseconds The time in milliseconds to use for all selected
     * cells onset / start time.
     */
    public SetSelectedCellStartTimeC(final long milliseconds) {

        // Get the view for this controller (the main spreadsheet panel.
        SpreadsheetPanel view = (SpreadsheetPanel) OpenSHAPA.getApplication()
                                                            .getMainView()
                                                            .getComponent();

        try {
            for (DataCell c : view.getSelectedCells()) {
                c.setOnset(new TimeStamp(Constants.TICKS_PER_SECOND,
                                         milliseconds));
                OpenSHAPA.getDB().replaceCell(c);
            }
        } catch (SystemErrorException se) {
            logger.error("Unable to set selected cell onset", se);
        }
    }
}
