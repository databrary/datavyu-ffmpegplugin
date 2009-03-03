package au.com.nicta.openshapa.views.discrete;

import au.com.nicta.openshapa.db.DataCell;
import au.com.nicta.openshapa.db.Matrix;
import java.awt.event.KeyEvent;
import java.util.Vector;

/**
 * This class is the view representation of a NominalDataValue as stored within
 * the database.
 *
 * @author cfreeman
 */
public final class NominalDataValueView extends DataValueView {

    /** No preserved characters in the nominal data value view. */
    static final Vector<Character> preservedChars = new Vector<Character>();

    /**
     * Constructor.
     *
     * @param cellSelection The parent selection for spreadsheet cells.
     * @param cell The parent datacell for the nominal data value that this view
     * represents.
     * @param matrix The parent matrix for the nominal data value that this view
     * represents.
     * @param matrixIndex The index of the NominalDataValue within the above
     * parent matrix that this view represents.
     * @param editable Is the dataValueView editable by the user? True if the
     * value is permitted to be altered by the user. False otherwise.
     */
    NominalDataValueView(final Selector cellSelection,
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
        this.removeSelectedText(preservedChars);
    }

    /**
     * The action to invoke when a key is released.
     *
     * @param e The KeyEvent that triggered this action.
     */
    /*
    public void keyReleased(KeyEvent e) {
        // Ignore key release.
    }*/
}
