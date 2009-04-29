package au.com.nicta.openshapa.views.discrete.datavalues;

import au.com.nicta.openshapa.db.DataCell;
import au.com.nicta.openshapa.db.Matrix;
import au.com.nicta.openshapa.db.PredDataValue;
import au.com.nicta.openshapa.views.discrete.Selector;

/**
 * A view representation for timestamp values as arguments / parameters of
 * matrices, predicates, etc.
 *
 * @author cfreeman
 */
public final class TimeStampValueView extends TimeStampDataValueView {

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
    public TimeStampValueView(final Selector cellSelection,
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
     * @param cell The parent cell for this timestamp.
     * @param predicate The parent predicate that holds the data value that this
     * view will represent.
     * @param predicateIndex The index of the formal argument within the parent
     * predicate that this will act as a view for.
     * @param editable Is the data value editable or not - true if it is
     * editable false otherwise.
     */
    public TimeStampValueView(final Selector cellSelection,
                              final DataCell cell,
                              final PredDataValue predicate,
                              final int predicateIndex,
                              final Matrix matrix,
                              final int matrixIndex,
                              final boolean editable) {
        super(cellSelection, cell, predicate, predicateIndex,
              matrix, matrixIndex, editable);
    }
}
