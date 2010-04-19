package org.openshapa.views.discrete.datavalues;

import javax.swing.text.JTextComponent;
import org.openshapa.models.db.DataCell;
import org.openshapa.models.db.Matrix;
import org.openshapa.models.db.PredDataValue;

/**
 * This class is the character editor of a UndefinedDataValue.
 */
public final class UndefinedDataValueEditor extends DataValueEditor {

    /**
     * Constructor.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param cell The parent data cell this editor resides within.
     * @param matrix Matrix holding the datavalue this editor will represent.
     * @param matrixIndex The index of the datavalue within the matrix.
     */
    public UndefinedDataValueEditor(final JTextComponent ta,
                            final DataCell cell,
                            final Matrix matrix,
                            final int matrixIndex) {
        super(ta, cell, matrix, matrixIndex);
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
    public UndefinedDataValueEditor(final JTextComponent ta,
                            final DataCell cell,
                            final PredDataValue p,
                            final int pi,
                            final Matrix matrix,
                            final int matrixIndex) {
        super(ta, cell, p, pi, matrix, matrixIndex);
    }
}