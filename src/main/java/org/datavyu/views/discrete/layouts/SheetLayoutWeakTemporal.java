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
package org.datavyu.views.discrete.layouts;

import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
		    if(cell != null)
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
                    if (lastCell != null && lastCell.getOffsetTicks() <= 0) {
                        ratioTicks = Math.max(ratioTicks, lastCell.getOnsetTicks());
                    } else if(lastCell != null){
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
        for(int i = 0; i < visible_columns.size(); i++) {
	    cellCache.put(i, visible_columns.get(i).getCellsTemporally());
        }
        
//        long starttime = System.currentTimeMillis();
        
        // The size the of the gap to use between cells and as overlap on
        // overlapping cells in different columns
        int gapSize = 10;
	int minCellHeight = 45;

        
        // This loop should place the cells in O(#cells * #cols)
        // so once #cells >> #cols this actually places in O(#cells) :)
        // Informal time mapping seems to confirm that the amount of time
        // spend placing cells does indeed have linear growth.
        long[] timers = new long[4];
        while (laidCells < totalCells) {
            SpreadsheetCell workingCell = null;
            SpreadsheetColumn workingCol = null;
            int currColIndex = -1;
            

            // Get the current row cells
            long lowest_offset = Long.MAX_VALUE;
            long max_onset = Long.MIN_VALUE;
            for(int i = 0; i < visible_columns.size(); i++) {

                long currCellOnset = 0;
                long currCellOffset = 0;
                
                if(cellCache.get(i).size() > position_index[i]) {
                    rowCells[i] = cellCache.get(i).get(position_index[i]);
                    currCellOnset = rowCells[i].getOnsetTicks();
                    currCellOffset = rowCells[i].getOffsetTicks();
                    if(currCellOffset < currCellOnset) {
                        currCellOffset = currCellOnset;
                    }
                }
                else {
                    rowCells[i] = null;
                }
                
                
                // Do we want to position this cell at this time?
                // If we don't, we should try to mark the current top position
                // Find the cell in this row with the minimum offset.
                if(rowCells[i] != null && currCellOffset < lowest_offset) {
                    lowest_offset = currCellOffset;
                }
                
                // If the current cell contains any other cell
           }
            
            // Get the minimum onset of the cells with this offset
            for(int i = 0; i < visible_columns.size(); i++) {

                if(rowCells[i] != null && 
                        rowCells[i].getOffsetTicks() == lowest_offset && 
                        rowCells[i].getOnsetTicks() > max_onset) {
                    
                    max_onset = rowCells[i].getOnsetTicks();
                    workingCell = rowCells[i];
                    workingCol = visible_columns.get(i);
                    currColIndex = i;
                }
            }
	    
	    	
            // Does the top of this cell overlap with the previously laid cell?
            // If so, adjust the top a little bit so the two are overlapping
            if(prevLaidCell != null && 
		workingCell.getOnsetTicks() < prevLaidCell.getOffsetTicks() &&
	        workingCell.getOffsetTicks() != prevLaidCell.getOffsetTicks()) {	  	
               prev_b = prev_b + gapSize;
            }
            
            // Lay out the last cell and ready this one for layout
            if(laidCells > 0) {
                prevLaidCell.setBounds(0, prev_t, (prevLaidCol.getWidth() - marginSize), (prev_b - prev_t));
                
                if(position_index[prevColIndex] < cellCache.get(currColIndex).size()-1 && 
                    prevLaidCell.getOnsetTicks() - cellCache.get(currColIndex).get(position_index[prevColIndex]+1).getOffsetTicks() > 1) {
//			prev_b += gapSize;
                }
                
                prevLaidCol.setWorkingHeight(prev_b);
                workingCell.setBeingProcessed(false);
                
                maxHeight = Math.max(prevLaidCell.getY() + prevLaidCell.getHeight(), maxHeight);
            }
            
            
            // Now that we're sure the previous column is updated, get the col bottoms
            column_bottoms.clear();
            for(int i = 0; i < visible_columns.size(); i++) {
                column_bottoms.add(visible_columns.get(i).getWorkingHeight());
            }
            
            // Go through all of the columns and set their working height
            // if the onset of their next cell to be placed is greater than that
            // of the cell currently being placed
            for(int i = 0; i < visible_columns.size(); i++) {
                
                if(workingCol != visible_columns.get(i) 
                        && cellCache.get(i).size() > position_index[i] 
                        && !cellCache.get(i).get(position_index[i]).isBeingProcessed()) {
                    
                    long nextCellOnset = cellCache.get(i).get(position_index[i]).getOnsetTicks();
                    
                    if(nextCellOnset <= workingCell.getOffsetTicks() && 
                            workingCell.getOnsetTicks() <= nextCellOnset &&
                            workingCol.getWorkingHeight() > visible_columns.get(i).getWorkingHeight()) {
                        
//                        System.out.println(String.format("Setting top of %s to %d because of %s", 
//                            visible_columns.get(i).getColumnName(), workingCol.getWorkingHeight(), workingCol.getColumnName()));
                        
                        visible_columns.get(i).setWorkingHeight(workingCol.getWorkingHeight());
                        
                        cellCache.get(i).get(position_index[i]).setBeingProcessed(true);
                    }
                }
                
            }
            
            // Now that we've selected a cell for positioning, find its position
            // We want to place it from the current bottom of the column it is in
            // to the max bottom of the other columns so it encloses the other cells.
            
	    // Start at the current bottom of the column
            int t = workingCol.getWorkingHeight();
            
            // Add a front buffer to the cell or align to previous cell
            if(prevLaidCell != null && workingCell.getOnsetTicks() > prevLaidCell.getOnsetTicks()) {
                if(t < prevLaidCell.getY() + gapSize) {
                    t = prevLaidCell.getY() + gapSize;
                }
            } else if(prevLaidCell != null && workingCell.getOnsetTicks() == prevLaidCell.getOffsetTicks()) {
                t = prevLaidCell.getY() + prevLaidCell.getHeight();
            }
            
            // Check to see if there is a gap between these two cells.
            // If so, then use the top of the prevCell + gapsize to lay this one out.
            if(prevLaidCell != null && workingCell.getOnsetTicks() - prevLaidCell.getOffsetTicks() > 1) {
                long prevColCellOffset;
                if(position_index[currColIndex] > 0) {
                    prevColCellOffset = cellCache.get(currColIndex).get(position_index[currColIndex]-1).getOffsetTicks();
                }
                else {
                    prevColCellOffset = -1;
                }
                if(prevLaidCol == workingCol) {
                    t = prevLaidCell.getY() + prevLaidCell.getHeight() + 10;
                } else if(prevColCellOffset == -1 || workingCell.getOnsetTicks() - prevColCellOffset > 1) {
                    t = prevLaidCell.getY() + prevLaidCell.getHeight() + 10;
                }
		
            }
	    
	    // Check to see if the previous cell we laid in this column's bottom touches the top of this one.
	    // If this is the case, and the prev cell offset is not the same as the working onset,
	    // add a small gap.
	    if(prevRowCells[currColIndex] != null) {
		    SpreadsheetCell prevCell = prevRowCells[currColIndex];
		    if(prevCell.getY() + prevCell.getSize().height == t &&
		       (prevCell.getOffsetTicks() != workingCell.getOnsetTicks() &&
			prevCell.getOffsetTicks() + 1 != workingCell.getOnsetTicks())) {
			t += 5;    
		    }
	    }
            
            // Calculate b
	    // Add a minimum size to the bottom, otherwise we won't make room for
	    // new cells that are within the one we are placing
            int b = Collections.max(column_bottoms);
	    if(prevLaidCell != null && workingCell.getOffsetTicks() == prevLaidCell.getOffsetTicks()) {
		b = prev_b;
	    } else if (prevLaidCell != null && workingCell.getOffsetTicks() > prevLaidCell.getOffsetTicks()) {
                b += gapSize;
            }
            
	    if(b < t + workingCell.getPreferredSize().height) {
                b = t + workingCell.getPreferredSize().height;
            }
            
             // Update the previous dimensions and get ready to lay them next time around.
            column_bottoms.clear();
	    prevRowCells[currColIndex] = workingCell;
	    
            position_index[currColIndex]++;
            prevColIndex = currColIndex;
            prevLaidCell = workingCell;
            prevLaidCol = workingCol;
            prev_t = t;
            prev_b = b;
            maxHeight = Math.max(workingCell.getY() + workingCell.getHeight(), maxHeight);
            laidCells++;
//            System.out.println(String.format("Laid Cells: %d\tTotal Cells: %d",laidCells, totalCells));
            
            
            // Lay out the final cell
            if(laidCells == totalCells) {
                workingCell.setBounds(0, t, (workingCol.getWidth() - marginSize), (b - t));
                workingCol.setWorkingHeight(b);
                workingCell.setBeingProcessed(false);
            }
        }
        
//        long endtime = System.currentTimeMillis();
//        System.out.println(endtime-starttime);

        // Pad the columns so that they are all the same length.
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
	
	for(SpreadsheetColumn c : mainView.getColumns()) {
	    if(c.isVisible()) {
		    viscolumns.add(c);
	    }
	}
	
	return viscolumns;
    }
}
