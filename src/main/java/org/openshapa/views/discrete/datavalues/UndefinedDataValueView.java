package org.openshapa.views.discrete.datavalues;

import org.openshapa.db.DataCell;
import org.openshapa.db.Matrix;
import org.openshapa.db.PredDataValue;
import org.openshapa.views.discrete.Editor;
import org.openshapa.views.discrete.Selector;
import java.awt.event.KeyEvent;

/**
 * A view representation for an undefined data value.
 */
public final class UndefinedDataValueView extends DataValueElementV {

    /**
     * Constructor.
     *
     * @param cellSelection The parent selector to use with this data value
     * view.
     * @param cell The parent cell that this data value view will reside within.
     * @param matrix The matrix holding the data value that this view will
     * represent.
     * @param matrixIndex The index of the data value within the matrix.
     * @param editable Is the value editable or not, true if it is editable,
     * false otherwise.
     */
    public UndefinedDataValueView(final Selector cellSelection,
                                  final DataCell cell,
                                  final Matrix matrix,
                                  final int matrixIndex,
                                  final boolean editable) {
        super(cellSelection, cell, matrix, matrixIndex, editable);
    }

    /**
     * Constructor.
     *
     * @param cellSelection The parent selector to use with this data value
     * view.
     * @param cell The parent cell that this data value view will reside within.
     * @param predicate The predicate holding the data value that this view will
     * represent.
     * @param predicateIndex Teh index of the data value within the predicate.
     * @param editable Is the value editable or not, true if it is editable,
     * false otherwise.
     */
    public UndefinedDataValueView(final Selector cellSelection,
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
        return new UndefinedEditor();
    }

    /**
     * Editor to use for the undefined data value.
     */
    class UndefinedEditor extends DataValueElementV.DataValueEditor {

        /**
         * The action to invoke when a key is typed.
         *
         * @param e The KeyEvent that triggered this action.
         */
        public void keyTyped(final KeyEvent e) {
            this.removeSelectedText();
        }
    }
}
