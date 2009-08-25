package org.openshapa.views.discrete.datavalues;

import java.awt.event.KeyEvent;
import javax.swing.text.JTextComponent;
import org.apache.log4j.Logger;
import org.openshapa.db.DataCell;
import org.openshapa.db.Matrix;
import org.openshapa.db.PredDataValue;
import org.openshapa.db.SystemErrorException;
import org.openshapa.db.TextStringDataValue;

/**
 * This class is the character editor of a TextStringDataValue.
 */
public final class TextStringDataValueEditor extends DataValueEditor {

    /** The logger for this class. */
    private static Logger logger = Logger
                                   .getLogger(TextStringDataValueEditor.class);

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
        if (!e.isConsumed() && !e.isMetaDown() && !e.isControlDown()) {
            this.removeSelectedText();
            StringBuffer currentValue = new StringBuffer(getText());
            currentValue.insert(getCaretPosition(), e.getKeyChar());

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
            tsdv.setItsValue(this.getText());
            updateDatabase();
        } catch (SystemErrorException se) {
            logger.error("Unable to edit text string", se);
        }
    }
}