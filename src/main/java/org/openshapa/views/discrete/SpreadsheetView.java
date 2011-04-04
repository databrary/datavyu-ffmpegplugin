package org.openshapa.views.discrete;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

/**
 * SpreadsheetView implements the Scrollable interface and
 * is the view to use in the viewport of the JScrollPane in Spreadsheet.
 */
public class SpreadsheetView extends JPanel implements Scrollable {

    /** Maximum unit scroll amount. */
    private static final int MAX_UNIT_INCREMENT = 50;

    private List<SpreadsheetColumn> columns;

    /** Creates new form SpreadsheetView. */
    public SpreadsheetView() {
        columns = new ArrayList<SpreadsheetColumn>();
    }

    public void addColumn(final SpreadsheetColumn newColumn) {
        columns.add(newColumn);
        this.add(newColumn.getDataPanel());
    }

    public void removeColumn(final SpreadsheetColumn delColumn) {
        this.remove(delColumn.getDataPanel());
        columns.remove(delColumn);
    }

    public List<SpreadsheetColumn> getColumns() {
        return columns;
    }

    /**
     * Returns the preferred size of the viewport for a view component.
     * In this instance it returns getPreferredSize
     *
     * @return the preferredSize of a <code>JViewport</code> whose view
     *    is this <code>SpreadsheetView</code>
     */
    @Override
    public final Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    /**
     * @return False - the spreadsheet can scroll left to right if needed.
     */
    @Override
    public final boolean getScrollableTracksViewportWidth() {
        return false;
    }

    /**
     * @return False - the spreadsheet can scroll up and down if needed.
     */
    @Override
    public final boolean getScrollableTracksViewportHeight() {
        return false;
    }

    /**
     * Computes the scroll increment that will completely expose one new row
     * or column, depending on the value of orientation.
     *
     * @param visibleRect The view area visible within the viewport
     * @param orientation VERTICAL or HORIZONTAL.
     * @param direction Less than zero up/left, greater than zero down/right.
     * @return The "unit" increment for scrolling in the specified direction.
     *         This value should always be positive.
     */
    @Override
    public final int getScrollableUnitIncrement(final Rectangle visibleRect,
                                                final int orientation,
                                                final int direction) {
        //Get the current position.
        int currentPosition = 0;
        if (orientation == SwingConstants.HORIZONTAL) {
            currentPosition = visibleRect.x;
        } else {
            currentPosition = visibleRect.y;
        }

        //Return the number of pixels between currentPosition
        //and the nearest tick mark in the indicated direction.
        if (direction < 0) {
            int newPosition = currentPosition
                                - (currentPosition / MAX_UNIT_INCREMENT)
                                * MAX_UNIT_INCREMENT;
            if (newPosition == 0) {
                return MAX_UNIT_INCREMENT;
            } else {
                return newPosition;
            }
        } else {
            return ((currentPosition / MAX_UNIT_INCREMENT) + 1)
                   * MAX_UNIT_INCREMENT
                   - currentPosition;
        }
    }

    /**
     * Computes the block scroll increment that will completely expose a row
     * or column, depending on the value of orientation.
     *
     * @param visibleRect The view area visible within the viewport
     * @param orientation VERTICAL or HORIZONTAL.
     * @param direction Less than zero up/left, greater than zero down/right.
     * @return The "block" increment for scrolling in the specified direction.
     *         This value should always be positive.
     */
    @Override
    public final int getScrollableBlockIncrement(final Rectangle visibleRect,
                                                 final int orientation,
                                                 final int direction) {
        if (orientation == SwingConstants.HORIZONTAL) {
            return visibleRect.width - MAX_UNIT_INCREMENT;
        } else {
            return visibleRect.height - MAX_UNIT_INCREMENT;
        }
    }

}
