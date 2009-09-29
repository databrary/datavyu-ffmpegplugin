package org.openshapa.views.discrete;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JPanel;

/**
 * A JPanel which can be used for piecing together spreadsheet elements, it will
 * correctly pass on mouse events.
 */
public class SpreadsheetElementPanel extends JPanel
implements MouseListener {

    /**
     * Constructor.
     */
    public SpreadsheetElementPanel() {
        super();
        this.addMouseListener(this);
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
    }

    /**
     * The action to invoke when a mouse button is pressed.
     *
     * @param me The mouse event that triggered this action.
     */
    public void mousePressed(final MouseEvent me) {
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
     * Processes Mouse Events that have been dispatched to this component, pass
     * them through to all listeners, and if they are not consumed pass it onto
     * the parent of this component.
     *
     * @param me The mouse event that was dispatched to this component.
     */
    @Override
    public final void processMouseEvent(final MouseEvent me) {
        super.processMouseEvent(me);

        if (!me.isConsumed()) {
            me.translatePoint(this.getX(), this.getY());
            this.getParent().dispatchEvent(me);
        }
    }
}
