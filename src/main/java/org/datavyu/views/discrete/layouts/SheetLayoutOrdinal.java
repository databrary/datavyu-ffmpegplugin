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

import org.datavyu.Datavyu;
import org.datavyu.views.discrete.SpreadsheetCell;
import org.datavyu.views.discrete.SpreadsheetColumn;
import org.datavyu.views.discrete.SpreadsheetView;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SheetLayoutOrdinal implements the ordinal style layout of SpreadsheetCells
 * in the spreadsheet.
 */
public class SheetLayoutOrdinal extends SheetLayout {
    // The of the right hand margin.
    private int marginSize;
    private JScrollPane pane;

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
         * @param columnID     The ID of the column we are ordering.
         * @param columnHeight The height of the column in pixels
         */
        public ColInfo(final int columnID, final int columnHeight) {
            colID = columnID;
            colHeight = columnHeight;
        }
    }

    /**
     * SheetLayoutOrdinal constructor.
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

        Datavyu.getView().setRedraw(false);

        // This layout must be applied to a Spreadsheet panel.
        pane = (JScrollPane) parent;
        SpreadsheetView mainView = (SpreadsheetView) pane.getViewport()
                .getView();

        List<ColInfo> columnHeight = new ArrayList<ColInfo>();

        int maxHeight = 0;
        int selectedHeight = -1;
        int colID = 0;
        SpreadsheetCell selectedCell = null;
        for (SpreadsheetColumn col : mainView.getColumns()) {

            // Only layout 'visible' columns.
            if (col.isVisible()) {
                int ord = 1;
                int currentHeight = 0;
                SpreadsheetCell prevCell = null;

                for (SpreadsheetCell cell : col.getCellsTemporally()) {
                    if (cell == null) {
                        // We may have a race condition where a cell got deleted but
                        // we don't know about it yet
                        continue;
                    }
                    Dimension d = cell.getPreferredSize();
                    if (cell.getCell().isSelected()) {
                        selectedHeight = currentHeight;
                        selectedCell = cell;
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
                    
                    // Determine if this cell's onset is later than its (non-zero) offset
                    if (cell.isUpsideDown()){
                        cell.setOverlapBorder(true);
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

        if (selectedCell != null) {


        }
    }

    public void reorientView(SpreadsheetCell cell) {
        // Set the new position of the scroll window.

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
}