package org.openshapa.controllers;

import com.usermetrix.jclient.Logger;
import java.util.Vector;

import org.openshapa.OpenSHAPA;
import org.openshapa.models.db.legacy.Cell;
import org.openshapa.models.db.legacy.DataColumn;
import org.openshapa.models.db.legacy.MacshapaDatabase;
import org.openshapa.models.db.legacy.SystemErrorException;
import org.openshapa.views.discrete.SpreadsheetPanel;

import com.usermetrix.jclient.UserMetrix;

/**
 * Controller for deleting cells from the database.
 */
public final class DeleteColumnC {

    /** The logger for this class. */
    private Logger logger = UserMetrix.getLogger(DeleteColumnC.class);

    /**
     * Constructor.
     * 
     * @param colsToDelete
     *            The columns to remove from the database/spreadsheet.
     */
    public DeleteColumnC(final Vector<DataColumn> colsToDelete) {
        logger.usage("delete columns");

        // The spreadsheet is the view for this controller.
        SpreadsheetPanel view =
                (SpreadsheetPanel) OpenSHAPA.getApplication().getMainView()
                        .getComponent();
        MacshapaDatabase model = OpenSHAPA.getProjectController().getLegacyDB().getDatabase();

        try {
            // Deselect everything.
            view.deselectAll();

            for (DataColumn dc : colsToDelete) {

                // All cells in the column removed - now delete the column.
                // Must remove cells from the data column before removing it.
                while (dc.getNumCells() > 0) {
                    Cell c = model.getCell(dc.getID(), 1);
                    // Check if the cell we are deleting is the last created
                    // cell... Default this back to 0.
                    if (c.getID() == OpenSHAPA.getProjectController().getLastCreatedCellId()) {
                        OpenSHAPA.getProjectController().setLastCreatedCellId(0);
                    }
                    model.removeCell(c.getID());
                    dc = model.getDataColumn(dc.getID());
                }
                // Check if the column we are deleting was the last created
                // column... Default this back to 0 if it is.
                if (dc.getID() == OpenSHAPA.getProjectController().getLastCreatedColId()) {
                    OpenSHAPA.getProjectController().setLastCreatedColId(0);
                }
                model.removeColumn(dc.getID());
                view.revalidate();
                view.repaint();
            }
        } catch (SystemErrorException e) {
            logger.error("Unable to delete columns.", e);
        }
    }
}
