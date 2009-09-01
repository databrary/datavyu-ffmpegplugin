package org.openshapa.views.discrete.datavalues;

import org.openshapa.db.DataCell;
import org.openshapa.db.Matrix;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import javax.swing.text.JTextComponent;
import org.openshapa.db.FloatDataValue;
import org.openshapa.db.PredDataValue;
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
        this.addPreservedChars(".");
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
        this.addPreservedChars(".");
    }

    /**
     * The action to invoke when a key is typed.
     * @param e The KeyEvent that triggered this action.
     */
    @Override
    public void keyTyped(final KeyEvent e) {
        FloatDataValue fdv = (FloatDataValue) getModel();

        // '-' key toggles the state of a negative / positive number.
        if ((e.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD
          || e.getKeyCode() == KeyEvent.KEY_LOCATION_UNKNOWN)
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
            fdv.setItsValue(fdv.getItsValue() * Math.pow(BASE, factor));

            // Determine the precision to use - prevent the user from exceding
            // six decimal places.
            DecimalFormat formatter = new DecimalFormat(Constants.FLOAT_FORMAT);
            int maxFrac = Math.min(MAX_DECIMAL_PLACES, getText().length()
                                                       - getText().indexOf('.')
                                                       - factor - 1);
            formatter.setMaximumFractionDigits(maxFrac);
            this.setText(formatter.format(fdv.getItsValue()));

            // Work out the position of the caret (just after the '.' point).
            setCaretPosition(this.getText().indexOf('.') + 1);
            e.consume();

        // The backspace key removes digits from behind the caret.
        } else if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
                   && e.getKeyChar() == '\u0008') {

            // Can't delete empty float data value.
            if (!fdv.isEmpty()) {
                this.removeBehindCaret();

                // Allow the provision of a 'null' value - that will permit
                // users to transition the cell contents to a '<val>' state.
                Double newD = buildValue(this.getText());
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
                this.removeAheadOfCaret();

                // Allow the provision of a 'null' value - that will permit
                // users to transition the cell contents to a '<val>' state.
                Double newD = buildValue(this.getText());
                if (newD != null && !newD.equals(fdv.getItsValue())) {
                    fdv.setItsValue(newD);
                    this.setCaretPosition(this.getCaretPosition());
                } else if (newD == null) {
                    fdv.clearValue();
                }
                e.consume();
            }

        // Key stoke is number - insert number at current caret position.
        } else if (Character.isDigit(e.getKeyChar()) && !excedesPrecision()) {
            this.removeSelectedText();
            StringBuffer currentValue = new StringBuffer(getText());
            currentValue.insert(getCaretPosition(), e.getKeyChar());

            // Advance caret over the top of the new char.
            int pos = this.getCaretPosition() + 1;
            this.setText(currentValue.toString());
            this.setCaretPosition(pos);

            fdv.setItsValue(buildValue(currentValue.toString()));
            e.consume();

        // Every other key stroke is ignored by the float editor.
        } else {
            e.consume();
        }

        this.updateDatabase();
    }

    /**
     * @return True if adding another character at the current caret position
     * will execde the allowed precision for a floating value, false otherwise.
     */
    public boolean excedesPrecision() {
        this.getText().length();
        if (this.getCaretPosition() < this.getText().indexOf('.')) {
            return false;
        } else if (this.getText().length()
                   - this.getText().indexOf('.') > MAX_DECIMAL_PLACES) {
            return true;
        }

        return false;
    }

    /**
     * Recalculate the string for this editor.  In particular check if it
     * is "null" and display the appropriate FormalArg.
     */
    @Override
    public void updateStrings() {
        String t = "";
        FloatDataValue fdv = (FloatDataValue) getModel();
        if (!fdv.isEmpty()) {
            DecimalFormat formatter = new DecimalFormat(Constants.FLOAT_FORMAT);

            // BugzID:522 - Prevent overiding precision defined by user.
            if (this.getText() != null && this.getText().length() > 0) {
                int maxFrac = getText().length() - getText().indexOf('.') - 1;
                formatter.setMaximumFractionDigits(maxFrac);
            }

            t = formatter.format(fdv.getItsValue());
        } else {
            t = getNullArg();
        }

        this.resetText(t);
    }

    /**
     * Builds a new Double value from a string.
     *
     * @param textField The String that you want to create a Double from.
     *
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
     * Check if the string supplied is one allowed while typing in a float.
     * For when typing in the first characters '-' and '.'.
     *
     * @param str the string to check
     *
     * @return true if we allow this string to represent a float even
     * though it would not pass a conversion operation.
     */
    private boolean allowedSpecial(final String str) {
        return (str.equals(".") || str.equals("-") || str.equals("-."));
    }
}