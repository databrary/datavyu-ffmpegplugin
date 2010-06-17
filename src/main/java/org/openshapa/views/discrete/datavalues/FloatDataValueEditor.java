package org.openshapa.views.discrete.datavalues;

import java.awt.event.KeyEvent;
import java.text.DecimalFormat;

import javax.swing.text.JTextComponent;

import org.openshapa.models.db.DataCell;
import org.openshapa.models.db.FloatDataValue;
import org.openshapa.models.db.Matrix;
import org.openshapa.models.db.PredDataValue;
import org.openshapa.util.Constants;

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
     * The action to invoke when a key is typed.
     *
     * @param e
     *            The KeyEvent that triggered this action.
     */
    @Override
    public void keyTyped(final KeyEvent e) {
        FloatDataValue fdv = (FloatDataValue) getModel();

        // BugzID:422 - Disallow key presses if user is in front of -ve sign
        if (getText().startsWith("-") && getCaretPosition() == 0
                && e.getKeyChar() != '-') {
            e.consume();
            return;
        }

        // '-' key toggles the state of a negative / positive number.
        if ((e.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD || e
                .getKeyCode() == KeyEvent.KEY_LOCATION_UNKNOWN)
                && e.getKeyChar() == '-') {

            // Move the caret to behind the - sign, or front of the number.
            if (fdv.getItsValue() < 0.0) {
                setCaretPosition(0);
            } else {
                setCaretPosition(1);
            }

            // Toggle state of a negative / positive number.
            fdv.setItsValue(-fdv.getItsValue());
            e.consume();

            // '.' key shifts the location of the decimal point.
        } else if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
                && e.getKeyChar() == '.') {

            // Shift the decimal point to the current caret position.
            int factor = getCaretPosition() - getText().indexOf('.');
            if (factor > 0) {
                factor--;
            }

            // Determine max fraction - if empty, use a default of 1.
            int maxFrac = 1;
            if (!fdv.isEmpty()) {
                maxFrac =
                        Math.min(MAX_DECIMAL_PLACES, getText().length()
                                - getText().indexOf('.') - factor - 1);
            }

            fdv.setItsValue(fdv.getItsValue() * Math.pow(BASE, factor));

            // Determine the precision to use - prevent the user from exceding
            // six decimal places.
            DecimalFormat formatter = new DecimalFormat(Constants.FLOAT_FORMAT);
            formatter.setMaximumFractionDigits(maxFrac);
            setText(formatter.format(fdv.getItsValue()));

            // Work out the position of the caret (just after the '.' point).
            setCaretPosition(getText().indexOf('.') + 1);
            e.consume();

            // The backspace key removes digits from behind the caret.
        } else if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
                && e.getKeyChar() == '\u0008') {

            // Can't delete empty float data value.
            if (!fdv.isEmpty()) {
                removeBehindCaret();

                // Allow the provision of a 'null' value - that will permit
                // users to transition the cell contents to a '<val>' state.
                Double newD = buildValue(getText());
                if (newD != null && !newD.equals(fdv.getItsValue())) {
                    fdv.setItsValue(newD);
                } else if (newD == null) {
                    fdv.clearValue();
                }
                e.consume();
            }

            // The delete key removes digits ahead of the caret.
        } else if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
                && e.getKeyChar() == '\u007F') {

            // Can't delete empty float data value.
            if (!fdv.isEmpty()) {
                removeAheadOfCaret();

                // Allow the provision of a 'null' value - that will permit
                // users to transition the cell contents to a '<val>' state.
                Double newD = buildValue(getText());
                if (newD != null && !newD.equals(fdv.getItsValue())) {
                    fdv.setItsValue(newD);
                    setCaretPosition(getCaretPosition());
                } else if (newD == null) {
                    fdv.clearValue();
                }
                e.consume();
            }

            // Key stoke is number - insert number at current caret position.
        } else if (Character.isDigit(e.getKeyChar())
                && (!excedesPrecision() || isAllSelected())) {
            removeSelectedText();

            // BugzID: 565 - Reject keystroke if a leading zero.
            if (e.getKeyChar() == '0' && getText().length() > 0
                    && !getText().equals(".")) {
                if ((fdv.getItsValue() > 0 && getCaretPosition() == 0)
                        || (fdv.getItsValue() < 0 && getCaretPosition() <= 1)
                        || fdv.getItsValue() == 0.0) {
                    e.consume();
                    return;
                }
            }

            // BugzID: 568 - If we are likely to excede the precision of a
            // double reject the keystroke.
            if (getText().indexOf('.') >= MAX_LENGTH) {
                e.consume();
                return;
            }

            StringBuffer currentValue = new StringBuffer(getText());
            currentValue.insert(getCaretPosition(), e.getKeyChar());

            // BugzID:612 - Truncate last value if too long so we don't round
            // precision.
            if ((currentValue.toString().length()
                    - currentValue.toString().indexOf('.') > MAX_DECIMAL_PLACES + 1)) {
                currentValue.deleteCharAt(currentValue.toString().length() - 1);
            }

            // Dealing with someone who has just entered in - remove trailing 0
            // between the caret and decimal place.
            if (getText().equals(NEGATIVE_ZERO) && getCaretPosition() == 1) {
                currentValue.deleteCharAt(getCaretPosition() + 1);
            }

            String nText = currentValue.toString();
            if (nText.length() - 1 - nText.indexOf('.') > MAX_DECIMAL_PLACES) {
                nText = nText.substring(0, nText.length() - 1);
            }

            // Advance caret over the top of the new char.
            int pos = getCaretPosition();
            if (fdv.getItsValue() != 0.0 || getText().equals(NEGATIVE_ZERO)
                    || getCaretPosition() != 1) {
                pos = pos + 1;
            }
            setText(nText);

            setCaretPosition(pos);
            fdv.setItsValue(buildValue(currentValue.toString()));
            e.consume();

            // Every other key stroke is ignored by the float editor.
        } else {
            e.consume();
        }

        updateDatabase();
    }

    /**
     * @return True if adding another character at the current caret position
     *         will execde the allowed precision for a floating value, false
     *         otherwise.
     */
    public boolean excedesPrecision() {
        String text = getText();
        if ((text.length() - text.indexOf('.') > MAX_DECIMAL_PLACES)
                && (getCaretPosition() - text.indexOf('.') > MAX_DECIMAL_PLACES)) {
            return true;
        }

        return false;
    }

    /**
     * Recalculate the string for this editor. In particular check if it is
     * "null" and display the appropriate FormalArg.
     */
    @Override
    public void updateStrings() {
        String t = "";
        FloatDataValue fdv = (FloatDataValue) getModel();
        if (!fdv.isEmpty()) {
            DecimalFormat formatter = new DecimalFormat(Constants.FLOAT_FORMAT);

            // BugzID:522 - Prevent overiding precision defined by user.
            if (getText() != null && getText().length() > 0) {
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
}