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
package org.datavyu.views.discrete;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;
import org.datavyu.Datavyu;
import org.datavyu.Datavyu.Platform;
import org.datavyu.controllers.NewVariableC;
import org.datavyu.controllers.project.ProjectController;
import org.datavyu.event.component.FileDropEvent;
import org.datavyu.event.component.FileDropEventListener;
import org.datavyu.models.db.*;
import org.datavyu.models.project.Project;
import org.datavyu.util.ArrayDirection;
import org.datavyu.util.Constants;
import org.datavyu.views.DVProgressBar;
import org.datavyu.views.DataControllerV;
import org.datavyu.views.discrete.layouts.SheetLayout;
import org.datavyu.views.discrete.layouts.SheetLayoutFactory;
import org.datavyu.views.discrete.layouts.SheetLayoutFactory.SheetLayoutType;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import javax.swing.Box.Filler;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Spreadsheetpanel is a custom component for viewing the contents of the
 * Datavyu database as a spreadsheet.
 */
public final class SpreadsheetPanel extends JPanel
        implements DatastoreListener,
        CellSelectionListener,
        ColumnSelectionListener,
        ColumnVisibilityListener,
        KeyEventDispatcher {

    /**
     * To use when navigating left.
     */
    static final int LEFT_DIR = -1;
    /**
     * To use when navigating right.
     */
    static final int RIGHT_DIR = 1;
    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = UserMetrix.getLogger(SpreadsheetPanel.class);
    private static int numNewSheets = 1;
    /**
     * List containing listeners interested in file drop events.
     */
    private final transient CopyOnWriteArrayList<FileDropEventListener> fileDropListeners;
    /**
     * The current project.
     */
    private ProjectController projectController;
    
    //WR WANT TO RIGHT JUSTIFY THIS
    //private final SpringLayout.Constraints HIDDEN_VARS_CONSTRAINTS = SpringLayout.Constraints(Spring(SpringLayout.WEST),SpringLayout.VERTICAL_CENTER);
    private DataControllerV dataController;
    /**
     * Scrollable view inserted into the JScrollPane.
     */
    private SpreadsheetView mainView;
    /**
     * View showing the Column titles.
     */
    private JPanel headerView;
    /**
     * The Database being viewed.
     */
    private Datastore datastore;
    /**
     * Vector of the Spreadsheetcolumns added to the Spreadsheet.
     */
    private List<SpreadsheetColumn> columns;
    /**
     * Reference to the scrollPane.
     */
    private JScrollPane scrollPane;
    /**
     * New variable button to be added to the column header panel.
     */
    private JButton newVar = new JButton();
    private JLabel newVarSpacer = new JLabel();
    ;
    /**
     * Hidden variables button to be added to the column header panel.
     */
    private JButton hiddenVars;
    private JLabel hiddenVarsSpacer = new JLabel();
    /**
     * The currently highlighted cell.
     */
    private SpreadsheetCell highlightedCell;
    /**
     * Last selected cell - used as an end point for continuous selections.
     */
    private SpreadsheetCell lastSelectedCell;
    /**
     * The layout that is currently being used.
     */
    private SheetLayoutType currentLayoutType;

    //    public SpreadsheetPanel(final Datastore db, DVProgressBar progressBar) {
//        ProjectController pc = new ProjectController(null, this);
//        new SpreadsheetPanel(pc, progressBar);
//    }
    public SpreadsheetPanel(final ProjectController pc, DVProgressBar progressBar) {
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
        scrollPane.setDoubleBuffered(true);
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

        ResourceMap rMap = Application.getInstance(Datavyu.class).getContext()
                .getResourceMap(SpreadsheetPanel.class);

        
        // Set up the add new variable button
        newVar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, Constants.BORDER_SIZE, Color.black));
        newVar.setName("newVarPlusButton");
        newVar.setToolTipText(rMap.getString("add.tooltip"));
        
        ActionMap aMap = Application.getInstance(Datavyu.class).getContext()
                .getActionMap(SpreadsheetPanel.class, this);
        newVar.setAction(aMap.get("openNewVarMenu"));
        newVar.setText(" + ");
        headerView.add(newVar);


        // set the database and layout the columns
        if (pc.getDB() == null) {
            pc.setDatastore(new DatavyuDatastore());
        }
        setDatabase(pc.getDB());
        newVarSpacer.setText(" + ");
        newVarSpacer.setForeground(newVarSpacer.getBackground());
        mainView.add(newVarSpacer);

        hiddenVars = makeHiddenVarsButton();
        updateHiddenVars();
        headerView.add(hiddenVars);
        hiddenVarsSpacer.setForeground(hiddenVarsSpacer.getBackground());
        mainView.add(hiddenVarsSpacer);


        //layout the columns
        projectController = pc;
        buildColumns(progressBar);
        pc.setSpreadsheetPanel(this);

        setName(datastore.getName());
        numNewSheets++;


        // Enable drag and drop support.
        setDropTarget(new DropTarget(this, new SSDropTarget()));
        fileDropListeners = new CopyOnWriteArrayList<FileDropEventListener>();

        lastSelectedCell = null;

        updateHiddenVars();
        headerView.add(hiddenVars);

    }
    
    private JButton makeHiddenVarsButton()
    {
        JButton res = new JButton();
        res.setBorder(BorderFactory.createMatteBorder(0, 0, 0, Constants.BORDER_SIZE, Color.black));
        res.setName("hiddenVarsButton");       
        return res;
    }

    private void updateHiddenVars() {
        List<Variable> allVars = datastore.getAllVariables();
        List<Variable> hiddensOnly = new ArrayList<Variable>();
        final JPopupMenu dropdown = new JPopupMenu();
        for(final Variable v: allVars)
        {
            if(v.isHidden()) 
            {
                hiddensOnly.add(v);
                dropdown.add(new JMenuItem(
                        new AbstractAction(v.getName()) {
                            public void actionPerformed(ActionEvent e)
                            {
                                v.setHidden(false);
                            }
                        }));
            }
        }
        
        
        hiddenVars.setText("  " +  hiddensOnly.size() + " Hidden Column");
        if(hiddensOnly.size() != 1) hiddenVars.setText(hiddenVars.getText() + "s");
        hiddenVars.setText(hiddenVars.getText() + "  "); //cheating: easier than resizing the button
        
        hiddenVars.setEnabled(hiddensOnly.size() != 0);
        hiddenVars.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                dropdown.show(e.getComponent(), e.getX(), e.getY());
            }
        });
        hiddenVarsSpacer.setText(hiddenVars.getText());
    }

    public void redrawCells() {
        for (SpreadsheetColumn col : getColumns()) {
            for (SpreadsheetCell cell : col.getCells()) {
                cell.valueChange(cell.getCell().getValue());
            }
        }
    }

    /**
     * Gets the single instance project associated with the currently running
     * with Datavyu.
     *
     * @return The single project in use with this instance of Datavyu
     */
    public ProjectController getProjectController() {
        return projectController;
    }

    /**
     * Creates a new project controller. No usages as of 8/13/2014
     */
    public void newProjectController() {
        projectController = new ProjectController();
    }

    public DataControllerV getDataController() {
        return dataController;
    }

    public void setDataController(DataControllerV dataController) {
        this.dataController = dataController;
    }

    /**
     * Creates a new project controller, using the given project as the
     * underlying project.
     *
     * @param project
     */
    public void newProjectController(final Project project) {
//        projectController = new ProjectController(project);
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
    private void buildColumns(DVProgressBar progressBar) {
        List<Variable> vlist = getDatastore().getAllVariables();
        int pb_increment = 0;
        int i = 0;

        if (progressBar != null) {
            pb_increment = (100 - progressBar.getProgress()) / vlist.size();
        }

        for (Variable v : vlist) {
            if (progressBar != null) {
                progressBar.setProgress(progressBar.getProgress() + pb_increment, "Adding column " + ++i + " of " + vlist.size());
            }
            addColumn(getDatastore(), v);
        }
    }

    /**
     * Add a column panel to the scroll panel.
     *
     * @param db  database.
     * @param var The variable that this column represents.
     */
    private void addColumn(final Datastore db, final Variable var) {
        // Remove previous instance of newVar from the header.
        headerView.remove(newVar);
        headerView.remove(hiddenVars);
        mainView.remove(newVarSpacer);
        mainView.remove(hiddenVarsSpacer);

        // Create the spreadsheet column and register it.
        SpreadsheetColumn col = new SpreadsheetColumn(db, var, this, this, this);
        col.registerListeners();

        // add the datapanel to the scrollpane viewport
        mainView.addColumn(col);

        // add the headerpanel to the scrollpane headerviewport
        headerView.add(col);

        // add the new variable '+' button to the header.
        headerView.add(newVar);
        updateHiddenVars();
        headerView.add(hiddenVars);
        mainView.add(newVarSpacer);
        mainView.add(hiddenVarsSpacer);

        // and add it to our maintained ref collection
        columns.add(col);

        var.setOrderIndex(columns.size() - 1);

//        updateColumnIndex();
    }

    /**
     * Remove all the columns from the spreadsheet panel.
     */
    @Override
    public void removeAll() {
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
        datastore.clearCellSelection();
        datastore.clearVariableSelection();
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
        setName(db.getName());

        db.deselectAll();
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
        variableVisible(newVariable);
    }

    @Override
    public void variableRemoved(final Variable deletedVariable) {
        deselectAll();
        removeColumn(deletedVariable);
        revalidate();
        variableHidden(deletedVariable);
    }

    @Override
    public void variableOrderChanged() {
        // Do nothing.
    }

    @Override
    public void variableHidden(final Variable hiddenVariable) {
    }

    @Override
    public void variableVisible(final Variable visibleVariable) {
    }

    @Override
    public void variableNameChange(final Variable editedVariable) {
        // Do nothing.
    }

    /**
     * Dispatches the key event to the desired components.
     *
     * @param e The key event to dispatch.
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
     *                  adjacent column.
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
                        setHighlightedCell(newCell);

                        return;
                    }
                }
            }
        }
    }

    /**
     * @param dir The direction in which to search for adjacent cells (left or
     *            right).
     * @return An indication of the columns adjacent of the current cell
     * selection,
     * 0 = no columns to the left of the current cell selection.
     * 1 = one column to the left of the current cell selection.
     * 2 = many columns to the left of the current cell selection.
     */
    public int getAdjacentSelectedCells(final ArrayDirection dir) {
        int result = 0;

        for (Cell cell : datastore.getSelectedCells()) {
            for (int i = 0; i < datastore.getVisibleVariables().size(); i++) {
                if (datastore.getVisibleVariables().get(i).equals(datastore.getVariable(cell))) {
                    // We have at least one column to the left of the cells.
                    if (((i + dir.getModifier()) >= 0) && ((i + dir.getModifier())
                            < datastore.getVisibleVariables().size())) {
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
    @Action
    public void openNewVarMenu() {
        new NewVariableC();
    }

    /**
     * Moves a given column to the left by a certain number of positions.
     *
     * @param var       The variable for the column to move
     * @param positions the number of positions to the left to move the given
     *                  column.
     */
    public void moveColumnLeft(final Variable var, final int positions) {
        LOGGER.event("move column left");

        // What index does the given column sit at
        int columnIndex = var.getOrderIndex();

        if (columnIndex >= 0) {
            int newIndex = columnIndex - positions;

            if (newIndex < 0) {
                newIndex = 0;
            }
//            updateColumnIndex();
            shuffleColumn(columnIndex, newIndex);
        }
    }

    public void updateColumnIndex() {
        // What index does the given column sit at

        int columnIndex = -1;
        for (int i = 0; i < columns.size(); i++) {
            columns.get(i).getVariable().setOrderIndex(i);
        }
    }

    /**
     * Moves a given column to the right by a certain number of positions.
     *
     * @param var       The variable for the column to move
     * @param positions the number for positions to the right to move the given
     *                  column.
     */
    public void moveColumnRight(final Variable var, final int positions) {
        LOGGER.event("move column right");

        // What index does the column sit at
        int columnIndex = var.getOrderIndex();

        if (columnIndex >= 0) {
            int newIndex = columnIndex + positions;

            if (newIndex < columns.size()) {
//                updateColumnIndex();
                shuffleColumn(columnIndex, newIndex);
            }
        }
    }

    /**
     * Removes the source column and reinserts the column at a given
     * destination.
     *
     * @param source      index of the source column
     * @param destination index of the destination column
     */
    private void shuffleColumn(final int source, final int destination) {
        if ((source >= columns.size()) || (destination >= columns.size())) {
            return;
        }

        if (source == destination) {
            return;
        }
        System.out.println(source + ", " + destination);

        // Reorder the columns vector
        SpreadsheetColumn sourceColumn = columns.get(source);
        columns.get(source).getVariable().setOrderIndex(destination);
        columns.get(destination).getVariable().setOrderIndex(source);
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

//        updateColumnIndex();
        revalidate();
    }

    /**
     * Returns the cells of the supplied column as ordered by the current
     * layout.
     *
     * @param col The column to fetch the cells from.
     * @return The cells in ordinal order if SheetLayoutType is Ordinal,
     * otherwise the cells will be in temporal alignment.
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

            // We can only do continuous selections in a single column at
            // at the moment.
            if (v1.equals(v2)) {

                // Deselect the highlighted cell.
                if (highlightedCell != null) {
                    highlightedCell.getCell().setHighlighted(false);
                    highlightedCell.getCell().setSelected(true);
                    highlightedCell = null;
                }

                for (SpreadsheetColumn col : getColumns()) {

                    if (v1.equals(col.getVariable())) {

                        // Perform continuous selection.
                        boolean addToSelection = false;

                        for (SpreadsheetCell c : getOrderedCells(col)) {

                            if (!addToSelection) {
                                c.getCell().setSelected(false);
                            }

                            if (c.equals(cell) || c.equals(lastSelectedCell)) {
                                addToSelection = !addToSelection;

                                // We always include start and end cells.
                                c.getCell().setSelected(true);
                            }

                            if (addToSelection) {
                                c.getCell().setSelected(true);
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
            highlightedCell.getCell().setSelected(true);
            highlightedCell.getCell().setHighlighted(false);
            highlightedCell = null;
        }

        lastSelectedCell = cell;
    }

    @Override
    public void removeCellFromSelection(final SpreadsheetCell cell) {

        if (highlightedCell != null) {
            highlightedCell.getCell().setSelected(false);
            highlightedCell.getCell().setHighlighted(false);
            highlightedCell = null;
        }

//        lastSelectedCell = cell;
    }

    /**
     * Set the currently highlighted cell.
     *
     * @param cell The cell to highlight.
     */
    @Override
    public void setHighlightedCell(final SpreadsheetCell cell) {
        if (highlightedCell != null) {
            highlightedCell.getCell().setSelected(false);
            highlightedCell.invalidate();
        }

        highlightedCell = cell;
        lastSelectedCell = cell;
        highlightedCell.getCell().setHighlighted(true);
        clearColumnSelection();
    }

    /**
     * Clears the current cell selection.
     */
    @Override
    public void clearCellSelection() {
        highlightedCell = null;
        lastSelectedCell = null;

        datastore.clearCellSelection();
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
    
    @Override
    public void columnVisibilityChanged() {
        updateHiddenVars();
    }

    /**
     * Utility method for determining if the platform specific input mask is
     * triggered.
     *
     * @param e KeyEvent to examine.
     * @return true if the input mask is used, false otherwise.
     */
    private boolean platformCellMovementMask(final KeyEvent e) {

        if ((Datavyu.getPlatform() == Platform.MAC)
                && (e.getModifiers() == InputEvent.ALT_MASK)) {
            return true;
        } else if ((Datavyu.getPlatform() == Platform.WINDOWS)
                && (e.getModifiers() == InputEvent.CTRL_MASK)) {
            return true;
        }

        return false;
    }

    public void reorientView(SpreadsheetCell cell) {
        ((SheetLayout) scrollPane.getLayout()).reorientView(cell);
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
         * @param dtde The event to handle.
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
