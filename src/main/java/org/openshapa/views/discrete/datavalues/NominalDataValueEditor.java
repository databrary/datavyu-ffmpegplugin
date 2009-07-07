package org.openshapa.views.discrete.datavalues;

import org.openshapa.db.DataCell;
import org.openshapa.db.Matrix;
import java.awt.event.KeyEvent;
import javax.swing.text.JTextComponent;
import org.apache.log4j.Logger;
import org.openshapa.db.NominalDataValue;
import org.openshapa.db.PredDataValue;
import org.openshapa.db.SystemErrorException;

/**
 * This class is the character editor of a NominalDataValue.
 */
public final class NominalDataValueEditor extends DataValueEditor {

    /** String holding the reserved characters. */
    private static final String NOMINAL_RESERVED_CHARS = ")(<>|,;";

    /** The logger for this class. */
    private static Logger logger = Logger
                                   .getLogger(NominalDataValueEditor.class);

    /**
     * Constructor.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param cell The parent data cell this editor resides within.
     * @param matrix Matrix holding the datavalue this editor will represent.
     * @param matrixIndex The index of the datavalue within the matrix.
     */
    public NominalDataValueEditor(final JTextComponent ta,
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
    public NominalDataValueEditor(final JTextComponent ta,
                                  final DataCell cell,
                                  final PredDataValue p,
                                  final int pi,
                                  final Matrix matrix,
                                  final int matrixIndex) {
        super(ta, cell, p, pi, matrix, matrixIndex);
    }

    /**
     * The action to invoke when a key is typed.
     * @param e The KeyEvent that triggered this action.
     */
    @Override
    public void keyTyped(final KeyEvent e) {
        super.keyTyped(e);

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
    public boolean isReserved(final char aChar) {
        return (NOMINAL_RESERVED_CHARS.indexOf(aChar) >= 0);
    }

    /**
     * Update the model to reflect the value represented by the
     * editor's text representation.
     */
    @Override
    public void updateModelValue() {
        NominalDataValue dv = (NominalDataValue) getModel();
        try {
            dv.setItsValue(getText());
        } catch (SystemErrorException e) {
            logger.error("Unable to edit nominal value", e);
        }
    }

    /**
     * Sanity check the current text of the editor and return a boolean.
     * @return true if the text is an okay representation for this DataValue.
     */
    @Override
    public boolean sanityCheck() {
        boolean res = true;
        // could call a subRange test for this dataval
        return res;
    }
}