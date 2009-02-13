package au.com.nicta.openshapa.views.discrete;

import au.com.nicta.openshapa.db.DataCell;
import au.com.nicta.openshapa.db.IntDataValue;
import au.com.nicta.openshapa.db.Matrix;
import java.awt.event.KeyEvent;

/**
 * This class is the view representation of a IntDataValue as stored within the
 * database.
 *
 * @author cfreeman
 */
public final class IntDataValueView extends DataValueView {

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
    IntDataValueView(final Selector cellSelection,
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
     * @param cellSelection The parent selection for spreadsheet cells.
     * @param dataValue The intDataValue that this view represents.
     * @param editable Is this DataValueView editable by the user? True if the
     * value is permitted to be altered by the user. False otherwise.
     */
    IntDataValueView(final Selector cellSelection,
                     final IntDataValue intDataValue,
                     final boolean editable) {
        super (cellSelection, intDataValue, editable);
    }

    /**
     * The action to invoke when a key is pressed.
     *
     * @param e The KeyEvent that triggered this action.
     */
    public void keyPressed(KeyEvent e) {
        // Ignore key release.
        switch (e.getKeyChar()) {
            case KeyEvent.VK_BACK_SPACE:
            case KeyEvent.VK_DELETE:
                // Ignore - handled when the key is typed.
                e.consume();
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
                // Move caret left and right (underlying text field handles
                // this).
                break;

            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_UP:
                // Key stroke gets passed up a parent element to navigate
                // cells up and down.
                break;
            default:
                break;
        }
    }

    /**
     * The action to invoke when a key is typed.
     *
     * @param e The KeyEvent that triggered this action.
     */
    public void keyTyped(KeyEvent e) {        
        IntDataValue idv = (IntDataValue) getValue();

        // '-' key toggles the state of a negative / positive number.
        if ((e.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD ||
             e.getKeyCode() == KeyEvent.KEY_LOCATION_UNKNOWN)
             && e.getKeyChar() == '-') {

            // Move the caret to behind the - sign, or the front of the number.
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
            this.removeBehindCaret();
            idv.setItsValue(new Integer(this.getText()));
            e.consume();

        // The delete key removes digits ahead of the caret.
        } else if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
                   && e.getKeyChar() == '\u007F') {
            this.removeAheadOfCaret();
            idv.setItsValue(new Integer(this.getText()));
            e.consume();

        // Key stoke is number - insert number into the current caret position.
        } else if (isKeyStrokeNumeric(e)) {
            this.removeSelectedText();
            StringBuffer currentValue = new StringBuffer(getText());
            currentValue.insert(getCaretPosition(), e.getKeyChar());
            setCaretPosition(Math.min(getCaretPosition() + 1,
                             getText().length()));
            idv.setItsValue(new Integer(currentValue.toString()));
            e.consume();

        // Every other key stroke is ignored by the float editor.
        } else {
            e.consume();
        }

        // Push the value back into the database.
        updateDatabase();
    }

    /**
     * The action to invoke when a key is released.
     *
     * @param e The KeyEvent that triggered this action.
     */
    public void keyReleased(KeyEvent e) {
        // Ignore key release.
    }
}
