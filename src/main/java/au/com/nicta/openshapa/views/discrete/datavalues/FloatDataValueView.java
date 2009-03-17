package au.com.nicta.openshapa.views.discrete.datavalues;

import au.com.nicta.openshapa.db.DataCell;
import au.com.nicta.openshapa.db.FloatDataValue;
import au.com.nicta.openshapa.db.Matrix;
import au.com.nicta.openshapa.views.discrete.Selector;
import java.awt.event.KeyEvent;

/**
 * This class is the view representation of a FloatDataValue as stored within
 * the database.
 *
 * @author cfreeman
 */
public final class FloatDataValueView extends DataValueView {

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
        addPreservedChar(new Character('.'));
    }

    /**
     * The action to invoke when a key is typed.
     *
     * @param e The KeyEvent that triggered this action.
     */
    public void keyTyped(KeyEvent e) {        
        FloatDataValue fdv = (FloatDataValue) getValue();

        // '-' key toggles the state of a negative / positive number.
        if ((e.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD ||
             e.getKeyCode() == KeyEvent.KEY_LOCATION_UNKNOWN)
             && e.getKeyChar() == '-') {

            // Move the caret to behind the - sign, or the front of the number.
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
            fdv.setItsValue(fdv.getItsValue() * Math.pow(10, factor));
            e.consume();

        // The backspace key removes digits from behind the caret.
        } else if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
                   && e.getKeyChar() == '\u0008') {            
            this.removeBehindCaret();
            fdv.setItsValue(buildValue(this.getText()));
            e.consume();

        // The delete key removes digits ahead of the caret.
        } else if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
                   && e.getKeyChar() == '\u007F') {

            this.removeAheadOfCaret();
            fdv.setItsValue(buildValue(this.getText()));
            e.consume();

        // Key stoke is number - insert number into the current caret position.
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

        // Push the value back into the database.
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
        if (textField == null || textField.equals("")) {
            return new Double(0);
        } else {
            return new Double(textField);
        }
    }
}
