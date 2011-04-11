package org.openshapa.views.discrete.layouts;

import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;
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

    // The total number of
    int laidCells;

    // The total number of cells we need to lay.
    //int totalCells;

    int maxHeight;

    private class RowInfo {
        public RowInfo(SpreadsheetCell nCell, SpreadsheetColumn nCol) {
            cell = nCell;
            col = nCol;
        }

        public SpreadsheetCell cell;
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

        // This layout must be applied to a Spreadsheet panel.
        JScrollPane pane = (JScrollPane) parent;
        SpreadsheetView mainView = (SpreadsheetView) pane.getViewport()
                                                         .getView();

        // TEST CODE
        //int rowLock = 300;
        //int rowsCompleted = 0;
        // END TEST CODE.

        laidCells = 0;
        maxHeight = parent.getHeight();

        // Find the largest column size in pixels and temporal length in ticks:
        long ratioHeight = 0;
        long ratioTicks = 0;
        int totalCells = 0;
        for (SpreadsheetColumn c : mainView.getColumns()) {
            // Initalise the working data for the columns.
            c.setWorkingHeight(0);
            c.setWorkingOrd(0);
            c.setWorkingOnsetPadding(0);
            c.setWorkingOffsetPadding(0);

            // Determine the total height in pixels of the cells in this column.
            List<SpreadsheetCell> colCells = c.getCellsTemporally();
            int colHeight = 0;
            for (SpreadsheetCell cell : colCells) {
                colHeight += cell.getPreferredSize().height;

                // TEST CODE.
                cell.setBounds(0, 0, 0, 0);
                // END TEST CODE.
            }

            // Determine the maximum column height in pixels.
            ratioHeight = Math.max(ratioHeight, colHeight);

            // Determine the temporal length of the column in ticks.
            if (colCells.size() > 0) {
                ratioTicks = Math.max(ratioTicks,
                                      colCells.get((colCells.size() - 1)).getOffsetTicks());
            }
            totalCells = totalCells + colCells.size();
        }
        // Determine the ratio / scale to use with temporal ordering - we pick
        // The maximum height in pixels and the maximum length in ticks.
        double ratio = 1.0;
        if (ratioTicks > 0) {
            ratio = ratioHeight / (float) ratioTicks;
        }

        System.err.println("Ratio: " + ratioTicks + " / " + ratioHeight + " = " + ratio);
        System.err.println("Cells to lay: " + totalCells);

        int pad = 0;

        // Position the cells temporally.
        while (laidCells < totalCells) {
            List<RowInfo> rowCells = new ArrayList<RowInfo>(mainView.getColumns().size());

            long maxOffset = 0L;
            int numMaxOffsetCells = 0;

            for (SpreadsheetColumn col : mainView.getColumns()) {
                SpreadsheetCell cell = col.getWorkingTemporalCell();
                if (cell != null) {
                    rowCells.add(new RowInfo(cell, col));

                    if (cell.getOffsetTicks() > maxOffset) {
                        numMaxOffsetCells = 1;
                        maxOffset = cell.getOffsetTicks();
                    } else if (cell.getOffsetTicks() == maxOffset) {
                        numMaxOffsetCells++;
                    }
                }
            }
            rowCells = sortRow(rowCells);

            //System.err.println("\n");
            //System.err.println("**** Laying row[" + rowsCompleted + "]");

            for (int i = 0; i < rowCells.size(); i++) {
                RowInfo ri = rowCells.get(i);

                int t = ri.cell.getTemporalTop(ratio) + ri.col.getWorkingOnsetPadding();
                int b = ri.cell.getTemporalBottom(ratio) + ri.col.getWorkingOffsetPadding();
                int ts = Math.max(b - t, 0);

                // The size of the cell must be at least the preffered size in height.
                pad = Math.max(pad, (ri.cell.getPreferredSize().height - ts));

                if (ri.cell.getPreferredSize().height > ts) {
                    ri.col.setWorkingOffsetPadding(ri.col.getWorkingOffsetPadding() + pad);
                    b += pad;
                }

                //System.err.println("C: [" + t + ", " + b + ", " + ts + ", " + pad + "," + ri.cell.getPreferredSize().height + "]: " + ri.cell.getDataView().getText());

                // Position the cell in the column.
                ri.cell.setBounds(0, t, (ri.col.getWidth() - marginSize), (b - t));
                ri.col.setWorkingHeight(b);

                if (ri.cell.getOffsetTicks() < maxOffset) {
                    ri.col.setWorkingOnsetPadding(ri.col.getWorkingOffsetPadding());

                    // We have finished laying this cell.
                    ri.col.setWorkingOrd(ri.col.getWorkingOrd() + 1);
                    maxHeight = Math.max(b, maxHeight);
                    ri.cell.setOrdinal(ri.col.getWorkingOrd());
                    laidCells++;

                    // Pass the padding onto the next cell in the row.
                    int j = i + 1;
                    if (j < rowCells.size()) {
                        RowInfo nri = rowCells.get(j);

                        //System.err.println("Passing pad on:" + pad);
                        if (nri.cell.getOnsetTicks() > ri.cell.getOnsetTicks()) {
                            nri.col.setWorkingOnsetPadding(nri.col.getWorkingOffsetPadding() + pad);
                        }

                        nri.col.setWorkingOffsetPadding(nri.col.getWorkingOffsetPadding() + pad);
                    }
                   

                } else if ((totalCells - laidCells) == numMaxOffsetCells) {
                    //System.err.println("Finished laying: " + numMaxOffsetCells);

                    // Push the final padding backwards.
                    for (int j = (i - 1); j >= 0; j--) {
                        RowInfo nri = rowCells.get(j);

                        if (nri.cell.getOffsetTicks() >= ri.cell.getOffsetTicks()) {
                            nri.col.setWorkingOffsetPadding(ri.col.getWorkingOffsetPadding());

                            t = nri.cell.getTemporalTop(ratio) + nri.col.getWorkingOnsetPadding();
                            b = nri.cell.getTemporalBottom(ratio) + nri.col.getWorkingOffsetPadding();
                            nri.cell.setBounds(0, t, (ri.col.getWidth() - marginSize), (b - t));
                            nri.col.setWorkingHeight(b);

                            //System.err.println("Pushing pad back: [" + j + ", " + i + "]: " + t + ", " + b);
                        }                        
                    }

                    // Eventually we will be left with only the cells that
                    // exist with the maximum offset.
                    numMaxOffsetCells--;
                    laidCells++;
                }
            }

            // TODO: REMOVE - THIS IS TEST CODE ************
            /*
            rowsCompleted++;
            if (rowsCompleted >= rowLock) {
                break;
            }*/
            // TODO: END TEST CODE *************************
        }

        // Pad the columns so that they are all the same length.
        padColumns(mainView, parent);
    }

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
                } else if ((a.cell.getOnsetTicks() == b.cell.getOnsetTicks()) && (a.cell.getOffsetTicks() > b.cell.getOffsetTicks())) {
                    sorted = false;
                    row.set(i, b);
                    row.set(i + 1, a);
                }
            }
        } while (!sorted);

        return row;
    }

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
