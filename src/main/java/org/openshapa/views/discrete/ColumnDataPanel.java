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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

import java.util.AbstractList;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

import com.usermetrix.jclient.UserMetrix;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openshapa.models.db.Cell;
import org.openshapa.models.db.Datastore;
import org.openshapa.models.db.Variable;
import org.openshapa.util.Constants;

/**
 * ColumnDataPanel panel that contains the SpreadsheetCell panels.
 */
public final class ColumnDataPanel extends JPanel implements KeyEventDispatcher {
    /** Width of the column. */
    private int columnWidth;

    /** Height of the column. */
    private int columnHeight;

    /** The model that this variable represents. */
    private Variable model;

    /** The cell selection listener used for cells in this column. */
    private CellSelectionListener cellSelectionL;

    /** Collection of the SpreadsheetCells held in by this data panel. */
    private List<SpreadsheetCell> cells;

    /** The mapping between the database and the spreadsheet cells. */
    private Map<Cell, SpreadsheetCell> viewMap;

    /** The logger for this class. */
    private static final Logger LOGGER = UserMetrix.getLogger(ColumnDataPanel.class);

    /** button for creating a new empty cell. */
    private SpreadsheetEmptyCell newCellButton;

    /** Padding for the bottom of the column. */
    private JPanel padding;

    /**
     * Creates a new ColumnDataPanel.
     *
     * @param db The datastore that this column data panel reflects.
     * @param width The width of the new column data panel in pixels.
     * @param variable The Data Column that this panel represents.
     * @param cellSelL Spreadsheet cell selection listener.
     */
    public ColumnDataPanel(final Datastore db,
                           final int width,
                           final Variable variable,
                           final CellSelectionListener cellSelL) {
        super();

        // Store member variables.
        columnWidth = width;
        columnHeight = 0;
        cells = new ArrayList<SpreadsheetCell>();
        viewMap = new HashMap<Cell, SpreadsheetCell>();
        cellSelectionL = cellSelL;
        model = variable;

        setLayout(null);
        //setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, Constants.BORDER_SIZE, new Color(175, 175, 175)));

        newCellButton = new SpreadsheetEmptyCell(variable);
        this.add(newCellButton);

        padding = new JPanel();
        padding.setBackground(new Color(237, 237, 237));
        padding.setBorder(BorderFactory.createMatteBorder(0, 0, 0, Constants.BORDER_SIZE, new Color(175, 175, 175)));
        this.add(padding);

        // Populate the data column with spreadsheet cells.
        buildDataPanelCells(db, variable, cellSelL);
    }

    /**
     * Registers this column data panel with everything that needs to notify
     * this class of events.
     */
    public void registerListeners() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                            .addKeyEventDispatcher(this);
    }

    /**
     * Deregisters this column data panel with everything that is currently
     * notifying this class of events.
     */
    public void deregisterListeners() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                            .removeKeyEventDispatcher(this);
    }

    /**
     * Build the SpreadsheetCells and add to the DataPanel.
     *
     * @param db The datastore holding cells that this column will represent.
     * @param variable The variable to display.
     * @param cellSelL Spreadsheet listener to notify about cell selection
     * changes.
     */
    private void buildDataPanelCells(final Datastore db,
                                     final Variable variable,
                                     final CellSelectionListener cellSelL) {

        // traverse and build the cells
        for (Cell cell : variable.getCellsTemporally()) {
            SpreadsheetCell sc = new SpreadsheetCell(db, cell, cellSelL);
            cell.addListener(sc);

            // add cell to the JPanel
            this.add(sc);

            // and add it to our reference list
            cells.add(sc);

            // Add the ID's to the mapping.
            viewMap.put(cell, sc);
            columnHeight += sc.getHeight();
        }

        this.add(newCellButton);
        this.setSize(columnWidth, columnHeight);
    }

    /**
     * Clears the cells stored in the column data panel.
     */
    public void clear() {
        for (SpreadsheetCell cell : cells) {
            cell.getCell().removeListener(cell);
            this.remove(cell);
        }

        cells.clear();
        viewMap.clear();
    }

    /**
     * Find and delete SpreadsheetCell by its ID.
     *
     * @param cell The cell to find and delete from the column data panel.
     */
    public void deleteCell(final Cell cell) {
        SpreadsheetCell sCell = viewMap.get(cell);
        cell.removeListener(sCell);
        this.remove(sCell);
        cells.remove(sCell);
        viewMap.remove(cell);
    }

    /**
     * Insert a new SpreadsheetCell for a given cell.
     *
     * @param db The database holding the cell that is being inserted into this
     * column data panel.
     * @param cell The cell to create and insert into this column data panel.
     * @param cellSelL SpreadsheetCellSelectionListener to notify of changes in
     * selection.
     */
    public void insertCell(final Datastore ds,
                           final Cell cell,
                           final CellSelectionListener cellSelL) {

        SpreadsheetCell nCell = new SpreadsheetCell(ds, cell, cellSelL);
        nCell.setWidth(this.getWidth());
        cell.addListener(nCell);

        nCell.setAlignmentX(Component.RIGHT_ALIGNMENT);
        this.add(nCell);
        viewMap.put(cell, nCell);
        nCell.requestFocus();

//        try {
//            DataCell dc = (DataCell) db.getCell(cellID);
//            SpreadsheetCell nCell = new SpreadsheetCell(db, dc, cellSelL);
//            nCell.setWidth(this.getWidth());
//            db.registerDataCellListener(dc.getID(), nCell);
//
//            Long newOrd = new Long(dc.getOrd());
//            nCell.setAlignmentX(Component.RIGHT_ALIGNMENT);
//
//            if (cells.size() > newOrd.intValue()) {
//                cells.add(newOrd.intValue() - 1, nCell);
//                this.add(nCell, newOrd.intValue() - 1);
//            } else {
//                cells.add(nCell);
//                this.add(nCell);
//            }
//            viewMap.put(cellID, nCell);
//
//            nCell.requestFocus();
//        } catch (SystemErrorException e) {
//            LOGGER.error("Problem inserting a new SpreadsheetCell", e);
//        }
    }

    /**
     * Set the width of the SpreadsheetCell.
     *
     * @param width New width of the SpreadsheetCell.
     */
    public void setWidth(final int width) {
        columnWidth = width;
    }

    public void setHeight(final int height) {
        columnHeight = height;
    }

    /**
     * Override Preferred size to fix the width.
     *
     * @return the preferred size of the data column.
     */
    @Override public Dimension getPreferredSize() {
        return new Dimension(columnWidth, columnHeight);
    }

    public SpreadsheetEmptyCell getNewCellButton() {
        return this.newCellButton;
    }

    public JPanel getPadding() {
        return this.padding;
    }

    public SpreadsheetCell getCellTemporally(final int index) {
        return viewMap.get(model.getCellTemporally(index));
    }

    /**
     * @return The SpreadsheetCells in this column temporally.
     */
    public List<SpreadsheetCell> getCellsTemporally() {
        ArrayList<SpreadsheetCell> result = new ArrayList<SpreadsheetCell>();

        for (Cell c : model.getCellsTemporally()) {
            result.add(viewMap.get(c));
        }

        return result;
    }

    /**
     * @return The number of cells stored in this column.
     */
    public int getNumCells() {
        return cells.size();
    }

    /**
     * @return The SpreadsheetCells in this column.
     */
    public List<SpreadsheetCell> getCells() {
        return cells;
    }

    /**
     * @return The selected spreadsheet cells in this column.
     */
    public AbstractList<SpreadsheetCell> getSelectedCells() {
        AbstractList<SpreadsheetCell> selectedCells = new ArrayList<SpreadsheetCell>();

        for (SpreadsheetCell c : selectedCells) {
            if (c.isSelected()) {
                selectedCells.add(c);
            }
        }

        return selectedCells;
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

        // Quick filter - if we aren't dealing with a key press or up and down
        // arrow. Forget about it - just chuck it back to Java to deal with.
        if ((e.getID() != KeyEvent.KEY_PRESSED)
                && ((e.getKeyCode() != KeyEvent.VK_UP)
                    || (e.getKeyCode() != KeyEvent.VK_DOWN))) {
            return false;
        }

        Component[] components = getComponents();
        int numCells = getComponentCount() - 1;

        // For each of the cells in the column - see if one has focus.
        for (int i = 0; i < numCells; i++) {

            if (components[i].isFocusOwner()
                    && components[i].getClass().equals(JButton.class)) {

                if ((e.getKeyCode() == KeyEvent.VK_UP) && (i > 0)) {
                    SpreadsheetCell sc = (SpreadsheetCell) components[i - 1];
                    EditorTracker et = sc.getDataView().getEdTracker();
                    EditorComponent ec = et.getCurrentEditor();

                    // Get the caret position within the active editor
                    // component.
                    int relativePos = et.getCurrentEditor().getCaretPosition();
                    int absolutePos = sc.getDataView().getCaretPosition();

                    try {

                        // Determine if we are at the top of a multi-lined cell,
                        // if we are not on the top line - pressing up should
                        // select the line above.
                        JTextArea a = (JTextArea) ec.getParentComponent();

                        if (a.getLineOfOffset(a.getCaretPosition()) == 0) {
                            sc = (SpreadsheetCell) components[i - 1];
                            et = sc.getDataView().getEdTracker();
                            ec = et.findEditor(absolutePos);
                            et.setEditor(ec);

                            a = (JTextArea) ec.getParentComponent();

                            // Determine the line start and end points.
                            int lastLine = (a.getLineCount() - 1);
                            int lineEnd = a.getLineEndOffset(lastLine);
                            int lineStart = a.getLineStartOffset(lastLine);

                            // We take either the position or the last element
                            // in the line
                            int newPos = Math.min(relativePos + lineStart,
                                    lineEnd);

                            // Set the caret position in the newly focused
                            // editor.
                            ec.setCaretPosition(newPos);
                            sc.requestFocus();
                            sc.setHighlighted(true);
                            cellSelectionL.setHighlightedCell(sc);

                            e.consume();

                            return true;
                        }
                    } catch (BadLocationException be) {
                        LOGGER.error("BadLocation on arrow up", be);
                    }
                }

                return false;

            }

            // The current cell has focus.
            if (components[i].isFocusOwner()
                    && components[i].getClass().equals(SpreadsheetCell.class)) {

                // Get the current editor tracker and component for the cell
                // that has focus.
                SpreadsheetCell sc = (SpreadsheetCell) components[i];
                EditorTracker et = sc.getDataView().getEdTracker();
                EditorComponent ec = et.getCurrentEditor();

                // Get the caret position within the active editor component.
                int relativePos = et.getCurrentEditor().getCaretPosition();
                int absolutePos = sc.getDataView().getCaretPosition();

                // The key stroke is up - select the editor component in the
                // cell above, setting the caret position to what we just found
                // in the current cell.
                if ((e.getKeyCode() == KeyEvent.VK_UP) && (i > 0)) {

                    try {

                        // Determine if we are at the top of a multi-lined cell,
                        // if we are not on the top line - pressing up should
                        // select the line above.
                        JTextArea a = (JTextArea) ec.getParentComponent();

                        if (a.getLineOfOffset(a.getCaretPosition()) == 0) {
                            sc = (SpreadsheetCell) components[i - 1];
                            et = sc.getDataView().getEdTracker();
                            ec = et.findEditor(absolutePos);
                            et.setEditor(ec);

                            a = (JTextArea) ec.getParentComponent();

                            // Determine the line start and end points.
                            int lastLine = (a.getLineCount() - 1);
                            int lineEnd = a.getLineEndOffset(lastLine);
                            int lineStart = a.getLineStartOffset(lastLine);

                            // We take either the position or the last element
                            // in the line
                            int newPos = Math.min(relativePos + lineStart,
                                    lineEnd);

                            // Set the caret position in the newly focused
                            // editor.
                            ec.setCaretPosition(newPos);
                            sc.requestFocus();
                            sc.setHighlighted(true);
                            cellSelectionL.setHighlightedCell(sc);

                            e.consume();

                            return true;
                        }
                    } catch (BadLocationException be) {
                        LOGGER.error("BadLocation on arrow up", be);
                    }
                }

                // The key stroke is down - select the editor component in the
                // cell below, setting the caret position to what we found from
                // the current cell.
                if ((e.getKeyCode() == KeyEvent.VK_DOWN)
                        && ((i + 1) < numCells)) {

                    try {

                        // Determine if we are at the bottom of a multi-lined
                        // cell, if we are not on the bottom line - pressing
                        // down should select the line below.
                        JTextArea a = (JTextArea) ec.getParentComponent();

                        if ((a.getLineOfOffset(a.getCaretPosition()) + 1)
                                >= a.getLineCount()) {
                            components[i + 1].requestFocus();

                            if (components[i + 1] instanceof SpreadsheetCell) {
                                sc = (SpreadsheetCell) components[i + 1];

                                et = sc.getDataView().getEdTracker();
                                ec = et.findEditor(absolutePos);
                                et.setEditor(ec);
                                ec.setCaretPosition(relativePos);
                                sc.setHighlighted(true);
                                cellSelectionL.setHighlightedCell(sc);
                            } else {
                                sc = (SpreadsheetCell) components[i];
                                sc.setHighlighted(false);
                                cellSelectionL.clearCellSelection();
                            }

                            e.consume();

                            return true;
                        }
                    } catch (BadLocationException be) {
                        LOGGER.error("BadLocation on arrow down", be);
                    }
                }

                return false;
            }
        }

        return false;
    }
}
