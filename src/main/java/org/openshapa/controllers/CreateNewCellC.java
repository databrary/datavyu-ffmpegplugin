package org.openshapa.controllers;

import com.usermetrix.jclient.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import org.openshapa.OpenSHAPA;

import database.DataCell;
import database.DataColumn;
import database.MacshapaDatabase;
import database.MatrixVocabElement;
import database.SystemErrorException;
import database.TimeStamp;

import org.openshapa.util.ArrayDirection;
import org.openshapa.util.Constants;

import org.openshapa.views.discrete.SpreadsheetPanel;

import com.usermetrix.jclient.UserMetrix;
import java.util.List;
import javax.swing.undo.UndoableEdit;
import org.openshapa.models.db.Cell;
import org.openshapa.models.db.Datastore;
import org.openshapa.models.db.DeprecatedDatabase;
import org.openshapa.models.db.DeprecatedVariable;
import org.openshapa.models.db.Variable;
import org.openshapa.undoableedits.AddCellEdit;


/**
 * Controller for creating new cell.
 */
public final class CreateNewCellC {

    /** The logger for this class. */
    private static Logger LOGGER = UserMetrix.getLogger(CreateNewCellC.class);

    /** The view (the spreadsheet) for this controller. */
    private SpreadsheetPanel view;

    /** The model (the database) for this controller. */
    private Datastore model;

    /**
     * Default constructor.
     */
    public CreateNewCellC() {
        // The spreadsheet is the view for this controller.
        view = (SpreadsheetPanel) OpenSHAPA.getApplication().getMainView().getComponent();
        model = OpenSHAPA.getProjectController().getDB();
    }

    /**
     * @return The legacy database that controller is interacting with.
     *
     * @deprecated Should use methods defined in datastore interface rather than
     * the db.legacy package.
     */
    @Deprecated
    private MacshapaDatabase modelAsLegacyDB() {
        return ((DeprecatedDatabase) model).getDatabase();
    }

    /**
     * @param newVariable The variable we want to fetch the legacy type for.
     *
     * @return A legacy variable/column for the supplied variable.
     *
     * @deprecated Should use method defined in the new Variable interface
     * rather than the db.legacy package.
     */
    @Deprecated
    private DataColumn asLegacy(Variable newVariable) {
        return ((DeprecatedVariable) newVariable).getLegacyVariable();
    }

    /**
     * Inserts a cell into the end of the supplied variable.
     *
     * @param v The variable that we want to add a cell too.
     *
     * @return The ID of the cell that was inserted.
     */
    public long createCell(final Variable v) {
        long cellID = 0;
        
        try {
            LOGGER.event("create cell in selected column");
            
            

            
            // perform the operation
            List<Cell> cells = v.getCellsTemporally();

            long newOnset = 0;
            if (!cells.isEmpty()) {
                Cell lastCell = cells.get(cells.size() - 1);
                newOnset = Math.max(lastCell.getOnset(), lastCell.getOffset());
            }

            MatrixVocabElement mve = modelAsLegacyDB().getMatrixVE(asLegacy(v).getItsMveID());
            DataCell newCell = new DataCell(asLegacy(v).getDB(), asLegacy(v).getID(), mve.getID());
            newCell.setOnset(new TimeStamp(Constants.TICKS_PER_SECOND, newOnset));

            if (newOnset > 0) {
                cellID = modelAsLegacyDB().appendCell(newCell);
                OpenSHAPA.getProjectController().setLastCreatedCellId(cellID);
            } else {
                cellID = modelAsLegacyDB().insertdCell(newCell, 1);
                OpenSHAPA.getProjectController().setLastCreatedCellId(cellID);
            }

            OpenSHAPA.getProjectController().setLastCreatedColId(asLegacy(v).getID());
            
     

            
        } catch (SystemErrorException se) {
            LOGGER.error("Unable to create new default cell", se);
        }

        return cellID;
    }

    /**
     * Create a default cell at the end of the nominated variable.
     *
     * @param v The variable we are adding a cell to the end of.
     */
    public void createDefaultCell(final Variable v) {       
        long cellID = createCell(v);
        
        view.deselectAll();
        view.highlightCell(cellID);
        // record the effect
        UndoableEdit edit = new AddCellEdit(v.getName());            
//        UndoableEdit edit = new AddCellEdit1(v, cellID);            
        // Display any changes.
        OpenSHAPA.getApplication().getMainView().getComponent().revalidate();
        // notify the listeners
        OpenSHAPA.getView().getUndoSupport().postEdit(edit);
    
    }

    /**
     * Create a default cell - at the end of the selected variables.
     */
    public void createDefaultCell() {
        long cellID = 0;
        for (Variable v : model.getAllVariables()) {
            if (v.isSelected()) {
                cellID = createCell(v); 
                // record the effect
                UndoableEdit edit = new AddCellEdit(v.getName());            
//                UndoableEdit edit = new AddCellEdit1(v, cellID);            
                // Display any changes.
                OpenSHAPA.getApplication().getMainView().getComponent().revalidate();
                // notify the listeners
                OpenSHAPA.getView().getUndoSupport().postEdit(edit); 
            }
        }

        view.deselectAll();
        view.highlightCell(cellID);

    }

    /**
     * Create New Cell Controller - creates new cells in columns adjacent to the
     * supplied cells. If no column is adjacent in the specified direction, no
     * cell will be created.
     *
     * @param sourceCells The list of source cells that we wish to create cells
     * adjacent too.
     * @param direction The direction in which we wish to create adjacent cells.
     */
    public CreateNewCellC(final Vector<DataCell> sourceCells,
                          final ArrayDirection direction) {
        view = (SpreadsheetPanel) OpenSHAPA.getApplication().getMainView().getComponent();
        model = OpenSHAPA.getProjectController().getDB();

        long cellID = 0;

        try {
            LOGGER.event("create adjacent cells:" + direction);

            // Get the column that is the parent of the source cell.
            for (DataCell sourceCell : sourceCells) {
                long sourceColumn = sourceCell.getItsColID();
                Vector<Long> columnOrder = modelAsLegacyDB().getColOrderVector();

                for (int i = 0; i < columnOrder.size(); i++) {

                    // Found the source column in the order column.
                    if (columnOrder.get(i) == sourceColumn) {
                        i = i + direction.getModifier();

                        // Only create the cell if a valid column exists.
                        if ((i >= 0) && (i < columnOrder.size())) {
                            DataColumn c = modelAsLegacyDB().getDataColumn(columnOrder.get(i));
                            MatrixVocabElement mve = modelAsLegacyDB().getMatrixVE(c.getItsMveID());
                            DataCell cell = new DataCell(c.getDB(), c.getID(), mve.getID());

                            cell.setOnset(sourceCell.getOnset());
                            cell.setOffset(sourceCell.getOffset());
                            cellID = modelAsLegacyDB().appendCell(cell);
                            OpenSHAPA.getProjectController().setLastCreatedCellId(cellID);
                            
                            // record the effect
                            String columnCellName;
                            columnCellName = ((MacshapaDatabase)cell.getDB()).getDataColumn(cell.getItsColID()).getName();
                            for (Variable v : model.getAllVariables()) {
                                String variableName = v.getName();
                                if (variableName.equals(columnCellName)) {
                                    // Add the undoable action
                                    UndoableEdit edit = new AddCellEdit(v.getName());            
//                                   UndoableEdit edit = new AddCellEdit1(v, cellID);
                                   // notify the listeners
                                   OpenSHAPA.getView().getUndoSupport().postEdit(edit);
                                   break;
                                }
                            }    
                            /////
                        }

                        break;
                    }
                }
            }

        } catch (SystemErrorException se) {
            LOGGER.error("Unable to create cell in adjacent column", se);
            OpenSHAPA.getApplication().showErrorDialog();
        }

        view.deselectAll();
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
        view = (SpreadsheetPanel) OpenSHAPA.getApplication().getMainView()
            .getComponent();
        model = OpenSHAPA.getProjectController().getDB();

        // BugzID:758 - Before creating a new cell and setting onset. We need
        // the last created cell and need to set the previous cells offset...
        // But only if it is not 0.
        try {
            final long lastCreatedCellId = OpenSHAPA.getProjectController()
                .getLastCreatedCellId();

            if (lastCreatedCellId != 0) {
                DataCell dc = (DataCell) modelAsLegacyDB().getCell(lastCreatedCellId);

                // BugzID:1285 - Only update the last created cell if it is in
                // the same column as the newly created cell.
                ArrayList<Long> matchingColumns = new ArrayList<Long>();

                for (DataColumn col : view.getSelectedCols()) {
                    matchingColumns.add(col.getID());
                }

                if (matchingColumns.isEmpty()) {
                    matchingColumns.add(OpenSHAPA.getProjectController().getLastCreatedColId());
                }

                for (Long colID : matchingColumns) {

                    if (colID == dc.getItsColID()) {
                        TimeStamp ts = dc.getOffset();

                        if (ts.getTime() == 0) {
                            ts.setTime(Math.max(0, (milliseconds - 1)));
                            dc.setOffset(ts);
                            modelAsLegacyDB().replaceCell(dc);
                        }
                    }
                }
            }

            // Create the new cell.
            createNewCell(milliseconds);
        } catch (SystemErrorException se) {
            LOGGER.error("Unable to set offset of previous cell", se);
            OpenSHAPA.getApplication().showErrorDialog();
        }
    }

    /**
     * Create a new cell with given onset. Currently just appends to the
     * selected column or the column that last had a cell added to it.
     *
     * @param milliseconds The number of milliseconds since the origin of the
     * spreadsheet to create a new cell from.
     *
     * @throws SystemErrorException If unable to create the desired new cell and
     * append it to the database.
     */
    public void createNewCell(final long milliseconds)
        throws SystemErrorException {

        /*
         * Concept of operation: Creating a new cell.
         *
         * Situation 1: Spreadsheet has one or more selected columns For each
         * selected column do Create a new cell with the supplied onset and
         * insert into db.
         *
         * Situation 2: Spreadsheet has one or more selected cells For each
         * selected cell do Create a new cell with the selected cell onset and
         * offset and insert into the db.
         *
         * Situation 3: User has set focus on a particular cell in the
         * spreadsheet - the caret is or has been in one of the editable parts
         * of a spreadsheet cell. First check this request has not come from the
         * video controller. For the focussed cell do Create a new cell with the
         * focussed cell onset and offset and insert into the db.
         *
         * Situation 4: Request has come from the video controller and there is
         * no currently selected column. Create a new cell in the same column as
         * the last created cell or the last focussed cell.
         */
        long onset = milliseconds;

        // If not coming from video controller (milliseconds < 0) allow
        // multiple adds
        boolean multiadd = (milliseconds < 0);

        if (milliseconds < 0) {
            onset = 0;
        }

        long cellID = 0;

        boolean newcelladded = false;

        // check for Situation 1: one or more selected columns
        for (DataColumn col : view.getSelectedCols()) {
            LOGGER.event("create cell in selected column");

            MatrixVocabElement mve = modelAsLegacyDB().getMatrixVE(col.getItsMveID());
            DataCell cell = new DataCell(col.getDB(), col.getID(), mve.getID());
            cell.setOnset(new TimeStamp(Constants.TICKS_PER_SECOND, onset));

            if (onset > 0) {
                cellID = modelAsLegacyDB().appendCell(cell);
                OpenSHAPA.getProjectController().setLastCreatedCellId(cellID);
            } else {
                cellID = modelAsLegacyDB().insertdCell(cell, 1);
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
                LOGGER.event("create cell below selected cell");

                // reget the selected cell from the database using its id
                // in case a previous insert has changed its ordinal.
                // recasting to DataCell without checking as the iterator
                // only returns DataCells (no ref cells allowed so far)
                DataCell dc = (DataCell) modelAsLegacyDB().getCell(itCells.next().getID());
                DataCell cell = new DataCell(modelAsLegacyDB(), dc.getItsColID(),
                        dc.getItsMveID());

                if (multiadd) {

                    // BugzID:1837 - We want a zero onset here.
                    cell.setOnset(new TimeStamp(Constants.TICKS_PER_SECOND, onset));
                    cellID = modelAsLegacyDB().insertdCell(cell, dc.getOrd() + 1);
                    OpenSHAPA.getProjectController().setLastCreatedCellId(cellID);
                } else {
                    cell.setOnset(new TimeStamp(Constants.TICKS_PER_SECOND, onset));
                    cellID = modelAsLegacyDB().appendCell(cell);
                    OpenSHAPA.getProjectController().setLastCreatedCellId(cellID);
                }

                OpenSHAPA.getProjectController().setLastCreatedColId(cell.getItsColID());
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
                LOGGER.event("create cell while editing existing cell");

                DataCell dc = (DataCell) modelAsLegacyDB().getCell(OpenSHAPA.getProjectController().getLastSelectedCellId());
                DataCell cell = new DataCell(modelAsLegacyDB(), dc.getItsColID(), dc.getItsMveID());

                // BugzID:1837 - We want a zero onset here.
                cell.setOnset(new TimeStamp(Constants.TICKS_PER_SECOND, onset));
                cellID = modelAsLegacyDB().insertdCell(cell, dc.getOrd() + 1);
                OpenSHAPA.getProjectController().setLastCreatedCellId(cellID);
                OpenSHAPA.getProjectController().setLastCreatedColId(cell.getItsColID());
                newcelladded = true;
            }
        }

        if (!newcelladded) {
            LOGGER.event("create cell in same location as last created cell");
            // else go with Situation 4: Video controller requested
            // - create in the same column as the last created cell or
            // the last focused cell.

            // BugzID:779 - Check for presence of columns, else return
            if (modelAsLegacyDB().getDataColumns().isEmpty()) {
                return;
            }

            if (OpenSHAPA.getProjectController().getLastCreatedColId() == 0) {
                OpenSHAPA.getProjectController().setLastCreatedColId(modelAsLegacyDB().getDataColumns().get(0).getID());
            }

            // would throw by now if no columns exist
            DataColumn col = modelAsLegacyDB().getDataColumn(OpenSHAPA.getProjectController().getLastCreatedColId());

            DataCell cell = new DataCell(col.getDB(), col.getID(),
                    col.getItsMveID());
            cell.setOnset(new TimeStamp(Constants.TICKS_PER_SECOND, onset));
            cellID = modelAsLegacyDB().appendCell(cell);
            OpenSHAPA.getProjectController().setLastCreatedCellId(cellID);
        }

        view.deselectAll();
        view.highlightCell(cellID);
    }
}
