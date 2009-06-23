package org.openshapa.views.discrete.datavalues;

import org.openshapa.db.DataCell;
import org.openshapa.db.Matrix;
import java.awt.event.KeyEvent;
import javax.swing.text.JTextComponent;
import org.apache.log4j.Logger;
import org.openshapa.db.IntDataValue;
import org.openshapa.db.PredDataValue;

/**
 * This class is the character editor of a IntDataValue.
 */
public final class IntDataValueEditor extends DataValueEditor {

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(IntDataValueEditor.class);

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

        if (e.isConsumed()) {
            return;
        }

        char ch = e.getKeyChar();

        // consume characters that we do not use.
        if (!Character.isDigit(ch) && ch != '-') {
            e.consume();
            return;
        }

        // Current text in the editor
        String t = getText();
        // the new text that will be set from this keystroke
        String newStr = "";
        // current selection start and end (start can equal end)
        int selStart = getSelectionStart();
        int selEnd = getSelectionEnd();
        // the new position of the caret
        int caret = 0;

        // '-' key toggles the state of a negative / positive number.
        // ignores the current selection and resets the caret
        if (ch == '-') {
            if (isNullArg()) {
                newStr = "-";
                caret = 1;
            } else if (t.startsWith("-")) {
                // remove the '-'
                newStr = t.substring(1);
                caret = 0;
            } else {
                // add the '-'
                newStr = "-" + t;
                caret = 1;
            }

        // else handle digits
        } else if (Character.isDigit(e.getKeyChar())) {
            newStr = t.substring(0, selStart) + ch + t.substring(selEnd);
            caret = selStart + 1;
        }

        // check if the new string is now a "null" value.
        if (newStr.length() == 0) {
            setText(newStr);
            e.consume();
            return;
        }

        // reformat the value.
        if (!allowedSpecial(newStr)) {
            // set the datavalue and retrieve the string version of it
            IntDataValue idv = (IntDataValue) getModel();
            try {
                idv.setItsValue(newStr);
                newStr = idv.toString();
            } catch (NumberFormatException ex) {
                // whatever was typed is not allowed as a new integer.
                // restore to previous text and caret
                newStr = t;
                caret = selStart;
            }
        }

        // set the new text and caret location
        setText(newStr);
        setCaretPosition(caret);
        e.consume();
    }

    /**
     * Update the model to reflect the value represented by the
     * editor's text representation.
     */
    @Override
    public void updateModelValue() {
        IntDataValue idv = (IntDataValue) getModel();
        String str = getText();
        if (allowedSpecial(str)) {
            str = "0";
        }
        idv.setItsValue(str);
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
        if (!allowedSpecial(getText())) {
            IntDataValue idv = (IntDataValue) getModel();
            try {
                idv.setItsValue(getText());
            } catch (NumberFormatException ex) {
                res = false;
            }
        }
        return res;
    }

    /**
     * Check if the string supplied is one allowed while typing in an integer.
     * For when typing in the first characters '-'.
     * @param str the string to check
     * @return true if we allow this string to represent an integer even
     * though it would not pass a conversion operation.
     */
    private boolean allowedSpecial(final String str) {
        return (str.equals("-"));
    }
}