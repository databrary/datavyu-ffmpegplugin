/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openshapa.views.discrete.datavalues;

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;

import java.text.DecimalFormat;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import database.DataCell;
import database.FloatDataValue;
import database.Matrix;
import database.PredDataValue;

import org.openshapa.util.Constants;
import org.openshapa.util.FloatUtils;



/**
 * This class is the character editor of a FloatDataValue.
 */
public final class FloatDataValueEditor extends DataValueEditor {

    /** The base of the number system we are using. */
    private static final int BASE = 10;

    /** The maximum number of decimal places. */
    private static final int MAX_DECIMAL_PLACES = 6;

    /**
     * What is the maximum length a floating value can take before we start to
     * run into rounding errors.
     */
    private static final int MAX_LENGTH = 15;

    /** 'Negative' zero - used for checking specials. */
    private static final String NEGATIVE_ZERO = "-0.00000";

    /** Standard version of zero. */
    private static final String STANDARD_ZERO = "0.0";

    /**
     * Constructor.
     *
     * @param ta
     *            The parent JTextComponent the editor is in.
     * @param cell
     *            The parent data cell this editor resides within.
     * @param matrix
     *            Matrix holding the datavalue this editor will represent.
     * @param matrixIndex
     *            The index of the datavalue within the matrix.
     */
    public FloatDataValueEditor(final JTextComponent ta, final DataCell cell,
        final Matrix matrix, final int matrixIndex) {
        super(ta, cell, matrix, matrixIndex);
        addPreservedChars(".");
    }

    /**
     * Constructor.
     *
     * @param ta
     *            The parent JTextComponent the editor is in.
     * @param cell
     *            The parent data cell this editor resides within.
     * @param p
     *            The predicate holding the datavalue this editor will
     *            represent.
     * @param pi
     *            The index of the datavalue within the predicate.
     * @param matrix
     *            Matrix holding the datavalue this editor will represent.
     * @param matrixIndex
     *            The index of the datavalue within the matrix.
     */
    public FloatDataValueEditor(final JTextComponent ta, final DataCell cell,
        final PredDataValue p, final int pi, final Matrix matrix,
        final int matrixIndex) {
        super(ta, cell, p, pi, matrix, matrixIndex);
        addPreservedChars(".");
    }

    /**
     * Action to take when focus is lost for this editor.
     *
     * @param fe
     *            Focus Event
     */
    @Override public void focusLost(final FocusEvent fe) {
        FloatDataValue fdv = (FloatDataValue) getModel();

        if (!fdv.isEmpty()) {

            //Make strings valid on focus lost
            String fixedString = fixString(getText());

            if (fixedString != null) {
                setText(fixedString);
            }
        }

        super.focusLost(fe);
    }

    @Override public void keyPressed(final KeyEvent e) {
        FloatDataValue fdv = (FloatDataValue) getModel();

        StringBuilder newValue;

        // The backspace key removes digits from behind the caret.
        if (e.getKeyChar() == '\u0008') {

            // Can't delete empty float data value.
            if (!fdv.isEmpty()) {

                //Record previous state
                String prevValue = getText();
                int prevCaretPos = getCaretPosition();
                double prevDouble = fdv.getItsValue();

                //Perform change. Ideally we should simulate this change.
                removeBehindCaret();

                Double newD = buildValue(getText());

                if ((newD != null) && !newD.equals(fdv.getItsValue())) {
                    fdv.setItsValue(newD);
                } else if (newD == null) {

                    //Workaround, because clearvalue only changes a boolean
                    fdv.setItsValue(0);
                    fdv.clearValue();
                }

                //Check if new state is valid
                newValue = new StringBuilder(getText());

                String validString = validState(newValue.toString());

                if (validString == null) {

                    //Revert state
                    setText(prevValue);
                    fdv.setItsValue(prevDouble);
                    setCaretPosition(prevCaretPos);
                    e.consume();

                    return;
                } else {
                    setText(validString);
                }

                e.consume();
                updateDatabase();
            }
            // The delete key removes digits ahead of the caret.
        } else if (e.getKeyChar() == '\u007F') {

            // Can't delete empty float data value.
            if (!fdv.isEmpty()) {

                //Record previous state
                String prevValue = getText();
                int prevCaretPos = getCaretPosition();
                double prevDouble = fdv.getItsValue();

                //If we're at the leading zero, just skip over it
                if ((getText().matches("-?0.[0-9]{1,6}"))
                        && (getCaretPosition() < getText().indexOf("."))
                        && (getText().substring(getCaretPosition() + 1,
                                getCaretPosition() + 2).equals("."))) {
                    setCaretPosition(getCaretPosition() + 1);
                    e.consume();

                    return;
                }

                //Perform change. Ideally we should simulate this change.
                removeAheadOfCaret();

                Double newD = buildValue(getText());

                if ((newD != null) && !newD.equals(fdv.getItsValue())) {
                    fdv.setItsValue(newD);
                } else if (newD == null) {
                    fdv.clearValue();
                }

                //Check if new state is valid
                newValue = new StringBuilder("" + fdv.getItsValue());

                String validString = validState(newValue.toString());

                if (validString == null) {

                    //Revert state
                    setText(prevValue);
                    fdv.setItsValue(prevDouble);
                    setCaretPosition(prevCaretPos);
                    e.consume();

                    return;
                } else {
                    setText(validString);
                }

                e.consume();
                updateDatabase();
            }
        }
    }

    /**
     * The action to invoke when a key is typed.
     *
     * @param e
     *            The KeyEvent that triggered this action.
     */
    @Override public void keyTyped(final KeyEvent e) {
        FloatDataValue fdv = (FloatDataValue) getModel();

        JTextArea newValue = new JTextArea(getText());

        Double valueAsDouble = fdv.getItsValue();
        newValue.setCaretPosition(getCaretPosition());
        newValue.select(getSelectionStart(), getSelectionEnd());

        //Ignore everything in front of the -ve
        if (getText().startsWith("-") && (getCaretPosition() == 0)
                && !((getSelectionStart() == 0) && (getSelectionEnd() > 0))) {
            e.consume();

            return;
        }

        // '-' key toggles the state of a negative / positive number.
        if (((e.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD)
                    || (e.getKeyCode() == KeyEvent.KEY_LOCATION_UNKNOWN))
                && (e.getKeyChar() == '-')) {

            //Delete selected value first
            if (!fdv.isEmpty() && (getSelectionStart() != getSelectionEnd())) {
                keyPressed(new KeyEvent(e.getComponent(), e.getID(),
                        e.getWhen(), e.getModifiers(), KeyEvent.VK_UNDEFINED,
                        '\u0008'));
                valueAsDouble = fdv.getItsValue();
            }

            valueAsDouble = -valueAsDouble;
            newValue.setText("" + valueAsDouble);


            // Move the caret to behind the - sign, or front of the number.
            if (newValue.getText().startsWith("-")) {
                newValue.setCaretPosition(1);
            } else {
                newValue.setCaretPosition(0);
            }

            if (validState(newValue.getText()) == null) {
                e.consume();

                return;
            } else {

                //Toggle state of a negative / positive number.
                fdv.setItsValue(valueAsDouble);
                setText(validState(newValue.getText()));
                setCaretPosition(newValue.getCaretPosition());
                e.consume();
            }
            // '.' key shifts the location of the decimal point.
        } else if ((e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN)
                && (e.getKeyChar() == '.')) {

            // Shift the decimal point to the current caret position.
            int factor = getCaretPosition() - getText().indexOf('.');

            if (factor > 0) {
                factor--;
            }

            // Determine max fraction - if empty, use a default of 1.
            int maxFrac = 1;

            if (!fdv.isEmpty()) {
                maxFrac = Math.min(MAX_DECIMAL_PLACES,
                        getText().length() - getText().indexOf('.') - factor
                        - 1);
            }

            valueAsDouble = valueAsDouble * Math.pow(BASE, factor);

            // Determine the precision to use - prevent the user from exceding
            // six decimal places.
            DecimalFormat formatter = new DecimalFormat(Constants.FLOAT_FORMAT);
            formatter.setMaximumFractionDigits(maxFrac);
            newValue.setText(formatter.format(valueAsDouble));
            newValue.setCaretPosition(newValue.getText().indexOf('.') + 1);

            if (validState(newValue.getText()) == null) {
                e.consume();

                return;
            } else {

                //Work out the position of the caret (just after the '.' point).
                fdv.setItsValue(fdv.getItsValue() * Math.pow(BASE, factor));
                setText(validState(newValue.getText()));
                setCaretPosition(newValue.getCaretPosition());
                e.consume();
            }

            // Key stoke is number - insert number at current caret position.
        } else if (Character.isDigit(e.getKeyChar())
                && (!exceedsPrecision() || isAllSelected())) {

            //Record previous state because we need to use removeSelectedText.
            //Might need to move this function later.
            String prevValue = getText();
            int prevCaretPos = getCaretPosition();
            double prevDouble = fdv.getItsValue();

            //Delete selected value first
            newValue = removeSelectedText(newValue);
            newValue.setCaretPosition(getSelectionStart());


            // BugzID: 565 - Reject keystroke if a leading zero.
            if ((e.getKeyChar() == '0') && (newValue.getText().length() > 0)
                    && !newValue.getText().equals(".")) {

                if (((fdv.getItsValue() > 0) && (getCaretPosition() == 0))
                        || ((fdv.getItsValue() < 0)
                            && (getCaretPosition() <= 1))
                        || FloatUtils.closeEnough(fdv.getItsValue(), 0)) {
                    newValue.insert(Character.toString(e.getKeyChar()),
                        newValue.getCaretPosition());

                    //Let's see if this is could result in a valid state. If not, reject it as before.
                    if (validState(newValue.getText()) != null) {
                        newValue.setText(prevValue);
                        newValue.setCaretPosition(prevCaretPos);
                    } else {
                        e.consume();

                        return;
                    }
                }
            }

            // BugzID: 568 - If we are likely to excede the precision of a
            // double reject the keystroke.
            if (getText().indexOf('.') >= MAX_LENGTH) {
                e.consume();

                return;
            }

            //Cases where we need to overwrite a zero
            try {

                if (((newValue.getText().matches("-?[0-9]+.0{1,6}"))
                            && (newValue.getText(getCaretPosition(), 1).equals(
                                    "0"))
                            && (getCaretPosition()
                                > newValue.getText().indexOf(".")))
                        || ((newValue.getText().matches("-?0.0{1,6}"))
                            && (newValue.getText(getCaretPosition(), 1).equals(
                                    "0")))) {
                    newValue.replaceRange(Character.toString(e.getKeyChar()),
                        getCaretPosition(), getCaretPosition() + 1);
                } else if ((newValue.getText().matches("-?0.[0-9]{1,6}"))
                        && (newValue.getText(getCaretPosition(), 1).equals(
                                "."))) {
                    newValue.replaceRange(Character.toString(e.getKeyChar()),
                        getCaretPosition() - 1, getCaretPosition());
                } else {
                    newValue.insert(Character.toString(e.getKeyChar()),
                        newValue.getCaretPosition());
                }
            } catch (BadLocationException ex) {
                Logger.getLogger(FloatDataValueEditor.class.getName()).log(
                    Level.SEVERE, null, ex);
            }

            // BugzID:612 - Truncate last value if too long so we don't round
            // precision.
            if (((newValue.getText().length()
                            - newValue.getText().indexOf('.'))
                        > (MAX_DECIMAL_PLACES + 1))) {
                newValue.replaceRange(null, newValue.getText().length() - 1,
                    newValue.getText().length());
            }

            // Dealing with someone who has just entered in - remove trailing 0
            // between the caret and decimal place.
            if (getText().equals(NEGATIVE_ZERO) && (getCaretPosition() == 1)) {
                newValue.replaceRange(null, newValue.getCaretPosition() + 1,
                    newValue.getCaretPosition() + 2);
            }

            if ((newValue.getText().length() - 1
                        - newValue.getText().indexOf('.'))
                    > MAX_DECIMAL_PLACES) {

                try {
                    newValue.setText(newValue.getText(0,
                            newValue.getText().length() - 1));
                } catch (BadLocationException ex) {
                    Logger.getLogger(FloatDataValueEditor.class.getName()).log(
                        Level.SEVERE, null, ex);
                }
            }

            String validString = validState(newValue.getText());

            if (validString == null) {

                //Don't make any changes
                setText(prevValue);
                setCaretPosition(prevCaretPos);
                fdv.setItsValue(prevDouble);
                e.consume();

                return;
            } else {
                setText(validString);
                setCaretPosition(newValue.getCaretPosition());
                fdv.setItsValue(buildValue(newValue.getText()));
                e.consume();
            }


            // Every other key stroke is ignored by the float editor.
        } else {
            e.consume();
        }

        updateDatabase();
    }


    /**
     * @return True if adding another character at the current caret position
     *         will exceed the allowed precision for a floating value, false
     *         otherwise.
     */
    public boolean exceedsPrecision() {
        String text = getText();

        return exceedsPrecision(text);
    }

    /**
     * @return True if adding another character at the current caret position
     *         will exceed the allowed precision for a floating value, false
     *         otherwise.
     */
    public boolean exceedsPrecision(final String text) {

        if (((text.length() - text.indexOf('.')) > MAX_DECIMAL_PLACES)
                && ((getCaretPosition() - text.indexOf('.'))
                    > MAX_DECIMAL_PLACES)) {
            return true;
        }

        return false;
    }

    /**
     * Recalculate the string for this editor. In particular check if it is
     * "null" and display the appropriate FormalArg.
     */
    @Override public void updateStrings() {
        String t = "";
        FloatDataValue fdv = (FloatDataValue) getModel();

        if (!fdv.isEmpty()) {
            DecimalFormat formatter = new DecimalFormat(Constants.FLOAT_FORMAT);

            // BugzID:522 - Prevent overiding precision defined by user.
            if ((getText() != null) && (getText().length() > 0)) {

                if (getText().equals(getNullArg())) {
                    formatter.setMaximumFractionDigits(MAX_DECIMAL_PLACES - 1);
                } else {
                    int mFrac = getText().length() - getText().indexOf('.') - 1;
                    formatter.setMaximumFractionDigits(Math.max(mFrac, 1));
                }
            }

            t = formatter.format(fdv.getItsValue());
        } else {
            t = getNullArg();
        }

        resetText(t);
    }

    /**
     * Builds a new Double value from a string.
     *
     * @param textField
     *            The String that you want to create a Double from.
     * @return A Double value that can be used setting the database.
     */
    public Double buildValue(final String textField) {

        try {
            return new Double(textField);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Checks if text is a valid float state and returns corrected float state
     * if possible.
     * @param text float string
     * @return Corrected float state if possible, or null if no correction
     * possible.
     */
    public String validState(final String text) {

        //null state
        String state0 = "<val>";

        //transition state when "-", ".", or "0" are pressed
        String state1 = "-?0.0{1,6}";

        //valid number
        String state2 = "-?[0-9]+\\.[0-9]{1,6}";

        //invalid state
        String invalidState1 = "0+[0-9]+\\.[0-9]{1,6}";

        if (text.matches(invalidState1)) {
            return null;
        } else if ((text.equals(state0)) || (text.matches(state1))
                || (text.matches(state2))) {
            return text;
        } else {
            return fixString(text);
        }
    }

    /**
     * Returns a fixed float string.
     * @param text  float string
     * @return a fixed float string.
     */
    public String fixString(final String text) {

        if (text.length() == 0) {
            return text;
        }

        if (text.startsWith(".")) {
            return "0" + text;
        }

        if (text.startsWith("-.")) {
            return "-0." + text.substring(2);
        }

        if (text.endsWith(".")) {
            return text + "0";
        }

        if (text.matches("[0-9]+")) {
            return text + ".0";
        }

        if (text.length() > MAX_LENGTH) {
            return text.substring(0, MAX_LENGTH);
        }

        if ((text.length() - text.indexOf(".")) > (MAX_DECIMAL_PLACES + 1)) {
            return text.substring(0,
                    text.indexOf(".") + MAX_DECIMAL_PLACES + 1);
        }


        try {

            if (FloatUtils.closeEnough(Double.parseDouble(text), 0)
                    && (!text.matches("0.0+"))) {
                return STANDARD_ZERO;
            }
        } catch (Exception e) {
            //Not a number. Ignore.
        }

        return text;
    }

    /**
     * Removes selected text from a JTextArea, maintaining float format.
     * @param textArea JTextArea
     * @return JText area with selected text removed
     */
    public final JTextArea removeSelectedText(final JTextArea textArea) {

        // Get the current value of the visual representation of this DataValue.
        StringBuilder cValue = new StringBuilder(textArea.getText());
        JTextArea result = textArea;

        // Obtain the start and finish of the selected text.
        int start = textArea.getSelectionStart();
        int end = textArea.getSelectionEnd();
        int pos = start;

        for (int i = start; i < end; i++) {

            // Current character is not reserved - either delete or replace it.
            if (!isPreserved(cValue.charAt(pos))) {
                cValue.deleteCharAt(pos);

                // Current character is reserved, skip over current position.
            } else {
                pos++;
            }
        }

        // BugzID:747 - If all we have is preserved chars clear everything.
        String newValue = cValue.toString();
        boolean foundNonPreserved = false;

        for (int i = 0; i < newValue.length(); i++) {

            if (!isPreserved(newValue.charAt(i))) {
                foundNonPreserved = true;

                break;
            }
        }

        // Set the text for this data value to the new string.
        if (foundNonPreserved) {
            result.setText(newValue);
            result.setCaretPosition(start);
        } else {
            result.setText("");
            result.setCaretPosition(0);
        }

        return result;
    }

    /**
     * Paste the contents of the clipboard into the FloatDataValueEditor, then
     * try and fix it as necessary. If it fails, then try the old way of
     * typing in each character.
     */

    @Override public void paste() {
        FloatDataValue fdv = (FloatDataValue) getModel();
        JTextArea pastedField = new JTextArea(getText());
        pastedField.setCaretPosition(getCaretPosition());
        pastedField.setSelectionStart(getSelectionStart());
        pastedField.setSelectionEnd(getSelectionEnd());
        pastedField.paste();

        try {
            Double value = Double.parseDouble(pastedField.getText());
            pastedField.setText(fixString(pastedField.getText()));
            setText(pastedField.getText());
            setCaretPosition(pastedField.getCaretPosition());
            fdv.setItsValue(buildValue(pastedField.getText()));
        } catch (Exception e) {
            super.paste();
        }
    }
}
