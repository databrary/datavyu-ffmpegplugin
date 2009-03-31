package au.com.nicta.openshapa.views.discrete;

import au.com.nicta.openshapa.db.DataCell;
import au.com.nicta.openshapa.db.DataColumn;
import au.com.nicta.openshapa.db.Database;
import au.com.nicta.openshapa.db.ExternalCascadeListener;
import au.com.nicta.openshapa.db.ExternalDataColumnListener;
import au.com.nicta.openshapa.db.SystemErrorException;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.JComponent;
import org.apache.log4j.Logger;

/**
 * This class maintains the visual representation of the column in the
 * Spreadsheet window.
 *
 * @author swhitcher
 */
public final class SpreadsheetColumn
implements ExternalDataColumnListener, ExternalCascadeListener {

    /** Database reference. */
    private Database database;

    /** Database reference colID of the DataColumn this column displays. */
    private long dbColID;

    /** ColumnDataPanel this column manages. */
    private ColumnDataPanel datapanel;

    /** ColumnHeaderPanel this column manages. */
    private ColumnHeaderPanel headerpanel;

    /** Spreadhseet panel this column belongs to. */
    private SpreadsheetPanel spreadsheetPanel;

    /** Collection of the SpreadsheetCells in the column. */
    private Vector<SpreadsheetCell> cells;

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(SpreadsheetColumn.class);

    /** Records changes to column during a cascade. */
    private ColumnChanges colChanges;

    /** Default column width. */
    private static final int DEFAULT_COLUMN_WIDTH = 210;

    /** Default column height. */
    private static final int DEFAULT_HEADER_HEIGHT = 16;

    /** filler box for use when there are no datacells. */
    private Component filler;

    /**
     * Private class for recording the changes reported by the listener
     * callbacks on this column.
     */
    private final class ColumnChanges {
        /** nameChanged. */
        private boolean nameChanged;
        /** varLenChanged. */
        private boolean varLenChanged;
        /** List of cell IDs of newly inserted cells. */
        private Vector<Long> cellInserted;
        /** List of cell IDs of deleted cells. */
        private Vector<Long> cellDeleted;
        /** colDeleted. */
        private boolean colDeleted;

        /**
         * ColumnChanges constructor.
         */
        private ColumnChanges() {
            cellInserted = new Vector<Long>();
            cellDeleted = new Vector<Long>();
            reset();
        }

        /**
         * Reset the ColumnChanges flags and lists.
         */
        private void reset() {
            nameChanged = false;
            varLenChanged = false;
            cellInserted.clear();
            cellDeleted.clear();
            colDeleted = false;
        }
    }

    /**
     * Creates new SpreadsheetColumn.
     * @param sheet Spreadsheet parent.
     * @param db Database reference.
     * @param colID the database colID this column displays.
     * @param selector The selection for all columns.
     */
    public SpreadsheetColumn(final SpreadsheetPanel sheet,
                             final Database db,
                             final long colID,
                             final Selector selector) {
        this.database = db;
        this.dbColID = colID;
        this.spreadsheetPanel = sheet;

        this.cells = new Vector<SpreadsheetCell>();

        try {
            database.registerDataColumnListener(dbColID, this);
            database.registerCascadeListener(this);

            DataColumn dbColumn = database.getDataColumn(dbColID);

            headerpanel = new ColumnHeaderPanel(this, dbColumn.getName()
                            + "  (" + dbColumn.getItsMveType() + ")", selector);

            datapanel = new ColumnDataPanel();
            datapanel.setPreferredSize(new Dimension(getWidth(),
                                                            Integer.MAX_VALUE));

            buildDataPanelCells(dbColumn);

        } catch (SystemErrorException e) {
            logger.error("Problem retrieving DataColumn", e);
        }
        colChanges = new ColumnChanges();
    }

    /**
     * Build the SpreadsheetCells and add to the DataPanel.
     * @param dbColumn DataColumn to display.
     */
    private void buildDataPanelCells(final DataColumn dbColumn) {
        try {
            int numCells = dbColumn.getNumCells();

            // hold onto a filler box for when there are no datacells
            filler = Box.createRigidArea(new Dimension(getWidth(), 0));
            if (numCells == 0) {
               datapanel.add(filler);
            }
            // traverse and build the cells
            for (int j = 1; j <= numCells; j++) {
                DataCell dc = (DataCell) dbColumn.getDB()
                                    .getCell(dbColumn.getID(), j);

                SpreadsheetCell sc = new SpreadsheetCell(dbColumn.getDB(), dc,
                                            spreadsheetPanel.getCellSelector());
                // add cell to the JPanel
                datapanel.add(sc);
                // and add it to our reference list
                cells.add(sc);
            }
        } catch (SystemErrorException e) {
           logger.error("Failed to populate Spreadsheet.", e);
        }

    }

    /**
     * @return Column Header size as a dimension.
     */
    public Dimension getHeaderSize() {
        return new Dimension(getWidth(), DEFAULT_HEADER_HEIGHT);
    }

    /**
     * @return Column Width in pixels.
     */
    public int getWidth() {
        return DEFAULT_COLUMN_WIDTH;
    }

    /**
     * @return The headerpanel.
     */
    public JComponent getHeaderPanel() {
        return headerpanel;
    }

    /**
     * @return The datapanel.
     */
    public JComponent getDataPanel() {
        return datapanel;
    }

    /**
     * @return The column ID of the datacolumn being displayed.
     */
    public long getColID() {
        return dbColID;
    }

    /**
     * Set the selected state for the DataColumn this displays.
     * @param selected Selected state.
     */
    public void setSelected(final boolean selected) {
        try {
            DataColumn dc = database.getDataColumn(dbColID);

            dc.setSelected(selected);
            database.replaceColumn(dc);

        } catch (SystemErrorException e) {
           logger.error("Failed setting column select state.", e);
        }
    }

    /**
     * Called at the beginning of a cascade of changes through the database.
     * @param db The database.
     */
    public void beginCascade(final Database db) {
        colChanges.reset();
    }

    /**
     * Called at the end of a cascade of changes through the database.
     * @param db The database.
     */
    public void endCascade(final Database db) {
        boolean dirty = false;
        if (colChanges.colDeleted) {
            // Not tested yet should be handled by ColumnListener in spreadsheet
            return;
        }
        if (colChanges.cellDeleted.size() > 0) {
            for (Long cellID : colChanges.cellDeleted) {
                deleteCellByID(cellID);
            }
            dirty = true;
        }
        if (colChanges.cellInserted.size() > 0) {
            for (Long cellID : colChanges.cellInserted) {
                insertCellByID(cellID);
            }
            dirty = true;
        }
        if (colChanges.varLenChanged) {
            // all cells need to be redrawn but none are being added or deleted
            dirty = true;
        }

        if (colChanges.nameChanged) {
            try {
                DataColumn dbColumn = db.getDataColumn(dbColID);
                headerpanel.setText(dbColumn.getName()
                            + "  (" + dbColumn.getItsMveType() + ")");
            } catch (SystemErrorException e) {
                logger.warn("Problem getting data column", e);
            }
        }

        if (dirty) {
            spreadsheetPanel.relayoutCells();
        }
        colChanges.reset();
    }

    /**
     * Find and delete SpreadsheetCell by its ID.
     * @param cellID ID of cell to find and delete.
     */
    private void deleteCellByID(final long cellID) {
        for (SpreadsheetCell cell : cells) {
            if (cell.getCellID() == cellID) {
                cells.remove(cell);
                datapanel.remove(cell);
                if (cells.size() == 0) {
                    // add in filler to keep column width
                    datapanel.add(filler);
                }
                break;
            }
        }
    }

    /**
     * Insert a new SpreadsheetCell given the cells ID.
     * @param cellID ID of cell to create and insert.
     */
    private void insertCellByID(final long cellID) {
        try {
            DataCell dc = (DataCell) database.getCell(cellID);
            SpreadsheetCell newCell = new SpreadsheetCell(database, dc,
                                            spreadsheetPanel.getCellSelector());
            Long newOrd = new Long(dc.getOrd());
            if (cells.size() > newOrd.intValue()) {
                cells.insertElementAt(newCell, newOrd.intValue() - 1);
            } else {
                cells.add(newCell);
            }
            newCell.setWidth(getWidth() - 1);
            datapanel.add(newCell);
            datapanel.remove(filler);
            //newCell.requestFocusInWindow();
            //UISPEC4J: The line above was commented out for testing purposes. Will discuss. - michael
        } catch (SystemErrorException e) {
            logger.error("Problem inserting a new SpreadsheetCell", e);
        }
    }

    /**
     * Called when a DataCell is deleted from the DataColumn.
     * @param db The database the column belongs to.
     * @param colID The ID assigned to the DataColumn.
     * @param cellID ID of the DataCell that is being deleted.
     */
    public void DColCellDeletion(final Database db,
                                       final long colID,
                                       final long cellID) {
        colChanges.cellDeleted.add(cellID);
    }


    /**
     * Called when a DataCell is inserted in the vocab list.
     * @param db The database the column belongs to.
     * @param colID The ID assigned to the DataColumn.
     * @param cellID ID of the DataCell that is being inserted.
     */
    public void DColCellInsertion(final Database db,
                                        final long colID,
                                        final long cellID) {
        colChanges.cellInserted.add(cellID);
    }

    /**
     * Called when one fields of the target DataColumn are changed.
     * @param db The database.
     * @param colID The ID assigned to the DataColumn.
     * @param nameChanged indicates whether the name changed.
     * @param oldName reference to oldName.
     * @param newName reference to newName.
     * @param hiddenChanged indicates the hidden field changed.
     * @param oldHidden Old Hidden value.
     * @param newHidden New Hidden value.
     * @param readOnlyChanged indicates the readOnly field changed.
     * @param oldReadOnly Old ReadOnly value.
     * @param newReadOnly New ReadOnly value.
     * @param varLenChanged indicates the varLen field changed.
     * @param oldVarLen Old varLen value.
     * @param newVarLen New varLen value.
     * @param selectedChanged indicates the selection status of the DataColumn
     * has changed.
     * @param oldSelected Old Selected value.
     * @param newSelected New Selected value.
     */
    public void DColConfigChanged(final Database db,
                                      final long colID,
                                      final boolean nameChanged,
                                      final String oldName,
                                      final String newName,
                                      final boolean hiddenChanged,
                                      final boolean oldHidden,
                                      final boolean newHidden,
                                      final boolean readOnlyChanged,
                                      final boolean oldReadOnly,
                                      final boolean newReadOnly,
                                      final boolean varLenChanged,
                                      final boolean oldVarLen,
                                      final boolean newVarLen,
                                      final boolean selectedChanged,
                                      final boolean oldSelected,
                                      final boolean newSelected) {
        colChanges.nameChanged = nameChanged;
        colChanges.varLenChanged = varLenChanged;
    }

    /**
     * Called when the DataColumn of interest is deleted.
     * @param db The database.
     * @param colID The ID assigned to the DataColumn.
     */
    public void DColDeleted(final Database db,
                                  final long colID) {
        colChanges.colDeleted = true;
    }

    /**
     * Set the preferred size of the column.
     * @param bottom Number of pixels to set.
     */
    public void setBottomBound(final int bottom) {
        datapanel.setPreferredSize(
                    new Dimension(datapanel.getPreferredSize().width, bottom));
    }

    /**
     * @return The SpreadsheetCells in this column.
     */
    public Vector<SpreadsheetCell> getCells() {
        return cells;
    }

    /**
     * Request focus for this column. It will request focus for the first
     * SpreadsheetCell in the column if one exists. If no cells exist it
     * will request focus for the datapanel of the column.
     */
    public void requestFocus() {
        if (cells.size() > 0) {
            cells.firstElement().requestFocusInWindow();
        } else {
            datapanel.requestFocusInWindow();
        }
    }
}
