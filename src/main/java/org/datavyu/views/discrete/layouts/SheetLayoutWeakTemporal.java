/**
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.datavyu.views.discrete.layouts;

import org.datavyu.views.discrete.SpreadsheetCell;
import org.datavyu.views.discrete.SpreadsheetColumn;
import org.datavyu.views.discrete.SpreadsheetView;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
/**
 * SheetLayoutWeakTemporal - mimics the weak temporal alignment style from
 * original MacSHAPA.
 */
public class SheetLayoutWeakTemporal extends SheetLayout {
    // The of the right hand margin.

    // The maximum height of the layout in pixels.
    int maxHeight;
    private JScrollPane pane;

    /**
     * SheetLayoutOrdinal constructor.
     *
     * @param margin The size of the margin used for this layout.
     */
    public SheetLayoutWeakTemporal(final int margin) {
    }

    @Override
    public void layoutContainer(Container parent) {
        //TODO: figure out how to make this whole algorithm neater.
//        long startTime = System.currentTimeMillis();
        super.layoutContainer(parent);
        pane = (JScrollPane) parent;

        // This layout must be applied to a Spreadsheet panel.
        SpreadsheetView mainView = (SpreadsheetView) pane.getViewport().getView();

        maxHeight = parent.getHeight();

        // Get visible columns.
        List<SpreadsheetColumn> visible_columns = getVisibleColumns(mainView);

        // Map columns to temporally ordered cells from column.
        HashMap<SpreadsheetColumn, List<SpreadsheetCell>> cellMap = new HashMap<>();
        for(SpreadsheetColumn col : visible_columns){
            cellMap.put(col, col.getCellsTemporally());
        }

        TreeSet<Long> times = new TreeSet<Long>();
        HashMap<Long, Integer> heightMap = new HashMap<>();

        /* Go through each column and assign "height" values to onset times.
           This will determine how much space to allocate between onset times.
           For each column, create an intermediate map and then merge with the
           actual map using maximum values.  This is required since a column can have
           multiple cells with the same onset.
           Also accumulate all unique times.
         */
        for( SpreadsheetColumn col : visible_columns){
            HashMap<Long, Integer> intermediateMap = new HashMap<>();
            for( SpreadsheetCell curCell : cellMap.get(col)){
                long onset = curCell.getOnsetTicks();
                int height = curCell.getPreferredSize().height;
                intermediateMap.compute(onset, (k, v) -> (v == null) ? height : v + height);

                // Add the onset and offset times to our master set.
                times.add(onset);
                long offset = curCell.getOffsetTicksActual();
                if(offset > onset) times.add(offset);
            }
            // Merge the intermediate map entries.
            intermediateMap.forEach( (k, v) -> heightMap.merge(k, v, Integer::max));
        }

        /* Iterate over sorted set of times and assign position values.
           For onset times o1, o2 : Map(o2) = Map(o1) + heightMap(o1) + gapSize
         */
        int gapSize = 15;   // default space separating unique times
        HashMap<Long, Integer> onsetMap = new HashMap<>();
        int pos = 0;    // position to assign to next onset time
        for(Long time : times){
            onsetMap.put(time, pos);
            pos += heightMap.getOrDefault(time, 0);
            pos += gapSize;
        }

        /* Iterate over all spreadsheet cells and set boundaries using onset and offset maps.
           Keep a local copy of the position maps.  Since each time value gets a range of positions
           (starting from onset map's value and ending at the offset's value), update the local copy of
           the onset map to get positions for cells sharing onsets.
         */
        HashMap<Long, Integer> offsetMap = new HashMap<>();
        for( SpreadsheetColumn col : visible_columns){
            List<SpreadsheetCell> orderedCells = cellMap.get(col);
            int colWidth = col.getWidth();
            int colHeight = 0;
            HashMap<Long, Integer> onsetMapLocal = new HashMap<>(onsetMap);
            SpreadsheetCell prevCell = null;
            for (int i = 0; i < orderedCells.size(); i++) {
                SpreadsheetCell curCell = orderedCells.get(i);
                SpreadsheetCell nextCell = (i == orderedCells.size() - 1) ? null : orderedCells.get(i + 1);

                long onset = curCell.getOnsetTicks();
                long offset = curCell.getOffsetTicksActual();
                int cellTopY = onsetMapLocal.get(onset);
                
                // Get height for cell
                int cellHeight;
                int cellHeightMin = curCell.getPreferredSize().height;
                // Figure out height by looking at next cell's onset and offset times.
                if (onset > offset) { // cell is reversed
                    cellHeight = cellHeightMin;
                    curCell.setOverlapBorder(true);
                }
                // Current onset equals next onset
                else if (nextCell != null && onset == nextCell.getOnsetTicks()) {
                    cellHeight = cellHeightMin;
                    if (onset != offset || offset == nextCell.getOffsetTicksActual()) curCell.setOverlapBorder(true);
                }
                // Current offset greater than or equal to next onset
                else if (nextCell != null && offset >= nextCell.getOnsetTicks()) {
                    cellHeight = onsetMapLocal.get(nextCell.getOnsetTicks()) - cellTopY;
                    curCell.setOverlapBorder(true);
                } else {
                    cellHeight = offsetMap.getOrDefault(offset, onsetMap.get(offset)) - cellTopY;
                    curCell.setOverlapBorder(false);
                }

                // Treat cells with 1ms interval as continuous. Stretch bottom of previous cell to top of current cell.
                if (prevCell != null && onset - prevCell.getOffsetTicks() == 1) {
                    prevCell.setBounds(0, prevCell.getY(), colWidth - 1, cellTopY - prevCell.getY());
                    offsetMap.compute(offset, (k, v) -> (v == null) ? cellTopY : Math.max(v, cellTopY));
                }

                cellHeight = Math.max(cellHeight, cellHeightMin); // fix for edge cases...maybe investigate later
                // Set cell boundary
                curCell.setBounds(0, cellTopY, colWidth - 1, cellHeight);

                // Update local onset map
                int adjOn = cellHeight;
                onsetMapLocal.compute(onset, (k, v) -> v + adjOn);

                // Update offset map
                int adjOff = cellTopY + cellHeight;
                offsetMap.compute(offset, (k, v) -> (v == null) ? adjOff : Math.max(v, adjOff));

                // Update vars
                colHeight = cellTopY + cellHeight;
                prevCell = curCell;
            }

            // Set column working height
            col.setWorkingHeight(colHeight);
            maxHeight = Math.max(maxHeight, colHeight);
        }

        /* Do a second pass to update the offsets again. */
        for (SpreadsheetColumn col : visible_columns) {
            int colWidth = col.getWidth();
            for (SpreadsheetCell sc : cellMap.get(col)) {
                if (!sc.getOverlapBorder() && sc.getSize().getHeight() < offsetMap.get(sc.getOffsetTicks()) - sc.getY())
                    sc.setBounds(0, sc.getY(), colWidth - 1, offsetMap.get(sc.getOffsetTicks()));
            }
        }

        padColumns(mainView, parent);
//        System.err.println(String.format("Aligned.  Time: %d.", System.currentTimeMillis() - startTime));
    }

    public void reorientView(SpreadsheetCell cell) {
        double viewMax = pane.getViewport().getViewRect().getY() + pane.getViewport().getViewRect().getHeight();
        double viewMin = pane.getViewport().getViewRect().getY();
        int cellMax = cell.getY() + cell.getHeight();
        int cellMin = cell.getY();

        if (viewMax < cellMax) {
            pane.getViewport().setViewPosition(
                    new Point((int) pane.getViewport().getViewRect().getX(),
                            cellMax - pane.getViewport().getHeight()));
            //                pane.getVerticalScrollBar().setValue(cellMax);
        } else if (viewMin > cellMin) {
            pane.getViewport().setViewPosition(
                    new Point((int) pane.getViewport().getViewRect().getX(),
                            cellMin));
        }
    }

    /**
     * Pads all the columns so that they all fill out to the maximum height of
     * the spreadsheet. This function also ensures that the new cell button is
     * added to the bottom of each of the columns.
     *
     * @param mainView The actual spreadsheet that we are populating.
     * @param parent   The parent container that holds the spreadsheet.
     */
    private void padColumns(SpreadsheetView mainView, Container parent) {
        for (SpreadsheetColumn col : mainView.getColumns()) {
            Integer colHeight = col.getWorkingHeight();

//            System.out.println(colHeight);

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

    private List<SpreadsheetColumn> getVisibleColumns(SpreadsheetView mainView) {
        return mainView.getColumns().parallelStream()
                .filter(c -> c.isVisible())
                .collect(Collectors.toList());
    }
}
