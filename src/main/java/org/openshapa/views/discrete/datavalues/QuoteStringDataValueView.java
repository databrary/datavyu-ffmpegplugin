package org.openshapa.views.discrete.datavalues;

import org.openshapa.db.DataCell;
import org.openshapa.db.Matrix;
import org.openshapa.db.PredDataValue;
import org.openshapa.views.discrete.Editor;
import org.openshapa.views.discrete.Selector;
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
     * Constructor.
     *
     * @param cellSelection The parent cell selection.
     * @param cell The parent cell for this value timestamp.
     * @param predicate The parent predicate that holds the data value that this
     * view will represent.
     * @param predicateIndex The index of the data value within the above
     * predicate that this view will represent.
     * @param editable Is thie datavalue editable or not - true if it is
     * editable, false otherwise.
     */
    public QuoteStringDataValueView(final Selector cellSelection,
                                    final DataCell cell,
                                    final PredDataValue predicate,
                                    final int predicateIndex,
                                    final Matrix matrix,
                                    final int matrixIndex,
                                    final boolean editable) {
        super(cellSelection, cell, predicate, predicateIndex,
              matrix, matrixIndex, editable);
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
