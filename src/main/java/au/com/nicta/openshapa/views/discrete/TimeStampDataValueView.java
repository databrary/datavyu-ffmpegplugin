package au.com.nicta.openshapa.views.discrete;

import au.com.nicta.openshapa.db.DataCell;
import au.com.nicta.openshapa.db.Matrix;
import au.com.nicta.openshapa.db.SystemErrorException;
import au.com.nicta.openshapa.db.TimeStamp;
import au.com.nicta.openshapa.db.TimeStampDataValue;
import java.awt.event.KeyEvent;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 *
 * @author cfreeman
 */
public abstract class TimeStampDataValueView extends DataValueView {

    private Vector<Character> preservedChars;

    private static int HH_UPPER_RANGE = 99;

    private static long HH_TO_TICKS =  3600000;

    private static int MM_UPPER_RANGE = 59;

    private static long MM_TO_TICKS = 60000;

    private static int SS_UPPER_RANGE = 59;

    private static long SS_TO_TICKS = 1000;

    private static int TTT_UPPER_RANGE = 999;

    private static int TICKS_PER_SECOND = 1000;

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(TimeStampDataValueView.class);

    TimeStampDataValueView(final Selector cellSelection,
                           final DataCell cell,
                           final Matrix matrix,
                           final int matrixIndex,
                           final boolean editable) {
        super(cellSelection, cell, matrix, matrixIndex, editable);

        preservedChars = new Vector<Character>();
        preservedChars.add(new Character(':'));
    }

    TimeStampDataValueView(final Selector cellSelection,
                           final DataCell cell,
                           final TimeStampDataValue timeStampDataValue,
                           final boolean editable) {
        super(cellSelection, cell, timeStampDataValue, editable);

        preservedChars = new Vector<Character>();
        preservedChars.add(new Character(':'));
    }

    /**
     *
     * @param e
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
     *
     * @param e
     */
    public void keyTyped(KeyEvent e) {
        try {
            TimeStampDataValue tdv = (TimeStampDataValue) getValue();

            // The backspace key removes digits from behind the caret.
            if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
                       && e.getKeyChar() == '\u0008') {
                this.removeBehindCaret(preservedChars);
                StringBuffer currentValue = new StringBuffer(getText());
                currentValue.insert(getCaretPosition(), "0");
                tdv.setItsValue(buildValue(currentValue.toString()));
                e.consume();

            // The delete key removes digits ahead of the caret.
            } else if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
                       && e.getKeyChar() == '\u007F') {

                this.removeAheadOfCaret(preservedChars);
                StringBuffer currentValue = new StringBuffer(getText());
                currentValue.insert(getCaretPosition(), "0");
                tdv.setItsValue(buildValue(currentValue.toString()));
                e.consume();

            // Key stoke is number - insert number into the current caret position.
            } else if (isKeyStrokeNumeric(e)) {
                this.removeAheadOfCaret(preservedChars);
                StringBuffer currentValue = new StringBuffer(getText());
                currentValue.insert(getCaretPosition(), e.getKeyChar());
                advanceCaret();
                tdv.setItsValue(buildValue(currentValue.toString()));
                e.consume();

            // Every other key stroke is ignored by the float editor.
            } else {
                e.consume();
            }

            // Push the value back into the database.
            updateDatabase();
        } catch (SystemErrorException se) {
            logger.error("Unable to update TimeStampDataValue", se);
        }

        //this.removeSelectedText(preservedChars);
        //this.handleKeyEvent(e);
    }

    public void setValue(final TimeStamp ts) {
        try {
            TimeStampDataValue tsdv = (TimeStampDataValue) this.getValue();
            tsdv.setItsValue(ts);
            updateStrings();
            restoreCaretPosition();
        } catch (SystemErrorException se) {
            logger.error("Sets the value for the TimeStampDataValue", se);
        }

    }

    /**
     * Builds a new Double value from a string.
     *
     * @param textField The String that you want to create a Double from.
     *
     * @return A Double value that can be used setting the database.
     */
    public TimeStamp buildValue(final String textField) {
        try {
            long ticks = 0;

            String[] timeChunks = textField.split(":");

            ticks += (new Long(timeChunks[0]) * HH_TO_TICKS);
            ticks += (new Long(timeChunks[1]) * MM_TO_TICKS);
            ticks += (new Long(timeChunks[2]) * SS_TO_TICKS);
            ticks += (new Long(timeChunks[3]));

            return new TimeStamp(1000, ticks);
        } catch (SystemErrorException e) {
            logger.error("Unable to build TimeStamp value", e);
            return null;
        }
    }

    /**
     *
     * @param e
     */
    public void keyReleased(KeyEvent e) {
        // Ignore key release.
    }
}
