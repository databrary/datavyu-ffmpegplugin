package au.com.nicta.openshapa.views.discrete;

import au.com.nicta.openshapa.db.NominalDataValue;
import java.awt.event.KeyEvent;

/**
 *
 * @author cfreeman
 */
public final class NominalDataValueView extends DataValueView {

    NominalDataValueView(final NominalDataValue nominal,
                           final boolean editable) {
        super(nominal, editable);
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
