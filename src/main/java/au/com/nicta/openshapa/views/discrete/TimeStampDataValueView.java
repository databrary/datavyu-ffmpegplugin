package au.com.nicta.openshapa.views.discrete;

import au.com.nicta.openshapa.db.DataCell;
import au.com.nicta.openshapa.db.Matrix;
import au.com.nicta.openshapa.db.SystemErrorException;
import au.com.nicta.openshapa.db.TimeStamp;
import au.com.nicta.openshapa.db.TimeStampDataValue;
import au.com.nicta.openshapa.OpenSHAPA;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * An abstract view for TimeStampDataValues.
 *
 * @author cfreeman
 */
public abstract class TimeStampDataValueView extends DataValueView {

    // A list of preservedCharacters for this editor.
    private Vector<Character> preservedChars;

    // Conversion factor for converting hours to ticks.
    private static long HH_TO_TICKS =  3600000;

    // Conversion factor for converting minutes to ticks.
    private static long MM_TO_TICKS = 60000;

    // Conversion factor for converting seconds to ticks.
    private static int SS_TO_TICKS = 1000;

    /** Logger for this class. */
    private static Logger logger = Logger
                                   .getLogger(TimeStampDataValueView.class);

    /**
     * Constructor.
     *
     * @param cellSelection The parent cell selection that may contain this
     * view.
     * @param cell The parent cell that this view partially represents.
     * @param matrix The parent matrix containing the formal argument that this
     * view represents.
     * @param matrixIndex The index of the formal argument that this view
     * represents.
     * @param editable Is this view editable or not, true if the view is
     * editable. False otherwise.
     */
    TimeStampDataValueView(final Selector cellSelection,
                           final DataCell cell,
                           final Matrix matrix,
                           final int matrixIndex,
                           final boolean editable) {
        super(cellSelection, cell, matrix, matrixIndex, editable);

        preservedChars = new Vector<Character>();
        preservedChars.add(new Character(':'));
    }

    /**
     * Constructor.
     *
     * @param cellSelection The parent cell selection that may contain this view
     * @param cell The parent cell that this view partially represents.
     * @param timeStampDataValue The wrapper timeStampDataValue that this view
     * will represent.
     * @param editable Is this view editable or not, true if the view is
     * editable. False otherwise.
     */
    TimeStampDataValueView(final Selector cellSelection,
                           final DataCell cell,
                           final TimeStampDataValue timeStampDataValue,
                           final boolean editable) {
        super(cellSelection, cell, timeStampDataValue, editable);

        preservedChars = new Vector<Character>();
        preservedChars.add(new Character(':'));
    }

    /**
     * The action to invoke when a key is pressed.
     *
     * @param e The key event that triggered this action.
     */
    public void keyPressed(KeyEvent e) {
        //if (e.i)
        switch (e.getKeyCode()) {
            case KeyEvent.VK_BACK_SPACE:
            case KeyEvent.VK_DELETE:
                // Ignore - handled when the key is typed.
                e.consume();
                break;

            case KeyEvent.VK_LEFT:
                // If the character two steps to the left is a preserved
                // character we need to skip one before passing the key event
                // down to skip again (effectively skipping the preserved
                // character).
                for (int i = 0; i < preservedChars.size(); i++) {
                    int c = Math.max(0, getCaretPosition() - 2);

                    if (getText().charAt(c) == preservedChars.get(i)) {
                        setCaretPosition(Math.max(0, getCaretPosition() - 1));
                        break;
                    }
                }
                break;

            case KeyEvent.VK_RIGHT:
                // If the character to the right is a preserved character, we
                // need to skip one before passing the key event down to skip
                // again (effectively skipping the preserved character).
                for (int i = 0; i < preservedChars.size(); i++) {
                    int c = Math.min(getText().length() - 1,
                                     getCaretPosition() + 1);
                    if (getText().charAt(c) == preservedChars.get(i)) {
                        setCaretPosition(Math.min(getText().length() - 1,
                                                  getCaretPosition() + 1));
                        break;
                    }
                }
                break;

            case KeyEvent.VK_V:

                // Depending on platform, check appropriate modifier keys and
                // paste into timestamp.
                switch (OpenSHAPA.getPlatform()) {
                    case MAC:
                        if (e.isMetaDown()) {
                            pasteTimeStamp();
                            e.consume();
                        }
                        break;
                    default:
                        if (e.isControlDown()) {
                            pasteTimeStamp();
                            e.consume();
                        }
                        break;
                }
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
     * Attempt to paste teh contents of the clipboard into this timestamp.
     */
    public void pasteTimeStamp() {
        // Get the contents of the clipboard.
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);
        boolean hasText = (contents != null)
                     && contents.isDataFlavorSupported(DataFlavor.stringFlavor);

        // No valid text in clipboard. Bail.
        if (!hasText) {
            return;
        }

        // Valid text in clipboard - attempt to copy it into timestamp.
        try {
            String text = (String) contents
                                   .getTransferData(DataFlavor.stringFlavor);

            // Validate clipboard contents - if it is invalid, don't attempt
            // to paste it into the timestamp.
            boolean reject = true;
            for (int i = 0; i < text.length(); i++) {
                if (Character.isDigit(text.charAt(i))) {
                    reject = false;
                }

                for (int j = 0; reject && j < preservedChars.size(); j++) {
                    if (preservedChars.get(i) == text.charAt(i)) {
                        reject = false;
                    }
                }
            }

            // Contents of clipboard contain valid value - attempt to paste
            // it into this timestamp.
            if (!reject) {
                // Truncate any delimiters out of the timestamp datavalue.
                // and treat it as a continous stream of digits.
                for (int i = 0; i < preservedChars.size(); i++) {
                    char[] temp = new char[1];
                    temp[0] = preservedChars.get(i);
                    String chars = new String(temp);
                    text = text.replace(chars, "");
                }

                TimeStampDataValue tsdv = (TimeStampDataValue) getValue();
                TimeStamp ts = tsdv.getItsValue();

                // For each digit in the clipboard - add it to the timestamp
                // rebuilding the timestamp as we go (to get smart edits).
                for (int i = 0; i < text.length(); i++) {
                    // Build a string buffer for the timestamp we are
                    // copying digits into.
                    StringBuffer v = new StringBuffer(ts.toString());

                    // If the character in the current caret position of the
                    // destination time stamp is a preservedCharacter, skip
                    // over it.
                    for (int j = 0; j < preservedChars.size(); j++) {
                        if (v.charAt(getCaretPosition())
                            == preservedChars.get(j)) {
                            setCaretPosition(getCaretPosition() + 1);
                        }
                    }

                    // Replace the character in the current caret position
                    // with one from our clipboard and rebuild the timestamp
                    // to benefit from 'smart' edits.
                    v.deleteCharAt(getCaretPosition());
                    v.insert(getCaretPosition(), text.charAt(i));
                    ts = buildValue(v.toString());

                    // If we have got no more room in the timestamp - stop
                    // copying values.
                    if (getCaretPosition() + 1 == ts.toString().length()) {
                        setCaretPosition(getCaretPosition() + 1);
                        break;
                    }

                    // Advance the caret position to the next available slot
                    // in the destination timestamp.
                    setCaretPosition(getCaretPosition() + 1);
                }

                // Push the value back into the database.
                tsdv.setItsValue(ts);
                updateDatabase();

                // Update the strings if we don't change the value.
                updateStrings();
                restoreCaretPosition();
            }
        } catch (Exception ex) {
            logger.error("Unable to get clipboard contents", ex);
        }
    }

    /**
     * The action to invoke when a key is typed.
     *
     * @param e The KeyEvent that triggered this action.
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
                advanceCaret();
                tdv.setItsValue(buildValue(currentValue.toString()));
                e.consume();

            // Key stoke is number - insert number into the current caret position.
            } else if (Character.isDigit(e.getKeyChar())) {
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

            // Update the strings just in case we don't change the value.
            updateStrings();
            restoreCaretPosition();
        } catch (SystemErrorException se) {
            logger.error("Unable to update TimeStampDataValue", se);
        }
    }

    /**
     * Sets the value of that this TimeStampDataValueView represents.
     *
     * @param ts The new value of that this TimeStampDataValueView will
     * represent.
     */
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

            return new TimeStamp(SS_TO_TICKS, ticks);
        } catch (SystemErrorException e) {
            logger.error("Unable to build TimeStamp value", e);
            return null;
        }
    }

    /**
     * The action to invoke when a key is released.
     *
     * @param e The event that triggered this action.
     */
    public void keyReleased(KeyEvent e) {
        // Ignore key release.
    }
}
