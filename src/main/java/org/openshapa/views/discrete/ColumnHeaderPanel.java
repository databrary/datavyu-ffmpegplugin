package org.openshapa.views.discrete;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import org.openshapa.Configuration;

/**
 * ColumnHeaderPanel displays the column variable name.
 * Public for use by UISpec4J
 */
public class ColumnHeaderPanel extends JLabel implements Selectable, MouseListener {

    /** Selected state. */
    private boolean selected = false;

    /** SpreadsheetColumn this header is part of. */
    private SpreadsheetColumn parentCol;

    /** The current column selection. */
    private Selector selection;

    /** Background color of the header when unselected. */
    private Color backColor;

    /**
     * Creates new ColumnHeaderPanel.
     * @param col SpreadsheetColumn this header is part of.
     * @param text String to display
     * @param selector The selection for all columns.
     */
    public ColumnHeaderPanel(final SpreadsheetColumn col,
                             final String text,
                             final Selector selector) {
        super(text);

        parentCol = col;

        setOpaque(true);
        setHorizontalAlignment(JLabel.CENTER);
        setBorder(BorderFactory.createLineBorder(Color.black));
        backColor = getBackground();
        Dimension dim = col.getHeaderSize();
        setMinimumSize(dim);
        setPreferredSize(dim);
        setMaximumSize(dim);
        this.addMouseListener(this);
        selection = selector;
    }

    /**
     * Selectable Implementation.
     */

    /** set the selected state.
     * @param sel selected state.
     */
    public void setSelected(final boolean sel) {
        selected = sel;
        parentCol.setSelected(selected);
        if (selected) {
            setBackground(Configuration.getInstance().getSSSelectedColour());
        } else {
            setBackground(backColor);
        }
        repaint();
    }

    /**
     * @return selected state.
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * The action to invoke when the mouse enters this component.
     *
     * @param me The mouse event that triggered this action.
     */
    public void mouseEntered(MouseEvent me) {
    }

    /**
     * The action to invoke when the mouse exits this component.
     *
     * @param me The mouse event that triggered this action.
     */
    public void mouseExited(MouseEvent me) {
    }

    /**
     * The action to invoke when a mouse button is pressed.
     *
     * @param me The mouse event that triggered this action.
     */
    public void mousePressed(MouseEvent me) {
    }

    /**
     * The action to invoke when a mouse button is released.
     *
     * @param me The mouse event that triggered this action.
     */
    public void mouseReleased(MouseEvent me) {
    }

    /**
     * The action to invoke when a mouse button is clicked.
     *
     * @param me The mouse event that triggered this action.
     */
    public void mouseClicked(MouseEvent me) {
        selection.addToSelection(me, this);
        parentCol.requestFocus();
    }
}
