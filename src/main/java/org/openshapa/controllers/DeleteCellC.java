package org.openshapa.controllers;

import com.usermetrix.jclient.Logger;

import org.openshapa.OpenSHAPA;

import org.openshapa.models.db.legacy.DataCell;
import org.openshapa.models.db.legacy.MacshapaDatabase;
import org.openshapa.models.db.legacy.SystemErrorException;

import org.openshapa.views.discrete.SpreadsheetPanel;

import com.usermetrix.jclient.UserMetrix;
import java.util.List;

/**
 * Controller for deleting cells from the database.
 */
public final class DeleteCellC {

    /** The logger for this class. */
    private static final Logger LOGGER = UserMetrix.getLogger(DeleteColumnC.class);

    /**
     * Constructor.
     *
     * @param cellsToDelete The cells to delete from the spreadsheet.
     */
    public DeleteCellC(final List<DataCell> cellsToDelete) {
        LOGGER.usage("delete cells");

        // The spreadsheet is the view for this controller.
        SpreadsheetPanel view = (SpreadsheetPanel) OpenSHAPA.getApplication()
                                                   .getMainView().getComponent();
        MacshapaDatabase model = OpenSHAPA.getProjectController()
                                          .getLegacyDB().getDatabase();
        view.deselectAll();

        try {
            for (DataCell c : cellsToDelete) {

                // Check if the cell we are deleting is the last created cell.
                // Default this back to 0 if it is.
                if (c.getID() == OpenSHAPA.getProjectController()
                                          .getLastCreatedCellId()) {
                    OpenSHAPA.getProjectController().setLastCreatedCellId(0);
                }

                // Check if the cell we are deleting is the last selected cell.
                // Default this back to 0 if it is.
                if (c.getID() == OpenSHAPA.getProjectController()
                                          .getLastSelectedCellId()) {
                    OpenSHAPA.getProjectController().setLastSelectedCellId(0);
                }

                model.removeCell(c.getID());
            }
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to delete cells", e);
        }
    }
}
