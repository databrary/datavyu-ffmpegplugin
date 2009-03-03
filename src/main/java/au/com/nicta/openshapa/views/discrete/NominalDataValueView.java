package au.com.nicta.openshapa.views.discrete;

import au.com.nicta.openshapa.db.DataCell;
import au.com.nicta.openshapa.db.Matrix;
import java.awt.event.KeyEvent;

/**
 * This class is the view representation of a NominalDataValue as stored within
 * the database.
 *
 * @author cfreeman
 */
public final class NominalDataValueView extends DataValueView {

    /**
     * Constructor.
     *
     * @param cellSelection The parent selection for spreadsheet cells.
     * @param cell The parent datacell for the nominal data value that this view
     * represents.
     * @param matrix The parent matrix for the nominal data value that this view
     * represents.
     * @param matrixIndex The index of the NominalDataValue within the above
     * parent matrix that this view represents.
     * @param editable Is the dataValueView editable by the user? True if the
     * value is permitted to be altered by the user. False otherwise.
     */
    NominalDataValueView(final Selector cellSelection,
                         final DataCell cell,
                         final Matrix matrix,
                         final int matrixIndex,
                         final boolean editable) {
        super(cellSelection, cell, matrix, matrixIndex, editable);
    }

    /**
     * The action to invoke when a key is typed.
     *
     * @param e The KeyEvent that triggered this action.
     */
    public void keyTyped(KeyEvent e) {
        this.removeSelectedText();
    }
}
