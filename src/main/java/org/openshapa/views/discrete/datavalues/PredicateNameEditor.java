package org.openshapa.views.discrete.datavalues;

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import javax.swing.text.JTextComponent;
import org.openshapa.db.DataCell;
import org.openshapa.db.Matrix;
import org.openshapa.db.PredDataValue;
import org.openshapa.db.Predicate;
import org.openshapa.db.SystemErrorException;
import org.openshapa.views.discrete.EditorComponent;

/**
 * This class is the character editor of a Predicate name.
 * TODO: not finished. Extends EditorComponent for now to avoid
 * DataValueEditor issues.
 */
public final class PredicateNameEditor extends EditorComponent {

    /** String holding the reserved characters. */
    final static String PREDNAME_RESERVED_CHARS = ")(<>|,;";

    /**
     * Constructor.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param cell The parent data cell this editor resides within.
     * @param matrix Matrix holding the datavalue this editor will represent.
     * @param matrixIndex The index of the datavalue within the matrix.
     */
    public PredicateNameEditor(final JTextComponent ta,
                            final DataCell cell,
                            final Matrix matrix,
                            final int matrixIndex) {

        super(ta);
        setEditable(true);
        
        try {
            PredDataValue pdv = (PredDataValue) matrix.getArgCopy(matrixIndex);
            Predicate pred = pdv.getItsValue();
            this.setText(pred.getPredName());
        } catch (SystemErrorException e) {

        }
//        super(ta, cell, matrix, matrixIndex);
    }

    /**
     * Reset the values by retrieving from the database.
     * @param cell The Parent cell that holds the matrix.
     * @param matrix The parent matrix that holds the DataValue.
     */
//    @Override
    public void resetValue(final DataCell cell, final Matrix matrix) {
    }

    /**
     * The action to invoke when a key is typed.
     * @param e The KeyEvent that triggered this action.
     */
    @Override
    public void keyTyped(final KeyEvent e) {
//        super.keyTyped(e);

        if (!e.isConsumed()) {

            if (isReserved(e.getKeyChar())) {
                // Ignore reserved characters.
                e.consume();
            }
        }
    }

    /**
     * @param aChar Character to test
     * @return true if the character is a reserved character.
     */
    public boolean isReserved(char aChar) {
        return (PREDNAME_RESERVED_CHARS.indexOf(aChar) >= 0);
    }

    /**
     * Subclass overrides to handle focusLost events.
      * @param fe FocusEvent details.
    */
    @Override
    public void focusLost(final FocusEvent fe) {
    }

    /**
     * Subclass overrides to handle keyPressed events.
     * @param e KeyEvent details.
     */
    @Override
    public void keyPressed(final KeyEvent e) {

    }

    /**
     * Subclass overrides to handle keyReleased events.
     * @param e KeyEvent details.
     */
    @Override
    public void keyReleased(final KeyEvent e) {
        
    }

}