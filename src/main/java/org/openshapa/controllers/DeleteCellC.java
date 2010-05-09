package org.openshapa.controllers;

import java.util.Vector;

import org.openshapa.OpenSHAPA;
import org.openshapa.models.db.DataCell;
import org.openshapa.models.db.MacshapaDatabase;
import org.openshapa.models.db.SystemErrorException;
import org.openshapa.views.discrete.SpreadsheetPanel;

import com.usermetrix.jclient.UserMetrix;
import org.jdesktop.swingworker.SwingWorker;

/**
 * Controller for deleting cells from the database.
 */
public final class DeleteCellC extends SwingWorker<Object, String> {

    /** The logger for this class. */
    private UserMetrix logger = UserMetrix.getInstance(DeleteColumnC.class);

    /** The list of cells to remove. */
    private Vector<DataCell> cellsToRemove;

    /**
     * Constructor.
     *
     * @param cellsToDelete The cells to delete from the spreadsheet.
     */
    public DeleteCellC(final Vector<DataCell> cellsToDelete) {
        cellsToRemove = cellsToDelete;

        // Delselect everything in the spreadsheet before we delete.
        SpreadsheetPanel view = (SpreadsheetPanel) OpenSHAPA.getApplication()
                                .getMainView().getComponent();
        view.deselectAll();
    }

    /**
     * The task to perform in the background - actually deletes the cells from
     * the database.
     *
     * @return Always null.
     */
    @Override protected Object doInBackground() {
        try {
            logger.usage("deleting cells");

            MacshapaDatabase model = OpenSHAPA.getProjectController().getDB();
            for (DataCell c : cellsToRemove) {
                // Check if the cell we are deleting is the last created cell..
                // Default this back to 0 if it is.
                if (c.getID() == OpenSHAPA.getProjectController()
                        .getLastCreatedCellId()) {
                    OpenSHAPA.getProjectController().setLastCreatedCellId(0);
                }

                model.removeCell(c.getID());
            }
        } catch (SystemErrorException e) {
            logger.error("Unable to delete cells", e);
        }

        return null;
    }
}
