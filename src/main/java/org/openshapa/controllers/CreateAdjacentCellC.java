package org.openshapa.controllers;

import com.usermetrix.jclient.UserMetrix;

import java.util.Vector;

import org.jdesktop.swingworker.SwingWorker;

import org.openshapa.OpenSHAPA;

import org.openshapa.models.db.DataCell;
import org.openshapa.models.db.DataColumn;
import org.openshapa.models.db.MacshapaDatabase;
import org.openshapa.models.db.MatrixVocabElement;
import org.openshapa.models.db.SystemErrorException;

import org.openshapa.util.ArrayDirection;


/**
 * Controller for creating a cell adjacent to the current selection.
 */
public class CreateAdjacentCellC extends SwingWorker<Object, String> {

    /** The cells to create new cells adjacent too. */
    private Vector<DataCell> cells;

    /** The direction in which to create adjacent cells. */
    private ArrayDirection dir;

    /** The model that we are manipulating. */
    private MacshapaDatabase model;

    /** The logger for this class. */
    private UserMetrix logger = UserMetrix.getInstance(
            CreateAdjacentCellC.class);

    /**
     * Create New Cell Controller - creates new cells in columns adjacent to the
     * supplied cells. If no column is adjacent in the specified direction, no
     * cell will be created.
     *
     * @param sourceCells
     *            The list of source cells that we wish to create cells adjacent
     *            too.
     * @param direction
     *            The direction in which we wish to create adjacent cells.
     */
    public CreateAdjacentCellC(final Vector<DataCell> sourceCells,
        final ArrayDirection direction) {
        model = OpenSHAPA.getProjectController().getDB();
        cells = sourceCells;
        dir = direction;
    }

    /**
     * The task to perform in the background behind the EDT.
     *
     * @return always null.
     */
    @Override protected Object doInBackground() {

        try {
            logger.usage("Creating Adjacent Cell: " + dir);
            long cellID = 0;

            // Get the column that is the parent of the source cell.
            for (DataCell sourceCell : cells) {
                long sourceColumn = sourceCell.getItsColID();
                Vector<Long> columnOrder = model.getColOrderVector();

                for (int i = 0; i < columnOrder.size(); i++) {

                    // Found the source column in the order column.
                    if (columnOrder.get(i) == sourceColumn) {
                        i = i + dir.getModifier();

                        // Only create the cell if a valid column exists.
                        if ((i >= 0) && (i < columnOrder.size())) {
                            DataColumn c = model.getDataColumn(columnOrder.get(
                                        i));
                            MatrixVocabElement mve = model.getMatrixVE(
                                    c.getItsMveID());
                            DataCell cell = new DataCell(c.getDB(), c.getID(),
                                    mve.getID());

                            cell.setOnset(sourceCell.getOnset());
                            cell.setOffset(sourceCell.getOffset());
                            cellID = model.appendCell(cell);
                            OpenSHAPA.getProjectController()
                                .setLastCreatedCellId(cellID);
                        }

                        break;
                    }
                }
            }

        } catch (SystemErrorException se) {
            logger.error("Unable to create cell in adjacent column", se);

            // Needs to be pushed back into the EDT.
            OpenSHAPA.getApplication().showErrorDialog();
        }

        return null;
    }
}
