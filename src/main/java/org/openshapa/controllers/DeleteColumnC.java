package org.openshapa.controllers;

import java.util.Vector;

import org.openshapa.OpenSHAPA;
import org.openshapa.models.db.Cell;
import org.openshapa.models.db.DataColumn;
import org.openshapa.models.db.MacshapaDatabase;
import org.openshapa.models.db.SystemErrorException;
import org.openshapa.views.discrete.SpreadsheetPanel;

import com.usermetrix.jclient.UserMetrix;

/**
 * Controller for deleting cells from the database.
 */
public final class DeleteColumnC {

    /** The logger for this class. */
    private UserMetrix logger = UserMetrix.getInstance(DeleteColumnC.class);

    /**
     * Constructor.
     * 
     * @param colsToDelete
     *            The columns to remove from the database/spreadsheet.
     */
    public DeleteColumnC(final Vector<DataColumn> colsToDelete) {
        logger.usage("deleting columns");

        // The spreadsheet is the view for this controller.
        SpreadsheetPanel view =
                (SpreadsheetPanel) OpenSHAPA.getApplication().getMainView()
                        .getComponent();
        MacshapaDatabase model = OpenSHAPA.getProjectController().getDB();

        try {
            // Deselect everything.
            view.deselectAll();

            for (DataColumn dc : colsToDelete) {
                // Must remove cells from the data column before removing it.
                while (dc.getNumCells() > 0) {
                    Cell c = model.getCell(dc.getID(), 1);

                    // Check if the cell we are deleting is the last created
                    // cell... Default this back to 0.
                    if (c.getID() == OpenSHAPA.getProjectController()
                            .getLastCreatedCellId()) {
                        OpenSHAPA.getProjectController()
                                .setLastCreatedCellId(0);
                    }

                    OpenSHAPA.getProjectController().getDB().removeCell(
                            c.getID());
                    dc =
                            OpenSHAPA.getProjectController().getDB()
                                    .getDataColumn(dc.getID());
                }

                // Check if the column we are deleting was the last created
                // column... Default this back to 0 if it is.
                if (dc.getID() == OpenSHAPA.getProjectController()
                        .getLastCreatedColId()) {
                    OpenSHAPA.getProjectController().setLastCreatedColId(0);
                }

                // All cells in the column removed - now delete the column.
                model.removeColumn(dc.getID());
                view.revalidate();
                view.repaint();
            }
        } catch (SystemErrorException e) {
            logger.error("Unable to delete columns.", e);
        }
    }
}
