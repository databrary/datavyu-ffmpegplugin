package au.com.nicta.openshapa.views.discrete;

import au.com.nicta.openshapa.db.DataCell;
import au.com.nicta.openshapa.db.Matrix;
import au.com.nicta.openshapa.db.TimeStampDataValue;
import java.awt.event.KeyEvent;
import java.util.Vector;

/**
 *
 * @author cfreeman
 */
public final class TimeStampDataValueView extends DataValueView {

    static final Vector<Character> preservedChars = new Vector<Character>();

    /**
     *
     * @param timestamp
     * @param editable
     */
    TimeStampDataValueView(final Selector cellSelection,
                           final DataCell cell,
                           final Matrix matrix,
                           final int matrixIndex,
                           final boolean editable) {
        super(cellSelection, cell, matrix, matrixIndex, editable);
    }

    TimeStampDataValueView(final Selector cellSelection,
                           final TimeStampDataValue timeStampDataValue,
                           final boolean editable) {
        super(cellSelection, timeStampDataValue, editable);
    }

    public void handleKeyEvent(KeyEvent e) {
        if (this.isKeyStrokeNumeric(e)) {
            // Passed into editor.
        } else {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_BACK_SPACE:
                case KeyEvent.VK_DELETE:
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_RIGHT:
                    // Key needs to be passed into editor.
                    break;

                case KeyEvent.VK_UP:
                case KeyEvent.VK_DOWN:
                    break;

                default:
                    e.consume();
                    break;
            }
        }
    }

    /**
     *
     * @param e
     */
    public void keyPressed(KeyEvent e) {
        //this.handleKeyEvent(e);
    }

    /**
     *
     * @param e
     */
    public void keyTyped(KeyEvent e) {
        this.removeSelectedText(preservedChars);
        //this.handleKeyEvent(e);
    }

    /**
     *
     * @param e
     */
    public void keyReleased(KeyEvent e) {
        //this.handleKeyEvent(e);
    }
}
