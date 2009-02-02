package au.com.nicta.openshapa.views.discrete;

import au.com.nicta.openshapa.db.DataCell;
import au.com.nicta.openshapa.db.DataColumn;
import au.com.nicta.openshapa.db.Database;
import au.com.nicta.openshapa.db.ExternalColumnListListener;
import au.com.nicta.openshapa.db.MatrixVocabElement;
import au.com.nicta.openshapa.db.SystemErrorException;
import au.com.nicta.openshapa.db.TimeStamp;
import au.com.nicta.openshapa.views.OpenSHAPADialog;
import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
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
    private SpreadsheetView mainView;
    /** View showing the Column titles. */
    private JPanel headerView;

    /** The Database being viewed. */
    private Database database;

    /** Mapping between database column id to the Spreadsheetcolumn. */
    private HashMap<Long, SpreadsheetColumn> columns;

    /** Selector object for handling Column header selection. */
    private Selector colSelector;

    /** Selector object for handling SpreadsheetCell selection. */
    private Selector cellSelector;

    /** The id of the last datacell that was created. */
    private long lastCreatedCellID;

    /** The id of the last datacell that was created. */
    private long lastCreatedColID = 0;

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(Spreadsheet.class);

    /** The default number of ticks per second to use. */
    private final static int TICKS_PER_SECOND = 1000;

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

        mainView = new SpreadsheetView();
        mainView.setLayout(new BoxLayout(mainView, BoxLayout.X_AXIS));

        headerView = new JPanel();
        headerView.setLayout(new BoxLayout(headerView, BoxLayout.X_AXIS));

        columns = new HashMap<Long, SpreadsheetColumn>();

        JScrollPane jScrollPane3 = new JScrollPane();
        this.add(jScrollPane3, BorderLayout.CENTER);
        jScrollPane3.setViewportView(mainView);
        jScrollPane3.setColumnHeaderView(headerView);

        colSelector = new Selector();
        cellSelector = new Selector();
        colSelector.addOther(cellSelector);
        cellSelector.addOther(colSelector);
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
        clearAll();
        try {
            Vector <DataColumn> dbColumns = getDatabase().getDataColumns();

            for (int i = 0; i < dbColumns.size(); i++) {
                DataColumn dbColumn = dbColumns.elementAt(i);

                addColumn(getDatabase(), dbColumn.getID());
            }
        } catch (SystemErrorException e) {
            logger.error("Failed to populate Spreadsheet.", e);
        }
    }

    /**
     * Clear all previous panels and references.
     */
    private void clearAll() {
        columns.clear();
        mainView.removeAll();
        headerView.removeAll();
    }

    /**
     * Add a column panel to the scroll panel.
     * @param db database.
     * @param colID ID of the column to add.
     */
    private void addColumn(final Database db, final long colID) {
        // make the SpreadsheetColumn
        SpreadsheetColumn col = new SpreadsheetColumn(this, db,
                                                      colID, colSelector);
        // add the datapanel to the scrollpane viewport
        mainView.add(col.getDataPanel());
        // add the headerpanel to the scrollpane headerviewport
        headerView.add(col.getHeaderPanel());

        // and add it to our maintained ref collection
        columns.put(colID, col);
    }

    /**
     * Remove a column panel from the scroll panel viewport.
     * @param colID ID of column to remove
     */
    private void removeColumn(final long colID) {

        SpreadsheetColumn foundcol = columns.get(colID);
        if (foundcol != null) {
            mainView.remove(foundcol.getDataPanel());
            headerView.remove(foundcol.getHeaderPanel());
            columns.remove(colID);
        } else {
            logger.warn("Did not find column to delete by id = " + colID);
        }
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

        deselectAll();
    }

    /**
     * @return Database this spreadsheet displays
     */
    public final Database getDatabase() {
        return (this.database);
    }

    /**
     * @return Selector handling the SpreadsheetCells.
     */
    public final Selector getCellSelector() {
        return cellSelector;
    }

    /**
     * @return Deselect all selected items in the Spreadsheet.
     */
    public final void deselectAll() {
        cellSelector.deselectAll();
        colSelector.deselectAll();
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
    public final void colDeletion(final Database db, final long colID) {
        deselectAll();
        removeColumn(colID);
        validate();
    }

    /**
     * Action to invoke when a column is added to a database.
     *
     * @param db The database that the column has been added to.
     * @param colID The id of the newly added column.
     */
    public final void colInsertion(final Database db, final long colID) {
        deselectAll();
        addColumn(db, colID);
        validate();
    }

    /**
     * @return Vector of the selected columns.
     */
    private Vector <DataColumn> getSelectedCols() {
        Vector <DataColumn> selcols = new Vector <DataColumn>();

        try {
            Vector <DataColumn> cols = database.getDataColumns();
            int numCols = columns.size();
            for (int i = 0; i < numCols; i++) {
                DataColumn col = cols.elementAt(i);
                if (col.getSelected()) {
                    selcols.add(col);
                }
            }
        } catch (SystemErrorException e) {
           logger.error("Unable to set new cell stop time.", e);
        }
        return selcols;
    }

    /**
     * @return Vector of the selected columns.
     */
    private Vector <DataCell> getSelectedCells() {
        Vector <DataCell> selcells = new Vector <DataCell>();

        try {
            Vector <DataColumn> cols = database.getDataColumns();
            int numCols = cols.size();
            for (int i = 0; i < numCols; i++) {
                DataColumn col = cols.elementAt(i);
                int numCells = col.getNumCells();
                for (int j = 1; j <= numCells; j++) {
                    DataCell dc = (DataCell) col.getDB()
                                            .getCell(col.getID(), j);
                    if (dc.getSelected()) {
                        selcells.add(dc);
                    }
                }
            }
        } catch (SystemErrorException e) {
           logger.error("Unable to set new cell stop time.", e);        }
        return selcells;
    }

    /**
     * Create a new cell with given onset. Currently just appends to the
     * selected column or the column that last had a cell added to it.
     *
     * @param milliseconds The number of milliseconds since the origin of the
     * spreadsheet to create a new cell from.
     */
    public final void createNewCell(final long milliseconds) {
        try {
            long onset = milliseconds;
            // if not coming from video controller (milliseconds < 0) allow
            // multiple adds
            boolean multiadd = (milliseconds < 0);
            if (milliseconds < 0) {
                onset = 0;
            }

            boolean newcelladded = false;
            // try for selected columns
            Iterator <DataColumn> itCols = getSelectedCols().iterator();
            while (itCols.hasNext()) {
                DataColumn col = itCols.next();
                MatrixVocabElement mve =
                                        database.getMatrixVE(col.getItsMveID());
                DataCell cell = new DataCell(col.getDB(),
                                                col.getID(),
                                                mve.getID());
                cell.setOnset(new TimeStamp(TICKS_PER_SECOND, onset));
                if (onset > 0) {
                    lastCreatedCellID = database.appendCell(cell);
                } else {
                    lastCreatedCellID = database.insertdCell(cell, 1);
                }
                lastCreatedColID = col.getID();
                newcelladded = true;
                if (!multiadd) {
                    break;
                }
            }

            if (!newcelladded) {
                // next try for selected cells
                Iterator <DataCell> itCells = getSelectedCells().iterator();
                while (itCells.hasNext()) {
                    // reget the selected cell from the database using its id
                    // in case a previous insert has changed its ordinal.
                    // recasting to DataCell without checking as the iterator
                    // only returns DataCells (no ref cells allowed so far)
                    DataCell dc = (DataCell) database
                                               .getCell(itCells.next().getID());
                    DataCell cell = new DataCell(database,
                                                 dc.getItsColID(),
                                                 dc.getItsMveID());
                    if (multiadd) {
                        cell.setOnset(dc.getOnset());
                        cell.setOffset(dc.getOffset());
                        lastCreatedCellID = database
                                            .insertdCell(cell, dc.getOrd() + 1);
                    } else {
                        cell.setOnset(new TimeStamp(TICKS_PER_SECOND, onset));
                        lastCreatedCellID = database.appendCell(cell);
                    }
                    lastCreatedColID = cell.getItsColID();
                    newcelladded = true;
                    if (!multiadd) {
                        break;
                    }
                }
            }
            // last try lastColCreated
            if (!newcelladded) {
                if (lastCreatedColID == 0) {
                    lastCreatedColID = database.getDataColumns().get(0).getID();
                }
                // would throw by now if no columns exist
                DataColumn col = database.getDataColumn(lastCreatedColID);
                DataCell cell = new DataCell(col.getDB(),
                                                col.getID(),
                                                col.getItsMveID());
                cell.setOnset(new TimeStamp(TICKS_PER_SECOND, onset));
                lastCreatedCellID = database.appendCell(cell);
            }
            deselectAll();
        } catch (SystemErrorException e) {
            logger.error("Unable to create a new cell.", e);
        }
    }

    /**
     * Sets the stop time of the last cell that was created.
     *
     * @param milliseconds The number of milliseconds since the origin of the
     * spreadsheet to set the stop time for.
     */
    public void setNewCellStopTime(final long milliseconds) {
        try {
            DataCell cell = (DataCell) database.getCell(lastCreatedCellID);
            cell.setOffset(new TimeStamp(TICKS_PER_SECOND, milliseconds));
            database.replaceCell(cell);
        } catch (SystemErrorException e) {
            logger.error("Unable to set new cell stop time.", e);
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

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 587, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 402, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
