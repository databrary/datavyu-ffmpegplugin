package org.openshapa.views.discrete;

import org.openshapa.db.DataColumn;
import org.openshapa.db.Database;
import org.openshapa.db.ExternalCascadeListener;
import org.openshapa.db.ExternalDataColumnListener;
import org.openshapa.db.SystemErrorException;
import java.awt.Dimension;
import java.util.Vector;
import javax.swing.JComponent;
import org.apache.log4j.Logger;
import org.openshapa.views.discrete.layouts.SheetLayoutFactory.SheetLayoutType;

/**
 * This class maintains the visual representation of the column in the
 * Spreadsheet window.
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

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(SpreadsheetColumn.class);

    /** Records changes to column during a cascade. */
    private ColumnChanges colChanges;

    /** Default column width. */
    private static final int DEFAULT_COLUMN_WIDTH = 230;

    /** Default column height. */
    public static final int DEFAULT_HEADER_HEIGHT = 16;

    /** Width of the column in pixels. */
    private int width = DEFAULT_COLUMN_WIDTH;

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
     * @param colSelector The selection for all columns.
     * @param cellSelector The selection of all cells.
     */
    public SpreadsheetColumn(final Database db,
                             final long colID,
                             final Selector colSelector,
                             final Selector cellSelector) {
        this.database = db;
        this.dbColID = colID;

        try {
            database.registerDataColumnListener(dbColID, this);
            database.registerCascadeListener(this);

            DataColumn dbColumn = database.getDataColumn(dbColID);

            headerpanel = new ColumnHeaderPanel(this,
                dbColumn.getName() + "  (" + dbColumn.getItsMveType() + ")",
                colSelector);

            datapanel = new ColumnDataPanel(width, dbColumn, cellSelector);

        } catch (SystemErrorException e) {
            logger.error("Problem retrieving DataColumn", e);
        }
        colChanges = new ColumnChanges();
    }

    /**
     * @return Column Header size as a dimension.
     */
    public Dimension getHeaderSize() {
        return new Dimension(getWidth(), DEFAULT_HEADER_HEIGHT);
    }

    /**
     * @param colWidth Column width to set in pixels.
     */
    public void setWidth(final int colWidth) {
        width = colWidth;
        Dimension dim = getHeaderSize();
        headerpanel.setMinimumSize(dim);
        headerpanel.setPreferredSize(dim);
        headerpanel.setMaximumSize(dim);
        Dimension dim2 = getHeaderSize();
        dim2.height = Integer.MAX_VALUE;

        datapanel.setWidth(width);
        for (SpreadsheetCell cell : getCells()) {
            cell.setWidth(width);
        }
        headerpanel.revalidate();
        datapanel.revalidate();
        // Whereever we resize we will need to spreadsheetPanel.relayoutCells();
    }

    /**
     * @return Column Width in pixels.
     */
    public int getWidth() {
        return width;
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
        if (colChanges.colDeleted) {
            // Not tested yet should be handled by ColumnListener in spreadsheet
            return;
        }

        if (colChanges.cellDeleted.size() > 0) {
            for (Long cellID : colChanges.cellDeleted) {
                datapanel.deleteCellByID(cellID);
            }
        }
        if (colChanges.cellInserted.size() > 0) {
            for (Long cellID : colChanges.cellInserted) {
                datapanel.insertCellByID(db, cellID);
            }
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

        colChanges.reset();
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
     * resetLayout changes the layout manager depending on the SheetLayoutType.
     * @param type SheetLayoutType
     */
    public void resetLayoutManager(final SheetLayoutType type) {
        datapanel.resetLayoutManager(type);
    }

    /**
     * Set the preferred size of the column.
     * @param bottom Number of pixels to set.
     */
    public void setBottomBound(final int bottom) {
        if (bottom < 0) {
            datapanel.setPreferredSize(null);
        } else {
            datapanel.setPreferredSize(new Dimension(this.getWidth(), bottom));
        }
    }

    /**
     * @return The SpreadsheetCells in this column.
     */
    public Vector<SpreadsheetCell> getCells() {
        return datapanel.getCells();
    }

    /**
     * Request focus for this column. It will request focus for the first
     * SpreadsheetCell in the column if one exists. If no cells exist it
     * will request focus for the datapanel of the column.
     */
    public void requestFocus() {
        if (datapanel.getCells().size() > 0) {
            datapanel.getCells().firstElement().requestFocusInWindow();
        } else {
            datapanel.requestFocusInWindow();
        }
    }
}
