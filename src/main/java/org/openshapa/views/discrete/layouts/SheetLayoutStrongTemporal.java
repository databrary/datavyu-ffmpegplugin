package org.openshapa.views.discrete.layouts;

import org.openshapa.db.SystemErrorException;
import org.openshapa.util.Constants;
import org.openshapa.views.discrete.SpreadsheetCell;
import org.openshapa.views.discrete.SpreadsheetColumn;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * SheetLayoutStrongTemporal implements the strong temporal style layout of
 * SpreadsheetCells in the spreadsheet.
 *
 * TODO: Work out the interface to allow user to set the scale. Current
 * approach just checks the range of cells in seconds and tries to fit it
 * to somewhere between 1000 and 5000 pixels of scrolling on screen.
 */
public class SheetLayoutStrongTemporal extends SheetLayout {

    /** Minimum time range in seconds for scale factor calculation. */
    private static final int MIN_RANGE_SECONDS = 600;
    /** Maximum time range in seconds for scale factor calculation. */
    private static final int MAX_RANGE_SECONDS = 10000;
    /** Minimum pixels to scale to. */
    private static final int MIN_SCROLL_PIXELS = 2000;
    /** Maximum pixels to scale to. */
    private static final int MAX_SCROLL_PIXELS = 10000;

    /** Logger for this class. */
    private static Logger logger =
                              Logger.getLogger(SheetLayoutStrongTemporal.class);

    /**
     * SheetLayoutStrongTemporal constructor.
     * @param cols Reference to the SpreadsheetColumns in the spreadsheet.
     */
    public SheetLayoutStrongTemporal(final Vector<SpreadsheetColumn> cols) {
        setColumns(cols);
    }

    /**
     * Recalculate positions of all the cells in the spreadsheet.
     */
    public final void relayoutCells() {
        try {
            long[] minmax = {Long.MAX_VALUE, Long.valueOf(0)};
            for (SpreadsheetColumn col : getColumns()) {
                CalculateMaxMins(col, minmax);
            }
            double pixPerTick = calcPixPerTick(minmax[0], minmax[1]);
            for (SpreadsheetColumn col : getColumns()) {
                layoutColumnCells(col, minmax[0], pixPerTick);
            }
        } catch (SystemErrorException e) {
            logger.error("Failed to relayout cells.", e);
        }
    }

    /**
     * Calculate the maximum and minimum times represented by this column.
     * @param col SpreadsheetColumn to use.
     * @param minmax Array of longs passed in to hold the result min and max.
     * @throws SystemErrorException if a problem occurs.
     */
    private void CalculateMaxMins(final SpreadsheetColumn col,
                             final long[] minmax) throws SystemErrorException {
        for (SpreadsheetCell cell : col.getCells()) {
            if (minmax[0] > cell.getOnsetTicks()) {
                minmax[0] = cell.getOnsetTicks();
            }
            if (minmax[0] > cell.getOffsetTicks()) {
                minmax[0] = cell.getOffsetTicks();
            }
            if (minmax[1] < cell.getOnsetTicks()) {
                minmax[1] = cell.getOnsetTicks();
            }
            if (minmax[1] < cell.getOffsetTicks()) {
                minmax[1] = cell.getOffsetTicks();
            }
        }
    }

    /**
     * Calculates a scale factor to use in laying out the cells.
     * Simple idea to check the range of cells in seconds and fit to
     * between MIN_SCROLL_PIXELS and MAX_SCROLL_PIXELS pixels on screen.
     * @param minOnset Minimum time value of the cells to be layed out.
     * @param maxOffset Maximum time value of the cells to be layed out.
     * @return The pixels per tick scale factor.
     */
    private double calcPixPerTick(final long minOnset, final long maxOffset) {
        double pixPerTick;
        long seconds = Math.round((maxOffset - minOnset) /
                                  Constants.TICKS_PER_SECOND);
        if (seconds < MIN_RANGE_SECONDS) {
            // small range - scale to fit in a fixed min number of pixels
            pixPerTick = 1.0 * MIN_SCROLL_PIXELS / (maxOffset - minOnset);
        } else if (seconds < MAX_RANGE_SECONDS) {
            // medium sized range - scale to 1 second per 10 pixels
            pixPerTick = 10.0 / Constants.TICKS_PER_SECOND;
        } else {
            // large range - scale to fit in a fixed max number of pixels
            pixPerTick = 1.0 * MAX_SCROLL_PIXELS / (maxOffset - minOnset);
        }
        return pixPerTick;
    }

    /**
     * Layout the cells of this column in the Strong Temporal style.
     * @param minOnset Minimum onset value for range calculation.
     * @param maxOffset Maximum offset value for range calculation.
     * @throws SystemErrorException if a problem occurs.
     */
    private void layoutColumnCells(final SpreadsheetColumn col,
           final long minOnset, double pixPerTick) throws SystemErrorException {

        Long vPos = Long.valueOf(0);
        Long vHeight = Long.valueOf(0);
        SpreadsheetCell prevCell = null;
        int intvPos = 0;
        int intvHeight = 0;
        int prevvPos = 0;
        int prevvHeight = 0;

        // layout the cells.
        for (SpreadsheetCell cell : col.getCells()) {
            vPos = Math.round((cell.getOnsetTicks() - minOnset) * pixPerTick);
            vHeight = Math.round((cell.getOffsetTicks() - minOnset)
                                                          * pixPerTick) - vPos;
            intvPos = vPos.intValue();
            intvHeight = vHeight.intValue();
            if (prevCell != null) {
                if (intvPos < prevvPos + prevvHeight) {
                    // we have overlap - modify size and border
                    prevCell.setBounds(0, prevvPos, col.getWidth() - 1,
                                                       intvPos - prevvPos + 1);
                    prevCell.setOverlapBorder(true);
                } else {
                    prevCell.setOverlapBorder(false);
                }
            }
            cell.setBounds(0, intvPos, col.getWidth() - 1, intvHeight + 1);
            prevCell = cell;
            prevvPos = intvPos;
            prevvHeight = intvHeight;
        }
        col.setBottomBound(vPos.intValue() + vHeight.intValue()
                           + Constants.BOTTOM_MARGIN);
    }
}
