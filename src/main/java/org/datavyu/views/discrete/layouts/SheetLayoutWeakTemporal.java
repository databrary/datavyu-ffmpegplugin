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

import org.datavyu.Datavyu;
import org.datavyu.models.db.Datastore;
import org.datavyu.views.discrete.SpreadsheetCell;
import org.datavyu.views.discrete.SpreadsheetColumn;
import org.datavyu.views.discrete.SpreadsheetView;
import org.datavyu.views.discrete.layouts.SheetLayoutFactory.SheetLayoutType;

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
        long startTime = System.currentTimeMillis();
        super.layoutContainer(parent);
        pane = (JScrollPane) parent;

        // This layout must be applied to a Spreadsheet panel.
        SpreadsheetView mainView = (SpreadsheetView) pane.getViewport()
                .getView();

        maxHeight = parent.getHeight();

        // Get visible columns.
        List<SpreadsheetColumn> visible_columns = getVisibleColumns(mainView);

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
            for( SpreadsheetCell curCell : col.getCells()){
                long onset = curCell.getOnsetTicks();
                int height = curCell.getPreferredSize().height;
                intermediateMap.compute(onset, (k, v) -> (v == null) ? height : v + height);

                // Add the onset and offset times to our master set.
                times.add(onset);
                long offset = curCell.getOffsetTicks();
                if(offset > onset) times.add(offset);
            }
            // Merge the intermediate map entries.
            intermediateMap.forEach( (k, v) -> heightMap.merge(k, v, Integer::max));
        }

        /* Iterate over sorted set of times and assign position values.
           For onset times o1, o2 : Map(o2) = Map(o1) + heightMap(o1) + gapSize
           Keep separate maps for onset times and offset times.
           The offset mapping for a time is it's onset mapping plus the height;
         */
        int gapSize = 10;   // default space separating unique times
        HashMap<Long, Integer> onsetMap = new HashMap<>();
        HashMap<Long, Integer> offsetMap = new HashMap<>();
        int pos = 0;    // position to assign to next onset time
        for(Long time : times){
            if( !heightMap.containsKey(time)) {
                offsetMap.put(time, pos - gapSize);
                continue;
            }
            onsetMap.put(time, pos);
            int height = heightMap.get(time);
            offsetMap.putIfAbsent(time, pos + height);
            pos += height + gapSize;
        }

        /* Iterate over all spreadsheet cells and set boundaries using onset and offset maps.
           Special case for point (onset==offset) and reversed (onset>offset) cells: use the cell's
           preferred height instead of the offset map's.
         */
        for( SpreadsheetColumn col : visible_columns){
            List<SpreadsheetCell> orderedCells = col.getCellsTemporally();
            int colWidth = col.getWidth();
            int colHeight = 0;
            SpreadsheetCell prevCell = null;
            for (SpreadsheetCell curCell : orderedCells) {
                long onset = curCell.getOnsetTicks();
                long offset = curCell.getOffsetTicks();
                int cellTopY = onsetMap.get(onset);

                // Clear overlap border on this cell.
                curCell.setOverlapBorder(false);

                /* BugzID:302 - Fix overlapping cells when onsets are same. */
                if (prevCell != null && (prevCell.getOnsetTicks() >= onset || prevCell.getOffsetTicks() > onset)) {
                    cellTopY = prevCell.getY() + prevCell.getPreferredSize().height;
                    prevCell.setOverlapBorder(true);
                    prevCell.setBounds(0, prevCell.getY(), colWidth - 1, prevCell.getPreferredSize().height);
                }

                // Set cell boundary.
                int cellHeight = offsetMap.get(offset) - cellTopY;
                int cellHeightMin = curCell.getPreferredSize().height;
                cellHeight = Math.max(cellHeight, cellHeightMin);
                curCell.setBounds(0, cellTopY, colWidth-1, cellHeight);

                // Overlapping if cell is upside down
                if (onset > offset) {
                    curCell.setOverlapBorder(true);
                }

                // Update vars
                colHeight = Math.max(colHeight, cellTopY+cellHeight);
                prevCell = curCell;
            }

            // Set column working height
            col.setWorkingHeight(colHeight);
            maxHeight = Math.max(maxHeight, colHeight);
        }

        // Now go through each of the columns and shorten cells that overlap
        // with the one ahead of them to guarantee all cells can be seen

        padColumns(mainView, parent);
        System.err.println(String.format("Aligned.  Time: %d.", System.currentTimeMillis() - startTime));
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
