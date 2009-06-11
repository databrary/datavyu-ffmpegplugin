package org.openshapa.views.discrete;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.Box.Filler;
import javax.swing.BoxLayout;
import org.openshapa.util.Constants;

/**
 * ColumnDataPanel panel that contains the SpreadsheetCell panels.
 */
public class ColumnDataPanel extends SpreadsheetElementPanel {

    /** Width of the column. */
    private int columnWidth;

    /** Provides a strut to leave a gap at the bottom of the panel. */
    private Component bottomStrut;

    /**
     * Creates a new ColumnDataPanel.
     * @param width Width of the column.
     */
    public ColumnDataPanel(final int width) {
        super();
        columnWidth = width;

        Dimension d = new Dimension(0, Constants.BOTTOM_MARGIN);
        bottomStrut = new Filler(d, d, d);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.BLACK));
        this.add(bottomStrut, -1);
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
        super.add(comp, this.getComponentCount() - 1);
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
        for (int i = 0; i < this.getComponentCount(); i++) {
            if (components[i].isFocusOwner()) {
                if (ke.getKeyCode() == KeyEvent.VK_UP && i > 0) {
                    components[i - 1].requestFocus();
                }

                if (ke.getKeyCode() == KeyEvent.VK_DOWN
                                        && (i + 1) < this.getComponentCount()) {
                    components[i + 1].requestFocus();
                }
            }
        }
    }
}
