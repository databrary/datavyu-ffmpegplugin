package au.com.nicta.openshapa.views.discrete;

import au.com.nicta.openshapa.db.DataColumn;
import au.com.nicta.openshapa.db.Database;
import au.com.nicta.openshapa.db.ExternalDataColumnListener;
import au.com.nicta.openshapa.db.SystemErrorException;
import javax.swing.JComponent;
import org.apache.log4j.Logger;

/**
 * Column panel that contains the SpreadsheetCell panels.
 * @author swhitcher
 */
public class SpreadsheetColumn
        implements ExternalDataColumnListener {

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

    /**
     * Creates new SpreadsheetColumn.
     * @param db Database reference.
     * @param colID the database colID this column displays.
     */
    public SpreadsheetColumn(final Database db, final long colID) {
        this.database = db;
        this.dbColID = colID;

        try {
            database.registerDataColumnListener(dbColID, this);

            DataColumn dbColumn = database.getDataColumn(dbColID);

            headerpanel = new ColumnHeaderPanel(dbColumn.getName()
                            + "  (" + dbColumn.getItsMveType() + ")");

            datapanel = new ColumnDataPanel(dbColumn);

        } catch (SystemErrorException e) {
            logger.error("Problem retrieving DataColumn", e);
        }
    }

    /**
     * @return The headerpanel.
     */
    public final JComponent getHeaderPanel() {
        return headerpanel;
    }

    /**
     * @return The datapanel.
     */
    public final JComponent getDataPanel() {
        return datapanel;
    }

    /**
     * @return The column ID of the datacolumn being displayed.
     */
    public final long getColID() {
        return dbColID;
    }

    /**
     * Brute force rebuild of all cells in the Spreadsheet column.
     * @param db The database the column belongs to.
     * @param colID The ID assigned to the DataColumn.
     */
    private void rebuildAll(final Database db, final long colID) {
        try {
            DataColumn dbColumn = db.getDataColumn(colID);

            datapanel.rebuildAll(dbColumn);
            headerpanel.setText(dbColumn.getName()
                            + "  (" + dbColumn.getItsMveType() + ")");

        } catch (SystemErrorException e) {
            logger.error("Failed to reget DataColumn from colID = " + colID, e);
        }
    }

    /** ExternalDataColumnListener overrides */

    /**
     * Called when a DataCell is deleted from the DataColumn.
     * @param db The database the column belongs to.
     * @param colID The ID assigned to the DataColumn.
     * @param cellID ID of the DataCell that is being deleted.
     */
    @Override
    public final void DColCellDeletion(final Database db,
                                       final long colID,
                                       final long cellID) {
        rebuildAll(db, colID);
    }


    /**
     * Called when a DataCell is inserted in the vocab list.
     * @param db The database the column belongs to.
     * @param colID The ID assigned to the DataColumn.
     * @param cellID ID of the DataCell that is being inserted.
     */
    @Override
    public final void DColCellInsertion(final Database db,
                                        final long colID,
                                        final long cellID) {
        rebuildAll(db, colID);
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
    @Override
    public final void DColConfigChanged(final Database db,
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
        rebuildAll(db, colID);
    }

    /**
     * Called when the DataColumn of interest is deleted.
     * @param db The database.
     * @param colID The ID assigned to the DataColumn.
     */
    @Override
    public final void DColDeleted(final Database db,
                                  final long colID) {
        logger.warn("Not sure what to do in DColDeleted");
    }

}
