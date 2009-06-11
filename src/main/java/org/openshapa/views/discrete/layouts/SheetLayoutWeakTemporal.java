package org.openshapa.views.discrete.layouts;

import org.openshapa.db.SystemErrorException;
import org.openshapa.views.discrete.SpreadsheetCell;
import org.openshapa.views.discrete.SpreadsheetColumn;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * SheetLayoutWeakTemporal - implements the weak temporal ordering style
 * from original MacSHAPA.
 */
public class SheetLayoutWeakTemporal extends SheetLayout {

    /** Logger for this class. */
    private static Logger logger =
                                Logger.getLogger(SheetLayoutWeakTemporal.class);

    /** Temporal ordering information for each column in the layout. */
    private Vector<ColumnTemporalInfo> colsInfo;

    /** Gap in pixels between non-adjacent cells on spreadsheet. */
    private static final int GAP = 3;

    /**
     * Only one of these objects is declared.  It exists to gather together
     * related information for the temporal ordering algorithm. It can be saved
     * and used across multiple calls to doUpdateTemporal to allow for
     * incremental ordering of the spreadsheet.
     * Currently the algorithm reorders all cells in one go.
     */
    private final class TemporalInfo {
        /** time being processed this iteration. */
        private long time;
        /** Vertical position in JPanel pixels reached so far. */
        private int vPos;
        /** SpreadsheetColumns with onsets that need processing this pass. */
        private Vector<ColumnTemporalInfo> onsets;
        /** SpreadsheetColumns with offsets that need processing this pass. */
        private Vector<ColumnTemporalInfo> offsets;

        /** TemporalInfo constructor. */
        private TemporalInfo() {
            time = 0;
            vPos = 0;
            onsets = new Vector<ColumnTemporalInfo>();
            offsets = new Vector<ColumnTemporalInfo>();
        }

        /** Reset the TemporalInfo contents. */
        private void reset() {
            onsets.clear();
            offsets.clear();
        }
    }

    /**
     * Stores information for each column in the spreadsheet to
     * calculate the weak temporal layout positions of the cells.
     */
    private final class ColumnTemporalInfo {

        /** Reference to the SpreadsheetColumn. */
        private SpreadsheetColumn col;

        /** Index into the cells vector used by temporal ordering algorithm. */
        private int cellsPos;

        /** Temporal ordering - reference to cell currently ordered up to. */
        private SpreadsheetCell curr = null;

        /** Temporal ordering - reference to cell previous to curr. */
        private SpreadsheetCell prev = null;

        /** Temporal ordering - Virtual offset currently ordered up to. */
        private long virtOffset;

        /**
         * ColumnTemporalInfo constructor.
         * @param column Reference to the SpreadsheetColumn.
         */
        private ColumnTemporalInfo(final SpreadsheetColumn column) {
            col = column;
        }

        /**
         * Reset temporal ordering fields to initial values.
         */
        private void resetToFirstCell() {
            prev = null;
            curr = null;
            virtOffset = 0;
            cellsPos = 0;
        }

        /**
         * @param pos Position of cell  to retrieve.
         * @return SpreadsheetCell at position pos in the cells list.
         * Can return null if pos is out of bounds.
         */
        private SpreadsheetCell getSpreadsheetCellByPos(final int pos) {
            if (pos < 0) {
                return null;
            }
            if (pos < col.getCells().size()) {
                return col.getCells().get(pos);
            }
            return null;
        }

        /**
         * Get the information ready for this column for the temporal ordering
         * algorithm to use. Calculates the current and previous cells in the
         * process and the virtual offset of the current cell. Also marks the
         * current cell unprocessed.
         * @throws SystemErrorException if a problem occurs.
         */
        private void loadVarCellTemporal() throws SystemErrorException {

            prev = getSpreadsheetCellByPos(cellsPos - 1);
            curr = getSpreadsheetCellByPos(cellsPos);
            cellsPos += 1;

            if (curr != null) {
                curr.setOnsetProcessed(false);

                /* Calculate the virtual offset.
                 * If there is a next cell, then
                 *   virtOffset = Max (cur.onset, Min (cur.offset, next.onset))
                 * else
                 *   virtOffset = Max (cur.onset, cur.offset)
                 */
                SpreadsheetCell next = getSpreadsheetCellByPos(cellsPos);
                virtOffset = curr.getOffsetTicks();
                if (next != null && next.getOnsetTicks() < virtOffset) {
                    virtOffset = next.getOnsetTicks();
                }
                if (curr.getOnsetTicks() > virtOffset) {
                    virtOffset = curr.getOnsetTicks();
                }
            }
        }

        /**
         * @return Current cell to order in the temporal ordering algorithm.
         * Can return null if the column has no cells or all cells in the column
         * are now ordered.
         */
        private SpreadsheetCell getCurrCell() {
            return curr;
        }

        /**
         * @return Previous cell ordered in the temporal ordering algorithm.
         * Can return null if column has no cells or at the start of layout.
         */
        private SpreadsheetCell getPrevCell() {
            return prev;
        }

        /**
         * @return the virtual offset we are processing in the temporal ordering
         * algorithm.
         */
        private long getVirtOffset() {
            return virtOffset;
        }
    }

    /**
     * SheetLayoutWeakTemporal constructor.
     * @param cols Reference to the SpreadsheetColumns in the spreadsheet.
     */
    public SheetLayoutWeakTemporal(final Vector<SpreadsheetColumn> cols) {
        setColumns(cols);
        colsInfo = new Vector<ColumnTemporalInfo>();
    }

    /**
     * Recalculate positions of all the cells in the spreadsheet.
     */
    public final void relayoutCells() {
        for (SpreadsheetColumn col : getColumns()) {
            for (SpreadsheetCell cell : col.getCells()) {
                cell.setOnsetvGap(0);
                cell.setLayoutPreferredHeight(0);
            }
        }

        try {
            doUpdateTemporalWeak();
        } catch (SystemErrorException e) {
            logger.error("Failed to relayout cells.", e);
        }
    }

    /**
     * loadInitialCells
     * Setup the initial temporal ordering data from the SpreadsheetColumns.
     * @throws org.openshapa.db.SystemErrorException
     */
    private void loadInitialCells() throws SystemErrorException {
        for (SpreadsheetColumn col : getColumns()) {
            ColumnTemporalInfo colinfo = new ColumnTemporalInfo(col);
            colsInfo.add(colinfo);
        }
        for (ColumnTemporalInfo colInfo : colsInfo) {
            colInfo.resetToFirstCell();
            colInfo.loadVarCellTemporal();
        }
    }


    /**
     * Find the next time to be processed in the temporal array. This is
     * the smallest onset time of a current varcell, if the onset has not
     * been processed, or the smallest offset.
     * If none is found, return Long.MAX_VALUE in info.time field.
     *
     * This also creates lists of those cells which have onset or
     * offset equal to the returned time. These are the cells that
     * will be updated in this iteration. The cells whose onsets
     * are equal to the time are in the onsets list. The cells whose
     * offsets are equal to the time are in the offsets list.
     * @param info Temporal ordering information.
     * @throws SystemErrorException if a problem occurs.
     */
    private void findNextTimeTemporal(TemporalInfo info)
    throws SystemErrorException {
        // For each column, pick out the earliest offset or unprocessed onset.
        long time = Long.MAX_VALUE;
        for (ColumnTemporalInfo colInfo : colsInfo) {
            SpreadsheetCell curr = colInfo.getCurrCell();
            if (curr != null) {
                if (!curr.isOnsetProcessed()) {
                    // The cells vPos (position of top) is not yet set.
                    if (time > curr.getOnsetTicks()) {
                        time = curr.getOnsetTicks();
                        info.onsets.clear();
                        info.onsets.add(colInfo);
                        info.offsets.clear();
                    } else if (time == curr.getOnsetTicks()) {
                        info.onsets.add(colInfo);
                    }
                } else {
                    // The cells vPos is set and its vExtent isn't.
                    long offset = colInfo.getVirtOffset();
                    if (time > offset) {
                        time = offset;
                        info.onsets.clear();
                        info.offsets.clear();
                        info.offsets.add(colInfo);
                    } else if (time == offset) {
                        info.offsets.add(colInfo);
                    }
                }
            }
        }
        info.time = time;
    }

    /**
     * Calculate the next vPos that needs to be considered in the onset cells.
     * Leave it in info.vPos.
     * @param info Temporal ordering information.
     * @throws SystemErrorException if a problem occurs.
     */
    private void calcOnsetVPos(TemporalInfo info) throws SystemErrorException {
        int nextVPos;
        int vPos = info.vPos;
        for (ColumnTemporalInfo colInfo : info.onsets) {
            SpreadsheetCell prev = colInfo.getPrevCell();
            if (prev != null) {
                nextVPos = prev.getLayoutPreferredY()
                                            + prev.getLayoutPreferredHeight();

                if (prev.getOffsetTicks()
                        < colInfo.getCurrCell().getOnsetTicks() - 1) {
                    nextVPos += GAP;
                }
                if (nextVPos > vPos) {
                    vPos = nextVPos;
                }
            }
        }
        info.vPos = vPos;
    }

    /**
     * Set the vPos for the next onset cells.
     * @param info Temporal ordering information.
     * @throws SystemErrorException if a problem occurs.
     */
    private void setOnsetVPos(TemporalInfo info) throws SystemErrorException {
        for (ColumnTemporalInfo colInfo : info.onsets) {
            SpreadsheetCell curr = colInfo.getCurrCell();
            if (curr != null) {
                // Set the vpos of the currentcell by setting the strut height
                // between it and the previous cell
                SpreadsheetCell prev = colInfo.getPrevCell();
                curr.setLayoutPreferredY(info.vPos, prev);
            }
        }
    }

    /**
     * Calculate the next vPos that needs to be considered in the offset cells.
     * Leave it in info.vPos.
     * @param info Temporal ordering information.
     * @throws SystemErrorException if a problem occurs.
     */
    private void calcOffsetVPos(TemporalInfo info) throws SystemErrorException {
        int nextVPos;
        int vPos = info.vPos;
        /* Next vPos is the greatest vPos for any offset column. */
        for (ColumnTemporalInfo colInfo : info.offsets) {
            SpreadsheetCell curr = colInfo.getCurrCell();
            if (curr != null) {
                nextVPos = curr.getLayoutPreferredY()
                                                    + curr.getPreferredHeight();
                if (nextVPos > vPos) {
                    vPos = nextVPos;
                }
            }
        }
        info.vPos = vPos;
    }

    /**
     * Set the vPos for the next offset cells.
     * @param info Temporal ordering information.
     * @throws SystemErrorException if a problem occurs.
     */
    private void setOffsetVPos(TemporalInfo info) throws SystemErrorException {
        long oldOffset;
        for (ColumnTemporalInfo colInfo : info.offsets) {
            SpreadsheetCell curr = colInfo.getCurrCell();
            if (curr != null) {
                curr.setLayoutPreferredHeight(info.vPos
                                                - curr.getLayoutPreferredY());

                oldOffset = colInfo.getVirtOffset();
                colInfo.loadVarCellTemporal();

                // If new cell in column has same onset as old cells virtual
                // offset, then it should begin where the old cell leaves off.
                // loadVarCellTemporal() leaves the vPos of the current cell
                // in this column as unprocessed. Setting it here means that
                // the onset is considered processed and temporal ordering
                // goes right ahead and deals with its offset.
                // This tweak is needed to handle temporal ordering which
                // does not start with the first cell in each column.
                curr = colInfo.getCurrCell();
                if (curr != null && curr.getOnsetTicks() == oldOffset) {
                    curr.setLayoutPreferredY(info.vPos);
                }
            }
        }
    }

    /**
     * Update the positions of the cells in weak temporal layout.
     * @throws SystemErrorException if a problem occurs.
     */
    private void doUpdateTemporalWeak() throws SystemErrorException {
        TemporalInfo info = new TemporalInfo();
        long lastTime;

        // Setup ordering info.
        info.reset();
        loadInitialCells(); // setup SpreadsheetColumns kickoff info
        findNextTimeTemporal(info); // set info.time
        info.vPos = 0;

        // The main temporal updating loop. It calls FindNextTimeTemporal() to
        // find the next timestamp to be processed, the next onset or virtual
        // offset in any variable. Then it sets the vertical position or the
        // vertical extent of cells that begin or end at that time.

        while (info.time != Long.MAX_VALUE) {
            if (info.onsets.size() > 0 && info.offsets.size() > 0) {
                // If both cell onsets and cell offsets are to be set in this
                // iteration, then set them together. The times are the same,
                // so the positions should be the same, and we must call both
                // CalcOnsetVPos() and CalcOffsetVPos() before we know what
                // the position should be.
                calcOnsetVPos(info);
                calcOffsetVPos(info);
                setOnsetVPos(info);
                setOffsetVPos(info);
            } else {
                if (info.onsets.size() > 0) {
                    calcOnsetVPos (info);
                    setOnsetVPos (info);
                }
                if (info.offsets.size() > 0) {
                    calcOffsetVPos (info);
                    setOffsetVPos (info);
                }
            }
            lastTime = info.time;
            findNextTimeTemporal (info);

            // Leave a slight gap if the time differs more than one tick.
            // Times 1 tick apart can follow one another without a break.
            if (info.time > lastTime + 1) {
                info.vPos += GAP;
            }

            // If the time decreased, there's a bug.
            if (info.time < lastTime) {
                logger.warn("DoUpdateTemporal - time < lastTime");
                break; // leave the loop
            }
        }

        // Clean things up.
        info.reset();
        colsInfo.clear();
    }
}
