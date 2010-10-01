package org.openshapa.views;

import com.google.common.collect.HashBiMap;
import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;
import java.util.logging.Level;
import org.openshapa.OpenSHAPA;
import java.util.Vector;
import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.openshapa.models.db.legacy.DataColumn;
import org.openshapa.models.db.legacy.Database;
import org.openshapa.models.db.legacy.ExternalCascadeListener;
import org.openshapa.models.db.legacy.ExternalColumnListListener;
import org.openshapa.models.db.legacy.ExternalDataColumnListener;
import org.openshapa.models.db.legacy.LogicErrorException;
import org.openshapa.models.db.legacy.MacshapaDatabase;
import org.openshapa.models.db.legacy.SystemErrorException;
import org.openshapa.views.discrete.SpreadsheetColumn;

/**
 * The dialog to list database variables.
 */
public final class VariableListV extends OpenSHAPADialog
implements ExternalDataColumnListener, ExternalCascadeListener, ExternalColumnListListener, TableModelListener {

    /** The logger for this class. */
    private Logger logger = UserMetrix.getLogger(VariableListV.class);

    /** Records changes to column during a cascade. */
    private ColumnChanges colChanges;

    /** The column for if a variable is visible or not. */
    private static final int VCOLUMN = 0;

    /** The column for a variables name. */
    private static final int NCOLUMN = 1;

    /** The column for a variables type. */
    private static final int TCOLUMN = 2;

    /** The column for a variables comment. */
    private static final int CCOLUMN = 3;

    /** The total number of columns in the variables list. */
    private static final int TOTAL_COLUMNS = 4;

    /** The database containing the variables you wish to list. */
    private Database database;

    /** The table model of the JTable that lists the actual variables. */
    private VListTableModel tableModel;

    /** Mapping between database column id - to the table model. */
    private HashBiMap<Long, Integer> dbToTableMap;

    private ResourceMap rMap = Application.getInstance(OpenSHAPA.class)
                                      .getContext()
                                      .getResourceMap(VariableListV.class);

    public void populateTable() {
        // Populate table with a variable listing from the database
        try {
            Vector<SpreadsheetColumn> ssColumns = OpenSHAPA.getView().getSpreadsheetPanel().getColumns();
            Vector<DataColumn> dbColumns = database.getDataColumns();
            //This is just in case something weird has happened
            if (ssColumns.size() != database.getColumns().size()) {
                for (int i = 0; i < dbColumns.size(); i++) {
                    DataColumn dbColumn = dbColumns.elementAt(i);
                    // TODO bug #21 Add comment field.
                    insertRow(dbColumn, rMap);
                }
                //This is what should normally happen
            } else {
                for (int i = 0; i < ssColumns.size(); i++) {
                    SpreadsheetColumn ssColumn = ssColumns.elementAt(i);
                    DataColumn dbColumn = getDataColumn(ssColumn.getColID());
                    if (dbColumn != null) {
                        // TODO bug #21 Add comment field.
                        insertRow(dbColumn, rMap);
                    }
                }
            }
        } catch (SystemErrorException e) {
            logger.error("Unable to list variables.", e);
        }
    }

    public void recreateMap() {
        //update Map
        dbToTableMap.clear();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            DataColumn dbColumn = null;
            try {
                dbColumn = database.getDataColumn((String) tableModel.getValueAt(i, NCOLUMN));
            } catch (SystemErrorException ex) {
                java.util.logging.Logger.getLogger(VariableListV.class.getName()).log(Level.SEVERE, null, ex);
            }
            dbToTableMap.put(dbColumn.getID(), i);
        }
    }

     /**
     * Private class for recording the changes reported by the listener
     * callbacks on this column.
     */
    private final class ColumnChanges {
        /** True if external changes have taken place. */
        boolean changes;
        /** Column name changed. */
        private Vector<Long> colsNameChanged;
        /** Column hidden changed. */
        private Vector<Long> colsHiddenChanged;
        /** List of cell IDs of newly inserted cells. */
        private Vector<Long> colsInserted;
        /** List of cell IDs of deleted cells. */
        private Vector<Long> colsDeleted;


        /**
         * ColumnChanges constructor.
         */
        private ColumnChanges() {
            colsHiddenChanged = new Vector<Long>();
            colsNameChanged = new Vector<Long>();
            colsInserted = new Vector<Long>();
            colsDeleted = new Vector<Long>();
            reset();
        }

        /**
         * Reset the ColumnChanges flags and lists.
         */
        private void reset() {
            changes = false;
            colsHiddenChanged.clear();
            colsNameChanged.clear();
            colsInserted.clear();
            colsDeleted.clear();
        }
    }

    /**
     * Creates new form ListVariablesView.
     *
     * @param parent The parent frame for this dialog.
     * @param modal Is this dialog to be modal (true), or not.
     * @param db The database containing the variables you wish to list.
     */
    public VariableListV(final java.awt.Frame parent,
                         final boolean modal,
                         final Database db) {
        super(parent, modal);
        tableModel = new VListTableModel();
        dbToTableMap = HashBiMap.create();
        initComponents();
        setName(this.getClass().getSimpleName());
        database = db;
        // Set the names of the columns.
        tableModel.addColumn(rMap.getString("Table.visibleColumn"));
        tableModel.addColumn(rMap.getString("Table.nameColumn"));
        tableModel.addColumn(rMap.getString("Table.typeColumn"));
        tableModel.addColumn(rMap.getString("Table.commentColumn"));
        //Use JTextfield to edit variable name cells
        variableList.getColumnModel().getColumn(NCOLUMN).setCellEditor(new DefaultCellEditor(new JTextField()));
        
        populateTable();

        //Listeners
        tableModel.addTableModelListener(this);
        registerCascadeListener();

        colChanges = new ColumnChanges();
    }

    /**
     * Registers this spreadsheet column with everything that needs to notify
     * this class of events.
     */
    public void registerCascadeListener() {
        try {
            database.registerCascadeListener(this);
        } catch (SystemErrorException e) {
            logger.error("Unable to register listeners for the variable list.", e);
        }
    }

    /**
     * Deregisters this spreadsheet column with everything that needs to notify
     * this class of events.
     */
    public void deregisterCascadeListener() {
        try {
            database.deregisterCascadeListener(this);
        } catch (SystemErrorException e) {
            logger.error("Unable to deregister listeners for the variable list.", e);
        }
    }

     /**
     * Registers this spreadsheet column with everything that needs to notify
     * this class of events.
     */
    public void registerDataColumnListener(long dbColID) {
        try {
            database.registerDataColumnListener(dbColID, this);
        } catch (SystemErrorException e) {
            logger.error("Unable to deregister listeners for the variable list.", e);
        }
    }
    
    /**
     * Deregisters this spreadsheet column with everything that needs to notify
     * this class of events.
     */
    public void deregisterDataColumnListener(long dbColID) {
        try {
            database.deregisterDataColumnListener(dbColID, this);
            database.deregisterCascadeListener(this);
        } catch (SystemErrorException e) {
            logger.error("Unable to deregister listeners for the variable list.", e);
        }
    }
  
     /**
     * Returns DataColumn with the specific column name.
     * @param columnName name of column variable
     * @return DataColumn for column, null if not found.
     */
    private DataColumn getDataColumn(final String columnName) throws SystemErrorException {
        Vector<DataColumn> dataCol = database.getDataColumns();

        for (DataColumn dc : dataCol) {
            if (dc.getName().equalsIgnoreCase(columnName)) {
                return dc;
            }
        }

        return null;
    }

         /**
     * Returns DataColumn with the specific column name.
     * @param columnID name of column variable
     * @return DataColumn for column, null if not found.
     */
    private DataColumn getDataColumn(final long columnID) throws SystemErrorException {
        Vector<DataColumn> dataCol = database.getDataColumns();

        for (DataColumn dc : dataCol) {
            if (dc.getID() == columnID) {
                return dc;
            }
        }

        return null;
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        if (!colChanges.changes) {
            int row = e.getFirstRow();
            int column = e.getColumn();
            TableModel model = (TableModel)e.getSource();
            String columnName = model.getColumnName(column);
            Object data = model.getValueAt(row, column);

            long varID = getIDInRow(row);
            DataColumn dc = null;
            try {
                dc = database.getDataColumn(varID);
            } catch (SystemErrorException ex) {
                java.util.logging.Logger.getLogger(VariableListV.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (columnName.equals(rMap.getString("Table.visibleColumn"))) {
                if (dc.getHidden() == (Boolean)data) {
                    try {
                        dc.setHidden(!(Boolean)data);
                        dc.setSelected(false);
                        MacshapaDatabase msdb = OpenSHAPA.getProjectController().getLegacyDB().getDatabase();
                        msdb.replaceColumn(dc);
                    } catch (SystemErrorException ex) {
                        java.util.logging.Logger.getLogger(VariableListV.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else if (columnName.equals(rMap.getString("Table.nameColumn"))) {
                if (!dc.getName().equals(data)) {
                    try {
                        if ((!dc.getName().equals((String)data)) && (dc.isValidColumnName(database, (String)data))) {
                            MacshapaDatabase msdb = OpenSHAPA.getProjectController().getLegacyDB().getDatabase();
                            dc.setName((String)data);
                            msdb.replaceColumn(dc);
                        }
                    } catch (LogicErrorException fe) {
                        OpenSHAPA.getApplication().showWarningDialog(fe);
                        tableModel.setValueAt(dc.getName(), row, column);
                    } catch (SystemErrorException see) {
                        OpenSHAPA.getApplication().showErrorDialog();
                        tableModel.setValueAt(dc.getName(), row, column);
                    }
                }
            } else if (columnName.equals(rMap.getString("Table.commentColumn"))) {
                if (!dc.getComment().equals(data)) {
                    try {
                        if ((!dc.getComment().equals((String)data)) && (dc.isValidColumnComment(database, (String)data))) {
                            MacshapaDatabase msdb = OpenSHAPA.getProjectController().getLegacyDB().getDatabase();
                            dc.setComment((String)data);
                            msdb.replaceColumn(dc);
                        }
                    } catch (LogicErrorException fe) {
                        OpenSHAPA.getApplication().showWarningDialog(fe);
                        tableModel.setValueAt(dc.getComment(), row, column);
                    } catch (SystemErrorException see) {
                        OpenSHAPA.getApplication().showErrorDialog();
                        tableModel.setValueAt(dc.getComment(), row, column);
                    }
                }
            }

            OpenSHAPA.getView().showSpreadsheet();
        }
    }

    public int getRowWithID(Long colID) {
        return dbToTableMap.get(colID);
    }

    public Long getIDInRow(int row) {
        return dbToTableMap.inverse().get(row);
    }


    public void insertRow(final DataColumn dbColumn, final ResourceMap rMap) {
        insertRow(dbColumn, rMap, -1);
    }


    /**
     * Add a new row to the variable list.
     *
     * @param dbColumn the DataColumn being added to the variable list.
     * @param rMap The resource map - used to find localised string bundles.
     */
    public void insertRow(final DataColumn dbColumn, final ResourceMap rMap, int position) {
        Object[] vals = new Object[TOTAL_COLUMNS];

        vals[VCOLUMN] = !dbColumn.getHidden();
        vals[NCOLUMN] = dbColumn.getName();
        switch (dbColumn.getItsMveType()) {
            case FLOAT:
                vals[TCOLUMN] = rMap.getString("VarType.float");
                break;
            case INTEGER:
                vals[TCOLUMN] = rMap.getString("VarType.integer");
                break;
            case TEXT:
                vals[TCOLUMN] = rMap.getString("VarType.text");
                break;
            case NOMINAL:
                vals[TCOLUMN] = rMap.getString("VarType.nominal");
                break;
            case PREDICATE:
                vals[TCOLUMN] = rMap.getString("VarType.predicate");
                break;
            case MATRIX:
                vals[TCOLUMN] = rMap.getString("VarType.matrix");
                break;
            default:
                vals[TCOLUMN] = rMap.getString("VarType.undefined");
                break;
        }

        // Comment field
        vals[CCOLUMN] = dbColumn.getComment();

        // Add new row to table model.
        int rowId = tableModel.getRowCount();
        if ((position >= 0) && (position <= tableModel.getRowCount())) {
            rowId = position;
        }
        tableModel.insertRow(rowId, vals);
        dbToTableMap.put(dbColumn.getID(), rowId);
        registerDataColumnListener(dbColumn.getID());
    }

    @Override
    public void colDeletion(Database db, long colID, Vector<Long> old_cov, Vector<Long> new_cov) {
        colChanges.changes = true;
        deregisterDataColumnListener(colID);

        //Remove row
        int tableModelID = dbToTableMap.get(Long.valueOf(colID));
        tableModel.removeRow(tableModelID);
        recreateMap();
    }

    @Override
    public void colOrderVectorEdited(Database db, Vector<Long> old_cov, Vector<Long> new_cov) {
        try {
            colChanges.changes = true;
            //Wipe everything and recreate
            tableModel.setRowCount(0);
            dbToTableMap.clear();
            Vector<DataColumn> dbColumns = database.getDataColumns();
            //This is just in case something weird has happened
            for (int i = 0; i < new_cov.size(); i++) {
                DataColumn dbColumn = getDataColumn(new_cov.elementAt(i));
                if (dbColumn != null) {
                    // TODO bug #21 Add comment field.
                    insertRow(dbColumn, rMap);
                }
            }
        } catch (SystemErrorException ex) {
            java.util.logging.Logger.getLogger(VariableListV.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Action to invoke when a column is added to a database.
     *
     * @param db The database that the column has been added to.
     * @param colID The id of the newly added column.
     * @param old_cov The column order vector prior to the insertion.
     * @param new_cov The column order vector after to the insertion.
     */
    public void colInsertion(final Database db, 
                             final long colID,
                             final Vector<Long> old_cov,
                             final Vector<Long> new_cov) {
        ResourceMap rMap = Application.getInstance(OpenSHAPA.class)
                                      .getContext()
                                      .getResourceMap(VariableListV.class);
        colChanges.changes = true;
        try {
            DataColumn dbColumn = db.getDataColumn(colID);
            //New columns always go to the end. If this changes,
            //you'll need to specify where to put the new row
            insertRow(dbColumn, rMap);
        } catch (SystemErrorException e) {
            logger.error("Unable to insert column into variable list", e);
        }
    }

    @Override
    public void DColCellDeletion(Database db, long colID, long cellID) {
        //Don't care
    }

    @Override
    public void DColCellInsertion(Database db, long colID, long cellID) {
        //Don't care
    }

    @Override
    public void DColConfigChanged(Database db, long colID, boolean nameChanged, String oldName, String newName, boolean hiddenChanged, boolean oldHidden, boolean newHidden, boolean readOnlyChanged, boolean oldReadOnly, boolean newReadOnly, boolean varLenChanged, boolean oldVarLen, boolean newVarLen, boolean selectedChanged, boolean oldSelected, boolean newSelected) {
        if (nameChanged) {
            colChanges.colsNameChanged.add(colID);
            colChanges.changes = true;
        }

        if (hiddenChanged) {
            colChanges.changes = true;
            colChanges.colsHiddenChanged.add(colID);
        }
    }

    @Override
    public void DColDeleted(Database db, long colID) {
        colChanges.changes = true;
        //Handled by ColDeletion
    }

    @Override
    public void beginCascade(Database db) {
        colChanges.reset();
    }

    @Override
    public void endCascade(Database db) {
        if (colChanges.colsHiddenChanged.size() > 0) {
            for (Long colID : colChanges.colsHiddenChanged) {
                try {
                    tableModel.setValueAt(!database.getColumn(colID).getHidden(), getRowWithID(colID), VCOLUMN);
                } catch (SystemErrorException ex) {
                    java.util.logging.Logger.getLogger(VariableListV.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        if (colChanges.colsNameChanged.size() > 0) {
            for (Long colID : colChanges.colsNameChanged) {
                try {
                    tableModel.setValueAt(database.getColumn(colID).getName(), getRowWithID(colID), NCOLUMN);
                } catch (SystemErrorException ex) {
                    java.util.logging.Logger.getLogger(VariableListV.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        if (colChanges.colsDeleted.size() > 0) {
            for (Long colID : colChanges.colsDeleted) {
                colDeletion(database, colID, null, null);
            }
        }
        colChanges.reset();
    }

    private class VListTableModel extends DefaultTableModel {

        @Override
        public Class<?> getColumnClass(int column) {
            if (column == VCOLUMN) {
                return Boolean.class;
            }
            return Object.class;
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            if (column == VCOLUMN || column == NCOLUMN || column == CCOLUMN) {
                return true;
            }
            return false;
        }
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        variableList = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(org.openshapa.OpenSHAPA.class).getContext().getResourceMap(VariableListV.class);
        setTitle(resourceMap.getString("variableListDialog.title")); // NOI18N
        setName("variableListDialog"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        variableList.setModel(tableModel);
        variableList.setMinimumSize(new java.awt.Dimension(400, 200));
        variableList.setName("variableList");
        variableList.setAutoCreateRowSorter(true);
        jScrollPane1.setViewportView(variableList);

        MigLayout layout = new MigLayout("nogrid");
                //new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        getContentPane().add(jScrollPane1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable variableList;
    // End of variables declaration//GEN-END:variables

}
