package org.openshapa.views.discrete.datavalues;

import javax.swing.text.JTextComponent;
import org.openshapa.db.DataCell;
import org.openshapa.db.Matrix;
import org.openshapa.db.PredDataValue;
import org.openshapa.db.SystemErrorException;
import org.openshapa.db.TextStringDataValue;

/**
 * This class is the character editor of a TextStringDataValue.
 */
public final class TextStringDataValueEditor extends DataValueEditor {

    /**
     * Constructor.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param cell The parent data cell this editor resides within.
     * @param matrix Matrix holding the datavalue this editor will represent.
     * @param matrixIndex The index of the datavalue within the matrix.
     */
    public TextStringDataValueEditor(final JTextComponent ta,
                            final DataCell cell,
                            final Matrix matrix,
                            final int matrixIndex) {
        super(ta, cell, matrix, matrixIndex);
        setAcceptReturnKey(true);
    }

    /**
     * Constructor.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param cell The parent data cell this editor resides within.
     * @param p The predicate holding the datavalue this editor will represent.
     * @param pi The index of the datavalue within the predicate.
     * @param matrix Matrix holding the datavalue this editor will represent.
     * @param matrixIndex The index of the datavalue within the matrix.
     */
    public TextStringDataValueEditor(final JTextComponent ta,
                            final DataCell cell,
                            final PredDataValue p,
                            final int pi,
                            final Matrix matrix,
                            final int matrixIndex) {
        super(ta, cell, p, pi, matrix, matrixIndex);
        setAcceptReturnKey(true);
    }

    /**
     * Update the model to reflect the value represented by the
     * editor's text representation.
     */
    @Override
    public void updateModelValue() {
        TextStringDataValue dv = (TextStringDataValue) getModel();
        try {
            dv.setItsValue(getText());
        } catch (SystemErrorException e) {
            // logger
        }
    }

    /**
     * Sanity check the current text of the editor and return a boolean.
     * @return true if the text is an okay representation for this DataValue.
     */
    @Override
    public boolean sanityCheck() {
        boolean res = true;
        // could call a subRange test for this dataval?
        return res;
    }
}