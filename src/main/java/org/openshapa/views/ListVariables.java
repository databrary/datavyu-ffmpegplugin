package org.openshapa.views;

import org.openshapa.OpenSHAPA;
import org.openshapa.db.DataColumn;
import org.openshapa.db.Database;
import org.openshapa.db.ExternalColumnListListener;
import org.openshapa.db.SystemErrorException;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
import org.apache.log4j.Logger;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

/**
 * The dialog to list database variables.
 *
 * @author cfreeman
 */
public final class ListVariables extends OpenSHAPADialog
implements ExternalColumnListListener {

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(ListVariables.class);

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
    private DefaultTableModel tableModel;

    /** Mapping between database column id - to the table model. */
    private HashMap<Long, Integer> dbToTableMap;

    /**
     * Creates new form ListVariablesView.
     *
     * @param parent The parent frame for this dialog.
     * @param modal Is this dialog to be modal (true), or not.
     * @param db The database containing the variables you wish to list.
     */
    public ListVariables(final java.awt.Frame parent,
                         final boolean modal,
                         final Database db) {
        super(parent, modal);
        tableModel = new DefaultTableModel();
        dbToTableMap = new HashMap<Long, Integer>();

        initComponents();
        setName(this.getClass().getSimpleName());

        database = db;

        ResourceMap rMap = Application.getInstance(OpenSHAPA.class)
                                      .getContext()
                                      .getResourceMap(ListVariables.class);

        // Set the names of the columns.
        tableModel.addColumn(rMap.getString("Table.visibleColumn"));
        tableModel.addColumn(rMap.getString("Table.nameColumn"));
        tableModel.addColumn(rMap.getString("Table.typeColumn"));
        tableModel.addColumn(rMap.getString("Table.commentColumn"));

        // Populate table with a variable listing from the database
        try {
            Vector<DataColumn> dbColumns = database.getDataColumns();

            for (int i = 0; i < dbColumns.size(); i++) {
                DataColumn dbColumn = dbColumns.elementAt(i);

                // TODO bug #21 Add comment field.
                addRow(dbColumn, rMap);
            }
        } catch (SystemErrorException e) {
            logger.error("Unable to list variables.", e);
        }
    }

    /**
     * Add a new row to the variable list.
     *
     * @param dbColumn the DataColumn being added to the variable list.
     * @param rMap The resource map - used to find localised string bundles.
     */
    public void addRow(final DataColumn dbColumn, final ResourceMap rMap) {
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

        // TODO bug #21 Add comment field.

        // Add new row to table model.
        int rowId = tableModel.getRowCount();
        tableModel.insertRow(rowId, vals);
        dbToTableMap.put(dbColumn.getID(), rowId);
    }

    /**
     * Action to invoke when a column is removed from a database.
     *
     * @param db The database that the column has been removed from.
     * @param colID The id of the freshley removed column.
     */
    public void colDeletion(final Database db, final long colID) {
        int tableModelID = dbToTableMap.get(Long.valueOf(colID));
        tableModel.removeRow(tableModelID);
    }

    /**
     * Action to invoke when a column is added to a database.
     *
     * @param db The database that the column has been added to.
     * @param colID The id of the newly added column.
     */
    public void colInsertion(final Database db, final long colID) {
        ResourceMap rMap = Application.getInstance(OpenSHAPA.class)
                                      .getContext()
                                      .getResourceMap(ListVariables.class);

        try {
            DataColumn dbColumn = db.getDataColumn(colID);
            addRow(dbColumn, rMap);

        } catch (SystemErrorException e) {
            logger.error("Unable to insert column into variable list", e);
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
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(org.openshapa.OpenSHAPA.class).getContext().getResourceMap(ListVariables.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        variableList.setEnabled(false);
        variableList.setModel(tableModel);
        variableList.setName("variableList");
        variableList.setRowSelectionAllowed(false);
        jScrollPane1.setViewportView(variableList);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 375, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 275, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable variableList;
    // End of variables declaration//GEN-END:variables

}
