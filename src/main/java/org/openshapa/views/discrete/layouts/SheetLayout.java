package org.openshapa.views.discrete.layouts;

import java.awt.LayoutManager2;
import java.util.List;
import org.openshapa.views.discrete.SpreadsheetColumn;

/**
 * SheetLayout - abstract class for spreadsheet layouts.
 */
public abstract class SheetLayout implements LayoutManager2 {

    /** The Spreadsheetcolumns in the spreadsheet. */
    private List<SpreadsheetColumn> columns;

    /**
     * SheetLayout constructor.
     */
    protected SheetLayout() {
        columns = null;
    }

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
