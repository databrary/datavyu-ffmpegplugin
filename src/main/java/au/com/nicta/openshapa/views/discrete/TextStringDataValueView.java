package au.com.nicta.openshapa.views.discrete;

import au.com.nicta.openshapa.db.DataCell;
import au.com.nicta.openshapa.db.Matrix;
import au.com.nicta.openshapa.db.SystemErrorException;
import au.com.nicta.openshapa.db.TextStringDataValue;
import java.awt.event.KeyEvent;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * This class is a view representation of a TextStringDataValue as stored within
 * the database.
 *
 * @author cfreeman
 */
public final class TextStringDataValueView extends DataValueView {

    /** No Preserved characters for TextStringDataValues **/
    static final Vector<Character> preservedChars = new Vector<Character>();

    /** The logger for TextStringDataValueView. */
    private static Logger logger = Logger
                                      .getLogger(TextStringDataValueView.class);

    /**
     * Constructor.
     *
     * @param cellSelection The parent selection for spreadsheet cells.
     * @param cell The parent datacell for the text string data value that this
     * view represents.
     * @param matrix The parent matrix for the text string data value that this
     * view represents.
     * @param matrixIndex The index of the TextStringDataValue within the above
     * parent matrix that this view represents.
     * @param editable Is the dataValueView editable by the user? True if the
     * value is permitted to be altered by the user. False otherwise.
     */
    TextStringDataValueView(final Selector cellSelection,
                            final DataCell cell,
                            final Matrix matrix,
                            final int matrixIndex,
                            final boolean editable) {
        super(cellSelection, cell, matrix, matrixIndex, editable);
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
        TextStringDataValue tsdv = (TextStringDataValue) getValue();

        // The backspace key removes digits from behind the caret.
        if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
                   && e.getKeyChar() == '\u0008') {
            this.removeBehindCaret(preservedChars);
            e.consume();

        // The delete key removes digits ahead of the caret.
        } else if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
                   && e.getKeyChar() == '\u007F') {
            this.removeAheadOfCaret(preservedChars);
            e.consume();

        // Just a regular vanilla keystroke - insert it into text field.
        } else {
            this.removeSelectedText(preservedChars);
            StringBuffer currentValue = new StringBuffer(getText());
            currentValue.insert(getCaretPosition(), e.getKeyChar());
            advanceCaret(); // Advance caret over the top of the new char.
            storeCaretPosition();
            this.setText(currentValue.toString());
            restoreCaretPosition();
            e.consume();
        }

        // Push the character changes into the database.
        try {
            tsdv.setItsValue(this.getText());
        } catch (SystemErrorException se) {
            logger.error("Unable to edit text string", se);
        }
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
