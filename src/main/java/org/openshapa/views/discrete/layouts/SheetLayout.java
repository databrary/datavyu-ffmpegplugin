package org.openshapa.views.discrete.layouts;

import org.openshapa.views.discrete.SpreadsheetColumn;
import java.util.Vector;

/**
 * SheetLayout - abstract class for spreadsheet layouts.
 */
public abstract class SheetLayout {

    /** Vector of the Spreadsheetcolumns in the spreadsheet. */
    private Vector<SpreadsheetColumn> columns;

    /**
     * SheetLayout constructor.
     */
    protected SheetLayout() {
        columns = null;
    }

    /**
     * Recalculate positions of all the cells in the spreadsheet.
     */
    public abstract void relayoutCells();

    /**
     * Setup a reference to the SpreadsheetColumns in the spreadsheet.
     */
    protected void setColumns(Vector<SpreadsheetColumn> cols) {
        columns = cols;
    }

    /**
     * @return SpreadsheetColumns in the spreadsheet.
     */
    protected Vector<SpreadsheetColumn> getColumns() {
        return columns;
    }

}
