/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openshapa.views.discrete.layouts;

import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
    
    // Buffer for otherwise unknown gaps above/below cells.
    double gapSize;
    
    // Denominator of overlap ratio
    double overlapSize;
    
    // Hashmap where we will store neighbor and placement information for each cell
    HashMap<SpreadsheetCell, RowInfo> rowmapping = new HashMap<SpreadsheetCell, RowInfo>();
    
    // Hashmap where we will store cells obtained from the database in temporal order.
    HashMap<SpreadsheetColumn, List<SpreadsheetCell>> cellCache = new HashMap<SpreadsheetColumn, List<SpreadsheetCell>>();

    /**
     * Information on each element in the row we are currently processing.
     */ 
    private class RowInfo {
        public RowInfo(SpreadsheetCell nCell, SpreadsheetCell pCell, List<SpreadsheetCell> tOCells, List<SpreadsheetCell> bOCells, List<SpreadsheetCell> cCells, List<SpreadsheetCell> cBCells, SpreadsheetColumn nCol) {
            cell = nCell;
            prevCell = pCell;
            col = nCol;
            bottomOverlappingCells = bOCells;
            topOverlappingCells = tOCells;
            containedCells = cCells;
            containedByCells = cBCells;
        }

        // The cell that this row information is about.
        public SpreadsheetCell cell, prevCell;
        
        // The cells that overlap the above cell.
        public List<SpreadsheetCell> bottomOverlappingCells;
        public List<SpreadsheetCell> topOverlappingCells;
        
        // The cells that are contained by the above cell.
        public List<SpreadsheetCell> containedCells;
        
        // Cells that contain the above cell
        public List<SpreadsheetCell> containedByCells;

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
        overlapSize = 2;
        gapSize = 21;
        rowmapping.clear();
        cellCache.clear();

        for (SpreadsheetColumn c : mainView.getColumns()) {
            
            c.setWorkingHeight(0);
            c.setWorkingOrd(0);
            c.setWorkingOnsetPadding(0);
            c.setWorkingOffsetPadding(0);
            c.setAllCellsProcessed(false);
            cellCache.put(c, c.getCellsTemporally());
            
        }
        long startTime = System.currentTimeMillis();


        List<SpreadsheetCell> cells;
        SpreadsheetCell currCell = null;
        SpreadsheetCell prevCell = null;
        for (SpreadsheetColumn c : mainView.getColumns()) {
            
            prevCell = null;
            cells = cellCache.get(c);
            
            for(int i = 0; i < cells.size(); i++) {
                if( i > 0 ) {
                    prevCell = cells.get(i-1);
                }
                currCell = cells.get(i);
                rowmapping.put(currCell, getNeighbors(mainView, c, currCell, prevCell));
            }
        }
        
        long endTime = System.currentTimeMillis();
        System.out.println("FINISHED MAPPING ROWS");
        System.out.println(endTime-startTime);
        
        for (SpreadsheetColumn c : mainView.getColumns()) {
            
            for(SpreadsheetCell cell : cellCache.get(c)) {
                
                layCell(mainView, c, cell, true);
                
            }
            
        }
        
        
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
    }
    
    private SpreadsheetColumn getColFromCell(SpreadsheetView mainView, SpreadsheetCell cell) {
        for(SpreadsheetColumn c : mainView.getColumns()) {
            if(cellCache.get(c).contains(cell)) {
                System.out.println(c.getName());
                return c;
            }
        }
        
        return null;
    }
    
    private void layCell(SpreadsheetView mainView, SpreadsheetColumn col, SpreadsheetCell cell, boolean recurse) {
        if(cell.isBeingProcessed()) {
            if(col.getWorkingOffsetPadding() < cell.getBounds().y) {
                col.setWorkingOnsetPadding(cell.getBounds().y);
                col.setWorkingOffsetPadding(cell.getBounds().y);
            }
            return;
        }
        
        
        
        // Current top placement of cell
        int t = col.getWorkingOffsetPadding();
        
        // Current bottom of cell
        int b = col.getWorkingOffsetPadding() + cell.getPreferredSize().height;
        
        
        
        
        // Get the neighbors that will decide the placement of this cell
        RowInfo ri = this.rowmapping.get(cell);
        List<Integer> neighborBottoms = new ArrayList<Integer>();
        List<Integer> neighborTops = new ArrayList<Integer>();
        
        // Is there a gap between this cell and the previous one in the column?
        // If so, distance them.
        if(ri.prevCell != null) {
            
            // If we have no other placement references, place the previous
            // cell and then use that as the reference.
            
            // If containedCells or overlappingCells is not empty, a cycle can
            // occur.
            if(!ri.prevCell.isBeingProcessed()) {
                layCell(mainView, getColFromCell(mainView, ri.prevCell), ri.prevCell, false);
            }
            
            t = ri.prevCell.getBounds().y + ri.prevCell.getSize().height;
            b = ri.prevCell.getBounds().y + ri.prevCell.getSize().height;
            
            // Insert gap
            if(ri.prevCell.getOffsetTicks() < cell.getOnsetTicks()) {
                t += gapSize;
                b += gapSize;
                
                if(ri.topOverlappingCells.size() > 0) {
                    t += gapSize;
                    b += gapSize;
                }
            }
            
        }
        
        // Check its overlapping cells and cells it is contained in to see if
        // they've been placed yet. If so, adjust the current tops and bottoms
        // to match.
        
        // Now check to see how many cells it contains so we can get the size of
        // this cell
        
        for(SpreadsheetCell c : ri.containedCells) {
            
            // Are the child cells laid out? If not, recurse down through them
            // and lay them out so we can get their position
            if(!c.isBeingProcessed() && recurse) {
                layCell(mainView, getColFromCell(mainView, c), c, true);
            }
            
//            b += c.getPreferredSize().height;
            
            // Now that it is processed, add where it is to our lists.
            neighborTops.add(c.getBounds().y);
            neighborBottoms.add(c.getBounds().y + c.getSize().height);
        }
        
        //If we have contained neighbors, line this cell up with them.
        if(neighborTops.size() > 0) {
            t = Collections.min(neighborTops);
            b = Collections.max(neighborBottoms);
        }
        
        // Do the same with the containedBy cells. If there is a gap then
        // the top position will be determined by the outer container
        
       

        
        // Does it overlap any cells on the bottom of each cell? If yes, add some padding for those
        if(ri.bottomOverlappingCells.size() > 0) {
            for(SpreadsheetCell c : ri.containedCells) {
            
            // Are the child cells laid out? If not, recurse down through them
            // and lay them out so we can get their position
                if(!c.isBeingProcessed() && recurse) {
//                    layCell(mainView, getColFromCell(mainView, c), c, true);
                }
            }
            b += ri.bottomOverlappingCells.get(0).getPreferredSize().height / overlapSize;
        }
        
        // Finally, display it.
        // This cell did not interact with any other cells, make it standard len
        if(b - t < cell.getPreferredSize().height) {
            b += cell.getPreferredSize().height;
        }

        
        // If we are moving through this function canonically, then update the
        // column params to reflect the current bottom position. All other position
        // changes within the column will be made from related cells.
        if(b > col.getWorkingOnsetPadding()) {
            col.setWorkingOnsetPadding(b);
            col.setWorkingOffsetPadding(b);
        }
                
        cell.setBeingProcessed(true);
        ri.cell.setBounds(0, t, (ri.col.getWidth() - marginSize), (b - t));
        ri.col.setWorkingHeight(b);
        markCellAsCompleted(ri);
        
    }
    
    /**
     * Gathers information relevant to placing each cell.
     *
     */
    private RowInfo getNeighbors(SpreadsheetView mainView, SpreadsheetColumn currCol, SpreadsheetCell currCell, SpreadsheetCell prevCell) {
        ArrayList<SpreadsheetCell> containedCells = new ArrayList<SpreadsheetCell>();
        ArrayList<SpreadsheetCell> containedByCells = new ArrayList<SpreadsheetCell>();
        ArrayList<SpreadsheetCell> bottomOverlappingCells = new ArrayList<SpreadsheetCell>();
        ArrayList<SpreadsheetCell> topOverlappingCells = new ArrayList<SpreadsheetCell>();

        
        Long currCellOnset = currCell.getOnsetTicks();
        Long currCellOffset = currCell.getOffsetTicks();
        
        // If the cell is newly added 
        if(currCellOffset < currCellOnset) {
            currCellOffset = currCellOnset;
        }
        
        for (SpreadsheetColumn col : mainView.getColumns()) {
            for (SpreadsheetCell cell : cellCache.get(col)) {
                if(col != currCol && cell != currCell) {
                    
                    Long cellOnset = cell.getOnsetTicks();
                    Long cellOffset = cell.getOffsetTicks();
                    
                    if(cellOffset < cellOnset) {
                        cellOffset = cellOnset;
                    }
                    
                    // Check to see if currCell contains this cell
                    if(cellOnset >= currCellOnset 
                            && cellOffset <= currCellOffset) {
                        containedCells.add(cell);
                    }
                    
                    // Check to see if currCell is contained by this cell
                    else if(cellOnset <= currCellOnset
                            && cellOffset >= currCellOffset) {
                        containedByCells.add(cell);
                    }
                    
                    // Overlapping top
                    else if(cellOnset < currCellOnset
                            && cellOffset > currCellOnset
                            && cellOffset < currCellOffset) {
                        topOverlappingCells.add(cell);
                    }
                    
                    // Overlapping bottom
                    else if(cellOnset < currCellOffset
                            && cellOnset > currCellOnset
                            && cellOffset > currCellOffset) {
                        bottomOverlappingCells.add(cell);
                    }
                }
            }
        }
        

        return new RowInfo(currCell, prevCell, topOverlappingCells, bottomOverlappingCells, containedCells, containedByCells, currCol);
    }

    /**
     * Performs a bubble sort of a row of cells by onset and offset. The collection
     * of cells will be ordered by onset, if the onsets match, they will then be
     * ordered by offset.
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
                } else if ((a.cell.getOnsetTicks() == b.cell.getOnsetTicks()) && (a.cell.getOffsetTicks() > b.cell.getOffsetTicks())) {
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
