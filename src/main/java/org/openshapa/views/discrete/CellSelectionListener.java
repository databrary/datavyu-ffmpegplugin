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
     * Notifiers the listeners to clear all the currently selected cells.
     */
    void clearCellSelection();
}
