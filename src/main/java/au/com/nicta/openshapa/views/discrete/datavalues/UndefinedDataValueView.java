package au.com.nicta.openshapa.views.discrete.datavalues;

import au.com.nicta.openshapa.db.DataCell;
import au.com.nicta.openshapa.db.Matrix;
import au.com.nicta.openshapa.views.discrete.Editor;
import au.com.nicta.openshapa.views.discrete.Selector;
import java.awt.event.KeyEvent;

/**
 * A view representation for an undefined data value.
 *
 * @author cfreeman
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
         * The action to invoke when a key is released.
         *
         * @param e The KeyEvent that triggered this action.
         */
        @Override
        public void keyReleased(final KeyEvent e) {
            // Ignore key release.
        }

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
