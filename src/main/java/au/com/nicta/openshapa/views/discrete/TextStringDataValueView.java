package au.com.nicta.openshapa.views.discrete;

import au.com.nicta.openshapa.db.TextStringDataValue;
import java.awt.event.KeyEvent;

/**
 *
 * @author cfreeman
 */
public final class TextStringDataValueView extends DataValueView {

    TextStringDataValueView(final TextStringDataValue textString,
                           final boolean editable) {
        super(textString, editable);
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
