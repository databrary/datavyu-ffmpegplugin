package org.openshapa.views.discrete;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.Box.Filler;
import javax.swing.BoxLayout;

/**
 * ColumnDataPanel panel that contains the SpreadsheetCell panels.
 */
public class ColumnDataPanel extends SpreadsheetElementPanel {

    private int columnWidth;

    /** Provides a glue feature which fills from the bottom of the column.
     */
//    private Component stretcher;

    /**
     * Creates a new ColumnDataPanel.
     */
    public ColumnDataPanel(final int width) {
        super();
        columnWidth = width;

//        stretcher = new Filler(new Dimension(0, 0), new Dimension(0, 0),
//			  new Dimension(0, Short.MAX_VALUE));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.BLACK));
//        this.add(stretcher, -1);
    }

//    public Component add(Component comp) {
//        super.add(comp, this.getComponentCount() - 1);
//        return comp;
//    }

    /**
     * Set the width of the SpreadsheetCell.
     * @param width New width of the SpreadsheetCell.
     */
    public void setWidth(int width) {
        columnWidth = width;
    }

    @Override
    public final Dimension getMaximumSize() {
        return new Dimension(columnWidth, Short.MAX_VALUE);
    }

    @Override
    public final Dimension getMinimumSize() {
        return new Dimension(columnWidth, 0);
    }

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
    public void keyReleased(KeyEvent ke) {
        Component components[] = this.getComponents();
        for (int i = 0; i < this.getComponentCount(); i++) {
            if (components[i].isFocusOwner()) {
                if (ke.getKeyCode() == KeyEvent.VK_UP && i > 0) {
                    components[i - 1].requestFocus();
                }

                if (ke.getKeyCode() == KeyEvent.VK_DOWN && (i + 1) < this.getComponentCount()) {
                    components[i + 1].requestFocus();
                }
            }
        }
    }
}
