package au.com.nicta.openshapa.views.discrete;

import au.com.nicta.openshapa.db.TimeStampDataValue;
import java.awt.event.KeyEvent;

/**
 *
 * @author cfreeman
 */
public final class TimeStampDataValueView extends DataValueView {

    /**
     *
     * @param timestamp
     * @param editable
     */
    TimeStampDataValueView(final TimeStampDataValue timestamp,
                           final boolean editable) {
        super(timestamp, editable);
    }

    public void handleKeyEvent(KeyEvent e) {
        if (this.isKeyStrokeNumeric(e)) {
            // Passed into editor.
        } else {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_BACK_SPACE:
                case KeyEvent.VK_DELETE:
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_RIGHT:
                    // Key needs to be passed into editor.
                    break;

                case KeyEvent.VK_UP:
                case KeyEvent.VK_DOWN:
                    // Key needs to be passed up to column to navigate cells.
                    e.consume();
                    break;

                default:
                    e.consume();
                    break;
            }
        }
    }

    /**
     *
     * @param e
     */
    public void keyPressed(KeyEvent e) {
        this.handleKeyEvent(e);
    }

    /**
     *
     * @param e
     */
    public void keyTyped(KeyEvent e) {
        this.handleKeyEvent(e);
    }

    /**
     *
     * @param e
     */
    public void keyReleased(KeyEvent e) {
        this.handleKeyEvent(e);
    }
}
