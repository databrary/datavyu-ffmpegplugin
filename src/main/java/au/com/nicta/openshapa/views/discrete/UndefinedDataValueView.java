package au.com.nicta.openshapa.views.discrete;

import au.com.nicta.openshapa.db.UndefinedDataValue;
import java.awt.event.KeyEvent;

/**
 *
 * @author cfreeman
 */
public final class UndefinedDataValueView extends DataValueView {

    UndefinedDataValueView(final UndefinedDataValue undefinedDV,
                           final boolean editable) {
        super(undefinedDV, editable);
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
