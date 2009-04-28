package au.com.nicta.openshapa.views.discrete.datavalues;

import au.com.nicta.openshapa.db.DataCell;
import au.com.nicta.openshapa.db.Matrix;
import au.com.nicta.openshapa.views.discrete.Editor;
import au.com.nicta.openshapa.views.discrete.Selector;

/**
 *
 * @author cfreeman
 */
public final class PredicateDataValueView extends DataValueElementV {

    //private JTextField predicateName;

    /** The data views used for each of the arguments. */
    //private Vector<DataValueV> argViews;

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
    public PredicateDataValueView(final Selector cellSelection,
                                  final DataCell cell,
                                  final Matrix matrix,
                                  final int matrixIndex,
                                  final boolean editable) {
        super(cellSelection, cell, matrix, matrixIndex, editable);
        //argViews = new Vector<DataValueV>();

        //predicateName = new JTextField("blah");
        //this.add(predicateName);
    }

    /**
     * @return Builds the editor to be used for this data value.
     */
    public Editor buildEditor() {
        return new PredicateEditor();
    }

    // Temp - will go when predicate gets implemented.
    class PredicateEditor extends Editor {

    }
}
