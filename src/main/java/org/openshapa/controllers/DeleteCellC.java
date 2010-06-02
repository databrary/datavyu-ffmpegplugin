package org.openshapa.controllers;

import java.util.Vector;

import org.openshapa.OpenSHAPA;

import org.openshapa.models.db.DataCell;
import org.openshapa.models.db.MacshapaDatabase;
import org.openshapa.models.db.SystemErrorException;

import org.openshapa.views.OpenSHAPAView;
import org.openshapa.views.discrete.SpreadsheetPanel;

import com.usermetrix.jclient.UserMetrix;


/**
 * Controller for deleting cells from the database.
 */
public final class DeleteCellC {

    /** The logger for this class. */
    private UserMetrix logger = UserMetrix.getInstance(DeleteColumnC.class);

    /**
     * Constructor.
     *
     * @param cellsToDelete
     *            The cells to delete from the spreadsheet.
     */
    public DeleteCellC(final Vector<DataCell> cellsToDelete) {
        logger.usage("delete cells");

        // The spreadsheet is the view for this controller.
        SpreadsheetPanel view = (SpreadsheetPanel) OpenSHAPA.getApplication()
            .getMainView().getComponent();
        MacshapaDatabase model = OpenSHAPA.getProjectController().getDB();
        view.deselectAll();

        try {
            for (DataCell c : cellsToDelete) {

                // Check if the cell we are deleting is the last created cell.
                // Default this back to 0 if it is.
                if (c.getID()
                        == OpenSHAPA.getProjectController()
                        .getLastCreatedCellId()) {
                    OpenSHAPA.getProjectController().setLastCreatedCellId(0);
                }

                // Check if the cell we are deleting is the last selected cell.
                // Default this back to 0 if it is.
                if (c.getID()
                        == OpenSHAPA.getProjectController()
                        .getLastSelectedCellId()) {
                    OpenSHAPA.getProjectController().setLastSelectedCellId(0);
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
