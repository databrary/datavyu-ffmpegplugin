package au.com.nicta.openshapa.views.discrete.datavalues;

import au.com.nicta.openshapa.db.DataCell;
import au.com.nicta.openshapa.db.Matrix;
import au.com.nicta.openshapa.views.discrete.Selector;
import java.util.Vector;
import java.awt.event.KeyEvent;
import javax.swing.JTextField;

/**
 *
 * @author cfreeman
 */
public final class PredicateDataValueView extends DataValueView {

    private JTextField predicateName;

    /** The data views used for each of the arguments. */
    private Vector<DataValueView> argViews;

    public PredicateDataValueView(final Selector cellSelection,
                                  final DataCell cell,
                                  final Matrix matrix,
                                  final int matrixIndex,
                                  final boolean editable) {
        super(cellSelection, cell, matrix, matrixIndex, editable);
        argViews = new Vector<DataValueView>();

        predicateName = new JTextField("blah");
        this.add(predicateName);
    }

    public void keyTyped(KeyEvent e) {
        this.removeSelectedText();
    }
}
