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
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }
}
