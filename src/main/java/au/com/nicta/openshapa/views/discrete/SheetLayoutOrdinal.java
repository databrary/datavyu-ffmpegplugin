package au.com.nicta.openshapa.views.discrete;

import java.awt.Dimension;
import java.util.Vector;

/**
 * SheetLayoutOrdinal implements the ordinal style layout of SpreadsheetCells
 * in the spreadsheet.
 * @author swhitcher
 */
public class SheetLayoutOrdinal extends SheetLayout {

    /**
     * SheetLayoutOrdinal constructor.
     * @param cols Reference to the SpreadsheetColumns in the spreadsheet.
     */
    public SheetLayoutOrdinal(final Vector<SpreadsheetColumn> cols) {
        setColumns(cols);
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
        int vPos = 0;
        for (SpreadsheetCell cell : col.getCells()) {
            Dimension dim = cell.getPreferredSize();
            cell.setBounds(0, vPos, col.getWidth() - 1, dim.height + 1);
            vPos += dim.height;
        }
        col.setBottomBound(vPos + 1);
    }
}
