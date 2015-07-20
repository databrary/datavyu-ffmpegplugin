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
import javax.swing.undo.UndoableEdit;
/**
 * SheetLayoutWeakTemporal - mimics the weak temporal alignment style from
 * original MacSHAPA.
 */
public class SheetLayoutWeakTemporal extends SheetLayout {
    // The of the right hand margin.

    // The maximum height of the layout in pixels.
    int maxHeight;
    private JScrollPane pane;
    int editIdx;    // index of edit in UndoManager when layoutContainer() was last run
    boolean mustRun; // force layoutContainer() method to run fully
    Integer columnHash;

    /**
     * SheetLayoutOrdinal constructor.
     *
     * @param margin The size of the margin used for this layout.
     */
    public SheetLayoutWeakTemporal(final int margin) {
        editIdx = Datavyu.getView().getSpreadsheetUndoManager().getIndexOfNextAdd();
        mustRun = true;
    }

    /* Set mustRun to true. */
    public void forceRun(){
        mustRun = true;
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

        /* Check if we can skip laying the entire spreadsheet.
           Relies on the UndoManager, so it isn't very intuitive on when it skips
            (e.g. redraws when column name is changed, but not when columns are hidden or shown).
           But it should stop over-running this routine on scrolls.
         */
        int eidx = Datavyu.getView().getSpreadsheetUndoManager().getIndexOfNextAdd();
        int ch = visible_columns.stream()
                .map( col -> col.getColumnName())
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString().hashCode();
        if( !mustRun
                && (columnHash!=null && columnHash==ch)
                && (editIdx == eidx)){
//            System.err.println(String.format("Didn't align.  Time: %d.",System.currentTimeMillis()-startTime));
            return;
        }

        // Cell cache so we only have to get from the DB once.
        // Greatly speeds up the algorithm.
        // Redrawing would be even faster if this was only done on DB update
        int width = 0;
        HashMap<Integer, List<SpreadsheetCell>> cellCache = new HashMap<Integer, List<SpreadsheetCell>>();
        for (int i = 0; i < visible_columns.size(); i++) {
            cellCache.put(i, visible_columns.get(i).getCellsTemporally());
            width = visible_columns.get(i).getWidth();
        }
        width--;

        // The size the of the gap to use between cells and as overlap on
        // overlapping cells in different columns
        int gapSize = 10;

        Set<Long> times = new TreeSet<Long>();
        HashMap<Long, List<SpreadsheetCell>> cellsByOnset = new HashMap<Long, List<SpreadsheetCell>>();
        HashMap<Long, List<SpreadsheetCell>> cellsByOffset = new HashMap<Long, List<SpreadsheetCell>>();

        for (int key : cellCache.keySet()) {
            for (SpreadsheetCell cell : cellCache.get(key)) {
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

        /* Go through our time mapping and ensure uniqueness. */
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
        // Set all of the cell positions now that we have figured them out
        for (Long time : times) {
            List<SpreadsheetCell> cellsWithOnset = cellsByOnset.get(time);
            List<SpreadsheetCell> cellsWithOffset = cellsByOffset.get(time);


            if (cellsWithOnset != null) {
                for (SpreadsheetCell cell : cellsWithOnset) {
                    cell.setBounds(0, timeByLoc2.get(time), width, cell.getPreferredSize().height);
                }
            }

            if (cellsWithOffset != null) {
                for (SpreadsheetCell cell : cellsWithOffset) {
                    cell.setBounds(0, cell.getY(), width, (timeByLoc2.get(time) - cell.getY()));
                }
            }

            if (maxHeight < timeByLoc2.get(time)) {
                maxHeight = timeByLoc2.get(time);
            }
        }
//        System.out.println(maxHeight);

        // Set the working heights for all of the columns
        List<SpreadsheetColumn> visibleColumns = getVisibleColumns(mainView);
        for (int key : cellCache.keySet()) {
            SpreadsheetColumn col = visibleColumns.get(key);
            if (cellCache.get(key).size() > 0) {
                SpreadsheetCell cell = cellCache.get(key).get(cellCache.get(key).size() - 1);
                col.setWorkingHeight(cell.getY() + cell.getHeight());
            }
        }

        // Now go through each of the columns and shorten cells that overlap
        // with the one ahead of them to guarantee all cells can be seen

        for (int key : cellCache.keySet()) {
            for (int i = 0; i < cellCache.get(key).size() - 1; i++) {
                SpreadsheetCell curCell = cellCache.get(key).get(i);
                SpreadsheetCell nextCell = cellCache.get(key).get(i + 1);
                SpreadsheetColumn col = visibleColumns.get(key);

                if (curCell.getOffsetTicks() > nextCell.getOnsetTicks()) {
                    curCell.setBounds(0, curCell.getY(), width, nextCell.getY() - curCell.getY());
                    curCell.setOverlapBorder(true);
                }
                
                if (nextCell.isUpsideDown()){
                        nextCell.setOverlapBorder(true);
                }

                if (curCell.getOnsetTicks() == nextCell.getOnsetTicks()) {
                    curCell.setBounds(0, curCell.getY(), width, curCell.getPreferredSize().height);
                    nextCell.setBounds(0, curCell.getY() + curCell.getHeight(), width, nextCell.getPreferredSize().height);
                    if (col.getWorkingHeight() < nextCell.getY() + nextCell.getHeight()) {
                        col.setWorkingHeight(nextCell.getY() + nextCell.getHeight());
                    }
                }
            }
        }

        padColumns(mainView, parent);
        editIdx = eidx;
        mustRun = false;
        columnHash = new Integer(ch);
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
