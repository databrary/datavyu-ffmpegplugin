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
import java.util.List;
import javax.swing.JScrollPane;
import org.openshapa.OpenSHAPA;
import org.openshapa.views.discrete.SpreadsheetCell;
import org.openshapa.views.discrete.SpreadsheetColumn;
import org.openshapa.views.discrete.SpreadsheetView;

/**
 * SheetLayoutOrdinal implements the ordinal style layout of SpreadsheetCells
 * in the spreadsheet.
 */
public class SheetLayoutOrdinal extends SheetLayout {
    // The of the right hand margin.
    private int marginSize;

    /**
     * Helper class for tracking what we are ordering.
     */
    private class ColInfo {
        // The ID of the column we are ordering.
        public int colID;

        // The height of the column in pixels.
        public int colHeight;

        /**
         * Constructor.
         *
         * @param columnID The ID of the column we are ordering.
         * @param columnHeight The height of the column in pixels
         */
        public ColInfo(final int columnID, final int columnHeight) {
            colID = columnID;
            colHeight = columnHeight;
        }
    }

    /**
     * SheetLayoutOrdinal constructor.
     * @param cols Reference to the SpreadsheetColumns in the spreadsheet.
     */
    public SheetLayoutOrdinal(final int margin) {
        marginSize = margin;
    }

    /**
     * Lays the container - positioning the cells within.
     *
     * @param parent The parent component for the container.
     */
    @Override
    public void layoutContainer(Container parent) {
        super.layoutContainer(parent);
        
        OpenSHAPA.getView().setRedraw(false);

        // This layout must be applied to a Spreadsheet panel.
        JScrollPane pane = (JScrollPane) parent;
        SpreadsheetView mainView = (SpreadsheetView) pane.getViewport()
                                                         .getView();

        List<ColInfo> columnHeight = new ArrayList<ColInfo>();

        int maxHeight = 0;
        int selectedHeight = -1;
        int colID = 0;
        for (SpreadsheetColumn col : mainView.getColumns()) {

            // Only layout 'visible' columns.
            if (col.isVisible()) {
                int ord = 1;
                int currentHeight = 0;
                SpreadsheetCell prevCell = null;
                
                for (SpreadsheetCell cell : col.getCellsTemporally()) {
		    if(cell == null) {
		        // We may have a race condition where a cell got deleted but
			// we don't know about it yet
			continue;
		    }
                    Dimension d = cell.getPreferredSize();
                    if (cell.getCell().isSelected() && currentHeight != cell.getBounds().y) {
                        selectedHeight = currentHeight;
                    }

                    cell.setBounds(0,
                                   currentHeight,
                                   (col.getWidth() - marginSize),
                                   (int) d.getHeight());
                    cell.setOrdinal(ord);
                    cell.repaint();
                    ord++;
                    currentHeight += d.getHeight();

                    // Determine if this cell overlaps with the previous cell.
                    if (prevCell != null) {
                        if (prevCell.getOffsetTicks() > cell.getOnsetTicks()) {
                            prevCell.setOverlapBorder(true);
                        } else {
                            prevCell.setOverlapBorder(false);
                        }
                    }

                    prevCell = cell;
                }

                // Put the new cell button at the end of the column.
                Dimension d = col.getDataPanel().getNewCellButton().getPreferredSize();
                col.getDataPanel().getNewCellButton().setBounds(0,
                                                                currentHeight,
                                                                parent.getWidth(),
                                                                (int) d.getHeight());
                currentHeight += (int) d.getHeight();
                columnHeight.add(new ColInfo(colID, currentHeight));
                maxHeight = Math.max(maxHeight, currentHeight);
            }

            colID++;
        }

        // Pad the columns out at the bottom.
        maxHeight = Math.max(maxHeight, parent.getHeight());
        for (int i = 0; i < columnHeight.size(); i++) {
            SpreadsheetColumn col = mainView.getColumns().get(columnHeight.get(i).colID);
            Integer colHeight = columnHeight.get(i).colHeight;

            col.getDataPanel().setHeight(maxHeight);
            col.getDataPanel().getPadding().setBounds(0,
                                                      colHeight,
                                                      col.getWidth(),
                                                      (maxHeight - colHeight));
        }

        if (selectedHeight != -1) {
            // Determine the cell position relative to the entire column.
            double cellPos = 0;
            if (maxHeight > 0) {
                cellPos = (selectedHeight / (double) maxHeight);
            }

            // Determine the new scroll position to ensure the focused cell is highlighted.
            int newPos = (int) (cellPos * (pane.getVerticalScrollBar().getMaximum() - pane.getVerticalScrollBar().getVisibleAmount()));
            // Make sure the position is within a valid bound.
            newPos = Math.max(0, newPos);
            newPos = Math.min(newPos, pane.getVerticalScrollBar().getMaximum());

            // Set the new position of the scroll window.
            pane.getVerticalScrollBar().setValue(newPos);
        }
    }
}