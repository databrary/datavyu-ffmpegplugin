package org.openshapa.views.discrete.datavalues;

import org.openshapa.db.DataCell;
import org.openshapa.db.Matrix;
import java.awt.event.KeyEvent;
import javax.swing.text.JTextComponent;
import org.openshapa.db.FloatDataValue;
import org.openshapa.db.PredDataValue;

/**
 * This class is the character editor of a FloatDataValue.
 */
public final class FloatDataValueEditor extends DataValueEditor {

    /**
     * Constructor.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param cell The parent data cell this editor resides within.
     * @param matrix Matrix holding the datavalue this editor will represent.
     * @param matrixIndex The index of the datavalue within the matrix.
     */
    public FloatDataValueEditor(final JTextComponent ta,
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
    public FloatDataValueEditor(final JTextComponent ta,
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
    public void keyPressed(final KeyEvent e) {
        super.keyPressed(e);

        if (!e.isConsumed()) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_BACK_SPACE:
                case KeyEvent.VK_DELETE:
                    // Ignore - handled when the key is typed.
                    e.consume();
                    break;
                default:
                    break;
            }
        }
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
        //  For some of the tests with ch
        // '\u007F' = Delete character
        // '\u0008' = Backspace character

        // consume characters that we do not use.
        if (!Character.isDigit(ch) && ch != '.' && ch != '-'
                                  && ch != '\u007F' && ch != '\u0008') {
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

        // else handle digits, decimal, backspace and delete keys
        } else if (Character.isDigit(ch) || ch == '.' || ch == '\u007F'
                || ch == '\u0008') {

                // if its a backspace and there is no selection,
                // create a selection of one char to the left
            if (ch == '\u0008' && selStart == selEnd) {
                selStart = Math.max(selStart - 1, 0);

                // if it is a delete and there is no selection,
                // create a selection of one char to the right.
            } else if (ch == '\u007F' && selStart == selEnd) {
                selEnd = Math.min(selEnd + 1, t.length());
            }

            // Add the char just typed.  Backspace and Delete do not add any.
            String addStr = "";
            if (ch != '\u0008' && ch != '\u007F') {
                addStr += ch;
            }
            // create the new string and calculate the new caret position
            newStr = t.substring(0, selStart) + addStr + t.substring(selEnd);
            caret = selStart + addStr.length();
        }

        // check if the new string is now a "null" value.
        if (newStr.length() == 0) {
            setText(newStr);
            e.consume();
            return;
        }

        // the new string is not empty
        // check the decimal is still okay (might have been deleted)
        int dotPos = newStr.indexOf('.');
        // if no decimal found, put one back at the caret
        if (dotPos < 0) {
            newStr = newStr.substring(0, caret) + "." + newStr.substring(caret);

            // special case if delete key, move caret to other side of decimal.
            if (ch == '\u007F') {
                caret++;
            }

        // else if user typed a '.', check incase we now have two decimals
        } else if (ch == '.') {
            int secDotPos = newStr.indexOf('.', dotPos + 1);
            // if we have two decimals we must have just added one.
            // Keep the one where the original selection started.
            if (secDotPos >= 0) {
                if (dotPos == selStart) {
                    dotPos = secDotPos;
                }
                // remove the decimal we don't want
                newStr = newStr.substring(0, dotPos)
                                                + newStr.substring(dotPos + 1);
            }
        }

        // before we reformat the value, record the position of the decimal.
        dotPos = newStr.indexOf('.');

        // reformat the value to a correct double.
        if (!allowedSpecial(newStr)) {
            // set the datavalue and retrieve the string version of it
            FloatDataValue fdv = (FloatDataValue) getModel();
            try {
                fdv.setItsValue(newStr);
                newStr = fdv.toString();
            } catch (NumberFormatException ex) {
                // whatever was typed is not allowed as a new float.
                // restore to previous text and caret
                newStr = t;
                caret = selStart;
            }
        }

        // recalculate the caret incase the decimal moved in the reformat
        // if user typed a '.', caret is always one beyond.
            if (ch == '.') {
                caret = newStr.indexOf('.') + 1;

        // else nudge the caret by any change in position of the decimal.
        } else {
            caret += (newStr.indexOf('.') - dotPos);
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
        FloatDataValue dv = (FloatDataValue) getModel();
        String str = getText();
        if (allowedSpecial(str)) {
            str = "0.0";
        }
        dv.setItsValue(str);
        // special case for numeric - reget the text from the db if losing focus
        // incase the user types characters that will not cause a change in the
        // numeric data value - no notification of a change will be sent by db
        // so we need to do this
        setText(dv.toString());
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
            FloatDataValue fdv = (FloatDataValue) getModel();
            try {
                fdv.setItsValue(getText());
            } catch (NumberFormatException e) {
                res = false;
            }
        }
        return res;
    }

    /**
     * Check if the string supplied is one allowed while typing in a float.
     * For when typing in the first characters '-' and '.'.
     * @param str the string to check
     * @return true if we allow this string to represent a float even
     * though it would not pass a conversion operation.
     */
    private boolean allowedSpecial(final String str) {
        return (str.equals(".") || str.equals("-") || str.equals("-."));
    }
}