package org.openshapa.views.discrete;

/**
 * A listener for listening to cell selection changes.
 */
public interface CellSelectionListener {

    /**
     * Adds a cell to the selected cells.
     *
     * @param cell The cell to add to the selection.
     */
    void addCellToSelection(SpreadsheetCell cell);

    /**
     * Adds the cell and everything in between to the selected
     * cells.
     *
     * @param cell The end point cell to use for the continous selection.
     */
    void addCellToContinousSelection(SpreadsheetCell cell);

    /**
     * Sets the currently selected cell.
     *
     * @param cell The cell to use as the highlight.
     */
    void setHighlightedCell(SpreadsheetCell cell);

    /**
     * Notifiers the listeners to clear all the currently selected cells.
     */
    void clearCellSelection();
}
