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

    /**
     * String holding the reserved characters - these are characters that are
     * users are unable to enter into a nominal field.
     */
    // BugzID:524 - If Character is an escape key - ignore it.
    private static final String RESERVED_CHARS = ")(<>|,;\t\r\n\"\u001B";

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
     *
     * @param e The KeyEvent that triggered this action.
     */
    @Override
    public void keyTyped(final KeyEvent e) {
        super.keyTyped(e);

        // Just a regular vanilla keystroke - insert it into nominal field.
        NominalDataValue ndv = (NominalDataValue) getModel();

        if (!e.isConsumed() && !e.isMetaDown() && !e.isControlDown()
            && !isReserved(e.getKeyChar())) {
            this.removeSelectedText();
            StringBuffer currentValue = new StringBuffer(getText());

            // If we have a delete or backspace key - do not insert.
            if (!(e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
                  && e.getKeyChar() == '\u007F') &&
                !(e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
                  && e.getKeyChar() == '\u0008')) {
                currentValue.insert(getCaretPosition(), e.getKeyChar());
            }

            // Advance caret over the top of the new char.
            int pos = this.getCaretPosition() + 1;
            this.setText(currentValue.toString());
            this.setCaretPosition(pos);
            e.consume();

        // All other keystrokes are consumed.
        } else {
            e.consume();
        }

        // Push the character changes into the database.
        try {
            ndv.setItsValue(this.getText());
            updateDatabase();
        } catch (SystemErrorException se) {
            logger.error("Unable to edit text string", se);
        }
    }

    /**
     * @param aChar Character to test
     * @return true if the character is a reserved character.
     */
    public boolean isReserved(final char aChar) {
        return (RESERVED_CHARS.indexOf(aChar) >= 0);
    }
}