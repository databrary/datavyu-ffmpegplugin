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
//        long startTime = System.currentTimeMillis();
        super.layoutContainer(parent);
        pane = (JScrollPane) parent;

        // This layout must be applied to a Spreadsheet panel.
        SpreadsheetView mainView = (SpreadsheetView) pane.getViewport()
                .getView();

        maxHeight = parent.getHeight();

        // Get visible columns.
        List<SpreadsheetColumn> visible_columns = getVisibleColumns(mainView);

        // Cell cache so we only have to get from the DB once.
        // Greatly speeds up the algorithm.
        // Redrawing would be even faster if this was only done on DB update
        HashMap<Integer, List<SpreadsheetCell>> cellCache = new HashMap<Integer, List<SpreadsheetCell>>();
        for (int i = 0; i < visible_columns.size(); i++) {
            cellCache.put(i, visible_columns.get(i).getCellsTemporally());
        }

        // The size the of the gap to use between cells and as overlap on
        // overlapping cells in different columns
        int gapSize = 10;

        Set<Long> times = new TreeSet<Long>();
        HashMap<Long, List<SpreadsheetCell>> cellsByOnset = new HashMap<Long, List<SpreadsheetCell>>();
        HashMap<Long, List<SpreadsheetCell>> cellsByOffset = new HashMap<Long, List<SpreadsheetCell>>();

        for (int key : cellCache.keySet()) {
            int width = visible_columns.get(key).getWidth();
            for (SpreadsheetCell cell : cellCache.get(key)) {
                // Fix cell width to column width
                cell.setWidth(width);

                long onset = cell.getOnsetTicks();
                long offset = cell.getOffsetTicks();

                if (!cellsByOnset.containsKey(onset)) {
                    cellsByOnset.put(onset, new LinkedList<SpreadsheetCell>());
                }
                cellsByOnset.get(onset).add(cell);

                // Adjust the offsets of point and negative cells so we can
                // place them easier
                if (offset <= onset) {
                    offset = onset + 1;
                }

                if (!cellsByOffset.containsKey(offset)) {
                    cellsByOffset.put(offset, new LinkedList<SpreadsheetCell>());
                }
                cellsByOffset.get(offset).add(cell);

                times.add(onset);
                times.add(offset);

                cell.setOverlapBorder(false);
            }
        }

        Long[] timeArray = times.toArray(new Long[]{});

        // Now go through all the times and try to map them to a position
        int maxPosition = 0;
        TreeMap<Long, Integer> timeByLoc = new TreeMap<Long, Integer>();
        for (int i = 0; i < times.size(); i++) {
            Long time = timeArray[i];
            List<SpreadsheetCell> cellsWithOnset = cellsByOnset.get(time);
            List<SpreadsheetCell> cellsWithOffset = cellsByOffset.get(time);

            // Get the minimum height we can go down from these cells
            int minHeight = 0;

            if (cellsWithOnset != null) {
                for (SpreadsheetCell cell : cellsWithOnset) {
                    if (minHeight < cell.getPreferredSize().height) {
                        minHeight = cell.getPreferredSize().height;
                    }
                }
            }

            timeByLoc.put(time, maxPosition);
            maxPosition += minHeight;

        }

        for (int key : cellCache.keySet()) {
            for (int i = 0; i < cellCache.get(key).size() - 1; i++) {
                SpreadsheetCell cell = cellCache.get(key).get(i);
                SpreadsheetCell nextCell = cellCache.get(key).get(i + 1);
                long onset = cell.getOnsetTicks();
                long offset = cell.getOffsetTicks();
                long nextOnset = nextCell.getOnsetTicks();
                long nextOffset = nextCell.getOffsetTicks();

                // Non-continuous cells
                if (nextOnset - offset > 1) {
                    timeByLoc.put(nextOnset, timeByLoc.get(nextOnset) + gapSize);

                    for (int j = 0; j < timeArray.length; j++) {
                        if (timeArray[j] > nextOnset) {
                            timeByLoc.put(timeArray[j], timeByLoc.get(timeArray[j]) + gapSize);
                        }
                    }
                }

                if (onset == nextOnset) {
//                if (nextOnset - offset <= 1) {
                    for (int j = 0; j < timeArray.length; j++) {
                        if (timeArray[j] > nextOnset) {
                            timeByLoc.put(timeArray[j], timeByLoc.get(timeArray[j]) + cell.getPreferredSize().height);
                        }
                    }
                }

            }
        }

        /* BugzID:255 - Fixes different offsets aligning.
           Go through our time mapping and ensure uniqueness. */
        HashMap<Long, Integer> timeByLoc2 = new HashMap<>();
        int adjust = 0;
        Map.Entry<Long, Integer> p = timeByLoc.pollFirstEntry();
        long pk = p.getKey();
        int pv = p.getValue();
        timeByLoc2.put(pk, pv);
        for( Map.Entry<Long, Integer> e : timeByLoc.entrySet()){
            long ck = e.getKey();
            int cv = e.getValue();
            if(cv==pv){
                adjust+= gapSize;
            }
            timeByLoc2.put(ck, cv+adjust);
            pv = cv;
        }

        // Now go through each of the columns and shorten cells that overlap
        // with the one ahead of them to guarantee all cells can be seen
        for (int key : cellCache.keySet()) {
            SpreadsheetColumn col = visible_columns.get(key);
            int colWidth = col.getWidth();
            SpreadsheetCell prevCell = null;
            int colHeight = 0;
            for (SpreadsheetCell curCell : col.getCells()) {
//                SpreadsheetCell nextCell = cellCache.get(key).get(i + 1);

                // Overlapping if cell is upside down
                if (curCell.isUpsideDown()) {
                    curCell.setOverlapBorder(true);
                }

                // Overlapping if previous cell offset extends past current cell onset
                if (prevCell != null && prevCell.getOffsetTicks() > curCell.getOnsetTicks()) {
                    prevCell.setOverlapBorder(true);
                }

                // Set cell boundary.
                int cellTopY = timeByLoc2.get(curCell.getOnsetTicks());
                int cellHeight = timeByLoc2.get(curCell.getOffsetTicks()) - cellTopY;
                curCell.setBounds(0, cellTopY, colWidth-1, cellHeight);

                // Increment column height variable
                colHeight += cellHeight;

                prevCell = curCell;
            }

            // Set column working height
            col.setWorkingHeight(colHeight);
            maxHeight = Math.max(maxHeight, colHeight);
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
