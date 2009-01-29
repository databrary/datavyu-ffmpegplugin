package au.com.nicta.openshapa.views.discrete;

import au.com.nicta.openshapa.db.QuoteStringDataValue;
import java.awt.event.KeyEvent;

/**
 *
 * @author cfreeman
 */
public final class QuoteStringDataValueView extends DataValueView {

    QuoteStringDataValueView(final QuoteStringDataValue quoteString,
                           final boolean editable) {
        super(quoteString, editable);
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
