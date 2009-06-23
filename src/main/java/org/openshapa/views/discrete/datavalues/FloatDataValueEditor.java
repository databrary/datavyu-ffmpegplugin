package org.openshapa.views.discrete.datavalues;

import org.openshapa.db.DataCell;
import org.openshapa.db.Matrix;
import java.awt.event.KeyEvent;
import javax.swing.text.JTextComponent;
import org.apache.log4j.Logger;
import org.openshapa.db.FloatDataValue;
import org.openshapa.db.PredDataValue;
import org.openshapa.db.SystemErrorException;

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
        if (Character.isDigit(ch) || ch == '.' || ch == '-'
                                  || ch == '\u007F' || ch == '\u0008') {

            String t = getText();
            String newStr = "";
            int selStart = getSelectionStart();
            int selEnd = getSelectionEnd();
            int caret = 0;

            if (ch == '-') {
                // '-' key toggles the state of a negative / positive number.
                if (t.startsWith("-")) {
                    // take off the '-'
                    // check start and end aren't 0
                    selStart = Math.max(selStart, 1);
                    selEnd = Math.max(selEnd, 1);
                    newStr = t.substring(1, selStart) + t.substring(selEnd);
                    caret = 0;
                } else {
                    // add the '-'
                    newStr = "-" + t.substring(0, selStart)
                                                        + t.substring(selEnd);
                    caret = 1;
                }

            } else if (Character.isDigit(ch) || ch == '.' || ch == '\u007F'
                    || ch == '\u0008') {

                if (ch == '\u0008' && selStart == selEnd) {
                    selStart = Math.max(selStart - 1, 0);
                } else if (ch == '\u007F' && selStart == selEnd) {
                    selEnd = Math.min(selEnd + 1, t.length());
                }
                String addStr = "";
                if (ch != '\u0008' && ch != '\u007F') {
                    addStr += ch;
                }
                newStr = t.substring(0, selStart) + addStr
                                                          + t.substring(selEnd);
                caret = selStart + addStr.length();

                if (newStr.length() > 0) {
                    int dotPos = newStr.indexOf('.');
                    if (dotPos < 0) {
                        // no decimal, put one back
                        newStr = newStr.substring(0, caret) + "."
                                                      + newStr.substring(caret);
                        if (ch == '\u007F') {
                            caret++;
                        }
                    } else {
                        int secDotPos = newStr.indexOf('.', dotPos + 1);
                        if (secDotPos >= 0) {
                            // two decimals - must have just added one
                            if (dotPos == selStart) {
                                dotPos = secDotPos;
                            } else {
                                // modify our caret position
                                caret--;
                            }
                            // remove the other decimal
                            newStr = newStr.substring(0, dotPos)
                                    + newStr.substring(dotPos + 1);
                        }
                    }
                }
            }

            if (newStr.length() > 0 && !allowedOddFloat(newStr)) {
                // rework the value so it is a good looking double.
                FloatDataValue fdv = (FloatDataValue) getModel();
                try {
                    fdv = new FloatDataValue((FloatDataValue) getModel());
                    fdv.setItsValue(newStr);
                    newStr = fdv.toString();
                    if (t.equals("-.") || t.equals(".")) {
                        caret++;
                    }
                } catch (SystemErrorException exx) {
                    logger.error("Problem setting Float value.", exx);
                } catch (NumberFormatException ex) {
                    logger.error("Problem with Float format.", ex);
                }
            }
            setText(newStr);
            setCaretPosition(caret);
        }
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
        if (allowedOddFloat(str)) {
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
        if (!allowedOddFloat(getText())) {
            try {
                Double.valueOf(getText());
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
    private boolean allowedOddFloat(final String str) {
        return (str.equals(".") || str.equals("-") || str.equals("-."));
    }
}