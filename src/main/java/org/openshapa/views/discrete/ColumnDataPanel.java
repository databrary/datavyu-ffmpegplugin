package org.openshapa.views.discrete;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.Box.Filler;
import javax.swing.BoxLayout;
import org.openshapa.util.Constants;
import org.openshapa.views.discrete.layouts.SheetLayoutFactory.SheetLayoutType;

/**
 * ColumnDataPanel panel that contains the SpreadsheetCell panels.
 */
public class ColumnDataPanel extends SpreadsheetElementPanel {

    /** Width of the column. */
    private int columnWidth;

    /** Provides a strut to leave a gap at the bottom of the panel. */
    private Component bottomStrut;

    /** Layout type for Ordinal and Weak Temporal Ordering. */
    private LayoutManager boxLayout;

    /**
     * Creates a new ColumnDataPanel.
     * @param width Width of the column.
     */
    public ColumnDataPanel(final int width) {
        super();
        columnWidth = width;

        Dimension d = new Dimension(0, Constants.BOTTOM_MARGIN);
        bottomStrut = new Filler(d, d, d);
        boxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(boxLayout);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.BLACK));
        this.add(bottomStrut, -1);
    }

    /**
     * resetLayout changes the layout manager depending on the SheetLayoutType.
     * @param type SheetLayoutType
     */
    public final void resetLayoutManager(final SheetLayoutType type) {
        if (type != SheetLayoutType.StrongTemporal) {
            setLayout(boxLayout);
            this.setPreferredSize(null);
        } else {
            setLayout(null);
        }
    }

    /**
     * Adds the specified component to this container at the given
     * position.
     * Overridden to keep the bottomStrut as the last component in the column.
     * @param comp Component to add.
     * @return Component added.
     */
    @Override
    public final Component add(final Component comp) {
        super.add(comp, getComponentCount() - 1);
        return comp;
    }

    /**
     * Set the width of the SpreadsheetCell.
     * @param width New width of the SpreadsheetCell.
     */
    public final void setWidth(final int width) {
        columnWidth = width;
    }

    /**
     * Override Maximum size to fix the width.
     * @return the maximum size of the data column.
     */
    @Override
    public final Dimension getMaximumSize() {
        return new Dimension(columnWidth, Short.MAX_VALUE);
    }

    /**
     * Override Minimum size to fix the width.
     * @return the minimum size of the data column.
     */
    @Override
    public final Dimension getMinimumSize() {
        return new Dimension(columnWidth, 0);
    }

    /**
     * Override Preferred size to fix the width.
     * @return the preferred size of the data column.
     */
    @Override
    public final Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        return new Dimension(columnWidth, size.height);
    }

    /**
     * The action to invoke when a key is released on the keyboard.
     *
     * @param ke The key event that triggered this action.
     */
    @Override
    public final void keyReleased(final KeyEvent ke) {
        Component[] components = this.getComponents();
        int numCells = getComponentCount() - 1;
        for (int i = 0; i < numCells; i++) {
            if (components[i].isFocusOwner()) {
                if (ke.getKeyCode() == KeyEvent.VK_UP && i > 0) {
                    components[i - 1].requestFocus();
                }
                if (ke.getKeyCode() == KeyEvent.VK_DOWN && (i + 1) < numCells) {
                    components[i + 1].requestFocus();
                }
            }
        }
    }
}
