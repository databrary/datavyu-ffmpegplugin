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
import org.openshapa.OpenSHAPA;

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

    /** Background color of the header when unselected. */
    private Color backColor;

    /**
     * Creates new ColumnHeaderPanel.
     *
     * @param col SpreadsheetColumn this header is part of.
     * @param text String to display.
     */
    public ColumnHeaderPanel(final SpreadsheetColumn col,
                             final String text) {
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
    }

    /**
     * set the selected state.
     *
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
        if (moveable) {
             setCursor(new Cursor(Cursor.MOVE_CURSOR));
             final int columnWidth = this.getSize().width;
             if (me.getX() > columnWidth) {
                 int positions = Math.round((me.getX() * 1F) / (columnWidth * 1F));
                 SpreadsheetPanel sp = (SpreadsheetPanel) OpenSHAPA
                         .getApplication().getMainView().getComponent();
                 sp.moveColumnRight(parentCol.getColID(), positions);
             } else if (me.getX() < 0) {
                 int positions = Math.round((me.getX() * -1F) / (columnWidth * 1F));
                 SpreadsheetPanel sp = (SpreadsheetPanel) OpenSHAPA
                         .getApplication().getMainView().getComponent();
                 sp.moveColumnLeft(parentCol.getColID(), positions);
             }
        }
    }

    /**
     * The action to invoke when the mouse is moved.
     *
     * @param me The mouse event that triggered this action
     */
    public void mouseMoved(final MouseEvent me) {
        final int xCoord = me.getX();
        final int componentWidth = this.getSize().width;
        final int rangeStart = Math.round(componentWidth / 4F);
        final int rangeEnd = Math.round(3F * componentWidth / 4F);

        // BugzID:660 - Implements columns dragging.
        if (componentWidth - xCoord < 4) {
            setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
            draggable = true;
        // BugzID:128 - Implements moveable columns
        } else if ((rangeStart <= xCoord) && (xCoord <= rangeEnd)) {
            moveable = true;
        } else {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            draggable = false;
            moveable = false;
        }
    }

    /** Can the column be dragged? */
    private boolean draggable;
    /** Can the column be moved? */
    private boolean moveable;
}
