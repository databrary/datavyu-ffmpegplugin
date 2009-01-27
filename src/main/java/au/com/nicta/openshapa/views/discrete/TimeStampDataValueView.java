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

    /**
     *
     * @param e
     */
    public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
            case KeyEvent.VK_0:
            case KeyEvent.VK_1:
            case KeyEvent.VK_2:
            case KeyEvent.VK_3:
            case KeyEvent.VK_4:
            case KeyEvent.VK_5:
            case KeyEvent.VK_6:
            case KeyEvent.VK_7:
            case KeyEvent.VK_8:
            case KeyEvent.VK_9:
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
                // Key needs to be passed into editor.
                break;

            case KeyEvent.VK_UP:
            case KeyEvent.VK_DOWN:
                // Key needs to be passed to column to navigate to another cell.
                e.consume();
                break;

            default:
                e.consume();
                break;
        }
    }

    /**
     *
     * @param e
     */
    public void keyTyped(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_0:
            case KeyEvent.VK_1:
            case KeyEvent.VK_2:
            case KeyEvent.VK_3:
            case KeyEvent.VK_4:
            case KeyEvent.VK_5:
            case KeyEvent.VK_6:
            case KeyEvent.VK_7:
            case KeyEvent.VK_8:
            case KeyEvent.VK_9:
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
                // Key needs to be passed into editor.
                break;

            case KeyEvent.VK_UP:
            case KeyEvent.VK_DOWN:
                // Key needs to be passed to column to navigate to another cell.
                e.consume();
                break;

            default:
                e.consume();
                break;
        }
    }

    /**
     *
     * @param e
     */
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_0:
            case KeyEvent.VK_1:
            case KeyEvent.VK_2:
            case KeyEvent.VK_3:
            case KeyEvent.VK_4:
            case KeyEvent.VK_5:
            case KeyEvent.VK_6:
            case KeyEvent.VK_7:
            case KeyEvent.VK_8:
            case KeyEvent.VK_9:
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
                // Key needs to be passed into editor.
                break;

            case KeyEvent.VK_UP:
            case KeyEvent.VK_DOWN:
                // Key needs to be passed to column to navigate to another cell.
                e.consume();
                break;

            default:
                e.consume();
                break;
        }
    }
}
