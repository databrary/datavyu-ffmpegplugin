package org.openshapa.views.discrete.layouts;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;
import java.util.List;
import org.openshapa.views.discrete.SpreadsheetCell;

import org.openshapa.views.discrete.SpreadsheetColumn;
import org.openshapa.views.discrete.layouts.SheetLayoutFactory.SheetLayoutType;

/**
 * SheetLayoutWeakTemporal - mimics the weak temporal ordering style from
 * original MacSHAPA.
 */
public class SheetLayoutWeakTemporal extends SheetLayout {

    /** The logger for this class. */
    private Logger logger = UserMetrix.getLogger(SheetLayoutWeakTemporal.class);

    /** Gap in pixels between non-adjacent cells on spreadsheet. */
    private static final int GAP = 3;

    /**
     * SheetLayoutWeakTemporal constructor.
     * @param cols Reference to the SpreadsheetColumns in the spreadsheet.
     */
    public SheetLayoutWeakTemporal(final List<SpreadsheetColumn> cols) {
        setColumns(cols);

        for (SpreadsheetColumn col : cols) {
            col.resetLayoutManager(SheetLayoutType.WeakTemporal);
        }
    }

    /**
     * Recalculate positions of all the cells in the spreadsheet.
     */
    @Override public final void relayoutCells() {
        for (SpreadsheetColumn col : getColumns()) {
            int currentHeight = 0;

            for (SpreadsheetCell cell : col.getCellsTemporally()) {
                cell.setBounds(0, currentHeight, cell.getWidth(), cell.getHeight());
                currentHeight += cell.getHeight();
            }
        }
    }
}
