package au.com.nicta.openshapa.views.discrete;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JPanel;

/**
 * A JPanel which can be used for piecing together spreadsheet elements, it will
 * correctly pass on mouse events.
 *
 * @author cfreeman
 */
public class SpreadsheetPanel extends JPanel
implements MouseListener, KeyListener {

    /**
     * Constructor.
     */
    public SpreadsheetPanel() {
        super();
        this.addMouseListener(this);
        this.addKeyListener(this);
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
     * The action to invoke when a key is pressed on the keyboard
     *
     * @param ke The key event that triggered this action.
     */
    public void keyPressed(KeyEvent ke) {
    }

    /**
     * The action to invoke when a key is released on the keyboard.
     *
     * @param ke The key event that triggered this action.
     */
    public void keyReleased(KeyEvent ke) {
    }

    /**
     * The action to invoke when a key is typed on the keyboard.
     *
     * @param ke The key event that triggered this action.
     */
    public void keyTyped(KeyEvent ke) {
    }

    /**
     * Process key events that have been dispatched to this component, pass them
     * through to all listeners, and then if they are not consumed pass it onto
     * the parent of this component.
     *
     * @param ke They keyboard event that was dispatched to this component.
     */
    @Override
    public void processKeyEvent(KeyEvent ke) {
        super.processKeyEvent(ke);

        if (!ke.isConsumed() || ke.getKeyCode() == KeyEvent.VK_UP
            || ke.getKeyCode() == KeyEvent.VK_DOWN) {
            this.getParent().dispatchEvent(ke);
        }
    }

    /**
     * Processes Mouse Events that have been dispatched to this component, pass
     * them through to all listeners, and if they are not consumed pass it onto
     * the parent of this component.
     *
     * @param me The mouse event that was dispatched to this component.
     */
    @Override
    public void processMouseEvent(MouseEvent me) {
        super.processMouseEvent(me);

        if (!me.isConsumed()) {
            me.translatePoint(this.getX(), this.getY());
            this.getParent().dispatchEvent(me);
        }
    }
}
