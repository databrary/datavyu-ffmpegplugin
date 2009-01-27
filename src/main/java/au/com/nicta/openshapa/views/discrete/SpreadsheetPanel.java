package au.com.nicta.openshapa.views.discrete;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JPanel;

/**
 * A JPanel which can be used for piecing together spreadsheet elements, it will
 * correctly pass on mouse events.
 *
 * @author cfreeman
 */
public class SpreadsheetPanel extends JPanel implements MouseListener {

    /**
     * Constructor.
     */
    public SpreadsheetPanel() {
        super();
        this.addMouseListener(this);
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
    }

    /**
     * Processes Mouse Events that have been dispatched this component.
     *
     * @param me The mouse event that was dispatched to this component.
     */
    @Override
    public void processMouseEvent(MouseEvent me) {
        MouseListener[] list = this.getMouseListeners();

        for (int i = 0; i < list.length && !me.isConsumed(); i++) {
            switch (me.getID()) {
                case MouseEvent.MOUSE_CLICKED:
                    list[i].mouseClicked(me);
                    break;
                case MouseEvent.MOUSE_ENTERED:
                    list[i].mouseEntered(me);
                    break;
                case MouseEvent.MOUSE_EXITED:
                    list[i].mouseExited(me);
                    break;
                case MouseEvent.MOUSE_PRESSED:
                    list[i].mousePressed(me);
                    break;
                default:
                    list[i].mouseReleased(me);
            }
        }

        if (!me.isConsumed()) {
            me.translatePoint(this.getX(), this.getY());
            this.getParent().dispatchEvent(me);
        }
    }
}
