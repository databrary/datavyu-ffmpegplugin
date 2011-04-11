package org.openshapa.views.discrete.layouts;

import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JScrollPane;
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

        List<Integer> columnHeight = new ArrayList<Integer>();

        // This layout must be applied to a Spreadsheet panel.
        JScrollPane pane = (JScrollPane) parent;
        SpreadsheetView mainView = (SpreadsheetView) pane.getViewport()
                                                         .getView();

        int maxHeight = 0;
        for (SpreadsheetColumn col : mainView.getColumns()) {
            int ord = 1;
            int currentHeight = 0;
            for (SpreadsheetCell cell : col.getCells()) {
                Dimension d = cell.getPreferredSize();
                cell.setBounds(0,
                               currentHeight,
                               (col.getWidth() - marginSize),
                               (int) d.getHeight());
                cell.setOrdinal(ord);
                cell.repaint();
                ord++;
                currentHeight += d.getHeight();
            }

            // Put the new cell button at the end of the column.
            Dimension d = col.getDataPanel().getNewCellButton().getPreferredSize();
            col.getDataPanel().getNewCellButton().setBounds(0,
                                                            currentHeight,
                                                            parent.getWidth(),
                                                            (int) d.getHeight());
            currentHeight += (int) d.getHeight();
            columnHeight.add(currentHeight);
            maxHeight = Math.max(maxHeight, currentHeight);
        }

        // Pad the columns out at the bottom.
        maxHeight = Math.max(maxHeight, parent.getHeight());
        for (int i = 0; i < columnHeight.size(); i++) {
            SpreadsheetColumn col = mainView.getColumns().get(i);
            Integer colHeight = columnHeight.get(i);

            col.getDataPanel().setHeight(maxHeight);
            col.getDataPanel().getPadding().setBounds(0,
                                                      colHeight,
                                                      col.getWidth(),
                                                      (maxHeight - colHeight));
        }
    }
}