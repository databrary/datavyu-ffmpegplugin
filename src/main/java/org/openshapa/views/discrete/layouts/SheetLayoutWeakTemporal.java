package org.openshapa.views.discrete.layouts;

import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JScrollPane;
import org.openshapa.views.discrete.SpreadsheetCell;
import org.openshapa.views.discrete.SpreadsheetColumn;
import org.openshapa.views.discrete.SpreadsheetView;

/**
 * SheetLayoutWeakTemporal - mimics the weak temporal ordering style from
 * original MacSHAPA.
 */
public class SheetLayoutWeakTemporal extends SheetLayout {
    // The of the right hand margin.
    int marginSize;

    // The total number of cells that we have laid.
    int laidCells;

    // The maximum height of the layout in pixels.
    int maxHeight;

    // The temporal ratio/scale we are using to display cells.
    double ratio;

    /**
     * Information on each element in the row we are currently processing.
     */ 
    private class RowInfo {
        public RowInfo(SpreadsheetCell nCell, List<SpreadsheetCell> oCells, SpreadsheetColumn nCol) {
            cell = nCell;
            col = nCol;
            overlappingCells = oCells;
        }

        // The cell that this row information is about.
        public SpreadsheetCell cell;
        
        // The cells that overlap the above cell.
        public List<SpreadsheetCell> overlappingCells;

        // The column that the above cell belongs too.
        public SpreadsheetColumn col;
    }

    /**
     * SheetLayoutOrdinal constructor.
     *
     * @param margin The size of the margin used for this layout.
     */
    public SheetLayoutWeakTemporal(final int margin) {
        marginSize = margin;
        laidCells = 0;
    }

    @Override
    public void layoutContainer(Container parent) {
        super.layoutContainer(parent);

// TODO: DEBUGGING REMOVE
//        System.err.println("================================ Laying container");

        // This layout must be applied to a Spreadsheet panel.
        JScrollPane pane = (JScrollPane) parent;
        SpreadsheetView mainView = (SpreadsheetView) pane.getViewport()
                                                         .getView();

        laidCells = 0;
        maxHeight = parent.getHeight();

        // Determine the ratio/scale to use with temporal ordering, we pick the
        // the largest column size in pixels and temporal length in ticks to
        // use as an aggressive ratio/scale to fit as many cells on the screen
        // as possible.
        long ratioHeight = 0;   // The height required to fit all the visible cells on the spreadsheet without any spacing.
        long ratioTicks = 0;    // The maximum offset used in the entire visible spreadsheet.
        int totalCells = 0;     // The total number of visible cells we are laying out on the spreadsheet.
        ratio = 1.0;            // The ratio / scale that we are using to position the cells temporally.

        for (SpreadsheetColumn c : mainView.getColumns()) {
            // We only work with visible columns.
            if (c.isVisible()) {
                // Take this opportunity to initalise the working data we need for each of the columns.
                c.setWorkingHeight(0);
                c.setWorkingOrd(0);
                c.setWorkingOnsetPadding(0);
                c.setWorkingOffsetPadding(0);

                // Determine the total height in pixels of the cells in this column.
                List<SpreadsheetCell> colCells = c.getCellsTemporally();
                int colHeight = 0;
                for (SpreadsheetCell cell : colCells) {
                    colHeight += cell.getPreferredSize().height;
                }

                // Determine the maximum column height in pixels.
                ratioHeight = Math.max(ratioHeight, colHeight);

                // Determine the temporal length of the column in ticks.
                if (colCells.size() > 0) {
                    SpreadsheetCell lastCell = colCells.get((colCells.size() - 1));

                    // If the offset is zero or smaller, we need to consider the
                    // onset of the cell instead for determining the maximum offset
                    // required to display the entire spreadsheet.
                    if (lastCell.getOffsetTicks() <= 0) {
                        ratioTicks = Math.max(ratioTicks, lastCell.getOnsetTicks());
                    } else {
                        ratioTicks = Math.max(ratioTicks, lastCell.getOffsetTicks());
                    }
                }
                totalCells = totalCells + colCells.size();
            }
        }
        // Determine the final temporal ratio/scale we are going to use for the spreadsheet.
        if (ratioTicks > 0) {
            ratio = ratioHeight / (float) ratioTicks;
        }

        // Untill we have laid all the cells, we position each of them
        // temporarily. We do this row by row with the cells sorted temporarily,
        // untill we have no cells left to lay.
        int pad = 0;    // The cumulative padding we need to apply to cell positioning - this is the total
                        // amount of extra vertical space we have had to pad out, to make entire cells visible.

        while (laidCells < totalCells) {
            RowInfo previous = null;
            List<RowInfo> rowCells = new ArrayList<RowInfo>(mainView.getColumns().size());
            LinkedList<RowInfo> overlappingRowCells = new LinkedList<RowInfo>();

            // Get the cells for the row we are laying.
            for (SpreadsheetColumn col : mainView.getColumns()) {

                // We only work with visible columns.
                if (col.isVisible()) {
                    SpreadsheetCell cell = col.getWorkingTemporalCell();

                    if (cell != null) {
                        List<SpreadsheetCell> overlapping = col.getOverlappingCells();
                        rowCells.add(new RowInfo(cell, overlapping, col));
                    }
                }
            }
            // We work with the row is sorted by onset and offset.
            rowCells = sortRow(rowCells);

// TODO: DEBUGGING REMOVE
//            System.err.println("************** Laying Row");

            // For each cell in the row, we are working our way from the cells
            // with the lowest onset and offset positioning as we go. Adding to
            // the culmative padding when we are unable to fit the cell using
            // the current temporal ratio / scale.
            for (int i = 0; i < rowCells.size(); i++) {
                RowInfo ri = rowCells.get(i);

                // If we have already positioned a cell in this row already, we
                // might need to apply some padding as required.
                if (previous != null &&
                    ri.cell.getOnsetTicks() >= previous.cell.getOffsetTicks() &&
                    ri.cell.getOnsetTicks() != previous.cell.getOnsetTicks()) {
                    ri.col.setWorkingOnsetPadding(previous.col.getWorkingOffsetPadding());
                }
                
                // Determine the top (t) and bottom (b) position of the cell based
                // on the current temporal ratio/scale of the spreadsheet.
                int t = ri.cell.getTemporalTop(ratio) + ri.col.getWorkingOnsetPadding();
                int b = ri.cell.getTemporalBottom(ratio) + ri.col.getWorkingOffsetPadding();

                // Make sure the total size (ts) of the cell is at least zero.
                int ts = 0;
                if ((b - t) < 0) {
                    b = t;
                } else {
                    ts = (b - t);
                }

                // The size of the cell must be at least the preffered size in height.
                pad = Math.max(pad, (ri.cell.getPreferredSize().height - ts));
                if (ri.cell.getPreferredSize().height > ts) {
                    ri.col.setWorkingOffsetPadding(ri.col.getWorkingOffsetPadding() + pad);
                    b += pad;
                }

                // Position the cell in the column.
                ri.cell.setBounds(0, t, (ri.col.getWidth() - marginSize), (b - t));
                ri.col.setWorkingHeight(b);
                
// TODO: DEBUGGING REMOMVE
//                System.err.println("C[" + ri.cell.getOnsetTicks() + ":" + ri.cell.getY() + ", " + ri.cell.getOffsetTicks() + ":" + ri.cell.getHeight() + "] - P: " + pad + " - " + ri.cell.getDataView().getText());
//                System.err.println("rowCell: [" + i + ", " + ri.overlappingCells.size() + "]");
                
                // Remove stuff from the front of the list if we are not overlapping anymore.
                while (!overlappingRowCells.isEmpty() &&
                       overlappingRowCells.getFirst().cell.getOffsetTicks() < ri.cell.getOnsetTicks() &&
                       overlappingRowCells.getFirst().cell.getOnsetTicks() < ri.cell.getOnsetTicks()) {

                    RowInfo completed = overlappingRowCells.removeFirst();
                    markCellAsCompleted(completed);

// TODO: DEBUGGING REMOVE
//                    System.err.println("Completed lapping[" + laidCells + "] - " + completed.cell.getDataView().getText());
                }

                // If we have already positioned a cell in this row - we need to
                // check for any overlapping cells, so that we can aggregate the
                // padding.
                if (previous != null) {
                    // If current cell overlaps the previous cell - add the previous cell to our
                    // list of overlapping cells.
                    if (previous.cell.getOffsetTicks() > ri.cell.getOnsetTicks() || 
                        previous.cell.getOnsetTicks() > ri.cell.getOnsetTicks()) {
                        overlappingRowCells.add(previous);

// TODO: DEBUGGING REMOVE
//                        System.err.println("Adding overlapping cell - " + previous.cell.getDataView().getText());

                    // Cell doesn't overlap - we have finished laying this cell - mark it as completed.
                    } else {
                        markCellAsCompleted(previous);

// TODO: DEBUGGING REMOVE
//                        System.err.println("Completed:[" + laidCells + "] - " + previous.cell.getDataView().getText());
                    }
                }

                // The last cell in the row never overlaps - and is always completed.
                if (i == (rowCells.size() - 1)) {
                    markCellAsCompleted(ri);

// TODO: DEBUGGING REMOVE
//                    System.err.println("last:[" + laidCells + "] - " + ri.cell.getDataView().getText());
                }

                // Iterate over everything in our overlapping list and accumulate the latest padding.
                // Into the overlapping list. Cells in the overlapping list still might not be completed,
                // we simply make sure that the padding is up to date, and check in other places if we
                // have finished the cell.
                for (RowInfo overlappingRI : overlappingRowCells) {
                    overlappingRI.col.setWorkingOffsetPadding(ri.col.getWorkingOffsetPadding());
                    t = overlappingRI.cell.getTemporalTop(ratio) + overlappingRI.col.getWorkingOnsetPadding();
                    b = overlappingRI.cell.getTemporalBottom(ratio) + overlappingRI.col.getWorkingOffsetPadding();
                    overlappingRI.cell.setBounds(0, t, (ri.col.getWidth() - marginSize), (b - t));
                    overlappingRI.col.setWorkingHeight(b);

// TODO: DEBUGGING REMOVE
//                    System.err.println("Flaying: [" + t + "," + (b - t)+ "] - " + overlappingRI.cell.getDataView().getText());
                }

                previous = ri;
            }
        }

        // Pad the columns so that they are all the same length.
        padColumns(mainView, parent);
    }

    /**
     * Marks the cell in the current row info as completed.
     *
     * @param ri The row information containing the cell to mark as completed.
     */
    private void markCellAsCompleted(final RowInfo ri) {
        ri.col.setWorkingOnsetPadding(ri.col.getWorkingOffsetPadding());
        ri.col.setWorkingOrd(ri.col.getWorkingOrd() + 1);
        maxHeight = Math.max((ri.cell.getY() + ri.cell.getHeight()), maxHeight);
        ri.cell.setOrdinal(ri.col.getWorkingOrd());
        laidCells++;

        for (SpreadsheetCell overlappingCell : ri.overlappingCells) {
            laidCells++;
        }        
    }

    /**
     * Performs a bubble sort of a row of cells by onset and offset. The collection
     * of cells will be ordered by onset, if the onsets match, they will then be
     * ordered by offset, with cells having the greatest offset coming before cells
     * with the lowest offset.
     *
     * @param row The row of cells to be ordered by onset and offset.
     *
     * @return A row of cells ordered by onset and offset.
     */
    private List<RowInfo> sortRow(List<RowInfo> row) {
        boolean sorted;
        do {
            sorted = true;

            for (int i = 0; i < (row.size() - 1); i++) {
                RowInfo a = row.get(i);
                RowInfo b = row.get(i + 1);

                if (a.cell.getOnsetTicks() > b.cell.getOnsetTicks()) {
                    sorted = false;
                    row.set(i, b);
                    row.set(i + 1, a);
                } else if ((a.cell.getOnsetTicks() == b.cell.getOnsetTicks()) && (b.cell.getOffsetTicks() > a.cell.getOffsetTicks())) {
                    sorted = false;
                    row.set(i, b);
                    row.set(i + 1, a);
                }
            }
        } while (!sorted);

        return row;
    }

    /**
     * Pads all the columns so that they all fill out to the maximum height of
     * the spreadsheet. This function also ensures that the new cell button is
     * added to the bottom of each of the columns.
     *
     * @param mainView The actual spreadsheet that we are populating.
     * @param parent The parent container that holds the spreadsheet.
     */
    private void padColumns(SpreadsheetView mainView, Container parent) {
        for (SpreadsheetColumn col : mainView.getColumns()) {
            Integer colHeight = col.getWorkingHeight();

            // Put the new cell button at the end of the column.
            Dimension d = col.getDataPanel().getNewCellButton().getPreferredSize();
            col.getDataPanel().getNewCellButton().setBounds(0,
                                                            colHeight,
                                                            parent.getWidth(),
                                                            (int) d.getHeight());
            colHeight += (int) d.getHeight();

            col.getDataPanel().setHeight(maxHeight + (int) d.getHeight());
            col.getDataPanel().getPadding().setBounds(0,
                                                      colHeight,
                                                      col.getWidth(),
                                                      (maxHeight - colHeight));
        }
    }
}
