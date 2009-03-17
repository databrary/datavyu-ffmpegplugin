package au.com.nicta.openshapa.views.discrete.datavalues;

import au.com.nicta.openshapa.db.DataCell;
import au.com.nicta.openshapa.db.Matrix;
import au.com.nicta.openshapa.views.discrete.Selector;

/**
 * A view representation for timestamp values as arguments / parameters of
 * matrices, predicates, etc.
 *
 * @author cfreeman
 */
public final class TimeStampValueView extends TimeStampDataValueView {
    /**
     * Constructor
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
    public TimeStampValueView(final Selector cellSelection,
                              final DataCell cell,
                              final Matrix matrix,
                              final int matrixIndex,
                              final boolean editable) {
        super(cellSelection, cell, matrix, matrixIndex, editable);
    }
}
