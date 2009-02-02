package au.com.nicta.openshapa.views.discrete;

import au.com.nicta.openshapa.db.FloatDataValue;
import java.awt.event.KeyEvent;

/**
 *
 * @author cfreeman
 */
public final class FloatDataValueView extends DataValueView {

    /**
     *
     * @param timestamp
     * @param editable
     */
    FloatDataValueView(final FloatDataValue floatDataValue,
                           final boolean editable) {
        super(floatDataValue, editable);
    }

    /**
     *
     * @param e
     */
    public void keyPressed(KeyEvent e) {
    }

    /**
     *
     * @param e
     */
    public void keyTyped(KeyEvent e) {
    }

    /**
     *
     * @param e
     */
    public void keyReleased(KeyEvent e) {
    }
}
