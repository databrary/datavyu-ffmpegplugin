package org.openshapa.views.discrete.layouts;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;
import java.util.List;

import org.openshapa.views.discrete.SpreadsheetColumn;

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

    }

    /**
     * Recalculate positions of all the cells in the spreadsheet.
     */
    @Override public final void relayoutCells() {
       
    }
}
