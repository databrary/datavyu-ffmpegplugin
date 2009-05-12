package org.openshapa.views.discrete.datavalues;

import org.openshapa.db.DataCell;
import org.openshapa.db.FloatDataValue;
import org.openshapa.db.Matrix;
import org.openshapa.db.PredDataValue;
import org.openshapa.views.discrete.Editor;
import org.openshapa.views.discrete.Selector;
import java.awt.event.KeyEvent;

/**
 * This class is the view representation of a FloatDataValue as stored within
 * the database.
 */
public final class FloatDataValueView extends DataValueElementV {

    /** The base of the number system we are using. */
    private static final int BASE = 10;

    /**
     * Constructor.
     *
     * @param cellSelection The parent selection for spreadsheet cells.
     * @param cell The parent datacell for the int data value that this view
     * represents.
     * @param matrix The parent matrix for the int data value that this view
     * represents.
     * @param matrixIndex The index of the IntDataValue within the above parent
     * matrix that this view represents.
     * @param editable Is the dataValueView editable by the user? True if the
     * value is permitted to be altered by the user. False otherwise.
     */
    public FloatDataValueView(final Selector cellSelection,
                              final DataCell cell,
                              final Matrix matrix,
                              final int matrixIndex,
                              final boolean editable) {
        super(cellSelection, cell, matrix, matrixIndex, editable);
    }

    /**
     * Constructor.
     *
     * @param cellSelection The parent selection for spreadsheet cells.
     * @param cell The parent data cell that this view will reside within.
     * @param predicate The parent predicate for the int data value that this
     * view represents.
     * @param predicateIndex The index of the int data value view within the
     * above parent matrix that this view represents.
     * @param editable Is the data value view editable by the user? True if the
     * value is permitted to be altered by the user. False otherwise.
     */
    public FloatDataValueView(final Selector cellSelection,
                              final DataCell cell,
                              final PredDataValue predicate,
                              final int predicateIndex,
                              final Matrix matrix,
                              final int matrixIndex,
                              final boolean editable) {
        super(cellSelection, cell, predicate, predicateIndex,
              matrix, matrixIndex, editable);
    }

    /**
     * @return Builds the editor to be used for this data value.
     */
    protected Editor buildEditor() {
        FloatEditor f = new FloatEditor();
        f.addPreservedChar(new Character('.'));

        return f;
    }

    /**
     * The editor for the float data value.
     */
    class FloatEditor extends DataValueElementV.DataValueEditor {

        /**
         * The action to invoke when a key is typed.
         *
         * @param e The KeyEvent that triggered this action.
         */
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
                fdv.setItsValue(fdv.getItsValue() * Math.pow(BASE, factor));
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
                    if (newD != null) {
                        fdv.setItsValue(newD);
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
                    if (newD != null) {
                        fdv.setItsValue(newD);
                    }
                    e.consume();
                }

            // Key stoke is number - insert number at current caret position.
            } else if (Character.isDigit(e.getKeyChar())) {
                this.removeSelectedText();
                StringBuffer currentValue = new StringBuffer(getText());
                currentValue.insert(getCaretPosition(), e.getKeyChar());
                advanceCaret();  // Advance caret over the top of the new char.
                fdv.setItsValue(buildValue(currentValue.toString()));
                e.consume();

            // Every other key stroke is ignored by the float editor.
            } else {
                e.consume();
            }

            updateDatabase();
        }

        /**
         * Builds a new Double value from a string.
         *
         * @param textField The String that you want to create a Double from.
         *
         * @return A Double value that can be used setting the database.
         */
        public Double buildValue(final String textField) {
            if (textField == null || textField.equals(".")) {
                return null;
            } else {
                return new Double(textField);
            }
        }
    }
}
