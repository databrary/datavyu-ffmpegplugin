/*
 * SpreadsheetColumn.java
 *
 * Created on 26/11/2008, 2:21:02 PM
 */

package au.com.nicta.openshapa.views.discrete;

import au.com.nicta.openshapa.db.DataCell;
import au.com.nicta.openshapa.db.DataColumn;
import au.com.nicta.openshapa.db.Database;
import au.com.nicta.openshapa.db.ExternalDataColumnListener;
import au.com.nicta.openshapa.db.SystemErrorException;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import org.apache.log4j.Logger;

/**
 * Column panel that contains the SpreadsheetCell panels.
 * @author swhitcher
 */
public class SpreadsheetColumn extends javax.swing.JPanel
        implements ExternalDataColumnListener {

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(SpreadsheetColumn.class);

    /** DataColumn to display. */
    private DataColumn dbColumn;

    /** ID of the DataColumn to display. */
    private long dbColID;

    /** Creates new SpreadsheetColumn. No reference to a database column. */
    public SpreadsheetColumn() {
        initComponents();
    }

    /**
     * Creates new SpreadsheetColumn.
     * @param dbCol the database column this panel displays
     */
    public SpreadsheetColumn(final DataColumn dbCol) {
        this();

        this.dbColumn = dbCol;
        this.dbColID = dbCol.getID();

        try {
            dbColumn.getDB().registerDataColumnListener(dbColID, this);
        } catch (SystemErrorException e) {
            logger.error("Problem registering DataColumnListener", e);
        }
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setAlignmentY(TOP_ALIGNMENT);
        this.setBorder(BorderFactory.createLineBorder(Color.black));

        // if spreadsheet has vars but no data, help scrollbars appear
//        this.setPreferredSize(new Dimension(200, 0));
//        this.setMinimumSize(new Dimension(200, 2));
//        this.setSize(200, 2);

        updateComponents();
    }

    /**
     * updateComponents. Called when the SpreadsheetCell panels need to be
     * built and added to this Column panel.
     */
    private void updateComponents() {
        try {
            for (int j = 1; j <= dbColumn.getNumCells(); j++) {
                DataCell dc = (DataCell) dbColumn.getDB()
                                    .getCell(dbColumn.getID(), j);

                SpreadsheetCell sc =
                                new SpreadsheetCell(dbColumn.getDB(), dc);
                sc.setSize(200,50);
                this.add(sc);
            }
        } catch (SystemErrorException e) {
           logger.error("Failed to populate Spreadsheet.", e);
        }
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
            dbColumn = db.getDataColumn(colID);
        } catch (SystemErrorException e) {
            logger.error("Failed to reget DataColumn from colID = " + colID, e);
        }
        this.removeAll();
        // would not work without setting the size to something
        // I guess after first building the panel with no cells
        // the size goes to 0 by 0 and never regrows after that
        this.setSize(200,1000);
        updateComponents();
        validate();
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


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setName("Form"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
