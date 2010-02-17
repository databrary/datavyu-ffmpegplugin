package org.openshapa.views.discrete;

/**
 * A listener for listening to column selection changes.
 */
public interface ColumnSelectionListener {

    /**
     * Adds a column to the selected columns.
     *
     * @param column The cell to add to the selection.
     */
    void addColumnToSelection(SpreadsheetColumn column);

    /**
     * Notifiers the listeners to clear all the currently selected columns.
     */
    void clearColumnSelection();
}
