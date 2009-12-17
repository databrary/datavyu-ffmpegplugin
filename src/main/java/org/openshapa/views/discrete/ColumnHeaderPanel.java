package org.openshapa.views.discrete;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import org.openshapa.Configuration;

/**
 * ColumnHeaderPanel displays the column variable name.
 * Public for use by UISpec4J
 */
public final class ColumnHeaderPanel extends JLabel
implements Selectable, MouseListener, MouseMotionListener {

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
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.black));
        backColor = getBackground();
        Dimension dim = col.getHeaderSize();
        setMinimumSize(dim);
        setPreferredSize(dim);
        setMaximumSize(dim);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        selection = selector;
    }

    /**
     * Selectable Implementation.
     */

    /** set the selected state.
     * @param sel selected state.
     */
    public final void setSelected(final boolean sel) {
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
    public final boolean isSelected() {
        return selected;
    }

    /**
     * The action to invoke when the mouse enters this component.
     *
     * @param me The mouse event that triggered this action.
     */
    public void mouseEntered(final MouseEvent me) {
    }

    /**
     * The action to invoke when the mouse exits this component.
     *
     * @param me The mouse event that triggered this action.
     */
    public void mouseExited(final MouseEvent me) {
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * The action to invoke when a mouse button is pressed.
     *
     * @param me The mouse event that triggered this action.
     */
    public void mousePressed(final MouseEvent me) {
        selection.addToSelection(me, this);
        parentCol.requestFocus();
    }

    /**
     * The action to invoke when a mouse button is released.
     *
     * @param me The mouse event that triggered this action.
     */
    public void mouseReleased(final MouseEvent me) {
    }

    /**
     * The action to invoke when a mouse button is clicked.
     *
     * @param me The mouse event that triggered this action.
     */
    public void mouseClicked(final MouseEvent me) {

    }

    /**
     * The action to invoke when the mouse is dragged.
     *
     * @param me The mouse event that triggered this action
     */
    public void mouseDragged(final MouseEvent me) {
        // BugzID:660 - Implements columns dragging.
        if (draggable) {
            int newWidth = me.getX();
            if (newWidth >= this.getMinimumSize().width) {
                parentCol.setWidth(newWidth);
            }
        }
    }

    /**
     * The action to invoke when the mouse is moved.
     *
     * @param me The mouse event that triggered this action
     */
    public void mouseMoved(final MouseEvent me) {
        // BugzID:660 - Implements columns dragging.
        if (this.getSize().width - me.getX() < 4) {
            setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
            draggable = true;
        } else {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            draggable = false;
        }
    }

    /** Can the column be dragged? */
    private boolean draggable;
}
