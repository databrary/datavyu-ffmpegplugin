package org.openshapa.views.discrete.layouts;

import org.openshapa.views.discrete.SpreadsheetCell;
import org.openshapa.views.discrete.SpreadsheetColumn;
import java.util.Vector;
import org.openshapa.views.discrete.layouts.SheetLayoutFactory.SheetLayoutType;

/**
 * SheetLayoutOrdinal implements the ordinal style layout of SpreadsheetCells
 * in the spreadsheet.
 */
public class SheetLayoutOrdinal extends SheetLayout {

    /**
     * SheetLayoutOrdinal constructor.
     * @param cols Reference to the SpreadsheetColumns in the spreadsheet.
     */
    public SheetLayoutOrdinal(final Vector<SpreadsheetColumn> cols) {
        setColumns(cols);
        for (SpreadsheetColumn col : cols) {
            col.resetLayoutManager(SheetLayoutType.Ordinal);
        }
    }

    /**
     * Recalculate positions of all the cells in the spreadsheet.
     */
    public final void relayoutCells() {
        for (SpreadsheetColumn col : getColumns()) {
            layoutColumnCells(col);
        }
    }

    /**
     * Layout the cells of a column in the Ordinal style.
     * @param col SpreadsheetColumn to use.
     */
    private void layoutColumnCells(final SpreadsheetColumn col) {
        for (SpreadsheetCell cell : col.getCells()) {
            cell.setOnsetvGap(0);
            cell.setLayoutPreferredHeight(0);
        }
    }
}
