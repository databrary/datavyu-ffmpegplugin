package au.com.nicta.openshapa.views.discrete.datavalues;

import au.com.nicta.openshapa.db.DataCell;
import au.com.nicta.openshapa.db.Matrix;
import au.com.nicta.openshapa.views.discrete.Editor;
import au.com.nicta.openshapa.views.discrete.Selector;
import java.awt.event.KeyEvent;

/**
 *
 * @author cfreeman
 */
public final class QuoteStringDataValueView extends DataValueElementV {

    /**
     * Constructor.
     *
     * @param cellSelection The parent cell selection.
     * @param cell The parent cell for this value timestamp.
     * @param matrix The parent matrix for which this is will act as a view for
     * one of its formal arguments.
     * @param matrixIndex The index of the formal argument within the parent
     * matrix that this will act as a view for.
     * @param editable Is the datavalue editable or not - true if it is editable
     * false otherwise.
     */
    public QuoteStringDataValueView(final Selector cellSelection,
                                    final DataCell cell,
                                    final Matrix matrix,
                                    final int matrixIndex,
                                    final boolean editable) {
        super(cellSelection, cell, matrix, matrixIndex, editable);
    }

    /**
     * @return Builds the editor to be used for this data value.
     */
    protected Editor buildEditor() {
        return new QuoteStringEditor();
    }

    /**
     * Editor for quote string data value view.
     */
    class QuoteStringEditor extends DataValueElementV.DataValueEditor {

        /**
         * The action to invoke when a key is typed.
         *
         * @param e The event that invoked this action.
         */
        public void keyTyped(final KeyEvent e) {
            this.removeSelectedText();
        }
    }
}
