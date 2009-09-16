package org.openshapa.views.discrete;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.KeyEvent;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Box.Filler;
import javax.swing.BoxLayout;
import org.apache.log4j.Logger;
import org.openshapa.db.DataCell;
import org.openshapa.db.DataColumn;
import org.openshapa.db.Database;
import org.openshapa.db.SystemErrorException;
import org.openshapa.util.Constants;
import org.openshapa.views.discrete.layouts.SheetLayoutFactory.SheetLayoutType;

/**
 * ColumnDataPanel panel that contains the SpreadsheetCell panels.
 */
public final class ColumnDataPanel extends SpreadsheetElementPanel {

    /** Width of the column. */
    private int columnWidth;

    /** Provides a strut to leave a gap at the bottom of the panel. */
    private Component bottomStrut;

    /** Layout type for Ordinal and Weak Temporal Ordering. */
    private LayoutManager boxLayout;

    /** Collection of the SpreadsheetCells held in by this data panel. */
    private Vector<SpreadsheetCell> cells;

    /** Selector for cells. */
    private Selector cellSelector;

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(ColumnDataPanel.class);

    /**
     * Creates a new ColumnDataPanel.
     *
     * @param width The width of the new column data panel in pixels.
     * @param model The Data Column that this panel represents.
     * @param parentCellSelector The cell selector to use with cells held in
     * this column data panel.
     */
    public ColumnDataPanel(final int width,
                           final DataColumn model,
                           final Selector parentCellSelector) {
        super();

        // Store member variables.
        columnWidth = width;
        cellSelector = parentCellSelector;
        this.cells = new Vector<SpreadsheetCell>();

        // Create visual container for spreadsheet cells.
        Dimension d = new Dimension(0, Constants.BOTTOM_MARGIN);
        bottomStrut = new Filler(d, d, d);
        boxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(boxLayout);
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
        this.add(bottomStrut, -1);

        // Populate the data column with spreadsheet cells.
        this.buildDataPanelCells(model);
    }

    /**
     * Build the SpreadsheetCells and add to the DataPanel.
     *
     * @param dbColumn DataColumn to display.
     */
    private void buildDataPanelCells(final DataColumn dbColumn) {
        try {
            // traverse and build the cells
            for (int j = 1; j <= dbColumn.getNumCells(); j++) {
                DataCell dc = (DataCell) dbColumn.getDB()
                                    .getCell(dbColumn.getID(), j);

                SpreadsheetCell sc = new SpreadsheetCell(dbColumn.getDB(),
                                                         dc,
                                                         cellSelector);
                // add cell to the JPanel
                this.add(sc);
                // and add it to our reference list
                cells.add(sc);
            }
        } catch (SystemErrorException e) {
           logger.error("Failed to populate Spreadsheet.", e);
        }
    }

    /**
     * Find and delete SpreadsheetCell by its ID.
     *
     * @param cellID ID of cell to find and delete.
     */
    public void deleteCellByID(final long cellID) {
        for (SpreadsheetCell cell : cells) {
            if (cell.getCellID() == cellID) {
                cells.remove(cell);
                this.remove(cell);
                break;
            }
        }
    }

    /**
     * Insert a new SpreadsheetCell given the cells ID.
     *
     * @param db The database holding the cell that is being inserted into this
     * column data panel.
     * @param cellID ID of cell to create and insert.
     */
    public void insertCellByID(final Database db, final long cellID) {
        try {
            DataCell dc = (DataCell) db.getCell(cellID);
            SpreadsheetCell nCell = new SpreadsheetCell(db,
                                                        dc,
                                                        cellSelector);
            Long newOrd = new Long(dc.getOrd());
            if (cells.size() > newOrd.intValue()) {
                cells.insertElementAt(nCell, newOrd.intValue() - 1);
                this.add(nCell, newOrd.intValue() - 1);
            } else {
                cells.add(nCell);
                this.add(nCell);
            }
            nCell.requestFocus();
        } catch (SystemErrorException e) {
            logger.error("Problem inserting a new SpreadsheetCell", e);
        }
    }

    /**
     * resetLayout changes the layout manager depending on the SheetLayoutType.
     * @param type SheetLayoutType
     */
    public void resetLayoutManager(final SheetLayoutType type) {
        if (type != SheetLayoutType.StrongTemporal) {
            setLayout(boxLayout);
            this.setPreferredSize(null);
        } else {
            setLayout(null);
        }
    }

    /**
     * Adds the specified component to this container at the given
     * position.
     * Overridden to keep the bottomStrut as the last component in the column.
     * @param comp Component to add.
     * @return Component added.
     */
    @Override
    public Component add(final Component comp) {
        super.add(comp, getComponentCount() - 1);
        return comp;
    }

    /**
     * Set the width of the SpreadsheetCell.
     * @param width New width of the SpreadsheetCell.
     */
    public void setWidth(final int width) {
        columnWidth = width;
    }

    /**
     * Override Maximum size to fix the width.
     * @return the maximum size of the data column.
     */
    @Override
    public Dimension getMaximumSize() {
        return new Dimension(columnWidth, Short.MAX_VALUE);
    }

    /**
     * Override Minimum size to fix the width.
     * @return the minimum size of the data column.
     */
    @Override
    public Dimension getMinimumSize() {
        return new Dimension(columnWidth, 0);
    }

    /**
     * Override Preferred size to fix the width.
     * @return the preferred size of the data column.
     */
    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        return new Dimension(columnWidth, size.height);
    }

    /**
     * @return The SpreadsheetCells in this column.
     */
    public Vector<SpreadsheetCell> getCells() {
        return cells;
    }

    /**
     * The action to invoke when a key is released on the keyboard.
     *
     * @param ke The key event that triggered this action.
     */
    @Override
    public void keyReleased(KeyEvent ke) {
        Component[] components = this.getComponents();
        int numCells = getComponentCount() - 1;
        for (int i = 0; i < numCells; i++) {
            if (components[i].isFocusOwner()) {
                if (ke.getKeyCode() == KeyEvent.VK_UP && i > 0) {
                    components[i - 1].requestFocus();
                }

                if (ke.getKeyCode() == KeyEvent.VK_DOWN && (i + 1) < numCells) {
                    components[i + 1].requestFocus();
                }
            }


            /*
            if (components[i].isFocusOwner()
                && components[i].getClass().equals(SpreadsheetCell.class)) {
                SpreadsheetCell sc = (SpreadsheetCell) components[i];
                EditorTracker et = sc.getDataView().getEdTracker();
                EditorComponent ec = et.getCurrentEditor();
                int relativePos = et.getCurrentEditor().getCaretPosition();
                int absolutePos = sc.getDataView().getCaretPosition();

                if (ke.getKeyCode() == KeyEvent.VK_UP && i > 0) {
                    try {
                        JTextArea a = (JTextArea) ec.getParentComponent();
                        if (a.getLineOfOffset(a.getCaretPosition()) == 0 && ke.isConsumed()) {
                            components[i - 1].requestFocus();
                            sc = (SpreadsheetCell) components[i - 1];

                            et = sc.getDataView().getEdTracker();
                            ec = et.findEditor(absolutePos);
                            et.setEditor(ec);
                            ec.setCaretPosition(relativePos);
                        }
                    } catch (BadLocationException be) {
                        //logger.warn("BadLocation on down", be);
                        //components[i - 1].requestFocus();
                        //sc = (SpreadsheetCell) components[i - 1];
                    }
                }

                if (ke.getKeyCode() == KeyEvent.VK_DOWN && (i + 1) < numCells) {
                    try {
                        JTextArea a = (JTextArea) ec.getParentComponent();
                        if (a.getLineOfOffset(a.getCaretPosition()) + 1 >= a.getLineCount() && ke.isConsumed()) {
                            components[i + 1].requestFocus();
                            sc = (SpreadsheetCell) components[i + 1];

                            et = sc.getDataView().getEdTracker();
                            ec = et.findEditor(absolutePos);
                            et.setEditor(ec);
                            ec.setCaretPosition(relativePos);
                        }
                    } catch (BadLocationException be) {
                        //logger.warn("BadLocation on up", be);
                        //e.consume();
                    }
                }

                return;
            }*/
        }
    }
}
