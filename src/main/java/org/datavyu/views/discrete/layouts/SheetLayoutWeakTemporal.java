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

import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.swing.JScrollPane;
import org.datavyu.Datavyu;
import org.datavyu.views.discrete.SpreadsheetCell;
import org.datavyu.views.discrete.SpreadsheetColumn;
import org.datavyu.views.discrete.SpreadsheetView;

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
    // Hash of placements by onset
    HashMap<Long, List<SpreadsheetCell>> onsetToLoc;
    // Hash of placements by offset
    HashMap<Long, List<SpreadsheetCell>> offsetToLoc;

    /**
     * Information on each element in the row we are currently processing.
     */
    private class CellInfo {

        public CellInfo(SpreadsheetCell nCell, long onset, long offset) {
            cell = nCell;
            onset = cell.getOnsetTicks();
            offset = cell.getOffsetTicks();
        }
        // The cell that this row information is about.
        public SpreadsheetCell cell;
        public long onset;
        // The column that the above cell belongs too.
        public long offset;
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
        
        long overallTime = System.currentTimeMillis();

        onsetToLoc = new HashMap();
        offsetToLoc = new HashMap();

        // This layout must be applied to a Spreadsheet panel.
        JScrollPane pane = (JScrollPane) parent;
        SpreadsheetView mainView = (SpreadsheetView) pane.getViewport()
                .getView();

        // See if we need to redraw this spreadsheet
//        if(!Datavyu.getProjectController().getDB().isChanged() && !Datavyu.getView().getRedraw()) {
//            return;
//        }
        Datavyu.getProjectController().getDB().markAsUnchanged();
//        Datavyu.getView().setRedraw(false);

        laidCells = 0;
//        maxHeight = 0;
        maxHeight = parent.getHeight();

        // Determine the ratio/scale to use with temporal ordering, we pick the
        // the largest column size in pixels and temporal length in ticks to
        // use as an aggressive ratio/scale to fit as many cells on the screen
        // as possible.
        long ratioHeight = 0;   // The height required to fit all the visible cells on the spreadsheet without any spacing.
        long ratioTicks = 0;    // The maximum offset used in the entire visible spreadsheet.
        int totalCells = 0;     // The total number of visible cells we are laying out on the spreadsheet.
        ratio = 1.0;            // The ratio / scale that we are using to position the cells temporally.


        List<SpreadsheetColumn> visible_columns = getVisibleColumns(mainView);

        for (SpreadsheetColumn c : visible_columns) {
            // Take this opportunity to initalise the working data we need for each of the columns.
            c.setWorkingHeight(0);
            c.setWorkingOrd(0);
            c.setWorkingOnsetPadding(0);
            c.setWorkingOffsetPadding(0);

            // Determine the total height in pixels of the cells in this column.
            List<SpreadsheetCell> colCells = c.getCellsTemporally();
            int colHeight = 0;
            for (SpreadsheetCell cell : colCells) {
                if (cell != null) {
                    colHeight += cell.getPreferredSize().height;
                }
            }

            // Determine the maximum column height in pixels.
            ratioHeight = Math.max(ratioHeight, colHeight);

            // Determine the temporal length of the column in ticks.
            if (colCells.size() > 0) {
                SpreadsheetCell lastCell = colCells.get((colCells.size() - 1));

                // If the offset is zero or smaller, we need to consider the
                // onset of the cell instead for determining the maximum offset
                // required to display the entire spreadsheet.
                if (lastCell != null && lastCell.getOffsetTicks() <= 0) {
                    ratioTicks = Math.max(ratioTicks, lastCell.getOnsetTicks());
                } else if (lastCell != null) {
                    ratioTicks = Math.max(ratioTicks, lastCell.getOffsetTicks());
                }
            }
            totalCells = totalCells + colCells.size();
        }
        // Determine the final temporal ratio/scale we are going to use for the spreadsheet.
        if (ratioTicks > 0) {
            ratio = ratioHeight / (double) ratioTicks;
        }

        // Untill we have laid all the cells, we position each of them
        // temporarily. We do this row by row with the cells sorted temporarily,
        // untill we have no cells left to lay.
        int pad = 0;    // The cumulative padding we need to apply to cell positioning - this is the total
        // amount of extra vertical space we have had to pad out, to make entire cells visible.

        // This array is guaranteed initialized to 0 by the java lang spec
        // Stores the current cell position for each col
        int[] position_index = new int[visible_columns.size()];
        ArrayList<Integer> column_bottoms = new ArrayList<Integer>();
        ArrayList<Long> current_offsets = new ArrayList<Long>();
        SpreadsheetCell[] rowCells = new SpreadsheetCell[visible_columns.size()];
        SpreadsheetCell[] prevRowCells = new SpreadsheetCell[visible_columns.size()];
        SpreadsheetCell prevLaidCell = null;
        SpreadsheetColumn prevLaidCol = null;
        int prevColIndex = -1;

        int prev_t = 0;
        int prev_b = 0;

        // Cell cache so we only have to get from the DB once.
        // Greatly speeds up the algorithm.
        // Redrawing would be even faster if this was only done on DB update
        HashMap<Integer, List<SpreadsheetCell>> cellCache = new HashMap<Integer, List<SpreadsheetCell>>();
        for (int i = 0; i < visible_columns.size(); i++) {
            cellCache.put(i, visible_columns.get(i).getCellsTemporally());
        }

//        long starttime = System.currentTimeMillis();

        // The size the of the gap to use between cells and as overlap on
        // overlapping cells in different columns
        int gapSize = 10;
        int minCellHeight = 45;


        int numCells = 0;
        Set<Long> times = new TreeSet<Long>(); 
        TreeMap<Long, List<SpreadsheetCell>> cellsByOnset = new TreeMap<Long, List<SpreadsheetCell>>();
        TreeMap<Long, List<SpreadsheetCell>> cellsByOffset = new TreeMap<Long, List<SpreadsheetCell>>();

        for(int key : cellCache.keySet()) {
            for(SpreadsheetCell cell : cellCache.get(key)) {
                long onset = cell.getOnsetTicks();
                long offset = cell.getOffsetTicks();
                numCells += 1;
                if(!cellsByOnset.containsKey(onset)) {
                    cellsByOnset.put(onset, new LinkedList<SpreadsheetCell>());
                }
                cellsByOnset.get(onset).add(cell);
                
                // Adjust the offsets of point and negative cells so we can
                // place them easier
                if(offset <= onset) {
                    offset = onset + 1;
                }
                
                if(!cellsByOffset.containsKey(offset)) {
                    cellsByOffset.put(offset, new LinkedList<SpreadsheetCell>());
                }
                cellsByOffset.get(offset).add(cell);
                
                times.add(onset);
                times.add(offset);

            }
        }
        
        Long[] timeArray = times.toArray(new Long[]{});
        
        // Now go through all the times and try to map them to a position
        int maxPosition = 0;
        HashMap<Long, Integer> timeByLoc = new HashMap<Long, Integer>();
        for(int i = 0; i < times.size(); i++) {
            Long time = timeArray[i];
            List<SpreadsheetCell> cellsWithOnset = cellsByOnset.get(time);
            List<SpreadsheetCell> cellsWithOffset = cellsByOffset.get(time);
            
            // Get the minimum height we can go down from these cells
            int minHeight = gapSize;
            if(cellsWithOnset != null) {
                for(SpreadsheetCell cell : cellsWithOnset) {
                    if(minHeight < cell.getPreferredSize().height) {
                        minHeight = cell.getPreferredSize().height;
                    }
                }
            }
            
//            if(cellsWithOffset != null) {
//                for(SpreadsheetCell cell : cellsWithOffset) {
//                    if(minHeight < cell.getPreferredSize().height) {
//                        minHeight = cell.getPreferredSize().height;
//                    }
//                }
//            }
            
            timeByLoc.put(time, maxPosition);
            maxPosition += minHeight;

        }
        
        for(int key : cellCache.keySet()) {
            for(int i = 0; i < cellCache.get(key).size() - 1; i++) {
                SpreadsheetCell cell = cellCache.get(key).get(i);
                SpreadsheetCell nextCell = cellCache.get(key).get(i+1);
                long onset = cell.getOnsetTicks();
                long offset = cell.getOffsetTicks();
                long nextOnset = nextCell.getOnsetTicks();
                long nextOffset = nextCell.getOffsetTicks();
                                
                // Non-continuous cells
                if(nextOnset - offset > 1) {
                    timeByLoc.put(nextOnset, timeByLoc.get(nextOnset) + gapSize);
                    
                    for(int j = 0; j < timeArray.length; j++) {
                        if(timeArray[j] > nextOnset) {
                            timeByLoc.put(timeArray[j], timeByLoc.get(timeArray[j]) + gapSize);
                        }
                    }
                }
                
                if(onset == nextOnset) {
                    for(int j = 0; j < timeArray.length; j++) {
                        if(timeArray[j] > nextOnset) {
                            timeByLoc.put(timeArray[j], timeByLoc.get(timeArray[j]) + cell.getPreferredSize().height);
                        }
                    }
                }
                
                // Cells with same onsets
            }
        }
                
        // Set all of the cell positions now that we have figured them out
        for(Long time : times) { 
            List<SpreadsheetCell> cellsWithOnset = cellsByOnset.get(time);
            List<SpreadsheetCell> cellsWithOffset = cellsByOffset.get(time);
            
//            System.out.println(String.valueOf(time) + " at position " + String.valueOf(timeByLoc.get(time)));
            
            if(cellsWithOnset != null) {
                for(SpreadsheetCell cell : cellsWithOnset) {
//                    if(!cell.isBeingProcessed()) {
                        cell.setBounds(0, timeByLoc.get(time), (cell.getWidth()), cell.getPreferredSize().height);
//                    }
                }
            }
            
            if(cellsWithOffset != null) {
                for(SpreadsheetCell cell : cellsWithOffset) {
//                    if(!cell.isBeingProcessed()) {
                        cell.setBounds(0, cell.getY(), (cell.getWidth()), (timeByLoc.get(time) - cell.getY()));
//                    }
                }
            }
            
            if(maxHeight < timeByLoc.get(time)) {
                maxHeight = timeByLoc.get(time);
            }
        }
//        System.out.println(maxHeight);
        
        // Set the working heights for all of the columns
        List<SpreadsheetColumn> visibleColumns = getVisibleColumns(mainView);
        for(int key : cellCache.keySet()) {
            SpreadsheetColumn col = visibleColumns.get(key);
//            System.out.println(key);
//            System.out.println(cellCache.get(key).size());
            if(cellCache.get(key).size() > 0) {
                SpreadsheetCell cell = cellCache.get(key).get(cellCache.get(key).size()-1);
                col.setWorkingHeight(cell.getY() + cell.getHeight());
            }
        }
        
        // Now go through each of the columns and shorten cells that overlap
        // with the one ahead of them to guarantee all cells can be seen
        for(int key : cellCache.keySet()) {
            for(int i = 0; i < cellCache.get(key).size() - 1; i++) {
                SpreadsheetCell curCell = cellCache.get(key).get(i);
                SpreadsheetCell nextCell = cellCache.get(key).get(i+1);
                SpreadsheetColumn col = visibleColumns.get(key);
                if(curCell.getOffsetTicks() > nextCell.getOnsetTicks()) {
                    curCell.setBounds(0, curCell.getY(), curCell.getWidth(), nextCell.getY() - curCell.getY());
                }
                
                if(curCell.getOnsetTicks() == nextCell.getOnsetTicks()) {
                    curCell.setBounds(0, curCell.getY(), curCell.getWidth(), curCell.getPreferredSize().height);
                    nextCell.setBounds(0, curCell.getY() + curCell.getHeight(), nextCell.getWidth(), nextCell.getPreferredSize().height);
                    if(col.getWorkingHeight() < nextCell.getY() + nextCell.getHeight()) {
                        col.setWorkingHeight(nextCell.getY() + nextCell.getHeight());
                    }
                }
            }
        }
        
        padColumns(mainView, parent);
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
        List<SpreadsheetColumn> viscolumns = new ArrayList<SpreadsheetColumn>();

        for (SpreadsheetColumn c : mainView.getColumns()) {
            if (c.isVisible()) {
                viscolumns.add(c);
            }
        }

        return viscolumns;
    }
}
