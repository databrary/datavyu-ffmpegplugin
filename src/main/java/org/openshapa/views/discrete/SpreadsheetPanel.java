package org.openshapa.views.discrete;

import com.usermetrix.jclient.UserMetrix;
import org.openshapa.models.db.DataCell;
import org.openshapa.models.db.DataColumn;
import org.openshapa.models.db.Database;
import org.openshapa.models.db.ExternalColumnListListener;
import org.openshapa.models.db.SystemErrorException;
import org.openshapa.views.discrete.layouts.SheetLayout;
import org.openshapa.views.discrete.layouts.SheetLayoutFactory;
import org.openshapa.views.discrete.layouts.SheetLayoutFactory.SheetLayoutType;
import org.openshapa.controllers.NewVariableC;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.openshapa.OpenSHAPA;
import org.openshapa.util.ArrayDirection;

/**
 * Spreadsheetpanel is a custom component for viewing the contents of the
 * OpenSHAPA database as a spreadsheet.
 */
public final class SpreadsheetPanel extends JPanel
implements ExternalColumnListListener, ComponentListener,
           CellSelectionListener, ColumnSelectionListener {

    /** Scrollable view inserted into the JScrollPane. */
    private SpreadsheetView mainView;

    /** View showing the Column titles. */
    private JPanel headerView;

    /** The Database being viewed. */
    private Database database;

    /** Vector of the Spreadsheetcolumns added to the Spreadsheet. */
    private Vector<SpreadsheetColumn> columns;

    /** The logger for this class. */
    private UserMetrix logger = UserMetrix.getInstance(SpreadsheetPanel.class);

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

    /** The currently highlighted cell. */
    private SpreadsheetCell highlightedCell;

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
        headerView.setName("headerView");

        columns = new Vector<SpreadsheetColumn>();

        scrollPane = new JScrollPane();
        this.add(scrollPane, BorderLayout.CENTER);
        scrollPane.setViewportView(mainView);
        scrollPane.setColumnHeaderView(headerView);

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
        // Remove previous instance of newVar from the header.
        headerView.remove(newVar);

        // Create the spreadsheet column and register it.
        SpreadsheetColumn col = new SpreadsheetColumn(db, colID, this, this);
        col.registerListeners();

        // add the datapanel to the scrollpane viewport
        mainView.add(col.getDataPanel());

        // add the headerpanel to the scrollpane headerviewport
        headerView.add(col);
        // add the new variable '+' button to the header.
        headerView.add(newVar);

        // and add it to our maintained ref collection
        columns.add(col);
    }

    public void removeAll() {
        for (SpreadsheetColumn col : columns) {
            col.deregisterListeners();
            col.clear();

            mainView.remove(col.getDataPanel());
            headerView.remove(col);
        }

        columns.clear();
    }

    /**
     * Remove a column panel from the scroll panel viewport.
     *
     * @param colID ID of column to remove
     */
    private void removeColumn(final long colID) {
        for (SpreadsheetColumn col : columns) {
            if (col.getColID() == colID) {
                col.deregisterListeners();
                mainView.remove(col.getDataPanel());
                headerView.remove(col);
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
        for (SpreadsheetColumn col : this.columns) {
            if (col.isSelected()) {
                col.setSelected(false);
            }

            for (SpreadsheetCell cell : col.getCells()) {
                if (cell.isSelected()) {
                    cell.setSelected(false);
                }
            }
        }
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
                logger.error("deregisterColumnListListener failed", e);
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
     * @param dir The direction in which to search for adjacent cells (left or
     * right).
     *
     * @return An indication of the columns adjacent of the current cell
     * selection,
     * 0 = no columns to the left of the current cell selection.
     * 1 = one column to the left of the current cell selection.
     * 2 = many columns to the left of the current cell selection.
     */
    public int getAdjacentSelectedCells(final ArrayDirection dir) {
        int result = 0;

        try {
            Vector <DataCell> selectedCells = this.getSelectedCells();
            Vector <Long> columnOrder = database.getColOrderVector();

            // For each of the selected cells search to see if we have a column
            // to the left.
            for (DataCell cell : selectedCells) {
                for (int i = 0; i < columnOrder.size(); i++) {
                    if (columnOrder.get(i) == cell.getItsColID()) {
                        // We have at least one column to the left of the cells.
                        if ((i + dir.getModifier()) >= 0
                            && (i + dir.getModifier()) < columnOrder.size()) {
                            result++;
                        }

                        // We have many columns to the left of the cells - end.
                        if (result == 2) {
                            return result;
                        }
                    }
                }
            }
        } catch (SystemErrorException e) {
            logger.error("Unable to find columns to left of selection", e);
        }

        return result;
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

    /**
     * Moves a given column to the left by a certain number of positions.
     * @param colID the ID of the column to move
     * @param positions the number of positions to the left to move the given
     * column.
     */
    public void moveColumnLeft(final long colID, final int positions) {
        int columnIndex = -1;
        // What index does the given column sit at
        for (int i = 0; i < columns.size(); i++) {
            if (columns.elementAt(i).getColID() == colID) {
                columnIndex = i;
            }
        }
        if (columnIndex >= 0) {
            int newIndex = columnIndex - positions;
            if (newIndex < 0) {
                newIndex = 0;
            }
            shuffleColumn(columnIndex, newIndex);
            relayoutCells();
            this.invalidate();
            this.repaint();
        }
    }

    /**
     * Moves a given column to the right by a certain number of positions.
     * @param colID the ID of the column to move
     * @param positions the number for positions to the right to move the given
     * column.
     */
    public void moveColumnRight(final long colID, final int positions) {
        int columnIndex = -1;
        // What index does the column sit at
        for (int i = 0; i < columns.size(); i++) {
                        if (columns.elementAt(i).getColID() == colID) {
                columnIndex = i;
            }
        }
        if (columnIndex >= 0) {
            int newIndex = columnIndex + positions;
            if (newIndex < columns.size()) {
                shuffleColumn(columnIndex, newIndex);
                relayoutCells();
                this.invalidate();
                this.repaint();
            }
        }
    }

    /**
     * Removes the source column and reinserts the column at a given
     * destination.
     * @param source index of the source column
     * @param destination index of the destination column
     */
    private void shuffleColumn(final int source, final int destination) {
        if (source >= columns.size() || destination >= columns.size()) {
            return;
        }
        if (source == destination) {
            return;
        }

        try {
            // Write the new column order back to the database.
            Vector<Long> orderVec = this.database.getColOrderVector();

            Long sourceColumn = orderVec.elementAt(source);
            orderVec.removeElementAt(source);
            orderVec.insertElementAt(sourceColumn, destination);

            this.database.setColOrderVector(orderVec);
        } catch (SystemErrorException se) {
            logger.error("Unable to shuffle column order", se);
        }

        // Reorder the columns vector
        SpreadsheetColumn sourceColumn = columns.elementAt(source);
        columns.removeElementAt(source);
        columns.insertElementAt(sourceColumn, destination);

        // Reorder the header components
        Vector<Component> newHeaders = new Vector<Component>();
        Component[] headers = headerView.getComponents();
        for (int i = 0; i < headers.length; i++) {
            newHeaders.add(headers[i]);
        }
        Component sourceHeaderComponent = newHeaders.elementAt(source + 1);
        newHeaders.removeElementAt(source + 1);
        newHeaders.insertElementAt(sourceHeaderComponent, destination + 1);

        // Reorder the data components
        Vector<Component> newData = new Vector<Component>();
        Component[] data = mainView.getComponents();
        for (int i = 0; i < data.length; i++) {
            newData.add(data[i]);
        }
        Component sourceDataComponent = newData.elementAt(source + 1);
        newData.removeElementAt(source + 1);
        newData.insertElementAt(sourceDataComponent, destination + 1);

        // Reset the containers
        headerView.removeAll();
        mainView.removeAll();

        // Re-insert components into the containers
        for (int i = 0; i < headers.length; i++) {
            if (i < data.length) {
                headerView.add(newHeaders.elementAt(i));
                mainView.add(newData.elementAt(i));
            } else {
                headerView.add(newHeaders.elementAt(i));
            }
        }
    }

    public void addCellToSelection(SpreadsheetCell cell) {
        this.clearColumnSelection();
        if (highlightedCell != null) {
            highlightedCell.setHighlighted(false);
            highlightedCell.setSelected(true);

            highlightedCell = null;
        }
    }

    public void setHighlightedCell(SpreadsheetCell cell) {
        if (highlightedCell != null) {
            highlightedCell.setSelected(false);
        }

        this.highlightedCell = cell;
        this.clearColumnSelection();
    }

    public void clearCellSelection() {
        highlightedCell = null;

        for (SpreadsheetColumn col : this.getColumns()) {
            for (SpreadsheetCell cell : col.getCells()) {
                cell.setSelected(false);
                cell.setHighlighted(false);
            }
        }
    }

    public void addColumnToSelection(SpreadsheetColumn column) {
        this.clearCellSelection();
        column.requestFocus();
    }

    public void clearColumnSelection() {
        for (SpreadsheetColumn col : this.getColumns()) {
            col.setSelected(false);
        }
    }
}
