package au.com.nicta.openshapa.views.discrete;

import au.com.nicta.openshapa.db.PredDataValue;
import java.awt.event.KeyEvent;

/**
 *
 * @author cfreeman
 */
public final class PredicateDataValueView extends DataValueView {

    PredicateDataValueView(final PredDataValue predicate,
                           final boolean editable) {
        super(predicate, editable);
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
