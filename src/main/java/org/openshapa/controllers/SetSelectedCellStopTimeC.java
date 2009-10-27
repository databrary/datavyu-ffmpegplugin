package org.openshapa.controllers;

import org.apache.log4j.Logger;
import org.openshapa.OpenSHAPA;
import org.openshapa.db.DataCell;
import org.openshapa.db.SystemErrorException;
import org.openshapa.db.TimeStamp;
import org.openshapa.util.Constants;
import org.openshapa.views.discrete.SpreadsheetPanel;

/**
 * Controller for setting all selected cells to have the specified stop time /
 * offset.
 */
public class SetSelectedCellStopTimeC {
    /** The logger for this class. */
    private static Logger logger = Logger
                                   .getLogger(SetSelectedCellStopTimeC.class);

    /**
     * Sets all selected cells to have the specified stop time / offset.
     *
     * @param milliseconds The time in milliseconds to use for all selected
     * cells offset / stop time.
     */
    public SetSelectedCellStopTimeC(final long milliseconds) {

        // Get the view for this controller (the main spreadsheet panel.
        SpreadsheetPanel view = (SpreadsheetPanel) OpenSHAPA.getApplication()
                                                            .getMainView()
                                                            .getComponent();

        try {
            for (DataCell c : view.getSelectedCells()) {
                c.setOffset(new TimeStamp(Constants.TICKS_PER_SECOND,
                                          milliseconds));
                OpenSHAPA.getDatabase().replaceCell(c);
            }
        } catch (SystemErrorException se) {
            logger.error("Unable to set selected cell onset", se);
        }
    }
}
