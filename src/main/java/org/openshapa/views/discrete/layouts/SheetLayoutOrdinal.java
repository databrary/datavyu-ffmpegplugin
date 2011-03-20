package org.openshapa.views.discrete.layouts;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import org.openshapa.util.Constants;
import org.openshapa.views.discrete.ColumnDataPanel;
import org.openshapa.views.discrete.SpreadsheetEmptyCell;

/**
 * SheetLayoutOrdinal implements the ordinal style layout of SpreadsheetCells
 * in the spreadsheet.
 */
public class SheetLayoutOrdinal extends SheetLayout {
    // The of the right hand margin.
    int marginSize;

    /**
     * SheetLayoutOrdinal constructor.
     * @param cols Reference to the SpreadsheetColumns in the spreadsheet.
     */
    public SheetLayoutOrdinal(final int margin) {
        marginSize = margin;
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return preferredLayoutSize(parent);
    }

    @Override
    public Dimension maximumLayoutSize(Container parent) {
        return preferredLayoutSize(parent);
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        Dimension result = new Dimension(0, 0);

        // This layout can only be applied to Column Data Panels.
        ColumnDataPanel panel = (ColumnDataPanel) parent;

        for (Component c : panel.getCells()) {
            int width = Math.max(c.getWidth(), (int) result.getWidth());
            int height = ((int) result.getHeight()) + c.getHeight();

            result.setSize(width, height);
        }

        int width = Math.max(panel.getNewCellButton().getWidth(), (int) result.getWidth());
        int height = ((int) result.getHeight()) + panel.getNewCellButton().getHeight();
        height += Constants.BOTTOM_MARGIN;
        int containerHeight = parent.getParent().getParent().getHeight();
        
        result.setSize(width, Math.max(height, containerHeight));

        return result;
    }

    @Override
    public void removeLayoutComponent(Component comp) {
    }

    public void setEmptyCell(final SpreadsheetEmptyCell seCell) {
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
    }

    @Override
    public void addLayoutComponent(Component comp, Object constraints) {
    }

    @Override
    public void invalidateLayout(Container target) {
        // Nothing to invalidate.
    }

    @Override
    public void layoutContainer(Container parent) {
        int currentHeight = 0;

        // This layout can only be applied to Column Data Panels.
        ColumnDataPanel panel = (ColumnDataPanel) parent;

        for (Component c : panel.getCells()) {
            Dimension d = c.getPreferredSize();
            c.setBounds(0, currentHeight, parent.getWidth() - marginSize, (int) d.getHeight());
            currentHeight += d.getHeight();
        }

        // Put the new cell button at the end of the column.
        Dimension d = panel.getNewCellButton().getPreferredSize();
        panel.getNewCellButton().setBounds(0, currentHeight, parent.getWidth(), (int) d.getHeight());

        currentHeight += (int) d.getHeight();
        parent.setBounds(parent.getX(), parent.getY(), parent.getWidth(), currentHeight);
    }

    @Override
    public float getLayoutAlignmentX(Container target) {
        // Always align along the X-axis with respect to the origin.
        return 0.0f;
    }

    @Override
    public float getLayoutAlignmentY(Container target) {
        // Always align along the Y-axis with respect to the origin.
        return 0.0f;
    }
}
