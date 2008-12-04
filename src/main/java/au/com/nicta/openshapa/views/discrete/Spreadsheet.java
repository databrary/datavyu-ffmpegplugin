/*
 * Spreadsheet.java
 *
 * Created on 26/11/2008, 2:14:43 PM
 */

package au.com.nicta.openshapa.views.discrete;

import au.com.nicta.openshapa.db.DataColumn;
import au.com.nicta.openshapa.db.Database;
import au.com.nicta.openshapa.db.ExternalColumnListListener;
import au.com.nicta.openshapa.db.SystemErrorException;
import au.com.nicta.openshapa.views.OpenSHAPADialog;
import java.awt.BorderLayout;
import java.util.Vector;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import org.apache.log4j.Logger;

/**
 * The main spreadsheet window. Displays the database it refers
 * to, showing the database columns and cells within.
 * @author swhitcher
 */
public class Spreadsheet extends OpenSHAPADialog
        implements ExternalColumnListListener {

    /** Scrollable view inserted into the JScrollPane. */
    private SpreadsheetView mainview;
    /** View showing the Column titles. */
    private SpreadsheetColumnHeader headerView;

    /** The Database being viewed. */
    private Database database;

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(Spreadsheet.class);

    /**
     * Creates new, empty Spreadsheet. No database referred to as yet
     *
     * @param parent The parent frame for this dialog.
     * @param modal Is this dialog to be modal (true), or not.
     */
    public Spreadsheet(final java.awt.Frame parent,
                       final boolean modal) {
        super(parent, modal);
        initComponents();

        setName(this.getClass().getSimpleName());

        this.setLayout(new BorderLayout());

        mainview = new SpreadsheetView();
        mainview.setLayout(new BoxLayout(mainview, BoxLayout.X_AXIS));

        headerView = new SpreadsheetColumnHeader();

        JScrollPane jScrollPane3 = new JScrollPane();
        this.add(jScrollPane3, BorderLayout.CENTER);
        jScrollPane3.setViewportView(mainview);
        jScrollPane3.setColumnHeaderView(headerView);
    }

    /**
     * Creates new Spreadsheet.
     *
     * @param parent The parent frame for this dialog.
     * @param modal Is this dialog to be modal (true), or not.
     * @param db The database to display.
     */
    public Spreadsheet(final java.awt.Frame parent,
                       final boolean modal,
                       final Database db) {
        this(parent, modal);

        this.setDatabase(db);

        this.updateComponents();
    }

    /**
     * Populate from the database.
     */
    private void updateComponents() {
        try {
            Vector <DataColumn> dbColumns = getDatabase().getDataColumns();

            for (int i = 0; i < dbColumns.size(); i++) {
                DataColumn dbColumn = dbColumns.elementAt(i);

                addColumn(dbColumn);
            }
        } catch (SystemErrorException e) {
           logger.error("Failed to populate Spreadsheet.", e);
        }
    }

    /**
     * Add a column panel to the scroll panel.
     * @param dbColumn Column to add
     */
    private void addColumn(final DataColumn dbColumn) {
        SpreadsheetColumn col = new SpreadsheetColumn(dbColumn);

        mainview.add(col);

        String name = dbColumn.getName()
                                   + "   (" + dbColumn.getItsMveType() + ")";
        headerView.addColumn(name, dbColumn.getID());
    }

    /**
     * Remove a column panel from the scroll panel viewport.
     * @param colID ID of column to remove
     */
    private void removeColumn(final long colID) {
        SpreadsheetColumn foundcol = null;
        for (int i = 0; i < mainview.getComponentCount(); i++) {
            try {
                SpreadsheetColumn col =
                                (SpreadsheetColumn) mainview.getComponent(i);
                if (col.getColID() == colID) {
                    foundcol = col;
                    break;
                }
            } catch (ClassCastException e) {
                logger.info("Unexpected Component in mainview", e);
            }
        }
        if (foundcol != null) {
            mainview.remove(foundcol);
        } else {
            logger.warn("Did not find column to delete by id = " + colID);
        }

        headerView.removeColumn(colID);
    }

    /**
     * Set Database.
     * @param db Database to set
     */
    public final void setDatabase(final Database db) {
        // check if we need to deregister
        if ((database != null) && (database != db)) {
            try {
                database.deregisterColumnListListener(this);
            } catch (SystemErrorException e) {
                logger.warn("deregisterColumnListListener failed", e);
            }
        }

        // set the database
        this.database = db;

        // register as a columnListListener
        try {
            db.registerColumnListListener(this);
        } catch (SystemErrorException e) {
            logger.error("registerColumnListListener failed", e);
        }

        // setName to remember screen locations
        setName(this.getClass().getSimpleName() + db.getName());

        // show database name in title bar
        setTitle(db.getName());
    }

    /**
     * @return Database this spreadsheet displays
     */
    public final Database getDatabase() {
        return (this.database);
    }

    /**
     * ExternalColumnListListener overrides
     */

    /**
     * Action to invoke when a column is removed from a database.
     *
     * @param db The database that the column has been removed from.
     * @param colID The id of the freshly removed column.
     */
    @Override
    public final void colDeletion(final Database db, final long colID) {
        removeColumn(colID);
        validate();
    }

    /**
     * Action to invoke when a column is added to a database.
     *
     * @param db The database that the column has been added to.
     * @param colID The id of the newly added column.
     */
    @Override
    public final void colInsertion(final Database db, final long colID) {
        try {
            DataColumn dbColumn = (DataColumn) database.getColumn(colID);
            addColumn(dbColumn);

            // repaint the children - this is possibly more than needed
            // may only have to call validate on the SpreadsheetColumn
            // created in the call to addColumn
            validate();
        } catch (ClassCastException e) {
            logger.info("Not DataColumn in colInsertion", e);
        } catch (SystemErrorException e) {
            logger.error("Problem getting Column from DB = " + colID, e);
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 587, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 402, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
