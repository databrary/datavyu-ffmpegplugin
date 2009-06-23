package org.openshapa.views.discrete.datavalues;

import org.openshapa.db.DataCell;
import org.openshapa.db.Matrix;
import java.awt.event.KeyEvent;
import javax.swing.text.JTextComponent;
import org.openshapa.db.IntDataValue;
import org.openshapa.db.PredDataValue;

/**
 * This class is the character editor of a IntDataValue.
 */
public final class IntDataValueEditor extends DataValueEditor {

    /**
     * Constructor.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param cell The parent data cell this editor resides within.
     * @param matrix Matrix holding the datavalue this editor will represent.
     * @param matrixIndex The index of the datavalue within the matrix.
     */
    public IntDataValueEditor(final JTextComponent ta,
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
    public IntDataValueEditor(final JTextComponent ta,
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

            // '-' key toggles the state of a negative / positive number.
            if ((e.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD
                || e.getKeyCode() == KeyEvent.KEY_LOCATION_UNKNOWN)
                && e.getKeyChar() == '-') {

                String t = getText();
                String newt = "";
                int start = getSelectionStart();
                int end = getSelectionEnd();
                if (t.startsWith("-")) {
                    // take off the '-'
                    // check start and end aren't 0
                    start = Math.max(start, 1);
                    end = Math.max(end, 1);
                    newt = t.substring(1, start) + t.substring(end);
                    start = 0;
                } else {
                    // add the '-'
                    newt = "-" + t.substring(0, start) + t.substring(end);
                    start = 1;
                }
                setText(newt);
                setCaretPosition(start);
                e.consume();

            } else if (!Character.isDigit(e.getKeyChar())) {
                // all other non-digit keys are ignored by the editor.
                e.consume();
            }
        }
    }

    /**
     * Update the model to reflect the value represented by the
     * editor's text representation.
     */
    @Override
    public void updateModelValue() {
        IntDataValue idv = (IntDataValue) getModel();
        idv.setItsValue(getText());
        // special case for numeric - reget the text from the db if losing focus
        // incase the user types characters that will not cause a change in the
        // numeric data value - no notification of a change will be sent by db
        // so we need to do this
        setText(idv.toString());
    }

    /**
     * Sanity check the current text of the editor and return a boolean.
     * @return true if the text is an okay representation for this DataValue.
     */
    @Override
    public boolean sanityCheck() {
        boolean res = true;
        // could call a subRange test for this dataval
        if (getText().equals("-")) {
            // special case where user has managed to delete all text but the
            // '-' - Set to "-0"
            setText("-0");
            setCaretPosition(2);
        } else {
            try {
                Integer.valueOf(getText());
            } catch (NumberFormatException e) {
                res = false;
            }
        }
        return res;
    }
}