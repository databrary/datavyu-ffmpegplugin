package org.openshapa.views.discrete.layouts;

import java.util.List;
import org.openshapa.views.discrete.SpreadsheetColumn;

/**
 * SheetLayout - abstract class for spreadsheet layouts.
 */
public abstract class SheetLayout {

    /** Vector of the Spreadsheetcolumns in the spreadsheet. */
    private List<SpreadsheetColumn> columns;

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
    protected void setColumns(List<SpreadsheetColumn> cols) {
        columns = cols;
    }

    /**
     * @return SpreadsheetColumns in the spreadsheet.
     */
    protected List<SpreadsheetColumn> getColumns() {
        return columns;
    }

}
