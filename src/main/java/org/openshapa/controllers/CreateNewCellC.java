package org.openshapa.controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import org.openshapa.OpenSHAPA;
import org.openshapa.models.db.DataCell;
import org.openshapa.models.db.DataColumn;
import org.openshapa.models.db.Database;
import org.openshapa.models.db.MatrixVocabElement;
import org.openshapa.models.db.SystemErrorException;
import org.openshapa.models.db.TimeStamp;
import org.openshapa.util.ArrayDirection;
import org.openshapa.util.Constants;
import org.openshapa.views.discrete.SpreadsheetPanel;

import com.usermetrix.jclient.UserMetrix;

/**
 * Controller for creating new cell.
 */
public final class CreateNewCellC {

    /**
     * Default constructor.
     */
    public CreateNewCellC() {
        // The spreadsheet is the view for this controller.
        view =
                (SpreadsheetPanel) OpenSHAPA.getApplication().getMainView()
                        .getComponent();
        model = OpenSHAPA.getProjectController().getDB();

        try {
            createNewCell(-1);
        } catch (SystemErrorException se) {
            logger.error("Unable to create new default cell", se);
        }
    }

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
    public CreateNewCellC(final Vector<DataCell> sourceCells,
            final ArrayDirection direction) {
        view =
                (SpreadsheetPanel) OpenSHAPA.getApplication().getMainView()
                        .getComponent();
        model = OpenSHAPA.getProjectController().getDB();
        long cellID = 0;

        try {
            // Get the column that is the parent of the source cell.
            for (DataCell sourceCell : sourceCells) {
                long sourceColumn = sourceCell.getItsColID();
                Vector<Long> columnOrder = model.getColOrderVector();

                for (int i = 0; i < columnOrder.size(); i++) {
                    // Found the source column in the order column.
                    if (columnOrder.get(i) == sourceColumn) {
                        i = i + direction.getModifier();

                        // Only create the cell if a valid column exists.
                        if (i >= 0 && i < columnOrder.size()) {
                            DataColumn c =
                                    model.getDataColumn(columnOrder.get(i));
                            MatrixVocabElement mve =
                                    model.getMatrixVE(c.getItsMveID());
                            DataCell cell =
                                    new DataCell(c.getDB(), c.getID(), mve
                                            .getID());

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
            OpenSHAPA.getApplication().showErrorDialog();
        }

        view.deselectAll();
        view.relayoutCells();
        view.highlightCell(cellID);
    }

    /**
     * Constructor - creates new controller.
     * 
     * @param milliseconds
     *            The milliseconds to use for the onset for the new cell.
     */
    public CreateNewCellC(final long milliseconds) {
        // The spreadsheet is the view for this controller.
        view =
                (SpreadsheetPanel) OpenSHAPA.getApplication().getMainView()
                        .getComponent();
        model = OpenSHAPA.getProjectController().getDB();

        // BugzID:758 - Before creating a new cell and setting onset. We need
        // the last created cell and need to set the previous cells offset...
        // But only if it is not 0.
        try {
            final long lastCreatedCellId =
                    OpenSHAPA.getProjectController().getLastCreatedCellId();
            if (lastCreatedCellId != 0) {
                DataCell dc = (DataCell) model.getCell(lastCreatedCellId);

                // BugzID:1285 - Only update the last created cell if it is in
                // the same column as the newly created cell.
                ArrayList<Long> matchingColumns = new ArrayList<Long>();
                for (DataColumn col : view.getSelectedCols()) {
                    matchingColumns.add(col.getID());
                }

                if (matchingColumns.size() == 0) {
                    matchingColumns.add(OpenSHAPA.getProjectController()
                            .getLastCreatedColId());
                }

                for (Long colID : matchingColumns) {
                    if (colID == dc.getItsColID()) {
                        TimeStamp ts = dc.getOffset();
                        if (ts.getTime() == 0) {
                            ts.setTime(Math.max(0, (milliseconds - 1)));
                            dc.setOffset(ts);
                            model.replaceCell(dc);
                        }
                    }
                }
            }

            // Create the new cell.
            createNewCell(milliseconds);
        } catch (SystemErrorException se) {
            logger.error("Unable to set offset of previous cell", se);
            OpenSHAPA.getApplication().showErrorDialog();
        }
    }

    /**
     * Create a new cell with given onset. Currently just appends to the
     * selected column or the column that last had a cell added to it.
     * 
     * @param milliseconds
     *            The number of milliseconds since the origin of the spreadsheet
     *            to create a new cell from.
     * @throws SystemErrorException
     *             If unable to create the desired new cell and append it to the
     *             database.
     */
    public void createNewCell(final long milliseconds)
            throws SystemErrorException {
        /*
         * Concept of operation: Creating a new cell. Situation 1: Spreadsheet
         * has one or more selected columns For each selected column do Create a
         * new cell with the supplied onset and insert into db. Situation 2:
         * Spreadsheet has one or more selected cells For each selected cell do
         * Create a new cell with the selected cell onset and offset and insert
         * into the db. Situation 3: User has set focus on a particular cell in
         * the spreadsheet - the caret is or has been in one of the editable
         * parts of a spreadsheet cell. First check this request has not come
         * from the video controller. For the focussed cell do Create a new cell
         * with the focussed cell onset and offset and insert into the db.
         * Situation 4: Request has come from the video controller and there is
         * no currently selected column. Create a new cell in the same column as
         * the last created cell or the last focussed cell.
         */
        long onset = milliseconds;
        // if not coming from video controller (milliseconds < 0) allow
        // multiple adds
        boolean multiadd = (milliseconds < 0);
        if (milliseconds < 0) {
            onset = 0;
        }

        long cellID = 0;

        boolean newcelladded = false;
        // check for Situation 1: one or more selected columns
        for (DataColumn col : view.getSelectedCols()) {
            MatrixVocabElement mve = model.getMatrixVE(col.getItsMveID());
            DataCell cell = new DataCell(col.getDB(), col.getID(), mve.getID());
            cell.setOnset(new TimeStamp(Constants.TICKS_PER_SECOND, onset));

            if (onset > 0) {
                cellID = model.appendCell(cell);
                OpenSHAPA.getProjectController().setLastCreatedCellId(cellID);
            } else {
                cellID = model.insertdCell(cell, 1);
                OpenSHAPA.getProjectController().setLastCreatedCellId(cellID);

            }
            OpenSHAPA.getProjectController().setLastCreatedColId(col.getID());
            newcelladded = true;
            if (!multiadd) {
                break;
            }
        }

        if (!newcelladded) {
            // else check for Situation 2: one or more selected cells
            Iterator<DataCell> itCells = view.getSelectedCells().iterator();

            while (itCells.hasNext()) {
                // reget the selected cell from the database using its id
                // in case a previous insert has changed its ordinal.
                // recasting to DataCell without checking as the iterator
                // only returns DataCells (no ref cells allowed so far)
                DataCell dc = (DataCell) model.getCell(itCells.next().getID());
                DataCell cell =
                        new DataCell(model, dc.getItsColID(), dc.getItsMveID());
                if (multiadd) {
                    cell.setOnset(dc.getOnset());
                    cell.setOffset(dc.getOffset());
                    cellID = model.insertdCell(cell, dc.getOrd() + 1);
                    OpenSHAPA.getProjectController().setLastCreatedCellId(
                            cellID);
                } else {
                    cell.setOnset(new TimeStamp(Constants.TICKS_PER_SECOND,
                            onset));
                    cellID = model.appendCell(cell);
                    OpenSHAPA.getProjectController().setLastCreatedCellId(
                            cellID);
                }
                OpenSHAPA.getProjectController().setLastCreatedColId(
                        cell.getItsColID());
                newcelladded = true;
                if (!multiadd) {
                    break;
                }
            }
        }

        if (!newcelladded && multiadd) {
            // else check for Situation 3: User is or was editing an
            // existing cell and has requested a new cell
            if (OpenSHAPA.getProjectController().getLastSelectedCellId() != 0) {
                DataCell dc =
                        (DataCell) model
                                .getCell(OpenSHAPA.getProjectController()
                                        .getLastSelectedCellId());
                DataCell cell =
                        new DataCell(model, dc.getItsColID(), dc.getItsMveID());
                cell.setOnset(dc.getOnset());
                cell.setOffset(dc.getOffset());
                cellID = model.insertdCell(cell, dc.getOrd() + 1);
                OpenSHAPA.getProjectController().setLastCreatedCellId(cellID);
                OpenSHAPA.getProjectController().setLastCreatedColId(
                        cell.getItsColID());
                newcelladded = true;
            }
        }

        if (!newcelladded) {
            // else go with Situation 4: Video controller requested
            // - create in the same column as the last created cell or
            // the last focused cell.

            // BugzID:779 - Check for presence of columns, else return
            if (model.getDataColumns().size() == 0) {
                return;
            }

            if (OpenSHAPA.getProjectController().getLastCreatedColId() == 0) {
                OpenSHAPA.getProjectController().setLastCreatedColId(
                        model.getDataColumns().get(0).getID());
            }

            // would throw by now if no columns exist
            DataColumn col =
                    model.getDataColumn(OpenSHAPA.getProjectController()
                            .getLastCreatedColId());

            DataCell cell =
                    new DataCell(col.getDB(), col.getID(), col.getItsMveID());
            cell.setOnset(new TimeStamp(Constants.TICKS_PER_SECOND, onset));
            cellID = model.appendCell(cell);
            OpenSHAPA.getProjectController().setLastCreatedCellId(cellID);
        }
        view.deselectAll();
        view.relayoutCells();
        view.highlightCell(cellID);
    }

    /** The logger for this class. */
    private UserMetrix logger = UserMetrix.getInstance(CreateNewCellC.class);

    /** The view (the spreadsheet) for this controller. */
    private SpreadsheetPanel view;

    /** The model (the database) for this controller. */
    private Database model;
}
