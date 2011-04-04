package org.openshapa.views.discrete.layouts;

import java.awt.Container;
import java.awt.Dimension;
import java.util.List;
import org.openshapa.views.discrete.ColumnDataPanel;
import org.openshapa.views.discrete.SpreadsheetCell;


/**
 * SheetLayoutWeakTemporal - mimics the weak temporal ordering style from
 * original MacSHAPA.
 */
public class SheetLayoutWeakTemporal extends SheetLayout {
    // The of the right hand margin.
    int marginSize;

    /**
     * SheetLayoutOrdinal constructor.
     * @param cols Reference to the SpreadsheetColumns in the spreadsheet.
     */
    public SheetLayoutWeakTemporal(final int margin) {
        marginSize = margin;
    }

    @Override
    public void layoutContainer(Container parent) {
        super.layoutContainer(parent);
        int currentHeight = 0;

        // This layout can only be applied to Column Data Panels.
        ColumnDataPanel panel = (ColumnDataPanel) parent;
        List<SpreadsheetCell> cells = panel.getCellsTemporally();

        long totalHeight = preferredLayoutSize(parent).height;
        long temporalSize = 0;
        if (cells.size() > 0) {
            temporalSize = cells.get((cells.size() - 1)).getOffsetTicks();
        }

        /*
        // Find the largest column size and temporal size:
        for (SpreadsheetColumn c : panel.getAdjacentColumns()) {
            totalHeight = Math.max(totalHeight, c.getDataPanel().getPreferredSize().height);
            List<SpreadsheetCell> colCells = c.getCellsTemporally();
            if (colCells.size() > 0) {
                temporalSize = Math.max(temporalSize, colCells.get((colCells.size() - 1)).getOffsetTicks());
            }
        }*/
        double ratio = temporalSize / (float) totalHeight;

        System.err.println("Max temp: " + temporalSize);
        System.err.println("Max ratio: " + ratio);
        System.err.println("Total Height: " + totalHeight);

        int ord = 1;
        System.err.println("Laying out column");
        for (SpreadsheetCell c : panel.getCellsTemporally()) {
            Dimension d = c.getPreferredSize();
            int t = (int) (c.getOnsetTicks() / ratio);
            t = t + Math.max((currentHeight - t), 0);

            int b = (int) (c.getOffsetTicks() / ratio);
            b = Math.max((b - t), d.height);

            //c.setBounds(0, currentHeight, parent.getW idth() - marginSize, (int) d.getHeight());
            c.setBounds(0, t, parent.getWidth() - marginSize, b);
            //System.err.println("cell: [" + 0 + ", " + t + ", " + (parent.getWidth() - marginSize) + ", "+ b + "]");
            
            c.setOrdinal(ord);
            ord++;
            //currentHeight += d.getHeight();
            currentHeight = t + b;
        }

        //System.out.println("currentHeight: " + currentHeight);

        // Put the new cell button at the end of the column.
        Dimension d = panel.getNewCellButton().getPreferredSize();
        panel.getNewCellButton().setBounds(0, currentHeight, parent.getWidth(), (int) d.getHeight());
        currentHeight += (int) d.getHeight();

        System.err.println("CurrentHeight: " + currentHeight);
        padColumn(parent, panel, currentHeight, ratio);
    }
}
