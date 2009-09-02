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
        IntDataValue idv = (IntDataValue) getModel();

        // Ensure that everything is selected if the value is empty.
        if (idv.isEmpty()) {
            this.selectAll();
        }

        // '-' key toggles the state of a negative / positive number.
        if ((e.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD
            || e.getKeyCode() == KeyEvent.KEY_LOCATION_UNKNOWN)
            && e.getKeyChar() == '-') {

            // Move the caret to behind the - sign, or the front of the
            // number.
            if (idv.getItsValue() < 0) {
                setCaretPosition(0);
            } else {
                setCaretPosition(1);
            }

            // Toggle state of a negative / positive number.
            idv.setItsValue(-idv.getItsValue());
            e.consume();

        // The backspace key removes digits from behind the caret.
        } else if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
                   && e.getKeyChar() == '\u0008') {

            // Can't delete empty int datavalue.
            if (!idv.isEmpty()) {
                this.removeBehindCaret();

                // Allow the provision of a 'null' value - that will permit
                // users to transition the cell contents to a '<val>' state.
                Long newL = buildValue(this.getText());
                if (newL != null) {
                    idv.setItsValue(newL);
                } else {
                    idv.clearValue();
                }
                e.consume();
            }

        // The delete key removes digits ahead of the caret.
        } else if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
                   && e.getKeyChar() == '\u007F') {

            // Can't delete empty int datavalue.
            if (!idv.isEmpty()) {
                this.removeAheadOfCaret();

                // Allow the provision of a 'null' value - that will permit
                // users to transition the cell contents to a '<val>' state.
                Long newL = buildValue(this.getText());
                if (newL != null) {
                    idv.setItsValue(newL);
                } else {
                    idv.clearValue();
                }
                e.consume();
            }

        // Key stoke is number - insert number at current caret position.
        } else if (Character.isDigit(e.getKeyChar())) {
            this.removeSelectedText();

            // BugzID: 565 - Reject keystroke if a leading zero.
            if (e.getKeyChar() == '0' && this.getText().length() > 0) {
                if ((idv.getItsValue() > 0 && getCaretPosition() == 0)
                    || (idv.getItsValue() < 0 && getCaretPosition() <= 1)
                    || this.getText().charAt(0) == '0') {
                  e.consume();
                  return;
                }
            }

            StringBuffer currentValue = new StringBuffer(getText());
            currentValue.insert(getCaretPosition(), e.getKeyChar());

            // Only insert value into the database if it is well formed.
            Long newValue = buildValue(currentValue.toString());
            if (newValue != null) {
                int pos = this.getCaretPosition() + 1;
                this.setText(currentValue.toString());
                this.setCaretPosition(pos);
                idv.setItsValue(newValue);
            }

            e.consume();

        // Every other key stroke is ignored by the float editor.
        } else {
            e.consume();
        }

        updateDatabase();
    }

    /**
     * Builds a new Integer value from a string.
     *
     * @param textField The String that you want to create an Integer from.
     *
     * @return An Integer value that can be used setting the database, if
     * unable to create an integer value, null is returned.
     */
    public Long buildValue(final String textField) {

        // User has removed everything - return a null value.
        if (textField == null || textField.equals("")) {
            return null;

        // User has _something_ attempt to build a value from it.
        } else {
            try {
                return new Long(textField);
            } catch (NumberFormatException e) {
                return null;
            }

        }
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