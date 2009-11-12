package org.openshapa.views.discrete;

import org.openshapa.db.DataCell;
import org.openshapa.db.DataColumn;
import org.openshapa.db.Database;
import org.openshapa.db.ExternalColumnListListener;
import org.openshapa.db.SystemErrorException;
import org.openshapa.views.discrete.layouts.SheetLayout;
import org.openshapa.views.discrete.layouts.SheetLayoutFactory;
import org.openshapa.views.discrete.layouts.SheetLayoutFactory.SheetLayoutType;
import org.openshapa.controllers.NewVariableC;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Vector;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box.Filler;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.apache.log4j.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.openshapa.OpenSHAPA;

/**
 * Spreadsheetpanel is a custom component for viewing the contents of the
 * OpenSHAPA database as a spreadsheet.
 */
public final class SpreadsheetPanel extends JPanel
    implements ExternalColumnListListener, ComponentListener {

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
        headerView.setBorder(
                      BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK));

        columns = new Vector<SpreadsheetColumn>();

        scrollPane = new JScrollPane();
        this.add(scrollPane, BorderLayout.CENTER);
        scrollPane.setViewportView(mainView);
        scrollPane.setColumnHeaderView(headerView);
        colSelector = new Selector(this);
        cellSelector = new Selector(this);
        colSelector.addOther(cellSelector);
        cellSelector.addOther(colSelector);

        // setup strut for the mainView - used when scrollPane is resized
        Dimension d = new Dimension(0, DEFAULT_HEIGHT);
        viewportStrut = new Filler(d, d, d);
        mainView.add(viewportStrut);

        // set strut for headerView - necessary while there are no col headers
        d = new Dimension(0, SpreadsheetColumn.DEFAULT_HEADER_HEIGHT);
        Filler headerStrut = new Filler(d, d, d);
        headerView.add(headerStrut);

        // Set a border for the top right corner
        JPanel rightCorner = new JPanel();
        rightCorner.setBorder(
                      BorderFactory.createMatteBorder(1, 1, 1, 0, Color.BLACK));
        scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, rightCorner);

        // set the database and layout the columns
        this.setDatabase(db);
        this.buildColumns();
        setLayoutType(SheetLayoutType.Ordinal);

        // add a listener for window resize events
        scrollPane.addComponentListener(this);

        ResourceMap rMap = Application.getInstance(OpenSHAPA.class)
                                      .getContext()
                                      .getResourceMap(SpreadsheetPanel.class);

        // Set up the add new variable button
        newVar.setBorder(BorderFactory
                         .createMatteBorder(0, 0, 0, 1, Color.black));
        newVar.setName(rMap.getString("add.name"));
        newVar.setToolTipText(rMap.getString("add.tooltip"));

        ActionMap aMap = Application.getInstance(OpenSHAPA.class)
                                    .getContext()
                                    .getActionMap(SpreadsheetPanel.class, this);
        newVar.setAction(aMap.get("openNewVarMenu"));
        newVar.setText(" + ");

        newVar.setSize(newVar.getWidth(),
                       SpreadsheetColumn.DEFAULT_HEADER_HEIGHT);
        headerView.add(newVar);
    }



    /**
     * Populate from the database.
     */
    private void buildColumns() {
        try {
            Vector<Long> dbColIds = getDatabase().getColOrderVector();

            for (int i = 0; i < dbColIds.size(); i++) {
                addColumn(getDatabase(), dbColIds.elementAt(i));
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
        // make the SpreadsheetColumn

        // Remove previous instance of newVar from the header.
        headerView.remove(newVar);

        SpreadsheetColumn col = new SpreadsheetColumn(this,
                                                      db,
                                                      colID,
                                                      colSelector,
                                                      cellSelector);
        // add the datapanel to the scrollpane viewport
        mainView.add(col.getDataPanel());
        // add the headerpanel to the scrollpane headerviewport
        headerView.add(col.getHeaderPanel());
        // add the new variable '+' button to the header.
        headerView.add(newVar);

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
    public void deselectAll() {
        cellSelector.deselectAll();
        colSelector.deselectAll();
    }

    /**
     * Set Database.
     *
     * @param db Database to set
     */
    public void setDatabase(final Database db) {
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
    public Database getDatabase() {
        return (this.database);
    }

    /**
     * @return Selector handling the SpreadsheetCells.
     */
    public Selector getCellSelector() {
        return cellSelector;
    }

    /**
     * Action to invoke when a column is removed from a database.
     *
     * @param db The database that the column has been removed from.
     * @param colID The id of the freshly removed column.
     * @param old_cov The column order vector prior to the deletion.
     * @param new_cov The column order vector after to the deletion.
     */
    public void colDeletion(final Database db,
                            final long colID,
                            final Vector<Long> old_cov,
                            final Vector<Long> new_cov) {
        deselectAll();
        removeColumn(colID);
        relayoutCells();
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
        deselectAll();
        addColumn(db, colID);
    }

    /**
     * Action to invoke when the column order vector is edited (i.e, the order
     * of the columns is changed without any insertions or deletions).
     *
     * @param db The database that the column has been added to.
     * @param old_cov The column order vector prior to the insertion.
     * @param new_cov The column order vector after to the insertion.
     */
    public void colOrderVectorEdited(final Database db,
                                     final Vector<Long> old_cov,
                                     final Vector<Long> new_cov) {
        // do nothing for now
        return;
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
    public void setLayoutType(final SheetLayoutType type) {
        sheetLayout = SheetLayoutFactory.getLayout(type, columns);
        relayoutCells();
    }

    /**
     * Invoked when the component's size changes.
     *
     * @param e Component event.
     */
    public void componentResized(final ComponentEvent e) {
        // resize the strut height to at least the size of the viewport.
        Dimension d = new Dimension(0,
                                   scrollPane.getViewportBorderBounds().height);
        viewportStrut.changeShape(d, d, d);
        // force a validate of the contents.
        this.revalidate();
    }

    /**
     * Invoked when the component has been made invisible.
     *
     * @param e Component event.
     */
    public void componentHidden(final ComponentEvent e) {
    }

    /**
     * Invoked when the component's position changes.
     *
     * @param e Component event.
     */
    public void componentMoved(final ComponentEvent e) {
    }

    /**
     * Invoked when the component has been made visible.
     *
     * @param e Component event.
     */
    public void componentShown(final ComponentEvent e) {
    }

    /**
     * Method to invoke when the user clicks on the "+" icon in the spreadsheet
     * header.
     */
    @Action
    public void openNewVarMenu() {
        new NewVariableC();
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

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(SpreadsheetPanel.class);

    /** Reference to the spreadsheet layout handler. */
    private SheetLayout sheetLayout;

    /** Reference to the scrollPane. */
    private JScrollPane scrollPane;

    /** Strut used to expand the viewport to fill the scrollpane. */
    private Filler viewportStrut;

    /** Default height for the viewport if no cells yet. */
    private static final int DEFAULT_HEIGHT = 50;

    /** New variable button to be added to the column header panel. */
    private JButton newVar = new JButton();
}
