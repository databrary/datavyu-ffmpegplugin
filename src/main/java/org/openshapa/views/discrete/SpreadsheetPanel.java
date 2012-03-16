/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import java.io.File;
import java.io.IOException;

import java.util.LinkedList;
import java.util.List;

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


import org.openshapa.util.ArrayDirection;

import org.openshapa.views.discrete.layouts.SheetLayoutFactory.SheetLayoutType;

import com.usermetrix.jclient.UserMetrix;
import java.util.ArrayList;
import java.util.Arrays;
import org.openshapa.models.db.Cell;
import org.openshapa.models.db.Datastore;
import org.openshapa.models.db.DatastoreListener;
import org.openshapa.models.db.Variable;
import org.openshapa.util.Constants;
import org.openshapa.views.discrete.layouts.SheetLayoutFactory;


/**
 * Spreadsheetpanel is a custom component for viewing the contents of the
 * OpenSHAPA database as a spreadsheet.
 */
public final class SpreadsheetPanel extends JPanel
implements DatastoreListener,
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
    private static final Logger LOGGER = UserMetrix.getLogger(SpreadsheetPanel.class);

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

        columns = new ArrayList<SpreadsheetColumn>();
        scrollPane = new JScrollPane();
        this.add(scrollPane, BorderLayout.CENTER);
        add(scrollPane);
        scrollPane.setViewportView(mainView);
        scrollPane.setColumnHeaderView(headerView);

        // Default layout is ordinal.
        setLayoutType(SheetLayoutType.Ordinal);

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
        KeyboardFocusManager m = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        m.addKeyEventDispatcher(this);
    }

    /**
     * Deregisters this column data panel with everything that is currently
     * notiying it of events.
     */
    public void deregisterListeners() {
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
        mainView.addColumn(col);

        // add the headerpanel to the scrollpane headerviewport
        headerView.add(col);

        // add the new variable '+' button to the header.
        headerView.add(newVar);

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

            mainView.removeColumn(col);
            headerView.remove(col);
        }

        columns.clear();
    }

    /**
     * Remove a column panel from the scroll panel viewport.
     *
     * @param var
     */
    private void removeColumn(final Variable var) {
        for (SpreadsheetColumn col : columns) {
            if (col.getVariable().equals(var)) {
                mainView.removeColumn(col);
                headerView.remove(col);
                columns.remove(col);

                break;
            }
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
        // check if we need to deregister any existing listeners.
        if ((datastore != null) && (datastore != db)) {
            datastore.removeListener(this);
        }

        // set the database
        datastore = db;
        datastore.addListener(this);

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

    @Override
    public void variableAdded(final Variable newVariable) {
        addColumn(datastore, newVariable);
    }

    @Override
    public void variableRemoved(final Variable deletedVariable) {
        deselectAll();
        removeColumn(deletedVariable);
        revalidate();

    }

    @Override
    public void variableOrderChanged() {
        // Do nothing.
    }

    @Override
    public void variableHidden(final Variable hiddenVariable) {
        // Do nothing.
    }

    @Override
    public void variableVisible(final Variable visibleVariable) {
        // Do nothing.
    }

    @Override
    public void variableNameChange(final Variable editedVariable) {
        // Do nothing.
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
                SpreadsheetCell cell = columns.get(colID).getCells().get(cellID);

                if (cell.getCell().equals(highlightedCell.getCell())) {

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

        for (Cell cell : datastore.getSelectedCells()) {
            for (int i = 0; i < datastore.getAllVariables().size(); i++) {
                if (datastore.getAllVariables().get(i).equals(datastore.getVariable(cell))) {
                    // We have at least one column to the left of the cells.
                    if (((i + dir.getModifier()) >= 0) && ((i + dir.getModifier())
                                < datastore.getAllVariables().size())) {
                        result++;
                    }

                    // We have many columns to the left of the cells - end.
                    if (result == 2) {
                        return result;
                    }
                }
            }
        }

        return result;
    }

    /**
     * Mark a cell as highlighted in the spreadsheet panel.
     *
     * @param cell The cell to mark as highlighted.
     */
    public void highlightCell(final Cell cell) {
        for (SpreadsheetColumn col : getColumns()) {
            for (SpreadsheetCell spreadsheetCell : col.getCells()) {
                if (cell != null && spreadsheetCell.getCell().equals(cell)) {
                    spreadsheetCell.setHighlighted(true);
                    setHighlightedCell(spreadsheetCell);

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
        this.scrollPane.setLayout(SheetLayoutFactory.createLayout(type));

        revalidate();
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
     *
     * @param var The variable for the column to move
     * @param positions the number of positions to the left to move the given
     * column.
     */
    public void moveColumnLeft(final Variable var, final int positions) {
        LOGGER.event("move column left");

        // What index does the given column sit at
        int columnIndex = -1;
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).getVariable().equals(var)) {
                columnIndex = i;
            }
        }

        if (columnIndex >= 0) {
            int newIndex = columnIndex - positions;

            if (newIndex < 0) {
                newIndex = 0;
            }

            shuffleColumn(columnIndex, newIndex);
        }
    }

    /**
     * Moves a given column to the right by a certain number of positions.
     * @param var The variable for the column to move
     *
     * @param positions the number for positions to the right to move the given
     * column.
     */
    public void moveColumnRight(final Variable var, final int positions) {
        LOGGER.event("move column right");

        // What index does the column sit at
        int columnIndex = -1;
        for (int i = 0; i < columns.size(); i++) {

            if (columns.get(i).getVariable().equals(var)) {
                columnIndex = i;
            }
        }

        if (columnIndex >= 0) {
            int newIndex = columnIndex + positions;

            if (newIndex < columns.size()) {
                shuffleColumn(columnIndex, newIndex);
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

        Variable sourceVar = datastore.getAllVariables().get(source);
        datastore.getAllVariables().remove(source);
        datastore.getAllVariables().add(destination, sourceVar);

        // Reorder the columns vector
        SpreadsheetColumn sourceColumn = columns.get(source);
        columns.remove(source);
        columns.add(destination, sourceColumn);

        // Reorder the header components
        List<Component> newHeaders = new ArrayList<Component>(Arrays.asList(headerView.getComponents()));

        Component sourceHeaderComponent = newHeaders.get(source + 1);
        newHeaders.remove(source + 1);
        newHeaders.add(destination + 1, sourceHeaderComponent);

        headerView.removeAll();
        for (Component header : newHeaders) {
            headerView.add(header);
        }

        // Reorder the data components
        List<Component> newData = new ArrayList<Component>(Arrays.asList(mainView.getComponents()));
        Component sourceDataComponent = newData.get(source);
        newData.remove(source);
        newData.add(destination, sourceDataComponent);

        mainView.removeAll();
        for (Component data : newData) {
            mainView.add(data);
        }

        revalidate();
    }

    @Deprecated
    public void reorderColumns(List<Long> newOrderVec) {
        // No longer supported.
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
        if (lastSelectedCell != null) {
            Cell c1 = lastSelectedCell.getCell();
            Variable v1 = datastore.getVariable(c1);

            Cell c2 = cell.getCell();
            Variable v2 = datastore.getVariable(c2);

            // We can only do continous selections in a single column at
            // at the moment.
            if (v1.equals(v2)) {

                // Deselect the highlighted cell.
                if (highlightedCell != null) {
                    highlightedCell.setHighlighted(false);
                    highlightedCell.setSelected(true);
                    highlightedCell = null;
                }

                for (SpreadsheetColumn col : getColumns()) {

                    if (v1.equals(col.getVariable())) {

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
