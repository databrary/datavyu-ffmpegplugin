package org.openshapa.views.discrete.datavalues;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;
import java.awt.event.KeyEvent;
import javax.swing.text.JTextComponent;
import org.openshapa.models.db.DataCell;
import org.openshapa.models.db.Matrix;
import org.openshapa.models.db.PredDataValue;
import org.openshapa.models.db.SystemErrorException;
import org.openshapa.models.db.TextStringDataValue;

/**
 * This class is the character editor of a TextStringDataValue.
 */
public final class TextStringDataValueEditor extends DataValueEditor {

    /**
     * String holding the reserved characters - these are characters that are
     * users are unable to enter into a text field.
     */
    // BugzID:524 - If Character is an escape key - ignore it.
    private static final String RESERVED_CHARS = "\u001B\t";

    /** The logger for this class. */
    private Logger logger = UserMetrix.getLogger(TextStringDataValueEditor.class);

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
     * The action to invoke when a key is typed.
     *
     * @param e The KeyEvent that triggered this action.
     */
    @Override
    public void keyTyped(final KeyEvent e) {
        super.keyTyped(e);

        TextStringDataValue tsdv = (TextStringDataValue) getModel();
        // Just a regular vanilla keystroke - insert it into text field.
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

        // All other key strokes are consumed.
        } else {
            e.consume();
        }

        // Push the character changes into the database.
        try {
            // BugzID:668 - The user is reverting back to a 'placeholder' state.
            if (this.getText().equals("")) {
                tsdv.clearValue();
            } else {
                tsdv.setItsValue(this.getText());
            }
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