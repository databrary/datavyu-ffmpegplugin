package org.openshapa.views.discrete;

import com.usermetrix.jclient.Logger;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import java.io.File;
import java.io.IOException;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.Box.Filler;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import org.openshapa.OpenSHAPA;

import org.openshapa.OpenSHAPA.Platform;

import org.openshapa.controllers.NewVariableC;

import org.openshapa.event.component.FileDropEvent;
import org.openshapa.event.component.FileDropEventListener;

import org.openshapa.models.db.legacy.DataCell;
import org.openshapa.models.db.legacy.DataColumn;
import org.openshapa.models.db.legacy.Database;
import org.openshapa.models.db.legacy.ExternalColumnListListener;
import org.openshapa.models.db.legacy.SystemErrorException;

import org.openshapa.util.ArrayDirection;

import org.openshapa.views.discrete.layouts.SheetLayoutFactory.SheetLayoutType;

import com.usermetrix.jclient.UserMetrix;
import java.util.ArrayList;
import org.openshapa.models.db.Datastore;
import org.openshapa.models.db.DeprecatedDatabase;
import org.openshapa.models.db.Variable;
import org.openshapa.util.Constants;
import org.openshapa.views.discrete.layouts.SheetLayoutFactory;


/**
 * Spreadsheetpanel is a custom component for viewing the contents of the
 * OpenSHAPA database as a spreadsheet.
 */
public final class SpreadsheetPanel extends JPanel
implements ExternalColumnListListener,
           ComponentListener,
           CellSelectionListener,
           ColumnSelectionListener,
           KeyEventDispatcher {

    /** To use when navigating left. */
    static final int LEFT_DIR = -1;

    /** To use when navigating right. */
    static final int RIGHT_DIR = 1;

    /** Scrollable view inserted into the JScrollPane. */
    private SpreadsheetView mainView;

    /** View showing the Column titles. */
    private JPanel headerView;

    /** The Database being viewed. */
    private Datastore datastore;

    /** Vector of the Spreadsheetcolumns added to the Spreadsheet. */
    private List<SpreadsheetColumn> columns;

    /** The logger for this class. */
    private Logger logger = UserMetrix.getLogger(SpreadsheetPanel.class);

    /** Reference to the scrollPane. */
    private JScrollPane scrollPane;

    /** New variable button to be added to the column header panel. */
    private JButton newVar = new JButton();

    /** The currently highlighted cell. */
    private SpreadsheetCell highlightedCell;

    /** Last selected cell - used as an end point for continous selections. */
    private SpreadsheetCell lastSelectedCell;

    /** The layout that is currently being used. */
    private SheetLayoutType currentLayoutType;

    /** List containing listeners interested in file drop events. */
    private final transient List<FileDropEventListener> fileDropListeners;

    /**
     * Constructor.
     *
     * @param db The model (i.e. database) that we are creating the view
     * (i.e. Spreadsheet panel) for.
     */
    public SpreadsheetPanel(final Datastore db) {
        setName(this.getClass().getSimpleName());
        setLayout(new BorderLayout());

        mainView = new SpreadsheetView();
        mainView.setLayout(new BoxLayout(mainView, BoxLayout.X_AXIS));

        headerView = new JPanel();
        headerView.setLayout(new BoxLayout(headerView, BoxLayout.X_AXIS));
        headerView.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK));
        headerView.setName("headerView");

        // Default layout is ordinal.
        currentLayoutType = SheetLayoutType.Ordinal;

        columns = new ArrayList<SpreadsheetColumn>();

        scrollPane = new JScrollPane();
        this.add(scrollPane, BorderLayout.CENTER);
        add(scrollPane);
        scrollPane.setViewportView(mainView);
        scrollPane.setColumnHeaderView(headerView);

        // set strut for headerView - necessary while there are no col headers
        Dimension d = new Dimension(0, SpreadsheetColumn.DEFAULT_HEADER_HEIGHT);

        Filler headerStrut = new Filler(d, d, d);
        headerView.add(headerStrut);

        // Set a border for the top right corner
        JPanel rightCorner = new JPanel();
        rightCorner.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, Color.BLACK));
        scrollPane.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, rightCorner);

        ResourceMap rMap = Application.getInstance(OpenSHAPA.class).getContext()
                                      .getResourceMap(SpreadsheetPanel.class);

        // Set up the add new variable button
        newVar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, Constants.BORDER_SIZE, Color.black));
        newVar.setName("newVarPlusButton");
        newVar.setToolTipText(rMap.getString("add.tooltip"));

        ActionMap aMap = Application.getInstance(OpenSHAPA.class).getContext()
                                    .getActionMap(SpreadsheetPanel.class, this);
        newVar.setAction(aMap.get("openNewVarMenu"));
        newVar.setText(" + ");
        newVar.setSize(SpreadsheetColumn.DEFAULT_COLUMN_WIDTH,
                       SpreadsheetColumn.DEFAULT_HEADER_HEIGHT);
        headerView.add(newVar);

        // set the database and layout the columns
        setDatabase(db);
        buildColumns();

        // Enable drag and drop support.
        setDropTarget(new DropTarget(this, new SSDropTarget()));
        fileDropListeners = new LinkedList<FileDropEventListener>();

        lastSelectedCell = null;
    }

    /**
     * Registers this column data panel with everything that needs to notify
     * this class of events.
     */
    public void registerListeners() {
        // add a listener for window resize events
        scrollPane.addComponentListener(this);

        KeyboardFocusManager m = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        m.addKeyEventDispatcher(this);
    }

    /**
     * Deregisters this column data panel with everything that is currently
     * notiying it of events.
     */
    public void deregisterListeners() {
        // remove a listener for window resize events.
        scrollPane.removeComponentListener(this);

        KeyboardFocusManager m = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        m.removeKeyEventDispatcher(this);
    }

    /**
     * Populate from the database.
     */
    private void buildColumns() {
        for(Variable v : getDatastore().getAllVariables()) {
            addColumn(getDatastore(), v);
        }
    }

    /**
     * Add a column panel to the scroll panel.
     *
     * @param db database.
     * @param var The variable that this column represents.
     */
    private void addColumn(final Datastore db, final Variable var) {
        // Remove previous instance of newVar from the header.
        headerView.remove(newVar);

        // Create the spreadsheet column and register it.
        SpreadsheetColumn col = new SpreadsheetColumn(db, var, this, this);
        col.registerListeners();

        // add the datapanel to the scrollpane viewport
        mainView.add(col.getDataPanel());

        // add the headerpanel to the scrollpane headerviewport
        headerView.add(col);

        // add the new variable '+' button to the header.
        headerView.add(newVar);

        // Add the new column to the adjacent list of all the existing columns.
        for (SpreadsheetColumn c : columns) {
            c.addAdjacentColumn(col);
            col.addAdjacentColumn(c);
        }

        // and add it to our maintained ref collection
        columns.add(col);
    }

    /**
     * Remove all the columns from the spreadsheet panel.
     */
    @Override public void removeAll() {

        for (SpreadsheetColumn col : columns) {
            col.deregisterListeners();
            col.clear();
            col.clearAdjacentColumns();

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
        SpreadsheetColumn colToRemove = null;
        for (SpreadsheetColumn col : columns) {
            if (col.getColID() == colID) {
                colToRemove = col;
                col.deregisterListeners();
                mainView.remove(col.getDataPanel());
                headerView.remove(col);
                columns.remove(col);

                break;
            }
        }

        if (colToRemove != null) {
            for (SpreadsheetColumn col : columns) {
                col.removeAdjacentColumn(colToRemove);
            }
            colToRemove.clearAdjacentColumns();
        }
    }

    /**
     * @return the vector of Spreadsheet columns.
     * Need for UISpec4J testing
     */
    public List<SpreadsheetColumn> getColumns() {
        return columns;
    }

    /**
     * Deselect all selected items in the Spreadsheet.
     */
    public void deselectAll() {

        for (SpreadsheetColumn col : columns) {

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
    public void setDatabase(final Datastore db) {

        // check if we need to deregister
        if ((datastore != null) && (datastore != db)) {

            try {
                DeprecatedDatabase dd = (DeprecatedDatabase) db;
                dd.getDatabase().deregisterColumnListListener(this);
            } catch (SystemErrorException e) {
                logger.error("deregisterColumnListListener failed", e);
            }
        }

        // set the database
        datastore = db;

        // register as a columnListListener
        try {
            DeprecatedDatabase dd = (DeprecatedDatabase) db;
            dd.getDatabase().registerColumnListListener(this);
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
    public Datastore getDatastore() {
        return (datastore);
    }

    @Deprecated public Database getLegacyDatabase() {
        return ((DeprecatedDatabase) datastore).getDatabase();
    }

    /**
     * Action to invoke when a column is removed from a database.
     *
     * @param db The database that the column has been removed from.
     * @param colID The id of the freshly removed column.
     * @param oldCov The column order vector prior to the deletion.
     * @param newCov The column order vector after to the deletion.
     */
    @Override
    public void colDeletion(final Database db,
                            final long colID,
                            final Vector<Long> oldCov,
                            final Vector<Long> newCov) {
        deselectAll();
        removeColumn(colID);
        relayoutCells();
    }

    /**
     * Action to invoke when a column is added to a database.
     *
     * @param db The database that the column has been added to.
     * @param colID The id of the newly added column.
     * @param oldCov The column order vector prior to the insertion.
     * @param newCov The column order vector after to the insertion.
     */
    @Override
    public void colInsertion(final Database db,
                             final long colID,
                             final Vector<Long> oldCov,
                             final Vector<Long> newCov) {
        deselectAll();
        addColumn(datastore, ((DeprecatedDatabase) datastore).getByLegacyID(colID));
    }

    /**
     * Action to invoke when the column order vector is edited (i.e, the order
     * of the columns is changed without any insertions or deletions).
     *
     * @param db The database that the column has been added to.
     * @param oldCov The column order vector prior to the insertion.
     * @param newCov The column order vector after to the insertion.
     */
    @Override
    public void colOrderVectorEdited(final Database db,
                                     final Vector<Long> oldCov,
                                     final Vector<Long> newCov) {

        // do nothing for now
        return;
    }

    /**
     * Relayout the SpreadsheetCells in the spreadsheet.
     */
    @Deprecated
    public void relayoutCells() {
        this.revalidate();
        this.repaint();
        for (SpreadsheetColumn col : columns) {
            col.getDataPanel().revalidate();
            col.getDataPanel().repaint();
        }
    }

    /**
     * Dispatches the key event to the desired components.
     *
     * @param e The key event to dispatch.
     *
     * @return true if the event has been consumed by this dispatch, false
     * otherwise
     */
    @Override
    public boolean dispatchKeyEvent(final KeyEvent e) {

        // Quick filter - if we aren't dealing with a key press and left or
        // right arrow. Forget about it - just chuck it back to Java to deal
        // with.
        if ((e.getID() == KeyEvent.KEY_PRESSED)
                && ((e.getKeyCode() == KeyEvent.VK_LEFT)
                    || (e.getKeyCode() == KeyEvent.VK_RIGHT))) {

            // User is attempting to move to the column to the left.
            if ((e.getKeyCode() == KeyEvent.VK_LEFT)
                    && platformCellMovementMask(e)) {
                highlightAdjacentCell(LEFT_DIR);
                e.consume();

                return true;

                // User is attempting to move to the column to the right.
            } else if ((e.getKeyCode() == KeyEvent.VK_RIGHT)
                    && platformCellMovementMask(e)) {
                highlightAdjacentCell(RIGHT_DIR);
                e.consume();

                return true;
            }
        }

        return false;
    }

    /**
     * Highlights a cell in an adjacent column.
     *
     * @param direction The direction in which you wish to highlight an
     * adjacent column.
     */
    private void highlightAdjacentCell(final int direction) {

        // No cell selected - simply return, can't move left or right.
        if (highlightedCell == null) {
            return;
        }

        for (int colID = 0; colID < columns.size(); colID++) {

            for (int cellID = 0; cellID < columns.get(colID).getCells().size();
                    cellID++) {

                // For each of the cells in the columns - look for the
                // highlighted cell.
                SpreadsheetCell cell = columns.get(colID).getCells().get(
                        cellID);

                if (cell.getCellID() == highlightedCell.getCellID()) {

                    // Find column in the desired direction
                    int newColID = colID + direction;

                    if ((newColID >= 0) && (newColID < columns.size())) {

                        // Find the most appopriate cell in the new
                        // column.
                        int newCellID = Math.min(cellID,
                                (columns.get(newColID).getCells().size() - 1));

                        SpreadsheetCell newCell = columns.get(newColID)
                            .getCells().get(newCellID);
                        newCell.requestFocus();
                        newCell.setHighlighted(true);
                        setHighlightedCell(newCell);

                        return;
                    }
                }
            }
        }
    }

    /**
     * @return Vector of the selected columns.
     */
    public Vector<DataColumn> getSelectedCols() {
        Vector<DataColumn> selcols = new Vector<DataColumn>();

        try {
            Vector<DataColumn> cols = getLegacyDatabase().getDataColumns();
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
            Vector<DataCell> selectedCells = getSelectedCells();
            Vector<Long> columnOrder = getLegacyDatabase().getColOrderVector();

            // For each of the selected cells search to see if we have a column
            // to the left.
            for (DataCell cell : selectedCells) {

                for (int i = 0; i < columnOrder.size(); i++) {

                    if (columnOrder.get(i) == cell.getItsColID()) {

                        // We have at least one column to the left of the cells.
                        if (((i + dir.getModifier()) >= 0)
                                && ((i + dir.getModifier())
                                    < columnOrder.size())) {
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
    public Vector<DataCell> getSelectedCells() {
        Vector<DataCell> selcells = new Vector<DataCell>();

        try {
            Vector<DataColumn> cols = getLegacyDatabase().getDataColumns();
            int numCols = cols.size();

            for (int i = 0; i < numCols; i++) {
                DataColumn col = cols.elementAt(i);
                int numCells = col.getNumCells();

                for (int j = 1; j <= numCells; j++) {
                    DataCell dc = (DataCell) col.getDB().getCell(col.getID(),
                            j);

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
     * Mark a cell as highlighted in the spreadsheet panel.
     *
     * @param cellID The id of the cell to mark as highlighted.
     */
    public void highlightCell(final long cellID) {
        for (SpreadsheetColumn col : getColumns()) {
            for (SpreadsheetCell cell : col.getCells()) {
                if (cell.getCellID() == cellID) {
                    cell.setHighlighted(true);
                    setHighlightedCell(cell);

                    return;
                }
            }
        }
    }

    /**
     * Set the layout type for the spreadsheet.
     *
     * @param type SheetLayoutType to set.
     */
    public void setLayoutType(final SheetLayoutType type) {
        this.currentLayoutType = type;

        // Inject the layout manager into the columns
        for (SpreadsheetColumn col : columns) {
            col.setLayoutManager(SheetLayoutFactory.createLayout(type));
        }

        relayoutCells();
    }

    /**
     * Invoked when the component's size changes.
     *
     * @param e Component event.
     */
    @Override
    public void componentResized(final ComponentEvent e) {

        // resize the strut height to at least the size of the viewport.
        /*
        Dimension d = new Dimension(0,
                scrollPane.getViewportBorderBounds().height);
        viewportStrut.changeShape(d, d, d);
        //viewportStrut.change

        //System.err.println("component resized:" + d.height);

        // force a validate of the contents.
        revalidate();
         */
    }

    /**
     * Invoked when the component has been made invisible.
     *
     * @param e Component event.
     */
    @Override
    public void componentHidden(final ComponentEvent e) {
    }

    /**
     * Invoked when the component's position changes.
     *
     * @param e Component event.
     */
    @Override
    public void componentMoved(final ComponentEvent e) {
    }

    /**
     * Invoked when the component has been made visible.
     *
     * @param e Component event.
     */
    @Override
    public void componentShown(final ComponentEvent e) {
    }

    /**
     * Method to invoke when the user clicks on the "+" icon in the spreadsheet
     * header.
     */
    @Action public void openNewVarMenu() {
        new NewVariableC();
    }

    /**
     * Moves a given column to the left by a certain number of positions.
     * @param colID the ID of the column to move
     * @param positions the number of positions to the left to move the given
     * column.
     */
    public void moveColumnLeft(final long colID, final int positions) {
        logger.usage("move column left");

        int columnIndex = -1;

        // What index does the given column sit at
        for (int i = 0; i < columns.size(); i++) {

            if (columns.get(i).getColID() == colID) {
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
            invalidate();
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
        logger.usage("move column right");

        int columnIndex = -1;

        // What index does the column sit at
        for (int i = 0; i < columns.size(); i++) {

            if (columns.get(i).getColID() == colID) {
                columnIndex = i;
            }
        }

        if (columnIndex >= 0) {
            int newIndex = columnIndex + positions;

            if (newIndex < columns.size()) {
                shuffleColumn(columnIndex, newIndex);
                relayoutCells();
                invalidate();
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

        if ((source >= columns.size()) || (destination >= columns.size())) {
            return;
        }

        if (source == destination) {
            return;
        }

        try {

            // Write the new column order back to the database.
            Vector<Long> orderVec = getLegacyDatabase().getColOrderVector();

            Long sourceColumn = orderVec.elementAt(source);
            orderVec.removeElementAt(source);
            orderVec.insertElementAt(sourceColumn, destination);

            getLegacyDatabase().setColOrderVector(orderVec);
        } catch (SystemErrorException se) {
            logger.error("Unable to shuffle column order", se);
        }

        // Reorder the columns vector
        SpreadsheetColumn sourceColumn = columns.get(source);
        columns.remove(source);
        columns.add(destination, sourceColumn);

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

    /**
     * Returns the cells of the supplied column as ordered by the current
     * layout.
     *
     * @param col The column to fetch the cells from.
     *
     * @return The cells in ordinal order if SheetLayoutType is Ordinal,
     * otherwise the cells will be in temporal order.
     */
    public List<SpreadsheetCell> getOrderedCells(SpreadsheetColumn col) {
        if (this.currentLayoutType == SheetLayoutType.Ordinal) {
            return col.getCells();
        } else {
            return col.getCellsTemporally();
        }
    }

    /**
     * Adds a series of cells as a continuous selection.
     *
     * @param cell The cell to use as the end point for the selection.
     */
    @Override
    public void addCellToContinousSelection(final SpreadsheetCell cell) {

        try {
            if (lastSelectedCell != null) {
                DataCell c1 = (DataCell) getLegacyDatabase()
                                         .getCell(lastSelectedCell.getCellID());
                DataCell c2 = (DataCell) getLegacyDatabase()
                                         .getCell(cell.getCellID());

                // We can only do continous selections in a single column at
                // at the moment.
                if (c1.getItsColID() == c2.getItsColID()) {

                    // Deselect the highlighted cell.
                    if (highlightedCell != null) {
                        highlightedCell.setHighlighted(false);
                        highlightedCell.setSelected(true);
                        highlightedCell = null;
                    }

                    for (SpreadsheetColumn col : getColumns()) {

                        if (c1.getItsColID() == col.getColID()) {

                            // Perform continous selection.
                            boolean addToSelection = false;

                            for (SpreadsheetCell c : getOrderedCells(col)) {

                                if (!addToSelection) {
                                    c.setSelected(false);
                                }

                                if (c.equals(cell) || c.equals(lastSelectedCell)) {
                                    addToSelection = !addToSelection;

                                    // We always include start and end cells.
                                    c.setSelected(true);
                                }

                                if (addToSelection) {
                                    c.setSelected(true);
                                }
                            }

                            break;
                        }
                    }
                }
            } else {
                lastSelectedCell = cell;
            }
        } catch (SystemErrorException se) {
            logger.error("Unable to continous select cells", se);
        }
    }

    /**
     * Add a cell to the current selection.
     *
     * @param cell The cell to add to the selection.
     */
    @Override
    public void addCellToSelection(final SpreadsheetCell cell) {
        clearColumnSelection();

        if (highlightedCell != null) {
            highlightedCell.setHighlighted(false);
            highlightedCell.setSelected(true);

            highlightedCell = null;
        }

        lastSelectedCell = cell;
    }

    /**
     * Set the currently highlighted cell.
     *
     * @param cell The cell to highlight.
     */
    @Override
    public void setHighlightedCell(final SpreadsheetCell cell) {

        if (highlightedCell != null) {
            highlightedCell.setSelected(false);
            highlightedCell.setHighlighted(false);
            highlightedCell.invalidate();
        }

        highlightedCell = cell;
        lastSelectedCell = cell;
        clearColumnSelection();
    }

    /**
     * Clears the current cell selection.
     */
    @Override
    public void clearCellSelection() {
        highlightedCell = null;
        lastSelectedCell = null;

        for (SpreadsheetColumn col : getColumns()) {
            for (SpreadsheetCell cell : col.getCells()) {
                cell.setSelected(false);
                cell.setHighlighted(false);
            }
        }
    }

    /**
     * Add a column to the current selection.
     *
     * @param column The column to add to the current selection.
     */
    @Override
    public void addColumnToSelection(final SpreadsheetColumn column) {
        clearCellSelection();
        column.requestFocus();
    }

    /**
     * Clears the current column selection.
     */
    @Override
    public void clearColumnSelection() {
        for (SpreadsheetColumn col : getColumns()) {
            col.setSelected(false);
        }
    }

    /**
     * Utility method for determining if the platform specific input mask is
     * triggered.
     *
     * @param e KeyEvent to examine.
     * @return true if the input mask is used, false otherwise.
     */
    private boolean platformCellMovementMask(final KeyEvent e) {

        if ((OpenSHAPA.getPlatform() == Platform.MAC)
                && (e.getModifiers() == InputEvent.ALT_MASK)) {
            return true;
        } else if ((OpenSHAPA.getPlatform() == Platform.WINDOWS)
                && (e.getModifiers() == InputEvent.CTRL_MASK)) {
            return true;
        }

        return false;
    }

    /**
     * Add a listener interested in file drop events.
     *
     * @param listener The listener to add.
     */
    public void addFileDropEventListener(final FileDropEventListener listener) {

        synchronized (this) {
            fileDropListeners.add(listener);
        }
    }

    /**
     * Remove listener from being notified of file drop events.
     *
     * @param listener The listener to remove.
     */
    public void removeFileDropEventListener(
        final FileDropEventListener listener) {

        synchronized (this) {
            fileDropListeners.remove(listener);
        }
    }

    /**
     * Notifies all registered listeners about the file drag and drop event.
     *
     * @param files The files that were dropped onto the spreadsheet.
     */
    private void notifyFileDropEventListeners(final Iterable<File> files) {
        final FileDropEvent event = new FileDropEvent(this, files);

        synchronized (this) {

            for (FileDropEventListener listener : fileDropListeners) {
                listener.filesDropped(event);
            }
        }
    }

    /**
     * Inner class for handling file drag and drop.
     */
    private class SSDropTarget extends DropTargetAdapter {

        /**
         * Creates a new drag and drop handler.
         */
        public SSDropTarget() {
            super();
        }

        /**
         * The event handler for when a file is dropped onto the interface.
         *
         *@param dtde The event to handle.
         */
        @Override
        public void drop(final DropTargetDropEvent dtde) {
            Transferable tr = dtde.getTransferable();
            DataFlavor[] flavors = tr.getTransferDataFlavors();

            for (int type = 0; type < flavors.length; type++) {

                if (flavors[type].isFlavorJavaFileListType()) {
                    dtde.acceptDrop(DnDConstants.ACTION_REFERENCE);

                    List fileList = new LinkedList();

                    try {
                        fileList = (List) tr.getTransferData(flavors[type]);

                        // If we made it this far, everything worked.
                        dtde.dropComplete(true);
                    } catch (UnsupportedFlavorException e) {
                        dtde.rejectDrop();
                    } catch (IOException e) {
                        dtde.rejectDrop();
                    }

                    notifyFileDropEventListeners(fileList);

                    return;
                }
            }

            dtde.rejectDrop();
        }
    }

}
