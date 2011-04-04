package org.openshapa.views.discrete.layouts;

import java.awt.Component;
import java.util.List;
import javax.swing.ScrollPaneLayout;
import org.openshapa.views.discrete.ColumnDataPanel;
import org.openshapa.views.discrete.SpreadsheetColumn;

/**
 * SheetLayout - abstract class for spreadsheet layouts.
 */
public abstract class SheetLayout extends ScrollPaneLayout {

    /** The Spreadsheetcolumns in the spreadsheet. */
    private List<SpreadsheetColumn> columns;

    /**
     * SheetLayout constructor.
     */
    protected SheetLayout() {
        columns = null;
    }

    /**
     * Setup a reference to the SpreadsheetColumns in the spreadsheet.
     */
    protected void setColumns(List<SpreadsheetColumn> cols) {
        columns = cols;
    }

    /**
     * @return SpreadsheetColumns in the spreadsheet.
     */
    protected List<SpreadsheetColumn> getColumns() {
        return columns;
    }

    protected void padColumn(Component panelParent, ColumnDataPanel panel, int currentHeight, double ratio) {
        int finalHeight = currentHeight;

        // Find max height of adjacent columns.
        int adjacentHeight = 0;
        /*
        for (SpreadsheetColumn col : panel.getAdjacentColumns()) {
            int adjacentSize = (int) (col.getDataPanel().getPreferredSize().height / ratio);
            adjacentHeight = Math.max(adjacentSize, adjacentHeight);
        }*/

        // Find max column height.
        int columnHeight = currentHeight;
        int maxColumnHeight = Math.max(adjacentHeight, columnHeight);
        int containerHeight = panelParent.getParent().getParent().getHeight();

        if (containerHeight > maxColumnHeight) {
            panel.getPadding().setBounds(0,
                                         currentHeight, panelParent.getWidth(),
                                         (containerHeight - currentHeight));
            finalHeight += (containerHeight - columnHeight);
        } else {
            panel.getPadding().setBounds(0,
                                         currentHeight, panelParent.getWidth(),
                                         (maxColumnHeight - currentHeight));
            finalHeight += (maxColumnHeight - columnHeight);
        }

        panelParent.setBounds(panelParent.getX(),
                              panelParent.getY(),
                              panelParent.getWidth(),
                              finalHeight);
    }
}
