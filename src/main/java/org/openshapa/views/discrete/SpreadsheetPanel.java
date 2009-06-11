package org.openshapa.views.discrete;

import org.openshapa.db.DataCell;
import org.openshapa.db.DataColumn;
import org.openshapa.db.Database;
import org.openshapa.db.ExternalColumnListListener;
import org.openshapa.db.SystemErrorException;
import org.openshapa.views.discrete.layouts.SheetLayout;
import org.openshapa.views.discrete.layouts.SheetLayoutFactory;
import org.openshapa.views.discrete.layouts.SheetLayoutFactory.SheetLayoutType;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.apache.log4j.Logger;

/**
 * Spreadsheetpanel is a custom component for viewing the contents of the
 * OpenSHAPA database as a spreadsheet.
 */
public class SpreadsheetPanel extends JPanel
    implements ExternalColumnListListener {

    /**
     * Constructor.
     *
     * @param db The model (i.e. database) that we are creating the view
     * (i.e. Spreadsheet panel) for.
     */
    public SpreadsheetPanel(final Database db) {

        setName(this.getClass().getSimpleName());

        this.setLayout(new BorderLayout());

        mainView = new SpreadsheetView();
        mainView.setLayout(new BoxLayout(mainView, BoxLayout.X_AXIS));

        headerView = new JPanel();
        headerView.setLayout(new BoxLayout(headerView, BoxLayout.X_AXIS));

        columns = new Vector<SpreadsheetColumn>();

        JScrollPane jScrollPane3 = new JScrollPane();
        this.add(jScrollPane3, BorderLayout.CENTER);
        jScrollPane3.setViewportView(mainView);
        jScrollPane3.setColumnHeaderView(headerView);

        colSelector = new Selector(this);
        cellSelector = new Selector(this);
        colSelector.addOther(cellSelector);
        cellSelector.addOther(colSelector);

        this.setDatabase(db);
        this.buildColumns();
        setLayoutType(SheetLayoutType.Ordinal);
    }

    /**
     * Populate from the database.
     */
    private void buildColumns() {
        try {
            Vector <DataColumn> dbColumns = getDatabase().getDataColumns();

            // setup a filler box if the sheet has no columns yet
            // size is relative to its parent for now
            filler = Box.createRigidArea(new Dimension(FILLER_WIDTH,
                                                       FILLER_WIDTH));
            if (dbColumns.size() == 0) {
                mainView.add(filler);
            }

            for (int i = 0; i < dbColumns.size(); i++) {
                DataColumn dbColumn = dbColumns.elementAt(i);
                addColumn(getDatabase(), dbColumn.getID());
            }
        } catch (SystemErrorException e) {
            logger.error("Failed to populate Spreadsheet.", e);
        }
    }

    /**
     * Add a column panel to the scroll panel.
     *
     * @param db database.
     * @param colID ID of the column to add.
     */
    private void addColumn(final Database db, final long colID) {
        mainView.remove(filler);
        // make the SpreadsheetColumn
        SpreadsheetColumn col = new SpreadsheetColumn(this, db,
                                                      colID, colSelector);
        // add the datapanel to the scrollpane viewport
        mainView.add(col.getDataPanel());
        // add the headerpanel to the scrollpane headerviewport
        headerView.add(col.getHeaderPanel());

        // and add it to our maintained ref collection
        columns.add(col);
    }

    /**
     * Remove a column panel from the scroll panel viewport.
     *
     * @param colID ID of column to remove
     */
    private void removeColumn(final long colID) {
        for (SpreadsheetColumn col : columns) {
            if (col.getColID() == colID) {
                mainView.remove(col.getDataPanel());
                headerView.remove(col.getHeaderPanel());
                columns.remove(col);
                break;
            }
        }
        if (columns.size() == 0) {
            mainView.add(filler);
        }
    }

    /**
     * Returns the vector of Spreadsheet columns.
     * Need for UISpec4J testing
     */
    public Vector<SpreadsheetColumn> getColumns() {
        return columns;
    }

    /**
     * Deselect all selected items in the Spreadsheet.
     */
    public final void deselectAll() {
        cellSelector.deselectAll();
        colSelector.deselectAll();
    }

    /**
     * Set Database.
     *
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

        /*
        // set Temporal Ordering on
        try {
            db.setTemporalOrdering(true);
        } catch (SystemErrorException e) {
            logger.error("setTemporalOrdering failed", e);
        }*/

        // register as a columnListListener
        try {
            db.registerColumnListListener(this);
        } catch (SystemErrorException e) {
            logger.error("registerColumnListListener failed", e);
        }

        // setName to remember screen locations
        setName(this.getClass().getSimpleName() + db.getName());

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
     * Action to invoke when a column is removed from a database.
     *
     * @param db The database that the column has been removed from.
     * @param colID The id of the freshly removed column.
     */
    public final void colDeletion(final Database db, final long colID) {
        deselectAll();
        removeColumn(colID);
        relayoutCells();
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
        relayoutCells();
    }

    /**
     * Relayout the SpreadsheetCells in the spreadsheet.
     */
    public final void relayoutCells() {
        sheetLayout.relayoutCells();
        this.validate();
    }

    /**
     * @return Vector of the selected columns.
     */
    public Vector <DataColumn> getSelectedCols() {
        Vector <DataColumn> selcols = new Vector <DataColumn>();

        try {
            Vector <DataColumn> cols = database.getDataColumns();
            int numCols = cols.size();
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
    public Vector <DataCell> getSelectedCells() {
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
           logger.error("Unable to set new cell stop time.", e);
        }
        return selcells;
    }

    /**
     * Set the layout type for the spreadsheet.
     *
     * @param type SheetLayoutType to set.
     */
    public final void setLayoutType(final SheetLayoutType type) {
        sheetLayout = SheetLayoutFactory.getLayout(type, columns);
        relayoutCells();
    }

    /** Scrollable view inserted into the JScrollPane. */
    private SpreadsheetView mainView;

    /** View showing the Column titles. */
    private JPanel headerView;

    /** The Database being viewed. */
    private Database database;

    /** Vector of the Spreadsheetcolumns added to the Spreadsheet. */
    private Vector<SpreadsheetColumn> columns;

    /** Selector object for handling Column header selection. */
    private Selector colSelector;

    /** Selector object for handling SpreadsheetCell selection. */
    private Selector cellSelector;

    /** filler box for use when there are no datacells. */
    private Component filler;

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(SpreadsheetPanel.class);

    /** The width in pixels of filler blocks for empty columns. */
    private static final int FILLER_WIDTH = 50;

    /** Reference to the spreadsheet layout handler. */
    private SheetLayout sheetLayout;
}
