package org.openshapa.views.discrete.layouts;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.util.ArrayList;
import java.util.List;
import org.openshapa.util.Constants;
import org.openshapa.views.discrete.SpreadsheetEmptyCell;

/**
 * SheetLayoutOrdinal implements the ordinal style layout of SpreadsheetCells
 * in the spreadsheet.
 */
public class SheetLayoutOrdinal implements LayoutManager2 {
    // The list of all the other contents of this column.
    List<Component> layoutContents;

    // The of the right hand margin.
    int marginSize;

    // The new cell button to include at the bottom of the column.
    SpreadsheetEmptyCell newCellButton;

    /**
     * SheetLayoutOrdinal constructor.
     * @param cols Reference to the SpreadsheetColumns in the spreadsheet.
     */
    public SheetLayoutOrdinal(final int margin) {
        layoutContents = new ArrayList<Component>();
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
        for (Component c : layoutContents) {
            int width = Math.max(c.getWidth(), (int) result.getWidth());
            int height = ((int) result.getHeight()) + c.getHeight();

            result.setSize(width, height);
        }

        int width = Math.max(this.newCellButton.getWidth(), (int) result.getWidth());
        int height = ((int) result.getHeight()) + this.newCellButton.getHeight();
        height += Constants.BOTTOM_MARGIN;
        result.setSize(width, height);

        return result;
    }

    @Override
    public void removeLayoutComponent(Component comp) {
        layoutContents.remove(comp);
    }

    public void setEmptyCell(final SpreadsheetEmptyCell seCell) {
        this.newCellButton = seCell;
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
        if (comp instanceof SpreadsheetEmptyCell) {
            this.newCellButton = (SpreadsheetEmptyCell) comp;
        } else {
            layoutContents.add(comp);
        }
    }

    @Override
    public void addLayoutComponent(Component comp, Object constraints) {
        this.addLayoutComponent("", comp);
    }

    @Override
    public void invalidateLayout(Container target) {
        // Nothing to invalidate.
    }

    @Override
    public void layoutContainer(Container parent) {
        int currentHeight = 0;

        for (Component c : layoutContents) {
            Dimension d = c.getPreferredSize();
            c.setBounds(0, currentHeight, parent.getWidth() - marginSize, (int) d.getHeight());
            currentHeight += d.getHeight();
        }

        // Put the new cell button at the end of the column.
        Dimension d = newCellButton.getPreferredSize();
        this.newCellButton.setBounds(0, currentHeight, parent.getWidth(), (int) d.getHeight());

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
