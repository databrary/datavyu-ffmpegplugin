package au.com.nicta.openshapa.views.discrete;

import au.com.nicta.openshapa.db.DataCell;
import au.com.nicta.openshapa.db.Matrix;
import java.awt.event.KeyEvent;

/**
 *
 * @author cfreeman
 */
public final class QuoteStringDataValueView extends DataValueView {

    QuoteStringDataValueView(final DataCell cell,
                             final Matrix matrix,
                             final int matrixIndex,
                             final boolean editable) {
        super(cell, matrix, matrixIndex, editable);
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }
}
