package org.openshapa.views.discrete.datavalues;

import org.openshapa.db.DataCell;
import org.openshapa.db.Matrix;
import java.awt.event.KeyEvent;
import javax.swing.text.JTextComponent;
import org.apache.log4j.Logger;
import org.openshapa.db.FloatDataValue;
import org.openshapa.db.PredDataValue;

/**
 * This class is the character editor of a FloatDataValue.
 */
public final class FloatDataValueEditor extends DataValueEditor {

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(FloatDataValueEditor.class);

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
        if (!Character.isDigit(ch) && ch != '.' && ch != '-'
                                  && ch != '\u007F' && ch != '\u0008') {
            // character is not one we use for a float editor.
            e.consume();
            return;
        }

        // Current text in the editor
        String t = getText();
        // the new text that will be set from this keystroke
        String newStr = "";
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

            if (ch == '\u0008' && selStart == selEnd) {
                // if its a backspace and there is no selection,
                // create a selection of one char to the left
                selStart = Math.max(selStart - 1, 0);
            } else if (ch == '\u007F' && selStart == selEnd) {
                // if it is a delete and there is no selection,
                // create a selection of one char to the right.
                selEnd = Math.min(selEnd + 1, t.length());
            }
            // the char to be added.  Backspace and Delete do not add any char.
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
        // now check that the decimal is still okay (might have been deleted)
        int dotPos = newStr.indexOf('.');
        if (dotPos < 0) {
            // no decimal now, put one back at the caret
            newStr = newStr.substring(0, caret) + "." + newStr.substring(caret);

            if (ch == '\u007F') {
                // special case if delete key was hit, move caret one more.
                caret++;
            }
        } else if (ch == '.') {
            // found a decimal and user typed '.', check incase we now have two
            int secDotPos = newStr.indexOf('.', dotPos + 1);
            if (secDotPos >= 0) {
                // two decimals - must have just added one.  Decide which
                // one needs to be deleted
                if (dotPos == selStart) {
                    dotPos = secDotPos;
                } else {
                    // modify our caret position
                    caret--;
                }
                // remove the decimal we don't want
                newStr = newStr.substring(0, dotPos)
                                                + newStr.substring(dotPos + 1);
            }
        }

        // rework the value so it is a good looking double.
        if (!allowedSpecial(newStr)) {

            // some special cases where caret location needs modification
            if ((t.equals("-.") && selStart == 2)
                            || (t.equals(".") && selStart == 1)) {
                // cases need caret incremented.
                caret++;
            } else if (ch != '-') {
                if ((t.startsWith("0.") && selStart == 1)
                                || (t.startsWith("-0.") && selStart == 2)) {
                    // case where we change from zero to a number
                    // in front of the decimal - needs caret decremented.
                    caret--;
                }
            }

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
            // catch the case where rebuild of float may have moved the decimal
            if (ch == '.') {
                caret = newStr.indexOf('.') + 1;
            }
        }
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