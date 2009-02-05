package au.com.nicta.openshapa.views.discrete;

import au.com.nicta.openshapa.db.DataCell;
import au.com.nicta.openshapa.db.FloatDataValue;
import au.com.nicta.openshapa.db.Matrix;
import java.awt.event.KeyEvent;

/**
 *
 * @author cfreeman
 */
public final class FloatDataValueView extends DataValueView {

    /**
     *
     * @param timestamp
     * @param editable
     */
    FloatDataValueView(final DataCell cell,
                       final Matrix matrix,
                       final int matrixIndex,
                       final boolean editable) {
        super(cell, matrix, matrixIndex, editable);
    }

    /**
     * This method handles keystrokes entered into a float editor.
     *
     * @param e The KeyEvent that needs to be handled by the editor.
     */
    public void handleKeyStroke(KeyEvent e) {
        FloatDataValue fdv = (FloatDataValue) getValue();

        // '-' key toggles the state of a negative / positive number.
        if ((e.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD ||
             e.getKeyCode() == KeyEvent.KEY_LOCATION_UNKNOWN)
             && e.getKeyChar() == '-') {

            // Toggle state of a negative / positive number.
            fdv.setItsValue(-fdv.getItsValue());
            e.consume();

        // '.' key shifts the location of the decimal point.
        } else if ((e.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD ||
                    e.getKeyLocation() == KeyEvent.KEY_LOCATION_STANDARD)
                    && e.getKeyChar() == '.') {
            // Shift the decimal point to the current caret position.

            int goo = 6;
        // If the key stroke is not numeric we need to perform additional
        // checks.
        } else if (!isKeyStrokeNumeric(e)) {
            switch (e.getKeyCode()) {
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

                case KeyEvent.VK_BACK_SPACE:
                case KeyEvent.VK_DELETE:
                    // Delete numbers, but not the decimal point (underlying
                    // text field handles this.
                    break;

                default:
                    // Every other key stroke is ignored by the float editor.
                    e.consume();
                    break;
            }
        }

        // Key stoke is number - insert number into the current caret position.

        // Push the value back into the database.
        updateDatabase();
    }

    /**
     * The action to invoke when a key is pressed.
     *
     * @param e The KeyEvent that triggered this action.
     */
    public void keyPressed(KeyEvent e) {
        handleKeyStroke(e);
    }

    /**
     * The action to invoke when a key is typed.
     *
     * @param e The KeyEvent that triggered this action.
     */
    public void keyTyped(KeyEvent e) {
        handleKeyStroke(e);
    }

    /**
     * The action to invoke when a key is released.
     *
     * @param e The KeyEvent that triggered this action.
     */
    public void keyReleased(KeyEvent e) {
        handleKeyStroke(e);
    }
}
