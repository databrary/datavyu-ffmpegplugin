package org.openshapa.controllers;

import java.util.Vector;
import org.apache.log4j.Logger;
import org.openshapa.OpenSHAPA;
import org.openshapa.models.db.DataCell;
import org.openshapa.models.db.MacshapaDatabase;
import org.openshapa.models.db.SystemErrorException;
import org.openshapa.views.OpenSHAPAView;
import org.openshapa.views.discrete.SpreadsheetPanel;

/**
 * Controller for deleting cells from the database.
 */
public final class DeleteCellC {

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(DeleteColumnC.class);

    /**
     * Constructor.
     *
     * @param cellsToDelete The cells to delete from the spreadsheet.
     */
    public DeleteCellC(final Vector<DataCell> cellsToDelete) {
        // The spreadsheet is the view for this controller.
        SpreadsheetPanel view = (SpreadsheetPanel) OpenSHAPA.getApplication()
                                                            .getMainView()
                                                            .getComponent();
        MacshapaDatabase model = OpenSHAPA.getProject().getDB();

        view.deselectAll();

        try {
            for (DataCell c : cellsToDelete) {
                // Check if the cell we are deleting is the last created cell...
                // Default this back to 0 if it is.
                if (c.getID() == OpenSHAPA.getLastCreatedCellId()) {
                    OpenSHAPA.setLastCreatedCellId(0);
                }

                model.removeCell(c.getID());
            }

            OpenSHAPAView v = (OpenSHAPAView) OpenSHAPA.getApplication()
                                                       .getMainView();
            v.showSpreadsheet();
        } catch (SystemErrorException e) {
            logger.error("Unable to delete cells", e);
        }
    }
}
