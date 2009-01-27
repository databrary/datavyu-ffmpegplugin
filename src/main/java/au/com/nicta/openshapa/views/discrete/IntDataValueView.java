package au.com.nicta.openshapa.views.discrete;

import au.com.nicta.openshapa.db.IntDataValue;
import java.awt.event.KeyEvent;

/**
 *
 * @author cfreeman
 */
public final class IntDataValueView extends DataValueView {

    /**
     *
     * @param timestamp
     * @param editable
     */
    IntDataValueView(final IntDataValue timestamp,
                           final boolean editable) {
        super(timestamp, editable);
    }

    public void keyPressed(KeyEvent e) {
        e.consume();
    }

    public void keyReleased(KeyEvent e) {
        e.consume();
    }

    public void keyTyped(KeyEvent e) {
        e.consume();
    }
}
